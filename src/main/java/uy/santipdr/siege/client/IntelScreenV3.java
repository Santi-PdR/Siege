package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;
import uy.santipdr.siege.SiegeMod;

import java.util.ArrayList;
import java.util.List;

/** Third-generation responsive Intel database. */
public final class IntelScreenV3 extends Screen {
    private static final int BOSS_FRAME_COUNT = 6;
    private static final List<String> CATEGORIES = List.of(
            "UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT"
    );
    private static String rememberedCategory = "UNIT";
    private static String rememberedCode;
    private static String rememberedQuery = "";
    private static final java.util.Map<String, Integer> READING_POSITIONS = new java.util.HashMap<>();
    private static final java.util.Map<String, String> CATEGORY_SELECTIONS = new java.util.HashMap<>();

    private final Screen parent;
    private final String requestedCategory;
    private final String requestedCode;
    private final List<SiegeButton> categoryButtons = new ArrayList<>();
    private final List<SiegeButton> entryButtons = new ArrayList<>();
    private final List<SiegeButton> navigationButtons = new ArrayList<>();
    private EditBox search;
    private Component contextualHint;
    private String query = rememberedQuery;
    private SiegeButton clearSearch, readingButton, inspectButton;
    private boolean readingMode = SiegeConfig.intelReadingMode;
    private String lastReadingCode;
    private SiegeButton readStart, readEnd;
    private List<IntelEntry> cachedFiles;
    private String cachedQuery, cachedCategory;
    private boolean cachedSpanish;
    private int thumbTop, thumbHeight, scrollGrab;
    private int pointerX, pointerY;
    private String bodyCacheKey, summaryCacheKey;
    private final java.util.Map<String, ResourceLocation> textures = new java.util.HashMap<>();
    private List<DetailLine> bodyCache = List.of();
    private List<FormattedCharSequence> summaryCache = List.of();
    private int summaryScroll, summaryMax, summaryX, summaryTop, summaryRight, summaryBottom;
    private int summaryThumbTop, summaryThumbHeight, summaryGrab;
    private boolean draggingSummary;
    private SiegeButton summaryUp, summaryDown;
    private int portraitX, portraitY, portraitW, portraitH;
    private int bodyLeft, bodyRight, bodyBottom;
    private boolean draggingScroll;
    private long entryChangedAt;

    private String category = "UNIT";
    private int selected;
    private int listOffset;
    private int detailScroll;
    private int maxDetailScroll;
    private int detailBodyTop;

    private SiegeIntelLayout layout;
    private boolean wide;
    private boolean ultraCompact;
    private int sidebarWidth;
    private int categoryTop;
    private int listTop;
    private int listBottom;
    private int contentTop;
    private boolean requestedEntryApplied;

    public IntelScreenV3(Screen parent) {
        this(parent, null);
    }

    public IntelScreenV3(Screen parent, IntelEntry requestedEntry) {
        super(Component.translatable("siege.intel.title"));
        this.parent = parent;
        this.requestedCategory = requestedEntry == null ? null : requestedEntry.category();
        this.requestedCode = requestedEntry == null ? null : requestedEntry.code();
        if (requestedEntry != null) query = "";
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        bodyCacheKey = summaryCacheKey = null;
        draggingScroll = draggingSummary = false;
        portraitW = summaryMax = maxDetailScroll = 0;
        categoryButtons.clear();
        entryButtons.clear();
        navigationButtons.clear();

        layout = SiegeIntelLayout.of(width, height, CATEGORIES.size());
        wide = layout.wide();
        ultraCompact = layout.ultraCompact();
        if (entryChangedAt == 0L) entryChangedAt = System.currentTimeMillis();
        applyInitialEntry();
        if (wide) initWide(); else initCompact();
        initTools();
        initReadingActions();
        refreshCategoryButtons();
        rebuildEntryNavigator();
    }

    private void applyInitialEntry() {
        if (requestedEntryApplied) return;
        requestedEntryApplied = true;
        String targetCategory = requestedCategory != null ? requestedCategory : rememberedCategory;
        String targetCode = requestedCode != null ? requestedCode : rememberedCode;
        if (!CATEGORIES.contains(targetCategory)) return;
        category = targetCategory;
        if (targetCode == null) return;
        List<IntelEntry> files = filtered();
        for (int i = 0; i < files.size(); i++) {
            if (targetCode.equals(files.get(i).code())) {
                selected = i;
                listOffset = 0;
                detailScroll = 0;
                return;
            }
        }
    }

    private void initWide() {
        sidebarWidth = layout.sidebarWidth();
        int margin = 10;
        int buttonHeight = layout.categoryHeight();
        int gap = ultraCompact ? 2 : 3;

        addRenderableWidget(new SiegeButton(margin, 9, sidebarWidth - margin * 2, 21,
                Component.literal("< ").append(Component.translatable("siege.intel.return")),
                b -> onClose(), SiegeTheme.WARNING));

        categoryTop = layout.categoryTop();
        int y = categoryTop;
        for (String value : CATEGORIES) {
            SiegeButton button = new SiegeButton(margin, y, sidebarWidth - margin * 2, buttonHeight,
                    Component.literal(categoryLabel(value)), b -> setCategory(value), categoryAccent(value));
            categoryButtons.add(button);
            addRenderableWidget(button);
            y += buttonHeight + gap;
        }

        listTop = layout.listTop();
        listBottom = layout.listBottom();
        contentTop = layout.contentTop();
    }

    private void initCompact() {
        sidebarWidth = 0;
        addRenderableWidget(new SiegeButton(6, 5, Math.min(72, Math.max(54, width / 5)), ultraCompact ? 16 : 18,
                Component.literal("< ").append(Component.translatable("siege.intel.return")),
                b -> onClose(), SiegeTheme.WARNING));

        int margin = 5;
        int gap = 2;
        int columns = layout.columns();
        int buttonHeight = layout.categoryHeight();
        int buttonWidth = Math.max(38, (width - margin * 2 - gap * (columns - 1)) / columns);
        int rows = (CATEGORIES.size() + columns - 1) / columns;
        categoryTop = layout.categoryTop();

        for (int i = 0; i < CATEGORIES.size(); i++) {
            String value = CATEGORIES.get(i);
            int row = i / columns;
            int col = i % columns;
            int x = margin + col * (buttonWidth + gap);
            int y = categoryTop + row * (buttonHeight + 2);
            SiegeButton button = new SiegeButton(x, y, buttonWidth, buttonHeight,
                    Component.literal(categoryLabel(value)), b -> setCategory(value), categoryAccent(value));
            categoryButtons.add(button);
            addRenderableWidget(button);
        }

        listTop = layout.listTop();
        listBottom = layout.listBottom();
        contentTop = layout.contentTop();
    }

    private void initTools() {
        int x = wide ? sidebarWidth + 15 : 6;
        int y = wide ? 50 : listTop - 24;
        int space = wide ? width - sidebarWidth - 30 : width - 12;
        int small = 22;
        boolean twoRows = space < 340;
        int modeW = twoRows ? (space - 8) / 3 : Math.min(90, space / 5);
        int inspectW = twoRows ? (space - 8) / 3 : Math.min(88, space / 5);
        int guideW = twoRows ? space - modeW - inspectW - 8 : Math.min(66, space / 6);
        int searchW = twoRows ? space - small - 4 : space - small - modeW - inspectW - guideW - 16;
        search = new EditBox(font, x, y, searchW, 18, Component.literal(label("Buscar expediente", "Search dossiers")));
        search.setMaxLength(80);
        search.setHint(Component.literal(label("Buscar...", "Search...")));
        search.setTooltip(Tooltip.create(Component.literal(label(
                "Busca texto, usa comillas para frases y !palabra para excluir",
                "Search text, quote phrases and use !word to exclude"))));
        search.setValue(query);
        search.setResponder(value -> {
            List<IntelEntry> before = filtered();
            String keep = before.isEmpty() ? null : before.get(Math.min(selected, before.size() - 1)).code();
            query = value;
            rememberedQuery = value;
            selected = listOffset = detailScroll = 0;
            List<IntelEntry> after = filtered();
            for (int i = 0; i < after.size(); i++) if (after.get(i).code().equals(keep)) selected = i;
            refreshCategoryButtons();
            rebuildEntryNavigator();
        });
        addRenderableWidget(search);
        clearSearch = addRenderableWidget(new SiegeButton(x + searchW + 4, y, small, 18,
                Component.literal("×"), b -> { search.setValue(""); setFocused(search); SiegeUiSounds.click(); }, SiegeTheme.WARNING));
        clearSearch.setTooltip(Tooltip.create(Component.literal(label("Limpiar búsqueda", "Clear search"))));
        readingButton = addRenderableWidget(new SiegeButton(twoRows ? x : x + searchW + small + 8, twoRows ? y + 22 : y, modeW, 18,
                Component.literal(label(readingMode ? "CON IMAGEN" : "LECTURA", readingMode ? "SHOW IMAGE" : "READING")), b -> {
                    readingMode = !readingMode;
                    SiegeConfig.intelReadingMode = readingMode; SiegeConfig.save();
                    lastReadingCode = null;
                    readingButton.setMessage(Component.literal(label(readingMode ? "CON IMAGEN" : "LECTURA", readingMode ? "SHOW IMAGE" : "READING")));
                    readingButton.setSelected(readingMode);
                    SiegeUiSounds.click();
                }, SiegeTheme.GOLD).setSelected(readingMode));
        readingButton.setTooltip(Tooltip.create(Component.literal(label("Alternar entre texto e imagen del expediente", "Switch between text and dossier image"))));
        inspectButton = addRenderableWidget(new SiegeButton(x + space - inspectW - guideW - 4, twoRows ? y + 22 : y, inspectW, 18,
                Component.literal(label("AMPLIAR", "INSPECT")), b -> {
                    List<IntelEntry> files = filtered();
                    if (!files.isEmpty()) {
                        SiegeUiSounds.click();
                        minecraft.setScreen(new IntelPortraitScreen(this, files.get(selected)));
                    }
                }, SiegeTheme.CYAN));
        inspectButton.setTooltip(Tooltip.create(Component.literal(label("Ver el documento completo con zoom", "View the complete document with zoom"))));
        addRenderableWidget(new SiegeButton(x + space - guideW, twoRows ? y + 22 : y, guideW, 18,
                Component.literal(label("GUÍA", "GUIDE")), b -> {
            SiegeUiSounds.click(); minecraft.setScreen(new SiegeGuideScreen(this));
        }, SiegeTheme.GOLD).setCompactCenter(true)).setTooltip(Tooltip.create(Component.literal(label(
                "Objetos, lore, dificultades, crónicas e inspiraciones", "Items, lore, difficulties, chronicles and inspirations"))));
    }

    private void initReadingActions() {
        summaryUp = addRenderableWidget(new SiegeButton(0, 0, 24, 16, Component.literal("↑"), b -> {
            summaryScroll = Math.max(0, summaryScroll - 1); SiegeUiSounds.click();
        }, SiegeTheme.CYAN));
        summaryDown = addRenderableWidget(new SiegeButton(0, 0, 24, 16, Component.literal("↓"), b -> {
            summaryScroll = Math.min(summaryMax, summaryScroll + 1); SiegeUiSounds.click();
        }, SiegeTheme.CYAN));
        summaryUp.setTooltip(Tooltip.create(Component.literal(label("Subir ficha táctica", "Scroll tactical brief up"))));
        summaryDown.setTooltip(Tooltip.create(Component.literal(label("Bajar ficha táctica", "Scroll tactical brief down"))));
        summaryUp.visible = summaryDown.visible = false;
        readStart = addRenderableWidget(new SiegeButton(6, height - 26, 24, 16, Component.literal("↑"), b -> {
            detailScroll = 0; SiegeUiSounds.click();
        }, SiegeTheme.CYAN));
        readEnd = addRenderableWidget(new SiegeButton(34, height - 26, 24, 16, Component.literal("↓"), b -> {
            detailScroll = maxDetailScroll; SiegeUiSounds.click();
        }, SiegeTheme.CYAN));
        readStart.setTooltip(Tooltip.create(Component.literal(label("Inicio del texto", "Start of text"))));
        readEnd.setTooltip(Tooltip.create(Component.literal(label("Final del texto", "End of text"))));
        readStart.visible = readEnd.visible = false;
    }
... (rest of the file follows the same pattern, replace hardcoded colors with constants)

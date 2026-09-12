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

    private final Screen parent;
    private final String requestedCategory;
    private final String requestedCode;
    private final List<SiegeButton> categoryButtons = new ArrayList<>();
    private final List<SiegeButton> entryButtons = new ArrayList<>();
    private final List<SiegeButton> navigationButtons = new ArrayList<>();
    private EditBox search;
    private String query = "";
    private SiegeButton clearSearch, readingButton, inspectButton;
    private boolean readingMode = SiegeConfig.intelReadingMode;
    private final java.util.Map<String, Integer> readingPositions = new java.util.HashMap<>();
    private String lastReadingCode;
    private SiegeButton readStart, readEnd;
    private final java.util.Map<String, String> categorySelections = new java.util.HashMap<>();
    private List<IntelEntry> cachedFiles;
    private String cachedQuery, cachedCategory;
    private boolean cachedSpanish;
    private int thumbTop, thumbHeight, scrollGrab;
    private int pointerX, pointerY;
    private String bodyCacheKey, summaryCacheKey;
    private List<DetailLine> bodyCache = List.of();
    private List<FormattedCharSequence> summaryCache = List.of();
    private int summaryScroll, summaryMax, summaryX, summaryTop, summaryRight, summaryBottom;
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
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        bodyCacheKey = summaryCacheKey = null;
        categoryButtons.clear();
        entryButtons.clear();
        navigationButtons.clear();

        layout = SiegeIntelLayout.of(width, height, CATEGORIES.size());
        wide = layout.wide();
        ultraCompact = layout.ultraCompact();
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
                b -> onClose(), 0xFFD64B4B));

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
                b -> onClose(), 0xFFD64B4B));

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
        int modeW = twoRows ? (space - 4) / 2 : Math.min(90, space / 4);
        int inspectW = twoRows ? space - modeW - 4 : Math.min(88, space / 4);
        int searchW = twoRows ? space - small - 4 : space - small - modeW - inspectW - 12;
        search = new EditBox(font, x, y, searchW, 18, Component.literal(label("Buscar expediente", "Search dossiers")));
        search.setMaxLength(80);
        search.setHint(Component.literal(label("Buscar...", "Search...")));
        search.setValue(query);
        search.setResponder(value -> {
            List<IntelEntry> before = filtered();
            String keep = before.isEmpty() ? null : before.get(Math.min(selected, before.size() - 1)).code();
            query = value;
            selected = listOffset = detailScroll = 0;
            List<IntelEntry> after = filtered();
            for (int i = 0; i < after.size(); i++) if (after.get(i).code().equals(keep)) selected = i;
            rebuildEntryNavigator();
        });
        addRenderableWidget(search);
        clearSearch = addRenderableWidget(new SiegeButton(x + searchW + 4, y, small, 18,
                Component.literal("×"), b -> search.setValue(""), 0xFFD65A4B));
        clearSearch.setTooltip(Tooltip.create(Component.literal(label("Limpiar búsqueda", "Clear search"))));
        readingButton = addRenderableWidget(new SiegeButton(twoRows ? x : x + searchW + small + 8, twoRows ? y + 22 : y, modeW, 18,
                Component.literal(label(readingMode ? "CON IMAGEN" : "LECTURA", readingMode ? "SHOW IMAGE" : "READING")), b -> {
                    readingMode = !readingMode;
                    SiegeConfig.intelReadingMode = readingMode; SiegeConfig.save();
                    lastReadingCode = null;
                    readingButton.setMessage(Component.literal(label(readingMode ? "CON IMAGEN" : "LECTURA", readingMode ? "SHOW IMAGE" : "READING")));
                    readingButton.setSelected(readingMode);
                    SiegeUiSounds.click();
                }, 0xFFD6A94B).setSelected(readingMode));
        readingButton.setTooltip(Tooltip.create(Component.literal(label("Alternar entre texto e imagen del expediente", "Switch between text and dossier image"))));
        inspectButton = addRenderableWidget(new SiegeButton(x + space - inspectW, twoRows ? y + 22 : y, inspectW, 18,
                Component.literal(label("AMPLIAR", "INSPECT")), b -> {
                    List<IntelEntry> files = filtered();
                    if (!files.isEmpty()) {
                        SiegeUiSounds.click();
                        minecraft.setScreen(new IntelPortraitScreen(this, files.get(selected)));
                    }
                }, 0xFF55BFD9));
        inspectButton.setTooltip(Tooltip.create(Component.literal(label("Ver el documento completo con zoom", "View the complete document with zoom"))));
    }

    private void initReadingActions() {
        summaryUp = addRenderableWidget(new SiegeButton(0, 0, 24, 16, Component.literal("↑"), b -> {
            summaryScroll = Math.max(0, summaryScroll - 1); SiegeUiSounds.click();
        }, 0xFF55BFD9));
        summaryDown = addRenderableWidget(new SiegeButton(0, 0, 24, 16, Component.literal("↓"), b -> {
            summaryScroll = Math.min(summaryMax, summaryScroll + 1); SiegeUiSounds.click();
        }, 0xFF55BFD9));
        summaryUp.setTooltip(Tooltip.create(Component.literal(label("Subir ficha táctica", "Scroll tactical brief up"))));
        summaryDown.setTooltip(Tooltip.create(Component.literal(label("Bajar ficha táctica", "Scroll tactical brief down"))));
        summaryUp.visible = summaryDown.visible = false;
        readStart = addRenderableWidget(new SiegeButton(6, height - 26, 24, 16, Component.literal("↑"), b -> {
            detailScroll = 0; SiegeUiSounds.click();
        }, 0xFF55BFD9));
        readEnd = addRenderableWidget(new SiegeButton(34, height - 26, 24, 16, Component.literal("↓"), b -> {
            detailScroll = maxDetailScroll; SiegeUiSounds.click();
        }, 0xFF55BFD9));
        readStart.setTooltip(Tooltip.create(Component.literal(label("Inicio del texto", "Start of text"))));
        readEnd.setTooltip(Tooltip.create(Component.literal(label("Final del texto", "End of text"))));
        readStart.visible = readEnd.visible = false;
    }

    @Override
    public void tick() { search.tick(); }

    private void setCategory(String value) {
        if (category.equals(value)) return;
        SiegeUiSounds.click();
        rememberSelection();
        category = value;
        selected = 0;
        List<IntelEntry> nextCategory = filtered();
        String keep = categorySelections.get(value);
        for (int i = 0; i < nextCategory.size(); i++) if (nextCategory.get(i).code().equals(keep)) selected = i;
        listOffset = 0;
        detailScroll = 0;
        refreshCategoryButtons();
        rebuildEntryNavigator();
        rememberSelection();
    }

    private void refreshCategoryButtons() {
        for (int i = 0; i < categoryButtons.size(); i++) {
            String value = CATEGORIES.get(i);
            SiegeButton button = categoryButtons.get(i);
            button.setMessage(Component.literal(categoryLabel(value)));
            button.setTooltip(Tooltip.create(Component.literal(categoryFullName(value) + " · " + IntelCatalog.count(value))));
            button.setSelected(value.equals(category));
        }
    }

    private void rebuildEntryNavigator() {
        for (SiegeButton button : entryButtons) removeWidget(button);
        for (SiegeButton button : navigationButtons) removeWidget(button);
        entryButtons.clear();
        navigationButtons.clear();
        draggingScroll = false;
        maxDetailScroll = 0;
        List<IntelEntry> files = filtered();
        if (inspectButton != null) inspectButton.active = !files.isEmpty();
        if (readingButton != null) readingButton.active = !files.isEmpty();
        if (files.isEmpty()) {
            selected = 0;
            listOffset = 0;
            return;
        }
        selected = Math.max(0, Math.min(selected, files.size() - 1));

        if (!wide) {
            int arrowWidth = Math.min(40, Math.max(28, width / 11));
            int navHeight = ultraCompact ? 16 : 18;
            SiegeButton previous = new SiegeButton(6, listTop, arrowWidth, navHeight, Component.literal("←"),
                    b -> stepEntry(-1), categoryAccent(category));
            SiegeButton next = new SiegeButton(width - arrowWidth - 6, listTop, arrowWidth, navHeight, Component.literal("→"),
                    b -> stepEntry(1), categoryAccent(category));
            navigationButtons.add(previous);
            navigationButtons.add(next);
            addRenderableWidget(previous);
            addRenderableWidget(next);
            return;
        }

        int arrowWidth = sidebarWidth - 20;
        SiegeButton previous = new SiegeButton(10, listTop, arrowWidth, 18,
                Component.literal("←"),
                b -> stepEntry(-1), categoryAccent(category));
        SiegeButton next = new SiegeButton(10, height - 29, arrowWidth, 18,
                Component.literal("→  " + label("SIGUIENTE", "NEXT")),
                b -> stepEntry(1), categoryAccent(category));
        previous.active = next.active = files.size() > 1;
        previous.setTooltip(Tooltip.create(Component.literal(label("Expediente anterior", "Previous dossier"))));
        next.setTooltip(Tooltip.create(Component.literal(label("Expediente siguiente", "Next dossier"))));
        navigationButtons.add(previous);
        navigationButtons.add(next);
        addRenderableWidget(previous);
        addRenderableWidget(next);

        int visible = visibleEntries();
        ensureSelectedVisible(visible, files.size());
        int y = listTop + 22;
        for (int i = listOffset; i < files.size() && i < listOffset + visible; i++) {
            int index = i;
            IntelEntry entry = files.get(i);
            SiegeButton button = new SiegeButton(10, y, sidebarWidth - 20, 19,
                    Component.literal(entry.code() + "  " + entry.name()),
                    b -> selectEntry(index), categoryAccent(entry.category()));
            button.setTooltip(Tooltip.create(Component.literal(entry.code() + " · " + entry.name())));
            button.setSelected(i == selected);
            entryButtons.add(button);
            addRenderableWidget(button);
            y += 22;
        }
    }

    private void selectEntry(int index) {
        if (index == selected) return;
        SiegeUiSounds.click();
        selected = index;
        entryChangedAt = System.currentTimeMillis();
        detailScroll = 0;
        refreshEntrySelection();
        rememberSelection();
    }

    private void stepEntry(int direction) {
        List<IntelEntry> files = filtered();
        if (files.isEmpty()) return;
        SiegeUiSounds.click();
        selected = Math.floorMod(selected + direction, files.size());
        entryChangedAt = System.currentTimeMillis();
        detailScroll = 0;
        if (wide) {
            ensureSelectedVisible(visibleEntries(), files.size());
            rebuildEntryNavigator();
        }
        rememberSelection();
    }

    private void ensureSelectedVisible(int visible, int size) {
        if (visible <= 0) { listOffset = 0; return; }
        if (selected < listOffset) listOffset = selected;
        if (selected >= listOffset + visible) listOffset = selected - visible + 1;
        listOffset = Math.max(0, Math.min(listOffset, Math.max(0, size - visible)));
    }

    private void refreshEntrySelection() {
        if (wide) {
            for (int i = 0; i < entryButtons.size(); i++) {
                entryButtons.get(i).setSelected(listOffset + i == selected);
            }
        }
    }

    private int visibleEntries() {
        return Math.max(0, (listBottom - (listTop + 22)) / 22);
    }

    private List<IntelEntry> filtered() {
        boolean es = spanish();
        if (cachedFiles != null && query.equals(cachedQuery) && category.equals(cachedCategory) && es == cachedSpanish) return cachedFiles;
        cachedQuery = query; cachedCategory = category; cachedSpanish = es;
        List<IntelEntry> source = IntelCatalog.filtered(category);
        cachedFiles = query.isBlank() ? source : source.stream().filter(entry -> {
            IntelEntry.IntelText text = entry.text(es);
            return IntelSearch.matches(query, entry.code() + " " + entry.name() + " " + text.origin()
                    + " " + text.armament() + " " + text.description() + " " + text.advisory()
                    + " " + text.status() + " " + text.variants() + " " + entry.hp() + " " + entry.defense());
        }).toList();
        return cachedFiles;
    }

    private void rememberSelection() {
        rememberedCategory = category;
        List<IntelEntry> files = filtered();
        rememberedCode = files.isEmpty() ? null : files.get(Math.max(0, Math.min(selected, files.size() - 1))).code();
        if (rememberedCode != null) categorySelections.put(category, rememberedCode);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta == 0) return false;
        int direction = delta < 0 ? 1 : -1;
        if (summaryMax > 0 && mouseX >= summaryX && mouseX < summaryRight && mouseY >= summaryTop && mouseY < summaryBottom) {
            summaryScroll = Math.max(0, Math.min(summaryMax, summaryScroll + direction)); return true;
        }

        if (wide && mouseX >= 10 && mouseX < sidebarWidth - 10 && mouseY >= listTop && mouseY < listBottom) {
            stepEntry(direction);
            return true;
        }
        if (!wide && mouseY >= listTop && mouseY < contentTop) {
            stepEntry(direction);
            return true;
        }
        if (mouseY >= detailBodyTop && mouseY < bodyBottom && mouseX >= bodyLeft && mouseX < bodyRight && maxDetailScroll > 0) {
            detailScroll = Math.max(0, Math.min(maxDetailScroll, detailScroll + direction));
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (search != null && search.isFocused()) return super.keyPressed(keyCode, scanCode, modifiers);
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            stepEntry(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            stepEntry(1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        pointerX = mouseX; pointerY = mouseY;
        clearSearch.active = !query.isEmpty();
        readStart.visible = readEnd.visible = false;
        portraitW = 0;
        summaryMax = 0; summaryUp.visible = summaryDown.visible = false;
        bodyBottom = detailBodyTop = maxDetailScroll = 0;
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        int accent = categoryAccent(category);
        g.fill(0, 0, width, height, 0xA006090B);

        if (wide) renderWideChrome(g, accent); else renderCompactChrome(g, accent);

        List<IntelEntry> files = filtered();
        if (files.isEmpty()) {
            int centerX = wide ? (sidebarWidth + width) / 2 : width / 2;
            g.drawCenteredString(font, font.plainSubstrByWidth(label("SIN RESULTADOS", "NO RESULTS"), wide ? width - sidebarWidth - 30 : width - 20),
                    centerX, height / 2 - 5, 0xFF8A9298);
            String empty = !query.isBlank() ? label("Prueba otra búsqueda o pulsa × para borrarla.", "Try another search or use × to clear it.")
                    : label("La base de datos todavía no contiene registros.", "The database does not contain records yet.");
            g.drawCenteredString(font, font.plainSubstrByWidth(empty, wide ? width - sidebarWidth - 30 : width - 20),
                    centerX, height / 2 + 10, 0xFF68727A);
        } else {
            selected = Math.max(0, Math.min(selected, files.size() - 1));
            IntelEntry entry = files.get(selected);
            if (!wide) {
                renderFile(g, entry, 6, contentTop, width - 12, true);
            } else {
                renderFile(g, entry, sidebarWidth + 15, contentTop, width - sidebarWidth - 30, false);

            }
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    private void renderWideChrome(GuiGraphics g, int accent) {
        g.fill(0, 0, width, 43, 0xF207090B);
        g.fill(0, 43, sidebarWidth, height, 0xF20B1014);
        g.fill(sidebarWidth, 43, width, height, 0x70080A0C);
        g.fill(sidebarWidth - 2, 43, sidebarWidth, height, accent);
        g.fill(0, 41, width, 43, accent);

        for (int y = 70; y < height; y += 28) {
            g.fill(0, y, sidebarWidth, y + 1, 0x1219A5BC);
        }

        if (!ultraCompact) {
            g.drawString(font, label("CATEGORÍAS", "CATEGORIES"), 12, 51, 0xFF89959D, false);
        }

        int filesBandTop = listTop - (ultraCompact ? 18 : 28);
        int filesBandBottom = listTop - 4;
        g.fill(0, filesBandTop, sidebarWidth, filesBandBottom, 0xFF0B1014);
        g.fill(10, filesBandBottom - 2, sidebarWidth - 10, filesBandBottom - 1, 0xFF24323A);

        String heading = label("INTEL // CLASIFICADO", "INTEL // CLASSIFIED");
        g.drawCenteredString(font, font.plainSubstrByWidth(heading, width - sidebarWidth - 20),
                (sidebarWidth + width) / 2, 16, 0xFFF0EEE8);
        g.drawString(font, label("ARCHIVOS", "FILES") + " [" + filtered().size() + "]",
                12, filesBandTop + 7, 0xFF89959D, false);
    }

    private void renderCompactChrome(GuiGraphics g, int accent) {
        g.fill(0, 0, width, 26, 0xF207090B);
        g.fill(0, 25, width, 27, accent);
        g.drawCenteredString(font, label("INTEL // CLASIFICADO", "INTEL // CLASSIFIED"), width / 2, 8, 0xFFF0EEE8);
        g.fill(0, contentTop - 3, width, contentTop - 2, 0x4C454E55);
    }

    private void renderFile(GuiGraphics g, IntelEntry entry, int x, int y, int availableWidth, boolean compactMode) {
        IntelEntry.IntelText text = entry.text(spanish());
        boolean advanced = entry.category().equals("ADVANCED");
        boolean dark = SiegeConfig.darkIntelPaper;
        int paper = dark ? 0xFF20262B : advanced ? 0xFFE0E7EB : 0xFFDEDCD3;
        int ink = dark ? 0xFFE5E7E2 : advanced ? 0xFF13232D : 0xFF29261F;
        int muted = dark ? 0xFFADB8BE : advanced ? 0xFF53646E : 0xFF6B6454;
        int accent = dark ? 0xFF91CFE2 : accentInk(entry.category());
        int warning = dark ? 0xFFFFA99B : 0xFF8A2E27;
        int bottom = height - 7;
        int pad = compactMode ? 6 : 12;
        int inner = availableWidth - pad * 2;
        g.fill(x + 3, y + 3, x + availableWidth + 3, bottom + 3, 0x66000000);
        g.fill(x, y, x + availableWidth, bottom, paper);
        g.fill(x, y + 2, x + 1, bottom, 0x40505050);
        g.fill(x + availableWidth - 1, y + 2, x + availableWidth, bottom, 0x40505050);
        g.fill(x, y, x + availableWidth, y + 2, categoryAccent(entry.category()));
        String ref = entry.code() + "  ·  " + (selected + 1) + "/" + filtered().size();
        g.drawString(font, ref, x + pad, y + 6, muted, false);

        g.drawString(font, font.plainSubstrByWidth(entry.name(), inner), x + pad, y + 18, ink, false);
        bodyLeft = x + pad;
        bodyRight = x + availableWidth - pad;
        detailBodyTop = y + 34;
        bodyBottom = bottom - 25;
        // At compact scales, metadata belongs to the scroll area; it cannot push it off-screen.
        if (!readingMode && !compactMode && availableWidth >= 420 && bodyBottom - detailBodyTop >= 150) {
            int imageW = SiegeIntelLayout.portraitWidth(inner, bodyBottom - detailBodyTop);
            int imageH = imageW * 9 / 16;
            int imageY = y + 37;
            portraitX = bodyRight - imageW; portraitY = imageY; portraitW = imageW; portraitH = imageH;
            g.blit(portraitTexture(entry, bossFrame(entry)), portraitX, imageY, imageW, imageH, 0, 0, 640, 360, 640, 360);
            renderTacticalSummary(g, entry, portraitX, imageY + imageH + 8, imageW, bodyBottom, ink, muted, accent);
            bodyRight = portraitX - 14;
        } else if (!readingMode && bodyBottom - detailBodyTop >= 140) {
            int imageH = Math.min(80, (bodyBottom - detailBodyTop) / 3);
            int imageW = Math.min(inner, imageH * 16 / 9);
            imageH = imageW * 9 / 16;
            portraitX = bodyLeft + (inner - imageW) / 2; portraitY = detailBodyTop; portraitW = imageW; portraitH = imageH;
            g.blit(portraitTexture(entry, bossFrame(entry)), portraitX, detailBodyTop, imageW, imageH, 0, 0, 640, 360, 640, 360);
            detailBodyTop += imageH + 8;
        }
        if (portraitW > 0 && pointerX >= portraitX && pointerX < portraitX + portraitW && pointerY >= portraitY && pointerY < portraitY + portraitH) {
            g.fill(portraitX, portraitY, portraitX + portraitW, portraitY + 1, accent);
            g.fill(portraitX, portraitY + portraitH - 1, portraitX + portraitW, portraitY + portraitH, accent);
        }
        int bodyWidth = bodyRight - bodyLeft - 8;
        String cacheKey = entry.code() + ":" + bodyWidth + ":" + spanish() + ":" + dark + ":" + readingMode;
        if (!cacheKey.equals(bodyCacheKey)) {
        DetailLine anchor = bodyCache.isEmpty() ? null : bodyCache.get(Math.min(detailScroll, bodyCache.size() - 1));
        boolean sameEntry = lastReadingCode != null && lastReadingCode.startsWith(entry.code() + ":");
        List<DetailLine> lines = new ArrayList<>();
        appendWrapped(lines, label("AMENAZA", "THREAT") + " " + (entry.threat() > 0 ? stars(entry.threat()) : label("SIN DATOS", "NO DATA")), bodyWidth, warning);
        appendWrapped(lines, "HP " + entry.hp() + ("N/D".equals(entry.defense()) ? "" : "  DEF " + entry.defense()), bodyWidth, ink);
        appendWrapped(lines, label("ORIGEN: ", "ORIGIN: ") + text.origin(), bodyWidth, muted);
        appendWrapped(lines, label("ESTADO: ", "STATUS: ") + text.status(), bodyWidth, muted);
        appendWrapped(lines, label("ARMAMENTO: ", "ARMAMENT: ") + text.armament(), bodyWidth, ink);
        appendWrapped(lines, label("VARIANTES: ", "VARIANTS: ") + text.variants(), bodyWidth, muted);
        lines.add(blankLine());
        appendWrapped(lines, label("PERFIL OPERATIVO", "OPERATIONAL PROFILE"), bodyWidth, accent);
        appendWrapped(lines, text.description(), bodyWidth, ink);
        lines.add(blankLine());
        appendWrapped(lines, label("ADVERTENCIA TÁCTICA", "TACTICAL ADVISORY"), bodyWidth, warning);
        appendWrapped(lines, text.advisory(), bodyWidth, warning);
        if (sameEntry && anchor != null) {
            for (int i = 0; i < lines.size(); i++) {
                DetailLine line = lines.get(i);
                if (line.source().equals(anchor.source()) && line.offset() <= anchor.offset()) detailScroll = i;
            }
        }
        bodyCache = List.copyOf(lines); bodyCacheKey = cacheKey;
        }
        List<DetailLine> lines = bodyCache;
        int lineHeight = SiegeConfig.comfortableReading ? 14 : 11;
        int visible = Math.max(1, (bodyBottom - detailBodyTop) / lineHeight);
        String readingKey = entry.code() + ":" + readingMode;
        if (!readingKey.equals(lastReadingCode)) {
            lastReadingCode = readingKey;
            detailScroll = readingPositions.getOrDefault(readingKey, 0);
        }
        maxDetailScroll = Math.max(0, lines.size() - visible);
        detailScroll = Math.max(0, Math.min(detailScroll, maxDetailScroll));
        g.enableScissor(bodyLeft, detailBodyTop, bodyRight - 6, bodyBottom);
        for (int i = detailScroll; i < Math.min(lines.size(), detailScroll + visible); i++)
            g.drawString(font, lines.get(i).value(), bodyLeft, detailBodyTop + (i - detailScroll) * lineHeight, lines.get(i).color(), false);
        g.disableScissor();
        if (maxDetailScroll > 0) {
            int trackH = bodyBottom - detailBodyTop;
            int thumbH = Math.min(trackH, Math.max(10, trackH * visible / lines.size()));
            int thumbY = detailBodyTop + (trackH - thumbH) * detailScroll / maxDetailScroll;
            thumbTop = thumbY; thumbHeight = thumbH;
            g.fill(bodyRight - 4, detailBodyTop, bodyRight, bodyBottom, 0x33413B32);
            g.fill(bodyRight - 4, thumbY, bodyRight, thumbY + thumbH, accent);
        }
        readingPositions.put(readingKey, detailScroll);
        readStart.visible = readEnd.visible = maxDetailScroll > 0;
        readStart.setX(bodyLeft); readEnd.setX(bodyLeft + 28);
        readStart.active = detailScroll > 0; readEnd.active = detailScroll < maxDetailScroll;
        String progress = maxDetailScroll == 0 ? label("COMPLETO", "COMPLETE") : (detailScroll + 1) + "–" + Math.min(lines.size(), detailScroll + visible) + " / " + lines.size();
        g.drawString(font, progress, bodyRight - font.width(progress), bottom - 11, muted, false);

        long age = System.currentTimeMillis() - entryChangedAt;
        if (age < 230 && SiegeConfig.menuEffects && !SiegeConfig.reducedMotion) {
            int reveal = Math.round(availableWidth * age / 230F);
            g.fill(x, y, x + reveal, y + 2, 0xFFF0D46A);
        }
    }

    private void renderTacticalSummary(GuiGraphics g, IntelEntry entry, int x, int y, int w, int bottom, int ink, int muted, int accent) {
        if (bottom - y < 52) return; // Full advisory remains in the main reader at very short heights.
        g.fill(x, y, x + w, y + 1, accent);
        g.drawString(font, font.plainSubstrByWidth(label("ADVERTENCIA TÁCTICA", "TACTICAL ADVISORY"), w), x, y + 5, accent, false);
        String key = entry.code() + ":" + w + ":" + spanish();
        if (!key.equals(summaryCacheKey)) {
            summaryCache = List.copyOf(font.split(Component.literal(entry.text(spanish()).advisory()), w - 8));
            summaryScroll = 0; summaryCacheKey = key;
        }
        int visible = Math.max(1, (bottom - y - 40) / 11);
        summaryMax = Math.max(0, summaryCache.size() - visible);
        summaryScroll = Math.max(0, Math.min(summaryMax, summaryScroll));
        summaryX = x; summaryTop = y + 20; summaryRight = x + w; summaryBottom = bottom;
        g.enableScissor(x, summaryTop, x + w, bottom - 20);
        for (int i = summaryScroll; i < Math.min(summaryCache.size(), summaryScroll + visible); i++)
            g.drawString(font, summaryCache.get(i), x + 4, summaryTop + (i - summaryScroll) * 11, ink, false);
        g.disableScissor();
        summaryUp.visible = summaryDown.visible = summaryMax > 0;
        summaryUp.setX(x); summaryDown.setX(x + 28);
        summaryUp.setY(bottom - 17); summaryDown.setY(bottom - 17);
        summaryUp.active = summaryScroll > 0; summaryDown.active = summaryScroll < summaryMax;
        if (summaryMax > 0) {
            String range = (summaryScroll + 1) + "–" + Math.min(summaryCache.size(), summaryScroll + visible) + "/" + summaryCache.size();
            g.drawString(font, range, x + w - font.width(range), bottom - 12, muted, false);
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0 && portraitW > 0 && x >= portraitX && x < portraitX + portraitW && y >= portraitY && y < portraitY + portraitH) {
            List<IntelEntry> files = filtered();
            if (!files.isEmpty()) { SiegeUiSounds.click(); minecraft.setScreen(new IntelPortraitScreen(this, files.get(selected))); return true; }
        }
        if (button == 0 && maxDetailScroll > 0 && x >= bodyRight - 7 && x < bodyRight + 2 && y >= detailBodyTop && y < bodyBottom) {
            draggingScroll = true;
            scrollGrab = y >= thumbTop && y < thumbTop + thumbHeight ? (int)y - thumbTop : thumbHeight / 2;
            scrollTo(y);
            return true;
        }
        return super.mouseClicked(x, y, button);
    }
    private void scrollTo(double y) {
        detailScroll = (int)Math.round(Math.max(0, Math.min(1, (y - detailBodyTop - scrollGrab) / Math.max(1, bodyBottom - detailBodyTop - thumbHeight))) * maxDetailScroll);
    }
    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (draggingScroll && button == 0) { scrollTo(y); return true; }
        return super.mouseDragged(x, y, button, dx, dy);
    }
    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (button == 0) draggingScroll = false;
        return super.mouseReleased(x, y, button);
    }

    private void appendWrapped(List<DetailLine> target, String value, int width, int color) {
        int offset = 0;
        for (FormattedCharSequence line : font.split(Component.literal(value), Math.max(24, width))) {
            target.add(new DetailLine(line, color, value, offset));
            int[] count = {0};
            line.accept((index, style, codePoint) -> { count[0] += Character.charCount(codePoint); return true; });
            offset += count[0];
        }
    }

    private DetailLine blankLine() {
        return new DetailLine(FormattedCharSequence.forward(" ", net.minecraft.network.chat.Style.EMPTY), 0x00000000, "", 0);
    }

    private int accentInk(String category) {
        return switch (category) {
            case "ADVANCED" -> 0xFF245F86;
            case "TANK" -> 0xFF8A571B;
            default -> 0xFF8C302B;
        };
    }

    private int bossFrame(IntelEntry entry) {
        if (!entry.category().equals("BOSS") || !SiegeConfig.animatedIntel || SiegeConfig.reducedMotion) return 0;
        return Math.floorMod((int) (Math.max(0, System.currentTimeMillis() - entryChangedAt) / 450L), BOSS_FRAME_COUNT);
    }

    private ResourceLocation portraitTexture(IntelEntry entry, int frame) {
        String image = entry.image();
        if (entry.category().equals("BOSS")) {
            image = image.substring(0, image.length() - 2) + String.format("%02d", frame);
        }
        return new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/intel/" + image + ".png");
    }

    private String categoryFullName(String value) {
        return switch (value) {
            case "UNIT" -> label("UNIDADES", "UNITS");
            case "ADVANCED" -> label("AVANZADOS", "ADVANCED");
            case "TANK" -> label("TANQUES", "TANKS");
            case "BOSS" -> label("JEFES", "BOSSES");
            case "ELITE" -> label("ÉLITES", "ELITES");
            default -> label("SUPERUNIDADES", "SUPER-UNITS");
        };
    }

    private String categoryLabel(String value) {
        int count = IntelCatalog.count(value);
        if (sidebarWidth < 180) {
            String shortName = switch (value) {
                case "ALL" -> "ALL";
                case "UNIT" -> spanish() ? "UNI" : "UNIT";
                case "ADVANCED" -> "ADV";
                case "TANK" -> spanish() ? "TNQ" : "TNK";
                case "BOSS" -> spanish() ? "JEF" : "BOS";
                case "ELITE" -> "ELT";
                case "SUPER-UNIT" -> "SUP";
                default -> value;
            };
            return shortName;
        }

        String name = switch (value) {
            case "ALL" -> label("TODAS", "ALL");
            case "UNIT" -> label("UNIDADES", "UNITS");
            case "ADVANCED" -> label("AVANZADOS", "ADVANCED");
            case "TANK" -> label("TANQUES", "TANKS");
            case "BOSS" -> label("JEFES", "BOSSES");
            case "ELITE" -> label("ÉLITES", "ELITES");
            case "SUPER-UNIT" -> label("SUPERUNIDADES", "SUPER-UNITS");
            default -> value;
        };
        return name + "  [" + count + "]";
    }

    private int categoryAccent(String value) {
        return switch (value) {
            case "ALL" -> 0xFF55BFD9;
            case "UNIT" -> 0xFFD94A4A;
            case "ADVANCED" -> 0xFF2F80FF;
            case "TANK" -> 0xFFD98A2B;
            case "BOSS" -> 0xFFB5162D;
            case "ELITE" -> 0xFF9B59D0;
            case "SUPER-UNIT" -> 0xFFE0B93F;
            default -> 0xFFB8C0C8;
        };
    }

    private String stars(int count) {
        return "★".repeat(Math.max(0, count)) + "☆".repeat(Math.max(0, 5 - count));
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private String label(String es, String en) {
        return spanish() ? es : en;
    }

    @Override
    public void onClose() {
        SiegeUiSounds.back();
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record DetailLine(FormattedCharSequence value, int color, String source, int offset) { }
}

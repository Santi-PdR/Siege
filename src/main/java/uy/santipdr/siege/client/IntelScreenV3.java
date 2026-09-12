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
            "UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT", "FAVORITES"
    );
    private static String rememberedCategory = "UNIT";
    private static String rememberedCode;

    private final Screen parent;
    private final String requestedCategory;
    private final String requestedCode;
    private final List<SiegeButton> categoryButtons = new ArrayList<>();
    private final List<SiegeButton> entryButtons = new ArrayList<>();
    private final List<SiegeButton> navigationButtons = new ArrayList<>();
    private SiegeButton favoriteButton;
    private EditBox search;
    private String query = "";
    private SiegeButton clearSearch, readingButton, inspectButton;
    private boolean readingMode = SiegeConfig.intelReadingMode;
    private final java.util.Map<String, Integer> readingPositions = new java.util.HashMap<>();
    private String lastReadingCode;
    private SiegeButton readStart, readEnd, copyText;
    private long copiedUntil;
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
        categoryButtons.clear();
        entryButtons.clear();
        navigationButtons.clear();
        favoriteButton = null;

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
        int gap = 3;

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
        int modeW = Math.min(90, space / 4);
        int inspectW = Math.min(88, space / 4);
        int searchW = space - small - modeW - inspectW - 12;
        search = new EditBox(font, x, y, searchW, 18, Component.literal(label("Buscar expediente", "Search dossiers")));
        search.setMaxLength(80);
        search.setHint(Component.literal(label("Buscar...", "Search...")));
        search.setValue(query);
        search.setResponder(value -> {
            query = value;
            selected = listOffset = detailScroll = 0;
            rebuildEntryNavigator();
        });
        addRenderableWidget(search);
        clearSearch = addRenderableWidget(new SiegeButton(x + searchW + 4, y, small, 18,
                Component.literal("×"), b -> search.setValue(""), 0xFFD65A4B));
        clearSearch.setTooltip(Tooltip.create(Component.literal(label("Limpiar búsqueda", "Clear search"))));
        readingButton = addRenderableWidget(new SiegeButton(x + searchW + small + 8, y, modeW, 18,
                Component.literal(label("LECTURA", "READING")), b -> {
                    readingMode = !readingMode;
                    SiegeConfig.intelReadingMode = readingMode; SiegeConfig.save();
                    detailScroll = 0;
                    readingButton.setSelected(readingMode);
                    SiegeUiSounds.click();
                }, 0xFFD6A94B).setSelected(readingMode));
        readingButton.setTooltip(Tooltip.create(Component.literal(label("Leer todo el texto sin la imagen", "Read the full text without the image"))));
        inspectButton = addRenderableWidget(new SiegeButton(x + space - inspectW, y, inspectW, 18,
                Component.literal(label("AMPLIAR", "INSPECT")), b -> {
                    List<IntelEntry> files = filtered();
                    if (!files.isEmpty()) {
                        SiegeUiSounds.click();
                        minecraft.setScreen(new IntelPortraitScreen(this, files.get(selected)));
                    }
                }, 0xFF55BFD9));
    }

    private void initReadingActions() {
        readStart = addRenderableWidget(new SiegeButton(6, height - 26, 24, 16, Component.literal("↑"), b -> {
            detailScroll = 0; SiegeUiSounds.click();
        }, 0xFF55BFD9));
        readEnd = addRenderableWidget(new SiegeButton(34, height - 26, 24, 16, Component.literal("↓"), b -> {
            detailScroll = maxDetailScroll; SiegeUiSounds.click();
        }, 0xFF55BFD9));
        copyText = addRenderableWidget(new SiegeButton(62, height - 26, 66, 16, Component.literal(label("COPIAR", "COPY")), b -> {
            List<IntelEntry> files = filtered();
            if (files.isEmpty()) return;
            IntelEntry e = files.get(selected);
            IntelEntry.IntelText t = e.text(spanish());
            minecraft.keyboardHandler.setClipboard(e.code() + " · " + e.name() + "\nHP " + e.hp()
                    + " · DEF " + e.defense() + " · " + label("AMENAZA ", "THREAT ") + e.threat()
                    + "\n" + t.origin() + "\n" + t.armament() + "\n" + t.variants() + "\n" + t.status()
                    + "\n\n" + t.description() + "\n\n" + t.advisory());
            copiedUntil = System.currentTimeMillis() + 1600;
            SiegeUiSounds.click();
        }, 0xFFD6A94B));
        readStart.setTooltip(Tooltip.create(Component.literal(label("Inicio del texto", "Start of text"))));
        readEnd.setTooltip(Tooltip.create(Component.literal(label("Final del texto", "End of text"))));
        copyText.setTooltip(Tooltip.create(Component.literal(label("Copiar la información al portapapeles", "Copy dossier information to clipboard"))));
        readStart.visible = readEnd.visible = copyText.visible = false;
    }

    @Override
    public void tick() { search.tick(); }

    private void setCategory(String value) {
        if (category.equals(value)) return;
        SiegeUiSounds.click();
        category = value;
        selected = 0;
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
            button.setTooltip(Tooltip.create(Component.literal(value + " · " + IntelCatalog.count(value))));
            button.setSelected(value.equals(category));
        }
    }

    private void rebuildEntryNavigator() {
        for (SiegeButton button : entryButtons) removeWidget(button);
        for (SiegeButton button : navigationButtons) removeWidget(button);
        entryButtons.clear();
        navigationButtons.clear();
        favoriteButton = null;
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
            int favoriteWidth = Math.min(104, (width - arrowWidth * 2 - 28) / 2);
            int centerX = (width - favoriteWidth * 2 - 4) / 2;
            addIndexButton(centerX, listTop, favoriteWidth, navHeight);
            favoriteButton = new SiegeButton(centerX + favoriteWidth + 4, listTop, favoriteWidth, navHeight,
                    favoriteButtonLabel(files.get(selected)), b -> toggleFavorite(), 0xFFF0D46A);
            favoriteButton.setSelected(SiegeConfig.isFavoriteIntel(files.get(selected).code()));
            navigationButtons.add(favoriteButton);
            addRenderableWidget(favoriteButton);
            return;
        }

        int arrowWidth = sidebarWidth - 20;
        addIndexButton(52, listTop, arrowWidth - 42, 18);
        SiegeButton previous = new SiegeButton(10, listTop, 38, 18,
                Component.literal("←"),
                b -> stepEntry(-1), categoryAccent(category));
        SiegeButton next = new SiegeButton(10, height - 29, arrowWidth, 18,
                Component.literal("→  " + label("SIGUIENTE", "NEXT")),
                b -> stepEntry(1), categoryAccent(category));
        navigationButtons.add(previous);
        navigationButtons.add(next);
        favoriteButton = new SiegeButton(10, height - 52, arrowWidth, 18,
                favoriteButtonLabel(files.get(selected)), b -> toggleFavorite(), 0xFFF0D46A);
        favoriteButton.setSelected(SiegeConfig.isFavoriteIntel(files.get(selected).code()));
        navigationButtons.add(favoriteButton);
        addRenderableWidget(previous);
        addRenderableWidget(next);
        addRenderableWidget(favoriteButton);

        int visible = visibleEntries();
        ensureSelectedVisible(visible, files.size());
        int y = listTop + 22;
        for (int i = listOffset; i < files.size() && i < listOffset + visible; i++) {
            int index = i;
            IntelEntry entry = files.get(i);
            SiegeButton button = new SiegeButton(10, y, sidebarWidth - 20, 19,
                    Component.literal((SiegeConfig.isFavoriteIntel(entry.code()) ? "★ " : "")
                            + entry.code() + "  " + entry.name()),
                    b -> selectEntry(index), categoryAccent(entry.category()));
            button.setSelected(i == selected);
            entryButtons.add(button);
            addRenderableWidget(button);
            y += 22;
        }
    }

    private void addIndexButton(int x, int y, int w, int h) {
        SiegeButton button = new SiegeButton(x, y, w, h, Component.literal(label("ÍNDICE", "INDEX")), b -> {
            List<IntelEntry> files = filtered();
            if (files.isEmpty()) return;
            SiegeUiSounds.click();
            minecraft.setScreen(new IntelIndexScreen(this, files, categoryLabel(category), files.get(selected).code(), "FAVORITES".equals(category), code -> {
                List<IntelEntry> current = filtered();
                for (int i = 0; i < current.size(); i++) if (current.get(i).code().equals(code)) {
                    selectEntry(i);
                    break;
                }
            }));
        }, 0xFF55BFD9);
        button.setTooltip(Tooltip.create(Component.literal(label("Ver y ordenar los expedientes de esta selección", "Browse and sort dossiers in this selection"))));
        navigationButtons.add(button);
        addRenderableWidget(button);
    }

    private void selectEntry(int index) {
        if (index == selected) { SiegeUiSounds.click(); return; }
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
        refreshFavoriteButton();
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
        refreshFavoriteButton();
    }

    private int visibleEntries() {
        return Math.max(0, (listBottom - (listTop + 22)) / 22);
    }

    private List<IntelEntry> filtered() {
        List<IntelEntry> source = IntelCatalog.filtered(category);
        if (query.isBlank()) return source;
        return source.stream().filter(entry -> {
            IntelEntry.IntelText text = entry.text(spanish());
            return IntelSearch.matches(query, entry.code() + " " + entry.name() + " " + text.origin()
                    + " " + text.armament() + " " + text.description() + " " + text.advisory());
        }).toList();
    }

    private void rememberSelection() {
        rememberedCategory = category;
        List<IntelEntry> files = filtered();
        rememberedCode = files.isEmpty() ? null : files.get(Math.max(0, Math.min(selected, files.size() - 1))).code();
    }

    private void toggleFavorite() {
        List<IntelEntry> files = filtered();
        if (files.isEmpty()) return;
        IntelEntry entry = files.get(Math.max(0, Math.min(selected, files.size() - 1)));
        SiegeConfig.toggleFavoriteIntel(entry.code());
        SiegeUiSounds.click();
        rebuildEntryNavigator();
        rememberSelection();
        refreshCategoryButtons();
    }

    private void refreshFavoriteButton() {
        if (favoriteButton == null) return;
        List<IntelEntry> files = filtered();
        if (files.isEmpty()) {
            favoriteButton.active = false;
            favoriteButton.setSelected(false);
            favoriteButton.setMessage(Component.literal(label("SIN EXPEDIENTE", "NO DOSSIER")));
            return;
        }
        IntelEntry entry = files.get(Math.max(0, Math.min(selected, files.size() - 1)));
        favoriteButton.active = true;
        favoriteButton.setSelected(SiegeConfig.isFavoriteIntel(entry.code()));
        favoriteButton.setMessage(favoriteButtonLabel(entry));
    }

    private Component favoriteButtonLabel(IntelEntry entry) {
        return Component.literal(SiegeConfig.isFavoriteIntel(entry.code())
                ? label("★ GUARDADO", "★ SAVED") : label("☆ GUARDAR", "☆ SAVE"));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta == 0) return false;
        int direction = delta < 0 ? 1 : -1;

        if (wide && mouseX < sidebarWidth && mouseY >= listTop) {
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
        clearSearch.active = !query.isEmpty();
        readStart.visible = readEnd.visible = copyText.visible = false;
        portraitW = 0;
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        int accent = categoryAccent(category);
        g.fill(0, 0, width, height, 0xA006090B);

        if (wide) renderWideChrome(g, accent); else renderCompactChrome(g, accent);

        List<IntelEntry> files = filtered();
        if (files.isEmpty()) {
            int centerX = wide ? (sidebarWidth + width) / 2 : width / 2;
            g.drawCenteredString(font, font.plainSubstrByWidth(label("SIN RESULTADOS", "NO RESULTS"), wide ? width - sidebarWidth - 30 : width - 20),
                    centerX, height / 2 - 5, 0xFF8A9298);
            String empty = !query.isBlank() ? label("Prueba otra búsqueda o pulsa × para borrarla.", "Try another search or use × to clear it.") : "FAVORITES".equals(category)
                    ? label("Usa GUARDAR en un expediente para añadirlo aquí.", "Use SAVE on a dossier to add it here.")
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
                String counter = String.format("%02d/%02d", selected + 1, files.size());
                g.drawString(font, counter, width - 12 - font.width(counter), 17, 0xFF97A0A7, false);
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

        int categoryBandTop = 45;
        int categoryBandBottom = categoryTop - 5;
        g.fill(0, categoryBandTop, sidebarWidth, categoryBandBottom, 0xFF0B1014);
        // No full-width underline here: it was visually colliding with the CATEGORIES label.
        g.fill(10, categoryBandTop + 5, 12, categoryBandBottom - 5, accent);

        int filesBandTop = listTop - 28;
        int filesBandBottom = listTop - 4;
        g.fill(0, filesBandTop, sidebarWidth, filesBandBottom, 0xFF0B1014);
        g.fill(10, filesBandBottom - 2, sidebarWidth - 10, filesBandBottom - 1, 0xFF24323A);

        g.drawCenteredString(font, label("BASE DE DATOS DE INTELIGENCIA", "INTELLIGENCE DATABASE"),
                (sidebarWidth + width) / 2, 16, 0xFFF0EEE8);
        g.drawString(font, "// " + label("CATEGORÍAS", "CATEGORIES"),
                16, categoryBandTop + 6, 0xFF89959D, false);
        g.drawString(font, "// " + label("EXPEDIENTES", "FILES") + " [" + filtered().size() + "]",
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
        int paper = dark ? 0xFF20262B : advanced ? 0xFFE0E7EB : 0xFFE7DFC9;
        int ink = dark ? 0xFFE5E7E2 : advanced ? 0xFF13232D : 0xFF29261F;
        int muted = dark ? 0xFFADB8BE : advanced ? 0xFF53646E : 0xFF6B6454;
        int accent = dark ? 0xFF91CFE2 : accentInk(entry.category());
        int warning = dark ? 0xFFFFA99B : 0xFF8A2E27;
        int bottom = height - 7;
        int pad = compactMode ? 6 : 12;
        int inner = availableWidth - pad * 2;
        g.fill(x + 3, y + 3, x + availableWidth + 3, bottom + 3, 0x66000000);
        g.fill(x, y, x + availableWidth, bottom, paper);
        g.fill(x, y, x + availableWidth, y + 2, categoryAccent(entry.category()));
        String ref = entry.code() + "  ·  " + (selected + 1) + "/" + filtered().size();
        g.drawString(font, ref, x + pad, y + 6, muted, false);
        String stamp = SiegeConfig.isFavoriteIntel(entry.code()) ? "★" : "";
        g.drawString(font, stamp, x + availableWidth - pad - font.width(stamp), y + 6, accent, false);
        g.drawString(font, font.plainSubstrByWidth(entry.name(), inner), x + pad, y + 18, ink, false);
        bodyLeft = x + pad;
        bodyRight = x + availableWidth - pad;
        detailBodyTop = y + 34;
        bodyBottom = bottom - 25;
        // At compact scales, metadata belongs to the scroll area; it cannot push it off-screen.
        if (!readingMode && !compactMode && availableWidth >= 420) {
            int imageW = Math.min(292, inner * 43 / 100);
            int imageH = imageW * 9 / 16;
            int imageY = y + 37;
            portraitX = bodyLeft; portraitY = imageY; portraitW = imageW; portraitH = imageH;
            g.blit(portraitTexture(entry, bossFrame(entry)), bodyLeft, imageY, imageW, imageH, 0, 0, 640, 360, 640, 360);
            g.drawString(font, label("AMPLIAR: VER IMAGEN", "INSPECT: VIEW IMAGE"), bodyLeft, imageY + imageH + 8, muted, false);
            bodyLeft += imageW + 14;
        } else if (!readingMode && bodyBottom - detailBodyTop >= 140) {
            int imageH = Math.min(80, (bodyBottom - detailBodyTop) / 3);
            int imageW = imageH * 16 / 9;
            portraitX = bodyLeft; portraitY = detailBodyTop; portraitW = imageW; portraitH = imageH;
            g.blit(portraitTexture(entry, bossFrame(entry)), bodyLeft, detailBodyTop, imageW, imageH, 0, 0, 640, 360, 640, 360);
            detailBodyTop += imageH + 8;
        }
        int bodyWidth = bodyRight - bodyLeft - 8;
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
        int lineHeight = SiegeConfig.comfortableReading ? 14 : 11;
        int visible = Math.max(1, (bodyBottom - detailBodyTop) / lineHeight);
        if (!entry.code().equals(lastReadingCode)) {
            lastReadingCode = entry.code();
            detailScroll = readingPositions.getOrDefault(entry.code(), 0);
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
            g.fill(bodyRight - 4, detailBodyTop, bodyRight, bodyBottom, 0x33413B32);
            g.fill(bodyRight - 4, thumbY, bodyRight, thumbY + thumbH, accent);
        }
        readingPositions.put(entry.code(), detailScroll);
        readStart.visible = readEnd.visible = copyText.visible = true;
        readStart.setX(bodyLeft); readEnd.setX(bodyLeft + 28); copyText.setX(bodyLeft + 56);
        readStart.active = detailScroll > 0; readEnd.active = detailScroll < maxDetailScroll;
        copyText.setMessage(Component.literal(System.currentTimeMillis() < copiedUntil ? label("COPIADO", "COPIED") : label("COPIAR", "COPY")));
        String progress = (maxDetailScroll == 0 ? 100 : (int)Math.round(100.0 * detailScroll / maxDetailScroll)) + "%";
        g.drawString(font, progress, bodyRight - font.width(progress), bottom - 11, muted, false);

        long age = System.currentTimeMillis() - entryChangedAt;
        if (age < 230 && SiegeConfig.menuEffects && !SiegeConfig.reducedMotion) {
            int reveal = Math.round(availableWidth * age / 230F);
            g.fill(x, y, x + reveal, y + 2, 0xFFF0D46A);
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
            scrollTo(y);
            return true;
        }
        return super.mouseClicked(x, y, button);
    }
    private void scrollTo(double y) {
        detailScroll = (int)Math.round(Math.max(0, Math.min(1, (y - detailBodyTop) / Math.max(1, bodyBottom - detailBodyTop - 1))) * maxDetailScroll);
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
        for (FormattedCharSequence line : font.split(Component.literal(value), Math.max(24, width))) {
            target.add(new DetailLine(line, color));
        }
    }

    private DetailLine blankLine() {
        return new DetailLine(FormattedCharSequence.forward(" ", net.minecraft.network.chat.Style.EMPTY), 0x00000000);
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
        return Math.floorMod((int) (System.currentTimeMillis() / 450L), BOSS_FRAME_COUNT);
    }

    private ResourceLocation portraitTexture(IntelEntry entry, int frame) {
        String image = entry.image();
        if (entry.category().equals("BOSS")) {
            image = image.substring(0, image.length() - 2) + String.format("%02d", frame);
        }
        return new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/intel/" + image + ".png");
    }

    private String categoryLabel(String value) {
        int count = IntelCatalog.count(value);
        if (!wide) {
            String shortName = switch (value) {
                case "ALL" -> "ALL";
                case "UNIT" -> spanish() ? "UNI" : "UNIT";
                case "ADVANCED" -> "ADV";
                case "TANK" -> spanish() ? "TNQ" : "TNK";
                case "BOSS" -> spanish() ? "JEF" : "BOS";
                case "ELITE" -> "ELT";
                case "SUPER-UNIT" -> "SUP";
                case "FAVORITES" -> spanish() ? "FAV" : "FAV";
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
            case "FAVORITES" -> label("FAVORITOS", "FAVORITES");
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
            case "FAVORITES" -> 0xFFF0D46A;
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

    private record DetailLine(FormattedCharSequence value, int color) { }
}

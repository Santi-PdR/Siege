package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;
import uy.santipdr.siege.SiegeMod;

import java.util.ArrayList;
import java.util.List;

/** Responsive Intel database for GUI scales 1-4. */
public final class IntelScreenV2 extends Screen {
    private static final List<String> CATEGORIES = List.of("ALL", "UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT");

    private final Screen parent;
    private final List<SiegeButton> categoryButtons = new ArrayList<>();
    private final List<SiegeButton> entryButtons = new ArrayList<>();
    private final List<SiegeButton> navigationButtons = new ArrayList<>();

    private String category = "ALL";
    private int selected;
    private int listOffset;
    private int detailScroll;
    private int maxDetailScroll;
    private int sidebarWidth;
    private int categoryTop;
    private int listTop;
    private int listBottom;
    private int contentTop;
    private int detailBodyTop;
    private boolean compact;

    public IntelScreenV2(Screen parent) {
        super(Component.translatable("siege.intel.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        categoryButtons.clear();
        entryButtons.clear();
        navigationButtons.clear();
        compact = width < 620 || height < 340;
        if (compact) initCompact(); else initWide();
        refreshCategoryButtons();
        rebuildEntryNavigator();
    }

    private void initWide() {
        sidebarWidth = Math.min(238, Math.max(174, width / 5));
        int margin = 10;
        int buttonHeight = height < 430 ? 18 : 20;
        int gap = 3;

        addRenderableWidget(new SiegeButton(margin, 9, sidebarWidth - margin * 2, 21,
                Component.literal("< ").append(Component.translatable("siege.intel.return")),
                b -> onClose(), 0xFFD64B4B));

        // A dedicated header band lives between the cyan database rule and the
        // first category button. Text is never drawn directly on the accent line.
        categoryTop = 61;
        int y = categoryTop;
        for (String value : CATEGORIES) {
            SiegeButton button = new SiegeButton(margin, y, sidebarWidth - margin * 2, buttonHeight,
                    Component.literal(categoryLabel(value)), b -> setCategory(value), categoryAccent(value));
            categoryButtons.add(button);
            addRenderableWidget(button);
            y += buttonHeight + gap;
        }

        // Reserve a second solid band for the FILES heading. This prevents the
        // grey sidebar scanline from visually cutting through the label.
        listTop = y + 29;
        listBottom = height - 36;
        contentTop = 56;
    }

    private void initCompact() {
        sidebarWidth = 0;
        addRenderableWidget(new SiegeButton(7, 6, Math.min(76, Math.max(58, width / 5)), 18,
                Component.literal("< ").append(Component.translatable("siege.intel.return")),
                b -> onClose(), 0xFFD64B4B));

        int margin = 6;
        int gap = 2;
        int columns = 4;
        int buttonHeight = 17;
        int buttonWidth = Math.max(44, (width - margin * 2 - gap * (columns - 1)) / columns);
        categoryTop = 30;
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
        listTop = categoryTop + 2 * (buttonHeight + 2) + 3;
        listBottom = listTop + 19;
        contentTop = listBottom + 5;
    }

    private void setCategory(String value) {
        if (category.equals(value)) return;
        SiegeUiSounds.click();
        category = value;
        selected = 0;
        listOffset = 0;
        detailScroll = 0;
        refreshCategoryButtons();
        rebuildEntryNavigator();
    }

    private void refreshCategoryButtons() {
        for (int i = 0; i < categoryButtons.size(); i++) {
            String value = CATEGORIES.get(i);
            SiegeButton button = categoryButtons.get(i);
            button.setMessage(Component.literal(categoryLabel(value)));
            button.setSelected(value.equals(category));
        }
    }

    private void rebuildEntryNavigator() {
        for (SiegeButton button : entryButtons) removeWidget(button);
        for (SiegeButton button : navigationButtons) removeWidget(button);
        entryButtons.clear();
        navigationButtons.clear();

        List<IntelEntry> files = filtered();
        if (files.isEmpty()) {
            selected = 0;
            listOffset = 0;
            return;
        }
        selected = Math.max(0, Math.min(selected, files.size() - 1));

        if (compact) {
            int arrowWidth = Math.min(42, Math.max(30, width / 10));
            int y = listTop;
            SiegeButton up = new SiegeButton(7, y, arrowWidth, 18, Component.literal("▲"),
                    b -> stepEntry(-1), categoryAccent(category));
            SiegeButton down = new SiegeButton(width - arrowWidth - 7, y, arrowWidth, 18, Component.literal("▼"),
                    b -> stepEntry(1), categoryAccent(category));
            navigationButtons.add(up);
            navigationButtons.add(down);
            addRenderableWidget(up);
            addRenderableWidget(down);
            return;
        }

        int arrowWidth = sidebarWidth - 20;
        SiegeButton up = new SiegeButton(10, listTop, arrowWidth, 18, Component.literal("▲  " + label("ANTERIOR", "PREVIOUS")),
                b -> stepEntry(-1), categoryAccent(category));
        SiegeButton down = new SiegeButton(10, height - 29, arrowWidth, 18, Component.literal("▼  " + label("SIGUIENTE", "NEXT")),
                b -> stepEntry(1), categoryAccent(category));
        navigationButtons.add(up);
        navigationButtons.add(down);
        addRenderableWidget(up);
        addRenderableWidget(down);

        int visible = visibleEntries();
        ensureSelectedVisible(visible, files.size());
        int y = listTop + 22;
        for (int i = listOffset; i < files.size() && i < listOffset + visible; i++) {
            int index = i;
            IntelEntry entry = files.get(i);
            SiegeButton button = new SiegeButton(10, y, sidebarWidth - 20, 19,
                    Component.literal(entry.code() + "  " + entry.name()), b -> selectEntry(index), categoryAccent(entry.category()));
            button.setSelected(i == selected);
            entryButtons.add(button);
            addRenderableWidget(button);
            y += 22;
        }
    }

    private void selectEntry(int index) {
        SiegeUiSounds.click();
        selected = index;
        detailScroll = 0;
        refreshEntrySelection();
    }

    private void stepEntry(int direction) {
        List<IntelEntry> files = filtered();
        if (files.isEmpty()) return;
        SiegeUiSounds.click();
        selected = Math.floorMod(selected + direction, files.size());
        detailScroll = 0;
        if (!compact) {
            ensureSelectedVisible(visibleEntries(), files.size());
            rebuildEntryNavigator();
        }
    }

    private void ensureSelectedVisible(int visible, int size) {
        if (selected < listOffset) listOffset = selected;
        if (selected >= listOffset + visible) listOffset = selected - visible + 1;
        listOffset = Math.max(0, Math.min(listOffset, Math.max(0, size - visible)));
    }

    private void refreshEntrySelection() {
        if (compact) return;
        for (int i = 0; i < entryButtons.size(); i++) {
            entryButtons.get(i).setSelected(listOffset + i == selected);
        }
    }

    private int visibleEntries() {
        return Math.max(1, (listBottom - (listTop + 22)) / 22);
    }

    private List<IntelEntry> filtered() {
        return IntelCatalog.filtered(category);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta == 0) return false;
        int direction = delta < 0 ? 1 : -1;

        if (!compact && mouseX < sidebarWidth) {
            stepEntry(direction);
            return true;
        }

        if (compact && mouseY < contentTop) {
            stepEntry(direction);
            return true;
        }

        if (mouseY >= detailBodyTop && maxDetailScroll > 0) {
            detailScroll = Math.max(0, Math.min(maxDetailScroll, detailScroll + direction * 2));
            return true;
        }

        stepEntry(direction);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_LEFT) {
            stepEntry(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_RIGHT) {
            stepEntry(1);
            return true;
        }
        if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_6) {
            int index = keyCode - GLFW.GLFW_KEY_0;
            if (index < CATEGORIES.size()) {
                setCategory(CATEGORIES.get(index));
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        int accent = categoryAccent(category);
        g.fill(0, 0, width, height, 0xA006090B);

        if (compact) renderCompactChrome(g, accent); else renderWideChrome(g, accent);

        List<IntelEntry> files = filtered();
        if (files.isEmpty()) {
            int centerX = compact ? width / 2 : (sidebarWidth + width) / 2;
            g.drawCenteredString(font, label("SIN EXPEDIENTES EN ESTA CATEGORÍA", "NO FILES IN THIS CATEGORY"),
                    centerX, height / 2 - 5, 0xFF8A9298);
            g.drawCenteredString(font, label("La base de datos todavía no contiene registros.", "The database does not contain records yet."),
                    centerX, height / 2 + 10, 0xFF68727A);
        } else {
            selected = Math.max(0, Math.min(selected, files.size() - 1));
            IntelEntry entry = files.get(selected);
            if (compact) {
                String nav = String.format("%02d/%02d  //  %s  %s", selected + 1, files.size(), entry.code(), entry.name());
                int maxWidth = Math.max(80, width - 112);
                g.drawCenteredString(font, font.plainSubstrByWidth(nav, maxWidth), width / 2, listTop + 5, 0xFFE2E5E7);
                renderFile(g, entry, 7, contentTop, width - 14, true);
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

        // Background telemetry lines are intentionally painted first. Solid
        // section-header bands are then painted over them so labels never sit
        // on top of a line with a different tone.
        for (int y = 66; y < height; y += 28) g.fill(0, y, sidebarWidth, y + 1, 0x1519A5BC);

        int categoryBandTop = 44;
        int categoryBandBottom = categoryTop - 4;
        g.fill(0, categoryBandTop, sidebarWidth, categoryBandBottom, 0xFC0B1014);
        g.fill(10, categoryBandBottom - 2, sidebarWidth - 10, categoryBandBottom - 1, 0xFF24323A);

        int filesBandTop = listTop - 27;
        int filesBandBottom = listTop - 4;
        g.fill(0, filesBandTop, sidebarWidth, filesBandBottom, 0xFC0B1014);
        g.fill(10, filesBandBottom - 2, sidebarWidth - 10, filesBandBottom - 1, 0xFF24323A);

        g.drawCenteredString(font, label("BASE DE DATOS DE INTELIGENCIA", "INTELLIGENCE DATABASE"),
                (sidebarWidth + width) / 2, 16, 0xFFF0EEE8);
        g.drawString(font, "// " + label("CATEGORÍAS [0-6]", "CATEGORIES [0-6]"), 12, categoryBandTop + 5, 0xFF89959D, false);
        g.drawString(font, "// " + label("EXPEDIENTES", "FILES") + " [" + filtered().size() + "]", 12, filesBandTop + 7, 0xFF89959D, false);
    }

    private void renderCompactChrome(GuiGraphics g, int accent) {
        g.fill(0, 0, width, 27, 0xF207090B);
        g.fill(0, 26, width, 28, accent);
        g.drawCenteredString(font, label("INTEL // CLASIFICADO", "INTEL // CLASSIFIED"), width / 2, 9, 0xFFF0EEE8);
        g.fill(0, contentTop - 3, width, contentTop - 2, 0x66454E55);
    }

    private void renderFile(GuiGraphics g, IntelEntry entry, int x, int y, int availableWidth, boolean compactMode) {
        IntelEntry.IntelText text = entry.text(spanish());
        boolean advanced = entry.category().equals("ADVANCED");
        int accent = categoryAccent(entry.category());
        int paper = advanced ? 0xFFE0E7EB : 0xFFE7DFC9;
        int paperDark = advanced ? 0xFFB8C5CD : 0xFFC7BCA1;
        int ink = advanced ? 0xFF13232D : 0xFF29261F;
        int muted = advanced ? 0xFF53646E : 0xFF6B6454;
        int warning = advanced ? 0xFF842C3C : 0xFF8A2E27;
        int bottom = height - 8;
        if (bottom <= y + 35 || availableWidth < 90) return;

        g.fill(x + 3, y + 3, x + availableWidth + 3, bottom + 3, 0x66000000);
        g.fill(x, y, x + availableWidth, bottom, paper);
        g.fill(x, y, x + availableWidth, y + 3, accent);
        g.fill(x, y, x + 1, bottom, paperDark);
        g.fill(x + availableWidth - 1, y, x + availableWidth, bottom, paperDark);

        int pad = compactMode ? 6 : 10;
        int headerX = x + pad;
        int headerY = y + 7;
        int innerWidth = availableWidth - pad * 2;

        String ref = label("EXPEDIENTE", "FILE") + " " + entry.code();
        g.drawString(font, ref, headerX, headerY, muted, false);
        if (availableWidth > 230) {
            String stamp = label("CLASIFICADO", "CLASSIFIED");
            g.drawString(font, stamp, x + availableWidth - pad - font.width(stamp), headerY, accent, false);
        }
        g.drawString(font, entry.name(), headerX, headerY + 12, ink, false);
        String threat = label("AMENAZA", "THREAT") + " " + stars(entry.threat()) + "   HP " + entry.hp();
        g.drawString(font, font.plainSubstrByWidth(threat, innerWidth), headerX, headerY + 24, warning, false);

        int mediaTop = headerY + 39;
        int imageWidth = compactMode
                ? Math.min(116, Math.max(82, availableWidth * 32 / 100))
                : Math.min(292, Math.max(142, availableWidth * 40 / 100));
        int maxImageHeight = Math.max(48, bottom - mediaTop - (compactMode ? 62 : 80));
        imageWidth = Math.min(imageWidth, Math.max(80, maxImageHeight * 16 / 9));
        int imageHeight = imageWidth * 9 / 16;
        int imageX = headerX;
        ResourceLocation portrait = new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/intel/" + entry.image() + ".png");
        g.fill(imageX - 3, mediaTop - 3, imageX + imageWidth + 3, mediaTop + imageHeight + 3, paperDark);
        g.blit(portrait, imageX, mediaTop, imageWidth, imageHeight, 0, 0, 640, 360, 640, 360);
        g.fill(imageX, mediaTop, imageX + imageWidth, mediaTop + 2, accent);

        int metaX = imageX + imageWidth + (compactMode ? 7 : 13);
        int metaWidth = Math.max(50, x + availableWidth - pad - metaX);
        int metaEnd = mediaTop;
        if (metaWidth >= 50) {
            metaEnd = drawMeta(g, label("ORIGEN", "ORIGIN"), text.origin(), metaX, metaEnd, metaWidth, muted, ink);
            metaEnd = drawMeta(g, label("ESTADO", "STATUS"), text.status(), metaX, metaEnd + 3, metaWidth, muted, warning);
            if (!compactMode && metaWidth >= 90) {
                metaEnd = drawMeta(g, label("ARMAMENTO", "ARMAMENT"), text.armament(), metaX, metaEnd + 3, metaWidth, muted, ink);
                metaEnd = drawMeta(g, label("VARIANTES", "VARIANTS"), text.variants(), metaX, metaEnd + 3, metaWidth, muted, ink);
            }
        }

        // Critical layout rule: the body begins after BOTH the portrait and metadata.
        // This prevents the overlap seen on tall metadata blocks and on GUI-scale changes.
        int bodyTop = Math.max(mediaTop + imageHeight + 9, metaEnd + 7);
        int bodyBottom = bottom - 13;
        detailBodyTop = bodyTop;
        int bodyWidth = innerWidth;

        List<DetailLine> lines = new ArrayList<>();
        if (compactMode) {
            appendWrapped(lines, label("ARMAMENTO", "ARMAMENT") + ": " + text.armament(), bodyWidth, muted);
            appendWrapped(lines, label("VARIANTES", "VARIANTS") + ": " + text.variants(), bodyWidth, muted);
            lines.add(blankLine());
        }
        appendWrapped(lines, label("PERFIL OPERATIVO", "OPERATIONAL PROFILE"), bodyWidth, accentInk(advanced));
        appendWrapped(lines, text.description(), bodyWidth, ink);
        lines.add(blankLine());
        appendWrapped(lines, label("ADVERTENCIA TÁCTICA", "TACTICAL ADVISORY"), bodyWidth, warning);
        appendWrapped(lines, text.advisory(), bodyWidth, warning);

        if (bodyBottom > bodyTop + 4) {
            int lineHeight = 11;
            int visibleLines = Math.max(1, (bodyBottom - bodyTop) / lineHeight);
            maxDetailScroll = Math.max(0, lines.size() - visibleLines);
            detailScroll = Math.max(0, Math.min(detailScroll, maxDetailScroll));

            g.fill(headerX, bodyTop - 4, x + availableWidth - pad, bodyTop - 3, paperDark);
            g.enableScissor(headerX, bodyTop, x + availableWidth - pad, bodyBottom);
            int lineY = bodyTop - detailScroll * lineHeight;
            for (DetailLine line : lines) {
                if (lineY >= bodyTop - lineHeight && lineY < bodyBottom) {
                    g.drawString(font, line.value(), headerX, lineY, line.color(), false);
                }
                lineY += lineHeight;
            }
            g.disableScissor();
        } else {
            maxDetailScroll = 0;
        }

        if (maxDetailScroll > 0) {
            String hint = label("RUEDA SOBRE EL TEXTO: LEER MÁS", "WHEEL OVER TEXT: READ MORE");
            g.drawString(font, font.plainSubstrByWidth(hint, innerWidth), headerX, bottom - 10, muted, false);
        }
    }

    private int drawMeta(GuiGraphics g, String label, String value, int x, int y, int width, int labelColor, int valueColor) {
        if (width <= 10) return y;
        g.drawString(font, label + ":", x, y, labelColor, false);
        int nextY = y + 10;
        for (FormattedCharSequence line : font.split(Component.literal(value), width)) {
            g.drawString(font, line, x, nextY, valueColor, false);
            nextY += 10;
        }
        return nextY;
    }

    private void appendWrapped(List<DetailLine> target, String value, int width, int color) {
        for (FormattedCharSequence line : font.split(Component.literal(value), Math.max(24, width))) {
            target.add(new DetailLine(line, color));
        }
    }

    private DetailLine blankLine() {
        return new DetailLine(FormattedCharSequence.forward(" ", net.minecraft.network.chat.Style.EMPTY), 0x00000000);
    }

    private int accentInk(boolean advanced) {
        return advanced ? 0xFF245F86 : 0xFF8C302B;
    }

    private String categoryLabel(String value) {
        int number = CATEGORIES.indexOf(value);
        int count = IntelCatalog.count(value);
        if (compact) {
            String shortName = switch (value) {
                case "ALL" -> "ALL";
                case "UNIT" -> spanish() ? "UNID" : "UNIT";
                case "ADVANCED" -> "ADV";
                case "TANK" -> spanish() ? "TNQ" : "TANK";
                case "BOSS" -> spanish() ? "JEF" : "BOSS";
                case "ELITE" -> "ELITE";
                case "SUPER-UNIT" -> "SUPER";
                default -> value;
            };
            return number + " " + shortName + "·" + count;
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
        return number + " // " + name + "  [" + count + "]";
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

    private record DetailLine(FormattedCharSequence value, int color) { }
}

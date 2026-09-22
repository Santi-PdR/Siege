package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** Simple category-first server guide for newcomers and quick reference. */
public final class SiegeServerGuideScreen extends Screen {
    private final Screen parent;
    private final List<SiegeButton> categoryButtons = new ArrayList<>();
    private final List<SiegeButton> entryButtons = new ArrayList<>();
    private SiegeServerGuideData.Category category = SiegeServerGuideData.Category.START;
    private List<SiegeKnowledgeData.Entry> entries = List.of();
    private SiegeKnowledgeData.Entry selected;
    private int offset;
    private int detailOffset;
    private int panelX, panelY, panelW, panelH;
    private int categoryTop, categoryRows;
    private int listX, listY, listW, detailX, detailY, detailW, detailH;
    private boolean compact;
    private boolean shortView;

    public SiegeServerGuideScreen(Screen parent) {
        super(Component.literal("SIEGE // SERVER GUIDE"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        categoryButtons.clear();
        entryButtons.clear();
        compact = width < 660 || height < 390;
        shortView = height < 280;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 38;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 18 : 24));

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 88, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));
        int atlasW = compact ? 86 : 126;
        addRenderableWidget(new SiegeButton(Math.max(8, width - atlasW - 8), 7, atlasW, 19,
                Component.literal(compact ? "ATLAS" : label("ENCICLOPEDIA", "ENCYCLOPEDIA")),
                b -> minecraft.setScreen(new SiegeKnowledgeScreen(this)), SiegeTheme.GREEN)
                .withIcon("overview").setCompactCenter(true));

        categoryTop = panelY + (shortView ? 35 : 40);
        int gap = 3;
        int cols = compact ? (panelW < 250 ? 3 : 4) : 4;
        int availableW = panelW - 20;
        int cellW = Math.max(32, (availableW - gap * (cols - 1)) / cols);
        SiegeServerGuideData.Category[] categories = SiegeServerGuideData.Category.values();
        for (int i = 0; i < categories.length; i++) {
            SiegeServerGuideData.Category value = categories[i];
            int row = i / cols;
            int col = i % cols;
            int x = panelX + 10 + col * (cellW + gap);
            int w = col == cols - 1 ? panelX + panelW - 10 - x : cellW;
            int y = categoryTop + row * 22;
            SiegeButton button = new SiegeButton(x, y, w, 19,
                    Component.literal(categoryLabel(value, w)), b -> switchCategory(value),
                    SiegeServerGuideData.accent(value)).setCompactCenter(true).setSelected(value == category);
            button.setTooltip(Tooltip.create(Component.literal(value.description(spanish()))));
            categoryButtons.add(addRenderableWidget(button));
        }
        categoryRows = (categories.length + cols - 1) / cols;

        int bodyTop = categoryTop + categoryRows * 22 + (shortView ? 7 : 11);
        if (shortView) {
            listX = panelX + 10;
            listY = bodyTop;
            listW = panelW - 20;
            detailX = detailY = detailW = detailH = 0;
        } else if (compact) {
            listX = panelX + 10;
            listY = bodyTop;
            listW = panelW - 20;
            detailX = listX;
            detailY = bodyTop + 3 * 22 + 8;
            detailW = listW;
            detailH = Math.max(34, panelY + panelH - detailY - 10);
        } else {
            listX = panelX + 10;
            listY = bodyTop;
            listW = Math.max(210, Math.min(350, panelW * 38 / 100));
            detailX = listX + listW + 10;
            detailY = bodyTop;
            detailW = panelX + panelW - 10 - detailX;
            detailH = Math.max(90, panelY + panelH - detailY - 10);
        }

        int rowCount;
        if (shortView) {
            rowCount = Math.max(2, Math.min(5, Math.max(2, (panelY + panelH - listY - 9) / 22)));
        } else {
            rowCount = compact ? 3 : Math.max(4, Math.min(9, detailH / 22));
        }
        for (int i = 0; i < rowCount; i++) {
            int slot = i;
            SiegeButton row = new SiegeButton(listX, listY + i * 22, listW, 19,
                    Component.empty(), b -> choose(slot), SiegeTheme.CYAN).withIcon("overview");
            row.visible = false;
            row.active = false;
            entryButtons.add(addRenderableWidget(row));
        }
        refresh();
    }

    private void switchCategory(SiegeServerGuideData.Category next) {
        if (next == category) return;
        category = next;
        offset = 0;
        detailOffset = 0;
        selected = null;
        SiegeUiSounds.category();
        rebuildWidgets();
    }

    private void refresh() {
        entries = SiegeServerGuideData.entries(category);
        int max = Math.max(0, entries.size() - entryButtons.size());
        offset = Math.max(0, Math.min(max, offset));
        if (selected == null || entries.stream().noneMatch(e -> e.id().equals(selected.id()))) {
            selected = entries.isEmpty() ? null : entries.get(0);
            detailOffset = 0;
        }
        for (SiegeButton button : categoryButtons) button.setSelected(false);
        int active = category.ordinal();
        if (active >= 0 && active < categoryButtons.size()) categoryButtons.get(active).setSelected(true);
        refreshRows();
    }

    private void refreshRows() {
        for (int i = 0; i < entryButtons.size(); i++) {
            SiegeButton button = entryButtons.get(i);
            int index = offset + i;
            boolean present = index >= 0 && index < entries.size();
            button.visible = present;
            button.active = present;
            if (!present) continue;
            SiegeKnowledgeData.Entry entry = entries.get(index);
            String marker = entry.zone() == SiegeKnowledgeData.Zone.HISTORY
                    ? label("ANTIGUO", "OLD") : entry.domain().label(spanish());
            button.setMessage(Component.literal(fit(marker + " · " + entry.title(spanish()), Math.max(20, listW - 28))));
            button.setSelected(!shortView && selected != null && selected.id().equals(entry.id()));
        }
    }

    private void choose(int slot) {
        int index = offset + slot;
        if (index < 0 || index >= entries.size()) return;
        selected = entries.get(index);
        detailOffset = 0;
        SiegeUiSounds.selection();
        if (shortView) {
            minecraft.setScreen(new SiegeKnowledgeFileScreen(this, selected));
            return;
        }
        refreshRows();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= listX && mouseX < listX + listW && mouseY >= listY && mouseY < listY + entryButtons.size() * 22) {
            int max = Math.max(0, entries.size() - entryButtons.size());
            offset = Math.max(0, Math.min(max, offset - (int)Math.signum(delta)));
            refreshRows();
            return true;
        }
        if (!shortView && mouseX >= detailX && mouseX < detailX + detailW && mouseY >= detailY && mouseY < detailY + detailH) {
            detailOffset = Math.max(0, detailOffset - (int)Math.signum(delta) * 2);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xC9000000 : 0xA0000000);
        int accent = SiegeServerGuideData.accent(category);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, accent);

        g.drawString(font, fit(label("GUÍA DEL SERVIDOR", "SERVER GUIDE") + " // " + SiegeRuntimeStatus.version(), panelW - 24),
                panelX + 12, panelY + 9, SiegeTheme.INK, false);
        if (!shortView) {
            g.drawString(font, fit(label(
                    "Elegí un tema: primero lo importante, después el detalle.",
                    "Choose a topic: important information first, details second."), panelW - 24),
                    panelX + 12, panelY + 21, SiegeTheme.MUTED, false);
        }

        int descY = categoryTop + categoryRows * 22 + 1;
        g.drawString(font, fit(shortView
                        ? label("Elegí una ficha para abrirla.", "Choose an entry to open it.")
                        : category.description(spanish()), panelW - 24),
                panelX + 12, descY, accent, false);

        SiegeTheme.panel(g, listX - 3, listY - 3, listW + 6, entryButtons.size() * 22 + 6, SiegeTheme.CYAN);
        if (!shortView) {
            SiegeTheme.panel(g, detailX - 3, detailY - 3, detailW + 6, detailH + 6, accent);
            renderDetail(g, accent);
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private void renderDetail(GuiGraphics g, int accent) {
        if (selected == null) {
            g.drawString(font, label("No hay información para esta categoría.", "No information for this category."),
                    detailX + 8, detailY + 8, SiegeTheme.MUTED, false);
            return;
        }
        int x = detailX + 9;
        int y = detailY + 8;
        int w = Math.max(40, detailW - 18);
        g.drawString(font, fit(selected.title(spanish()), w), x, y, SiegeTheme.INK, false);
        y += 13;
        String meta = selected.domain().label(spanish());
        if (selected.zone() == SiegeKnowledgeData.Zone.HISTORY)
            meta += label(" · PUEDE HABER CAMBIADO", " · MAY HAVE CHANGED");
        g.drawString(font, fit(meta, w), x, y, selected.zone() == SiegeKnowledgeData.Zone.HISTORY ? SiegeTheme.ORANGE : accent, false);
        y += 13;
        SiegeTheme.divider(g, x, y, w, accent);
        y += 7;

        List<FormattedCharSequence> lines = new ArrayList<>();
        FormattedCharSequence blank = Component.empty().getVisualOrderText();
        lines.addAll(font.split(Component.literal(selected.summary(spanish())), w));
        lines.add(blank);
        for (String paragraph : selected.body(spanish()).split("\\n", -1)) {
            if (paragraph.isEmpty()) lines.add(blank);
            else lines.addAll(font.split(Component.literal(paragraph), w));
        }
        List<String> related = selected.related().stream()
                .map(SiegeKnowledgeRegistry::get).filter(java.util.Objects::nonNull)
                .map(e -> e.title(spanish())).distinct().limit(5).toList();
        if (!related.isEmpty()) {
            lines.add(blank);
            lines.addAll(font.split(Component.literal(label("TAMBIÉN VER: ", "SEE ALSO: ") + String.join(" · ", related)), w));
        }

        int first = Math.max(0, Math.min(detailOffset, Math.max(0, lines.size() - 1)));
        int maxY = detailY + detailH - 8;
        int yy = y;
        g.enableScissor(detailX, yy - 1, detailX + detailW, maxY);
        for (int i = first; i < lines.size() && yy + font.lineHeight <= maxY; i++) {
            g.drawString(font, lines.get(i), x, yy, SiegeTheme.INK, false);
            yy += font.lineHeight + 2;
        }
        g.disableScissor();
    }

    private String categoryLabel(SiegeServerGuideData.Category value, int width) {
        String full = value.label(spanish());
        if (font.width(full) <= Math.max(8, width - 16)) return full;
        return switch (value) {
            case START -> label("INICIO", "START");
            case RACES -> label("RAZAS", "RACES");
            case PROGRESSION -> label("PROG.", "PROG.");
            case THREATS -> label("AMEN.", "THREAT");
            case SYSTEMS -> label("SIST.", "SYSTEMS");
            case SURVIVAL -> label("SUPERV.", "SURV.");
            case HISTORY -> label("ANTIGUO", "HISTORY");
        };
    }

    private String fit(String text, int px) {
        if (text == null || px <= 0) return "";
        if (font.width(text) <= px) return text;
        if (px <= font.width("…")) return font.plainSubstrByWidth(text, px);
        return font.plainSubstrByWidth(text, Math.max(1, px - font.width("…"))) + "…";
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }
    private String label(String es, String en) { return spanish() ? es : en; }
    @Override public void onClose() { SiegeUiSounds.back(); if (minecraft != null) minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** SIEGE 4.00 Tactical Atlas: simple player-facing guide by category. */
public final class SiegeAtlasScreen extends Screen {
    private static final int ROWS = 8;

    private final Screen parent;
    private SiegeAtlasIndex.View view;
    private final List<SiegeButton> rows = new ArrayList<>();
    private List<SiegeKnowledgeData.Entry> visible = List.of();
    private SiegeKnowledgeData.Entry selected;
    private EditBox search;
    private SiegeButton openButton;
    private int panelX, panelY, panelW, panelH;
    private int listX, listY, listW, detailX, detailY, detailW, detailH;
    private int listOffset;
    private int detailOffset;
    private boolean compact;

    public SiegeAtlasScreen(Screen parent) { this(parent, SiegeAtlasIndex.View.BRIEFING); }

    public SiegeAtlasScreen(Screen parent, SiegeAtlasIndex.View view) {
        super(Component.literal("SIEGE // TACTICAL ATLAS"));
        this.parent = parent;
        this.view = view == null ? SiegeAtlasIndex.View.BRIEFING : view;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        rows.clear();
        compact = width < 690 || height < 390;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 36;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 17 : 23));

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 86, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));

        int tabX = panelX + 10;
        int tabY = panelY + 41;
        int gap = 4;
        int cols = panelW < 560 ? 2 : 4;
        int tabW = Math.max(62, (panelW - 20 - gap * (cols - 1)) / cols);
        SiegeAtlasIndex.View[] values = SiegeAtlasIndex.View.values();
        int tabRows = (values.length + cols - 1) / cols;
        for (int i = 0; i < values.length; i++) {
            SiegeAtlasIndex.View next = values[i];
            int row = i / cols;
            int col = i % cols;
            int x = tabX + col * (tabW + gap);
            int right = col == cols - 1 ? panelX + panelW - 10 : x + tabW;
            int w = Math.max(44, right - x);
            int y = tabY + row * 23;
            SiegeButton button = new SiegeButton(x, y, w, 19,
                    Component.literal(tabLabel(next)), b -> switchView(next), tabAccent(next))
                    .setCompactCenter(true).setSelected(next == view);
            button.setTooltip(Tooltip.create(Component.literal(SiegeAtlasIndex.description(next, spanish()))));
            addRenderableWidget(button);
        }

        int searchY = tabY + tabRows * 23 + 3;
        search = new EditBox(font, panelX + 10, searchY, panelW - 20, 20,
                Component.literal(label("Buscar en Atlas", "Search Atlas")));
        search.setHint(Component.literal(label(
                "Raza, Trial, Executor, reliquia, estructura, revive, dimensión…",
                "Race, Trial, Executor, relic, structure, revive, dimension…")));
        search.setResponder(value -> { listOffset = 0; detailOffset = 0; refresh(); });
        addRenderableWidget(search);

        int bodyTop = searchY + 28;
        if (compact) {
            listX = panelX + 10;
            listY = bodyTop;
            listW = panelW - 20;
            int listRows = height < 340 ? 2 : 3;
            detailX = listX;
            detailY = listY + listRows * 22 + 8;
            detailW = listW;
            detailH = Math.max(50, panelY + panelH - detailY - 10);
        } else {
            listX = panelX + 10;
            listY = bodyTop;
            listW = Math.max(220, Math.min(360, panelW * 38 / 100));
            detailX = listX + listW + 10;
            detailY = bodyTop;
            detailW = panelX + panelW - 10 - detailX;
            detailH = Math.max(90, panelY + panelH - detailY - 10);
        }

        int rowCount = compact ? Math.max(2, Math.min(3, (detailY - listY - 6) / 22)) : ROWS;
        for (int i = 0; i < rowCount; i++) {
            int slot = i;
            SiegeButton row = new SiegeButton(listX, listY + i * 22, listW, 19,
                    Component.empty(), b -> select(slot), SiegeTheme.GOLD).withIcon("overview");
            row.visible = false;
            row.active = false;
            rows.add(addRenderableWidget(row));
        }

        openButton = new SiegeButton(detailX + Math.max(0, detailW - (compact ? 108 : 144)),
                detailY + Math.max(0, detailH - 22), Math.min(compact ? 108 : 144, detailW), 18,
                Component.literal(label("VER INFORMACIÓN", "VIEW DETAILS")), b -> openSelected(), SiegeTheme.CYAN)
                .withIcon("search").setCompactCenter(true);
        openButton.visible = false;
        addRenderableWidget(openButton);
        refresh();
    }

    private void switchView(SiegeAtlasIndex.View next) {
        if (next == view) return;
        view = next;
        listOffset = 0;
        detailOffset = 0;
        selected = null;
        SiegeUiSounds.category();
        rebuildWidgets();
    }

    private void refresh() {
        String query = search == null ? "" : search.getValue();
        visible = SiegeAtlasIndex.entries(view, query, spanish(), 128);
        int max = Math.max(0, visible.size() - rows.size());
        listOffset = Math.max(0, Math.min(max, listOffset));
        if (selected == null || visible.stream().noneMatch(e -> e.id().equals(selected.id()))) {
            selected = visible.isEmpty() ? null : visible.get(0);
            detailOffset = 0;
        }
        refreshRows();
    }

    private void refreshRows() {
        for (int i = 0; i < rows.size(); i++) {
            SiegeButton button = rows.get(i);
            int index = listOffset + i;
            boolean present = index >= 0 && index < visible.size();
            button.visible = present;
            button.active = present;
            if (!present) continue;
            SiegeKnowledgeData.Entry entry = visible.get(index);
            String status = entry.zone() == SiegeKnowledgeData.Zone.HISTORY
                    ? label("HIST", "HIST") : entry.domain().label(spanish());
            button.setMessage(Component.literal(status + " · " + entry.title(spanish())));
            button.setSelected(selected != null && selected.id().equals(entry.id()));
        }
        if (openButton != null) {
            openButton.visible = selected != null;
            openButton.active = selected != null;
        }
    }

    private void select(int slot) {
        int index = listOffset + slot;
        if (index < 0 || index >= visible.size()) return;
        selected = visible.get(index);
        detailOffset = 0;
        SiegeUiSounds.selection();
        refreshRows();
    }

    private void openSelected() {
        if (selected == null) return;
        SiegeUiSounds.confirm();
        minecraft.setScreen(new SiegeKnowledgeFileScreen(this, selected));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= listX && mouseX < listX + listW && mouseY >= listY && mouseY < listY + rows.size() * 22) {
            int max = Math.max(0, visible.size() - rows.size());
            listOffset = Math.max(0, Math.min(max, listOffset - (int)Math.signum(delta)));
            refreshRows();
            return true;
        }
        if (mouseX >= detailX && mouseX < detailX + detailW && mouseY >= detailY && mouseY < detailY + detailH) {
            detailOffset = Math.max(0, detailOffset - (int)Math.signum(delta) * 2);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xC5000000 : 0x98000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, tabAccent(view));

        int x = panelX + 12;
        String title = label("ATLAS TÁCTICO", "TACTICAL ATLAS") + " // " + SiegeRuntimeStatus.version();
        g.drawString(font, fit(title, panelW - 24), x, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(SiegeAtlasIndex.description(view, spanish()), panelW - 24),
                x, panelY + 21, SiegeTheme.MUTED, false);
        String metrics = SiegeAtlasIndex.label(view, spanish()) + " " + visible.size()
                + " / " + SiegeAtlasIndex.count(view) + "   ·   "
                + label("TEMAS ", "TOPICS ") + SiegeKnowledgeRegistry.entries().size();
        g.drawString(font, fit(metrics, panelW - 24), x, panelY + 32, tabAccent(view), false);

        SiegeTheme.panel(g, listX - 3, listY - 3, listW + 6, rows.size() * 22 + 6, SiegeTheme.GOLD);
        SiegeTheme.panel(g, detailX - 3, detailY - 3, detailW + 6, detailH + 6, tabAccent(view));
        if (visible.isEmpty()) {
            g.drawString(font, label("No hay temas que coincidan.", "No matching topics."),
                    listX + 7, listY + 7, SiegeTheme.MUTED, false);
        }
        renderDetail(g);
        if (search != null) {
            SiegeTheme.frame(g, search.getX() - 1, search.getY() - 1,
                    search.getWidth() + 2, search.getHeight() + 2,
                    search.isFocused() ? SiegeTheme.FOCUS : tabAccent(view));
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private void renderDetail(GuiGraphics g) {
        if (selected == null) {
            g.drawString(font, label("Seleccioná un tema.", "Select a topic."),
                    detailX + 8, detailY + 8, SiegeTheme.MUTED, false);
            return;
        }
        int x = detailX + 8;
        int y = detailY + 7;
        int textW = Math.max(40, detailW - 16);
        int accent = statusAccent(selected);
        g.drawString(font, fit(selected.title(spanish()), textW), x, y, SiegeTheme.INK, false);
        g.drawString(font, fit(simpleStatus(selected) + " · " + selected.domain().label(spanish()), textW),
                x, y + 12, accent, false);
        SiegeTheme.divider(g, x, y + 24, textW, accent);

        String text = selected.summary(spanish()) + "\n\n" + selected.body(spanish());
        List<FormattedCharSequence> lines = new ArrayList<>();
        FormattedCharSequence blank = Component.empty().getVisualOrderText();
        for (String paragraph : text.split("\\n", -1)) {
            if (paragraph.isEmpty()) lines.add(blank);
            else lines.addAll(font.split(Component.literal(paragraph), textW));
        }
        int maxY = detailY + detailH - (openButton != null && openButton.visible ? 28 : 8);
        int first = Math.max(0, Math.min(detailOffset, Math.max(0, lines.size() - 1)));
        int yy = y + 31;
        g.enableScissor(detailX, yy - 1, detailX + detailW, maxY);
        for (int i = first; i < lines.size() && yy + font.lineHeight <= maxY; i++) {
            g.drawString(font, lines.get(i), x, yy, SiegeTheme.INK, false);
            yy += font.lineHeight + 2;
        }
        g.disableScissor();
    }

    private String simpleStatus(SiegeKnowledgeData.Entry entry) {
        if (entry.zone() == SiegeKnowledgeData.Zone.HISTORY
                || entry.confidence() == SiegeKnowledgeData.Confidence.HISTORICAL) return label("HISTÓRICO", "HISTORY");
        if (entry.confidence() == SiegeKnowledgeData.Confidence.CONTRADICTION) return label("CAMBIÓ", "CHANGED");
        if (entry.confidence() == SiegeKnowledgeData.Confidence.UNCONFIRMED) return label("POR CONFIRMAR", "UNCONFIRMED");
        return label("GENERAL", "GENERAL");
    }

    private int statusAccent(SiegeKnowledgeData.Entry entry) {
        if (entry.zone() == SiegeKnowledgeData.Zone.HISTORY
                || entry.confidence() == SiegeKnowledgeData.Confidence.HISTORICAL) return SiegeTheme.ORANGE;
        if (entry.confidence() == SiegeKnowledgeData.Confidence.CONTRADICTION) return SiegeTheme.RED;
        if (entry.confidence() == SiegeKnowledgeData.Confidence.UNCONFIRMED) return SiegeTheme.MUTED;
        return tabAccent(view);
    }

    private String tabLabel(SiegeAtlasIndex.View value) {
        return switch (value) {
            case BRIEFING -> label("EMPEZAR", "START");
            case RACES -> label("RAZAS", "RACES");
            case SYSTEMS -> label("SISTEMAS", "SYSTEMS");
            case RESEARCH -> label("HISTÓRICO", "HISTORY");
        };
    }

    private int tabAccent(SiegeAtlasIndex.View value) {
        return switch (value) {
            case BRIEFING -> SiegeTheme.ORANGE;
            case RACES -> SiegeTheme.GREEN;
            case SYSTEMS -> SiegeTheme.CYAN;
            case RESEARCH -> SiegeTheme.RED;
        };
    }

    private String fit(String value, int pixels) {
        if (value == null || pixels <= 0) return "";
        if (font.width(value) <= pixels) return value;
        if (pixels <= font.width("…")) return font.plainSubstrByWidth(value, pixels);
        return font.plainSubstrByWidth(value, pixels - font.width("…")) + "…";
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private String label(String es, String en) { return spanish() ? es : en; }

    @Override
    public void onClose() { minecraft.setScreen(parent); }
}

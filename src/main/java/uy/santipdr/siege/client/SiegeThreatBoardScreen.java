package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** SIEGE 4.00 unified threat board without merging source domains. */
public final class SiegeThreatBoardScreen extends Screen {
    private enum Mode { DOSSIERS, EXECUTORS, BOSSES, EVENTS }
    private record ThreatRef(String id, String title, String subtitle, String body,
                             int accent, IntelEntry intel, SiegeKnowledgeData.Entry knowledge) { }

    private final Screen parent;
    private final List<SiegeButton> rows = new ArrayList<>();
    private List<ThreatRef> visible = List.of();
    private ThreatRef selected;
    private EditBox search;
    private SiegeButton openButton;
    private Mode mode = Mode.DOSSIERS;
    private int panelX, panelY, panelW, panelH;
    private int listX, listY, listW, detailX, detailY, detailW, detailH;
    private int listOffset, detailOffset;
    private boolean compact;

    public SiegeThreatBoardScreen(Screen parent) {
        super(Component.literal("SIEGE // THREAT BOARD"));
        this.parent = parent;
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

        int tabX = panelX + 10, tabY = panelY + 40, gap = 4;
        int tabW = Math.max(44, (panelW - 20 - gap * 3) / 4);
        Mode[] values = Mode.values();
        for (int i = 0; i < values.length; i++) {
            Mode next = values[i];
            int x = tabX + i * (tabW + gap);
            int w = i == values.length - 1 ? panelX + panelW - 10 - x : tabW;
            SiegeButton button = new SiegeButton(x, tabY, w, 19, Component.literal(modeLabel(next)),
                    b -> switchMode(next), modeAccent(next)).setCompactCenter(true).setSelected(next == mode);
            button.setTooltip(Tooltip.create(Component.literal(modeDescription(next))));
            addRenderableWidget(button);
        }

        search = new EditBox(font, panelX + 10, tabY + 25, panelW - 20, 20,
                Component.literal(label("Buscar amenaza", "Search threat")));
        search.setHint(Component.literal(label(
                "Buscar unidad, código, categoría, Executor, boss o evento…",
                "Search unit, code, category, Executor, boss or event…")));
        search.setResponder(value -> { listOffset = 0; detailOffset = 0; refresh(); });
        addRenderableWidget(search);

        int bodyTop = tabY + 52;
        if (compact) {
            listX = panelX + 10; listY = bodyTop; listW = panelW - 20;
            detailX = listX; detailY = bodyTop + 3 * 22 + 7; detailW = listW;
            detailH = Math.max(52, panelY + panelH - detailY - 10);
        } else {
            listX = panelX + 10; listY = bodyTop;
            listW = Math.max(220, Math.min(360, panelW * 38 / 100));
            detailX = listX + listW + 10; detailY = bodyTop;
            detailW = panelX + panelW - 10 - detailX;
            detailH = Math.max(90, panelY + panelH - detailY - 10);
        }

        int rowCount = compact ? 3 : 8;
        for (int i = 0; i < rowCount; i++) {
            int slot = i;
            SiegeButton row = new SiegeButton(listX, listY + i * 22, listW, 19,
                    Component.empty(), b -> select(slot), SiegeTheme.RED).withIcon("intel");
            row.visible = false; row.active = false;
            rows.add(addRenderableWidget(row));
        }

        openButton = new SiegeButton(detailX + Math.max(0, detailW - (compact ? 108 : 144)),
                detailY + Math.max(0, detailH - 22), Math.min(compact ? 108 : 144, detailW), 18,
                Component.literal(label("ABRIR ARCHIVO", "OPEN FILE")), b -> openSelected(), SiegeTheme.RED)
                .withIcon("intel").setCompactCenter(true);
        openButton.visible = false;
        addRenderableWidget(openButton);
        refresh();
    }

    private void switchMode(Mode next) {
        if (next == mode) return;
        mode = next; listOffset = 0; detailOffset = 0; selected = null;
        SiegeUiSounds.category(); rebuildWidgets();
    }

    private void refresh() {
        String q = search == null ? "" : SiegeOperationsIndex.normalize(search.getValue());
        List<ThreatRef> all = buildRefs();
        if (!q.isBlank()) {
            all = all.stream().filter(ref -> SiegeOperationsIndex.normalize(
                    ref.id() + " " + ref.title() + " " + ref.subtitle() + " " + ref.body()).contains(q)).toList();
        }
        visible = all;
        int max = Math.max(0, visible.size() - rows.size());
        listOffset = Math.max(0, Math.min(max, listOffset));
        if (selected == null || visible.stream().noneMatch(e -> e.id().equals(selected.id()))) {
            selected = visible.isEmpty() ? null : visible.get(0); detailOffset = 0;
        }
        refreshRows();
    }

    private List<ThreatRef> buildRefs() {
        List<ThreatRef> out = new ArrayList<>();
        if (mode == Mode.DOSSIERS) {
            for (IntelEntry intel : IntelCatalog.files()) {
                IntelEntry.IntelText text = intel.text(spanish());
                out.add(new ThreatRef("intel:" + intel.code(), intel.name(),
                        intel.code() + " · " + intel.category() + " · HP " + intel.hp(),
                        text.description() + "\n\n" + text.advisory(), accentForCategory(intel.category()), intel, null));
            }
            out.sort(Comparator.comparing(ThreatRef::title));
            return out;
        }
        SiegeKnowledgeData.Domain domain = switch (mode) {
            case EXECUTORS -> SiegeKnowledgeData.Domain.EXECUTORS;
            case BOSSES -> SiegeKnowledgeData.Domain.BOSSES;
            case EVENTS -> SiegeKnowledgeData.Domain.RAIDS_EVENTS;
            case DOSSIERS -> throw new IllegalStateException();
        };
        for (SiegeKnowledgeData.Entry entry : SiegeKnowledgeRegistry.entries()) {
            if (entry.domain() != domain) continue;
            out.add(new ThreatRef("knowledge:" + entry.id(), entry.title(spanish()),
                    entry.domain().label(spanish()) + " · " + entry.confidence().label(spanish()),
                    entry.summary(spanish()) + "\n\n" + entry.body(spanish()),
                    SiegeKnowledgeData.confidenceAccent(entry.confidence()), null, entry));
        }
        out.sort(Comparator.comparing(ThreatRef::title));
        return out;
    }

    private void refreshRows() {
        for (int i = 0; i < rows.size(); i++) {
            SiegeButton button = rows.get(i);
            int index = listOffset + i;
            boolean present = index >= 0 && index < visible.size();
            button.visible = present; button.active = present;
            if (!present) continue;
            ThreatRef ref = visible.get(index);
            button.setMessage(Component.literal(ref.title() + "  //  " + ref.subtitle()));
            button.setSelected(selected != null && selected.id().equals(ref.id()));
        }
        if (openButton != null) { openButton.visible = selected != null; openButton.active = selected != null; }
    }

    private void select(int slot) {
        int index = listOffset + slot;
        if (index < 0 || index >= visible.size()) return;
        selected = visible.get(index); detailOffset = 0; SiegeUiSounds.selection(); refreshRows();
    }

    private void openSelected() {
        if (selected == null) return;
        SiegeUiSounds.confirm();
        if (selected.intel() != null) minecraft.setScreen(new IntelScreenV3(this, selected.intel()));
        else if (selected.knowledge() != null) minecraft.setScreen(new SiegeKnowledgeFileScreen(this, selected.knowledge()));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= listX && mouseX < listX + listW && mouseY >= listY && mouseY < listY + rows.size() * 22) {
            int max = Math.max(0, visible.size() - rows.size());
            listOffset = Math.max(0, Math.min(max, listOffset - (int)Math.signum(delta))); refreshRows(); return true;
        }
        if (mouseX >= detailX && mouseX < detailX + detailW && mouseY >= detailY && mouseY < detailY + detailH) {
            detailOffset = Math.max(0, detailOffset - (int)Math.signum(delta) * 2); return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying(); SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xC8000000 : 0x99000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, modeAccent(mode));
        int x = panelX + 12;
        g.drawString(font, fit(label("TABLERO DE AMENAZAS", "THREAT BOARD") + " // " + SiegeRuntimeStatus.version(), panelW - 24),
                x, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label("Intel verificado y archivo del servidor se consultan juntos sin mezclar sus fuentes.",
                "Verified Intel and server archive are viewed together without merging their sources."), panelW - 24),
                x, panelY + 21, SiegeTheme.MUTED, false);
        g.drawString(font, fit(modeDescription(mode) + " · " + visible.size(), panelW - 24), x, panelY + 32, modeAccent(mode), false);
        SiegeTheme.panel(g, listX - 3, listY - 3, listW + 6, rows.size() * 22 + 6, SiegeTheme.RED);
        SiegeTheme.panel(g, detailX - 3, detailY - 3, detailW + 6, detailH + 6, modeAccent(mode));
        if (visible.isEmpty()) g.drawString(font, label("No hay registros que coincidan.", "No matching records."),
                listX + 7, listY + 7, SiegeTheme.MUTED, false);
        renderDetail(g);
        if (search != null) SiegeTheme.frame(g, search.getX() - 1, search.getY() - 1,
                search.getWidth() + 2, search.getHeight() + 2, search.isFocused() ? SiegeTheme.FOCUS : modeAccent(mode));
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children()); SiegeScreenChrome.renderOverlay(this, g);
    }

    private void renderDetail(GuiGraphics g) {
        if (selected == null) { g.drawString(font, label("Seleccioná una amenaza.", "Select a threat."),
                detailX + 8, detailY + 8, SiegeTheme.MUTED, false); return; }
        int x = detailX + 8, y = detailY + 7, textW = Math.max(40, detailW - 16);
        g.drawString(font, fit(selected.title(), textW), x, y, SiegeTheme.INK, false);
        g.drawString(font, fit(selected.subtitle(), textW), x, y + 12, selected.accent(), false);
        SiegeTheme.divider(g, x, y + 24, textW, selected.accent());
        List<FormattedCharSequence> lines = new ArrayList<>();
        FormattedCharSequence blank = Component.empty().getVisualOrderText();
        for (String paragraph : selected.body().split("\\n", -1)) {
            if (paragraph.isEmpty()) lines.add(blank); else lines.addAll(font.split(Component.literal(paragraph), textW));
        }
        int maxY = detailY + detailH - (openButton != null && openButton.visible ? 28 : 8);
        int first = Math.max(0, Math.min(detailOffset, Math.max(0, lines.size() - 1))), yy = y + 31;
        g.enableScissor(detailX, yy - 1, detailX + detailW, maxY);
        for (int i = first; i < lines.size() && yy + font.lineHeight <= maxY; i++) {
            g.drawString(font, lines.get(i), x, yy, SiegeTheme.INK, false); yy += font.lineHeight + 2;
        }
        g.disableScissor();
    }

    private String modeLabel(Mode value) { return switch (value) {
        case DOSSIERS -> "DOSSIERS"; case EXECUTORS -> label("EJECUTORES", "EXECUTORS");
        case BOSSES -> "BOSSES"; case EVENTS -> label("EVENTOS", "EVENTS"); }; }
    private String modeDescription(Mode value) { return switch (value) {
        case DOSSIERS -> label("Unidades clasificadas por Intel", "Units classified by Intel");
        case EXECUTORS -> label("Ejecutores recuperados del archivo", "Executors recovered from the archive");
        case BOSSES -> label("Bosses y reglas de adaptación", "Bosses and adaptation rules");
        case EVENTS -> label("Raids, hordas y riesgos de área", "Raids, hordes and area risks"); }; }
    private int modeAccent(Mode value) { return switch (value) {
        case DOSSIERS -> SiegeTheme.GOLD; case EXECUTORS -> SiegeTheme.RED;
        case BOSSES -> SiegeTheme.ORANGE; case EVENTS -> SiegeTheme.CYAN; }; }
    private int accentForCategory(String category) {
        if (category == null) return SiegeTheme.MUTED;
        return switch (category) { case "BOSS", "SUPER-UNIT" -> SiegeTheme.RED; case "TANK", "ELITE" -> SiegeTheme.ORANGE;
            case "ADVANCED" -> SiegeTheme.GOLD; case "UNKNOWN" -> SiegeTheme.MUTED; default -> SiegeTheme.CYAN; };
    }
    private String fit(String value, int pixels) {
        if (value == null || pixels <= 0) return ""; if (font.width(value) <= pixels) return value;
        if (pixels <= font.width("…")) return font.plainSubstrByWidth(value, pixels);
        return font.plainSubstrByWidth(value, pixels - font.width("…")) + "…";
    }
    private boolean spanish() { return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_"); }
    private String label(String es, String en) { return spanish() ? es : en; }
    @Override public void onClose() { minecraft.setScreen(parent); }
}

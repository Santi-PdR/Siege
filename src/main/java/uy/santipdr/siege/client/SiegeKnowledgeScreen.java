package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/**
 * SIEGE 3.00 knowledge vault. Current player notes and the Eternal Craft
 * encyclopedia are separate surfaces inside the same screen. Historical or
 * uncertain records keep their confidence/date, and spoiler entries are masked
 * until explicitly revealed for this screen session.
 */
public final class SiegeKnowledgeScreen extends Screen {
    private enum Mode { CURRENT, ENCYCLOPEDIA, SURVIVAL, SOURCES }
    private static final int ROWS = 7;

    private final Screen parent;
    private final String requestedId;
    private final Set<String> revealed = new HashSet<>();
    private final List<SiegeButton> entryButtons = new ArrayList<>();
    private List<SiegeKnowledgeData.Entry> visibleEntries = List.of();
    private SiegeKnowledgeData.Entry selected;
    private EditBox search;
    private SiegeButton revealButton;
    private Mode mode = Mode.CURRENT;
    private int listOffset;
    private int detailOffset;
    private int panelX, panelY, panelW, panelH;
    private int listX, listY, listW, detailX, detailY, detailW, detailH;
    private boolean compact;

    public SiegeKnowledgeScreen(Screen parent) { this(parent, null); }

    public SiegeKnowledgeScreen(Screen parent, String requestedId) {
        super(Component.literal("SIEGE // KNOWLEDGE VAULT"));
        this.parent = parent;
        this.requestedId = requestedId;
        if (requestedId != null && !requestedId.isBlank()) mode = Mode.ENCYCLOPEDIA;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        entryButtons.clear();
        compact = width < 650 || height < 380;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 36;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 17 : 23));

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 86, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));

        int modeX = panelX + 10;
        int modeY = panelY + 35;
        int gap = 4;
        int modeW = Math.max(48, (panelW - 20 - gap * 3) / 4);
        for (int i = 0; i < Mode.values().length; i++) {
            Mode value = Mode.values()[i];
            int x = modeX + i * (modeW + gap);
            int w = i == Mode.values().length - 1 ? panelX + panelW - 10 - x : modeW;
            SiegeButton button = new SiegeButton(x, modeY, w, 19, Component.literal(modeLabel(value)),
                    b -> switchMode(value), modeAccent(value)).setCompactCenter(true).setSelected(value == mode);
            button.setTooltip(Tooltip.create(Component.literal(modeDescription(value))));
            addRenderableWidget(button);
        }

        search = new EditBox(font, panelX + 10, modeY + 25, panelW - 20, 20,
                Component.literal(label("Buscar conocimiento", "Search knowledge")));
        search.setHint(Component.literal(label(
                "Buscar Deteriorer, RE, Geography Table, revive, prompts…",
                "Search Deteriorer, RE, Geography Table, revive, prompts…")));
        search.setResponder(value -> { listOffset = 0; refresh(); });
        addRenderableWidget(search);

        int bodyTop = modeY + 51;
        if (compact) {
            listX = panelX + 10;
            listY = bodyTop;
            listW = panelW - 20;
            detailX = listX;
            detailY = bodyTop + 3 * 22 + 8;
            detailW = listW;
            detailH = Math.max(46, panelY + panelH - detailY - 10);
        } else {
            listX = panelX + 10;
            listY = bodyTop;
            listW = Math.max(200, Math.min(330, panelW * 37 / 100));
            detailX = listX + listW + 10;
            detailY = bodyTop;
            detailW = panelX + panelW - 10 - detailX;
            detailH = Math.max(80, panelY + panelH - detailY - 10);
        }

        int rowCount = compact ? 3 : ROWS;
        for (int i = 0; i < rowCount; i++) {
            int slot = i;
            SiegeButton row = new SiegeButton(listX, listY + i * 22, listW, 19,
                    Component.empty(), b -> selectVisible(slot), SiegeTheme.GOLD)
                    .withIcon("overview");
            row.visible = false;
            row.active = false;
            entryButtons.add(addRenderableWidget(row));
        }

        revealButton = new SiegeButton(detailX + Math.max(0, detailW - 126),
                detailY + Math.max(0, detailH - 22), Math.min(126, detailW), 18,
                Component.literal(label("REVELAR ARCHIVO", "REVEAL FILE")), b -> revealSelected(), SiegeTheme.ORANGE)
                .withIcon("eye").setCompactCenter(true);
        revealButton.visible = false;
        addRenderableWidget(revealButton);

        refresh();
        if (requestedId != null) selectById(requestedId);
    }

    private void switchMode(Mode next) {
        if (next == mode) return;
        mode = next;
        listOffset = 0;
        detailOffset = 0;
        SiegeUiSounds.category();
        rebuildWidgets();
    }

    private void refresh() {
        String query = search == null ? "" : search.getValue();
        boolean es = spanish();
        List<SiegeKnowledgeData.Entry> base = switch (mode) {
            case CURRENT -> SiegeKnowledgeData.search(query, es, SiegeKnowledgeData.Zone.CURRENT, 128);
            case ENCYCLOPEDIA -> SiegeKnowledgeData.search(query, es, SiegeKnowledgeData.Zone.ENCYCLOPEDIA, 128);
            case SURVIVAL -> SiegeKnowledgeData.search(query, es, null, 128).stream()
                    .filter(SiegeKnowledgeData.Entry::critical).toList();
            case SOURCES -> SiegeKnowledgeData.search(query, es, SiegeKnowledgeData.Zone.ENCYCLOPEDIA, 128).stream()
                    .filter(e -> e.domain() == SiegeKnowledgeData.Domain.SOURCES).toList();
        };
        visibleEntries = base;
        int rows = entryButtons.size();
        int maxOffset = Math.max(0, visibleEntries.size() - rows);
        listOffset = Math.max(0, Math.min(maxOffset, listOffset));
        if (selected == null || visibleEntries.stream().noneMatch(e -> e.id().equals(selected.id()))) {
            selected = visibleEntries.isEmpty() ? null : visibleEntries.get(0);
            detailOffset = 0;
        }
        refreshButtons();
    }

    private void refreshButtons() {
        for (int i = 0; i < entryButtons.size(); i++) {
            SiegeButton button = entryButtons.get(i);
            int index = listOffset + i;
            boolean present = index >= 0 && index < visibleEntries.size();
            button.visible = present;
            button.active = present;
            if (!present) continue;
            SiegeKnowledgeData.Entry entry = visibleEntries.get(index);
            String prefix = entry.zone() == SiegeKnowledgeData.Zone.CURRENT ? label("ACTUAL", "CURRENT")
                    : entry.domain().label(spanish());
            button.setMessage(Component.literal(prefix + " · " + entry.title(spanish())));
            button.setSelected(selected != null && selected.id().equals(entry.id()));
        }
        updateRevealButton();
    }

    private void selectVisible(int slot) {
        int index = listOffset + slot;
        if (index < 0 || index >= visibleEntries.size()) return;
        selected = visibleEntries.get(index);
        detailOffset = 0;
        SiegeUiSounds.selection();
        refreshButtons();
    }

    private void selectById(String id) {
        if (id == null) return;
        SiegeKnowledgeData.Entry entry = SiegeKnowledgeData.get(id);
        if (entry == null) return;
        mode = entry.zone() == SiegeKnowledgeData.Zone.CURRENT ? Mode.CURRENT : Mode.ENCYCLOPEDIA;
        selected = entry;
        String query = search == null ? "" : search.getValue();
        if (!query.isBlank()) search.setValue("");
        refresh();
        for (int i = 0; i < visibleEntries.size(); i++) {
            if (visibleEntries.get(i).id().equals(id)) {
                listOffset = Math.max(0, i - 1);
                break;
            }
        }
        refreshButtons();
    }

    private void revealSelected() {
        if (selected == null) return;
        revealed.add(selected.id());
        SiegeUiSounds.confirm();
        updateRevealButton();
    }

    private void updateRevealButton() {
        if (revealButton == null) return;
        revealButton.visible = selected != null && selected.spoiler() && !revealed.contains(selected.id());
        revealButton.active = revealButton.visible;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= listX && mouseX < listX + listW && mouseY >= listY && mouseY < listY + entryButtons.size() * 22) {
            int max = Math.max(0, visibleEntries.size() - entryButtons.size());
            listOffset = Math.max(0, Math.min(max, listOffset - (int)Math.signum(delta)));
            refreshButtons();
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
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xC2000000 : 0x96000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, SiegeTheme.CYAN);

        String title = label("ARCHIVO DE CONOCIMIENTO", "KNOWLEDGE VAULT") + " // " + SiegeRuntimeStatus.version();
        g.drawString(font, fit(title, panelW - 24), panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "SIEGE ACTUAL permanece separado de la Enciclopedia histórica del Discord.",
                "CURRENT SIEGE stays separate from the historical Discord Encyclopedia."), panelW - 24),
                panelX + 12, panelY + 21, SiegeTheme.MUTED, false);

        SiegeTheme.panel(g, listX - 3, listY - 3, listW + 6, entryButtons.size() * 22 + 6, SiegeTheme.GOLD);
        SiegeTheme.panel(g, detailX - 3, detailY - 3, detailW + 6, detailH + 6, SiegeTheme.CYAN);

        if (visibleEntries.isEmpty()) {
            g.drawString(font, label("No hay registros que coincidan.", "No matching records."),
                    listX + 7, listY + 7, SiegeTheme.MUTED, false);
        }

        renderDetail(g);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private void renderDetail(GuiGraphics g) {
        if (selected == null) {
            g.drawString(font, label("Seleccioná un registro.", "Select a record."),
                    detailX + 8, detailY + 8, SiegeTheme.MUTED, false);
            return;
        }

        int x = detailX + 8;
        int y = detailY + 7;
        int textW = Math.max(40, detailW - 16);
        int confidence = SiegeKnowledgeData.confidenceAccent(selected.confidence());
        g.drawString(font, fit(selected.title(spanish()), textW), x, y, SiegeTheme.INK, false);
        g.drawString(font, fit(selected.domain().label(spanish()) + " · "
                + SiegeKnowledgeData.sourceLine(selected, spanish()), textW), x, y + 12, confidence, false);
        SiegeTheme.divider(g, x, y + 24, textW, confidence);

        boolean locked = selected.spoiler() && !revealed.contains(selected.id());
        String text = locked
                ? label("ARCHIVO CON SPOILERS\n\n" + selected.summary(true)
                        + "\n\nEl detalle completo está oculto. Revelalo sólo si querés consultar conocimiento recuperado del Discord que puede adelantarte mecánicas.",
                        "SPOILER FILE\n\n" + selected.summary(false)
                        + "\n\nFull detail is hidden. Reveal it only if you want to consult recovered Discord knowledge that may expose mechanics early.")
                : selected.body(spanish());

        List<FormattedCharSequence> lines = new ArrayList<>();
        for (String paragraph : text.split("\\n", -1)) {
            if (paragraph.isEmpty()) lines.add(FormattedCharSequence.EMPTY);
            else lines.addAll(font.split(Component.literal(paragraph), textW));
        }

        if (!locked && !selected.related().isEmpty()) {
            lines.add(FormattedCharSequence.EMPTY);
            lines.addAll(font.split(Component.literal(label("RELACIONADO: ", "RELATED: ")
                    + String.join(" · ", selected.related())), textW));
        }
        if (!locked && !selected.sources().isEmpty()) {
            lines.add(FormattedCharSequence.EMPTY);
            lines.addAll(font.split(Component.literal(label("FUENTES", "SOURCES")), textW));
            for (SiegeKnowledgeData.Source source : selected.sources()) {
                String sourceText = "• " + source.confidence().label(spanish())
                        + (source.date().isBlank() ? "" : " · " + source.date())
                        + (source.channel().isBlank() ? "" : " · " + source.channel())
                        + (source.note(spanish()).isBlank() ? "" : " — " + source.note(spanish()));
                lines.addAll(font.split(Component.literal(sourceText), textW));
            }
        }

        int first = Math.max(0, Math.min(detailOffset, Math.max(0, lines.size() - 1)));
        int maxY = detailY + detailH - (revealButton != null && revealButton.visible ? 28 : 8);
        int yy = y + 31;
        g.enableScissor(detailX, yy - 1, detailX + detailW, maxY);
        for (int i = first; i < lines.size() && yy + font.lineHeight <= maxY; i++) {
            g.drawString(font, lines.get(i), x, yy, i == first && locked ? SiegeTheme.ORANGE : SiegeTheme.INK, false);
            yy += font.lineHeight + 2;
        }
        g.disableScissor();
    }

    private String modeLabel(Mode value) {
        return switch (value) {
            case CURRENT -> label("SIEGE ACTUAL", "CURRENT SIEGE");
            case ENCYCLOPEDIA -> label("ENCICLOPEDIA", "ENCYCLOPEDIA");
            case SURVIVAL -> label("SUPERVIVENCIA", "SURVIVAL");
            case SOURCES -> label("FUENTES", "SOURCES");
        };
    }

    private String modeDescription(Mode value) {
        return switch (value) {
            case CURRENT -> label("Último estado y herramientas registradas de tu partida.", "Latest recorded state and tools from your run.");
            case ENCYCLOPEDIA -> label("Conocimiento recuperado del Discord, fechado y con nivel de confianza.", "Recovered Discord knowledge with dates and confidence.");
            case SURVIVAL -> label("Advertencias y consejos que pueden evitar muerte, pérdida o daño accidental.", "Warnings and advice that can prevent death, loss or accidental damage.");
            case SOURCES -> label("Auditoría del export y reglas para distinguir confirmado, histórico y no confirmado.", "Export audit and rules for distinguishing confirmed, historical and unconfirmed data.");
        };
    }

    private int modeAccent(Mode value) {
        return switch (value) {
            case CURRENT -> SiegeTheme.GREEN;
            case ENCYCLOPEDIA -> SiegeTheme.GOLD;
            case SURVIVAL -> SiegeTheme.ORANGE;
            case SOURCES -> SiegeTheme.CYAN;
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
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}

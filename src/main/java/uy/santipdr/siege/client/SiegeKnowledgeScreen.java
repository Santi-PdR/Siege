package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/**
 * SIEGE 3.00 server encyclopedia.
 *
 * This surface is intentionally non-personal: it explains Eternal Craft/SIEGE
 * systems, races, progression and dated history for any player who wants to
 * learn a topic. Player profiles, inventories and private progress do not live
 * here.
 */
public final class SiegeKnowledgeScreen extends Screen {
    private enum Mode { START, RACES, SYSTEMS, HISTORY }
    private static final int ROWS = 7;

    private final Screen parent;
    private final String requestedId;
    private final List<SiegeButton> entryButtons = new ArrayList<>();
    private List<SiegeKnowledgeData.Entry> visibleEntries = List.of();
    private SiegeKnowledgeData.Entry selected;
    private EditBox search;
    private Mode mode = Mode.START;
    private int listOffset;
    private int detailOffset;
    private int panelX, panelY, panelW, panelH;
    private int listX, listY, listW, detailX, detailY, detailW, detailH;
    private boolean compact;
    private boolean requestedApplied;

    public SiegeKnowledgeScreen(Screen parent) { this(parent, null); }

    public SiegeKnowledgeScreen(Screen parent, String requestedId) {
        super(Component.literal("SIEGE // SERVER ENCYCLOPEDIA"));
        this.parent = parent;
        this.requestedId = requestedId;
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
                Component.literal(label("Buscar tema del servidor", "Search server topic")));
        search.setHint(Component.literal(label(
                "Buscar raza, rareza, Trial, Executor, reliquia, revive…",
                "Search race, rarity, Trial, Executor, relic, revive…")));
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

        refresh();
        if (!requestedApplied && requestedId != null) {
            requestedApplied = true;
            selectById(requestedId);
        }
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
        List<SiegeKnowledgeData.Entry> searched = SiegeKnowledgeData.search(query, es, null, 128);
        visibleEntries = searched.stream().filter(this::belongsToMode).toList();

        int rows = entryButtons.size();
        int maxOffset = Math.max(0, visibleEntries.size() - rows);
        listOffset = Math.max(0, Math.min(maxOffset, listOffset));
        if (selected == null || visibleEntries.stream().noneMatch(e -> e.id().equals(selected.id()))) {
            selected = visibleEntries.isEmpty() ? null : visibleEntries.get(0);
            detailOffset = 0;
        }
        refreshButtons();
    }

    private boolean belongsToMode(SiegeKnowledgeData.Entry entry) {
        if (entry == null) return false;
        return switch (mode) {
            case START -> isBeginnerEntry(entry);
            case RACES -> entry.domain() == SiegeKnowledgeData.Domain.RACES
                    || entry.id().equals("rarity-order")
                    || entry.id().equals("progression-v1-v4")
                    || entry.id().equals("fabled-acquisition");
            case SYSTEMS -> entry.zone() == SiegeKnowledgeData.Zone.SERVER
                    && entry.domain() != SiegeKnowledgeData.Domain.RACES
                    && entry.domain() != SiegeKnowledgeData.Domain.SOURCES;
            case HISTORY -> entry.zone() == SiegeKnowledgeData.Zone.HISTORY
                    || entry.domain() == SiegeKnowledgeData.Domain.SOURCES
                    || entry.domain() == SiegeKnowledgeData.Domain.CONTRADICTIONS;
        };
    }

    private boolean isBeginnerEntry(SiegeKnowledgeData.Entry entry) {
        return switch (entry.id()) {
            case "server-overview", "server-exploration", "rarity-order", "race-catalog",
                 "progression-v1-v4", "trials-basics", "executors-basics", "structures-basics",
                 "bosses-basics", "missions-npcs", "dimensions-basics", "respawn-cards",
                 "relic-basics", "economy-basics", "source-policy" -> true;
            default -> false;
        };
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
            String prefix = entry.zone() == SiegeKnowledgeData.Zone.HISTORY
                    ? label("HIST", "HIST")
                    : entry.domain().label(spanish());
            button.setMessage(Component.literal(prefix + " · " + entry.title(spanish())));
            button.setSelected(selected != null && selected.id().equals(entry.id()));
        }
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
        mode = preferredMode(entry);
        selected = entry;
        if (search != null && !search.getValue().isBlank()) search.setValue("");
        refresh();
        for (int i = 0; i < visibleEntries.size(); i++) {
            if (visibleEntries.get(i).id().equals(id)) {
                listOffset = Math.max(0, i - 1);
                break;
            }
        }
        refreshButtons();
    }

    private Mode preferredMode(SiegeKnowledgeData.Entry entry) {
        if (entry.zone() == SiegeKnowledgeData.Zone.HISTORY
                || entry.domain() == SiegeKnowledgeData.Domain.SOURCES
                || entry.domain() == SiegeKnowledgeData.Domain.CONTRADICTIONS) return Mode.HISTORY;
        if (entry.domain() == SiegeKnowledgeData.Domain.RACES
                || entry.id().equals("rarity-order")
                || entry.id().equals("progression-v1-v4")
                || entry.id().equals("fabled-acquisition")) return Mode.RACES;
        if (isBeginnerEntry(entry)) return Mode.START;
        return Mode.SYSTEMS;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= listX && mouseX < listX + listW
                && mouseY >= listY && mouseY < listY + entryButtons.size() * 22) {
            int max = Math.max(0, visibleEntries.size() - entryButtons.size());
            listOffset = Math.max(0, Math.min(max, listOffset - (int)Math.signum(delta)));
            refreshButtons();
            return true;
        }
        if (mouseX >= detailX && mouseX < detailX + detailW
                && mouseY >= detailY && mouseY < detailY + detailH) {
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

        String title = label("ENCICLOPEDIA DEL SERVIDOR", "SERVER ENCYCLOPEDIA")
                + " // " + SiegeRuntimeStatus.version();
        g.drawString(font, fit(title, panelW - 24), panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Guía general: razas, rarezas, progresión, Trials, sistemas y cambios históricos.",
                "General guide: races, rarities, progression, Trials, systems and historical changes."), panelW - 24),
                panelX + 12, panelY + 21, SiegeTheme.MUTED, false);

        SiegeTheme.panel(g, listX - 3, listY - 3, listW + 6, entryButtons.size() * 22 + 6, SiegeTheme.GOLD);
        SiegeTheme.panel(g, detailX - 3, detailY - 3, detailW + 6, detailH + 6, SiegeTheme.CYAN);

        if (visibleEntries.isEmpty()) {
            g.drawString(font, label("No hay temas que coincidan.", "No matching topics."),
                    listX + 7, listY + 7, SiegeTheme.MUTED, false);
        }

        renderDetail(g);
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
        int confidence = SiegeKnowledgeData.confidenceAccent(selected.confidence());
        g.drawString(font, fit(selected.title(spanish()), textW), x, y, SiegeTheme.INK, false);
        g.drawString(font, fit(selected.domain().label(spanish()) + " · "
                + SiegeKnowledgeData.sourceLine(selected, spanish()), textW), x, y + 12, confidence, false);
        SiegeTheme.divider(g, x, y + 24, textW, confidence);

        List<FormattedCharSequence> lines = new ArrayList<>();
        FormattedCharSequence blank = Component.empty().getVisualOrderText();
        for (String paragraph : selected.body(spanish()).split("\\n", -1)) {
            if (paragraph.isEmpty()) lines.add(blank);
            else lines.addAll(font.split(Component.literal(paragraph), textW));
        }

        if (!selected.related().isEmpty()) {
            lines.add(blank);
            lines.addAll(font.split(Component.literal(label("RELACIONADO: ", "RELATED: ")
                    + String.join(" · ", selected.related())), textW));
        }
        if (!selected.sources().isEmpty()) {
            lines.add(blank);
            lines.addAll(font.split(Component.literal(label("REFERENCIA", "REFERENCE")), textW));
            for (SiegeKnowledgeData.Source source : selected.sources()) {
                String sourceText = "• " + source.confidence().label(spanish())
                        + (source.date().isBlank() ? "" : " · " + source.date())
                        + (source.section().isBlank() ? "" : " · " + source.section())
                        + (source.note(spanish()).isBlank() ? "" : " — " + source.note(spanish()));
                lines.addAll(font.split(Component.literal(sourceText), textW));
            }
        }

        int first = Math.max(0, Math.min(detailOffset, Math.max(0, lines.size() - 1)));
        int maxY = detailY + detailH - 8;
        int yy = y + 31;
        g.enableScissor(detailX, yy - 1, detailX + detailW, maxY);
        for (int i = first; i < lines.size() && yy + font.lineHeight <= maxY; i++) {
            g.drawString(font, lines.get(i), x, yy, SiegeTheme.INK, false);
            yy += font.lineHeight + 2;
        }
        g.disableScissor();
    }

    private String modeLabel(Mode value) {
        return switch (value) {
            case START -> label("EMPEZAR", "START");
            case RACES -> label("RAZAS", "RACES");
            case SYSTEMS -> label("SISTEMAS", "SYSTEMS");
            case HISTORY -> label("HISTÓRICO", "HISTORY");
        };
    }

    private String modeDescription(Mode value) {
        return switch (value) {
            case START -> label(
                    "Lo principal que conviene entender antes de jugar: progresión, riesgos, Trials, revive, exploración y economía.",
                    "Core concepts to understand before playing: progression, risks, Trials, revival, exploration and economy.");
            case RACES -> label(
                    "Razas documentadas, rarezas, progresión y límites conocidos sin perfiles de jugadores.",
                    "Documented races, rarities, progression and known limits without player profiles.");
            case SYSTEMS -> label(
                    "Executores, bosses, estructuras, habilidades, Assembling, reliquias, dimensiones, raids y otros sistemas.",
                    "Executors, bosses, structures, abilities, Assembling, relics, dimensions, raids and other systems.");
            case HISTORY -> label(
                    "Cambios de versiones anteriores, contradicciones y política de fuentes para no confundir datos viejos con actuales.",
                    "Older-version changes, contradictions and source policy so old data is not confused with current information.");
        };
    }

    private int modeAccent(Mode value) {
        return switch (value) {
            case START -> SiegeTheme.GREEN;
            case RACES -> SiegeTheme.GOLD;
            case SYSTEMS -> SiegeTheme.CYAN;
            case HISTORY -> SiegeTheme.ORANGE;
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

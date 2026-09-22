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
 * SIEGE 4.0 server encyclopedia.
 *
 * Player-facing text is deliberately simple: internal source/confidence metadata
 * remains in the data model for maintenance and tests, but it is not rendered as
 * research/debug information in the ordinary interface.
 */
public final class SiegeKnowledgeScreen extends Screen {
    private enum Mode { START, RACES, PROGRESSION, SYSTEMS, HISTORY }

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
        compact = width < 690 || height < 395;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 38;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 18 : 24));

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 88, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));
        addRenderableWidget(new SiegeButton(Math.max(8, width - (compact ? 84 : 124) - 8), 7,
                compact ? 84 : 124, 19, Component.literal(label("ATLAS RAZAS", "RACE ATLAS")),
                b -> minecraft.setScreen(new SiegeRaceAtlasScreen(this)), SiegeTheme.GOLD)
                .withIcon("intel").setCompactCenter(true));

        int modeX = panelX + 10;
        int modeY = panelY + 41;
        int gap = 4;
        int cols = panelW < 620 ? 2 : panelW < 920 ? 3 : 5;
        int modeW = Math.max(68, (panelW - 20 - gap * (cols - 1)) / cols);
        Mode[] modes = Mode.values();
        int rows = (modes.length + cols - 1) / cols;
        for (int i = 0; i < modes.length; i++) {
            Mode value = modes[i];
            int row = i / cols;
            int col = i % cols;
            int x = modeX + col * (modeW + gap);
            int right = col == cols - 1 ? panelX + panelW - 10 : x + modeW;
            int w = Math.max(40, right - x);
            int y = modeY + row * 23;
            SiegeButton button = new SiegeButton(x, y, w, 19, Component.literal(modeLabel(value)),
                    b -> switchMode(value), modeAccent(value)).setCompactCenter(true).setSelected(value == mode);
            button.setTooltip(Tooltip.create(Component.literal(modeDescription(value))));
            addRenderableWidget(button);
        }

        int searchY = modeY + rows * 23 + 3;
        search = new EditBox(font, panelX + 10, searchY, panelW - 20, 20,
                Component.literal(label("Buscar tema", "Search topic")));
        search.setHint(Component.literal(label(
                "Raza, rareza, Trial, Executor, reliquia, revive, dimensión…",
                "Race, rarity, Trial, Executor, relic, revive, dimension…")));
        search.setResponder(value -> { listOffset = 0; refresh(); });
        addRenderableWidget(search);

        int bodyTop = searchY + 28;
        if (compact) {
            listX = panelX + 10;
            listY = bodyTop;
            listW = panelW - 20;
            int listRows = height < 330 ? 2 : 3;
            detailX = listX;
            detailY = listY + listRows * 22 + 8;
            detailW = listW;
            detailH = Math.max(44, panelY + panelH - detailY - 10);
        } else {
            listX = panelX + 10;
            listY = bodyTop;
            listW = Math.max(215, Math.min(350, panelW * 36 / 100));
            detailX = listX + listW + 10;
            detailY = bodyTop;
            detailW = panelX + panelW - 10 - detailX;
            detailH = Math.max(90, panelY + panelH - detailY - 10);
        }

        int desiredRows = compact ? Math.max(2, Math.min(3, (detailY - listY - 6) / 22))
                : Math.max(5, Math.min(10, detailH / 22));
        for (int i = 0; i < desiredRows; i++) {
            int slot = i;
            SiegeButton row = new SiegeButton(listX, listY + i * 22, listW, 19,
                    Component.empty(), b -> selectVisible(slot), SiegeTheme.GOLD).withIcon("overview");
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
        selected = null;
        SiegeUiSounds.category();
        rebuildWidgets();
    }

    private void refresh() {
        String query = search == null ? "" : search.getValue();
        boolean es = spanish();
        List<SiegeKnowledgeData.Entry> searched = SiegeKnowledgeData.search(query, es, null, 128);
        visibleEntries = searched.stream().filter(this::belongsToMode).toList();
        int maxOffset = Math.max(0, visibleEntries.size() - entryButtons.size());
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
            case RACES -> entry.domain() == SiegeKnowledgeData.Domain.RACES;
            case PROGRESSION -> entry.domain() == SiegeKnowledgeData.Domain.PROGRESSION
                    || entry.domain() == SiegeKnowledgeData.Domain.TRIALS
                    || entry.domain() == SiegeKnowledgeData.Domain.MEDITATION
                    || entry.domain() == SiegeKnowledgeData.Domain.DIMENSIONS;
            case SYSTEMS -> entry.zone() == SiegeKnowledgeData.Zone.SERVER
                    && entry.domain() != SiegeKnowledgeData.Domain.RACES
                    && entry.domain() != SiegeKnowledgeData.Domain.PROGRESSION
                    && entry.domain() != SiegeKnowledgeData.Domain.TRIALS
                    && entry.domain() != SiegeKnowledgeData.Domain.MEDITATION
                    && entry.domain() != SiegeKnowledgeData.Domain.DIMENSIONS
                    && entry.domain() != SiegeKnowledgeData.Domain.SOURCES;
            case HISTORY -> entry.zone() == SiegeKnowledgeData.Zone.HISTORY
                    || entry.domain() == SiegeKnowledgeData.Domain.CONTRADICTIONS;
        };
    }

    private boolean isBeginnerEntry(SiegeKnowledgeData.Entry entry) {
        return switch (entry.id()) {
            case "server-overview", "server-exploration", "rarity-order", "race-catalog",
                 "progression-v1-v4", "trials-basics", "executors-basics", "structures-basics",
                 "bosses-basics", "missions-npcs", "dimensions-basics", "respawn-cards",
                 "relic-basics", "economy-basics", "assembling-table" -> true;
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
                    ? label("HIST", "HIST") : entry.domain().label(spanish());
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
        SiegeKnowledgeData.Entry entry = SiegeKnowledgeData.get(id);
        if (entry == null) return;
        mode = preferredMode(entry);
        selected = entry;
        if (search != null && !search.getValue().isBlank()) search.setValue("");
        refresh();
        for (int i = 0; i < visibleEntries.size(); i++) {
            if (visibleEntries.get(i).id().equals(id)) {
                listOffset = Math.max(0, Math.min(i, Math.max(0, visibleEntries.size() - entryButtons.size())));
                break;
            }
        }
        refreshButtons();
    }

    private Mode preferredMode(SiegeKnowledgeData.Entry entry) {
        if (entry.zone() == SiegeKnowledgeData.Zone.HISTORY
                || entry.domain() == SiegeKnowledgeData.Domain.CONTRADICTIONS) return Mode.HISTORY;
        if (entry.domain() == SiegeKnowledgeData.Domain.RACES) return Mode.RACES;
        if (entry.domain() == SiegeKnowledgeData.Domain.PROGRESSION
                || entry.domain() == SiegeKnowledgeData.Domain.TRIALS
                || entry.domain() == SiegeKnowledgeData.Domain.MEDITATION
                || entry.domain() == SiegeKnowledgeData.Domain.DIMENSIONS) return Mode.PROGRESSION;
        return isBeginnerEntry(entry) ? Mode.START : Mode.SYSTEMS;
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
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xC7000000 : 0x99000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, modeAccent(mode));

        String title = label("ENCICLOPEDIA DEL SERVIDOR", "SERVER ENCYCLOPEDIA")
                + " // " + SiegeRuntimeStatus.version();
        g.drawString(font, fit(title, panelW - 24), panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Elegí una categoría y abrí sólo el tema que necesitás.",
                "Choose a category and open only the topic you need."), panelW - 24),
                panelX + 12, panelY + 22, SiegeTheme.MUTED, false);

        SiegeTheme.panel(g, listX - 3, listY - 3, listW + 6, entryButtons.size() * 22 + 6, modeAccent(mode));
        SiegeTheme.panel(g, detailX - 3, detailY - 3, detailW + 6, detailH + 6, modeAccent(mode));

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
        int accent = selected.zone() == SiegeKnowledgeData.Zone.HISTORY ? SiegeTheme.ORANGE : modeAccent(mode);
        g.drawString(font, fit(selected.title(spanish()), textW), x, y, SiegeTheme.INK, false);
        g.drawString(font, fit(selected.zone() == SiegeKnowledgeData.Zone.HISTORY
                ? label("ARCHIVO HISTÓRICO", "HISTORICAL FILE")
                : selected.domain().label(spanish()), textW), x, y + 13, accent, false);
        SiegeTheme.divider(g, x, y + 26, textW, accent);

        List<FormattedCharSequence> lines = new ArrayList<>();
        for (String paragraph : selected.body(spanish()).split("\\n", -1)) {
            if (paragraph.isEmpty()) lines.add(Component.empty().getVisualOrderText());
            else lines.addAll(font.split(Component.literal(paragraph), textW));
        }
        int first = Math.max(0, Math.min(detailOffset, Math.max(0, lines.size() - 1)));
        int yy = y + 34;
        int maxY = detailY + detailH - 8;
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
            case PROGRESSION -> label("PROGRESIÓN", "PROGRESSION");
            case SYSTEMS -> label("SISTEMAS", "SYSTEMS");
            case HISTORY -> label("HISTÓRICO", "HISTORY");
        };
    }

    private String modeDescription(Mode value) {
        return switch (value) {
            case START -> label("Lo esencial para entender SIEGE/Eternal Craft.", "Essentials for understanding SIEGE/Eternal Craft.");
            case RACES -> label("Razas conocidas, rarezas, variantes y diferencias.", "Known races, rarities, variants and differences.");
            case PROGRESSION -> label("V1→V4, Trials, meditación, dimensiones y rutas de avance.", "V1→V4, Trials, meditation, dimensions and progression routes.");
            case SYSTEMS -> label("Executores, bosses, estructuras, objetos, reliquias, Assembling, raids y economía.", "Executors, bosses, structures, items, relics, Assembling, raids and economy.");
            case HISTORY -> label("Mecánicas antiguas o contradictorias separadas de lo general.", "Old or conflicting mechanics kept separate from general guidance.");
        };
    }

    private int modeAccent(Mode value) {
        return switch (value) {
            case START -> SiegeTheme.GREEN;
            case RACES, PROGRESSION -> SiegeTheme.GOLD;
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

    @Override public void onClose() { SiegeUiSounds.back(); if (minecraft != null) minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

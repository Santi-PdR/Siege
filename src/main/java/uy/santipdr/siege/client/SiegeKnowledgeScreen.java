package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** Current player-facing SIEGE encyclopedia. Old/editorial records stay outside the normal UI. */
public final class SiegeKnowledgeScreen extends Screen {
    private enum Mode { START, RACES, PROGRESSION, SYSTEMS, ITEMS }
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
        super(Component.literal("SIEGE // ENCYCLOPEDIA"));
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
        int gap = 3;
        int modeW = Math.max(38, (panelW - 20 - gap * 4) / 5);
        for (int i = 0; i < Mode.values().length; i++) {
            Mode value = Mode.values()[i];
            int x = modeX + i * (modeW + gap);
            int w = i == Mode.values().length - 1 ? panelX + panelW - 10 - x : modeW;
            SiegeButton button = new SiegeButton(x, modeY, w, 19,
                    Component.literal(modeButtonLabel(value, w)), b -> switchMode(value), modeAccent(value))
                    .setCompactCenter(true).setSelected(value == mode);
            button.setTooltip(Tooltip.create(Component.literal(modeDescription(value))));
            addRenderableWidget(button);
        }

        search = new EditBox(font, panelX + 10, modeY + 25, panelW - 20, 20,
                Component.literal(label("Buscar", "Search")));
        search.setHint(Component.literal(label(
                "Raza, Trial, Executor, reliquia, desfibrilador, Geography Table…",
                "Race, Trial, Executor, relic, defibrillator, Geography Table…")));
        search.setResponder(value -> { listOffset = 0; detailOffset = 0; refresh(); });
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
        List<SiegeKnowledgeData.Entry> searched = SiegeKnowledgeRegistry.search(query, spanish(), 128);
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
                    || entry.domain() == SiegeKnowledgeData.Domain.ABILITIES
                    || entry.domain() == SiegeKnowledgeData.Domain.MEDITATION;
            case SYSTEMS -> entry.domain() == SiegeKnowledgeData.Domain.EXECUTORS
                    || entry.domain() == SiegeKnowledgeData.Domain.BOSSES
                    || entry.domain() == SiegeKnowledgeData.Domain.DEATH_REVIVE
                    || entry.domain() == SiegeKnowledgeData.Domain.STRUCTURES
                    || entry.domain() == SiegeKnowledgeData.Domain.MISSIONS
                    || entry.domain() == SiegeKnowledgeData.Domain.RAIDS_EVENTS
                    || entry.domain() == SiegeKnowledgeData.Domain.FACTIONS
                    || entry.domain() == SiegeKnowledgeData.Domain.DIMENSIONS;
            case ITEMS -> entry.domain() == SiegeKnowledgeData.Domain.ITEMS
                    || entry.domain() == SiegeKnowledgeData.Domain.RELICS
                    || entry.domain() == SiegeKnowledgeData.Domain.ASSEMBLING
                    || entry.domain() == SiegeKnowledgeData.Domain.ECONOMY;
        };
    }

    private boolean isBeginnerEntry(SiegeKnowledgeData.Entry entry) {
        return switch (entry.id()) {
            case "server-overview", "newcomer-operational-rule", "race-system", "abilities-experience",
                 "progression-v1-v4", "trials-basics", "executors-basics", "bosses-basics",
                 "death-revive-current" -> true;
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
            button.setMessage(Component.literal(fit(entry.title(spanish()), Math.max(20, listW - 28))));
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
        SiegeKnowledgeData.Entry entry = SiegeKnowledgeRegistry.get(id);
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
        if (isBeginnerEntry(entry)) return Mode.START;
        if (entry.domain() == SiegeKnowledgeData.Domain.RACES) return Mode.RACES;
        if (entry.domain() == SiegeKnowledgeData.Domain.PROGRESSION
                || entry.domain() == SiegeKnowledgeData.Domain.TRIALS
                || entry.domain() == SiegeKnowledgeData.Domain.ABILITIES
                || entry.domain() == SiegeKnowledgeData.Domain.MEDITATION) return Mode.PROGRESSION;
        if (entry.domain() == SiegeKnowledgeData.Domain.ITEMS
                || entry.domain() == SiegeKnowledgeData.Domain.RELICS
                || entry.domain() == SiegeKnowledgeData.Domain.ASSEMBLING
                || entry.domain() == SiegeKnowledgeData.Domain.ECONOMY) return Mode.ITEMS;
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
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, modeAccent(mode));

        String title = label("ENCICLOPEDIA", "ENCYCLOPEDIA") + " // " + SiegeRuntimeStatus.version();
        g.drawString(font, fit(title, panelW - 24), panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Información actual del servidor, separada por tema y escrita para entenderla jugando.",
                "Current server information, separated by topic and written to make sense while playing."), panelW - 24),
                panelX + 12, panelY + 21, SiegeTheme.MUTED, false);

        SiegeTheme.panel(g, listX - 3, listY - 3, listW + 6, entryButtons.size() * 22 + 6, SiegeTheme.GOLD);
        SiegeTheme.panel(g, detailX - 3, detailY - 3, detailW + 6, detailH + 6, modeAccent(mode));
        if (visibleEntries.isEmpty()) {
            g.drawString(font, label("No hay información que coincida.", "No matching information."),
                    listX + 7, listY + 7, SiegeTheme.MUTED, false);
        }

        renderDetail(g);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private void renderDetail(GuiGraphics g) {
        if (selected == null) {
            g.drawString(font, label("Elegí un tema.", "Choose a topic."),
                    detailX + 8, detailY + 8, SiegeTheme.MUTED, false);
            return;
        }

        int x = detailX + 8;
        int y = detailY + 7;
        int textW = Math.max(40, detailW - 16);
        int accent = modeAccent(mode);
        g.drawString(font, fit(selected.title(spanish()), textW), x, y, SiegeTheme.INK, false);
        g.drawString(font, fit(selected.domain().label(spanish()), textW), x, y + 12, accent, false);
        SiegeTheme.divider(g, x, y + 24, textW, accent);

        List<FormattedCharSequence> lines = new ArrayList<>();
        FormattedCharSequence blank = Component.empty().getVisualOrderText();
        lines.addAll(font.split(Component.literal(selected.summary(spanish())), textW));
        lines.add(blank);
        for (String paragraph : selected.body(spanish()).split("\\n", -1)) {
            if (paragraph.isEmpty()) lines.add(blank);
            else lines.addAll(font.split(Component.literal(paragraph), textW));
        }

        int yy = y + 31;
        int maxY = detailY + detailH - 8;
        int visibleLines = Math.max(1, (maxY - yy) / (font.lineHeight + 2));
        int maxOffset = Math.max(0, lines.size() - visibleLines);
        detailOffset = Math.max(0, Math.min(detailOffset, maxOffset));
        g.enableScissor(detailX, yy - 1, detailX + detailW, maxY);
        for (int i = detailOffset; i < lines.size() && yy + font.lineHeight <= maxY; i++) {
            g.drawString(font, lines.get(i), x, yy, SiegeTheme.INK, false);
            yy += font.lineHeight + 2;
        }
        g.disableScissor();
    }

    private String modeButtonLabel(Mode value, int width) {
        String full = modeLabel(value);
        int usable = Math.max(8, width - 14);
        if (font.width(full) <= usable) return full;
        return switch (value) {
            case START -> label("INICIO", "START");
            case RACES -> label("RAZAS", "RACES");
            case PROGRESSION -> label("PROG.", "PROG.");
            case SYSTEMS -> label("SIST.", "SYSTEMS");
            case ITEMS -> label("OBJ.", "ITEMS");
        };
    }

    private String modeLabel(Mode value) {
        return switch (value) {
            case START -> label("EMPEZAR", "START");
            case RACES -> label("RAZAS", "RACES");
            case PROGRESSION -> label("PROGRESIÓN", "PROGRESSION");
            case SYSTEMS -> label("SISTEMAS", "SYSTEMS");
            case ITEMS -> label("OBJETOS", "ITEMS");
        };
    }

    private String modeDescription(Mode value) {
        return switch (value) {
            case START -> label("Lo necesario para empezar sin tener que aprender todo el servidor de golpe.", "What you need to start without learning the whole server at once.");
            case RACES -> label("Razas y variantes conocidas.", "Known races and variants.");
            case PROGRESSION -> label("V1→V4, transformaciones, habilidades, meditación y Trials.", "V1→V4, transformations, abilities, meditation and Trials.");
            case SYSTEMS -> label("Executores, unidades, heridas, reanimación, estructuras y eventos.", "Executors, units, injuries, revival, structures and events.");
            case ITEMS -> label("Objetos, reliquias, herramientas de investigación y Assembling.", "Items, relics, research tools and Assembling.");
        };
    }

    private int modeAccent(Mode value) {
        return switch (value) {
            case START -> SiegeTheme.CYAN;
            case RACES -> SiegeTheme.GREEN;
            case PROGRESSION -> SiegeTheme.GOLD;
            case SYSTEMS -> SiegeTheme.RED;
            case ITEMS -> SiegeTheme.BLUE;
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
    @Override public void onClose() { if (minecraft != null) minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

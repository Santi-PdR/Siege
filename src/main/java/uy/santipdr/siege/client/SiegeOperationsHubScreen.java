package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** SIEGE 5.00 Operations: a small set of useful routes instead of a wall of tools. */
public final class SiegeOperationsHubScreen extends Screen {
    private final Screen parent;
    private final List<SiegeButton> resultButtons = new ArrayList<>();
    private List<SiegeOperationsIndex.Entry> results = List.of();
    private SiegeCommandNetwork.Lane lane = SiegeCommandNetwork.Lane.DEPLOYMENT;
    private EditBox searchBox;
    private int panelX, panelY, panelW, panelH;
    private int laneTop, routeTop, routeButtonH, routeGap;
    private int searchY, resultsY, visibleResultSlots;
    private boolean compact;

    public SiegeOperationsHubScreen(Screen parent) {
        super(Component.literal("SIEGE // OPERATIONS"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        resultButtons.clear();
        compact = width < 600 || height < 360;
        int margin = compact ? 7 : Math.max(12, width / 70);
        panelX = margin;
        panelY = compact ? 31 : 36;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 18 : 24));

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 86, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));

        routeButtonH = compact ? 18 : 20;
        routeGap = compact ? 3 : 5;
        laneTop = panelY + (compact ? 58 : 66);
        int contentX = panelX + 10;
        int contentW = panelW - 20;

        SiegeCommandNetwork.Lane[] lanes = SiegeCommandNetwork.Lane.values();
        int laneCols = panelW < 340 ? 1 : lanes.length;
        int laneRows = (lanes.length + laneCols - 1) / laneCols;
        int laneW = Math.max(1, (contentW - routeGap * (laneCols - 1)) / laneCols);
        for (int i = 0; i < lanes.length; i++) {
            SiegeCommandNetwork.Lane value = lanes[i];
            int row = i / laneCols, col = i % laneCols;
            int x = contentX + col * (laneW + routeGap);
            int w = col == laneCols - 1 ? contentX + contentW - x : laneW;
            int y = laneTop + row * (routeButtonH + routeGap);
            SiegeButton button = new SiegeButton(x, y, w, routeButtonH,
                    Component.literal(laneButtonLabel(value, w)), b -> switchLane(value), laneAccent(value))
                    .withIcon(laneIcon(value)).setCompactCenter(true).setSelected(value == lane);
            button.setTooltip(Tooltip.create(Component.literal(laneDescription(value))));
            addRenderableWidget(button);
        }

        routeTop = laneTop + laneRows * (routeButtonH + routeGap) + (compact ? 20 : 23);
        List<SiegeOperationsIndex.Route> routes = SiegeCommandNetwork.routes(lane);
        int routeCols = panelW < 320 ? 1 : Math.min(compact ? 2 : 4, routes.size());
        int routeRows = (routes.size() + routeCols - 1) / routeCols;
        int cellW = Math.max(1, (contentW - routeGap * (routeCols - 1)) / routeCols);
        for (int i = 0; i < routes.size(); i++) {
            SiegeOperationsIndex.Route route = routes.get(i);
            int row = i / routeCols, col = i % routeCols;
            int x = contentX + col * (cellW + routeGap);
            int w = col == routeCols - 1 ? contentX + contentW - x : cellW;
            int y = routeTop + row * (routeButtonH + routeGap);
            SiegeButton button = new SiegeButton(x, y, w, routeButtonH,
                    Component.literal(routeButtonLabel(route, w)), b -> openRoute(route), routeAccent(route))
                    .withIcon(routeIcon(route)).setCompactCenter(true);
            button.setTooltip(Tooltip.create(Component.literal(routeDescription(route))));
            addRenderableWidget(button);
        }

        searchY = routeTop + routeRows * (routeButtonH + routeGap) + (compact ? 10 : 13);
        int searchX = panelX + 10;
        int searchW = panelW - 20;
        searchBox = new EditBox(font, searchX, searchY, searchW, 20,
                Component.literal(label("Buscar", "Search")));
        searchBox.setHint(Component.literal(label(
                "Raza, Trial, Executor, unidad, reliquia o tema…",
                "Race, Trial, Executor, unit, relic or topic…")));
        searchBox.setResponder(value -> refreshResults());
        addRenderableWidget(searchBox);

        resultsY = searchY + 25;
        int available = Math.max(0, panelY + panelH - resultsY - 22);
        visibleResultSlots = Math.max(0, Math.min(5, available / (routeButtonH + 3)));
        for (int i = 0; i < 5; i++) {
            int slot = i;
            SiegeButton button = new SiegeButton(searchX, resultsY + i * (routeButtonH + 3), searchW, routeButtonH,
                    Component.empty(), b -> openResult(slot), SiegeTheme.CYAN).withIcon("search");
            button.visible = i < visibleResultSlots;
            button.active = false;
            resultButtons.add(addRenderableWidget(button));
        }
        setInitialFocus(searchBox);
        refreshResults();
    }

    private void switchLane(SiegeCommandNetwork.Lane next) {
        if (next == null || next == lane) return;
        lane = next;
        SiegeUiSounds.category();
        rebuildWidgets();
    }

    private void refreshResults() {
        if (searchBox == null) return;
        results = SiegeOperationsIndex.search(searchBox.getValue(), spanish(), Math.max(1, visibleResultSlots));
        for (int i = 0; i < resultButtons.size(); i++) {
            SiegeButton button = resultButtons.get(i);
            boolean present = i < results.size() && i < visibleResultSlots;
            button.visible = i < visibleResultSlots;
            button.active = present;
            if (!present) {
                button.setMessage(Component.literal(i == 0 && !searchBox.getValue().isBlank()
                        ? label("Sin resultados", "No results") : ""));
                continue;
            }
            SiegeOperationsIndex.Entry entry = results.get(i);
            String prefix = switch (entry.kind()) {
                case ROUTE -> label("IR", "OPEN");
                case INTEL -> "INTEL";
                case KNOWLEDGE -> label("INFO", "INFO");
            };
            button.setMessage(Component.literal(fit(prefix + " · " + entry.title() + "  //  " + entry.subtitle(),
                    Math.max(20, button.getWidth() - 30))));
        }
    }

    private void openResult(int slot) {
        if (slot < 0 || slot >= results.size()) return;
        SiegeOperationsIndex.Entry entry = results.get(slot);
        SiegeUiSounds.confirm();
        if (entry.kind() == SiegeOperationsIndex.Kind.INTEL && entry.intel() != null) {
            SiegeRouteHistory.record(SiegeOperationsIndex.Route.INTEL);
            minecraft.setScreen(new IntelScreenV3(this, entry.intel()));
            return;
        }
        if (entry.kind() == SiegeOperationsIndex.Kind.KNOWLEDGE && !entry.knowledgeId().isBlank()) {
            SiegeKnowledgeData.Entry knowledge = SiegeKnowledgeRegistry.get(entry.knowledgeId());
            if (knowledge != null) {
                SiegeRouteHistory.record(SiegeOperationsIndex.Route.ATLAS);
                minecraft.setScreen(new SiegeKnowledgeFileScreen(this, knowledge));
                return;
            }
        }
        openRoute(entry.route());
    }

    private void openRoute(SiegeOperationsIndex.Route route) {
        if (route == null || !SiegeCommandNetwork.isVisible(route)) return;
        SiegeRouteHistory.record(route);
        SiegeUiSounds.confirm();
        switch (route) {
            case BRIEFING -> minecraft.setScreen(new SiegeBriefingScreen(this));
            case ATLAS -> minecraft.setScreen(new SiegeAtlasScreen(this));
            case RACES -> minecraft.setScreen(new SiegeRaceAtlasScreen(this));
            case PROGRESSION -> minecraft.setScreen(new SiegeProgressionMapScreen(this));
            case THREATS -> minecraft.setScreen(new SiegeThreatBoardScreen(this));
            case MEDIA -> minecraft.setScreen(new SiegeMediaRoomScreen(this));
            case DEPLOYMENT -> minecraft.setScreen(new SiegeMultiplayerScreen(this));
            case INTEL -> minecraft.setScreen(new IntelScreenV3(this));
            case KNOWLEDGE -> minecraft.setScreen(new SiegeKnowledgeScreen(this));
            case FIELD_MANUAL -> minecraft.setScreen(new SiegeArchiveScreen(this));
            default -> { }
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xB9000000 : 0x8A000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, SiegeTheme.CYAN);

        int tx = panelX + 12, ty = panelY + 9;
        g.drawString(font, fit(label("OPERACIONES", "OPERATIONS") + " // " + SiegeRuntimeStatus.version(), panelW - 24),
                tx, ty, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Elegí qué querés hacer. Archivo, Arsenal y Ajustes quedan en el menú principal para no repetir accesos.",
                "Choose what you want to do. Archive, Armory and Settings stay on the main menu to avoid duplicate shortcuts."),
                panelW - 24), tx, ty + 13, SiegeTheme.MUTED, false);
        g.drawString(font, fit(laneDescription(lane), panelW - 24), tx, ty + 27, laneAccent(lane), false);

        SiegeTheme.divider(g, panelX + 10, laneTop - 7, panelW - 20, SiegeTheme.CYAN);
        if (routeTop - 11 > laneTop) {
            g.drawString(font, fit(laneLabel(lane), panelW - 24),
                    panelX + 12, routeTop - 11, laneAccent(lane), false);
        }

        if (searchBox != null) SiegeTheme.frame(g, searchBox.getX() - 1, searchBox.getY() - 1,
                searchBox.getWidth() + 2, searchBox.getHeight() + 2,
                searchBox.isFocused() ? SiegeTheme.FOCUS : SiegeTheme.CYAN);

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private String laneButtonLabel(SiegeCommandNetwork.Lane value, int width) {
        String full = laneLabel(value);
        if (font.width(full) <= Math.max(8, width - 28)) return full;
        return switch (value) {
            case DEPLOYMENT -> "DEP";
            case INTELLIGENCE -> "INTEL";
            case REFERENCE -> label("INFO", "INFO");
        };
    }

    private String laneLabel(SiegeCommandNetwork.Lane value) { return switch (value) {
        case DEPLOYMENT -> label("ENTRAR Y PREPARARSE", "JOIN & PREPARE");
        case INTELLIGENCE -> label("AMENAZAS Y PROGRESO", "THREATS & PROGRESS");
        case REFERENCE -> label("CONSULTAR INFORMACIÓN", "REFERENCE");
    }; }

    private String laneDescription(SiegeCommandNetwork.Lane value) { return switch (value) {
        case DEPLOYMENT -> label("Para empezar, revisar lo básico y entrar al servidor.", "Start here, review the basics and join the server.");
        case INTELLIGENCE -> label("Dossiers, razas, progresión y Atlas.", "Dossiers, races, progression and Atlas.");
        case REFERENCE -> label("Enciclopedia, manual de campo y multimedia.", "Encyclopedia, field manual and media.");
    }; }

    private int laneAccent(SiegeCommandNetwork.Lane value) { return switch (value) {
        case DEPLOYMENT -> SiegeTheme.RED;
        case INTELLIGENCE -> SiegeTheme.GOLD;
        case REFERENCE -> SiegeTheme.GREEN;
    }; }

    private String laneIcon(SiegeCommandNetwork.Lane value) { return switch (value) {
        case DEPLOYMENT -> "connect";
        case INTELLIGENCE -> "intel";
        case REFERENCE -> "search";
    }; }

    private String routeButtonLabel(SiegeOperationsIndex.Route route, int width) {
        String full = routeLabel(route);
        if (font.width(full) <= Math.max(8, width - 28)) return full;
        return routeShort(route);
    }

    private String routeLabel(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> label("PRIMEROS PASOS", "FIRST STEPS");
        case ATLAS -> "ATLAS";
        case RACES -> label("RAZAS", "RACES");
        case PROGRESSION -> label("PROGRESIÓN", "PROGRESSION");
        case THREATS -> label("AMENAZAS", "THREATS");
        case MEDIA -> label("MULTIMEDIA", "MEDIA");
        case DEPLOYMENT -> label("ENTRAR AL SERVIDOR", "JOIN SERVER");
        case INTEL -> "INTEL";
        case KNOWLEDGE -> label("ENCICLOPEDIA", "ENCYCLOPEDIA");
        case FIELD_MANUAL -> label("MANUAL DE CAMPO", "FIELD MANUAL");
        default -> route.name();
    }; }

    private String routeShort(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> "BRF"; case ATLAS -> "ATL"; case RACES -> "RAC"; case PROGRESSION -> "PRG";
        case THREATS -> "THR"; case MEDIA -> "MED"; case DEPLOYMENT -> "DEP"; case INTEL -> "INT";
        case KNOWLEDGE -> "ENC"; case FIELD_MANUAL -> "MAN"; default -> route.name();
    }; }

    private String routeDescription(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> label("Qué hacer al empezar y qué sistemas conviene conocer primero.", "What to do first and which systems matter early.");
        case ATLAS -> label("Consulta detallada de razas, progresión, sistemas y objetos.", "Detailed reference for races, progression, systems and items.");
        case RACES -> label("Rarezas, razas, variantes y rutas conocidas.", "Rarities, races, variants and known paths.");
        case PROGRESSION -> label("V1→V4, transformaciones, rutas especiales y Trials.", "V1→V4, transformations, special paths and Trials.");
        case THREATS -> label("Unidades, Executores, bosses y eventos peligrosos.", "Units, Executors, bosses and dangerous events.");
        case MEDIA -> label("Música, ambientes y fondos.", "Music, moods and backgrounds.");
        case DEPLOYMENT -> label("Conectarse al servidor oficial.", "Connect to the official server.");
        case INTEL -> label("Dossiers de unidades y amenazas conocidas.", "Dossiers for known units and threats.");
        case KNOWLEDGE -> label("Información actual del servidor organizada por tema.", "Current server information organized by topic.");
        case FIELD_MANUAL -> label("Heridas, muerte, misiones y protocolos útiles.", "Injuries, death, missions and useful protocols.");
        default -> "";
    }; }

    private int routeAccent(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> SiegeTheme.ORANGE; case ATLAS, RACES, KNOWLEDGE -> SiegeTheme.GREEN;
        case PROGRESSION -> SiegeTheme.GOLD; case THREATS, DEPLOYMENT -> SiegeTheme.RED;
        case MEDIA -> SiegeTheme.CYAN; case INTEL, FIELD_MANUAL -> SiegeTheme.GOLD; default -> SiegeTheme.CYAN;
    }; }

    private String routeIcon(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> "shield"; case ATLAS, PROGRESSION, FIELD_MANUAL -> "overview";
        case RACES, THREATS, INTEL -> "intel"; case MEDIA -> "image"; case DEPLOYMENT -> "connect";
        case KNOWLEDGE -> "search"; default -> "overview";
    }; }

    private String fit(String value, int pixels) {
        if (value == null || pixels <= 0) return "";
        if (font.width(value) <= pixels) return value;
        if (pixels <= font.width("…")) return font.plainSubstrByWidth(value, pixels);
        return font.plainSubstrByWidth(value, pixels - font.width("…")) + "…";
    }
    private boolean spanish() { return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_"); }
    private String label(String es, String en) { return spanish() ? es : en; }
    @Override public void onClose() { if (minecraft != null) minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

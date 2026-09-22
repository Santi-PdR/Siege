package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** SIEGE 5.00 War Room: hierarchical Command Network plus global local/offline search. */
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
        super(Component.literal("SIEGE // COMMAND NETWORK"));
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
        laneTop = panelY + (compact ? 68 : 84);
        int contentX = panelX + 10;
        int contentW = panelW - 20;

        SiegeCommandNetwork.Lane[] lanes = SiegeCommandNetwork.Lane.values();
        int laneCols = panelW < 360 ? 2 : 4;
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

        routeTop = laneTop + laneRows * (routeButtonH + routeGap) + (compact ? 9 : 12);
        List<SiegeOperationsIndex.Route> routes = SiegeCommandNetwork.routes(lane);
        int routeCols = panelW < 320 ? 2 : Math.min(compact ? 3 : 5, Math.max(2, routes.size()));
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

        searchY = routeTop + routeRows * (routeButtonH + routeGap) + (compact ? 8 : 12);
        int searchX = panelX + 10;
        int searchW = panelW - 20;
        searchBox = new EditBox(font, searchX, searchY, searchW, 20,
                Component.literal(label("Buscar", "Search")));
        searchBox.setHint(Component.literal(label(
                "Dossier, raza, rareza, Trial, Executor, reliquia, música, ambiente o ruta…",
                "Dossier, race, rarity, Trial, Executor, relic, music, mood or route…")));
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
                case ROUTE -> label("RUTA", "ROUTE");
                case INTEL -> "INTEL";
                case ARMORY -> label("ARSENAL", "ARMORY");
                case KNOWLEDGE -> label("INFO", "INFO");
            };
            String text = prefix + " · " + entry.title() + "  //  " + entry.subtitle();
            button.setMessage(Component.literal(fit(text, Math.max(20, button.getWidth() - 30))));
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
        if (route == null) return;
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
            case ARCHIVE -> minecraft.setScreen(new SiegeGuideScreen(this, SiegeGuideScreen.Mode.ARCHIVE));
            case ARMORY -> minecraft.setScreen(new SiegeGuideScreen(this, SiegeGuideScreen.Mode.ARMORY));
            case FIELD_MANUAL -> minecraft.setScreen(new SiegeArchiveScreen(this));
            case COMMAND -> minecraft.setScreen(new SiegeSystemScreen(this));
            case DIAGNOSTICS -> minecraft.setScreen(new SiegeDiagnosticsScreen(this));
            case SETTINGS -> minecraft.setScreen(new SiegeSettingsScreen(this));
            case BACKGROUNDS -> minecraft.setScreen(new SiegeSceneScreen(this));
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xB9000000 : 0x8A000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, SiegeTheme.CYAN);

        int tx = panelX + 12, ty = panelY + 9;
        String title = label("RED DE COMANDO", "COMMAND NETWORK") + " // " + SiegeRuntimeStatus.version();
        g.drawString(font, fit(title, panelW - 24), tx, ty, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Elegí un frente de trabajo y después la herramienta exacta. La búsqueda sigue cubriendo todo SIEGE.",
                "Choose an operational lane, then the exact tool. Search still covers all of SIEGE."), panelW - 24),
                tx, ty + 12, SiegeTheme.MUTED, false);

        int statusY = ty + 30;
        g.drawString(font, fit(label("CLIENTE ", "CLIENT ") + SiegeRuntimeStatus.healthLabel(spanish()) + " · "
                        + SiegeRuntimeStatus.profileFitLabel(spanish()), panelW - 24),
                tx, statusY, SiegeRuntimeStatus.healthAccent(), false);
        g.drawString(font, fit(SiegeRuntimeStatus.intelLabel(spanish()), panelW - 24), tx, statusY + 11, SiegeTheme.GOLD, false);
        if (!compact) {
            String knowledge = label("TEMAS ", "TOPICS ") + SiegeKnowledgeRegistry.entries().size()
                    + " · " + label("RAZAS ", "RACES ") + SiegeRaceAtlasData.all().size();
            g.drawString(font, fit(knowledge, panelW - 24), tx, statusY + 22, SiegeTheme.GREEN, false);
            int scene = SiegeBackgrounds.currentIndex(System.currentTimeMillis());
            String line = SiegeBackgrounds.sceneTag(scene, spanish()) + " · " + SiegeBackgrounds.name(scene, spanish())
                    + "   |   " + SiegeRuntimeStatus.audioLabel(spanish());
            g.drawString(font, fit(line, panelW - 24), tx, statusY + 33, SiegeTheme.CYAN, false);
        }

        SiegeTheme.divider(g, panelX + 10, laneTop - 7, panelW - 20, SiegeTheme.CYAN);
        g.drawString(font, label("RED DE COMANDO", "COMMAND LANES"), panelX + 13, laneTop - 18, SiegeTheme.CYAN, false);
        if (routeTop - 11 > laneTop) {
            g.drawString(font, fit(laneLabel(lane) + " · " + laneDescription(lane), panelW - 24),
                    panelX + 12, routeTop - 11, laneAccent(lane), false);
        }

        int recentY = panelY + panelH - 15;
        List<SiegeOperationsIndex.Route> recent = SiegeRouteHistory.snapshot();
        if (!recent.isEmpty()) {
            String recentText = label("RECIENTE: ", "RECENT: ") + recent.stream().limit(4)
                    .map(this::routeShort).reduce((a, b) -> a + "  ›  " + b).orElse("");
            g.drawString(font, fit(recentText, panelW - 24), panelX + 12, recentY, SiegeTheme.MUTED, false);
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
            case KNOWLEDGE -> label("INFO", "INFO");
            case SYSTEMS -> label("SIST.", "SYS");
        };
    }

    private String laneLabel(SiegeCommandNetwork.Lane value) { return switch (value) {
        case DEPLOYMENT -> label("DESPLIEGUE", "DEPLOYMENT");
        case INTELLIGENCE -> label("INTELIGENCIA", "INTELLIGENCE");
        case KNOWLEDGE -> label("CONOCIMIENTO", "KNOWLEDGE");
        case SYSTEMS -> label("SISTEMAS", "SYSTEMS");
    }; }

    private String laneDescription(SiegeCommandNetwork.Lane value) { return switch (value) {
        case DEPLOYMENT -> label("Entrar, prepararse y evaluar amenazas.", "Enter, prepare and evaluate threats.");
        case INTELLIGENCE -> label("Dossiers, razas, progresión y Atlas.", "Dossiers, races, progression and Atlas.");
        case KNOWLEDGE -> label("Enciclopedia, archivo, arsenal, manual y multimedia.", "Encyclopedia, archive, armory, manual and media.");
        case SYSTEMS -> label("Perfil, diagnóstico, ajustes y fondos.", "Profile, diagnostics, settings and backgrounds.");
    }; }

    private int laneAccent(SiegeCommandNetwork.Lane value) { return switch (value) {
        case DEPLOYMENT -> SiegeTheme.RED;
        case INTELLIGENCE -> SiegeTheme.GOLD;
        case KNOWLEDGE -> SiegeTheme.GREEN;
        case SYSTEMS -> SiegeTheme.CYAN;
    }; }

    private String laneIcon(SiegeCommandNetwork.Lane value) { return switch (value) {
        case DEPLOYMENT -> "connect";
        case INTELLIGENCE -> "intel";
        case KNOWLEDGE -> "search";
        case SYSTEMS -> "settings";
    }; }

    private String routeButtonLabel(SiegeOperationsIndex.Route route, int width) {
        String full = routeLabel(route);
        if (font.width(full) <= Math.max(8, width - 28)) return full;
        return routeShort(route);
    }

    private String routeLabel(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> "BRIEFING"; case ATLAS -> "ATLAS"; case RACES -> label("RAZAS", "RACES");
        case PROGRESSION -> label("PROGRESIÓN", "PROGRESSION"); case THREATS -> label("AMENAZAS", "THREATS");
        case MEDIA -> label("MULTIMEDIA", "MEDIA"); case DEPLOYMENT -> label("DESPLIEGUE", "DEPLOYMENT");
        case INTEL -> "INTEL"; case KNOWLEDGE -> label("ENCICLOPEDIA", "ENCYCLOPEDIA");
        case ARCHIVE -> label("ARCHIVO", "ARCHIVE"); case ARMORY -> label("ARSENAL", "ARMORY");
        case FIELD_MANUAL -> label("MANUAL", "MANUAL"); case COMMAND -> label("COMANDO", "COMMAND");
        case DIAGNOSTICS -> label("DIAGNÓSTICO", "DIAGNOSTICS"); case SETTINGS -> label("AJUSTES", "SETTINGS");
        case BACKGROUNDS -> label("FONDOS", "BACKGROUNDS"); }; }

    private String routeShort(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> "BRF"; case ATLAS -> "ATL"; case RACES -> "RAC"; case PROGRESSION -> "PRG";
        case THREATS -> "THR"; case MEDIA -> "MED"; case DEPLOYMENT -> "DEP"; case INTEL -> "INT";
        case KNOWLEDGE -> "ENC"; case ARCHIVE -> "ARC"; case ARMORY -> "ARS"; case FIELD_MANUAL -> "FLD";
        case COMMAND -> "CMD"; case DIAGNOSTICS -> "DIA"; case SETTINGS -> "CFG"; case BACKGROUNDS -> "BG"; }; }

    private String routeDescription(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> label("Ruta corta para entender lo esencial antes de jugar.", "Short route to understand the essentials before playing.");
        case ATLAS -> label("Temas del servidor organizados para consulta rápida.", "Server topics organized for quick reference.");
        case RACES -> label("Catálogo de razas, rarezas y rutas de progresión.", "Race catalog, rarities and progression routes.");
        case PROGRESSION -> label("Mapa de V1→V4, rutas especiales y sistemas avanzados.", "Map of V1→V4, special routes and advanced systems.");
        case THREATS -> label("Dossiers, Executores, bosses, raids y eventos.", "Dossiers, Executors, bosses, raids and events.");
        case MEDIA -> label("Música, ambientes, fondos, galería y referencias DVN.", "Music, moods, backgrounds, gallery and DVN references.");
        case DEPLOYMENT -> label("Servidor oficial, compatibilidad y conexión.", "Official server, compatibility and connection.");
        case INTEL -> label("Dossiers de unidades y amenazas.", "Unit and threat dossiers.");
        case KNOWLEDGE -> label("Enciclopedia general del servidor.", "General server encyclopedia.");
        case ARCHIVE -> label("SIEGE, 2044, facciones, Núcleo, Gates/Rifts e inspiraciones.", "SIEGE, 2044, factions, Core, Gates/Rifts and inspirations.");
        case ARMORY -> label("Equipamiento, objetos y material de Arsenal.", "Equipment, items and Armory material.");
        case FIELD_MANUAL -> label("Estados de muerte/heridas, misiones y protocolos.", "Death/injury states, missions and protocols.");
        case COMMAND -> label("Perfil visual y estado del cliente.", "Visual profile and client state.");
        case DIAGNOSTICS -> label("Problemas y recuperación del cliente.", "Client problems and recovery.");
        case SETTINGS -> label("Apariencia, audio, Intel y accesibilidad.", "Appearance, audio, Intel and accessibility.");
        case BACKGROUNDS -> label("Galería y rotación de escenas del menú.", "Menu scene gallery and rotation."); }; }

    private int routeAccent(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> SiegeTheme.ORANGE; case ATLAS, RACES -> SiegeTheme.GREEN; case PROGRESSION -> SiegeTheme.GOLD;
        case THREATS -> SiegeTheme.RED; case MEDIA -> SiegeTheme.CYAN; case DEPLOYMENT -> SiegeTheme.RED;
        case INTEL, ARCHIVE, ARMORY, FIELD_MANUAL -> SiegeTheme.GOLD; case KNOWLEDGE -> SiegeTheme.GREEN;
        case COMMAND -> SiegeTheme.CYAN; case DIAGNOSTICS -> SiegeTheme.ORANGE; case SETTINGS -> SiegeTheme.RED;
        case BACKGROUNDS -> SiegeTheme.BLUE; }; }

    private String routeIcon(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> "shield"; case ATLAS, PROGRESSION -> "overview"; case RACES, THREATS, INTEL -> "intel";
        case MEDIA, BACKGROUNDS -> "image"; case DEPLOYMENT -> "connect"; case KNOWLEDGE -> "search";
        case ARCHIVE, FIELD_MANUAL -> "overview"; case ARMORY -> "package"; case COMMAND, DIAGNOSTICS -> "shield";
        case SETTINGS -> "settings"; }; }

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

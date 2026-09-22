package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** SIEGE 4.00 War Room: operational front door and global local/offline search. */
public final class SiegeOperationsHubScreen extends Screen {
    private final Screen parent;
    private final List<SiegeButton> resultButtons = new ArrayList<>();
    private List<SiegeOperationsIndex.Entry> results = List.of();
    private EditBox searchBox;
    private int panelX, panelY, panelW, panelH;
    private int routeTop, routeButtonH, routeGap;
    private int searchY, resultsY, visibleResultSlots;
    private boolean compact;

    public SiegeOperationsHubScreen(Screen parent) {
        super(Component.literal("SIEGE // WAR ROOM"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        compact = width < 560 || height < 350;
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
        routeTop = panelY + (compact ? 68 : 84);
        int routeX = panelX + 10;
        int routeW = panelW - 20;
        int cols = compact && panelW < 350 ? 2 : (panelW > 900 ? 4 : 3);
        int cellW = Math.max(1, (routeW - routeGap * (cols - 1)) / cols);
        SiegeOperationsIndex.Route[] routes = SiegeOperationsIndex.Route.values();
        for (int i = 0; i < routes.length; i++) {
            SiegeOperationsIndex.Route route = routes[i];
            int row = i / cols, col = i % cols;
            int x = routeX + col * (cellW + routeGap);
            int w = col == cols - 1 ? routeX + routeW - x : cellW;
            int y = routeTop + row * (routeButtonH + routeGap);
            SiegeButton button = new SiegeButton(x, y, w, routeButtonH,
                    Component.literal(routeLabel(route)), b -> openRoute(route), routeAccent(route))
                    .withIcon(routeIcon(route)).setCompactCenter(true);
            button.setTooltip(Tooltip.create(Component.literal(routeDescription(route))));
            addRenderableWidget(button);
        }

        int rows = (routes.length + cols - 1) / cols;
        searchY = routeTop + rows * (routeButtonH + routeGap) + (compact ? 8 : 12);
        int searchX = panelX + 10;
        int searchW = panelW - 20;
        searchBox = new EditBox(font, searchX, searchY, searchW, 20,
                Component.literal(label("Búsqueda operacional", "Operational search")));
        searchBox.setHint(Component.literal(label(
                "Buscar dossier, raza, rareza, Trial, Executor, reliquia, estructura o ruta…",
                "Search dossier, race, rarity, Trial, Executor, relic, structure or route…")));
        searchBox.setResponder(value -> refreshResults());
        addRenderableWidget(searchBox);

        resultsY = searchY + 25;
        int available = Math.max(0, panelY + panelH - resultsY - 24);
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
                        ? label("Sin coincidencias verificables", "No verified matches") : ""));
                continue;
            }
            SiegeOperationsIndex.Entry entry = results.get(i);
            String prefix = switch (entry.kind()) {
                case ROUTE -> label("RUTA", "ROUTE");
                case INTEL -> "INTEL";
                case ARMORY -> label("ARSENAL", "ARMORY");
                case KNOWLEDGE -> label("ARCHIVO", "FILE");
            };
            button.setMessage(Component.literal(prefix + " · " + entry.title() + "  //  " + entry.subtitle()));
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
            case THREATS -> minecraft.setScreen(new SiegeThreatBoardScreen(this));
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
        String title = label("SALA DE OPERACIONES", "WAR ROOM") + " // " + SiegeRuntimeStatus.version();
        g.drawString(font, fit(title, panelW - 24), tx, ty, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Briefing, Atlas, amenazas, despliegue, Intel, archivo y herramientas técnicas desde un solo lugar.",
                "Briefing, Atlas, threats, deployment, Intel, archive and technical tools from one place."), panelW - 24),
                tx, ty + 12, SiegeTheme.MUTED, false);

        int statusY = ty + 30;
        g.drawString(font, fit(label("CLIENTE ", "CLIENT ") + SiegeRuntimeStatus.healthLabel(spanish()) + " · "
                        + SiegeRuntimeStatus.profileFitLabel(spanish()), panelW - 24),
                tx, statusY, SiegeRuntimeStatus.healthAccent(), false);
        g.drawString(font, fit(SiegeRuntimeStatus.intelLabel(spanish()), panelW - 24), tx, statusY + 11, SiegeTheme.GOLD, false);
        if (!compact) {
            String knowledge = label("ARCHIVO ", "ARCHIVE ") + SiegeKnowledgeRegistry.entries().size()
                    + " · " + label("CRÍTICOS ", "CRITICAL ") + SiegeKnowledgeRegistry.critical().size()
                    + " · " + label("RUTAS ", "ROUTES ") + SiegeOperationsIndex.Route.values().length;
            g.drawString(font, fit(knowledge, panelW - 24), tx, statusY + 22, SiegeTheme.GREEN, false);
            int scene = SiegeBackgrounds.currentIndex(System.currentTimeMillis());
            String line = SiegeBackgrounds.sceneTag(scene, spanish()) + " · " + SiegeBackgrounds.name(scene, spanish())
                    + "   |   " + SiegeRuntimeStatus.audioLabel(spanish());
            g.drawString(font, fit(line, panelW - 24), tx, statusY + 33, SiegeTheme.CYAN, false);
        }

        SiegeTheme.divider(g, panelX + 10, routeTop - 7, panelW - 20, SiegeTheme.CYAN);
        g.drawString(font, label("RUTAS OPERACIONALES", "OPERATIONAL ROUTES"), panelX + 13, routeTop - 18, SiegeTheme.CYAN, false);

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

    private String routeLabel(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> "BRIEFING"; case ATLAS -> "ATLAS"; case THREATS -> label("AMENAZAS", "THREATS");
        case DEPLOYMENT -> label("DESPLIEGUE", "DEPLOYMENT"); case INTEL -> "INTEL";
        case KNOWLEDGE -> label("ENCICLOPEDIA", "ENCYCLOPEDIA"); case ARCHIVE -> label("ARCHIVO", "ARCHIVE");
        case ARMORY -> label("ARSENAL", "ARMORY"); case FIELD_MANUAL -> label("MANUAL", "MANUAL");
        case COMMAND -> label("COMANDO", "COMMAND"); case DIAGNOSTICS -> label("DIAGNÓSTICO", "DIAGNOSTICS");
        case SETTINGS -> label("AJUSTES", "SETTINGS"); case BACKGROUNDS -> label("FONDOS", "BACKGROUNDS"); }; }

    private String routeShort(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> "BRF"; case ATLAS -> "ATL"; case THREATS -> "THR"; case DEPLOYMENT -> "DEP";
        case INTEL -> "INT"; case KNOWLEDGE -> "ENC"; case ARCHIVE -> "ARC"; case ARMORY -> "ARS";
        case FIELD_MANUAL -> "FLD"; case COMMAND -> "CMD"; case DIAGNOSTICS -> "DIA";
        case SETTINGS -> "CFG"; case BACKGROUNDS -> "BG"; }; }

    private String routeDescription(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> label("Ruta corta para entender lo esencial antes de jugar.", "Short route to understand the essentials before playing.");
        case ATLAS -> label("Razas, sistemas, investigación e histórico del servidor.", "Races, systems, research and server history.");
        case THREATS -> label("Dossiers, Executores, bosses, raids y eventos.", "Dossiers, Executors, bosses, raids and events.");
        case DEPLOYMENT -> label("Servidor oficial, compatibilidad y conexión.", "Official server, compatibility and connection.");
        case INTEL -> label("Dossiers actuales de unidades y amenazas.", "Current unit and threat dossiers.");
        case KNOWLEDGE -> label("Archivo detallado de la enciclopedia 3.00.", "Detailed 3.00 encyclopedia archive.");
        case ARCHIVE -> label("SIEGE, 2044, facciones, Núcleo, Gates/Rifts e inspiraciones.", "SIEGE, 2044, factions, Core, Gates/Rifts and inspirations.");
        case ARMORY -> label("Equipamiento, objetos y evidencia multimedia.", "Equipment, items and multimedia evidence.");
        case FIELD_MANUAL -> label("Estados de muerte/heridas, misiones y protocolos.", "Death/injury states, missions and protocols.");
        case COMMAND -> label("Perfil visual y estado del cliente.", "Visual profile and client state.");
        case DIAGNOSTICS -> label("Problemas técnicos y recuperación explícita.", "Technical problems and explicit recovery.");
        case SETTINGS -> label("Configuración visual, audio, Intel y accesibilidad.", "Visual, audio, Intel and accessibility configuration.");
        case BACKGROUNDS -> label("Galería y rotación de escenas del menú.", "Menu scene gallery and rotation."); }; }

    private int routeAccent(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> SiegeTheme.ORANGE; case ATLAS -> SiegeTheme.GREEN; case THREATS -> SiegeTheme.RED;
        case DEPLOYMENT -> SiegeTheme.RED; case INTEL, ARCHIVE, ARMORY, FIELD_MANUAL -> SiegeTheme.GOLD;
        case KNOWLEDGE -> SiegeTheme.GREEN; case COMMAND -> SiegeTheme.CYAN; case DIAGNOSTICS -> SiegeTheme.ORANGE;
        case SETTINGS -> SiegeTheme.RED; case BACKGROUNDS -> SiegeTheme.BLUE; }; }

    private String routeIcon(SiegeOperationsIndex.Route route) { return switch (route) {
        case BRIEFING -> "shield"; case ATLAS -> "overview"; case THREATS -> "intel"; case DEPLOYMENT -> "connect";
        case INTEL -> "intel"; case KNOWLEDGE -> "search"; case ARCHIVE -> "overview"; case ARMORY -> "package";
        case FIELD_MANUAL -> "overview"; case COMMAND, DIAGNOSTICS -> "shield"; case SETTINGS -> "settings";
        case BACKGROUNDS -> "image"; }; }

    private String fit(String value, int pixels) {
        if (pixels <= 0) return ""; if (font.width(value) <= pixels) return value;
        if (pixels <= font.width("…")) return font.plainSubstrByWidth(value, pixels);
        return font.plainSubstrByWidth(value, pixels - font.width("…")) + "…";
    }
    private boolean spanish() { return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_"); }
    private String label(String es, String en) { return spanish() ? es : en; }
    @Override public void onClose() { minecraft.setScreen(parent); }
}

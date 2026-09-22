package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * First-contact screen for players who want to understand the server before
 * opening deep Intel or advanced configuration.
 */
public final class SiegeRecruitBriefingScreen extends Screen {
    private enum Topic {
        SERVER, RACES, PROGRESSION, SURVIVAL, THREATS, EQUIPMENT, DEPLOYMENT, MEDIA
    }

    private final Screen parent;
    private final List<SiegeButton> topicButtons = new ArrayList<>();
    private Topic selected = Topic.SERVER;
    private int panelX, panelY, panelW, panelH, gridX, gridY, gridW, detailY, detailH;
    private boolean compact;

    public SiegeRecruitBriefingScreen(Screen parent) {
        super(Component.literal("SIEGE // RECRUIT BRIEFING"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        topicButtons.clear();
        compact = width < 620 || height < 370;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 38;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 18 : 24));
        gridX = panelX + 10;
        gridY = panelY + 56;
        gridW = panelW - 20;

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 88, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));

        int cols = compact ? 2 : 4;
        int gap = 5;
        int h = compact ? 21 : 24;
        int cellW = Math.max(70, (gridW - gap * (cols - 1)) / cols);
        Topic[] topics = Topic.values();
        for (int i = 0; i < topics.length; i++) {
            Topic topic = topics[i];
            int row = i / cols;
            int col = i % cols;
            int x = gridX + col * (cellW + gap);
            int w = col == cols - 1 ? gridX + gridW - x : cellW;
            int y = gridY + row * (h + gap);
            SiegeButton button = new SiegeButton(x, y, w, h, Component.literal(topicLabel(topic)),
                    b -> select(topic), topicAccent(topic)).withIcon(topicIcon(topic)).setCompactCenter(true)
                    .setSelected(topic == selected);
            button.setTooltip(Tooltip.create(Component.literal(topicDescription(topic))));
            topicButtons.add(addRenderableWidget(button));
        }

        int rows = (topics.length + cols - 1) / cols;
        detailY = gridY + rows * (h + gap) + 8;
        detailH = Math.max(75, panelY + panelH - detailY - 10);
    }

    private void select(Topic topic) {
        selected = topic;
        SiegeUiSounds.selection();
        for (int i = 0; i < topicButtons.size(); i++) topicButtons.get(i).setSelected(Topic.values()[i] == topic);
    }

    private void openSelected() {
        SiegeUiSounds.confirm();
        switch (selected) {
            case SERVER -> minecraft.setScreen(new SiegeKnowledgeScreen(this, "server-overview"));
            case RACES -> minecraft.setScreen(new SiegeRaceAtlasScreen(this));
            case PROGRESSION -> minecraft.setScreen(new SiegeProgressionMapScreen(this));
            case SURVIVAL -> minecraft.setScreen(new SiegeArchiveScreen(this));
            case THREATS -> minecraft.setScreen(new SiegeThreatBoardScreen(this));
            case EQUIPMENT -> minecraft.setScreen(new SiegeGuideScreen(this, SiegeGuideScreen.Mode.ARMORY));
            case DEPLOYMENT -> minecraft.setScreen(new SiegeMultiplayerScreen(this));
            case MEDIA -> minecraft.setScreen(new SiegeMediaRoomScreen(this));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= gridX && mouseX <= gridX + gridW
                && mouseY >= detailY && mouseY <= detailY + detailH) {
            openSelected();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xCB000000 : 0xA4000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, SiegeTheme.CYAN);

        g.drawString(font, label("BRIEFING PARA RECLUTAS", "RECRUIT BRIEFING") + " // " + SiegeRuntimeStatus.version(),
                panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Una entrada rápida al servidor. Cada bloque abre el sistema especializado correspondiente.",
                "A fast entry into the server. Each block opens the matching specialized system."), panelW - 24),
                panelX + 12, panelY + 22, SiegeTheme.MUTED, false);
        g.drawString(font, fit(label(
                "No usa datos personales: sólo conocimiento general de Eternal Craft / SIEGE.",
                "No personal data: only general Eternal Craft / SIEGE knowledge."), panelW - 24),
                panelX + 12, panelY + 34, SiegeTheme.GREEN, false);

        int accent = topicAccent(selected);
        SiegeTheme.panel(g, gridX - 3, detailY - 3, gridW + 6, detailH + 6, accent);
        int x = gridX + 10;
        int y = detailY + 9;
        int w = gridW - 20;
        g.drawString(font, topicLabel(selected), x, y, accent, false);
        y += 14;
        g.drawString(font, fit(topicDescription(selected), w), x, y, SiegeTheme.INK, false);
        y += 18;
        String[] bullets = topicBullets(selected);
        for (String bullet : bullets) {
            if (y + font.lineHeight >= detailY + detailH - 30) break;
            g.drawString(font, fit("• " + bullet, w), x, y, SiegeTheme.MUTED, false);
            y += font.lineHeight + 3;
        }

        int by = detailY + detailH - 22;
        boolean hot = mouseX >= x && mouseX <= x + w && mouseY >= by && mouseY <= by + 16;
        g.fill(x, by, x + w, by + 16, hot ? 0xD13A4C55 : 0xB51A252B);
        g.fill(x, by, x + 2, by + 16, accent);
        g.drawCenteredString(font, label("ABRIR", "OPEN") + " // " + topicLabel(selected), x + w / 2, by + 4,
                hot ? SiegeTheme.INK : SiegeTheme.MUTED);

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private String topicLabel(Topic topic) {
        return switch (topic) {
            case SERVER -> label("QUÉ ES SIEGE", "WHAT IS SIEGE");
            case RACES -> label("RAZAS", "RACES");
            case PROGRESSION -> label("PROGRESIÓN", "PROGRESSION");
            case SURVIVAL -> label("SUPERVIVENCIA", "SURVIVAL");
            case THREATS -> label("AMENAZAS", "THREATS");
            case EQUIPMENT -> label("ARSENAL", "ARMORY");
            case DEPLOYMENT -> label("DESPLIEGUE", "DEPLOYMENT");
            case MEDIA -> label("SALA MULTIMEDIA", "MEDIA ROOM");
        };
    }

    private String topicDescription(Topic topic) {
        return switch (topic) {
            case SERVER -> label("Servidor, sistemas principales, diferencia entre lore, reglas e histórico.",
                    "Server, core systems, and the difference between lore, rules and history.");
            case RACES -> label("Catálogo de razas, rarezas conocidas, progresión y qué datos siguen abiertos.",
                    "Race catalog, known rarities, progression and what remains unresolved.");
            case PROGRESSION -> label("V1→V4, rutas especiales, Trials, investigación, reliquias y sistemas avanzados.",
                    "V1→V4, special routes, Trials, research, relics and advanced systems.");
            case SURVIVAL -> label("Muerte, heridas, revive, misiones y protocolos que conviene conocer antes de arriesgar recursos.",
                    "Death, injuries, revive, missions and protocols to know before risking resources.");
            case THREATS -> label("Intel de unidades, bosses, Executores, raids, estructuras y otros riesgos documentados.",
                    "Unit Intel, bosses, Executors, raids, structures and other documented risks.");
            case EQUIPMENT -> label("Objetos, reliquias, Third Justice, Assembling y herramientas de investigación.",
                    "Items, relics, Third Justice, Assembling and research tools.");
            case DEPLOYMENT -> label("Servidor oficial, compatibilidad, ping, conexión y estados de despliegue.",
                    "Official server, compatibility, ping, connection and deployment states.");
            case MEDIA -> label("Fondos, soundtrack, controles de reproducción, referencias DVN y material multimedia.",
                    "Backgrounds, soundtrack, playback controls, DVN references and multimedia material.");
        };
    }

    private String[] topicBullets(Topic topic) {
        return switch (topic) {
            case SERVER -> new String[] {
                    label("Empieza por la Enciclopedia del Servidor.", "Start with the Server Encyclopedia."),
                    label("Los datos antiguos permanecen fechados como histórico.", "Old data remains dated as history."),
                    label("No se rellenan requisitos o stats inexistentes.", "Missing requirements or stats are not invented.") };
            case RACES -> new String[] {
                    label("Orden de rareza completo documentado.", "Documented complete rarity order."),
                    label("17 familias/razas visibles en el atlas inicial.", "17 visible race/family entries in the initial atlas."),
                    label("Rareza desconocida se muestra como SIN CONFIRMAR.", "Unknown rarity is shown as UNCONFIRMED.") };
            case PROGRESSION -> new String[] {
                    label("Ruta general y rutas especiales separadas.", "General and special routes are separated."),
                    label("Trials no tienen una receta universal.", "Trials do not have one universal recipe."),
                    label("Cada nodo abre su fuente de enciclopedia.", "Every node opens its encyclopedia source.") };
            case SURVIVAL -> new String[] {
                    label("Estados de muerte/heridas en un manual propio.", "Death/injury states live in their own manual."),
                    label("Versiones viejas no sustituyen reglas actuales.", "Old versions do not overwrite current rules."),
                    label("Protocolos priorizan claridad y salida segura.", "Protocols prioritize clarity and safe exit.") };
            case THREATS -> new String[] {
                    label("Intel permanece separado del lore general.", "Intel remains separate from general lore."),
                    label("UNKNOWN se conserva cuando falta fuente fiable.", "UNKNOWN remains when reliable sourcing is missing."),
                    label("Threat Board resume dominios sin inventar amenazas nuevas.", "Threat Board summarizes domains without inventing new threats.") };
            case EQUIPMENT -> new String[] {
                    label("Geography Table = investigación; Daemonium Kit = extracción.", "Geography Table = research; Daemonium Kit = extraction."),
                    label("Third Justice mantiene su evidencia multimedia separada.", "Third Justice keeps its multimedia evidence separate."),
                    label("Precios antiguos siempre llevan fecha/contexto.", "Old prices always keep date/context.") };
            case DEPLOYMENT -> new String[] {
                    label("Servidor oficial protegido contra Editar/Eliminar.", "Official server protected from Edit/Delete."),
                    label("Callbacks vanilla siguen siendo autoritativos.", "Vanilla callbacks remain authoritative."),
                    label("Estados QUERYING/OFFLINE/NO RESPONSE/INCOMPATIBLE/ONLINE.", "QUERYING/OFFLINE/NO RESPONSE/INCOMPATIBLE/ONLINE states.") };
            case MEDIA -> new String[] {
                    label("Tempest Jutcherson sigue fuera de la galería normal.", "Tempest Jutcherson remains outside the normal gallery."),
                    label("Música aleatoria sin repetición hasta completar la bolsa.", "Random music without repeats until the bag completes."),
                    label("Referencias externas no se distribuyen sin derechos claros.", "External references are not redistributed without clear rights.") };
        };
    }

    private int topicAccent(Topic topic) {
        return switch (topic) {
            case SERVER, DEPLOYMENT -> SiegeTheme.RED;
            case RACES, PROGRESSION, EQUIPMENT -> SiegeTheme.GOLD;
            case SURVIVAL -> SiegeTheme.GREEN;
            case THREATS -> SiegeTheme.ORANGE;
            case MEDIA -> SiegeTheme.CYAN;
        };
    }

    private String topicIcon(Topic topic) {
        return switch (topic) {
            case SERVER -> "overview";
            case RACES -> "intel";
            case PROGRESSION -> "overview";
            case SURVIVAL -> "shield";
            case THREATS -> "intel";
            case EQUIPMENT -> "package";
            case DEPLOYMENT -> "connect";
            case MEDIA -> "image";
        };
    }

    private String fit(String text, int px) {
        if (text == null || px <= 0) return "";
        if (font.width(text) <= px) return text;
        return font.plainSubstrByWidth(text, Math.max(1, px - font.width("…"))) + "…";
    }
    private boolean spanish() { return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_"); }
    private String label(String es, String en) { return spanish() ? es : en; }
    @Override public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

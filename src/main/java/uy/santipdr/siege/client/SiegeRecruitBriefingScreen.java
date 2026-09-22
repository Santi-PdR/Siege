package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** First-contact briefing for SIEGE 4.0. */
public final class SiegeRecruitBriefingScreen extends Screen {
    private enum Topic { SERVER, RACES, PROGRESSION, SURVIVAL, THREATS, EQUIPMENT, DEPLOYMENT, MEDIA }

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
        compact = width < 650 || height < 390;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 38;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 18 : 24));
        gridX = panelX + 10;
        gridW = panelW - 20;

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 88, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));
        int searchW = compact ? 74 : 112;
        addRenderableWidget(new SiegeButton(Math.max(8, width - searchW - 8), 7, searchW, 19,
                Component.literal(label("BUSCAR", "SEARCH")),
                b -> minecraft.setScreen(new SiegeOperationsHubScreen(this)), SiegeTheme.CYAN)
                .withIcon("search").setCompactCenter(true));

        int introLines = compact ? 2 : 2;
        gridY = panelY + 40 + introLines * 12;
        int cols = panelW < 430 ? 2 : panelW < 900 ? 4 : 4;
        int gap = 5;
        int h = compact ? 20 : 23;
        int cellW = Math.max(64, (gridW - gap * (cols - 1)) / cols);
        Topic[] topics = Topic.values();
        for (int i = 0; i < topics.length; i++) {
            Topic topic = topics[i];
            int row = i / cols;
            int col = i % cols;
            int x = gridX + col * (cellW + gap);
            int right = col == cols - 1 ? gridX + gridW : x + cellW;
            int y = gridY + row * (h + gap);
            SiegeButton button = new SiegeButton(x, y, Math.max(40, right - x), h,
                    Component.literal(topicLabel(topic)), b -> select(topic), topicAccent(topic))
                    .withIcon(topicIcon(topic)).setCompactCenter(true).setSelected(topic == selected);
            button.setTooltip(Tooltip.create(Component.literal(topicDescription(topic))));
            topicButtons.add(addRenderableWidget(button));
        }

        int rows = (topics.length + cols - 1) / cols;
        detailY = gridY + rows * (h + gap) + 9;
        detailH = Math.max(68, panelY + panelH - detailY - 10);
    }

    private void select(Topic topic) {
        selected = topic;
        SiegeUiSounds.selection();
        for (int i = 0; i < topicButtons.size(); i++)
            topicButtons.get(i).setSelected(Topic.values()[i] == topic);
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

        int textW = panelW - 24;
        g.drawString(font, fit(label("BRIEFING DE RECLUTA", "RECRUIT BRIEFING")
                + " // " + SiegeRuntimeStatus.version(), textW), panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Si recién entrás, empezá acá: cada bloque responde una pregunta distinta.",
                "New here? Start here: each block answers a different question."), textW),
                panelX + 12, panelY + 22, SiegeTheme.MUTED, false);
        g.drawString(font, fit(label(
                "Elegí un tema y después abrilo para ver la pantalla especializada.",
                "Choose a topic, then open it to enter the specialized screen."), textW),
                panelX + 12, panelY + 34, SiegeTheme.CYAN, false);

        int accent = topicAccent(selected);
        SiegeTheme.panel(g, gridX - 3, detailY - 3, gridW + 6, detailH + 6, accent);
        int x = gridX + 10;
        int y = detailY + 9;
        int w = gridW - 20;
        g.drawString(font, fit(topicLabel(selected), w), x, y, accent, false);
        y += 14;

        for (FormattedCharSequence line : font.split(Component.literal(topicDescription(selected)), w)) {
            if (y > detailY + detailH - 55) break;
            g.drawString(font, line, x, y, SiegeTheme.INK, false);
            y += font.lineHeight + 2;
        }
        y += 4;
        for (String bullet : topicBullets(selected)) {
            for (FormattedCharSequence line : font.split(Component.literal("• " + bullet), w)) {
                if (y + font.lineHeight >= detailY + detailH - 28) break;
                g.drawString(font, line, x, y, SiegeTheme.MUTED, false);
                y += font.lineHeight + 2;
            }
            if (y + font.lineHeight >= detailY + detailH - 28) break;
        }

        int by = detailY + detailH - 22;
        boolean hot = mouseX >= x && mouseX <= x + w && mouseY >= by && mouseY <= by + 16;
        g.fill(x, by, x + w, by + 16, hot ? 0xD13A4C55 : 0xB51A252B);
        g.fill(x, by, x + 2, by + 16, accent);
        g.drawCenteredString(font, fit(label("ABRIR", "OPEN") + " // " + topicLabel(selected), w - 8),
                x + w / 2, by + 4, hot ? SiegeTheme.INK : SiegeTheme.MUTED);

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
            case MEDIA -> label("MULTIMEDIA", "MEDIA");
        };
    }

    private String topicDescription(Topic topic) {
        return switch (topic) {
            case SERVER -> label("Qué tipo de servidor es, cómo se divide la información y cuáles son sus sistemas principales.",
                    "What kind of server it is, how information is organized and which systems matter most.");
            case RACES -> label("Razas conocidas, orden de rareza, variantes y formas generales de progresar.",
                    "Known races, rarity order, variants and general progression routes.");
            case PROGRESSION -> label("V1→V4, Trials, exploración, reliquias, Assembling, dimensiones y otras rutas de avance.",
                    "V1→V4, Trials, exploration, relics, Assembling, dimensions and other progression routes.");
            case SURVIVAL -> label("Estados de muerte/heridas, revive, misiones y protocolos que conviene conocer antes de arriesgar recursos.",
                    "Death/injury states, revival, missions and protocols to know before risking resources.");
            case THREATS -> label("Unidades, bosses, Executores, raids, estructuras y otros riesgos que conviene reconocer antes de combatir.",
                    "Units, bosses, Executors, raids, structures and other risks worth recognizing before combat.");
            case EQUIPMENT -> label("Objetos, reliquias, Third Justice, Assembling y herramientas de investigación.",
                    "Items, relics, Third Justice, Assembling and research tools.");
            case DEPLOYMENT -> label("Servidor oficial, compatibilidad, ping, conexión y estados de despliegue.",
                    "Official server, compatibility, ping, connection and deployment states.");
            case MEDIA -> label("Fondos, soundtrack, galería, controles de reproducción y estética Dummies vs Noobs.",
                    "Backgrounds, soundtrack, gallery, playback controls and Dummies vs Noobs visual direction.");
        };
    }

    private String[] topicBullets(Topic topic) {
        return switch (topic) {
            case SERVER -> new String[] {
                    label("La Enciclopedia explica sistemas generales; Intel queda para unidades.", "The Encyclopedia explains general systems; Intel stays for units."),
                    label("El Manual de Campo concentra heridas, revive y protocolos.", "The Field Manual concentrates injuries, revival and protocols."),
                    label("Arsenal concentra objetos y equipamiento.", "Armory concentrates items and equipment.") };
            case RACES -> new String[] {
                    label("Rarezas: Común → Poco común → Raro → Ultra raro → Legendario → Obsainan → Mítico → Godly → Eternal → Fabled.",
                            "Rarities: Common → Uncommon → Rare → Ultra Rare → Legendary → Obsainan → Mythic → Godly → Eternal → Fabled."),
                    label("No todas las razas usan V1→V4.", "Not every race uses V1→V4."),
                    label("El Atlas separa variantes y datos históricos.", "The Atlas separates variants and historical data.") };
            case PROGRESSION -> new String[] {
                    label("Muchos Trials o artefactos usan V4, pero no todos.", "Many Trials or artifacts use V4, but not all."),
                    label("Explorar, investigar reliquias y mejorar movilidad también forman parte del progreso.", "Exploration, relic research and mobility are also progression."),
                    label("Las rutas especiales se muestran por separado.", "Special routes are shown separately.") };
            case SURVIVAL -> new String[] {
                    label("El sistema de heridas/revive cambió varias veces.", "The injury/revival system changed several times."),
                    label("Usá el Manual de Campo para la referencia operativa.", "Use the Field Manual for operational reference."),
                    label("Entrá a dimensiones y raids con una salida preparada.", "Enter dimensions and raids with an exit plan.") };
            case THREATS -> new String[] {
                    label("Intel mantiene UNIT / ADVANCED / TANK / BOSS / ELITE / SUPER-UNIT / UNKNOWN.", "Intel keeps UNIT / ADVANCED / TANK / BOSS / ELITE / SUPER-UNIT / UNKNOWN."),
                    label("Executores tienen su propio comportamiento y terror radius.", "Executors have their own behavior and terror radius."),
                    label("Variar tácticas importa contra amenazas que pueden adaptarse.", "Varying tactics matters against threats that may adapt.") };
            case EQUIPMENT -> new String[] {
                    label("Geography Table revela información oculta de ciertos objetos/reliquias.", "Geography Table reveals hidden information on some items/relics."),
                    label("Daemonium Kit está relacionado con extraer materiales de reliquias.", "Daemonium Kit is related to extracting relic materials."),
                    label("Assembling instala chips y trasplantes avanzados.", "Assembling installs advanced chips and transplants.") };
            case DEPLOYMENT -> new String[] {
                    label("El servidor oficial aparece primero y está protegido contra Editar/Eliminar.", "The official server appears first and is protected from Edit/Delete."),
                    label("La pantalla muestra estados de conexión y compatibilidad.", "The screen shows connection and compatibility states."),
                    label("La conexión sigue usando el flujo normal de Minecraft.", "Connection still uses Minecraft's normal flow.") };
            case MEDIA -> new String[] {
                    label("La galería normal sólo usa escenas serias del menú.", "The normal gallery only uses serious menu scenes."),
                    label("Tempest Jutcherson sigue reservado como easter egg.", "Tempest Jutcherson remains reserved as an easter egg."),
                    label("La Sala Multimedia también reúne ideas de soundtrack y dirección visual DVN.", "The Media Room also collects soundtrack ideas and DVN visual direction.") };
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
            case SERVER, PROGRESSION -> "overview";
            case RACES, THREATS -> "intel";
            case SURVIVAL -> "shield";
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
    @Override public void onClose() { SiegeUiSounds.back(); if (minecraft != null) minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

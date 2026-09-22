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
    private int detailOffset;
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

        // Eight destinations always fit in two rows on normal supported widths.
        // This avoids the old 2-column/4-row layout that pushed the detail panel
        // into the footer at high GUI scales.
        int cols = panelW < 280 ? 2 : 4;
        int gap = compact ? 3 : 5;
        int h = compact ? 18 : 22;
        gridY = panelY + (compact ? 48 : 57);
        int cellW = Math.max(46, (gridW - gap * (cols - 1)) / cols);
        Topic[] topics = Topic.values();
        for (int i = 0; i < topics.length; i++) {
            Topic topic = topics[i];
            int row = i / cols;
            int col = i % cols;
            int x = gridX + col * (cellW + gap);
            int right = col == cols - 1 ? gridX + gridW : x + cellW;
            int y = gridY + row * (h + gap);
            int buttonW = Math.max(40, right - x);
            SiegeButton button = new SiegeButton(x, y, buttonW, h,
                    Component.literal(topicButtonLabel(topic, buttonW)), b -> select(topic), topicAccent(topic))
                    .withIcon(topicIcon(topic)).setCompactCenter(true).setSelected(topic == selected);
            button.setTooltip(Tooltip.create(Component.literal(topicDescription(topic))));
            topicButtons.add(addRenderableWidget(button));
        }

        int rows = (topics.length + cols - 1) / cols;
        detailY = gridY + rows * (h + gap) + (compact ? 5 : 9);
        detailH = Math.max(34, panelY + panelH - detailY - 9);
    }

    private void select(Topic topic) {
        selected = topic;
        detailOffset = 0;
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
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= gridX && mouseX <= gridX + gridW && mouseY >= detailY && mouseY <= detailY + detailH) {
            detailOffset = Math.max(0, detailOffset - (int)Math.signum(delta) * 2);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= gridX && mouseX <= gridX + gridW
                && mouseY >= detailY + detailH - 21 && mouseY <= detailY + detailH) {
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
                "Todo lo esencial del servidor está separado por tema. Elegí qué querés entender.",
                "Everything essential is separated by topic. Choose what you want to understand."), textW),
                panelX + 12, panelY + 22, SiegeTheme.MUTED, false);
        if (!compact) {
            g.drawString(font, fit(label(
                    "Cada bloque abre una pantalla dedicada: sin mezclar razas, amenazas, equipo y supervivencia.",
                    "Each block opens a dedicated screen: races, threats, equipment and survival stay separate."), textW),
                    panelX + 12, panelY + 34, SiegeTheme.CYAN, false);
        }

        int accent = topicAccent(selected);
        SiegeTheme.panel(g, gridX - 3, detailY - 3, gridW + 6, detailH + 6, accent);
        int x = gridX + 10;
        int y = detailY + 8;
        int w = Math.max(40, gridW - 20);
        g.drawString(font, fit(topicLabel(selected), w), x, y, accent, false);
        y += 14;

        List<FormattedCharSequence> lines = new ArrayList<>();
        lines.addAll(font.split(Component.literal(topicDescription(selected)), w));
        lines.add(Component.empty().getVisualOrderText());
        for (String bullet : topicBullets(selected)) {
            lines.addAll(font.split(Component.literal("• " + bullet), w));
        }

        int by = detailY + detailH - 21;
        int maxY = by - 4;
        int first = Math.max(0, Math.min(detailOffset, Math.max(0, lines.size() - 1)));
        g.enableScissor(gridX, y - 1, gridX + gridW, maxY);
        int yy = y;
        for (int i = first; i < lines.size() && yy + font.lineHeight <= maxY; i++) {
            g.drawString(font, lines.get(i), x, yy, i == first ? SiegeTheme.INK : SiegeTheme.MUTED, false);
            yy += font.lineHeight + 2;
        }
        g.disableScissor();

        boolean hot = mouseX >= x && mouseX <= x + w && mouseY >= by && mouseY <= by + 16;
        g.fill(x, by, x + w, by + 16, hot ? 0xD13A4C55 : 0xB51A252B);
        g.fill(x, by, x + 2, by + 16, accent);
        g.drawCenteredString(font, fit(label("ABRIR", "OPEN") + " // " + topicLabel(selected), w - 8),
                x + w / 2, by + 4, hot ? SiegeTheme.INK : SiegeTheme.MUTED);

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private String topicButtonLabel(Topic topic, int buttonW) {
        String full = topicLabel(topic);
        int usable = Math.max(8, buttonW - 27);
        if (font.width(full) <= usable) return full;
        return switch (topic) {
            case SERVER -> label("SIEGE", "SIEGE");
            case RACES -> label("RAZAS", "RACES");
            case PROGRESSION -> label("PROG.", "PROG.");
            case SURVIVAL -> label("VIVIR", "SURVIVE");
            case THREATS -> label("RIESGOS", "THREATS");
            case EQUIPMENT -> label("ARSENAL", "ARMORY");
            case DEPLOYMENT -> label("DEPLOY", "DEPLOY");
            case MEDIA -> label("MEDIA", "MEDIA");
        };
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
            case SERVER -> label("Qué es Eternal Craft – SIEGE y cómo se organizan sus sistemas principales.",
                    "What Eternal Craft – SIEGE is and how its main systems are organized.");
            case RACES -> label("Razas conocidas, rarezas, variantes, slots y formas generales de progresar.",
                    "Known races, rarities, variants, slots and general progression routes.");
            case PROGRESSION -> label("V1→V4, Trials, exploración, reliquias, Assembling, dimensiones y rutas especiales.",
                    "V1→V4, Trials, exploration, relics, Assembling, dimensions and special routes.");
            case SURVIVAL -> label("Heridas, muerte, revive, misiones y protocolos que conviene conocer antes de arriesgar recursos.",
                    "Injuries, death, revival, missions and protocols to know before risking resources.");
            case THREATS -> label("Unidades, bosses, Executores, raids, estructuras y peligros que conviene reconocer antes de combatir.",
                    "Units, bosses, Executors, raids, structures and dangers worth recognizing before combat.");
            case EQUIPMENT -> label("Objetos, reliquias, Third Justice, Assembling y herramientas importantes del servidor.",
                    "Items, relics, Third Justice, Assembling and important server tools.");
            case DEPLOYMENT -> label("Servidor oficial, compatibilidad, ping, conexión y estados de despliegue.",
                    "Official server, compatibility, ping, connection and deployment states.");
            case MEDIA -> label("Fondos, música actual, galería y referencias visuales/sonoras inspiradas en Dummies vs Noobs.",
                    "Backgrounds, current music, gallery and Dummies vs Noobs inspired visual/audio references.");
        };
    }

    private String[] topicBullets(Topic topic) {
        return switch (topic) {
            case SERVER -> new String[] {
                    label("Enciclopedia: sistemas y conceptos generales del servidor.", "Encyclopedia: general server systems and concepts."),
                    label("Intel: unidades y amenazas concretas.", "Intel: specific units and threats."),
                    label("Manual de Campo: heridas, revive, misiones y protocolos.", "Field Manual: injuries, revival, missions and protocols."),
                    label("Arsenal: objetos, reliquias y equipamiento.", "Armory: items, relics and equipment.") };
            case RACES -> new String[] {
                    label("Rarezas: Común → Poco común → Raro → Ultra raro → Legendario → Obsainan → Mítico → Godly → Eternal → Fabled.",
                            "Rarities: Common → Uncommon → Rare → Ultra Rare → Legendary → Obsainan → Mythic → Godly → Eternal → Fabled."),
                    label("Human, Hacker, Shark, Saiyan, Deteriorer, Faraón, Apotheosis, Muerte, Cyborg, Ghoul, Subhuman, Terrariano, Kaioshin, Dragon, Shinigami y Majin están documentadas.",
                            "Human, Hacker, Shark, Saiyan, Deteriorer, Pharaoh, Apotheosis, Death, Cyborg, Ghoul, Subhuman, Terrarian, Kaioshin, Dragon, Shinigami and Majin are documented."),
                    label("No todas las razas progresan con V1→V4.", "Not every race progresses through V1→V4.") };
            case PROGRESSION -> new String[] {
                    label("Los requisitos dependen de la raza o sistema: no existe una receta universal.", "Requirements depend on the race or system: there is no universal recipe."),
                    label("Trials pueden pedir raza/versión, nivel, kills u objetos.", "Trials may require race/version, level, kills or items."),
                    label("Exploración, movilidad, investigación de reliquias y Assembling también forman parte del progreso.", "Exploration, mobility, relic research and Assembling are also progression.") };
            case SURVIVAL -> new String[] {
                    label("Las reglas de heridas y revive cambiaron: el Manual separa lo útil de lo antiguo.", "Injury and revival rules changed: the Manual separates useful guidance from old rules."),
                    label("Entrá a dimensiones y raids con una salida preparada.", "Enter dimensions and raids with an exit plan."),
                    label("No gastes recursos raros sin entender el siguiente paso.", "Do not spend rare resources without understanding the next step.") };
            case THREATS -> new String[] {
                    label("Intel mantiene UNIT / ADVANCED / TANK / BOSS / ELITE / SUPER-UNIT / UNKNOWN.", "Intel keeps UNIT / ADVANCED / TANK / BOSS / ELITE / SUPER-UNIT / UNKNOWN."),
                    label("Executores se reconocen por su terror radius y tienen sistemas propios.", "Executors are recognized by their terror radius and have their own systems."),
                    label("Algunas amenazas castigan repetir siempre la misma táctica.", "Some threats punish repeating the same tactic every time.") };
            case EQUIPMENT -> new String[] {
                    label("Geography Table sirve para investigar información oculta de ciertos objetos/reliquias.", "Geography Table is used to investigate hidden information on some items/relics."),
                    label("Daemonium Kit se relaciona con extraer materiales de reliquias.", "Daemonium Kit is related to extracting relic materials."),
                    label("Assembling permite trabajar con chips, trasplantes y sistemas Cyborg.", "Assembling works with chips, transplants and Cyborg systems."),
                    label("Third Justice tiene su ficha y material visual dentro de Arsenal.", "Third Justice has its own file and visual material inside Armory.") };
            case DEPLOYMENT -> new String[] {
                    label("El servidor oficial aparece primero y Editar/Eliminar permanecen bloqueados.", "The official server appears first and Edit/Delete stay blocked."),
                    label("Se muestran estados de conexión, compatibilidad y ping sin reemplazar la conexión normal de Minecraft.", "Connection, compatibility and ping are shown without replacing Minecraft's normal connection flow.") };
            case MEDIA -> new String[] {
                    label("La galería normal usa sólo escenas serias; Tempest Jutcherson sigue reservado como easter egg.", "The normal gallery uses serious scenes only; Tempest Jutcherson remains reserved as an easter egg."),
                    label("La playlist instalada sigue usando únicamente los audios que ya forman parte del proyecto.", "The installed playlist keeps using only audio already included in the project."),
                    label("La Sala Multimedia muestra referencias DVN que encajan con el menú sin descargarlas automáticamente.", "The Media Room shows DVN references that fit the menu without downloading them automatically.") };
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

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private String label(String es, String en) { return spanish() ? es : en; }

    @Override
    public void onClose() {
        SiegeUiSounds.back();
        if (minecraft != null) minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}

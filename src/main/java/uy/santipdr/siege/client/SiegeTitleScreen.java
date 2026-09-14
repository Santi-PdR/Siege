package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public final class SiegeTitleScreen extends Screen {
    private static final int ACCENT = 0xFF55BFD9;
    private int menuX;
    private int menuWidth;
    private int menuTop;
    private int menuBottom;
    private final SiegePreviewClock previewClock = new SiegePreviewClock();
    private long manualIntelPreviewUntil;
    private int previewX = -1;
    private int previewY = -1;
    private int previewW;
    private int previewH;
    private int previewCardHeight;
    private int previewGap;
    private boolean previewDual;
    private IntelEntry primaryPreviewEntry;
    private IntelEntry secondaryPreviewEntry;
    private int lastRenderedPreview = -1;
    private int previewTransitionDirection = 1;
    private long previewTransitionStarted;
    private boolean previewReading;
    private long previewCycleStartedAt;
    private SiegeButton musicButton;

    public SiegeTitleScreen() {
        super(Component.literal("Eternal Craft: SIEGE"));
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        boolean compact = SiegeUiLayout.compactTitle(width, height);
        double guiScale = minecraft.getWindow().getGuiScale();
        boolean scaleThree = guiScale >= 2.75D && guiScale < 3.75D && !compact;
        int margin = compact ? 9 : Math.max(14, width / 55);
        menuWidth = scaleThree
                ? Math.min(218, Math.max(198, width / 5))
                : Math.min(compact ? 176 : 212, Math.max(138, width / (compact ? 2 : 5)));
        menuWidth = Math.min(menuWidth, width - margin * 2);
        int buttonHeight = compact ? 18 : scaleThree ? 23 : 22;
        int gap = compact ? 3 : scaleThree ? 5 : 5;
        int totalHeight = buttonHeight * 5 + gap * 4;
        menuX = margin;

        int desiredTop = compact ? 72 : 112;
        menuTop = desiredTop;
        menuTop = Math.min(menuTop, Math.max(50, height - totalHeight - 14));
        menuBottom = menuTop + totalHeight;

        int y = menuTop;
        SiegeButton deployment = addRenderableWidget(command(menuX, y, menuWidth, buttonHeight, "siege.menu.deployment",
                b -> minecraft.setScreen(new SiegeMultiplayerScreen(this))));
        setInitialFocus(deployment);
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.intel",
                b -> minecraft.setScreen(new IntelScreenV3(this))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.armory",
                b -> minecraft.setScreen(new OptionsScreen(this, minecraft.options))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.settings",
                b -> minecraft.setScreen(new SiegeSettingsScreen(this))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.quit",
                b -> requestQuit()));

        int trackWidth = compact ? 88 : 100;
        int musicY = SiegeUiLayout.musicButtonY(height, compact);
        musicButton = addRenderableWidget(new SiegeButton(width - trackWidth - 9, musicY, trackWidth, compact ? 18 : 20,
                Component.literal(musicButtonLabel()), b -> changeTrack(), 0xFFD64B4B).setCompactCenter(true));

    }

    private SiegeButton command(int x, int y, int width, int height, String key, Button.OnPress press) {
        return new SiegeButton(x, y, width, height, Component.translatable(key), button -> {
            SiegeUiSounds.click();
            press.onPress(button);
        }, 0xFFE54852).setMainMenuStyle(true);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(graphics, width, height, System.currentTimeMillis());

        boolean compact = SiegeUiLayout.compactTitle(width, height);
        int panelRight = Math.min(width, menuX + menuWidth + (compact ? 12 : 18));
        // Neutral photographic shade from the original menu: no blue plate or hard divider.
        SiegeBackgrounds.renderPanel(graphics, 0, 0, panelRight, height);
        graphics.fill(0, 0, width, 1, 0x681B1B1B);

        renderTitle(graphics, compact, panelRight);

        previewX = -1;
        primaryPreviewEntry = null;
        secondaryPreviewEntry = null;
        if (SiegeConfig.mainMenuIntel) renderIntelPreview(graphics, panelRight, mouseX, mouseY);
        else {
            previewReading = false;
        }
        renderTrackAnnouncement(graphics, compact);
        String musicLabel = musicButtonLabel();
        if (musicButton != null && !musicButton.getMessage().getString().equals(musicLabel))
            musicButton.setMessage(Component.literal(musicLabel));

        if (width >= 610 && SiegeConfig.showBuildLabel) renderBuildLabel(graphics);

        super.render(graphics, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    private void renderTitle(GuiGraphics g, boolean compact, int panelRight) {
        String main = "ETERNAL CRAFT";
        String sub = "S I E G E";
        int headerWidth = SiegeUiLayout.centeredTitleWidth(width, compact, menuWidth);
        int center = width / 2;
        float scale = Math.min(compact ? 2.05F : 2.75F, (headerWidth - 12F) / font.width(main));
        int mainWidth = Math.round(font.width(main) * scale);
        int x = center - mainWidth / 2;
        int y = compact ? 17 : 21;

        g.pose().pushPose();
        g.pose().translate(x, y, 0.0F);
        g.pose().scale(scale, scale, 1.0F);
        g.drawString(font, main, 2, 2, 0xB8000000, false);
        g.drawString(font, main, 1, 0, 0xFFD53842, false);
        g.drawString(font, main, 0, 0, 0xFFF0EDEA, false);
        g.pose().popPose();

        if (SiegeConfig.titleInterference && SiegeConfig.menuEffects && !SiegeConfig.reducedMotion
                && (System.currentTimeMillis() / 110L) % 43 < 3) {
            long phase = System.currentTimeMillis() / 110L;
            int sliceWidth = Math.max(16, mainWidth / 7);
            int sliceX = x + (int) ((phase * 37L) % Math.max(1, mainWidth - sliceWidth));
            int sliceY = y + 4 + (int) ((phase * 5L) % Math.max(5, Math.round(7 * scale)));
            g.enableScissor(sliceX, sliceY, sliceX + sliceWidth, sliceY + 2);
            g.pose().pushPose();
            g.pose().translate(x + ((phase & 1L) == 0L ? 2 : -2), y, 0.0F);
            g.pose().scale(scale, scale, 1.0F);
            g.drawString(font, main, 0, 0, 0xFFE54852, false);
            g.pose().popPose();
            g.disableScissor();
        }

        float subScale = compact ? 1.25F : 1.55F;
        int subWidth = Math.round(font.width(sub) * subScale);
        int subX = center - subWidth / 2;
        int subY = y + Math.round(font.lineHeight * scale) + (compact ? 7 : 9);
        g.pose().pushPose();
        g.pose().translate(subX, subY, 0.0F);
        g.pose().scale(subScale, subScale, 1.0F);
        g.drawString(font, sub, 1, 1, 0xB8000000, false);
        g.drawString(font, sub, 0, 0, 0xFFFF4C55, false);
        g.pose().popPose();

        int dividerY = subY + Math.round(font.lineHeight * subScale) + 5;
        int dividerHalf = Math.min(110, Math.max(34, mainWidth / 2));
        g.fill(center - dividerHalf, dividerY, center + dividerHalf, dividerY + 1, 0x8055BFD9);
        g.fill(center - 12, dividerY, center + 12, dividerY + 2, 0xFFE54852);
    }

    private void renderTrackAnnouncement(GuiGraphics g, boolean compact) {
        if (!SiegeConfig.trackAnnouncements) return;
        if (compact) return;
        long age = SiegeMusic.trackAnnouncementAgeMs();
        if (age < 0L) return;

        long noticeDuration = SiegeMusic.trackAnnouncementDurationMs();
        long fadeStart = Math.max(0L, noticeDuration - 1_700L);
        int alpha = age <= fadeStart ? 255 : Math.max(0, 255 - (int) ((age - fadeStart) * 255L / Math.max(1L, noticeDuration - fadeStart)));
        int boxWidth = SiegeUiLayout.trackNoticeWidth(width, menuX, menuWidth, compact);
        if (boxWidth == 0) return;
        int boxHeight = compact ? 29 : 34;
        int x = width - boxWidth - 9;
        if (SiegeConfig.menuEffects && !SiegeConfig.reducedMotion && age < 360L) {
            x += Math.round((1.0F - age / 360.0F) * 18.0F);
        }
        int y = compact ? 55 : 57;
        g.fill(x + 2, y + 2, x + boxWidth + 2, y + boxHeight + 2, (Math.min(150, alpha) << 24));
        g.fill(x, y, x + boxWidth, y + boxHeight, (Math.min(222, alpha) << 24) | 0x00070A0D);
        g.fill(x, y, x + 2, y + boxHeight, (alpha << 24) | 0x00E54852);

        String rec = "● REC";
        g.drawString(font, rec, x + 8, y + 6, (alpha << 24) | 0x00FF5555, false);
        String track = font.plainSubstrByWidth(SiegeMusic.currentTrackName(), boxWidth - 16);
        g.drawString(font, track, x + 8, y + (compact ? 17 : 20), (alpha << 24) | 0x00F4D36A, false);

        int progress = Math.min(boxWidth - 4, Math.max(0,
                (int) ((boxWidth - 4L) * age / noticeDuration)));
        g.fill(x + 2, y + boxHeight - 2, x + 2 + progress, y + boxHeight - 1,
                (Math.min(190, alpha) << 24) | 0x00E54852);
    }

    private void renderBuildLabel(GuiGraphics g) {
        g.pose().pushPose();
        g.pose().translate(10.0F, height - 9.0F, 0.0F);
        g.pose().scale(0.68F, 0.68F, 1.0F);
        g.drawString(font, "BUILD 0.13.0", 0, 0, 0xFF747D84, false);
        g.pose().popPose();
    }

    /**
     * Small operational Intel cards. GUI scale 1-2 gets one Unit and one Advanced
     * card at the same time; scale 3 gets a single alternating card; GUI scale 4
     * deliberately hides the feed so the command menu keeps enough room.
     */
    private void renderIntelPreview(GuiGraphics g, int leftPanelRight, int mouseX, int mouseY) {
        if (minecraft == null) return;
        double guiScale = minecraft.getWindow().getGuiScale();
        if (guiScale >= 3.75D || width < 460 || height < 248) return;

        List<IntelEntry> previewable = IntelCatalog.previewable();
        if (previewable.isEmpty()) return;

        boolean dual = guiScale < 2.75D && width >= 650 && height >= 390 && previewable.size() > 1;
        int cardWidth = dual ? Math.min(310, Math.max(240, width / 4)) : Math.min(300, Math.max(220, width / 3));
        int cardHeight = dual ? 112 : 122;
        int x = width - cardWidth - 14;
        if (x <= leftPanelRight + 16) {
            x = leftPanelRight + 16;
            cardWidth = width - x - 14;
        }
        if (cardWidth < 196) return;

        if (dual) {
            int totalHeight = cardHeight * 2 + 8;
            int y = Math.max(100, Math.min(height - totalHeight - 28, (height - totalHeight) / 2));
            setPreviewBounds(x, y, cardWidth, totalHeight, cardHeight, 8, true);
            int currentPreview = currentIntelPreview(previewable.size(), isInsidePreview(mouseX, mouseY));
            int slide = previewSlide(currentPreview);
            int first = currentPreview;
            IntelEntry firstEntry = previewable.get(first);
            IntelEntry secondEntry = previewable.get((first + 1) % previewable.size());
            primaryPreviewEntry = firstEntry;
            secondaryPreviewEntry = secondEntry;
            renderIntelCard(g, firstEntry, first, previewable.size(), x + slide, y, cardWidth, cardHeight, mouseX, mouseY);
            renderIntelCard(g, secondEntry, (first + 1) % previewable.size(), previewable.size(),
                    x + slide, y + cardHeight + 8, cardWidth, cardHeight, mouseX, mouseY);
        } else {
            int y = Math.max(100, height - cardHeight - 31);
            setPreviewBounds(x, y, cardWidth, cardHeight, cardHeight, 0, false);
            int currentPreview = currentIntelPreview(previewable.size(), isInsidePreview(mouseX, mouseY));
            IntelEntry entry = previewable.get(currentPreview);
            primaryPreviewEntry = entry;
            renderIntelCard(g, entry, currentPreview, previewable.size(), x + previewSlide(currentPreview), y,
                    cardWidth, cardHeight, mouseX, mouseY);
        }
    }

    private void renderIntelCard(GuiGraphics g, IntelEntry entry, int entryIndex, int entryCount,
                                 int x, int y, int w, int h,
                                 int mouseX, int mouseY) {
        int accent = switch (entry.category()) {
            case "ADVANCED" -> 0xFF2F80FF;
            case "TANK" -> 0xFFD98A2B;
            case "BOSS" -> 0xFFB5162D;
            default -> 0xFFD94A4A;
        };
        String type = switch (entry.category()) {
            case "ADVANCED" -> label("AVANZADO", "ADVANCED");
            case "TANK" -> label("TANQUE", "TANK");
            case "BOSS" -> label("JEFE", "BOSS");
            default -> label("UNIDAD", "UNIT");
        };

        g.fill(x + 3, y + 3, x + w + 3, y + h + 3, 0x5A000000);
        g.fill(x, y, x + w, y + h, 0xEA080D11);
        g.fill(x, y, x + w, y + 2, accent);
        g.fill(x, y, x + 2, y + h, accent);
        g.fill(x + 8, y + 23, x + w - 8, y + 24, 0xFF27343C);

        boolean cardHot = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        String header = type + " " + String.format("%02d/%02d", entryIndex + 1, entryCount) + " // " + entry.code();
        boolean showOpen = cardHot && mouseY < y + h - 18;
        String rightLabel = showOpen ? label("ABRIR EXPEDIENTE ↗", "OPEN DOSSIER ↗") : SiegeConfig.showIntelState ? intelStateLabel() : "";
        int reserved = rightLabel.isEmpty() ? 0 : Math.min(font.width(rightLabel), showOpen ? w / 2 : w / 3) + 8;
        int headerWidth = Math.max(1, w - 18 - reserved);
        g.drawString(font, font.plainSubstrByWidth(header, headerWidth), x + 9, y + 7, 0xFF9EA9B0, false);
        if (cardHot && mouseY < y + h - 18) {
            String open = label("ABRIR EXPEDIENTE ↗", "OPEN DOSSIER ↗");
            open = font.plainSubstrByWidth(open, Math.max(40, w / 2));
            g.drawString(font, open, x + w - 9 - font.width(open), y + 7, 0xFF7FC7D9, false);
        } else if (SiegeConfig.showIntelState) {
            String state = intelStateLabel();
            state = font.plainSubstrByWidth(state, Math.max(38, w / 3));
            g.drawString(font, state, x + w - 9 - font.width(state), y + 7,
                    previewReading ? 0xFFF4D36A : 0xFF71828C, false);
        }
        g.drawString(font, font.plainSubstrByWidth(entry.name(), w - 18), x + 9, y + 29, 0xFFF1EEE8, false);

        String threat = entry.threat() > 0 ? entry.threat() + "/5" : label("SIN DATOS", "NO DATA");
        String meta = label("AMENAZA ", "THREAT ") + threat + "  //  HP " + entry.hp();
        if (!"N/D".equals(entry.defense())) meta += "  DEF " + entry.defense();
        g.drawString(font, font.plainSubstrByWidth(meta, w - 18), x + 9, y + 41, accent, false);

        int textY = y + 54;
        int footerY = y + h - 12;
        int maxLines = Math.max(1, (footerY - textY - 2) / 10);
        List<FormattedCharSequence> summaryLines = font.split(
                Component.literal(fitCompleteSummary(previewSummary(entry), w - 18, maxLines)), w - 18);
        for (int line = 0; line < summaryLines.size(); line++) {
            g.drawString(font, summaryLines.get(line), x + 9, textY + line * 10, 0xFFBCC4C9, false);
        }

        String footer = label("← ANTERIOR  ·  SIGUIENTE →", "← PREVIOUS  ·  NEXT →");
        boolean footerHot = mouseX >= x && mouseX < x + w && mouseY >= footerY - 3 && mouseY < y + h;
        if (footerHot) {
            int half = x + w / 2;
            if (mouseX < half) g.fill(x + 4, footerY - 4, half, y + h - 3, 0x283AAFCB);
            else g.fill(half, footerY - 4, x + w - 4, y + h - 3, 0x283AAFCB);
        }
        if (SiegeConfig.showIntelProgress && SiegeConfig.autoRotateIntel) {
            int progressWidth = Math.round((w - 8) * intelCycleProgress());
            if (progressWidth > 0) g.fill(x + 4, y + h - 3, x + 4 + progressWidth, y + h - 2, 0xAA55BFD9);
        }
        g.drawString(font, font.plainSubstrByWidth(footer, w - 18), x + 9, footerY, 0xFF7FC7D9, false);
    }

    private String previewSummary(IntelEntry entry) {
        if (spanish()) return switch (entry.name()) {
            case "INFANTRY" -> "Unidad básica que comparte información y coordina ataques en grupo. Su fuerza aumenta cuando logra formar una escuadra.";
            case "SHIELDER" -> "Emboscador blindado con escopeta y dos escudos frontales. Es vulnerable cuando se lo obliga a girar.";
            case "SABOTEUR" -> "Infiltrado invisible equipado con C4 y sabotaje electrónico. Puede inutilizar defensas y marcar estructuras.";
            case "STALKER" -> "Espía camuflado que observa bases y coloca rastreadores GPS. Suele operar dentro de escuadras de infiltración.";
            case "NATZUKA" -> "Cuadrúpedo armado capaz de cruzar terrenos difíciles. Puede terminar el ataque con una carga explosiva.";
            case "SNIPER" -> "Francotirador nusiano que amenaza desde gran distancia. Su láser revela brevemente la línea de tiro.";
            case "GRENADIER" -> "Unidad de retaguardia equipada con gas y granadas. Obliga a abandonar coberturas y espacios cerrados.";
            case "GUNNER" -> "Tanque común de fuego sostenido con gran reserva de munición. Conviene atacarlo desde cobertura sólida.";
            case "JETPACKER" -> "Unidad aérea explosiva capaz de alcanzar velocidad sónica. Su trayectoria debe cortarse antes del impacto.";
            case "PATRIOT" -> "Solo se recuperó un boceto de Patriot y su vínculo con Nusia. Sus capacidades continúan clasificadas.";
            case "SPECIALIST" -> "Estratega invisible que dirige escuadras y prepara trampas complejas. Puede teletransportarse y cambiar de plan.";
            case "DEMOMAN" -> "Kamikaze avanzado con rifle y una carga corporal extrema. Su demora de detonación permite una breve retirada.";
            case "ARTILLER" -> "Especialista oculto que solicita bombardeos mediante radio. Debe interrumpirse antes de completar la transmisión.";
            case "CLOAKER" -> "Cazador de velocidad extrema cuyo impacto ignora armaduras. El chillido anuncia el inicio de la carga.";
            case "APU" -> "Mech pesado con lanzallamas que ignora la invulnerabilidad temporal. Su defensa disminuye dentro del agua.";
            case "MISSILER" -> "Francotirador invisible que dispara misiles guiados. Un destello amarillo concede cinco segundos para escapar.";
            case "ZAPPER" -> "Tanque eléctrico con bastón y bobinas Tesla recargables. Sus impactos pueden encadenar aturdimientos prolongados.";
            case "COMBATANT" -> "Tanque de asalto pesado con M48 Tomahawk. Su carga causa daño devastador y puede ignorar defensas.";
            case "AGREEMENT" -> "Solo se confirmó su vínculo con Secure Contain Protect. Armamento y capacidades permanecen sin datos.";
            case "JAGANT" -> "La captura y sus valores de resistencia son los únicos datos recuperados. Su método de ataque sigue desconocido.";
            case "STRIDER" -> "Se recuperó el render de una estructura mecánica de patas largas. Su origen y comportamiento siguen desconocidos.";
            case "TEMPEST" -> "Jefe eléctrico que castiga a grupos y objetivos cercanos. También puede sabotear habilidades durante el combate.";
            case "FUSILIER" -> "Jefe lento que bombardea a distancia con seis granadas. Usa una pala para defenderse a corta distancia.";
            case "ACHILLES" -> "Francotirador lento cuyo Armour Peeler atraviesa toda armadura. Las líneas de visión son su principal ventaja.";
            case "TRIDENT" -> "Jefe blindado que atrae víctimas con un gancho y las ejecuta con machete. También carga contra objetivos lejanos.";
            case "PROMETHEUS" -> "Jefe incendiario armado con el FAHRENNEIT-3000. Sus tanques de combustible constituyen su punto vulnerable.";
            case "DAEDALUS" -> "Jefe minero que excava a velocidad supersónica para emboscar. Puede derrotar sin armadura con un solo golpe.";
            case "HERMES" -> "El video y una resistencia de 45.000 HP son los únicos datos confirmados. Sus capacidades permanecen desconocidas.";
            case "LELANTOS" -> "Solo existen un video y una resistencia estimada de 8.000 HP. El resto del expediente sigue sin confirmar.";
            case "GAIA" -> "El archivo contiene metraje parcial y una resistencia estimada de 20.000 HP. No hay capacidades verificadas.";
            default -> "Expediente operativo disponible.";
        };
        return switch (entry.name()) {
            case "INFANTRY" -> "A basic unit that shares information and coordinates group attacks. Its strength rises after forming a squad.";
            case "SHIELDER" -> "An armoured ambusher with a shotgun and two frontal shields. It is vulnerable when forced to turn.";
            case "SABOTEUR" -> "An invisible infiltrator carrying C4 and electronic sabotage gear. It can disable defences and mark structures.";
            case "STALKER" -> "A camouflaged spy that watches bases and plants GPS trackers. It usually operates in infiltration squads.";
            case "NATZUKA" -> "An armed quadruped designed to cross difficult terrain. It can finish an attack with an explosive charge.";
            case "SNIPER" -> "A Nusian marksman that threatens targets from long range. Its laser briefly reveals the firing line.";
            case "GRENADIER" -> "A rear-line unit equipped with gas and grenades. It forces targets out of cover and enclosed spaces.";
            case "GUNNER" -> "A common tank built for sustained fire with a large ammunition reserve. Solid cover is essential.";
            case "JETPACKER" -> "An airborne explosive unit capable of sonic speed. Its trajectory must be broken before impact.";
            case "PATRIOT" -> "Only a Patriot sketch and a connection to Nusia were recovered. Its capabilities remain classified.";
            case "SPECIALIST" -> "An invisible strategist that commands squads and prepares complex traps. It can teleport and change plans.";
            case "DEMOMAN" -> "An advanced kamikaze carrying a rifle and an extreme body charge. Its delay leaves a brief escape window.";
            case "ARTILLER" -> "A hidden specialist that calls bombardments by radio. The transmission must be interrupted before completion.";
            case "CLOAKER" -> "An extreme-speed hunter whose impact ignores armour. Its screech announces the beginning of a charge.";
            case "APU" -> "A heavy flamethrower mech that ignores temporary invulnerability. Its defence drops while submerged.";
            case "MISSILER" -> "An invisible marksman that launches guided missiles. A yellow flash grants five seconds to escape.";
            case "ZAPPER" -> "An electric tank with a staff and rechargeable Tesla coils. Its hits can chain prolonged stuns.";
            case "COMBATANT" -> "A heavy assault tank carrying an M48 Tomahawk. Its charge deals devastating damage and may ignore defence.";
            case "AGREEMENT" -> "Only its link to Secure Contain Protect is confirmed. Weapons and capabilities remain unknown.";
            case "JAGANT" -> "The image and durability values are the only recovered data. Its attack method remains unknown.";
            case "STRIDER" -> "A render of a long-legged mechanical structure was recovered. Its origin and behaviour remain unknown.";
            case "TEMPEST" -> "An electric boss that punishes groups and nearby targets. It can also sabotage abilities during combat.";
            case "FUSILIER" -> "A slow boss that bombards targets with six grenades. It uses a shovel for close-range defence.";
            case "ACHILLES" -> "A slow sniper whose Armour Peeler bypasses all armour. Dangerous sight lines are its main advantage.";
            case "TRIDENT" -> "An armoured boss that hooks victims and executes them with a machete. It also charges distant targets.";
            case "PROMETHEUS" -> "An incendiary boss armed with the FAHRENNEIT-3000. Its fuel tanks are the critical weak point.";
            case "DAEDALUS" -> "A mining boss that tunnels at supersonic speed to ambush targets. One hit can defeat an unarmoured victim.";
            case "HERMES" -> "The video and an estimated 45,000 HP are the only confirmed data. Its capabilities remain unknown.";
            case "LELANTOS" -> "Only a video and an estimated 8,000 HP are available. The rest of the dossier remains unconfirmed.";
            case "GAIA" -> "The file contains partial footage and an estimated 20,000 HP. No capabilities are verified.";
            default -> "Operational dossier available.";
        };
    }

    private String fitCompleteSummary(String summary, int width, int maxLines) {
        String best = "";
        for (String sentence : summary.split("(?<=[.!?])\\s+")) {
            String candidate = best.isEmpty() ? sentence : best + " " + sentence;
            if (font.split(Component.literal(candidate), width).size() > maxLines) break;
            best = candidate;
        }
        return best.isEmpty() ? label("Expediente disponible.", "Dossier available.") : best;
    }

    private void changeTrack() {
        SiegeUiSounds.nextTrack();
        SiegeMusic.nextTrack();
        if (musicButton != null) musicButton.setMessage(Component.literal(musicButtonLabel()));
    }

    private String musicButtonLabel() {
        int number = SiegeMusic.currentTrackNumber();
        return label("> MÚSICA", "> MUSIC") + (number > 0 ? "  " + number + "/" + SiegeMusic.trackNames().size() : "");
    }

    private int currentIntelPreview(int size, boolean hovered) {
        if (size <= 0) return 0;
        long now = System.currentTimeMillis();
        int result = previewClock.update(size, now, hovered, SiegeConfig.pauseIntelOnHover, SiegeConfig.autoRotateIntel);
        previewReading = previewClock.reading;
        previewCycleStartedAt = previewClock.started;
        manualIntelPreviewUntil = previewClock.deadline;
        return result;
    }

    private void stepIntelPreview(int direction) {
        List<IntelEntry> previewable = IntelCatalog.previewable();
        if (previewable.isEmpty()) return;
        previewTransitionDirection = direction < 0 ? -1 : 1;
        currentIntelPreview(previewable.size(), false);
        previewClock.step(previewable.size(), System.currentTimeMillis(), direction);
        previewCycleStartedAt = previewClock.started;
        manualIntelPreviewUntil = previewClock.deadline;
        SiegeUiSounds.click();
    }

    private void setPreviewBounds(int x, int y, int w, int h, int cardHeight, int gap, boolean dual) {
        previewX = x;
        previewY = y;
        previewW = w;
        previewH = h;
        previewCardHeight = cardHeight;
        previewGap = gap;
        previewDual = dual;
    }

    private boolean isInsidePreview(double mouseX, double mouseY) {
        return previewX >= 0 && mouseX >= previewX && mouseX < previewX + previewW
                && mouseY >= previewY && mouseY < previewY + previewH;
    }

    private boolean isInsidePreviewFooter(double mouseY) {
        if (previewX < 0) return false;
        double localY = mouseY - previewY;
        if (localY < 0 || localY >= previewH) return false;
        if (!previewDual) return localY >= previewCardHeight - 18;
        if (localY < previewCardHeight) return localY >= previewCardHeight - 18;
        double secondY = localY - previewCardHeight - previewGap;
        return secondY >= previewCardHeight - 18 && secondY < previewCardHeight;
    }

    private int previewSlide(int index) {
        if (lastRenderedPreview != index) {
            if (lastRenderedPreview >= 0) previewTransitionStarted = System.currentTimeMillis();
            lastRenderedPreview = index;
        }
        if (SiegeConfig.reducedMotion || !SiegeConfig.menuEffects || previewTransitionStarted <= 0L) return 0;
        float progress = Math.min(1.0F, (System.currentTimeMillis() - previewTransitionStarted) / 220.0F);
        float eased = 1.0F - (1.0F - progress) * (1.0F - progress);
        return Math.round(previewTransitionDirection * (1.0F - eased) * 12.0F);
    }

    private IntelEntry previewEntryAt(double mouseY) {
        if (!isInsidePreview(previewX + 1, mouseY)) return null;
        if (!previewDual) return primaryPreviewEntry;
        double localY = mouseY - previewY;
        if (localY < previewCardHeight) return primaryPreviewEntry;
        if (localY >= previewCardHeight + previewGap) return secondaryPreviewEntry;
        return null;
    }

    private void openPreviewEntry(IntelEntry entry) {
        if (entry == null) return;
        SiegeUiSounds.click();
        minecraft.setScreen(new IntelScreenV3(this, entry));
    }

    private String intelStateLabel() {
        if (!SiegeConfig.autoRotateIntel) return label("FIJO", "FIXED");
        if (previewReading) return label("LEYENDO", "READING");
        long seconds = Math.max(0L, (manualIntelPreviewUntil - System.currentTimeMillis() + 999L) / 1_000L);
        return "AUTO " + seconds + "s";
    }

    private float intelCycleProgress() {
        if (previewReading) return 0.0F;
        long now = System.currentTimeMillis();
        long duration = Math.max(1L, manualIntelPreviewUntil - previewCycleStartedAt);
        return Math.max(0.0F, Math.min(1.0F, (now - previewCycleStartedAt) / (float) duration));
    }

    private void requestQuit() {
        if (!SiegeConfig.confirmQuit) {
            minecraft.stop();
            return;
        }
        minecraft.setScreen(new ConfirmScreen(confirmed -> {
            if (confirmed) minecraft.stop();
            else minecraft.setScreen(this);
        }, Component.literal(label("¿SALIR DE SIEGE?", "LEAVE SIEGE?")),
                Component.literal(label("La sesión de Minecraft se cerrará.", "Minecraft will close."))));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isInsidePreview(mouseX, mouseY)
                && isInsidePreviewFooter(mouseY)) {
            stepIntelPreview(mouseX < previewX + previewW / 2.0D ? -1 : 1);
            return true;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isInsidePreview(mouseX, mouseY)) {
            IntelEntry entry = previewEntryAt(mouseY);
            if (entry != null) {
                openPreviewEntry(entry);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta != 0.0D && previewX >= 0 && mouseX >= previewX && mouseX < previewX + previewW
                && mouseY >= previewY && mouseY < previewY + previewH) {
            stepIntelPreview(delta > 0.0D ? -1 : 1);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private String label(String es, String en) {
        return spanish() ? es : en;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (previewX >= 0 && keyCode == GLFW.GLFW_KEY_LEFT) {
            stepIntelPreview(-1);
            return true;
        }
        if (previewX >= 0 && keyCode == GLFW.GLFW_KEY_RIGHT) {
            stepIntelPreview(1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_S && Screen.hasControlDown()) {
            minecraft.setScreen(new SelectWorldScreen(this));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

package uy.santipdr.siege.client;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uy.santipdr.siege.SiegeMod;

/** SIEGE 4.0 home integration and session navigation trail. */
@Mod.EventBusSubscriber(modid = SiegeMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class Siege250MenuEvents {
    private Siege250MenuEvents() { }

    @SubscribeEvent
    public static void initialized(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof SiegeTitleScreen screen)) return;

        for (var listener : List.copyOf(event.getListenersList())) {
            if (!(listener instanceof SiegeButton original)) continue;
            if (!"siege.menu.settings".equals(translationKey(original.getMessage()))) continue;

            int x = original.getX();
            int y = original.getY();
            int full = original.getWidth();
            int h = original.getHeight();
            int gap = full >= 150 ? 4 : 2;
            int opsW = Math.max(1, Math.round((full - gap) * 0.60F));
            int settingsW = Math.max(1, full - gap - opsW);
            event.removeListener(original);

            String operationsLabel = buttonLabel(label("OPERACIONES", "OPERATIONS"), "OPS", opsW);
            String settingsLabel = buttonLabel(label("AJUSTES", "SETTINGS"), label("AJ.", "CFG"), settingsW);

            SiegeButton operations = new SiegeButton(x, y, opsW, h,
                    Component.literal(operationsLabel), b -> {
                SiegeUiSounds.confirm();
                Minecraft.getInstance().setScreen(new SiegeRecruitBriefingScreen(screen));
            }, SiegeTheme.CYAN).setMainMenuStyle(true).withIcon("overview").setCompactCenter(true);
            operations.setTooltip(Tooltip.create(Component.literal(label(
                    "Briefing, razas, progresión, amenazas, arsenal, despliegue y multimedia.",
                    "Briefing, races, progression, threats, armory, deployment and media."))));

            SiegeButton settings = new SiegeButton(x + opsW + gap, y, settingsW, h,
                    Component.literal(settingsLabel), b -> {
                SiegeUiSounds.confirm();
                Minecraft.getInstance().setScreen(new SiegeSettingsScreen(screen));
            }, SiegeTheme.RED).setMainMenuStyle(true).withIcon("settings").setCompactCenter(true);
            settings.setTooltip(Tooltip.create(Component.literal(label(
                    "Apariencia, movimiento, audio, Intel y accesibilidad.",
                    "Appearance, motion, audio, Intel and accessibility."))));

            event.addListener(operations);
            event.addListener(settings);
            return;
        }
    }

    @SubscribeEvent
    public static void opening(ScreenEvent.Opening event) {
        Screen next = event.getNewScreen();
        if (next == null) return;
        SiegeOperationsIndex.Route route = routeFor(next.getClass().getSimpleName());
        if (route != null) SiegeRouteHistory.record(route);
    }

    private static String buttonLabel(String full, String shortLabel, int width) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return shortLabel;
        // Reserve icon + internal button padding. The previous width-only check
        // could let OPERACIONES/AJUSTES collide at GUI scale 3-4.
        int usable = Math.max(1, width - 30);
        return minecraft.font.width(full) <= usable ? full : shortLabel;
    }

    private static SiegeOperationsIndex.Route routeFor(String simple) {
        return switch (simple) {
            case "SiegeRecruitBriefingScreen", "SiegeOperationsHubScreen" -> SiegeOperationsIndex.Route.KNOWLEDGE;
            case "SiegeMultiplayerScreen", "JoinMultiplayerScreen", "DirectJoinServerScreen", "ConnectScreen" ->
                    SiegeOperationsIndex.Route.DEPLOYMENT;
            case "IntelScreenV3", "IntelPortraitScreen", "SiegeThreatBoardScreen" -> SiegeOperationsIndex.Route.INTEL;
            case "SiegeKnowledgeScreen", "SiegeRaceAtlasScreen", "SiegeProgressionMapScreen", "SiegeProgressionScreen" ->
                    SiegeOperationsIndex.Route.KNOWLEDGE;
            case "SiegeArchiveScreen" -> SiegeOperationsIndex.Route.FIELD_MANUAL;
            case "SiegeGuideScreen", "SiegeEvidenceReelScreen" -> SiegeOperationsIndex.Route.ARMORY;
            case "SiegeSystemScreen" -> SiegeOperationsIndex.Route.COMMAND;
            case "SiegeDiagnosticsScreen" -> SiegeOperationsIndex.Route.DIAGNOSTICS;
            case "SiegeSettingsScreen" -> SiegeOperationsIndex.Route.SETTINGS;
            case "SiegeSceneScreen", "SiegeMediaRoomScreen" -> SiegeOperationsIndex.Route.BACKGROUNDS;
            default -> null;
        };
    }

    private static String translationKey(Component component) {
        return component != null && component.getContents() instanceof TranslatableContents tr ? tr.getKey() : "";
    }

    private static String label(String es, String en) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_") ? es : en;
    }
}

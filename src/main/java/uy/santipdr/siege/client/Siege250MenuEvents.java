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

/** SIEGE home integration and session navigation trail. */
@Mod.EventBusSubscriber(modid = SiegeMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class Siege250MenuEvents {
    private Siege250MenuEvents() { }

    @SubscribeEvent
    public static void initialized(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof SiegeTitleScreen screen)) return;
        boolean deploymentDone = false;
        boolean settingsDone = false;
        for (var listener : List.copyOf(event.getListenersList())) {
            if (!(listener instanceof SiegeButton original)) continue;
            String key = translationKey(original.getMessage());

            if (!deploymentDone && "siege.menu.deployment".equals(key)) {
                splitDeploymentRow(event, screen, original);
                deploymentDone = true;
                continue;
            }
            if (!settingsDone && "siege.menu.settings".equals(key)) {
                splitSettingsRow(event, screen, original);
                settingsDone = true;
            }
        }
    }

    private static void splitDeploymentRow(ScreenEvent.Init.Post event, SiegeTitleScreen screen, SiegeButton original) {
        int x = original.getX(), y = original.getY(), full = original.getWidth(), h = original.getHeight();
        int gap = full >= 150 ? 4 : 2;
        int briefingW = Math.max(1, Math.round((full - gap) * 0.42F));
        int deploymentW = Math.max(1, full - gap - briefingW);
        event.removeListener(original);

        SiegeButton briefing = new SiegeButton(x, y, briefingW, h,
                Component.literal(buttonLabel(label("GUÍA", "GUIDE"), "GUI", briefingW)), b -> {
            SiegeUiSounds.confirm();
            Minecraft.getInstance().setScreen(new SiegeServerGuideScreen(screen));
        }, SiegeTheme.ORANGE).setMainMenuStyle(true).withIcon("shield").setCompactCenter(true);
        briefing.setTooltip(Tooltip.create(Component.literal(label(
                "Razas, progresión, Trials, amenazas, revive, reliquias y sistemas explicados por categorías.",
                "Races, progression, Trials, threats, revival, relics and systems explained by category."))));

        SiegeButton deployment = new SiegeButton(x + briefingW + gap, y, deploymentW, h,
                Component.literal(buttonLabel(label("DESPLIEGUE", "DEPLOYMENT"), "DEP", deploymentW)), b -> {
            SiegeUiSounds.confirm();
            Minecraft.getInstance().setScreen(new SiegeMultiplayerScreen(screen));
        }, SiegeTheme.RED).setMainMenuStyle(true).withIcon("connect").setCompactCenter(true);
        deployment.setTooltip(Tooltip.create(Component.literal(label(
                "Servidor oficial, estado y conexión.",
                "Official server, status and connection."))));

        event.addListener(briefing);
        event.addListener(deployment);
    }

    private static void splitSettingsRow(ScreenEvent.Init.Post event, SiegeTitleScreen screen, SiegeButton original) {
        int x = original.getX(), y = original.getY(), full = original.getWidth(), h = original.getHeight();
        int gap = full >= 150 ? 4 : 2;
        int opsW = Math.max(1, Math.round((full - gap) * 0.60F));
        int settingsW = Math.max(1, full - gap - opsW);
        event.removeListener(original);

        SiegeButton operations = new SiegeButton(x, y, opsW, h,
                Component.literal(buttonLabel(label("OPERACIONES", "OPERATIONS"), "OPS", opsW)), b -> {
            SiegeUiSounds.confirm();
            Minecraft.getInstance().setScreen(new SiegeOperationsHubScreen(screen));
        }, SiegeTheme.CYAN).setMainMenuStyle(true).withIcon("overview").setCompactCenter(true);
        operations.setTooltip(Tooltip.create(Component.literal(label(
                "Razas, progresión, amenazas, multimedia, Intel, búsqueda y herramientas.",
                "Races, progression, threats, media, Intel, search and tools."))));

        SiegeButton settings = new SiegeButton(x + opsW + gap, y, settingsW, h,
                Component.literal(buttonLabel(label("AJUSTES", "SETTINGS"), label("AJ.", "CFG"), settingsW)), b -> {
            SiegeUiSounds.confirm();
            Minecraft.getInstance().setScreen(new SiegeSettingsScreen(screen));
        }, SiegeTheme.RED).setMainMenuStyle(true).withIcon("settings").setCompactCenter(true);
        settings.setTooltip(Tooltip.create(Component.literal(label(
                "Apariencia, movimiento, audio, Intel y accesibilidad.",
                "Appearance, motion, audio, Intel and accessibility."))));
        event.addListener(operations);
        event.addListener(settings);
    }

    /** Measures the rendered label instead of guessing from the full row width. */
    private static String buttonLabel(String full, String shortLabel, int width) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return shortLabel;
        int usable = Math.max(1, width - 30);
        return minecraft.font.width(full) <= usable ? full : shortLabel;
    }

    @SubscribeEvent
    public static void opening(ScreenEvent.Opening event) {
        Screen next = event.getNewScreen();
        if (next == null) return;
        SiegeOperationsIndex.Route route = routeFor(next.getClass().getSimpleName());
        if (route != null) SiegeRouteHistory.record(route);
    }

    private static SiegeOperationsIndex.Route routeFor(String simple) {
        return switch (simple) {
            case "SiegeBriefingScreen" -> SiegeOperationsIndex.Route.BRIEFING;
            case "SiegeServerGuideScreen", "SiegeKnowledgeScreen" -> SiegeOperationsIndex.Route.KNOWLEDGE;
            case "SiegeAtlasScreen", "SiegeKnowledgeFileScreen" -> SiegeOperationsIndex.Route.ATLAS;
            case "SiegeRaceAtlasScreen" -> SiegeOperationsIndex.Route.RACES;
            case "SiegeProgressionMapScreen" -> SiegeOperationsIndex.Route.PROGRESSION;
            case "SiegeThreatBoardScreen" -> SiegeOperationsIndex.Route.THREATS;
            case "SiegeMediaRoomScreen" -> SiegeOperationsIndex.Route.MEDIA;
            case "SiegeMultiplayerScreen", "JoinMultiplayerScreen", "DirectJoinServerScreen", "ConnectScreen" -> SiegeOperationsIndex.Route.DEPLOYMENT;
            case "IntelScreenV3", "IntelPortraitScreen" -> SiegeOperationsIndex.Route.INTEL;
            case "SiegeArchiveScreen" -> SiegeOperationsIndex.Route.FIELD_MANUAL;
            case "SiegeSystemScreen" -> SiegeOperationsIndex.Route.COMMAND;
            case "SiegeDiagnosticsScreen" -> SiegeOperationsIndex.Route.DIAGNOSTICS;
            case "SiegeSettingsScreen" -> SiegeOperationsIndex.Route.SETTINGS;
            case "SiegeSceneScreen" -> SiegeOperationsIndex.Route.BACKGROUNDS;
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

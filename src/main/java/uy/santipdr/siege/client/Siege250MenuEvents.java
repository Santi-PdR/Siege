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
                Component.literal(full < 165 ? "BRF" : "BRIEFING"), b -> {
            SiegeUiSounds.confirm();
            Minecraft.getInstance().setScreen(new SiegeBriefingScreen(screen));
        }, SiegeTheme.ORANGE).setMainMenuStyle(true).withIcon("shield").setCompactCenter(true);
        briefing.setTooltip(Tooltip.create(Component.literal(label(
                "Entrada rápida: qué saber antes de gastar recursos o entrar a sistemas peligrosos.",
                "Fast entry: what to know before spending resources or entering dangerous systems."))));

        SiegeButton deployment = new SiegeButton(x + briefingW + gap, y, deploymentW, h,
                Component.literal(full < 165 ? "DEP" : label("DESPLIEGUE", "DEPLOYMENT")), b -> {
            SiegeUiSounds.confirm();
            Minecraft.getInstance().setScreen(new SiegeMultiplayerScreen(screen));
        }, SiegeTheme.RED).setMainMenuStyle(true).withIcon("connect").setCompactCenter(true);
        deployment.setTooltip(Tooltip.create(Component.literal(label(
                "Servidor oficial, compatibilidad, estado y conexión.",
                "Official server, compatibility, status and connection."))));

        event.addListener(briefing);
        event.addListener(deployment);
    }

    private static void splitSettingsRow(ScreenEvent.Init.Post event, SiegeTitleScreen screen, SiegeButton original) {
        int x = original.getX(), y = original.getY(), full = original.getWidth(), h = original.getHeight();
        int gap = full >= 150 ? 4 : 2;
        int opsW = Math.max(1, Math.round((full - gap) * 0.58F));
        int settingsW = Math.max(1, full - gap - opsW);
        event.removeListener(original);

        SiegeButton operations = new SiegeButton(x, y, opsW, h,
                Component.literal(full < 165 ? "OPS" : label("OPERACIONES", "OPERATIONS")), b -> {
            SiegeUiSounds.confirm();
            Minecraft.getInstance().setScreen(new SiegeOperationsHubScreen(screen));
        }, SiegeTheme.CYAN).setMainMenuStyle(true).withIcon("overview").setCompactCenter(true);
        operations.setTooltip(Tooltip.create(Component.literal(label(
                "Sala de Operaciones 4.00: Atlas, razas, progresión, amenazas, multimedia, búsqueda global y herramientas.",
                "4.00 War Room: Atlas, races, progression, threats, media, global search and tools."))));

        SiegeButton settings = new SiegeButton(x + opsW + gap, y, settingsW, h,
                Component.literal(full < 165 ? label("AJ.", "CFG") : label("AJUSTES", "SETTINGS")), b -> {
            SiegeUiSounds.confirm();
            Minecraft.getInstance().setScreen(new SiegeSettingsScreen(screen));
        }, SiegeTheme.RED).setMainMenuStyle(true).withIcon("settings").setCompactCenter(true);
        settings.setTooltip(Tooltip.create(Component.literal(label(
                "Configuración visual, audio, Intel y accesibilidad.",
                "Visual, audio, Intel and accessibility configuration."))));
        event.addListener(operations);
        event.addListener(settings);
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
            case "SiegeAtlasScreen", "SiegeKnowledgeFileScreen" -> SiegeOperationsIndex.Route.ATLAS;
            case "SiegeRaceAtlasScreen" -> SiegeOperationsIndex.Route.RACES;
            case "SiegeProgressionMapScreen" -> SiegeOperationsIndex.Route.PROGRESSION;
            case "SiegeThreatBoardScreen" -> SiegeOperationsIndex.Route.THREATS;
            case "SiegeMediaRoomScreen" -> SiegeOperationsIndex.Route.MEDIA;
            case "SiegeMultiplayerScreen", "JoinMultiplayerScreen", "DirectJoinServerScreen", "ConnectScreen" -> SiegeOperationsIndex.Route.DEPLOYMENT;
            case "IntelScreenV3", "IntelPortraitScreen" -> SiegeOperationsIndex.Route.INTEL;
            case "SiegeKnowledgeScreen" -> SiegeOperationsIndex.Route.KNOWLEDGE;
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

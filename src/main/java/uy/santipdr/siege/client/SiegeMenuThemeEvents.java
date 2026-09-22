package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uy.santipdr.siege.SiegeMod;

/**
 * Applies the common SIEGE 2.0 presentation layer to owned and approved native
 * menu flows while Minecraft retains validation, state and navigation logic.
 */
@Mod.EventBusSubscriber(modid = SiegeMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SiegeMenuThemeEvents {
    private static Screen confirmedDialog;
    private static Screen transitionScreen;
    private static long openedAt;
    private static Screen boundScreen;
    private static final List<NativeButton> buttons = new ArrayList<>();
    private SiegeMenuThemeEvents() { }

    private static boolean owned(Screen s) {
        return s instanceof SiegeTitleScreen || s instanceof SiegeMultiplayerScreen
                || s instanceof SiegeSettingsScreen || s instanceof SiegeSystemScreen || s instanceof SiegeDiagnosticsScreen
                || s instanceof IntelScreenV3 || s instanceof SiegeArchiveScreen || s instanceof IntelPortraitScreen
                || s instanceof SiegeSceneScreen || s instanceof SiegeGuideScreen || s instanceof SiegeGuideImageScreen
                || s instanceof SiegeEvidenceReelScreen;
    }

    private static boolean nativeDialog(Screen s) {
        if (s == null) return false;
        return SiegeMenuPolicy.nativeDialog(s.getClass().getName(), Minecraft.getInstance().level != null, s == confirmedDialog);
    }

    private static boolean themed(Screen s) {
        return Minecraft.getInstance().level == null && (owned(s) || nativeDialog(s));
    }

    private static String translationKey(Component component) {
        return component != null && component.getContents() instanceof TranslatableContents tr ? tr.getKey() : "";
    }

    private static boolean spanish() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private static String label(String es, String en) { return spanish() ? es : en; }

    private static String compactVersion(String full) {
        if (full == null || full.isBlank()) return "DEV";
        int firstDot = full.indexOf('.');
        if (firstDot < 0) return full;
        int secondDot = full.indexOf('.', firstDot + 1);
        return secondDot < 0 ? full : full.substring(0, secondDot);
    }

    @SubscribeEvent
    public static void opening(ScreenEvent.Opening event) {
        Screen next = event.getNewScreen();
        boolean fromMenu = themed(event.getCurrentScreen());
        confirmedDialog = next != null && next.getClass() == ConfirmScreen.class && fromMenu ? next : null;
        boundScreen = null;
        buttons.clear();
        transitionScreen = next;
        openedAt = System.nanoTime() / 1_000_000L;
        SiegeUiSounds.resetHover();
    }

    @SubscribeEvent
    public static void initialized(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (!themed(screen)) return;

        for (var listener : event.getListenersList()) if (listener instanceof EditBox field) {
            field.setTextColor(SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK);
            field.setTextColorUneditable(SiegeConfig.highContrast ? 0xFFD5D7D8 : SiegeTheme.MUTED);
        }

        if (screen instanceof SiegeTitleScreen) rebuildTitleKnowledgeRow(event, screen);

        if (screen instanceof SiegeSettingsScreen) {
            int buttonWidth = screen.width < 360 ? 54 : Math.min(132, Math.max(96, screen.width / 6));
            String currentVersion = version();
            String shortVersion = compactVersion(currentVersion);
            String text = screen.width < 360 ? shortVersion : label("COMANDO " + shortVersion, "COMMAND " + shortVersion);
            SiegeClientProfile.Profile profile = SiegeRuntimeStatus.profile();
            SiegeButton system = new SiegeButton(Math.max(8, screen.width - buttonWidth - 8), 7, buttonWidth, 19,
                    Component.literal(text), b -> {
                        SiegeUiSounds.click();
                        Minecraft.getInstance().setScreen(new SiegeSystemScreen(screen));
                    }, SiegeClientProfile.accent(profile)).withIcon("shield").setCompactCenter(true);
            if (screen.width >= 430) system.withBadge(SiegeClientProfile.shortLabel(profile, spanish()));
            system.setTooltip(Tooltip.create(Component.literal(label(
                    "Centro de comando: perfiles, prioridades, diagnóstico y salud del cliente.",
                    "Command center: profiles, priorities, diagnostics and client health."))));
            event.addListener(system);
            replaceSettingsLoreShortcut(event, screen);
        }

        if (screen instanceof IntelScreenV3) replaceIntelGuideShortcut(event, screen);

        if (!nativeDialog(screen)) return;
        boundScreen = screen;
        buttons.clear();
        int accent = SiegeVanillaChrome.accent(screen);
        String screenName = screen.getClass().getName();
        for (var listener : List.copyOf(event.getListenersList())) {
            if (listener instanceof AbstractButton original && !(original instanceof SiegeButton)) {
                String key = translationKey(original.getMessage());

                if (SiegeMenuPolicy.hideNativeButton(screenName, key)) {
                    original.active = false;
                    original.visible = false;
                    if (screen.getFocused() == original) screen.setFocused(null);
                    event.removeListener(original);
                    continue;
                }

                int adjustedY = SiegeMenuPolicy.adjustedButtonY(screenName, key, original.getY());
                if (adjustedY != original.getY()) original.setY(adjustedY);

                NativeButton replacement = new NativeButton(original, accent);
                boolean focused = screen.getFocused() == original;
                event.removeListener(original);
                event.addListener(replacement);
                if (focused) screen.setFocused(replacement);
                buttons.add(replacement);
            }
        }
    }

    private static void rebuildTitleKnowledgeRow(ScreenEvent.Init.Post event, Screen screen) {
        for (var listener : List.copyOf(event.getListenersList())) {
            if (!(listener instanceof SiegeButton original)) continue;
            if (!"siege.menu.armory".equals(translationKey(original.getMessage()))) continue;

            int x = original.getX(), y = original.getY(), h = original.getHeight(), full = original.getWidth();
            int gap = full >= 150 ? 4 : 2;
            int left = Math.max(1, (full - gap) / 2);
            int right = Math.max(1, full - left - gap);
            event.removeListener(original);

            String archiveText = full < 170 ? label("ARCH.", "ARCH.") : label("ARCHIVO", "ARCHIVE");
            String armoryText = full < 170 ? label("ARS.", "ARM.") : label("ARSENAL", "ARMORY");
            SiegeButton archive = new SiegeButton(x, y, left, h, Component.literal(archiveText), b -> {
                SiegeUiSounds.confirm();
                Minecraft.getInstance().setScreen(new SiegeGuideScreen(screen, SiegeGuideScreen.Mode.ARCHIVE));
            }, SiegeTheme.RED).setMainMenuStyle(true).withIcon("overview").setCompactCenter(true);
            SiegeButton armory = new SiegeButton(x + left + gap, y, right, h, Component.literal(armoryText), b -> {
                SiegeUiSounds.confirm();
                Minecraft.getInstance().setScreen(new SiegeGuideScreen(screen, SiegeGuideScreen.Mode.ARMORY));
            }, SiegeTheme.GOLD).setMainMenuStyle(true).withIcon("package").setCompactCenter(true);
            archive.setTooltip(Tooltip.create(Component.literal(label(
                    "Qué es SIEGE, contexto, operaciones, dificultad, crónicas e inspiraciones.",
                    "What SIEGE is, context, operations, difficulty, chronicles and inspirations."))));
            armory.setTooltip(Tooltip.create(Component.literal(label(
                    "Equipo, objetos y registros multimedia de armamento.",
                    "Equipment, items and armament multimedia records."))));
            event.addListener(archive);
            event.addListener(armory);
            return;
        }
    }

    private static void replaceIntelGuideShortcut(ScreenEvent.Init.Post event, Screen screen) {
        for (var listener : List.copyOf(event.getListenersList())) {
            if (!(listener instanceof SiegeButton original)) continue;
            String message = original.getMessage().getString();
            if (!message.equalsIgnoreCase("GUÍA") && !message.equalsIgnoreCase("GUIDE")) continue;
            int x = original.getX(), y = original.getY(), w = original.getWidth(), h = original.getHeight();
            event.removeListener(original);
            SiegeButton archive = new SiegeButton(x, y, w, h, Component.literal(label("ARCHIVO", "ARCHIVE")), b -> {
                SiegeUiSounds.confirm();
                Minecraft.getInstance().setScreen(new SiegeGuideScreen(screen, SiegeGuideScreen.Mode.ARCHIVE));
            }, SiegeTheme.GOLD).withIcon("overview").setCompactCenter(true);
            archive.setTooltip(Tooltip.create(Component.literal(label(
                    "Contexto general y conocimiento de SIEGE; los dossiers permanecen en Intel.",
                    "General SIEGE context and knowledge; dossiers remain in Intel."))));
            event.addListener(archive);
            return;
        }
    }

    private static void replaceSettingsLoreShortcut(ScreenEvent.Init.Post event, Screen screen) {
        for (var listener : List.copyOf(event.getListenersList())) {
            if (!(listener instanceof SiegeButton original)) continue;
            String message = original.getMessage().getString();
            if (!message.equalsIgnoreCase("GUÍA OPERATIVA") && !message.equalsIgnoreCase("OPERATIONAL GUIDE")) continue;
            int x = original.getX(), y = original.getY(), w = original.getWidth(), h = original.getHeight();
            event.removeListener(original);
            SiegeButton diagnostics = new SiegeButton(x, y, w, h,
                    Component.literal(label("DIAGNÓSTICO Y RECUPERACIÓN", "DIAGNOSTICS & RECOVERY")), b -> {
                SiegeUiSounds.click();
                Minecraft.getInstance().setScreen(new SiegeDiagnosticsScreen(screen));
            }, SiegeTheme.ORANGE).withIcon("shield");
            diagnostics.setTooltip(Tooltip.create(Component.literal(label(
                    "Estado técnico del cliente y reparaciones explícitas; sin lore ni dossiers.",
                    "Client technical state and explicit repairs; no lore or dossiers."))));
            event.addListener(diagnostics);
            return;
        }
    }

    @SubscribeEvent
    public static void background(ScreenEvent.BackgroundRendered event) {
        Screen screen = event.getScreen();
        if (!nativeDialog(screen)) return;
        SiegeVanillaChrome.renderBackground(screen, event.getGuiGraphics());
    }

    @SubscribeEvent
    public static void beforeRender(ScreenEvent.Render.Pre event) {
        if (!themed(event.getScreen())) return;
        if (boundScreen == event.getScreen()) for (NativeButton button : buttons) button.sync();
    }

    @SubscribeEvent
    public static void afterRender(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        if (!themed(screen)) return;
        GuiGraphics g = event.getGuiGraphics();

        if (nativeDialog(screen)) {
            int firstWidgetY = screen.height;
            for (var child : screen.children()) if (child instanceof AbstractWidget widget && widget.visible)
                firstWidgetY = Math.min(firstWidgetY, widget.getY());
            int maskBottom = SiegeMenuPolicy.vanillaTitleMaskBottom(firstWidgetY, screen.height);
            if (maskBottom > 0) {
                g.pose().pushPose();
                g.pose().translate(0, 0, 425);
                g.fill(0, 0, screen.width, maskBottom, SiegeConfig.highContrast ? 0xFF090A0C : 0xF20D0E10);
                g.pose().popPose();
            }

            SiegeVanillaChrome.decorateWidgets(screen, g);
            SiegeUiSounds.updateHover(screen.children());
            SiegeVanillaChrome.renderOverlay(screen, g);
        } else {
            for (var child : screen.children()) if (child instanceof EditBox field && field.visible) {
                int color = field.isFocused() ? SiegeTheme.FOCUS
                        : SiegeConfig.highContrast ? 0xFF9AA1A6 : 0xFF656061;
                int x = Math.max(0, field.getX());
                int y = Math.max(0, field.getY());
                int right = Math.min(screen.width, field.getX() + field.getWidth());
                int bottom = Math.min(screen.height, field.getY() + field.getHeight());
                if (right - x >= 2 && bottom - y >= 2) {
                    SiegeTheme.frame(g, x, y, right - x, bottom - y, color);
                    if (field.isFocused()) SiegeTheme.focusCorners(g, x, y, right - x, bottom - y, color);
                }
            }
        }

        SiegeScreenChrome.renderOverlay(screen, g);
        if (!(screen instanceof SiegeTitleScreen)) SiegeCommandStrip.render(screen, g);

        if (transitionScreen != screen) {
            transitionScreen = screen;
            openedAt = System.nanoTime() / 1_000_000L;
        }
        int alpha = SiegeMenuPolicy.entryShade(System.nanoTime() / 1_000_000L - openedAt,
                SiegeConfig.menuEffects && !SiegeConfig.reduceFlashes, SiegeConfig.reducedMotion);
        if (alpha > 0) {
            g.pose().pushPose();
            g.pose().translate(0, 0, 500);
            g.fill(0, 0, screen.width, screen.height, alpha << 24);
            g.pose().popPose();
        }
    }

    private static String version() { return SiegeRuntimeStatus.version(); }

    @SubscribeEvent
    public static void tooltip(RenderTooltipEvent.Color event) {
        if (!themed(Minecraft.getInstance().screen)) return;
        int accent = nativeDialog(Minecraft.getInstance().screen)
                ? SiegeVanillaChrome.accent(Minecraft.getInstance().screen)
                : SiegeClientProfile.accent(SiegeRuntimeStatus.profile());
        event.setBackgroundStart(SiegeConfig.highContrast ? 0xFF08090A : 0xFA191719);
        event.setBackgroundEnd(SiegeConfig.highContrast ? 0xFF08090A : 0xFA0F1011);
        event.setBorderStart(accent);
        event.setBorderEnd(SiegeConfig.highContrast ? SiegeTheme.INK : 0xFF494346);
    }

    @SubscribeEvent
    public static void nativeClick(net.minecraftforge.client.event.sound.PlaySoundEvent event) {
        if (!nativeDialog(Minecraft.getInstance().screen) || event.getSound() == null) return;
        var location = event.getSound().getLocation();
        if (!location.getNamespace().equals("minecraft") || !location.getPath().equals("ui.button.click")) return;
        if (!SiegeConfig.uiSounds || SiegeConfig.uiVolume <= 0) {
            event.setSound(null);
            return;
        }
        var sound = net.minecraft.sounds.SoundEvent.createVariableRangeEvent(SiegeMod.UI_CLICK.getId());
        event.setSound(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(sound, 1.0F,
                SiegeConfig.clampVolume(SiegeConfig.uiVolume) / 100.0F));
    }

    private static final class NativeButton extends SiegeButton {
        private final AbstractButton source;
        private String visualKey = "";
        private Tooltip cachedTooltip;

        NativeButton(AbstractButton source, int accent) {
            super(source.getX(), source.getY(), source.getWidth(), source.getHeight(), source.getMessage(), b -> {}, accent);
            this.source = source;
            setCompactCenter(true);
            setFullHoverFrame(true);
            sync();
        }

        void sync() {
            active = source.active;
            visible = source.visible;
            setX(source.getX());
            setY(source.getY());
            setWidth(Math.max(1, source.getWidth()));
            setHeight(Math.max(1, source.getHeight()));

            Component message = source.getMessage();
            String key = message.getContents() instanceof TranslatableContents tr
                    ? tr.getKey() + '\u0000' + message.getString() : message.getString();
            if (!key.equals(visualKey)) {
                visualKey = key;
                setMessage(message);
                withIcon(SiegeVanillaChrome.buttonIcon(message));
            }

            Tooltip tooltip = source.getTooltip();
            if (tooltip != cachedTooltip) {
                cachedTooltip = tooltip;
                setTooltip(tooltip);
            }
        }

        @Override public void onPress() {
            sync();
            if (!active || !visible) return;
            super.onPress();
            String key = translationKey(source.getMessage());
            if (key.equals("gui.cancel") || key.equals("gui.back") || key.equals("gui.no")) SiegeUiSounds.back();
            else SiegeUiSounds.confirm();
            source.onPress();
        }

        @Override public boolean mouseClicked(double x, double y, int button) {
            sync();
            if (button == 1 && active && visible) return source.mouseClicked(x, y, button);
            return super.mouseClicked(x, y, button);
        }

        @Override public boolean mouseScrolled(double x, double y, double delta) {
            sync();
            return active && visible && source.isMouseOver(x, y) && source.mouseScrolled(x, y, delta);
        }

        @Override public boolean keyPressed(int key, int scan, int modifiers) {
            sync();
            return super.keyPressed(key, scan, modifiers);
        }
    }
}

package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import uy.santipdr.siege.SiegeMod;

/**
 * Applies SIEGE visual/audio language to approved vanilla title-menu flows while
 * retaining Minecraft's original validation, option state and navigation logic.
 */
@Mod.EventBusSubscriber(modid = SiegeMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SiegeMenuThemeEvents {
    private static Screen confirmedDialog;
    private static Screen transitionScreen;
    private static long openedAt;
    private static Screen boundScreen;
    private static final List<NativeButton> buttons = new ArrayList<>();
    private static String cachedVersion;
    private SiegeMenuThemeEvents() { }

    private static boolean owned(Screen s) {
        return s instanceof SiegeTitleScreen || s instanceof SiegeMultiplayerScreen
                || s instanceof SiegeSettingsScreen || s instanceof IntelScreenV3
                || s instanceof IntelPortraitScreen || s instanceof SiegeSceneScreen
                || s instanceof SiegeGuideScreen || s instanceof SiegeGuideImageScreen;
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

        // Text colors are screen state, not animation state. Set them once at init
        // instead of rewriting every EditBox on every rendered frame.
        for (var listener : event.getListenersList()) if (listener instanceof EditBox field) {
            field.setTextColor(SiegeTheme.INK);
            field.setTextColorUneditable(SiegeTheme.MUTED);
        }

        if (!nativeDialog(screen)) return;
        boundScreen = screen;
        buttons.clear();
        int accent = SiegeVanillaChrome.accent(screen);
        String screenName = screen.getClass().getName();
        for (var listener : List.copyOf(event.getListenersList())) {
            if (listener instanceof AbstractButton original && !(original instanceof SiegeButton)) {
                String key = translationKey(original.getMessage());

                // Remove telemetry/data and credits entry points from the Options
                // flow without replacing Minecraft's whole OptionsScreen.
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
            SiegeVanillaChrome.decorateWidgets(screen, g);
            SiegeUiSounds.updateHover(screen.children());
            SiegeVanillaChrome.renderOverlay(screen, g);
        } else {
            // SIEGE-owned screens keep focus decoration strictly inside the field
            // bounds so dense layouts cannot paint over adjacent controls.
            for (var child : screen.children()) if (child instanceof EditBox field && field.visible) {
                int color = field.isFocused() ? SiegeTheme.FOCUS : 0xFF656061;
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

        renderBuildTag(screen, g);

        if (transitionScreen != screen) {
            transitionScreen = screen;
            openedAt = System.nanoTime() / 1_000_000L;
        }
        int alpha = SiegeMenuPolicy.entryShade(System.nanoTime() / 1_000_000L - openedAt,
                SiegeConfig.menuEffects, SiegeConfig.reducedMotion);
        if (alpha > 0) {
            g.pose().pushPose();
            g.pose().translate(0, 0, 500);
            g.fill(0, 0, screen.width, screen.height, alpha << 24);
            g.pose().popPose();
        }
    }

    private static void renderBuildTag(Screen screen, GuiGraphics g) {
        if (!(screen instanceof SiegeTitleScreen) || !SiegeConfig.showBuildLabel || screen.width < 300) return;
        String text = "BUILD " + version();
        var font = Minecraft.getInstance().font;
        float scale = 0.68F;
        int textWidth = Math.round(font.width(text) * scale);
        int boxW = textWidth + 10;
        int boxH = 11;
        int x = 6;
        int y = screen.height - boxH - 3;

        g.fill(x, y, x + boxW, y + boxH, 0xC0131315);
        g.fill(x, y, x + 2, y + boxH, 0xB8E54852);
        g.pose().pushPose();
        g.pose().translate(x + 5.0F, y + 2.0F, 0.0F);
        g.pose().scale(scale, scale, 1.0F);
        g.drawString(font, text, 0, 0, 0xFF9CA4AA, false);
        g.pose().popPose();
    }

    private static String version() {
        if (cachedVersion != null) return cachedVersion;
        cachedVersion = ModList.get().getModContainerById(SiegeMod.MOD_ID)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("DEV");
        return cachedVersion;
    }

    @SubscribeEvent
    public static void tooltip(RenderTooltipEvent.Color event) {
        if (!themed(Minecraft.getInstance().screen)) return;
        int accent = nativeDialog(Minecraft.getInstance().screen)
                ? SiegeVanillaChrome.accent(Minecraft.getInstance().screen) : SiegeTheme.RED;
        event.setBackgroundStart(0xFA191719);
        event.setBackgroundEnd(0xFA0F1011);
        event.setBorderStart(accent);
        event.setBorderEnd(0xFF494346);
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

    /** Keeps the original object alive because vanilla screen fields update it after init. */
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
            // CycleButton supports reverse cycling with the secondary mouse button.
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

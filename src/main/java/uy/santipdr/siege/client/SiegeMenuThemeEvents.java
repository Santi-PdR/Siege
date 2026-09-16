package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.EditServerScreen;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uy.santipdr.siege.SiegeMod;

/** Skin native network dialogs in place, retaining their validation, cancellation and text. */
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
                || s instanceof SiegeSettingsScreen || s instanceof IntelScreenV3
                || s instanceof IntelPortraitScreen || s instanceof SiegeSceneScreen;
    }
    private static boolean nativeDialog(Screen s) {
        if (s == null) return false;
        return SiegeMenuPolicy.nativeDialog(s.getClass().getName(), Minecraft.getInstance().level != null, s == confirmedDialog);
    }
    private static boolean themed(Screen s) {
        return Minecraft.getInstance().level == null && (owned(s) || nativeDialog(s));
    }

    @SubscribeEvent
    public static void opening(ScreenEvent.Opening event) {
        Screen next = event.getNewScreen();
        boolean fromMenu = themed(event.getCurrentScreen());
        confirmedDialog = next != null && next.getClass() == ConfirmScreen.class && fromMenu ? next : null;
        boundScreen = null; buttons.clear();
        transitionScreen = next;
        openedAt = System.nanoTime() / 1_000_000L;
        SiegeUiSounds.resetHover();
    }
    @SubscribeEvent
    public static void initialized(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (!nativeDialog(screen)) return;
        boundScreen = screen; buttons.clear();
        for (var listener : List.copyOf(event.getListenersList())) {
            if (listener instanceof AbstractButton original && !(original instanceof SiegeButton)) {
                NativeButton replacement = new NativeButton(original);
                boolean focused = screen.getFocused() == original;
                event.removeListener(original); event.addListener(replacement);
                if (focused) screen.setFocused(replacement);
                buttons.add(replacement);
            } else if (listener instanceof EditBox field) {
                field.setTextColor(SiegeTheme.INK);
                field.setTextColorUneditable(SiegeTheme.MUTED);
            }
        }
    }
    @SubscribeEvent
    public static void background(ScreenEvent.BackgroundRendered event) {
        Screen screen = event.getScreen();
        if (!nativeDialog(screen)) return;
        GuiGraphics g = event.getGuiGraphics();
        SiegeBackgrounds.render(g, screen.width, screen.height, System.currentTimeMillis());
        g.fill(0, 0, screen.width, screen.height, 0xAD080809);
        int margin = Math.max(8, (screen.width - 720) / 2);
        SiegeTheme.panel(g, margin, 8, screen.width - margin * 2, screen.height - 16, SiegeTheme.RED);
        // Only perimeter ornament: native titles, errors and validation keep all their space.
        if (screen.width >= 520) {
            SiegeTheme.icon(g, margin + 10, screen.height - 29,
                    screen instanceof ConnectScreen ? "connect" : "intel", SiegeTheme.MUTED);
        }
    }
    @SubscribeEvent
    public static void beforeRender(ScreenEvent.Render.Pre event) {
        if (!themed(event.getScreen())) return;
        if (boundScreen == event.getScreen()) for (NativeButton button : buttons) button.sync();
        for (var child : event.getScreen().children()) if (child instanceof EditBox field) {
            field.setTextColor(SiegeTheme.INK); field.setTextColorUneditable(SiegeTheme.MUTED);
        }
    }
    @SubscribeEvent
    public static void afterRender(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        if (!themed(screen)) return;
        GuiGraphics g = event.getGuiGraphics();
        for (var child : screen.children()) if (child instanceof EditBox field && field.visible) {
            int color = field.isFocused() ? SiegeTheme.RED : 0xFF656061;
            SiegeTheme.frame(g, field.getX() - 1, field.getY() - 1, field.getWidth() + 2, field.getHeight() + 2, color);
        }
        if (nativeDialog(screen)) SiegeUiSounds.updateHover(screen.children());
        if (transitionScreen != screen) { transitionScreen = screen; openedAt = System.nanoTime() / 1_000_000L; }
        int alpha = SiegeMenuPolicy.entryShade(System.nanoTime() / 1_000_000L - openedAt, SiegeConfig.menuEffects, SiegeConfig.reducedMotion);
        if (alpha > 0) {
            g.pose().pushPose(); g.pose().translate(0, 0, 500);
            g.fill(0, 0, screen.width, screen.height, alpha << 24);
            g.pose().popPose();
        }
    }
    @SubscribeEvent
    public static void tooltip(RenderTooltipEvent.Color event) {
        if (!themed(Minecraft.getInstance().screen)) return;
        event.setBackgroundStart(0xFA191719);
        event.setBackgroundEnd(0xFA0F1011);
        event.setBorderStart(0xFF9B555A);
        event.setBorderEnd(0xFF494346);
    }
    /** Keeps the original object alive because vanilla screen fields update it after init. */
    private static final class NativeButton extends SiegeButton {
        private final AbstractButton source;
        NativeButton(AbstractButton source) {
            super(source.getX(), source.getY(), source.getWidth(), source.getHeight(), source.getMessage(), b -> {}, SiegeTheme.RED);
            this.source = source;
            setCompactCenter(true); setFullHoverFrame(true);
            sync();
        }
        void sync() {
            active = source.active; visible = source.visible;
            setX(source.getX()); setY(source.getY()); setWidth(source.getWidth());
            setMessage(source.getMessage()); setTooltip(source.getTooltip());
        }
        @Override public void onPress() {
            sync();
            if (!active || !visible) return;
            String key = source.getMessage().getContents() instanceof TranslatableContents tr ? tr.getKey() : "";
            if (key.equals("gui.cancel") || key.equals("gui.back") || key.equals("gui.no")) SiegeUiSounds.back();
            else SiegeUiSounds.confirm();
            source.onPress();
        }
        @Override public boolean mouseClicked(double x, double y, int button) { sync(); return super.mouseClicked(x, y, button); }
        @Override public boolean keyPressed(int key, int scan, int modifiers) { sync(); return super.keyPressed(key, scan, modifiers); }
    }
}

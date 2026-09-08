package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class SiegeTitleScreen extends Screen {
    public SiegeTitleScreen() { super(Component.literal("Eternal Craft: SIEGE")); }

    @Override protected void init() {
        SiegeUiSounds.resetHover();
        int w = Math.min(294, Math.max(170, width - 24));
        int h = height < 220 ? 18 : height < 320 ? 22 : 30;
        int gap = height < 220 ? 2 : height < 320 ? 4 : 9;
        int total = h * 5 + gap * 4;
        int x = width < 700 ? (width - w) / 2 : Math.min(30, width - w - 12);
        int y = height < 220 ? Math.max(52, Math.min(70, height - total - 10))
                : Math.max(76, Math.min(height - total - 18, (height - total) / 2 + 16));
        addRenderableWidget(command(x, y, w, h, "siege.menu.deployment", b -> minecraft.setScreen(new JoinMultiplayerScreen(this))));
        addRenderableWidget(command(x, y += h + gap, w, h, "siege.menu.intel", b -> minecraft.setScreen(new IntelScreen(this))));
        addRenderableWidget(command(x, y += h + gap, w, h, "siege.menu.armory", b -> minecraft.setScreen(new OptionsScreen(this, minecraft.options))));
        addRenderableWidget(command(x, y += h + gap, w, h, "siege.menu.settings", b -> minecraft.setScreen(new SiegeSettingsScreen(this))));
        addRenderableWidget(command(x, y += h + gap, w, h, "siege.menu.quit", b -> minecraft.stop()));

        int small = Math.min(142, Math.max(96, (width - 30) / 3));
        addRenderableWidget(command(width - small - 12, 12, small, 20, "siege.menu.next_track", b -> {
            SiegeUiSounds.nextTrack(); SiegeMusic.nextTrack();
        }));
    }

    private Button command(int x, int y, int w, int h, String key, Button.OnPress press) {
        return Button.builder(Component.translatable(key), b -> { SiegeUiSounds.click(); press.onPress(b); }).bounds(x, y, w, h).build();
    }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying(); SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        int titleY = height < 220 ? 34 : height < 300 ? 42 : 54;
        g.drawCenteredString(font, Component.literal("ETERNAL CRAFT"), width / 2, titleY, 0xFFF4F1E9);
        g.drawCenteredString(font, Component.literal("S  I  E  G  E"), width / 2, titleY + 18, 0xFFFF5555);
        if (width >= 520) g.drawString(font, "REC", width - 54, 38, 0xFFFF5555, false);
        if (width >= 420) g.drawString(font, "BUILD 0.2.0 // SECURE CHANNEL", Math.max(8, width - 206), height - 14, 0xFF8A8A8A, false);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    @Override public boolean isPauseScreen() { return false; }
    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_S && Screen.hasControlDown()) { minecraft.setScreen(new SelectWorldScreen(this)); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

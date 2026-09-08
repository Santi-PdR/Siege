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
    public SiegeTitleScreen() {
        super(Component.literal("Eternal Craft: SIEGE"));
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        int buttonWidth = Math.min(220, Math.max(148, width / 5));
        int buttonHeight = height < 260 ? 18 : 22;
        int gap = height < 260 ? 3 : 5;
        int totalHeight = buttonHeight * 5 + gap * 4;
        int x = Math.max(14, width / 45);
        int y = Math.max(70, (height - totalHeight) / 2 + 18);

        addRenderableWidget(command(x, y, buttonWidth, buttonHeight, "siege.menu.deployment", b -> minecraft.setScreen(new JoinMultiplayerScreen(this))));
        addRenderableWidget(command(x, y += buttonHeight + gap, buttonWidth, buttonHeight, "siege.menu.intel", b -> minecraft.setScreen(new IntelScreen(this))));
        addRenderableWidget(command(x, y += buttonHeight + gap, buttonWidth, buttonHeight, "siege.menu.armory", b -> minecraft.setScreen(new OptionsScreen(this, minecraft.options))));
        addRenderableWidget(command(x, y += buttonHeight + gap, buttonWidth, buttonHeight, "siege.menu.settings", b -> minecraft.setScreen(new SiegeSettingsScreen(this))));
        addRenderableWidget(command(x, y += buttonHeight + gap, buttonWidth, buttonHeight, "siege.menu.quit", b -> minecraft.stop()));

        int trackWidth = Math.min(124, Math.max(88, width / 9));
        addRenderableWidget(command(width - trackWidth - 10, 10, trackWidth, 18, "siege.menu.next_track", b -> {
            SiegeUiSounds.nextTrack();
            SiegeMusic.nextTrack();
        }));
    }

    private Button command(int x, int y, int width, int height, String key, Button.OnPress press) {
        return Button.builder(Component.translatable(key), button -> {
            SiegeUiSounds.click();
            press.onPress(button);
        }).bounds(x, y, width, height).build();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(graphics, width, height, System.currentTimeMillis());
        int panelWidth = Math.min(248, Math.max(176, width / 4));
        graphics.fill(0, 0, panelWidth, height, 0x77000000);
        graphics.fill(panelWidth - 1, 0, panelWidth, height, 0x6645B9D9);
        int titleY = height < 240 ? 30 : 44;
        graphics.drawCenteredString(font, Component.literal("ETERNAL CRAFT"), width / 2, titleY, 0xFFF4F1E9);
        graphics.drawCenteredString(font, Component.literal("S  I  E  G  E"), width / 2, titleY + 16, 0xFFFF5555);
        if (width >= 520) graphics.drawString(font, "REC", width - 50, 34, 0xFFFF5555, false);
        if (width >= 420) graphics.drawString(font, "BUILD 0.5.0 // SECURE CHANNEL", Math.max(8, width - 220), height - 14, 0xFF8A8A8A, false);
        super.render(graphics, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_S && Screen.hasControlDown()) {
            minecraft.setScreen(new SelectWorldScreen(this));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

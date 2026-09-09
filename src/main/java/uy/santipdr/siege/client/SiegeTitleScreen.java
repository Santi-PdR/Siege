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
    private static final int ACCENT = 0xFF55BFD9;
    private int menuX;
    private int menuWidth;
    private int menuTop;
    private int menuBottom;

    public SiegeTitleScreen() {
        super(Component.literal("Eternal Craft: SIEGE"));
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        boolean compact = width < 520 || height < 290;
        int margin = compact ? 10 : Math.max(14, width / 55);
        menuWidth = Math.min(compact ? 184 : 224, Math.max(138, width / (compact ? 2 : 5)));
        menuWidth = Math.min(menuWidth, width - margin * 2);
        int buttonHeight = compact ? 18 : 22;
        int gap = compact ? 3 : 5;
        int totalHeight = buttonHeight * 5 + gap * 4;
        menuX = margin;
        menuTop = Math.max(compact ? 66 : 84, (height - totalHeight) / 2 + (compact ? 12 : 20));
        menuTop = Math.min(menuTop, Math.max(48, height - totalHeight - 14));
        menuBottom = menuTop + totalHeight;

        int y = menuTop;
        addRenderableWidget(command(menuX, y, menuWidth, buttonHeight, "siege.menu.deployment", b -> minecraft.setScreen(new JoinMultiplayerScreen(this))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.intel", b -> minecraft.setScreen(new IntelScreen(this))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.armory", b -> minecraft.setScreen(new OptionsScreen(this, minecraft.options))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.settings", b -> minecraft.setScreen(new SiegeSettingsScreen(this))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.quit", b -> minecraft.stop()));

        int trackWidth = Math.min(126, Math.max(86, width / 9));
        SiegeButton track = new SiegeButton(width - trackWidth - 9, 9, trackWidth, compact ? 18 : 20,
                Component.translatable("siege.menu.next_track"), b -> {
                    SiegeUiSounds.nextTrack();
                    SiegeMusic.nextTrack();
                }, 0xFFD64B4B);
        addRenderableWidget(track);
    }

    private SiegeButton command(int x, int y, int width, int height, String key, Button.OnPress press) {
        return new SiegeButton(x, y, width, height, Component.translatable(key), button -> {
            SiegeUiSounds.click();
            press.onPress(button);
        }, ACCENT);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(graphics, width, height, System.currentTimeMillis());

        boolean compact = width < 520 || height < 290;
        int panelRight = Math.min(width, menuX + menuWidth + (compact ? 18 : 28));
        graphics.fill(0, 0, panelRight, height, 0xB5090C10);
        graphics.fill(panelRight - 2, 0, panelRight, height, 0x9955BFD9);
        graphics.fill(0, 0, width, 2, 0xAA1F262D);

        for (int y = 20; y < height; y += 32) {
            graphics.fill(0, y, panelRight, y + 1, 0x181C9AB0);
        }

        int titleY = compact ? 24 : 35;
        graphics.drawString(font, "ETERNAL CRAFT", menuX, titleY, 0xFFF1EEE7, false);
        graphics.drawString(font, "S  I  E  G  E", menuX, titleY + 13, 0xFFFF5555, false);
        graphics.fill(menuX, titleY + 26, Math.min(panelRight - 10, menuX + menuWidth), titleY + 27, 0x8855BFD9);

        if (height >= 215) {
            int infoY = Math.min(height - 26, menuBottom + 11);
            if (infoY > menuBottom + 3) {
                graphics.drawString(font, "// MENU COMMAND LINK", menuX, infoY, 0xFF77818A, false);
            }
        }

        if (width >= 430) {
            graphics.drawString(font, "REC", width - 46, 36, 0xFFFF5555, false);
            String music = "AUDIO " + SiegeConfig.musicVolume + "% // " + SiegeMusic.currentTrackName();
            graphics.drawString(font, font.plainSubstrByWidth(music, Math.max(80, width / 3)), width - Math.min(width / 3, 280) - 10, height - 14, 0xFF8C949B, false);
        }
        if (width >= 610) {
            graphics.drawString(font, "BUILD 0.5.4 // SECURE CHANNEL", 10, height - 14, 0xFF727A81, false);
        }

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

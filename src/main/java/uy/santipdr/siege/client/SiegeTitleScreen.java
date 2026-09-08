package uy.santipdr.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class SiegeTitleScreen extends Screen {
    public SiegeTitleScreen() { super(Component.literal("Eternal Craft: SIEGE")); }

    @Override protected void init() {
        int x = 30, w = 294, h = 34, gap = 14;
        int y = Math.max(190, height / 2 - 70);
        addRenderableWidget(command(x, y, w, h, "siege.menu.deployment",
                b -> minecraft.setScreen(new JoinMultiplayerScreen(this))));
        addRenderableWidget(command(x, y += h + gap, w, h, "siege.menu.intel",
                b -> minecraft.setScreen(new IntelScreen(this))));
        addRenderableWidget(command(x, y += h + gap, w, h, "siege.menu.armory",
                b -> minecraft.setScreen(new OptionsScreen(this, minecraft.options))));
        addRenderableWidget(command(x, y += h + gap, w, h, "siege.menu.command",
                b -> minecraft.setScreen(new OptionsScreen(this, minecraft.options))));
        addRenderableWidget(command(x, y += h + gap, w, h, "siege.menu.quit", b -> minecraft.stop()));
    }

    private Button command(int x, int y, int w, int h, String key, Button.OnPress press) {
        return Button.builder(Component.translatable(key), press).bounds(x, y, w, h).build();
    }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        Component eternal = Component.literal("ETERNAL CRAFT");
        Component siege = Component.literal("S  I  E  G  E");
        g.drawCenteredString(font, eternal, width / 2, 54, 0xFFF4F1E9);
        g.drawCenteredString(font, siege, width / 2, 72, 0xFFFF5555);
        g.drawString(font, "REC", width - 54, 16, 0xFFFF5555, false);
        g.drawString(font, "BUILD 0.1.0 // SECURE CHANNEL", width - 190, height - 18, 0xFF8A8A8A, false);
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_S && Screen.hasControlDown()) {
            minecraft.setScreen(new SelectWorldScreen(this));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

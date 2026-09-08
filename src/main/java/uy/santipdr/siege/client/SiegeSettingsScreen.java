package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SiegeSettingsScreen extends Screen {
    private final Screen parent;

    public SiegeSettingsScreen(Screen parent) {
        super(Component.translatable("siege.settings.title"));
        this.parent = parent;
    }

    @Override protected void init() {
        SiegeUiSounds.resetHover();
        int w = Math.min(360, Math.max(180, width - 32));
        int h = height < 220 ? 18 : height < 300 ? 20 : 24;
        int gap = height < 220 ? 2 : height < 300 ? 4 : 7;
        int total = h * 6 + gap * 5;
        int x = (width - w) / 2;
        int y = Math.max(height < 220 ? 32 : 48, (height - total) / 2);
        addRenderableWidget(toggle(x, y, w, h, "siege.settings.music", () -> {
            SiegeConfig.music = !SiegeConfig.music;
            if (!SiegeConfig.music) SiegeMusic.stop(); else SiegeMusic.ensurePlaying();
        }, () -> SiegeConfig.music));
        addRenderableWidget(toggle(x, y += h + gap, w, h, "siege.settings.ui_sounds", () -> SiegeConfig.uiSounds = !SiegeConfig.uiSounds, () -> SiegeConfig.uiSounds));
        addRenderableWidget(toggle(x, y += h + gap, w, h, "siege.settings.backgrounds", () -> SiegeConfig.animatedBackgrounds = !SiegeConfig.animatedBackgrounds, () -> SiegeConfig.animatedBackgrounds));
        addRenderableWidget(toggle(x, y += h + gap, w, h, "siege.settings.reduced_motion", () -> SiegeConfig.reducedMotion = !SiegeConfig.reducedMotion, () -> SiegeConfig.reducedMotion));
        addRenderableWidget(Button.builder(graphicsLabel(), b -> {
            SiegeUiSounds.click();
            SiegeConfig.graphics = SiegeConfig.graphics.next();
            b.setMessage(graphicsLabel());
            SiegeConfig.save();
        }).bounds(x, y += h + gap, w, h).build());
        addRenderableWidget(Button.builder(Component.translatable("siege.common.back"), b -> onClose()).bounds(x, y + h + gap, w, h).build());
    }

    private Button toggle(int x, int y, int w, int h, String key, Runnable action, Flag flag) {
        Button[] holder = new Button[1];
        holder[0] = Button.builder(toggleLabel(key, flag.get()), b -> {
            SiegeUiSounds.click(); action.run(); SiegeConfig.save(); b.setMessage(toggleLabel(key, flag.get()));
        }).bounds(x, y, w, h).build();
        return holder[0];
    }

    private Component graphicsLabel() {
        return Component.translatable("siege.settings.graphics").append(": ")
                .append(Component.translatable("siege.settings.graphics." + SiegeConfig.graphics.name().toLowerCase()));
    }
    private Component toggleLabel(String key, boolean enabled) {
        return Component.translatable(key).append(": ").append(Component.translatable(enabled ? "siege.common.on" : "siege.common.off"));
    }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, 0x79000000);
        g.drawCenteredString(font, title, width / 2, height < 220 ? 14 : 22, 0xFFFF5555);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    @Override public void onClose() { SiegeUiSounds.back(); SiegeConfig.save(); minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
    private interface Flag { boolean get(); }
}

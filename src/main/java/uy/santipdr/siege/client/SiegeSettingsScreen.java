package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SiegeSettingsScreen extends Screen {
    private static final int ACCENT = 0xFF55BFD9;
    private final Screen parent;
    private int gridX;
    private int gridY;
    private int gridWidth;
    private int cellHeight;
    private int rowGap;

    public SiegeSettingsScreen(Screen parent) {
        super(Component.translatable("siege.settings.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        boolean compact = width < 520 || height < 300;
        int margin = compact ? 8 : 14;
        gridWidth = Math.min(560, width - margin * 2);
        gridWidth = Math.max(220, gridWidth);
        gridX = (width - gridWidth) / 2;
        gridY = compact ? 48 : 68;
        cellHeight = compact ? 20 : 24;
        rowGap = compact ? 4 : 6;
        int colGap = compact ? 4 : 7;
        int cellWidth = (gridWidth - colGap) / 2;

        addRenderableWidget(new SiegeButton(8, 8, Math.min(82, Math.max(64, width / 6)), 19,
                Component.translatable("siege.common.back"), b -> onClose(), 0xFFD64B4B));

        int row0 = gridY;
        int row1 = row0 + cellHeight + rowGap;
        int row2 = row1 + cellHeight + rowGap;
        int row3 = row2 + cellHeight + rowGap;
        int right = gridX + cellWidth + colGap;

        addRenderableWidget(toggle(gridX, row0, cellWidth, cellHeight, "siege.settings.music", () -> {
            SiegeConfig.music = !SiegeConfig.music;
            if (!SiegeConfig.music) SiegeMusic.stop(); else SiegeMusic.ensurePlaying();
        }, () -> SiegeConfig.music));

        addRenderableWidget(action(right, row0, cellWidth, cellHeight, volumeLabel(), () -> adjustVolume(-10)));
        addRenderableWidget(action(gridX, row1, cellWidth, cellHeight, volumeUpLabel(), () -> adjustVolume(10)));
        addRenderableWidget(new SiegeButton(right, row1, cellWidth, cellHeight,
                Component.translatable("siege.menu.next_track"), b -> {
                    SiegeUiSounds.nextTrack();
                    SiegeMusic.nextTrack();
                }, 0xFFD6A94B));

        addRenderableWidget(toggle(gridX, row2, cellWidth, cellHeight, "siege.settings.ui_sounds",
                () -> SiegeConfig.uiSounds = !SiegeConfig.uiSounds, () -> SiegeConfig.uiSounds));
        addRenderableWidget(toggle(right, row2, cellWidth, cellHeight, "siege.settings.backgrounds",
                () -> SiegeConfig.animatedBackgrounds = !SiegeConfig.animatedBackgrounds, () -> SiegeConfig.animatedBackgrounds));
        addRenderableWidget(toggle(gridX, row3, cellWidth, cellHeight, "siege.settings.reduced_motion",
                () -> SiegeConfig.reducedMotion = !SiegeConfig.reducedMotion, () -> SiegeConfig.reducedMotion));

        SiegeButton graphics = new SiegeButton(right, row3, cellWidth, cellHeight, graphicsLabel(), b -> {
            SiegeUiSounds.click();
            SiegeConfig.graphics = SiegeConfig.graphics.next();
            b.setMessage(graphicsLabel());
            SiegeConfig.save();
        }, ACCENT);
        addRenderableWidget(graphics);
    }

    private SiegeButton action(int x, int y, int w, int h, Component text, Runnable action) {
        return new SiegeButton(x, y, w, h, text, b -> {
            SiegeUiSounds.click();
            action.run();
        }, ACCENT);
    }

    private SiegeButton toggle(int x, int y, int w, int h, String key, Runnable action, Flag flag) {
        SiegeButton button = new SiegeButton(x, y, w, h, toggleLabel(key, flag.get()), b -> {
            SiegeUiSounds.click();
            action.run();
            SiegeConfig.save();
            b.setMessage(toggleLabel(key, flag.get()));
            ((SiegeButton)b).setSelected(flag.get());
        }, ACCENT);
        return button.setSelected(flag.get());
    }

    private void adjustVolume(int amount) {
        SiegeConfig.musicVolume = SiegeConfig.clampVolume(SiegeConfig.musicVolume + amount);
        SiegeConfig.save();
        SiegeMusic.refreshVolume();
        rebuildWidgets();
    }

    private Component volumeLabel() {
        return Component.literal(label("MÚSICA -10%", "MUSIC -10%"));
    }

    private Component volumeUpLabel() {
        return Component.literal(label("MÚSICA +10%", "MUSIC +10%"));
    }

    private Component graphicsLabel() {
        return Component.translatable("siege.settings.graphics").append(": ")
                .append(Component.translatable("siege.settings.graphics." + SiegeConfig.graphics.name().toLowerCase()));
    }

    private Component toggleLabel(String key, boolean enabled) {
        return Component.translatable(key).append(": ")
                .append(Component.translatable(enabled ? "siege.common.on" : "siege.common.off"));
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private String label(String es, String en) { return spanish() ? es : en; }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, 0xA3070A0D);

        int panelTop = Math.max(34, gridY - 14);
        int panelBottom = Math.min(height - 24, gridY + (cellHeight + rowGap) * 4 + 31);
        g.fill(gridX - 8, panelTop, gridX + gridWidth + 8, panelBottom, 0xE50B1015);
        g.fill(gridX - 8, panelTop, gridX + gridWidth + 8, panelTop + 2, ACCENT);
        g.fill(gridX - 8, panelBottom - 1, gridX + gridWidth + 8, panelBottom, 0xFF29343D);

        g.drawCenteredString(font, title, width / 2, 13, 0xFFF0EEE8);
        g.drawCenteredString(font, label("CONTROL DE INTERFAZ // CLIENTE", "INTERFACE CONTROL // CLIENT"), width / 2,
                height < 300 ? 31 : 39, 0xFF778690);

        int statusY = gridY + (cellHeight + rowGap) * 4 + 5;
        if (statusY < height - 24) {
            String state = label("VOLUMEN", "VOLUME") + " " + SiegeConfig.musicVolume + "%  //  "
                    + label("PISTA", "TRACK") + " " + SiegeMusic.currentTrackName();
            g.drawCenteredString(font, font.plainSubstrByWidth(state, Math.max(120, gridWidth - 18)), width / 2, statusY, 0xFF9DA7AE);
        }

        if (height >= 250) {
            String rule = label("La música se reproduce solo fuera del gameplay.", "Music plays only outside gameplay.");
            g.drawCenteredString(font, rule, width / 2, height - 14, 0xFF69747C);
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    @Override
    public void onClose() {
        SiegeUiSounds.back();
        SiegeConfig.save();
        minecraft.setScreen(parent);
    }

    @Override public boolean isPauseScreen() { return false; }
    private interface Flag { boolean get(); }
}

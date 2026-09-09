package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SiegeSettingsScreen extends Screen {
    private static final int ACCENT = 0xFF55BFD9;
    private static final int WARNING = 0xFFD65A4B;

    private final Screen parent;
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelBottom;
    private boolean compact;

    public SiegeSettingsScreen(Screen parent) {
        super(Component.translatable("siege.settings.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        compact = width < 620 || height < 340;
        int margin = compact ? 7 : 14;
        panelWidth = Math.min(compact ? 420 : 650, width - margin * 2);
        panelWidth = Math.max(220, panelWidth);
        panelX = (width - panelWidth) / 2;
        panelY = compact ? 39 : 55;

        addRenderableWidget(new SiegeButton(8, 7, Math.min(82, Math.max(62, width / 6)), 19,
                Component.translatable("siege.common.back"), b -> onClose(), WARNING));

        if (compact) initCompactControls(); else initWideControls();
    }

    private void initWideControls() {
        int pad = 13;
        int gap = 8;
        int columnWidth = (panelWidth - pad * 2 - gap) / 2;
        int left = panelX + pad;
        int right = left + columnWidth + gap;
        int y = panelY + 33;
        int h = 24;
        int rowGap = 7;

        addRenderableWidget(toggle(left, y, columnWidth, h, "siege.settings.music", () -> {
            SiegeConfig.music = !SiegeConfig.music;
            if (SiegeConfig.music) SiegeMusic.ensurePlaying(); else SiegeMusic.stop();
        }, () -> SiegeConfig.music));

        addRenderableWidget(new SiegeSlider(left, y += h + rowGap, columnWidth, 30,
                Component.literal(label("VOLUMEN DE MÚSICA", "MUSIC VOLUME")), SiegeConfig.musicVolume,
                value -> SiegeMusic.setVolumeLive(value)));

        addRenderableWidget(new SiegeButton(left, y += 30 + rowGap, columnWidth, h,
                Component.translatable("siege.menu.next_track"), b -> {
                    SiegeUiSounds.nextTrack();
                    SiegeMusic.nextTrack();
                }, 0xFFD6A94B));

        int rightY = panelY + 33;
        addRenderableWidget(toggle(right, rightY, columnWidth, h, "siege.settings.ui_sounds",
                () -> SiegeConfig.uiSounds = !SiegeConfig.uiSounds, () -> SiegeConfig.uiSounds));
        addRenderableWidget(toggle(right, rightY += h + rowGap, columnWidth, h, "siege.settings.backgrounds",
                () -> SiegeConfig.animatedBackgrounds = !SiegeConfig.animatedBackgrounds, () -> SiegeConfig.animatedBackgrounds));
        addRenderableWidget(toggle(right, rightY += h + rowGap, columnWidth, h, "siege.settings.reduced_motion",
                () -> SiegeConfig.reducedMotion = !SiegeConfig.reducedMotion, () -> SiegeConfig.reducedMotion));
        addRenderableWidget(graphicsButton(right, rightY += h + rowGap, columnWidth, h));

        panelBottom = Math.max(y + h + 42, rightY + h + 42);
    }

    private void initCompactControls() {
        int pad = 8;
        int x = panelX + pad;
        int w = panelWidth - pad * 2;
        int y = panelY + 27;
        int h = 18;
        int gap = 3;

        addRenderableWidget(toggle(x, y, w, h, "siege.settings.music", () -> {
            SiegeConfig.music = !SiegeConfig.music;
            if (SiegeConfig.music) SiegeMusic.ensurePlaying(); else SiegeMusic.stop();
        }, () -> SiegeConfig.music));

        addRenderableWidget(new SiegeSlider(x, y += h + gap, w, 24,
                Component.literal(label("VOLUMEN", "VOLUME")), SiegeConfig.musicVolume,
                SiegeMusic::setVolumeLive));
        y += 24 + gap;

        addRenderableWidget(new SiegeButton(x, y, w, h, Component.translatable("siege.menu.next_track"), b -> {
            SiegeUiSounds.nextTrack();
            SiegeMusic.nextTrack();
        }, 0xFFD6A94B));
        addRenderableWidget(toggle(x, y += h + gap, w, h, "siege.settings.ui_sounds",
                () -> SiegeConfig.uiSounds = !SiegeConfig.uiSounds, () -> SiegeConfig.uiSounds));
        addRenderableWidget(toggle(x, y += h + gap, w, h, "siege.settings.backgrounds",
                () -> SiegeConfig.animatedBackgrounds = !SiegeConfig.animatedBackgrounds, () -> SiegeConfig.animatedBackgrounds));
        addRenderableWidget(toggle(x, y += h + gap, w, h, "siege.settings.reduced_motion",
                () -> SiegeConfig.reducedMotion = !SiegeConfig.reducedMotion, () -> SiegeConfig.reducedMotion));
        addRenderableWidget(graphicsButton(x, y += h + gap, w, h));
        panelBottom = y + h + 31;
    }

    private SiegeButton graphicsButton(int x, int y, int w, int h) {
        return new SiegeButton(x, y, w, h, graphicsLabel(), b -> {
            SiegeUiSounds.click();
            SiegeConfig.graphics = SiegeConfig.graphics.next();
            b.setMessage(graphicsLabel());
            SiegeConfig.save();
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

    private Component graphicsLabel() {
        return Component.translatable("siege.settings.graphics").append(": ")
                .append(Component.translatable("siege.settings.graphics." + SiegeConfig.graphics.name().toLowerCase()));
    }

    private Component toggleLabel(String key, boolean enabled) {
        return Component.translatable(key).append(": ")
                .append(Component.translatable(enabled ? "siege.common.on" : "siege.common.off"));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, 0xA8070A0D);

        int bottom = Math.min(height - 19, panelBottom);
        g.fill(panelX - 7, panelY - 8, panelX + panelWidth + 7, bottom, 0xF00B1015);
        g.fill(panelX - 7, panelY - 8, panelX + panelWidth + 7, panelY - 5, ACCENT);
        g.fill(panelX - 7, bottom - 1, panelX + panelWidth + 7, bottom, 0xFF2A353D);

        g.drawCenteredString(font, label("CONFIGURACIÓN SIEGE", "SIEGE SETTINGS"), width / 2, 11, 0xFFF0EEE8);
        g.drawCenteredString(font, label("CONTROL DE INTERFAZ // CLIENTE", "INTERFACE CONTROL // CLIENT"), width / 2,
                compact ? 26 : 31, 0xFF7B8790);

        if (!compact) {
            int titleY = panelY + 8;
            int leftCenter = panelX + panelWidth / 4;
            int rightCenter = panelX + panelWidth * 3 / 4;
            g.drawCenteredString(font, "AUDIO // MENU", leftCenter, titleY, ACCENT);
            g.drawCenteredString(font, label("INTERFAZ // GRÁFICOS", "INTERFACE // GRAPHICS"), rightCenter, titleY, ACCENT);
            g.fill(panelX + panelWidth / 2, panelY + 25, panelX + panelWidth / 2 + 1, bottom - 31, 0x6637444D);
        } else {
            g.drawCenteredString(font, "AUDIO // UI // GRAPHICS", width / 2, panelY + 7, ACCENT);
        }

        int statusY = bottom - 24;
        String status = label("PISTA", "TRACK") + "  " + SiegeMusic.currentTrackName() + "  //  "
                + SiegeConfig.musicVolume + "%  //  " + SiegeMusic.transitionLabel(spanish());
        g.drawCenteredString(font, font.plainSubstrByWidth(status, Math.max(120, panelWidth - 22)), width / 2, statusY, 0xFFA7B0B6);

        if (height >= 235) {
            String rule = label(
                    "La música termina cada pista antes de continuar. No se reproduce en mundos, servidores ni pausa.",
                    "Each track finishes before the next. Music never plays in worlds, servers or pause screens.");
            g.drawCenteredString(font, font.plainSubstrByWidth(rule, Math.max(120, width - 24)), width / 2, height - 13, 0xFF6E7981);
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private String label(String es, String en) {
        return spanish() ? es : en;
    }

    @Override
    public void onClose() {
        SiegeUiSounds.back();
        SiegeConfig.save();
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private interface Flag { boolean get(); }
}

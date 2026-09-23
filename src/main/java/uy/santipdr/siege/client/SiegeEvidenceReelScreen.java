package uy.santipdr.siege.client;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import uy.santipdr.siege.SiegeMod;

/**
 * Forge-native viewer for the Third Justice test footage.
 *
 * When the complete source video was available during the build, SIEGE plays every
 * prepared frame across the full source duration. No dark cinematic veil is drawn
 * over the footage. If the original MP4 was unavailable to the build, the repaired
 * three-frame historical reel remains as an explicit fallback instead of pretending
 * a complete video exists.
 */
public final class SiegeEvidenceReelScreen extends Screen {
    private final Screen parent;
    private final List<SiegeGuideData.Art> fallbackFrames;
    private final SiegeThirdJusticeVideo.Spec video = SiegeThirdJusticeVideo.spec();
    private int index;
    private boolean playing;
    private long changedAt;
    private SiegeButton play;

    public SiegeEvidenceReelScreen(Screen parent, List<SiegeGuideData.Art> supplied) {
        super(Component.literal("SIEGE // THIRD JUSTICE VIDEO"));
        this.parent = parent;
        List<SiegeGuideData.Art> reel = supplied == null ? List.of() : supplied.stream()
                .filter(art -> art.file().startsWith("third_justice_reel_"))
                .toList();
        this.fallbackFrames = reel.size() >= 2 ? reel : supplied == null ? List.of() : List.copyOf(supplied);
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private String label(String es, String en) { return spanish() ? es : en; }
    private boolean fullVideo() { return video.full() && video.frames() > 0; }
    private int frameCount() { return fullVideo() ? video.frames() : fallbackFrames.size(); }
    private long frameMs() { return fullVideo() ? Math.max(1L, Math.round(1000.0 / video.fps())) : 1100L; }

    @Override
    protected void init() {
        changedAt = System.currentTimeMillis();
        // Reduced Motion starts paused. A deliberate PLAY click is an explicit opt-in.
        playing = !SiegeConfig.reducedMotion && frameCount() > 1;
        int y = height - 27;
        int w = Math.min(96, Math.max(62, (width - 34) / 4));
        int total = w * 4 + 12;
        int x = Math.max(8, (width - total) / 2);
        addRenderableWidget(new SiegeButton(x, y, w, 19, Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .setCompactCenter(true).withIcon("back"));
        SiegeButton prev = addRenderableWidget(new SiegeButton(x + w + 4, y, w, 19, Component.literal("←"), b -> step(-1), SiegeTheme.GOLD)
                .setCompactCenter(true));
        play = addRenderableWidget(new SiegeButton(x + (w + 4) * 2, y, w, 19, Component.literal(playLabel()), b -> toggle(), SiegeTheme.CYAN)
                .setCompactCenter(true));
        SiegeButton next = addRenderableWidget(new SiegeButton(x + (w + 4) * 3, y, w, 19, Component.literal("→"), b -> step(1), SiegeTheme.GOLD)
                .setCompactCenter(true));
        prev.setTooltip(Tooltip.create(Component.literal(label("Retroceder", "Step back"))));
        next.setTooltip(Tooltip.create(Component.literal(label("Avanzar", "Step forward"))));
        play.setTooltip(Tooltip.create(Component.literal(fullVideo()
                ? label("Reproduce el test completo preparado desde el video original.",
                        "Plays the complete test prepared from the original video.")
                : label("El video original no estaba disponible en este build; se muestran los registros recuperados.",
                        "The original video was unavailable to this build; recovered records are shown."))));
    }

    private String playLabel() { return playing ? label("PAUSA", "PAUSE") : label("REPRODUCIR", "PLAY"); }

    private void toggle() {
        if (frameCount() < 2) return;
        if (!playing && fullVideo() && index >= frameCount() - 1) index = 0;
        playing = !playing;
        changedAt = System.currentTimeMillis();
        play.setMessage(Component.literal(playLabel()));
        SiegeUiSounds.click();
    }

    private void step(int direction) {
        int count = frameCount();
        if (count <= 0) return;
        index = Math.floorMod(index + direction, count);
        changedAt = System.currentTimeMillis();
        SiegeUiSounds.selection();
    }

    private ResourceLocation textureFor(int frameIndex) {
        if (fullVideo()) {
            return new ResourceLocation(SiegeMod.MOD_ID,
                    "textures/gui/guide/third_justice_video/frame_" + String.format("%05d", frameIndex + 1) + ".png");
        }
        SiegeGuideData.Art art = fallbackFrames.get(Math.floorMod(frameIndex, fallbackFrames.size()));
        return new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/guide/" + art.file());
    }

    private int sourceWidth() {
        if (fullVideo()) return video.width();
        return fallbackFrames.isEmpty() ? 640 : fallbackFrames.get(Math.floorMod(index, fallbackFrames.size())).width();
    }

    private int sourceHeight() {
        if (fullVideo()) return video.height();
        return fallbackFrames.isEmpty() ? 360 : fallbackFrames.get(Math.floorMod(index, fallbackFrames.size())).height();
    }

    private long currentTimeMs() {
        if (frameCount() <= 1) return 0L;
        if (fullVideo()) return Math.min(video.durationMs(), Math.round(index * 1000.0 / video.fps()));
        return index * frameMs();
    }

    private String clock(long ms) {
        long totalSeconds = Math.max(0L, ms / 1000L);
        return String.format("%d:%02d", totalSeconds / 60L, totalSeconds % 60L);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        // Neutral surround only. No darkness/filter overlay is composited over evidence.
        g.fill(0, 0, width, height, 0xFF111315);

        int count = frameCount();
        if (playing && count > 1) {
            long now = System.currentTimeMillis();
            long interval = frameMs();
            if (now - changedAt >= interval) {
                long elapsed = now - changedAt;
                int advance = Math.max(1, (int)(elapsed / interval));
                if (fullVideo()) {
                    index += advance;
                    if (index >= count) {
                        index = count - 1;
                        playing = false;
                        if (play != null) play.setMessage(Component.literal(playLabel()));
                    }
                } else {
                    index = Math.floorMod(index + advance, count);
                }
                changedAt += (elapsed / interval) * interval;
            }
        }

        int panelX = Math.max(8, width / 18);
        int panelY = 32;
        int panelW = Math.max(1, width - panelX * 2);
        int panelH = Math.max(1, height - panelY - 66);
        g.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xFF090B0D);
        SiegeTheme.frame(g, panelX, panelY, panelW, panelH, SiegeTheme.CYAN);
        g.drawCenteredString(font,
                label("THIRD JUSTICE // VIDEO DE PRUEBA", "THIRD JUSTICE // TEST VIDEO"),
                width / 2, 11, SiegeTheme.INK);

        if (count <= 0) {
            g.drawCenteredString(font, label("SIN VIDEO DISPONIBLE", "NO VIDEO AVAILABLE"), width / 2,
                    panelY + panelH / 2, SiegeTheme.MUTED);
        } else {
            int footer = 25;
            var box = SiegeGuideLayout.fit(new SiegeGuideLayout.Rect(panelX + 8, panelY + 8,
                    Math.max(1, panelW - 16), Math.max(1, panelH - footer - 10)), sourceWidth(), sourceHeight());
            g.blit(textureFor(index), box.x(), box.y(), box.w(), box.h(), 0, 0,
                    sourceWidth(), sourceHeight(), sourceWidth(), sourceHeight());
            SiegeTheme.frame(g, box.x(), box.y(), box.w(), box.h(), SiegeTheme.CYAN);

            long duration = fullVideo() ? video.durationMs() : Math.max(frameMs(), count * frameMs());
            String status = fullVideo()
                    ? clock(currentTimeMs()) + " / " + clock(duration)
                    : label("REGISTRO ", "FRAME ") + (index + 1) + "/" + count;
            g.drawString(font, status, panelX + 9, panelY + panelH - 17, SiegeTheme.CYAN, false);

            int barX = panelX + Math.min(92, Math.max(58, font.width(status) + 16));
            int barW = Math.max(18, panelW - (barX - panelX) - 10);
            int barY = panelY + panelH - 14;
            g.fill(barX, barY, barX + barW, barY + 3, 0xFF2B3035);
            float progress = count <= 1 ? 0.0F : index / (float)(count - 1);
            g.fill(barX, barY, barX + Math.round(barW * progress), barY + 3, SiegeTheme.CYAN);
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    @Override public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

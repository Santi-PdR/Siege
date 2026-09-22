package uy.santipdr.siege.client;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import uy.santipdr.siege.SiegeMod;

/**
 * Lightweight evidence-reel viewer for supplied reference footage.
 * Minecraft/Forge 1.20.1 does not ship an MP4 playback surface, so SIEGE stores a
 * curated sequence of lossless frames instead of bundling a video decoder and a
 * large H.264 file into the client JAR.
 */
public final class SiegeEvidenceReelScreen extends Screen {
    private static final long FRAME_MS = 1_100L;
    private final Screen parent;
    private final List<SiegeGuideData.Art> frames;
    private int index;
    private boolean playing;
    private long changedAt;
    private SiegeButton play;

    public SiegeEvidenceReelScreen(Screen parent, List<SiegeGuideData.Art> supplied) {
        super(Component.literal("SIEGE // EVIDENCE REEL"));
        this.parent = parent;
        List<SiegeGuideData.Art> reel = supplied == null ? List.of() : supplied.stream()
                .filter(art -> art.file().startsWith("third_justice_reel_"))
                .toList();
        this.frames = reel.size() >= 2 ? reel : supplied == null ? List.of() : List.copyOf(supplied);
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private String label(String es, String en) { return spanish() ? es : en; }

    @Override
    protected void init() {
        changedAt = System.currentTimeMillis();
        playing = !SiegeConfig.reducedMotion && frames.size() > 1;
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
        prev.setTooltip(Tooltip.create(Component.literal(label("Fotograma anterior", "Previous frame"))));
        next.setTooltip(Tooltip.create(Component.literal(label("Fotograma siguiente", "Next frame"))));
        play.setTooltip(Tooltip.create(Component.literal(label(
                "La reproducción usa fotogramas recuperados; no decodifica MP4 en tiempo real.",
                "Playback uses recovered still frames; it does not decode MP4 in real time."))));
    }

    private String playLabel() { return playing ? label("PAUSA", "PAUSE") : label("REPRODUCIR", "PLAY"); }

    private void toggle() {
        if (frames.size() < 2) return;
        playing = !playing;
        changedAt = System.currentTimeMillis();
        play.setMessage(Component.literal(playLabel()));
        SiegeUiSounds.click();
    }

    private void step(int direction) {
        if (frames.isEmpty()) return;
        index = Math.floorMod(index + direction, frames.size());
        changedAt = System.currentTimeMillis();
        SiegeUiSounds.selection();
    }

    private ResourceLocation texture(SiegeGuideData.Art art) {
        return new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/guide/" + art.file());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, 0xE20A0C0F);

        if (playing && !SiegeConfig.reducedMotion && frames.size() > 1) {
            long now = System.currentTimeMillis();
            if (now - changedAt >= FRAME_MS) {
                long elapsed = now - changedAt;
                index = Math.floorMod(index + (int)(elapsed / FRAME_MS), frames.size());
                changedAt += (elapsed / FRAME_MS) * FRAME_MS;
            }
        }

        int panelX = Math.max(8, width / 12);
        int panelY = 34;
        int panelW = Math.max(1, width - panelX * 2);
        int panelH = Math.max(1, height - panelY - 68);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, SiegeTheme.CYAN);
        g.drawCenteredString(font, label("THIRD JUSTICE // REGISTRO DE EVIDENCIA", "THIRD JUSTICE // EVIDENCE RECORD"),
                width / 2, 12, SiegeTheme.INK);

        if (frames.isEmpty()) {
            g.drawCenteredString(font, label("SIN FOTOGRAMAS RECUPERADOS", "NO RECOVERED FRAMES"), width / 2,
                    panelY + panelH / 2, SiegeTheme.MUTED);
        } else {
            SiegeGuideData.Art art = frames.get(Math.floorMod(index, frames.size()));
            var box = SiegeGuideLayout.fit(new SiegeGuideLayout.Rect(panelX + 10, panelY + 10,
                    Math.max(1, panelW - 20), Math.max(1, panelH - 34)), art.width(), art.height());
            g.blit(texture(art), box.x(), box.y(), box.w(), box.h(), 0, 0,
                    art.width(), art.height(), art.width(), art.height());
            SiegeTheme.frame(g, box.x(), box.y(), box.w(), box.h(), SiegeTheme.CYAN);
            String frame = label("REGISTRO ", "FRAME ") + (index + 1) + "/" + frames.size();
            g.drawString(font, frame, panelX + 10, panelY + panelH - 16, SiegeTheme.CYAN, false);
            String caption = font.plainSubstrByWidth(art.caption(spanish()), Math.max(1, panelW - 110));
            g.drawString(font, caption, panelX + panelW - 10 - font.width(caption), panelY + panelH - 16, SiegeTheme.MUTED, false);
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    @Override public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;

import java.util.List;

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
        int margin = compact ? 9 : Math.max(14, width / 55);
        menuWidth = Math.min(compact ? 184 : 226, Math.max(138, width / (compact ? 2 : 5)));
        menuWidth = Math.min(menuWidth, width - margin * 2);
        int buttonHeight = compact ? 18 : 22;
        int gap = compact ? 3 : 5;
        int totalHeight = buttonHeight * 5 + gap * 4;
        menuX = margin;

        int desiredTop = compact ? 72 : 112;
        menuTop = Math.max(desiredTop, (height - totalHeight) / 2 + (compact ? 10 : 20));
        menuTop = Math.min(menuTop, Math.max(50, height - totalHeight - 14));
        menuBottom = menuTop + totalHeight;

        int y = menuTop;
        addRenderableWidget(command(menuX, y, menuWidth, buttonHeight, "siege.menu.deployment",
                b -> minecraft.setScreen(new JoinMultiplayerScreen(this))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.intel",
                b -> minecraft.setScreen(new IntelScreenV2(this))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.armory",
                b -> minecraft.setScreen(new OptionsScreen(this, minecraft.options))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.settings",
                b -> minecraft.setScreen(new SiegeSettingsScreen(this))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.quit",
                b -> minecraft.stop()));

        int trackWidth = Math.min(126, Math.max(86, width / 9));
        addRenderableWidget(new SiegeButton(width - trackWidth - 9, 9, trackWidth, compact ? 18 : 20,
                Component.translatable("siege.menu.next_track"), b -> {
                    SiegeUiSounds.nextTrack();
                    SiegeMusic.nextTrack();
                }, 0xFFD64B4B));
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
        int panelRight = Math.min(width, menuX + menuWidth + (compact ? 18 : 30));
        graphics.fill(0, 0, panelRight, height, 0xBA090C10);
        graphics.fill(panelRight - 2, 0, panelRight, height, 0xAA55BFD9);
        graphics.fill(0, 0, width, 2, 0xAA1F262D);

        for (int y = 20; y < height; y += 32) graphics.fill(0, y, panelRight, y + 1, 0x181C9AB0);

        renderTitle(graphics, compact, panelRight);

        if (height >= 215) {
            int infoY = Math.min(height - 26, menuBottom + 11);
            if (infoY > menuBottom + 3) {
                graphics.drawString(font, "// MENU COMMAND LINK", menuX, infoY, 0xFF77818A, false);
            }
        }

        renderIntelPreview(graphics, panelRight);

        if (width >= 430) {
            graphics.drawString(font, "REC", width - 46, 36, 0xFFFF5555, false);
            String music = "AUDIO " + SiegeConfig.musicVolume + "% // " + SiegeMusic.currentTrackName();
            int musicWidth = Math.min(width / 3, 290);
            graphics.drawString(font, font.plainSubstrByWidth(music, Math.max(80, musicWidth)), width - musicWidth - 10,
                    height - 14, 0xFF929AA1, false);
        }
        if (width >= 610) {
            graphics.drawString(font, "BUILD 0.6.0 // SECURE CHANNEL", 10, height - 14, 0xFF747D84, false);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    private void renderTitle(GuiGraphics g, boolean compact, int panelRight) {
        int titleY = compact ? 20 : 29;
        g.pose().pushPose();
        g.pose().translate(menuX, titleY, 0.0F);
        float craftScale = compact ? 1.05F : 1.28F;
        g.pose().scale(craftScale, craftScale, 1.0F);
        g.drawString(font, "ETERNAL CRAFT", 0, 0, 0xFFF1EEE7, false);
        g.pose().popPose();

        g.pose().pushPose();
        g.pose().translate(menuX, titleY + (compact ? 15 : 20), 0.0F);
        float siegeScale = compact ? 1.38F : 1.78F;
        g.pose().scale(siegeScale, siegeScale, 1.0F);
        g.drawString(font, "S I E G E", 0, 0, 0xFFFF5555, false);
        g.pose().popPose();

        int lineY = titleY + (compact ? 34 : 45);
        g.fill(menuX, lineY, Math.min(panelRight - 10, menuX + menuWidth), lineY + 2, 0xAA55BFD9);
        if (!compact && height >= 330) {
            g.drawString(font, label("PROTOCOLO DE GUERRA // 2044", "WAR PROTOCOL // 2044"), menuX, lineY + 7, 0xFF69767E, false);
        }
    }

    /**
     * Reduced Intel feed: Units + Advanced only. It disappears automatically when
     * the logical screen is too small, which keeps GUI scale 4 and small windows clean.
     */
    private void renderIntelPreview(GuiGraphics g, int leftPanelRight) {
        if (width < 720 || height < 350) return;
        List<IntelEntry> entries = IntelCatalog.previewable();
        if (entries.isEmpty()) return;

        int previewWidth = Math.min(300, Math.max(230, width / 5));
        int previewHeight = 112;
        int x = width - previewWidth - 16;
        int y = Math.max(52, height - previewHeight - 34);
        if (x <= leftPanelRight + 18) return;

        int index = (int)((System.currentTimeMillis() / 7000L) % entries.size());
        IntelEntry entry = entries.get(index);
        IntelEntry.IntelText text = entry.text(spanish());
        int accent = entry.category().equals("ADVANCED") ? 0xFF2F80FF : 0xFFD94A4A;

        g.fill(x + 3, y + 3, x + previewWidth + 3, y + previewHeight + 3, 0x60000000);
        g.fill(x, y, x + previewWidth, y + previewHeight, 0xE90A0F13);
        g.fill(x, y, x + previewWidth, y + 2, accent);
        g.fill(x, y, x + 2, y + previewHeight, accent);
        g.fill(x + 8, y + 26, x + previewWidth - 8, y + 27, 0xFF28343C);

        String header = label("INTEL DE CAMPO // RESUMEN", "FIELD INTEL // SUMMARY");
        g.drawString(font, header, x + 9, y + 8, 0xFF9CA8AF, false);
        String counter = String.format("%02d/%02d", index + 1, entries.size());
        g.drawString(font, counter, x + previewWidth - 9 - font.width(counter), y + 8, 0xFF68747C, false);

        String categoryText = entry.category().equals("ADVANCED") ? label("AVANZADO", "ADVANCED") : label("UNIDAD", "UNIT");
        g.drawString(font, entry.name(), x + 9, y + 33, 0xFFF1EEE8, false);
        String meta = entry.code() + " // " + categoryText + " // " + label("AMENAZA ", "THREAT ") + entry.threat() + "/5 // HP " + entry.hp();
        g.drawString(font, font.plainSubstrByWidth(meta, previewWidth - 18), x + 9, y + 46, accent, false);

        int textY = y + 61;
        int linesDrawn = 0;
        for (FormattedCharSequence line : font.split(Component.literal(text.description()), previewWidth - 18)) {
            if (linesDrawn >= 2) break;
            g.drawString(font, line, x + 9, textY + linesDrawn * 11, 0xFFC2C8CC, false);
            linesDrawn++;
        }

        String footer = label("> MÁS INFORMACIÓN: ABRIR INTEL", "> MORE INFORMATION: OPEN INTEL");
        g.drawString(font, font.plainSubstrByWidth(footer, previewWidth - 18), x + 9, y + previewHeight - 15, 0xFF7FC7D9, false);
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private String label(String es, String en) {
        return spanish() ? es : en;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_S && Screen.hasControlDown()) {
            minecraft.setScreen(new SelectWorldScreen(this));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

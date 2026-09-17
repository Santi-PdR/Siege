package uy.santipdr.siege.client;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import uy.santipdr.siege.SiegeMod;

/** Aspect-correct viewer of supplied originals; never crops or synthesizes artwork. */
public final class SiegeGuideImageScreen extends Screen {
    private final Screen parent;
    private final List<SiegeGuideData.Art> images;
    private int index;
    private ResourceLocation texture;
    public SiegeGuideImageScreen(Screen parent, List<SiegeGuideData.Art> images, int index) {
        super(Component.literal("SIEGE"));
        this.parent = parent; this.images = List.copyOf(images);
        this.index = Math.floorMod(index, images.size()); updateTexture();
    }
    private boolean es() { return minecraft.getLanguageManager().getSelected().startsWith("es_"); }
    private void updateTexture() { texture = new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/guide/" + images.get(index).file()); }
    @Override protected void init() {
        SiegeUiSounds.resetHover();
        int buttonW = Math.min(100, (width - 32) / 3);
        addRenderableWidget(new SiegeButton(8, height - 27, buttonW, 19, Component.literal(es() ? "VOLVER" : "BACK"), b -> onClose(), SiegeTheme.RED).setCompactCenter(true));
        for (int direction : new int[] {-1, 1}) {
            SiegeButton button = addRenderableWidget(new SiegeButton(width - 8 - (direction < 0 ? buttonW * 2 + 4 : buttonW), height - 27, buttonW, 19,
                    Component.literal(direction < 0 ? "←" : "→"), b -> {
                index = Math.floorMod(index + direction, images.size()); updateTexture(); SiegeUiSounds.click();
            }, SiegeTheme.GOLD).setCompactCenter(true));
            button.active = images.size() > 1;
            button.setTooltip(Tooltip.create(Component.literal(es() ? "Cambiar imagen del artículo" : "Change article image")));
        }
    }
    @Override public void render(GuiGraphics g, int mx, int my, float partialTick) {
        SiegeMusic.ensurePlaying(); g.fill(0, 0, width, height, 0xFF111012);
        var art = images.get(index);
        String title = (index + 1) + "/" + images.size() + " · " + art.caption(es());
        g.drawCenteredString(font, font.plainSubstrByWidth(title, width - 16), width / 2, 8, SiegeTheme.INK);
        var box = SiegeGuideLayout.fit(new SiegeGuideLayout.Rect(8, 26, Math.max(1, width - 16), Math.max(1, height - 62)), art.width(), art.height());
        g.blit(texture, box.x(), box.y(), box.w(), box.h(), 0, 0, art.width(), art.height(), art.width(), art.height());
        super.render(g, mx, my, partialTick); SiegeUiSounds.updateHover(children());
        if (my >= 5 && my < 21 && font.width(title) > width - 16) g.renderTooltip(font, Component.literal(title), mx, my);
    }
    @Override public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

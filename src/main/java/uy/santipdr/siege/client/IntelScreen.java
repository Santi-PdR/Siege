package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class IntelScreen extends Screen {
    private static final List<IntelEntry> FILES = List.of(
            new IntelEntry("HU-001", "INFANTRY", "HU", 1, "Standard infantry weapons", "Rifle / support variants",
                    "ACTIVE", "Baseline hostile infantry. Individually limited, dangerous in coordinated groups.",
                    "Break formations and deny cover."),
            new IntelEntry("HU-002", "SHIELDER", "HU", 2, "Ballistic shield + sidearm", "Assault escort",
                    "ACTIVE", "Armored frontline unit deployed to protect advancing squads.",
                    "Flank the shield. Avoid direct sustained fire."));

    private final Screen parent;
    private int selected;

    public IntelScreen(Screen parent) {
        super(Component.translatable("siege.intel.title"));
        this.parent = parent;
    }

    @Override protected void init() {
        addRenderableWidget(Button.builder(Component.literal("< ").append(Component.translatable("siege.intel.return")),
                b -> onClose()).bounds(18, 18, 126, 26).build());
        int y = 112;
        for (int i = 0; i < FILES.size(); i++) {
            int index = i;
            addRenderableWidget(Button.builder(Component.literal(FILES.get(i).code() + "  " + FILES.get(i).name()),
                    b -> selected = index).bounds(28, y, 320, 24).build());
            y += 28;
        }
    }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, 66, 0xEB050505);
        g.fill(0, 66, 368, height, 0xD927252A);
        g.fill(384, 80, width - 18, height - 28, 0xE6050505);
        g.drawCenteredString(font, title, width / 2, 22, 0xFFFF5555);
        g.drawString(font, "// FILTERS", 28, 82, 0xFF77777E, false);
        g.drawString(font, "[ ALL ]   [ HU ]   [ SOP ]   [ MECH ]   [ BOSS ]", 28, 96, 0xFFB8B8BE, false);
        IntelEntry e = FILES.get(selected);
        int x = 406, y = 100;
        int accent = e.threat() >= 4 ? 0xFFFFB020 : 0xFF56C8FF;
        g.drawString(font, "FILE: " + e.code() + " / " + e.name(), x, y, 0xFFF4F4F4, false);
        g.fill(x, y + 18, width - 42, y + 20, accent);
        g.drawString(font, "THREAT: " + stars(e.threat()) + "   CATEGORY: " + e.category(), x, y + 34, accent, false);
        g.drawString(font, "ARMAMENT: " + e.armament(), x, y + 52, 0xFFD0D0D0, false);
        g.drawString(font, "VARIANTS: " + e.variants(), x, y + 68, 0xFFD0D0D0, false);
        g.drawString(font, "STATUS: " + e.status(), x, y + 84, 0xFF63E083, false);
        drawWrapped(g, e.description(), x, y + 114, width - x - 42, 0xFFC8C8C8);
        drawWrapped(g, "WARNING: " + e.advisory(), x, y + 160, width - x - 42, 0xFFFFB020);
        g.drawString(font, "FILE " + String.format("%02d/%02d", selected + 1, FILES.size()), width - 105, 101, 0xFF777777, false);
        g.drawCenteredString(font, "// CLICK FILE // ESC TO RETURN", width / 2, height - 17, 0xFF66666B);
        super.render(g, mouseX, mouseY, partialTick);
    }

    private String stars(int count) { return "★".repeat(Math.max(0, count)) + "☆".repeat(Math.max(0, 5 - count)); }
    private void drawWrapped(GuiGraphics g, String value, int x, int y, int maxWidth, int color) {
        for (var line : font.split(Component.literal(value), maxWidth)) { g.drawString(font, line, x, y, color, false); y += 12; }
    }

    @Override public void onClose() { minecraft.setScreen(parent); }
}


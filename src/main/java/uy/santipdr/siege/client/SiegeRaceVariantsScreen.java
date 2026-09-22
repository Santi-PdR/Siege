package uy.santipdr.siege.client;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Small browser for races whose progression branches into named variants/evolutions. */
public final class SiegeRaceVariantsScreen extends Screen {
    private final Screen parent;
    private final String raceId;
    private int panelX, panelY, panelW, panelH;

    public SiegeRaceVariantsScreen(Screen parent, String raceId) {
        super(Component.literal("SIEGE // RACE PATHS"));
        this.parent = parent;
        this.raceId = raceId == null ? "" : raceId;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        int margin = width < 520 ? 8 : Math.max(14, width / 70);
        panelX = margin;
        panelY = 36;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - 24);

        addRenderableWidget(new SiegeButton(8, 7, 82, 19, Component.literal(label("VOLVER", "BACK")),
                b -> onClose(), SiegeTheme.RED).withIcon("back").setCompactCenter(true));

        List<String> ids = entries();
        int y = panelY + 58;
        int h = 22;
        int gap = 5;
        for (String id : ids) {
            SiegeKnowledgeData.Entry entry = SiegeKnowledgeRegistry.get(id);
            if (entry == null) continue;
            SiegeButton button = new SiegeButton(panelX + 12, y, panelW - 24, h,
                    Component.literal(entry.title(spanish())), b -> {
                SiegeUiSounds.confirm();
                minecraft.setScreen(new SiegeKnowledgeFileScreen(this, entry));
            }, SiegeTheme.GOLD).withIcon("intel");
            addRenderableWidget(button);
            y += h + gap;
        }
    }

    private List<String> entries() {
        return switch (raceId) {
            case "subhuman" -> List.of("subhuman-adamantium-human", "subhuman-sorcerer", "subhuman-evil-morty");
            case "saiyan" -> List.of("saiyan-transformations");
            case "ghoul" -> List.of("ghoul-progression");
            default -> List.of();
        };
    }

    private String title() {
        return switch (raceId) {
            case "subhuman" -> label("VARIANTES SUBHUMAN", "SUBHUMAN VARIANTS");
            case "saiyan" -> label("TRANSFORMACIONES SAIYAN", "SAIYAN TRANSFORMATIONS");
            case "ghoul" -> label("EVOLUCIONES GHOUL", "GHOUL EVOLUTIONS");
            default -> label("RUTAS DE RAZA", "RACE PATHS");
        };
    }

    private String description() {
        return switch (raceId) {
            case "subhuman" -> label(
                    "Subhuman es una familia. Cada variante tiene su propia ficha y no se mezcla con Human normal.",
                    "Subhuman is a family. Each variant has its own entry and is not mixed with normal Human.");
            case "saiyan" -> label(
                    "Saiyan progresa mediante entrenamiento y transformaciones conocidas, no con una única receta V1→V4.",
                    "Saiyan progresses through training and known transformations rather than one V1→V4 recipe.");
            case "ghoul" -> label(
                    "Ghoul combina progreso por carne con evoluciones separadas como V2 y Super Ghoul.",
                    "Ghoul combines meat-based progression with separate evolutions such as V2 and Super Ghoul.");
            default -> label("No hay rutas separadas conocidas para mostrar.", "No separate known paths to show.");
        };
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xC7000000 : 0x9B000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, SiegeTheme.GOLD);
        g.drawString(font, fit(title() + " // " + SiegeRuntimeStatus.version(), panelW - 24),
                panelX + 12, panelY + 10, SiegeTheme.INK, false);
        g.drawString(font, fit(description(), panelW - 24), panelX + 12, panelY + 25, SiegeTheme.MUTED, false);
        SiegeTheme.divider(g, panelX + 10, panelY + 46, panelW - 20, SiegeTheme.GOLD);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private String fit(String value, int pixels) {
        if (value == null || pixels <= 0) return "";
        if (font.width(value) <= pixels) return value;
        return font.plainSubstrByWidth(value, Math.max(1, pixels - font.width("…"))) + "…";
    }
    private boolean spanish() { return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_"); }
    private String label(String es, String en) { return spanish() ? es : en; }
    @Override public void onClose() { if (minecraft != null) minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

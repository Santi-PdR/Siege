package uy.santipdr.siege.client;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;
import uy.santipdr.siege.SiegeMod;

/**
 * Client configuration/diagnostic center. Gameplay knowledge intentionally lives
 * under Intel, never under Settings.
 */
public final class SiegeSystemScreen extends Screen {
    private final Screen parent;
    private SiegeButton contrastButton;
    private SiegeButton flashesButton;
    private int panelX, panelY, panelW, panelBottom;
    private boolean compact;

    public SiegeSystemScreen(Screen parent) {
        super(Component.literal("SIEGE // SYSTEM"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        compact = width < 620 || height < 350;
        int margin = compact ? 7 : 14;
        panelW = Math.max(260, Math.min(780, width - margin * 2));
        panelW = Math.min(panelW, Math.max(1, width - margin * 2));
        panelX = (width - panelW) / 2;
        panelY = compact ? 35 : 48;
        panelBottom = height - (compact ? 8 : 20);

        addRenderableWidget(new SiegeButton(8, 7, Math.min(86, Math.max(62, width / 6)), 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED));

        int controlsHeight = compact ? 69 : 79;
        int controlsTop = Math.max(panelY + 105, panelBottom - controlsHeight);
        int gap = 5;
        int rowH = compact ? 17 : 20;
        int innerX = panelX + 10;
        int innerW = panelW - 20;
        int half = (innerW - gap) / 2;

        contrastButton = addRenderableWidget(new SiegeButton(innerX, controlsTop, half, rowH,
                contrastLabel(), b -> {
                    SiegeConfig.highContrast = !SiegeConfig.highContrast;
                    SiegeConfig.save();
                    refreshButtons();
                    SiegeUiSounds.click();
                }, SiegeTheme.CYAN).withIcon("eye"));
        contrastButton.setTooltip(Tooltip.create(Component.literal(label(
                "Aumenta la separación visual y oscurece fondos detrás del texto.",
                "Increases visual separation and darkens backgrounds behind text."))));

        flashesButton = addRenderableWidget(new SiegeButton(innerX + half + gap, controlsTop, innerW - half - gap, rowH,
                flashesLabel(), b -> {
                    SiegeConfig.reduceFlashes = !SiegeConfig.reduceFlashes;
                    SiegeConfig.save();
                    refreshButtons();
                    SiegeUiSounds.click();
                }, SiegeTheme.GREEN).withIcon("eye"));
        flashesButton.setTooltip(Tooltip.create(Component.literal(label(
                "Desactiva interferencia, paneo y transiciones rápidas de SIEGE.",
                "Disables SIEGE interference, panning and rapid transitions."))));

        int row2 = controlsTop + rowH + gap;
        addRenderableWidget(new SiegeButton(innerX, row2, half, rowH,
                Component.literal(label("PERFIL TRANQUILO", "CALM PRESET")), b -> {
                    SiegeConfig.applyCalmPreset();
                    refreshButtons();
                    SiegeUiSounds.confirm();
                }, SiegeTheme.BLUE).withIcon("shield"));
        addRenderableWidget(new SiegeButton(innerX + half + gap, row2, innerW - half - gap, rowH,
                Component.literal(label("PERFIL DE LECTURA", "READING PRESET")), b -> {
                    SiegeConfig.applyReadingPreset();
                    refreshButtons();
                    SiegeUiSounds.confirm();
                }, SiegeTheme.GOLD).withIcon("intel"));

        int row3 = row2 + rowH + gap;
        SiegeButton minecraftOptions = addRenderableWidget(new SiegeButton(innerX, row3, innerW, rowH,
                Component.literal(label("AJUSTES DE MINECRAFT", "MINECRAFT OPTIONS")), b -> {
                    SiegeUiSounds.click();
                    minecraft.setScreen(new OptionsScreen(this, minecraft.options));
                }, SiegeTheme.ORANGE).withIcon("settings").setCompactCenter(true));
        minecraftOptions.setTooltip(Tooltip.create(Component.literal(label(
                "Abre las opciones nativas de Minecraft tematizadas por SIEGE.",
                "Opens Minecraft's native options themed by SIEGE."))));

        refreshButtons();
    }

    private void refreshButtons() {
        if (contrastButton != null) {
            contrastButton.setMessage(contrastLabel());
            contrastButton.setSelected(SiegeConfig.highContrast);
        }
        if (flashesButton != null) {
            flashesButton.setMessage(flashesLabel());
            flashesButton.setSelected(SiegeConfig.reduceFlashes);
        }
    }

    private Component contrastLabel() {
        return Component.literal(label("ALTO CONTRASTE: ", "HIGH CONTRAST: ")
                + label(SiegeConfig.highContrast ? "SÍ" : "NO", SiegeConfig.highContrast ? "ON" : "OFF"));
    }

    private Component flashesLabel() {
        return Component.literal(label("REDUCIR DESTELLOS: ", "REDUCE FLASHES: ")
                + label(SiegeConfig.reduceFlashes ? "SÍ" : "NO", SiegeConfig.reduceFlashes ? "ON" : "OFF"));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xC708090B : 0xA808090B);
        SiegeTheme.panel(g, panelX, panelY, panelW, Math.max(6, panelBottom - panelY), SiegeTheme.CYAN);

        int textX = panelX + 12;
        int innerW = panelW - 24;
        g.drawString(font, "SIEGE // 0.40 SYSTEM SETTINGS", textX, panelY + 10, SiegeTheme.CYAN, false);
        String subtitle = label("Configuración avanzada, compatibilidad y accesibilidad del cliente.",
                "Advanced client configuration, compatibility and accessibility.");
        g.drawString(font, font.plainSubstrByWidth(subtitle, innerW), textX, panelY + 24, SiegeTheme.MUTED, false);
        SiegeTheme.divider(g, textX, panelY + 38, innerW, SiegeTheme.CYAN);

        int cardsTop = panelY + 48;
        int gap = 6;
        int cardW = compact ? innerW : (innerW - gap) / 2;
        int cardH = compact ? 24 : 30;
        int x2 = textX + cardW + gap;

        card(g, textX, cardsTop, cardW, cardH, "BUILD", version(SiegeMod.MOD_ID), SiegeTheme.RED);
        if (!compact) card(g, x2, cardsTop, innerW - cardW - gap, cardH,
                "MINECRAFT / FORGE", SharedConstants.getCurrentVersion().getName() + " / " + version("forge"), SiegeTheme.ORANGE);

        int row2 = cardsTop + cardH + gap;
        card(g, textX, row2, cardW, cardH, label("RENDER", "RENDER"), renderBackend(), SiegeTheme.CYAN);
        if (!compact) card(g, x2, row2, innerW - cardW - gap, cardH,
                label("PERFIL GRÁFICO", "GRAPHICS PROFILE"), SiegeConfig.graphics.name(), SiegeTheme.GOLD);

        int stateY = compact ? row2 + cardH + 7 : row2 + cardH + 9;
        String config = SiegeConfig.lastSaveSucceeded ? label("CONFIG GUARDADA", "CONFIG SAVED")
                : label("ERROR AL GUARDAR CONFIG", "CONFIG SAVE ERROR");
        g.drawString(font, config, textX, stateY,
                SiegeConfig.lastSaveSucceeded ? SiegeTheme.GREEN : 0xFFFF8B91, false);

        int sceneY = stateY + 13;
        String scene = label("FONDO: ", "BACKGROUND: ")
                + SiegeBackgrounds.name(SiegeBackgrounds.currentIndex(System.currentTimeMillis()), spanish())
                + " · " + SiegeBackgrounds.rotationState(spanish(), System.currentTimeMillis());
        g.drawString(font, font.plainSubstrByWidth(scene, innerW), textX, sceneY, SiegeTheme.MUTED, false);

        int musicY = sceneY + 12;
        String music = label("AUDIO: ", "AUDIO: ") + (!SiegeConfig.music ? label("desactivado", "off")
                : SiegeMusic.currentTrackName() + " · " + SiegeConfig.musicVolume + "%");
        g.drawString(font, font.plainSubstrByWidth(music, innerW), textX, musicY, SiegeTheme.MUTED, false);

        int noteY = musicY + 16;
        if (noteY < panelBottom - 82) {
            g.fill(textX, noteY, panelX + panelW - 12, noteY + 1, 0xFF31373C);
            String note = label(
                    "La información de unidades, misiones, equipo y estados de muerte se consulta desde Intel, no desde Configuración.",
                    "Units, missions, equipment and death-state information is accessed from Intel, not Settings.");
            int y = noteY + 6;
            for (var line : font.split(Component.literal(note), Math.max(60, innerW))) {
                if (y + font.lineHeight >= panelBottom - 78) break;
                g.drawString(font, line, textX, y, SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK, false);
                y += font.lineHeight + 2;
            }
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    private void card(GuiGraphics g, int x, int y, int w, int h, String title, String value, int accent) {
        SiegeTheme.panel(g, x, y, Math.max(1, w), Math.max(6, h), accent);
        g.drawString(font, font.plainSubstrByWidth(title, Math.max(1, w - 10)), x + 5, y + 4, SiegeTheme.MUTED, false);
        if (h >= 28)
            g.drawString(font, font.plainSubstrByWidth(value, Math.max(1, w - 10)), x + 5, y + 17,
                    SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK, false);
        else
            g.drawString(font, font.plainSubstrByWidth(title + " · " + value, Math.max(1, w - 10)), x + 5, y + 13,
                    SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK, false);
    }

    private String renderBackend() {
        ModList mods = ModList.get();
        if (mods.isLoaded("embeddium")) return "Embeddium " + version("embeddium");
        if (mods.isLoaded("rubidium")) return "Rubidium " + version("rubidium");
        if (mods.isLoaded("sodium")) return "Sodium " + version("sodium");
        return label("Minecraft vanilla", "Vanilla Minecraft");
    }

    private String version(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(c -> c.getModInfo().getVersion().toString())
                .orElse(label("no detectado", "not detected"));
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
    public boolean isPauseScreen() { return false; }
}

package uy.santipdr.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

/**
 * Shared operational chrome for SIEGE surfaces. Screen content remains
 * authoritative while section, build, profile, scene and health share one grammar.
 */
public final class SiegeScreenChrome {
    private SiegeScreenChrome() { }

    public static void renderOverlay(Screen screen, GuiGraphics g) {
        if (screen == null) return;
        SiegeNavigationModel.Descriptor descriptor = SiegeNavigationModel.forClassName(screen.getClass().getName());
        boolean spanish = spanish();
        if (screen instanceof SiegeTitleScreen) {
            renderHomeTag(screen, g, descriptor, spanish);
            renderSceneTag(screen, g, spanish);
            SiegeCommandStrip.render(screen, g);
            return;
        }
        if (screen instanceof SiegeMultiplayerScreen) {
            renderDeploymentHeader(screen, g, descriptor, spanish);
            return;
        }
        renderSectionTag(screen, g, descriptor, spanish);
    }

    public static void renderHeader(GuiGraphics g, Screen screen, int x, int y, int width, String detail) {
        if (screen == null || width < 24) return;
        var font = Minecraft.getInstance().font;
        boolean spanish = spanish();
        SiegeNavigationModel.Descriptor descriptor = SiegeNavigationModel.forClassName(screen.getClass().getName());
        int accent = descriptor.accent();
        String left = "SIEGE // " + descriptor.label(spanish);
        String right = detail == null ? "" : detail;
        g.fill(x, y, x + width, y + 18, SiegeConfig.highContrast ? 0xF708090B : 0xE70B0D10);
        g.fill(x, y, x + Math.min(3, width), y + 18, accent);
        g.fill(x + 7, y + 16, x + width - 5, y + 17, 0xFF30373C);
        g.drawString(font, fit(left, Math.max(1, width - 18 - font.width(right))), x + 9, y + 4,
                SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK, false);
        if (!right.isBlank()) {
            String clipped = fit(right, Math.max(1, width / 2));
            g.drawString(font, clipped, x + width - 7 - font.width(clipped), y + 4, accent, false);
        }
    }

    private static void renderHomeTag(Screen screen, GuiGraphics g, SiegeNavigationModel.Descriptor descriptor, boolean spanish) {
        if (!SiegeConfig.showBuildLabel || screen.width < 250) return;
        var font = Minecraft.getInstance().font;
        SiegeClientProfile.Profile profile = SiegeRuntimeStatus.profile();
        String build = "BUILD " + SiegeRuntimeStatus.version() + " // " + SiegeClientProfile.shortLabel(profile, spanish);
        String state = SiegeRuntimeStatus.healthLabel(spanish);
        int accent = SiegeClientProfile.accent(profile);
        int w = Math.min(screen.width - 12, Math.max(152, font.width(build) + font.width(state) + 29));
        int h = 15;
        int x = 6;
        int y = screen.height - h - 3;
        g.fill(x, y, x + w, y + h, SiegeConfig.highContrast ? 0xFA08090A : 0xF00B0D10);
        g.fill(x, y, x + 3, y + h, accent);
        g.fill(x + 6, y + h - 2, x + w - 4, y + h - 1, 0xFF30373C);
        g.drawString(font, fit(build, Math.max(24, w - font.width(state) - 22)), x + 8, y + 3,
                SiegeConfig.highContrast ? 0xFFFFFFFF : 0xFFC3C9CD, false);
        g.drawString(font, state, x + w - 7 - font.width(state), y + 3, SiegeRuntimeStatus.healthAccent(), false);
    }

    private static void renderSceneTag(Screen screen, GuiGraphics g, boolean spanish) {
        if (screen.width < 520 || screen.height < 300) return;
        long now = System.currentTimeMillis();
        int index = SiegeBackgrounds.currentIndex(now);
        var font = Minecraft.getInstance().font;
        String kind = SiegeBackgrounds.sceneTag(index, spanish);
        String name = SiegeBackgrounds.name(index, spanish);
        long remaining = SiegeBackgrounds.rotationRemainingMs(now);
        String countdown = remaining < 0L ? "" : " · " + Math.max(0L, (remaining + 999L) / 1_000L) + "s";
        String text = kind + " // " + name + countdown;
        int accent = SiegeBackgrounds.isAnomaly(index) ? SiegeTheme.GOLD
                : SiegeBackgrounds.isFeatured(index) ? SiegeTheme.CYAN : SiegeTheme.MUTED;
        int w = Math.min(screen.width / 3, Math.max(128, font.width(text) + 14));
        int x = screen.width - w - 7;
        int y = 34;
        g.fill(x, y, x + w, y + 13, SiegeConfig.highContrast ? 0xF708090B : 0xD90B0D10);
        g.fill(x, y, x + 2, y + 13, accent);
        g.drawString(font, fit(text, w - 10), x + 6, y + 2,
                SiegeBackgrounds.isAnomaly(index) ? SiegeTheme.GOLD : SiegeTheme.MUTED, false);
    }

    private static void renderDeploymentHeader(Screen screen, GuiGraphics g, SiegeNavigationModel.Descriptor descriptor, boolean spanish) {
        if (screen.width < 180 || screen.height < 80) return;
        var font = Minecraft.getInstance().font;
        int x = Math.max(6, (screen.width - Math.min(620, screen.width - 12)) / 2);
        int w = Math.min(620, screen.width - x * 2);
        int y = 8;
        g.fill(x, y, x + w, y + 18, 0xF20B0D10);
        g.fill(x, y, x + 3, y + 18, descriptor.accent());
        String title = "SIEGE // " + descriptor.label(spanish) + " " + SiegeRuntimeStatus.version();
        String profile = SiegeClientProfile.shortLabel(SiegeRuntimeStatus.profile(), spanish);
        g.drawString(font, fit(title, Math.max(40, w - font.width(profile) - 30)), x + 10, y + 5, SiegeTheme.INK, false);
        g.drawString(font, profile, x + w - 9 - font.width(profile), y + 5,
                SiegeClientProfile.accent(SiegeRuntimeStatus.profile()), false);
    }

    private static void renderSectionTag(Screen screen, GuiGraphics g, SiegeNavigationModel.Descriptor descriptor, boolean spanish) {
        if (screen.width < 360 || screen.height < 250) return;
        if (screen instanceof IntelScreenV3 || screen instanceof IntelPortraitScreen) return;
        var font = Minecraft.getInstance().font;
        String text = descriptor.code() + " // " + SiegeRuntimeStatus.version();
        String health = SiegeRuntimeStatus.healthLabel(spanish);
        int w = Math.min(screen.width / 3, Math.max(122, font.width(text) + font.width(health) + 21));
        int h = 12;
        int x = screen.width - w - 5;
        int y = screen.height - h - 3;
        g.fill(x, y, x + w, y + h, 0xD80B0D10);
        g.fill(x, y, x + 2, y + h, descriptor.accent());
        g.drawString(font, fit(text, Math.max(30, w - font.width(health) - 16)), x + 6, y + 2, SiegeTheme.MUTED, false);
        g.drawString(font, health, x + w - 5 - font.width(health), y + 2, SiegeRuntimeStatus.healthAccent(), false);
    }

    private static String fit(String value, int width) {
        var font = Minecraft.getInstance().font;
        if (width <= 0) return "";
        if (font.width(value) <= width) return value;
        if (width <= font.width("…")) return font.plainSubstrByWidth(value, width);
        return font.plainSubstrByWidth(value, width - font.width("…")) + "…";
    }

    private static boolean spanish() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }
}

package uy.santipdr.siege.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

/** Compact operational strip shared by the title and settings surfaces. */
public final class SiegeCommandStrip {
    private SiegeCommandStrip() { }

    public static void render(Screen screen, GuiGraphics g) {
        boolean title = screen instanceof SiegeTitleScreen;
        boolean settings = screen instanceof SiegeSettingsScreen;
        if ((!title && !settings) || screen.width < (title ? 650 : 520) || screen.height < 260) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return;

        boolean spanish = minecraft.getLanguageManager().getSelected().startsWith("es_");
        SiegeClientProfile.Profile profile = SiegeRuntimeStatus.profile();
        int accent = SiegeClientProfile.accent(profile);
        int health = SiegeRuntimeStatus.healthAccent();
        var font = minecraft.font;

        int maxWidth = title ? Math.max(260, screen.width - 320) : Math.max(280, screen.width - 80);
        int width = Math.min(settings ? 520 : 430, maxWidth);
        int x = (screen.width - width) / 2;
        int y = screen.height - 17;
        int h = 13;

        String profileText;
        if (profile == SiegeClientProfile.Profile.CUSTOM) {
            SiegeClientProfile.Profile nearest = SiegeProfileMetrics.nearest();
            profileText = "CUSTOM→" + SiegeClientProfile.shortLabel(nearest, spanish)
                    + " " + SiegeProfileMetrics.fitPercent(nearest) + "%";
        } else {
            profileText = (spanish ? "PERFIL " : "PROFILE ") + SiegeClientProfile.label(profile, spanish);
        }
        String intelText = "INTEL " + IntelCatalog.total();
        String audioText = !SiegeConfig.music ? "AUDIO OFF" : "AUDIO " + SiegeConfig.musicVolume + "%";
        String healthText = SiegeRuntimeStatus.healthLabel(spanish) + " " + SiegeRuntimeStatus.readiness() + "%";

        g.pose().pushPose();
        g.pose().translate(0, 0, 448);
        g.fill(x, y, x + width, y + h, SiegeConfig.highContrast ? 0xF20A0B0D : 0xD90C0E10);
        g.fill(x, y, x + width, y + 1, 0xFF2E353A);
        g.fill(x, y, x + Math.min(70, width), y + 2, accent);
        g.fill(x + width - Math.min(52, width), y + h - 2, x + width, y + h, health);

        int leftX = x + 6;
        int rightWidth = font.width(healthText);
        int rightX = x + width - 6 - rightWidth;
        int centerSpace = Math.max(1, rightX - leftX - 10);
        String detail = profileText + "  ·  " + intelText
                + (width >= 390 ? "  ·  " + audioText : "");
        String left = font.plainSubstrByWidth(detail, centerSpace);
        g.drawString(font, left, leftX, y + 3, SiegeConfig.highContrast ? 0xFFFFFFFF : 0xFFC7CDD1, false);
        g.drawString(font, healthText, rightX, y + 3, health, false);
        g.pose().popPose();
    }
}

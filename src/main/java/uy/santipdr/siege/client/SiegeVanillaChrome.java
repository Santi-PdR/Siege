package uy.santipdr.siege.client;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

/**
 * SIEGE shell for vanilla title-menu screens. It only draws around native
 * controls; validation, option values, lists and vanilla navigation remain the
 * original Minecraft implementations.
 */
public final class SiegeVanillaChrome {
    private SiegeVanillaChrome() { }

    public static SiegeMenuPolicy.NativeFamily family(Screen screen) {
        return screen == null ? SiegeMenuPolicy.NativeFamily.NONE
                : SiegeMenuPolicy.nativeFamily(screen.getClass().getName());
    }

    public static int accent(Screen screen) {
        return accent(family(screen));
    }

    private static int accent(SiegeMenuPolicy.NativeFamily family) {
        return switch (family) {
            case AUDIO, PACKS -> SiegeTheme.GOLD;
            case VIDEO, LANGUAGE -> SiegeTheme.CYAN;
            case CONTROLS -> SiegeTheme.BLUE;
            case WORLD -> SiegeTheme.GREEN;
            case NETWORK, CONFIRM -> SiegeTheme.RED;
            case SYSTEM -> SiegeTheme.ORANGE;
            default -> SiegeTheme.RED;
        };
    }

    public static String familyIcon(Screen screen) {
        return familyIcon(family(screen));
    }

    private static String familyIcon(SiegeMenuPolicy.NativeFamily family) {
        return switch (family) {
            case NETWORK -> "connect";
            case AUDIO -> "music";
            case VIDEO -> "image";
            case CONTROLS -> "keyboard";
            case LANGUAGE -> "globe";
            case PACKS -> "package";
            case WORLD -> "world";
            case CONFIRM -> "warning";
            case SYSTEM -> "settings";
            default -> "settings";
        };
    }

    public static String buttonIcon(Component message) {
        if (message == null) return "settings";
        String key = message.getContents() instanceof TranslatableContents tr
                ? tr.getKey().toLowerCase(Locale.ROOT)
                : message.getString().toLowerCase(Locale.ROOT);
        if (key.contains("back") || key.contains("cancel") || key.contains("no")) return "back";
        if (key.contains("done") || key.contains("yes") || key.contains("confirm")) return "check";
        if (key.contains("sound") || key.contains("music") || key.contains("volume")) return "music";
        if (key.contains("video") || key.contains("graphics") || key.contains("fullscreen")) return "image";
        if (key.contains("language")) return "globe";
        if (key.contains("control") || key.contains("keybind") || key.contains("key.")) return "keyboard";
        if (key.contains("accessib")) return "eye";
        if (key.contains("resource") || key.contains("pack")) return "package";
        if (key.contains("skin")) return "user";
        if (key.contains("chat")) return "chat";
        if (key.contains("server") || key.contains("connect") || key.contains("multiplayer")) return "connect";
        if (key.contains("world") || key.contains("create") || key.contains("game")) return "world";
        return "settings";
    }

    public static void renderBackground(Screen screen, GuiGraphics g) {
        int accent = accent(screen);
        SiegeBackgrounds.render(g, screen.width, screen.height, System.currentTimeMillis());
        g.fill(0, 0, screen.width, screen.height, 0xB908090B);

        int margin = screen.width < 380 ? 5 : screen.width < 640 ? 8 : 12;
        int top = 22;
        int bottom = Math.max(top + 8, screen.height - 6);
        int panelWidth = Math.max(6, screen.width - margin * 2);
        int panelHeight = Math.max(6, bottom - top);
        SiegeTheme.panel(g, margin, top, panelWidth, panelHeight, accent);

        // Tactical registration marks are fixed-cost and stay in the perimeter.
        int right = margin + panelWidth;
        for (int x = margin + 24; x < right - 16; x += 52) {
            g.fill(x, top + 3, x + 1, top + 6, 0x553E4348);
            g.fill(x, bottom - 6, x + 1, bottom - 3, 0x442B3035);
        }
        g.fill(margin + 2, top + 8, margin + 4, Math.min(bottom - 8, top + 42), accent);
        g.fill(Math.max(margin + 4, right - 4), Math.max(top + 8, bottom - 42), right - 2, bottom - 8, accent);
    }

    public static void renderOverlay(Screen screen, GuiGraphics g) {
        Minecraft minecraft = Minecraft.getInstance();
        var font = minecraft.font;
        SiegeMenuPolicy.NativeFamily family = family(screen);
        int accent = accent(family);
        int width = screen.width;

        g.pose().pushPose();
        g.pose().translate(0, 0, 430);
        g.fill(0, 0, width, 20, 0xF20D0E10);
        g.fill(0, 18, width, 20, 0xFF25292D);
        g.fill(0, 18, Math.min(width, 72), 20, accent);
        if (width > 92) g.fill(width - Math.min(width / 4, 92), 19, width, 20, accent);

        SiegeTheme.icon(g, 7, 5, familyIcon(family), accent);
        String title = screen.getTitle() == null ? "" : screen.getTitle().getString();
        if (title.isBlank()) title = familyLabel(family);

        if (width >= 420) {
            String familyText = "SIEGE // " + familyLabel(family);
            familyText = font.plainSubstrByWidth(familyText, Math.max(70, width / 4));
            g.drawString(font, familyText, 21, 6, accent, false);
            String clipped = font.plainSubstrByWidth(title, Math.max(70, width / 3));
            g.drawCenteredString(font, clipped, width / 2, 6, SiegeTheme.INK);
            if (width >= 620) {
                String state = spanish() ? "INTERFAZ SEGURA" : "SECURE INTERFACE";
                g.drawString(font, state, width - 8 - font.width(state), 6, SiegeTheme.MUTED, false);
            }
        } else {
            String clipped = font.plainSubstrByWidth(title, Math.max(1, width - 54));
            g.drawCenteredString(font, clipped, width / 2 + 7, 6, SiegeTheme.INK);
        }

        if (SiegeConfig.menuEffects && !SiegeConfig.reducedMotion && width > 80) {
            int span = Math.max(1, width - 36);
            int sweepX = 18 + (int)((System.currentTimeMillis() / 9L) % span);
            int sweepRight = Math.min(width - 2, sweepX + 28);
            if (sweepRight > sweepX)
                g.fill(sweepX, 18, sweepRight, 20, 0x90000000 | (accent & 0x00FFFFFF));
        }
        g.pose().popPose();
    }

    public static void decorateWidgets(Screen screen, GuiGraphics g) {
        SiegeMenuPolicy.NativeFamily family = family(screen);
        int accent = accent(family);
        for (var child : screen.children()) {
            if (child instanceof EditBox field && field.visible) {
                int color = field.isFocused() ? SiegeTheme.FOCUS : 0xFF666B70;
                frameWithin(screen, g, field.getX() - 1, field.getY() - 1,
                        field.getWidth() + 2, field.getHeight() + 2, color, field.isFocused());

                int railLeft = Math.max(0, field.getX() - 1);
                int railRight = Math.min(screen.width,
                        field.getX() + Math.min(field.getWidth() + 1, field.isFocused() ? 34 : 12));
                int railY = Math.min(screen.height - 1, field.getY() + field.getHeight());
                if (railRight > railLeft && railY >= 0 && railY < screen.height)
                    g.fill(railLeft, railY, railRight, railY + 1, field.isFocused() ? accent : 0xFF565B60);
            } else if (child instanceof AbstractSliderButton slider && slider.visible) {
                int color = slider.isFocused() ? SiegeTheme.FOCUS : accent;
                frameWithin(screen, g, slider.getX() - 1, slider.getY() - 1,
                        slider.getWidth() + 2, slider.getHeight() + 2, color, slider.isFocused());
                int left = Math.max(0, slider.getX() - 1);
                int top = Math.max(0, slider.getY() + 2);
                int bottom = Math.min(screen.height, slider.getY() + slider.getHeight() - 2);
                if (bottom > top && left < screen.width)
                    g.fill(left, top, Math.min(screen.width, left + 2), bottom, accent);
            } else if (child instanceof AbstractWidget widget && widget.visible && widget.isFocused()
                    && !(widget instanceof SiegeButton)) {
                frameWithin(screen, g, widget.getX() - 1, widget.getY() - 1,
                        widget.getWidth() + 2, widget.getHeight() + 2, SiegeTheme.FOCUS, true);
            }
        }

        // Selection lists in 1.20.1 are not AbstractWidgets. Give list-heavy screens
        // a safe inner rail instead of depending on inaccessible list coordinates.
        if ((family == SiegeMenuPolicy.NativeFamily.LANGUAGE
                || family == SiegeMenuPolicy.NativeFamily.PACKS
                || family == SiegeMenuPolicy.NativeFamily.WORLD
                || family == SiegeMenuPolicy.NativeFamily.CONTROLS)
                && screen.width >= 120 && screen.height >= 96) {
            int inset = screen.width < 420 ? 8 : 12;
            int top = 28;
            int bottom = screen.height - 38;
            if (bottom > top + 8) {
                SiegeTheme.frame(g, inset, top, screen.width - inset * 2, bottom - top, 0x804D555C);
                g.fill(inset, top, inset + 2, Math.min(bottom, top + 22), accent);
                g.fill(screen.width - inset - 2, Math.max(top, bottom - 22), screen.width - inset, bottom, accent);
            }
        }
    }

    private static void frameWithin(Screen screen, GuiGraphics g, int x, int y, int w, int h,
                                    int color, boolean focus) {
        int left = Math.max(0, x);
        int top = Math.max(0, y);
        int right = Math.min(screen.width, x + Math.max(0, w));
        int bottom = Math.min(screen.height, y + Math.max(0, h));
        if (right - left < 2 || bottom - top < 2) return;
        SiegeTheme.frame(g, left, top, right - left, bottom - top, color);
        if (focus) SiegeTheme.focusCorners(g, left, top, right - left, bottom - top, SiegeTheme.FOCUS);
    }

    public static String familyLabel(Screen screen) {
        return familyLabel(family(screen));
    }

    private static String familyLabel(SiegeMenuPolicy.NativeFamily family) {
        boolean es = spanish();
        return switch (family) {
            case NETWORK -> es ? "ENLACE DE RED" : "NETWORK LINK";
            case AUDIO -> "AUDIO";
            case VIDEO -> "VIDEO";
            case CONTROLS -> es ? "CONTROLES" : "CONTROLS";
            case LANGUAGE -> es ? "IDIOMA" : "LANGUAGE";
            case PACKS -> es ? "PAQUETES" : "PACKS";
            case WORLD -> es ? "OPERACIONES DE MUNDO" : "WORLD OPERATIONS";
            case CONFIRM -> es ? "CONFIRMACIÓN" : "CONFIRMATION";
            case SYSTEM -> es ? "SISTEMA" : "SYSTEM";
            default -> "SIEGE";
        };
    }

    private static boolean spanish() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }
}

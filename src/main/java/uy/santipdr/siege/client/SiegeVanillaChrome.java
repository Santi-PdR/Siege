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
            case CONTROLS, MOUSE -> SiegeTheme.BLUE;
            case ACCESSIBILITY, WORLD -> SiegeTheme.GREEN;
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
            case MOUSE -> "mouse";
            case ACCESSIBILITY -> "eye";
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
        if (key.contains("mouse") || key.contains("sensitivity")) return "mouse";
        if (key.contains("language")) return "globe";
        if (key.contains("control") || key.contains("keybind") || key.contains("key.")) return "keyboard";
        if (key.contains("accessib") || key.contains("narrator") || key.contains("subtitle")) return "eye";
        if (key.contains("resource") || key.contains("pack")) return "package";
        if (key.contains("skin")) return "user";
        if (key.contains("chat")) return "chat";
        if (key.contains("server") || key.contains("connect") || key.contains("multiplayer")) return "connect";
        if (key.contains("world") || key.contains("create") || key.contains("game")) return "world";
        return "settings";
    }

    public static void renderBackground(Screen screen, GuiGraphics g) {
        SiegeMenuPolicy.NativeFamily family = family(screen);
        int accent = accent(family);
        SiegeBackgrounds.render(g, screen.width, screen.height, System.currentTimeMillis());
        g.fill(0, 0, screen.width, screen.height, 0xB908090B);

        int margin = screen.width < 380 ? 5 : screen.width < 640 ? 8 : 12;
        int top = 22;
        int bottom = Math.max(top + 8, screen.height - 6);
        int panelWidth = Math.max(6, screen.width - margin * 2);
        int panelHeight = Math.max(6, bottom - top);
        SiegeTheme.panel(g, margin, top, panelWidth, panelHeight, accent);

        // Tactical registration marks stay in the empty perimeter and never sit
        // over option widgets. Each settings family has its own quiet signature.
        int right = margin + panelWidth;
        for (int x = margin + 24; x < right - 16; x += 52) {
            g.fill(x, top + 3, x + 1, top + 6, 0x553E4348);
            g.fill(x, bottom - 6, x + 1, bottom - 3, 0x442B3035);
        }
        g.fill(margin + 2, top + 8, margin + 4, Math.min(bottom - 8, top + 42), accent);
        g.fill(Math.max(margin + 4, right - 4), Math.max(top + 8, bottom - 42), right - 2, bottom - 8, accent);

        if (panelWidth >= 90 && panelHeight >= 50) {
            int motifY = top + 10;
            if (family == SiegeMenuPolicy.NativeFamily.AUDIO) {
                for (int i = 0; i < 4; i++)
                    g.fill(margin + 6 + i * 2, motifY + 8 - i * 2, margin + 7 + i * 2, motifY + 10, accent);
            } else if (family == SiegeMenuPolicy.NativeFamily.VIDEO) {
                g.fill(margin + 6, motifY, margin + 15, motifY + 1, accent);
                g.fill(margin + 6, motifY, margin + 7, motifY + 7, accent);
            } else if (family == SiegeMenuPolicy.NativeFamily.MOUSE) {
                SiegeTheme.icon(g, margin + 6, motifY, "mouse", accent);
            } else if (family == SiegeMenuPolicy.NativeFamily.ACCESSIBILITY) {
                SiegeTheme.icon(g, margin + 6, motifY, "eye", accent);
            }
        }
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

        renderContextStrip(screen, g, family, accent);

        if (SiegeConfig.menuEffects && !SiegeConfig.reducedMotion && width > 80) {
            int span = Math.max(1, width - 36);
            int sweepX = 18 + (int)((System.currentTimeMillis() / 9L) % span);
            int sweepRight = Math.min(width - 2, sweepX + 28);
            if (sweepRight > sweepX)
                g.fill(sweepX, 18, sweepRight, 20, 0x90000000 | (accent & 0x00FFFFFF));
        }
        g.pose().popPose();
    }

    private static void renderContextStrip(Screen screen, GuiGraphics g, SiegeMenuPolicy.NativeFamily family, int accent) {
        String note = contextNote(family);
        if (note.isBlank() || screen.width < 260) return;
        int firstWidgetY = screen.height;
        for (var child : screen.children()) {
            if (child instanceof AbstractWidget widget && widget.visible)
                firstWidgetY = Math.min(firstWidgetY, widget.getY());
        }
        int y = 23;
        if (firstWidgetY - y < 12) return;
        var font = Minecraft.getInstance().font;
        int x = screen.width < 420 ? 8 : 14;
        int max = Math.max(1, screen.width - x * 2 - 6);
        String clipped = font.plainSubstrByWidth(note, max);
        int right = Math.min(screen.width - x, x + font.width(clipped) + 8);
        g.fill(x, y, right, y + 10, 0xD018191B);
        g.fill(x, y, x + 2, y + 10, accent);
        g.drawString(font, clipped, x + 5, y + 1, SiegeTheme.MUTED, false);
    }

    private static String contextNote(SiegeMenuPolicy.NativeFamily family) {
        boolean es = spanish();
        return switch (family) {
            case AUDIO -> es ? "MEZCLA NATIVA · MAESTRO Y CATEGORÍAS" : "NATIVE MIX · MASTER AND CATEGORIES";
            case VIDEO -> es ? "VIDEO VANILLA · EMBEDDIUM CONSERVA SU PROPIA INTERFAZ" : "VANILLA VIDEO · EMBEDDIUM KEEPS ITS OWN INTERFACE";
            case MOUSE -> es ? "ENTRADA NATIVA · SENSIBILIDAD Y PUNTERO" : "NATIVE INPUT · SENSITIVITY AND POINTER";
            case ACCESSIBILITY -> es ? "ACCESIBILIDAD NATIVA · NARRADOR, SUBTÍTULOS Y EFECTOS" : "NATIVE ACCESSIBILITY · NARRATOR, SUBTITLES AND EFFECTS";
            default -> "";
        };
    }

    public static void decorateWidgets(Screen screen, GuiGraphics g) {
        SiegeMenuPolicy.NativeFamily family = family(screen);
        int accent = accent(family);
        for (var child : screen.children()) {
            if (child instanceof EditBox field && field.visible) {
                int color = field.isFocused() ? SiegeTheme.FOCUS : 0xFF666B70;
                // Decoration stays inside the native widget rectangle; neighbouring
                // controls therefore cannot be painted over at dense GUI scales.
                frameWithin(screen, g, field.getX(), field.getY(),
                        field.getWidth(), field.getHeight(), color, field.isFocused());

                int railLeft = Math.max(0, field.getX());
                int railRight = Math.min(screen.width,
                        field.getX() + Math.min(field.getWidth(), field.isFocused() ? 34 : 12));
                int railY = Math.min(screen.height - 1, field.getY() + Math.max(0, field.getHeight() - 1));
                if (railRight > railLeft && railY >= 0 && railY < screen.height)
                    g.fill(railLeft, railY, railRight, railY + 1, field.isFocused() ? accent : 0xFF565B60);
            } else if (child instanceof AbstractSliderButton slider && slider.visible) {
                int color = slider.isFocused() ? SiegeTheme.FOCUS : accent;
                frameWithin(screen, g, slider.getX(), slider.getY(),
                        slider.getWidth(), slider.getHeight(), color, slider.isFocused());
                int left = Math.max(0, slider.getX());
                int top = Math.max(0, slider.getY() + 2);
                int bottom = Math.min(screen.height, slider.getY() + slider.getHeight() - 2);
                if (bottom > top && left < screen.width)
                    g.fill(left, top, Math.min(screen.width, left + 2), bottom, accent);
            } else if (child instanceof AbstractWidget widget && widget.visible && widget.isFocused()
                    && !(widget instanceof SiegeButton)) {
                frameWithin(screen, g, widget.getX(), widget.getY(),
                        widget.getWidth(), widget.getHeight(), SiegeTheme.FOCUS, true);
            }
        }

        // Do not draw a broad list rail over ordinary Controls/Mouse screens.
        // Only actual list screens get this frame.
        if (SiegeMenuPolicy.listRail(screen.getClass().getName())
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
            case AUDIO -> es ? "MEZCLADOR DE AUDIO" : "AUDIO MIXER";
            case VIDEO -> es ? "VIDEO / RENDER" : "VIDEO / RENDER";
            case CONTROLS -> es ? "CONTROLES" : "CONTROLS";
            case MOUSE -> es ? "RATÓN" : "MOUSE INPUT";
            case ACCESSIBILITY -> es ? "ACCESIBILIDAD" : "ACCESSIBILITY";
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

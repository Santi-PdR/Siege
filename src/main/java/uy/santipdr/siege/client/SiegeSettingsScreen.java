package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.ArrayList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import org.lwjgl.glfw.GLFW;

/** Responsive, section-based client settings hub for SIEGE. */
public final class SiegeSettingsScreen extends Screen {
    private static final int ACCENT = 0xFF55BFD9;
    private static final int WARNING = 0xFFD65A4B;
    private static final int GOLD = 0xFFD6A94B;

    private final Screen parent;
    private final Section section;

    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelBottom;
    private int navX;
    private int navY;
    private int navWidth;
    private int contentX;
    private int contentY;
    private int contentWidth;
    private boolean compact;
    private final List<AbstractWidget> controls = new ArrayList<>();
    private final List<Integer> controlY = new ArrayList<>();
    private int scrollOffset, scrollMax, viewportTop, viewportBottom, informationY;


    public SiegeSettingsScreen(Screen parent) {
        this(parent, Section.OVERVIEW);
    }

    private SiegeSettingsScreen(Screen parent, Section section) {
        super(Component.translatable("siege.settings.title"));
        this.parent = parent;
        this.section = section;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        controls.clear();
        controlY.clear();
        scrollOffset = 0;
        compact = width < 700 || height < 355;

        int margin = compact ? 7 : 14;
        panelWidth = Math.max(230, Math.min(compact ? 520 : 760, width - margin * 2));
        panelX = (width - panelWidth) / 2;
        panelY = compact ? 42 : 54;
        panelBottom = Math.min(height - (height >= 250 ? 28 : 8), panelY + (compact ? 250 : 292));

        addRenderableWidget(new SiegeButton(8, 7, Math.min(82, Math.max(62, width / 6)), 19,
                Component.translatable("siege.common.back"), b -> onClose(), WARNING));

        if (compact) initCompactNavigation();
        else initWideNavigation();

        viewportTop = contentY + (compact ? 43 : 51);
        viewportBottom = panelBottom - 7;
        int firstControl = children().size();
        initSectionControls();
        int lastBottom = viewportTop;
        for (int i = firstControl; i < children().size(); i++) {
            if (children().get(i) instanceof AbstractWidget widget) {
                controls.add(widget);
                controlY.add(widget.getY());
                lastBottom = Math.max(lastBottom, widget.getY() + widget.getHeight());
                widget.setTooltip(Tooltip.create(widget.getMessage()));
            }
        }
        informationY = lastBottom + 12;
        scrollMax = Math.max(0, informationY + (section == Section.OVERVIEW ? 94 : 44) - viewportBottom);
        positionControls();
    }

    private void initWideNavigation() {
        int pad = 12;
        navX = panelX + pad;
        navY = panelY + 43;
        navWidth = 154;
        int h = 23;
        int gap = 5;

        int y = navY;
        for (Section value : Section.values()) {
            addRenderableWidget(sectionButton(navX, y, navWidth, h, value));
            y += h + gap;
        }

        contentX = navX + navWidth + 18;
        contentY = panelY + 43;
        contentWidth = panelX + panelWidth - 13 - contentX;
    }

    private void initCompactNavigation() {
        int pad = 8;
        navX = panelX + pad;
        navY = panelY + 29;
        int gap = 3;
        int columns = panelWidth >= 355 ? 3 : 2;
        navWidth = Math.max(68, (panelWidth - pad * 2 - gap * (columns - 1)) / columns);
        int h = 18;

        for (int i = 0; i < Section.values().length; i++) {
            Section value = Section.values()[i];
            int row = i / columns;
            int col = i % columns;
            int x = navX + col * (navWidth + gap);
            int y = navY + row * (h + 3);
            addRenderableWidget(sectionButton(x, y, navWidth, h, value));
        }

        int rows = (Section.values().length + columns - 1) / columns;
        contentX = panelX + pad;
        contentY = navY + rows * (h + 3) + 10;
        contentWidth = panelWidth - pad * 2;
    }

    private SiegeButton sectionButton(int x, int y, int w, int h, Section value) {
        SiegeButton button = new SiegeButton(x, y, w, h, Component.literal(sectionLabel(value)), b -> switchSection(value),
                value == Section.AUDIO ? GOLD : ACCENT);
        return button.setSelected(value == section);
    }

    private void switchSection(Section value) {
        if (value == section) return;
        SiegeUiSounds.click();
        SiegeConfig.save();
        minecraft.setScreen(new SiegeSettingsScreen(parent, value));
    }

    private void initSectionControls() {
        int y = contentY + (compact ? 43 : 51);
        int h = compact ? 19 : 24;
        int gap = compact ? 4 : 7;
        int w = contentWidth - 8;

        switch (section) {
            case OVERVIEW -> {
                addRenderableWidget(new SiegeButton(contentX, y, w, h,
                        Component.literal(label("RESTAURAR AJUSTES DE SIEGE", "RESET SIEGE SETTINGS")), b -> {
                    SiegeUiSounds.click();
                    minecraft.setScreen(new ConfirmScreen(confirmed -> {
                        if (confirmed) {
                            SiegeMusic.stop();
                            SiegeConfig.resetDefaults();
                            SiegeMusic.ensurePlaying();
                        }
                        minecraft.setScreen(this);
                    }, Component.literal(label("¿Restaurar SIEGE?", "Reset SIEGE?")),
                            Component.literal(label("Se restaurarán las preferencias del menú.", "Menu preferences will return to their defaults."))));
                }, WARNING));
            }
            case AUDIO -> {
                addRenderableWidget(toggle(contentX, y, w, h, "siege.settings.music", () -> {
                    SiegeConfig.music = !SiegeConfig.music;
                    if (SiegeConfig.music) SiegeMusic.ensurePlaying(); else SiegeMusic.stop();
                }, () -> SiegeConfig.music));

                int sliderHeight = compact ? 25 : 31;
                addRenderableWidget(new SiegeSlider(contentX, y += h + gap, w, sliderHeight,
                        Component.literal(label("VOLUMEN DE MÚSICA", "MUSIC VOLUME")), SiegeConfig.musicVolume,
                        percent -> {
                            SiegeMusic.setVolumeLive(percent);
                            // Save on release/close, not on every drag event.

                        }));

                y += sliderHeight + gap;
                addRenderableWidget(new SiegeButton(contentX, y, w, h,
                        Component.translatable("siege.menu.next_track"), b -> {
                    SiegeUiSounds.nextTrack();
                    SiegeMusic.nextTrack();
                }, GOLD));
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("AVISO DE NUEVA PISTA", "NEW TRACK NOTICE"),
                        () -> SiegeConfig.trackAnnouncements = !SiegeConfig.trackAnnouncements,
                        () -> SiegeConfig.trackAnnouncements));
            }
            case INTERFACE -> {
                addRenderableWidget(toggle(contentX, y, w, h, "siege.settings.ui_sounds",
                        () -> SiegeConfig.uiSounds = !SiegeConfig.uiSounds, () -> SiegeConfig.uiSounds));
                addRenderableWidget(new SiegeSlider(contentX, y += h + gap, w, compact ? 25 : 31,
                        Component.literal(label("VOLUMEN DE EFECTOS", "UI EFFECTS VOLUME")), SiegeConfig.uiVolume,
                        percent -> SiegeConfig.uiVolume = percent));
                y += (compact ? 25 : 31) - h;
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("SONIDO AL SEÑALAR", "HOVER SOUND"),
                        () -> SiegeConfig.hoverSounds = !SiegeConfig.hoverSounds,
                        () -> SiegeConfig.hoverSounds));
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("EFECTOS TÁCTICOS", "TACTICAL EFFECTS"),
                        () -> SiegeConfig.menuEffects = !SiegeConfig.menuEffects,
                        () -> SiegeConfig.menuEffects));
                addRenderableWidget(toggle(contentX, y += h + gap, w, h, "siege.settings.backgrounds",
                        () -> SiegeConfig.animatedBackgrounds = !SiegeConfig.animatedBackgrounds,
                        () -> SiegeConfig.animatedBackgrounds));
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("INTEL EN MENÚ PRINCIPAL", "MAIN-MENU INTEL"),
                        () -> SiegeConfig.mainMenuIntel = !SiegeConfig.mainMenuIntel,
                        () -> SiegeConfig.mainMenuIntel));
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("ANIMACIONES DE INTEL", "INTEL ANIMATIONS"),
                        () -> SiegeConfig.animatedIntel = !SiegeConfig.animatedIntel,
                        () -> SiegeConfig.animatedIntel));
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("ROTACIÓN AUTOMÁTICA INTEL", "AUTO-ROTATE INTEL"),
                        () -> SiegeConfig.autoRotateIntel = !SiegeConfig.autoRotateIntel,
                        () -> SiegeConfig.autoRotateIntel));
            }
            case ACCESSIBILITY -> {
                addRenderableWidget(toggle(contentX, y, w, h, "siege.settings.reduced_motion",
                        () -> SiegeConfig.reducedMotion = !SiegeConfig.reducedMotion,
                        () -> SiegeConfig.reducedMotion));
            }
            case GRAPHICS -> {
                addRenderableWidget(graphicsButton(contentX, y, w, h));
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("LÍNEAS DE ESCANEO", "SCANLINES"),
                        () -> SiegeConfig.scanlines = !SiegeConfig.scanlines,
                        () -> SiegeConfig.scanlines));
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("INTERFERENCIA DEL TÍTULO", "TITLE INTERFERENCE"),
                        () -> SiegeConfig.titleInterference = !SiegeConfig.titleInterference,
                        () -> SiegeConfig.titleInterference));
            }
        }
    }

    private SiegeButton graphicsButton(int x, int y, int w, int h) {
        return new SiegeButton(x, y, w, h, graphicsLabel(), b -> {
            SiegeUiSounds.click();
            SiegeConfig.graphics = SiegeConfig.graphics.next();
            b.setMessage(graphicsLabel());
            SiegeConfig.save();
        }, ACCENT);
    }

    private SiegeButton toggle(int x, int y, int w, int h, String key, Runnable action, Flag flag) {
        SiegeButton button = new SiegeButton(x, y, w, h, toggleLabel(key, flag.get()), b -> {
            SiegeUiSounds.click();
            action.run();
            SiegeConfig.save();
            b.setMessage(toggleLabel(key, flag.get()));
            b.setTooltip(Tooltip.create(b.getMessage()));
            ((SiegeButton)b).setSelected(flag.get());
        }, ACCENT);
        return button.setSelected(flag.get());
    }

    private SiegeButton literalToggle(int x, int y, int w, int h, String text, Runnable action, Flag flag) {
        SiegeButton button = new SiegeButton(x, y, w, h, literalToggleLabel(text, flag.get()), b -> {
            SiegeUiSounds.click();
            action.run();
            SiegeConfig.save();
            b.setMessage(literalToggleLabel(text, flag.get()));
            b.setTooltip(Tooltip.create(b.getMessage()));
            ((SiegeButton)b).setSelected(flag.get());
        }, ACCENT);
        return button.setSelected(flag.get());
    }

    private Component graphicsLabel() {
        return Component.translatable("siege.settings.graphics").append(": ")
                .append(Component.translatable("siege.settings.graphics." + SiegeConfig.graphics.name().toLowerCase()));
    }

    private Component toggleLabel(String key, boolean enabled) {
        return Component.translatable(key).append(": ")
                .append(Component.translatable(enabled ? "siege.common.on" : "siege.common.off"));
    }

    private Component literalToggleLabel(String text, boolean enabled) {
        return Component.literal(text + ": " + label(enabled ? "SÍ" : "NO", enabled ? "ON" : "OFF"));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, 0xAA070A0D);

        int bottom = Math.max(panelY + 90, panelBottom);
        g.fill(panelX - 5, panelY - 7, panelX + panelWidth + 5, bottom, 0xF20B1015);
        g.fill(panelX - 5, panelY - 7, panelX + panelWidth + 5, panelY - 4, ACCENT);
        g.fill(panelX - 5, bottom - 1, panelX + panelWidth + 5, bottom, 0xFF29353D);

        g.drawCenteredString(font, font.plainSubstrByWidth(label("CONFIGURACIÓN SIEGE", "SIEGE SETTINGS"), Math.max(40, width - 116)), (width + 100) / 2, 11, 0xFFF0EEE8);
        g.drawCenteredString(font, label("CENTRO DE CONTROL // CLIENTE", "CONTROL CENTER // CLIENT"), width / 2,
                compact ? 27 : 32, 0xFF79868E);

        if (!compact) {
            g.fill(contentX - 10, panelY + 34, contentX - 9, bottom - 12, 0x77404D55);
            g.drawString(font, label("// SECCIONES", "// SECTIONS"), navX, panelY + 19, 0xFF77838B, false);
        }

        renderSectionHeader(g);
        g.enableScissor(contentX, viewportTop, contentX + contentWidth, viewportBottom);
        renderSectionInformation(g, informationY + 110 - scrollOffset);
        g.disableScissor();
        if (scrollMax > 0) {
            int track = viewportBottom - viewportTop;
            int thumb = Math.max(10, track * track / (track + scrollMax));
            int top = viewportTop + (track - thumb) * scrollOffset / scrollMax;
            g.fill(contentX + contentWidth - 4, viewportTop, contentX + contentWidth - 2, viewportBottom, 0xFF27343C);
            g.fill(contentX + contentWidth - 4, top, contentX + contentWidth - 2, top + thumb, ACCENT);
        }

        if (height >= 250) {
            String rule = label(
                    "Los cambios son del cliente. La música SIEGE nunca se reproduce dentro del gameplay.",
                    "These are client settings. SIEGE music never plays during gameplay.");
            g.drawCenteredString(font, font.plainSubstrByWidth(rule, Math.max(120, width - 24)), width / 2,
                    height - 13, 0xFF68747C);
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    private void renderSectionHeader(GuiGraphics g) {
        int titleY = contentY + 2;
        int accent = section == Section.AUDIO ? GOLD : ACCENT;
        String kicker = "// " + sectionKicker(section);
        g.drawString(font, kicker, contentX, titleY, accent, false);

        String title = sectionTitle(section);
        g.pose().pushPose();
        g.pose().translate(contentX, titleY + 13, 0.0F);
        float scale = compact ? 1.08F : 1.22F;
        g.pose().scale(scale, scale, 1.0F);
        g.drawString(font, title, 0, 0, 0xFFF0EEE8, false);
        g.pose().popPose();

        int lineY = titleY + (compact ? 29 : 33);
        g.fill(contentX, lineY, contentX + contentWidth, lineY + 1, 0xFF27343C);
        g.fill(contentX, lineY, contentX + Math.min(contentWidth, 72), lineY + 2, accent);
    }

    private void renderSectionInformation(GuiGraphics g, int bottom) {
        int infoY = informationY - scrollOffset;

        int availableBottom = bottom - 12;
        if (infoY >= availableBottom) return;

        if (section == Section.OVERVIEW) {
            renderWrapped(g, sectionDescription(section), contentX, infoY, contentWidth, 0xFFB8C0C5,
                    compact ? 3 : 4, 11);
            int statusY = infoY + (compact ? 39 : 50);
            if (statusY < availableBottom - 10) {
                String audio = label("AUDIO", "AUDIO") + "  " + (SiegeConfig.music ? label("ACTIVO", "ON") : label("DESACTIVADO", "OFF"))
                        + "  //  " + SiegeConfig.musicVolume + "%";
                String ui = label("INTERFAZ", "INTERFACE") + "  "
                        + (SiegeConfig.animatedBackgrounds ? label("DINÁMICA", "DYNAMIC") : label("ESTÁTICA", "STATIC"))
                        + "  //  " + SiegeConfig.graphics.name();
                g.drawString(font, font.plainSubstrByWidth(audio, contentWidth), contentX, statusY, 0xFFD7DDE0, false);
                if (statusY + 13 < availableBottom) g.drawString(font, font.plainSubstrByWidth(ui, contentWidth), contentX, statusY + 13, 0xFF8FA0A9, false);
            }
            return;
        }

        if (section == Section.AUDIO) {
            String playback = label("ESTADO", "STATE") + "  "
                    + (SiegeMusic.isActuallyPlaying() ? label("REPRODUCIENDO", "PLAYING") : label("CARGANDO / EN ESPERA", "LOADING / WAITING"));
            String track = label("PISTA", "TRACK") + "  " + SiegeMusic.currentTrackName()
                    + "  //  " + SiegeMusic.transitionLabel(spanish());
            g.drawString(font, font.plainSubstrByWidth(playback, contentWidth), contentX, infoY, 0xFF9FCAD5, false);
            if (infoY + 13 < availableBottom) g.drawString(font, font.plainSubstrByWidth(track, contentWidth), contentX, infoY + 13, 0xFFAAB2B7, false);
            return;
        }

        renderWrapped(g, sectionDescription(section), contentX, infoY, contentWidth, 0xFF9DA8AE,
                compact ? 2 : 3, 11);
    }

    private void positionControls() {
        scrollOffset = Math.max(0, Math.min(scrollMax, scrollOffset));
        for (int i = 0; i < controls.size(); i++) {
            AbstractWidget widget = controls.get(i);
            widget.setY(controlY.get(i) - scrollOffset);
            widget.visible = widget.getY() >= viewportTop && widget.getY() + widget.getHeight() <= viewportBottom;
            if (!widget.visible && getFocused() == widget) setFocused(null);
        }
        SiegeUiSounds.resetHover();
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (delta != 0 && x >= contentX && x < contentX + contentWidth && y >= viewportTop && y < viewportBottom) {
            scrollOffset -= (int)Math.signum(delta) * (compact ? 23 : 31);
            positionControls();
            return true;
        }
        return super.mouseScrolled(x, y, delta);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_TAB) {
            List<AbstractWidget> order = new ArrayList<>();
            for (var child : children()) if (child instanceof AbstractWidget widget && widget.active) order.add(widget);
            if (order.isEmpty()) return false;
            int index = order.indexOf(getFocused());
            AbstractWidget next = order.get(Math.floorMod(index + (hasShiftDown() ? -1 : 1), order.size()));
            int control = controls.indexOf(next);
            if (control >= 0) {
                int top = controlY.get(control);
                if (top - scrollOffset < viewportTop) scrollOffset = top - viewportTop;
                else if (top + next.getHeight() - scrollOffset > viewportBottom)
                    scrollOffset = top + next.getHeight() - viewportBottom;
                positionControls();
            }
            setFocused(next);
            return true;
        }
        if (key == GLFW.GLFW_KEY_PAGE_DOWN || key == GLFW.GLFW_KEY_PAGE_UP) {
            scrollOffset += (key == GLFW.GLFW_KEY_PAGE_DOWN ? 1 : -1) * Math.max(23, viewportBottom - viewportTop - 23);
            positionControls();
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        boolean handled = super.mouseReleased(x, y, button);
        SiegeConfig.save();
        return handled;
    }

    @Override
    public void removed() {
        SiegeConfig.save();
        super.removed();
    }

    private void renderWrapped(GuiGraphics g, String text, int x, int y, int width, int color, int maxLines, int lineHeight) {
        List<FormattedCharSequence> lines = font.split(Component.literal(text), Math.max(40, width));
        for (int i = 0; i < lines.size() && i < maxLines; i++) {
            g.drawString(font, lines.get(i), x, y + i * lineHeight, color, false);
        }
    }

    private String sectionLabel(Section value) {
        return switch (value) {
            case OVERVIEW -> label("RESUMEN", "OVERVIEW");
            case AUDIO -> label("MÚSICA", "MUSIC");
            case INTERFACE -> label("INTERFAZ", "INTERFACE");
            case ACCESSIBILITY -> label("ACCESIBILIDAD", "ACCESSIBILITY");
            case GRAPHICS -> label("GRÁFICOS", "GRAPHICS");
        };
    }

    private String sectionKicker(Section value) {
        return switch (value) {
            case OVERVIEW -> label("ESTADO DEL SISTEMA", "SYSTEM STATUS");
            case AUDIO -> label("CANAL DE AUDIO", "AUDIO CHANNEL");
            case INTERFACE -> label("COMPORTAMIENTO DEL MENÚ", "MENU BEHAVIOR");
            case ACCESSIBILITY -> label("CONFORT VISUAL", "VISUAL COMFORT");
            case GRAPHICS -> label("PERFIL DE RENDER", "RENDER PROFILE");
        };
    }

    private String sectionTitle(Section value) {
        return switch (value) {
            case OVERVIEW -> label("CENTRO DE CONTROL", "CONTROL CENTER");
            case AUDIO -> label("MÚSICA DEL MENÚ", "MENU MUSIC");
            case INTERFACE -> label("INTERFAZ SIEGE", "SIEGE INTERFACE");
            case ACCESSIBILITY -> label("ACCESIBILIDAD", "ACCESSIBILITY");
            case GRAPHICS -> label("GRÁFICOS DEL MENÚ", "MENU GRAPHICS");
        };
    }

    private String sectionDescription(Section value) {
        return switch (value) {
            case OVERVIEW -> label(
                    "Selecciona una sección para configurar SIEGE. Música controla la banda sonora; Interfaz controla sonidos y fondos; Accesibilidad reduce movimiento; Gráficos cambia el perfil visual del menú.",
                    "Choose a section to configure SIEGE. Music controls the soundtrack; Interface controls UI sounds and backgrounds; Accessibility reduces motion; Graphics changes the menu render profile.");
            case AUDIO -> label(
                    "La banda sonora usa su propio volumen, conserva la posición de la pista mientras mueves el slider y cambia de canción con una transición suave.",
                    "The soundtrack uses its own volume, keeps the current track position while the slider moves and changes songs with a smooth transition.");
            case INTERFACE -> label(
                    "Controla sonidos, efectos tácticos, fondos, tarjetas Intel del menú y expedientes animados.",
                    "Controls UI sounds, tactical effects, backgrounds, menu Intel cards and animated dossiers.");
            case ACCESSIBILITY -> label(
                    "Movimiento reducido limita desplazamientos y animaciones ambientales del menú sin eliminar su identidad visual.",
                    "Reduced motion limits menu camera movement and ambient animation without removing the visual identity.");
            case GRAPHICS -> label(
                    "Cambia el perfil visual y permite controlar por separado las líneas de escaneo y la interferencia del título.",
                    "Changes the visual profile and separately controls scanlines and title interference.");
        };
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
    public boolean isPauseScreen() {
        return false;
    }

    private interface Flag { boolean get(); }

    private enum Section {
        OVERVIEW,
        AUDIO,
        INTERFACE,
        ACCESSIBILITY,
        GRAPHICS
    }
}


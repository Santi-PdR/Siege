package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.ArrayList;
import java.util.EnumMap;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import org.lwjgl.glfw.GLFW;

/** Responsive, section-based client settings hub for SIEGE. */
public final class SiegeSettingsScreen extends Screen {
    private static final int ACCENT = 0xFF55BFD9;
    private static final int WARNING = 0xFFD65A4B;
    private static final int GOLD = 0xFFD6A94B;
    private static final EnumMap<Section, Integer> SECTION_SCROLL = new EnumMap<>(Section.class);
    private static Section rememberedSection = Section.OVERVIEW;

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
    private final List<SiegeButton> musicTrackButtons = new ArrayList<>();
    private SiegeButton shuffleButton;
    private boolean draggingScrollbar;
    private int scrollThumbTop, scrollThumbHeight, scrollGrab;
    private int soundSample;
    private double wheelRemainder;
    private int scrollOffset, scrollMax, viewportTop, viewportBottom, informationY;


    public SiegeSettingsScreen(Screen parent) {
        this(parent, rememberedSection);
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
        musicTrackButtons.clear();
        shuffleButton = null;
        int previousScroll = SECTION_SCROLL.getOrDefault(section, scrollOffset);
        soundSample = Math.floorMod(soundSample, 3);
        draggingScrollbar = false;
        compact = width < 700 || height < 355;

        int margin = compact ? 7 : 14;
        panelWidth = Math.max(1, Math.min(compact ? 520 : 760, width - margin * 2));
        panelX = (width - panelWidth) / 2;
        panelY = compact ? 42 : 54;
        panelBottom = height - (height >= 300 ? 28 : 8);

        addRenderableWidget(new SiegeButton(8, 7, Math.min(82, Math.max(62, width / 6)), 19,
                Component.translatable("siege.common.back"), b -> onClose(), WARNING));

        if (compact) initCompactNavigation();
        else initWideNavigation();

        viewportTop = contentY + (section == Section.AUDIO ? 49 : compact ? 25 : 51);
        viewportBottom = Math.max(viewportTop + 1, panelBottom - 7);
        int firstControl = children().size();
        initSectionControls();
        int lastBottom = viewportTop;
        for (int i = firstControl; i < children().size(); i++) {
            if (children().get(i) instanceof AbstractWidget widget) {
                controls.add(widget);
                controlY.add(widget.getY());
                lastBottom = Math.max(lastBottom, widget.getY() + widget.getHeight());
                if (widget.getTooltip() == null) widget.setTooltip(Tooltip.create(widget.getMessage()));
            }
        }
        informationY = lastBottom + 12;
        scrollMax = Math.max(0, informationY + (section == Section.OVERVIEW ? 94 : 44) - viewportBottom);
        scrollOffset = previousScroll;
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
        int columns = SiegeUiLayout.settingsColumns(panelWidth);
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
        SECTION_SCROLL.put(section, scrollOffset);
        rememberedSection = value;
        minecraft.setScreen(new SiegeSettingsScreen(parent, value));
    }

    private void initSectionControls() {
        int y = viewportTop;
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
                int transportWidth = (w - 8) / 3;
                addRenderableWidget(new SiegeButton(contentX, y, transportWidth, h,
                        Component.literal(label("ANTERIOR", "PREVIOUS")), b -> {
                    SiegeUiSounds.nextTrack();
                    SiegeMusic.previousTrack();
                    refreshMusicSelection();
                }, GOLD));
                addRenderableWidget(new SiegeButton(contentX + transportWidth + 4, y, transportWidth, h,
                        Component.literal(label("REINICIAR", "RESTART")), b -> {
                    SiegeUiSounds.nextTrack();
                    SiegeMusic.restartTrack();
                }, GOLD));
                addRenderableWidget(new SiegeButton(contentX + (transportWidth + 4) * 2, y, transportWidth, h,
                        Component.literal(label("SIGUIENTE", "NEXT")), b -> {
                    SiegeUiSounds.nextTrack();
                    SiegeMusic.nextTrack();
                    refreshMusicSelection();
                }, GOLD));
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("AVISO DE NUEVA PISTA", "NEW TRACK NOTICE"),
                        () -> SiegeConfig.trackAnnouncements = !SiegeConfig.trackAnnouncements,
                        () -> SiegeConfig.trackAnnouncements));
                int noticeSliderHeight = compact ? 25 : 31;
                addRenderableWidget(new SiegeSlider(contentX, y += h + gap, w, noticeSliderHeight,
                        Component.literal(label("DURACIÓN DEL AVISO", "NOTICE DURATION")),
                        Math.round((SiegeConfig.trackNoticeSeconds - 3) * 100.0F / 12.0F),
                        percent -> SiegeConfig.trackNoticeSeconds = 3 + Math.round(percent * 12.0F / 100.0F))
                        .withValueText(percent -> (3 + Math.round(percent * 12.0F / 100.0F)) + " s"));
                y += noticeSliderHeight - h;
                shuffleButton = addRenderableWidget(new SiegeButton(contentX, y += h + gap, w, h,
                        Component.literal(label("ALEATORIO SIN REPETIR", "SHUFFLE WITHOUT REPEATS")), b -> {
                    SiegeUiSounds.click();
                    SiegeMusic.selectTrack(-1);
                    refreshMusicSelection();
                }, GOLD).setSelected(SiegeConfig.selectedTrack < 0));
                for (int i = 0; i < SiegeMusic.trackNames().size(); i++) {
                    final int index = i;
                    SiegeButton trackButton = addRenderableWidget(new SiegeButton(contentX, y += h + gap, w, h,
                            Component.literal((i + 1) + ". " + SiegeMusic.trackNames().get(i)), b -> {
                        SiegeUiSounds.nextTrack();
                        SiegeMusic.selectTrack(index);
                        refreshMusicSelection();
                    }, GOLD).setSelected(SiegeConfig.selectedTrack == index));
                    musicTrackButtons.add(trackButton);
                }
            }
            case INTERFACE -> {
                addRenderableWidget(new SiegeButton(contentX, y, w, h,
                        Component.literal(label("PROBAR SONIDOS", "PREVIEW UI SOUNDS")), b -> {
                    SiegeUiSounds.preview(soundSample++ % 3);
                }, GOLD));
                y += h + gap;
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
                        label("MOSTRAR BUILD", "SHOW BUILD LABEL"),
                        () -> SiegeConfig.showBuildLabel = !SiegeConfig.showBuildLabel,
                        () -> SiegeConfig.showBuildLabel));
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("CONFIRMAR AL SALIR", "CONFIRM BEFORE QUIT"),
                        () -> SiegeConfig.confirmQuit = !SiegeConfig.confirmQuit,
                        () -> SiegeConfig.confirmQuit));
            }
            case INTEL -> {
                addRenderableWidget(literalToggle(contentX, y, w, h,
                        label("MODO LECTURA AL ABRIR", "OPEN IN READING MODE"),
                        () -> SiegeConfig.intelReadingMode = !SiegeConfig.intelReadingMode,
                        () -> SiegeConfig.intelReadingMode));
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("LECTURA ESPACIADA", "COMFORTABLE LINE SPACING"),
                        () -> SiegeConfig.comfortableReading = !SiegeConfig.comfortableReading,
                        () -> SiegeConfig.comfortableReading));
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("PAPEL OSCURO", "DARK PAPER"),
                        () -> SiegeConfig.darkIntelPaper = !SiegeConfig.darkIntelPaper,
                        () -> SiegeConfig.darkIntelPaper));
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
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("PAUSAR INTEL AL LEER", "PAUSE INTEL ON HOVER"),
                        () -> SiegeConfig.pauseIntelOnHover = !SiegeConfig.pauseIntelOnHover,
                        () -> SiegeConfig.pauseIntelOnHover));
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("PROGRESO DE ROTACIÓN", "ROTATION PROGRESS"),
                        () -> SiegeConfig.showIntelProgress = !SiegeConfig.showIntelProgress,
                        () -> SiegeConfig.showIntelProgress));
                addRenderableWidget(literalToggle(contentX, y += h + gap, w, h,
                        label("ESTADO DEL DOSSIER", "DOSSIER STATE"),
                        () -> SiegeConfig.showIntelState = !SiegeConfig.showIntelState,
                        () -> SiegeConfig.showIntelState));
            }
            case ACCESSIBILITY -> {
                addRenderableWidget(toggle(contentX, y, w, h, "siege.settings.reduced_motion",
                        () -> SiegeConfig.reducedMotion = !SiegeConfig.reducedMotion,
                        () -> SiegeConfig.reducedMotion));
                addRenderableWidget(new SiegeButton(contentX, y += h + gap, w, h,
                        Component.literal(label("APLICAR PERFIL TRANQUILO", "APPLY CALM PRESET")), b -> {
                    SiegeConfig.applyCalmPreset();
                    minecraft.setScreen(new SiegeSettingsScreen(parent, Section.ACCESSIBILITY));
                }, ACCENT));
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
                int visualSliderHeight = compact ? 25 : 31;
                addRenderableWidget(new SiegeSlider(contentX, y += h + gap, w, visualSliderHeight,
                        Component.literal(label("OSCURIDAD DEL FONDO", "BACKGROUND DARKNESS")),
                        Math.round(SiegeConfig.backgroundDarkness * 100.0F / 70.0F),
                        percent -> SiegeConfig.backgroundDarkness = Math.round(percent * 70.0F / 100.0F))
                        .withValueText(percent -> Math.round(percent * 70.0F / 100.0F) + "%"));
                y += visualSliderHeight - h;
                addRenderableWidget(new SiegeSlider(contentX, y += h + gap, w, visualSliderHeight,
                        Component.literal(label("OSCURIDAD DEL PANEL", "PANEL DARKNESS")),
                        Math.round((SiegeConfig.panelDarkness - 20) * 100.0F / 70.0F),
                        percent -> SiegeConfig.panelDarkness = 20 + Math.round(percent * 70.0F / 100.0F))
                        .withValueText(percent -> (20 + Math.round(percent * 70.0F / 100.0F)) + "%"));
                y += visualSliderHeight - h;
                addRenderableWidget(new SiegeButton(contentX, y += h + gap, w, h,
                        Component.literal(label("GALERÍA DE FONDOS", "BACKGROUND GALLERY")), b -> {
                    SiegeUiSounds.click();
                    SECTION_SCROLL.put(section, scrollOffset);
                    minecraft.setScreen(new SiegeSceneScreen(this));
                }, ACCENT));
                addRenderableWidget(new SiegeButton(contentX, y += h + gap, w, h,
                        Component.literal(label("REACTIVAR ROTACIÓN DE FONDOS", "RESUME BACKGROUND ROTATION")), b -> {
                    SiegeUiSounds.click();
                    SiegeConfig.selectedScene = -1;
                    SiegeConfig.animatedBackgrounds = true;
                    SiegeConfig.save();
                }, ACCENT));
            }
        }
    }

    private void refreshMusicSelection() {
        if (shuffleButton != null) shuffleButton.setSelected(SiegeConfig.selectedTrack < 0);
        for (int i = 0; i < musicTrackButtons.size(); i++)
            musicTrackButtons.get(i).setSelected(SiegeConfig.selectedTrack == i);
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
            // Keep the descriptive tooltip while the state changes.
            ((SiegeButton)b).setSelected(flag.get());
        }, ACCENT);
        button.setTooltip(Tooltip.create(Component.literal(settingHelp(key))));
        return button.setSelected(flag.get());
    }

    private SiegeButton literalToggle(int x, int y, int w, int h, String text, Runnable action, Flag flag) {
        SiegeButton button = new SiegeButton(x, y, w, h, literalToggleLabel(text, flag.get()), b -> {
            SiegeUiSounds.click();
            action.run();
            SiegeConfig.save();
            b.setMessage(literalToggleLabel(text, flag.get()));
            // Keep the descriptive tooltip while the state changes.
            ((SiegeButton)b).setSelected(flag.get());
        }, ACCENT);
        button.setTooltip(Tooltip.create(Component.literal(settingHelp(text))));
        return button.setSelected(flag.get());
    }

    private String settingHelp(String key) {
        return switch (key) {
            case "siege.settings.music" -> label("Reproduce la banda sonora sólo fuera de mundos y servidores.", "Play the soundtrack only outside worlds and servers.");
            case "siege.settings.ui_sounds" -> label("Controla los sonidos de botones y navegación del menú.", "Control menu button and navigation sounds.");
            case "siege.settings.backgrounds" -> label("Alterna automáticamente los fondos completos del menú.", "Automatically cycle full menu backgrounds.");
            case "siege.settings.reduced_motion" -> label("Desactiva interferencia y animaciones de movimiento; conserva la respuesta de los controles.", "Disable interference and motion animations while retaining control feedback.");
            case "PAUSAR INTEL AL LEER", "PAUSE INTEL ON HOVER", "PAUSA AL LEER", "PAUSE WHILE READING", "PAUSA AL SEÑALAR", "PAUSE ON HOVER" -> label("Pausa el dossier bajo el cursor y reanuda dos segundos después de salir.", "Pause the dossier under the pointer and resume two seconds after leaving.");
            case "AVISO DE NUEVA PISTA", "NEW TRACK NOTICE" -> label("Muestra el nombre cuando el motor de audio confirma que comenzó la pista.", "Show the name when the audio engine confirms playback started.");
            case "PROGRESO DE ROTACIÓN", "ROTATION PROGRESS" -> label("Muestra cuánto falta para el próximo cambio automático de dossier.", "Show progress until the next automatic dossier change.");
            case "ESTADO DEL DOSSIER", "DOSSIER STATE" -> label("Distingue lectura, rotación automática y expediente fijo.", "Distinguish reading, automatic rotation and a fixed dossier.");
            case "PAPEL OSCURO", "DARK PAPER" -> label("Usa papel oscuro y texto claro para leer Intel.", "Use dark paper and light text in Intel.");
            case "LECTURA ESPACIADA", "COMFORTABLE LINE SPACING", "LECTURA CÓMODA", "COMFORTABLE READING" -> label("Aumenta la separación entre renglones del expediente.", "Increase the spacing between dossier lines.");
            default -> label("Cambia esta preferencia de presentación de SIEGE. Se guarda al modificarla.", "Change this SIEGE presentation preference. Changes are saved automatically.");
        };
    }

    private Component graphicsLabel() {
        return Component.translatable("siege.settings.graphics").append(": ")
                .append(Component.translatable("siege.settings.graphics." + SiegeConfig.graphics.name().toLowerCase(java.util.Locale.ROOT)));
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

        int bottom = panelBottom;
        g.fill(panelX - 5, panelY - 7, panelX + panelWidth + 5, bottom, 0xF20B1015);
        g.fill(panelX - 5, panelY - 7, panelX + panelWidth + 5, panelY - 4, ACCENT);
        g.fill(panelX - 5, bottom - 1, panelX + panelWidth + 5, bottom, 0xFF29353D);

        g.drawCenteredString(font, font.plainSubstrByWidth(label("CONFIGURACIÓN SIEGE", "SIEGE SETTINGS"), Math.max(40, width - 190)), width / 2, 11, 0xFFF0EEE8);
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
            int thumb = SiegeUiLayout.scrollThumb(track, track + scrollMax);
            int top = viewportTop + (track - thumb) * scrollOffset / scrollMax;
            scrollThumbTop = top; scrollThumbHeight = thumb;
            boolean overScroll = mouseX >= contentX + contentWidth - 8 && mouseX < contentX + contentWidth
                    && mouseY >= viewportTop && mouseY < viewportBottom;
            int barColor = overScroll || draggingScrollbar ? 0xFF7BE2F4 : ACCENT;
            g.fill(contentX + contentWidth - 5, viewportTop, contentX + contentWidth - 2, viewportBottom, 0xFF27343C);
            g.fill(contentX + contentWidth - 5, top, contentX + contentWidth - 2, top + thumb, barColor);
            String scrollState = Math.round(scrollOffset * 100.0F / scrollMax) + "%";
            if (contentWidth >= 100) g.drawString(font, scrollState, contentX + contentWidth - font.width(scrollState) - 9,
                    viewportBottom - 10, 0xFF72818A, false);
        }

        if (height >= 300) {
            String rule = label(
                    "Los cambios son del cliente. La música SIEGE nunca se reproduce dentro del gameplay.",
                    "These are client settings. SIEGE music never plays during gameplay.");
            g.drawCenteredString(font, font.plainSubstrByWidth(rule, Math.max(120, width - 24)), width / 2,
                    height - 13, 0xFF68747C);
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    private static String formatTime(long milliseconds) {
        long seconds = Math.max(0, milliseconds / 1000);
        return seconds / 60 + ":" + String.format(java.util.Locale.ROOT, "%02d", seconds % 60);
    }

    private void renderSectionHeader(GuiGraphics g) {
        int titleY = contentY + 2;
        int accent = section == Section.AUDIO ? GOLD : ACCENT;
        if (section == Section.AUDIO) {
            g.drawString(font, sectionTitle(section), contentX, titleY, GOLD, false);
            String track = SiegeConfig.music ? SiegeMusic.currentTrackName() : label("MÚSICA DESACTIVADA", "MUSIC OFF");
            g.drawString(font, font.plainSubstrByWidth(track, contentWidth), contentX, titleY + 13, 0xFFF0EEE8, false);
            long total = SiegeMusic.currentDurationMs();
            long remaining = Math.max(0, Math.min(total, SiegeMusic.currentRemainingMs()));
            String times = SiegeMusic.isActuallyPlaying() ? formatTime(total - remaining) + " / " + formatTime(total)
                    + "  ·  −" + formatTime(remaining) : SiegeConfig.music ? label("EN ESPERA", "WAITING") : label("SILENCIADO", "OFF");
            g.drawString(font, font.plainSubstrByWidth(times, contentWidth), contentX, titleY + 26, 0xFFABBBC5, false);
            g.fill(contentX, titleY + 39, contentX + contentWidth, titleY + 41, 0xFF29343B);
            if (SiegeMusic.isActuallyPlaying() && total > 0) g.fill(contentX, titleY + 39,
                    contentX + Math.round(contentWidth * SiegeMusic.currentProgress()), titleY + 41, GOLD);
            return;
        }
        if (compact) {
            g.drawString(font, font.plainSubstrByWidth(sectionTitle(section), contentWidth), contentX, titleY, accent, false);
            g.fill(contentX, titleY + 15, contentX + contentWidth, titleY + 16, 0xFF27343C);
            return;
        }
        String kicker = "// " + sectionKicker(section);
        g.drawString(font, kicker, contentX, titleY, accent, false);

        String title = sectionTitle(section);
        g.pose().pushPose();
        g.pose().translate(contentX, titleY + 13, 0.0F);
        float scale = compact ? 1.08F : 1.22F;
        g.pose().scale(scale, scale, 1.0F);
        g.drawString(font, font.plainSubstrByWidth(title, (int)(contentWidth / scale)), 0, 0, 0xFFF0EEE8, false);
        g.pose().popPose();

        int lineY = titleY + (compact ? 29 : 33);
        g.fill(contentX, lineY, contentX + contentWidth, lineY + 1, 0xFF27343C);
        g.fill(contentX, lineY, contentX + Math.min(contentWidth, 72), lineY + 2, accent);
    }

    private void renderSectionInformation(GuiGraphics g, int bottom) {
        int infoY = informationY - scrollOffset;

        int availableBottom = bottom - 12;
        if (infoY >= availableBottom) return;

        if (!SiegeConfig.lastSaveSucceeded) {
            renderWrapped(g, label("No se pudieron guardar los ajustes. Revisá el archivo y sus permisos.",
                    "Settings could not be saved. Check the file and its permissions."), contentX, infoY, contentWidth,
                    0xFFFFA0A5, 3, 11);
            return;
        }
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

        if (section == Section.AUDIO) return;

        renderWrapped(g, sectionDescription(section), contentX, infoY, contentWidth, 0xFF9DA8AE,
                compact ? 2 : 3, 11);
    }

    private void positionControls() {
        scrollOffset = SiegeUiLayout.clampScroll(scrollOffset, scrollMax);
        SECTION_SCROLL.put(section, scrollOffset);
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
        if (Double.isFinite(delta) && delta != 0 && x >= contentX && x < contentX + contentWidth && y >= viewportTop && y < viewportBottom) {
            if (scrollMax == 0 || delta > 0 && scrollOffset == 0 || delta < 0 && scrollOffset == scrollMax) { wheelRemainder = 0; return false; }
            wheelRemainder += delta * (compact ? 23 : 31);
            int amount = (int)wheelRemainder;
            wheelRemainder -= amount;
            scrollOffset -= amount;
            positionControls();
            return true;
        }
        return super.mouseScrolled(x, y, delta);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0 && scrollMax > 0 && x >= contentX + contentWidth - 7 && x < contentX + contentWidth
                && y >= viewportTop && y < viewportBottom) {
            draggingScrollbar = true;
            scrollGrab = y >= scrollThumbTop && y < scrollThumbTop + scrollThumbHeight ? (int)y - scrollThumbTop : scrollThumbHeight / 2;
            dragScroll(y);
            return true;
        }
        return super.mouseClicked(x, y, button);
    }

    private void dragScroll(double y) {
        double fraction = (y - viewportTop - scrollGrab) / Math.max(1, viewportBottom - viewportTop - scrollThumbHeight);
        scrollOffset = (int)Math.round(Math.max(0, Math.min(1, fraction)) * scrollMax);
        positionControls();
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (button == 0 && draggingScrollbar) { dragScroll(y); return true; }
        return super.mouseDragged(x, y, button, dx, dy);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_TAB) {
            List<AbstractWidget> order = new ArrayList<>();
            for (var child : children()) if (child instanceof AbstractWidget widget && widget.active) order.add(widget);
            if (order.isEmpty()) return false;
            int index = order.indexOf(getFocused());
            int target = index < 0 ? (hasShiftDown() ? order.size() - 1 : 0) : Math.floorMod(index + (hasShiftDown() ? -1 : 1), order.size());
            AbstractWidget next = order.get(target);
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
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        boolean wasDragging = button == 0 && draggingScrollbar;
        if (button == 0) draggingScrollbar = false;
        boolean handled = super.mouseReleased(x, y, button) || wasDragging;
        SiegeConfig.save();
        return handled;
    }

    @Override
    public void removed() {
        draggingScrollbar = false;
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
            case INTEL -> "INTEL";
            case ACCESSIBILITY -> label("ACCESIBILIDAD", "ACCESSIBILITY");
            case GRAPHICS -> label("GRÁFICOS", "GRAPHICS");
        };
    }

    private String sectionKicker(Section value) {
        return switch (value) {
            case OVERVIEW -> label("ESTADO DEL SISTEMA", "SYSTEM STATUS");
            case AUDIO -> label("CANAL DE AUDIO", "AUDIO CHANNEL");
            case INTERFACE -> label("COMPORTAMIENTO DEL MENÚ", "MENU BEHAVIOR");
            case INTEL -> label("EXPEDIENTES", "DOSSIERS");
            case ACCESSIBILITY -> label("CONFORT VISUAL", "VISUAL COMFORT");
            case GRAPHICS -> label("PERFIL DE RENDER", "RENDER PROFILE");
        };
    }

    private String sectionTitle(Section value) {
        return switch (value) {
            case OVERVIEW -> label("CENTRO DE CONTROL", "CONTROL CENTER");
            case AUDIO -> label("MÚSICA DEL MENÚ", "MENU MUSIC");
            case INTERFACE -> label("INTERFAZ SIEGE", "SIEGE INTERFACE");
            case INTEL -> label("LECTURA E INTEL", "READING AND INTEL");
            case ACCESSIBILITY -> label("ACCESIBILIDAD", "ACCESSIBILITY");
            case GRAPHICS -> label("GRÁFICOS DEL MENÚ", "MENU GRAPHICS");
        };
    }

    private String sectionDescription(Section value) {
        return switch (value) {
            case INTEL -> label("Configura la lectura, el contraste y los dossiers de portada.", "Configure reading, contrast and main-menu dossiers.");
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
                    "Abre la galería para elegir entre miniaturas, ampliar la vista previa y fijar un fondo.",
                    "Open the gallery to select thumbnails, expand the preview and pin a background.");
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
        INTEL,
        ACCESSIBILITY,
        GRAPHICS
    }
}


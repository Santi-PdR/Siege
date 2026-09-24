package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** SIEGE 5.60 settings: seven clear domains and one responsive control language. */
public final class SiegeSettingsScreen extends Screen {
    private static final int ACCENT = 0xFFE54852;
    private static final int GOLD = 0xFFD6AE65;
    private static final EnumMap<Section, Integer> SECTION_SCROLL = new EnumMap<>(Section.class);

    private final Screen parent;
    private final Section section;
    private final List<Slot> controlSlots = new ArrayList<>();
    private final List<SiegeButton> navButtons = new ArrayList<>();
    private final List<SiegeButton> musicTracks = new ArrayList<>();
    private int panelX, panelY, panelWidth, panelBottom;
    private int navX, navWidth, contentX, contentWidth;
    private int viewportTop, viewportBottom, controlStart;
    private int scrollOffset, scrollMax, contentHeight;
    private int scrollThumbTop, scrollThumbHeight;
    private boolean compact, draggingScrollbar;
    private SiegeButton shuffleButton;

    public SiegeSettingsScreen(Screen parent) { this(parent, Section.APPEARANCE); }

    private SiegeSettingsScreen(Screen parent, Section section) {
        super(Component.literal("SIEGE // SETTINGS"));
        this.parent = parent;
        this.section = section == null ? Section.APPEARANCE : section;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        controlSlots.clear(); navButtons.clear(); musicTracks.clear();
        compact = width < 680 || height < 350;
        int margin = SiegeUiLayout.safeMargin(width, height);
        panelWidth = Math.max(278, Math.min(980, width - margin * 2));
        panelX = (width - panelWidth) / 2;
        panelY = compact ? 34 : 42;
        panelBottom = height - (compact ? 8 : 16);

        addRenderableWidget(new SiegeButton(8, 7, Math.min(88, Math.max(58, width / 7)), 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), ACCENT)
                .withIcon("back").setCompactCenter(true));

        if (compact) initCompactNavigation(); else initWideNavigation();
        buildSectionControls();
        scrollOffset = SiegeUiLayout.clampScroll(SECTION_SCROLL.getOrDefault(section, 0), scrollMax);
        layoutControls();
    }

    private void initCompactNavigation() {
        navX = panelX + 8;
        navWidth = panelWidth - 16;
        int columns = SiegeUiLayout.settingsSectionColumns(navWidth);
        int gap = 3;
        int h = 17;
        int cellW = Math.max(54, (navWidth - gap * (columns - 1)) / columns);
        Section[] values = Section.values();
        for (int i = 0; i < values.length; i++) {
            Section value = values[i];
            int row = i / columns, col = i % columns;
            int x = navX + col * (cellW + gap);
            int w = col == columns - 1 ? navX + navWidth - x : cellW;
            SiegeButton button = new SiegeButton(x, panelY + 7 + row * (h + gap), w, h,
                    Component.literal(sectionLabel(value)), b -> switchSection(value), sectionAccent(value))
                    .setCompactCenter(true).setSelected(value == section);
            button.setTooltip(Tooltip.create(Component.literal(sectionDescription(value))));
            navButtons.add(addRenderableWidget(button));
        }
        int rows = (values.length + columns - 1) / columns;
        contentX = panelX + 9;
        contentWidth = panelWidth - 18;
        viewportTop = panelY + 13 + rows * (h + gap) + 28;
        viewportBottom = panelBottom - 9;
        controlStart = viewportTop + 34;
    }

    private void initWideNavigation() {
        navX = panelX + 10;
        navWidth = Math.min(170, Math.max(132, panelWidth / 5));
        int y = panelY + 33;
        int h = 20;
        for (Section value : Section.values()) {
            SiegeButton button = new SiegeButton(navX, y, navWidth, h, Component.literal(sectionLabel(value)),
                    b -> switchSection(value), sectionAccent(value)).withIcon(sectionIcon(value)).setSelected(value == section);
            button.setTooltip(Tooltip.create(Component.literal(sectionDescription(value))));
            navButtons.add(addRenderableWidget(button));
            y += h + 5;
        }
        contentX = navX + navWidth + 17;
        contentWidth = panelX + panelWidth - 11 - contentX;
        viewportTop = panelY + 32;
        viewportBottom = panelBottom - 10;
        controlStart = viewportTop + 42;
    }

    private void switchSection(Section value) {
        if (value == section) return;
        SECTION_SCROLL.put(section, scrollOffset);
        SiegeUiSounds.category();
        minecraft.setScreen(new SiegeSettingsScreen(parent, value));
    }

    private void buildSectionControls() {
        int h = compact ? 20 : 22;
        int sliderH = compact ? 26 : 30;
        int gap = compact ? 5 : 6;
        int y = 0;
        switch (section) {
            case APPEARANCE -> {
                add(graphicsButton(h), y); y += h + gap;
                add(toggle(label("MOSTRAR BUILD", "SHOW BUILD LABEL"), () -> SiegeConfig.showBuildLabel,
                        () -> SiegeConfig.showBuildLabel = !SiegeConfig.showBuildLabel, SiegeTheme.RED, h), y); y += h + gap;
                add(new SiegeSlider(0, 0, 100, sliderH,
                        Component.literal(label("OSCURIDAD DEL PANEL", "PANEL DARKNESS")),
                        Math.round((SiegeConfig.panelDarkness - 20) * 100.0F / 70.0F),
                        p -> SiegeConfig.panelDarkness = 20 + Math.round(p * 70.0F / 100.0F))
                        .withValueText(p -> (20 + Math.round(p * 70.0F / 100.0F)) + "%"), y); y += sliderH + gap;
                add(profileButton(SiegeClientProfile.Profile.CINEMATIC, h), y); y += h + gap;
                add(profileButton(SiegeClientProfile.Profile.TACTICAL, h), y); y += h + gap;
                add(profileButton(SiegeClientProfile.Profile.PERFORMANCE, h), y); y += h + gap;
            }
            case MOTION -> {
                add(toggle(label("EFECTOS TÁCTICOS", "TACTICAL EFFECTS"), () -> SiegeConfig.menuEffects,
                        () -> SiegeConfig.menuEffects = !SiegeConfig.menuEffects, SiegeTheme.RED, h), y); y += h + gap;
                add(toggle(label("MOVIMIENTO REDUCIDO", "REDUCED MOTION"), () -> SiegeConfig.reducedMotion,
                        () -> SiegeConfig.reducedMotion = !SiegeConfig.reducedMotion, SiegeTheme.CYAN, h), y); y += h + gap;
                add(toggle(label("REDUCIR DESTELLOS", "REDUCE FLASHES"), () -> SiegeConfig.reduceFlashes,
                        () -> SiegeConfig.reduceFlashes = !SiegeConfig.reduceFlashes, SiegeTheme.GREEN, h), y); y += h + gap;
                add(toggle(label("INTERFERENCIA DEL TÍTULO", "TITLE INTERFERENCE"), () -> SiegeConfig.titleInterference,
                        () -> SiegeConfig.titleInterference = !SiegeConfig.titleInterference, SiegeTheme.RED, h), y); y += h + gap;
                add(new SiegeSlider(0, 0, 100, sliderH,
                        Component.literal(label("INTENSIDAD DE INTERFERENCIA", "INTERFERENCE INTENSITY")),
                        SiegeConfig.interferenceIntensity, p -> SiegeConfig.interferenceIntensity = p)
                        .withValueText(p -> p + "%"), y); y += sliderH + gap;
            }
            case AUDIO -> {
                add(toggle(label("MÚSICA", "MUSIC"), () -> SiegeConfig.music,
                        () -> SiegeConfig.music = !SiegeConfig.music, GOLD, h), y); y += h + gap;
                add(new SiegeSlider(0, 0, 100, sliderH,
                        Component.literal(label("VOLUMEN DE MÚSICA", "MUSIC VOLUME")), SiegeConfig.musicVolume,
                        SiegeMusic::setVolumeLive).withAccent(GOLD), y); y += sliderH + gap;
                int third = Math.max(46, (contentWidth - gap * 2) / 3);
                SiegeButton prev = new SiegeButton(0, 0, third, h, Component.literal("← " + label("ANT.", "PREV")), b -> { SiegeMusic.previousTrack(); SiegeUiSounds.nextTrack(); }, GOLD).setCompactCenter(true);
                SiegeButton restart = new SiegeButton(0, 0, third, h, Component.literal(label("REINICIAR", "RESTART")), b -> { SiegeMusic.restartTrack(); SiegeUiSounds.confirm(); }, GOLD).setCompactCenter(true);
                SiegeButton next = new SiegeButton(0, 0, third, h, Component.literal(label("SIG. →", "NEXT →")), b -> { SiegeMusic.nextTrack(); SiegeUiSounds.nextTrack(); }, GOLD).setCompactCenter(true);
                addRow(List.of(prev, restart, next), y, gap); y += h + gap;
                add(toggle(label("AVISO DE NUEVA PISTA", "NEW TRACK NOTICE"), () -> SiegeConfig.trackAnnouncements,
                        () -> SiegeConfig.trackAnnouncements = !SiegeConfig.trackAnnouncements, GOLD, h), y); y += h + gap;
                add(new SiegeSlider(0, 0, 100, sliderH,
                        Component.literal(label("DURACIÓN DEL AVISO", "NOTICE DURATION")),
                        Math.round((SiegeConfig.trackNoticeSeconds - 3) * 100.0F / 12.0F),
                        p -> SiegeConfig.trackNoticeSeconds = 3 + Math.round(p * 12.0F / 100.0F))
                        .withAccent(GOLD).withValueText(p -> (3 + Math.round(p * 12.0F / 100.0F)) + "s"), y); y += sliderH + gap;
                shuffleButton = new SiegeButton(0, 0, contentWidth, h, Component.literal(label("ORDEN ALEATORIO SIN REPETIR", "SHUFFLE WITHOUT REPEATS")),
                        b -> { SiegeMusic.selectTrack(-1); SiegeUiSounds.confirm(); refreshMusicStates(); }, GOLD).withIcon("music");
                add(shuffleButton, y); y += h + gap;
                List<String> tracks = SiegeMusic.trackNames();
                for (int i = 0; i < tracks.size(); i++) {
                    int index = i;
                    SiegeButton track = new SiegeButton(0, 0, contentWidth, h, Component.literal((i + 1) + "  " + tracks.get(i)),
                            b -> { SiegeMusic.selectTrack(index); SiegeUiSounds.selection(); refreshMusicStates(); }, GOLD).withIcon("music");
                    musicTracks.add(track); add(track, y); y += h + gap;
                }
                add(toggle(label("SONIDOS DE INTERFAZ", "UI SOUNDS"), () -> SiegeConfig.uiSounds,
                        () -> SiegeConfig.uiSounds = !SiegeConfig.uiSounds, SiegeTheme.CYAN, h), y); y += h + gap;
                add(new SiegeSlider(0, 0, 100, sliderH,
                        Component.literal(label("VOLUMEN UI", "UI VOLUME")), SiegeConfig.uiVolume,
                        p -> SiegeConfig.uiVolume = p).withAccent(SiegeTheme.CYAN), y); y += sliderH + gap;
                add(toggle(label("SONIDO AL SEÑALAR", "HOVER SOUND"), () -> SiegeConfig.hoverSounds,
                        () -> SiegeConfig.hoverSounds = !SiegeConfig.hoverSounds, SiegeTheme.CYAN, h), y); y += h + gap;
            }
            case INTEL -> {
                add(toggle(label("MODO LECTURA AL ABRIR", "OPEN IN READING MODE"), () -> SiegeConfig.intelReadingMode,
                        () -> SiegeConfig.intelReadingMode = !SiegeConfig.intelReadingMode, GOLD, h), y); y += h + gap;
                add(toggle(label("LECTURA CÓMODA", "COMFORTABLE READING"), () -> SiegeConfig.comfortableReading,
                        () -> SiegeConfig.comfortableReading = !SiegeConfig.comfortableReading, GOLD, h), y); y += h + gap;
                add(toggle(label("PAPEL OSCURO", "DARK PAPER"), () -> SiegeConfig.darkIntelPaper,
                        () -> SiegeConfig.darkIntelPaper = !SiegeConfig.darkIntelPaper, GOLD, h), y); y += h + gap;
                add(toggle(label("INTEL EN PORTADA", "HOME INTEL"), () -> SiegeConfig.mainMenuIntel,
                        () -> SiegeConfig.mainMenuIntel = !SiegeConfig.mainMenuIntel, GOLD, h), y); y += h + gap;
                add(toggle(label("ANIMACIONES DE INTEL", "INTEL ANIMATIONS"), () -> SiegeConfig.animatedIntel,
                        () -> SiegeConfig.animatedIntel = !SiegeConfig.animatedIntel, GOLD, h), y); y += h + gap;
                add(toggle(label("ROTACIÓN AUTOMÁTICA", "AUTO ROTATE"), () -> SiegeConfig.autoRotateIntel,
                        () -> SiegeConfig.autoRotateIntel = !SiegeConfig.autoRotateIntel, GOLD, h), y); y += h + gap;
                add(toggle(label("PAUSA AL LEER", "PAUSE WHILE READING"), () -> SiegeConfig.pauseIntelOnHover,
                        () -> SiegeConfig.pauseIntelOnHover = !SiegeConfig.pauseIntelOnHover, GOLD, h), y); y += h + gap;
                add(toggle(label("PROGRESO DE ROTACIÓN", "ROTATION PROGRESS"), () -> SiegeConfig.showIntelProgress,
                        () -> SiegeConfig.showIntelProgress = !SiegeConfig.showIntelProgress, GOLD, h), y); y += h + gap;
                add(toggle(label("ESTADO DEL DOSSIER", "DOSSIER STATE"), () -> SiegeConfig.showIntelState,
                        () -> SiegeConfig.showIntelState = !SiegeConfig.showIntelState, GOLD, h), y); y += h + gap;
                add(new SiegeButton(0, 0, contentWidth, h, Component.literal(label("ABRIR INTEL", "OPEN INTEL")),
                        b -> { SiegeUiSounds.confirm(); minecraft.setScreen(new IntelScreenV3(this)); }, GOLD).withIcon("intel"), y); y += h + gap;
            }
            case ACCESSIBILITY -> {
                add(toggle(label("CONTRASTE AUTOMÁTICO", "AUTO CONTRAST"), () -> SiegeConfig.autoContrast,
                        () -> SiegeConfig.autoContrast = !SiegeConfig.autoContrast, SiegeTheme.CYAN, h), y); y += h + gap;
                add(toggle(label("ALTO CONTRASTE", "HIGH CONTRAST"), () -> SiegeConfig.highContrast,
                        () -> SiegeConfig.highContrast = !SiegeConfig.highContrast, SiegeTheme.CYAN, h), y); y += h + gap;
                add(toggle(label("REDUCIR DESTELLOS", "REDUCE FLASHES"), () -> SiegeConfig.reduceFlashes,
                        () -> SiegeConfig.reduceFlashes = !SiegeConfig.reduceFlashes, SiegeTheme.GREEN, h), y); y += h + gap;
                add(toggle(label("MOVIMIENTO REDUCIDO", "REDUCED MOTION"), () -> SiegeConfig.reducedMotion,
                        () -> SiegeConfig.reducedMotion = !SiegeConfig.reducedMotion, SiegeTheme.GREEN, h), y); y += h + gap;
                add(profileButton(SiegeClientProfile.Profile.CALM, h), y); y += h + gap;
                add(profileButton(SiegeClientProfile.Profile.READING, h), y); y += h + gap;
            }
            case BACKGROUNDS -> {
                add(toggle(label("FONDOS ANIMADOS", "ANIMATED BACKGROUNDS"), () -> SiegeConfig.animatedBackgrounds,
                        () -> SiegeConfig.animatedBackgrounds = !SiegeConfig.animatedBackgrounds, SiegeTheme.BLUE, h), y); y += h + gap;
                add(new SiegeSlider(0, 0, 100, sliderH,
                        Component.literal(label("MOVIMIENTO DEL FONDO", "BACKGROUND MOTION")),
                        SiegeConfig.backgroundMotionIntensity,
                        p -> SiegeConfig.backgroundMotionIntensity = p)
                        .withAccent(SiegeTheme.CYAN).withValueText(p -> p + "%"), y); y += sliderH + gap;
                add(new SiegeSlider(0, 0, 100, sliderH,
                        Component.literal(label("DURACIÓN DE ESCENA", "SCENE DURATION")),
                        Math.round((SiegeConfig.backgroundSceneSeconds - 12) * 100.0F / 48.0F),
                        p -> SiegeConfig.backgroundSceneSeconds = 12 + Math.round(p * 48.0F / 100.0F))
                        .withAccent(SiegeTheme.BLUE).withValueText(p -> (12 + Math.round(p * 48.0F / 100.0F)) + "s"), y); y += sliderH + gap;
                add(new SiegeSlider(0, 0, 100, sliderH,
                        Component.literal(label("FUNDIDO ENTRE ESCENAS", "SCENE CROSSFADE")),
                        SiegeConfig.backgroundCrossfadeSeconds * 10,
                        p -> SiegeConfig.backgroundCrossfadeSeconds = Math.round(p * 10.0F / 100.0F))
                        .withAccent(SiegeTheme.CYAN).withValueText(p -> Math.round(p * 10.0F / 100.0F) + "s"), y); y += sliderH + gap;
                add(new SiegeSlider(0, 0, 100, sliderH,
                        Component.literal(label("OSCURIDAD DEL FONDO", "BACKGROUND DARKNESS")),
                        Math.round(SiegeConfig.backgroundDarkness * 100.0F / 70.0F),
                        p -> SiegeConfig.backgroundDarkness = Math.round(p * 70.0F / 100.0F))
                        .withAccent(SiegeTheme.BLUE).withValueText(p -> Math.round(p * 70.0F / 100.0F) + "%"), y); y += sliderH + gap;
                add(toggle(label("LÍNEAS DE ESCANEO", "SCANLINES"), () -> SiegeConfig.scanlines,
                        () -> { SiegeConfig.scanlines = !SiegeConfig.scanlines; if (SiegeConfig.scanlines && SiegeConfig.scanlineIntensity == 0) SiegeConfig.scanlineIntensity = 45; }, SiegeTheme.BLUE, h), y); y += h + gap;
                add(new SiegeSlider(0, 0, 100, sliderH,
                        Component.literal(label("INTENSIDAD DE SCANLINES", "SCANLINE INTENSITY")), SiegeConfig.scanlineIntensity,
                        p -> { SiegeConfig.scanlineIntensity = p; SiegeConfig.scanlines = p > 0; })
                        .withAccent(SiegeTheme.BLUE), y); y += sliderH + gap;
                add(new SiegeButton(0, 0, contentWidth, h, Component.literal(label("GALERÍA DE FONDOS", "BACKGROUND GALLERY")),
                        b -> { SECTION_SCROLL.put(section, scrollOffset); SiegeUiSounds.confirm(); minecraft.setScreen(new SiegeSceneScreen(this)); }, SiegeTheme.BLUE)
                        .withIcon("image"), y); y += h + gap;
                add(new SiegeButton(0, 0, contentWidth, h, Component.literal(label("REACTIVAR ROTACIÓN", "RESUME ROTATION")),
                        b -> { SiegeConfig.selectedScene = -1; SiegeConfig.animatedBackgrounds = true; SiegeConfig.save(); SiegeUiSounds.confirm(); }, SiegeTheme.BLUE)
                        .withIcon("play"), y); y += h + gap;
            }
            case SYSTEM -> {
                add(new SiegeButton(0, 0, contentWidth, h, Component.literal(label("CENTRO DE COMANDO", "COMMAND CENTER")),
                        b -> { SiegeUiSounds.confirm(); minecraft.setScreen(new SiegeSystemScreen(this)); }, SiegeTheme.CYAN).withIcon("shield"), y); y += h + gap;
                add(new SiegeButton(0, 0, contentWidth, h, Component.literal(label("DIAGNÓSTICO Y RECUPERACIÓN", "DIAGNOSTICS & RECOVERY")),
                        b -> { SiegeUiSounds.confirm(); minecraft.setScreen(new SiegeDiagnosticsScreen(this)); }, SiegeRuntimeStatus.healthAccent()).withIcon("overview"), y); y += h + gap;
                add(new SiegeButton(0, 0, contentWidth, h, Component.literal(label("AJUSTES NATIVOS DE MINECRAFT", "MINECRAFT OPTIONS")),
                        b -> { SiegeUiSounds.click(); minecraft.setScreen(new OptionsScreen(this, minecraft.options)); }, SiegeTheme.ORANGE).withIcon("settings"), y); y += h + gap;
                add(new SiegeButton(0, 0, contentWidth, h, Component.literal(label("GUÍA OPERATIVA", "OPERATIONAL GUIDE")),
                        b -> { SiegeUiSounds.click(); minecraft.setScreen(new SiegeGuideScreen(this)); }, GOLD).withIcon("intel"), y); y += h + gap;
                add(toggle(label("CONFIRMAR AL SALIR", "CONFIRM BEFORE QUIT"), () -> SiegeConfig.confirmQuit,
                        () -> SiegeConfig.confirmQuit = !SiegeConfig.confirmQuit, SiegeTheme.RED, h), y); y += h + gap;
                add(new SiegeButton(0, 0, contentWidth, h, Component.literal(label("RESTABLECER CONFIGURACIÓN SIEGE", "RESET SIEGE SETTINGS")),
                        b -> confirmReset(), SiegeTheme.RED).withIcon("warning"), y); y += h + gap;
            }
        }
        contentHeight = Math.max(0, y);
        int visible = Math.max(1, viewportBottom - controlStart);
        scrollMax = Math.max(0, contentHeight - visible);
    }

    private void add(AbstractWidget widget, int baseY) {
        widget.setX(contentX);
        widget.setWidth(contentWidth);
        addRenderableWidget(widget);
        controlSlots.add(new Slot(widget, baseY));
    }

    private void addRow(List<? extends AbstractWidget> widgets, int baseY, int gap) {
        int count = widgets.size();
        int cell = Math.max(1, (contentWidth - gap * (count - 1)) / count);
        for (int i = 0; i < count; i++) {
            AbstractWidget widget = widgets.get(i);
            int x = contentX + i * (cell + gap);
            int w = i == count - 1 ? contentX + contentWidth - x : cell;
            widget.setX(x); widget.setWidth(w);
            addRenderableWidget(widget);
            controlSlots.add(new Slot(widget, baseY, x - contentX, w));
        }
    }

    private SiegeButton graphicsButton(int h) {
        SiegeButton button = new SiegeButton(0, 0, contentWidth, h, Component.literal(graphicsText()), b -> {
            SiegeConfig.graphics = SiegeConfig.graphics.next(); SiegeConfig.save(); SiegeUiSounds.selection(); b.setMessage(Component.literal(graphicsText()));
        }, SiegeTheme.ORANGE).withIcon("image");
        button.setTooltip(Tooltip.create(Component.literal(label(
                "Rendimiento reduce trabajo visual; Equilibrado mantiene identidad; Cinemático activa la presentación completa.",
                "Performance reduces visual work; Balanced keeps the identity; Cinematic enables the full presentation."))));
        return button;
    }

    private SiegeButton profileButton(SiegeClientProfile.Profile profile, int h) {
        SiegeButton button = new SiegeButton(0, 0, contentWidth, h,
                Component.literal(label("APLICAR PERFIL ", "APPLY PROFILE ") + SiegeClientProfile.label(profile, spanish())), b -> {
            SiegeClientProfile.apply(profile); SiegeUiSounds.confirm(); minecraft.setScreen(new SiegeSettingsScreen(parent, section));
        }, SiegeClientProfile.accent(profile)).withIcon(SiegeClientProfile.icon(profile));
        button.setTooltip(Tooltip.create(Component.literal(SiegeClientProfile.description(profile, spanish()))));
        return button;
    }

    private SiegeButton toggle(String text, Flag get, Runnable flip, int accent, int h) {
        SiegeButton button = new SiegeButton(0, 0, contentWidth, h, Component.literal(text), b -> {
            flip.run(); SiegeConfig.save(); SiegeUiSounds.click(); updateToggle((SiegeButton)b, text, get.get());
        }, accent).withFaceLabel(text);
        updateToggle(button, text, get.get());
        button.setTooltip(Tooltip.create(Component.literal(help(text))));
        return button;
    }

    private void updateToggle(SiegeButton button, String text, boolean enabled) {
        button.setMessage(Component.literal(text));
        button.setSelected(enabled).withBadge(label(enabled ? "SÍ" : "NO", enabled ? "ON" : "OFF"));
    }

    private void confirmReset() {
        minecraft.setScreen(new ConfirmScreen(confirmed -> {
            if (confirmed) {
                SiegeConfig.resetDefaults(); SiegeUiSounds.confirm(); minecraft.setScreen(new SiegeSettingsScreen(parent, Section.APPEARANCE));
            } else minecraft.setScreen(new SiegeSettingsScreen(parent, Section.SYSTEM));
        }, Component.literal(label("¿RESTABLECER SIEGE?", "RESET SIEGE?")),
                Component.literal(label("Se restaurarán únicamente las preferencias del cliente SIEGE.", "Only SIEGE client preferences will be restored."))));
    }

    private void layoutControls() {
        for (Slot slot : controlSlots) {
            AbstractWidget widget = slot.widget();
            int y = controlStart + slot.baseY() - scrollOffset;
            widget.setY(y);
            widget.setX(contentX + slot.relativeX());
            widget.setWidth(slot.width() <= 0 ? contentWidth : slot.width());
            widget.visible = y >= controlStart && y + widget.getHeight() <= viewportBottom;
        }
        refreshMusicStates();
    }

    private void refreshMusicStates() {
        if (shuffleButton != null) shuffleButton.setSelected(SiegeConfig.selectedTrack < 0)
                .withBadge(SiegeConfig.selectedTrack < 0 ? label("ACTIVO", "ACTIVE") : "");
        for (int i = 0; i < musicTracks.size(); i++) {
            SiegeButton track = musicTracks.get(i);
            boolean selected = SiegeConfig.selectedTrack == i;
            boolean live = SiegeMusic.isActuallyPlaying() && SiegeMusic.currentTrackNumber() == i + 1;
            track.setSelected(selected).withBadge(live ? label("SUENA", "LIVE") : selected ? label("FIJO", "PINNED") : "");
            track.withIcon(live ? "play" : "music");
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        layoutControls();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xD008090B : 0xB008090B);
        SiegeTheme.panel(g, panelX, panelY, panelWidth, panelBottom - panelY, sectionAccent(section));

        String detail = sectionLabel(section) + " · " + SiegeClientProfile.shortLabel(SiegeClientProfile.detect(), spanish());
        SiegeScreenChrome.renderHeader(g, this, panelX + 10, panelY + 7, panelWidth - 20, detail);
        if (!compact) {
            g.drawString(font, label("// SECCIONES", "// SECTIONS"), navX, panelY + 20, SiegeTheme.MUTED, false);
            g.fill(contentX - 9, panelY + 29, contentX - 8, panelBottom - 8, 0xFF343A3F);
        }
        renderSectionIntro(g);
        renderScrollBar(g, mouseX, mouseY);

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        refreshMusicStates();
    }

    private void renderSectionIntro(GuiGraphics g) {
        int y = viewportTop;
        String kicker = sectionKicker(section);
        g.drawString(font, kicker, contentX, y, sectionAccent(section), false);
        g.drawString(font, fit(sectionTitle(section), contentWidth), contentX, y + 12,
                SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK, false);
        String profile = SiegeClientProfile.detect() == SiegeClientProfile.Profile.CUSTOM
                ? "CUSTOM → " + SiegeClientProfile.shortLabel(SiegeProfileMetrics.nearest(), spanish()) + " " + SiegeProfileMetrics.fitPercent(SiegeProfileMetrics.nearest()) + "%"
                : SiegeClientProfile.label(SiegeClientProfile.detect(), spanish());
        g.drawString(font, fit(profile, Math.max(50, contentWidth / 2)), contentX + contentWidth - font.width(fit(profile, Math.max(50, contentWidth / 2))), y,
                SiegeClientProfile.accent(SiegeClientProfile.detect()), false);
    }

    private void renderScrollBar(GuiGraphics g, int mouseX, int mouseY) {
        if (scrollMax <= 0) return;
        int trackH = Math.max(1, viewportBottom - controlStart);
        int thumb = SiegeUiLayout.scrollThumb(trackH, trackH + scrollMax);
        int top = controlStart + (trackH - thumb) * scrollOffset / Math.max(1, scrollMax);
        scrollThumbTop = top; scrollThumbHeight = thumb;
        boolean hot = mouseX >= contentX + contentWidth - 7 && mouseX <= contentX + contentWidth
                && mouseY >= controlStart && mouseY < viewportBottom;
        int x = contentX + contentWidth - 3;
        g.fill(x, controlStart, x + 3, viewportBottom, 0xFF252A2E);
        g.fill(x, top, x + 3, top + thumb, hot || draggingScrollbar ? SiegeTheme.FOCUS : sectionAccent(section));
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (delta != 0.0D && x >= contentX && x <= contentX + contentWidth && y >= viewportTop && y < viewportBottom) {
            scrollOffset = SiegeUiLayout.clampScroll(scrollOffset - (int)Math.round(delta * 30), scrollMax);
            SECTION_SCROLL.put(section, scrollOffset); layoutControls(); return true;
        }
        return super.mouseScrolled(x, y, delta);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0 && scrollMax > 0 && x >= contentX + contentWidth - 8 && x <= contentX + contentWidth
                && y >= controlStart && y < viewportBottom) {
            draggingScrollbar = true; scrollTo(y); return true;
        }
        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (draggingScrollbar && button == 0) { scrollTo(y); return true; }
        return super.mouseDragged(x, y, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        boolean handled = button == 0 && draggingScrollbar;
        if (button == 0) draggingScrollbar = false;
        SiegeConfig.save();
        return super.mouseReleased(x, y, button) || handled;
    }

    private void scrollTo(double y) {
        int track = Math.max(1, viewportBottom - controlStart - scrollThumbHeight);
        double fraction = (y - controlStart - scrollThumbHeight / 2.0) / track;
        scrollOffset = SiegeUiLayout.clampScroll((int)Math.round(Math.max(0, Math.min(1, fraction)) * scrollMax), scrollMax);
        SECTION_SCROLL.put(section, scrollOffset); layoutControls();
    }

    private String graphicsText() {
        String value = switch (SiegeConfig.graphics) {
            case PERFORMANCE -> label("RENDIMIENTO", "PERFORMANCE");
            case BALANCED -> label("EQUILIBRADO", "BALANCED");
            case CINEMATIC -> label("CINEMÁTICO", "CINEMATIC");
        };
        return label("PERFIL DE RENDER: ", "RENDER PROFILE: ") + value;
    }

    private String help(String text) {
        if (text.contains("INTERFERENCIA") || text.contains("INTERFERENCE")) return label(
                "Controla el efecto de señal del título. Reducir destellos tiene prioridad.",
                "Controls the title signal effect. Reduce Flashes takes priority.");
        if (text.contains("MOVIMIENTO DEL FONDO") || text.contains("BACKGROUND MOTION")) return label(
                "Define cuánto se desplazan lentamente las escenas. Movimiento reducido y Rendimiento lo anulan.",
                "Sets how much scenes drift slowly. Reduced Motion and Performance override it.");
        if (text.contains("DURACIÓN DE ESCENA") || text.contains("SCENE DURATION")) return label(
                "Tiempo que una escena permanece activa antes de pasar a la siguiente.",
                "How long a scene remains active before rotating to the next one.");
        if (text.contains("FUNDIDO") || text.contains("CROSSFADE")) return label(
                "Duración de la transición entre fondos. En 0 s el cambio es inmediato.",
                "Duration of the transition between scenes. At 0 s the change is immediate.");
        if (text.contains("SCAN") || text.contains("ESCANEO")) return label(
                "Líneas discretas sobre el fondo; Rendimiento las desactiva.", "Subtle lines over the background; Performance disables them.");
        if (text.contains("CONTRASTE") || text.contains("CONTRAST")) return label(
                "Asegura legibilidad mínima sin reemplazar la escena seleccionada.", "Ensures minimum readability without replacing the selected scene.");
        if (text.contains("MÚSICA") || text.contains("MUSIC")) return label(
                "La banda sonora solo pertenece a los menús, nunca al mundo cargado.", "The soundtrack belongs to menus only, never a loaded world.");
        if (text.contains("ROTACIÓN") || text.contains("ROTATE")) return label(
                "Controla el cambio automático; las flechas manuales siguen disponibles.", "Controls automatic changes; manual arrows remain available.");
        return label("Preferencia de presentación del cliente SIEGE. Se guarda al modificarla.",
                "SIEGE client presentation preference. It is saved when changed.");
    }

    private String sectionLabel(Section value) {
        return switch (value) {
            case APPEARANCE -> label("APARIENCIA", "APPEARANCE");
            case MOTION -> label("MOVIMIENTO", "MOTION");
            case AUDIO -> "AUDIO";
            case INTEL -> "INTEL";
            case ACCESSIBILITY -> label("ACCESIBILIDAD", "ACCESSIBILITY");
            case BACKGROUNDS -> label("FONDOS", "BACKGROUNDS");
            case SYSTEM -> label("SISTEMA", "SYSTEM");
        };
    }

    private String sectionKicker(Section value) {
        return switch (value) {
            case APPEARANCE -> label("PRESENTACIÓN", "PRESENTATION");
            case MOTION -> label("RITMO VISUAL", "VISUAL MOTION");
            case AUDIO -> label("CANAL DE AUDIO", "AUDIO CHANNEL");
            case INTEL -> label("EXPEDIENTES", "DOSSIERS");
            case ACCESSIBILITY -> label("LECTURA Y CONFORT", "READABILITY & COMFORT");
            case BACKGROUNDS -> label("ESCENAS DEL MENÚ", "MENU SCENES");
            case SYSTEM -> label("CLIENTE Y RECUPERACIÓN", "CLIENT & RECOVERY");
        };
    }

    private String sectionTitle(Section value) { return sectionLabel(value) + " // SIEGE " + SiegeRuntimeStatus.version(); }

    private String sectionDescription(Section value) {
        return switch (value) {
            case APPEARANCE -> label("Render, panel y perfiles visuales.", "Render, panel and visual profiles.");
            case MOTION -> label("Transiciones, interferencia y reducción de movimiento.", "Transitions, interference and reduced motion.");
            case AUDIO -> label("Música, transiciones, avisos y sonidos UI.", "Music, transitions, notices and UI sounds.");
            case INTEL -> label("Lectura, dossier y rotación de expedientes.", "Reading, dossier and file rotation.");
            case ACCESSIBILITY -> label("Contraste, destellos y perfiles de confort.", "Contrast, flashes and comfort profiles.");
            case BACKGROUNDS -> label("Escena, movimiento, tiempos, oscuridad, scanlines y galería.",
                    "Scene, motion, timing, darkness, scanlines and gallery.");
            case SYSTEM -> label("Comando, diagnóstico, opciones nativas y recuperación.", "Command, diagnostics, native options and recovery.");
        };
    }

    private int sectionAccent(Section value) {
        return switch (value) {
            case APPEARANCE -> SiegeTheme.RED;
            case MOTION -> SiegeTheme.ORANGE;
            case AUDIO -> SiegeTheme.GOLD;
            case INTEL -> SiegeTheme.GOLD;
            case ACCESSIBILITY -> SiegeTheme.CYAN;
            case BACKGROUNDS -> SiegeTheme.BLUE;
            case SYSTEM -> SiegeTheme.GREEN;
        };
    }

    private String sectionIcon(Section value) {
        return switch (value) {
            case APPEARANCE -> "image";
            case MOTION -> "play";
            case AUDIO -> "music";
            case INTEL -> "intel";
            case ACCESSIBILITY -> "eye";
            case BACKGROUNDS -> "world";
            case SYSTEM -> "settings";
        };
    }

    private String fit(String value, int w) {
        if (font.width(value) <= w) return value;
        return font.plainSubstrByWidth(value, Math.max(1, w - font.width("…"))) + "…";
    }

    private boolean spanish() { return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_"); }
    private String label(String es, String en) { return spanish() ? es : en; }

    @Override
    public void removed() { SECTION_SCROLL.put(section, scrollOffset); SiegeConfig.save(); super.removed(); }
    @Override
    public void onClose() { SECTION_SCROLL.put(section, scrollOffset); SiegeConfig.save(); SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }

    private interface Flag { boolean get(); }
    private record Slot(AbstractWidget widget, int baseY, int relativeX, int width) {
        Slot(AbstractWidget widget, int baseY) { this(widget, baseY, 0, -1); }
    }
    private enum Section { APPEARANCE, MOTION, AUDIO, INTEL, ACCESSIBILITY, BACKGROUNDS, SYSTEM }
}

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
    private static final int ACCENT = SiegeTheme.RED;
    private static final int WARNING = SiegeTheme.WARNING;
    private static final int GOLD = SiegeTheme.GOLD;
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
    private SiegeButton shuffleButton, sampleButton;
    private SiegeSlider noticeDuration;
    private final List<AbstractWidget> musicControls = new ArrayList<>();
    private long calmAppliedUntil;
    private final List<Runnable> toggleRefreshers = new ArrayList<>();

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
        musicControls.clear();
        toggleRefreshers.clear();
        shuffleButton = sampleButton = null;
        noticeDuration = null;
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
                if (section == Section.AUDIO && widget instanceof SiegeSlider && widget != noticeDuration)
                    musicControls.add(widget);
            }
        }
        informationY = lastBottom + 12;
        scrollMax = Math.max(0, informationY + (section == Section.OVERVIEW ? 94 : 44) - viewportBottom);
        scrollOffset = previousScroll;
        positionControls();
    }
... (rest of the file follows, replace colors with constants)

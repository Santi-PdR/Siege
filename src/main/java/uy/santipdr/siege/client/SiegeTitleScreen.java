package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public final class SiegeTitleScreen extends Screen {
    private static final int ACCENT = SiegeTheme.CYAN;
    private int menuX;
    private int menuWidth;
    private int menuTop;
    private int menuBottom;
    private final SiegePreviewClock previewClock = new SiegePreviewClock();
    private long manualIntelPreviewUntil;
    private int previewX = -1;
    private int previewY = -1;
    private int previewW;
    private int previewH;
    private int previewCardHeight;
    private int previewGap;
    private boolean previewDual;
    private IntelEntry primaryPreviewEntry;
    private IntelEntry secondaryPreviewEntry;
    private int lastRenderedPreview = -1;
    private int previewTransitionDirection = 1;
    private long previewTransitionStarted;
    private boolean previewReading;
    private long previewCycleStartedAt;
    private SiegeButton musicButton;

    public SiegeTitleScreen() {
        super(Component.literal("Eternal Craft: SIEGE"));
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        boolean compact = SiegeUiLayout.compactTitle(width, height);
        double guiScale = minecraft.getWindow().getGuiScale();
        boolean scaleThree = guiScale >= 2.75D && guiScale < 3.75D && !compact;
        int margin = compact ? 9 : Math.max(14, width / 55);
        menuWidth = scaleThree
                ? Math.min(218, Math.max(198, width / 5))
                : Math.min(compact ? 176 : 212, Math.max(138, width / (compact ? 2 : 5)));
        menuWidth = Math.min(menuWidth, width - margin * 2);
        int buttonHeight = compact ? 18 : scaleThree ? 23 : 22;
        int gap = compact ? 3 : scaleThree ? 5 : 5;
        int totalHeight = buttonHeight * 5 + gap * 4;
        menuX = margin;

        int desiredTop = compact ? 72 : 112;
        menuTop = desiredTop;
        menuTop = Math.min(menuTop, Math.max(50, height - totalHeight - 14));
        menuBottom = menuTop + totalHeight;

        int y = menuTop;
        SiegeButton deployment = addRenderableWidget(command(menuX, y, menuWidth, buttonHeight, "siege.menu.deployment",
                b -> minecraft.setScreen(new SiegeMultiplayerScreen(this))));
        setInitialFocus(deployment);
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.intel",
                b -> minecraft.setScreen(new IntelScreenV3(this))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.armory",
                b -> minecraft.setScreen(new OptionsScreen(this, minecraft.options))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.settings",
                b -> minecraft.setScreen(new SiegeSettingsScreen(this))));
        addRenderableWidget(command(menuX, y += buttonHeight + gap, menuWidth, buttonHeight, "siege.menu.quit",
                b -> requestQuit()));

        int trackWidth = compact ? 88 : 100;
        int musicY = SiegeUiLayout.musicButtonY(height, compact);
        musicButton = addRenderableWidget(new SiegeButton(width - trackWidth - 9, musicY, trackWidth, compact ? 18 : 20,
                Component.literal(musicButtonLabel()), b -> changeTrack(), SiegeTheme.WARNING).setCompactCenter(true).withIcon("music"));

    }

    private SiegeButton command(int x, int y, int width, int height, String key, Button.OnPress press) {
        return new SiegeButton(x, y, width, height, Component.translatable(key), button -> {
            SiegeUiSounds.click();
            press.onPress(button);
        }, SiegeTheme.RED).setMainMenuStyle(true).withIcon(switch (key) {
            case "siege.menu.deployment" -> "connect";
            case "siege.menu.intel" -> "intel";
            case "siege.menu.settings", "siege.menu.armory" -> "settings";
            default -> "";
        });
    }
... (rest of the file follows, replace colors with constants)

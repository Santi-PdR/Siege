package uy.santipdr.siege.client;

import java.util.EnumMap;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * SIEGE 0.60 command center. Gameplay knowledge remains in Intel while this
 * surface controls profiles, diagnostics, recovery, compatibility and accessibility.
 */
public final class SiegeSystemScreen extends Screen {
    private final Screen parent;
    private final EnumMap<SiegeClientProfile.Profile, SiegeButton> profileButtons =
            new EnumMap<>(SiegeClientProfile.Profile.class);
    private SiegeButton contrastButton;
    private SiegeButton flashesButton;
    private SiegeButton diagnosticsButton;
    private int panelX, panelY, panelW, panelBottom;
    private int contentX, contentW;
    private int cardsTop, cardH, profileTop, profileBottom;
    private boolean compact;

    public SiegeSystemScreen(Screen parent) {
        super(Component.literal("SIEGE // COMMAND CENTER"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        profileButtons.clear();
        compact = width < 720 || height < 410;
        int margin = compact ? 7 : 14;
        panelW = Math.max(260, Math.min(880, width - margin * 2));
        panelW = Math.min(panelW, Math.max(1, width - margin * 2));
        panelX = (width - panelW) / 2;
        panelY = compact ? 35 : 46;
        panelBottom = height - (compact ? 8 : 18);
        contentX = panelX + 11;
        contentW = Math.max(1, panelW - 22);

        addRenderableWidget(new SiegeButton(8, 7, Math.min(86, Math.max(62, width / 6)), 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back"));

        cardH = compact ? 25 : 34;
        cardsTop = panelY + (compact ? 49 : 54);
        int cardRows = compact ? 2 : 1;
        int cardsBottom = cardsTop + cardRows * cardH + (cardRows - 1) * 5;
        profileTop = cardsBottom + (compact ? 11 : 14);

        int rowH = compact ? 18 : 21;
        int gap = 5;
        int controlsTop = Math.max(profileTop + 58, panelBottom - (rowH * 2 + gap + 10));
        profileBottom = controlsTop - 8;

        initProfileButtons(rowH);

        int half = (contentW - gap) / 2;
        contrastButton = addRenderableWidget(new SiegeButton(contentX, controlsTop, half, rowH,
                contrastLabel(), b -> {
                    SiegeConfig.highContrast = !SiegeConfig.highContrast;
                    SiegeConfig.save();
                    refreshButtons();
                    SiegeUiSounds.click();
                }, SiegeTheme.CYAN).withIcon("eye"));
        contrastButton.setTooltip(Tooltip.create(Component.literal(label(
                "Refuerza texto, marcos y fondos detrás de información importante.",
                "Strengthens text, frames and backgrounds behind important information."))));

        flashesButton = addRenderableWidget(new SiegeButton(contentX + half + gap, controlsTop,
                contentW - half - gap, rowH, flashesLabel(), b -> {
                    SiegeConfig.reduceFlashes = !SiegeConfig.reduceFlashes;
                    SiegeConfig.save();
                    refreshButtons();
                    SiegeUiSounds.click();
                }, SiegeTheme.GREEN).withIcon("eye"));
        flashesButton.setTooltip(Tooltip.create(Component.literal(label(
                "Bloquea interferencias y cambios visuales rápidos del cliente SIEGE.",
                "Blocks SIEGE client interference and rapid visual changes."))));

        int row2 = controlsTop + rowH + gap;
        diagnosticsButton = addRenderableWidget(new SiegeButton(contentX, row2, half, rowH,
                Component.literal(label("DIAGNÓSTICO Y RECUPERACIÓN", "DIAGNOSTICS & RECOVERY")), b -> {
                    SiegeUiSounds.click();
                    minecraft.setScreen(new SiegeDiagnosticsScreen(this));
                }, SiegeRuntimeStatus.healthAccent()).withIcon("overview").setCompactCenter(true));
        diagnosticsButton.setTooltip(Tooltip.create(Component.literal(label(
                "Analiza subsistemas, coherencia de perfil y problemas recuperables del cliente.",
                "Analyze client subsystems, profile coherence and recoverable issues."))));

        SiegeButton minecraftOptions = addRenderableWidget(new SiegeButton(contentX + half + gap, row2,
                contentW - half - gap, rowH,
                Component.literal(label("AJUSTES DE MINECRAFT", "MINECRAFT OPTIONS")), b -> {
                    SiegeUiSounds.click();
                    minecraft.setScreen(new OptionsScreen(this, minecraft.options));
                }, SiegeTheme.ORANGE).withIcon("settings").setCompactCenter(true));
        minecraftOptions.setTooltip(Tooltip.create(Component.literal(label(
                "Abre las opciones de Minecraft con la capa visual de SIEGE sin reemplazar su lógica.",
                "Opens Minecraft options with the SIEGE visual layer without replacing native logic."))));

        refreshButtons();
    }

    private void initProfileButtons(int preferredHeight) {
        SiegeClientProfile.Profile[] profiles = {
                SiegeClientProfile.Profile.CINEMATIC,
                SiegeClientProfile.Profile.TACTICAL,
                SiegeClientProfile.Profile.PERFORMANCE,
                SiegeClientProfile.Profile.CALM,
                SiegeClientProfile.Profile.READING
        };
        int gap = 4;
        int columns = !compact ? 5 : width < 520 ? 2 : 3;
        int rows = (profiles.length + columns - 1) / columns;
        int available = Math.max(rows * 15, profileBottom - profileTop - 19);
        int h = Math.max(15, Math.min(preferredHeight, (available - gap * Math.max(0, rows - 1)) / rows));
        int cellW = Math.max(48, (contentW - gap * (columns - 1)) / columns);

        for (int i = 0; i < profiles.length; i++) {
            SiegeClientProfile.Profile profile = profiles[i];
            int row = i / columns;
            int col = i % columns;
            int x = contentX + col * (cellW + gap);
            int w = col == columns - 1 ? contentX + contentW - x : cellW;
            int y = profileTop + 18 + row * (h + gap);
            SiegeButton button = new SiegeButton(x, y, w, h,
                    Component.literal(SiegeClientProfile.label(profile, spanish())), b -> applyProfile(profile),
                    SiegeClientProfile.accent(profile))
                    .withIcon(SiegeClientProfile.icon(profile))
                    .setCompactCenter(true);
            button.setTooltip(Tooltip.create(Component.literal(SiegeClientProfile.description(profile, spanish()))));
            profileButtons.put(profile, addRenderableWidget(button));
        }
    }

    private void applyProfile(SiegeClientProfile.Profile profile) {
        SiegeClientProfile.apply(profile);
        SiegeUiSounds.confirm();
        refreshButtons();
    }

    private void refreshButtons() {
        if (contrastButton != null) {
            contrastButton.setMessage(contrastLabel());
            contrastButton.setSelected(SiegeConfig.highContrast);
            contrastButton.withBadge(label(SiegeConfig.highContrast ? "SÍ" : "NO",
                    SiegeConfig.highContrast ? "ON" : "OFF"));
        }
        if (flashesButton != null) {
            flashesButton.setMessage(flashesLabel());
            flashesButton.setSelected(SiegeConfig.reduceFlashes);
            flashesButton.withBadge(label(SiegeConfig.reduceFlashes ? "SÍ" : "NO",
                    SiegeConfig.reduceFlashes ? "ON" : "OFF"));
        }
        if (diagnosticsButton != null) {
            diagnosticsButton.withBadge(SiegeRuntimeStatus.readiness() + "%");
        }
        SiegeClientProfile.Profile active = SiegeRuntimeStatus.profile();
        for (var entry : profileButtons.entrySet()) {
            boolean selected = entry.getKey() == active;
            entry.getValue().setSelected(selected);
            entry.getValue().withBadge(selected ? label("ACTIVO", "ACTIVE") : "");
        }
    }

    private Component contrastLabel() {
        return Component.literal(label("ALTO CONTRASTE", "HIGH CONTRAST"));
    }

    private Component flashesLabel() {
        return Component.literal(label("REDUCIR DESTELLOS", "REDUCE FLASHES"));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xCF08090B : 0xAD08090B);
        int profileAccent = SiegeClientProfile.accent(SiegeRuntimeStatus.profile());
        SiegeTheme.panel(g, panelX, panelY, panelW, Math.max(6, panelBottom - panelY), profileAccent);

        renderHeader(g, profileAccent);
        renderStatusCards(g);
        renderProfiles(g);
        renderDiagnostics(g);

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    private void renderHeader(GuiGraphics g, int profileAccent) {
        String title = "SIEGE // " + SiegeRuntimeStatus.version() + " // "
                + label("CENTRO DE COMANDO", "COMMAND CENTER");
        g.drawString(font, font.plainSubstrByWidth(title, contentW), contentX, panelY + 8,
                SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK, false);

        String subtitle = label("PERFIL: ", "PROFILE: ") + SiegeRuntimeStatus.profileFitLabel(spanish())
                + "  ·  " + SiegeRuntimeStatus.healthLabel(spanish());
        g.drawString(font, font.plainSubstrByWidth(subtitle, contentW), contentX, panelY + 21,
                profileAccent, false);

        int barY = panelY + 36;
        int barW = contentW;
        int readyW = Math.round(barW * SiegeRuntimeStatus.readiness() / 100.0F);
        g.fill(contentX, barY, contentX + barW, barY + 4, 0xFF272C30);
        if (readyW > 0) g.fill(contentX, barY, contentX + readyW, barY + 4, SiegeRuntimeStatus.healthAccent());
        String score = SiegeRuntimeStatus.readiness() + "%";
        if (!compact && contentW > 180)
            g.drawString(font, score, contentX + contentW - font.width(score), barY - 10,
                    SiegeRuntimeStatus.healthAccent(), false);
    }

    private void renderStatusCards(GuiGraphics g) {
        int gap = 5;
        if (!compact) {
            int w = (contentW - gap * 3) / 4;
            statusCard(g, contentX, cardsTop, w, cardH, "BUILD", SiegeRuntimeStatus.version(), SiegeTheme.RED);
            statusCard(g, contentX + (w + gap), cardsTop, w, cardH,
                    label("CLIENTE", "CLIENT"), SiegeRuntimeStatus.minecraftVersion() + " / Forge " + SiegeRuntimeStatus.forgeVersion(), SiegeTheme.ORANGE);
            statusCard(g, contentX + (w + gap) * 2, cardsTop, w, cardH,
                    label("RENDER", "RENDER"), SiegeRuntimeStatus.renderBackend(), SiegeTheme.CYAN);
            statusCard(g, contentX + (w + gap) * 3, cardsTop,
                    contentX + contentW - (contentX + (w + gap) * 3), cardH,
                    "INTEL", IntelCatalog.total() + " " + label("ARCHIVOS", "FILES"), SiegeTheme.GOLD);
        } else {
            int w = (contentW - gap) / 2;
            statusCard(g, contentX, cardsTop, w, cardH, "BUILD", SiegeRuntimeStatus.version(), SiegeTheme.RED);
            statusCard(g, contentX + w + gap, cardsTop, contentW - w - gap, cardH,
                    label("RENDER", "RENDER"), SiegeRuntimeStatus.renderBackend(), SiegeTheme.CYAN);
            int row2 = cardsTop + cardH + gap;
            statusCard(g, contentX, row2, w, cardH, "INTEL", IntelCatalog.total() + " " + label("ARCHIVOS", "FILES"), SiegeTheme.GOLD);
            statusCard(g, contentX + w + gap, row2, contentW - w - gap, cardH,
                    label("CLIENTE", "CLIENT"), SiegeRuntimeStatus.minecraftVersion() + " / Forge " + SiegeRuntimeStatus.forgeVersion(), SiegeTheme.ORANGE);
        }
    }

    private void renderProfiles(GuiGraphics g) {
        SiegeClientProfile.Profile profile = SiegeRuntimeStatus.profile();
        String heading = label("// PERFILES DEL CLIENTE", "// CLIENT PROFILES");
        g.drawString(font, heading, contentX, profileTop, SiegeClientProfile.accent(profile), false);

        if (profile == SiegeClientProfile.Profile.CUSTOM && profileBottom - profileTop > 72) {
            SiegeClientProfile.Profile nearest = SiegeProfileMetrics.nearest();
            String custom = label("Personalizado · perfil más cercano: ", "Custom · nearest profile: ")
                    + SiegeClientProfile.label(nearest, spanish()) + " · " + SiegeProfileMetrics.fitPercent(nearest) + "%";
            g.drawString(font, font.plainSubstrByWidth(custom, contentW), contentX,
                    profileBottom - 12, SiegeTheme.MUTED, false);
        }
    }

    private void renderDiagnostics(GuiGraphics g) {
        int controlsReserve = compact ? 52 : 58;
        int diagnosticsBottom = panelBottom - controlsReserve;
        int y = profileBottom + 1;
        if (y >= diagnosticsBottom - 12) return;

        g.fill(contentX, y, contentX + contentW, y + 1, 0xFF31373C);
        y += 5;
        String audio = label("AUDIO: ", "AUDIO: ") + SiegeRuntimeStatus.audioLabel(spanish());
        String background = label("FONDO: ", "BACKGROUND: ") + SiegeRuntimeStatus.backgroundLabel(spanish());
        String access = label("ACCESIBILIDAD: ", "ACCESSIBILITY: ") + SiegeRuntimeStatus.accessibilityLabel(spanish());
        g.drawString(font, font.plainSubstrByWidth(audio, contentW), contentX, y, SiegeTheme.MUTED, false);
        y += 11;
        if (y < diagnosticsBottom - 8) {
            g.drawString(font, font.plainSubstrByWidth(background, contentW), contentX, y, SiegeTheme.MUTED, false);
            y += 11;
        }
        if (!compact && y < diagnosticsBottom - 8) {
            g.drawString(font, font.plainSubstrByWidth(access, contentW), contentX, y, SiegeTheme.MUTED, false);
            y += 12;
        }

        List<String> warnings = SiegeRuntimeStatus.warnings(spanish());
        if (!warnings.isEmpty() && y < diagnosticsBottom - 8) {
            String warning = "! " + warnings.get(0);
            g.drawString(font, font.plainSubstrByWidth(warning, contentW), contentX, y,
                    SiegeRuntimeStatus.healthAccent(), false);
        } else if (warnings.isEmpty() && y < diagnosticsBottom - 8) {
            String ok = label("Diagnóstico: subsistemas sin errores ni advertencias.",
                    "Diagnostics: subsystems have no errors or warnings.");
            g.drawString(font, font.plainSubstrByWidth(ok, contentW), contentX, y, SiegeTheme.GREEN, false);
        }
    }

    private void statusCard(GuiGraphics g, int x, int y, int w, int h, String title, String value, int accent) {
        if (w <= 0 || h <= 0) return;
        SiegeTheme.panel(g, x, y, w, h, accent);
        g.fill(x, y, x + Math.min(2, w), y + h, accent);
        g.drawString(font, font.plainSubstrByWidth(title, Math.max(1, w - 10)), x + 6, y + 4,
                SiegeTheme.MUTED, false);
        int valueY = h >= 30 ? y + 18 : y + 14;
        g.drawString(font, font.plainSubstrByWidth(value, Math.max(1, w - 10)), x + 6, valueY,
                SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK, false);
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private String label(String es, String en) { return spanish() ? es : en; }

    @Override
    public void onClose() {
        SiegeUiSounds.back();
        SiegeConfig.save();
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}

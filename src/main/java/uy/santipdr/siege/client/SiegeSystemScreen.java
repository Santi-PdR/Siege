package uy.santipdr.siege.client;

import java.util.EnumMap;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** SIEGE 1.25 Command Center: priorities first, metrics second. */
public final class SiegeSystemScreen extends Screen {
    private final Screen parent;
    private final EnumMap<SiegeClientProfile.Profile, SiegeButton> profileButtons = new EnumMap<>(SiegeClientProfile.Profile.class);
    private int panelX, panelY, panelW, panelBottom, contentX, contentW;
    private int profileTop, profileBottom, statusTop;
    private boolean compact;
    private SiegeButton diagnosticsButton, contrastButton, flashesButton;

    public SiegeSystemScreen(Screen parent) {
        super(Component.literal("SIEGE // COMMAND CENTER"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        profileButtons.clear();
        compact = width < 720 || height < 410;
        int margin = SiegeUiLayout.safeMargin(width, height);
        panelW = Math.max(270, Math.min(920, width - margin * 2));
        panelX = (width - panelW) / 2;
        panelY = compact ? 33 : 42;
        panelBottom = height - (compact ? 8 : 16);
        contentX = panelX + 11;
        contentW = panelW - 22;

        addRenderableWidget(new SiegeButton(8, 7, Math.min(88, Math.max(60, width / 7)), 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));

        statusTop = panelY + 64;
        int priorityH = compact ? 27 : 32;
        int priorityRows = compact ? 2 : 1;
        profileTop = statusTop + priorityRows * priorityH + (priorityRows - 1) * 5 + 14;

        int controlH = compact ? 18 : 21;
        int bottomRows = compact ? 3 : 2;
        int controlsTop = panelBottom - bottomRows * controlH - (bottomRows - 1) * 5 - 9;
        profileBottom = controlsTop - 10;
        initProfiles(controlH);
        initActions(controlsTop, controlH);
        refreshButtons();
    }

    private void initProfiles(int preferredH) {
        SiegeClientProfile.Profile[] profiles = SiegeProfileSpec.presets();
        int gap = 4;
        int columns = compact ? (width < 500 ? 2 : 3) : 5;
        int rows = (profiles.length + columns - 1) / columns;
        int available = Math.max(rows * 15, profileBottom - profileTop - 18);
        int h = Math.max(15, Math.min(preferredH, (available - gap * Math.max(0, rows - 1)) / rows));
        int cellW = Math.max(50, (contentW - gap * (columns - 1)) / columns);
        for (int i = 0; i < profiles.length; i++) {
            SiegeClientProfile.Profile profile = profiles[i];
            int row = i / columns, col = i % columns;
            int x = contentX + col * (cellW + gap);
            int w = col == columns - 1 ? contentX + contentW - x : cellW;
            int y = profileTop + 18 + row * (h + gap);
            SiegeButton button = new SiegeButton(x, y, w, h,
                    Component.literal(SiegeClientProfile.label(profile, spanish())), b -> applyProfile(profile),
                    SiegeClientProfile.accent(profile)).withIcon(SiegeClientProfile.icon(profile)).setCompactCenter(true);
            button.setTooltip(Tooltip.create(Component.literal(SiegeClientProfile.description(profile, spanish()))));
            profileButtons.put(profile, addRenderableWidget(button));
        }
    }

    private void initActions(int top, int h) {
        int gap = 5;
        int half = (contentW - gap) / 2;
        contrastButton = addRenderableWidget(new SiegeButton(contentX, top, half, h,
                Component.literal(label("CONTRASTE AUTOMÁTICO", "AUTO CONTRAST")), b -> {
                    SiegeConfig.autoContrast = !SiegeConfig.autoContrast; SiegeConfig.save(); SiegeUiSounds.click(); refreshButtons();
                }, SiegeTheme.CYAN).withIcon("eye"));
        contrastButton.setTooltip(Tooltip.create(Component.literal(label(
                "Impone mínimos de oscuridad detrás del texto sin reemplazar el fondo seleccionado.",
                "Enforces minimum darkness behind text without replacing the selected background."))));

        flashesButton = addRenderableWidget(new SiegeButton(contentX + half + gap, top, contentW - half - gap, h,
                Component.literal(label("REDUCIR DESTELLOS", "REDUCE FLASHES")), b -> {
                    SiegeConfig.reduceFlashes = !SiegeConfig.reduceFlashes; SiegeConfig.save(); SiegeUiSounds.click(); refreshButtons();
                }, SiegeTheme.GREEN).withIcon("eye"));
        flashesButton.setTooltip(Tooltip.create(Component.literal(label(
                "Tiene prioridad sobre interferencias y cambios rápidos de luminancia.",
                "Takes priority over interference and rapid luminance changes."))));

        int row2 = top + h + gap;
        diagnosticsButton = addRenderableWidget(new SiegeButton(contentX, row2, half, h,
                Component.literal(label("DIAGNÓSTICO Y RECUPERACIÓN", "DIAGNOSTICS & RECOVERY")), b -> {
                    SiegeUiSounds.click(); minecraft.setScreen(new SiegeDiagnosticsScreen(this));
                }, SiegeRuntimeStatus.healthAccent()).withIcon("overview").setCompactCenter(true));
        addRenderableWidget(new SiegeButton(contentX + half + gap, row2, contentW - half - gap, h,
                Component.literal(label("CONFIGURACIÓN SIEGE", "SIEGE SETTINGS")), b -> {
                    SiegeUiSounds.click(); minecraft.setScreen(new SiegeSettingsScreen(this));
                }, SiegeTheme.RED).withIcon("settings").setCompactCenter(true));

        if (compact) {
            int row3 = row2 + h + gap;
            addRenderableWidget(new SiegeButton(contentX, row3, contentW, h,
                    Component.literal(label("AJUSTES NATIVOS DE MINECRAFT", "MINECRAFT OPTIONS")), b -> {
                        SiegeUiSounds.click(); minecraft.setScreen(new OptionsScreen(this, minecraft.options));
                    }, SiegeTheme.ORANGE).withIcon("settings").setCompactCenter(true));
        } else {
            diagnosticsButton.setTooltip(Tooltip.create(Component.literal(label(
                    "Abre diagnóstico detallado. Las opciones nativas permanecen disponibles desde Configuración > Sistema.",
                    "Open detailed diagnostics. Native options remain available from Settings > System."))));
        }
    }

    private void applyProfile(SiegeClientProfile.Profile profile) {
        SiegeClientProfile.apply(profile);
        SiegeUiSounds.confirm();
        refreshButtons();
    }

    private void refreshButtons() {
        if (contrastButton != null) contrastButton.setSelected(SiegeConfig.autoContrast)
                .withBadge(label(SiegeConfig.autoContrast ? "SÍ" : "NO", SiegeConfig.autoContrast ? "ON" : "OFF"));
        if (flashesButton != null) flashesButton.setSelected(SiegeConfig.reduceFlashes)
                .withBadge(label(SiegeConfig.reduceFlashes ? "SÍ" : "NO", SiegeConfig.reduceFlashes ? "ON" : "OFF"));
        if (diagnosticsButton != null) diagnosticsButton.withBadge(SiegeDiagnosticReport.readiness() + "%");
        SiegeClientProfile.Profile active = SiegeClientProfile.detect();
        for (var entry : profileButtons.entrySet()) {
            boolean selected = entry.getKey() == active;
            entry.getValue().setSelected(selected).withBadge(selected ? label("ACTIVO", "ACTIVE") : "");
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xD208090B : 0xB208090B);
        int profileAccent = SiegeClientProfile.accent(SiegeClientProfile.detect());
        SiegeTheme.panel(g, panelX, panelY, panelW, Math.max(6, panelBottom - panelY), profileAccent);
        renderHeader(g);
        renderPriorityCards(g);
        renderProfiles(g);
        renderOperationalSummary(g);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        refreshButtons();
    }

    private void renderHeader(GuiGraphics g) {
        SiegeClientProfile.Profile profile = SiegeClientProfile.detect();
        String detail = SiegeRuntimeStatus.healthLabel(spanish()) + " · " + SiegeDiagnosticReport.readiness() + "%";
        SiegeScreenChrome.renderHeader(g, this, contentX, panelY + 7, contentW, detail);
        String profileLine = profile == SiegeClientProfile.Profile.CUSTOM
                ? label("PERSONALIZADO · MÁS CERCANO: ", "CUSTOM · NEAREST: ")
                    + SiegeClientProfile.label(SiegeProfileMetrics.nearest(), spanish()) + " "
                    + SiegeProfileMetrics.fitPercent(SiegeProfileMetrics.nearest()) + "%"
                : label("PERFIL: ", "PROFILE: ") + SiegeClientProfile.label(profile, spanish()) + " · 100%";
        g.drawString(font, fit(profileLine, contentW), contentX, panelY + 31, SiegeClientProfile.accent(profile), false);
        int barY = panelY + 46;
        g.fill(contentX, barY, contentX + contentW, barY + 4, 0xFF272C30);
        int ready = Math.round(contentW * SiegeDiagnosticReport.readiness() / 100.0F);
        if (ready > 0) g.fill(contentX, barY, contentX + ready, barY + 4, SiegeRuntimeStatus.healthAccent());
    }

    private void renderPriorityCards(GuiGraphics g) {
        int gap = 5;
        int critical = SiegeDiagnosticReport.errors(spanish());
        int attention = SiegeDiagnosticReport.warnings(spanish()) + SiegeDiagnosticReport.notices(spanish());
        int operational = SiegeDiagnosticReport.operational(spanish());
        if (!compact) {
            int w = (contentW - gap * 3) / 4;
            priorityCard(g, contentX, statusTop, w, 32, label("CRÍTICO", "CRITICAL"), Integer.toString(critical), SiegeTheme.RED);
            priorityCard(g, contentX + w + gap, statusTop, w, 32, label("ATENCIÓN", "ATTENTION"), Integer.toString(attention), SiegeTheme.GOLD);
            priorityCard(g, contentX + (w + gap) * 2, statusTop, w, 32, label("OPERATIVO", "OPERATIONAL"), Integer.toString(operational), SiegeTheme.GREEN);
            priorityCard(g, contentX + (w + gap) * 3, statusTop, contentX + contentW - (contentX + (w + gap) * 3), 32,
                    label("PREPARACIÓN", "READINESS"), SiegeDiagnosticReport.readiness() + "%", SiegeRuntimeStatus.healthAccent());
        } else {
            int w = (contentW - gap) / 2;
            priorityCard(g, contentX, statusTop, w, 27, label("CRÍTICO", "CRITICAL"), Integer.toString(critical), SiegeTheme.RED);
            priorityCard(g, contentX + w + gap, statusTop, contentW - w - gap, 27, label("ATENCIÓN", "ATTENTION"), Integer.toString(attention), SiegeTheme.GOLD);
            int row2 = statusTop + 32;
            priorityCard(g, contentX, row2, w, 27, label("OPERATIVO", "OPERATIONAL"), Integer.toString(operational), SiegeTheme.GREEN);
            priorityCard(g, contentX + w + gap, row2, contentW - w - gap, 27,
                    label("PREPARACIÓN", "READINESS"), SiegeDiagnosticReport.readiness() + "%", SiegeRuntimeStatus.healthAccent());
        }
    }

    private void priorityCard(GuiGraphics g, int x, int y, int w, int h, String label, String value, int accent) {
        SiegeTheme.panel(g, x, y, w, h, accent);
        g.fill(x, y, x + 3, y + h, accent);
        g.drawString(font, fit(label, Math.max(16, w - 34)), x + 8, y + 5, SiegeTheme.MUTED, false);
        g.drawString(font, value, x + w - 7 - font.width(value), y + 5, accent, false);
    }

    private void renderProfiles(GuiGraphics g) {
        g.drawString(font, label("// PERFILES COMPLETOS", "// COMPLETE PROFILES"), contentX, profileTop,
                SiegeClientProfile.accent(SiegeClientProfile.detect()), false);
        SiegeClientProfile.Profile active = SiegeClientProfile.detect();
        if (active == SiegeClientProfile.Profile.CUSTOM && profileBottom - profileTop > 70) {
            String drift = label("Diferencia: ", "Drift: ") + SiegeProfileMetrics.distance(SiegeProfileMetrics.nearest())
                    + "/" + SiegeProfileMetrics.fieldCount() + " · " + SiegeProfileMetrics.fitPercent(SiegeProfileMetrics.nearest()) + "%";
            g.drawString(font, fit(drift, contentW), contentX, profileBottom - 11, SiegeTheme.MUTED, false);
        }
    }

    private void renderOperationalSummary(GuiGraphics g) {
        int bottomReserve = compact ? 70 : 55;
        int y = profileBottom + 1;
        int bottom = panelBottom - bottomReserve;
        if (bottom - y < 18) return;
        g.fill(contentX, y, contentX + contentW, y + 1, 0xFF31373C);
        y += 6;
        String[] lines = {
                "BUILD " + SiegeRuntimeStatus.version() + " · MC " + SiegeRuntimeStatus.minecraftVersion() + " · Forge " + SiegeRuntimeStatus.forgeVersion(),
                label("RENDER: ", "RENDER: ") + SiegeRuntimeStatus.renderBackend(),
                "INTEL: " + IntelCatalog.total() + " · " + SiegeRuntimeStatus.backgroundLabel(spanish()),
                SiegeRuntimeStatus.audioLabel(spanish()) + " · " + SiegeRuntimeStatus.accessibilityLabel(spanish())
        };
        for (String line : lines) {
            if (y + font.lineHeight > bottom) break;
            g.drawString(font, fit(line, contentW), contentX, y, SiegeTheme.MUTED, false);
            y += 11;
        }
        List<SiegeDiagnosticReport.Entry> entries = SiegeDiagnosticReport.entries(spanish());
        SiegeDiagnosticReport.Entry issue = entries.stream().filter(e -> e.severity() == SiegeDiagnosticReport.Severity.ERROR).findFirst()
                .orElse(entries.stream().filter(e -> e.severity() == SiegeDiagnosticReport.Severity.WARNING).findFirst()
                        .orElse(entries.stream().filter(e -> e.severity() == SiegeDiagnosticReport.Severity.NOTICE).findFirst().orElse(null)));
        if (issue != null && y + font.lineHeight <= bottom) {
            g.drawString(font, fit("! " + issue.code() + " · " + issue.detail(), contentW), contentX, y,
                    SiegeDiagnosticReport.accent(issue.severity()), false);
        }
    }

    private String fit(String value, int width) {
        if (font.width(value) <= width) return value;
        return font.plainSubstrByWidth(value, Math.max(1, width - font.width("…"))) + "…";
    }

    private boolean spanish() { return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_"); }
    private String label(String es, String en) { return spanish() ? es : en; }

    @Override
    public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

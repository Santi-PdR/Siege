package uy.santipdr.siege.client;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Full diagnostic and recovery surface introduced in SIEGE 0.60.0. */
public final class SiegeDiagnosticsScreen extends Screen {
    private final Screen parent;
    private int panelX, panelY, panelW, panelBottom;
    private int viewportTop, viewportBottom, scrollOffset, scrollMax;
    private boolean compact;
    private SiegeButton alignButton;

    public SiegeDiagnosticsScreen(Screen parent) {
        super(Component.literal("SIEGE // DIAGNOSTICS"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        compact = width < 680 || height < 390;
        int margin = compact ? 7 : 14;
        panelW = Math.min(compact ? 620 : 820, Math.max(260, width - margin * 2));
        panelX = (width - panelW) / 2;
        panelY = compact ? 35 : 46;
        panelBottom = height - (compact ? 8 : 18);

        addRenderableWidget(new SiegeButton(8, 7, Math.min(86, Math.max(62, width / 6)), 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED).withIcon("back"));

        int innerX = panelX + 11;
        int innerW = panelW - 22;
        int buttonH = compact ? 18 : 21;
        int gap = 5;
        int bottomY = panelBottom - buttonH - 8;
        int half = (innerW - gap) / 2;

        alignButton = addRenderableWidget(new SiegeButton(innerX, bottomY, half, buttonH,
                Component.literal(alignLabel()), b -> alignNearest(), SiegeTheme.CYAN)
                .withIcon("settings").setCompactCenter(true));
        addRenderableWidget(new SiegeButton(innerX + half + gap, bottomY, innerW - half - gap, buttonH,
                Component.literal(label("VOLVER AL CENTRO DE COMANDO", "BACK TO COMMAND CENTER")),
                b -> onClose(), SiegeTheme.RED).withIcon("overview").setCompactCenter(true));

        viewportTop = panelY + (compact ? 69 : 78);
        viewportBottom = bottomY - 8;
        int rowH = compact ? 31 : 35;
        scrollMax = Math.max(0, SiegeDiagnosticReport.entries(spanish()).size() * rowH - (viewportBottom - viewportTop));
        scrollOffset = Math.max(0, Math.min(scrollOffset, scrollMax));
        refreshAlignButton();
    }

    private void alignNearest() {
        SiegeClientProfile.Profile nearest = SiegeProfileMetrics.nearest();
        SiegeClientProfile.apply(nearest);
        SiegeUiSounds.confirm();
        scrollOffset = 0;
        init(minecraft, width, height);
    }

    private void refreshAlignButton() {
        if (alignButton == null) return;
        alignButton.setMessage(Component.literal(alignLabel()));
        SiegeClientProfile.Profile active = SiegeClientProfile.detect();
        alignButton.active = active == SiegeClientProfile.Profile.CUSTOM;
        alignButton.setSelected(active == SiegeClientProfile.Profile.CUSTOM);
        alignButton.withBadge(active == SiegeClientProfile.Profile.CUSTOM
                ? SiegeProfileMetrics.fitPercent(SiegeProfileMetrics.nearest()) + "%" : label("EXACTO", "EXACT"));
    }

    private String alignLabel() {
        SiegeClientProfile.Profile active = SiegeClientProfile.detect();
        if (active != SiegeClientProfile.Profile.CUSTOM)
            return label("PERFIL YA COHERENTE", "PROFILE ALREADY COHERENT");
        SiegeClientProfile.Profile nearest = SiegeProfileMetrics.nearest();
        return label("ALINEAR A ", "ALIGN TO ") + SiegeClientProfile.label(nearest, spanish());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xD508090B : 0xB608090B);

        int accent = SiegeRuntimeStatus.healthAccent();
        SiegeTheme.panel(g, panelX, panelY, panelW, Math.max(6, panelBottom - panelY), accent);
        renderHeader(g);
        renderEntries(g, mouseX, mouseY);

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    private void renderHeader(GuiGraphics g) {
        int x = panelX + 11;
        int w = panelW - 22;
        String title = "SIEGE // " + SiegeRuntimeStatus.version() + " // "
                + label("DIAGNÓSTICO Y RECUPERACIÓN", "DIAGNOSTICS & RECOVERY");
        g.drawString(font, font.plainSubstrByWidth(title, w), x, panelY + 8,
                SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK, false);

        int errors = SiegeDiagnosticReport.errors(spanish());
        int warnings = SiegeDiagnosticReport.warnings(spanish());
        int notices = SiegeDiagnosticReport.notices(spanish());
        String summary = label("PREPARACIÓN ", "READINESS ") + SiegeDiagnosticReport.readiness() + "%"
                + "  ·  " + label("ERRORES ", "ERRORS ") + errors
                + "  ·  " + label("AVISOS ", "WARNINGS ") + warnings
                + "  ·  " + label("INFO ", "INFO ") + notices;
        g.drawString(font, font.plainSubstrByWidth(summary, w), x, panelY + 22,
                SiegeRuntimeStatus.healthAccent(), false);

        SiegeClientProfile.Profile active = SiegeClientProfile.detect();
        SiegeClientProfile.Profile nearest = SiegeProfileMetrics.nearest();
        String profile = active == SiegeClientProfile.Profile.CUSTOM
                ? label("PERSONALIZADO · MÁS CERCANO: ", "CUSTOM · NEAREST: ")
                    + SiegeClientProfile.label(nearest, spanish()) + " " + SiegeProfileMetrics.fitPercent(nearest) + "%"
                : label("PERFIL EXACTO: ", "EXACT PROFILE: ") + SiegeClientProfile.label(active, spanish());
        g.drawString(font, font.plainSubstrByWidth(profile, w), x, panelY + 35,
                SiegeClientProfile.accent(active), false);

        int barY = panelY + 50;
        g.fill(x, barY, x + w, barY + 4, 0xFF252A2E);
        int readyW = Math.round(w * SiegeDiagnosticReport.readiness() / 100.0F);
        if (readyW > 0) g.fill(x, barY, x + readyW, barY + 4, SiegeRuntimeStatus.healthAccent());
        g.drawString(font, label("SUBSISTEMAS", "SUBSYSTEMS"), x, barY + 9, SiegeTheme.MUTED, false);
    }

    private void renderEntries(GuiGraphics g, int mouseX, int mouseY) {
        List<SiegeDiagnosticReport.Entry> entries = SiegeDiagnosticReport.entries(spanish());
        int x = panelX + 11;
        int w = panelW - 22;
        int rowH = compact ? 31 : 35;

        g.enableScissor(x, viewportTop, x + w, viewportBottom);
        for (int i = 0; i < entries.size(); i++) {
            SiegeDiagnosticReport.Entry entry = entries.get(i);
            int y = viewportTop + i * rowH - scrollOffset;
            if (y + rowH < viewportTop || y >= viewportBottom) continue;
            int accent = SiegeDiagnosticReport.accent(entry.severity());
            boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + rowH - 3;
            g.fill(x, y, x + w, y + rowH - 3, hovered ? 0xE5212529 : 0xD8171A1D);
            g.fill(x, y, x + 3, y + rowH - 3, accent);
            g.fill(x + 9, y + rowH - 4, x + w - 7, y + rowH - 3, 0xFF2F363B);

            String code = entry.code();
            g.drawString(font, code, x + 9, y + 6, accent, false);
            int titleX = x + 9 + Math.max(30, font.width(code) + 9);
            g.drawString(font, font.plainSubstrByWidth(entry.title(), Math.max(1, w - (titleX - x) - 80)),
                    titleX, y + 6, SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK, false);
            String state = severityLabel(entry.severity());
            g.drawString(font, state, x + w - 8 - font.width(state), y + 6, accent, false);
            g.drawString(font, font.plainSubstrByWidth(entry.detail(), Math.max(1, w - 18)),
                    x + 9, y + 18, SiegeTheme.MUTED, false);
        }
        g.disableScissor();

        if (scrollMax > 0) {
            int trackH = viewportBottom - viewportTop;
            int thumbH = Math.max(12, trackH * trackH / Math.max(trackH, trackH + scrollMax));
            int thumbY = viewportTop + (trackH - thumbH) * scrollOffset / Math.max(1, scrollMax);
            g.fill(x + w - 3, viewportTop, x + w, viewportBottom, 0xFF252A2E);
            g.fill(x + w - 3, thumbY, x + w, thumbY + thumbH, SiegeRuntimeStatus.healthAccent());
        }
    }

    private String severityLabel(SiegeDiagnosticReport.Severity severity) {
        return switch (severity) {
            case OK -> "OK";
            case NOTICE -> label("INFO", "INFO");
            case WARNING -> label("REVISAR", "CHECK");
            case ERROR -> label("ERROR", "ERROR");
        };
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (delta != 0.0D && y >= viewportTop && y < viewportBottom) {
            scrollOffset = Math.max(0, Math.min(scrollMax, scrollOffset - (int)Math.round(delta * (compact ? 28 : 32))));
            return true;
        }
        return super.mouseScrolled(x, y, delta);
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private String label(String es, String en) { return spanish() ? es : en; }

    @Override
    public void onClose() {
        SiegeUiSounds.back();
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}

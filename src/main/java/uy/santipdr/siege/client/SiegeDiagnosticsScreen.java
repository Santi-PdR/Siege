package uy.santipdr.siege.client;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** SIEGE 1.25 diagnostics: priority, impact, recommendation and explicit recovery. */
public final class SiegeDiagnosticsScreen extends Screen {
    private final Screen parent;
    private int panelX, panelY, panelW, panelBottom;
    private int viewportTop, viewportBottom, listX, listW, detailX, detailW;
    private int scrollOffset, scrollMax, selectedIndex;
    private boolean compact, split;
    private SiegeButton repairButton, alignButton;

    public SiegeDiagnosticsScreen(Screen parent) {
        super(Component.literal("SIEGE // DIAGNOSTICS"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        compact = SiegeUiLayout.density(width, height) == SiegeUiLayout.Density.ULTRA_COMPACT
                || SiegeUiLayout.density(width, height) == SiegeUiLayout.Density.COMPACT;
        split = width >= 760 && height >= 390;
        int margin = SiegeUiLayout.safeMargin(width, height);
        panelW = Math.min(920, Math.max(280, width - margin * 2));
        panelX = (width - panelW) / 2;
        panelY = compact ? 32 : 41;
        panelBottom = height - (compact ? 8 : 16);

        int backW = Math.min(88, Math.max(58, width / 7));
        addRenderableWidget(new SiegeButton(8, 7, backW, 19, Component.literal(label("VOLVER", "BACK")),
                b -> onClose(), SiegeTheme.RED).withIcon("back").setCompactCenter(true));

        int innerX = panelX + 10;
        int innerW = panelW - 20;
        int buttonH = compact ? 18 : 20;
        int gap = 5;
        int bottomY = panelBottom - buttonH - 8;
        int third = Math.max(68, (innerW - gap * 2) / 3);

        repairButton = addRenderableWidget(new SiegeButton(innerX, bottomY, third, buttonH,
                Component.literal(label("REPARAR SELECCIÓN", "REPAIR SELECTED")), b -> repairSelected(), SiegeTheme.GOLD)
                .withIcon("settings").setCompactCenter(true));
        alignButton = addRenderableWidget(new SiegeButton(innerX + third + gap, bottomY, third, buttonH,
                Component.literal(label("ALINEAR PERFIL", "ALIGN PROFILE")), b -> alignNearest(), SiegeTheme.CYAN)
                .withIcon("shield").setCompactCenter(true));
        addRenderableWidget(new SiegeButton(innerX + (third + gap) * 2, bottomY,
                innerW - (third + gap) * 2, buttonH,
                Component.literal(label("CENTRO DE COMANDO", "COMMAND CENTER")), b -> onClose(), SiegeTheme.RED)
                .withIcon("overview").setCompactCenter(true));

        viewportTop = panelY + (compact ? 66 : 76);
        viewportBottom = bottomY - 8;
        listX = innerX;
        if (split) {
            listW = Math.max(260, (innerW - 8) * 44 / 100);
            detailX = listX + listW + 8;
            detailW = innerW - listW - 8;
        } else {
            listW = innerW;
            detailX = detailW = 0;
        }

        List<SiegeDiagnosticReport.Entry> entries = SiegeDiagnosticReport.entries(spanish());
        selectedIndex = Math.max(0, Math.min(selectedIndex, Math.max(0, entries.size() - 1)));
        int rowH = compact ? 34 : 39;
        scrollMax = Math.max(0, entries.size() * rowH - Math.max(1, viewportBottom - viewportTop));
        scrollOffset = SiegeUiLayout.clampScroll(scrollOffset, scrollMax);
        refreshButtons();
    }

    private void repairSelected() {
        SiegeDiagnosticReport.Entry entry = selectedEntry();
        if (entry == null || !SiegeDiagnosticReport.repair(entry)) {
            SiegeUiSounds.warning();
            return;
        }
        SiegeUiSounds.confirm();
        minecraft.setScreen(new SiegeDiagnosticsScreen(parent));
    }

    private void alignNearest() {
        if (SiegeClientProfile.detect() != SiegeClientProfile.Profile.CUSTOM) {
            SiegeUiSounds.warning();
            return;
        }
        SiegeClientProfile.apply(SiegeProfileMetrics.nearest());
        SiegeUiSounds.confirm();
        minecraft.setScreen(new SiegeDiagnosticsScreen(parent));
    }

    private SiegeDiagnosticReport.Entry selectedEntry() {
        List<SiegeDiagnosticReport.Entry> entries = SiegeDiagnosticReport.entries(spanish());
        return entries.isEmpty() ? null : entries.get(Math.max(0, Math.min(selectedIndex, entries.size() - 1)));
    }

    private void refreshButtons() {
        SiegeDiagnosticReport.Entry entry = selectedEntry();
        boolean canRepair = entry != null && entry.recovery() != SiegeDiagnosticReport.Recovery.NONE;
        if (repairButton != null) {
            repairButton.active = canRepair;
            repairButton.setSelected(canRepair);
            repairButton.withBadge(canRepair ? label("LISTO", "READY") : label("N/A", "N/A"));
            repairButton.setMessage(Component.literal(canRepair ? recoveryLabel(entry.recovery())
                    : label("SIN REPARACIÓN SEGURA", "NO SAFE REPAIR")));
        }
        if (alignButton != null) {
            boolean custom = SiegeClientProfile.detect() == SiegeClientProfile.Profile.CUSTOM;
            alignButton.active = custom;
            alignButton.setSelected(custom);
            alignButton.withBadge(custom ? SiegeProfileMetrics.fitPercent(SiegeProfileMetrics.nearest()) + "%"
                    : label("EXACTO", "EXACT"));
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xD908090B : 0xB808090B);

        int accent = SiegeRuntimeStatus.healthAccent();
        SiegeTheme.panel(g, panelX, panelY, panelW, Math.max(6, panelBottom - panelY), accent);
        renderHeader(g);
        renderEntries(g, mouseX, mouseY);
        if (split) renderSelectedDetail(g);

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        refreshButtons();
    }

    private void renderHeader(GuiGraphics g) {
        int x = panelX + 11;
        int w = panelW - 22;
        String detail = SiegeDiagnosticReport.priorityLabel(highestSeverity(), spanish()) + " · "
                + SiegeDiagnosticReport.readiness() + "%";
        SiegeScreenChrome.renderHeader(g, this, x, panelY + 7, w, detail);

        int critical = SiegeDiagnosticReport.errors(spanish());
        int attention = SiegeDiagnosticReport.warnings(spanish()) + SiegeDiagnosticReport.notices(spanish());
        int operational = SiegeDiagnosticReport.operational(spanish());
        String summary = label("CRÍTICO ", "CRITICAL ") + critical
                + "  ·  " + label("ATENCIÓN ", "ATTENTION ") + attention
                + "  ·  " + label("OPERATIVO ", "OPERATIONAL ") + operational;
        g.drawString(font, fit(summary, w), x, panelY + 31, SiegeTheme.MUTED, false);

        int barY = panelY + 46;
        g.fill(x, barY, x + w, barY + 4, 0xFF252A2E);
        int readyW = Math.round(w * SiegeDiagnosticReport.readiness() / 100.0F);
        if (readyW > 0) g.fill(x, barY, x + readyW, barY + 4, SiegeRuntimeStatus.healthAccent());
        SiegeClientProfile.Profile profile = SiegeClientProfile.detect();
        String profileText = profile == SiegeClientProfile.Profile.CUSTOM
                ? "CUSTOM → " + SiegeClientProfile.label(SiegeProfileMetrics.nearest(), spanish())
                    + " " + SiegeProfileMetrics.fitPercent(SiegeProfileMetrics.nearest()) + "%"
                : SiegeClientProfile.label(profile, spanish()) + " · 100%";
        g.drawString(font, fit(profileText, w), x, barY + 9, SiegeClientProfile.accent(profile), false);
    }

    private SiegeDiagnosticReport.Severity highestSeverity() {
        SiegeDiagnosticReport.Severity result = SiegeDiagnosticReport.Severity.OK;
        for (SiegeDiagnosticReport.Entry entry : SiegeDiagnosticReport.entries(false)) {
            if (entry.severity() == SiegeDiagnosticReport.Severity.ERROR) return entry.severity();
            if (entry.severity() == SiegeDiagnosticReport.Severity.WARNING) result = entry.severity();
            else if (entry.severity() == SiegeDiagnosticReport.Severity.NOTICE && result == SiegeDiagnosticReport.Severity.OK)
                result = entry.severity();
        }
        return result;
    }

    private void renderEntries(GuiGraphics g, int mouseX, int mouseY) {
        List<SiegeDiagnosticReport.Entry> entries = SiegeDiagnosticReport.entries(spanish());
        int rowH = compact ? 34 : 39;
        g.enableScissor(listX, viewportTop, listX + listW, viewportBottom);
        for (int i = 0; i < entries.size(); i++) {
            SiegeDiagnosticReport.Entry entry = entries.get(i);
            int y = viewportTop + i * rowH - scrollOffset;
            if (y + rowH < viewportTop || y >= viewportBottom) continue;
            int accent = SiegeDiagnosticReport.accent(entry.severity());
            boolean selected = i == selectedIndex;
            boolean hovered = mouseX >= listX && mouseX < listX + listW && mouseY >= y && mouseY < y + rowH - 3;
            g.fill(listX, y, listX + listW, y + rowH - 3,
                    selected ? 0xF02A2528 : hovered ? 0xE5212529 : 0xD8171A1D);
            g.fill(listX, y, listX + (selected ? 4 : 3), y + rowH - 3, accent);
            if (selected) SiegeTheme.focusCorners(g, listX, y, listW, rowH - 3, accent);

            g.drawString(font, entry.code(), listX + 9, y + 6, accent, false);
            int titleX = listX + 44;
            String priority = SiegeDiagnosticReport.priorityLabel(entry.severity(), spanish());
            int priorityW = font.width(priority);
            g.drawString(font, fit(entry.title(), Math.max(24, listW - 57 - priorityW)),
                    titleX, y + 6, SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK, false);
            g.drawString(font, priority, listX + listW - 8 - priorityW, y + 6, accent, false);
            g.drawString(font, fit(entry.detail(), Math.max(1, listW - 18)), listX + 9, y + 20, SiegeTheme.MUTED, false);
        }
        g.disableScissor();

        if (scrollMax > 0) {
            int trackH = viewportBottom - viewportTop;
            int thumbH = SiegeUiLayout.scrollThumb(trackH, trackH + scrollMax);
            int thumbY = viewportTop + (trackH - thumbH) * scrollOffset / Math.max(1, scrollMax);
            g.fill(listX + listW - 3, viewportTop, listX + listW, viewportBottom, 0xFF252A2E);
            g.fill(listX + listW - 3, thumbY, listX + listW, thumbY + thumbH, SiegeRuntimeStatus.healthAccent());
        }
    }

    private void renderSelectedDetail(GuiGraphics g) {
        SiegeDiagnosticReport.Entry entry = selectedEntry();
        if (entry == null || detailW < 120) return;
        int accent = SiegeDiagnosticReport.accent(entry.severity());
        int h = viewportBottom - viewportTop;
        SiegeTheme.panel(g, detailX, viewportTop, detailW, h, accent);
        int x = detailX + 11;
        int y = viewportTop + 10;
        int w = detailW - 22;
        g.drawString(font, entry.code() + " // " + fit(entry.title(), Math.max(30, w - 54)), x, y, SiegeTheme.INK, false);
        String priority = SiegeDiagnosticReport.priorityLabel(entry.severity(), spanish());
        g.drawString(font, priority, detailX + detailW - 11 - font.width(priority), y, accent, false);
        y += 15;
        SiegeTheme.divider(g, x, y, w, accent);
        y += 8;
        y = drawBlock(g, label("ESTADO", "STATE"), entry.detail(), x, y, w, viewportBottom - 12, accent);
        y = drawBlock(g, label("IMPACTO", "IMPACT"), entry.impact(), x, y + 4, w, viewportBottom - 12, SiegeTheme.GOLD);
        drawBlock(g, label("RECOMENDACIÓN", "RECOMMENDATION"), entry.recommendation(), x, y + 4, w,
                viewportBottom - 12, SiegeTheme.CYAN);
    }

    private int drawBlock(GuiGraphics g, String title, String body, int x, int y, int w, int bottom, int accent) {
        if (y + font.lineHeight > bottom) return y;
        g.drawString(font, title, x, y, accent, false);
        y += 12;
        for (FormattedCharSequence line : font.split(Component.literal(body), Math.max(32, w))) {
            if (y + font.lineHeight > bottom) break;
            g.drawString(font, line, x, y, SiegeTheme.MUTED, false);
            y += font.lineHeight + 2;
        }
        return y;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0 && x >= listX && x < listX + listW && y >= viewportTop && y < viewportBottom) {
            int rowH = compact ? 34 : 39;
            int index = ((int)y - viewportTop + scrollOffset) / rowH;
            if (index >= 0 && index < SiegeDiagnosticReport.entries(spanish()).size()) {
                if (selectedIndex != index) SiegeUiSounds.selection();
                selectedIndex = index;
                refreshButtons();
                return true;
            }
        }
        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (delta != 0.0D && x >= listX && x < listX + listW && y >= viewportTop && y < viewportBottom) {
            scrollOffset = SiegeUiLayout.clampScroll(scrollOffset - (int)Math.round(delta * (compact ? 30 : 36)), scrollMax);
            return true;
        }
        return super.mouseScrolled(x, y, delta);
    }

    private String recoveryLabel(SiegeDiagnosticReport.Recovery recovery) {
        return switch (recovery) {
            case RESTORE_MUSIC_VOLUME -> label("RESTAURAR MÚSICA", "RESTORE MUSIC");
            case RESTORE_UI_VOLUME -> label("RESTAURAR EFECTOS", "RESTORE EFFECTS");
            case DISABLE_INTERFERENCE -> label("PROTEGER DESTELLOS", "PROTECT FLASHES");
            case PERFORMANCE_SAFE -> label("OPTIMIZAR RENDIMIENTO", "OPTIMIZE PERFORMANCE");
            case ALIGN_NEAREST_PROFILE -> label("ALINEAR PERFIL", "ALIGN PROFILE");
            case ENABLE_AUTO_CONTRAST -> label("ACTIVAR CONTRASTE AUTO", "ENABLE AUTO CONTRAST");
            case NONE -> label("SIN REPARACIÓN", "NO REPAIR");
        };
    }

    private String fit(String text, int width) {
        if (font.width(text) <= width) return text;
        return font.plainSubstrByWidth(text, Math.max(1, width - font.width("…"))) + "…";
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

    @Override public boolean isPauseScreen() { return false; }
}

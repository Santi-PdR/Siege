package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** Full player-facing server information file. */
public final class SiegeKnowledgeFileScreen extends Screen {
    private final Screen parent;
    private final SiegeKnowledgeData.Entry entry;
    private int panelX, panelY, panelW, panelH;
    private int contentX, contentY, contentW, contentH;
    private int offset;
    private boolean compact;

    public SiegeKnowledgeFileScreen(Screen parent, SiegeKnowledgeData.Entry entry) {
        super(Component.literal("SIEGE // SERVER FILE"));
        this.parent = parent;
        this.entry = entry;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        compact = width < 620 || height < 360;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 36;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 17 : 23));
        contentX = panelX + 10;
        contentY = panelY + 48;
        contentW = panelW - 20;
        contentH = Math.max(60, panelY + panelH - contentY - 10);

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 86, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= contentX && mouseX < contentX + contentW
                && mouseY >= contentY && mouseY < contentY + contentH) {
            offset = Math.max(0, offset - (int)Math.signum(delta) * 2);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xC7000000 : 0x99000000);
        int accent = entry == null ? SiegeTheme.MUTED
                : entry.zone() == SiegeKnowledgeData.Zone.HISTORY ? SiegeTheme.ORANGE : SiegeTheme.CYAN;
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, accent);

        String title = entry == null ? label("INFORMACIÓN NO DISPONIBLE", "INFORMATION UNAVAILABLE") : entry.title(spanish());
        g.drawString(font, fit(title, panelW - 24), panelX + 12, panelY + 9, SiegeTheme.INK, false);
        String meta = entry == null ? "--" : entry.domain().label(spanish())
                + (entry.zone() == SiegeKnowledgeData.Zone.HISTORY
                ? label(" · PUEDE HABER CAMBIADO", " · MAY HAVE CHANGED") : "");
        g.drawString(font, fit(meta, panelW - 24), panelX + 12, panelY + 22, accent, false);
        g.drawString(font, fit(label("Información general para entender este tema del servidor.",
                        "General information for understanding this server topic."), panelW - 24),
                panelX + 12, panelY + 34, SiegeTheme.MUTED, false);

        SiegeTheme.panel(g, contentX - 3, contentY - 3, contentW + 6, contentH + 6, accent);
        renderContent(g, accent);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private void renderContent(GuiGraphics g, int accent) {
        if (entry == null) return;
        List<FormattedCharSequence> lines = new ArrayList<>();
        FormattedCharSequence blank = Component.empty().getVisualOrderText();
        addParagraph(lines, entry.summary(spanish()), contentW - 16, blank);
        lines.add(blank);
        addParagraph(lines, entry.body(spanish()), contentW - 16, blank);

        List<String> relatedTitles = entry.related().stream()
                .map(SiegeKnowledgeRegistry::get)
                .filter(java.util.Objects::nonNull)
                .map(e -> e.title(spanish()))
                .distinct().limit(8).toList();
        if (!relatedTitles.isEmpty()) {
            lines.add(blank);
            addParagraph(lines, label("TAMBIÉN VER: ", "SEE ALSO: ") + String.join(" · ", relatedTitles),
                    contentW - 16, blank);
        }

        int maxOffset = Math.max(0, lines.size() - 1);
        offset = Math.max(0, Math.min(offset, maxOffset));
        int x = contentX + 8;
        int y = contentY + 7;
        int maxY = contentY + contentH - 8;
        g.enableScissor(contentX, contentY, contentX + contentW, contentY + contentH);
        for (int i = offset; i < lines.size() && y + font.lineHeight <= maxY; i++) {
            g.drawString(font, lines.get(i), x, y, i == offset ? accent : SiegeTheme.INK, false);
            y += font.lineHeight + 2;
        }
        g.disableScissor();
    }

    private void addParagraph(List<FormattedCharSequence> lines, String text, int width,
                              FormattedCharSequence blank) {
        for (String paragraph : text.split("\\n", -1)) {
            if (paragraph.isEmpty()) lines.add(blank);
            else lines.addAll(font.split(Component.literal(paragraph), width));
        }
    }

    private String fit(String value, int pixels) {
        if (value == null || pixels <= 0) return "";
        if (font.width(value) <= pixels) return value;
        if (pixels <= font.width("…")) return font.plainSubstrByWidth(value, pixels);
        return font.plainSubstrByWidth(value, pixels - font.width("…")) + "…";
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }
    private String label(String es, String en) { return spanish() ? es : en; }
    @Override public void onClose() { if (minecraft != null) minecraft.setScreen(parent); }
}

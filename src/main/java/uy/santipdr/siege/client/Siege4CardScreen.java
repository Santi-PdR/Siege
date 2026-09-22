package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** Reusable responsive SIEGE 4.0 reference browser. */
public class Siege4CardScreen extends Screen {
    protected final Screen parent;
    protected final List<Siege4ReferenceData.Card> cards;
    protected final String titleEs, titleEn, subtitleEs, subtitleEn;
    protected final int accent;

    private final List<SiegeButton> rows = new ArrayList<>();
    private int panelX, panelY, panelW, panelH;
    private int listX, listY, listW, detailX, detailY, detailW, detailH;
    private int selectedIndex;
    private int listOffset;
    private int detailOffset;
    private boolean compact;

    public Siege4CardScreen(Screen parent, String titleEs, String titleEn,
                            String subtitleEs, String subtitleEn,
                            List<Siege4ReferenceData.Card> cards, int accent) {
        super(Component.literal("SIEGE // " + titleEn));
        this.parent = parent;
        this.titleEs = titleEs;
        this.titleEn = titleEn;
        this.subtitleEs = subtitleEs;
        this.subtitleEn = subtitleEn;
        this.cards = cards == null ? List.of() : List.copyOf(cards);
        this.accent = accent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        rows.clear();
        compact = width < 650 || height < 380;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 36;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 17 : 23));

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 86, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));

        int bodyTop = panelY + (compact ? 45 : 52);
        if (compact) {
            listX = panelX + 9;
            listY = bodyTop;
            listW = panelW - 18;
            detailX = listX;
            detailY = listY + 3 * 22 + 8;
            detailW = listW;
            detailH = Math.max(50, panelY + panelH - detailY - 9);
        } else {
            listX = panelX + 10;
            listY = bodyTop;
            listW = Math.max(205, Math.min(330, panelW * 36 / 100));
            detailX = listX + listW + 10;
            detailY = bodyTop;
            detailW = panelX + panelW - 10 - detailX;
            detailH = Math.max(80, panelY + panelH - detailY - 10);
        }

        int visible = compact ? 3 : Math.max(5, Math.min(9, (detailH - 2) / 22));
        for (int i = 0; i < visible; i++) {
            int slot = i;
            SiegeButton row = new SiegeButton(listX, listY + i * 22, listW, 19,
                    Component.empty(), b -> selectSlot(slot), accent).withIcon("overview");
            rows.add(addRenderableWidget(row));
        }
        refreshRows();
    }

    protected void setSelectedById(String id) {
        if (id == null) return;
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).id().equals(id)) {
                selectedIndex = i;
                listOffset = Math.max(0, Math.min(i, Math.max(0, cards.size() - rows.size())));
                detailOffset = 0;
                refreshRows();
                return;
            }
        }
    }

    private void selectSlot(int slot) {
        int index = listOffset + slot;
        if (index < 0 || index >= cards.size()) return;
        selectedIndex = index;
        detailOffset = 0;
        SiegeUiSounds.selection();
        refreshRows();
    }

    private void refreshRows() {
        selectedIndex = Math.max(0, Math.min(Math.max(0, cards.size() - 1), selectedIndex));
        int maxOffset = Math.max(0, cards.size() - rows.size());
        listOffset = Math.max(0, Math.min(maxOffset, listOffset));
        for (int i = 0; i < rows.size(); i++) {
            SiegeButton row = rows.get(i);
            int index = listOffset + i;
            boolean present = index < cards.size();
            row.visible = present;
            row.active = present;
            if (!present) continue;
            Siege4ReferenceData.Card card = cards.get(index);
            row.setMessage(Component.literal(card.tag(spanish()) + " · " + card.title(spanish())));
            row.setSelected(index == selectedIndex);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= listX && mouseX < listX + listW
                && mouseY >= listY && mouseY < listY + rows.size() * 22) {
            int max = Math.max(0, cards.size() - rows.size());
            listOffset = Math.max(0, Math.min(max, listOffset - (int)Math.signum(delta)));
            refreshRows();
            return true;
        }
        if (mouseX >= detailX && mouseX < detailX + detailW
                && mouseY >= detailY && mouseY < detailY + detailH) {
            detailOffset = Math.max(0, detailOffset - (int)Math.signum(delta) * 2);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xC4000000 : 0x96000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, accent);

        g.drawString(font, fit(label(titleEs, titleEn) + " // " + SiegeRuntimeStatus.version(), panelW - 24),
                panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(subtitleEs, subtitleEn), panelW - 24),
                panelX + 12, panelY + 22, SiegeTheme.MUTED, false);

        SiegeTheme.panel(g, listX - 3, listY - 3, listW + 6, rows.size() * 22 + 6, accent);
        SiegeTheme.panel(g, detailX - 3, detailY - 3, detailW + 6, detailH + 6, accent);
        renderDetail(g);

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    protected void renderDetail(GuiGraphics g) {
        if (cards.isEmpty()) {
            g.drawString(font, label("Sin información disponible.", "No information available."),
                    detailX + 8, detailY + 8, SiegeTheme.MUTED, false);
            return;
        }
        Siege4ReferenceData.Card card = cards.get(selectedIndex);
        int x = detailX + 8;
        int y = detailY + 8;
        int textW = Math.max(40, detailW - 16);
        g.drawString(font, fit(card.title(spanish()), textW), x, y, SiegeTheme.INK, false);
        g.drawString(font, fit(card.summary(spanish()), textW), x, y + 13, card.accent(), false);
        SiegeTheme.divider(g, x, y + 26, textW, card.accent());

        List<FormattedCharSequence> lines = new ArrayList<>();
        for (String paragraph : card.body(spanish()).split("\\n", -1)) {
            if (paragraph.isEmpty()) lines.add(Component.empty().getVisualOrderText());
            else lines.addAll(font.split(Component.literal(paragraph), textW));
        }

        int first = Math.max(0, Math.min(detailOffset, Math.max(0, lines.size() - 1)));
        int yy = y + 34;
        int maxY = detailY + detailH - 8;
        g.enableScissor(detailX, yy - 1, detailX + detailW, maxY);
        for (int i = first; i < lines.size() && yy + font.lineHeight <= maxY; i++) {
            g.drawString(font, lines.get(i), x, yy, SiegeTheme.INK, false);
            yy += font.lineHeight + 2;
        }
        g.disableScissor();
    }

    protected boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    protected String label(String es, String en) { return spanish() ? es : en; }

    protected String fit(String value, int pixels) {
        if (value == null || pixels <= 0) return "";
        if (font.width(value) <= pixels) return value;
        if (pixels <= font.width("…")) return font.plainSubstrByWidth(value, pixels);
        return font.plainSubstrByWidth(value, pixels - font.width("…")) + "…";
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}

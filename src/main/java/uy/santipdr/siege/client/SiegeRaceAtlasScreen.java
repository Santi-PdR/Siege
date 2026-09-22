package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** Dedicated race browser for new and returning Eternal Craft players. */
public final class SiegeRaceAtlasScreen extends Screen {
    private final Screen parent;
    private final List<SiegeButton> rows = new ArrayList<>();
    private EditBox search;
    private List<SiegeRaceAtlasData.Race> visible = List.of();
    private SiegeRaceAtlasData.Race selected;
    private int offset;
    private int panelX, panelY, panelW, panelH, listX, listY, listW, detailX, detailY, detailW, detailH;
    private boolean compact;

    public SiegeRaceAtlasScreen(Screen parent) {
        super(Component.literal("SIEGE // RACE ATLAS"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        rows.clear();
        compact = width < 660 || height < 390;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 38;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 18 : 24));

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 88, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));
        addRenderableWidget(new SiegeButton(Math.max(8, width - (compact ? 88 : 126) - 8), 7,
                compact ? 88 : 126, 19, Component.literal(label("PROGRESIÓN", "PROGRESSION")),
                b -> minecraft.setScreen(new SiegeProgressionMapScreen(this)), SiegeTheme.GOLD)
                .withIcon("overview").setCompactCenter(true));

        search = new EditBox(font, panelX + 10, panelY + 47, panelW - 20, 20,
                Component.literal(label("Buscar raza", "Search race")));
        search.setHint(Component.literal(label("Human, Hacker, Saiyan, Obsainan, Cyborg…",
                "Human, Hacker, Saiyan, Obsainan, Cyborg…")));
        search.setResponder(v -> { offset = 0; refresh(); });
        addRenderableWidget(search);

        int bodyTop = panelY + 73;
        if (compact) {
            listX = panelX + 10;
            listY = bodyTop;
            listW = panelW - 20;
            detailX = listX;
            detailY = bodyTop + 3 * 22 + 8;
            detailW = listW;
            detailH = Math.max(54, panelY + panelH - detailY - 10);
        } else {
            listX = panelX + 10;
            listY = bodyTop;
            listW = Math.max(215, Math.min(340, panelW * 38 / 100));
            detailX = listX + listW + 10;
            detailY = bodyTop;
            detailW = panelX + panelW - 10 - detailX;
            detailH = Math.max(90, panelY + panelH - detailY - 10);
        }

        int count = compact ? 3 : Math.max(5, Math.min(9, (panelH - 92) / 22));
        for (int i = 0; i < count; i++) {
            int slot = i;
            SiegeButton row = new SiegeButton(listX, listY + i * 22, listW, 19, Component.empty(),
                    b -> choose(slot), SiegeTheme.CYAN).withIcon("intel");
            row.visible = false;
            row.active = false;
            rows.add(addRenderableWidget(row));
        }
        refresh();
    }

    private void refresh() {
        String q = search == null ? "" : search.getValue();
        visible = SiegeRaceAtlasData.search(q, 64);
        int max = Math.max(0, visible.size() - rows.size());
        offset = Math.max(0, Math.min(max, offset));
        if (selected == null || visible.stream().noneMatch(r -> r.id().equals(selected.id())))
            selected = visible.isEmpty() ? null : visible.get(0);
        for (int i = 0; i < rows.size(); i++) {
            SiegeButton button = rows.get(i);
            int index = offset + i;
            boolean present = index < visible.size();
            button.visible = present;
            button.active = present;
            if (!present) continue;
            SiegeRaceAtlasData.Race race = visible.get(index);
            button.setMessage(Component.literal(race.name() + "  //  " + race.rarity().label(spanish())));
            button.setSelected(selected != null && selected.id().equals(race.id()));
        }
    }

    private void choose(int slot) {
        int index = offset + slot;
        if (index < 0 || index >= visible.size()) return;
        selected = visible.get(index);
        SiegeUiSounds.selection();
        refresh();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= listX && mouseX < listX + listW && mouseY >= listY && mouseY < listY + rows.size() * 22) {
            int max = Math.max(0, visible.size() - rows.size());
            offset = Math.max(0, Math.min(max, offset - (int)Math.signum(delta)));
            refresh();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selected != null && button == 0 && mouseX >= detailX && mouseX <= detailX + detailW
                && mouseY >= detailY && mouseY <= detailY + detailH) {
            if (mouseY >= detailY + detailH - 30 && !selected.knowledgeId().isBlank()) {
                SiegeUiSounds.confirm();
                minecraft.setScreen(new SiegeKnowledgeScreen(this, selected.knowledgeId()));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xC8000000 : 0xA6000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, SiegeTheme.GOLD);
        g.drawString(font, label("ATLAS DE RAZAS", "RACE ATLAS") + " // " + SiegeRuntimeStatus.version(),
                panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Razas documentadas del servidor. Lo desconocido queda sin confirmar en vez de inventarse.",
                "Documented server races. Unknown fields stay unconfirmed instead of being invented."), panelW - 24),
                panelX + 12, panelY + 21, SiegeTheme.MUTED, false);
        renderRarityStrip(g);

        SiegeTheme.panel(g, listX - 3, listY - 3, listW + 6, rows.size() * 22 + 6, SiegeTheme.CYAN);
        SiegeTheme.panel(g, detailX - 3, detailY - 3, detailW + 6, detailH + 6,
                selected == null ? SiegeTheme.CYAN : selected.rarity().accent());
        renderDetail(g, mouseX, mouseY);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private void renderRarityStrip(GuiGraphics g) {
        List<SiegeRaceAtlasData.Rarity> order = SiegeRaceAtlasData.rarityOrder();
        int x = panelX + 12;
        int y = panelY + 34;
        int available = panelW - 24;
        String prefix = label("RAREZA: ", "RARITY: ");
        g.drawString(font, prefix, x, y, SiegeTheme.MUTED, false);
        int cursor = x + font.width(prefix);
        for (SiegeRaceAtlasData.Rarity rarity : order) {
            String text = rarity.label(spanish());
            int w = font.width(text);
            if (cursor + w > x + available) break;
            g.drawString(font, text, cursor, y, rarity.accent(), false);
            cursor += w + font.width(" > ");
            if (cursor < x + available) g.drawString(font, ">", cursor - font.width(" > ") + 2, y, SiegeTheme.MUTED, false);
        }
    }

    private void renderDetail(GuiGraphics g, int mouseX, int mouseY) {
        if (selected == null) {
            g.drawString(font, label("No hay razas para mostrar.", "No races to show."), detailX + 8, detailY + 8,
                    SiegeTheme.MUTED, false);
            return;
        }
        int x = detailX + 9;
        int y = detailY + 8;
        int w = detailW - 18;
        int accent = selected.rarity().accent();
        g.drawString(font, fit(selected.name(), w), x, y, SiegeTheme.INK, false);
        y += 13;
        g.drawString(font, fit(label("RAREZA: ", "RARITY: ") + selected.rarity().label(spanish()), w), x, y, accent, false);
        y += 11;
        g.drawString(font, fit(label("PROGRESIÓN: ", "PROGRESSION: ") + selected.progression().label(spanish()), w), x, y,
                SiegeTheme.CYAN, false);
        y += 12;
        if (selected.historical()) {
            g.drawString(font, label("ARCHIVO HISTÓRICO / VIGENCIA PARCIAL", "HISTORICAL FILE / PARTIAL VALIDITY"),
                    x, y, SiegeTheme.ORANGE, false);
            y += 13;
        }
        SiegeTheme.divider(g, x, y, w, accent);
        y += 7;
        List<FormattedCharSequence> lines = font.split(Component.literal(selected.summary(spanish())), w);
        for (FormattedCharSequence line : lines) {
            if (y + font.lineHeight >= detailY + detailH - 36) break;
            g.drawString(font, line, x, y, SiegeTheme.INK, false);
            y += font.lineHeight + 2;
        }
        String tags = label("TAGS: ", "TAGS: ") + String.join(" · ", selected.tags());
        if (y + 22 < detailY + detailH) {
            y += 5;
            g.drawString(font, fit(tags, w), x, y, SiegeTheme.MUTED, false);
        }

        int buttonY = detailY + detailH - 24;
        boolean hot = mouseX >= x && mouseX < x + w && mouseY >= buttonY && mouseY < buttonY + 18;
        int color = hot ? 0xCC314B59 : 0xB51A252B;
        g.fill(x, buttonY, x + w, buttonY + 18, color);
        g.fill(x, buttonY, x + 2, buttonY + 18, accent);
        String open = label("ABRIR FICHA DE ENCICLOPEDIA", "OPEN ENCYCLOPEDIA FILE");
        g.drawCenteredString(font, fit(open, w - 8), x + w / 2, buttonY + 5, hot ? SiegeTheme.INK : SiegeTheme.MUTED);
    }

    private String fit(String text, int px) {
        if (text == null || px <= 0) return "";
        if (font.width(text) <= px) return text;
        return font.plainSubstrByWidth(text, Math.max(1, px - font.width("…"))) + "…";
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private String label(String es, String en) { return spanish() ? es : en; }

    @Override public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** Dedicated race browser with complete rarity ladder and readable full details. */
public final class SiegeRaceAtlasScreen extends Screen {
    private final Screen parent;
    private final List<SiegeButton> rows = new ArrayList<>();
    private EditBox search;
    private List<SiegeRaceAtlasData.Race> visible = List.of();
    private SiegeRaceAtlasData.Race selected;
    private int offset;
    private int detailOffset;
    private int panelX, panelY, panelW, panelH, listX, listY, listW, detailX, detailY, detailW, detailH;
    private int rarityY, rarityRows, searchY;
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
        int progressionW = compact ? 88 : 126;
        addRenderableWidget(new SiegeButton(Math.max(8, width - progressionW - 8), 7,
                progressionW, 19, Component.literal(compact ? label("PROGRES.", "PROGRESS") : label("PROGRESIÓN", "PROGRESSION")),
                b -> minecraft.setScreen(new SiegeProgressionMapScreen(this)), SiegeTheme.GOLD)
                .withIcon("overview").setCompactCenter(true));

        rarityY = panelY + 34;
        rarityRows = rarityRows(panelW - 24);
        searchY = rarityY + rarityRows * 11 + 6;
        search = new EditBox(font, panelX + 10, searchY, panelW - 20, 20,
                Component.literal(label("Buscar raza", "Search race")));
        search.setHint(Component.literal(label("Human, Hacker, Saiyan, Obsainan, Cyborg…",
                "Human, Hacker, Saiyan, Obsainan, Cyborg…")));
        search.setResponder(v -> { offset = 0; detailOffset = 0; refresh(); });
        addRenderableWidget(search);

        int bodyTop = searchY + 27;
        if (compact) {
            listX = panelX + 10;
            listY = bodyTop;
            listW = panelW - 20;
            detailX = listX;
            detailY = bodyTop + 3 * 22 + 8;
            detailW = listW;
            detailH = Math.max(46, panelY + panelH - detailY - 10);
        } else {
            listX = panelX + 10;
            listY = bodyTop;
            listW = Math.max(215, Math.min(340, panelW * 38 / 100));
            detailX = listX + listW + 10;
            detailY = bodyTop;
            detailW = panelX + panelW - 10 - detailX;
            detailH = Math.max(90, panelY + panelH - detailY - 10);
        }

        int count = compact ? 3 : Math.max(4, Math.min(9, (detailH - 2) / 22));
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

    private int rarityRows(int available) {
        String prefix = label("RAREZAS: ", "RARITIES: ");
        int cursor = font.width(prefix);
        int rowsNeeded = 1;
        for (SiegeRaceAtlasData.Rarity rarity : SiegeRaceAtlasData.rarityOrder()) {
            int item = font.width(rarity.label(spanish())) + font.width(" > ");
            if (cursor + item > available && cursor > font.width(prefix)) {
                rowsNeeded++;
                cursor = 0;
            }
            cursor += item;
        }
        return Math.max(1, Math.min(3, rowsNeeded));
    }

    private void refresh() {
        String q = search == null ? "" : search.getValue();
        visible = SiegeRaceAtlasData.search(q, 64);
        int max = Math.max(0, visible.size() - rows.size());
        offset = Math.max(0, Math.min(max, offset));
        if (selected == null || visible.stream().noneMatch(r -> r.id().equals(selected.id()))) {
            selected = visible.isEmpty() ? null : visible.get(0);
            detailOffset = 0;
        }
        for (int i = 0; i < rows.size(); i++) {
            SiegeButton button = rows.get(i);
            int index = offset + i;
            boolean present = index < visible.size();
            button.visible = present;
            button.active = present;
            if (!present) continue;
            SiegeRaceAtlasData.Race race = visible.get(index);
            String text = race.name() + "  //  " + race.rarity().label(spanish());
            button.setMessage(Component.literal(fit(text, Math.max(20, listW - 28))));
            button.setSelected(selected != null && selected.id().equals(race.id()));
        }
    }

    private void choose(int slot) {
        int index = offset + slot;
        if (index < 0 || index >= visible.size()) return;
        selected = visible.get(index);
        detailOffset = 0;
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
        if (mouseX >= detailX && mouseX < detailX + detailW && mouseY >= detailY && mouseY < detailY + detailH) {
            detailOffset = Math.max(0, detailOffset - (int)Math.signum(delta) * 2);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selected != null && button == 0 && mouseX >= detailX && mouseX <= detailX + detailW
                && mouseY >= detailY && mouseY <= detailY + detailH) {
            int buttonY = detailY + detailH - 24;
            if (mouseY >= buttonY && !selected.knowledgeId().isBlank()) {
                SiegeKnowledgeData.Entry entry = SiegeKnowledgeRegistry.get(selected.knowledgeId());
                if (entry != null) {
                    SiegeUiSounds.confirm();
                    minecraft.setScreen(new SiegeKnowledgeFileScreen(this, entry));
                    return true;
                }
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
        g.drawString(font, fit(label("ATLAS DE RAZAS", "RACE ATLAS") + " // " + SiegeRuntimeStatus.version(), panelW - 24),
                panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Elegí una raza para ver su rareza, progresión y lo importante para entenderla.",
                "Choose a race to see its rarity, progression and the important things to know."), panelW - 24),
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
        int left = panelX + 12;
        int right = panelX + panelW - 12;
        int y = rarityY;
        String prefix = label("RAREZAS: ", "RARITIES: ");
        int cursor = left;
        g.drawString(font, prefix, cursor, y, SiegeTheme.MUTED, false);
        cursor += font.width(prefix);
        int usedRows = 1;
        for (int i = 0; i < order.size(); i++) {
            SiegeRaceAtlasData.Rarity rarity = order.get(i);
            String text = rarity.label(spanish());
            int itemWidth = font.width(text) + (i + 1 < order.size() ? font.width(" > ") : 0);
            if (cursor + itemWidth > right && usedRows < rarityRows) {
                usedRows++;
                y += 11;
                cursor = left;
            }
            if (cursor + itemWidth > right) {
                String fitted = fit(text, Math.max(8, right - cursor));
                g.drawString(font, fitted, cursor, y, rarity.accent(), false);
                continue;
            }
            g.drawString(font, text, cursor, y, rarity.accent(), false);
            cursor += font.width(text);
            if (i + 1 < order.size()) {
                g.drawString(font, " > ", cursor, y, SiegeTheme.MUTED, false);
                cursor += font.width(" > ");
            }
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
        int w = Math.max(40, detailW - 18);
        int accent = selected.rarity().accent();
        g.drawString(font, fit(selected.name(), w), x, y, SiegeTheme.INK, false);
        y += 13;
        g.drawString(font, fit(label("RAREZA: ", "RARITY: ") + selected.rarity().label(spanish()), w), x, y, accent, false);
        y += 11;
        g.drawString(font, fit(label("PROGRESIÓN: ", "PROGRESSION: ") + selected.progression().label(spanish()), w), x, y,
                SiegeTheme.CYAN, false);
        y += 12;
        if (selected.mayHaveChanged()) {
            g.drawString(font, fit(label("ALGUNOS PASOS PUEDEN HABER CAMBIADO", "SOME STEPS MAY HAVE CHANGED"), w), x, y,
                    SiegeTheme.ORANGE, false);
            y += 13;
        }
        SiegeTheme.divider(g, x, y, w, accent);
        y += 7;

        SiegeKnowledgeData.Entry knowledge = selected.knowledgeId().isBlank() ? null : SiegeKnowledgeRegistry.get(selected.knowledgeId());
        List<FormattedCharSequence> lines = new ArrayList<>();
        FormattedCharSequence blank = Component.empty().getVisualOrderText();
        lines.addAll(font.split(Component.literal(label("RESUMEN — ", "SUMMARY — ") + selected.summary(spanish())), w));
        if (knowledge != null) {
            lines.add(blank);
            lines.addAll(font.split(Component.literal(label("LO IMPORTANTE", "WHAT TO KNOW")), w));
            for (String paragraph : knowledge.body(spanish()).split("\\n", -1)) {
                if (paragraph.isEmpty()) lines.add(blank);
                else lines.addAll(font.split(Component.literal(paragraph), w));
            }
        }

        int buttonY = detailY + detailH - 24;
        int maxY = buttonY - 5;
        int first = Math.max(0, Math.min(detailOffset, Math.max(0, lines.size() - 1)));
        int yy = y;
        g.enableScissor(detailX, yy - 1, detailX + detailW, maxY);
        for (int i = first; i < lines.size() && yy + font.lineHeight <= maxY; i++) {
            g.drawString(font, lines.get(i), x, yy, SiegeTheme.INK, false);
            yy += font.lineHeight + 2;
        }
        g.disableScissor();

        if (knowledge != null) {
            boolean hot = mouseX >= x && mouseX < x + w && mouseY >= buttonY && mouseY < buttonY + 18;
            g.fill(x, buttonY, x + w, buttonY + 18, hot ? 0xCC314B59 : 0xB51A252B);
            g.fill(x, buttonY, x + 2, buttonY + 18, accent);
            String open = label("VER INFORMACIÓN COMPLETA", "OPEN FULL INFO");
            g.drawCenteredString(font, fit(open, w - 8), x + w / 2, buttonY + 5,
                    hot ? SiegeTheme.INK : SiegeTheme.MUTED);
        }
    }

    private String fit(String text, int px) {
        if (text == null || px <= 0) return "";
        if (font.width(text) <= px) return text;
        return font.plainSubstrByWidth(text, Math.max(1, px - font.width("…"))) + "…";
    }
    private boolean spanish() { return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_"); }
    private String label(String es, String en) { return spanish() ? es : en; }
    @Override public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** Visual server progression map. It links to source-aware encyclopedia entries. */
public final class SiegeProgressionMapScreen extends Screen {
    private final Screen parent;
    private final List<SiegeButton> nodeButtons = new ArrayList<>();
    private SiegeProgressionData.Track track = SiegeProgressionData.tracks().get(0);
    private int panelX, panelY, panelW, panelH, mapX, mapY, mapW, detailX, detailY, detailW, detailH;
    private boolean compact;
    private SiegeProgressionData.Node selected;

    public SiegeProgressionMapScreen(Screen parent) {
        super(Component.literal("SIEGE // PROGRESSION MAP"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        nodeButtons.clear();
        compact = width < 680 || height < 390;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 38;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 18 : 24));

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 88, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));
        addRenderableWidget(new SiegeButton(Math.max(8, width - (compact ? 82 : 114) - 8), 7,
                compact ? 82 : 114, 19, Component.literal(label("RAZAS", "RACES")),
                b -> minecraft.setScreen(new SiegeRaceAtlasScreen(this)), SiegeTheme.CYAN)
                .withIcon("intel").setCompactCenter(true));

        int tabY = panelY + 42;
        int gap = 4;
        int tabX = panelX + 10;
        int tabW = panelW - 20;
        int count = SiegeProgressionData.tracks().size();
        int cellW = Math.max(55, (tabW - gap * (count - 1)) / count);
        for (int i = 0; i < count; i++) {
            SiegeProgressionData.Track t = SiegeProgressionData.tracks().get(i);
            int x = tabX + i * (cellW + gap);
            int w = i == count - 1 ? panelX + panelW - 10 - x : cellW;
            SiegeButton tab = new SiegeButton(x, tabY, w, 19, Component.literal(t.title(spanish())),
                    b -> selectTrack(t), SiegeTheme.GOLD).setCompactCenter(true).setSelected(t.id().equals(track.id()));
            tab.setTooltip(Tooltip.create(Component.literal(t.description(spanish()))));
            addRenderableWidget(tab);
        }

        int bodyTop = tabY + 29;
        if (compact) {
            mapX = panelX + 10;
            mapY = bodyTop;
            mapW = panelW - 20;
            detailX = mapX;
            detailY = bodyTop + Math.min(4, track.nodes().size()) * 24 + 12;
            detailW = mapW;
            detailH = Math.max(55, panelY + panelH - detailY - 10);
        } else {
            mapX = panelX + 10;
            mapY = bodyTop;
            mapW = Math.max(300, panelW * 58 / 100);
            detailX = mapX + mapW + 10;
            detailY = bodyTop;
            detailW = panelX + panelW - 10 - detailX;
            detailH = Math.max(90, panelY + panelH - detailY - 10);
        }
        buildNodes();
    }

    private void selectTrack(SiegeProgressionData.Track next) {
        track = next;
        selected = null;
        SiegeUiSounds.category();
        rebuildWidgets();
    }

    private void buildNodes() {
        nodeButtons.clear();
        List<SiegeProgressionData.Node> nodes = track.nodes();
        int gap = compact ? 5 : 8;
        int h = compact ? 19 : 23;
        if (compact) {
            for (int i = 0; i < nodes.size(); i++) {
                SiegeProgressionData.Node node = nodes.get(i);
                int y = mapY + i * (h + gap);
                SiegeButton button = new SiegeButton(mapX, y, mapW, h,
                        Component.literal((i + 1) + " // " + node.title(spanish())), b -> choose(node), node.status().accent())
                        .withIcon("overview");
                button.withBadge(node.status().label(spanish()));
                nodeButtons.add(addRenderableWidget(button));
            }
        } else {
            int count = Math.max(1, nodes.size());
            int w = Math.max(120, Math.min(220, (mapW - gap * Math.max(0, count - 1)) / count));
            int total = count * w + Math.max(0, count - 1) * gap;
            int x0 = mapX + Math.max(0, (mapW - total) / 2);
            int y = mapY + Math.max(22, detailH / 3);
            for (int i = 0; i < nodes.size(); i++) {
                SiegeProgressionData.Node node = nodes.get(i);
                int x = x0 + i * (w + gap);
                SiegeButton button = new SiegeButton(x, y, w, h,
                        Component.literal((i + 1) + " // " + node.title(spanish())), b -> choose(node), node.status().accent())
                        .withIcon("overview").setCompactCenter(true);
                button.withBadge(node.status().label(spanish()));
                nodeButtons.add(addRenderableWidget(button));
            }
        }
        if (selected == null && !nodes.isEmpty()) selected = nodes.get(0);
    }

    private void choose(SiegeProgressionData.Node node) {
        selected = node;
        SiegeUiSounds.selection();
        for (SiegeButton button : nodeButtons) button.setSelected(button.getMessage().getString().contains(node.title(spanish())));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && selected != null && mouseX >= detailX && mouseX <= detailX + detailW
                && mouseY >= detailY + detailH - 24 && mouseY <= detailY + detailH) {
            SiegeUiSounds.confirm();
            minecraft.setScreen(new SiegeKnowledgeScreen(this, selected.knowledgeId()));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xC9000000 : 0xA3000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, SiegeTheme.GOLD);
        g.drawString(font, label("MAPA DE PROGRESIÓN", "PROGRESSION MAP") + " // " + SiegeRuntimeStatus.version(),
                panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(track.description(spanish()), panelW - 24), panelX + 12, panelY + 22,
                SiegeTheme.MUTED, false);

        SiegeTheme.panel(g, mapX - 3, mapY - 3, mapW + 6,
                compact ? nodeButtons.size() * 24 + 8 : Math.max(90, detailH), SiegeTheme.CYAN);
        if (!compact) renderConnectors(g);
        SiegeTheme.panel(g, detailX - 3, detailY - 3, detailW + 6, detailH + 6,
                selected == null ? SiegeTheme.CYAN : selected.status().accent());
        renderDetail(g, mouseX, mouseY);

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private void renderConnectors(GuiGraphics g) {
        for (int i = 0; i + 1 < nodeButtons.size(); i++) {
            SiegeButton a = nodeButtons.get(i), b = nodeButtons.get(i + 1);
            int x1 = a.getX() + a.getWidth();
            int y = a.getY() + a.getHeight() / 2;
            int x2 = b.getX();
            g.fill(x1 + 2, y, Math.max(x1 + 3, x2 - 2), y + 1, 0xAA66747C);
            g.fill(Math.max(x1 + 2, x2 - 5), y - 2, x2 - 2, y + 3, 0xAA66747C);
        }
    }

    private void renderDetail(GuiGraphics g, int mouseX, int mouseY) {
        if (selected == null) return;
        int x = detailX + 8;
        int y = detailY + 8;
        int w = detailW - 16;
        int accent = selected.status().accent();
        g.drawString(font, fit(selected.title(spanish()), w), x, y, SiegeTheme.INK, false);
        y += 13;
        g.drawString(font, selected.status().label(spanish()), x, y, accent, false);
        y += 13;
        SiegeTheme.divider(g, x, y, w, accent);
        y += 7;
        for (FormattedCharSequence line : font.split(Component.literal(selected.summary(spanish())), w)) {
            if (y > detailY + detailH - 35) break;
            g.drawString(font, line, x, y, SiegeTheme.INK, false);
            y += font.lineHeight + 2;
        }
        int by = detailY + detailH - 21;
        boolean hot = mouseX >= x && mouseX < x + w && mouseY >= by && mouseY < by + 16;
        g.fill(x, by, x + w, by + 16, hot ? 0xCC314B59 : 0xB51A252B);
        g.fill(x, by, x + 2, by + 16, accent);
        g.drawCenteredString(font, fit(label("ABRIR FUENTE", "OPEN SOURCE"), w - 8), x + w / 2, by + 4,
                hot ? SiegeTheme.INK : SiegeTheme.MUTED);
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

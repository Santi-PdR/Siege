package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** Tactical summary of existing Intel + source-aware server threat domains. */
public final class SiegeThreatBoardScreen extends Screen {
    private final Screen parent;
    private final List<SiegeButton> threatButtons = new ArrayList<>();
    private SiegeThreatBoardData.Threat selected = SiegeThreatBoardData.all().get(0);
    private int panelX, panelY, panelW, panelH, listX, listY, listW, detailX, detailY, detailW, detailH;
    private boolean compact;

    public SiegeThreatBoardScreen(Screen parent) {
        super(Component.literal("SIEGE // THREAT BOARD"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        threatButtons.clear();
        compact = width < 680 || height < 390;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 38;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 18 : 24));

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 88, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));
        addRenderableWidget(new SiegeButton(Math.max(8, width - (compact ? 82 : 112) - 8), 7,
                compact ? 82 : 112, 19, Component.literal("INTEL"),
                b -> minecraft.setScreen(new IntelScreenV3(this)), SiegeTheme.RED)
                .withIcon("intel").setCompactCenter(true));

        int bodyTop = panelY + 63;
        if (compact) {
            listX = panelX + 10;
            listY = bodyTop;
            listW = panelW - 20;
            detailX = listX;
            detailY = bodyTop + 4 * 22 + 8;
            detailW = listW;
            detailH = Math.max(58, panelY + panelH - detailY - 10);
        } else {
            listX = panelX + 10;
            listY = bodyTop;
            listW = Math.max(220, Math.min(330, panelW * 36 / 100));
            detailX = listX + listW + 10;
            detailY = bodyTop;
            detailW = panelX + panelW - 10 - detailX;
            detailH = Math.max(95, panelY + panelH - detailY - 10);
        }

        int visible = compact ? 4 : SiegeThreatBoardData.all().size();
        for (int i = 0; i < visible; i++) {
            SiegeThreatBoardData.Threat threat = SiegeThreatBoardData.all().get(i);
            SiegeButton button = new SiegeButton(listX, listY + i * 22, listW, 19,
                    Component.literal(threat.title(spanish())), b -> choose(threat), threat.severity().accent())
                    .withIcon(threat.opensIntel() ? "intel" : "shield");
            button.withBadge(threat.severity().label(spanish()));
            button.setSelected(threat.id().equals(selected.id()));
            button.setTooltip(Tooltip.create(Component.literal(threat.summary(spanish()))));
            threatButtons.add(addRenderableWidget(button));
        }
    }

    private void choose(SiegeThreatBoardData.Threat threat) {
        selected = threat;
        SiegeUiSounds.selection();
        for (int i = 0; i < threatButtons.size(); i++)
            threatButtons.get(i).setSelected(SiegeThreatBoardData.all().get(i).id().equals(threat.id()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && selected != null && mouseX >= detailX && mouseX <= detailX + detailW
                && mouseY >= detailY + detailH - 24 && mouseY <= detailY + detailH) {
            SiegeUiSounds.confirm();
            if (selected.opensIntel()) minecraft.setScreen(new IntelScreenV3(this));
            else minecraft.setScreen(new SiegeKnowledgeScreen(this, selected.knowledgeId()));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xCD000000 : 0xA8000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, SiegeTheme.ORANGE);

        g.drawString(font, label("TABLERO DE AMENAZAS", "THREAT BOARD") + " // " + SiegeRuntimeStatus.version(),
                panelX + 12, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Resumen operativo: dossiers reales + riesgos generales documentados. No genera enemigos ni stats nuevos.",
                "Operational summary: real dossiers + documented general risks. It does not create new enemies or stats."), panelW - 24),
                panelX + 12, panelY + 21, SiegeTheme.MUTED, false);
        renderIntelCounts(g, panelX + 12, panelY + 36, panelW - 24);

        SiegeTheme.panel(g, listX - 3, listY - 3, listW + 6, threatButtons.size() * 22 + 6, SiegeTheme.ORANGE);
        SiegeTheme.panel(g, detailX - 3, detailY - 3, detailW + 6, detailH + 6,
                selected == null ? SiegeTheme.ORANGE : selected.severity().accent());
        renderDetail(g, mouseX, mouseY);

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private void renderIntelCounts(GuiGraphics g, int x, int y, int w) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String category : List.of("UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT", "UNKNOWN"))
            counts.put(category, 0);
        for (IntelEntry entry : IntelCatalog.files())
            counts.computeIfPresent(entry.category(), (k, v) -> v + 1);
        String line = counts.entrySet().stream()
                .map(e -> shortCategory(e.getKey()) + " " + e.getValue())
                .reduce((a, b) -> a + "  ·  " + b).orElse("");
        g.drawString(font, fit(line, w), x, y, SiegeTheme.CYAN, false);
    }

    private String shortCategory(String category) {
        return switch (category) {
            case "ADVANCED" -> "ADV";
            case "SUPER-UNIT" -> "SUP";
            case "UNKNOWN" -> "UNK";
            default -> category;
        };
    }

    private void renderDetail(GuiGraphics g, int mouseX, int mouseY) {
        if (selected == null) return;
        int x = detailX + 9;
        int y = detailY + 8;
        int w = detailW - 18;
        int accent = selected.severity().accent();
        g.drawString(font, fit(selected.title(spanish()), w), x, y, SiegeTheme.INK, false);
        y += 13;
        g.drawString(font, selected.severity().label(spanish()), x, y, accent, false);
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
        g.fill(x, by, x + w, by + 16, hot ? 0xD13A4C55 : 0xB51A252B);
        g.fill(x, by, x + 2, by + 16, accent);
        String open = selected.opensIntel() ? label("ABRIR INTEL", "OPEN INTEL") : label("ABRIR FUENTE", "OPEN SOURCE");
        g.drawCenteredString(font, open, x + w / 2, by + 4, hot ? SiegeTheme.INK : SiegeTheme.MUTED);
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

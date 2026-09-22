package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** SIEGE 4.00 fast non-personal newcomer briefing. */
public final class SiegeBriefingScreen extends Screen {
    private static final List<String> STEPS = List.of(
            "server-overview", "newcomer-operational-rule", "server-exploration", "race-catalog",
            "rarity-order", "progression-v1-v4", "progression-mobility-priority", "trials-basics",
            "executors-basics", "structures-basics", "bosses-basics", "respawn-cards",
            "dimensions-basics", "relic-analysis-workflow", "economy-basics", "prompt-precision-framework"
    );

    private final Screen parent;
    private final List<SiegeButton> cards = new ArrayList<>();
    private int panelX, panelY, panelW, panelH;
    private int startY, cardW, cardH, gap;
    private int offset;
    private boolean compact;

    public SiegeBriefingScreen(Screen parent) {
        super(Component.literal("SIEGE // ENTRY BRIEFING"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        cards.clear();
        compact = width < 620 || height < 360;
        int margin = compact ? 7 : Math.max(12, width / 80);
        panelX = margin;
        panelY = compact ? 31 : 36;
        panelW = Math.max(1, width - margin * 2);
        panelH = Math.max(1, height - panelY - (compact ? 17 : 23));

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 86, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));

        int atlasW = compact ? 84 : 118;
        addRenderableWidget(new SiegeButton(panelX + panelW - atlasW - 10, panelY + 8, atlasW, 18,
                Component.literal(label("ABRIR ATLAS", "OPEN ATLAS")), b -> {
            SiegeUiSounds.confirm();
            minecraft.setScreen(new SiegeAtlasScreen(this, SiegeAtlasIndex.View.BRIEFING));
        }, SiegeTheme.CYAN).withIcon("overview").setCompactCenter(true));

        startY = panelY + (compact ? 58 : 66);
        gap = compact ? 4 : 6;
        cardH = compact ? 22 : 28;
        int cols = compact && panelW < 410 ? 1 : 2;
        cardW = Math.max(1, (panelW - 20 - gap * (cols - 1)) / cols);
        int available = panelY + panelH - startY - 10;
        int visibleRows = Math.max(1, available / (cardH + gap));
        int visibleCards = visibleRows * cols;

        for (int i = 0; i < visibleCards; i++) {
            int slot = i;
            int row = i / cols;
            int col = i % cols;
            int x = panelX + 10 + col * (cardW + gap);
            int y = startY + row * (cardH + gap);
            SiegeButton button = new SiegeButton(x, y, cardW, cardH, Component.empty(),
                    b -> openSlot(slot), i < 3 ? SiegeTheme.ORANGE : SiegeTheme.GOLD)
                    .withIcon(i < 3 ? "shield" : "overview");
            button.visible = false;
            button.active = false;
            cards.add(addRenderableWidget(button));
        }
        refresh();
    }

    private List<SiegeKnowledgeData.Entry> availableSteps() {
        List<SiegeKnowledgeData.Entry> out = new ArrayList<>();
        for (String id : STEPS) {
            SiegeKnowledgeData.Entry entry = SiegeKnowledgeRegistry.get(id);
            if (entry != null) out.add(entry);
        }
        return out;
    }

    private void refresh() {
        List<SiegeKnowledgeData.Entry> entries = availableSteps();
        int max = Math.max(0, entries.size() - cards.size());
        offset = Math.max(0, Math.min(max, offset));
        for (int i = 0; i < cards.size(); i++) {
            SiegeButton button = cards.get(i);
            int index = offset + i;
            boolean present = index < entries.size();
            button.visible = present;
            button.active = present;
            if (!present) continue;
            SiegeKnowledgeData.Entry entry = entries.get(index);
            String number = String.format("%02d", index + 1);
            button.setMessage(Component.literal(number + " · " + entry.title(spanish())));
            button.setTooltip(Tooltip.create(Component.literal(entry.summary(spanish()))));
        }
    }

    private void openSlot(int slot) {
        List<SiegeKnowledgeData.Entry> entries = availableSteps();
        int index = offset + slot;
        if (index < 0 || index >= entries.size()) return;
        SiegeUiSounds.confirm();
        minecraft.setScreen(new SiegeKnowledgeFileScreen(this, entries.get(index)));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseY >= startY && mouseY <= panelY + panelH) {
            List<SiegeKnowledgeData.Entry> entries = availableSteps();
            int max = Math.max(0, entries.size() - cards.size());
            int step = compact && panelW < 410 ? 1 : 2;
            offset = Math.max(0, Math.min(max, offset - (int)Math.signum(delta) * step));
            refresh();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xC5000000 : 0x97000000);
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, SiegeTheme.ORANGE);

        int x = panelX + 12;
        g.drawString(font, fit(label("BRIEFING DE INGRESO", "ENTRY BRIEFING") + " // "
                + SiegeRuntimeStatus.version(), panelW - 150), x, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "Ruta rápida para entender SIEGE antes de gastar recursos o entrar a sistemas peligrosos.",
                "Fast route for understanding SIEGE before spending resources or entering dangerous systems."), panelW - 24),
                x, panelY + 23, SiegeTheme.MUTED, false);
        g.drawString(font, fit(label(
                "Cada paso abre una ficha con fuente y fecha; lo histórico permanece marcado como histórico.",
                "Every step opens a sourced, dated file; historical information stays marked as historical."), panelW - 24),
                x, panelY + 35, SiegeTheme.CYAN, false);
        SiegeTheme.divider(g, panelX + 10, startY - 8, panelW - 20, SiegeTheme.ORANGE);

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
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
    @Override public void onClose() { minecraft.setScreen(parent); }
}

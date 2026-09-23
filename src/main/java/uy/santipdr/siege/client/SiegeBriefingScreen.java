package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** SIEGE 5.00 newcomer path: only the things a new player actually needs first. */
public final class SiegeBriefingScreen extends Screen {
    private static final List<String> STEPS = List.of(
            "server-overview",
            "newcomer-operational-rule",
            "race-system",
            "abilities-experience",
            "progression-v1-v4",
            "trials-basics",
            "executors-basics",
            "bosses-basics",
            "death-revive-current"
    );

    private final Screen parent;
    private final List<SiegeButton> cards = new ArrayList<>();
    private int panelX, panelY, panelW, panelH;
    private int startY, cardW, cardH, gap;
    private int offset;
    private boolean compact;

    public SiegeBriefingScreen(Screen parent) {
        super(Component.literal("SIEGE // BRIEFING"));
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
                Component.literal(compact ? "ATLAS" : label("ABRIR ATLAS", "OPEN ATLAS")), b -> {
            SiegeUiSounds.confirm();
            minecraft.setScreen(new SiegeAtlasScreen(this, SiegeAtlasIndex.View.RACES));
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
                    b -> openSlot(slot), i < 2 ? SiegeTheme.ORANGE : SiegeTheme.GOLD)
                    .withIcon(i < 2 ? "shield" : "overview");
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
            button.setMessage(Component.literal(fit(number + " · " + entry.title(spanish()), Math.max(20, cardW - 28))));
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
        int headerW = Math.max(30, panelW - (compact ? 112 : 160));
        g.drawString(font, fit(label("PRIMEROS PASOS", "FIRST STEPS") + " // "
                + SiegeRuntimeStatus.version(), headerW), x, panelY + 9, SiegeTheme.INK, false);
        g.drawString(font, fit(label(
                "No es una enciclopedia: es el camino corto para entrar y entender lo que te afecta primero.",
                "This is not an encyclopedia: it is the short path for joining and understanding what affects you first."), panelW - 24),
                x, panelY + 23, SiegeTheme.MUTED, false);
        g.drawString(font, fit(label(
                "Empezá por supervivencia y raza; después pasá a Trials, amenazas y reanimación.",
                "Start with survival and your race; then move into Trials, threats and revival."), panelW - 24),
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

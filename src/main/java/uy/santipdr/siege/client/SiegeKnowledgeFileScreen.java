package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** Full player-facing explanation for one current SIEGE topic. */
public final class SiegeKnowledgeFileScreen extends Screen {
    private final Screen parent;
    private final SiegeKnowledgeData.Entry entry;
    private int panelX, panelY, panelW, panelH;
    private int contentX, contentY, contentW, contentH;
    private int offset;
    private boolean compact;

    public SiegeKnowledgeFileScreen(Screen parent, SiegeKnowledgeData.Entry entry) {
        super(Component.literal("SIEGE // INFO"));
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
        int footer = hasNextStep() ? 27 : 0;
        contentH = Math.max(60, panelY + panelH - contentY - 10 - footer);

        addRenderableWidget(new SiegeButton(8, 7, compact ? 62 : 86, 19,
                Component.literal(label("VOLVER", "BACK")), b -> onClose(), SiegeTheme.RED)
                .withIcon("back").setCompactCenter(true));

        if (hasNextStep()) {
            addRenderableWidget(new SiegeButton(contentX, panelY + panelH - 24, contentW, 19,
                    Component.literal(nextStepLabel()), b -> openNextStep(), SiegeTheme.CYAN)
                    .withIcon(nextStepIcon()).setCompactCenter(true));
        }
    }

    private boolean hasNextStep() {
        if (entry == null) return false;
        return switch (entry.id()) {
            case "server-overview", "race-system", "progression-v1-v4", "trials-basics", "bosses-basics",
                 "race-subhuman", "race-saiyan", "race-ghoul" -> true;
            default -> false;
        };
    }

    private String nextStepLabel() {
        if (entry == null) return "";
        return switch (entry.id()) {
            case "server-overview" -> label("SIGUIENTE · CÓMO EMPEZAR", "NEXT · HOW TO START");
            case "race-system" -> label("ABRIR ATLAS DE RAZAS", "OPEN RACE ATLAS");
            case "progression-v1-v4" -> label("ABRIR MAPA DE PROGRESIÓN", "OPEN PROGRESSION MAP");
            case "trials-basics" -> label("VER TRIALS EN EL ATLAS", "OPEN TRIALS IN ATLAS");
            case "bosses-basics" -> label("ABRIR INTEL · UNIDADES Y BOSSES", "OPEN INTEL · UNITS AND BOSSES");
            case "race-subhuman" -> label("VER VARIANTES SUBHUMAN", "OPEN SUBHUMAN VARIANTS");
            case "race-saiyan" -> label("VER TRANSFORMACIONES SAIYAN", "OPEN SAIYAN TRANSFORMATIONS");
            case "race-ghoul" -> label("VER EVOLUCIONES GHOUL", "OPEN GHOUL EVOLUTIONS");
            default -> "";
        };
    }

    private String nextStepIcon() {
        if (entry == null) return "overview";
        return switch (entry.id()) {
            case "bosses-basics", "race-system", "race-subhuman", "race-saiyan", "race-ghoul" -> "intel";
            default -> "overview";
        };
    }

    private void openNextStep() {
        if (entry == null || minecraft == null) return;
        SiegeUiSounds.confirm();
        switch (entry.id()) {
            case "server-overview" -> minecraft.setScreen(new SiegeKnowledgeFileScreen(this,
                    SiegeKnowledgeRegistry.get("newcomer-operational-rule")));
            case "race-system" -> minecraft.setScreen(new SiegeRaceAtlasScreen(this));
            case "progression-v1-v4" -> minecraft.setScreen(new SiegeProgressionMapScreen(this));
            case "trials-basics" -> minecraft.setScreen(new SiegeAtlasScreen(this, SiegeAtlasIndex.View.PROGRESSION));
            case "bosses-basics" -> minecraft.setScreen(new IntelScreenV3(this));
            case "race-subhuman" -> minecraft.setScreen(new SiegeRaceVariantsScreen(this, "subhuman"));
            case "race-saiyan" -> minecraft.setScreen(new SiegeRaceVariantsScreen(this, "saiyan"));
            case "race-ghoul" -> minecraft.setScreen(new SiegeRaceVariantsScreen(this, "ghoul"));
            default -> { }
        }
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
        int accent = entry == null ? SiegeTheme.MUTED : domainAccent(entry.domain());
        SiegeTheme.panel(g, panelX, panelY, panelW, panelH, accent);

        String title = entry == null ? label("INFORMACIÓN NO DISPONIBLE", "INFORMATION UNAVAILABLE") : entry.title(spanish());
        g.drawString(font, fit(title, panelW - 24), panelX + 12, panelY + 9, SiegeTheme.INK, false);
        String meta = entry == null ? "--" : entry.domain().label(spanish());
        g.drawString(font, fit(meta, panelW - 24), panelX + 12, panelY + 22, accent, false);
        if (entry != null) {
            g.drawString(font, fit(entry.summary(spanish()), panelW - 24),
                    panelX + 12, panelY + 34, SiegeTheme.MUTED, false);
        }

        SiegeTheme.panel(g, contentX - 3, contentY - 3, contentW + 6, contentH + 6, accent);
        renderContent(g, accent);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
        SiegeScreenChrome.renderOverlay(this, g);
    }

    private int domainAccent(SiegeKnowledgeData.Domain domain) {
        return switch (domain) {
            case RACES -> SiegeTheme.GREEN;
            case PROGRESSION, TRIALS, MEDITATION -> SiegeTheme.GOLD;
            case EXECUTORS, BOSSES, DEATH_REVIVE -> SiegeTheme.RED;
            case ITEMS, RELICS, ASSEMBLING -> SiegeTheme.BLUE;
            default -> SiegeTheme.CYAN;
        };
    }

    private void renderContent(GuiGraphics g, int accent) {
        if (entry == null) return;
        List<FormattedCharSequence> lines = new ArrayList<>();
        FormattedCharSequence blank = Component.empty().getVisualOrderText();
        addParagraph(lines, entry.body(spanish()), contentW - 16, blank);

        int visibleLines = Math.max(1, contentH / (font.lineHeight + 2));
        int maxOffset = Math.max(0, lines.size() - visibleLines);
        offset = Math.max(0, Math.min(offset, maxOffset));
        int x = contentX + 8;
        int y = contentY + 7;
        int maxY = contentY + contentH - 8;
        g.enableScissor(contentX, contentY, contentX + contentW, contentY + contentH);
        for (int i = offset; i < lines.size() && y + font.lineHeight <= maxY; i++) {
            g.drawString(font, lines.get(i), x, y, SiegeTheme.INK, false);
            y += font.lineHeight + 2;
        }
        g.disableScissor();

        if (maxOffset > 0) {
            String scroll = label("RUEDA PARA LEER MÁS", "SCROLL TO READ MORE");
            g.drawString(font, scroll, contentX + contentW - font.width(scroll) - 7,
                    contentY + contentH - 11, accent, false);
        }
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

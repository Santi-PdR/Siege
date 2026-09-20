package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/**
 * Intel field/archive view. It belongs to Intel, not Settings: current notices,
 * missions, equipment, units, casualty states and older records live here.
 */
public final class SiegeArchiveScreen extends Screen {
    private final Screen parent;
    private SiegeArchiveData.Category category = SiegeArchiveData.Category.CURRENT;
    private SiegeArchiveData.Entry entry;
    private List<SiegeArchiveData.Entry> entries = List.of();
    private final List<SiegeButton> rows = new ArrayList<>();
    private final List<SiegeButton> tabs = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();
    private final Map<String, Integer> positions = new HashMap<>();
    private SiegeGuideLayout layout;
    private EditBox search;
    private SiegeButton prevPage, nextPage, up, down;
    private int page, scroll, maxScroll, contentHeight, thumbY, thumbHeight, grab;
    private boolean dragging;
    private double wheelRemainder;
    private String query = "";

    private record Line(int y, FormattedCharSequence text, int color, int spacing) { }

    public SiegeArchiveScreen(Screen parent) {
        super(Component.literal("SIEGE // INTEL ARCHIVE"));
        this.parent = parent;
    }

    private boolean es() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }
    private String label(String spanish, String english) { return es() ? spanish : english; }
    private Component text(String spanish, String english) { return Component.literal(label(spanish, english)); }

    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        rows.clear();
        tabs.clear();
        dragging = false;
        layout = SiegeGuideLayout.of(width, height);

        addRenderableWidget(new SiegeButton(8, 7, 64, 19, text("VOLVER", "BACK"), b -> onClose(), SiegeTheme.RED)
                .setCompactCenter(true));

        int i = 0;
        for (var value : SiegeArchiveData.Category.values()) {
            int slot = i++;
            int color = switch (value) {
                case CURRENT -> SiegeTheme.CYAN;
                case CASUALTY -> SiegeTheme.GREEN;
                case MISSIONS -> SiegeTheme.GOLD;
                case EQUIPMENT -> SiegeTheme.ORANGE;
                case UNITS -> SiegeTheme.RED;
                case HISTORY -> SiegeTheme.BLUE;
            };
            SiegeButton tab = new SiegeButton(
                    8 + slot % layout.columns() * (layout.tabWidth() + 4),
                    34 + slot / layout.columns() * 22,
                    layout.tabWidth(), 18,
                    Component.literal(value.title(es())), b -> {
                        if (value == category) return;
                        remember();
                        category = value;
                        entry = null;
                        page = scroll = 0;
                        query = "";
                        search.setValue("");
                        refresh();
                        SiegeUiSounds.click();
                    }, color).setSelected(value == category);
            tab.setTooltip(Tooltip.create(Component.literal(value.title(es()))));
            tabs.add(addRenderableWidget(tab));
        }

        int searchWidth = Math.max(50, width - 48);
        search = new EditBox(font, 8, layout.searchY(), searchWidth, 18,
                text("Buscar Intel", "Search Intel"));
        search.setHint(text("Buscar estado, misión, unidad, equipo…", "Search state, mission, unit, equipment…"));
        search.setMaxLength(100);
        search.setValue(query);
        search.setResponder(value -> {
            remember();
            query = value;
            page = 0;
            refresh();
        });
        addRenderableWidget(search);
        addRenderableWidget(new SiegeButton(width - 34, layout.searchY(), 26, 18, Component.literal("×"), b -> {
            search.setValue("");
            setFocused(search);
            SiegeUiSounds.click();
        }, SiegeTheme.RED).setCompactCenter(true)).setTooltip(Tooltip.create(text("Limpiar búsqueda", "Clear search")));

        int half = (layout.list().w() - 4) / 2;
        prevPage = addRenderableWidget(new SiegeButton(8, layout.footerY(), half, 18,
                Component.literal("←"), b -> turnPage(-1), SiegeTheme.RED).setCompactCenter(true));
        nextPage = addRenderableWidget(new SiegeButton(12 + half, layout.footerY(), layout.list().w() - half - 4, 18,
                Component.literal("→"), b -> turnPage(1), SiegeTheme.RED).setCompactCenter(true));
        prevPage.setTooltip(Tooltip.create(text("Página anterior", "Previous page")));
        nextPage.setTooltip(Tooltip.create(text("Página siguiente", "Next page")));

        var article = layout.article();
        up = addRenderableWidget(new SiegeButton(article.x(), layout.footerY(), 32, 18,
                Component.literal("↑"), b -> move(-Math.max(24, article.h() - 30)), SiegeTheme.GOLD).setCompactCenter(true));
        down = addRenderableWidget(new SiegeButton(article.x() + 36, layout.footerY(), 32, 18,
                Component.literal("↓"), b -> move(Math.max(24, article.h() - 30)), SiegeTheme.GOLD).setCompactCenter(true));
        up.setTooltip(Tooltip.create(text("Subir lectura", "Scroll up")));
        down.setTooltip(Tooltip.create(text("Bajar lectura", "Scroll down")));

        refresh();
    }

    private void refresh() {
        entries = SiegeArchiveData.entries(category, query, es());
        if (entry == null || !entries.contains(entry)) entry = entries.isEmpty() ? null : entries.get(0);
        if (entry != null) page = Math.max(0, entries.indexOf(entry)) / layout.capacity();
        scroll = entry == null ? 0 : positions.getOrDefault(entry.id(), 0);
        int index = 0;
        for (SiegeButton tab : tabs) tab.setSelected(SiegeArchiveData.Category.values()[index++] == category);
        buildRows();
        buildArticle();
    }

    private void buildRows() {
        for (SiegeButton row : rows) {
            if (getFocused() == row) setFocused(null);
            removeWidget(row);
        }
        rows.clear();
        int start = page * layout.capacity();
        int end = Math.min(entries.size(), start + layout.capacity());
        for (int i = start; i < end; i++) {
            var item = entries.get(i);
            SiegeButton row = new SiegeButton(layout.list().x(), layout.list().y() + (i - start) * 24,
                    layout.list().w(), 20, Component.literal(item.title(es())), b -> {
                remember();
                entry = item;
                scroll = positions.getOrDefault(item.id(), 0);
                buildArticle();
                for (SiegeButton candidate : rows)
                    candidate.setSelected(candidate.getMessage().getString().equals(item.title(es())));
                SiegeUiSounds.click();
            }, sourceColor(item.source())).setSelected(item == entry);
            row.setTooltip(Tooltip.create(Component.literal(item.source().label(es()) + " · " + item.title(es()))));
            rows.add(addRenderableWidget(row));
        }
        prevPage.active = page > 0;
        nextPage.active = end < entries.size();
    }

    private int sourceColor(SiegeArchiveData.Source source) {
        return switch (source) {
            case CURRENT_SIEGE -> SiegeTheme.CYAN;
            case SIEGE_ARCHIVE -> SiegeTheme.GOLD;
            case DVN_REFERENCE -> SiegeTheme.GREEN;
        };
    }

    private void buildArticle() {
        lines.clear();
        contentHeight = 0;
        wheelRemainder = 0;
        dragging = false;
        if (entry == null) {
            maxScroll = scroll = 0;
            return;
        }

        addLine(entry.title(es()), SiegeTheme.GOLD, 14);
        addLine(entry.source().label(es()) + "  //  PRIORITY " + entry.rank(), sourceColor(entry.source()), 17);
        addLine(label("INTEL 0.40: avisos más recientes prevalecen sobre avisos antiguos.",
                "INTEL 0.40: newer notices override older notices."), SiegeTheme.MUTED, 20);

        for (String paragraph : entry.body(es()).split("\n\n")) {
            int split = paragraph.indexOf('\n');
            if (split >= 0) {
                addLine(paragraph.substring(0, split), sourceColor(entry.source()), 12);
                addLine(paragraph.substring(split + 1), SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK, 19);
            } else {
                addLine(paragraph, SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK, 19);
            }
        }

        maxScroll = Math.max(0, contentHeight - Math.max(1, layout.article().h() - 16));
        scroll = SiegeUiLayout.clampScroll(scroll, maxScroll);
    }

    private void addLine(String value, int color, int after) {
        int wrap = Math.max(1, layout.article().w() - 28);
        for (var line : font.split(Component.literal(value), wrap)) {
            lines.add(new Line(contentHeight, line, color, 12));
            contentHeight += 12;
        }
        contentHeight += Math.max(0, after - 12);
    }

    private void remember() {
        if (entry != null) positions.put(entry.id(), scroll);
    }

    private void turnPage(int step) {
        int maxPage = Math.max(0, (entries.size() - 1) / Math.max(1, layout.capacity()));
        int next = Math.max(0, Math.min(maxPage, page + step));
        if (next == page) return;
        page = next;
        buildRows();
        SiegeUiSounds.click();
    }

    private void move(int amount) {
        scroll = SiegeUiLayout.clampScroll(scroll + amount, maxScroll);
        remember();
    }

    @Override
    public void tick() { if (search != null) search.tick(); }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, SiegeConfig.highContrast ? 0xE20A0B0D : 0xD00D0C0E);

        int titleLeft = 80;
        int titleRight = Math.max(titleLeft + 1, width - 8);
        int titleSpace = Math.max(1, titleRight - titleLeft);
        String rawTitle = width < 460 ? label("INTEL // ARCHIVO", "INTEL // ARCHIVE")
                : label("INTEL // ARCHIVO OPERATIVO · 0.40", "INTEL // OPERATIONAL ARCHIVE · 0.40");
        String title = font.plainSubstrByWidth(rawTitle, titleSpace);
        g.drawCenteredString(font, title, titleLeft + titleSpace / 2, 12,
                SiegeConfig.highContrast ? 0xFFFFFFFF : SiegeTheme.INK);

        var article = layout.article();
        int accent = entry == null ? SiegeTheme.GOLD : sourceColor(entry.source());
        SiegeTheme.panel(g, article.x(), article.y(), article.w(), article.h(), accent);
        g.enableScissor(article.x() + 2, article.y() + 2, article.right() - 2, article.bottom() - 2);
        if (entry == null) {
            String empty = label("SIN RESULTADOS", "NO RESULTS");
            g.drawString(font, font.plainSubstrByWidth(empty, article.w() - 16),
                    article.x() + 8, article.y() + 8, SiegeTheme.MUTED, false);
        } else {
            for (Line line : lines) {
                int y = article.y() + 8 + line.y() - scroll;
                if (y + 10 < article.y() || y >= article.bottom()) continue;
                g.drawString(font, line.text(), article.x() + 8, y, line.color(), false);
            }
        }
        g.disableScissor();

        up.active = scroll > 0;
        down.active = scroll < maxScroll;
        if (maxScroll > 0) {
            int track = Math.max(1, article.h() - 8);
            thumbHeight = SiegeUiLayout.scrollThumb(track, contentHeight);
            thumbY = article.y() + 4 + (track - thumbHeight) * scroll / maxScroll;
            g.fill(article.right() - 7, article.y() + 4, article.right() - 3, article.bottom() - 4, 0xFF4B4046);
            g.fill(article.right() - 7, thumbY, article.right() - 3, thumbY + thumbHeight, accent);
        }

        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (!Double.isFinite(delta) || delta == 0) return false;
        if (layout.list().contains(x, y)) {
            turnPage(delta > 0 ? -1 : 1);
            return true;
        }
        if (layout.article().contains(x, y) && maxScroll > 0) {
            wheelRemainder += Math.max(-5, Math.min(5, delta)) * 24;
            int amount = (int)wheelRemainder;
            wheelRemainder -= amount;
            move(-amount);
            return true;
        }
        return super.mouseScrolled(x, y, delta);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        var article = layout.article();
        if (button == 0 && maxScroll > 0 && x >= article.right() - 10 && x < article.right()
                && y >= article.y() + 4 && y < article.bottom() - 4) {
            dragging = true;
            grab = y >= thumbY && y < thumbY + thumbHeight ? (int)y - thumbY : thumbHeight / 2;
            drag(y);
            return true;
        }
        return super.mouseClicked(x, y, button);
    }

    private void drag(double y) {
        if (!Double.isFinite(y)) return;
        var article = layout.article();
        double fraction = (y - article.y() - 4 - grab) / Math.max(1, article.h() - 8 - thumbHeight);
        scroll = (int)Math.round(Math.max(0, Math.min(1, fraction)) * maxScroll);
        remember();
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (button == 0 && dragging) {
            drag(y);
            return true;
        }
        return super.mouseDragged(x, y, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        boolean handled = button == 0 && dragging;
        if (button == 0) dragging = false;
        return handled || super.mouseReleased(x, y, button);
    }

    @Override
    public void onClose() {
        SiegeUiSounds.back();
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}

package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import uy.santipdr.siege.SiegeMod;

/** Expandable reference archive. It never sends packets or changes gameplay. */
public final class SiegeGuideScreen extends Screen {
    private final Screen parent;
    private SiegeGuideData.Category category = SiegeGuideData.Category.ITEMS;
    private SiegeGuideData.Entry entry;
    private List<SiegeGuideData.Entry> entries = List.of();
    private final List<SiegeButton> entryButtons = new ArrayList<>();
    private final List<SiegeButton> tabButtons = new ArrayList<>();
    private final List<Block> blocks = new ArrayList<>();
    private final Map<String, Integer> positions = new HashMap<>();
    private final Set<String> revealed = new HashSet<>();
    private final Map<String, ResourceLocation> textures = new HashMap<>();
    private EditBox search;
    private String query = "";
    private SiegeGuideLayout layout;
    private SiegeButton prevPage, nextPage, up, down, pictures, reveal;
    private int page, scroll, maxScroll, contentHeight, thumbY, thumbHeight, grab;
    private boolean dragging;
    private double wheelRemainder;
    private record Block(int y, int h, FormattedCharSequence line, int color, SiegeGuideData.Art art) { }

    public SiegeGuideScreen(Screen parent) {
        super(Component.literal("SIEGE"));
        this.parent = parent;
    }
    private boolean es() { return minecraft.getLanguageManager().getSelected().startsWith("es_"); }
    private String label(String spanish, String english) { return es() ? spanish : english; }
    private Component text(String spanish, String english) { return Component.literal(label(spanish, english)); }
    @Override protected void init() {
        SiegeUiSounds.resetHover();
        entryButtons.clear(); tabButtons.clear(); dragging = false;
        layout = SiegeGuideLayout.of(width, height);
        addRenderableWidget(new SiegeButton(8, 7, 64, 19, text("VOLVER", "BACK"), b -> onClose(), SiegeTheme.RED).setCompactCenter(true));
        int i = 0;
        for (var value : SiegeGuideData.Category.values()) {
            int slot = i++;
            SiegeButton tab = new SiegeButton(8 + slot % layout.columns() * (layout.tabWidth() + 4),
                    34 + slot / layout.columns() * 22, layout.tabWidth(), 18, Component.literal(value.title(es())), b -> {
                if (value == category) return;
                remember(); category = value; entry = null; page = scroll = 0; query = "";
                search.setValue(""); refreshEntries(); SiegeUiSounds.click();
            }, value == SiegeGuideData.Category.CHRONICLES ? SiegeTheme.GOLD : SiegeTheme.RED).setSelected(value == category);
            tab.setTooltip(Tooltip.create(Component.literal(value.title(es()))));
            tabButtons.add(addRenderableWidget(tab));
        }
        search = new EditBox(font, 8, layout.searchY(), width - 48, 18, text("Buscar en esta sección", "Search this section"));
        search.setHint(text("Buscar en esta sección…", "Search this section…"));
        search.setMaxLength(80); search.setValue(query);
        search.setResponder(value -> { remember(); query = value; page = 0; refreshEntries(); });
        addRenderableWidget(search);
        addRenderableWidget(new SiegeButton(width - 34, layout.searchY(), 26, 18, Component.literal("×"), b -> {
            search.setValue(""); setFocused(search); SiegeUiSounds.click();
        }, SiegeTheme.RED).setCompactCenter(true)).setTooltip(Tooltip.create(text("Limpiar búsqueda", "Clear search")));
        int half = (layout.list().w() - 4) / 2;
        prevPage = addRenderableWidget(new SiegeButton(8, layout.footerY(), half, 18, Component.literal("←"), b -> turnPage(-1), SiegeTheme.RED).setCompactCenter(true));
        nextPage = addRenderableWidget(new SiegeButton(12 + half, layout.footerY(), layout.list().w() - half - 4, 18, Component.literal("→"), b -> turnPage(1), SiegeTheme.RED).setCompactCenter(true));
        prevPage.setTooltip(Tooltip.create(text("Página anterior", "Previous page")));
        nextPage.setTooltip(Tooltip.create(text("Página siguiente", "Next page")));
        var a = layout.article();
        up = addRenderableWidget(new SiegeButton(a.x(), layout.footerY(), 24, 18, Component.literal("↑"), b -> move(-Math.max(11, a.h() - 22)), SiegeTheme.GOLD).setCompactCenter(true));
        down = addRenderableWidget(new SiegeButton(a.x() + 28, layout.footerY(), 24, 18, Component.literal("↓"), b -> move(Math.max(11, a.h() - 22)), SiegeTheme.GOLD).setCompactCenter(true));
        up.setTooltip(Tooltip.create(text("Subir lectura", "Scroll reading up")));
        down.setTooltip(Tooltip.create(text("Bajar lectura", "Scroll reading down")));
        pictures = addRenderableWidget(new SiegeButton(a.x() + 56, layout.footerY(), Math.min(86, a.w() - 56), 18,
                text("IMÁGENES", "IMAGES"), b -> openImage(0), SiegeTheme.GOLD).setCompactCenter(true));
        pictures.setTooltip(Tooltip.create(text("Ampliar las imágenes originales completas", "Enlarge the complete original images")));
        reveal = addRenderableWidget(new SiegeButton(a.x() + 6, a.y() + Math.min(21, a.h() - 20), Math.max(1, a.w() - 20), 18,
                text("MOSTRAR SPOILERS", "SHOW SPOILERS"), b -> {
            if (entry == null) return;
            revealed.add(entry.id()); scroll = 0; buildArticle(); SiegeUiSounds.confirm();
        }, SiegeTheme.GOLD).setCompactCenter(true));
        refreshEntries();
    }
    private boolean locked() { return entry != null && entry.spoiler() && !revealed.contains(entry.id()); }
    private void remember() { if (entry != null) positions.put(entry.id(), scroll); }
    private void refreshEntries() {
        List<SiegeGuideData.Entry> merged = new ArrayList<>(SiegeGuideSupplemental.entries(category, query, es()));
        merged.addAll(SiegeGuideData.entries(category, query, es()));
        entries = List.copyOf(merged);
        if (entry == null || !entries.contains(entry)) entry = entries.isEmpty() ? null : entries.get(0);
        if (entry != null) page = entries.indexOf(entry) / layout.capacity();
        scroll = entry == null ? 0 : positions.getOrDefault(entry.id(), 0);
        int index = 0;
        for (SiegeButton button : tabButtons) button.setSelected(SiegeGuideData.Category.values()[index++] == category);
        buildList(); buildArticle();
    }
    private void turnPage(int step) {
        int next = Math.max(0, Math.min(Math.max(0, (entries.size() - 1) / layout.capacity()), page + step));
        if (next == page) return;
        page = next; buildList(); SiegeUiSounds.click();
    }
    private void buildList() {
        for (SiegeButton button : entryButtons) {
            if (getFocused() == button) setFocused(null);
            removeWidget(button);
        }
        entryButtons.clear();
        for (int i = page * layout.capacity(); i < Math.min(entries.size(), (page + 1) * layout.capacity()); i++) {
            var item = entries.get(i);
            SiegeButton button = new SiegeButton(layout.list().x(), layout.list().y() + (i % layout.capacity()) * 24,
                    layout.list().w(), 20, Component.literal(item.title(es())), b -> {
                remember(); entry = item; scroll = positions.getOrDefault(item.id(), 0); buildArticle();
                for (SiegeButton row : entryButtons) row.setSelected(row.getMessage().getString().equals(item.title(es())));
                SiegeUiSounds.click();
            }, SiegeTheme.RED).setSelected(item == entry);
            button.setTooltip(Tooltip.create(Component.literal(item.title(es()))));
            entryButtons.add(addRenderableWidget(button));
        }
        prevPage.active = page > 0;
        nextPage.active = (page + 1) * layout.capacity() < entries.size();
    }
    private void lines(String value, int color) {
        for (var line : font.split(Component.literal(value), Math.max(1, layout.article().w() - 26))) {
            blocks.add(new Block(contentHeight, 12, line, color, null)); contentHeight += 12;
        }
    }
    private void buildArticle() {
        blocks.clear(); contentHeight = 0; wheelRemainder = 0; dragging = false;
        reveal.visible = locked(); pictures.visible = entry != null && !entry.images().isEmpty();
        if (entry == null || locked()) { maxScroll = scroll = 0; return; }
        lines(entry.title(es()), SiegeTheme.GOLD); contentHeight += 10;
        if (entry.category() == SiegeGuideData.Category.INSPIRATIONS) addImages();
        for (String paragraph : entry.body(es()).split("\n\n")) {
            int split = paragraph.indexOf('\n');
            if (split >= 0) { lines(paragraph.substring(0, split), SiegeTheme.GOLD); lines(paragraph.substring(split + 1), SiegeTheme.INK); }
            else lines(paragraph, SiegeTheme.INK);
            contentHeight += 12;
        }
        if (entry.category() != SiegeGuideData.Category.INSPIRATIONS) addImages();
        maxScroll = Math.max(0, contentHeight - Math.max(1, layout.article().h() - 16));
        scroll = SiegeUiLayout.clampScroll(scroll, maxScroll);
    }
    private void addImages() {
        for (var art : entry.images()) {
            int imageH = Math.max(38, Math.min(220, layout.article().h() * 2 / 3));
            blocks.add(new Block(contentHeight, imageH, null, 0, art)); contentHeight += imageH + 5;
            lines(art.caption(es()), SiegeTheme.MUTED); contentHeight += 12;
        }
    }
    private ResourceLocation texture(SiegeGuideData.Art art) {
        return textures.computeIfAbsent(art.file(), file -> new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/guide/" + file));
    }
    private SiegeGuideLayout.Rect imageBox(Block block) {
        var a = layout.article();
        return SiegeGuideLayout.fit(new SiegeGuideLayout.Rect(a.x() + 8, a.y() + 8 + block.y() - scroll, a.w() - 26, block.h()), block.art().width(), block.art().height());
    }
    private void openImage(int index) {
        if (entry == null || entry.images().isEmpty() || locked()) return;
        remember(); SiegeUiSounds.click(); minecraft.setScreen(new SiegeGuideImageScreen(this, entry.images(), index));
    }
    private void move(int amount) { scroll = SiegeUiLayout.clampScroll(scroll + amount, maxScroll); remember(); }
    @Override public void tick() { search.tick(); }
    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying(); SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, 0xD00D0C0E);
        g.drawCenteredString(font, label("GUÍA SIEGE", "SIEGE GUIDE"), (80 + width - 8) / 2, 12, SiegeTheme.INK);
        var a = layout.article();
        SiegeTheme.panel(g, a.x(), a.y(), a.w(), a.h(), SiegeTheme.GOLD);
        g.enableScissor(a.x() + 2, a.y() + 2, a.right() - 2, a.bottom() - 2);
        if (entry == null) {
            g.drawString(font, font.plainSubstrByWidth(label("SIN RESULTADOS", "NO RESULTS"), a.w() - 16), a.x() + 8, a.y() + 8, SiegeTheme.MUTED, false);
        } else if (locked()) {
            g.drawString(font, font.plainSubstrByWidth(label("DESENLACE · SPOILERS", "OUTCOME · SPOILERS"), a.w() - 16), a.x() + 8, a.y() + 6, SiegeTheme.GOLD, false);
        } else {
            for (Block block : blocks) {
                int y = a.y() + 8 + block.y() - scroll;
                if (y + block.h() < a.y() || y >= a.bottom()) continue;
                if (block.art() == null) g.drawString(font, block.line(), a.x() + 8, y, block.color(), false);
                else {
                    var box = imageBox(block);
                    var art = block.art();
                    g.blit(texture(art), box.x(), box.y(), box.w(), box.h(), 0, 0, art.width(), art.height(), art.width(), art.height());
                    if (a.contains(mouseX, mouseY) && box.contains(mouseX, mouseY)) SiegeTheme.frame(g, box.x(), box.y(), box.w(), box.h(), SiegeTheme.GOLD);
                }
            }
        }
        g.disableScissor();
        up.active = scroll > 0; down.active = scroll < maxScroll;
        reveal.visible = locked();
        if (maxScroll > 0) {
            int track = Math.max(1, a.h() - 8);
            thumbHeight = SiegeUiLayout.scrollThumb(track, contentHeight);
            thumbY = a.y() + 4 + (track - thumbHeight) * scroll / maxScroll;
            g.fill(a.right() - 7, a.y() + 4, a.right() - 3, a.bottom() - 4, 0xFF4B4046);
            g.fill(a.right() - 7, thumbY, a.right() - 3, thumbY + thumbHeight, SiegeTheme.GOLD);
        }
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }
    @Override public boolean mouseScrolled(double x, double y, double delta) {
        if (!Double.isFinite(delta) || delta == 0) return false;
        if (layout.list().contains(x, y)) { turnPage(delta > 0 ? -1 : 1); return true; }
        if (layout.article().contains(x, y) && maxScroll > 0) {
            wheelRemainder += Math.max(-5, Math.min(5, delta)) * 24;
            int amount = (int)wheelRemainder; wheelRemainder -= amount; move(-amount); return true;
        }
        return super.mouseScrolled(x, y, delta);
    }
    @Override public boolean mouseClicked(double x, double y, int button) {
        var a = layout.article();
        if (button == 0 && maxScroll > 0 && x >= a.right() - 10 && x < a.right() && y >= a.y() + 4 && y < a.bottom() - 4) {
            dragging = true; grab = y >= thumbY && y < thumbY + thumbHeight ? (int)y - thumbY : thumbHeight / 2;
            drag(y); return true;
        }
        if (button == 0 && !locked() && a.contains(x, y)) for (Block block : blocks)
            if (block.art() != null && imageBox(block).contains(x, y)) { openImage(entry.images().indexOf(block.art())); return true; }
        return super.mouseClicked(x, y, button);
    }
    private void drag(double y) {
        if (!Double.isFinite(y)) return;
        double fraction = (y - layout.article().y() - 4 - grab) / Math.max(1, layout.article().h() - 8 - thumbHeight);
        scroll = (int)Math.round(Math.max(0, Math.min(1, fraction)) * maxScroll); remember();
    }
    @Override public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (button == 0 && dragging) { drag(y); return true; }
        return super.mouseDragged(x, y, button, dx, dy);
    }
    @Override public boolean mouseReleased(double x, double y, int button) {
        boolean handled = button == 0 && dragging;
        if (button == 0) dragging = false;
        return super.mouseReleased(x, y, button) || handled;
    }
    @Override public void onClose() { remember(); SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}

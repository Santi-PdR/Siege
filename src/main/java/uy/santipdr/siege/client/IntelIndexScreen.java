package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** A full-width, mouse-accessible dossier picker for every GUI scale. */
public final class IntelIndexScreen extends Screen {
    private final Screen parent;
    private final IntelIndexModel model;
    private final String categoryName, currentCode;
    private final Consumer<String> select;
    private final List<Row> rows = new ArrayList<>();
    private final List<SiegeButton> stars = new ArrayList<>();
    private final boolean favoriteScope;
    private boolean favoritesOnly, reverse = SiegeConfig.indexReverse;
    private int minimumThreat;
    private IntelIndexModel.Order order = IntelIndexModel.Order.values()[SiegeConfig.indexOrder];
    private String query = "";
    private EditBox search;
    private SiegeButton sort, previous, next, first, last, favorites, threat, direction, reset, clear;
    private int page, capacity, resultCount;
    private boolean initialSelection = true;

    public IntelIndexScreen(Screen parent, List<IntelEntry> entries, String categoryName, String currentCode, boolean favoriteScope, Consumer<String> select) {
        super(Component.literal("Intel"));
        this.parent = parent;
        this.favoriteScope = favoriteScope;
        this.model = new IntelIndexModel(entries);
        this.categoryName = categoryName;
        this.currentCode = currentCode;
        this.select = select;
    }
    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        int oldTop = page * Math.max(1, capacity);
        rows.clear(); stars.clear();
        capacity = IntelIndexModel.rowsPerPage(height);
        page = oldTop / capacity;
        addRenderableWidget(new SiegeButton(8, 7, 74, 20, text("VOLVER", "BACK"), b -> onClose(), 0xFFD65A4B));
        search = new EditBox(font, 8, 35, width - 144, 20, text("Buscar en el índice", "Search index"));
        search.setHint(text("Nombre, código o arma...", "Name, code or weapon..."));
        search.setMaxLength(80);
        search.setValue(query);
        search.setResponder(value -> { query = value; page = 0; refresh(); });
        addRenderableWidget(search);
        clear = addRenderableWidget(new SiegeButton(width - 132, 35, 24, 20, Component.literal("×"), b -> search.setValue(""), 0xFFD65A4B));
        clear.setTooltip(Tooltip.create(text("Limpiar búsqueda", "Clear search")));
        sort = addRenderableWidget(new SiegeButton(width - 104, 35, 96, 20, Component.empty(), b -> {
            order = order.next(); SiegeConfig.indexOrder = order.ordinal(); SiegeConfig.save(); page = 0; refresh(); SiegeUiSounds.click();
        }, 0xFFD6A94B));
        int filterWidth = (width - 28) / 4;
        favorites = addRenderableWidget(new SiegeButton(8, 59, filterWidth, 18, text("FAVORITOS", "FAVORITES"), b -> {
            favoritesOnly = !favoritesOnly; page = 0; refresh(); SiegeUiSounds.click();
        }, 0xFFD6A94B));
        threat = addRenderableWidget(new SiegeButton(12 + filterWidth, 59, filterWidth, 18, Component.empty(), b -> {
            minimumThreat = minimumThreat == 0 ? 3 : minimumThreat == 5 ? 0 : minimumThreat + 1;
            page = 0; refresh(); SiegeUiSounds.click();
        }, 0xFFD65A4B));
        direction = addRenderableWidget(new SiegeButton(16 + filterWidth * 2, 59, filterWidth, 18, Component.empty(), b -> {
            reverse = !reverse; SiegeConfig.indexReverse = reverse; SiegeConfig.save(); page = 0; refresh(); SiegeUiSounds.click();
        }, 0xFF55BFD9));
        reset = addRenderableWidget(new SiegeButton(20 + filterWidth * 3, 59, filterWidth, 18, text("LIMPIAR", "CLEAR"), b -> {
            favoritesOnly = false; minimumThreat = 0; page = 0; search.setValue(""); refresh(); SiegeUiSounds.click();
        }, 0xFF55BFD9));
        first = addRenderableWidget(new SiegeButton(8, height - 28, 24, 20, Component.literal("|←"), b -> goPage(0), 0xFF55BFD9));
        previous = addRenderableWidget(new SiegeButton(36, height - 28, 44, 20, Component.literal("←"), b -> step(-1), 0xFF55BFD9));
        next = addRenderableWidget(new SiegeButton(width - 80, height - 28, 44, 20, Component.literal("→"), b -> step(1), 0xFF55BFD9));
        last = addRenderableWidget(new SiegeButton(width - 32, height - 28, 24, 20, Component.literal("→|"), b -> goPage(IntelIndexModel.lastPage(resultCount, capacity)), 0xFF55BFD9));
        first.setTooltip(Tooltip.create(text("Primera página", "First page")));
        last.setTooltip(Tooltip.create(text("Última página", "Last page")));
        List<IntelEntry> results = results();
        if (initialSelection) {
            for (int i = 0; i < results.size(); i++) if (results.get(i).code().equals(currentCode)) page = i / capacity;
            initialSelection = false;
        }
        refresh();
    }
    private void refresh() {
        for (SiegeButton star : stars) removeWidget(star);
        stars.clear();
        for (Row row : rows) removeWidget(row);
        rows.clear();
        List<IntelEntry> results = results();
        resultCount = results.size();
        page = IntelIndexModel.clampPage(page, resultCount, capacity);
        first.active = previous.active = page > 0;
        last.active = next.active = page < IntelIndexModel.lastPage(resultCount, capacity);
        String mode = switch (order) {
            case CODE -> label("CÓDIGO", "CODE");
            case NAME -> label("NOMBRE", "NAME");
            case THREAT -> label("AMENAZA", "THREAT");
        };
        sort.setMessage(Component.literal(mode));
        clear.active = !query.isEmpty();
        favorites.setSelected(favoritesOnly || favoriteScope);
        favorites.active = !favoriteScope;
        threat.setMessage(Component.literal(minimumThreat == 0 ? label("AMENAZA: *", "THREAT: *") : label("AMENAZA ≥", "THREAT ≥") + minimumThreat));
        threat.setTooltip(Tooltip.create(text("Amenaza mínima: todas, 3, 4 o 5", "Minimum threat: all, 3, 4 or 5")));
        direction.setMessage(text(reverse ? "INVERSO" : "NORMAL", reverse ? "REVERSED" : "NORMAL"));
        reset.active = favoritesOnly || minimumThreat > 0 || !query.isEmpty();
        sort.setTooltip(Tooltip.create(text("Ordenar por código, nombre o amenaza", "Sort by code, name or threat")));
        for (int i = page * capacity; i < Math.min(resultCount, (page + 1) * capacity); i++) {
            Row row = new Row(92 + (i - page * capacity) * 34, results.get(i));
            rows.add(addRenderableWidget(row));
            IntelEntry entry = results.get(i);
            SiegeButton star = new SiegeButton(width - 38, row.getY(), 30, 30,
                    Component.literal(SiegeConfig.isFavoriteIntel(entry.code()) ? "★" : "☆"), b -> {
                        SiegeConfig.toggleFavoriteIntel(entry.code()); SiegeUiSounds.click(); refresh();
                    }, 0xFFD6A94B).setSelected(SiegeConfig.isFavoriteIntel(entry.code()));
            star.setTooltip(Tooltip.create(text("Guardar o quitar favorito", "Save or remove favorite")));
            stars.add(addRenderableWidget(star));
        }
    }
    private List<IntelEntry> results() {
        return model.results(query, order, spanish(), reverse, minimumThreat, favoritesOnly || favoriteScope, SiegeConfig::isFavoriteIntel);
    }
    private void goPage(int target) {
        int nextPage = IntelIndexModel.clampPage(target, resultCount, capacity);
        if (nextPage == page) return;
        page = nextPage; refresh(); SiegeUiSounds.click();
    }
    private void step(int direction) {
        int target = IntelIndexModel.clampPage(page + direction, resultCount, capacity);
        if (page == target) return;
        page = target; refresh(); SiegeUiSounds.click();
    }
    @Override
    public void tick() { search.tick(); }
    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, 0xED0B1014);
        g.drawString(font, font.plainSubstrByWidth(label("ÍNDICE · ", "INDEX · ") + categoryName, width - 104), 96, 13, 0xFFE7DFC9, false);
        g.drawString(font, resultCount + " / " + model.size() + label(" expedientes", " dossiers"), 8, 81, 0xFF8FA0A9, false);
        if (resultCount == 0) g.drawCenteredString(font, label("SIN RESULTADOS", "NO RESULTS"), width / 2, height / 2, 0xFFB8C1C7);
        String count = (resultCount == 0 ? 0 : page + 1) + " / " + (resultCount == 0 ? 0 : IntelIndexModel.lastPage(resultCount, capacity) + 1);
        g.drawCenteredString(font, count, width / 2, height - 22, 0xFFB8C1C7);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }
    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (delta != 0 && x >= 8 && x < width - 8 && y >= 92 && y < height - 38) {
            step(delta > 0 ? -1 : 1); return true;
        }
        return super.mouseScrolled(x, y, delta);
    }
    @Override
    public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
    @Override
    public boolean isPauseScreen() { return false; }
    private boolean spanish() { return minecraft.getLanguageManager().getSelected().startsWith("es_"); }
    private String label(String es, String en) { return spanish() ? es : en; }
    private Component text(String es, String en) { return Component.literal(label(es, en)); }

    private final class Row extends Button {
        private final IntelEntry entry;
        Row(int y, IntelEntry entry) {
            super(8, y, IntelIndexScreen.this.width - 50, 30, Component.literal(entry.code() + " · " + entry.name()),
                    b -> {}, DEFAULT_NARRATION);
            this.entry = entry;
            setTooltip(Tooltip.create(Component.literal(entry.name() + " · " + entry.text(spanish()).armament())));
        }
        @Override
        public void onPress() { select.accept(entry.code()); minecraft.setScreen(parent); }
        @Override
        public void playDownSound(SoundManager manager) { }
        @Override
        protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            int x = getX(), y = getY(), w = getWidth();
            boolean selected = entry.code().equals(currentCode);
            int accent = selected ? 0xFFD6A94B : isHoveredOrFocused() ? 0xFF78D8E8 : 0xFF53636C;
            g.fill(x, y, x + w, y + 30, isHoveredOrFocused() ? 0xFF1B2931 : 0xFF10191F);
            g.fill(x, y, x + 2, y + 30, accent);
            String name = (SiegeConfig.isFavoriteIntel(entry.code()) ? "★ " : "") + entry.code() + " · " + entry.name();
            g.drawString(font, font.plainSubstrByWidth(name, w - 18), x + 8, y + 5, selected ? 0xFFF0D889 : 0xFFE5E8E8, false);
            String detail = label("Amenaza ", "Threat ") + (entry.threat() > 0 ? entry.threat() + "/5" : "—")
                    + " · HP " + entry.hp() + " · " + entry.text(spanish()).armament();
            g.drawString(font, font.plainSubstrByWidth(detail, w - 18), x + 8, y + 17, 0xFF98ABB5, false);
        }
    }
}

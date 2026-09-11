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
    private IntelIndexModel.Order order = IntelIndexModel.Order.CODE;
    private String query = "";
    private EditBox search;
    private SiegeButton sort, previous, next;
    private int page, capacity, resultCount;
    private boolean initialSelection = true;

    public IntelIndexScreen(Screen parent, List<IntelEntry> entries, String categoryName, String currentCode, Consumer<String> select) {
        super(Component.literal("Intel"));
        this.parent = parent;
        this.model = new IntelIndexModel(entries);
        this.categoryName = categoryName;
        this.currentCode = currentCode;
        this.select = select;
    }
    @Override
    protected void init() {
        SiegeUiSounds.resetHover();
        rows.clear();
        capacity = IntelIndexModel.rowsPerPage(height);
        addRenderableWidget(new SiegeButton(8, 7, 74, 20, text("VOLVER", "BACK"), b -> onClose(), 0xFFD65A4B));
        search = new EditBox(font, 8, 35, width - 144, 20, text("Buscar en el índice", "Search index"));
        search.setHint(text("Nombre, código o arma...", "Name, code or weapon..."));
        search.setMaxLength(80);
        search.setValue(query);
        search.setResponder(value -> { query = value; page = 0; refresh(); });
        addRenderableWidget(search);
        SiegeButton clear = addRenderableWidget(new SiegeButton(width - 132, 35, 24, 20, Component.literal("×"), b -> search.setValue(""), 0xFFD65A4B));
        clear.setTooltip(Tooltip.create(text("Limpiar búsqueda", "Clear search")));
        sort = addRenderableWidget(new SiegeButton(width - 104, 35, 96, 20, Component.empty(), b -> {
            order = order.next(); page = 0; refresh(); SiegeUiSounds.click();
        }, 0xFFD6A94B));
        previous = addRenderableWidget(new SiegeButton(8, height - 28, 76, 20, text("← PÁGINA", "← PAGE"), b -> step(-1), 0xFF55BFD9));
        next = addRenderableWidget(new SiegeButton(width - 84, height - 28, 76, 20, text("PÁGINA →", "PAGE →"), b -> step(1), 0xFF55BFD9));
        List<IntelEntry> results = model.results(query, order, spanish());
        if (initialSelection) {
            for (int i = 0; i < results.size(); i++) if (results.get(i).code().equals(currentCode)) page = i / capacity;
            initialSelection = false;
        }
        refresh();
    }
    private void refresh() {
        for (Row row : rows) removeWidget(row);
        rows.clear();
        List<IntelEntry> results = model.results(query, order, spanish());
        resultCount = results.size();
        page = IntelIndexModel.clampPage(page, resultCount, capacity);
        previous.active = page > 0;
        next.active = page < IntelIndexModel.lastPage(resultCount, capacity);
        String mode = switch (order) {
            case CODE -> label("CÓDIGO", "CODE");
            case NAME -> label("NOMBRE", "NAME");
            case THREAT -> label("AMENAZA ↓", "THREAT ↓");
        };
        sort.setMessage(Component.literal(mode));
        sort.setTooltip(Tooltip.create(text("Ordenar por código, nombre o amenaza", "Sort by code, name or threat")));
        for (int i = page * capacity; i < Math.min(resultCount, (page + 1) * capacity); i++) {
            Row row = new Row(68 + (i - page * capacity) * 34, results.get(i));
            rows.add(addRenderableWidget(row));
        }
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
        g.drawString(font, resultCount + label(" expedientes", " dossiers"), 8, 58, 0xFF8FA0A9, false);
        if (resultCount == 0) g.drawCenteredString(font, label("SIN RESULTADOS", "NO RESULTS"), width / 2, height / 2, 0xFFB8C1C7);
        String count = (resultCount == 0 ? 0 : page + 1) + " / " + (resultCount == 0 ? 0 : IntelIndexModel.lastPage(resultCount, capacity) + 1);
        g.drawCenteredString(font, count, width / 2, height - 22, 0xFFB8C1C7);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }
    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (delta != 0 && x >= 8 && x < width - 8 && y >= 68 && y < height - 38) {
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
            super(8, y, IntelIndexScreen.this.width - 16, 30, Component.literal(entry.code() + " · " + entry.name()),
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

package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

/**
 * Presentation-only Multiplayer screen. Vanilla owns server persistence, pings,
 * LAN discovery, entry input, confirmation dialogs and the connection lifecycle.
 */
public final class SiegeMultiplayerScreen extends JoinMultiplayerScreen {
    private static final int RED = 0xFFE54852;
    private final Screen parent;
    private final List<Control> controls = new ArrayList<>();
    private SiegeMultiplayerLayout layout;
    private List<Component> entryTooltip;
    private ServerSelectionList.Entry lastSelection;

    public SiegeMultiplayerScreen(Screen parent) {
        super(parent);
        this.parent = parent;
    }

    @Override
    protected void init() {
        controls.clear();
        super.init();
        layout = SiegeMultiplayerLayout.of(width, height);
        serverSelectionList.updateSize(layout.listWidth(), height, layout.top(), layout.bottom());
        serverSelectionList.setLeftPos(layout.x());
        serverSelectionList.setRenderBackground(false);
        serverSelectionList.setRenderTopAndBottom(false);
        serverSelectionList.setScrollAmount(serverSelectionList.getScrollAmount());
        // Retain vanilla button objects for onSelectedChange and their original callbacks.
        // Only their registered visual/input widgets are replaced.
        for (var child : List.copyOf(children())) {
            if (!(child instanceof Button original)) continue;
            String key = original.getMessage().getContents() instanceof TranslatableContents tr ? tr.getKey() : "";
            int slot = switch (key) {
                case "selectServer.select" -> 0;
                case "selectServer.direct" -> 1;
                case "selectServer.add" -> 2;
                case "selectServer.edit" -> 3;
                case "selectServer.delete" -> 4;
                case "selectServer.refresh" -> 5;
                case "gui.back", "gui.cancel" -> 6;
                default -> -1;
            };
            if (slot < 0) continue;
            int count = slot < 3 ? 3 : 4;
            int index = slot < 3 ? slot : slot - 3;
            Component message = slot == 0 ? Component.literal(label("CONECTAR", "CONNECT")) : original.getMessage();
            SiegeButton button = new SiegeButton(layout.buttonX(index, count),
                    slot < 3 ? layout.firstRow() : layout.secondRow(),
                    layout.buttonWidth(index, count), 20, message, b -> {
                if (!original.active) return;
                if (slot == 6) onClose();
                else {
                    SiegeUiSounds.click();
                    if (slot == 5) refresh();
                    else original.onPress();
                }
            }, slot == 4 ? 0xFFCA7777 : RED).setCompactCenter(true);
            button.active = original.active;
            button.setTooltip(Tooltip.create(slot == 0
                    ? Component.literal(label("Conectar al servidor seleccionado.", "Connect to the selected server."))
                    : original.getMessage()));
            removeWidget(original);
            addRenderableWidget(button);
            controls.add(new Control(original, button));
        }
        SiegeUiSounds.resetHover();
        lastSelection = serverSelectionList.getSelected();
        setInitialFocus(serverSelectionList);
    }

    @Override
    protected void onSelectedChange() {
        super.onSelectedChange();
        syncControls();
    }

    private void syncControls() {
        if (controls == null) return;
        for (Control control : controls) control.view.active = control.original.active;
    }

    private void refresh() { minecraft.setScreen(new SiegeMultiplayerScreen(parent)); }

    @Override
    public void onClose() {
        SiegeUiSounds.back();
        minecraft.setScreen(parent);
    }

    @Override
    public boolean keyPressed(int key, int scan, int modifiers) {
        // Preserve vanilla F5 without constructing a vanilla screen or nesting parents.
        if (key == 294) { SiegeUiSounds.click(); refresh(); return true; }
        return super.keyPressed(key, scan, modifiers);
    }

    @Override
    public void setToolTip(List<Component> tooltip) { entryTooltip = tooltip; }

    @Override
    public void renderBackground(GuiGraphics g) {
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, 0x79000000);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        syncControls();
        entryTooltip = null;
        int x = layout.x(), right = x + layout.width();
        g.fill(x, 8, right, layout.top() - 5, 0xEB101113);
        g.fill(x, 8, x + 3, layout.top() - 5, RED);
        g.drawString(font, "SIEGE / " + label("DESPLIEGUE", "DEPLOYMENT"), x + 12, 15, 0xFFF0EDEA, false);
        String count = label("Servidores guardados: ", "Saved servers: ") + getServers().size();
        g.drawString(font, count, x + 12, 29, 0xFFADB1B5, false);
        if (layout.top() > 48)
            g.drawString(font, label("Seleccioná un destino para conectar.", "Select a destination to connect."),
                    x + 12, 43, 0xFFADB1B5, false);
        panel(g, x, layout.top(), layout.listWidth(), layout.bottom() - layout.top());
        renderRowPlates(g);
        // JoinMultiplayerScreen draws its own title and dirt background. Render its
        // registered children once instead, preserving entry behavior and tooltips.
        for (var child : children()) if (child instanceof Renderable renderable)
            renderable.render(g, mouseX, mouseY, partialTick);
        renderSelectionSummary(g, mouseX, mouseY);
        if (entryTooltip != null) g.renderComponentTooltip(font, entryTooltip, mouseX, mouseY);
        SiegeUiSounds.updateHover(children());
        var selected = serverSelectionList.getSelected();
        if (selected != lastSelection) {
            if (selected != null) SiegeUiSounds.click();
            lastSelection = selected;
        }
    }

    private void renderRowPlates(GuiGraphics g) {
        g.enableScissor(layout.x(), layout.top(), layout.x() + layout.listWidth(), layout.bottom());
        var entries = serverSelectionList.children();
        for (int i = 0; i < entries.size(); i++) {
            int y = layout.top() + 4 + i * 36 - (int)serverSelectionList.getScrollAmount();
            if (y + 36 < layout.top() || y >= layout.bottom()) continue;
            boolean selected = entries.get(i) == serverSelectionList.getSelected();
            int left = serverSelectionList.getRowLeft() - 2;
            g.fill(left, y, left + serverSelectionList.getRowWidth(), y + 34,
                    selected ? 0xC337292B : (i % 2 == 0 ? 0xA21E2023 : 0xA2181A1D));
            if (selected) g.fill(left - 3, y, left - 1, y + 34, RED);
        }
        g.disableScissor();
    }

    private void renderSelectionSummary(GuiGraphics g, int mouseX, int mouseY) {
        var selected = serverSelectionList.getSelected();
        ServerData server = selected instanceof ServerSelectionList.OnlineServerEntry online
                ? online.getServerData() : null;
        String status = server == null
                ? selected instanceof ServerSelectionList.NetworkServerEntry ? label("RED LOCAL", "LOCAL NETWORK")
                    : label("Seleccioná un servidor", "Select a server")
                : status(server);
        int color = server == null ? 0xFFADB1B5 : statusColor(server);
        int footerY = layout.bottom() + 5;
        String summary = server == null ? status : server.name + " / " + status;
        g.fill(layout.x(), layout.bottom() + 2, layout.x() + layout.width(), layout.firstRow() - 4, 0xEA101113);
        g.drawString(font, fit(summary, layout.width() - 16), layout.x() + 8, footerY, color, false);
        if (mouseX >= layout.x() && mouseX < layout.x() + layout.width()
                && mouseY >= footerY && mouseY < footerY + 12 && font.width(summary) > layout.width() - 16)
            entryTooltip = List.of(Component.literal(summary));
        if (layout.detailWidth() <= 0) return;
        int x = layout.detailX(), y = layout.top(), w = layout.detailWidth();
        panel(g, x, y, w, layout.bottom() - y);
        g.drawString(font, label("DESTINO SELECCIONADO", "SELECTED DESTINATION"), x + 10, y + 10, 0xFFAAAEB3, false);
        g.fill(x + 10, y + 24, x + w - 10, y + 25, 0xFF5C363B);
        if (server == null) {
            drawWrapped(g, label("Elegí un servidor de la lista o usá Conexión directa. Agregar servidor permite guardar un destino.",
                    "Choose a server from the list or use Direct Connection. Add Server saves a destination."),
                    x + 10, y + 35, w - 20, layout.bottom() - 12, 0xFFC4C6C9);
            return;
        }
        int next = drawWrapped(g, server.name, x + 10, y + 34, w - 20, y + 64, 0xFFF0EDEA);
        g.drawString(font, fit(status, w - 20), x + 10, next + 5, color, false);
        next += 23;
        g.drawString(font, fit(label("Versión: ", "Version: ") + server.version.getString(), w - 20),
                x + 10, next, 0xFFC4C6C9, false);
        next += 18;
        if (next + 24 < layout.bottom()) {
            g.drawString(font, label("MENSAJE DEL SERVIDOR", "SERVER MESSAGE"), x + 10, next, 0xFFAAAEB3, false);
            drawWrapped(g, server.motd.getString(), x + 10, next + 15, w - 20, layout.bottom() - 10, 0xFFDDDFE0);
        }
        if (mouseX >= x && mouseX < x + w && mouseY >= y + 26 && mouseY < layout.bottom())
            entryTooltip = List.of(Component.literal(server.name), server.version, server.motd);
    }

    private String status(ServerData s) {
        if (!s.pinged || s.ping < -1) return label("CONSULTANDO…", "QUERYING…");
        if (s.ping < 0) return label("SIN RESPUESTA", "NO RESPONSE");
        if (s.protocol != SharedConstants.getCurrentVersion().getProtocolVersion())
            return label("VERSIÓN INCOMPATIBLE", "INCOMPATIBLE VERSION");
        return label("EN LÍNEA", "ONLINE") + " / " + s.ping + " ms";
    }

    private int statusColor(ServerData s) {
        if (!s.pinged || s.ping < -1) return 0xFFE1C579;
        if (s.ping < 0 || s.protocol != SharedConstants.getCurrentVersion().getProtocolVersion()) return 0xFFFF8B91;
        return 0xFF9DCEAB;
    }

    private int drawWrapped(GuiGraphics g, String value, int x, int y, int w, int bottom, int color) {
        for (var line : font.split(Component.literal(value), Math.max(1, w))) {
            if (y + font.lineHeight > bottom) break;
            g.drawString(font, line, x, y, color, false);
            y += font.lineHeight + 3;
        }
        return y;
    }

    private String fit(String value, int w) {
        if (font.width(value) <= w) return value;
        return font.plainSubstrByWidth(value, Math.max(0, w - font.width("…"))) + "…";
    }

    private void panel(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, 0xE7101215);
        g.fill(x, y, x + w, y + 1, 0xFF55575B);
        g.fill(x, y + h - 1, x + w, y + h, 0xFF383A3E);
    }

    private String label(String es, String en) {
        return minecraft.getLanguageManager().getSelected().startsWith("es_") ? es : en;
    }

    private record Control(Button original, SiegeButton view) {}
}

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
    private static final String OFFICIAL_ADDRESS = "SiegeLacontinuacion.exaroton.me:18736";
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
        ensureOfficialServer();
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
            Component message = Component.literal(switch (slot) {
                case 0 -> label("CONECTAR", "CONNECT");
                case 1 -> label("CONEXIÓN DIRECTA", "DIRECT CONNECTION");
                case 2 -> label("AGREGAR", "ADD");
                case 3 -> label("EDITAR", "EDIT");
                case 4 -> label("ELIMINAR", "DELETE");
                case 5 -> label("ACTUALIZAR", "REFRESH");
                default -> label("VOLVER", "BACK");
            });
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
            }, slot == 4 ? 0xFFCA7777 : RED)
                    .setCompactCenter(true)
                    .setFullHoverFrame(true)
                    .setTextOffsetY(-1);
            button.active = original.active;
            button.setTooltip(Tooltip.create(Component.literal(switch (slot) {
                case 0 -> label("Conectar al servidor seleccionado.", "Connect to the selected server.");
                case 3 -> label("Editar el servidor seleccionado.", "Edit the selected server.");
                case 4 -> label("Eliminar el servidor seleccionado.", "Delete the selected server.");
                case 5 -> label("Volver a consultar los servidores.", "Query the servers again.");
                default -> message.getString();
            })));
            removeWidget(original);
            addRenderableWidget(button);
            controls.add(new Control(slot, original, button));
        }
        SiegeUiSounds.resetHover();
        lastSelection = serverSelectionList.getSelected();
        setInitialFocus(serverSelectionList);
    }

    @Override
    protected void onSelectedChange() {
        // The LAN scanner is a status row, never a destination. Vanilla can select
        // it when the list receives focus or is clicked, producing a large white frame.
        if (serverSelectionList.getSelected() instanceof ServerSelectionList.LANHeader) {
            serverSelectionList.setSelected(null);
            return;
        }
        super.onSelectedChange();
        syncControls();
    }

    private void syncControls() {
        if (controls == null) return;
        boolean locked = selectedServerIsOfficial();
        for (Control control : controls) {
            boolean protectedAction = locked && (control.slot == 3 || control.slot == 4);
            control.view.active = control.original.active && !protectedAction;
            if (control.slot == 3 || control.slot == 4) {
                String explanation = protectedAction
                        ? control.slot == 3
                            ? label("El servidor oficial está fijado y no se puede editar.",
                                    "The official server is pinned and cannot be edited.")
                            : label("El servidor oficial está fijado y no se puede eliminar.",
                                    "The official server is pinned and cannot be deleted.")
                        : control.slot == 3
                            ? label("Editar el servidor seleccionado.", "Edit the selected server.")
                            : label("Eliminar el servidor seleccionado.", "Delete the selected server.");
                control.view.setTooltip(Tooltip.create(Component.literal(explanation)));
            }
        }
    }

    private void ensureOfficialServer() {
        String name = label("Servidor oficial de SIEGE", "SIEGE Official Server");
        int found = -1;
        for (int i = 0; i < getServers().size(); i++) {
            if (OFFICIAL_ADDRESS.equalsIgnoreCase(getServers().get(i).ip.trim())) {
                found = i;
                break;
            }
        }
        if (found < 0) {
            getServers().add(new ServerData(name, OFFICIAL_ADDRESS, false), false);
            found = getServers().size() - 1;
        } else {
            getServers().get(found).name = name;
        }
        if (found > 0) getServers().swap(found, 0);
        else getServers().save();
        serverSelectionList.updateOnlineServers(getServers());
    }

    private boolean selectedServerIsOfficial() {
        var selected = serverSelectionList.getSelected();
        return selected instanceof ServerSelectionList.OnlineServerEntry online
                && OFFICIAL_ADDRESS.equalsIgnoreCase(online.getServerData().ip.trim());
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
        SiegeBackgrounds.renderCover(g, width, height, System.currentTimeMillis());
        g.fill(0, 0, width, height, 0x92000000);
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
        int saved = getServers().size();
        String count = saved + " " + label(saved == 1 ? "SERVIDOR" : "SERVIDORES",
                saved == 1 ? "SERVER" : "SERVERS");
        g.drawString(font, count, x + 12, 29, 0xFFADB1B5, false);
        panel(g, x, layout.top(), layout.listWidth(), layout.bottom() - layout.top());
        renderRowPlates(g);
        // JoinMultiplayerScreen draws its own title and dirt background. Render its
        // registered children once instead, preserving entry behavior and tooltips.
        for (var child : children()) if (child instanceof Renderable renderable)
            renderable.render(g, mouseX, mouseY, partialTick);
        // LANHeader centers itself against the whole Minecraft screen. Cover that
        // vanilla row and redraw it inside the SIEGE list column.
        renderLanScanner(g);
        renderEmptyState(g);
        renderSelectionSummary(g, mouseX, mouseY);
        renderLockedActionTooltip(mouseX, mouseY);
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
            if (entries.get(i) instanceof ServerSelectionList.LANHeader) continue;
            int y = layout.top() + 4 + i * 36 - (int)serverSelectionList.getScrollAmount();
            if (y + 36 < layout.top() || y >= layout.bottom()) continue;
            boolean selected = entries.get(i) == serverSelectionList.getSelected();
            int left = serverSelectionList.getRowLeft() - 2;
            g.fill(left, y, left + serverSelectionList.getRowWidth(), y + 34,
                    selected ? 0xC337292B : (i % 2 == 0 ? 0xA21E2023 : 0xA2181A1D));
            if (selected) {
                int rowRight = left + serverSelectionList.getRowWidth();
                g.fill(left - 3, y, rowRight, y + 1, RED);
                g.fill(left - 3, y + 33, rowRight, y + 34, RED);
                g.fill(left - 3, y, left - 2, y + 34, RED);
                g.fill(rowRight - 1, y, rowRight, y + 34, RED);
            }
        }
        g.disableScissor();
    }

    private void renderLanScanner(GuiGraphics g) {
        var entries = serverSelectionList.children();
        for (int i = 0; i < entries.size(); i++) {
            if (!(entries.get(i) instanceof ServerSelectionList.LANHeader)) continue;
            int y = layout.top() + 4 + i * 36 - (int)serverSelectionList.getScrollAmount();
            if (y + 34 < layout.top() || y >= layout.bottom()) return;
            int left = layout.x() + 4;
            int right = layout.x() + layout.listWidth() - 4;
            int bottom = Math.min(y + 34, layout.bottom());
            g.fill(left, y, right, bottom, 0xFF181A1D);
            g.fill(left, y, left + 2, bottom, 0xFF8A4148);
            g.fill(left + 8, bottom - 1, right - 8, bottom, 0xFF383A3E);

            String title = label("RED LOCAL", "LOCAL NETWORK");
            String dots = ".".repeat((int)(System.currentTimeMillis() / 450L % 4L));
            g.drawString(font, fit(title, Math.max(1, right - left - 42)), left + 10, y + 13,
                    0xFFBFC3C6, false);
            if (!dots.isEmpty())
                g.drawString(font, dots, right - 10 - font.width(dots), y + 13, 0xFFE1C579, false);
            return;
        }
    }

    private void renderEmptyState(GuiGraphics g) {
        if (getServers().size() != 0 || serverSelectionList.children().size() > 1) return;
        int center = layout.listCenterX();
        int start = layout.top() + 68;
        int available = layout.bottom() - start;
        if (available < 42) return;
        g.drawCenteredString(font, label("SIN DESTINOS GUARDADOS", "NO SAVED DESTINATIONS"),
                center, start, 0xFFB8BBBE);
        int textWidth = Math.min(layout.listWidth() - 40, 220);
        String help = label("Agregá un servidor para guardarlo o usá Conexión directa para desplegarte una sola vez.",
                "Add a server to save it or use Direct Connection for a one-time deployment.");
        int y = start + 18;
        for (var line : font.split(Component.literal(help), Math.max(1, textWidth))) {
            if (y + font.lineHeight > layout.bottom() - 10) break;
            g.drawCenteredString(font, line, center, y, 0xFF858A90);
            y += font.lineHeight + 3;
        }
    }

    private void renderSelectionSummary(GuiGraphics g, int mouseX, int mouseY) {
        var selected = serverSelectionList.getSelected();
        ServerData server = selected instanceof ServerSelectionList.OnlineServerEntry online
                ? online.getServerData() : null;
        String status = server == null
                ? selected instanceof ServerSelectionList.NetworkServerEntry ? label("RED LOCAL", "LOCAL NETWORK")
                    : label("Sin servidor seleccionado", "No server selected")
                : status(server);
        int color = server == null ? 0xFFADB1B5 : statusColor(server);

        // Compact layouts have no detail panel, so they receive one concise footer.
        if (layout.detailWidth() <= 0) {
            int footerY = layout.bottom() + 5;
            String summary = server == null ? status : server.name + " · " + status;
            g.fill(layout.x(), layout.bottom() + 2, layout.x() + layout.width(),
                    layout.firstRow() - 4, 0xF2101113);
            g.drawString(font, fit(summary, layout.width() - 16), layout.x() + 8, footerY, color, false);
            if (mouseX >= layout.x() && mouseX < layout.x() + layout.width()
                    && mouseY >= footerY && mouseY < footerY + 12
                    && font.width(summary) > layout.width() - 16)
                entryTooltip = List.of(Component.literal(summary));
            return;
        }

        int x = layout.detailX(), y = layout.top(), w = layout.detailWidth();
        panel(g, x, y, w, layout.bottom() - y);
        g.drawString(font, label("DETALLES", "DETAILS"), x + 10, y + 10, 0xFFAAAEB3, false);
        g.fill(x + 10, y + 24, x + w - 10, y + 25, 0xFF5C363B);
        if (server == null) {
            g.drawString(font, label("Sin servidor seleccionado.", "No server selected."),
                    x + 10, y + 36, 0xFFADB1B5, false);
            return;
        }

        int next = drawWrapped(g, server.name, x + 10, y + 36, w - 20, y + 66, 0xFFF0EDEA);
        boolean official = OFFICIAL_ADDRESS.equalsIgnoreCase(server.ip.trim());
        if (official) {
            g.drawString(font, label("OFICIAL · FIJO", "OFFICIAL · PINNED"),
                    x + 10, next + 3, 0xFFE1C579, false);
            next += 14;
        }
        g.drawString(font, fit(status, w - 20), x + 10, next + 5, color, false);
        next += 20;

        if (official) {
            g.drawString(font, fit(OFFICIAL_ADDRESS, w - 20), x + 10, next, 0xFF92979C, false);
            next += 17;
        }

        if (!isOfflineState(server)
                && server.protocol != SharedConstants.getCurrentVersion().getProtocolVersion()) {
            String version = server.version.getString().trim();
            if (!version.isEmpty())
                g.drawString(font, fit(version, w - 20), x + 10, next, 0xFFFFA0A5, false);
            next += 17;
        }

        String motd = server.motd.getString().trim();
        if (isUsefulMotd(server, motd) && next + font.lineHeight < layout.bottom() - 10)
            drawWrapped(g, motd, x + 10, next, w - 20, layout.bottom() - 10, 0xFFC4C6C9);
    }

    private void renderLockedActionTooltip(int mouseX, int mouseY) {
        if (!selectedServerIsOfficial()) return;
        for (Control control : controls) {
            if ((control.slot == 3 || control.slot == 4) && control.view.isMouseOver(mouseX, mouseY)) {
                String reason = control.slot == 3
                        ? label("El servidor oficial está fijado y no se puede editar.",
                                "The official server is pinned and cannot be edited.")
                        : label("El servidor oficial está fijado y no se puede eliminar.",
                                "The official server is pinned and cannot be deleted.");
                entryTooltip = List.of(Component.literal(reason));
                return;
            }
        }
    }

    private boolean isOfflineState(ServerData server) {
        String version = server.version.getString().trim();
        String motd = server.motd.getString().trim();
        return version.equalsIgnoreCase("Offline") || motd.equalsIgnoreCase("Offline");
    }

    private boolean isUsefulMotd(ServerData server, String motd) {
        return !motd.isEmpty()
                && !motd.equalsIgnoreCase("SIEGE")
                && !motd.equalsIgnoreCase("Offline")
                && !motd.equalsIgnoreCase(server.name);
    }

    private String status(ServerData s) {
        if (!s.pinged || s.ping < -1) return label("CONSULTANDO…", "QUERYING…");
        if (isOfflineState(s)) return label("SERVIDOR APAGADO", "SERVER OFFLINE");
        if (s.ping < 0) return label("SIN RESPUESTA", "NO RESPONSE");
        if (s.protocol != SharedConstants.getCurrentVersion().getProtocolVersion())
            return label("VERSIÓN INCOMPATIBLE", "INCOMPATIBLE VERSION");
        return label("EN LÍNEA", "ONLINE") + " · " + s.ping + " ms";
    }

    private int statusColor(ServerData s) {
        if (!s.pinged || s.ping < -1) return 0xFFE1C579;
        if (isOfflineState(s) || s.ping < 0
                || s.protocol != SharedConstants.getCurrentVersion().getProtocolVersion()) return 0xFFFF8B91;
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
        g.fill(x, y, x + w, y + h, 0xF2101215);
        g.fill(x, y, x + w, y + 1, 0xFF55575B);
        g.fill(x, y + h - 1, x + w, y + h, 0xFF383A3E);
    }

    private String label(String es, String en) {
        return minecraft.getLanguageManager().getSelected().startsWith("es_") ? es : en;
    }

    private record Control(int slot, Button original, SiegeButton view) {}
}

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
 * SIEGE 0.70 deployment console. Vanilla still owns server persistence, pings,
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
    private ServerSelectionList.Entry tooltipSelection;
    private String restoreAddress;
    private double restoreScroll;
    private long refreshedAt;
    private boolean initialSync = true;

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
                if (!original.active || (selectedServerIsOfficial() && (slot == 3 || slot == 4))) return;
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
                case 1 -> label("Desplegarse una vez sin guardar el destino.", "Deploy once without saving the destination.");
                case 2 -> label("Agregar un destino a la lista guardada.", "Add a destination to the saved list.");
                case 3 -> label("Editar el servidor seleccionado.", "Edit the selected server.");
                case 4 -> label("Eliminar el servidor seleccionado.", "Delete the selected server.");
                case 5 -> label("Volver a consultar los servidores manteniendo selección y desplazamiento.",
                        "Query servers again while preserving selection and scroll.");
                default -> message.getString();
            })));
            removeWidget(original);
            addRenderableWidget(button);
            controls.add(new Control(slot, original, button));
        }

        SiegeUiSounds.resetHover();
        for (var entry : serverSelectionList.children()) {
            if (entry instanceof ServerSelectionList.OnlineServerEntry online
                    && sameAddress(online.getServerData().ip, restoreAddress)) {
                serverSelectionList.setSelected(entry);
                break;
            }
        }
        if (restoreAddress != null) serverSelectionList.setScrollAmount(restoreScroll);
        lastSelection = serverSelectionList.getSelected();
        initialSync = true;
        syncControls();
        setInitialFocus(serverSelectionList);
    }

    @Override
    protected void onSelectedChange() {
        // The LAN scanner is a status row, never a deployment destination.
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
        var selection = serverSelectionList.getSelected();
        boolean updateTips = initialSync || tooltipSelection != selection;
        tooltipSelection = selection;
        initialSync = false;

        for (Control control : controls) {
            boolean protectedAction = locked && (control.slot == 3 || control.slot == 4);
            control.view.active = control.original.active && !protectedAction;
            if (control.slot == 0) {
                ServerData server = selectedServer();
                control.view.withBadge(server == null ? "" : shortState(server));
            }
            if (updateTips && (control.slot == 3 || control.slot == 4)) {
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

    private ServerData selectedServer() {
        var selected = serverSelectionList.getSelected();
        return selected instanceof ServerSelectionList.OnlineServerEntry online ? online.getServerData() : null;
    }

    private static boolean sameAddress(String a, String b) {
        return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
    }

    private void ensureOfficialServer() {
        String name = label("Servidor oficial de SIEGE", "SIEGE Official Server");
        ServerData official = null;
        boolean changed = false;
        for (int i = 0; i < getServers().size();) {
            ServerData data = getServers().get(i);
            if (sameAddress(data.ip, OFFICIAL_ADDRESS)) {
                if (official == null) official = data;
                else {
                    getServers().remove(data);
                    changed = true;
                    continue;
                }
            }
            i++;
        }
        if (official == null) {
            official = new ServerData(name, OFFICIAL_ADDRESS, false);
            getServers().add(official, false);
            changed = true;
        }
        if (!name.equals(official.name) || !OFFICIAL_ADDRESS.equals(official.ip)) {
            official.name = name;
            official.ip = OFFICIAL_ADDRESS;
            changed = true;
        }
        int found = 0;
        while (found < getServers().size() && getServers().get(found) != official) found++;
        // Adjacent moves preserve the user's relative order of all other servers.
        if (found > 0) changed = true;
        for (int i = found; i > 0; i--) getServers().swap(i, i - 1);
        if (changed) getServers().save();
        serverSelectionList.updateOnlineServers(getServers());
    }

    private boolean selectedServerIsOfficial() {
        ServerData selected = selectedServer();
        return selected != null && sameAddress(selected.ip, OFFICIAL_ADDRESS);
    }

    private void refresh() {
        long now = System.nanoTime() / 1_000_000L;
        if (now - refreshedAt < 750) return;
        SiegeMultiplayerScreen next = new SiegeMultiplayerScreen(parent);
        ServerData selected = selectedServer();
        if (selected != null) next.restoreAddress = selected.ip;
        next.restoreScroll = serverSelectionList.getScrollAmount();
        next.refreshedAt = now;
        minecraft.setScreen(next);
    }

    @Override
    public void tick() {
        super.tick();
        if (getServers().size() > 0 && !sameAddress(getServers().get(0).ip, OFFICIAL_ADDRESS)) ensureOfficialServer();
    }

    @Override
    public void onClose() {
        SiegeUiSounds.back();
        minecraft.setScreen(parent);
    }

    @Override
    public boolean keyPressed(int key, int scan, int modifiers) {
        // Preserve vanilla F5 without adding SIEGE-specific keyboard shortcuts.
        if (key == 294) {
            SiegeUiSounds.click();
            refresh();
            return true;
        }
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
        int x = layout.x();
        int right = x + layout.width();
        g.fill(x, 8, right, layout.top() - 5, 0xEB101113);
        g.fill(x, 8, x + 3, layout.top() - 5, RED);
        g.drawString(font, fit("SIEGE / " + label("DESPLIEGUE 0.70", "DEPLOYMENT 0.70"), layout.width() - 24),
                x + 12, 15, 0xFFF0EDEA, false);

        int saved = getServers().size();
        ServerData selectedServer = selectedServer();
        String count = saved + " " + label(saved == 1 ? "SERVIDOR" : "SERVIDORES",
                saved == 1 ? "SERVER" : "SERVERS");
        String headerStatus = selectedServer == null
                ? count
                : count + "  ·  " + SiegeDeploymentStatus.deploymentLabel(selectedServer, spanish());
        g.drawString(font, fit(headerStatus, layout.width() - 24), x + 12, 29,
                selectedServer == null ? 0xFFADB1B5 : SiegeDeploymentStatus.accent(selectedServer), false);

        panel(g, x, layout.top(), layout.listWidth(), layout.bottom() - layout.top());
        renderRowPlates(g);
        for (var child : children()) if (child instanceof Renderable renderable)
            renderable.render(g, mouseX, mouseY, partialTick);

        g.enableScissor(layout.x(), layout.top(), layout.x() + layout.listWidth(), layout.bottom());
        renderLanScanner(g);
        g.disableScissor();
        renderEmptyState(g);

        if (layout.detailWidth() > 0)
            g.enableScissor(layout.detailX(), layout.top(), layout.detailX() + layout.detailWidth(), layout.bottom());
        renderSelectionSummary(g, mouseX, mouseY);
        if (layout.detailWidth() > 0) g.disableScissor();

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
                    selected ? 0xD52F2226 : (i % 2 == 0 ? 0xA21F1D20 : 0xA218181B));
            if (selected) {
                int rowRight = left + serverSelectionList.getRowWidth();
                int accent = selectedServer() == null ? RED : SiegeDeploymentStatus.accent(selectedServer());
                g.fill(left - 3, y, rowRight, y + 1, accent);
                g.fill(left - 3, y + 33, rowRight, y + 34, accent);
                g.fill(left - 3, y, left - 2, y + 34, accent);
                g.fill(rowRight - 1, y, rowRight, y + 34, accent);
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
            String dots = SiegeConfig.reducedMotion ? "…" : ".".repeat((int)(System.nanoTime() / 450_000_000L % 4L));
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
        ServerData server = selectedServer();
        String status = server == null
                ? selected instanceof ServerSelectionList.NetworkServerEntry ? label("RED LOCAL", "LOCAL NETWORK")
                    : label("Sin servidor seleccionado", "No server selected")
                : SiegeDeploymentStatus.label(server, spanish());
        int color = server == null ? 0xFFADB1B5 : SiegeDeploymentStatus.accent(server);

        // Compact layouts have no detail panel, so they receive a concise operational footer.
        if (layout.detailWidth() <= 0) {
            int footerY = layout.bottom() + 5;
            String summary = server == null ? status
                    : server.name + " · " + status + (SiegeDeploymentStatus.state(server) == SiegeDeploymentStatus.State.ONLINE
                        ? " · " + SiegeDeploymentStatus.latencyLabel(server, spanish()) : "");
            g.fill(layout.x(), layout.bottom() + 2, layout.x() + layout.width(),
                    layout.firstRow() - 4, 0xF2101113);
            g.drawString(font, fit(summary, layout.width() - 16), layout.x() + 8, footerY, color, false);
            if (mouseX >= layout.x() && mouseX < layout.x() + layout.width()
                    && mouseY >= footerY && mouseY < footerY + 12
                    && font.width(summary) > layout.width() - 16)
                entryTooltip = List.of(Component.literal(summary));
            return;
        }

        int x = layout.detailX();
        int y = layout.top();
        int w = layout.detailWidth();
        panel(g, x, y, w, layout.bottom() - y);
        g.drawString(font, label("CONTROL DE DESPLIEGUE", "DEPLOYMENT CONTROL"), x + 10, y + 10, 0xFFAAAEB3, false);
        g.fill(x + 10, y + 24, x + w - 10, y + 25, 0xFF5C363B);
        if (server == null) {
            g.drawString(font, fit(label("Seleccioná un destino para ver su estado operativo.",
                    "Select a destination to view its operational state."), w - 20),
                    x + 10, y + 36, 0xFFADB1B5, false);
            g.drawString(font, fit(label("La conexión, ping y lista siguen siendo gestionados por Minecraft.",
                    "Connection, ping and server list remain managed by Minecraft."), w - 20),
                    x + 10, y + 52, 0xFF777F85, false);
            return;
        }

        int next = drawWrapped(g, server.name, x + 10, y + 36, w - 20, y + 66, 0xFFF0EDEA);
        boolean official = sameAddress(server.ip, OFFICIAL_ADDRESS);
        if (official) {
            SiegeTheme.icon(g, x + 10, next + 3, "pin", SiegeTheme.GOLD);
            g.drawString(font, label("DESTINO OFICIAL FIJADO", "PINNED OFFICIAL DESTINATION"),
                    x + 24, next + 3, SiegeTheme.GOLD, false);
            next += 15;
        }

        String deployment = SiegeDeploymentStatus.deploymentLabel(server, spanish());
        g.drawString(font, fit(deployment, w - 20), x + 10, next + 4, color, false);
        next += 14;
        g.drawString(font, fit(status, w - 20), x + 10, next + 2, color, false);
        next += 16;

        int meterW = Math.max(1, w - 20);
        g.fill(x + 10, next, x + 10 + meterW, next + 3, 0xFF292D31);
        int fill = SiegeDeploymentStatus.latencyFill(server, meterW);
        if (fill > 0) g.fill(x + 10, next, x + 10 + fill, next + 3, color);
        next += 7;
        g.drawString(font, fit(SiegeDeploymentStatus.latencyLabel(server, spanish()), w - 20),
                x + 10, next, 0xFF92979C, false);
        next += 15;

        if (official) {
            g.drawString(font, fit(OFFICIAL_ADDRESS, w - 20), x + 10, next, 0xFF92979C, false);
            if (mouseX >= x + 10 && mouseX < x + w - 10 && mouseY >= next && mouseY < next + font.lineHeight)
                entryTooltip = List.of(Component.literal(OFFICIAL_ADDRESS));
            next += 16;
        }

        if (server.pinged && server.ping >= 0 && !SiegeDeploymentStatus.offlineMarker(server)
                && server.protocol != SharedConstants.getCurrentVersion().getProtocolVersion()) {
            String version = server.version.getString().trim();
            if (!version.isEmpty()) {
                g.drawString(font, fit(label("SERVIDOR: ", "SERVER: ") + version, w - 20),
                        x + 10, next, 0xFFFFA0A5, false);
                next += 15;
            }
            String client = label("CLIENTE: ", "CLIENT: ") + SharedConstants.getCurrentVersion().getName();
            g.drawString(font, fit(client, w - 20), x + 10, next, 0xFFFFA0A5, false);
            next += 16;
        }

        int routeY = Math.max(next + 3, layout.bottom() - 42);
        if (routeY + 24 < layout.bottom()) {
            g.fill(x + 10, routeY, x + w - 10, routeY + 1, 0xFF353A3E);
            String route = label("DESTINO → ESTADO → CONECTAR", "DESTINATION → STATUS → CONNECT");
            g.drawString(font, fit(route, w - 20), x + 10, routeY + 6, color, false);
            String source = label("Red y conexión: Minecraft / Forge", "Network and connection: Minecraft / Forge");
            g.drawString(font, fit(source, w - 20), x + 10, routeY + 18, 0xFF777F85, false);
        }

        String motd = server.motd.getString().trim();
        int motdBottom = Math.min(layout.bottom() - 48, routeY - 4);
        if (SiegeDeploymentStatus.usefulMotd(server, motd) && next + font.lineHeight < motdBottom)
            drawWrapped(g, motd, x + 10, next, w - 20, motdBottom, 0xFFC4C6C9);
    }

    private void renderLockedActionTooltip(int mouseX, int mouseY) {
        if (!selectedServerIsOfficial()) return;
        for (Control control : controls) {
            if ((control.slot == 3 || control.slot == 4) && control.view.visible
                    && mouseX >= control.view.getX() && mouseX < control.view.getX() + control.view.getWidth()
                    && mouseY >= control.view.getY() && mouseY < control.view.getY() + control.view.getHeight()) {
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

    private String shortState(ServerData server) {
        return switch (SiegeDeploymentStatus.state(server)) {
            case ONLINE -> label("LISTO", "READY");
            case QUERYING -> label("PING", "PING");
            case OFFLINE -> label("OFF", "OFF");
            case NO_RESPONSE -> label("SIN RED", "NO NET");
            case INCOMPATIBLE -> label("VERSIÓN", "VERSION");
        };
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
        if (w <= 0) return "";
        if (font.width("…") > w) return font.plainSubstrByWidth(value, w);
        if (font.width(value) <= w) return value;
        return font.plainSubstrByWidth(value, Math.max(0, w - font.width("…"))) + "…";
    }

    private void panel(GuiGraphics g, int x, int y, int w, int h) {
        SiegeTheme.panel(g, x, y, w, h, 0xFF865055);
    }

    private boolean spanish() {
        return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_");
    }

    private String label(String es, String en) {
        return spanish() ? es : en;
    }

    private record Control(int slot, Button original, SiegeButton view) {}
}

package uy.santipdr.siege.client;

import net.minecraft.SharedConstants;
import net.minecraft.client.multiplayer.ServerData;

/** Shared 1.25 deployment-state presentation. Networking remains vanilla-owned. */
public final class SiegeDeploymentStatus {
    public enum State { QUERYING, OFFLINE, NO_RESPONSE, INCOMPATIBLE, ONLINE }

    private SiegeDeploymentStatus() { }

    public static State state(ServerData server) {
        if (server == null || !server.pinged || server.ping < -1) return State.QUERYING;
        if (offlineMarker(server)) return State.OFFLINE;
        if (server.ping < 0) return State.NO_RESPONSE;
        if (server.protocol != SharedConstants.getCurrentVersion().getProtocolVersion()) return State.INCOMPATIBLE;
        return State.ONLINE;
    }

    public static String label(ServerData server, boolean spanish) {
        return switch (state(server)) {
            case QUERYING -> spanish ? "CONSULTANDO…" : "QUERYING…";
            case OFFLINE -> spanish ? "SERVIDOR APAGADO" : "SERVER OFFLINE";
            case NO_RESPONSE -> spanish ? "SIN RESPUESTA" : "NO RESPONSE";
            case INCOMPATIBLE -> spanish ? "VERSIÓN INCOMPATIBLE" : "INCOMPATIBLE VERSION";
            case ONLINE -> (spanish ? "EN LÍNEA" : "ONLINE") + " · " + server.ping + " ms";
        };
    }

    public static String deploymentLabel(ServerData server, boolean spanish) {
        return switch (state(server)) {
            case ONLINE -> spanish ? "DESTINO DISPONIBLE" : "DESTINATION AVAILABLE";
            case QUERYING -> spanish ? "VERIFICANDO DESTINO" : "CHECKING DESTINATION";
            case INCOMPATIBLE -> spanish ? "CLIENTE INCOMPATIBLE" : "CLIENT INCOMPATIBLE";
            case OFFLINE, NO_RESPONSE -> spanish ? "DESTINO NO DISPONIBLE" : "DESTINATION UNAVAILABLE";
        };
    }

    public static String routeStep(ServerData server, boolean spanish) {
        return switch (state(server)) {
            case QUERYING -> spanish ? "DESTINO → VERIFICANDO" : "DESTINATION → CHECKING";
            case ONLINE -> spanish ? "DESTINO → ESTADO → CONECTAR" : "DESTINATION → STATUS → CONNECT";
            case INCOMPATIBLE -> spanish ? "DESTINO → VERSIÓN → BLOQUEADO" : "DESTINATION → VERSION → BLOCKED";
            case OFFLINE, NO_RESPONSE -> spanish ? "DESTINO → SIN RESPUESTA → ESPERAR" : "DESTINATION → NO RESPONSE → WAIT";
        };
    }

    public static String compatibilityLabel(ServerData server, boolean spanish) {
        if (server == null || !server.pinged) return spanish ? "COMPATIBILIDAD —" : "COMPATIBILITY —";
        if (state(server) == State.INCOMPATIBLE) {
            String version = server.version == null ? "" : server.version.getString().trim();
            return (spanish ? "INCOMPATIBLE" : "INCOMPATIBLE") + (version.isEmpty() ? "" : " · " + version);
        }
        if (state(server) == State.ONLINE) return spanish ? "CLIENTE COMPATIBLE" : "CLIENT COMPATIBLE";
        return spanish ? "COMPATIBILIDAD SIN CONFIRMAR" : "COMPATIBILITY UNCONFIRMED";
    }

    public static String latencyLabel(ServerData server, boolean spanish) {
        if (state(server) != State.ONLINE) return spanish ? "LATENCIA —" : "LATENCY —";
        long ping = Math.max(0, server.ping);
        return (spanish ? "LATENCIA " : "LATENCY ") + latencyBand(server, spanish) + " · " + ping + " ms";
    }

    public static String latencyBand(ServerData server, boolean spanish) {
        if (state(server) != State.ONLINE) return "—";
        long ping = Math.max(0, server.ping);
        return ping <= 80 ? (spanish ? "BAJA" : "LOW")
                : ping <= 160 ? (spanish ? "MEDIA" : "MEDIUM")
                : ping <= 300 ? (spanish ? "ALTA" : "HIGH")
                : (spanish ? "MUY ALTA" : "VERY HIGH");
    }

    public static int latencyFill(ServerData server, int width) {
        if (width <= 0 || state(server) != State.ONLINE) return 0;
        long ping = Math.max(0, server.ping);
        double ratio = 1.0 - Math.min(1.0, ping / 400.0);
        return Math.max(1, (int)Math.round(width * ratio));
    }

    public static int accent(ServerData server) {
        return switch (state(server)) {
            case ONLINE -> SiegeTheme.GREEN;
            case QUERYING -> SiegeTheme.GOLD;
            case INCOMPATIBLE -> SiegeTheme.ORANGE;
            case OFFLINE, NO_RESPONSE -> SiegeTheme.RED;
        };
    }

    public static boolean canDeploy(ServerData server) { return state(server) == State.ONLINE; }

    public static boolean offlineMarker(ServerData server) {
        if (server == null) return false;
        String version = server.version == null ? "" : server.version.getString().trim();
        String motd = server.motd == null ? "" : server.motd.getString().trim();
        return version.equalsIgnoreCase("Offline") || motd.equalsIgnoreCase("Offline");
    }

    public static boolean usefulMotd(ServerData server, String motd) {
        if (server == null || motd == null) return false;
        return !motd.isEmpty()
                && !motd.equalsIgnoreCase("SIEGE")
                && !motd.equalsIgnoreCase("Offline")
                && !motd.equalsIgnoreCase(server.name);
    }
}

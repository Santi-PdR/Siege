package uy.santipdr.siege.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Cross-domain index for SIEGE 3.00. It does not own gameplay data: it points to
 * Intel, Archive, Armory, Field Manual, Knowledge, Deployment and technical
 * screens. Search is accent-insensitive and intentionally local/offline.
 */
public final class SiegeOperationsIndex {
    public enum Kind { ROUTE, INTEL, ARMORY, KNOWLEDGE }
    public enum Route {
        DEPLOYMENT, INTEL, KNOWLEDGE, ARCHIVE, ARMORY, FIELD_MANUAL,
        COMMAND, DIAGNOSTICS, SETTINGS, BACKGROUNDS
    }

    public record Entry(Kind kind, String id, String title, String subtitle,
                        String keywords, Route route, IntelEntry intel, String knowledgeId) {
        public Entry {
            id = id == null ? "" : id;
            title = title == null ? "" : title;
            subtitle = subtitle == null ? "" : subtitle;
            keywords = keywords == null ? "" : keywords;
            knowledgeId = knowledgeId == null ? "" : knowledgeId;
        }
    }

    private SiegeOperationsIndex() { }

    public static List<Entry> all(boolean spanish) {
        List<Entry> out = new ArrayList<>();
        addRoutes(out, spanish);

        for (IntelEntry intel : IntelCatalog.files()) {
            IntelEntry.IntelText text = intel.text(spanish);
            String keywords = String.join(" ", intel.category(), intel.hp(), text.origin(), text.armament(),
                    text.variants(), text.status(), text.description(), text.advisory());
            out.add(new Entry(Kind.INTEL, intel.code(), intel.name(),
                    intel.code() + " · " + intel.category() + " · HP " + intel.hp(),
                    keywords, Route.INTEL, intel, ""));
        }

        for (SiegeGuideData.Entry item : SiegeGuideSupplemental.entries(
                SiegeGuideData.Category.ITEMS, "", spanish)) {
            out.add(new Entry(Kind.ARMORY, item.id(), item.title(spanish),
                    spanish ? "ARSENAL · EQUIPAMIENTO" : "ARMORY · EQUIPMENT",
                    item.body(spanish), Route.ARMORY, null, ""));
        }

        for (SiegeKnowledgeData.Entry knowledge : SiegeKnowledgeData.entries()) {
            String zone = knowledge.zone() == SiegeKnowledgeData.Zone.CURRENT
                    ? (spanish ? "SIEGE ACTUAL" : "CURRENT SIEGE")
                    : (spanish ? "ENCICLOPEDIA" : "ENCYCLOPEDIA");
            String subtitle = zone + " · " + knowledge.domain().label(spanish)
                    + " · " + knowledge.confidence().label(spanish);
            out.add(new Entry(Kind.KNOWLEDGE, knowledge.id(), knowledge.title(spanish), subtitle,
                    SiegeKnowledgeData.searchable(knowledge, spanish), Route.KNOWLEDGE, null, knowledge.id()));
        }
        return List.copyOf(out);
    }

    public static List<Entry> search(String query, boolean spanish, int limit) {
        int safeLimit = Math.max(1, Math.min(12, limit));
        String q = normalize(query);
        if (q.isBlank()) return List.of();

        record Ranked(Entry entry, int score) { }
        List<Ranked> ranked = new ArrayList<>();
        for (Entry entry : all(spanish)) {
            int score = score(entry, q);
            if (score > 0) ranked.add(new Ranked(entry, score));
        }
        ranked.sort(Comparator.comparingInt(Ranked::score).reversed()
                .thenComparing(r -> normalize(r.entry().title()))
                .thenComparing(r -> r.entry().id()));
        return ranked.stream().limit(safeLimit).map(Ranked::entry).toList();
    }

    private static int score(Entry entry, String query) {
        String id = normalize(entry.id());
        String title = normalize(entry.title());
        String subtitle = normalize(entry.subtitle());
        String keywords = normalize(entry.keywords());

        if (id.equals(query) || title.equals(query)) return 140;
        if (id.startsWith(query)) return 125;
        if (title.startsWith(query)) return 115;
        if (title.contains(query)) return 95;
        if (id.contains(query)) return 85;
        if (subtitle.contains(query)) return 70;
        if (keywords.contains(query)) return 45;

        String[] tokens = query.split("\\s+");
        int matched = 0;
        String haystack = id + " " + title + " " + subtitle + " " + keywords;
        for (String token : tokens) if (!token.isBlank() && haystack.contains(token)) matched++;
        return matched == tokens.length && matched > 0 ? 28 + matched * 4 : 0;
    }

    private static void addRoutes(List<Entry> out, boolean es) {
        route(out, Route.DEPLOYMENT, es ? "DESPLIEGUE" : "DEPLOYMENT",
                es ? "Servidor oficial, estado y conexión" : "Official server, status and connection",
                "server servidor online offline ping latency latencia connect conectar destino compatibility compatibilidad");
        route(out, Route.INTEL, "INTEL",
                es ? "Dossiers de unidades y amenazas" : "Unit and threat dossiers",
                "dossier unit unidad advanced avanzado tank boss elite super unknown threat hp armament");
        route(out, Route.KNOWLEDGE, es ? "CONOCIMIENTO" : "KNOWLEDGE",
                es ? "SIEGE actual, enciclopedia, supervivencia y fuentes" : "Current SIEGE, encyclopedia, survival and sources",
                "knowledge conocimiento wiki encyclopedia enciclopedia alex advice consejos current actual deteriorer re meditation meditacion relics reliquias sources fuentes survival supervivencia discord history historico");
        route(out, Route.ARCHIVE, es ? "ARCHIVO" : "ARCHIVE",
                es ? "Qué es SIEGE, 2044, facciones, Núcleo e inspiraciones" : "What SIEGE is, 2044, factions, Core and inspirations",
                "siege eternal craft 2044 lore nucleo core factions facciones gates rifts inspirations inspiraciones chronicle cronica");
        route(out, Route.ARMORY, es ? "ARSENAL" : "ARMORY",
                es ? "Objetos, equipamiento y evidencia" : "Items, equipment and evidence",
                "items objetos gear equipment equipamiento third justice aerorig riflator holo watch weapon arma");
        route(out, Route.FIELD_MANUAL, es ? "MANUAL DE CAMPO" : "FIELD MANUAL",
                es ? "Estados de muerte, trauma, misiones y protocolos" : "Death states, trauma, missions and protocols",
                "downed mangled mutilated dismembered disfigured bleeding burned erased shellshock death muerte trauma states estados protocol protocolo mission mision");
        route(out, Route.COMMAND, es ? "CENTRO DE COMANDO" : "COMMAND CENTER",
                es ? "Perfil, prioridades y estado del cliente" : "Profile, priorities and client state",
                "client cliente profile perfil health salud command command center priority prioridad");
        route(out, Route.DIAGNOSTICS, es ? "DIAGNÓSTICO" : "DIAGNOSTICS",
                es ? "Problemas detectados y recuperación segura" : "Detected problems and safe recovery",
                "diagnostic diagnostico recovery recuperacion repair reparar error warning aviso technical tecnico");
        route(out, Route.SETTINGS, es ? "AJUSTES" : "SETTINGS",
                es ? "Apariencia, movimiento, audio, Intel y accesibilidad" : "Appearance, motion, audio, Intel and accessibility",
                "settings ajustes appearance apariencia motion movimiento audio intel accessibility accesibilidad configuration configuracion");
        route(out, Route.BACKGROUNDS, es ? "FONDOS" : "BACKGROUNDS",
                es ? "Galería, rotación y contraste de escenas" : "Scene gallery, rotation and contrast",
                "background fondos scene escena gallery galeria rotation rotacion contrast contraste");
    }

    private static void route(List<Entry> out, Route route, String title, String subtitle, String keywords) {
        out.add(new Entry(Kind.ROUTE, "route:" + route.name().toLowerCase(Locale.ROOT), title, subtitle,
                keywords, route, null, ""));
    }

    static String normalize(String value) {
        if (value == null) return "";
        String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return decomposed.toLowerCase(Locale.ROOT).replace('·', ' ').replaceAll("[^a-z0-9?_-]+", " ").trim();
    }
}

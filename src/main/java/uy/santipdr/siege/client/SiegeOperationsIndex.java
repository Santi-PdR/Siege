package uy.santipdr.siege.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Search index for routes and information that actually belong in SIEGE Operations. */
public final class SiegeOperationsIndex {
    public enum Kind { ROUTE, INTEL, KNOWLEDGE }
    public enum Route {
        BRIEFING, ATLAS, RACES, PROGRESSION, THREATS, MEDIA,
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
        addVisibleRoutes(out, spanish);

        for (IntelEntry intel : IntelCatalog.files()) {
            IntelEntry.IntelText text = intel.text(spanish);
            String keywords = String.join(" ", intel.category(), intel.hp(), text.origin(), text.armament(),
                    text.variants(), text.status(), text.description(), text.advisory());
            out.add(new Entry(Kind.INTEL, intel.code(), intel.name(),
                    spanish ? "Unidad / amenaza" : "Unit / threat",
                    keywords, Route.INTEL, intel, ""));
        }

        for (SiegeKnowledgeData.Entry knowledge : SiegeKnowledgeRegistry.entries()) {
            out.add(new Entry(Kind.KNOWLEDGE, knowledge.id(), knowledge.title(spanish),
                    knowledge.domain().label(spanish),
                    SiegeKnowledgeRegistry.searchable(knowledge, spanish), Route.ATLAS, null, knowledge.id()));
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
                .thenComparing(r -> normalize(r.entry().title())).thenComparing(r -> r.entry().id()));
        return ranked.stream().limit(safeLimit).map(Ranked::entry).toList();
    }

    private static int score(Entry entry, String query) {
        String id = normalize(entry.id()), title = normalize(entry.title()), subtitle = normalize(entry.subtitle()), keywords = normalize(entry.keywords());
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

    private static void addVisibleRoutes(List<Entry> out, boolean es) {
        routeIfVisible(out, Route.BRIEFING, "BRIEFING",
                es ? "Primeros pasos para empezar" : "First steps for getting started",
                "start empezar newcomer nuevo beginner principiante basics básico survival supervivencia");
        routeIfVisible(out, Route.ATLAS, "ATLAS",
                es ? "Razas, progresión, sistemas y objetos" : "Races, progression, systems and items",
                "atlas knowledge conocimiento wiki encyclopedia enciclopedia systems sistemas trials executors reliquias items objetos");
        routeIfVisible(out, Route.RACES, es ? "ATLAS DE RAZAS" : "RACE ATLAS",
                es ? "Rarezas, razas y variantes" : "Rarities, races and variants",
                "race races raza razas rarity rareza obsainan fabled eternal saiyan ghoul subhuman cyborg human progression progresion");
        routeIfVisible(out, Route.PROGRESSION, es ? "PROGRESIÓN" : "PROGRESSION",
                es ? "V1→V4, rutas especiales y Trials" : "V1→V4, special routes and Trials",
                "progression progresion v1 v2 v3 v4 trials transformations transformaciones ghoul saiyan");
        routeIfVisible(out, Route.THREATS, es ? "AMENAZAS" : "THREATS",
                es ? "Unidades, Executores y bosses" : "Units, Executors and bosses",
                "threat threats amenaza amenazas dossier unit unidad boss bosses executor ejecutor raid event evento intel");
        routeIfVisible(out, Route.MEDIA, es ? "MULTIMEDIA" : "MEDIA",
                es ? "Música, ambientes y fondos" : "Music, moods and backgrounds",
                "media multimedia music musica soundtrack background fondo dvn dummies noobs gallery galeria");
        routeIfVisible(out, Route.DEPLOYMENT, es ? "DESPLIEGUE" : "DEPLOYMENT",
                es ? "Entrar al servidor oficial" : "Join the official server",
                "server servidor online offline ping latency latencia connect conectar destino compatibility compatibilidad");
        routeIfVisible(out, Route.INTEL, "INTEL", es ? "Dossiers de unidades y amenazas" : "Unit and threat dossiers",
                "dossier unit unidad advanced avanzado tank boss elite super unknown threat hp armament");
        routeIfVisible(out, Route.KNOWLEDGE, es ? "ENCICLOPEDIA" : "ENCYCLOPEDIA",
                es ? "Información actual del servidor" : "Current server information",
                "knowledge conocimiento server servidor raza trial executor item reliquia");
        routeIfVisible(out, Route.FIELD_MANUAL, es ? "MANUAL DE CAMPO" : "FIELD MANUAL",
                es ? "Heridas, muerte, misiones y protocolos" : "Injuries, death, missions and protocols",
                "downed mangled mutilated dismembered disfigured bleeding burned death muerte trauma states estados protocol mision");
    }

    private static void routeIfVisible(List<Entry> out, Route route, String title, String subtitle, String keywords) {
        if (SiegeCommandNetwork.isVisible(route)) route(out, route, title, subtitle, keywords);
    }

    private static void route(List<Entry> out, Route route, String title, String subtitle, String keywords) {
        out.add(new Entry(Kind.ROUTE, "route:" + route.name().toLowerCase(Locale.ROOT), title, subtitle, keywords, route, null, ""));
    }

    static String normalize(String value) {
        if (value == null) return "";
        String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return decomposed.toLowerCase(Locale.ROOT).replace('·', ' ').replaceAll("[^a-z0-9?_-]+", " ").trim();
    }
}

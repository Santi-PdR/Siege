package uy.santipdr.siege.client;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Authoritative metadata for every normal SIEGE menu scene.
 * Rendering, gallery labels, contrast bias, scheduling and CI all read the same table.
 *
 * SIEGE 5.61 keeps source provenance explicit: official DVN captures, SIEGE treatments
 * generated from verified DVN sources, and older SIEGE archive scenes are never shown
 * under the same source label. No scene is fake-HD.
 * Tempest Jutcherson is deliberately NOT part of this catalog: it remains an
 * easter-egg asset and cannot leak into normal rotation or the background gallery.
 */
public final class SiegeSceneCatalog {
    public enum Kind { STANDARD, FEATURED, ANOMALY }
    public enum Source { SIEGE_ARCHIVE, DVN_OFFICIAL, SIEGE_TREATMENT }

    public record Scene(String id, String es, String en, int width, int height,
                        int darknessBias, Kind kind, Source source, boolean comfortEligible) {
        public Scene {
            if (id == null || id.isBlank()) throw new IllegalArgumentException("scene id");
            if (width <= 0 || height <= 0) throw new IllegalArgumentException("scene dimensions");
            if (source == null) throw new IllegalArgumentException("scene source");
            darknessBias = Math.max(0, Math.min(30, darknessBias));
        }
        public String label(boolean spanish) { return spanish ? es : en; }
    }

    private static final int LEGACY_W = 960;
    private static final int LEGACY_H = 540;
    private static final int COMPACT_W = 720;
    private static final int COMPACT_H = 405;
    private static final int ROOFTOP_W = 896;
    private static final int ROOFTOP_H = 504;
    private static final int DVN_W = 768;
    private static final int DVN_H = 432;

    private static final List<Scene> SCENES = List.of(
            legacy("dummies_assault", "Asalto de Dummies", "Dummies Assault", 2),
            dvn("dvn_official_01", "DVN · Escena oficial 01", "DVN · Official Scene 01", 1),
            dvn("dvn_official_02", "DVN · Escena oficial 02", "DVN · Official Scene 02", 2),
            dvn("dvn_official_03", "DVN · Escena oficial 03", "DVN · Official Scene 03", 1),
            dvn("dvn_official_04", "DVN · Escena oficial 04", "DVN · Official Scene 04", 2),
            dvn("dvn_official_05", "DVN · Escena oficial 05", "DVN · Official Scene 05", 1),
            dvn("dvn_official_06", "DVN · Escena oficial 06", "DVN · Official Scene 06", 2),
            generated("nucleus_interference", "SIEGE · Interferencia del Núcleo", "SIEGE · Nucleus Interference", 1),
            generated("tesla_breach", "SIEGE · Ruptura Tesla", "SIEGE · Tesla Breach", 0),
            generated("stronghold_red_alert", "SIEGE · Stronghold en alerta roja", "SIEGE · Stronghold Red Alert", 1),
            legacy("anniversary", "Aniversario", "Anniversary", 0),
            legacy("frontline_19", "Frente 19", "Frontline 19", 3),
            legacy("cyborg", "Cíborg", "Cyborg", 4),
            legacy("last_stand", "Última resistencia", "Last Stand", 1),
            legacy("vought_siege", "Asedio Vought", "Vought Siege", 5),
            legacy("earth_orbit", "Órbita terrestre", "Earth Orbit", 6),
            legacy("canyon_engagement", "Combate en el cañón", "Canyon Engagement", 4),
            legacy("night_battle", "Batalla nocturna", "Night Battle", 0),
            compact("night_operation", "Operación nocturna", "Night Operation", 0),
            compact("urban_rendezvous", "Encuentro urbano", "Urban Rendezvous", 2),
            new Scene("rooftop_squad", "Escuadrón en azotea · Especial", "Rooftop Squad · Special",
                    ROOFTOP_W, ROOFTOP_H, 3, Kind.FEATURED, Source.SIEGE_ARCHIVE, true)
    );

    private static final List<Integer> STANDARD_INDICES = IntStream.range(0, SCENES.size())
            .filter(i -> SCENES.get(i).kind() == Kind.STANDARD)
            .boxed().toList();

    private SiegeSceneCatalog() { }

    private static Scene legacy(String id, String es, String en, int darknessBias) {
        return new Scene(id, es, en, LEGACY_W, LEGACY_H, darknessBias,
                Kind.STANDARD, Source.SIEGE_ARCHIVE, true);
    }

    private static Scene compact(String id, String es, String en, int darknessBias) {
        return new Scene(id, es, en, COMPACT_W, COMPACT_H, darknessBias,
                Kind.STANDARD, Source.SIEGE_ARCHIVE, true);
    }

    /** Official DVN thumbnails stay at their native 768x432 instead of being fake-upscaled. */
    private static Scene dvn(String id, String es, String en, int darknessBias) {
        return new Scene(id, es, en, DVN_W, DVN_H, darknessBias,
                Kind.STANDARD, Source.DVN_OFFICIAL, true);
    }

    /** SIEGE treatments preserve the same native canvas and remain normal rotation scenes. */
    private static Scene generated(String id, String es, String en, int darknessBias) {
        return new Scene(id, es, en, DVN_W, DVN_H, darknessBias,
                Kind.STANDARD, Source.SIEGE_TREATMENT, true);
    }

    public static int count() { return SCENES.size(); }
    public static Scene get(int index) { return SCENES.get(Math.floorMod(index, SCENES.size())); }
    public static String id(int index) { return get(index).id(); }
    public static String label(int index, boolean spanish) { return get(index).label(spanish); }
    public static int width(int index) { return get(index).width(); }
    public static int height(int index) { return get(index).height(); }
    public static int darknessBias(int index) { return get(index).darknessBias(); }
    public static Kind kind(int index) { return get(index).kind(); }
    public static Source source(int index) { return get(index).source(); }
    public static boolean comfortEligible(int index) { return get(index).comfortEligible(); }
    public static String sourceLabel(int index, boolean spanish) {
        return switch (source(index)) {
            case DVN_OFFICIAL -> spanish ? "DVN OFICIAL" : "OFFICIAL DVN";
            case SIEGE_TREATMENT -> spanish ? "TRATAMIENTO SIEGE" : "SIEGE TREATMENT";
            case SIEGE_ARCHIVE -> spanish ? "ARCHIVO SIEGE" : "SIEGE ARCHIVE";
        };
    }
    public static int anomalyIndex() { return indexOf(Kind.ANOMALY); }
    public static int featuredIndex() { return indexOf(Kind.FEATURED); }
    public static int standardCount() { return STANDARD_INDICES.size(); }
    public static int standardIndex(int ordinal) {
        if (STANDARD_INDICES.isEmpty()) return 0;
        return STANDARD_INDICES.get(Math.floorMod(ordinal, STANDARD_INDICES.size()));
    }
    public static boolean containsId(String id) {
        if (id == null) return false;
        return SCENES.stream().anyMatch(scene -> scene.id().equals(id));
    }
    public static int indexOfId(String id) {
        if (id == null) return -1;
        for (int i = 0; i < SCENES.size(); i++) if (SCENES.get(i).id().equals(id)) return i;
        return -1;
    }

    private static int indexOf(Kind kind) {
        for (int i = 0; i < SCENES.size(); i++) if (SCENES.get(i).kind() == kind) return i;
        return -1;
    }
}

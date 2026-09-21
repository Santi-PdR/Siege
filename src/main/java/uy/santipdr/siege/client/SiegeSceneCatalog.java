package uy.santipdr.siege.client;

import java.util.List;

/**
 * Authoritative metadata for every SIEGE menu scene.
 * Rendering, gallery labels, contrast bias, scheduling and CI all read the same table.
 */
public final class SiegeSceneCatalog {
    public enum Kind { STANDARD, FEATURED, ANOMALY }

    public record Scene(String id, String es, String en, int width, int height,
                        int darknessBias, Kind kind, boolean comfortEligible) {
        public Scene {
            if (id == null || id.isBlank()) throw new IllegalArgumentException("scene id");
            if (width <= 0 || height <= 0) throw new IllegalArgumentException("scene dimensions");
            darknessBias = Math.max(0, Math.min(30, darknessBias));
        }
        public String label(boolean spanish) { return spanish ? es : en; }
    }

    private static final List<Scene> SCENES = List.of(
            scene("dummies_assault", "Asalto de Dummies", "Dummies Assault", 960, 540, 2),
            scene("anniversary", "Aniversario", "Anniversary", 960, 540, 0),
            scene("frontline_19", "Frente 19", "Frontline 19", 960, 540, 3),
            scene("cyborg", "Cíborg", "Cyborg", 960, 540, 4),
            scene("last_stand", "Última resistencia", "Last Stand", 960, 540, 1),
            scene("vought_siege", "Asedio Vought", "Vought Siege", 960, 540, 5),
            scene("earth_orbit", "Órbita terrestre", "Earth Orbit", 960, 540, 6),
            scene("canyon_engagement", "Combate en el cañón", "Canyon Engagement", 960, 540, 4),
            scene("night_battle", "Batalla nocturna", "Night Battle", 960, 540, 0),
            scene("night_operation", "Operación nocturna", "Night Operation", 735, 490, 0),
            scene("urban_rendezvous", "Encuentro urbano", "Urban Rendezvous", 735, 414, 2),
            new Scene("rooftop_squad", "Escuadrón en azotea · Especial", "Rooftop Squad · Special",
                    680, 510, 3, Kind.FEATURED, true),
            new Scene("tempest_jutcherson", "TEMPEST JUTCHERSON", "TEMPEST JUTCHERSON",
                    720, 405, 6, Kind.ANOMALY, false)
    );

    private SiegeSceneCatalog() { }

    private static Scene scene(String id, String es, String en, int width, int height, int darknessBias) {
        return new Scene(id, es, en, width, height, darknessBias, Kind.STANDARD, true);
    }

    public static int count() { return SCENES.size(); }
    public static Scene get(int index) { return SCENES.get(Math.floorMod(index, SCENES.size())); }
    public static String id(int index) { return get(index).id(); }
    public static String label(int index, boolean spanish) { return get(index).label(spanish); }
    public static int width(int index) { return get(index).width(); }
    public static int height(int index) { return get(index).height(); }
    public static int darknessBias(int index) { return get(index).darknessBias(); }
    public static Kind kind(int index) { return get(index).kind(); }
    public static boolean comfortEligible(int index) { return get(index).comfortEligible(); }
    public static int anomalyIndex() { return indexOf(Kind.ANOMALY); }
    public static int featuredIndex() { return indexOf(Kind.FEATURED); }

    private static int indexOf(Kind kind) {
        for (int i = 0; i < SCENES.size(); i++) if (SCENES.get(i).kind() == kind) return i;
        return -1;
    }
}

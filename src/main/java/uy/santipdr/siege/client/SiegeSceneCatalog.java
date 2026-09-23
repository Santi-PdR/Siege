package uy.santipdr.siege.client;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Authoritative metadata for every normal SIEGE menu scene.
 * Rendering, gallery labels, contrast bias, scheduling and CI all read the same table.
 *
 * Tempest Jutcherson is deliberately NOT part of this catalog. It remains reserved
 * as an easter-egg asset and therefore cannot leak into normal rotation, the
 * background gallery or the home scene label.
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

    private static final int HD_W = 1920;
    private static final int HD_H = 1080;
    private static final int DVN_W = 768;
    private static final int DVN_H = 432;

    private static final List<Scene> SCENES = List.of(
            scene("dummies_assault", "Asalto de Dummies", "Dummies Assault", 2),
            dvn("dvn_official_01", "DVN · Escena oficial 01", "DVN · Official Scene 01", 1),
            dvn("dvn_official_02", "DVN · Escena oficial 02", "DVN · Official Scene 02", 2),
            scene("anniversary", "Aniversario", "Anniversary", 0),
            scene("frontline_19", "Frente 19", "Frontline 19", 3),
            scene("cyborg", "Cíborg", "Cyborg", 4),
            scene("last_stand", "Última resistencia", "Last Stand", 1),
            scene("vought_siege", "Asedio Vought", "Vought Siege", 5),
            scene("earth_orbit", "Órbita terrestre", "Earth Orbit", 6),
            scene("canyon_engagement", "Combate en el cañón", "Canyon Engagement", 4),
            scene("night_battle", "Batalla nocturna", "Night Battle", 0),
            scene("night_operation", "Operación nocturna", "Night Operation", 0),
            scene("urban_rendezvous", "Encuentro urbano", "Urban Rendezvous", 2),
            new Scene("rooftop_squad", "Escuadrón en azotea · Especial", "Rooftop Squad · Special",
                    HD_W, HD_H, 3, Kind.FEATURED, true)
    );

    private static final List<Integer> STANDARD_INDICES = IntStream.range(0, SCENES.size())
            .filter(i -> SCENES.get(i).kind() == Kind.STANDARD)
            .boxed().toList();

    private SiegeSceneCatalog() { }

    private static Scene scene(String id, String es, String en, int darknessBias) {
        return new Scene(id, es, en, HD_W, HD_H, darknessBias, Kind.STANDARD, true);
    }

    /** Official DVN thumbnails stay at their native 768x432 instead of being fake-upscaled. */
    private static Scene dvn(String id, String es, String en, int darknessBias) {
        return new Scene(id, es, en, DVN_W, DVN_H, darknessBias, Kind.STANDARD, true);
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
    public static int standardCount() { return STANDARD_INDICES.size(); }
    public static int standardIndex(int ordinal) {
        if (STANDARD_INDICES.isEmpty()) return 0;
        return STANDARD_INDICES.get(Math.floorMod(ordinal, STANDARD_INDICES.size()));
    }
    public static boolean containsId(String id) {
        if (id == null) return false;
        return SCENES.stream().anyMatch(scene -> scene.id().equals(id));
    }

    private static int indexOf(Kind kind) {
        for (int i = 0; i < SCENES.size(); i++) if (SCENES.get(i).kind() == kind) return i;
        return -1;
    }
}

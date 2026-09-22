import uy.santipdr.siege.client.SiegeProgressionData;

import java.util.HashSet;
import java.util.Set;

public final class ProgressionMapRegressionTest {
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }

    public static void main(String[] args) {
        check(SiegeProgressionData.tracks().size() >= 4, "Progression map lost major tracks");
        Set<String> tracks = new HashSet<>();
        Set<String> nodes = new HashSet<>();
        for (var track : SiegeProgressionData.tracks()) {
            check(tracks.add(track.id()), "Duplicate progression track: " + track.id());
            check(!track.nodes().isEmpty(), "Empty progression track: " + track.id());
            for (var node : track.nodes()) {
                check(nodes.add(track.id() + ":" + node.id()), "Duplicate progression node: " + node.id());
                check(!node.knowledgeId().isBlank(), "Progression node lacks source deep-link: " + node.id());
                check(!node.summaryEs().isBlank() && !node.summaryEn().isBlank(), "Progression node lacks bilingual summary");
            }
        }
        var v = SiegeProgressionData.get("v1v4");
        check(v.nodes().size() == 4, "V1→V4 track must retain four conceptual stages");
        check(v.nodes().get(0).title(false).equals("V1") && v.nodes().get(3).title(false).equals("V4"), "V1→V4 ordering regression");
        check(SiegeProgressionData.get("missing").id().equals("core"), "Unknown track fallback");
        System.out.println("SIEGE 4.00 progression map: tracks, node order and source boundaries passed");
    }
}

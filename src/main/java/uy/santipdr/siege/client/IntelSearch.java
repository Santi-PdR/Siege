package uy.santipdr.siege.client;

import java.text.Normalizer;
import java.util.Locale;

/** Literal, accent-insensitive tokens. User input is never interpreted as a regex. */
public final class IntelSearch {
    private IntelSearch() { }
    private static String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT).trim();
    }
    public static boolean matches(String query, String content) {
        String needle = normalize(query);
        if (needle.isEmpty()) return true;
        String haystack = normalize(content);
        for (String token : needle.split("\\s+")) if (!haystack.contains(token)) return false;
        return true;
    }
}

package uy.santipdr.siege.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Literal, accent-insensitive tokens. User input is never interpreted as a regex. */
public final class IntelSearch {
    private IntelSearch() { }
    static String normalize(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("(?<=\\d)[,._'’](?=\\d)", "")
                .replace('-', ' ')
                .replaceAll("\\s+", " ").trim();
    }
    public static boolean matches(String query, String content) {
        List<Term> terms = terms(query);
        if (terms.isEmpty()) return true;
        String haystack = normalize(content);
        for (Term term : terms) {
            boolean present = haystack.contains(term.value());
            if (term.excluded() ? present : !present) return false;
        }
        return true;
    }

    private static List<Term> terms(String query) {
        String value = normalize(query);
        List<Term> result = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i <= value.length(); i++) {
            char c = i == value.length() ? ' ' : value.charAt(i);
            if (c == '"') { quoted = !quoted; continue; }
            if (Character.isWhitespace(c) && !quoted) {
                if (!token.isEmpty()) {
                    String term = token.toString();
                    boolean excluded = term.length() > 1 && term.charAt(0) == '!';
                    result.add(new Term(excluded ? term.substring(1) : term, excluded));
                    token.setLength(0);
                }
            } else token.append(c);
        }
        return result;
    }

    private record Term(String value, boolean excluded) { }
}

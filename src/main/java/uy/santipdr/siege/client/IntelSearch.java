package uy.santipdr.siege.client;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/** Literal search; parsing is performed once per query, never once per dossier. */
public final class IntelSearch {
    private static final Pattern MARKS = Pattern.compile("\\p{M}+");
    private static final Pattern SPACES = Pattern.compile("[\\s\\p{Z}]+");
    private static final Pattern NUMBERS = Pattern.compile("(?<=\\d)[,._'’](?=\\d)");
    private IntelSearch() { }
    static String normalize(String value) {
        if (value == null) return "";
        String text = Normalizer.normalize(value, Normalizer.Form.NFKD);
        text = MARKS.matcher(text).replaceAll("").toLowerCase(Locale.ROOT);
        text = NUMBERS.matcher(text).replaceAll("");
        return SPACES.matcher(text.replace('−', ' ').replace('–', ' ').replace('—', ' ').replace('-', ' ')
                .replace("\u200B", "").replace("\uFEFF", "")).replaceAll(" ").trim();
    }
    public static boolean matches(String query, String content) { return compile(query).test(content); }
    public static Predicate<String> compile(String query) {
        List<Term> terms = terms(query);
        return content -> {
            String haystack = normalize(content);
            for (Term term : terms) {
                boolean present = haystack.contains(term.value());
                if (term.excluded() ? present : !present) return false;
            }
            return true;
        };
    }
    private static List<Term> terms(String query) {
        String raw = query == null ? "" : query;
        raw = raw.replace('“', '"').replace('”', '"').replace('«', '"').replace('»', '"');
        List<Term> result = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if (ch == '"') { quoted = !quoted; continue; }
            if ((Character.isWhitespace(ch) || Character.isSpaceChar(ch)) && !quoted) {
                add(result, token); token.setLength(0);
            } else token.append(ch);
        }
        add(result, token); // Also flush a phrase with an unfinished closing quote.
        return List.copyOf(result);
    }
    private static void add(List<Term> terms, StringBuilder token) {
        String raw = token.toString();
        boolean excluded = raw.startsWith("!");
        String value = normalize(excluded ? raw.substring(1) : raw);
        if (value.isEmpty()) return;
        Term term = new Term(value, excluded);
        if (!terms.contains(term)) terms.add(term);
    }
    private record Term(String value, boolean excluded) { }
}

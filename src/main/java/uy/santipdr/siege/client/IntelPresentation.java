package uy.santipdr.siege.client;

import java.math.BigInteger;
import java.util.Locale;

/** Pure presentation rules shared by Intel screens and regression tests. */
public final class IntelPresentation {
    private IntelPresentation() { }

    public static boolean unknown(String value) {
        if (value == null) return true;
        String normalized = value.strip().toUpperCase(Locale.ROOT);
        return normalized.isEmpty() || normalized.equals("?") || normalized.equals("???")
                || normalized.equals("N/D") || normalized.equals("N/A")
                || normalized.contains("SIN REGISTRO CONFIRMADO") || normalized.contains("NO CONFIRMED RECORD")
                || normalized.contains("NO RECUPERAD") || normalized.contains("NOT RECOVERED")
                || normalized.contains("SIN INFORMACIÓN OFICIAL CONFIRMADA")
                || normalized.contains("NO OFFICIAL INFORMATION CONFIRMED");
    }

    public static int knownFields(IntelEntry entry, IntelEntry.IntelText text) {
        int known = 0;
        if (!unknown(entry.hp())) known++;
        if (!unknown(entry.defense())) known++;
        if (!unknown(text.origin())) known++;
        if (!unknown(text.armament())) known++;
        if (!unknown(text.variants())) known++;
        if (!unknown(text.description())) known++;
        return known;
    }

    public static int completeness(IntelEntry entry, IntelEntry.IntelText text) {
        return Math.round(knownFields(entry, text) * 100.0F / 6.0F);
    }

    /** Compact official-file coverage grade; it does not claim certainty or threat. */
    public static String coverageGrade(IntelEntry entry, IntelEntry.IntelText text) {
        int value = completeness(entry, text);
        if (value >= 100) return "A";
        if (value >= 67) return "B";
        if (value >= 34) return "C";
        if (value > 0) return "D";
        return "E";
    }

    public static BigInteger hpValue(String value) {
        if (value == null) return BigInteger.ZERO;
        String digits = value.replaceAll("[^0-9]", "");
        return digits.isEmpty() ? BigInteger.ZERO : new BigInteger(digits);
    }

    public static String compactHp(String value) {
        BigInteger hp = hpValue(value);
        if (hp.signum() == 0) return value == null || value.isBlank() ? "N/D" : value;
        BigInteger billion = BigInteger.valueOf(1_000_000_000L);
        BigInteger million = BigInteger.valueOf(1_000_000L);
        BigInteger thousand = BigInteger.valueOf(1_000L);
        if (hp.compareTo(billion) >= 0) return compact(hp, billion, "B");
        if (hp.compareTo(million) >= 0) return compact(hp, million, "M");
        if (hp.compareTo(thousand) >= 0) return compact(hp, thousand, "K");
        return hp.toString();
    }

    private static String compact(BigInteger value, BigInteger unit, String suffix) {
        BigInteger[] parts = value.divideAndRemainder(unit);
        int tenth = parts[1].multiply(BigInteger.TEN).divide(unit).intValue();
        return parts[0] + (tenth == 0 ? "" : "." + tenth) + suffix;
    }

    public static String categoryCode(String category) {
        return switch (category) {
            case "UNIT" -> "UNI";
            case "ADVANCED" -> "ADV";
            case "TANK" -> "TNK";
            case "BOSS" -> "BOS";
            case "ELITE" -> "ELT";
            case "SUPER-UNIT" -> "SUP";
            case "UNKNOWN" -> "UNK";
            default -> "UNK";
        };
    }

    public static int accent(String category) {
        return switch (category) {
            case "UNIT" -> 0xFFD94A4A;
            case "ADVANCED" -> 0xFF2F80FF;
            case "TANK" -> 0xFFD98A2B;
            case "BOSS" -> 0xFFB5162D;
            case "ELITE" -> 0xFF9B59D0;
            case "SUPER-UNIT" -> 0xFFE0B93F;
            case "UNKNOWN" -> 0xFF9AA4AB;
            default -> 0xFFB8C0C8;
        };
    }
}

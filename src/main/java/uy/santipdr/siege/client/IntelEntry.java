package uy.santipdr.siege.client;

public record IntelEntry(String code, String name, String category, int threat, String hp, String image,
                         IntelText spanish, IntelText english) {
    public IntelText text(boolean useSpanish) {
        return useSpanish ? spanish : english;
    }

    public record IntelText(String origin, String armament, String variants, String status,
                            String description, String advisory) {}
}

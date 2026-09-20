package uy.santipdr.siege.client;

public record IntelEntry(String code, String name, String category, int threat, String hp, String defense, String image,
                         IntelText spanish, IntelText english) {
    public IntelText text(boolean useSpanish) {
        return useSpanish ? spanish : english;
    }

    /**
     * Generic incomplete dossiers live outside the generated Intel root so the
     * asset preparation pipeline cannot mistake them for supplied unit artwork.
     */
    @Override
    public String image() {
        return "classified".equals(image) ? "placeholder/classified" : image;
    }

    public record IntelText(String origin, String armament, String variants, String status,
                            String description, String advisory) {}
}

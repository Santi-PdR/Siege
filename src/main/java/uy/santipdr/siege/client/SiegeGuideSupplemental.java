package uy.santipdr.siege.client;

import java.util.List;

/** Current guide records that are useful references but are not unit dossiers. */
public final class SiegeGuideSupplemental {
    private SiegeGuideSupplemental() { }

    private static final List<SiegeGuideData.Entry> ITEMS = List.of(
            new SiegeGuideData.Entry(
                    "third-justice",
                    SiegeGuideData.Category.ITEMS,
                    "Third Justice",
                    "Third Justice",
                    "CLASIFICACIÓN\nHerramienta de parry fabricada con Daemonium Ore Metal Gear. Rareza Eternal."
                    + "\n\nFUNCIÓN\nEstá diseñada para interceptar ataques durante una ventana breve de bloqueo. La activación correcta desvía el impacto y abre una ventana de respuesta; usarla fuera de tiempo deja al portador expuesto."
                    + "\n\nEFECTO PASIVO\nMientras permanece equipada en el inventario concede +2 perks, según el registro suministrado."
                    + "\n\nEMPLEO\nNo debe tratarse como un escudo permanente. Su valor está en conservarla durante enfrentamientos prolongados y reservar el parry para ataques cuya trayectoria pueda leerse con claridad."
                    + "\n\nREGISTRO VISUAL\nExiste metraje operativo suministrado mostrando su uso. El enlace original era un archivo temporal de Discord y no puede integrarse de forma fiable en el cliente hasta disponer del video como archivo permanente.",
                    "CLASSIFICATION\nParry tool built from Daemonium Ore Metal Gear. Eternal rarity."
                    + "\n\nFUNCTION\nDesigned to intercept attacks during a brief blocking window. Correct activation deflects the impact and opens a response window; mistiming leaves the carrier exposed."
                    + "\n\nPASSIVE EFFECT\nWhile kept equipped in the inventory it grants +2 perks, according to the supplied record."
                    + "\n\nUSE\nIt should not be treated as a permanent shield. Its value is in carrying it through prolonged engagements and reserving the parry for attacks whose trajectory can be read clearly."
                    + "\n\nVISUAL RECORD\nSupplied operational footage shows its use. The original link was a temporary Discord file and cannot be integrated reliably into the client until the video is available as a permanent asset.",
                    List.of(), false),
            new SiegeGuideData.Entry(
                    "aerorig",
                    SiegeGuideData.Category.ITEMS,
                    "Aerorig",
                    "Aerorig",
                    "CLASIFICACIÓN\nDispositivo de vuelo personal."
                    + "\n\nFUNCIÓN\nPermite desplazamiento aéreo asistido. Los límites de autonomía, velocidad y carga útil no están documentados en el expediente disponible, por lo que no se asignan valores inventados.",
                    "CLASSIFICATION\nPersonal flight device."
                    + "\n\nFUNCTION\nProvides assisted aerial movement. Endurance, speed and payload limits are not documented in the available file, so no invented values are assigned.",
                    List.of(), false),
            new SiegeGuideData.Entry(
                    "riflator",
                    SiegeGuideData.Category.ITEMS,
                    "Riflator",
                    "Riflator",
                    "FUNCIÓN\nArma registrada con un daño nominal de 130 en la información actual disponible."
                    + "\n\nLÍMITE\nCadencia, munición, alcance y efectos secundarios no se completan sin documentación adicional.",
                    "FUNCTION\nWeapon recorded with a nominal damage value of 130 in the current available information."
                    + "\n\nLIMIT\nFire rate, ammunition, range and secondary effects remain unspecified without additional documentation.",
                    List.of(), false),
            new SiegeGuideData.Entry(
                    "holo-watch",
                    SiegeGuideData.Category.ITEMS,
                    "HOLO-Watch",
                    "HOLO-Watch",
                    "CLASIFICACIÓN\nDispositivo identificado como HOLO-Watch."
                    + "\n\nESTADO DEL EXPEDIENTE\nSu función exacta no está suficientemente documentada. El nombre y la referencia visual se conservan sin atribuir capacidades no confirmadas.",
                    "CLASSIFICATION\nDevice identified as HOLO-Watch."
                    + "\n\nFILE STATUS\nIts exact function is not sufficiently documented. The name and visual reference are retained without assigning unconfirmed capabilities.",
                    List.of(), false)
    );

    public static List<SiegeGuideData.Entry> entries(SiegeGuideData.Category category, String query, boolean spanish) {
        if (category != SiegeGuideData.Category.ITEMS) return List.of();
        var matcher = IntelSearch.compile(query);
        return ITEMS.stream()
                .filter(e -> matcher.test(e.title(spanish) + " " + e.body(spanish)))
                .toList();
    }

    public static int itemCount() { return ITEMS.size(); }
}

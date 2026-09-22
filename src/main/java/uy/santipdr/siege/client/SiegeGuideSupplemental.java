package uy.santipdr.siege.client;

import java.util.List;

/** Current equipment records that are useful references but are not unit dossiers. */
public final class SiegeGuideSupplemental {
    private SiegeGuideSupplemental() { }

    private static final List<SiegeGuideData.Entry> ITEMS = List.of(
            new SiegeGuideData.Entry(
                    "third-justice",
                    SiegeGuideData.Category.ITEMS,
                    "Third Justice",
                    "Third Justice",
                    "CLASIFICACIÓN\nHerramienta de parry identificada como Third Justice. Rareza Eternal en el registro suministrado."
                    + "\n\nVENTANA DE RESPUESTA\nLa captura del objeto indica que al bloquear se abre una ventana de parry de 0,1 s. Después de una intercepción existe un enfriamiento de 1 s antes de volver a responder con la misma técnica."
                    + "\n\nEFECTO DEL PARRY\nEl metraje suministrado muestra intercepciones contra objetivos hostiles y desplazamiento del atacante después de impactos correctamente desviados. No se presenta como defensa pasiva: depende de ejecutar el bloqueo en la ventana indicada."
                    + "\n\nACUMULACIÓN TEMPORAL\nEl video proporcionado contiene una indicación de acumulación temporal de parries y activación de una regeneración Fantasma. El archivo conserva ese comportamiento como evidencia observada, sin extenderlo a condiciones que no aparecen en el metraje."
                    + "\n\nCOSTE FÍSICO\nLa captura del objeto registra una reducción del 15% en velocidad de movimiento mientras se sostiene y hambre consumida al doble de velocidad mientras permanece en el inventario."
                    + "\n\nREGISTRO MULTIMEDIA\nLa ficha conserva las dos capturas originales y una secuencia de fotogramas recuperados del video entregado. El visor REEL reproduce esa secuencia dentro de SIEGE sin incluir un decodificador MP4 en tiempo real."
                    + "\n\nLÍMITE DEL ARCHIVO\nNo se añaden estadísticas, daño, durabilidad ni efectos que no estén respaldados por el material recibido u otra documentación ya conservada.",
                    "CLASSIFICATION\nParry tool identified as Third Justice. Eternal rarity in the supplied record."
                    + "\n\nRESPONSE WINDOW\nThe item capture states that blocking opens a 0.1 s parry window. After an interception there is a 1 s cooldown before the same technique can answer again."
                    + "\n\nPARRY EFFECT\nThe supplied footage shows interceptions against hostile targets and displacement of the attacker after correctly deflected hits. It is not presented as passive protection: it depends on blocking inside the documented window."
                    + "\n\nTEMPORARY ACCUMULATION\nThe supplied video contains an indication of temporary parry accumulation and activation of Phantom Regeneration. The archive preserves this as observed evidence without extending it to conditions that are not shown in the footage."
                    + "\n\nPHYSICAL COST\nThe item capture records 15% reduced movement speed while held and hunger consumed twice as fast while carried in the inventory."
                    + "\n\nMULTIMEDIA RECORD\nThe file retains both original captures and a sequence of frames recovered from the supplied video. The REEL viewer plays that sequence inside SIEGE without bundling a real-time MP4 decoder."
                    + "\n\nARCHIVE LIMIT\nNo damage, durability, statistics or effects are added unless they are supported by the supplied material or other documentation already retained.",
                    List.of(
                            new SiegeGuideData.Art("third_justice_tooltip.png", 762, 207,
                                    "Identificación y parámetros visibles de Third Justice",
                                    "Visible Third Justice identification and parameters"),
                            new SiegeGuideData.Art("third_justice_field.png", 1024, 579,
                                    "Third Justice equipada durante una prueba de campo",
                                    "Third Justice equipped during a field test"),
                            new SiegeGuideData.Art("third_justice_reel_01.png", 640, 360,
                                    "Registro de video · presentación de Third Justice",
                                    "Video record · Third Justice introduction"),
                            new SiegeGuideData.Art("third_justice_reel_02.png", 640, 360,
                                    "Registro de video · intercepción contra objetivo hostil",
                                    "Video record · interception against a hostile target"),
                            new SiegeGuideData.Art("third_justice_reel_03.png", 640, 360,
                                    "Registro de video · acumulación temporal y regeneración Fantasma",
                                    "Video record · temporary accumulation and Phantom Regeneration")
                    ), false),
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

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
                    + "\n\nVENTANA DE RESPUESTA\nAl bloquear abre una ventana de aproximadamente 0,1 s para interceptar el impacto. Después de una intercepción necesita cerca de 1 s antes de volver a responder con la misma técnica."
                    + "\n\nEFECTO DEL PARRY\nEl metraje suministrado confirma que una intercepción correcta puede expulsar al atacante a distancia. No funciona como una defensa pasiva: exige leer la trayectoria y ejecutar el bloqueo en el momento preciso."
                    + "\n\nACUMULACIÓN TEMPORAL\nEl mismo registro muestra que acumular parries durante un período limitado activa una «regeneración Fantasma». Mientras permanece activa puede compensar un fallo posterior de parry perfecto antes de agotarse."
                    + "\n\nCOSTE FÍSICO\nEl registro del objeto indica una reducción aproximada del 15% en movilidad mientras se sostiene y un consumo energético equivalente al doble mientras se transporta en el inventario."
                    + "\n\nEFECTO PASIVO\nMientras permanece equipada en el inventario concede +2 perks, según la documentación suministrada."
                    + "\n\nEMPLEO\nSu utilidad aumenta en enfrentamientos prolongados: conservar las acumulaciones y no gastar la ventana de parry en ataques poco legibles reduce la probabilidad de quedar expuesto. El objeto no debe tratarse como un escudo permanente."
                    + "\n\nREGISTRO VISUAL\nEl video suministrado muestra la herramienta en uso contra objetivos hostiles y confirma el retroceso producido por parries exitosos y la activación de la regeneración Fantasma por acumulación.",
                    "CLASSIFICATION\nParry tool built from Daemonium Ore Metal Gear. Eternal rarity."
                    + "\n\nRESPONSE WINDOW\nBlocking opens an approximately 0.1 s window to intercept an impact. After a successful interception it needs about 1 s before the same technique can answer again."
                    + "\n\nPARRY EFFECT\nThe supplied footage confirms that a correct interception can drive the attacker back over a significant distance. It is not passive protection: the incoming trajectory has to be read and the block timed correctly."
                    + "\n\nTEMPORARY ACCUMULATION\nThe same record shows that accumulating parries for a limited period activates a 'Phantom Regeneration'. While active it can compensate for a later failed perfect parry before the reserve is exhausted."
                    + "\n\nPHYSICAL COST\nThe item record indicates roughly 15% reduced mobility while held and energy consumption equivalent to twice normal while carried in the inventory."
                    + "\n\nPASSIVE EFFECT\nWhile kept equipped in the inventory it grants +2 perks, according to the supplied documentation."
                    + "\n\nUSE\nIts value increases in prolonged engagements: preserving accumulated parries and avoiding unnecessary attempts against unreadable attacks reduces the chance of being left exposed. It should not be treated as a permanent shield."
                    + "\n\nVISUAL RECORD\nThe supplied video shows the tool used against hostile targets and confirms the knockback from successful parries and activation of Phantom Regeneration through accumulation.",
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

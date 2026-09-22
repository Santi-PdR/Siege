package uy.santipdr.siege.client;

import java.util.List;

/** SIEGE 4.0 Arsenal entries: equipment and important server tools, never unit dossiers. */
public final class SiegeGuideSupplemental {
    private SiegeGuideSupplemental() { }

    private static final List<SiegeGuideData.Entry> ITEMS = List.of(
            new SiegeGuideData.Entry(
                    "third-justice", SiegeGuideData.Category.ITEMS,
                    "Third Justice", "Third Justice",
                    "CLASIFICACIÓN\nReliquia/herramienta de parry. Rareza Eternal en la ficha disponible."
                    + "\n\nPARRY\nAl bloquear abre una ventana de parry de 0,1 s. Después de un parry existe un enfriamiento de 1 s."
                    + "\n\nCOMBATE\nPuede responder directamente a atacantes durante el parry y tiene capacidad de aturdimiento. También se observó desplazamiento del atacante después de intercepciones correctas."
                    + "\n\nEFECTO ADICIONAL\nEl registro de combate muestra acumulación temporal de parries y activación de Regeneración Fantasma."
                    + "\n\nCOSTE\nReduce 15% la velocidad de movimiento mientras se sostiene y hace que el hambre se consuma al doble mientras permanece en el inventario."
                    + "\n\nMULTIMEDIA\nLa ficha incluye capturas y un REEL de prueba de campo.",
                    "CLASSIFICATION\nParry relic/tool. Eternal rarity in the available record."
                    + "\n\nPARRY\nBlocking opens a 0.1 s parry window. After a parry there is a 1 s cooldown."
                    + "\n\nCOMBAT\nCan answer attackers directly during the parry and has stunning capability. Attacker displacement was also observed after successful interceptions."
                    + "\n\nADDITIONAL EFFECT\nCombat footage shows temporary parry accumulation and activation of Phantom Regeneration."
                    + "\n\nCOST\nReduces movement speed by 15% while held and doubles hunger consumption while carried in the inventory."
                    + "\n\nMEDIA\nThe file includes captures and a field-test REEL.",
                    List.of(
                            new SiegeGuideData.Art("third_justice_tooltip.png", 762, 207,
                                    "Parámetros visibles de Third Justice", "Visible Third Justice parameters"),
                            new SiegeGuideData.Art("third_justice_field.png", 1024, 579,
                                    "Third Justice equipada", "Third Justice equipped"),
                            new SiegeGuideData.Art("third_justice_reel_01.png", 640, 360,
                                    "REEL · presentación", "REEL · introduction"),
                            new SiegeGuideData.Art("third_justice_reel_02.png", 640, 360,
                                    "REEL · intercepción", "REEL · interception"),
                            new SiegeGuideData.Art("third_justice_reel_03.png", 640, 360,
                                    "REEL · acumulación y regeneración", "REEL · accumulation and regeneration")
                    ), false),

            new SiegeGuideData.Entry(
                    "geography-table", SiegeGuideData.Category.ITEMS,
                    "Geography Table", "Geography Table",
                    "FUNCIÓN\nMesa de investigación usada para revelar información oculta de ciertos objetos y reliquias."
                    + "\n\nCUÁNDO USARLA\nSi una reliquia no muestra todos sus efectos o no sabés qué hace un objeto importante, investigalo antes de venderlo, equiparlo o manipularlo."
                    + "\n\nPRECIO DE REFERENCIA\nSe registró una referencia de 120 wins. Los precios pueden cambiar, así que no lo tomes como valor permanente."
                    + "\n\nRELACIONADO\nFallen Angel Halo es un ejemplo de reliquia cuya información estaba incompleta sin investigación adicional.",
                    "FUNCTION\nResearch table used to reveal hidden information on some items and relics."
                    + "\n\nWHEN TO USE IT\nIf a relic does not show all of its effects or an important item is unclear, research it before selling, equipping or manipulating it."
                    + "\n\nREFERENCE PRICE\nA 120-win reference was recorded. Prices can change, so do not treat it as permanent."
                    + "\n\nRELATED\nFallen Angel Halo is an example of a relic whose information was incomplete without further research.",
                    List.of(), false),

            new SiegeGuideData.Entry(
                    "daemonium-kit", SiegeGuideData.Category.ITEMS,
                    "Daemonium Kit", "Daemonium Kit",
                    "FUNCIÓN\nHerramienta necesaria para extraer materiales de reliquias."
                    + "\n\nADVERTENCIA\nUna reliquia no debería desmontarse como un objeto normal: métodos convencionales pueden destruirla."
                    + "\n\nANTES DE EXTRAER\nConfirmá qué reliquia tenés, qué querés recuperar y si conocés su procedimiento particular. Tener el kit no significa conocer automáticamente todos los pasos.",
                    "FUNCTION\nTool required for extracting materials from relics."
                    + "\n\nWARNING\nA relic should not be dismantled like a normal item: conventional methods may destroy it."
                    + "\n\nBEFORE EXTRACTION\nConfirm which relic you have, what you want to recover and whether its specific procedure is known. Having the kit does not automatically reveal every step.",
                    List.of(), false),

            new SiegeGuideData.Entry(
                    "fallen-angel-halo", SiegeGuideData.Category.ITEMS,
                    "Fallen Angel Halo", "Fallen Angel Halo",
                    "CLASIFICACIÓN\nReliquia con lifesteal conocido."
                    + "\n\nEFECTO CONOCIDO\nRobo de vida. El porcentaje, condiciones exactas y otros efectos todavía no están completos."
                    + "\n\nRECOMENDACIÓN\nAntes de construir una estrategia alrededor de la reliquia, investigá la información oculta con Geography Table.",
                    "CLASSIFICATION\nRelic with known lifesteal."
                    + "\n\nKNOWN EFFECT\nLife steal. The percentage, exact conditions and other effects are still incomplete."
                    + "\n\nRECOMMENDATION\nBefore building a strategy around it, research its hidden information with Geography Table.",
                    List.of(), false),

            new SiegeGuideData.Entry(
                    "improbability-scroll", SiegeGuideData.Category.ITEMS,
                    "Improbability Scroll", "Improbability Scroll",
                    "FUNCIÓN\nPermite vincular Assembling u otras mesas de Steel, guardarlas en una mochila telepática y usarlas sin colocarlas físicamente."
                    + "\n\nPENDIENTE\nObtención, coste, consumo y límites exactos todavía no están completos."
                    + "\n\nUTILIDAD\nReduce la necesidad de mover o desplegar estaciones físicas cada vez que necesitás una mesa vinculada.",
                    "FUNCTION\nAllows linking Assembling or other Steel tables, storing them in a telepathic backpack and using them without physically placing them."
                    + "\n\nOPEN DETAILS\nAcquisition, cost, consumption and exact limits are still incomplete."
                    + "\n\nUTILITY\nReduces the need to move or deploy physical stations every time a linked table is needed.",
                    List.of(), false),

            new SiegeGuideData.Entry(
                    "assembling-table", SiegeGuideData.Category.ITEMS,
                    "Assembling Table", "Assembling Table",
                    "FUNCIÓN\nInstala chips y trasplantes y forma parte de la ruta tecnológica asociada a Cyborg."
                    + "\n\nANTES DE INSTALAR\nComprobá compatibilidad, materiales, coste y consecuencia. No asumas que cualquier implante puede retirarse o que toda transformación es reversible."
                    + "\n\nPRECIO HISTÓRICO\nExiste una referencia antigua de compra por 4 millones; no debe tomarse como precio actual garantizado."
                    + "\n\nRELACIONADO\nImprobability Scroll puede facilitar el uso de mesas vinculadas sin colocarlas.",
                    "FUNCTION\nInstalls chips and transplants and is part of the technology route associated with Cyborg."
                    + "\n\nBEFORE INSTALLING\nCheck compatibility, materials, cost and consequence. Do not assume every implant can be removed or every transformation is reversible."
                    + "\n\nHISTORICAL PRICE\nAn old purchase reference of 4 million exists; it should not be treated as a guaranteed current price."
                    + "\n\nRELATED\nImprobability Scroll can make linked tables usable without placing them.",
                    List.of(), false),

            new SiegeGuideData.Entry(
                    "aerorig", SiegeGuideData.Category.ITEMS,
                    "Aerorig", "Aerorig",
                    "CLASIFICACIÓN\nDispositivo de vuelo personal."
                    + "\n\nFUNCIÓN\nPermite desplazamiento aéreo asistido. Autonomía, velocidad y carga útil todavía no están completas.",
                    "CLASSIFICATION\nPersonal flight device."
                    + "\n\nFUNCTION\nProvides assisted aerial movement. Endurance, speed and payload are still incomplete.",
                    List.of(), false),

            new SiegeGuideData.Entry(
                    "riflator", SiegeGuideData.Category.ITEMS,
                    "Riflator", "Riflator",
                    "FUNCIÓN\nArma registrada con daño nominal de 130."
                    + "\n\nPENDIENTE\nCadencia, munición, alcance y efectos secundarios todavía no están completos.",
                    "FUNCTION\nWeapon recorded with nominal damage of 130."
                    + "\n\nOPEN DETAILS\nFire rate, ammunition, range and secondary effects are still incomplete.",
                    List.of(), false),

            new SiegeGuideData.Entry(
                    "holo-watch", SiegeGuideData.Category.ITEMS,
                    "HOLO-Watch", "HOLO-Watch",
                    "CLASIFICACIÓN\nDispositivo identificado como HOLO-Watch."
                    + "\n\nESTADO\nSu función exacta todavía no está completa. Se conserva como equipo conocido sin asignarle capacidades que no estén confirmadas.",
                    "CLASSIFICATION\nDevice identified as HOLO-Watch."
                    + "\n\nSTATUS\nIts exact function is still incomplete. It remains known equipment without assigning unconfirmed capabilities.",
                    List.of(), false)
    );

    public static List<SiegeGuideData.Entry> entries(SiegeGuideData.Category category, String query, boolean spanish) {
        if (category != SiegeGuideData.Category.ITEMS) return List.of();
        var matcher = IntelSearch.compile(query);
        return ITEMS.stream().filter(e -> matcher.test(e.title(spanish) + " " + e.body(spanish))).toList();
    }

    public static int itemCount() { return ITEMS.size(); }
}

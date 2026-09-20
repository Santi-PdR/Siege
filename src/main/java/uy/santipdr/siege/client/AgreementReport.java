package uy.santipdr.siege.client;

/**
 * Guide/Operations field report only. Nothing in this class is official dossier
 * data and none of these claims may be injected into Intel cards.
 */
public final class AgreementReport {
    private AgreementReport() { }

    public static final String ES = "REPORTE DE CAMPO · SIN VERIFICAR\nUn jugador describe a los Agreements como una versión menos avanzada de Rick Sanchez: una comparación informal con su tecnología de portales, no una equivalencia de poderes."
        + "\n\nGATES\nSegún el reporte, son teletransportadores caseros y costosos. El jugador indica que no sirven para la táctica de saturación descrita; no deben confundirse con Rifts."
        + "\n\nRIFTS\nSe describen como viajes interdimensionales. La comparación incluye portales similares a los de Blox Fruits y fluido de portales. No se confirmaron alcance, coste ni tiempo de recarga."
        + "\n\nVISORES\nEl testimonio afirma que los visores pueden bloquearse al observar portales ajenos que no sean Rifts propios de Agreement. La duración y las condiciones exactas siguen sin comprobarse.";

    public static final String EN = "FIELD REPORT · UNVERIFIED\nA player describes Agreements as a less advanced version of Rick Sanchez: an informal comparison to portal technology, not an equivalence of powers."
        + "\n\nGATES\nAccording to the report, these are costly, homemade teleporters. The player says they do not work for the described overload tactic; they should not be confused with Rifts."
        + "\n\nRIFTS\nDescribed as interdimensional travel. The comparison includes Blox Fruits-like portals and portal fluid. Range, cost and cooldown have not been confirmed."
        + "\n\nVISORS\nThe testimony claims that visors can lock up when observing foreign portals that are not Agreement's own Rifts. Duration and exact conditions remain unverified.";

    public static final String ADVICE_ES = "HIPÓTESIS TÁCTICA · SIN VERIFICAR\nSabotajes repetidos ejecutados más rápido de lo que Agreement puede responder podrían saturarlo. El jugador también propone portales de combate ajenos para abrumar sus visores; distingue esa táctica de los Gates. No se ha demostrado que sea su única debilidad ni que garantice vencerlo.";
    public static final String ADVICE_EN = "TACTICAL HYPOTHESIS · UNVERIFIED\nRepeated sabotage performed faster than Agreement can respond may overload it. The player also proposes foreign combat portals to overwhelm its visors, distinguishing this tactic from Gates. This has not been shown to be its only weakness or a guaranteed way to defeat it.";

    /** Official Intel dossiers deliberately never consume this field report. */
    public static boolean applies(IntelEntry entry) { return false; }
}

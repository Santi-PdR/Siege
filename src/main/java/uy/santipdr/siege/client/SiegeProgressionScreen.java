package uy.santipdr.siege.client;

import net.minecraft.client.gui.screens.Screen;

/** Progression and Trial briefing for SIEGE 4.0. */
public final class SiegeProgressionScreen extends Siege4CardScreen {
    public SiegeProgressionScreen(Screen parent) {
        super(parent,
                "RUTA DE PROGRESIÓN", "PROGRESSION ROUTE",
                "V1→V4, Trials, exploración, reliquias, Assembling, dimensiones y economía en una sola ruta.",
                "V1→V4, Trials, exploration, relics, Assembling, dimensions and economy in one route.",
                Siege4ReferenceData.progression(), SiegeTheme.GOLD);
    }
}

package uy.santipdr.siege.client;

import net.minecraft.client.gui.screens.Screen;

/** New-player briefing for SIEGE 4.0. */
public final class SiegeRecruitScreen extends Siege4CardScreen {
    public SiegeRecruitScreen(Screen parent) {
        super(parent,
                "BRIEFING DE RECLUTA", "RECRUIT BRIEFING",
                "Lo esencial del servidor antes de gastar recursos, activar Trials o entrar a zonas peligrosas.",
                "The server essentials before spending resources, activating Trials or entering dangerous zones.",
                Siege4ReferenceData.recruit(), SiegeTheme.GREEN);
    }
}

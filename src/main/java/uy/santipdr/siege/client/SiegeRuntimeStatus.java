package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraftforge.fml.ModList;
import uy.santipdr.siege.SiegeMod;

/** Live client status shared by command, diagnostics and operational chrome. */
public final class SiegeRuntimeStatus {
    public enum Health { READY, ATTENTION, ERROR }

    private SiegeRuntimeStatus() { }

    public static String version() {
        return ModList.get().getModContainerById(SiegeMod.MOD_ID)
                .map(container -> container.getModInfo().getVersion().toString()).orElse("DEV");
    }

    public static String forgeVersion() { return version("forge"); }
    public static String minecraftVersion() { return SharedConstants.getCurrentVersion().getName(); }

    public static String renderBackend() {
        ModList mods = ModList.get();
        if (mods.isLoaded("embeddium")) return "Embeddium " + version("embeddium");
        if (mods.isLoaded("rubidium")) return "Rubidium " + version("rubidium");
        if (mods.isLoaded("sodium")) return "Sodium " + version("sodium");
        return "Minecraft";
    }

    public static SiegeClientProfile.Profile profile() { return SiegeClientProfile.detect(); }

    public static Health health() {
        if (SiegeDiagnosticReport.errors(false) > 0) return Health.ERROR;
        if (SiegeDiagnosticReport.warnings(false) > 0 || SiegeDiagnosticReport.notices(false) > 0) return Health.ATTENTION;
        return Health.READY;
    }

    public static int readiness() { return SiegeDiagnosticReport.readiness(); }

    public static List<String> warnings(boolean spanish) {
        List<String> warnings = new ArrayList<>();
        for (SiegeDiagnosticReport.Entry entry : SiegeDiagnosticReport.entries(spanish)) {
            if (entry.severity() != SiegeDiagnosticReport.Severity.OK) warnings.add(entry.detail());
        }
        return List.copyOf(warnings);
    }

    public static String healthLabel(boolean spanish) {
        return switch (health()) {
            case READY -> spanish ? "OPERATIVO" : "OPERATIONAL";
            case ATTENTION -> spanish ? "ATENCIÓN" : "ATTENTION";
            case ERROR -> spanish ? "CRÍTICO" : "CRITICAL";
        };
    }

    public static int healthAccent() {
        return switch (health()) {
            case READY -> SiegeTheme.GREEN;
            case ATTENTION -> SiegeTheme.GOLD;
            case ERROR -> SiegeTheme.RED;
        };
    }

    public static String profileFitLabel(boolean spanish) {
        SiegeClientProfile.Profile active = profile();
        if (active != SiegeClientProfile.Profile.CUSTOM)
            return SiegeClientProfile.label(active, spanish) + " · 100%";
        SiegeClientProfile.Profile nearest = SiegeProfileMetrics.nearest();
        return "CUSTOM → " + SiegeClientProfile.label(nearest, spanish)
                + " · " + SiegeProfileMetrics.fitPercent(nearest) + "%";
    }

    public static String audioLabel(boolean spanish) {
        if (!SiegeConfig.music) return spanish ? "MÚSICA OFF" : "MUSIC OFF";
        if (SiegeConfig.musicVolume == 0) return spanish ? "MÚSICA 0%" : "MUSIC 0%";
        String state = SiegeMusic.isActuallyPlaying() ? SiegeMusic.currentTrackName() : (spanish ? "EN ESPERA" : "WAITING");
        return state + " · " + SiegeConfig.musicVolume + "%";
    }

    public static String backgroundLabel(boolean spanish) {
        String state = SiegeConfig.selectedScene >= 0 ? (spanish ? "FIJO" : "PINNED")
                : SiegeConfig.animatedBackgrounds ? (spanish ? "ROTACIÓN" : "ROTATING") : (spanish ? "ESTÁTICO" : "STATIC");
        return state + " · " + SiegeBackgrounds.name(SiegeBackgrounds.currentIndex(System.currentTimeMillis()), spanish);
    }

    public static String intelLabel(boolean spanish) {
        return IntelCatalog.total() + " " + (spanish ? "EXPEDIENTES" : "DOSSIERS")
                + " · U " + IntelCatalog.count("UNIT") + " · A " + IntelCatalog.count("ADVANCED")
                + " · T " + IntelCatalog.count("TANK") + " · B " + IntelCatalog.count("BOSS")
                + " · E " + IntelCatalog.count("ELITE") + " · S " + IntelCatalog.count("SUPER-UNIT")
                + " · ? " + IntelCatalog.count("UNKNOWN");
    }

    public static String accessibilityLabel(boolean spanish) {
        List<String> states = new ArrayList<>();
        if (SiegeConfig.autoContrast) states.add(spanish ? "AUTO CONTRASTE" : "AUTO CONTRAST");
        if (SiegeConfig.highContrast) states.add(spanish ? "ALTO CONTRASTE" : "HIGH CONTRAST");
        if (SiegeConfig.reducedMotion) states.add(spanish ? "MOV. REDUCIDO" : "REDUCED MOTION");
        if (SiegeConfig.reduceFlashes) states.add(spanish ? "DESTELLOS OFF" : "FLASHES OFF");
        if (states.isEmpty()) return spanish ? "ESTÁNDAR" : "STANDARD";
        return String.join(" · ", states);
    }

    public static String prioritySummary(boolean spanish) {
        int critical = SiegeDiagnosticReport.errors(spanish);
        int attention = SiegeDiagnosticReport.warnings(spanish) + SiegeDiagnosticReport.notices(spanish);
        return (spanish ? "CRÍTICO " : "CRITICAL ") + critical + " · "
                + (spanish ? "ATENCIÓN " : "ATTENTION ") + attention + " · "
                + (spanish ? "OPERATIVO " : "OPERATIONAL ") + SiegeDiagnosticReport.operational(spanish);
    }

    private static String version(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(container -> container.getModInfo().getVersion().toString()).orElse("-");
    }
}

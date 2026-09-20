package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraftforge.fml.ModList;
import uy.santipdr.siege.SiegeMod;

/** Live, presentation-only diagnostics shared by the 0.50 command surfaces. */
public final class SiegeRuntimeStatus {
    public enum Health { READY, ATTENTION, ERROR }

    private SiegeRuntimeStatus() { }

    public static String version() {
        return ModList.get().getModContainerById(SiegeMod.MOD_ID)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("DEV");
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
        if (!SiegeConfig.lastSaveSucceeded) return Health.ERROR;
        return warnings(false).isEmpty() ? Health.READY : Health.ATTENTION;
    }

    public static int readiness() {
        if (!SiegeConfig.lastSaveSucceeded) return 35;
        int value = 100;
        if (SiegeConfig.music && SiegeConfig.musicVolume == 0) value -= 12;
        if (SiegeConfig.uiSounds && SiegeConfig.uiVolume == 0) value -= 8;
        if (SiegeConfig.reduceFlashes && SiegeConfig.titleInterference) value -= 16;
        if (SiegeConfig.graphics == SiegeConfig.Graphics.PERFORMANCE && SiegeConfig.animatedBackgrounds) value -= 8;
        if (SiegeConfig.graphics == SiegeConfig.Graphics.PERFORMANCE && SiegeConfig.animatedIntel) value -= 8;
        return Math.max(0, Math.min(100, value));
    }

    public static List<String> warnings(boolean spanish) {
        List<String> warnings = new ArrayList<>();
        if (!SiegeConfig.lastSaveSucceeded) warnings.add(spanish
                ? "La configuración no pudo guardarse; los últimos cambios pueden perderse."
                : "Configuration could not be saved; recent changes may be lost.");
        if (SiegeConfig.music && SiegeConfig.musicVolume == 0) warnings.add(spanish
                ? "La música está activada pero su volumen está en 0%."
                : "Music is enabled but its volume is at 0%.");
        if (SiegeConfig.uiSounds && SiegeConfig.uiVolume == 0) warnings.add(spanish
                ? "Los sonidos de interfaz están activados pero silenciados por volumen."
                : "UI sounds are enabled but muted by their volume setting.");
        if (SiegeConfig.reduceFlashes && SiegeConfig.titleInterference) warnings.add(spanish
                ? "Reducir destellos está activo mientras la interferencia del título sigue habilitada."
                : "Reduce flashes is active while title interference remains enabled.");
        if (SiegeConfig.graphics == SiegeConfig.Graphics.PERFORMANCE && SiegeConfig.animatedBackgrounds) warnings.add(spanish
                ? "El perfil gráfico de rendimiento conserva fondos animados."
                : "The performance graphics profile still has animated backgrounds enabled.");
        if (SiegeConfig.graphics == SiegeConfig.Graphics.PERFORMANCE && SiegeConfig.animatedIntel) warnings.add(spanish
                ? "El perfil gráfico de rendimiento conserva Intel animado."
                : "The performance graphics profile still has animated Intel enabled.");
        return warnings;
    }

    public static String healthLabel(boolean spanish) {
        return switch (health()) {
            case READY -> spanish ? "LISTO" : "READY";
            case ATTENTION -> spanish ? "REVISAR" : "CHECK";
            case ERROR -> spanish ? "ERROR DE CONFIG" : "CONFIG ERROR";
        };
    }

    public static int healthAccent() {
        return switch (health()) {
            case READY -> SiegeTheme.GREEN;
            case ATTENTION -> SiegeTheme.GOLD;
            case ERROR -> SiegeTheme.RED;
        };
    }

    public static String audioLabel(boolean spanish) {
        if (!SiegeConfig.music) return spanish ? "MÚSICA OFF" : "MUSIC OFF";
        if (SiegeConfig.musicVolume == 0) return spanish ? "MÚSICA 0%" : "MUSIC 0%";
        String state = SiegeMusic.isActuallyPlaying()
                ? SiegeMusic.currentTrackName()
                : (spanish ? "EN ESPERA" : "WAITING");
        return state + " · " + SiegeConfig.musicVolume + "%";
    }

    public static String backgroundLabel(boolean spanish) {
        String state = SiegeConfig.selectedScene >= 0
                ? (spanish ? "FIJO" : "PINNED")
                : SiegeConfig.animatedBackgrounds
                    ? (spanish ? "ROTACIÓN" : "ROTATING")
                    : (spanish ? "ESTÁTICO" : "STATIC");
        return state + " · " + SiegeBackgrounds.name(
                SiegeBackgrounds.currentIndex(System.currentTimeMillis()), spanish);
    }

    public static String intelLabel(boolean spanish) {
        return IntelCatalog.total() + " " + (spanish ? "EXPEDIENTES" : "DOSSIERS")
                + " · U " + IntelCatalog.count("UNIT")
                + " · A " + IntelCatalog.count("ADVANCED")
                + " · T " + IntelCatalog.count("TANK")
                + " · B " + IntelCatalog.count("BOSS")
                + " · E " + IntelCatalog.count("ELITE")
                + " · S " + IntelCatalog.count("SUPER-UNIT");
    }

    public static String accessibilityLabel(boolean spanish) {
        List<String> states = new ArrayList<>();
        if (SiegeConfig.highContrast) states.add(spanish ? "CONTRASTE" : "CONTRAST");
        if (SiegeConfig.reducedMotion) states.add(spanish ? "MOV. REDUCIDO" : "REDUCED MOTION");
        if (SiegeConfig.reduceFlashes) states.add(spanish ? "DESTELLOS OFF" : "FLASHES OFF");
        if (states.isEmpty()) return spanish ? "ESTÁNDAR" : "STANDARD";
        return String.join(" · ", states);
    }

    private static String version(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("-");
    }
}

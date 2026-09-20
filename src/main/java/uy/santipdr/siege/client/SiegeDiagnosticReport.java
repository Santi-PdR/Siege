package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;

/** Detailed client-only diagnostic model introduced in SIEGE 0.60.0. */
public final class SiegeDiagnosticReport {
    public enum Severity { OK, NOTICE, WARNING, ERROR }
    public record Entry(String code, String title, String detail, Severity severity) { }

    private SiegeDiagnosticReport() { }

    public static List<Entry> entries(boolean spanish) {
        List<Entry> entries = new ArrayList<>();

        entries.add(new Entry("CFG", spanish ? "CONFIGURACIÓN" : "CONFIGURATION",
                SiegeConfig.lastSaveSucceeded
                        ? (spanish ? "Cambios guardados correctamente." : "Changes are being saved correctly.")
                        : (spanish ? "No se pudo guardar la configuración; revisá permisos del archivo." : "Configuration could not be saved; check file permissions."),
                SiegeConfig.lastSaveSucceeded ? Severity.OK : Severity.ERROR));

        Severity musicSeverity = SiegeConfig.music && SiegeConfig.musicVolume == 0 ? Severity.WARNING : Severity.OK;
        entries.add(new Entry("MUS", spanish ? "MÚSICA" : "MUSIC",
                !SiegeConfig.music
                        ? (spanish ? "Banda sonora desactivada por preferencia." : "Soundtrack disabled by preference.")
                        : SiegeConfig.musicVolume == 0
                            ? (spanish ? "La música está habilitada pero su volumen está en 0%." : "Music is enabled but its volume is at 0%.")
                            : SiegeRuntimeStatus.audioLabel(spanish), musicSeverity));

        Severity uiSeverity = SiegeConfig.uiSounds && SiegeConfig.uiVolume == 0 ? Severity.WARNING : Severity.OK;
        entries.add(new Entry("SFX", spanish ? "SONIDOS DE INTERFAZ" : "UI SOUNDS",
                !SiegeConfig.uiSounds
                        ? (spanish ? "Efectos de interfaz desactivados por preferencia." : "UI effects disabled by preference.")
                        : SiegeConfig.uiVolume == 0
                            ? (spanish ? "Los efectos están habilitados pero su volumen está en 0%." : "UI effects are enabled but their volume is at 0%.")
                            : (spanish ? "Respuesta de botones activa al " : "Button feedback active at ") + SiegeConfig.uiVolume + "%.", uiSeverity));

        boolean flashConflict = SiegeConfig.reduceFlashes && SiegeConfig.titleInterference;
        entries.add(new Entry("VIS", spanish ? "EFECTOS VISUALES" : "VISUAL EFFECTS",
                flashConflict
                        ? (spanish ? "Reducir destellos está activo pero la interferencia del título sigue habilitada." : "Reduce flashes is active while title interference remains enabled.")
                        : (spanish ? "Efectos rápidos coherentes con la preferencia de accesibilidad." : "Rapid effects match the accessibility preference."),
                flashConflict ? Severity.WARNING : Severity.OK));

        boolean performanceConflict = SiegeConfig.graphics == SiegeConfig.Graphics.PERFORMANCE
                && (SiegeConfig.animatedBackgrounds || SiegeConfig.animatedIntel || SiegeConfig.menuEffects);
        entries.add(new Entry("GPU", spanish ? "PERFIL DE RENDER" : "RENDER PROFILE",
                performanceConflict
                        ? (spanish ? "Rendimiento conserva animaciones o efectos que contradicen el perfil." : "Performance still keeps animations or effects that conflict with the profile.")
                        : SiegeConfig.graphics.name(),
                performanceConflict ? Severity.WARNING : Severity.OK));

        SiegeClientProfile.Profile active = SiegeClientProfile.detect();
        SiegeClientProfile.Profile nearest = SiegeClientProfile.nearest();
        int drift = SiegeClientProfile.distance(nearest);
        int fit = SiegeClientProfile.fitPercent(nearest);
        entries.add(new Entry("PRF", spanish ? "COHERENCIA DE PERFIL" : "PROFILE COHERENCE",
                active == SiegeClientProfile.Profile.CUSTOM
                        ? (spanish ? "Personalizado: " : "Custom: ")
                            + drift + "/" + SiegeClientProfile.presetSettingCount()
                            + (spanish ? " ajustes difieren de " : " settings differ from ")
                            + SiegeClientProfile.label(nearest, spanish) + " (" + fit + "%)."
                        : (spanish ? "Coincidencia exacta con " : "Exact match with ")
                            + SiegeClientProfile.label(active, spanish) + ".",
                active == SiegeClientProfile.Profile.CUSTOM ? Severity.NOTICE : Severity.OK));

        entries.add(new Entry("INT", "INTEL",
                SiegeRuntimeStatus.intelLabel(spanish),
                IntelCatalog.total() > 0 ? Severity.OK : Severity.WARNING));

        entries.add(new Entry("BG", spanish ? "FONDO" : "BACKGROUND",
                SiegeRuntimeStatus.backgroundLabel(spanish), Severity.OK));

        entries.add(new Entry("ACC", spanish ? "ACCESIBILIDAD" : "ACCESSIBILITY",
                SiegeRuntimeStatus.accessibilityLabel(spanish), Severity.OK));

        return entries;
    }

    public static int errors(boolean spanish) {
        int count = 0;
        for (Entry entry : entries(spanish)) if (entry.severity() == Severity.ERROR) count++;
        return count;
    }

    public static int warnings(boolean spanish) {
        int count = 0;
        for (Entry entry : entries(spanish)) if (entry.severity() == Severity.WARNING) count++;
        return count;
    }

    public static int notices(boolean spanish) {
        int count = 0;
        for (Entry entry : entries(spanish)) if (entry.severity() == Severity.NOTICE) count++;
        return count;
    }

    public static int readiness() {
        int score = 100;
        for (Entry entry : entries(false)) {
            score -= switch (entry.severity()) {
                case ERROR -> 40;
                case WARNING -> 12;
                case NOTICE -> 3;
                case OK -> 0;
            };
        }
        return Math.max(0, Math.min(100, score));
    }

    public static int accent(Severity severity) {
        return switch (severity) {
            case OK -> SiegeTheme.GREEN;
            case NOTICE -> SiegeTheme.CYAN;
            case WARNING -> SiegeTheme.GOLD;
            case ERROR -> SiegeTheme.RED;
        };
    }
}

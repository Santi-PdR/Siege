package uy.santipdr.siege.client;

import java.util.ArrayList;
import java.util.List;

/** Live, side-effect-free diagnostic model. Repairs only run after explicit input. */
public final class SiegeDiagnosticReport {
    public enum Severity { OK, NOTICE, WARNING, ERROR }
    public enum Recovery {
        NONE,
        RESTORE_MUSIC_VOLUME,
        RESTORE_UI_VOLUME,
        DISABLE_INTERFERENCE,
        PERFORMANCE_SAFE,
        ALIGN_NEAREST_PROFILE,
        ENABLE_AUTO_CONTRAST
    }

    public record Entry(String code, String title, String detail, String impact,
                        String recommendation, Severity severity, Recovery recovery) { }

    private SiegeDiagnosticReport() { }

    public static List<Entry> entries(boolean spanish) {
        List<Entry> out = new ArrayList<>();

        if (!SiegeConfig.lastSaveSucceeded) out.add(e("CFG", spanish,
                "CONFIGURACIÓN", "CONFIGURATION",
                "El último guardado no pudo completarse.", "The latest save could not complete.",
                "Los cambios de presentación pueden perderse al cerrar.", "Presentation changes may be lost on exit.",
                "Revisá permisos y espacio disponible; no se sobrescribe un archivo corrupto automáticamente.",
                "Check permissions and free space; a corrupt file is never overwritten automatically.",
                Severity.ERROR, Recovery.NONE));
        else out.add(e("CFG", spanish, "CONFIGURACIÓN", "CONFIGURATION",
                "Lectura y guardado disponibles.", "Read and save paths are available.",
                "Las preferencias pueden persistir normalmente.", "Preferences can persist normally.",
                "Sin acción necesaria.", "No action required.", Severity.OK, Recovery.NONE));

        if (SiegeConfig.music && SiegeConfig.musicVolume == 0) out.add(e("MUS", spanish,
                "MÚSICA", "MUSIC", "La música está habilitada con volumen 0%.", "Music is enabled at 0% volume.",
                "El canal trabaja pero no produce salida audible.", "The channel remains active but produces no audible output.",
                "Restaurá un nivel audible o desactivá el canal.", "Restore an audible level or disable the channel.",
                Severity.WARNING, Recovery.RESTORE_MUSIC_VOLUME));
        else out.add(e("MUS", spanish, "MÚSICA", "MUSIC",
                SiegeRuntimeStatus.audioLabel(spanish), SiegeRuntimeStatus.audioLabel(false),
                "El canal de música responde a la configuración actual.", "The music channel follows the current configuration.",
                "Sin acción necesaria.", "No action required.", Severity.OK, Recovery.NONE));

        if (SiegeConfig.uiSounds && SiegeConfig.uiVolume == 0) out.add(e("SFX", spanish,
                "EFECTOS UI", "UI EFFECTS", "Los efectos están habilitados con volumen 0%.", "UI effects are enabled at 0% volume.",
                "Confirmaciones, avisos y navegación quedan mudos.", "Confirmations, warnings and navigation are silent.",
                "Restaurá un nivel audible o desactivá los efectos.", "Restore an audible level or disable effects.",
                Severity.NOTICE, Recovery.RESTORE_UI_VOLUME));
        else out.add(e("SFX", spanish, "EFECTOS UI", "UI EFFECTS",
                SiegeConfig.uiSounds ? "CANAL ACTIVO" : "CANAL DESACTIVADO",
                SiegeConfig.uiSounds ? "CHANNEL ACTIVE" : "CHANNEL OFF",
                "El estado coincide con la preferencia actual.", "State matches the current preference.",
                "Sin acción necesaria.", "No action required.", Severity.OK, Recovery.NONE));

        // Reduce Flashes is an override, not a conflict: the renderer deliberately
        // suppresses title interference while the safeguard is active. Reporting that
        // state as a warning made diagnostics contradict the actual runtime behavior.
        boolean interferenceConfigured = SiegeConfig.titleInterference && SiegeConfig.interferenceIntensity > 0;
        if (SiegeConfig.reduceFlashes && interferenceConfigured) out.add(e("VIS", spanish,
                "EFECTOS VISUALES", "VISUAL EFFECTS",
                "INTERFERENCIA SUPRIMIDA POR ACCESIBILIDAD", "INTERFERENCE SUPPRESSED BY ACCESSIBILITY",
                "Reducir destellos tiene prioridad y evita que la interferencia del título se dibuje.",
                "Reduce Flashes has priority and prevents title interference from being rendered.",
                "Sin acción necesaria; desactivá Reducir destellos sólo si querés recuperar el efecto.",
                "No action required; disable Reduce Flashes only if you want the effect back.",
                Severity.OK, Recovery.NONE));
        else if (SiegeConfig.titleInterference && SiegeConfig.interferenceIntensity == 0) out.add(e("VIS", spanish,
                "EFECTOS VISUALES", "VISUAL EFFECTS",
                "INTERFERENCIA ACTIVA CON INTENSIDAD 0%", "INTERFERENCE ENABLED AT 0% INTENSITY",
                "El interruptor está activo, pero el renderer no dibuja el efecto a intensidad cero.",
                "The toggle is enabled, but the renderer draws no effect at zero intensity.",
                "Subí la intensidad o desactivá el interruptor para que la configuración sea más clara.",
                "Raise the intensity or disable the toggle to make the configuration clearer.",
                Severity.NOTICE, Recovery.NONE));
        else out.add(e("VIS", spanish, "EFECTOS VISUALES", "VISUAL EFFECTS",
                "EFECTOS COHERENTES", "EFFECTS COHERENT",
                "No hay conflicto entre movimiento, destellos e interferencia.", "Motion, flash and interference settings do not conflict.",
                "Sin acción necesaria.", "No action required.", Severity.OK, Recovery.NONE));

        boolean heavyPerformance = SiegeConfig.graphics == SiegeConfig.Graphics.PERFORMANCE
                && (SiegeConfig.animatedBackgrounds || SiegeConfig.animatedIntel || SiegeConfig.menuEffects
                || SiegeConfig.scanlines || SiegeConfig.titleInterference);
        if (heavyPerformance) out.add(e("GPU", spanish, "RENDER", "RENDER",
                "El perfil de render es Rendimiento, pero siguen activos efectos costosos.",
                "Render profile is Performance while expensive effects remain enabled.",
                "El perfil pierde parte de su objetivo de reducir trabajo visual por frame.",
                "The profile loses part of its goal of reducing per-frame visual work.",
                "Aplicá la reparación de rendimiento para apagar únicamente los efectos ambientales.",
                "Apply the performance repair to disable ambient effects only.",
                Severity.WARNING, Recovery.PERFORMANCE_SAFE));
        else out.add(e("GPU", spanish, "RENDER", "RENDER",
                SiegeRuntimeStatus.renderBackend(), SiegeRuntimeStatus.renderBackend(),
                "Backend detectado: " + SiegeRuntimeStatus.renderBackend(),
                "Detected backend: " + SiegeRuntimeStatus.renderBackend(),
                "Sin acción necesaria.", "No action required.", Severity.OK, Recovery.NONE));

        SiegeClientProfile.Profile active = SiegeClientProfile.detect();
        if (active == SiegeClientProfile.Profile.CUSTOM) {
            SiegeClientProfile.Profile nearest = SiegeProfileMetrics.nearest();
            int fit = SiegeProfileMetrics.fitPercent(nearest);
            int distance = SiegeProfileMetrics.distance(nearest);
            int fields = SiegeProfileMetrics.fieldCount();
            out.add(e("PRF", spanish, "PERFIL", "PROFILE",
                    "PERSONALIZADO · MÁS CERCANO: " + SiegeClientProfile.label(nearest, true) + " " + fit + "%",
                    "CUSTOM · NEAREST: " + SiegeClientProfile.label(nearest, false) + " " + fit + "%",
                    "La mezcla manual difiere en " + distance + " de " + fields + " campos.",
                    "The manual mix differs in " + distance + " of " + fields + " fields.",
                    "Podés conservarla o alinear explícitamente al perfil más cercano.",
                    "Keep it or explicitly align to the nearest complete profile.",
                    Severity.NOTICE, Recovery.ALIGN_NEAREST_PROFILE));
        } else out.add(e("PRF", spanish, "PERFIL", "PROFILE",
                SiegeClientProfile.label(active, true) + " · 100%", SiegeClientProfile.label(active, false) + " · 100%",
                "Todos los campos del perfil coinciden con su contrato.", "Every profile field matches its contract.",
                "Sin acción necesaria.", "No action required.", Severity.OK, Recovery.NONE));

        if (!SiegeConfig.autoContrast && !SiegeConfig.highContrast) out.add(e("CON", spanish,
                "CONTRASTE", "CONTRAST", "Contraste automático desactivado.", "Automatic contrast is disabled.",
                "Fondos muy claros pueden reducir legibilidad según la escena.", "Bright backgrounds may reduce readability depending on the scene.",
                "Activá contraste automático para imponer mínimos de lectura sin cambiar el fondo.",
                "Enable automatic contrast to enforce readability minimums without changing the background.",
                Severity.NOTICE, Recovery.ENABLE_AUTO_CONTRAST));
        else out.add(e("CON", spanish, "CONTRASTE", "CONTRAST",
                SiegeConfig.highContrast ? "ALTO CONTRASTE" : "CONTRASTE AUTOMÁTICO",
                SiegeConfig.highContrast ? "HIGH CONTRAST" : "AUTO CONTRAST",
                "La capa de lectura mantiene mínimos de contraste.", "The reading layer maintains contrast minimums.",
                "Sin acción necesaria.", "No action required.", Severity.OK, Recovery.NONE));

        out.add(e("INT", spanish, "INTEL", "INTEL", SiegeRuntimeStatus.intelLabel(true), SiegeRuntimeStatus.intelLabel(false),
                "El catálogo y sus categorías están disponibles para consulta.", "The catalog and its categories are available for review.",
                "Sin acción necesaria.", "No action required.", Severity.OK, Recovery.NONE));

        int sceneIndex = SiegeBackgrounds.currentIndex(System.currentTimeMillis());
        boolean comfortPinnedAnomaly = SiegeConfig.selectedScene >= 0 && SiegeBackgrounds.isAnomaly(sceneIndex)
                && (SiegeConfig.reducedMotion || SiegeConfig.reduceFlashes);
        if (comfortPinnedAnomaly) out.add(e("BG", spanish, "FONDOS", "BACKGROUNDS",
                SiegeRuntimeStatus.backgroundLabel(true), SiegeRuntimeStatus.backgroundLabel(false),
                "La anomalía está fijada manualmente; los modos de confort solo la excluyen de la rotación automática.",
                "The anomaly is manually pinned; comfort modes only exclude it from automatic rotation.",
                "Elegí otra escena o reanudá la rotación si querés que el perfil de confort la evite.",
                "Choose another scene or resume rotation if you want the comfort profile to avoid it.",
                Severity.NOTICE, Recovery.NONE));
        else out.add(e("BG", spanish, "FONDOS", "BACKGROUNDS", SiegeRuntimeStatus.backgroundLabel(true), SiegeRuntimeStatus.backgroundLabel(false),
                "Oscuridad efectiva: " + SiegeBackgrounds.effectiveBackgroundDarkness(sceneIndex) + "% · panel " + SiegeBackgrounds.effectivePanelDarkness() + "%",
                "Effective darkness: " + SiegeBackgrounds.effectiveBackgroundDarkness(sceneIndex) + "% · panel " + SiegeBackgrounds.effectivePanelDarkness() + "%",
                "Sin acción necesaria.", "No action required.", Severity.OK, Recovery.NONE));

        out.add(e("ACC", spanish, "ACCESIBILIDAD", "ACCESSIBILITY", SiegeRuntimeStatus.accessibilityLabel(true), SiegeRuntimeStatus.accessibilityLabel(false),
                "Las garantías de destellos tienen prioridad sobre efectos decorativos.", "Flash safeguards take priority over decorative effects.",
                "Sin acción necesaria.", "No action required.", Severity.OK, Recovery.NONE));
        return List.copyOf(out);
    }

    public static boolean repair(Entry entry) {
        if (entry == null || entry.recovery() == Recovery.NONE) return false;
        switch (entry.recovery()) {
            case RESTORE_MUSIC_VOLUME -> SiegeConfig.musicVolume = 55;
            case RESTORE_UI_VOLUME -> SiegeConfig.uiVolume = 60;
            case DISABLE_INTERFERENCE -> {
                SiegeConfig.titleInterference = false;
                SiegeConfig.interferenceIntensity = 0;
            }
            case PERFORMANCE_SAFE -> {
                SiegeConfig.menuEffects = false;
                SiegeConfig.animatedBackgrounds = false;
                SiegeConfig.animatedIntel = false;
                SiegeConfig.scanlines = false;
                SiegeConfig.scanlineIntensity = 0;
                SiegeConfig.titleInterference = false;
                SiegeConfig.interferenceIntensity = 0;
                SiegeConfig.reducedMotion = true;
                SiegeConfig.reduceFlashes = true;
            }
            case ALIGN_NEAREST_PROFILE -> SiegeProfileSpec.apply(SiegeProfileMetrics.nearest());
            case ENABLE_AUTO_CONTRAST -> SiegeConfig.autoContrast = true;
            case NONE -> { return false; }
        }
        SiegeConfig.save();
        return true;
    }

    public static int errors(boolean spanish) { return count(entries(spanish), Severity.ERROR); }
    public static int warnings(boolean spanish) { return count(entries(spanish), Severity.WARNING); }
    public static int notices(boolean spanish) { return count(entries(spanish), Severity.NOTICE); }
    public static int operational(boolean spanish) { return count(entries(spanish), Severity.OK); }

    public static int readiness() {
        int penalty = 0;
        for (Entry entry : entries(false)) penalty += switch (entry.severity()) {
            case ERROR -> 24;
            case WARNING -> 10;
            case NOTICE -> 3;
            case OK -> 0;
        };
        return Math.max(0, Math.min(100, 100 - penalty));
    }

    public static int accent(Severity severity) {
        return switch (severity) {
            case OK -> SiegeTheme.GREEN;
            case NOTICE -> SiegeTheme.CYAN;
            case WARNING -> SiegeTheme.GOLD;
            case ERROR -> SiegeTheme.RED;
        };
    }

    public static String priorityLabel(Severity severity, boolean spanish) {
        return switch (severity) {
            case ERROR -> spanish ? "CRÍTICO" : "CRITICAL";
            case WARNING, NOTICE -> spanish ? "ATENCIÓN" : "ATTENTION";
            case OK -> spanish ? "OPERATIVO" : "OPERATIONAL";
        };
    }

    private static int count(List<Entry> entries, Severity severity) {
        return (int)entries.stream().filter(entry -> entry.severity() == severity).count();
    }

    private static Entry e(String code, boolean spanish, String titleEs, String titleEn,
                           String detailEs, String detailEn, String impactEs, String impactEn,
                           String recommendationEs, String recommendationEn,
                           Severity severity, Recovery recovery) {
        return new Entry(code, spanish ? titleEs : titleEn, spanish ? detailEs : detailEn,
                spanish ? impactEs : impactEn, spanish ? recommendationEs : recommendationEn, severity, recovery);
    }
}

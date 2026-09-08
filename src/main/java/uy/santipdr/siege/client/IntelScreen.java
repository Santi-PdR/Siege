package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import uy.santipdr.siege.SiegeMod;

import java.util.ArrayList;
import java.util.List;

public final class IntelScreen extends Screen {
    private static final String ALL = "ALL";
    private static final List<String> CATEGORIES = List.of(ALL, "UNIT", "ADVANCED", "TANK", "BOSS", "ELITE", "SUPER-UNIT");
    private static final List<IntelEntry> FILES = List.of(
            file("HU-001", "INFANTRY", "UNIT", 1, "100", "infantry",
                    "Desconocido", "FN SCAR + armadura táctica", "Esqueleto / Pillager", "ACTIVO",
                    "Unidad común y aparentemente débil, pero capaz de coordinar estrategias mediante su avanzada inteligencia artificial.", "No subestimar grupos. Romper su formación antes de avanzar.",
                    "Unknown", "FN SCAR + tactical armour", "Skeleton / Pillager", "ACTIVE",
                    "A common and seemingly weak unit capable of coordinating strategies through its advanced artificial intelligence.", "Do not underestimate groups. Break their formation before advancing."),
            file("HU-002", "SHIELDER", "UNIT", 2, "150", "shielder",
                    "Desconocido", "Escopeta + doble escudo", "Emboscador", "ACTIVO", "Unidad de asalto que bloquea ataques y organiza emboscadas cuando opera junto a otros Shielders.", "Evitar cuerpo a cuerpo y disparos frontales. Atacar desde los flancos.",
                    "Unknown", "Shotgun + dual shields", "Ambusher", "ACTIVE", "Assault unit that blocks attacks and organises ambushes when operating with other Shielders.", "Avoid close combat and frontal fire. Attack from the flanks."),
            file("SOP-001", "SABOTEUR", "UNIT", 4, "120", "saboteur",
                    "Desconocido", "Dos pistolas + C4 + saboteador electrónico", "C4 normal / C4 nuclear", "HOSTIL", "Especialista invisible en ataques a bases. Puede adherir cargas al jugador y neutralizar torretas Sentinel y aparatos electrónicos.", "Localizarlo antes de que revele su posición. Prioridad máxima cerca de una base.",
                    "Unknown", "Dual pistols + C4 + electronic disruptor", "Standard C4 / nuclear C4", "HOSTILE", "Invisible base-assault specialist. It can attach charges to players and disable Sentinel turrets and electronic devices.", "Locate it before it reveals its position. Maximum priority near a base."),
            file("SOP-002", "STALKER", "UNIT", 3, "100", "stalker",
                    "Desconocido", "Rifle subsónico + camuflaje + GPS", "Escuadras de infiltración", "HOSTIL", "Espía que aparece dentro de bombas de humo, observa bases y puede colocar discretamente un rastreador GPS.", "Vigilar humo repentino y comprobar rastreadores después del contacto.",
                    "Unknown", "Subsonic rifle + camouflage + GPS", "Infiltration squads", "HOSTILE", "Spy that appears inside smoke clouds, observes bases and can discreetly plant a GPS tracker.", "Watch for sudden smoke and check for trackers after contact."),
            file("MECH-001", "NATZUKA", "UNIT", 3, "105", "natzuka",
                    "Desconocido", "Armas de fuego + carga explosiva", "Cuadrúpedo de bajo costo", "ACTIVO", "Unidad mecánica para terrenos difíciles, fabricada en masa para combate directo y neutralización suicida de amenazas.", "Destruir a distancia. No permitir que complete su aproximación.",
                    "Unknown", "Firearms + explosive charge", "Low-cost quadruped", "ACTIVE", "Mechanical unit for difficult terrain, mass-produced for direct combat and suicidal threat neutralisation.", "Destroy it at range. Do not allow it to complete its approach."),
            file("HU-003", "SNIPER", "UNIT", 4, "150", "sniper",
                    "República de Nusia", "Rifle de precisión + radar + radio", "Retaguardia", "HOSTIL", "Francotirador que rastrea, se agacha y prepara un proyectil de alta velocidad señalado por un láser rojo. Cambia de posición si es descubierto.", "Romper línea de visión al ver el láser. Presionarlo para obligarlo a moverse.",
                    "Republic of Nusia", "Precision rifle + radar + radio", "Rear guard", "HOSTILE", "Sniper that tracks targets, crouches and prepares a high-velocity round marked by a red laser. It relocates when discovered.", "Break line of sight when the laser appears. Pressure it into moving."),
            file("HU-004", "GRENADIER", "UNIT", 4, "350", "grenadier",
                    "República de Nusia", "Lanzagranadas multifunción", "Gas / explosivo", "HOSTIL", "Unidad de retaguardia que analiza el combate, usa gas lacrimógeno y puede viajar entre explosiones de humo. Sus acrobacias pueden triplicar el daño.", "Separarse, abandonar el gas y evitar trayectorias previsibles.",
                    "Republic of Nusia", "Multifunction grenade launcher", "Gas / explosive", "HOSTILE", "Rear-line unit that analyses combat, deploys tear gas and can move between smoke bursts. Its acrobatics can triple its damage.", "Spread out, leave the gas and avoid predictable paths."),
            file("HU-005", "GUNNER", "UNIT", 5, "600", "gunner",
                    "República de Nusia", "Ametralladora ligera + reserva pesada", "Tanque común", "HOSTIL", "Unidad pesada que se inmoviliza para descargar ráfagas extremadamente rápidas durante periodos prolongados.", "Usar cobertura sólida y atacar durante sus cambios de posición.",
                    "Republic of Nusia", "Light machine gun + heavy reserve", "Common tank", "HOSTILE", "Heavy unit that anchors itself to unleash extremely rapid fire for extended periods.", "Use solid cover and attack while it changes position."),
            file("HU-006", "JETPACKER", "UNIT", 4, "150", "jetpacker",
                    "Desconocido", "Jetpack + RPG + cámara binocular", "Aéreo / terrestre", "HOSTIL", "Unidad veloz que se impulsa hasta una posición calculada para lanzar misiles. Si falla, activa supervelocidad y supersalto terrestre.", "Seguir el rastro amarillo, dispersarse y aprovechar que los misiles no son teledirigidos.",
                    "Unknown", "Jetpack + RPG + binocular camera", "Airborne / ground", "HOSTILE", "Fast unit that boosts into a calculated missile-launch position. If it misses, it activates ground super-speed and super-jump.", "Track the yellow trail, spread out and exploit its unguided missiles."),
            file("HU-007", "PATRIOT", "UNIT", 4, "350", "patriot",
                    "Brasil / sin confirmar", "Fusil militar no identificado", "Información insuficiente", "ARCHIVO INCOMPLETO", "Los fragmentos recuperados lo relacionan con contingentes Noob de defensa nacional. Se cree que estabiliza frentes al borde del colapso y desaparece antes de ser interrogado.", "Capacidades y lealtad sin confirmar. No asumir que pertenece a fuerzas aliadas.",
                    "Brazil / unconfirmed", "Unidentified military rifle", "Insufficient information", "INCOMPLETE FILE", "Recovered fragments link it to national-defence Noob contingents. It allegedly stabilises collapsing fronts and disappears before interrogation.", "Capabilities and allegiance unconfirmed. Do not assume it is allied."),

            file("ADV-001", "SPECIALIST", "ADVANCED", 2, "500", "specialist",
                    "República de Nusia", "C4 + bombas nucleares + teletransportación", "Planificador / líder de escuadra", "HOSTIL", "Unidad autoconsciente con IA planificadora avanzada. Se infiltra invisible, rodea estructuras con C4, sella rutas de escape y asume el mando de grupos numerosos.", "No permanecer dentro de estructuras ni pozos tras detectarlo. Romper su planificación antes de que distribuya órdenes.",
                    "Republic of Nusia", "C4 + nuclear bombs + teleportation", "Planner / squad leader", "HOSTILE", "Self-aware unit with advanced planning AI. It infiltrates invisibly, surrounds structures with C4, seals escape routes and commands large groups.", "Do not remain inside structures or pits after detection. Disrupt its plan before it distributes orders."),
            file("ADV-002", "DEMOMAN", "ADVANCED", 2, "NAN", "demoman",
                    "República de Nusia", "Rifle de asalto + dinamita corporal", "Kamikaze avanzado", "HOSTIL", "Unidad silenciosa de demolición suicida. Su detonación tarda más que la de un Kamikaze común, pero produce una explosión mucho mayor y más letal.", "Identificarlo por su carga roja y eliminarlo a máxima distancia. Nunca permitir que complete la aproximación.",
                    "Republic of Nusia", "Assault rifle + body-mounted dynamite", "Advanced kamikaze", "HOSTILE", "Silent suicidal demolition unit. Its detonation takes longer than a common Kamikaze's, but produces a much larger and deadlier blast.", "Identify its red payload and eliminate it at maximum range. Never allow it to complete its approach."),
            file("ADV-003", "ARTILLER", "ADVANCED", 2, "500", "artiller",
                    "República de Nusia", "SMG + Radio Striker", "Flame Strike / misiles / nuclear", "HOSTIL", "Especialista en bombardeo encubierto que fija una posición y solicita ataques mediante radio: impactos incendiarios, misiles de varios tamaños y bombas nucleares.", "Localizar la señal y forzarlo a desplazarse antes de que complete la solicitud de ataque.",
                    "Republic of Nusia", "SMG + Striker Radio", "Flame Strike / missiles / nuclear", "HOSTILE", "Covert bombardment specialist that holds a position and calls attacks by radio: incendiary strikes, missiles of several sizes and nuclear bombs.", "Locate the signal and force it to relocate before it completes the strike request."),
            file("ADV-004", "CLOAKER", "ADVANCED", 2, "150", "cloaker",
                    "República de Nusia", "Exoesqueleto + visión nocturna", "Cazador de alta velocidad", "HOSTIL", "Cazador desarmado capaz de alcanzar velocidades extremas y saltar grandes alturas. Emite pulsos de radar antes de cargar y ejecuta un dropkick de 100.000 de daño que ignora armadura.", "El chillido confirma la carga. Separarse y cortar su trayectoria inmediatamente; el impacto es letal en cualquier dificultad.",
                    "Republic of Nusia", "Exoskeleton + night vision", "High-speed hunter", "HOSTILE", "Unarmed hunter capable of extreme speed and high jumps. It emits radar pulses before charging and performs a 100,000-damage dropkick that ignores armour.", "The screech confirms the charge. Split up and break its path immediately; impact is lethal on every difficulty."),
            file("ADV-005", "APU", "ADVANCED", 2, "120,000", "apu",
                    "República de Nusia", "Mech de californita + lanzallamas", "Defensa reducida en agua", "HOSTIL", "Piloto protegido por un exoesqueleto de californita. A corta distancia quema sin interrupción, ignora los I-frames y vuelve inútiles los tótems. Posee 0,75 de defensa, ajustada por dificultad.", "Mantenerlo lejos y atraerlo al agua. No confiar en tótems ni en invulnerabilidad temporal.",
                    "Republic of Nusia", "Californite mech + flamethrower", "Reduced defence in water", "HOSTILE", "Protected pilot inside a Californite exoskeleton. At close range it burns continuously, ignores I-frames and renders totems useless. It has 0.75 defence, adjusted by difficulty.", "Keep it at range and lure it into water. Do not rely on totems or temporary invulnerability."),
            file("ADV-006", "MISSILER", "ADVANCED", 1, "250", "missiler",
                    "República de Nusia", "PVS-14 + Javelin + 8 DEF", "Francotirador teledirigido", "HOSTIL", "Evolución sigilosa del Sniper. Se desplaza invisible, elige posiciones lejanas y lanza misiles guiados. No utiliza láser: un destello amarillo concede cinco segundos para escapar.", "Al ver el destello amarillo, abandonar el área. Acercarse obliga al Missiler a desaparecer y reposicionarse.",
                    "Republic of Nusia", "PVS-14 + Javelin + 8 DEF", "Guided-missile sniper", "HOSTILE", "Stealth evolution of the Sniper. It moves invisibly, selects distant positions and launches guided missiles. It uses no laser: a yellow flash gives five seconds to escape.", "Leave the area when the yellow flash appears. Closing in forces the Missiler to vanish and reposition."));

    private final Screen parent;
    private String category = ALL;
    private int selected;
    private int listOffset;

    public IntelScreen(Screen parent) {
        super(Component.translatable("siege.intel.title"));
        this.parent = parent;
    }

    @Override protected void init() {
        SiegeUiSounds.resetHover();
        int buttonH = height < 320 ? 20 : 24;
        addRenderableWidget(Button.builder(Component.literal("< ").append(Component.translatable("siege.intel.return")), b -> onClose())
                .bounds(10, 10, Math.min(126, width / 3), buttonH).build());
        if (compact()) {
            int navW = Math.min(88, Math.max(56, (width - 174) / 2));
            addRenderableWidget(Button.builder(Component.literal("<"), b -> move(-1)).bounds(width / 2 - navW - 3, 10, navW, buttonH).build());
            addRenderableWidget(Button.builder(Component.literal(">"), b -> move(1)).bounds(width / 2 + 3, 10, navW, buttonH).build());
            addRenderableWidget(Button.builder(Component.literal(categoryLabel(category)), b -> cycleCategory())
                    .bounds(width - Math.min(126, width / 3) - 10, 10, Math.min(126, width / 3), buttonH).build());
            return;
        }
        int filterW = 94;
        for (int i = 0; i < CATEGORIES.size(); i++) {
            String value = CATEGORIES.get(i);
            int row = i / 3, col = i % 3;
            addRenderableWidget(Button.builder(Component.literal(categoryLabel(value)), b -> setCategory(value))
                    .bounds(28 + col * (filterW + 7), 78 + row * 24, filterW, 20).build());
        }
        rebuildFileButtons();
    }

    private void rebuildFileButtons() {
        children().removeIf(w -> w instanceof Button b && b.getY() >= 130);
        List<IntelEntry> files = filtered();
        int visible = Math.max(1, (height - 154) / 28);
        listOffset = Math.max(0, Math.min(listOffset, Math.max(0, files.size() - visible)));
        int y = 136;
        for (int i = listOffset; i < files.size() && i < listOffset + visible; i++) {
            int index = i;
            IntelEntry entry = files.get(i);
            addRenderableWidget(Button.builder(Component.literal(entry.code() + "  " + entry.name()), b -> {
                SiegeUiSounds.click(); selected = index;
            }).bounds(28, y, 320, 24).build());
            y += 28;
        }
    }

    private void setCategory(String value) {
        SiegeUiSounds.click(); category = value; selected = 0; listOffset = 0; rebuildFileButtons();
    }

    private void cycleCategory() {
        SiegeUiSounds.click();
        category = CATEGORIES.get((CATEGORIES.indexOf(category) + 1) % CATEGORIES.size());
        selected = 0;
    }

    private void move(int delta) {
        SiegeUiSounds.click();
        int size = filtered().size();
        if (size > 0) selected = (selected + delta + size) % size;
    }

    private List<IntelEntry> filtered() {
        if (ALL.equals(category)) return FILES;
        List<IntelEntry> result = new ArrayList<>();
        for (IntelEntry entry : FILES) if (entry.category().equals(category)) result.add(entry);
        return result;
    }

    private boolean compact() { return width < 760 || height < 430; }
    private boolean spanish() { return minecraft != null && minecraft.getLanguageManager().getSelected().startsWith("es_"); }
    private String categoryLabel(String value) {
        if (!spanish()) return value;
        return switch (value) { case ALL -> "TODOS"; case "UNIT" -> "UNIDADES"; case "ADVANCED" -> "AVANZADAS"; case "TANK" -> "TANQUES"; case "BOSS" -> "JEFES"; case "ELITE" -> "ÉLITES"; case "SUPER-UNIT" -> "SUPERUNIDAD"; default -> value; };
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!compact() && mouseX < 368 && mouseY >= 130) {
            int visible = Math.max(1, (height - 154) / 28);
            int max = Math.max(0, filtered().size() - visible);
            int next = Math.max(0, Math.min(max, listOffset + (delta < 0 ? 1 : -1)));
            if (next != listOffset) { listOffset = next; rebuildFileButtons(); }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        if (compact()) { renderCompact(g); super.render(g, mouseX, mouseY, partialTick); SiegeUiSounds.updateHover(children()); return; }
        g.fill(0, 0, width, 66, 0xEB050505);
        g.fill(0, 66, 368, height, 0xD927252A);
        g.fill(384, 80, width - 18, height - 28, 0xE6050505);
        g.drawCenteredString(font, title, width / 2, 22, 0xFFFF5555);
        List<IntelEntry> files = filtered();
        if (files.isEmpty()) { g.drawCenteredString(font, spanish() ? "SIN EXPEDIENTES" : "NO FILES", (width + 368) / 2, height / 2, 0xFF888888); super.render(g, mouseX, mouseY, partialTick); return; }
        selected = Math.min(selected, files.size() - 1);
        renderFile(g, files.get(selected), 406, 100, width - 448, false);
        g.drawString(font, String.format("%02d/%02d", selected + 1, files.size()), width - 52, 101, 0xFF777777, false);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    private void renderCompact(GuiGraphics g) {
        List<IntelEntry> files = filtered();
        if (files.isEmpty()) return;
        selected = Math.min(selected, files.size() - 1);
        int margin = 12, top = 42;
        g.fill(6, top - 5, width - 6, height - 7, 0xE6050505);
        renderFile(g, files.get(selected), margin, top, width - margin * 2, true);
        g.drawString(font, String.format("%02d/%02d", selected + 1, files.size()), width - 45, top + 17, 0xFF777777, false);
    }

    private void renderFile(GuiGraphics g, IntelEntry e, int x, int y, int availableW, boolean compact) {
        IntelEntry.IntelText t = e.text(spanish());
        int accent = e.threat() >= 4 ? 0xFFFFB020 : e.category().equals("ADVANCED") ? 0xFFFF5555 : 0xFF56C8FF;
        g.drawString(font, (compact ? "" : "FILE: ") + e.code() + " / " + e.name(), x, y, 0xFFF4F4F4, false);
        g.drawString(font, stars(e.threat()) + "  HP " + e.hp() + "  " + categoryLabel(e.category()), x, y + 16, accent, false);
        int imageW = compact ? Math.min(168, Math.max(104, width / 3)) : Math.min(240, Math.max(180, availableW / 2));
        int imageH = imageW * 9 / 16;
        ResourceLocation portrait = new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/intel/" + e.image() + ".png");
        g.blit(portrait, x, y + 34, imageW, imageH, 0, 0, 640, 360, 640, 360);
        g.fill(x, y + 34, x + imageW, y + 36, accent);
        int tx = x + imageW + 10;
        int textW = Math.max(72, availableW - imageW - 10);
        drawWrapped(g, label("ORIGEN", "ORIGIN") + ": " + t.origin(), tx, y + 34, textW, 0xFFD0D0D0);
        drawWrapped(g, label("ARMAMENTO", "ARMAMENT") + ": " + t.armament(), tx, y + 58, textW, 0xFFD0D0D0);
        drawWrapped(g, label("VARIANTES", "VARIANTS") + ": " + t.variants(), tx, y + 94, textW, 0xFFD0D0D0);
        int descY = Math.max(y + 34 + imageH + 8, y + 126);
        drawWrapped(g, t.description(), x, descY, availableW, 0xFFC8C8C8);
        if (!compact || height - descY > 58) drawWrapped(g, label("ADVERTENCIA", "ADVISORY") + ": " + t.advisory(), x, descY + 48, availableW, 0xFFFFB020);
    }

    private String label(String es, String en) { return spanish() ? es : en; }
    private String stars(int count) { return "★".repeat(Math.max(0, count)) + "☆".repeat(Math.max(0, 5 - count)); }
    private static IntelEntry file(String code, String name, String category, int threat, String hp, String image,
                                   String esOrigin, String esArmament, String esVariants, String esStatus, String esDescription, String esAdvisory,
                                   String enOrigin, String enArmament, String enVariants, String enStatus, String enDescription, String enAdvisory) {
        return new IntelEntry(code, name, category, threat, hp, image,
                new IntelEntry.IntelText(esOrigin, esArmament, esVariants, esStatus, esDescription, esAdvisory),
                new IntelEntry.IntelText(enOrigin, enArmament, enVariants, enStatus, enDescription, enAdvisory));
    }
    private void drawWrapped(GuiGraphics g, String value, int x, int y, int maxWidth, int color) {
        for (var line : font.split(Component.literal(value), maxWidth)) { g.drawString(font, line, x, y, color, false); y += 12; }
    }
    @Override public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
}

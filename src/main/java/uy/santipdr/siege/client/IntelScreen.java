package uy.santipdr.siege.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import uy.santipdr.siege.SiegeMod;

import java.util.List;

public final class IntelScreen extends Screen {
    private static final List<IntelEntry> FILES = List.of(
            file("HU-001", "INFANTRY", "UNIT", 1, 100, "Desconocido", "infantry",
                    "FN SCAR + armadura táctica", "Esqueleto / Pillager", "ACTIVO",
                    "Unidad común y aparentemente débil, pero capaz de coordinar estrategias mediante su avanzada inteligencia artificial.",
                    "No subestimar grupos. Romper su formación antes de avanzar."),
            file("HU-002", "SHIELDER", "UNIT", 2, 150, "Desconocido", "shielder",
                    "Escopeta + doble escudo", "Emboscador", "ACTIVO",
                    "Unidad de asalto que bloquea ataques y organiza emboscadas cuando opera junto a otros Shielders.",
                    "Evitar cuerpo a cuerpo y disparos frontales. Atacar desde los flancos."),
            file("SOP-001", "SABOTEUR", "UNIT", 4, 120, "Desconocido", "saboteur",
                    "Dos pistolas + C4 + saboteador electrónico", "C4 normal / C4 nuclear", "HOSTIL",
                    "Especialista invisible en ataques a bases. Puede adherir cargas al jugador y neutralizar torretas Sentinel y aparatos electrónicos.",
                    "Localizarlo antes de que revele su posición. Prioridad máxima cerca de una base."),
            file("SOP-002", "STALKER", "UNIT", 3, 100, "Desconocido", "stalker",
                    "Rifle subsónico + camuflaje + GPS", "Escuadras de infiltración", "HOSTIL",
                    "Espía que aparece dentro de bombas de humo, observa bases y puede colocar discretamente un rastreador GPS.",
                    "Vigilar humo repentino y comprobar rastreadores después del contacto."),
            file("MECH-001", "NATZUKA", "UNIT", 3, 105, "Desconocido", "natzuka",
                    "Armas de fuego + carga explosiva", "Cuadrúpedo de bajo costo", "ACTIVO",
                    "Unidad mecánica para terrenos difíciles, fabricada en masa para combate directo y neutralización suicida de amenazas.",
                    "Destruir a distancia. No permitir que complete su aproximación."),
            file("HU-003", "SNIPER", "UNIT", 4, 150, "República de Nusia", "sniper",
                    "Rifle de precisión + radar + radio", "Retaguardia", "HOSTIL",
                    "Francotirador que rastrea, se agacha y prepara un proyectil de alta velocidad señalado por un láser rojo. Cambia de posición si es descubierto.",
                    "Romper línea de visión al ver el láser. Presionarlo para obligarlo a moverse."),
            file("HU-004", "GRENADIER", "UNIT", 4, 350, "República de Nusia", "grenadier",
                    "Lanzagranadas multifunción", "Gas / explosivo", "HOSTIL",
                    "Unidad de retaguardia que analiza el combate, usa gas lacrimógeno y puede viajar entre explosiones de humo. Sus acrobacias pueden triplicar el daño.",
                    "Separarse, abandonar el gas y evitar trayectorias previsibles."),
            file("HU-005", "GUNNER", "UNIT", 5, 600, "República de Nusia", "gunner",
                    "Ametralladora ligera + reserva pesada", "Tanque común", "HOSTIL",
                    "Unidad pesada que se inmoviliza para descargar ráfagas extremadamente rápidas durante periodos prolongados.",
                    "Usar cobertura sólida y atacar durante sus cambios de posición."),
            file("HU-006", "JETPACKER", "UNIT", 4, 150, "Desconocido", "jetpacker",
                    "Jetpack + RPG + cámara binocular", "Aéreo / terrestre", "HOSTIL",
                    "Unidad veloz que se impulsa hasta una posición calculada para lanzar misiles. Si falla, activa supervelocidad y supersalto terrestre.",
                    "Seguir el rastro amarillo, dispersarse y aprovechar que los misiles no son teledirigidos."),
            file("HU-007", "PATRIOT", "UNIT", 4, 350, "Brasil / sin confirmar", "patriot",
                    "Fusil militar no identificado", "Información insuficiente", "ARCHIVO INCOMPLETO",
                    "Los fragmentos recuperados lo relacionan con contingentes Noob de defensa nacional. Se cree que aparece cuando una línea de combate está a punto de colapsar, estabiliza el frente y desaparece antes de poder ser interrogado. El resto del expediente fue eliminado.",
                    "Capacidades y lealtad sin confirmar. Mantener observación y evitar asumir que pertenece a fuerzas aliadas."));

    private final Screen parent;
    private int selected;

    public IntelScreen(Screen parent) {
        super(Component.translatable("siege.intel.title"));
        this.parent = parent;
    }

    @Override protected void init() {
        SiegeUiSounds.resetHover();
        int buttonH = height < 320 ? 20 : 24;
        addRenderableWidget(Button.builder(Component.literal("< ").append(Component.translatable("siege.intel.return")),
                b -> onClose()).bounds(10, 10, Math.min(126, width / 3), buttonH).build());
        if (compact()) {
            int navW = Math.min(92, Math.max(62, (width - 40) / 3));
            addRenderableWidget(Button.builder(Component.literal("< ").append(Component.translatable("siege.intel.previous")), b -> {
                SiegeUiSounds.click(); selected = (selected - 1 + FILES.size()) % FILES.size();
            }).bounds(width / 2 - navW - 4, 10, navW, buttonH).build());
            addRenderableWidget(Button.builder(Component.translatable("siege.intel.next").append(" >"), b -> {
                SiegeUiSounds.click(); selected = (selected + 1) % FILES.size();
            }).bounds(width / 2 + 4, 10, navW, buttonH).build());
            return;
        }
        int y = 132;
        for (int i = 0; i < FILES.size() && y + 24 < height - 12; i++) {
            int index = i;
            addRenderableWidget(Button.builder(Component.literal(FILES.get(i).code() + "  " + FILES.get(i).name()),
                    b -> { SiegeUiSounds.click(); selected = index; }).bounds(28, y, 320, 24).build());
            y += 28;
        }
    }

    private boolean compact() { return width < 760 || height < 430; }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        SiegeMusic.ensurePlaying();
        SiegeBackgrounds.render(g, width, height, System.currentTimeMillis());
        if (compact()) {
            renderCompact(g);
            super.render(g, mouseX, mouseY, partialTick);
            SiegeUiSounds.updateHover(children());
            return;
        }
        g.fill(0, 0, width, 66, 0xEB050505);
        g.fill(0, 66, 368, height, 0xD927252A);
        g.fill(384, 80, width - 18, height - 28, 0xE6050505);
        g.drawCenteredString(font, title, width / 2, 22, 0xFFFF5555);
        g.drawString(font, "// FILTERS", 28, 82, 0xFF77777E, false);
        g.drawString(font, "[ ALL ] [ UNITS ] [ ADVANCED ] [ TANKS ]", 28, 96, 0xFFB8B8BE, false);
        g.drawString(font, "[ BOSSES ] [ ELITES ] [ SUPER-UNIT ]", 28, 108, 0xFFB8B8BE, false);
        IntelEntry e = FILES.get(selected);
        int x = 406, y = 100;
        int accent = e.threat() >= 4 ? 0xFFFFB020 : 0xFF56C8FF;
        g.drawString(font, "FILE: " + e.code() + " / " + e.name(), x, y, 0xFFF4F4F4, false);
        g.fill(x, y + 18, width - 42, y + 20, accent);
        g.drawString(font, "THREAT: " + stars(e.threat()) + "   HP: " + e.hp() + "   CATEGORY: " + e.category(), x, y + 34, accent, false);
        ResourceLocation portrait = new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/intel/" + e.image() + ".png");
        g.blit(portrait, x, y + 54, 240, 135, 0, 0, 640, 360, 640, 360);
        g.fill(x, y + 54, x + 240, y + 56, accent);
        g.fill(x, y + 187, x + 240, y + 189, accent);
        int tx = x + 258;
        g.drawString(font, "ORIGEN: " + e.origin(), tx, y + 56, 0xFFD0D0D0, false);
        drawWrapped(g, "ARMAMENTO: " + e.armament(), tx, y + 74, width - tx - 42, 0xFFD0D0D0);
        drawWrapped(g, "VARIANTES: " + e.variants(), tx, y + 106, width - tx - 42, 0xFFD0D0D0);
        g.drawString(font, "ESTADO: " + e.status(), tx, y + 138, 0xFF63E083, false);
        drawWrapped(g, e.description(), x, y + 210, width - x - 42, 0xFFC8C8C8);
        drawWrapped(g, "ADVERTENCIA: " + e.advisory(), x, y + 260, width - x - 42, 0xFFFFB020);
        g.drawString(font, "FILE " + String.format("%02d/%02d", selected + 1, FILES.size()), width - 105, 101, 0xFF777777, false);
        g.drawCenteredString(font, "// CLICK FILE // ESC TO RETURN", width / 2, height - 17, 0xFF66666B);
        super.render(g, mouseX, mouseY, partialTick);
        SiegeUiSounds.updateHover(children());
    }

    private void renderCompact(GuiGraphics g) {
        IntelEntry e = FILES.get(selected);
        int accent = e.threat() >= 4 ? 0xFFFFB020 : 0xFF56C8FF;
        int margin = 12, top = 42;
        g.fill(6, top - 5, width - 6, height - 7, 0xE6050505);
        g.drawCenteredString(font, title, width / 2, top, 0xFFFF5555);
        g.drawString(font, e.code() + " / " + e.name(), margin, top + 16, 0xFFF4F4F4, false);
        g.drawString(font, stars(e.threat()) + "  HP " + e.hp(), margin, top + 29, accent, false);
        int imageW = Math.min(168, Math.max(104, width / 3));
        int imageH = imageW * 9 / 16;
        ResourceLocation portrait = new ResourceLocation(SiegeMod.MOD_ID, "textures/gui/intel/" + e.image() + ".png");
        g.blit(portrait, margin, top + 43, imageW, imageH, 0, 0, 640, 360, 640, 360);
        int tx = margin + imageW + 10;
        int textW = Math.max(80, width - tx - margin);
        drawWrapped(g, Component.translatable("siege.intel.origin").getString() + ": " + e.origin(), tx, top + 43, textW, 0xFFD0D0D0);
        drawWrapped(g, Component.translatable("siege.intel.armament").getString() + ": " + e.armament(), tx, top + 68, textW, 0xFFD0D0D0);
        int descY = Math.max(top + 43 + imageH + 8, top + 102);
        drawWrapped(g, e.description(), margin, descY, width - margin * 2, 0xFFC8C8C8);
        if (height - descY > 56) drawWrapped(g, Component.translatable("siege.intel.advisory").getString() + ": " + e.advisory(), margin, descY + 36, width - margin * 2, 0xFFFFB020);
        g.drawString(font, String.format("%02d/%02d", selected + 1, FILES.size()), width - 45, top + 17, 0xFF777777, false);
    }

    private String stars(int count) { return "★".repeat(Math.max(0, count)) + "☆".repeat(Math.max(0, 5 - count)); }
    private static IntelEntry file(String code, String name, String category, int threat, int hp, String origin,
                                   String image, String armament, String variants, String status,
                                   String description, String advisory) {
        return new IntelEntry(code, name, category, threat, hp, origin, image, armament, variants, status, description, advisory);
    }
    private void drawWrapped(GuiGraphics g, String value, int x, int y, int maxWidth, int color) {
        for (var line : font.split(Component.literal(value), maxWidth)) { g.drawString(font, line, x, y, color, false); y += 12; }
    }

    @Override public void onClose() { SiegeUiSounds.back(); minecraft.setScreen(parent); }
}

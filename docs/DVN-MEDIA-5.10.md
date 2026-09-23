# Dummies vs Noobs media — SIEGE 5.10

Documento interno del proyecto. Estas fuentes y notas de licencia no se muestran como metodología dentro de la interfaz normal del jugador.

## Fondos incorporados

SIEGE 5.10 añade dos miniaturas reales de **Dummies vs Noobs** obtenidas de la página oficial de Roblox del juego. La página oficial acredita varias miniaturas a **pro100ker**.

- `dvn_arctic_standoff.png`
  - fuente Roblox CDN: `https://tr.rbxcdn.com/180DAY-f57d526d7c860645d35d5f1f6ac81be3/768/432/Image/Webp/noFilter`
  - tamaño de origen: 768×432
  - uso: rotación normal del menú / galería de fondos
- `dvn_coastal_assault.png`
  - fuente Roblox CDN: `https://tr.rbxcdn.com/180DAY-436fbbe5a341fdb4293653da659ebe0a/768/432/Image/Webp/noFilter`
  - tamaño de origen: 768×432
  - uso: rotación normal del menú / galería de fondos

No se hace upscale artificial a 1920×1080. El renderer ya puede mostrar escenas de distinto tamaño manteniendo 16:9, así que estos fondos se conservan a su resolución nativa para no inventar detalle.

La página oficial de Roblox identifica el juego como **Dummies vs Noobs**, por **Stronghold 5-5**, y acredita las miniaturas a `pro100ker`. No publica en esa página una licencia general de reutilización para las imágenes. Se incorporan aquí como material de un mod fan no comercial; si el proyecto cambia de forma de distribución, hay que volver a revisar este punto.

## Música incorporada

### Arc - Enemy — Potoe

- fuente: `https://soundcloud.com/potoe-50708490/arc-enemy`
- relacionada directamente con Dummies vs Noobs por su autor
- la página individual de SoundCloud marca la pista como **CC BY-NC-SA**
- SIEGE conserva atribución dentro de `assets/siege/licenses/arc_enemy.txt`
- el build descarga el audio fuente y lo convierte a Ogg Vorbis estéreo 44.1 kHz con -3 dB de headroom
- nombre dentro del menú: `Arc - Enemy · Potoe`

No se modifica la composición ni se presenta como música propia de SIEGE. Sólo se prepara el archivo para reproducción correcta dentro de Minecraft.

## Música que sigue como referencia

Las demás pistas DVN que aparecen en Multimedia siguen siendo referencias de ambientación mientras no haya una fuente individual con permiso claro para incluir el binario. Entre ellas están `Convenience Store`, `New Store`, `Jazz Music`, `From the Ashes`, `Sad Choir` y las pistas del Boss OST.

No se debe asumir que una playlist completa hereda la licencia de una pista individual. `Arc - Enemy` se incorpora porque su propia página muestra CC BY-NC-SA; otras pistas se revisan de forma individual antes de entrar al JAR.

## Reglas para próximos medios

1. Buscar material real de DVN; no generar imitaciones con IA.
2. Priorizar páginas oficiales, autores originales o cuentas del creador.
3. Mantener 16:9 para fondos normales.
4. No reescalar una imagen pequeña sólo para llamarla HD.
5. Tempest Jutcherson sigue reservado como easter egg y queda fuera de la rotación normal.
6. Para música, comprobar la licencia de la pista concreta antes de empaquetarla.
7. Mantener atribución cuando corresponda.

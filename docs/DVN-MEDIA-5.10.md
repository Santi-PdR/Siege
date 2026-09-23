# Dummies vs Noobs media — SIEGE 5.10

Documento interno del proyecto. Estas fuentes y notas de licencia no se muestran como metodología dentro de la interfaz normal del jugador.

## Fondos incorporados

SIEGE 5.10 incorpora dos miniaturas reales de **Dummies vs Noobs** obtenidas mediante la API oficial de thumbnails de Roblox para el universo del juego.

- universo DVN: `3293525400`
- endpoint usado por el build: `https://thumbnails.roblox.com/v1/games/multiget/thumbnails`
- tamaño solicitado: `768x432`
- formato solicitado: PNG
- archivos internos:
  - `dvn_official_01.png`
  - `dvn_official_02.png`
- uso: rotación normal del menú y galería de fondos

El build consulta la API en cada preparación de medios y resuelve dos thumbnails oficiales completados. Se usan IDs internos genéricos (`01` y `02`) porque la selección o el orden de miniaturas de Roblox puede cambiar; SIEGE no inventa un nombre como “Arctic” o “Coastal” sin comprobar qué imagen devolvió la API ese día.

No se hace upscale artificial a 1920×1080. El renderer admite escenas de distinto tamaño manteniendo 16:9, así que estos fondos se conservan a su resolución nativa 768×432 para no inventar detalle.

La página oficial de Roblox identifica el juego como **Dummies vs Noobs**, por **Stronghold 5-5**, y acredita varias miniaturas a `pro100ker`. La página no publica una licencia general de reutilización para todas las imágenes. Se incorporan aquí como material de un mod fan no comercial para un grupo pequeño; si el proyecto cambia de forma de distribución, hay que revisar este punto otra vez.

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

Las demás pistas DVN que aparecen en Multimedia siguen siendo referencias de ambientación mientras no haya una fuente individual suficientemente clara para incluir el binario. Entre ellas están `Convenience Store`, `New Store`, `Jazz Music`, `From the Ashes`, `Sad Choir` y las pistas del Boss OST.

No se asume que una playlist completa da automáticamente permiso para empaquetar cada pista individual. `Arc - Enemy` se incorpora porque su propia página muestra CC BY-NC-SA; las demás se revisan una por una antes de entrar al JAR.

## Reglas para próximos medios

1. Buscar material real de DVN; no generar imitaciones con IA.
2. Priorizar páginas oficiales, autores originales o cuentas del creador.
3. Usar APIs o páginas estables en vez de guardar URLs temporales de CDN cuando sea posible.
4. Mantener 16:9 para fondos normales.
5. No reescalar una imagen pequeña sólo para llamarla HD.
6. Tempest Jutcherson sigue reservado como easter egg y queda fuera de la rotación normal.
7. Para música, comprobar la licencia de la pista concreta antes de empaquetarla.
8. Mantener atribución cuando corresponda.

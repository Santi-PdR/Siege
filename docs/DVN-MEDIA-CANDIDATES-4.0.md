# Dummies vs Noobs — candidatos de media para SIEGE 4.00

Este documento separa **referencia descubierta** de **recurso autorizado para distribuir**. Encontrar una imagen o canción pública no equivale a tener permiso para incorporarla al JAR.

## Fondos visuales

Se localizaron miniaturas oficiales actuales de la experiencia Dummies vs Noobs / Stronghold 5-5 en Roblox. Son útiles para confirmar estética, composición y escenas, pero las copias encontradas son de **768x432**. SIEGE usa masters de fondo de 1920x1080, por lo que no se incorporan como fondos normales: ampliarlas directamente degradaría demasiado la imagen.

Referencias encontradas:

- Escena de combate/agua: `https://tr.rbxcdn.com/180DAY-436fbbe5a341fdb4293653da659ebe0a/768/432/Image/Webp/noFilter`
- Escena ártica/nieve: `https://tr.rbxcdn.com/180DAY-f57d526d7c860645d35d5f1f6ac81be3/768/432/Image/Webp/noFilter`
- Escena de portal/última resistencia: `https://tr.rbxcdn.com/180DAY-7dfaac86ba73f94ba298a0bb6000f2e8/768/432/Image/Webp/noFilter`

### Política para 4.00

1. No meter miniaturas 768x432 en la rotación HD sólo para aumentar cantidad.
2. Preferir un master oficial de mayor resolución, una imagen aportada con permiso o una recreación original inspirada en la composición sin copiar el arte.
3. Mantener relación 16:9 y legibilidad de los paneles del menú.
4. Conservar auto-contraste, scanlines y opciones de confort.
5. Tempest Jutcherson continúa siendo un easter egg aislado y nunca un fondo normal.

## Música

La búsqueda localizó el álbum **Dummies VS Noobs: Boss Original Soundtrack** de MrPotoe (2026), con 18 pistas. Entre las candidatas que encajan con el menú/operaciones están:

- `Voltaic Dispatch`
- `Dweller's Fury`
- `Imperishable Valour`
- `Death Sentence`
- sus versiones underscore cuando estén disponibles/licenciadas

El propio autor pide que se solicite **permiso antes de usar sus canciones originales**. Por eso estas pistas se recomiendan como candidatas, pero **no se incluyen ni se descargan dentro de SIEGE 4.00** sin permiso explícito o un archivo que el propietario del proyecto pueda distribuir legalmente.

También existen pistas asociadas a Dummies vs Noobs en bibliotecas/Creator Store. Deben revisarse caso por caso; que un asset sea reproducible en Roblox no garantiza que pueda redistribuirse dentro de un mod de Minecraft.

## Soundtrack actual protegido

Hasta recibir nuevo audio autorizado, SIEGE conserva:

- Tale of a Cruel World
- The Darkest of Days
- Kaptain – Music Box (segmento autorizado ya integrado)
- Heaven's Hell-Sent Gift

La cola aleatoria sin repetición, fades y avisos de pista continúan funcionando sobre ese catálogo.

## Cómo añadir una pista nueva correctamente

Para una futura 4.x, aportar uno de estos:

- archivo original propio;
- archivo entregado por el autor con permiso para redistribuirlo en el mod;
- licencia que permita expresamente empaquetarlo y redistribuirlo.

Después se puede integrar al pipeline `assets-source/music-full/`, recalcular duración, añadir SoundEvent, probar shuffle/no-repeat y publicar sólo si CI valida el JAR.

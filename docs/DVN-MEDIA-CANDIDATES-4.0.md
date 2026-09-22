# Dummies vs Noobs — candidatos de media para SIEGE 4.00

Este documento separa **referencia descubierta** de **recurso autorizado para distribuir**. Encontrar una imagen o canción pública no equivale a tener permiso para incorporarla al JAR.

## Dirección visual confirmada

La descripción oficial de Dummies vs Noobs en Roblox lo presenta como un shooter cooperativo por oleadas para una escuadra de 8 defendiendo la última fortaleza de Dummykind contra Noobs con armamento moderno y de futuro cercano. Esa combinación —fortaleza, escuadra táctica, defensa por oleadas y tecnología militar— es la referencia visual principal para SIEGE 4.00.

Para futuros fondos se priorizan cuatro familias:

1. Stronghold / última fortaleza.
2. Operación urbana nocturna.
3. Hangar, briefing o sala de despliegue.
4. Defensa de oleada con escala y presión visibles.

## Fondos visuales encontrados

Se localizaron miniaturas oficiales actuales de la experiencia Dummies vs Noobs / Stronghold 5-5 en Roblox. Son útiles para confirmar estética, composición y escenas, pero las copias encontradas son de **768x432**. SIEGE usa masters de fondo de 1920x1080, por lo que no se incorporan como fondos normales: ampliarlas directamente degradaría demasiado la imagen.

Referencias encontradas:

- Escena de combate/agua: `https://tr.rbxcdn.com/180DAY-436fbbe5a341fdb4293653da659ebe0a/768/432/Image/Webp/noFilter`
- Escena ártica/nieve: `https://tr.rbxcdn.com/180DAY-f57d526d7c860645d35d5f1f6ac81be3/768/432/Image/Webp/noFilter`
- Escena de portal/última resistencia: `https://tr.rbxcdn.com/180DAY-7dfaac86ba73f94ba298a0bb6000f2e8/768/432/Image/Webp/noFilter`

### Política visual 4.00

1. No meter miniaturas 768x432 en la rotación HD sólo para aumentar cantidad.
2. Preferir un master oficial de mayor resolución, una imagen aportada con permiso o una recreación original inspirada en la composición sin copiar el arte.
3. Mantener relación 16:9 y, preferentemente, **1920x1080 o más**.
4. Mantener la relación de aspecto; no deformar para llenar pantalla.
5. Conservar auto-contraste, scanlines y opciones de confort.
6. Tempest Jutcherson continúa siendo un easter egg aislado y nunca un fondo normal.
7. Una escena nueva no entra a producción hasta superar validación de decodificación, dimensiones y contraste de UI.

## Soundtrack histórico/referenciado de Dummies vs Noobs

Como referencias asociadas al juego se documentan:

### Lobby
- `Convenience Store`
- `Music Box`
- `New Store`
- `Jazz Music`

### Resultado de partida
- `From the Ashes` — referencia asociada a victoria.
- `Sad Choir` — referencia asociada a derrota.

Estas entradas aparecen en **Sala Multimedia → Música DVN** como referencias, no como audio distribuido.

## Música adicional encontrada

La búsqueda también localizó el álbum **Dummies VS Noobs: Boss Original Soundtrack** de MrPotoe (2026), con 18 pistas. Entre las candidatas que encajan con el menú/operaciones están:

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

## Qué NO hace SIEGE

- no descarga audio de YouTube ni de sitios de streaming;
- no copia una miniatura 768x432 y la llama "HD";
- no mete un easter egg en la rotación principal;
- no transforma una referencia estética en un asset redistribuible sin revisar origen/permisos.

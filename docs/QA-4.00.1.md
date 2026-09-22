# SIEGE 4.00.1 — QA

## Guía del Servidor

- Abrir GUÍA desde la portada.
- Confirmar que no se crea una fila nueva que empuje los botones fuera del menú.
- Probar GUI Scale 1, 2, 3 y 4 cuando estén disponibles.
- El texto GUÍA/DESPLIEGUE y OPERACIONES/AJUSTES no debe solaparse.
- Probar resoluciones 1280×720, 1366×768 y 1920×1080.
- Verificar categorías: EMPEZAR, RAZAS, PROGRESIÓN, AMENAZAS, SISTEMAS, SUPERVIVENCIA, HISTÓRICO.
- Cada categoría debe tener contenido.
- Los textos largos deben desplazarse dentro del panel de detalle sin invadir otros botones.
- La lista y el detalle deben desplazarse de forma independiente.
- HISTÓRICO debe advertir cuando una mecánica puede haber cambiado.
- La interfaz normal no debe mostrar términos de mantenimiento como fuentes, corpus, auditoría o tags.

## Contenido

- EMPEZAR debe incluir Primeros pasos y los conceptos principales.
- RAZAS debe incluir el orden de rarezas y el catálogo conocido.
- PROGRESIÓN debe explicar V1→V4, Trials y rutas especiales sin inventar una receta universal.
- AMENAZAS debe incluir Executores, bosses, raids y estructuras.
- SISTEMAS debe incluir reliquias, Geography Table, Daemonium Kit, Assembling, dimensiones y economía.
- SUPERVIVENCIA debe incluir revive, movilidad, preparación y avisos de alto riesgo.
- La guía no debe incluir inventarios, builds, progreso o perfiles privados de jugadores.

## War Room / búsqueda

- Ruta GUÍA abre `SiegeServerGuideScreen`.
- Buscar una raza sigue devolviendo Atlas/Info adecuados.
- Buscar Geography Table abre su ficha completa.
- Buscar Third Justice sigue priorizando Arsenal.
- Buscar ATLAS sigue pudiendo abrir el dossier Intel SUP-001.
- Los subtítulos de resultados de información deben ser simples y no mostrar estados técnicos de confianza.

## Multimedia

- La playlist actual debe seguir funcionando.
- Anterior / Reiniciar / Siguiente deben responder.
- Rotación de fondos y galería deben seguir funcionando.
- Las recomendaciones DVN deben permanecer como referencias, sin incorporar automáticamente audio externo al JAR.
- Tempest Jutcherson no debe aparecer en rotación o galería normal.

## Regresiones

- Forge build limpio.
- Intel UNKNOWN continúa disponible.
- No vuelven Favoritos, Índice, Guardar ni Copiar.
- Singleplayer sigue oculto salvo Ctrl+S.
- El servidor oficial continúa primero y Editar/Eliminar siguen protegidos.
- Pantallas de terceros no deben tematizarse accidentalmente.
- `dist/manifest.json` debe publicar 4.00.1 sólo después del build verde de main.

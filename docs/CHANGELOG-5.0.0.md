# SIEGE 5.00.0 — Command Network

Estado: desarrollo activo en `chatgpt/siege-5.00.0-nextgen`.

5.00 no se plantea como una suma de parches sobre 4.00. La base es convertir las pantallas ya separadas de SIEGE en una experiencia más coherente, rápida de leer y mucho más audiovisual, manteniendo Forge 1.20.1 / Java 17 y los contratos de privacidad, Intel, Deployment y accesibilidad existentes.

## Bloques ya implementados

### Command Network / War Room 5.0
- Operations deja de mostrar todas sus rutas como una pared plana de botones.
- Cuatro frentes claros: **Despliegue**, **Inteligencia**, **Conocimiento** y **Sistemas**.
- Cada frente muestra únicamente las herramientas que corresponden a ese contexto.
- La búsqueda global sigue cubriendo todo SIEGE y conserva deep-links a dossiers y fichas.
- La jerarquía no elimina rutas existentes: las 16 rutas de 4.00 siguen cubiertas exactamente una vez.
- La distribución cambia de 4 a 2 columnas en anchos estrechos para mantener lectura y evitar solapamientos.

### Media Room 5.0
- Nueva sección **AMBIENTES / MOODS**.
- Los ambientes conectan una pista ya instalada con referencias temáticas para una situación concreta.
- Primeros ambientes: **Stronghold**, **Deployment**, **Intel / Archive** y **Last Stand**.
- Los catálogos DVN y de dirección visual ahora tienen scroll; ninguna lista depende de cortar silenciosamente lo que no entra en pantalla.
- Se amplía la dirección visual a Stronghold, Arctic Standoff, combate urbano nocturno, ciudad sitiada, frente desértico, zona industrial, hangar/briefing, wave defense, boss assault y armería de despliegue.
- Las referencias externas siguen siendo referencias. No se inyecta audio de terceros al JAR por aparecer en un catálogo.

### Perfil STRONGHOLD
- Nuevo perfil visual 5.0 inspirado en la identidad militar de Dummies vs Noobs / Stronghold.
- Mantiene fondos e Intel animados y scanlines suaves.
- Reduce flashes y elimina la interferencia de título para no sacrificar legibilidad por estética.
- Conserva contraste automático y una oscuridad de panel más fuerte que el perfil Tactical.

## Contratos heredados que siguen siendo obligatorios
- Forge 1.20.1 / Java 17.
- Singleplayer sólo mediante Ctrl+S.
- servidor oficial y callbacks vanilla autoritativos.
- UNKNOWN permanece en Intel.
- sin Favoritos / Índice / Guardar / Copiar en Intel.
- dossier a la derecha.
- música aleatoria sin repetición.
- Tempest Jutcherson continúa aislado como easter egg, fuera de fondos y galería normales.
- pantallas de Embeddium/Sodium/otros mods no se tematizan accidentalmente.
- reduce flashes / reduce motion / high contrast siguen siendo contratos de accesibilidad.
- la información general nunca debe convertirse en perfil, inventario, build o progreso personal de un jugador.

## Próximos frentes del salto 5.00
El desarrollo continuará sobre Deployment, Intel, búsqueda contextual, Enciclopedia/Atlas, rendimiento, pantalla principal y nuevas superficies de briefing. Cada bloque debe aportar una diferencia visible y verificable; no se contarán como novedades sistemas que ya existían en 4.00.

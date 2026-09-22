# QA — SIEGE 2.25.0

## Portada

- La etiqueta de build muestra 2.25.0 desde `SiegeRuntimeStatus.version()`.
- La barra operacional inferior no se solapa con la etiqueta de build.
- La barra muestra perfil, escena actual y audio cuando hay espacio suficiente.
- El estado del cliente sigue siendo cualitativo; no aparece un porcentaje de readiness.
- La etiqueta superior derecha muestra el tipo de escena, nombre y cuenta regresiva solo cuando la rotación automática está activa.

## Fondos

- Los fondos estándar se recorren mediante una bolsa barajada determinista.
- Ninguna escena estándar se repite antes de agotar la bolsa.
- No hay repetición inmediata al pasar de una bolsa a la siguiente.
- Rooftop Squad conserva los slots FEATURED.
- TEMPEST JUTCHERSON conserva dos slots de anomalía por cada 100 slots cuando las sorpresas están permitidas.
- Reduced Motion / Reduce Flashes excluyen la anomalía.
- El fondo fijado sigue respetando `selectedScene` y no muestra una cuenta regresiva falsa.

## Regresiones

- `SceneScheduleTest` valida bolsas completas, límites, continuidad, rareza y comfort mode.
- `test_release_014.py` reconoce 2.25.0 como build activo y conserva todos los contratos históricos de 2.0.
- Las validaciones de recursos GUI e Intel siguen ejecutándose.
- El build Forge limpio y el JAR siguen siendo obligatorios antes de publicar en `dist/`.

## Compatibilidad a verificar dentro del juego

- GUI Scale 1, 2, 3 y 4.
- 16:9 y ultrawide.
- Perfil CUSTOM y perfiles predefinidos.
- Fondo fijado, rotación automática y reduced motion.
- Hover/click del menú principal, Intel y galería.

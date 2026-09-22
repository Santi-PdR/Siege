# SIEGE 2.0 — arquitectura de información

La versión 2.0 separa conocimiento del mundo, dossiers, equipo, manual de campo y estado técnico. El objetivo es que cada dato tenga un hogar único y que las rutas de navegación expliquen por sí mismas qué se encontrará detrás.

| Superficie | Contenido autoritativo | No debe contener |
| --- | --- | --- |
| **Intel · Dossiers** | Unidades, categoría, amenaza, HP/DEF confirmados, armamento, conducta, variantes, arte Intel y estado UNKNOWN | Lore general, estados de muerte, ajustes, equipo no asociado a una unidad |
| **Archivo** | Qué es SIEGE, 2044, Dominia/facciones, El Núcleo, Gates/Rifts, dificultad, crónicas e inspiraciones | Dossiers de unidad, diagnóstico técnico |
| **Arsenal** | Objetos y equipo: Third Justice, Aerorig, Riflator, HOLO-Watch y futuras fichas de equipamiento | Unidades enemigas, configuración |
| **Manual de campo** | Misiones, estados corporales, Bleeding/Burned/Erased, protocolos y prioridades operativas | Changelog, estadísticas del cliente |
| **Despliegue** | Lista de servidores, servidor oficial, ping, compatibilidad, MOTD y conexión | Lore, ajustes visuales, contactos inventados |
| **Ajustes** | Apariencia, movimiento, audio, Intel, accesibilidad, fondos y sistema | Lore o manual de campo |
| **Centro de comando** | Perfil del cliente, prioridades técnicas y rutas hacia diagnóstico/configuración | Conocimiento del mundo |
| **Diagnóstico** | Problemas detectados, impacto, recomendación y reparaciones seguras explícitas | Lore, dossiers, porcentajes crípticos en navegación normal |
| **Fondos** | Galería, selección/rotación y metadatos visuales | Intel o estado técnico |

## Rutas principales

`PORTADA → INTEL → DOSSIER → INSPECTOR`

`PORTADA → ARCHIVO → artículo / inspiración / crónica`

`PORTADA → ARSENAL → objeto → imágenes / reel de evidencia`

`ARCHIVO → MANUAL DE CAMPO → misión / estado / protocolo`

`PORTADA → DESPLIEGUE → DESTINO → ESTADO → CONECTAR`

`PORTADA → AJUSTES → CENTRO DE COMANDO → DIAGNÓSTICO`

## Reglas de contenido

- UNKNOWN continúa significando que faltan referencias suficientemente fiables; no se rellena por intuición.
- Las fuentes suministradas u oficiales pueden respaldar una ficha, pero el texto visible se redacta como archivo interno de 2044 y no como comentario de desarrollo.
- Un objeto pertenece al Arsenal aunque aparezca en una operación o en manos de una unidad.
- Un estado médico/corporal pertenece al Manual de campo, no a un dossier.
- Los controles eliminados de Intel (Favoritos, Índice, Guardar y Copiar) no forman parte de esta arquitectura.
- Sistema y Diagnóstico pueden conocer métricas internas, pero la navegación ordinaria no muestra porcentajes de readiness sin significado directo para el jugador.

# SIEGE 2.25.0 — Command & Scene Overhaul

Esta versión continúa directamente sobre la reconstrucción 2.0.0. El objetivo es que el menú principal se sienta más vivo sin volver a llenarlo de controles innecesarios: mejor rotación visual, más información operacional útil y contratos de regresión más estrictos.

1. La versión activa del mod pasa a `2.25.0`.
2. El catálogo de escenas ahora mantiene una lista autoritativa de índices estándar separada de escenas especiales.
3. El programador de fondos deja de asumir que FEATURED y ANOMALY están al final del catálogo.
4. La rotación normal usa una bolsa barajada determinista en lugar del orden lineal 0→1→2→3.
5. Cada escena estándar aparece una vez antes de repetir la bolsa normal.
6. La nueva permutación evita repeticiones consecutivas dentro de una misma bolsa.
7. El cambio entre bolsas también evita repetir inmediatamente la última escena del ciclo anterior.
8. La selección sigue siendo estable para cualquier slot temporal, incluidos slots negativos usados por regresiones.
9. La escena FEATURED conserva su presencia especial sin convertirse en parte del ciclo normal.
10. TEMPEST JUTCHERSON conserva su aparición rara aproximada del 2% cuando las sorpresas están habilitadas.
11. Los modos de comodidad siguen excluyendo por completo la anomalía visual.
12. La escena actual y la escena entrante de un crossfade consultan el mismo programador, evitando desincronizaciones.
13. El catálogo expone `standardCount()` y `standardIndex()` para que render, pruebas y futuras escenas compartan la misma fuente de verdad.
14. La barra operacional inferior vuelve a estar disponible en la portada de SIEGE.
15. En la portada esa barra se eleva por encima de la etiqueta de build para impedir solapes.
16. La barra muestra el perfil de cliente activo o el perfil más cercano cuando se usa CUSTOM.
17. La barra de portada muestra el tipo y nombre de la escena que está realmente en pantalla.
18. En resoluciones con espacio suficiente también muestra el estado/volumen de audio.
19. El extremo derecho conserva el estado cualitativo de salud del cliente sin regresar al porcentaje críptico de readiness.
20. La etiqueta superior de escena incorpora una cuenta regresiva hasta la siguiente rotación automática.
21. La cuenta regresiva desaparece cuando el fondo está fijado o la rotación automática está desactivada.
22. FEATURED y ANOMALY conservan acentos visuales distintos dentro del chrome operacional.
23. `SceneScheduleTest` ahora verifica que cada bolsa contenga todas las escenas estándar sin duplicados.
24. Las regresiones también comprueban continuidad entre ciclos, rareza de anomalías, frecuencia FEATURED y exclusión en modo comfort.
25. Los contratos de release y CI se actualizan para reconocer 2.25.0 como siguiente salto oficial después de 2.0.0.

## Compatibilidad preservada

- Forge 1.20.1 / Java 17.
- Servidor oficial y callbacks vanilla de Multiplayer.
- Singleplayer oculto salvo `Ctrl+S`.
- Intel sin Favoritos/Índice/Guardar/Copiar.
- UNKNOWN continúa siendo la categoría conservadora cuando faltan fuentes fiables.
- Embeddium conserva su propia interfaz de vídeo.
- Música aleatoria sin repetición y avisos REC únicamente al inicio de pista.
- Third Justice y el resto del Arsenal permanecen separados de Intel y del Manual de campo.

La publicación sigue el flujo rama → PR → CI → squash merge → CI de `main` → JAR validado en `dist/` con SHA-256.

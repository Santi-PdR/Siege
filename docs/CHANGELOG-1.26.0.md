# SIEGE 1.26.0 — integración visual sobre 1.25

Esta actualización parte de `chatgpt/siege-1.25.0-generational-overhaul` y conserva su arquitectura de siete secciones, perfiles autoritativos, Diagnóstico avanzado, prioridades operativas y audio semántico.

1. La versión avanza de 1.25.0 a 1.26.0.
2. Se integran cuatro imágenes originales como fondos del menú.
3. Operación nocturna entra en la rotación habitual.
4. Encuentro urbano entra en la rotación habitual.
5. Escuadrón en azotea entra como escena especial menos frecuente.
6. Tempest Jutcherson entra como anomalía visual rara.
7. La anomalía ocupa dos slots aislados de cada cien, equivalentes al 2% de los periodos de rotación.
8. Movimiento reducido y Reducir destellos excluyen la anomalía de la rotación automática.
9. La galería conserva acceso manual a las trece escenas.
10. Cada imagen nueva conserva su proporción original en galería y presentación contenida.
11. Multiplayer conserva el encuadre de cobertura definido por 1.25.
12. El fundido calcula el siguiente fondo mediante la misma secuencia estable que usa el frame posterior.
13. La selección de escenas no cambia aleatoriamente entre frames.
14. Los índices 9–12 se guardan y cargan correctamente.
15. Intel resuelve rutas ausentes o inválidas mediante el expediente clasificado existente.
16. El visor ampliado usa la misma resolución segura de retratos.
17. La disponibilidad se consulta al gestor de recursos activo para respetar recargas.
18. Los nombres de frames Boss solo se transforman cuando terminan en dos dígitos.
19. Reducir destellos congela también las secuencias de retratos Boss.
20. El cambio de categoría y la selección de dossier usan los sonidos semánticos de 1.25.
21. Los barridos de botones respetan Reducir destellos.
22. La portada elimina la etiqueta histórica fija `BUILD 0.13.0`.
23. La placa dinámica 1.25 queda como única fuente visible de versión, perfil y salud.
24. La interferencia del título respeta Reducir destellos durante el render.
25. El aviso de pista se sitúa bajo la columna de comandos cuando existe altura segura.
26. El aviso se oculta si no cabe completo, evitando invadir controles o dossiers.
27. La cola musical, los cuatro masters completos y el shuffle sin repetición permanecen intactos.
28. No se añaden atajos de teclado.
29. No regresan Favoritos, Índice, Guardar ni Copiar en Intel.
30. Se mantiene un solo control Ampliar por dossier.
31. Se añade una prueba determinista para rareza, continuidad, límites y modo tranquilo.
32. Las pruebas de configuración cubren persistencia de los cuatro índices nuevos.
33. CI compila el modelo de secuencia junto con configuración antes del build Forge.
34. Las cuatro imágenes nuevas se incluyen sin modificar sus bytes originales.

## Verificación previa

- Sintaxis de todas las fuentes Java.
- Geometría responsive, búsqueda, guía, archivo, configuración y Multiplayer.
- Distribución de controles, sliders, rutas de retorno y límites del tema.
- Contratos duraderos hasta 1.25 actualizados para 1.26.
- Secuencia de fondos y persistencia de índices nuevos.
- Validación de audio, catálogo y PNG Intel usando los recursos versionados.

El build Forge completo y la revisión visual dentro de Minecraft deben completarse mediante GitHub Actions y una prueba real antes de fusionar en producción.

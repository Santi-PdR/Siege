# SIEGE 1.50.0 — Operational Presentation Overhaul

1. La versión avanza de 1.26.0 a 1.50.0.
2. Se crea `SiegeSceneCatalog` como fuente autoritativa de metadatos para todos los fondos del menú.
3. Los trece fondos dejan de repartir sus identificadores entre distintas clases.
4. Los nombres en español e inglés de cada escena pasan al catálogo común.
5. Las dimensiones nativas de cada escena pasan al catálogo común.
6. Cada escena recibe un sesgo de oscuridad propio para proteger la lectura.
7. Las escenas se clasifican como STANDARD, FEATURED o ANOMALY.
8. La elegibilidad para modos de confort se declara por escena.
9. Tempest Jutcherson queda identificado formalmente como ANOMALY.
10. Escuadrón en azotea queda identificado formalmente como FEATURED.
11. `SiegeSceneSchedule` obtiene su cantidad de escenas del catálogo en lugar de mantener otro número mágico.
12. El índice de la anomalía deja de estar hardcodeado en la secuencia de rotación.
13. El índice de la escena destacada deja de estar hardcodeado en la secuencia de rotación.
14. Los modos de confort continúan excluyendo la anomalía de la rotación automática.
15. La rareza de Tempest Jutcherson permanece determinista en dos slots de cada cien.
16. La escena destacada conserva seis slots de cada cien sin repeticiones consecutivas.
17. `SiegeBackgrounds` construye su lista de recursos desde el catálogo común.
18. El render obtiene dimensiones nativas desde el catálogo en lugar de un switch separado.
19. La galería obtiene nombres de escena desde el mismo catálogo usado por el render.
20. Se añaden etiquetas visuales ESCENA, DESTACADO y ANOMALÍA VISUAL.
21. El oscurecimiento efectivo incorpora el sesgo de contraste de la escena actual.
22. Durante un crossfade se tiene en cuenta también el sesgo de la escena entrante.
23. El auto contraste sigue imponiendo mínimos de legibilidad sobre el sesgo propio de cada imagen.
24. Alto contraste mantiene un mínimo superior sin alterar los bytes de las imágenes originales.
25. El estado de runtime informa ahora si el fondo actual es normal, destacado o anomalía.
26. La portada muestra la escena actual mediante el chrome operacional cuando existe espacio seguro.
27. La anomalía visual usa un acento diferenciado en la placa de escena.
28. Las escenas destacadas usan un acento diferenciado sin competir con el título principal.
29. La placa de build de portada muestra también el porcentaje real de preparación del cliente.
30. Las etiquetas de sección muestran salud y preparación en las superficies donde existe espacio seguro.
31. Mouse deja de aparecer como un `SYS` genérico dentro del chrome compartido.
32. Audio recibe identidad `AUD // AUDIO MIX` en el chrome común.
33. Video recibe identidad `VID // VIDEO`.
34. Controls y Key Binds reciben identidad `CTL // CONTROLS`.
35. Mouse recibe identidad `MSE // MOUSE`.
36. Accesibilidad recibe identidad `ACC // ACCESSIBILITY`.
37. Idioma recibe identidad `LNG // LANGUAGE`.
38. Resource Packs recibe identidad `PAK // RESOURCES`.
39. Las pantallas de mundos reciben identidad `WRD // WORLD FILES`.
40. Network/Connect conserva la identidad de Despliegue y no se mezcla con Sistema.
41. El encabezado interno de Multiplayer elimina definitivamente el texto heredado `DESPLIEGUE 0.70`.
42. Multiplayer muestra su versión real mediante `SiegeRuntimeStatus.version()`.
43. Se mantiene la autoridad vanilla sobre conexión, ping, persistencia, LAN y callbacks.
44. Diagnóstico detecta cuando la anomalía está fijada manualmente durante un perfil de confort y lo explica sin modificar nada automáticamente.
45. Diagnóstico informa la oscuridad efectiva específica de la escena actual.
46. Se añade `GuiResourceRegressionTest` para decodificar todos los PNG bajo `textures/gui`.
47. CI bloquea la release si cualquier PNG de interfaz falta, está corrupto o no puede ser leído por `ImageIO`.
48. CI verifica que las dimensiones reales de todos los fondos coincidan exactamente con el catálogo 1.50.
49. Las pruebas de configuración recorren todas las escenas del catálogo y verifican persistencia sin depender de índices fijos.
50. Las pruebas de navegación cubren identidades nativas, breakpoints responsive y el mapeo real de `MouseSettingsScreen` de Forge 1.20.1.

## Invariantes preservados

- Forge 1.20.1 y Java 17.
- Singleplayer continúa oculto salvo Ctrl+S.
- Servidor oficial: `SiegeLacontinuacion.exaroton.me:18736`.
- Editar/Eliminar siguen bloqueados para el destino oficial.
- Los callbacks vanilla siguen siendo la autoridad de conexión.
- No regresan Favoritos, Índice, Guardar ni Copiar dentro de Intel.
- Se mantiene un único control Ampliar por dossier.
- UNKNOWN sigue reservado para expedientes sin clasificación suficientemente fiable.
- La validación estricta de PNG Intel y de los seis frames Boss permanece activa.
- La música conserva shuffle sin repetición y las cuatro pistas configuradas.
- No se añaden atajos de teclado nuevos.

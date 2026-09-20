# SIEGE 0.50.0 — Command Center

0.50 es un salto grande desde la línea 0.40. No cambia gameplay: reconstruye la capa de control del cliente para que Configuración, estado técnico, accesibilidad y perfiles visuales funcionen como un sistema único.

## 50 mejoras

1. La versión del mod salta a **0.50.0**.
2. `System` se reconstruye como **Centro de Comando SIEGE**.
3. El encabezado del Centro de Comando usa la versión real del mod.
4. Se elimina el texto fijo `0.40 SYSTEM SETTINGS`.
5. Se añade una barra de preparación del cliente de 0 a 100%.
6. La barra cambia de color entre listo, revisar y error.
7. El estado de guardado de configuración participa en el diagnóstico.
8. Un fallo de guardado pasa a estado de error visible.
9. Música activada con volumen 0% genera una advertencia útil.
10. Sonidos de interfaz activados con volumen 0% generan una advertencia útil.
11. `Reducir destellos` junto a interferencia activa se detecta como contradicción.
12. El perfil gráfico Rendimiento detecta fondos animados incompatibles con su objetivo.
13. El perfil gráfico Rendimiento detecta Intel animado incompatible con su objetivo.
14. El Centro de Comando muestra el build instalado.
15. Muestra versión de Minecraft y Forge.
16. Muestra el backend de render detectado, incluido Embeddium/Rubidium/Sodium cuando corresponde.
17. Muestra el total actual de expedientes Intel.
18. El diagnóstico conoce el desglose de Intel por Unit, Advanced, Tank, Boss, Elite y Super Unit.
19. El diagnóstico muestra el estado de audio actual.
20. El diagnóstico muestra el estado y nombre del fondo actual.
21. El diagnóstico resume las opciones de accesibilidad activas.
22. Se introduce el perfil **Cinemático**.
23. Cinemático activa el conjunto visual completo de SIEGE.
24. Cinemático coordina fondos, Intel animado, scanlines e interferencia.
25. Se introduce el perfil **Táctico**.
26. Táctico mantiene información y animación moderada sin interferencia de título.
27. Táctico usa un equilibrio gráfico pensado como perfil general recomendado.
28. Se introduce el perfil **Rendimiento**.
29. Rendimiento desactiva efectos, fondos animados e Intel animado.
30. Rendimiento desactiva rotaciones y avisos visuales innecesarios.
31. Se amplía el perfil **Tranquilo** a un conjunto completo y coherente.
32. Tranquilo reduce movimiento, destellos, sonidos de hover y rotaciones automáticas.
33. Se amplía el perfil **Lectura** a un conjunto completo centrado en Intel.
34. Lectura activa papel oscuro, espaciado cómodo y alto contraste.
35. Lectura desactiva la rotación automática para no mover el expediente mientras se lee.
36. Lectura oculta Intel de portada para reducir carga visual fuera del lector.
37. Los cinco perfiles tienen color propio.
38. Los cinco perfiles tienen icono propio.
39. Los cinco perfiles tienen descripción explicativa en tooltip.
40. El Centro de Comando marca visualmente el perfil activo.
41. Si los ajustes no coinciden con ningún preset aparece el estado **Personalizado/Custom**.
42. El botón superior de Configuración pasa de “Sistema” a **Comando/Command**.
43. Ese botón usa `0.50` obtenido de la versión real en vez de texto escrito a mano.
44. El botón de Comando muestra una insignia con el perfil actual cuando hay espacio.
45. El color del botón de Comando sigue el perfil activo.
46. Los tooltips generales de SIEGE toman el acento del perfil actual.
47. El build tag de portada incluye versión y perfil detectado.
48. La portada añade una barra operacional compacta con perfil, cantidad Intel y salud del cliente.
49. La barra operacional se oculta automáticamente en resoluciones pequeñas para no saturar la interfaz.
50. La lógica de versión y diagnóstico se centraliza para evitar que futuras pantallas vuelvan a quedarse con números viejos escritos a mano.

## Principios conservados

- No se añaden atajos de teclado nuevos.
- No se recuperan Favoritos, Índice, Guardar ni Copiar en Intel.
- El dossier principal continúa a la derecha.
- Multiplayer conserva el servidor oficial fijado y el comportamiento vanilla de conexión/ping.
- La lógica de Minecraft permanece nativa; SIEGE sólo añade presentación y preferencias del cliente.
- El lore y las mecánicas del servidor no se inventan desde la interfaz.

## Validación

La entrega debe pasar las regresiones existentes, las invariantes nuevas de 0.50 y la compilación completa Forge 1.20.1 antes de fusionarse a `main`.

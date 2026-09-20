# SIEGE 0.75.0 — Intel Texture Recovery

1. La versión avanza de **0.70.0** a **0.75.0**.
2. Se identifica la causa real del checker negro/magenta de Intel: siete PNG de placeholder estaban presentes dentro del JAR, pero su flujo de datos era inválido y Java no podía decodificarlos.
3. Se reemplaza `placeholder/classified.png` por un PNG válido con presentación SIEGE clasificada.
4. Se reemplazan `bosses/classified/frame_00..05.png` por placeholders Boss válidos.
5. Engineer deja de caer en la textura de error de Minecraft cuando no existe un render local verificado.
6. Informant, Tranquilizer, Agitator, Grappler, Skydiver, Skyliner y cualquier otro expediente que comparta el placeholder usan ahora un recurso decodificable.
7. Sparta y Proteus dejan de depender de los seis frames corruptos del placeholder Boss.
8. La reparación preserva la política de no copiar renders externos no verificados dentro del mod.
9. `RuntimeRegressionTest` ya no comprueba solamente que el archivo exista: abre cada PNG mediante `ImageIO`.
10. Los seis frames de cada dossier Boss se decodifican durante CI para detectar corrupción que antes podía pasar inadvertida.
11. Un PNG ausente, ilegible o con dimensiones inválidas hace fallar el build antes de generar/publicar la release.
12. El instalador continúa recibiendo únicamente el JAR validado que publica el workflow de `main`.

## Alcance

0.75.0 es una evolución incremental de 0.70.0 centrada en recuperación y endurecimiento de recursos Intel. Mantiene intactas las mejoras de Despliegue, las nuevas clasificaciones y la información DVN añadidas en 0.70.0.

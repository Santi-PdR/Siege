# SIEGE 5.00.0 — Command Network

Estado: candidato final. La rama `chatgpt/siege-5.00.0-nextgen` conserva Forge 1.20.1 / Java 17 y prepara el salto completo desde 4.00.0.

5.00 no es una suma de botones o textos nuevos. Reorganiza navegación, Briefing, Atlas, razas, progresión, información actual, audio y Multimedia a partir de los errores detectados jugando 4.00 y del export completo de Eternal Craft / SIEGE.

## Operations simplificado
- Operations deja de ser una pared de accesos repetidos.
- Quedan tres frentes: **Entrar y prepararse**, **Amenazas y progreso** y **Consultar información**.
- Archivo y Arsenal siguen en el menú principal y ya no se duplican desde Operations o Intel.
- Comando, Diagnóstico, Ajustes y Fondos dejan de duplicarse dentro de Operations.
- Diagnóstico deja de ser una ruta visible normal.
- El encabezado elimina estado técnico del cliente, `profile fit` y textos que no ayudan a jugar.
- La búsqueda sigue encontrando dossiers, razas, Trials, reliquias y temas reales.

## Briefing rehecho para nuevos
- El Briefing se presenta como **Primeros pasos**.
- Nueve temas: qué es SIEGE, cómo empezar, razas, habilidades/experiencia, V1→V4, Trials, Executores, unidades/bosses y heridas/reanimación.
- Explica primero que se puede arrancar como un survival normal: comida, herramientas, armadura, refugio y recursos.
- Después introduce los sistemas especiales sin asumir que el jugador ya conoce nombres o abreviaciones.
- Se quitan frases de auditoría, “evidencia”, “revisión”, “corpus” y lenguaje técnico innecesario de las pantallas normales.
- Desaparece el “También ver” automático. Sólo quedan siguientes pasos concretos cuando ayudan de verdad, por ejemplo bosses → Intel o razas → Atlas de Razas.

## Atlas y Enciclopedia
- Atlas queda para consultar a fondo, no para repetir el Briefing.
- Áreas principales: **Razas**, **Progresión**, **Sistemas** y **Objetos**.
- La Enciclopedia usa categorías equivalentes y sólo expone información actual destinada a jugadores.
- Registros viejos/editoriales pueden quedar internamente para mantenimiento, pero no se muestran como fichas normales.
- Cuando un mismo tema cambió, la capa más nueva reemplaza la anterior en la interfaz.

## Razas y progresión
- V1, V2, V3 y V4 se explican como etapas de evolución para las razas que usan ese sistema; ya no se finge que todas progresan igual.
- Saiyan usa entrenamiento, dojos y transformaciones como parte central de su progreso.
- Ghoul separa progreso por carne, V2 y Super Ghoul.
- Subhuman se trata como una familia. Hay fichas separadas para **Adamantium Human**, **Sorcerer**, **Evil Morty** y **Rick Sanchez**.
- Las fichas de Subhuman, Saiyan y Ghoul pueden abrir rutas/variantes propias.
- Los giros de raza se explican como resultados aleatorios y se aclara que algunas razas usan pasos o spins especiales.
- El Atlas de Razas se amplía con el catálogo grande recuperado del chat: Human, Mink, Tsufurujin, Shark, Angel, Ghoul, Cyborg, Deteriorer, Involver, Majin, Dark Manor, Hermes, Saiyan, Otsutsuki, Ackerman, Titan, Exceed, Cold Demon, Kaioshin, Dragon, Gas, Muerte, Shinigami, Fullbringer, Arrancar, Hakaishin, Zeno, Quincy, Divine, Namekian, Angel Guía, Aryano, Narehate, Arclighter, Faraón, Sun, Almirante, Archie, Emperador, Glitch Core, Jiren, Winter Hunter, Apotheosis, Lunarian, Virtud, Super Ghoul, Terrariano, Fénix, Diclonius, Windwhirl, Void Master, SOBRINO, Oni, Iluminati y Undertale AU.
- Si sólo está confirmado el nombre de una raza, la ficha lo dice. No se inventan habilidades, rareza o requisitos.

## Sistemas recuperados del export
El export dividido enviado para 5.00 fue reconstruido e indexado: **251.065 mensajes**, desde noviembre de 2025 hasta septiembre de 2026, disponibles para buscar por tema y fecha.

Se incorporó información reutilizable y no personal sobre:
- Room y Gate de Hacker;
- niveles y usos de meditación sin congelar números viejos como permanentes;
- stamina de habilidades añadida en septiembre;
- dojos y entrenamiento Saiyan;
- Maze Executor y aviso de Executor cercano;
- Respawn Cards y su función separada del sistema médico;
- Assembling, implantes, chips y relación con Cyborg;
- Geography Table y Daemonium Kit con funciones separadas;
- Trial Spire, Trials de meditación, Trials de raza, Trials de armas, Witch Trials, Trial de Third Justice y Trial global del santuario;
- dimensiones sólo al nivel que puede confirmarse, sin rellenar huecos con consejos genéricos.

No se importan inventarios personales, progreso privado, precios de una persona ni anécdotas como reglas del servidor.

## Información actual y recetas
- La interfaz normal conserva únicamente la versión más nueva que puede confirmarse.
- **Medkit / Botiquín:** crafteo actual indicado durante la revisión 5.00 del 22/09/2026: **3 bloques de hierro + 1 mesa de encantamientos**.
- **Desfibrilador:** el export más reciente mantiene **3 bloques de hierro + 1 bloque de oro**.
- RCP deja de mostrarse como método general actual de reanimación; el desfibrilador es la herramienta normal del sistema de muerte actual.
- Respawn Cards se explican como una vía separada de revive.
- Las recetas anteriores no aparecen en la ficha normal.
- Geography Table se explica por su utilidad: investigar información o propiedades ocultas de objetos/reliquias.
- Daemonium Kit se usa para extraer componentes al desarmar reliquias.

## Navegación duplicada
- Intel deja de ser un acceso alternativo a Archivo/Arsenal.
- Diagnóstico desaparece de la navegación normal.
- Comando/Cine permanece como opción de Ajustes, sin duplicarse en Operations.

## Audio 5.00
- El soundtrack se regenera desde los masters limpios a **Ogg Vorbis 44.1 kHz**.
- Se aplica **3 dB de headroom** antes de codificar para evitar clipping/crackling que podía sentirse como interferencia en 4.00.
- CI decodifica completamente cada OGG y rechaza audio corrupto o con tasa incorrecta.
- Las pistas preparadas también pasan comprobación de pico decodificado.
- Los sonidos de interfaz se verifican como Vorbis 44.1 kHz.
- Click, volver, advertencias, categoría y otros sonidos dejan de deformar el mismo sample cambiando artificialmente el pitch: se reproducen al pitch original `1.0`.

## Dummies vs Noobs / Multimedia
- Media Room incorpora **Ambientes / Moods**: Stronghold, Deployment, Intel / Archive, Last Stand, Industrial War y Boss / Red Alert.
- Los catálogos tienen scroll.
- La dirección musical DVN incluye las referencias anteriores y amplía bosses con el álbum 2026: **Powerplay, Bewitched, Dissonant, Voltaic Dispatch, Ablaze, Dweller's Fury, Dead Center, Imperishable Valour y Death Sentence**.
- Esas pistas son referencias de dirección: no se introducen archivos externos en el JAR sin una fuente que permita redistribuirlos.
- La dirección de fondos DVN suma Stronghold, portal/last stand, frente costero, Arctic, urbano nocturno, industrial, wave defense, boss assault y deployment.
- Las miniaturas oficiales localizadas a resolución baja, como 768×432, no se inflan artificialmente para fingir un master HD.

## Perfil STRONGHOLD
- Perfil visual inspirado en la identidad militar de Dummies vs Noobs / Stronghold.
- Mantiene fondos e Intel animados y scanlines suaves.
- Reduce flashes y elimina interferencia del título para no sacrificar legibilidad.

## Contratos que 5.00 conserva
- Forge 1.20.1 / Java 17.
- Singleplayer sólo con Ctrl+S.
- Servidor oficial y callbacks vanilla autoritativos.
- UNKNOWN permanece cuando realmente falta información.
- Sin Favoritos / Índice / Guardar / Copiar en Intel.
- Dossier a la derecha.
- Música aleatoria sin repetición.
- Tempest Jutcherson aislado como easter egg, fuera de fondos y galería normales.
- Pantallas de Embeddium/Sodium/otros mods no se tematizan accidentalmente.
- Reduce flashes / reduce motion / high contrast siguen siendo controles reales.
- Información general del servidor nunca se transforma en perfil, inventario, build o progreso personal de un jugador.

## Cierre del release
5.00 queda listo para merge cuando el CI final del PR esté verde. La publicación del JAR ocurre únicamente desde `main`, después de compilar y validar los recursos, audio, layouts y contratos de la versión.

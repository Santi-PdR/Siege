# SIEGE 3.00 — Arquitectura de la Enciclopedia del Servidor

## Objetivo

SIEGE 3.00 incorpora conocimiento recuperado del historial de Eternal Craft como una **guía general del servidor para cualquier jugador**.

La enciclopedia no guarda ni muestra:

- perfiles de jugadores;
- inventarios personales;
- progreso individual;
- builds privadas;
- notas personales;
- anécdotas centradas en personas concretas.

Su función es responder preguntas como:

- ¿Qué razas existen?
- ¿Cuál es el orden de rarezas?
- ¿Cómo funciona la progresión V1 → V4?
- ¿Qué son los Trials?
- ¿Qué se sabe de Executores, bosses, estructuras, reliquias o dimensiones?
- ¿Qué sistemas cambiaron con el tiempo?
- ¿Qué información está confirmada y cuál sigue pendiente?

## Dominios del mod

La Enciclopedia no reemplaza las demás secciones:

- **Intel**: dossiers de unidades y amenazas.
- **Archivo**: SIEGE, 2044, Núcleo, facciones, Gates/Rifts, inspiración y lore general.
- **Arsenal**: objetos/equipamiento curado y evidencia multimedia del mod.
- **Manual de Campo**: estados, heridas, misiones y protocolos operativos.
- **Enciclopedia del Servidor**: razas, rarezas, progresión, sistemas generales e historial verificable.
- **Deployment**: servidor, compatibilidad y conexión.
- **Command / Diagnostics / Settings**: estado técnico del cliente; nunca gameplay/lore.

## Navegación 3.00

La Enciclopedia tiene cuatro vistas:

### EMPEZAR

Información que debería conocer un jugador nuevo antes de meterse a sistemas avanzados:

- qué tipo de servidor es Eternal Craft / SIEGE;
- exploración y supervivencia;
- rarezas de raza;
- catálogo básico de razas;
- progresión general;
- Trials;
- Executores;
- estructuras;
- bosses;
- misiones/NPC;
- dimensiones;
- revive;
- reliquias;
- economía;
- cómo interpretar información incompleta.

### RAZAS

Catálogo de razas y conceptos relacionados:

- Human;
- Hacker;
- Shark;
- Saiyan;
- Deteriorer;
- Faraón;
- Apotheosis;
- Muerte;
- Cyborg;
- Ghoul;
- Subhuman;
- Terrariano;
- Kaioshin;
- Dragon;
- Shinigami;
- Majin;
- razas ocultas de AUs de Undertale;
- slots de raza;
- rarezas;
- Fabled;
- progresión V1 → V4 cuando corresponde.

La existencia de una raza en la enciclopedia no garantiza disponibilidad en la temporada actual.

### SISTEMAS

Sistemas del servidor agrupados por tema:

- progresión;
- Executores;
- Trials;
- estructuras;
- bosses;
- misiones/NPC;
- habilidades;
- energía/meditación;
- objetos;
- Assembling/Cyborgs;
- reliquias;
- dimensiones;
- muerte/revive;
- raids/eventos;
- facciones;
- economía;
- prompts/acciones.

### HISTÓRICO

Información que existió, cambió, se contradice o necesita contexto temporal:

- reglas antiguas de Deteriorer;
- cambios de edición de Executores;
- rutas antiguas de razas;
- cambios del sistema de muerte/revive;
- umbrales contradictorios;
- política de fuentes;
- cobertura de la investigación.

La vista histórica existe precisamente para impedir que una regla vieja vuelva a aparecer como actual.

## Modelo de datos

Cada entrada contiene:

- `id` estable;
- `zone`: `SERVER` o `HISTORY`;
- `domain`;
- título ES/EN;
- resumen ES/EN;
- cuerpo ES/EN;
- indicador `critical` cuando el tema implica riesgo operativo importante;
- relaciones con otras entradas;
- una o más referencias.

## Confianza

La Enciclopedia usa cinco estados:

1. `STAFF_CONFIRMED`
2. `SYSTEM_OBSERVED`
3. `HISTORICAL`
4. `UNCONFIRMED`
5. `CONTRADICTION`

Estos estados describen la **calidad temporal/de evidencia**, no rareza ni amenaza.

## Cobertura del corpus

La auditoría disponible reporta:

- 488 archivos encontrados;
- 252/252 JSON abiertos íntegramente;
- 251.065 mensajes únicos indexados;
- 0 duplicados;
- cobertura 02/11/2025 → 20/09/2026;
- sólo canal General en el export disponible.

Esto significa que el texto fue indexado, no que toda la semántica ya esté revisada ni que cada mecánica siga vigente.

## Regla de privacidad/contenido

El código y los tests de 3.00 deben bloquear regresiones hacia contenido personal.

No se deben introducir en `SiegeKnowledgeData`:

- nombres de jugadores;
- estado de una partida concreta;
- HP/energía de un jugador concreto;
- inventario de una persona;
- logros personales;
- conversaciones usadas como anécdota de jugador cuando no aportan una regla general del servidor.

Si una fuente original contiene nombres pero el dato útil es general, la ficha debe resumir la mecánica sin conservar la identidad de la persona.

## Política de datos históricos

Cuando una mecánica cambia:

- el dato antiguo puede conservarse en `HISTORY`;
- siempre debe llevar fecha/contexto;
- no sustituye automáticamente un dato nuevo;
- no se muestra como regla vigente.

Ejemplos ya preparados:

- Deteriorer con debuffs antiguos extremadamente largos;
- cambios de edición de Executores;
- Shark V2 histórica;
- Faraón y disponibilidad variable;
- cambios del sistema RCP/medkit/desfibrilador;
- contradicciones de Injured/Bleeding;
- niveles de meditación con umbrales diferentes según fecha/contexto.

## Política de huecos

Campo faltante significa **no confirmado**, nunca “no existe” o “no hace falta”.

No se inventan:

- recetas;
- porcentajes;
- probabilidades;
- stats;
- drops;
- pasos de Trials;
- costes;
- requisitos;
- disponibilidad actual.

## Búsqueda

La búsqueda interna y Operations Hub indexan:

- título;
- resumen;
- cuerpo;
- dominio;
- referencias;
- relaciones.

Ejemplos de deep-link válidos:

- `Obsainan`
- `Fabled`
- `Geography Table`
- `Daemonium Kit`
- `Executors terror radius`
- `V4 Trials`
- `Respawn Cards`
- `Assembling`
- `Deteriorer`

## Frontera con gameplay

`SiegeKnowledgeData` es referencia client-side.

Nunca debe:

- cambiar stats;
- cambiar raza;
- conceder objetos;
- alterar energía;
- registrar inventarios;
- crear recetas;
- modificar el servidor;
- convertir automáticamente lore en regla jugable.

## Futuro

El modelo está preparado para ampliar más temas conforme se siga revisando el corpus: Ejecutores individuales, Trials concretos, estructuras, bosses, razas, dimensiones, reliquias, economía y sistemas todavía incompletos.

La prioridad no es llenar categorías: es **añadir únicamente información general que le sirva a un jugador nuevo o a alguien que quiere aprender un tema concreto**.

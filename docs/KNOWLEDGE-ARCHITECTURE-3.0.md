# SIEGE 3.00 — Arquitectura del conocimiento

## Objetivo

SIEGE 3.00 incorpora conocimiento recuperado del Discord de Eternal Craft sin convertirlo en una verdad única ni mezclarlo con el estado actual del jugador.

El mod mantiene cinco dominios distintos:

- **Intel**: dossiers de unidades y amenazas.
- **Archivo / Arsenal**: lore general, contexto, objetos y material curado.
- **Manual de Campo**: estados, heridas, misiones y protocolos operativos.
- **Knowledge Vault**: estado actual registrado + enciclopedia histórica/source-aware de Eternal Craft.
- **Command / Diagnostics**: estado técnico del cliente; nunca gameplay/lore.

## Regla principal

`SIEGE ACTUAL != ETERNAL CRAFT — ENCICLOPEDIA`

La zona CURRENT responde: “¿qué está registrado de mi partida ahora?”.
La zona ENCYCLOPEDIA responde: “¿qué se recuperó del historial del servidor y con qué confianza?”.

Un dato histórico nunca sustituye silenciosamente un dato actual. Un dato posible nunca se muestra como confirmado.

## Fuente del primer corpus 3.00

La investigación recuperada declara:

- 488 archivos procesados.
- 251.065 mensajes.
- 72.384 mensajes de Alex.
- 29.605 mensajes de Santi.
- 37.435 replies reconstruidas.
- 153 prompts clasificados.
- 35 documentos temáticos.
- transcripciones completas y un índice SQLite/FTS.

Estos números describen el corpus; no implican que cada mensaje sea correcto.

## Jerarquía de confianza

1. `CURRENT_CONFIRMED`: dato del bloc actual del jugador.
2. `ALEX_CONFIRMED`: explicación directa atribuida a Alex/staff en la investigación.
3. `SYSTEM_OBSERVED`: resultado mostrado/observado.
4. `PLAYER_EXPERIENCE`: experiencia de jugador sin autoridad de staff.
5. `HISTORICAL`: dato real de una etapa antigua que puede haber cambiado.
6. `UNCONFIRMED`: mención posible, incompleta o pendiente.
7. `CONTRADICTION`: dos fuentes no reconciliadas.

Los colores de la UI expresan confianza, no rareza ni amenaza.

## Spoiler guard

El Knowledge Vault abre en **SIEGE ACTUAL**. La Enciclopedia no se muestra por defecto.

Entradas que pueden adelantar mecánicas usan `spoiler=true`. En ese estado:

- el título sigue visible;
- el resumen sigue visible;
- la fuente/confianza sigue visible;
- el cuerpo detallado permanece oculto hasta `REVELAR ARCHIVO`;
- el reveal dura sólo durante la sesión de esa pantalla.

Supervivencia puede mostrar resúmenes críticos aunque el cuerpo esté bloqueado, porque su función es evitar pérdidas graves.

## Dominios preparados

El modelo admite:

- Overview
- Progression
- Executors
- Trials
- Structures
- Bosses
- Missions
- NPCs
- Races
- Pets / Summons
- Abilities
- Energies
- Meditation
- Magic
- Items
- Crafting
- Assembling / Cyborgs
- Relics
- Dimensions
- Rituals
- Sabotage / Hacking
- Death / Injury / Revive
- Raids / Events
- Factions
- Economy / Wins
- Prompts / Commands
- Action → Consequence
- Hidden Mechanics
- Mistakes
- Alex Advice
- History
- Mysteries
- Contradictions
- Sources

No se crean fichas vacías fingiendo conocimiento. Los dominios existen para incorporar futuras extracciones con evidencia.

## Deep links

Operations Hub indexa:

- rutas;
- Intel;
- Arsenal;
- Knowledge.

Un resultado KNOWLEDGE guarda `knowledgeId`, por lo que `Geography Table`, `Rust Guard`, `Daemonium Kit`, `meditación`, etc. pueden abrir directamente su ficha.

## Relaciones

Cada ficha puede almacenar IDs relacionados. Ejemplo:

`current-rust-guard`
→ `alex-raid-oxidation`
→ `prompt-precision`
→ `alex-adaptation`

Las relaciones no significan causalidad. Sólo indican que conviene consultar esas entradas juntas.

## Política de historia

Cuando una mecánica cambia:

- el dato antiguo conserva fecha/fuente;
- pasa a `HISTORICAL` cuando corresponde;
- el dato nuevo se añade por separado;
- nunca se reescribe la historia para parecer consistente.

Ejemplo ya implementado: duración antigua extrema de Deteriorer permanece separada de su snapshot actual.

## Política de incertidumbre

Una cifra marcada como posible no se usa como cifra de cálculo actual.

Ejemplos 3.00:

- +6% RE/5 turnos → `UNCONFIRMED`.
- posible reset posterior a V4 → `UNCONFIRMED`.
- Filter Rod → `MYSTERIES`.
- posible riesgo de cruce con Halo → `MYSTERIES`.

## Frontera con gameplay

`SiegeKnowledgeData` es referencia client-side.

Nunca debe:

- cambiar atributos;
- cambiar raza;
- tocar RE;
- conceder objetos;
- crear recetas;
- modificar el servidor;
- afirmar reglas no respaldadas sólo para rellenar categorías.

## Futuro

La siguiente expansión natural es importar más documentos temáticos del corpus de 251.065 mensajes, especialmente Ejecutores, Trials, Estructuras, Bosses, Razas, Items, Magia y progresión, manteniendo la misma trazabilidad por fecha/fuente/confianza.

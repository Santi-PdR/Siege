# SIEGE 2.50 · Centro de Operaciones

El Centro de Operaciones no crea un nuevo depósito de datos. Funciona como capa de navegación y búsqueda sobre las fuentes que ya existen.

## Dominios
- **Intel:** dossiers de unidades, categorías, amenaza, HP, armamento y UNKNOWN.
- **Archivo:** qué es SIEGE/Eternal Craft, 2044, facciones, El Núcleo, Gates/Rifts, dificultad, crónicas e inspiraciones.
- **Arsenal:** objetos, equipamiento y evidencia multimedia; Third Justice permanece aquí.
- **Manual de Campo:** estados de muerte/heridas, trauma, misiones y protocolos.
- **Despliegue:** servidor oficial, estado, compatibilidad, latencia y conexión.
- **Centro de Comando:** perfil y estado general del cliente.
- **Diagnóstico:** problemas técnicos y recuperaciones explícitas.
- **Ajustes:** configuración visual, movimiento, audio, Intel, accesibilidad y fondos.
- **Fondos:** galería de escenas normales.

## Búsqueda
`SiegeOperationsIndex` construye un índice local en memoria. No consulta Internet y no modifica datos. Los resultados de Intel pueden abrir directamente un dossier; los resultados de Arsenal y rutas llevan al dominio correspondiente.

La normalización elimina diferencias de mayúsculas y diacríticos para que `Núcleo` y `nucleo` se comporten igual. La puntuación prioriza coincidencia exacta, prefijo, título, identificador, subtítulo y finalmente texto auxiliar.

## Easter eggs
Los easter eggs no son escenas públicas. `SiegeEasterEggVault` reserva identificadores que no pueden formar parte de `SiegeSceneCatalog`. Tempest Jutcherson queda fuera de rotación, galería y etiquetas normales del menú.

## Límites
- El hub no inventa clima, eventos de servidor, amigos, chat ni gameplay.
- No fusiona Archivo, Intel, Arsenal o Manual en una sola base de datos.
- No reemplaza callbacks vanilla de conexión.
- No reintroduce controles retirados de Intel.
- No añade atajos globales nuevos.

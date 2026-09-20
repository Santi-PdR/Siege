# SIEGE 0.40.3

Corrección de pulido para las pantallas vanilla tematizadas y el acceso a Intel.

## Interfaz nativa

- La cabecera SIEGE de las pantallas vanilla se renderiza una sola vez.
- Se mantiene un único título centrado, con icono de familia y estado contextual a la derecha cuando hay espacio.
- La reserva lateral del título se adapta al ancho disponible para evitar solapes con el estado.
- Se conserva la franja contextual y el efecto de barrido sin volver a dibujar una segunda cabecera encima.

## Sistema e Intel

- El botón de Sistema ya no tiene `0.40` escrito a mano: usa la versión real del mod y una versión compacta en anchos pequeños.
- El acceso al Archivo Intel deja de describirse como historial de SIEGE y vuelve a la idea vigente de conocimiento operativo actual.
- El tooltip ahora describe estados, misiones, equipo, avisos actuales, unidades y protocolos operativos.

## Regresión

- Se añadieron comprobaciones automáticas para impedir que regrese el segundo render de cabecera.
- También se protege el versionado dinámico y el lenguaje de conocimiento actual en Intel.

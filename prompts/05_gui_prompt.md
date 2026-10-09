# Prompt 05: componente gui

**Diseño adjunto:** `doc/gui_design.md`

```text
Genera la interfaz web del prototipo Sprint 0: una pantalla única "Última
medición".

Respeta exactamente el diseño adjunto (doc/gui_design.md): la descripción de la
pantalla, el diseño del controlador, las aclaraciones y las reglas generales
forman parte de este prompt.

Medicion = (id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text)

Tecnología: HTML, CSS y JavaScript para el navegador, sin librerías externas, en
ficheros separados. Tests con Jest y jsdom.

Ficheros a generar, dentro de src/gui/: index.html, estilos.css, controladorUX.js,
main.js (arranque mínimo) y controladorUX.test.js.

Directrices:
- Clase ControladorUX(logica) con iniciar(), actualizar() y el método estático
  formatearMedicion(m), que no accede al DOM.
- La interfaz obtiene datos solo a través de la lógica del navegador
  (recuperarUltimaMedicion); no hace peticiones HTTP ni tiene lógica de negocio.
- Cuatro estados: cargando ("Cargando...", botón desactivado), con datos
  (tarjeta con tipo, valor, fecha dd/mm/aaaa y hora hh:mm en la zona horaria del
  navegador), sin datos ("Todavía no hay mediciones") y error ("No se pudo
  obtener la medición").
- Diseño sencillo y adaptable a pantallas pequeñas. Sin pantallas ni
  funcionalidades fuera del diseño.

Comentarios: cabecera con propósito, diseño lógico entre líneas de guiones,
parámetros, retorno y errores en cada función.

Tests (con la lógica fake y el index.html real cargado en jsdom): formato de
formatearMedicion, representación de una medición, estado sin datos, estado de
error, estado cargando con el botón desactivado, actualización al pulsar el botón.
```

**Revisión del resultado:** tests en verde; los `id` del HTML coinciden con los
del controlador; la interfaz no importa nada del servidor.

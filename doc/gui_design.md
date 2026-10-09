# Diseño del componente: gui

## 1. Diseño del componente

Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text )

### Descripción de la pantalla

Pantalla única "Última medición":

- Título de la aplicación.
- Tarjeta con la última medición: tipo de contaminante, valor, fecha y hora.
- Botón "Actualizar".
- Zona de mensaje de estado, con cuatro estados:
  - cargando: "Cargando...";
  - con datos: se muestra la tarjeta;
  - sin datos: "Todavía no hay mediciones";
  - error: "No se pudo obtener la medición".
- Mientras se cargan los datos, el botón "Actualizar" está desactivado.

### Diseño lógico

```text
 --------- ControladorUX ------------------------
 |
 |  logica: LogicaNavegador
 |
 |
 logica: LogicaNavegador --> ControladorUX() -->
 |
 |
 iniciar() -->
 |
 |
 actualizar() -->
 |
 |
 m: Medicion --> formatearMedicion() --x
   texto: (tipo: Text, valor: Text, fecha: Text, hora: Text) <--
 |
 ------------------------------------------------
```

## 2. Aclaraciones del diseño

- `LogicaNavegador` es la lógica de negocio del cliente definida en `frontend_business_logic_design.md`: puede ser la implementación real (`LogicaNavegador`, que consulta al servidor) o la fake (`LogicaNavegadorFake`). Ambas tienen la misma interfaz, por lo que cambiar de una a otra no modifica la interfaz gráfica.
- La interfaz obtiene los datos solo a través de `LogicaNavegador`. No accede a la base de datos, no contiene código de comunicación con el servidor y no contiene lógica de negocio.
- `iniciar()` asocia el botón "Actualizar" a `actualizar()` y carga la última medición al abrir la página.
- `actualizar()` pasa por los estados de la pantalla: cargando, y después con datos, sin datos o error.
- `formatearMedicion()` separa `fechaHora` en fecha y hora, en la zona horaria del navegador. No accede al DOM.
- HTML, CSS y JavaScript están en ficheros separados.
- La interfaz es sencilla y se adapta a pantallas pequeñas (responsive).
- No se añaden pantallas ni funcionalidades que no aparezcan en este diseño.

## 3. Reglas generales

- Lenguaje de programación: HTML, CSS y JavaScript para el navegador, sin librerías externas.
- El encabezado de cada función o método incluye su diseño lógico dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`), con propósito, parámetros, tipos, retorno y errores.
- El código debe ser lo más claro y autoexplicativo posible, de modo que apenas requiera comentarios adicionales.
- Se generan tests automáticos (Jest con jsdom), usando la lógica fake, para:
  - la representación de una medición (tipo, valor, fecha y hora);
  - el formato de `formatearMedicion()`;
  - el estado sin datos;
  - el estado de error;
  - el estado cargando y el botón desactivado durante la carga;
  - la actualización al pulsar el botón.
- El código no añade funcionalidades que no estén incluidas en el diseño.

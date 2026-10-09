# Diseño del componente: frontend_business_logic

## 1. Diseño del componente

Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text )

### Interfaz que consume la interfaz gráfica

Es un subconjunto de la lógica de negocio del servidor (`business_logic_design.md`), con firmas idénticas.

```text
 --------- FrontendBusinessLogic (interfaz) -----
 |
 |
 Medicion <-- recuperarUltimaMedicion() <--
 |
 |
 [Medicion] <-- recuperarMediciones() <--
 |
 ------------------------------------------------
```

### Implementación real (proxy del servidor)

```text
 --------- LogicaNavegador ----------------------
 |
 |  urlBase: Text
 |
 |
 urlBase: Text --> LogicaNavegador() -->
 |
 |
 Medicion <-- recuperarUltimaMedicion() <--
 |
 |
 [Medicion] <-- recuperarMediciones() <--
 |
 ------------------------------------------------
```

### Implementación fake (datos simulados)

```text
 --------- LogicaNavegadorFake ------------------
 |
 |  mediciones: [Medicion]
 |  falla: B
 |
 |
 mediciones: [Medicion], falla: B --> LogicaNavegadorFake() -->
 |
 |
 Medicion <-- recuperarUltimaMedicion() <--
 |
 |
 [Medicion] <-- recuperarMediciones() <--
 |
 ------------------------------------------------
```

## 2. Aclaraciones del diseño

- La interfaz gráfica (`gui`) usa solo esta interfaz y nunca se comunica directamente con el servidor. Las dos implementaciones se pueden intercambiar sin cambiar nada en la interfaz gráfica.
- Solo se especifican las dos operaciones de lectura que usa la interfaz gráfica. Los nombres, los tipos de los parámetros y los tipos de retorno son los de `LogicaMediciones` en `business_logic_design.md`.
- En JavaScript las operaciones son asíncronas (devuelven una Promise). El diseño lógico omite esa mecánica.
- `LogicaNavegador` es un proxy: encapsula la comunicación con el servidor (componente `communication`) y presenta a la interfaz gráfica la misma interfaz de dominio que la lógica de negocio:
  - `recuperarUltimaMedicion()` devuelve la `Medicion` más reciente, o `null` si no hay mediciones;
  - `recuperarMediciones()` devuelve la lista de mediciones, vacía si no hay ninguna;
  - ante un fallo de comunicación o una respuesta que no tenga la forma esperada, las operaciones terminan con un error.
- `urlBase` es la dirección del servidor. Si está vacía, se usa el mismo origen desde el que se cargó la página.
- `LogicaNavegadorFake` simula al servidor para desarrollar la interfaz sin depender de él. No hace peticiones:
  - si no se le pasa ninguna lista de mediciones, usa un conjunto de ejemplo con al menos una medición de CO2;
  - `recuperarUltimaMedicion()` devuelve la medición de mayor `fechaHora` y, si hay empate, la de mayor `id`; `null` si no hay mediciones;
  - `recuperarMediciones()` devuelve una lista vacía si no hay mediciones;
  - si `falla` es `true`, ambas operaciones terminan con un error simulado, para probar cómo reacciona la interfaz.
- Ninguna de las dos implementaciones accede al DOM, contiene HTML ni valida reglas de negocio.
- Los datos usan los mismos nombres de campo que `Medicion` y `fechaHora` en formato ISO 8601.
- No se añaden operaciones que no aparezcan en este diseño.

## 3. Reglas generales

- Lenguaje de programación: JavaScript para el navegador, sin librerías externas (`LogicaNavegador` usa `fetch`). Debe poder cargarse también desde Node.js para los tests.
- El encabezado de cada función o método incluye su diseño lógico dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`), con propósito, parámetros, tipos, retorno y errores.
- El código debe ser lo más claro y autoexplicativo posible, de modo que apenas requiera comentarios adicionales.
- Se generan tests automáticos (Jest) para `LogicaNavegador`, con `fetch` simulado:
  - última medición con datos, sin mediciones (`null`) y ante un fallo del servidor;
  - fallo de red y respuesta que no es JSON válido;
  - lista de mediciones con datos y vacía;
  - la dirección que se llama con `urlBase` vacío y con `urlBase` indicado.
- Se generan tests automáticos (Jest) para `LogicaNavegadorFake`:
  - última medición, desempate por `id` y comparación de instantes con zonas horarias distintas;
  - sin mediciones (`null`) y lista vacía;
  - uso de los datos de ejemplo cuando no se pasa ninguna lista;
  - error simulado en ambas operaciones.
- El código no añade funcionalidades que no estén incluidas en el diseño.

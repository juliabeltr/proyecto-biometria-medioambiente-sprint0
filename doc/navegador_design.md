# Diseño del componente: navegador

## 1. Diseño del componente

Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text )

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

## 2. Aclaraciones del diseño

- Esta es la lógica real del navegador. Obtiene las mediciones llamando al API REST (componente `rest`) y las devuelve a la interfaz.
- Tiene las mismas operaciones y devuelve datos con la misma forma que `LogicaNavegadorFake`, por lo que la interfaz puede usar una u otra sin cambios.
- `recuperarUltimaMedicion()` llama a `GET /mediciones/ultima`:
  - código 200: devuelve la `Medicion` recibida;
  - código 404: devuelve `null` (no hay mediciones);
  - cualquier otro código, fallo de red o respuesta que no sea JSON válido: termina con un error.
- `recuperarMediciones()` llama a `GET /mediciones` y devuelve la lista recibida, vacía si no hay mediciones. Ante cualquier código distinto de 200, fallo de red o JSON no válido, termina con un error.
- `urlBase` es la dirección del servidor. Si está vacía, se usa el mismo origen desde el que se cargó la página.
- No accede al DOM, no contiene HTML y no valida reglas de negocio.
- En JavaScript las operaciones son asíncronas (devuelven una Promise). El diseño lógico omite esa mecánica.
- No se añaden operaciones que no aparezcan en este diseño.

## 3. Reglas generales

- Lenguaje de programación: JavaScript para el navegador, sin librerías externas (usa `fetch`). Debe poder cargarse también desde Node.js para los tests.
- El encabezado de cada función o método incluye su diseño lógico dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`), con propósito, parámetros, tipos, retorno y errores.
- El código debe ser lo más claro y autoexplicativo posible, de modo que apenas requiera comentarios adicionales.
- Se generan tests automáticos (Jest) con `fetch` simulado para:
  - última medición con código 200;
  - última medición con código 404 (devuelve `null`);
  - código 500, fallo de red y JSON no válido (error);
  - lista de mediciones con datos y vacía;
  - la URL llamada con `urlBase` vacío y con `urlBase` indicado.
- El código no añade funcionalidades que no estén incluidas en el diseño.

# Diseño del componente: navegador_fake

## 1. Diseño del componente

Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text )

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

- Esta lógica simula en el navegador a la lógica real. Permite desarrollar la interfaz web sin necesitar el servidor REST.
- No hace peticiones al servidor, no accede al DOM y no contiene HTML.
- Tiene las mismas operaciones y devuelve datos con la misma forma que la lógica real del navegador, para poder sustituirla sin cambiar la interfaz.
- Los datos simulados usan los mismos nombres de campo que `Medicion` y `fechaHora` en formato ISO 8601.
- Si no se le pasa ninguna lista de mediciones, usa un conjunto de ejemplo con al menos una medición de CO2.
- `recuperarUltimaMedicion()` devuelve la medición de mayor `fechaHora`. Si hay empate, la de mayor `id`. Si no hay mediciones, indica ausencia de datos (`null`).
- `recuperarMediciones()` devuelve una lista vacía si no hay mediciones.
- Si `falla` es `true`, ambas operaciones terminan con un error simulado. Sirve para probar cómo reacciona la interfaz ante un fallo.
- En JavaScript las operaciones son asíncronas (devuelven una Promise), como lo serán las de la lógica real. El diseño lógico omite esa mecánica.
- No se añaden operaciones que no aparezcan en este diseño.

## 3. Reglas generales

- Lenguaje de programación: JavaScript para el navegador, sin librerías externas. Debe poder cargarse también desde Node.js para los tests.
- El encabezado de cada función o método incluye su diseño lógico dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`), con propósito, parámetros, tipos, retorno y errores.
- El código debe ser lo más claro y autoexplicativo posible, de modo que apenas requiera comentarios adicionales.
- Se generan tests automáticos (Jest) para:
  - recuperar la última medición con datos y el desempate por `id`;
  - recuperar la última medición sin mediciones (`null`);
  - recuperar varias mediciones y la lista vacía;
  - usar los datos de ejemplo cuando no se pasa ninguna lista;
  - el error simulado en ambas operaciones.
- El código no añade funcionalidades que no estén incluidas en el diseño.

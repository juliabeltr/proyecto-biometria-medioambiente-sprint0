# Prompt 03: componente communication (API REST)

**Diseño adjunto:** `doc/communication_design.md`

```text
Genera el servidor REST del prototipo Sprint 0 de una aplicación de
monitorización de contaminación atmosférica.

Debes respetar exactamente el diseño adjunto (doc/communication_design.md): sus tres
secciones forman parte de este prompt.

Medicion = (id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text)

Tecnología: JavaScript (Node.js) con Express. Tests con Jest y Supertest.

Ficheros a generar, dentro de src/communication/: servidorREST.js (clase ServidorREST),
servidorREST.test.js e index.js (arranque mínimo, sin lógica; puerto PORT o 8080).

Directrices:
- ServidorREST recibe una LogicaMediciones en el constructor y expone la
  aplicación Express para poder probarla con Supertest.
- Rutas, y solo estas: POST /mediciones, GET /mediciones, GET /mediciones/ultima.
- Respuestas: POST correcto 201 {guardada:true}; POST con JSON mal formado,
  campo ausente o datos rechazados 400 {error}; GET /mediciones 200 con lista;
  GET /mediciones/ultima 200 con objeto o 404 {error} si no hay; excepción 500
  {error} sin trazas ni detalles internos.
- Solo comprueba la forma de la petición (JSON válido y cinco campos). Validar
  valores es trabajo de la lógica. El id recibido se ignora.
- Sin lógica de negocio ni acceso a la base de datos. Sin rutas fuera del diseño.

Comentarios: cabecera con propósito, diseño lógico entre líneas de guiones,
parámetros, retorno y errores en cada función. Código autoexplicativo.

Tests con una lógica simulada para las tres rutas: POST correcto, JSON mal
formado, campo ausente, datos rechazados y fallo interno; GET con datos, lista
vacía y fallo; GET última con datos, sin mediciones y fallo; los 500 no contienen
trazas.

Muestra el árbol de ficheros, cómo ejecutar los tests y cómo arrancar el servidor.
```

**Revisión del resultado:** tests en verde; solo las tres rutas del diseño;
códigos de respuesta de la tabla; el servidor no importa `database` ni `business_logic`.

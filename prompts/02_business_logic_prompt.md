# Prompt 02: componente business_logic

**Diseño adjunto:** `doc/business_logic_design.md`

```text
Genera la lógica de negocio del backend del prototipo Sprint 0 de una
aplicación de monitorización de contaminación atmosférica.

Debes respetar exactamente el diseño adjunto (doc/business_logic_design.md): sus tres
secciones forman parte de este prompt.

Medicion = (id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text)
TipoMedicion = { CO2, TEMP, RUIDO }

Tecnología: JavaScript (Node.js). Tests con Jest.

Ficheros a generar, dentro de src/business_logic/: logicaMediciones.js y
logicaMediciones.test.js. No borres las dependencias existentes del package.json.

Directrices:
- La clase LogicaMediciones expone solo el constructor, guardarMedicion(),
  recuperarUltimaMedicion() y recuperarMediciones().
- El constructor recibe un RepositorioMediciones (componente bd). No importes ni
  instancies la BD dentro de la lógica.
- guardarMedicion(m) devuelve false si los datos no son válidos y true si se
  guarda; no lanza excepción por datos inválidos. Validaciones: tipo en
  TipoMedicion; valor número finito; latitud entre -90 y 90; longitud entre
  -180 y 180; fechaHora fecha válida en ISO 8601. Campo ausente = inválido. El
  id se ignora.
- Si el repositorio lanza un error, se propaga sin capturarlo.
- recuperarUltimaMedicion() devuelve la medición o null; recuperarMediciones()
  devuelve una lista (vacía si no hay).
- Sin HTTP, JSON ni interfaz gráfica. Sin operaciones fuera del diseño.

Comentarios: cabecera con propósito, diseño lógico entre líneas de guiones,
parámetros, retorno y errores en cada función. Código autoexplicativo.

Tests con un repositorio simulado: guardar válida (llama a insertar), rechazo de
cada dato inválido (no llama a insertar), límites exactos de latitud y longitud,
última medición y caso sin mediciones, varias y lista vacía, propagación de
errores del repositorio.

No implementes nada que no esté en el diseño. Muestra el árbol de ficheros y
cómo ejecutar los tests.
```

**Revisión del resultado:** tests en verde; la lógica no importa `database`; datos
inválidos devuelven `false` sin llamar al repositorio; los fallos se propagan.

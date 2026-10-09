# Prompt 01: componente database

**Diseño adjunto:** `doc/database_design.md`

```text
Genera el componente de base de datos del prototipo Sprint 0 de una aplicación
de monitorización de contaminación atmosférica.

Debes respetar exactamente el diseño adjunto (doc/database_design.md): sus tres
secciones (diseño, aclaraciones y reglas generales) forman parte de este prompt.

Entidad principal:
Medicion = (id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text)

Tecnología: JavaScript (Node.js) con SQLite (better-sqlite3). Tests con Jest.

Ficheros a generar, dentro de src/database/:
- repositorioMediciones.js: la clase RepositorioMediciones del diseño.
- repositorioMediciones.test.js: los tests.
- package.json en la raíz, con las dependencias y el script "test".

Directrices:
- No añadas campos, tablas ni métodos que no aparezcan en el diseño.
- La clase expone solo el constructor, insertar(), recuperarUltima() y
  recuperarTodas().
- El constructor recibe la ruta del fichero de la BD (":memory:" para tests) y
  crea la tabla MEDICIONES si no existe.
- id es clave primaria autoincremental; los demás campos son NOT NULL.
- insertar() ignora el id recibido y devuelve el id asignado.
- recuperarUltima() devuelve la de mayor fechaHora y, en empate, la de mayor id;
  null si no hay ninguna.
- recuperarTodas() devuelve una lista, vacía si no hay mediciones.
- Consultas parametrizadas. Sin validación de negocio, HTTP ni JSON.
- Si SQLite falla, lanza un Error claro sin exponer la consulta SQL.

Comentarios: cada función lleva una cabecera con propósito, diseño lógico (entre
líneas de guiones), parámetros con tipos, retorno y errores. Código claro y
autoexplicativo.

Tests (BD en memoria): inserción e id, ids autoincrementales, última medición,
desempate por id, null sin mediciones, varias mediciones y lista vacía, datos
inválidos y errores de base de datos.

No implementes nada que no esté en el diseño ni modifiques la arquitectura.
Al terminar, muestra el árbol de ficheros y cómo ejecutar los tests.
```

**Revisión del resultado:** tests en verde; métodos y nombres iguales a los del
diseño; consultas parametrizadas; sin lógica de negocio en el componente.

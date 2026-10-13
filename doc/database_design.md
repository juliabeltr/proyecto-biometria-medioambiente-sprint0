# Diseño del componente: database

## 1. Diseño del componente

### Tabla relacional

```text
====================================================================================
TABLE: MEDICIONES

DESCRIPTION: Mediciones ambientales recibidas por el sistema. Cada fila es una
medición de un tipo de contaminante, hecha por un sensor, en un lugar y un instante.

COLUMNS:

+ sensorId | TEXT | NOT NULL
+ tipo | TEXT | NOT NULL
+ valor | REAL | NOT NULL
+ ubicacion | POINT | NOT NULL
+ fechaHora | TEXT | NOT NULL

PRIMARY KEY: sensorId, fechaHora

FOREIGN KEYS:

+ (ninguna)

CONSTRAINTS:

+ sensorId identifica al sensor que hizo la medición y no puede estar vacío (lo comprueba la lógica de negocio).
+ ubicacion es un punto geográfico (latitud y longitud) y se guarda en una sola columna.
+ tipo solo admite los valores CO2, TEMP o RUIDO (lo comprueba la lógica de negocio).
+ fechaHora se guarda en formato ISO 8601 (UTC).
+ La clave primaria es compuesta: no puede haber dos mediciones del mismo sensor (sensorId) en el mismo instante (fechaHora).
====================================================================================
```

### Rutinas de acceso a datos

Punto = ( latitud: R, longitud: R )

Medicion = ( sensorId: Text, tipo: Text, valor: R, ubicacion: Punto, fechaHora: Text )

Cada `Medicion` corresponde a una fila de la tabla `MEDICIONES`, con una columna por campo y el mismo nombre. El campo `ubicacion` (un `Punto`) se guarda entero en la columna `ubicacion`, de tipo `POINT`.

```text
                  --------- RepositorioMediciones ---------------
                  |
                  |  conexion: conexión a la base de datos
                  |
                  |
   ruta: Text --> RepositorioMediciones() -->
                  |
                  |
  m: Medicion --> insertar() -->
 resultado: B <--
                  |
                  |
     Medicion <-- recuperarUltima() <--
                  |
                  |
   [Medicion] <-- recuperarTodas() <--
                  |
                  -----------------------------------------------
```

## 2. Aclaraciones del diseño

- La base de datos es SQLite. `ruta` es la ruta del fichero de la base de datos, o `:memory:` para una base en memoria (la usan los tests). Al crear el repositorio se crea la tabla `MEDICIONES` si no existe.
- `insertar()` devuelve `true` si guarda la medición y `false` si ya existe una medición con la misma clave primaria (mismo `sensorId` y misma `fechaHora`), en cuyo caso no modifica la base de datos.
- `ubicacion` es una única columna de tipo `POINT`, el tipo específico para coordenadas geográficas. SQLite no tiene un tipo `POINT` nativo, así que la columna se declara como `POINT` y el punto se guarda en formato WKT (texto estándar de datos geográficos): `POINT(longitud latitud)`, por ejemplo `POINT(-0.1806 38.9675)`. En WKT el primer número es la longitud (eje X) y el segundo la latitud (eje Y). `RepositorioMediciones` convierte entre `Punto` y este texto al insertar y al recuperar, de modo que el resto del sistema solo ve `Punto`. En una base con tipos geográficos nativos (PostGIS, MySQL) la columna `POINT` se usaría tal cual.
- `sensorId` es un texto que identifica al sensor. Con la placa del Sprint 0 es la dirección Bluetooth con la que emite sus anuncios.
- `fechaHora` se guarda como texto en formato ISO 8601, por lo que el orden alfabético coincide con el cronológico cuando todas las fechas están en UTC.
- La "última medición" es la de mayor `fechaHora`. Si hay empate, la de mayor `sensorId`. Si no hay mediciones, `recuperarUltima()` indica ausencia de datos (`null`).
- `recuperarTodas()` devuelve las mediciones ordenadas por `fechaHora` y, si hay empate, por `sensorId`, y una lista vacía si no hay ninguna.
- Si falta algún campo obligatorio, `insertar()` termina con error antes de tocar la base de datos.
- Este componente solo guarda y recupera datos. No valida reglas de negocio (rangos, tipos de contaminante) y no conoce ningún mecanismo de comunicación.
- Las consultas a la base de datos son parametrizadas.
- Si falla la base de datos, se termina con un error claro que no revela la consulta SQL.
- No se añaden tablas, columnas ni operaciones que no aparezcan en este diseño.

## 3. Reglas generales

- Lenguaje de programación: JavaScript (Node.js), con SQLite como base de datos (biblioteca `better-sqlite3`).
- El encabezado de cada función o método incluye su diseño lógico dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`), con propósito, parámetros, tipos, retorno y errores.
- El código debe ser lo más claro y autoexplicativo posible, de modo que apenas requiera comentarios adicionales.
- Se generan tests automáticos (Jest, con la base de datos en memoria) para las funciones clave:
  - inserción correcta de una medición (devuelve `true`);
  - la clave primaria es (`sensorId`, `fechaHora`);
  - una medición repetida (mismo sensor y mismo instante) no se guarda y devuelve `false`, y sí se guardan mediciones del mismo instante de sensores distintos o del mismo sensor en instantes distintos;
  - el `sensorId` y la `ubicacion` (latitud y longitud, también negativas) se recuperan idénticos a como se guardaron;
  - la columna `ubicacion` guarda el punto en formato `POINT(longitud latitud)`;
  - recuperación de la última medición, desempate por `sensorId` y caso sin mediciones;
  - recuperación de varias mediciones y de la lista vacía;
  - gestión de datos inválidos (campo ausente o nulo, `ubicacion` sin latitud o sin longitud);
  - gestión de errores de base de datos.
- El código no añade funcionalidades que no estén incluidas en el diseño.

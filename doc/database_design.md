# Diseño del componente: database

## 1. Diseño del componente

### Tabla relacional

```text
====================================================================================
TABLE: MEDICIONES

DESCRIPTION: Mediciones ambientales recibidas por el sistema. Cada fila es una
medición de un tipo de contaminante en un lugar y un instante.

COLUMNS:

+ id | INTEGER | NOT NULL | Auto-Increment
+ tipo | TEXT | NOT NULL
+ valor | REAL | NOT NULL
+ latitud | REAL | NOT NULL
+ longitud | REAL | NOT NULL
+ fechaHora | TEXT | NOT NULL

PRIMARY KEY: id

FOREIGN KEYS:

+ (ninguna)

CONSTRAINTS:

+ tipo solo admite los valores CO2, TEMP o RUIDO (lo comprueba la lógica de negocio).
+ fechaHora se guarda en formato ISO 8601 (UTC).
+ id lo asigna la base de datos y no se puede asignar desde fuera.
====================================================================================
```

### Rutinas de acceso a datos

Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text )

Cada `Medicion` corresponde a una fila de la tabla `MEDICIONES`, con una columna por campo y el mismo nombre.

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
       id: N <--
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
- `insertar()` ignora el `id` de la medición recibida, porque lo asigna la base de datos, y devuelve el `id` asignado.
- `fechaHora` se guarda como texto en formato ISO 8601, por lo que el orden alfabético coincide con el cronológico cuando todas las fechas están en UTC.
- La "última medición" es la de mayor `fechaHora`. Si hay empate, la de mayor `id`. Si no hay mediciones, `recuperarUltima()` indica ausencia de datos (`null`).
- `recuperarTodas()` devuelve las mediciones ordenadas por `id`, y una lista vacía si no hay ninguna.
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
  - inserción correcta de una medición y devolución de su `id`;
  - `id` autoincrementales y distintos;
  - recuperación de la última medición, desempate por `id` y caso sin mediciones;
  - recuperación de varias mediciones y de la lista vacía;
  - gestión de datos inválidos (campo ausente o nulo);
  - gestión de errores de base de datos.
- El código no añade funcionalidades que no estén incluidas en el diseño.

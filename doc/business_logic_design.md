# Diseño del componente: business_logic

## 1. Diseño del componente

Punto = ( latitud: R, longitud: R )

Medicion = ( sensorId: Text, tipo: Text, valor: R, ubicacion: Punto, fechaHora: Text )

TipoMedicion = { CO2, TEMP, RUIDO }

```text
                                        --------- LogicaMediciones ---------------------
                                        |
                                        |  repositorio: RepositorioMediciones
                                        |
                                        |
 repositorio: RepositorioMediciones --> LogicaMediciones() -->
                                        |
                                        |
                        m: Medicion --> guardarMedicion() -->
                       resultado: B <--
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

- La lógica de negocio valida y gestiona las mediciones. Es independiente de cómo se acceda a ella: sus firmas usan solo tipos de dominio y no depende de ningún mecanismo de comunicación ni de interfaz de usuario.
- Persistencia: las mediciones se guardan en la tabla `MEDICIONES` definida en `database_design.md`, cuyas columnas son `sensorId`, `tipo`, `valor`, `ubicacion` (de tipo `POINT`) y `fechaHora`, una por cada campo de `Medicion`; su clave primaria es (`sensorId`, `fechaHora`). El acceso se hace solo mediante `RepositorioMediciones` (componente `database`), que se recibe en el constructor para que los tests puedan sustituirlo por un repositorio simulado:
  - `guardarMedicion()` usa `RepositorioMediciones.insertar()`;
  - `recuperarUltimaMedicion()` usa `RepositorioMediciones.recuperarUltima()`;
  - `recuperarMediciones()` usa `RepositorioMediciones.recuperarTodas()`.
- Validaciones de `guardarMedicion()` (la columna `tipo` solo recibe valores de `TipoMedicion`):
  - `sensorId` debe ser un texto no vacío.
  - `tipo` debe pertenecer a `TipoMedicion`.
  - `valor` debe ser un número finito.
  - `ubicacion` debe ser un `Punto`: su `latitud` debe estar entre -90 y 90 y su `longitud` entre -180 y 180.
  - `fechaHora` debe ser una fecha y hora válidas en formato ISO 8601, con día existente en el calendario.
  - Si falta algún campo, la medición no es válida.
- `guardarMedicion()` devuelve `false` si los datos no son válidos (sin tocar la base de datos) o si ya existe una medición del mismo `sensorId` en la misma `fechaHora` (el repositorio no la guarda), y `true` si la medición se ha guardado.
- Si falla la base de datos, el error se propaga sin capturarlo. Así, quien invoque a la lógica puede distinguir entre "datos no válidos" (resultado `false`) y "fallo del sistema" (error).
- `recuperarUltimaMedicion()` indica ausencia de datos (`null`) si no hay ninguna medición.
- `recuperarMediciones()` devuelve una lista vacía si no hay mediciones.
- No se añaden operaciones que no aparezcan en este diseño.

## 3. Reglas generales

- Lenguaje de programación: JavaScript (Node.js).
- El encabezado de cada función o método incluye su diseño lógico dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`), con propósito, parámetros, tipos, retorno y errores.
- El código debe ser lo más claro y autoexplicativo posible, de modo que apenas requiera comentarios adicionales.
- El código no importa ni referencia ningún componente de comunicación ni framework de transporte.
- Se generan tests automáticos (Jest) con un repositorio simulado para:
  - guardar una medición válida;
  - devolver `false` si el repositorio indica que la medición ya existía;
  - rechazar cada tipo de dato inválido (sensorId, tipo, valor, ubicacion, fecha) y los campos ausentes;
  - límites exactos de latitud y longitud, y ubicación sin latitud o sin longitud;
  - recuperar la última medición y el caso sin mediciones;
  - recuperar varias mediciones y el caso de lista vacía;
  - propagación de errores de la capa de datos.
- El código no añade funcionalidades que no estén incluidas en el diseño.

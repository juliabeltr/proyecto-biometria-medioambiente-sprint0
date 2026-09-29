# Diseño del componente: logica

## 1. Diseño del componente

Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text )

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

- La lógica de negocio valida y gestiona las mediciones. No contiene código HTTP, JSON ni interfaz gráfica.
- El acceso a datos se hace solo a través de `RepositorioMediciones` (componente `bd`), que se recibe en el constructor. Así los tests pueden sustituirlo por un repositorio simulado.
- Validaciones de `guardarMedicion()`:
  - `tipo` debe pertenecer a `TipoMedicion`.
  - `valor` debe ser un número finito.
  - `latitud` debe estar entre -90 y 90.
  - `longitud` debe estar entre -180 y 180.
  - `fechaHora` debe ser una fecha válida en formato ISO 8601.
- El campo `id` de la medición recibida se ignora: lo asigna la base de datos.
- `guardarMedicion()` devuelve `false` si los datos no son válidos, y `true` si la medición se ha guardado.
- Si falla la capa de datos, el error se propaga sin capturarlo. Así la capa REST distingue "datos inválidos" de "fallo interno".
- `recuperarUltimaMedicion()` indica ausencia de datos si no hay ninguna medición.
- `recuperarMediciones()` devuelve una lista vacía si no hay mediciones.
- No se añaden operaciones que no aparezcan en este diseño.

## 3. Reglas generales

- Lenguaje de programación: JavaScript (Node.js).
- El encabezado de cada función o método incluye su diseño lógico dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`), con propósito, parámetros, tipos, retorno y errores.
- El código debe ser lo más claro y autoexplicativo posible, de modo que apenas requiera comentarios adicionales.
- Se generan tests automáticos con un repositorio simulado para:
  - guardar una medición válida;
  - rechazar cada tipo de dato inválido (tipo, valor, latitud, longitud, fecha);
  - recuperar la última medición y el caso sin mediciones;
  - recuperar varias mediciones y el caso de lista vacía;
  - propagación de errores de la capa de datos.
- El código no añade funcionalidades que no estén incluidas en el diseño.

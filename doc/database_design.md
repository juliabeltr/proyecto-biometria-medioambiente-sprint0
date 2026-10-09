# Diseño del componente: bd

## 1. Diseño del componente

Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text )

Tabla MEDICIONES: una fila por cada Medicion.

```text
 --------- RepositorioMediciones ---------------
 |
 |  conexion: conexión a la base de datos
 |
 |
                  Repositorio() -->
 |
 |
 m: Medicion --> insertar() -->
   id: N     <--
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

- La base de datos guarda las mediciones que recibe el servidor.
- La tabla principal se llama MEDICIONES.
- La clave primaria `id` es autoincremental. La asigna la base de datos, no quien llama a `insertar()`.
- Los campos `tipo`, `valor`, `latitud`, `longitud` y `fechaHora` son obligatorios.
- `fechaHora` se guarda como texto en formato ISO 8601.
- La "última medición" es la de mayor `fechaHora`. Si hay empate, la de mayor `id`.
- Si no hay mediciones, `recuperarUltima()` indica que no hay datos.
- Este componente solo guarda y recupera datos. No valida reglas de negocio, no contiene código HTTP ni JSON.
- Las consultas a la base de datos son parametrizadas.
- No se añaden campos ni tablas que no aparezcan en este diseño.

## 3. Reglas generales

- Lenguaje de programación: JavaScript (Node.js), con SQLite como base de datos.
- El encabezado de cada función o método incluye su diseño lógico dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`), con propósito, parámetros, tipos, retorno y errores.
- El código debe ser lo más claro y autoexplicativo posible, de modo que apenas requiera comentarios adicionales.
- Se generan tests automáticos para las funciones clave:
  - inserción correcta de una medición;
  - recuperación de la última medición;
  - recuperación de varias mediciones;
  - gestión de datos inválidos;
  - gestión de errores de base de datos.
- El código no añade funcionalidades que no estén incluidas en el diseño.

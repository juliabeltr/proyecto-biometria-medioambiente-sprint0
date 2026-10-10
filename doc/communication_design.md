# Diseño del componente: communication

## 1. Diseño del componente

Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text )

```text
                                             --------- ServidorREST -------------------------
                                             |
                                             |  logica: LogicaMediciones
                                             |  rutaWeb: Text
                                             |
                                             |
 logica: LogicaMediciones, rutaWeb: Text --> ServidorREST() -->
                                             |
                                             |
                             m: Medicion --> postMediciones() -->
                 codigo: N, cuerpo: Text <--
                                             |
                                             |
                 codigo: N, cuerpo: Text <-- getMediciones() <--
                                             |
                                             |
                 codigo: N, cuerpo: Text <-- getUltimaMedicion() <--
                                             |
                                             ------------------------------------------------
```

Rutas (HTTP y JSON):

| Ruta | Método lógico | Llama a (`business_logic`) |
|---|---|---|
| POST /mediciones | postMediciones() | `guardarMedicion()` |
| GET /mediciones | getMediciones() | `recuperarMediciones()` |
| GET /mediciones/ultima | getUltimaMedicion() | `recuperarUltimaMedicion()` |
| GET /gui/, /frontend_business_logic/ | (ficheros estáticos) | sirve la interfaz web |

Respuestas:

| Caso | Código | Cuerpo |
|---|---|---|
| POST correcto | 201 | `{ "guardada": true }` |
| POST con JSON mal formado, campo ausente o datos rechazados por la lógica (`false`) | 400 | `{ "error": "<motivo>" }` |
| GET /mediciones | 200 | lista JSON de `Medicion`, vacía si no hay ninguna |
| GET /mediciones/ultima correcto | 200 | un objeto JSON `Medicion` |
| GET /mediciones/ultima sin mediciones | 404 | `{ "error": "<motivo>" }` |
| Fallo de la lógica o de la capa de datos (excepción) | 500 | `{ "error": "<motivo>" }` |

## 2. Aclaraciones del diseño

- Este componente es el punto de entrada externo del sistema: gestiona las peticiones REST (HTTP y JSON) e invoca al componente `business_logic`. Es la única capa que conoce el protocolo de transporte.
- No contiene lógica de negocio ni accede directamente a la base de datos.
- Recibe `LogicaMediciones` en el constructor. Así los tests pueden sustituirla por una lógica simulada.
- El servidor comprueba solo la forma de la petición: que el cuerpo sea JSON válido y que estén presentes los cinco campos `tipo`, `valor`, `latitud`, `longitud` y `fechaHora`. Comprobar si los valores son correctos (rangos, tipo de contaminante, fecha) es trabajo de la lógica de negocio.
- El campo `id` del cuerpo, si llega, se ignora.
- Los mensajes de error no revelan detalles internos (trazas, consultas SQL).
- Los campos de la respuesta usan los mismos nombres que `Medicion`.
- Además de las rutas de la API, el servidor sirve los ficheros estáticos de la interfaz web (carpetas `gui` y `frontend_business_logic`), para que la página y la API compartan origen y el navegador no bloquee las peticiones.
- `rutaWeb` es la carpeta que contiene `gui` y `frontend_business_logic`. Si no se indica, no se sirve ninguna página (como en los tests de la API).
- Solo se sirven esas dos carpetas. El código del servidor, de la lógica de negocio y de la base de datos nunca se publica.
- Servir ficheros estáticos no es lógica de negocio.
- El fichero de arranque (`index.js`) solo crea el repositorio, la lógica y el servidor, y escucha en el puerto indicado por la variable `PORT` (8080 por defecto). La ruta de la base de datos se toma de `DB_PATH` (`mediciones.sqlite` por defecto). No contiene lógica.
- No se añaden rutas que no aparezcan en este diseño.

## 3. Reglas generales

- Lenguaje de programación: JavaScript (Node.js) con Express.
- El encabezado de cada función o método incluye su diseño lógico dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`), con propósito, parámetros, tipos, retorno y errores.
- El código debe ser lo más claro y autoexplicativo posible, de modo que apenas requiera comentarios adicionales.
- Se generan tests automáticos (Jest y Supertest) con una lógica simulada para las tres rutas:
  - POST correcto, JSON mal formado, campo ausente, datos rechazados y fallo interno;
  - GET /mediciones con datos, con lista vacía y con fallo interno;
  - GET /mediciones/ultima con datos, sin mediciones y con fallo interno;
  - los errores 500 no contienen trazas ni detalles internos.
- Se generan tests automáticos para los ficheros estáticos:
  - con `rutaWeb`, `GET /gui/` devuelve la página `index.html`;
  - con `rutaWeb`, se sirven los ficheros de `gui` y de `frontend_business_logic`;
  - con `rutaWeb`, no se sirven las carpetas `database`, `business_logic` ni `communication`, y no se puede salir de las carpetas servidas;
  - sin `rutaWeb`, `GET /gui/` devuelve 404;
  - servir la web no altera las rutas de la API.
- El código no añade funcionalidades que no estén incluidas en el diseño.

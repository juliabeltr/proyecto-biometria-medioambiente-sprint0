# Diseño del componente: rest

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

| Ruta | Método lógico | Llama a |
|---|---|---|
| POST /mediciones | postMediciones() | `guardarMedicion()` |
| GET /mediciones | getMediciones() | `recuperarMediciones()` |
| GET /mediciones/ultima | getUltimaMedicion() | `recuperarUltimaMedicion()` |
| GET /ux/, /navegador/, /navegador_fake/ | (ficheros estáticos) | sirve la interfaz web |

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

- El servidor REST conecta a los clientes con la lógica de negocio. Solo recibe peticiones, llama a la lógica correspondiente y devuelve la respuesta en JSON.
- No contiene lógica de negocio ni accede directamente a la base de datos.
- Recibe `LogicaMediciones` en el constructor. Así los tests pueden sustituirla por una lógica simulada.
- El servidor comprueba solo la forma de la petición: que el cuerpo sea JSON válido y que estén presentes los cinco campos `tipo`, `valor`, `latitud`, `longitud` y `fechaHora`. Comprobar si los valores son correctos (rangos, tipo de contaminante, fecha) es trabajo de la lógica.
- El campo `id` del cuerpo, si llega, se ignora.
- Los mensajes de error no revelan detalles internos (trazas,

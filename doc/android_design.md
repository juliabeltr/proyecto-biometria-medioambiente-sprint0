# Diseño del componente: android

## 1. Diseño del componente

Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text )

TipoMedicion = { CO2, TEMP, RUIDO }

La app escucha los anuncios iBeacon que emite la placa Arduino, convierte cada uno en una `Medicion` y la envía al servidor REST (`POST /mediciones`).

Flujo: `EscanerBeacons` → `MainActivity` → `ProcesadorBeacons` → `LogicaTelefono` → servidor REST.

### Medicion

```text
 --------- Medicion -----------------------------
 |
 |  id: N
 |  tipo: Text
 |  valor: R
 |  latitud: R
 |  longitud: R
 |  fechaHora: Text
 |
 |
 id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text
     --> Medicion() -->
 |
 |
 texto: Text <-- aJSON() <--
 |
 ------------------------------------------------
```

### ConversorBeacon

```text
 --------- ConversorBeacon ----------------------
 |
 |  UUID_PROYECTO: Text
 |
 |
 trama: TramaIBeacon, fechaHora: Text, latitud: R, longitud: R
     --> convertir() --x
     medicion: Medicion <--
 |
 ------------------------------------------------
```

### FiltroDuplicados

```text
 --------- FiltroDuplicados ---------------------
 |
 |  ultimoMajorPorTipo: [(tipo: N, major: N)]
 |
 |
 FiltroDuplicados() -->
 |
 |
 major: N --> esNueva() -->
   resultado: B <--
 |
 ------------------------------------------------
```

### LogicaTelefono (interfaz), LogicaTelefonoREST y LogicaTelefonoFake

```text
 --------- LogicaTelefono (interfaz) ------------
 |
 |
 m: Medicion --> enviarMedicion() -->
   resultado: B <--
 |
 ------------------------------------------------

 --------- LogicaTelefonoREST -------------------
 |
 |  urlBase: Text
 |
 |
 urlBase: Text --> LogicaTelefonoREST() -->
 |
 |
 m: Medicion --> enviarMedicion() -->
   resultado: B <--
 |
 ------------------------------------------------

 --------- LogicaTelefonoFake -------------------
 |
 |  enviadas: [Medicion]
 |  falla: B
 |
 |
 falla: B --> LogicaTelefonoFake() -->
 |
 |
 m: Medicion --> enviarMedicion() -->
   resultado: B <--
 |
 |
 [Medicion] <-- getEnviadas() <--
 |
 ------------------------------------------------
```

### ProcesadorBeacons

```text
 --------- ProcesadorBeacons --------------------
 |
 |  logica: LogicaTelefono
 |  filtro: FiltroDuplicados
 |  latitud: R
 |  longitud: R
 |
 |
 logica: LogicaTelefono, latitud: R, longitud: R
     --> ProcesadorBeacons() -->
 |
 |
 bytes: [Z], fechaHora: Text --> procesar() -->
   resultado: B <--
 |
 ------------------------------------------------
```

### EscanerBeacons

```text
 --------- EscanerBeacons -----------------------
 |
 |  escuchador: EscuchadorBeacons
 |  escaneando: B
 |
 |
 escuchador: EscuchadorBeacons --> EscanerBeacons() -->
 |
 |
 iniciarEscaneo() -->
 |
 |
 detenerEscaneo() -->
 |
 |
 resultado: B <-- estaEscaneando() <--
 |
 ------------------------------------------------

 --------- EscuchadorBeacons (interfaz) ---------
 |
 |
 bytes: [Z] --> alRecibirAnuncio() -->
 |
 ------------------------------------------------
```

### MainActivity

```text
 --------- MainActivity -------------------------
 |
 |  procesador: ProcesadorBeacons
 |  escaner: EscanerBeacons
 |
 |
 iniciarEscucha() -->
 |
 |
 detenerEscucha() -->
 |
 |
 resultado: B <-- tienePermisos() <--
 |
 |
 pedirPermisos() -->
 |
 ------------------------------------------------
```

Pantalla única de la app:

- Título "Mediciones".
- Campo de texto con la dirección del servidor (por ejemplo `http://192.168.1.35:8080`), editable.
- Botón "Iniciar escucha" y botón "Detener escucha".
- Texto de estado: "Detenido", "Escuchando...", "Faltan permisos de Bluetooth".
- Texto con la última medición enviada (por ejemplo "CO2 = 235 enviada") o el último error de envío.

### Clases reutilizadas del código proporcionado

UUID = ( masSignificativos: Z, menosSignificativos: Z )

```text
 --------- TramaIBeacon -------------------------
 |
 |  prefijo: [Z]_9
 |  uuid: [Z]_16
 |  major: [Z]_2
 |  minor: [Z]_2
 |  txPower: Z
 |  losBytes: [Z]
 |  advFlags: [Z]_3
 |  advHeader: [Z]_2
 |  companyID: [Z]_2
 |  iBeaconType: Z
 |  iBeaconLength: Z
 |
 |
 bytes: [Z] --> TramaIBeacon() -->
 |
 |
 [Z]_9 <-- getPrefijo() <--
 |
 |
 [Z]_16 <-- getUUID() <--
 |
 |
 [Z]_2 <-- getMajor() <--
 |
 |
 [Z]_2 <-- getMinor() <--
 |
 |
 Z <-- getTxPower() <--
 |
 |
 [Z] <-- getLosBytes() <--
 |
 |
 [Z]_3 <-- getAdvFlags() <--
 |
 |
 [Z]_2 <-- getAdvHeader() <--
 |
 |
 [Z]_2 <-- getCompanyID() <--
 |
 |
 Z <-- getiBeaconType() <--
 |
 |
 Z <-- getiBeaconLength() <--
 |
 ------------------------------------------------

 --------- Utilidades ---------------------------
 |
 |
 texto: Text --> stringToBytes() --x
   [Z] <--
 |
 |
 uuid: Text --> stringToUUID() --x
   UUID <--
 |
 |
 uuid: UUID --> uuidToString() --x
   Text <--
 |
 |
 uuid: UUID --> uuidToHexString() --x
   Text <--
 |
 |
 bytes: [Z] --> bytesToString() --x
   Text <--
 |
 |
 mas_significativos: Z, menos_significativos: Z --> dosLongToBytes() --x
   [Z]_16 <--
 |
 |
 bytes: [Z] --> bytesToInt() --x
   Z <--
 |
 |
 bytes: [Z] --> bytesToLong() --x
   Z <--
 |
 |
 bytes: [Z] --> bytesToIntOK() --x
   Z <--
 |
 |
 bytes: [Z] --> bytesToHexString() --x
   Text <--
 |
 ------------------------------------------------

 --------- PeticionarioREST ---------------------
 |
 |  metodo: Text
 |  urlDestino: Text
 |  cuerpoPeticion: Text
 |  codigoRespuesta: Z
 |  cuerpoRespuesta: Text
 |
 |
 PeticionarioREST() -->
 |
 |
 metodo: Text, url_destino: Text, cuerpo: Text --> hacerPeticionREST() -->
   codigo: Z, cuerpo: Text <--
 |
 ------------------------------------------------

 --------- RespuestaREST (interfaz) -------------
 |
 |
 codigo: Z, cuerpo: Text --> callback() -->
 |
 ------------------------------------------------
```

## 2. Aclaraciones del diseño

- La placa emite iBeacons con esta estructura: el UUID es el texto `EPSG-GTI-PROY-3A`, el `major` contiene en su byte alto el tipo de medición (11 = CO2, 12 = TEMP, 13 = RUIDO) y en el bajo un contador, y el `minor` contiene el valor medido como entero de 16 bits con signo.
- `ConversorBeacon.convertir()`:
  - devuelve ausencia de datos (`null`) si el UUID no es `UUID_PROYECTO` o si el tipo no es 11, 12 o 13;
  - el valor es el `minor` interpretado con signo, de modo que una temperatura de -12 llega como -12;
  - usa `fechaHora`, `latitud` y `longitud` tal como se reciben (la trama no los lleva);
  - asigna `id = 0`, porque el servidor ignora el id.
- La placa repite el mismo anuncio durante un segundo, y el móvil lo recibe muchas veces. `FiltroDuplicados.esNueva()` devuelve `true` solo si el `major` (tipo y contador) es distinto del último recibido para ese tipo. Así cada medición se envía una sola vez. Si la placa se reinicia y repite el mismo contador, esa medición se descarta.
- `ProcesadorBeacons.procesar()` ejecuta, en este orden:
  1. ignora la trama si es nula o tiene menos de 30 bytes;
  2. construye la `TramaIBeacon` y la convierte con `ConversorBeacon`; si no es una medición nuestra, la ignora;
  3. consulta el `FiltroDuplicados`; si no es nueva, la ignora;
  4. envía la `Medicion` con `LogicaTelefono.enviarMedicion()`.
  Devuelve `true` si ha enviado la medición y `false` si la ha ignorado.
- La fecha y hora la decide quien llama a `procesar()` (la app usa el reloj del teléfono en UTC, en formato ISO 8601, por ejemplo `2026-10-03T10:00:00Z`). Así el procesador es determinista y se puede probar.
- Ubicación del Sprint 0: la latitud y la longitud son valores fijos definidos en `MainActivity`. Obtener la ubicación real con GPS queda para un sprint posterior.
- `LogicaTelefono` es la interfaz común, igual que `LogicaNavegador` y `LogicaNavegadorFake` en la web. `LogicaTelefonoREST` envía la medición con `PeticionarioREST`: `POST {urlBase}/mediciones` con el JSON de `Medicion.aJSON()`; el resultado es `true` si el servidor responde 201, y `false` con cualquier otro código o fallo de red.
- `LogicaTelefonoFake` no usa la red: guarda las mediciones en memoria y devuelve `true`; si `falla` es `true`, no guarda nada y devuelve `false`. Sirve para desarrollar y probar sin servidor.
- `Medicion.aJSON()` produce un objeto JSON con los campos `tipo`, `valor`, `latitud`, `longitud` y `fechaHora` (sin `id`). Los números usan punto decimal.
- `EscanerBeacons` escanea sin filtros en modo de baja latencia, y entrega los bytes de cada anuncio a su `EscuchadorBeacons`. No interpreta los datos.
- `MainActivity` solo coordina: pide permisos, arranca y detiene el escaneo, crea `LogicaTelefonoREST` con la dirección escrita en pantalla, y muestra el estado. No contiene lógica de conversión ni de comunicación.
- Para mostrar si cada medición se ha enviado, `MainActivity` envuelve la lógica en un `LogicaTelefono` que delega en la lógica real y avisa a la pantalla con el resultado. No cambia el comportamiento del envío.
- Permisos: en Android 12 o posterior se piden `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT` y `ACCESS_FINE_LOCATION`; en versiones anteriores, `ACCESS_FINE_LOCATION`. No se declara `neverForLocation` para no arriesgar que Android oculte los iBeacon. Sin permisos no se inicia el escaneo.
- `TramaIBeacon`, `Utilidades` y `PeticionarioREST` proceden del código proporcionado y se reutilizan; su diseño está en la sección "Clases reutilizadas del código proporcionado".
- `TramaIBeacon` interpreta una trama de al menos 30 bytes: prefijo de 9 bytes (flags, cabecera, fabricante, tipo y longitud de iBeacon), UUID de 16 bytes, major de 2, minor de 2 y txPower de 1. Si la trama es nula o más corta, el constructor termina con error.
- `Utilidades.bytesToInt()` interpreta los bytes como entero con signo, de modo que `0xFFF4` es -12. `stringToUUID()` exige un texto de exactamente 16 caracteres.
- `PeticionarioREST.hacerPeticionREST()` envía una petición HTTP en segundo plano y avisa del código y el cuerpo de la respuesta mediante `RespuestaREST`. Si hay un fallo de red, el código es 0. En el diseño lógico se omiten los métodos propios de `AsyncTask` (`doInBackground`, `onPostExecute`).
- En el diseño lógico se omiten los callbacks y la mecánica propia de Android (`onCreate`, `onRequestPermissionsResult`, `AsyncTask`). La operación `enviarMedicion()` es asíncrona: avisa del resultado mediante un `ResultadoEnvio` con `callback(resultado: B)`.
- No se añaden clases ni operaciones que no aparezcan en este diseño.

## 3. Reglas generales

- Lenguaje de programación: Java para Android. Paquete `com.example.jbelgir.medicionesapp`. SDK mínimo 28.
- El encabezado de cada función o método incluye su diseño lógico dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`), con propósito, parámetros, tipos, retorno y errores.
- El código debe ser lo más claro y autoexplicativo posible, de modo que apenas requiera comentarios adicionales.
- El JSON se genera con `org.json.JSONObject`. Para los tests con JUnit se añade la dependencia `testImplementation 'org.json:json:20231013'`.
- Se generan tests automáticos con JUnit 4, sin dispositivo ni emulador, para:
  - `ConversorBeacon`: medición de CO2, de TEMP con valor negativo y de RUIDO; UUID ajeno (`null`); tipo desconocido (`null`); uso de la fecha, la latitud y la longitud recibidas;
  - `FiltroDuplicados`: primer anuncio nuevo, anuncio repetido, contador distinto, tipos independientes;
  - `ProcesadorBeacons` con `LogicaTelefonoFake`: trama válida enviada, trama repetida enviada una sola vez, trama nula o de menos de 30 bytes ignorada, UUID ajeno ignorado, fallo del envío;
  - `LogicaTelefonoFake`: guarda mediciones, y con `falla` no guarda y devuelve `false`;
  - `Medicion.aJSON()`: los cinco campos, punto decimal y valores negativos.
- Los tests usan una clase de apoyo, `TramasDePrueba`, que construye tramas iBeacon como las emite la placa.
- `LogicaTelefonoREST`, `EscanerBeacons` y `MainActivity` usan la API de Android y no se prueban con JUnit: se comprueban en la prueba completa del sistema.
- El código no añade funcionalidades que no estén incluidas en el diseño.

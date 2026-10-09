# Diseño del componente: arduino

## 1. Diseño del componente

MedicionesID = { CO2, TEMPERATURA, RUIDO }

Valores numéricos de `MedicionesID` en la trama: CO2 = 11, TEMPERATURA = 12, RUIDO = 13.

El programa de la placa mide (de forma simulada) CO2 y temperatura, y los emite como anuncios iBeacon Bluetooth Low Energy para que los reciba el teléfono.

Flujo: `Medidor` → `Publicador` → `EmisoraBLE` → anuncio BLE → teléfono.

### Funciones generales (fuera de las clases)

```text
 tiempo: Z --> esperar() -->
 |
 |
 p: [T], n: N --> alReves() -->
   p: [T] <--
 |
 |
 texto: Text, pUint: [N], tamMax: N --> stringAUint8AlReves() -->
   pUint: [N] <--
 |
 |
 tipo: N, contador: N --> calcularMajor() -->
   major: N <--
```

### LED

```text
               --------- LED ----------------------------------
               |
               |  numeroLED: N
               |  encendido: B
               |
               |
 numero: N --> LED() -->
               |
               |
               encender() -->
               |
               |
               apagar() -->
               |
               |
               alternar() -->
               |
               |
 tiempo: Z --> brillar() -->
               |
               ------------------------------------------------
```

### PuertoSerie

```text
                   --------- PuertoSerie --------------------------
                   |
                   |
    baudios: N --> PuertoSerie() -->
                   |
                   |
                   esperarDisponible() -->
                   |
                   |
 mensaje: Text --> escribir() -->
                   |
                   ------------------------------------------------
```

### Medidor

```text
       --------- Medidor ------------------------------
       |
       |
       Medidor() -->
       |
       |
       iniciarMedidor() -->
       |
       |
 N <-- medirCO2() -->
       |
       |
 Z <-- medirTemperatura() -->
       |
       ------------------------------------------------
```

### EmisoraBLE

```text
                                                                    --------- EmisoraBLE ---------------------------
                                                                    |
                                                                    |  nombreEmisora: Text
                                                                    |  fabricanteID: N
                                                                    |  txPower: Z
                                                                    |
                                                                    |
            nombre_emisora: Text, fabricante_id: N, tx_power: Z --> EmisoraBLE() -->
                                                                    |
                                                                    |
                                                                    encenderEmisora() -->
                                                                    |
                                                                    |
                                                                    detenerAnuncio() -->
                                                                    |
                                                                    |
                                                              B <-- estaAnunciando() -->
                                                                    |
                                                                    |
               beacon_uuid: [N]_16, major: Z, minor: Z, rssi: N --> emitirAnuncioIBeacon() -->
                                                                    |
                                                                    |
                                  carga: Text, tamanyo_carga: N --> emitirAnuncioIBeaconLibre() -->
                                                                    |
                                                                    |
                                    servicio: ServicioEnEmisora --> anyadirServicio() -->
                                                              B <--
                                                                    |
                                                                    |
 servicio: ServicioEnEmisora, caracteristicas: [Caracteristica] --> anyadirServicioConSusCaracteristicas() -->
                                                              B <--
                                                                    |
                                                                    |
 servicio: ServicioEnEmisora, caracteristicas: [Caracteristica] --> anyadirServicioConSusCaracteristicasYActivar() -->
                                                              B <--
                                                                    |
                                                                    |
                                                                    instalarCallbackConexionEstablecida() -->
                                                                    |
                                                                    |
                                                                    instalarCallbackConexionTerminada() -->
                                                                    |
                                                                    |
                                                 conn_handle: N --> getConexion() -->
                                             conexion: Conexion <--
                                                                    |
                                                                    ------------------------------------------------
```

### ServicioEnEmisora y Caracteristica

```text
                         --------- ServicioEnEmisora --------------------
                         |
                         |  uuidServicio: [N]_16
                         |  elServicio: BLEService
                         |  lasCaracteristicas: [Caracteristica]
                         |
                         |
                         ServicioEnEmisora() -->
                         |
                         |
                         escribeUUID() -->
                         |
                         |
 car: Caracteristica --> anyadirCaracteristica() -->
                         |
                         |
                         activarServicio() -->
                         |
                         ------------------------------------------------

                                                                                          --------- Caracteristica -----------------------
                                                                                          |
                                                                                          |  uuidCaracteristica: [N]_16
                                                                                          |  laCaracteristica: BLECharacteristic
                                                                                          |
                                                                                          |
                                                          nombre_caracteristica: Text --> Caracteristica() -->
                                                                                          |
                                                                                          |
 nombre_caracteristica: Text, props: Z, permisoRead: Text, permisoWrite: Text, tam: Z --> Caracteristica() -->
                                                                                          |
                                                                                          |
                              props: Z, permisoRead: Text, permisoWrite: Text, tam: Z --> asignarPropiedadesPermisosYTamanyoDatos() -->
                                                                                          |
                                                                                          |
                                                                            str: Text --> escribirDatos() -->
                                                                    bytes_escritos: N <--
                                                                                          |
                                                                                          |
                                                                            str: Text --> notificarDatos() -->
                                                                    bytes_enviados: N <--
                                                                                          |
                                                                                          |
                                                                                          instalarCallbackCaracteristicaEscrita() -->
                                                                                          |
                                                                                          |
                                                                                          activar() -->
                                                                                          |
                                                                                          |
                                                                             props: Z --> asignarPropiedades() -->
                                                                                          |
                                                                                          |
                                                permisoRead: Text, permisoWrite: Text --> asignarPermisos() -->
                                                                                          |
                                                                                          |
                                                                               tam: Z --> asignarTamanyoDatos() -->
                                                                                          |
                                                                                          ------------------------------------------------
```

La clase `Caracteristica` pertenece a `ServicioEnEmisora` (un servicio contiene características). Los métodos `asignarPropiedades()`, `asignarPermisos()` y `asignarTamanyoDatos()` son privados.

### Publicador

```text
                                                         --------- Publicador ---------------------------
                                                         |
                                                         |  beaconUUID: [N]_16
                                                         |  laEmisora: EmisoraBLE
                                                         |
                                                         |
                                                         Publicador() -->
                                                         |
                                                         |
                                                         encenderEmisora() -->
                                                         |
                                                         |
         valor_co2: Z, contador: N, tiempo_espera: Z --> publicarCO2() -->
                                                         |
                                                         |
 valor_temperatura: Z, contador: N, tiempo_espera: Z --> publicarTemperatura() -->
                                                         |
                                                         |
                       carga: Text, tamanyo_carga: N --> emitirAnuncioLibre() -->
                                                         |
                                                         |
                                                         detenerAnuncio() -->
                                                         |
                                                         ------------------------------------------------
```

### Programa principal (HolaMundoIBeacon)

```text
  --------- HolaMundoIBeacon ---------------------
  |
  |  elLED: LED
  |  elPuerto: PuertoSerie
  |  elPublicador: Publicador
  |  elMedidor: Medidor
  |  cont: N
  |
  |
  inicializarPlaquita() -->
  |
  |
  setup() -->
  |
  |
  lucecitas() -->
  |
  |
  loop() -->
  |
  ------------------------------------------------
```

## 2. Aclaraciones del diseño

- La placa emite anuncios iBeacon con esta estructura de datos: el UUID es el texto `EPSG-GTI-PROY-3A` (16 caracteres), el `major` contiene el tipo de medición en el byte alto y un contador en el byte bajo (`major = tipo * 256 + contador`), y el `minor` contiene el valor medido como entero de 16 bits con signo. El campo de potencia (`rssi`) es -53.
- Medida simulada del Sprint 0: `Medidor.medirCO2()` devuelve un valor fijo (235) y `Medidor.medirTemperatura()` un valor fijo (-12). Para la demostración se cambia ese número en el código y se vuelve a cargar en la placa. Más adelante estas funciones leerán el sensor real.
- `setup()` espera a que el puerto serie esté disponible, enciende la emisora, inicia el medidor y espera un segundo.
- Cada ejecución de `loop()` (un ciclo de unos 7 u 8 segundos):
  1. incrementa el contador `cont` (que da la vuelta tras 255) y lo escribe por el puerto serie;
  2. hace parpadear el LED (`lucecitas()`);
  3. mide el CO2 y lo publica durante un segundo;
  4. mide la temperatura y la publica durante un segundo;
  5. emite un anuncio libre (carga de 21 caracteres, sin el formato iBeacon de medición) durante dos segundos y lo detiene.
- `Publicador.publicarCO2()` y `publicarTemperatura()` emiten el anuncio, esperan `tiempo_espera` milisegundos y detienen el anuncio. `emitirAnuncioLibre()` no espera: el que llama detiene el anuncio con `detenerAnuncio()`.
- `EmisoraBLE.emitirAnuncioIBeacon()` detiene antes cualquier anuncio activo. `emitirAnuncioIBeaconLibre()` copia como máximo 21 caracteres de la carga.
- `EmisoraBLE.encenderEmisora()` inicializa Bluefruit y se asegura de que no haya ningún anuncio activo. Existe una variante que además instala los callbacks de conexión; en el diseño lógico los callbacks se omiten, por lo que ambas variantes tienen la misma firma.
- `Publicador` expone la constante pública `RSSI` (-53) y los valores de `MedicionesID`.
- `esperar()` bloquea la ejecución durante el tiempo indicado.
- `alReves()`, `stringAUint8AlReves()` y `calcularMajor()` son funciones puras, sin dependencia de Arduino, y están en `Utilidades.h`. `calcularMajor()` devuelve `tipo * 256 + contador`, el campo major del iBeacon.
- `ServicioEnEmisora` y `Caracteristica` permiten definir servicios y características GATT. El programa principal del Sprint 0 no los utiliza: emite solo anuncios iBeacon.
- `alReves()` es genérica (cualquier tipo de elemento) y `escribir()` acepta cualquier valor que se pueda escribir por el puerto serie.
- `inicializarPlaquita()` está vacía en esta versión.
- El nombre de la emisora es `GTI-3A`, el identificador de fabricante es `0x004C` y la potencia de transmisión es 4.
- El programa principal está en `src/arduino/arduino.ino`: el Arduino IDE exige que el fichero `.ino` se llame igual que la carpeta del sketch, y los ficheros `.h` están en esa misma carpeta.
- No se añaden clases ni operaciones que no aparezcan en este diseño.

## 3. Reglas generales

- Lenguaje de programación: C++ para Arduino (Arduino IDE), placa nRF52840 con la biblioteca Bluefruit (`bluefruit.h`).
- El encabezado de cada función o método incluye su diseño lógico dentro de un bloque de comentarios delimitado por líneas discontinuas (`--------------------`), con propósito, parámetros, tipos, retorno y errores.
- El código debe ser lo más claro y autoexplicativo posible, de modo que apenas requiera comentarios adicionales.
- Se generan tests automáticos en el ordenador (C++ con `g++`, sin Arduino ni placa) para las funciones puras de `Utilidades.h`, en `src/arduino/tests/test_utilidades.cpp`:
  - `alReves()`: longitud par, impar, un solo elemento, vacío y puntero nulo;
  - `stringAUint8AlReves()`: copia inversa de un UUID de 16 caracteres, texto más corto, texto más largo que el destino, punteros nulos y tamaño no válido;
  - `calcularMajor()`: tipo en el byte alto y contador en el byte bajo para CO2, TEMPERATURA y RUIDO.
- Las clases que usan la biblioteca Bluefruit (`LED`, `PuertoSerie`, `EmisoraBLE`, `ServicioEnEmisora`, `Publicador`, `Medidor`) dependen del hardware: se comprueban compilando en el Arduino IDE y con la prueba completa del sistema (medida ficticia en la placa, recibida por el teléfono, guardada por el servidor y mostrada en la web).
- El código no añade funcionalidades que no estén incluidas en el diseño.

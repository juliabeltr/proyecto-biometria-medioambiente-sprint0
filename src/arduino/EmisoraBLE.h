// -*- mode: c++ -*-

// ----------------------------------------------------------
// Júlia Beltrán Girbés
// ----------------------------------------------------------

#ifndef EMISORA_H_INCLUIDO
#define EMISORA_H_INCLUIDO

// Buena introducción:
// https://learn.adafruit.com/introduction-to-bluetooth-low-energy/gap
// https://os.mbed.com/blog/entry/BLE-Beacons-URIBeacon-AltBeacons-iBeacon/

// Fuente:
// https://www.instructables.com/id/Beaconeddystone-and-Adafruit-NRF52-Advertise-Your-/
// https://github.com/nkolban/ESP32_BLE_Arduino/blob/master/src/BLEBeacon.h
// https://learn.adafruit.com/bluefruit-nrf52-feather-learning-guide/bleadvertising

#include "ServicioEnEmisora.h"

/*
 * --------------------------------------------------------------
 * Propósito: Gestiona la emisora Bluetooth Low Energy de la placa.
 *
 * Permite inicializar Bluefruit, emitir y detener anuncios iBeacon,
 * añadir servicios BLE y gestionar callbacks de conexión.
 * --------------------------------------------------------------
 */
class EmisoraBLE {

private:

  const char * nombreEmisora;
  const uint16_t fabricanteID;
  const int8_t txPower;

public:

  using CallbackConexionEstablecida =
    void (uint16_t connHandle);

  using CallbackConexionTerminada =
    void (uint16_t connHandle, uint8_t reason);

  /*
   * --------------------------------------------------------------
   * Propósito: Constructor de la emisora BLE.
   *
   * Diseño lógico:
   * nombre_emisora: Text,
   * fabricante_id: N,
   * tx_power: Z
   *      --> EmisoraBLE()
   *
   * Parámetro: nombreEmisora_ Nombre visible de la emisora.
   * Parámetro: fabricanteID_ Identificador del fabricante.
   * Parámetro: txPower_ Potencia de transmisión configurada.
   *
   * Nota: La emisora no se inicia en el constructor.
   * --------------------------------------------------------------
   */
  EmisoraBLE(
    const char * nombreEmisora_,
    const uint16_t fabricanteID_,
    const int8_t txPower_
  )
    :
    nombreEmisora(nombreEmisora_),
    fabricanteID(fabricanteID_),
    txPower(txPower_)
  {
  }

  /*
   * Constructor alternativo con callbacks.
   *
   * Se mantiene comentado porque la emisora debe estar
   * inicializada antes de instalar determinados callbacks.
   *
   * EmisoraBLE(
   *   const char * nombreEmisora_,
   *   const uint16_t fabricanteID_,
   *   const int8_t txPower_,
   *   CallbackConexionEstablecida cbce,
   *   CallbackConexionTerminada cbct
   * )
   *   :
   *   EmisoraBLE(
   *     nombreEmisora_,
   *     fabricanteID_,
   *     txPower_
   *   )
   * {
   *   instalarCallbackConexionEstablecida(cbce);
   *   instalarCallbackConexionTerminada(cbct);
   * }
   */

  /*
   * --------------------------------------------------------------
   * Propósito: Inicializa la emisora BLE.
   *
   * Diseño lógico:
   * encenderEmisora() -->
   *
   * Inicializa Bluefruit y garantiza que no exista
   * ningún anuncio activo inicialmente.
   * --------------------------------------------------------------
   */
  void encenderEmisora() {

    Bluefruit.begin();

    // Se garantiza que no haya un anuncio previo activo.
    (*this).detenerAnuncio();
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Inicializa la emisora BLE e instala callbacks de conexión.
   *
   * Diseño lógico:
   * callback_conexion_establecida,
   * callback_conexion_terminada
   *      --> encenderEmisora()
   * --------------------------------------------------------------
   */
  void encenderEmisora(
    CallbackConexionEstablecida cbce,
    CallbackConexionTerminada cbct
  ) {

    encenderEmisora();

    instalarCallbackConexionEstablecida(cbce);
    instalarCallbackConexionTerminada(cbct);
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Detiene el anuncio BLE si está activo.
   *
   * Diseño lógico:
   * detenerAnuncio() -->
   * --------------------------------------------------------------
   */
  void detenerAnuncio() {

    if ((*this).estaAnunciando()) {
      Bluefruit.Advertising.stop();
    }
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Indica si existe actualmente un anuncio BLE activo.
   *
   * Diseño lógico:
   * B <-- estaAnunciando() -->
   *
   * Retorno: true si la emisora está anunciando;
   * false en caso contrario.
   * --------------------------------------------------------------
   */
  bool estaAnunciando() {
    return Bluefruit.Advertising.isRunning();
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Emite un anuncio Bluetooth LE en formato iBeacon.
   *
   * Diseño lógico:
   * beacon_uuid: [N]_16,
   * major: Z,
   * minor: Z,
   * rssi: Z
   *      --> emitirAnuncioIBeacon()
   *
   * Parámetro: beaconUUID UUID de 16 bytes del iBeacon.
   * Parámetro: major Campo major del iBeacon.
   * Parámetro: minor Campo minor del iBeacon.
   * Parámetro: rssi Potencia de referencia incluida en el anuncio.
   * --------------------------------------------------------------
   */
  void emitirAnuncioIBeacon(
    uint8_t * beaconUUID,
    int16_t major,
    int16_t minor,
    uint8_t rssi
  ) {

    // Antes de emitir un nuevo anuncio se detiene el anterior.
    (*this).detenerAnuncio();

    // Se crea el beacon con los datos recibidos.
    BLEBeacon elBeacon(
      beaconUUID,
      major,
      minor,
      rssi
    );

    elBeacon.setManufacturer(
      (*this).fabricanteID
    );

    // Configuración de potencia y nombre de la emisora.
    Bluefruit.setTxPower(
      (*this).txPower
    );

    Bluefruit.setName(
      (*this).nombreEmisora
    );

    // Añade el nombre de la emisora a la respuesta de escaneo.
    Bluefruit.ScanResponse.addName();

    // Configura el anuncio como iBeacon.
    Bluefruit.Advertising.setBeacon(
      elBeacon
    );

    // Reinicia automáticamente la publicidad tras una desconexión.
    Bluefruit.Advertising.restartOnDisconnect(true);

    // Intervalo de publicidad BLE.
    // La librería usa unidades de 0.625 ms.
    // 100 unidades equivalen aproximadamente a 62.5 ms.
    Bluefruit.Advertising.setInterval(
      100,
      100
    );

    // 0 indica que el anuncio continúa indefinidamente
    // hasta que detenerAnuncio() sea llamado.
    Bluefruit.Advertising.start(0);
  }

  // ----------------------------------------------------------
  // Estructura de un iBeacon:
  //
  // Prefijo:
  //   - Flags
  //   - Manufacturer data
  //   - Company ID
  //   - Beacon type
  //   - Longitud
  //
  // Datos:
  //   - UUID:    16 bytes
  //   - major:    2 bytes
  //   - minor:    2 bytes
  //   - txPower:  1 byte
  //
  // Total de datos configurables en esta parte: 21 bytes.
  // ----------------------------------------------------------

  /*
   * --------------------------------------------------------------
   * Propósito: Emite un anuncio iBeacon utilizando una carga personalizada.
   *
   * Diseño lógico:
   * carga: Text,
   * tamanyo_carga: N
   *      --> emitirAnuncioIBeaconLibre() -->
   *
   * Parámetro: carga Datos que se desean transmitir.
   * Parámetro: tamanyoCarga Tamaño de la carga.
   *
   * Nota: La carga útil está limitada a 21 bytes.
   * Si se proporcionan más, solo se copian los primeros 21.
   * --------------------------------------------------------------
   */
  void emitirAnuncioIBeaconLibre(
    const char * carga,
    const uint8_t tamanyoCarga
  ) {

    // Se evita acceder a memoria inválida si la carga es nula.
    if (carga == nullptr) {

      Globales::elPuerto.escribir(
        "Error: carga nula en emitirAnuncioIBeaconLibre()\n"
      );

      return;
    }

    (*this).detenerAnuncio();

    // Se eliminan posibles datos de anuncios anteriores.
    Bluefruit.Advertising.clearData();

    // Limpia los datos previos de la respuesta de escaneo.
    Bluefruit.ScanResponse.clearData();

    Bluefruit.setName(
      (*this).nombreEmisora
    );

    Bluefruit.ScanResponse.addName();

    // Configura el anuncio BLE como general discoverable.
    Bluefruit.Advertising.addFlags(
      BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE
    );

    /*
     * Se construyen:
     *
     * 4 bytes fijos:
     *   - Company ID: 2 bytes
     *   - Beacon type: 1 byte
     *   - Longitud: 1 byte
     *
     * 21 bytes de carga libre.
     */
    uint8_t restoPrefijoYCarga[4 + 21] = {

      0x4c, 0x00, // Company ID (Apple)
      0x02,       // iBeacon type
      21,         // Longitud de la carga

      '-', '-', '-', '-',
      '-', '-', '-', '-',
      '-', '-', '-', '-',
      '-', '-', '-', '-',
      '-', '-', '-', '-',
      '-'
    };

    /*
     * Se copian como máximo 21 bytes.
     * De este modo se evita escribir fuera de los límites
     * del array restoPrefijoYCarga.
     */
    memcpy(
      &restoPrefijoYCarga[4],
      &carga[0],
      (tamanyoCarga > 21 ? 21 : tamanyoCarga)
    );

    // Se añade el bloque completo al anuncio BLE.
    Bluefruit.Advertising.addData(
      BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA,
      &restoPrefijoYCarga[0],
      4 + 21
    );

    // Reinicia automáticamente la publicidad tras desconexión.
    Bluefruit.Advertising.restartOnDisconnect(true);

    // Intervalo de publicidad:
    // 100 * 0.625 ms = aproximadamente 62.5 ms.
    Bluefruit.Advertising.setInterval(
      100,
      100
    );

    // Tiempo en segundos durante el modo rápido de publicidad.
    Bluefruit.Advertising.setFastTimeout(1);

    // Se inicia el anuncio sin límite temporal.
    Bluefruit.Advertising.start(0);

    Globales::elPuerto.escribir(
      "emitirAnuncioIBeaconLibre(): anuncio iniciado\n"
    );
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Añade un servicio BLE a la publicidad.
   *
   * Diseño lógico:
   * servicio: ServicioEnEmisora
   *      --> anyadirServicio() -->
   * B <--
   * Retorno: true si el servicio se ha añadido correctamente.
   * --------------------------------------------------------------
   */
  bool anyadirServicio(
    ServicioEnEmisora & servicio
  ) {

    Globales::elPuerto.escribir(
      "Bluefruit.Advertising.addService(servicio)\n"
    );

    bool r =
      Bluefruit.Advertising.addService(
        servicio
      );

    if (!r) {
      Serial.println(
        "SERVICIO NO AÑADIDO\n"
      );
    }

    return r;
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Añade un servicio BLE sin características adicionales.
   *
   * Retorno: Resultado de añadir el servicio.
   *
   * Diseño lógico:
   *     servicio: ServicioEnEmisora, caracteristicas: [Caracteristica]
   *         --> anyadirServicioConSusCaracteristicas() -->
   *         B <--
   * --------------------------------------------------------------
   */
  bool anyadirServicioConSusCaracteristicas(
    ServicioEnEmisora & servicio
  ) {
    return (*this).anyadirServicio(
      servicio
    );
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Añade características a un servicio de forma recursiva.
   *
   * Diseño lógico:
   *     servicio: ServicioEnEmisora, caracteristicas: [Caracteristica]
   *         --> anyadirServicioConSusCaracteristicas() -->
   *         B <--
   * --------------------------------------------------------------
   */
  template <typename ... T>
  bool anyadirServicioConSusCaracteristicas(
    ServicioEnEmisora & servicio,
    ServicioEnEmisora::Caracteristica & caracteristica,
    T& ... restoCaracteristicas
  ) {

    servicio.anyadirCaracteristica(
      caracteristica
    );

    return anyadirServicioConSusCaracteristicas(
      servicio,
      restoCaracteristicas...
    );
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Añade las características y posteriormente activa el servicio.
   *
   * Diseño lógico:
   *     servicio: ServicioEnEmisora, caracteristicas: [Caracteristica]
   *         --> anyadirServicioConSusCaracteristicasYActivar() -->
   *         B <--
   * --------------------------------------------------------------
   */
  template <typename ... T>
  bool anyadirServicioConSusCaracteristicasYActivar(
    ServicioEnEmisora & servicio,
    T& ... restoCaracteristicas
  ) {

    bool r =
      anyadirServicioConSusCaracteristicas(
        servicio,
        restoCaracteristicas...
      );

    servicio.activarServicio();

    return r;
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Instala el callback ejecutado al establecer una conexión BLE.
   *
   * Diseño lógico:
   *     instalarCallbackConexionEstablecida() -->
   * --------------------------------------------------------------
   */
  void instalarCallbackConexionEstablecida(
    CallbackConexionEstablecida cb
  ) {
    Bluefruit.Periph.setConnectCallback(
      cb
    );
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Instala el callback ejecutado al terminar una conexión BLE.
   *
   * Diseño lógico:
   *     instalarCallbackConexionTerminada() -->
   * --------------------------------------------------------------
   */
  void instalarCallbackConexionTerminada(
    CallbackConexionTerminada cb
  ) {
    Bluefruit.Periph.setDisconnectCallback(
      cb
    );
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Obtiene una conexión BLE mediante su identificador.
   *
   * Parámetro: connHandle Identificador de la conexión.
   * Retorno: Puntero a la conexión BLE correspondiente.
   *
   * Nota: El resultado podría ser nulo si la conexión no existe.
   *
   * Diseño lógico:
   *     conn_handle: N --> getConexion() -->
   *         conexion: Conexion <--
   * --------------------------------------------------------------
   */
  BLEConnection * getConexion(
    uint16_t connHandle
  ) {
    return Bluefruit.Connection(
      connHandle
    );
  }

};

#endif

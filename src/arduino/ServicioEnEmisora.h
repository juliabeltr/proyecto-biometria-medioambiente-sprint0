// -*- mode: c++ -*-

// ----------------------------------------------------------
// Júlia Beltrán Girbés
// ----------------------------------------------------------

#ifndef SERVICIO_EMISORA_H_INCLUIDO
#define SERVICIO_EMISORA_H_INCLUIDO

#include <vector>
#include "Utilidades.h"

// ----------------------------------------------------------
// ServicioEnEmisora
// ----------------------------------------------------------

/*
 * --------------------------------------------------------------
 * Propósito: Representa un servicio BLE que puede contener
 * distintas características.
 *
 * Permite crear un servicio, añadir características
 * y activarlas sobre la emisora BLE.
 * --------------------------------------------------------------
 */
class ServicioEnEmisora {

public:

  /*
   * --------------------------------------------------------------
   * Propósito: Tipo de callback ejecutado cuando una característica
   * recibe una escritura.
   * --------------------------------------------------------------
   */
  using CallbackCaracteristicaEscrita =
    void (
      uint16_t conn_handle,
      BLECharacteristic * chr,
      uint8_t * data,
      uint16_t len
    );

  // --------------------------------------------------------
  // Caracteristica
  // --------------------------------------------------------

  /*
   * --------------------------------------------------------------
   * Propósito: Representa una característica perteneciente
   * a un servicio BLE.
   * --------------------------------------------------------------
   */
  class Caracteristica {

  private:

    /*
     * UUID de 16 bytes.
     *
     * El UUID definitivo se obtiene a partir del nombre
     * proporcionado al constructor.
     */
    uint8_t uuidCaracteristica[16] = {
      '0', '1', '2', '3',
      '4', '5', '6', '7',
      '8', '9', 'A', 'B',
      'C', 'D', 'E', 'F'
    };

    // Característica BLE proporcionada por la biblioteca.
    BLECharacteristic laCaracteristica;

  public:

    /*
     * --------------------------------------------------------------
     * Propósito: Constructor de una característica BLE.
     *
     * Diseño lógico:
     * nombre_caracteristica: Text
     *      --> Caracteristica()
     *
     * Parámetro: nombreCaracteristica_ Nombre utilizado para
     * construir el UUID.
     * --------------------------------------------------------------
     */
    Caracteristica(
      const char * nombreCaracteristica_
    )
      :
      laCaracteristica(
        stringAUint8AlReves(
          nombreCaracteristica_,
          &uuidCaracteristica[0],
          16
        )
      )
    {
    }

    /*
     * --------------------------------------------------------------
     * Propósito: Constructor que permite configurar además las
     * propiedades, permisos y tamaño máximo de los datos.
     *
     * Diseño lógico:
     * nombre: Text,
     * propiedades: N,
     * permiso_lectura,
     * permiso_escritura,
     * tamanyo: N
     *      --> Caracteristica()
     * --------------------------------------------------------------
     */
    Caracteristica(
      const char * nombreCaracteristica_,
      uint8_t props,
      BleSecurityMode permisoRead,
      BleSecurityMode permisoWrite,
      uint8_t tam
    )
      :
      Caracteristica(nombreCaracteristica_)
    {

      (*this).asignarPropiedadesPermisosYTamanyoDatos(
        props,
        permisoRead,
        permisoWrite,
        tam
      );
    }

  private:

    /*
     * --------------------------------------------------------------
     * Propósito: Configura las propiedades de la característica.
     *
     * Por ejemplo:
     * CHR_PROPS_WRITE, CHR_PROPS_READ o CHR_PROPS_NOTIFY.
     * --------------------------------------------------------------
     */
    void asignarPropiedades(
      uint8_t props
    ) {

      (*this).laCaracteristica.setProperties(
        props
      );
    }

    /*
     * --------------------------------------------------------------
     * Propósito: Configura los permisos de lectura y escritura.
     * --------------------------------------------------------------
     */
    void asignarPermisos(
      BleSecurityMode permisoRead,
      BleSecurityMode permisoWrite
    ) {

      (*this).laCaracteristica.setPermission(
        permisoRead,
        permisoWrite
      );
    }

    /*
     * --------------------------------------------------------------
     * Propósito: Establece el tamaño máximo de los datos
     * de la característica.
     *
     * Parámetro: tam Tamaño máximo permitido.
     * --------------------------------------------------------------
     */
    void asignarTamanyoDatos(
      uint8_t tam
    ) {

      (*this).laCaracteristica.setMaxLen(
        tam
      );
    }

  public:

    /*
     * --------------------------------------------------------------
     * Propósito: Configura propiedades, permisos y tamaño
     * de los datos de una característica.
     * --------------------------------------------------------------
     */
    void asignarPropiedadesPermisosYTamanyoDatos(
      uint8_t props,
      BleSecurityMode permisoRead,
      BleSecurityMode permisoWrite,
      uint8_t tam
    ) {

      asignarPropiedades(props);

      asignarPermisos(
        permisoRead,
        permisoWrite
      );

      asignarTamanyoDatos(tam);
    }

    /*
     * --------------------------------------------------------------
     * Propósito: Escribe un texto en la característica BLE.
     *
     * Diseño lógico:
     * datos: Text --> escribirDatos() -->
     * N <--
     * Parámetro: str Texto que se desea escribir.
     *
     * Retorno: Número de bytes escritos.
     * --------------------------------------------------------------
     */
    uint16_t escribirDatos(
      const char * str
    ) {

      if (str == nullptr) {
        return 0;
      }

      uint16_t r =
        (*this).laCaracteristica.write(
          str
        );

      return r;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: Envía una notificación BLE con los datos indicados.
     *
     * Diseño lógico:
     * datos: Text --> notificarDatos() -->
     * N <--
     * Parámetro: str Texto que se desea notificar.
     *
     * Retorno: Resultado de la operación de notificación.
     * --------------------------------------------------------------
     */
    uint16_t notificarDatos(
      const char * str
    ) {

      if (str == nullptr) {
        return 0;
      }

      uint16_t r =
        laCaracteristica.notify(
          &str[0]
        );

      return r;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: Instala el callback que se ejecutará cuando
     * se escriba en la característica.
     * --------------------------------------------------------------
     */
    void instalarCallbackCaracteristicaEscrita(
      CallbackCaracteristicaEscrita cb
    ) {

      (*this).laCaracteristica.setWriteCallback(
        cb
      );
    }

    /*
     * --------------------------------------------------------------
     * Propósito: Activa la característica BLE.
     *
     * Llama a begin() sobre la característica e informa
     * del código de error mediante el puerto serie.
     * --------------------------------------------------------------
     */
    void activar() {

      err_t error =
        (*this).laCaracteristica.begin();

      Globales::elPuerto.escribir(
        "laCaracteristica.begin(); error = "
      );

      Globales::elPuerto.escribir(
        error
      );
    }

  }; // class Caracteristica

private:

  /*
   * UUID del servicio.
   *
   * Se sustituye a partir del nombre proporcionado
   * al constructor.
   */
  uint8_t uuidServicio[16] = {
    '0', '1', '2', '3',
    '4', '5', '6', '7',
    '8', '9', 'A', 'B',
    'C', 'D', 'E', 'F'
  };

  // Servicio BLE proporcionado por la biblioteca.
  BLEService elServicio;

  /*
   * Lista de características asociadas al servicio.
   *
   * Se almacenan punteros a las características,
   * por lo que estas deben seguir existiendo mientras
   * el servicio las utilice.
   */
  std::vector<Caracteristica *> lasCaracteristicas;

public:

  /*
   * --------------------------------------------------------------
   * Propósito: Constructor del servicio BLE.
   *
   * Diseño lógico:
   * nombre_servicio: Text
   *      --> ServicioEnEmisora()
   *
   * Parámetro: nombreServicio_ Nombre utilizado para
   * construir el UUID del servicio.
   * --------------------------------------------------------------
   */
  ServicioEnEmisora(
    const char * nombreServicio_
  )
    :
    elServicio(
      stringAUint8AlReves(
        nombreServicio_,
        &uuidServicio[0],
        16
      )
    )
  {
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Escribe el UUID del servicio por el puerto serie.
   *
   * Método de apoyo para depuración.
   * --------------------------------------------------------------
   */
  void escribeUUID() {

    Serial.println("**********");

    for (int i = 0; i <= 15; i++) {
      Serial.print(
        (char) uuidServicio[i]
      );
    }

    Serial.println(
      "\n**********"
    );
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Añade una característica al servicio.
   *
   * Diseño lógico:
   * caracteristica: Caracteristica
   *      --> anyadirCaracteristica()
   * --------------------------------------------------------------
   */
  void anyadirCaracteristica(
    Caracteristica & car
  ) {

    (*this).lasCaracteristicas.push_back(
      &car
    );
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Activa el servicio y todas sus características.
   *
   * Diseño lógico:
   * activarServicio()
   *
   * Primero se inicia el servicio BLE y después
   * se activan las características asociadas.
   * --------------------------------------------------------------
   */
  void activarServicio() {

    err_t error =
      (*this).elServicio.begin();

    Serial.print(
      "elServicio.begin(); error = "
    );

    Serial.println(
      error
    );

    for (
      auto pCar :
      (*this).lasCaracteristicas
    ) {

      /*
       * Las características almacenadas proceden de
       * anyadirCaracteristica(), que recibe referencias válidas.
       */
      if (pCar != nullptr) {
        (*pCar).activar();
      }
    }
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Permite utilizar ServicioEnEmisora allí donde
   * la biblioteca necesite un BLEService.
   *
   * Retorno: Referencia al servicio BLE interno.
   * --------------------------------------------------------------
   */
  operator BLEService&() {

    return elServicio;
  }

}; // class ServicioEnEmisora

#endif

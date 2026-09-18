// -*- mode: c++ -*-

// ----------------------------------------------------------
// Júlia Beltrán Girbés
// ----------------------------------------------------------

#ifndef SERVICIO_EMISORA_H_INCLUIDO
#define SERVICIO_EMISORA_H_INCLUIDO

#include <vector>

// ----------------------------------------------------------
// Utilidades
// ----------------------------------------------------------

/**
 * @brief Invierte el contenido de un array sobre el mismo array.
 *
 * Diseño lógico:
 * datos: [T], n: N --> alReves() --> [T]
 *
 * @tparam T Tipo de los elementos del array.
 * @param p Puntero al primer elemento del array.
 * @param n Número de elementos.
 *
 * @return Puntero al array invertido.
 */
template<typename T>
T * alReves(T * p, int n) {

  if (p == nullptr || n <= 0) {
    return p;
  }

  T aux;

  for (int i = 0; i < n / 2; i++) {
    aux = p[i];
    p[i] = p[n - i - 1];
    p[n - i - 1] = aux;
  }

  return p;
}

/**
 * @brief Copia un texto en un array de uint8_t en orden inverso.
 *
 * Diseño lógico:
 * texto: Text, array: [N], tam_max: N
 *      --> stringAUint8AlReves() --> [N]
 *
 * @param pString Texto que se desea copiar.
 * @param pUint Array de destino.
 * @param tamMax Tamaño máximo del array destino.
 *
 * @return Puntero al array de destino.
 */
uint8_t * stringAUint8AlReves(
  const char * pString,
  uint8_t * pUint,
  int tamMax
) {

  // Evita accesos inválidos a memoria.
  if (pString == nullptr ||
      pUint == nullptr ||
      tamMax <= 0) {

    return pUint;
  }

  int longitudString = strlen(pString);

  int longitudCopiar =
    (longitudString > tamMax)
      ? tamMax
      : longitudString;

  /*
   * Copia el texto de forma inversa dentro del array.
   * Se utiliza para construir UUIDs en el formato esperado
   * por la biblioteca BLE.
   */
  for (int i = 0; i < longitudCopiar; i++) {

    pUint[tamMax - i - 1] =
      pString[i];
  }

  return pUint;
}

// ----------------------------------------------------------
// ServicioEnEmisora
// ----------------------------------------------------------

/**
 * @brief Representa un servicio BLE que puede contener
 * distintas características.
 *
 * Permite crear un servicio, añadir características
 * y activarlas sobre la emisora BLE.
 */
class ServicioEnEmisora {

public:

  /**
   * @brief Tipo de callback ejecutado cuando una característica
   * recibe una escritura.
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

  /**
   * @brief Representa una característica perteneciente
   * a un servicio BLE.
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

    /**
     * @brief Constructor de una característica BLE.
     *
     * Diseño lógico:
     * nombre_caracteristica: Text
     *      --> Caracteristica()
     *
     * @param nombreCaracteristica_ Nombre utilizado para
     * construir el UUID.
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

    /**
     * @brief Constructor que permite configurar además las
     * propiedades, permisos y tamaño máximo de los datos.
     *
     * Diseño lógico:
     * nombre: Text,
     * propiedades: N,
     * permiso_lectura,
     * permiso_escritura,
     * tamanyo: N
     *      --> Caracteristica()
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

    /**
     * @brief Configura las propiedades de la característica.
     *
     * Por ejemplo:
     * CHR_PROPS_WRITE, CHR_PROPS_READ o CHR_PROPS_NOTIFY.
     */
    void asignarPropiedades(
      uint8_t props
    ) {

      (*this).laCaracteristica.setProperties(
        props
      );
    }

    /**
     * @brief Configura los permisos de lectura y escritura.
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

    /**
     * @brief Establece el tamaño máximo de los datos
     * de la característica.
     *
     * @param tam Tamaño máximo permitido.
     */
    void asignarTamanyoDatos(
      uint8_t tam
    ) {

      (*this).laCaracteristica.setMaxLen(
        tam
      );
    }

  public:

    /**
     * @brief Configura propiedades, permisos y tamaño
     * de los datos de una característica.
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

    /**
     * @brief Escribe un texto en la característica BLE.
     *
     * Diseño lógico:
     * datos: Text --> escribirDatos() --> N
     *
     * @param str Texto que se desea escribir.
     *
     * @return Número de bytes escritos.
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

    /**
     * @brief Envía una notificación BLE con los datos indicados.
     *
     * Diseño lógico:
     * datos: Text --> notificarDatos() --> N
     *
     * @param str Texto que se desea notificar.
     *
     * @return Resultado de la operación de notificación.
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

    /**
     * @brief Instala el callback que se ejecutará cuando
     * se escriba en la característica.
     */
    void instalarCallbackCaracteristicaEscrita(
      CallbackCaracteristicaEscrita cb
    ) {

      (*this).laCaracteristica.setWriteCallback(
        cb
      );
    }

    /**
     * @brief Activa la característica BLE.
     *
     * Llama a begin() sobre la característica e informa
     * del código de error mediante el puerto serie.
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

  /**
   * @brief Constructor del servicio BLE.
   *
   * Diseño lógico:
   * nombre_servicio: Text
   *      --> ServicioEnEmisora()
   *
   * @param nombreServicio_ Nombre utilizado para
   * construir el UUID del servicio.
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

  /**
   * @brief Escribe el UUID del servicio por el puerto serie.
   *
   * Método de apoyo para depuración.
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

  /**
   * @brief Añade una característica al servicio.
   *
   * Diseño lógico:
   * caracteristica: Caracteristica
   *      --> anyadirCaracteristica()
   */
  void anyadirCaracteristica(
    Caracteristica & car
  ) {

    (*this).lasCaracteristicas.push_back(
      &car
    );
  }

  /**
   * @brief Activa el servicio y todas sus características.
   *
   * Diseño lógico:
   * activarServicio()
   *
   * Primero se inicia el servicio BLE y después
   * se activan las características asociadas.
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

  /**
   * @brief Permite utilizar ServicioEnEmisora allí donde
   * la biblioteca necesite un BLEService.
   *
   * @return Referencia al servicio BLE interno.
   */
  operator BLEService&() {

    return elServicio;
  }

}; // class ServicioEnEmisora

#endif

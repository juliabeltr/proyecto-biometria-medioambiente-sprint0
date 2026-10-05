// -*- mode: c++ -*-

#ifndef PUERTO_SERIE_H_INCLUIDO
#define PUERTO_SERIE_H_INCLUIDO

// ----------------------------------------------------------
// Júlia Beltrán Girbés
// ----------------------------------------------------------

/**
 * @brief Clase encargada de gestionar la comunicación por puerto serie.
 *
 * Permite inicializar el puerto, esperar a que esté disponible
 * y escribir mensajes de distintos tipos.
 */
class PuertoSerie {

public:

  /**
   * @brief Constructor del puerto serie.
   *
   * Diseño lógico:
   * baudios: N --> PuertoSerie()
   *
   * @param baudios Velocidad de comunicación en baudios.
   */
  PuertoSerie(long baudios) {
    Serial.begin(baudios);
  }

  /**
   * @brief Espera hasta que el puerto serie esté disponible.
   *
   * Diseño lógico:
   * esperarDisponible()
   *
   * @note Utiliza una espera bloqueante de 10 ms mientras
   * el puerto no está disponible.
   */
  void esperarDisponible() {

    while (!Serial) {
      delay(10);
    }
  }

  /**
   * @brief Escribe un mensaje por el puerto serie.
   *
   * Diseño lógico:
   * mensaje --> escribir()
   *
   * @tparam T Tipo del dato que se desea escribir.
   * @param mensaje Valor que se enviará por el puerto serie.
   */
  template<typename T>
  void escribir(T mensaje) {
    Serial.print(mensaje);
  }

};

#endif

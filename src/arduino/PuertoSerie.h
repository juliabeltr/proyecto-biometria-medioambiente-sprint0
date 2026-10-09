// -*- mode: c++ -*-

#ifndef PUERTO_SERIE_H_INCLUIDO
#define PUERTO_SERIE_H_INCLUIDO

// ----------------------------------------------------------
// Júlia Beltrán Girbés
// ----------------------------------------------------------

/*
 * --------------------------------------------------------------
 * Propósito: Clase encargada de gestionar la comunicación por puerto serie.
 *
 * Permite inicializar el puerto, esperar a que esté disponible
 * y escribir mensajes de distintos tipos.
 * --------------------------------------------------------------
 */
class PuertoSerie {

public:

  /*
   * --------------------------------------------------------------
   * Propósito: Constructor del puerto serie.
   *
   * Diseño lógico:
   * baudios: N --> PuertoSerie() -->
   *
   * Parámetro: baudios Velocidad de comunicación en baudios.
   * --------------------------------------------------------------
   */
  PuertoSerie(long baudios) {
    Serial.begin(baudios);
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Espera hasta que el puerto serie esté disponible.
   *
   * Diseño lógico:
   * esperarDisponible() -->
   *
   * Nota: Utiliza una espera bloqueante de 10 ms mientras
   * el puerto no está disponible.
   * --------------------------------------------------------------
   */
  void esperarDisponible() {

    while (!Serial) {
      delay(10);
    }
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Escribe un mensaje por el puerto serie.
   *
   * Diseño lógico:
   * mensaje: Text --> escribir() -->
   *
   * Tipo genérico: T Tipo del dato que se desea escribir.
   * Parámetro: mensaje Valor que se enviará por el puerto serie.
   * --------------------------------------------------------------
   */
  template<typename T>
  void escribir(T mensaje) {
    Serial.print(mensaje);
  }

};

#endif

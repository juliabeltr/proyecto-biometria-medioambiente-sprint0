// -*- mode: c++ -*-

#ifndef LED_H_INCLUIDO
#define LED_H_INCLUIDO

// ----------------------------------------------------------
// Júlia Beltrán Girbés
// ----------------------------------------------------------

/*
 * --------------------------------------------------------------
 * Propósito: Realiza una espera bloqueante.
 *
 * Diseño lógico:
 * tiempo: Z --> esperar() -->
 *
 * Parámetro: tiempo Tiempo de espera en milisegundos.
 *
 * Nota: Esta función bloquea la ejecución durante el tiempo indicado.
 * --------------------------------------------------------------
 */
void esperar(long tiempo) {
  delay(tiempo);
}

/*
 * --------------------------------------------------------------
 * Propósito: Clase encargada de controlar un LED digital.
 *
 * Mantiene el número de pin asociado al LED y su estado lógico.
 * --------------------------------------------------------------
 */
class LED {

private:

  int numeroLED;
  bool encendido;

public:

  /*
   * --------------------------------------------------------------
   * Propósito: Constructor de la clase LED.
   *
   * Diseño lógico:
   * numero: N --> LED() -->
   *
   * Parámetro: numero Número de pin al que está conectado el LED.
   * --------------------------------------------------------------
   */
  LED(int numero)
    : numeroLED(numero), encendido(false)
  {
    pinMode(numeroLED, OUTPUT);
    apagar();
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Enciende el LED.
   *
   * Diseño lógico:
   * encender()
   * --------------------------------------------------------------
   */
  void encender() {
    digitalWrite(numeroLED, HIGH);
    encendido = true;
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Apaga el LED.
   *
   * Diseño lógico:
   * apagar()
   * --------------------------------------------------------------
   */
  void apagar() {
    digitalWrite(numeroLED, LOW);
    encendido = false;
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Cambia el estado actual del LED.
   *
   * Diseño lógico:
   * alternar()
   *
   * Si está encendido, lo apaga.
   * Si está apagado, lo enciende.
   * --------------------------------------------------------------
   */
  void alternar() {

    if (encendido) {
      apagar();
    } else {
      encender();
    }
  }

  /*
   * --------------------------------------------------------------
   * Propósito: Enciende el LED durante un tiempo determinado.
   *
   * Diseño lógico:
   * tiempo: Z --> brillar() -->
   *
   * Parámetro: tiempo Tiempo, en milisegundos, que el LED permanece encendido.
   *
   * Nota: Utiliza una espera bloqueante.
   * --------------------------------------------------------------
   */
  void brillar(long tiempo) {

    encender();
    esperar(tiempo);
    apagar();
  }

};

#endif

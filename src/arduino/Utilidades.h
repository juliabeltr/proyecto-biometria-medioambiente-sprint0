// -*- mode: c++ -*-

// ----------------------------------------------------------
// Utilidades.h
//
// Descripción: funciones puras (sin Arduino ni Bluefruit) que usa la placa.
//              Se pueden probar en el ordenador (ver tests/).
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Aportación:  Implementación de doc/arduino_design.md (funciones generales).
// ----------------------------------------------------------

#ifndef UTILIDADES_H_INCLUIDO
#define UTILIDADES_H_INCLUIDO

#include <stdint.h>
#include <string.h>

/*
 * --------------------------------------------------------------
 * Propósito: invierte el contenido de un array sobre el mismo array.
 *
 * Diseño lógico:
 *     p: [T], n: N --> alReves() -->
 *     p: [T] <--
 *
 * Parámetros:
 *     p: [T]. Array que se invierte.
 *     n: N. Número de elementos.
 * Retorno: el mismo array, invertido.
 * Errores: con p nulo o n <= 0 no cambia nada.
 * --------------------------------------------------------------
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

/*
 * --------------------------------------------------------------
 * Propósito: copia un texto en un array de bytes en orden inverso. Se
 *            usa para construir UUIDs en el formato de la biblioteca BLE.
 *
 * Diseño lógico:
 *     texto: Text, pUint: [N], tamMax: N --> stringAUint8AlReves() -->
 *     pUint: [N] <--
 *
 * Parámetros:
 *     texto: Text. Texto que se copia.
 *     pUint: [N]. Array de destino.
 *     tamMax: N. Tamaño del array de destino.
 * Retorno: el array de destino.
 * Errores: con puntero nulo o tamMax <= 0 no cambia nada.
 * --------------------------------------------------------------
 */
inline uint8_t * stringAUint8AlReves(
  const char * pString,
  uint8_t * pUint,
  int tamMax
) {

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

  for (int i = 0; i < longitudCopiar; i++) {

    pUint[tamMax - i - 1] =
      pString[i];
  }

  return pUint;
}

/*
 * --------------------------------------------------------------
 * Propósito: calcula el campo major del iBeacon: el tipo de medida en el
 *            byte alto y el contador en el byte bajo.
 *
 * Diseño lógico:
 *     tipo: N, contador: N --> calcularMajor() -->
 *     major: N <--
 *
 * Parámetros:
 *     tipo: N. Identificador de la medida (11 CO2, 12 TEMPERATURA, 13 RUIDO).
 *     contador: N. Contador de anuncios (0 a 255).
 * Retorno: major = tipo * 256 + contador.
 * Errores: ninguno.
 * --------------------------------------------------------------
 */
inline uint16_t calcularMajor(uint8_t tipo, uint8_t contador) {
  return (uint16_t) ((tipo << 8) + contador);
}

#endif

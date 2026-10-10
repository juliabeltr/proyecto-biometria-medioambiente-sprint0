// -----------------------------------------------------------------
// test_utilidades.cpp
//
// Tests automáticos en el ordenador de las funciones puras de la placa
// (Utilidades.h). No necesitan Arduino ni la placa.
//
// Compilar y ejecutar (desde src/arduino):
//     g++ -std=c++11 -I. tests/test_utilidades.cpp -o test_utilidades
//     ./test_utilidades
// -----------------------------------------------------------------

#include <cstdio>
#include <cstdlib>
#include "Utilidades.h"

static int fallos = 0;

#define COMPROBAR(cond) \
  do { if (!(cond)) { std::printf("FALLO linea %d: %s\n", __LINE__, #cond); fallos++; } } while (0)

static void alReves_invierte_longitud_par() {
  int a[4] = {1, 2, 3, 4};
  alReves(a, 4);
  COMPROBAR(a[0] == 4 && a[1] == 3 && a[2] == 2 && a[3] == 1);
}

static void alReves_invierte_longitud_impar() {
  int a[5] = {1, 2, 3, 4, 5};
  alReves(a, 5);
  COMPROBAR(a[0] == 5 && a[2] == 3 && a[4] == 1);
}

static void alReves_un_elemento_y_vacio() {
  int a[1] = {7};
  alReves(a, 1);
  COMPROBAR(a[0] == 7);
  alReves(a, 0);
  COMPROBAR(a[0] == 7);
}

static void alReves_puntero_nulo() {
  int * p = nullptr;
  COMPROBAR(alReves(p, 3) == nullptr);
}

static void stringAUint8_copia_inversa_en_16() {
  uint8_t uuid[16] = {0};
  stringAUint8AlReves("EPSG-GTI-PROY-3A", uuid, 16);
  COMPROBAR(uuid[15] == 'E');
  COMPROBAR(uuid[0] == 'A');
  COMPROBAR(uuid[1] == '3');
}

static void stringAUint8_texto_corto() {
  uint8_t destino[4] = {9, 9, 9, 9};
  stringAUint8AlReves("ab", destino, 4);
  COMPROBAR(destino[3] == 'a');
  COMPROBAR(destino[2] == 'b');
  COMPROBAR(destino[0] == 9);
}

static void stringAUint8_texto_largo_se_recorta() {
  uint8_t destino[2] = {0, 0};
  stringAUint8AlReves("abcdef", destino, 2);
  COMPROBAR(destino[1] == 'a');
  COMPROBAR(destino[0] == 'b');
}

static void stringAUint8_punteros_nulos_y_tam_invalido() {
  uint8_t destino[2] = {5, 5};
  COMPROBAR(stringAUint8AlReves(nullptr, destino, 2) == destino);
  COMPROBAR(stringAUint8AlReves("a", nullptr, 2) == nullptr);
  COMPROBAR(stringAUint8AlReves("a", destino, 0) == destino);
  COMPROBAR(destino[0] == 5 && destino[1] == 5);
}

static void major_co2_temperatura_ruido() {
  COMPROBAR(calcularMajor(11, 0) == 2816);
  COMPROBAR(calcularMajor(11, 5) == 2821);
  COMPROBAR(calcularMajor(12, 255) == 3327);
  COMPROBAR(calcularMajor(13, 1) == 3329);
}

static void major_tipo_en_byte_alto_contador_en_bajo() {
  uint16_t m = calcularMajor(12, 7);
  COMPROBAR((m >> 8) == 12);
  COMPROBAR((m & 0xFF) == 7);
}

int main() {
  alReves_invierte_longitud_par();
  alReves_invierte_longitud_impar();
  alReves_un_elemento_y_vacio();
  alReves_puntero_nulo();
  stringAUint8_copia_inversa_en_16();
  stringAUint8_texto_corto();
  stringAUint8_texto_largo_se_recorta();
  stringAUint8_punteros_nulos_y_tam_invalido();
  major_co2_temperatura_ruido();
  major_tipo_en_byte_alto_contador_en_bajo();
  if (fallos == 0) {
    std::printf("Todos los tests de Utilidades.h pasan.\n");
    return 0;
  }
  std::printf("%d fallos\n", fallos);
  return 1;
}

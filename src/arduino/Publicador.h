// -*- mode: c++ -*-

// --------------------------------------------------------------
// Jordi Bataller i Mascarell
// --------------------------------------------------------------

#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO

#include "Utilidades.h"

// --------------------------------------------------------------
// --------------------------------------------------------------
class Publicador {

  // ............................................................
  // ............................................................
private:

  uint8_t beaconUUID[16] = { 
	'E', 'P', 'S', 'G', '-', 'G', 'T', 'I', 
	'-', 'P', 'R', 'O', 'Y', '-', '3', 'A'
	};

EmisoraBLE laEmisora {
    "GTI-3A",
    0x004c,
    4
  };

  // ............................................................
  // ............................................................
public:
  const int RSSI = -53; // por poner algo, de momento no lo uso

  // ............................................................
  // ............................................................
public:

  // ............................................................
  // ............................................................
  enum MedicionesID  {
	CO2 = 11,
	TEMPERATURA = 12,
	RUIDO = 13
  };

  // ............................................................
  // ............................................................
  /*
   * --------------------------------------------------------------
   * Propósito: crea el publicador de anuncios.
   *
   * Diseño lógico:
   *     Publicador() -->
   * --------------------------------------------------------------
   */
  Publicador( ) {
	// ATENCION: no hacerlo aquí. (*this).laEmisora.encenderEmisora();
	// Pondremos un método para llamarlo desde el setup() más tarde
  } // ()

  // ............................................................
  // ............................................................
  /*
   * --------------------------------------------------------------
   * Propósito: enciende la emisora BLE (se llama desde setup()).
   *
   * Diseño lógico:
   *     encenderEmisora() -->
   * --------------------------------------------------------------
   */
  void encenderEmisora() {
	(*this).laEmisora.encenderEmisora();
  } // ()

  // ............................................................
  // ............................................................
  /*
   * --------------------------------------------------------------
   * Propósito: emite un anuncio con el CO2, espera y lo detiene.
   *
   * Diseño lógico:
   *     valor_co2: Z, contador: N, tiempo_espera: Z --> publicarCO2() -->
   *
   * Parámetros: valor en el minor, contador en el byte bajo del major,
   * tiempo_espera en milisegundos.
   * --------------------------------------------------------------
   */
  void publicarCO2( int16_t valorCO2, uint8_t contador,
					long tiempoEspera ) {

	//
	// 1. empezamos anuncio
	//
	uint16_t major = calcularMajor(MedicionesID::CO2, contador);
	(*this).laEmisora.emitirAnuncioIBeacon( (*this).beaconUUID, 
											major,
											valorCO2, // minor
											(*this).RSSI // rssi
									);

	/*
	Globales::elPuerto.escribir( "   publicarCO2(): valor=" );
	Globales::elPuerto.escribir( valorCO2 );
	Globales::elPuerto.escribir( "   contador=" );
	Globales::elPuerto.escribir( contador );
	Globales::elPuerto.escribir( "   todo="  );
	Globales::elPuerto.escribir( major );
	Globales::elPuerto.escribir( "\n" );
	*/

	//
	// 2. esperamos el tiempo que nos digan
	//
	esperar( tiempoEspera );

	//
	// 3. paramos anuncio
	//
	(*this).laEmisora.detenerAnuncio();
  } // ()

  // ............................................................
  // ............................................................
  /*
   * --------------------------------------------------------------
   * Propósito: emite un anuncio con la temperatura, espera y lo detiene.
   *
   * Diseño lógico:
   *     valor_temperatura: Z, contador: N, tiempo_espera: Z --> publicarTemperatura() -->
   *
   * Parámetros: valor en el minor, contador en el byte bajo del major,
   * tiempo_espera en milisegundos.
   * --------------------------------------------------------------
   */
  void publicarTemperatura( int16_t valorTemperatura,
							uint8_t contador, long tiempoEspera ) {

	uint16_t major = calcularMajor(MedicionesID::TEMPERATURA, contador);
	(*this).laEmisora.emitirAnuncioIBeacon( (*this).beaconUUID, 
											major,
											valorTemperatura, // minor
											(*this).RSSI // rssi
									);
	esperar( tiempoEspera );

	(*this).laEmisora.detenerAnuncio();
  } // ()

  /*
   * --------------------------------------------------------------
   * Propósito: emite un anuncio iBeacon con una carga libre (sin esperar).
   *
   * Diseño lógico:
   *     carga: Text, tamanyo_carga: N --> emitirAnuncioLibre() -->
   * --------------------------------------------------------------
   */
  void emitirAnuncioLibre(const char * carga, uint8_t tamanyoCarga) {
    (*this).laEmisora.emitirAnuncioIBeaconLibre(carga, tamanyoCarga);
  }

  /*
   * --------------------------------------------------------------
   * Propósito: detiene el anuncio en curso.
   *
   * Diseño lógico:
   *     detenerAnuncio() -->
   * --------------------------------------------------------------
   */
  void detenerAnuncio() {
    (*this).laEmisora.detenerAnuncio();
  }
}; // class

// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
#endif

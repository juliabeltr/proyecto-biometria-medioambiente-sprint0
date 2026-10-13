package com.example.jbelgir.medicionesapp;

// =============================================================================
// Punto.java
//
// Descripción: punto geográfico (latitud y longitud) de una medición.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/android_design.md (clase Punto).
// =============================================================================

/**
 * Punto = ( latitud: R, longitud: R )
 */
public final class Punto {

    private final double latitud;
    private final double longitud;

    /*
     * --------------------------------------------------------------
     * Propósito: crea un punto geográfico.
     *
     * Diseño lógico:
     *     latitud: R, longitud: R --> Punto() -->
     *
     * Parámetros:
     *     latitud: R. Grados, entre -90 y 90.
     *     longitud: R. Grados, entre -180 y 180.
     * Retorno: ninguno.
     * Errores: ninguno (el servidor valida los rangos).
     * --------------------------------------------------------------
     */
    public Punto(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: devuelve la latitud del punto.
     *
     * Diseño lógico:
     *     latitud: R <-- getLatitud() <--
     *
     * Parámetros: ninguno.
     * Retorno: latitud: R.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public double getLatitud() {
        return latitud;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: devuelve la longitud del punto.
     *
     * Diseño lógico:
     *     longitud: R <-- getLongitud() <--
     *
     * Parámetros: ninguno.
     * Retorno: longitud: R.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public double getLongitud() {
        return longitud;
    }
}

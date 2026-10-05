package com.example.jbelgir.medicionesapp;

// =============================================================================
// EscuchadorBeacons.java
//
// Descripción: receptor de los anuncios BLE que entrega EscanerBeacons.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/android_design.md (EscuchadorBeacons).
// =============================================================================

public interface EscuchadorBeacons {

    /*
     * --------------------------------------------------------------
     * Propósito: recibe los bytes de un anuncio BLE detectado.
     *
     * Diseño lógico:
     *     bytes: [Z] --> alRecibirAnuncio() -->
     *
     * Parámetros:
     *     bytes: [Z]. Bytes del anuncio.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    void alRecibirAnuncio(byte[] bytes);
}
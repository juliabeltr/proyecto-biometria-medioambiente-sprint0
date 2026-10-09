package com.example.jbelgir.medicionesapp;

// =============================================================================
// LogicaTelefono.java
//
// Descripción: interfaz común de la lógica del teléfono (real y fake).
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/android_design.md (LogicaTelefono).
//              Misma idea que LogicaNavegador y LogicaNavegadorFake.
// =============================================================================

public interface LogicaTelefono {

    /**
     * Recibe el resultado de enviar una medición.
     */
    interface ResultadoEnvio {

        /*
         * Diseño lógico:
         *     resultado: B --> callback() -->
         *
         * resultado: true si la medición se ha guardado en el servidor.
         */
        void callback(boolean resultado);
    }

    /*
     * --------------------------------------------------------------
     * Propósito: envía una medición al servidor. La operación es
     *            asíncrona: el resultado llega por ResultadoEnvio.
     *
     * Diseño lógico:
     *     m: Medicion --> guardarMedicion() -->
     *       resultado: B <--
     *
     * Parámetros:
     *     m: Medicion. Medición a enviar.
     *     resultadoEnvio: ResultadoEnvio. Recibe true si se ha guardado.
     * Retorno: ninguno.
     * Errores: ninguno hacia fuera: un fallo se notifica con false.
     * --------------------------------------------------------------
     */
    void guardarMedicion(Medicion m, ResultadoEnvio resultadoEnvio);
}
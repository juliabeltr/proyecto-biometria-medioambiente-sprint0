package com.example.jbelgir.medicionesapp;

import java.util.ArrayList;
import java.util.List;

// =============================================================================
// LogicaTelefonoFake.java
//
// Descripción: lógica fake del teléfono: guarda las mediciones en memoria sin
//              usar la red.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/android_design.md (LogicaTelefonoFake).
// =============================================================================

public class LogicaTelefonoFake implements LogicaTelefono {

    private final List<Medicion> enviadas = new ArrayList<>();
    private final boolean falla;

    /*
     * --------------------------------------------------------------
     * Propósito: crea la lógica fake.
     *
     * Diseño lógico:
     *     falla: B --> LogicaTelefonoFake() -->
     *
     * Parámetros:
     *     falla: B. Si es true, guardarMedicion() no guarda y devuelve false.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public LogicaTelefonoFake(boolean falla) {
        this.falla = falla;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: guarda la medición en memoria y avisa del resultado
     *            de inmediato (sin red).
     *
     * Diseño lógico:
     *     m: Medicion --> guardarMedicion() -->
     *       resultado: B <--
     *
     * Parámetros:
     *     m: Medicion. Medición a guardar.
     *     resultadoEnvio: ResultadoEnvio. Recibe true si se ha guardado.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    @Override
    public void guardarMedicion(Medicion m, ResultadoEnvio resultadoEnvio) {
        if (falla) {
            resultadoEnvio.callback(false);
            return;
        }
        enviadas.add(m);
        resultadoEnvio.callback(true);
    }

    /*
     * --------------------------------------------------------------
     * Propósito: devuelve las mediciones guardadas, en orden de envío.
     *
     * Diseño lógico:
     *     [Medicion] <-- getEnviadas() <--
     *
     * Parámetros: ninguno.
     * Retorno: [Medicion]. Copia de la lista; vacía si no hay ninguna.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public List<Medicion> getEnviadas() {
        return new ArrayList<>(enviadas);
    }
}

package com.example.jbelgir.medicionesapp;

import org.json.JSONException;
import org.json.JSONObject;

// =============================================================================
// Medicion.java
//
// Descripción: medición ambiental que la app envía al servidor.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/android_design.md (clase Medicion).
// =============================================================================

/**
 * Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R,
 *              fechaHora: Text )
 */
public final class Medicion {

    private final int id;
    private final String tipo;
    private final double valor;
    private final double latitud;
    private final double longitud;
    private final String fechaHora;

    /*
     * --------------------------------------------------------------
     * Propósito: crea una medición.
     *
     * Diseño lógico:
     *     id: N, tipo: Text, valor: R, latitud: R, longitud: R,
     *     fechaHora: Text --> Medicion() -->
     *
     * Parámetros: los seis campos de la medición.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public Medicion(int id, String tipo, double valor, double latitud,
                    double longitud, String fechaHora) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fechaHora = fechaHora;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: devuelve el campo id de la medición.
     *
     * Diseño lógico:
     *     id: N <-- getId() <--
     *
     * Parámetros: ninguno.
     * Retorno: id: N.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public int getId() {
        return id;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: devuelve el campo tipo de la medición.
     *
     * Diseño lógico:
     *     tipo: Text <-- getTipo() <--
     *
     * Parámetros: ninguno.
     * Retorno: tipo: Text.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public String getTipo() {
        return tipo;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: devuelve el campo valor de la medición.
     *
     * Diseño lógico:
     *     valor: R <-- getValor() <--
     *
     * Parámetros: ninguno.
     * Retorno: valor: R.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public double getValor() {
        return valor;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: devuelve el campo latitud de la medición.
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
     * Propósito: devuelve el campo longitud de la medición.
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

    /*
     * --------------------------------------------------------------
     * Propósito: devuelve el campo fechaHora de la medición.
     *
     * Diseño lógico:
     *     fechaHora: Text <-- getFechaHora() <--
     *
     * Parámetros: ninguno.
     * Retorno: fechaHora: Text.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public String getFechaHora() {
        return fechaHora;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: devuelve la medición como objeto JSON con los campos
     *            tipo, valor, latitud, longitud y fechaHora (sin id, que
     *            asigna la base de datos). Los números usan punto decimal.
     *
     * Diseño lógico:
     *     texto: Text <-- aJSON() <--
     *
     * Parámetros: ninguno.
     * Retorno: texto: Text. Objeto JSON.
     * Errores: IllegalStateException si un número no es representable
     *          en JSON (NaN o infinito).
     * --------------------------------------------------------------
     */
    public String aJSON() {
        try {
            JSONObject json = new JSONObject();
            json.put("tipo", tipo);
            json.put("valor", valor);
            json.put("latitud", latitud);
            json.put("longitud", longitud);
            json.put("fechaHora", fechaHora);
            return json.toString();
        } catch (JSONException e) {
            throw new IllegalStateException("Medicion no representable en JSON", e);
        }
    }
}
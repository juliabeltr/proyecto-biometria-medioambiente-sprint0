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

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

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
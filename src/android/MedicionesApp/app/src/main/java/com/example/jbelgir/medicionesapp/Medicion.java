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
 * Medicion = ( sensorId: Text, tipo: Text, valor: R, ubicacion: Punto,
 *              fechaHora: Text )
 */
public final class Medicion {

    private final String sensorId;
    private final String tipo;
    private final double valor;
    private final Punto ubicacion;
    private final String fechaHora;

    /*
     * --------------------------------------------------------------
     * Propósito: crea una medición.
     *
     * Diseño lógico:
     *     sensorId: Text, tipo: Text, valor: R, ubicacion: Punto,
     *     fechaHora: Text --> Medicion() -->
     *
     * Parámetros: los cinco campos de la medición.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public Medicion(String sensorId, String tipo, double valor,
                    Punto ubicacion, String fechaHora) {
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valor = valor;
        this.ubicacion = ubicacion;
        this.fechaHora = fechaHora;
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
     * Propósito: devuelve el campo sensorId de la medición.
     *
     * Diseño lógico:
     *     sensorId: Text <-- getSensorId() <--
     *
     * Parámetros: ninguno.
     * Retorno: sensorId: Text.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public String getSensorId() {
        return sensorId;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: devuelve el campo ubicacion de la medición.
     *
     * Diseño lógico:
     *     ubicacion: Punto <-- getUbicacion() <--
     *
     * Parámetros: ninguno.
     * Retorno: ubicacion: Punto.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public Punto getUbicacion() {
        return ubicacion;
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
     *            sensorId, tipo, valor, ubicacion (objeto con latitud y
     *            longitud) y fechaHora. Los números usan punto decimal.
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
            JSONObject jsonUbicacion = new JSONObject();
            jsonUbicacion.put("latitud", ubicacion.getLatitud());
            jsonUbicacion.put("longitud", ubicacion.getLongitud());
            json.put("sensorId", sensorId);
            json.put("tipo", tipo);
            json.put("valor", valor);
            json.put("ubicacion", jsonUbicacion);
            json.put("fechaHora", fechaHora);
            return json.toString();
        } catch (JSONException e) {
            throw new IllegalStateException("Medicion no representable en JSON", e);
        }
    }
}
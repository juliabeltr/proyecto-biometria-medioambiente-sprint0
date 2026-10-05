package com.example.jbelgir.medicionesapp;

// =============================================================================
// ConversorBeacon.java
//
// Descripción: convierte una trama iBeacon de la placa en una Medicion.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/android_design.md (ConversorBeacon).
//              UUID = texto del proyecto; major = tipo (byte alto) y
//              contador (byte bajo); minor = valor con signo.
// =============================================================================

public final class ConversorBeacon {

    public static final String UUID_PROYECTO = "EPSG-GTI-PROY-3A";

    private static final int ID_CO2 = 11;
    private static final int ID_TEMPERATURA = 12;
    private static final int ID_RUIDO = 13;

    private ConversorBeacon() {
    }

    /*
     * --------------------------------------------------------------
     * Propósito: convierte la trama en una Medicion con la fecha y la
     *            ubicación recibidas (la trama no las lleva).
     *
     * Diseño lógico:
     *     trama: TramaIBeacon, fechaHora: Text, latitud: R, longitud: R
     *         --> convertir() --x
     *         medicion: Medicion <--
     *
     * Parámetros:
     *     trama: TramaIBeacon. Anuncio recibido.
     *     fechaHora: Text. Fecha y hora ISO 8601 de la recepción.
     *     latitud: R. Latitud de la medición.
     *     longitud: R. Longitud de la medición.
     * Retorno: Medicion, o null si el UUID no es el del proyecto o el
     *          tipo no es 11, 12 o 13.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public static Medicion convertir(TramaIBeacon trama, String fechaHora,
                                     double latitud, double longitud) {
        if (trama == null) {
            return null;
        }
        if (!UUID_PROYECTO.equals(Utilidades.bytesToString(trama.getUUID()))) {
            return null;
        }
        int major = Utilidades.bytesToInt(trama.getMajor());
        String tipo = nombreDelTipo((major >> 8) & 0xFF);
        if (tipo == null) {
            return null;
        }
        double valor = Utilidades.bytesToInt(trama.getMinor());
        return new Medicion(0, tipo, valor, latitud, longitud, fechaHora);
    }

    /*
     * --------------------------------------------------------------
     * Propósito: traduce el identificador de tipo de la placa al nombre
     *            de TipoMedicion.
     *
     * Diseño lógico:
     *     idTipo: N --> nombreDelTipo() --x --> tipo: Text
     *
     * Parámetros:
     *     idTipo: N. 11 = CO2, 12 = TEMP, 13 = RUIDO.
     * Retorno: Text, o null si el identificador no es conocido.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    private static String nombreDelTipo(int idTipo) {
        switch (idTipo) {
            case ID_CO2:
                return "CO2";
            case ID_TEMPERATURA:
                return "TEMP";
            case ID_RUIDO:
                return "RUIDO";
            default:
                return null;
        }
    }
}
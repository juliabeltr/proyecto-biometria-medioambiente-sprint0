package com.example.jbelgir.medicionesapp;

// =============================================================================
// ProcesadorBeacons.java
//
// Descripción: procesa cada anuncio BLE recibido: lo interpreta, descarta lo
//              que no es nuestro o está repetido y envía la medición.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/android_design.md (ProcesadorBeacons).
// =============================================================================

public class ProcesadorBeacons {

    private static final int LONGITUD_MINIMA_TRAMA = 30;

    private final LogicaTelefono logica;
    private final FiltroDuplicados filtro = new FiltroDuplicados();
    private final double latitud;
    private final double longitud;

    /*
     * --------------------------------------------------------------
     * Propósito: crea el procesador con la lógica a la que enviar las
     *            mediciones y la ubicación fija del Sprint 0.
     *
     * Diseño lógico:
     *     logica: LogicaTelefono, latitud: R, longitud: R
     *         --> ProcesadorBeacons() -->
     *
     * Parámetros:
     *     logica: LogicaTelefono. Lógica real o fake.
     *     latitud: R. Latitud que se asigna a las mediciones.
     *     longitud: R. Longitud que se asigna a las mediciones.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public ProcesadorBeacons(LogicaTelefono logica, double latitud, double longitud) {
        this.logica = logica;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: procesa un anuncio BLE. Ignora una trama nula o de
     *            menos de 30 bytes, una que no sea medición nuestra y una
     *            repetida; en otro caso envía la medición.
     *
     * Diseño lógico:
     *     bytes: [Z], fechaHora: Text --> procesar() -->
     *       resultado: B <--
     *
     * Parámetros:
     *     bytes: [Z]. Bytes del anuncio recibido.
     *     fechaHora: Text. Fecha y hora ISO 8601 UTC de la recepción.
     * Retorno: B. true si ha enviado la medición; false si la ha ignorado.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public boolean procesar(byte[] bytes, String fechaHora) {
        if (bytes == null || bytes.length < LONGITUD_MINIMA_TRAMA) {
            return false;
        }
        TramaIBeacon trama = new TramaIBeacon(bytes);
        Medicion medicion = ConversorBeacon.convertir(trama, fechaHora, latitud, longitud);
        if (medicion == null) {
            return false;
        }
        if (!filtro.esNueva(Utilidades.bytesToInt(trama.getMajor()))) {
            return false;
        }
        logica.enviarMedicion(medicion, new LogicaTelefono.ResultadoEnvio() {
            @Override
            public void callback(boolean resultado) {
                // El resultado lo muestra quien envuelve la lógica (MainActivity).
            }
        });
        return true;
    }
}
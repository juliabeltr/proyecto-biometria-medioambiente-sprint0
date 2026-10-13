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
    private final Punto ubicacion;

    /*
     * --------------------------------------------------------------
     * Propósito: crea el procesador con la lógica a la que enviar las
     *            mediciones y la ubicación fija del Sprint 0.
     *
     * Diseño lógico:
     *     logica: LogicaTelefono, ubicacion: Punto
     *         --> ProcesadorBeacons() -->
     *
     * Parámetros:
     *     logica: LogicaTelefono. Lógica real o fake.
     *     ubicacion: Punto. Ubicación que se asigna a las mediciones.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public ProcesadorBeacons(LogicaTelefono logica, Punto ubicacion) {
        this.logica = logica;
        this.ubicacion = ubicacion;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: procesa un anuncio BLE. Ignora una trama nula o de
     *            menos de 30 bytes, una que no sea medición nuestra y una
     *            repetida; en otro caso envía la medición.
     *
     * Diseño lógico:
     *     bytes: [Z], sensorId: Text, fechaHora: Text --> procesar() -->
     *       resultado: B <--
     *
     * Parámetros:
     *     bytes: [Z]. Bytes del anuncio recibido.
     *     sensorId: Text. Dirección Bluetooth del emisor del anuncio.
     *     fechaHora: Text. Fecha y hora ISO 8601 UTC de la recepción.
     * Retorno: B. true si ha enviado la medición; false si la ha ignorado.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public boolean procesar(byte[] bytes, String sensorId, String fechaHora) {
        if (bytes == null || bytes.length < LONGITUD_MINIMA_TRAMA) {
            return false;
        }
        TramaIBeacon trama = new TramaIBeacon(bytes);
        Medicion medicion = ConversorBeacon.convertir(trama, sensorId, fechaHora, ubicacion);
        if (medicion == null) {
            return false;
        }
        if (!filtro.esNueva(Utilidades.bytesToInt(trama.getMajor()))) {
            return false;
        }
        logica.guardarMedicion(medicion, new LogicaTelefono.ResultadoEnvio() {
            @Override
            public void callback(boolean resultado) {
                // El resultado lo muestra quien envuelve la lógica (MainActivity).
            }
        });
        return true;
    }
}
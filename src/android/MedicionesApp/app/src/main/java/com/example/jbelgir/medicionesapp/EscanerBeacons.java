package com.example.jbelgir.medicionesapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.util.Log;

// =============================================================================
// EscanerBeacons.java
//
// Descripción: escanea anuncios Bluetooth Low Energy y entrega sus bytes a un
//              EscuchadorBeacons.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/android_design.md (EscanerBeacons).
//              Escanea sin filtros en modo de baja latencia. No interpreta
//              los datos.
// =============================================================================

public class EscanerBeacons {

    private static final String ETIQUETA_LOG = "MedicionesApp";

    private final EscuchadorBeacons escuchador;
    private BluetoothLeScanner escaner = null;
    private ScanCallback callbackDelEscaneo = null;

    /*
     * --------------------------------------------------------------
     * Propósito: crea el escáner. No empieza a escanear.
     *
     * Diseño lógico:
     *     escuchador: EscuchadorBeacons --> EscanerBeacons() -->
     *
     * Parámetros:
     *     escuchador: EscuchadorBeacons. Recibe los bytes de cada anuncio.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public EscanerBeacons(EscuchadorBeacons escuchador) {
        this.escuchador = escuchador;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: empieza a escanear. Si ya escanea, no hace nada. Si el
     *            Bluetooth está apagado o faltan permisos, no escanea:
     *            estaEscaneando() seguirá devolviendo false.
     *
     * Diseño lógico:
     *     iniciarEscaneo() -->
     *
     * Parámetros: ninguno.
     * Retorno: ninguno.
     * Errores: ninguno hacia fuera: el fallo se anota en el log.
     * --------------------------------------------------------------
     */
    @SuppressLint("MissingPermission")
    public void iniciarEscaneo() {
        if (estaEscaneando()) {
            return;
        }
        BluetoothAdapter adaptador = BluetoothAdapter.getDefaultAdapter();
        if (adaptador == null || !adaptador.isEnabled()) {
            Log.d(ETIQUETA_LOG, "iniciarEscaneo(): Bluetooth no disponible o apagado");
            return;
        }
        BluetoothLeScanner nuevoEscaner = adaptador.getBluetoothLeScanner();
        if (nuevoEscaner == null) {
            Log.d(ETIQUETA_LOG, "iniciarEscaneo(): no hay escáner BLE");
            return;
        }
        ScanCallback callback = crearCallback();
        ScanSettings ajustes = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        try {
            nuevoEscaner.startScan(null, ajustes, callback);
        } catch (SecurityException e) {
            Log.d(ETIQUETA_LOG, "iniciarEscaneo(): faltan permisos: " + e.getMessage());
            return;
        }
        escaner = nuevoEscaner;
        callbackDelEscaneo = callback;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: detiene el escaneo. Si no escanea, no hace nada.
     *
     * Diseño lógico:
     *     detenerEscaneo() -->
     *
     * Parámetros: ninguno.
     * Retorno: ninguno.
     * Errores: ninguno hacia fuera.
     * --------------------------------------------------------------
     */
    @SuppressLint("MissingPermission")
    public void detenerEscaneo() {
        if (callbackDelEscaneo == null) {
            return;
        }
        try {
            escaner.stopScan(callbackDelEscaneo);
        } catch (SecurityException e) {
            Log.d(ETIQUETA_LOG, "detenerEscaneo(): faltan permisos: " + e.getMessage());
        }
        escaner = null;
        callbackDelEscaneo = null;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: indica si hay un escaneo en marcha.
     *
     * Diseño lógico:
     *     resultado: B <-- estaEscaneando() <--
     *
     * Parámetros: ninguno.
     * Retorno: B. true si está escaneando.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public boolean estaEscaneando() {
        return callbackDelEscaneo != null;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: crea el callback de Android que entrega los bytes de
     *            cada anuncio al escuchador y anota los fallos.
     *
     * Diseño lógico:
     *     crearCallback() --> callback
     *
     * Parámetros: ninguno.
     * Retorno: ScanCallback.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    private ScanCallback crearCallback() {
        return new ScanCallback() {
            @Override
            public void onScanResult(int tipoCallback, ScanResult resultado) {
                ScanRecord registro = resultado.getScanRecord();
                if (registro != null && registro.getBytes() != null) {
                    escuchador.alRecibirAnuncio(registro.getBytes());
                }
            }

            @Override
            public void onScanFailed(int codigoError) {
                Log.d(ETIQUETA_LOG, "onScanFailed(): error = " + codigoError);
                escaner = null;
                callbackDelEscaneo = null;
            }
        };
    }
}

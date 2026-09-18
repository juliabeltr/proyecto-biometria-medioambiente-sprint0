package org.jordi.btlealumnos2021;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

/**
 * Actividad principal de la aplicación de prueba BLE.
 *
 * Permite:
 * - Inicializar Bluetooth.
 * - Buscar dispositivos Bluetooth Low Energy.
 * - Buscar un dispositivo concreto por nombre.
 * - Detener el escaneo.
 * - Interpretar y mostrar una trama iBeacon recibida.
 */
public class MainActivity extends AppCompatActivity {

    private static final String ETIQUETA_LOG = ">>>>";

    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    /**
     * Escáner Bluetooth Low Energy utilizado por la aplicación.
     */
    private BluetoothLeScanner elEscanner;

    /**
     * Callback asociado al escaneo BLE actualmente activo.
     */
    private ScanCallback callbackDelEscaneo = null;


    /**
     * Inicia la búsqueda de todos los dispositivos BLE cercanos.
     *
     * Diseño lógico:
     * buscarTodosLosDispositivosBTLE()
     */
    private void buscarTodosLosDispositivosBTLE() {

        Log.d(
                ETIQUETA_LOG,
                "buscarTodosLosDispositivosBTLE(): empieza"
        );

        if (this.elEscanner == null) {
            Log.d(
                    ETIQUETA_LOG,
                    "buscarTodosLosDispositivosBTLE(): no hay escáner BLE disponible"
            );
            return;
        }

        /*
         * Si ya había un escaneo activo, se detiene antes
         * de comenzar uno nuevo.
         */
        detenerBusquedaDispositivosBTLE();

        this.callbackDelEscaneo = new ScanCallback() {

            /**
             * Se ejecuta cuando se detecta un dispositivo BLE.
             */
            @Override
            public void onScanResult(
                    int callbackType,
                    ScanResult resultado
            ) {

                super.onScanResult(
                        callbackType,
                        resultado
                );

                Log.d(
                        ETIQUETA_LOG,
                        "buscarTodosLosDispositivosBTLE(): onScanResult()"
                );

                mostrarInformacionDispositivoBTLE(
                        resultado
                );
            }

            /**
             * Se ejecuta cuando Android entrega varios resultados
             * de escaneo de forma conjunta.
             */
            @Override
            public void onBatchScanResults(
                    List<ScanResult> results
            ) {

                super.onBatchScanResults(results);

                Log.d(
                        ETIQUETA_LOG,
                        "buscarTodosLosDispositivosBTLE(): onBatchScanResults()"
                );

                if (results == null) {
                    return;
                }

                for (ScanResult resultado : results) {
                    mostrarInformacionDispositivoBTLE(
                            resultado
                    );
                }
            }

            /**
             * Se ejecuta cuando el escaneo BLE falla.
             */
            @Override
            public void onScanFailed(
                    int errorCode
            ) {

                super.onScanFailed(errorCode);

                Log.d(
                        ETIQUETA_LOG,
                        "buscarTodosLosDispositivosBTLE(): onScanFailed(), error = "
                                + errorCode
                );
            }
        };

        Log.d(
                ETIQUETA_LOG,
                "buscarTodosLosDispositivosBTLE(): empezamos a escanear"
        );

        this.elEscanner.startScan(
                this.callbackDelEscaneo
        );
    }


    /**
     * Procesa y muestra la información de un dispositivo BLE detectado.
     *
     * Diseño lógico:
     * resultado: ScanResult
     *      --> mostrarInformacionDispositivoBTLE()
     *
     * @param resultado Resultado obtenido durante el escaneo BLE.
     */
    private void mostrarInformacionDispositivoBTLE(
            ScanResult resultado
    ) {

        if (resultado == null) {

            Log.d(
                    ETIQUETA_LOG,
                    "mostrarInformacionDispositivoBTLE(): resultado de escaneo nulo"
            );

            return;
        }

        BluetoothDevice bluetoothDevice =
                resultado.getDevice();

        if (bluetoothDevice == null) {

            Log.d(
                    ETIQUETA_LOG,
                    "mostrarInformacionDispositivoBTLE(): dispositivo Bluetooth nulo"
            );

            return;
        }

        if (resultado.getScanRecord() == null) {

            Log.d(
                    ETIQUETA_LOG,
                    "mostrarInformacionDispositivoBTLE(): el dispositivo no contiene ScanRecord"
            );

            return;
        }

        byte[] bytes =
                resultado.getScanRecord().getBytes();

        if (bytes == null) {

            Log.d(
                    ETIQUETA_LOG,
                    "mostrarInformacionDispositivoBTLE(): trama BLE nula"
            );

            return;
        }

        int rssi =
                resultado.getRssi();

        Log.d(
                ETIQUETA_LOG,
                "****************************************************"
        );

        Log.d(
                ETIQUETA_LOG,
                "****** DISPOSITIVO DETECTADO BTLE ******************"
        );

        Log.d(
                ETIQUETA_LOG,
                "****************************************************"
        );

        Log.d(
                ETIQUETA_LOG,
                "nombre = " + bluetoothDevice.getName()
        );

        Log.d(
                ETIQUETA_LOG,
                "toString = " + bluetoothDevice.toString()
        );

        Log.d(
                ETIQUETA_LOG,
                "dirección = " + bluetoothDevice.getAddress()
        );

        Log.d(
                ETIQUETA_LOG,
                "rssi = " + rssi
        );

        Log.d(
                ETIQUETA_LOG,
                "bytes = " + new String(bytes)
        );

        Log.d(
                ETIQUETA_LOG,
                "bytes (" + bytes.length + ") = "
                        + Utilidades.bytesToHexString(bytes)
        );

        /*
         * La trama recibida se interpreta como una trama iBeacon.
         */
        TramaIBeacon tib =
                new TramaIBeacon(bytes);

        Log.d(
                ETIQUETA_LOG,
                "----------------------------------------------------"
        );

        Log.d(
                ETIQUETA_LOG,
                "prefijo = "
                        + Utilidades.bytesToHexString(
                                tib.getPrefijo()
                        )
        );

        Log.d(
                ETIQUETA_LOG,
                "advFlags = "
                        + Utilidades.bytesToHexString(
                                tib.getAdvFlags()
                        )
        );

        Log.d(
                ETIQUETA_LOG,
                "advHeader = "
                        + Utilidades.bytesToHexString(
                                tib.getAdvHeader()
                        )
        );

        Log.d(
                ETIQUETA_LOG,
                "companyID = "
                        + Utilidades.bytesToHexString(
                                tib.getCompanyID()
                        )
        );

        Log.d(
                ETIQUETA_LOG,
                "iBeacon type = "
                        + Integer.toHexString(
                                tib.getiBeaconType()
                        )
        );

        Log.d(
                ETIQUETA_LOG,
                "iBeacon length 0x = "
                        + Integer.toHexString(
                                tib.getiBeaconLength()
                        )
                        + " ( "
                        + tib.getiBeaconLength()
                        + " )"
        );

        Log.d(
                ETIQUETA_LOG,
                "uuid = "
                        + Utilidades.bytesToHexString(
                                tib.getUUID()
                        )
        );

        Log.d(
                ETIQUETA_LOG,
                "uuid texto = "
                        + Utilidades.bytesToString(
                                tib.getUUID()
                        )
        );

        Log.d(
                ETIQUETA_LOG,
                "major = "
                        + Utilidades.bytesToHexString(
                                tib.getMajor()
                        )
                        + " ( "
                        + Utilidades.bytesToInt(
                                tib.getMajor()
                        )
                        + " )"
        );

        Log.d(
                ETIQUETA_LOG,
                "minor = "
                        + Utilidades.bytesToHexString(
                                tib.getMinor()
                        )
                        + " ( "
                        + Utilidades.bytesToInt(
                                tib.getMinor()
                        )
                        + " )"
        );

        Log.d(
                ETIQUETA_LOG,
                "txPower = "
                        + Integer.toHexString(
                                tib.getTxPower()
                        )
                        + " ( "
                        + tib.getTxPower()
                        + " )"
        );

        Log.d(
                ETIQUETA_LOG,
                "****************************************************"
        );
    }


    /**
     * Busca únicamente un dispositivo BLE con el nombre indicado.
     *
     * Diseño lógico:
     * dispositivo_buscado: Text
     *      --> buscarEsteDispositivoBTLE()
     *
     * @param dispositivoBuscado Nombre del dispositivo BLE buscado.
     */
    private void buscarEsteDispositivoBTLE(
            final String dispositivoBuscado
    ) {

        Log.d(
                ETIQUETA_LOG,
                "buscarEsteDispositivoBTLE(): empieza"
        );

        if (this.elEscanner == null) {

            Log.d(
                    ETIQUETA_LOG,
                    "buscarEsteDispositivoBTLE(): no hay escáner BLE disponible"
            );

            return;
        }

        if (dispositivoBuscado == null) {

            Log.d(
                    ETIQUETA_LOG,
                    "buscarEsteDispositivoBTLE(): nombre de dispositivo nulo"
            );

            return;
        }

        /*
         * Se detiene cualquier escaneo previo para evitar
         * tener varios ScanCallback activos simultáneamente.
         */
        detenerBusquedaDispositivosBTLE();

        this.callbackDelEscaneo =
                new ScanCallback() {

                    @Override
                    public void onScanResult(
                            int callbackType,
                            ScanResult resultado
                    ) {

                        super.onScanResult(
                                callbackType,
                                resultado
                        );

                        if (resultado == null ||
                                resultado.getDevice() == null) {
                            return;
                        }

                        BluetoothDevice dispositivo =
                                resultado.getDevice();

                        String nombre =
                                dispositivo.getName();

                        /*
                         * El código original creaba un ScanFilter,
                         * pero posteriormente no lo utilizaba en startScan().
                         *
                         * Aquí se realiza realmente el filtrado comprobando
                         * el nombre del dispositivo recibido.
                         */
                        if (nombre != null &&
                                nombre.equals(dispositivoBuscado)) {

                            Log.d(
                                    ETIQUETA_LOG,
                                    "buscarEsteDispositivoBTLE(): dispositivo encontrado: "
                                            + dispositivoBuscado
                            );

                            mostrarInformacionDispositivoBTLE(
                                    resultado
                            );
                        }
                    }

                    @Override
                    public void onBatchScanResults(
                            List<ScanResult> results
                    ) {

                        super.onBatchScanResults(results);

                        if (results == null) {
                            return;
                        }

                        for (ScanResult resultado : results) {

                            if (resultado == null ||
                                    resultado.getDevice() == null) {
                                continue;
                            }

                            String nombre =
                                    resultado.getDevice().getName();

                            if (nombre != null &&
                                    nombre.equals(dispositivoBuscado)) {

                                mostrarInformacionDispositivoBTLE(
                                        resultado
                                );
                            }
                        }
                    }

                    @Override
                    public void onScanFailed(
                            int errorCode
                    ) {

                        super.onScanFailed(
                                errorCode
                        );

                        Log.d(
                                ETIQUETA_LOG,
                                "buscarEsteDispositivoBTLE(): error de escaneo = "
                                        + errorCode
                        );
                    }
                };

        Log.d(
                ETIQUETA_LOG,
                "buscarEsteDispositivoBTLE(): empezamos a escanear buscando: "
                        + dispositivoBuscado
        );

        this.elEscanner.startScan(
                this.callbackDelEscaneo
        );
    }


    /**
     * Detiene el escaneo BLE actualmente activo.
     *
     * Diseño lógico:
     * detenerBusquedaDispositivosBTLE()
     */
    private void detenerBusquedaDispositivosBTLE() {

        if (this.callbackDelEscaneo == null) {
            return;
        }

        if (this.elEscanner == null) {

            this.callbackDelEscaneo = null;
            return;
        }

        this.elEscanner.stopScan(
                this.callbackDelEscaneo
        );

        this.callbackDelEscaneo = null;

        Log.d(
                ETIQUETA_LOG,
                "detenerBusquedaDispositivosBTLE(): escaneo detenido"
        );
    }


    /**
     * Acción del botón que inicia la búsqueda
     * de todos los dispositivos BLE.
     */
    public void botonBuscarDispositivosBTLEPulsado(
            View v
    ) {

        Log.d(
                ETIQUETA_LOG,
                "botón buscar dispositivos BTLE pulsado"
        );

        this.buscarTodosLosDispositivosBTLE();
    }


    /**
     * Acción del botón que inicia la búsqueda
     * del dispositivo BLE concreto.
     */
    public void botonBuscarNuestroDispositivoBTLEPulsado(
            View v
    ) {

        Log.d(
                ETIQUETA_LOG,
                "botón nuestro dispositivo BTLE pulsado"
        );

        /*
         * Nombre utilizado actualmente por el ejemplo.
         *
         * Para buscar la placa del proyecto habría que
         * sustituirlo por el nombre real anunciado por ella.
         */
        this.buscarEsteDispositivoBTLE(
                "fistro"
        );
    }


    /**
     * Acción del botón que detiene el escaneo BLE.
     */
    public void botonDetenerBusquedaDispositivosBTLEPulsado(
            View v
    ) {

        Log.d(
                ETIQUETA_LOG,
                "botón detener búsqueda dispositivos BTLE pulsado"
        );

        this.detenerBusquedaDispositivosBTLE();
    }


    /**
     * Inicializa Bluetooth y obtiene el escáner BLE.
     *
     * Diseño lógico:
     * inicializarBlueTooth()
     */
    private void inicializarBlueTooth() {

        Log.d(
                ETIQUETA_LOG,
                "inicializarBlueTooth(): obtenemos adaptador Bluetooth"
        );

        BluetoothAdapter bta =
                BluetoothAdapter.getDefaultAdapter();

        /*
         * Si getDefaultAdapter() devuelve null,
         * el dispositivo no dispone de Bluetooth.
         */
        if (bta == null) {

            Log.d(
                    ETIQUETA_LOG,
                    "inicializarBlueTooth(): el dispositivo no dispone de Bluetooth"
            );

            return;
        }

        /*
         * Se intenta habilitar Bluetooth si todavía
         * se encuentra desactivado.
         */
        if (!bta.isEnabled()) {

            Log.d(
                    ETIQUETA_LOG,
                    "inicializarBlueTooth(): Bluetooth está desactivado"
            );

            bta.enable();
        }

        Log.d(
                ETIQUETA_LOG,
                "inicializarBlueTooth(): habilitado = "
                        + bta.isEnabled()
        );

        Log.d(
                ETIQUETA_LOG,
                "inicializarBlueTooth(): estado = "
                        + bta.getState()
        );

        /*
         * Se obtiene una única vez el escáner BLE.
         */
        this.elEscanner =
                bta.getBluetoothLeScanner();

        if (this.elEscanner == null) {

            Log.d(
                    ETIQUETA_LOG,
                    "inicializarBlueTooth(): no se ha podido obtener el escáner BLE"
            );

            return;
        }

        Log.d(
                ETIQUETA_LOG,
                "inicializarBlueTooth(): escáner BLE obtenido correctamente"
        );

        /*
         * Se comprueba si la aplicación dispone
         * de los permisos necesarios.
         */
        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED

                        ||

                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_ADMIN
                ) != PackageManager.PERMISSION_GRANTED

                        ||

                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {

            Log.d(
                    ETIQUETA_LOG,
                    "inicializarBlueTooth(): solicitando permisos"
            );

            ActivityCompat.requestPermissions(
                    MainActivity.this,

                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },

                    CODIGO_PETICION_PERMISOS
            );

        } else {

            Log.d(
                    ETIQUETA_LOG,
                    "inicializarBlueTooth(): permisos disponibles"
            );
        }
    }


    /**
     * Método ejecutado al crear la actividad.
     */
    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_main
        );

        Log.d(
                ETIQUETA_LOG,
                "onCreate(): empieza"
        );

        inicializarBlueTooth();

        Log.d(
                ETIQUETA_LOG,
                "onCreate(): termina"
        );
    }


    /**
     * Gestiona la respuesta del usuario a la solicitud
     * de permisos de la aplicación.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (
                requestCode
                        != CODIGO_PETICION_PERMISOS
        ) {
            return;
        }

        /*
         * Si el usuario cancela la petición,
         * grantResults puede estar vacío.
         */
        if (grantResults.length == 0) {

            Log.d(
                    ETIQUETA_LOG,
                    "onRequestPermissionsResult(): no se ha concedido ningún permiso"
            );

            return;
        }

        boolean todosConcedidos = true;

        /*
         * El código original únicamente comprobaba
         * grantResults[0].
         *
         * Como se solicitan varios permisos, deben
         * comprobarse todos.
         */
        for (int resultado : grantResults) {

            if (
                    resultado
                            != PackageManager.PERMISSION_GRANTED
            ) {

                todosConcedidos = false;
                break;
            }
        }

        if (todosConcedidos) {

            Log.d(
                    ETIQUETA_LOG,
                    "onRequestPermissionsResult(): permisos concedidos"
            );

        } else {

            Log.d(
                    ETIQUETA_LOG,
                    "onRequestPermissionsResult(): uno o más permisos NO concedidos"
            );
        }
    }


    /**
     * Detiene el escaneo cuando la actividad se destruye,
     * evitando dejar activo el callback de Bluetooth.
     */
    @Override
    protected void onDestroy() {

        detenerBusquedaDispositivosBTLE();

        super.onDestroy();
    }
}

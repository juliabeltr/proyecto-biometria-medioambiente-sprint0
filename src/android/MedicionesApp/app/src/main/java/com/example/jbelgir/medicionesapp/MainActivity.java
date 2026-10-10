package com.example.jbelgir.medicionesapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

// =============================================================================
// MainActivity.java
//
// Descripción: pantalla única de la app. Pide permisos, arranca y detiene el
//              escaneo y muestra el estado y la última medición enviada.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/android_design.md (MainActivity). Solo
//              coordina: la conversión y el envío están en otras clases.
// =============================================================================

public class MainActivity extends AppCompatActivity {

    // Ubicación fija del Sprint 0 (el GPS real queda para un sprint posterior).
    private static final double LATITUD_PRUEBAS = 38.9675;
    private static final double LONGITUD_PRUEBAS = -0.1806;

    private static final int CODIGO_PETICION_PERMISOS = 1001;

    private EditText campoServidor;
    private TextView textoEstado;
    private TextView textoUltimaMedicion;

    private ProcesadorBeacons procesador = null;
    private EscanerBeacons escaner = null;

    /*
     * --------------------------------------------------------------
     * Propósito: método ejecutado al crear la actividad: enlaza la pantalla y los botones.
     *
     * Diseño lógico:
     *     onCreate() -->
     *
     * Es mecánica de Android (ciclo de vida de la actividad): queda fuera del diseño lógico.
     * --------------------------------------------------------------
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        campoServidor = findViewById(R.id.campoServidor);
        textoEstado = findViewById(R.id.textoEstado);
        textoUltimaMedicion = findViewById(R.id.textoUltimaMedicion);
        Button botonIniciar = findViewById(R.id.botonIniciar);
        Button botonDetener = findViewById(R.id.botonDetener);

        botonIniciar.setOnClickListener(vista -> iniciarEscucha());
        botonDetener.setOnClickListener(vista -> detenerEscucha());

        mostrarEstado("Detenido");
    }

    /*
     * --------------------------------------------------------------
     * Propósito: arranca el escaneo: comprueba permisos y dirección del
     *            servidor, monta el procesador con la lógica real y empieza
     *            a escanear.
     *
     * Diseño lógico:
     *     iniciarEscucha() -->
     *
     * Parámetros: ninguno.
     * Retorno: ninguno.
     * Errores: ninguno hacia fuera: el problema se muestra en pantalla.
     * --------------------------------------------------------------
     */
    private void iniciarEscucha() {
        if (escaner != null && escaner.estaEscaneando()) {
            return;
        }
        if (!tienePermisos()) {
            mostrarEstado("Faltan permisos de Bluetooth");
            pedirPermisos();
            return;
        }
        String urlServidor = campoServidor.getText().toString().trim();
        if (urlServidor.isEmpty()) {
            mostrarEstado("Escribe la dirección del servidor");
            return;
        }

        LogicaTelefono logica = logicaConAviso(new LogicaTelefonoREST(urlServidor));
        procesador = new ProcesadorBeacons(logica, LATITUD_PRUEBAS, LONGITUD_PRUEBAS);
        escaner = new EscanerBeacons(bytes -> procesador.procesar(bytes, ahoraUTC()));
        escaner.iniciarEscaneo();

        if (escaner.estaEscaneando()) {
            mostrarEstado("Escuchando...");
        } else {
            mostrarEstado("No se pudo iniciar el escaneo (¿Bluetooth activado?)");
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: detiene el escaneo.
     *
     * Diseño lógico:
     *     detenerEscucha() -->
     *
     * Parámetros: ninguno.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    private void detenerEscucha() {
        if (escaner != null) {
            escaner.detenerEscaneo();
        }
        mostrarEstado("Detenido");
    }

    /*
     * --------------------------------------------------------------
     * Propósito: indica si la app tiene los permisos necesarios.
     *
     * Diseño lógico:
     *     resultado: B <-- tienePermisos() <--
     *
     * Parámetros: ninguno.
     * Retorno: B. true si están concedidos todos los permisos necesarios.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    private boolean tienePermisos() {
        for (String permiso : permisosNecesarios()) {
            if (ContextCompat.checkSelfPermission(this, permiso)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: pide al usuario los permisos necesarios.
     *
     * Diseño lógico:
     *     pedirPermisos() -->
     *
     * Parámetros: ninguno.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    private void pedirPermisos() {
        ActivityCompat.requestPermissions(this, permisosNecesarios(), CODIGO_PETICION_PERMISOS);
    }

    /*
     * --------------------------------------------------------------
     * Propósito: respuesta del usuario a la petición de permisos: si los concede
     *            todos, arranca el escaneo; si no, lo indica en pantalla.
     *
     * Diseño lógico:
     *     onRequestPermissionsResult() -->
     *
     * Es mecánica de Android (ciclo de vida de la actividad): queda fuera del diseño lógico.
     * --------------------------------------------------------------
     */
    @Override
    public void onRequestPermissionsResult(int codigo, String[] permisos, int[] resultados) {
        super.onRequestPermissionsResult(codigo, permisos, resultados);
        if (codigo != CODIGO_PETICION_PERMISOS) {
            return;
        }
        if (tienePermisos()) {
            iniciarEscucha();
        } else {
            mostrarEstado("Faltan permisos de Bluetooth");
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: al destruir la actividad se detiene el escaneo para no dejarlo activo.
     *
     * Diseño lógico:
     *     onDestroy() -->
     *
     * Es mecánica de Android (ciclo de vida de la actividad): queda fuera del diseño lógico.
     * --------------------------------------------------------------
     */
    @Override
    protected void onDestroy() {
        if (escaner != null) {
            escaner.detenerEscaneo();
        }
        super.onDestroy();
    }

    /*
     * --------------------------------------------------------------
     * Propósito: lista los permisos que exige la versión de Android:
     *            desde Android 12, Bluetooth (escaneo y conexión) y
     *            ubicación; antes, solo ubicación.
     *
     * Diseño lógico:
     *     permisosNecesarios() --> [Text]
     *
     * Parámetros: ninguno.
     * Retorno: [Text]. Nombres de los permisos.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    private String[] permisosNecesarios() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }
        return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    }

    /*
     * --------------------------------------------------------------
     * Propósito: envuelve la lógica del teléfono para mostrar en pantalla
     *            si cada medición se ha enviado, sin cambiar su
     *            comportamiento.
     *
     * Diseño lógico:
     *     logica: LogicaTelefono --> logicaConAviso() --> LogicaTelefono
     *
     * Parámetros:
     *     real: LogicaTelefono. Lógica a la que se delega el envío.
     * Retorno: LogicaTelefono. Misma lógica, con aviso en pantalla.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    private LogicaTelefono logicaConAviso(final LogicaTelefono real) {
        return new LogicaTelefono() {
            @Override
            public void guardarMedicion(final Medicion m, final ResultadoEnvio resultadoEnvio) {
                real.guardarMedicion(m, resultado -> {
                    String texto = m.getTipo() + " = " + formatearValor(m.getValor())
                            + (resultado ? " enviada" : " NO enviada (error de red o del servidor)");
                    textoUltimaMedicion.setText(texto);
                    resultadoEnvio.callback(resultado);
                });
            }
        };
    }

    /*
     * --------------------------------------------------------------
     * Propósito: da formato a un valor: sin decimales si es entero.
     *
     * Diseño lógico:
     *     valor: R --> formatearValor() --x --> texto: Text
     * --------------------------------------------------------------
     */
    private static String formatearValor(double valor) {
        if (valor == Math.rint(valor)) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor);
    }

    /*
     * --------------------------------------------------------------
     * Propósito: devuelve la fecha y hora actuales en UTC, en formato
     *            ISO 8601 a segundos, por ejemplo 2026-10-03T10:00:00Z.
     *
     * Diseño lógico:
     *     ahoraUTC() --x --> fechaHora: Text
     * --------------------------------------------------------------
     */
    private static String ahoraUTC() {
        return Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();
    }

    /*
     * --------------------------------------------------------------
     * Propósito: muestra el estado de la escucha en pantalla.
     *
     * Diseño lógico:
     *     estado: Text --> mostrarEstado() -->
     * --------------------------------------------------------------
     */
    private void mostrarEstado(String estado) {
        textoEstado.setText(estado);
    }
}
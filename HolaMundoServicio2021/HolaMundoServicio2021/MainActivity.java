package org.jordi.holamundoservicio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------

/**
 * Actividad principal encargada de iniciar y detener
 * el servicio ServicioEscuharBeacons.
 */
public class MainActivity extends AppCompatActivity {

    private static final String ETIQUETA_LOG = ">>>>";

    /**
     * Intent utilizado para arrancar y detener el servicio.
     *
     * Si es null, la actividad considera que el servicio
     * no ha sido iniciado desde esta instancia.
     */
    private Intent elIntentDelServicio = null;


    /**
     * Arranca el servicio si todavía no ha sido iniciado.
     *
     * Diseño lógico:
     * botonArrancarServicioPulsado()
     *
     * @param v Vista que ha provocado el evento.
     */
    public void botonArrancarServicioPulsado(View v) {

        Log.d(
                ETIQUETA_LOG,
                "botonArrancarServicioPulsado(): pulsado"
        );

        /*
         * Si ya existe un Intent asociado al servicio,
         * se considera que ya está arrancado.
         */
        if (this.elIntentDelServicio != null) {

            Log.d(
                    ETIQUETA_LOG,
                    "botonArrancarServicioPulsado(): servicio ya iniciado"
            );

            return;
        }

        /*
         * Se crea un Intent dirigido al servicio.
         */
        this.elIntentDelServicio =
                new Intent(
                        this,
                        ServicioEscuharBeacons.class
                );

        /*
         * Tiempo de espera que utilizará el servicio
         * entre iteraciones.
         */
        this.elIntentDelServicio.putExtra(
                "tiempoDeEspera",
                5000L
        );

        startService(
                this.elIntentDelServicio
        );

        Log.d(
                ETIQUETA_LOG,
                "botonArrancarServicioPulsado(): servicio iniciado"
        );
    }


    /**
     * Detiene el servicio si había sido iniciado.
     *
     * Diseño lógico:
     * botonDetenerServicioPulsado()
     *
     * @param v Vista que ha provocado el evento.
     */
    public void botonDetenerServicioPulsado(View v) {

        Log.d(
                ETIQUETA_LOG,
                "botonDetenerServicioPulsado(): pulsado"
        );

        /*
         * Si no existe un Intent asociado,
         * se considera que el servicio no estaba arrancado.
         */
        if (this.elIntentDelServicio == null) {

            Log.d(
                    ETIQUETA_LOG,
                    "botonDetenerServicioPulsado(): servicio no iniciado"
            );

            return;
        }

        stopService(
                this.elIntentDelServicio
        );

        /*
         * Se elimina la referencia para permitir
         * volver a arrancar posteriormente el servicio.
         */
        this.elIntentDelServicio = null;

        Log.d(
                ETIQUETA_LOG,
                "botonDetenerServicioPulsado(): servicio detenido"
        );
    }


    /**
     * Método llamado al crear la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_main
        );

        Log.d(
                ETIQUETA_LOG,
                "MainActivity.onCreate(): empieza"
        );

        Log.d(
                ETIQUETA_LOG,
                "MainActivity.onCreate(): acaba"
        );
    }


    /**
     * Se ejecuta cuando la actividad es destruida.
     *
     * Si el servicio había sido arrancado desde esta actividad,
     * se solicita su parada para evitar dejarlo ejecutándose
     * accidentalmente.
     */
    @Override
    protected void onDestroy() {

        if (this.elIntentDelServicio != null) {

            stopService(
                    this.elIntentDelServicio
            );

            this.elIntentDelServicio = null;
        }

        super.onDestroy();
    }
}

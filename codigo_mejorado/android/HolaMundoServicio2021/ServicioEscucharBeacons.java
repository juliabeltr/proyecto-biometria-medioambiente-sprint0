package org.jordi.holamundoservicio;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

// -------------------------------------------------------------------------------------------------
// Júlia Beltrán Girbés
// -------------------------------------------------------------------------------------------------

/**
 * Servicio que ejecuta una tarea periódica en un hilo de trabajo.
 *
 * El servicio recibe mediante un Intent el tiempo que debe esperar
 * entre cada ejecución y permanece activo mientras la variable
 * "seguir" sea verdadera.
 */
public class ServicioEscuharBeacons extends IntentService {

    private static final String ETIQUETA_LOG = ">>>>";

    /**
     * Tiempo de espera entre iteraciones, en milisegundos.
     */
    private long tiempoDeEspera = 10000;

    /**
     * Indica si el bucle de trabajo debe continuar ejecutándose.
     *
     * Se declara volatile porque puede ser consultada y modificada
     * desde hilos diferentes.
     */
    private volatile boolean seguir = true;


    /**
     * Constructor del servicio.
     *
     * Diseño lógico:
     * ServicioEscuharBeacons()
     */
    public ServicioEscuharBeacons() {

        super("HelloIntentService");

        Log.d(
                ETIQUETA_LOG,
                "ServicioEscucharBeacons.constructor: termina"
        );
    }


    /*
     * Esta versión de onStartCommand() estaba incluida originalmente
     * como código experimental.
     *
     * No se utiliza porque IntentService gestiona automáticamente
     * la recepción del Intent y ejecuta onHandleIntent() en su
     * hilo de trabajo.
     *
     * @Override
     * public int onStartCommand(
     *         Intent elIntent,
     *         int losFlags,
     *         int startId
     * ) {
     *
     *     super.onStartCommand(
     *             elIntent,
     *             losFlags,
     *             startId
     *     );
     *
     *     this.tiempoDeEspera =
     *             elIntent.getLongExtra(
     *                     "tiempoDeEspera",
     *                     50000
     *             );
     *
     *     return Service.START_STICKY;
     * }
     */


    /**
     * Solicita la parada del servicio.
     *
     * Diseño lógico:
     * parar()
     *
     * Cambia el estado de ejecución y solicita a Android
     * que detenga el servicio.
     */
    public void parar() {

        Log.d(
                ETIQUETA_LOG,
                "ServicioEscucharBeacons.parar(): empieza"
        );

        if (!this.seguir) {
            return;
        }

        this.seguir = false;

        this.stopSelf();

        Log.d(
                ETIQUETA_LOG,
                "ServicioEscucharBeacons.parar(): acaba"
        );
    }


    /**
     * Se ejecuta cuando Android destruye el servicio.
     *
     * Se marca el bucle como finalizado para que el hilo
     * de trabajo deje de ejecutarse.
     */
    @Override
    public void onDestroy() {

        Log.d(
                ETIQUETA_LOG,
                "ServicioEscucharBeacons.onDestroy()"
        );

        /*
         * No se vuelve a llamar a stopSelf() desde aquí,
         * ya que el servicio ya está siendo destruido.
         */
        this.seguir = false;

        super.onDestroy();
    }


    /**
     * Ejecuta el trabajo principal del servicio.
     *
     * IntentService llama a este método desde su hilo de trabajo.
     *
     * Diseño lógico:
     * intent: Intent --> onHandleIntent()
     *
     * @param intent Intent que inició el servicio y que puede contener
     *               el tiempo de espera entre iteraciones.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        /*
         * Se comprueba que el Intent sea válido antes
         * de intentar acceder a sus parámetros.
         */
        if (intent == null) {

            Log.d(
                    ETIQUETA_LOG,
                    "ServicioEscucharBeacons.onHandleIntent(): Intent nulo"
            );

            return;
        }

        this.tiempoDeEspera =
                intent.getLongExtra(
                        "tiempoDeEspera",
                        50000
                );

        /*
         * Thread.sleep() no acepta tiempos negativos.
         * Si llega un valor inválido se utiliza el valor
         * por defecto.
         */
        if (this.tiempoDeEspera < 0) {
            this.tiempoDeEspera = 50000;
        }

        this.seguir = true;

        long contador = 1;

        Log.d(
                ETIQUETA_LOG,
                "ServicioEscucharBeacons.onHandleIntent(): empieza, thread="
                        + Thread.currentThread().getId()
        );

        try {

            while (this.seguir) {

                Thread.sleep(
                        this.tiempoDeEspera
                );

                /*
                 * El servicio podría haber recibido una orden
                 * de parada mientras estaba esperando.
                 */
                if (!this.seguir) {
                    break;
                }

                Log.d(
                        ETIQUETA_LOG,
                        "ServicioEscucharBeacons.onHandleIntent(): tras la espera: "
                                + contador
                );

                contador++;
            }

            Log.d(
                    ETIQUETA_LOG,
                    "ServicioEscucharBeacons.onHandleIntent(): tarea terminada"
            );

        } catch (InterruptedException e) {

            Log.d(
                    ETIQUETA_LOG,
                    "ServicioEscucharBeacons.onHandleIntent(): hilo interrumpido"
            );

            /*
             * Se restaura el estado de interrupción del hilo.
             */
            Thread.currentThread().interrupt();
        }

        Log.d(
                ETIQUETA_LOG,
                "ServicioEscucharBeacons.onHandleIntent(): termina"
        );
    }
}

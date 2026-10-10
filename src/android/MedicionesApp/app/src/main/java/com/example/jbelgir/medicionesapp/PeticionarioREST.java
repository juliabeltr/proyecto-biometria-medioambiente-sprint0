package com.example.jbelgir.medicionesapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// ------------------------------------------------------------------------
// ------------------------------------------------------------------------

/*
 * --------------------------------------------------------------
 * Clase encargada de realizar peticiones REST en segundo plano.
 *
 * Utiliza HttpURLConnection para enviar una petición HTTP y devuelve
 * el código de respuesta y el cuerpo mediante un callback.
 *
 * Obsoleto: AsyncTask está obsoleto en versiones modernas de Android.
 * Se mantiene aquí porque forma parte del ejemplo original de la práctica.
 * --------------------------------------------------------------
 */
public class PeticionarioREST extends AsyncTask<Void, Void, Boolean> {

    private static final String ETIQUETA_LOG = "clienterestandroid";

    /*
     * --------------------------------------------------------------
     * Interfaz utilizada para devolver el resultado de la petición REST.
     * --------------------------------------------------------------
     */
    public interface RespuestaREST {

        /*
         * --------------------------------------------------------------
         * Se ejecuta cuando termina la petición.
         *
         * Diseño lógico:
         * codigo: Z, cuerpo: Text --> callback()
         *
         * Parámetro: codigo Código HTTP recibido.
         * Parámetro: cuerpo Cuerpo de la respuesta.
         * --------------------------------------------------------------
         */
        void callback(int codigo, String cuerpo);
    }

    private String elMetodo;
    private String urlDestino;
    private String elCuerpo = null;
    private RespuestaREST laRespuesta;

    private int codigoRespuesta = 0;
    private String cuerpoRespuesta = "";

    /*
     * --------------------------------------------------------------
     * Configura y ejecuta una petición REST.
     *
     * Diseño lógico:
     * metodo: Text,
     * url_destino: Text,
     * cuerpo: Text,
     * respuesta: RespuestaREST
     *      --> hacerPeticionREST()
     *
     * Parámetro: metodo Método HTTP: GET, POST, PUT, DELETE, etc.
     * Parámetro: urlDestino URL de destino.
     * Parámetro: cuerpo Cuerpo de la petición. Puede ser null.
     * Parámetro: laRespuesta Callback que recibirá el resultado.
     * --------------------------------------------------------------
     */
    public void hacerPeticionREST(
            String metodo,
            String urlDestino,
            String cuerpo,
            RespuestaREST laRespuesta
    ) {

        if (metodo == null || metodo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "hacerPeticionREST(): el método HTTP no puede estar vacío"
            );
        }

        if (urlDestino == null || urlDestino.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "hacerPeticionREST(): la URL no puede estar vacía"
            );
        }

        if (laRespuesta == null) {
            throw new IllegalArgumentException(
                    "hacerPeticionREST(): el callback no puede ser null"
            );
        }

        this.elMetodo = metodo.trim().toUpperCase();
        this.urlDestino = urlDestino;
        this.elCuerpo = cuerpo;
        this.laRespuesta = laRespuesta;

        this.execute();
    }

    /*
     * --------------------------------------------------------------
     * Propósito: constructor sin parámetros del peticionario.
     *
     * Diseño lógico:
     *     PeticionarioREST() -->
     * --------------------------------------------------------------
     */
    public PeticionarioREST() {

        Log.d(
                ETIQUETA_LOG,
                "constructor()"
        );
    }

    /*
     * --------------------------------------------------------------
     * Ejecuta la petición HTTP en segundo plano.
     *
     * Diseño lógico:
     * doInBackground() -->
     *     B <--
     * Retorno: true si la petición ha podido ejecutarse;
     * false si se ha producido una excepción.
     * --------------------------------------------------------------
     */
    @Override
    protected Boolean doInBackground(Void... params) {

        Log.d(
                ETIQUETA_LOG,
                "doInBackground()"
        );

        HttpURLConnection connection = null;

        try {

            Log.d(
                    ETIQUETA_LOG,
                    "doInBackground(): conexión a >"
                            + urlDestino
                            + "<"
            );

            URL url =
                    new URL(urlDestino);

            connection =
                    (HttpURLConnection) url.openConnection();

            /*
             * Se especifica JSON con codificación UTF-8.
             */
            connection.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8"
            );

            connection.setRequestProperty(
                    "Accept",
                    "application/json"
            );

            connection.setRequestMethod(
                    this.elMetodo
            );

            /*
             * Evita que una conexión pueda bloquearse
             * indefinidamente.
             */
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            connection.setUseCaches(false);
            connection.setDoInput(true);

            /*
             * Si no es una petición GET y existe cuerpo,
             * se envía utilizando UTF-8.
             */
            if (
                    !"GET".equals(this.elMetodo)
                            &&
                            this.elCuerpo != null
            ) {

                Log.d(
                        ETIQUETA_LOG,
                        "doInBackground(): enviando cuerpo"
                );

                connection.setDoOutput(true);

                try (
                        OutputStreamWriter writer =
                                new OutputStreamWriter(
                                        connection.getOutputStream(),
                                        StandardCharsets.UTF_8
                                )
                ) {

                    writer.write(
                            this.elCuerpo
                    );

                    writer.flush();
                }
            }

            Log.d(
                    ETIQUETA_LOG,
                    "doInBackground(): petición enviada"
            );

            /*
             * Obtención del código y mensaje HTTP.
             */
            this.codigoRespuesta =
                    connection.getResponseCode();

            String mensajeRespuesta =
                    connection.getResponseMessage();

            Log.d(
                    ETIQUETA_LOG,
                    "doInBackground(): respuesta = "
                            + this.codigoRespuesta
                            + " : "
                            + mensajeRespuesta
            );

            /*
             * Para respuestas correctas se utiliza getInputStream().
             * Para errores HTTP se utiliza getErrorStream(), ya que
             * getInputStream() puede lanzar IOException.
             */
            InputStream inputStream;

            if (
                    this.codigoRespuesta >= 200
                            &&
                            this.codigoRespuesta < 400
            ) {

                inputStream =
                        connection.getInputStream();

            } else {

                inputStream =
                        connection.getErrorStream();
            }

            /*
             * Algunas respuestas HTTP no contienen cuerpo.
             */
            if (inputStream != null) {

                try (
                        BufferedReader br =
                                new BufferedReader(
                                        new InputStreamReader(
                                                inputStream,
                                                StandardCharsets.UTF_8
                                        )
                                )
                ) {

                    StringBuilder acumulador =
                            new StringBuilder();

                    String linea;

                    while (
                            (linea = br.readLine()) != null
                    ) {

                        acumulador.append(
                                linea
                        );
                    }

                    this.cuerpoRespuesta =
                            acumulador.toString();
                }

            } else {

                this.cuerpoRespuesta = "";
            }

            Log.d(
                    ETIQUETA_LOG,
                    "doInBackground(): cuerpo recibido = "
                            + this.cuerpoRespuesta
            );

            return true;

        } catch (Exception ex) {

            Log.d(
                    ETIQUETA_LOG,
                    "doInBackground(): excepción: "
                            + ex.getMessage()
            );

            return false;

        } finally {

            /*
             * Se garantiza que la conexión se cierre
             * incluso si se produce una excepción.
             */
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /*
     * --------------------------------------------------------------
     * Se ejecuta en el hilo principal una vez terminada
     * la petición en segundo plano.
     *
     * Diseño lógico:
     * como_fue: B --> onPostExecute()
     *
     * Parámetro: comoFue true si la petición terminó sin excepciones.
     * --------------------------------------------------------------
     */
    @Override
    protected void onPostExecute(Boolean comoFue) {

        Log.d(
                ETIQUETA_LOG,
                "onPostExecute(): comoFue = "
                        + comoFue
        );

        /*
         * El callback se valida en hacerPeticionREST(),
         * por lo que debería existir siempre.
         */
        if (this.laRespuesta != null) {

            this.laRespuesta.callback(
                    this.codigoRespuesta,
                    this.cuerpoRespuesta
            );
        }
    }
}
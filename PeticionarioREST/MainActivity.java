package org.jordi.clienterestandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------

/**
 * Actividad principal del cliente REST.
 *
 * Permite lanzar una petición HTTP mediante PeticionarioREST
 * y mostrar en pantalla el código y el cuerpo de la respuesta.
 */
public class MainActivity extends AppCompatActivity {

    private static final String ETIQUETA_LOG = "clienterestandroid";

    private TextView elTexto;

    /**
     * Método ejecutado al crear la actividad.
     *
     * Diseño lógico:
     * onCreate()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_main
        );

        this.elTexto =
                findViewById(
                        R.id.elTexto
                );

        Log.d(
                ETIQUETA_LOG,
                "fin onCreate()"
        );
    }


    /**
     * Acción ejecutada al pulsar el botón de envío.
     *
     * Diseño lógico:
     * boton_enviar_pulsado()
     *
     * @param quien Vista que ha provocado el evento.
     */
    public void boton_enviar_pulsado(View quien) {

        Log.d(
                ETIQUETA_LOG,
                "boton_enviar_pulsado()"
        );

        this.elTexto.setText(
                "Enviando petición..."
        );

        /*
         * Se crea un nuevo PeticionarioREST para cada petición.
         *
         * AsyncTask solo puede ejecutarse una vez,
         * por lo que no debe reutilizarse una instancia anterior.
         */
        PeticionarioREST elPeticionario =
                new PeticionarioREST();

        elPeticionario.hacerPeticionREST(
                "GET",
                "http://158.42.144.126:8080/prueba",
                null,

                new PeticionarioREST.RespuestaREST() {

                    @Override
                    public void callback(
                            int codigo,
                            String cuerpo
                    ) {

                        elTexto.setText(
                                "Código respuesta = "
                                        + codigo
                                        + "\n\n"
                                        + cuerpo
                        );
                    }
                }
        );
    }


    /**
     * Crea el menú de opciones de la actividad.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(
                R.menu.menu_main,
                menu
        );

        return true;
    }
}

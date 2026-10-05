package com.example.jbelgir.medicionesapp;

// =============================================================================
// LogicaTelefonoREST.java
//
// Descripción: lógica real del teléfono: envía las mediciones al servidor REST.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/android_design.md (LogicaTelefonoREST).
//              Usa PeticionarioREST para hacer POST {urlBase}/mediciones.
// =============================================================================

public class LogicaTelefonoREST implements LogicaTelefono {

    private static final int CODIGO_CREADA = 201;

    private final String urlBase;

    /*
     * --------------------------------------------------------------
     * Propósito: crea la lógica real apuntando a un servidor.
     *
     * Diseño lógico:
     *     urlBase: Text --> LogicaTelefonoREST() -->
     *
     * Parámetros:
     *     urlBase: Text. Dirección del servidor, p. ej.
     *              "http://192.168.1.35:8080". Se ignora la barra final.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public LogicaTelefonoREST(String urlBase) {
        this.urlBase = urlBase.replaceAll("/+$", "");
    }

    /*
     * --------------------------------------------------------------
     * Propósito: envía la medición con POST {urlBase}/mediciones. El
     *            resultado es true si el servidor responde 201.
     *
     * Diseño lógico:
     *     m: Medicion --> enviarMedicion() -->
     *       resultado: B <--
     *
     * Parámetros:
     *     m: Medicion. Medición a enviar.
     *     resultadoEnvio: ResultadoEnvio. Recibe true si el código es 201;
     *                     false con otro código o ante un fallo de red.
     * Retorno: ninguno.
     * Errores: ninguno hacia fuera: un fallo se notifica con false.
     * --------------------------------------------------------------
     */
    @Override
    public void enviarMedicion(Medicion m, final ResultadoEnvio resultadoEnvio) {
        PeticionarioREST peticionario = new PeticionarioREST();
        peticionario.hacerPeticionREST(
                "POST",
                urlBase + "/mediciones",
                m.aJSON(),
                new PeticionarioREST.RespuestaREST() {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        resultadoEnvio.callback(codigo == CODIGO_CREADA);
                    }
                }
        );
    }
}
package com.example.jbelgir.medicionesapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

// =============================================================================
// LogicaTelefonoFakeTest.java
//
// Descripción: tests automáticos de LogicaTelefonoFake.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Casos pedidos en doc/android_design.md.
// =============================================================================

public class LogicaTelefonoFakeTest {

    private static Medicion medicion(double valor) {
        return new Medicion(0, "CO2", valor, 38.96, -0.18, "2026-10-03T10:00:00Z");
    }

    /*
     * Recoge el resultado que entrega la lógica por su callback.
     */
    private static class Resultado implements LogicaTelefono.ResultadoEnvio {
        Boolean recibido = null;

        @Override
        public void callback(boolean resultado) {
            recibido = resultado;
        }
    }

    @Test
    public void guardaLasMedicionesEnOrden() {
        LogicaTelefonoFake logica = new LogicaTelefonoFake(false);

        logica.enviarMedicion(medicion(1), new Resultado());
        logica.enviarMedicion(medicion(2), new Resultado());

        assertEquals(2, logica.getEnviadas().size());
        assertEquals(1.0, logica.getEnviadas().get(0).getValor(), 0.0);
        assertEquals(2.0, logica.getEnviadas().get(1).getValor(), 0.0);
    }

    @Test
    public void avisaConTrueSiSeGuarda() {
        Resultado resultado = new Resultado();

        new LogicaTelefonoFake(false).enviarMedicion(medicion(1), resultado);

        assertTrue(resultado.recibido);
    }

    @Test
    public void conFallaNoGuardaYAvisaConFalse() {
        LogicaTelefonoFake logica = new LogicaTelefonoFake(true);
        Resultado resultado = new Resultado();

        logica.enviarMedicion(medicion(1), resultado);

        assertFalse(resultado.recibido);
        assertEquals(0, logica.getEnviadas().size());
    }

    @Test
    public void sinMedicionesDevuelveListaVacia() {
        assertTrue(new LogicaTelefonoFake(false).getEnviadas().isEmpty());
    }

    @Test
    public void modificarLaListaDevueltaNoAlteraLaLogica() {
        LogicaTelefonoFake logica = new LogicaTelefonoFake(false);
        logica.enviarMedicion(medicion(1), new Resultado());

        logica.getEnviadas().clear();

        assertEquals(1, logica.getEnviadas().size());
    }
}
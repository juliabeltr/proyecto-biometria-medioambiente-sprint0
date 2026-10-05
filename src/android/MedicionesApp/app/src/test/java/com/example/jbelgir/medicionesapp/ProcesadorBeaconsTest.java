package com.example.jbelgir.medicionesapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

// =============================================================================
// ProcesadorBeaconsTest.java
//
// Descripción: tests automáticos de ProcesadorBeacons con LogicaTelefonoFake.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Casos pedidos en doc/android_design.md.
// =============================================================================

public class ProcesadorBeaconsTest {

    private static final String FECHA = "2026-10-03T10:00:00Z";

    private LogicaTelefonoFake logica;
    private ProcesadorBeacons procesador;

    @Before
    public void preparar() {
        logica = new LogicaTelefonoFake(false);
        procesador = new ProcesadorBeacons(logica, 38.9675, -0.1806);
    }

    private static byte[] trama(int idTipo, int contador, int minor) {
        return TramasDePrueba.crear(
                TramasDePrueba.UUID_NUESTRO, TramasDePrueba.major(idTipo, contador), minor);
    }

    @Test
    public void unaTramaValidaEnviaLaMedicion() {
        assertTrue(procesador.procesar(trama(11, 1, 235), FECHA));

        assertEquals(1, logica.getEnviadas().size());
        Medicion m = logica.getEnviadas().get(0);
        assertEquals("CO2", m.getTipo());
        assertEquals(235.0, m.getValor(), 0.0);
        assertEquals(FECHA, m.getFechaHora());
        assertEquals(38.9675, m.getLatitud(), 0.0);
        assertEquals(-0.1806, m.getLongitud(), 0.0);
    }

    @Test
    public void unaTramaRepetidaSeEnviaUnaSolaVez() {
        assertTrue(procesador.procesar(trama(11, 1, 235), FECHA));
        assertFalse(procesador.procesar(trama(11, 1, 235), FECHA));
        assertFalse(procesador.procesar(trama(11, 1, 235), FECHA));

        assertEquals(1, logica.getEnviadas().size());
    }

    @Test
    public void unaMedicionNuevaSeEnvia() {
        procesador.procesar(trama(11, 1, 235), FECHA);
        procesador.procesar(trama(11, 2, 240), FECHA);

        assertEquals(2, logica.getEnviadas().size());
        assertEquals(240.0, logica.getEnviadas().get(1).getValor(), 0.0);
    }

    @Test
    public void envaCO2YTemperaturaDelMismoCiclo() {
        procesador.procesar(trama(11, 7, 235), FECHA);
        procesador.procesar(trama(12, 7, -12), FECHA);

        assertEquals(2, logica.getEnviadas().size());
        assertEquals("TEMP", logica.getEnviadas().get(1).getTipo());
        assertEquals(-12.0, logica.getEnviadas().get(1).getValor(), 0.0);
    }

    @Test
    public void ignoraUnaTramaNula() {
        assertFalse(procesador.procesar(null, FECHA));
        assertEquals(0, logica.getEnviadas().size());
    }

    @Test
    public void ignoraUnaTramaDeMenosDe30Bytes() {
        byte[] corta = new byte[29];

        assertFalse(procesador.procesar(corta, FECHA));
        assertFalse(procesador.procesar(new byte[0], FECHA));
        assertEquals(0, logica.getEnviadas().size());
    }

    @Test
    public void ignoraUnUuidAjeno() {
        byte[] ajena = TramasDePrueba.crear("MolaMolaMolaMola", TramasDePrueba.major(11, 1), 235);

        assertFalse(procesador.procesar(ajena, FECHA));
        assertEquals(0, logica.getEnviadas().size());
    }

    @Test
    public void ignoraUnTipoDesconocido() {
        assertFalse(procesador.procesar(trama(99, 1, 235), FECHA));
        assertEquals(0, logica.getEnviadas().size());
    }

    @Test
    public void siFallaElEnvioLaMedicionSeIntentaUnaSolaVez() {
        LogicaTelefonoFake logicaQueFalla = new LogicaTelefonoFake(true);
        ProcesadorBeacons conFallo = new ProcesadorBeacons(logicaQueFalla, 0, 0);

        assertTrue(conFallo.procesar(trama(11, 1, 235), FECHA));
        assertFalse(conFallo.procesar(trama(11, 1, 235), FECHA));
        assertEquals(0, logicaQueFalla.getEnviadas().size());
    }
}
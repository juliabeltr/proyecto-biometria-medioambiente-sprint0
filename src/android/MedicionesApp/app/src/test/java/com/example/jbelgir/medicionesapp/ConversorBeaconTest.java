package com.example.jbelgir.medicionesapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

// =============================================================================
// ConversorBeaconTest.java
//
// Descripción: tests automáticos de ConversorBeacon.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Casos pedidos en doc/android_design.md.
// =============================================================================

public class ConversorBeaconTest {

    private static final String FECHA = "2026-10-03T10:00:00Z";

    private static Medicion convertir(String uuid, int major, int minor) {
        TramaIBeacon trama = new TramaIBeacon(TramasDePrueba.crear(uuid, major, minor));
        return ConversorBeacon.convertir(trama, FECHA, 38.9675, -0.1806);
    }

    @Test
    public void convierteUnaMedicionDeCO2() {
        Medicion m = convertir(TramasDePrueba.UUID_NUESTRO, TramasDePrueba.major(11, 5), 235);

        assertNotNull(m);
        assertEquals("CO2", m.getTipo());
        assertEquals(235.0, m.getValor(), 0.0);
    }

    @Test
    public void convierteUnaTemperaturaNegativaConSigno() {
        Medicion m = convertir(TramasDePrueba.UUID_NUESTRO, TramasDePrueba.major(12, 5), -12);

        assertNotNull(m);
        assertEquals("TEMP", m.getTipo());
        assertEquals(-12.0, m.getValor(), 0.0);
    }

    @Test
    public void convierteUnaMedicionDeRuido() {
        Medicion m = convertir(TramasDePrueba.UUID_NUESTRO, TramasDePrueba.major(13, 200), 58);

        assertNotNull(m);
        assertEquals("RUIDO", m.getTipo());
        assertEquals(58.0, m.getValor(), 0.0);
    }

    @Test
    public void interpretaMinorDeValorAlto() {
        Medicion m = convertir(TramasDePrueba.UUID_NUESTRO, TramasDePrueba.major(11, 1), 1200);

        assertEquals(1200.0, m.getValor(), 0.0);
    }

    @Test
    public void usaLaFechaYLaUbicacionRecibidas() {
        Medicion m = convertir(TramasDePrueba.UUID_NUESTRO, TramasDePrueba.major(11, 1), 235);

        assertEquals(FECHA, m.getFechaHora());
        assertEquals(38.9675, m.getLatitud(), 0.0);
        assertEquals(-0.1806, m.getLongitud(), 0.0);
    }

    @Test
    public void asignaIdCeroPorqueLoAsignaLaBaseDeDatos() {
        Medicion m = convertir(TramasDePrueba.UUID_NUESTRO, TramasDePrueba.major(11, 1), 235);

        assertEquals(0, m.getId());
    }

    @Test
    public void ignoraUnUuidAjeno() {
        assertNull(convertir("MolaMolaMolaMola", TramasDePrueba.major(11, 1), 235));
    }

    @Test
    public void ignoraUnTipoDesconocido() {
        assertNull(convertir(TramasDePrueba.UUID_NUESTRO, TramasDePrueba.major(14, 1), 235));
        assertNull(convertir(TramasDePrueba.UUID_NUESTRO, TramasDePrueba.major(0, 1), 235));
        assertNull(convertir(TramasDePrueba.UUID_NUESTRO, TramasDePrueba.major(10, 1), 235));
    }

    @Test
    public void ignoraUnaTramaNula() {
        assertNull(ConversorBeacon.convertir(null, FECHA, 0, 0));
    }
}
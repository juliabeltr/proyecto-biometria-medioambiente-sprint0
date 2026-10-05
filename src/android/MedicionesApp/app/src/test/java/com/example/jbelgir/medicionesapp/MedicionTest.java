package com.example.jbelgir.medicionesapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;

// =============================================================================
// MedicionTest.java
//
// Descripción: tests automáticos de Medicion.aJSON().
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Casos pedidos en doc/android_design.md.
// =============================================================================

public class MedicionTest {

    @Test
    public void elJSONTieneLosCincoCamposDelServidor() throws Exception {
        Medicion m = new Medicion(0, "CO2", 235, 38.9675, -0.1806, "2026-10-03T10:00:00Z");

        JSONObject json = new JSONObject(m.aJSON());

        assertEquals("CO2", json.getString("tipo"));
        assertEquals(235.0, json.getDouble("valor"), 0.0);
        assertEquals(38.9675, json.getDouble("latitud"), 0.0);
        assertEquals(-0.1806, json.getDouble("longitud"), 0.0);
        assertEquals("2026-10-03T10:00:00Z", json.getString("fechaHora"));
    }

    @Test
    public void elJSONNoIncluyeElId() throws Exception {
        Medicion m = new Medicion(7, "CO2", 235, 38.96, -0.18, "2026-10-03T10:00:00Z");

        assertFalse(new JSONObject(m.aJSON()).has("id"));
        assertEquals(5, new JSONObject(m.aJSON()).length());
    }

    @Test
    public void losNumerosUsanPuntoDecimal() {
        Medicion m = new Medicion(0, "TEMP", 21.5, 38.9675, -0.1806, "2026-10-03T10:00:00Z");

        String json = m.aJSON();

        assertTrue(json.contains("21.5"));
        assertTrue(json.contains("38.9675"));
        assertFalse(json.contains("21,5"));
    }

    @Test
    public void admiteValoresNegativos() throws Exception {
        Medicion m = new Medicion(0, "TEMP", -12, 38.96, -0.18, "2026-10-03T10:00:00Z");

        assertEquals(-12.0, new JSONObject(m.aJSON()).getDouble("valor"), 0.0);
    }

    @Test(expected = IllegalStateException.class)
    public void unNumeroNoRepresentableEnJSONLanzaError() {
        new Medicion(0, "CO2", Double.NaN, 0, 0, "2026-10-03T10:00:00Z").aJSON();
    }

    @Test
    public void losGettersDevuelvenLosDatos() {
        Medicion m = new Medicion(3, "RUIDO", 58, 1.5, 2.5, "2026-10-03T10:00:00Z");

        assertEquals(3, m.getId());
        assertEquals("RUIDO", m.getTipo());
        assertEquals(58.0, m.getValor(), 0.0);
        assertEquals(1.5, m.getLatitud(), 0.0);
        assertEquals(2.5, m.getLongitud(), 0.0);
        assertEquals("2026-10-03T10:00:00Z", m.getFechaHora());
    }
}
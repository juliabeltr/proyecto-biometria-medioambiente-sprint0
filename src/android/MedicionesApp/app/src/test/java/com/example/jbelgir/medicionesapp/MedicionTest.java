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

    private static final String SENSOR = "AA:BB:CC:DD:EE:01";

    @Test
    public void elJSONTieneLosCincoCamposDelServidorYLaUbicacionEsUnObjeto() throws Exception {
        Medicion m = new Medicion(SENSOR, "CO2", 235, new Punto(38.9675, -0.1806), "2026-10-03T10:00:00Z");

        JSONObject json = new JSONObject(m.aJSON());

        assertEquals(SENSOR, json.getString("sensorId"));
        assertEquals("CO2", json.getString("tipo"));
        assertEquals(235.0, json.getDouble("valor"), 0.0);
        JSONObject ubicacion = json.getJSONObject("ubicacion");
        assertEquals(38.9675, ubicacion.getDouble("latitud"), 0.0);
        assertEquals(-0.1806, ubicacion.getDouble("longitud"), 0.0);
        assertEquals("2026-10-03T10:00:00Z", json.getString("fechaHora"));
    }

    @Test
    public void elJSONTieneExactamenteLosCincoCampos() throws Exception {
        Medicion m = new Medicion(SENSOR, "CO2", 235, new Punto(38.96, -0.18), "2026-10-03T10:00:00Z");

        assertFalse(new JSONObject(m.aJSON()).has("id"));
        assertEquals(5, new JSONObject(m.aJSON()).length());
    }

    @Test
    public void losNumerosUsanPuntoDecimal() {
        Medicion m = new Medicion(SENSOR, "TEMP", 21.5, new Punto(38.9675, -0.1806), "2026-10-03T10:00:00Z");

        String json = m.aJSON();

        assertTrue(json.contains("21.5"));
        assertTrue(json.contains("38.9675"));
        assertFalse(json.contains("21,5"));
    }

    @Test
    public void admiteValoresNegativos() throws Exception {
        Medicion m = new Medicion(SENSOR, "TEMP", -12, new Punto(38.96, -0.18), "2026-10-03T10:00:00Z");

        assertEquals(-12.0, new JSONObject(m.aJSON()).getDouble("valor"), 0.0);
    }

    @Test(expected = IllegalStateException.class)
    public void unNumeroNoRepresentableEnJSONLanzaError() {
        new Medicion(SENSOR, "CO2", Double.NaN, new Punto(0, 0), "2026-10-03T10:00:00Z").aJSON();
    }

    @Test
    public void losGettersDevuelvenLosDatos() {
        Medicion m = new Medicion(SENSOR, "RUIDO", 58, new Punto(1.5, 2.5), "2026-10-03T10:00:00Z");

        assertEquals("RUIDO", m.getTipo());
        assertEquals(58.0, m.getValor(), 0.0);
        assertEquals(SENSOR, m.getSensorId());
        assertEquals(1.5, m.getUbicacion().getLatitud(), 0.0);
        assertEquals(2.5, m.getUbicacion().getLongitud(), 0.0);
        assertEquals("2026-10-03T10:00:00Z", m.getFechaHora());
    }

    @Test
    public void unPuntoDevuelveSuLatitudYSuLongitud() {
        Punto p = new Punto(-33.8688, 151.2093);

        assertEquals(-33.8688, p.getLatitud(), 0.0);
        assertEquals(151.2093, p.getLongitud(), 0.0);
    }
}

package com.example.jbelgir.medicionesapp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

// =============================================================================
// FiltroDuplicadosTest.java
//
// Descripción: tests automáticos de FiltroDuplicados.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Casos pedidos en doc/android_design.md.
// =============================================================================

public class FiltroDuplicadosTest {

    @Test
    public void elPrimerAnuncioEsNuevo() {
        assertTrue(new FiltroDuplicados().esNueva(TramasDePrueba.major(11, 1)));
    }

    @Test
    public void unAnuncioRepetidoNoEsNuevo() {
        FiltroDuplicados filtro = new FiltroDuplicados();

        assertTrue(filtro.esNueva(TramasDePrueba.major(11, 1)));
        assertFalse(filtro.esNueva(TramasDePrueba.major(11, 1)));
        assertFalse(filtro.esNueva(TramasDePrueba.major(11, 1)));
    }

    @Test
    public void unContadorDistintoEsNuevo() {
        FiltroDuplicados filtro = new FiltroDuplicados();

        assertTrue(filtro.esNueva(TramasDePrueba.major(11, 1)));
        assertTrue(filtro.esNueva(TramasDePrueba.major(11, 2)));
    }

    @Test
    public void losTiposSonIndependientes() {
        FiltroDuplicados filtro = new FiltroDuplicados();

        assertTrue(filtro.esNueva(TramasDePrueba.major(11, 1)));
        assertTrue(filtro.esNueva(TramasDePrueba.major(12, 1)));
        assertFalse(filtro.esNueva(TramasDePrueba.major(11, 1)));
        assertFalse(filtro.esNueva(TramasDePrueba.major(12, 1)));
    }

    @Test
    public void alternarTiposNoOlvidaElUltimoDeCadaUno() {
        FiltroDuplicados filtro = new FiltroDuplicados();

        assertTrue(filtro.esNueva(TramasDePrueba.major(11, 3)));
        assertTrue(filtro.esNueva(TramasDePrueba.major(12, 3)));
        assertFalse(filtro.esNueva(TramasDePrueba.major(11, 3)));
    }
}
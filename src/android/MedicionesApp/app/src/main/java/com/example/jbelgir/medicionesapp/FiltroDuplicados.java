package com.example.jbelgir.medicionesapp;

import java.util.HashMap;
import java.util.Map;

// =============================================================================
// FiltroDuplicados.java
//
// Descripción: evita enviar varias veces la misma medición de la placa.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/android_design.md (FiltroDuplicados).
//              La placa repite el mismo anuncio durante un segundo.
// =============================================================================

public class FiltroDuplicados {

    private final Map<Integer, Integer> ultimoMajorPorTipo = new HashMap<>();

    /*
     * --------------------------------------------------------------
     * Propósito: indica si el major (tipo y contador) es distinto del
     *            último recibido para ese tipo, y lo recuerda.
     *
     * Diseño lógico:
     *     major: N --> esNueva() -->
     *       resultado: B <--
     *
     * Parámetros:
     *     major: N. Major del anuncio: byte alto = tipo, byte bajo = contador.
     * Retorno: B. true si es una medición nueva; false si es repetida.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    public boolean esNueva(int major) {
        int tipo = (major >> 8) & 0xFF;
        Integer ultimo = ultimoMajorPorTipo.get(tipo);
        if (ultimo != null && ultimo == major) {
            return false;
        }
        ultimoMajorPorTipo.put(tipo, major);
        return true;
    }
}
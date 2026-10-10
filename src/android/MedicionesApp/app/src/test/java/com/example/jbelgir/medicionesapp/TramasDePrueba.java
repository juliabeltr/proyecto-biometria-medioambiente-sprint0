package com.example.jbelgir.medicionesapp;

import java.nio.charset.StandardCharsets;

// =============================================================================
// TramasDePrueba.java
//
// Descripción: apoyo de los tests: construye tramas iBeacon como las emite la
//              placa Arduino.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Utilidad de test; no forma parte del diseño de la app.
// =============================================================================

final class TramasDePrueba {

    static final String UUID_NUESTRO = "EPSG-GTI-PROY-3A";

    private TramasDePrueba() {
    }

    /*
     * Construye una trama de 30 bytes: prefijo (flags, cabecera, Apple,
     * tipo iBeacon y longitud), UUID de 16 caracteres, major, minor y txPower.
     */
    static byte[] crear(String uuid, int major, int minor) {
        byte[] trama = new byte[30];
        int[] prefijo = {0x02, 0x01, 0x06, 0x1A, 0xFF, 0x4C, 0x00, 0x02, 0x15};
        for (int i = 0; i < prefijo.length; i++) {
            trama[i] = (byte) prefijo[i];
        }
        System.arraycopy(uuid.getBytes(StandardCharsets.US_ASCII), 0, trama, 9, 16);
        trama[25] = (byte) (major >> 8);
        trama[26] = (byte) major;
        trama[27] = (byte) (minor >> 8);
        trama[28] = (byte) minor;
        trama[29] = (byte) 0xCB;
        return trama;
    }

    /*
     * Major como lo calcula la placa: tipo en el byte alto y contador en el bajo.
     */
    static int major(int idTipo, int contador) {
        return (idTipo << 8) + contador;
    }
}
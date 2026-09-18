package org.jordi.btlealumnos2021;

import java.util.Arrays;

// -----------------------------------------------------------------------------------
// Júlia Beltrán Girbés
// -----------------------------------------------------------------------------------

/**
 * Representa y descompone una trama iBeacon.
 *
 * La trama esperada contiene:
 * - Prefijo: 9 bytes
 * - UUID: 16 bytes
 * - Major: 2 bytes
 * - Minor: 2 bytes
 * - TxPower: 1 byte
 *
 * Total mínimo esperado: 30 bytes.
 */
public class TramaIBeacon {

    private byte[] prefijo = null;       // 9 bytes
    private byte[] uuid = null;          // 16 bytes
    private byte[] major = null;         // 2 bytes
    private byte[] minor = null;         // 2 bytes
    private byte txPower = 0;            // 1 byte

    private byte[] losBytes;

    private byte[] advFlags = null;       // 3 bytes
    private byte[] advHeader = null;      // 2 bytes
    private byte[] companyID = null;      // 2 bytes
    private byte iBeaconType = 0;         // 1 byte
    private byte iBeaconLength = 0;       // 1 byte

    /**
     * Devuelve el prefijo completo de la trama iBeacon.
     *
     * Diseño lógico:
     * getPrefijo() --> [Z]_9
     *
     * @return Prefijo de 9 bytes.
     */
    public byte[] getPrefijo() {
        return prefijo;
    }

    /**
     * Devuelve el UUID del iBeacon.
     *
     * Diseño lógico:
     * getUUID() --> [Z]_16
     *
     * @return UUID de 16 bytes.
     */
    public byte[] getUUID() {
        return uuid;
    }

    /**
     * Devuelve el campo major.
     *
     * Diseño lógico:
     * getMajor() --> [Z]_2
     *
     * @return Campo major de 2 bytes.
     */
    public byte[] getMajor() {
        return major;
    }

    /**
     * Devuelve el campo minor.
     *
     * Diseño lógico:
     * getMinor() --> [Z]_2
     *
     * @return Campo minor de 2 bytes.
     */
    public byte[] getMinor() {
        return minor;
    }

    /**
     * Devuelve el valor TxPower de la trama iBeacon.
     *
     * Diseño lógico:
     * getTxPower() --> Z
     *
     * @return Valor TxPower.
     */
    public byte getTxPower() {
        return txPower;
    }

    /**
     * Devuelve la trama original completa.
     *
     * Diseño lógico:
     * getLosBytes() --> [Z]
     *
     * @return Array de bytes original.
     */
    public byte[] getLosBytes() {
        return losBytes;
    }

    /**
     * Devuelve los flags del anuncio BLE.
     *
     * Diseño lógico:
     * getAdvFlags() --> [Z]_3
     *
     * @return Flags de publicidad BLE.
     */
    public byte[] getAdvFlags() {
        return advFlags;
    }

    /**
     * Devuelve la cabecera del anuncio.
     *
     * Diseño lógico:
     * getAdvHeader() --> [Z]_2
     *
     * @return Cabecera del anuncio.
     */
    public byte[] getAdvHeader() {
        return advHeader;
    }

    /**
     * Devuelve el identificador del fabricante.
     *
     * Diseño lógico:
     * getCompanyID() --> [Z]_2
     *
     * @return Company ID de 2 bytes.
     */
    public byte[] getCompanyID() {
        return companyID;
    }

    /**
     * Devuelve el tipo de iBeacon.
     *
     * Diseño lógico:
     * getiBeaconType() --> Z
     *
     * @return Tipo de iBeacon.
     */
    public byte getiBeaconType() {
        return iBeaconType;
    }

    /**
     * Devuelve la longitud declarada de la carga iBeacon.
     *
     * Diseño lógico:
     * getiBeaconLength() --> Z
     *
     * @return Longitud indicada en la trama.
     */
    public byte getiBeaconLength() {
        return iBeaconLength;
    }

    /**
     * Construye una TramaIBeacon a partir de un array de bytes.
     *
     * Diseño lógico:
     * bytes: [Z] --> TramaIBeacon()
     *
     * @param bytes Trama BLE que se quiere interpretar como iBeacon.
     *
     * @throws IllegalArgumentException Si la trama es nula o contiene
     * menos de 30 bytes.
     */
    public TramaIBeacon(byte[] bytes) {

        if (bytes == null) {
            throw new IllegalArgumentException(
                    "La trama iBeacon no puede ser nula"
            );
        }

        if (bytes.length < 30) {
            throw new IllegalArgumentException(
                    "La trama iBeacon debe contener al menos 30 bytes"
            );
        }

        /*
         * Se guarda una copia para evitar que modificaciones externas
         * del array original alteren posteriormente la trama.
         */
        this.losBytes = Arrays.copyOf(
                bytes,
                bytes.length
        );

        /*
         * Estructura esperada:
         *
         * bytes 0-8   -> prefijo       (9 bytes)
         * bytes 9-24  -> UUID          (16 bytes)
         * bytes 25-26 -> major         (2 bytes)
         * bytes 27-28 -> minor         (2 bytes)
         * byte 29     -> txPower       (1 byte)
         */
        prefijo = Arrays.copyOfRange(
                losBytes,
                0,
                9
        );

        uuid = Arrays.copyOfRange(
                losBytes,
                9,
                25
        );

        major = Arrays.copyOfRange(
                losBytes,
                25,
                27
        );

        minor = Arrays.copyOfRange(
                losBytes,
                27,
                29
        );

        txPower = losBytes[29];

        /*
         * Descomposición del prefijo:
         *
         * bytes 0-2 -> advertising flags
         * bytes 3-4 -> advertising header
         * bytes 5-6 -> company ID
         * byte 7    -> iBeacon type
         * byte 8    -> iBeacon length
         */
        advFlags = Arrays.copyOfRange(
                prefijo,
                0,
                3
        );

        advHeader = Arrays.copyOfRange(
                prefijo,
                3,
                5
        );

        companyID = Arrays.copyOfRange(
                prefijo,
                5,
                7
        );

        iBeaconType = prefijo[7];
        iBeaconLength = prefijo[8];
    }
}

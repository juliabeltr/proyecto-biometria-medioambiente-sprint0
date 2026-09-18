package org.jordi.btlealumnos2021;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

// -----------------------------------------------------------------------------------
// Júlia Beltrán Girbés
// -----------------------------------------------------------------------------------

/**
 * Clase de utilidades para realizar conversiones entre textos,
 * bytes, enteros, valores long y UUID.
 *
 * Todos los métodos son estáticos y no mantienen estado interno.
 */
public class Utilidades {

    /**
     * Convierte un texto en un array de bytes utilizando UTF-8.
     *
     * Diseño lógico:
     * texto: Text --> stringToBytes() --> [Z]
     *
     * @param texto Texto que se desea convertir.
     * @return Bytes correspondientes al texto.
     */
    public static byte[] stringToBytes(String texto) {

        if (texto == null) {
            return new byte[0];
        }

        return texto.getBytes(StandardCharsets.UTF_8);
    }


    /**
     * Convierte un texto de exactamente 16 caracteres en un UUID.
     *
     * Los primeros 8 caracteres se utilizan como parte más significativa
     * y los 8 restantes como parte menos significativa.
     *
     * Diseño lógico:
     * uuid: Text --> stringToUUID() --> UUID
     *
     * @param uuid Texto de 16 caracteres.
     * @return UUID correspondiente.
     *
     * @throws IllegalArgumentException Si el texto es nulo
     * o no contiene exactamente 16 caracteres.
     */
    public static UUID stringToUUID(String uuid) {

        if (uuid == null) {
            throw new IllegalArgumentException(
                    "stringToUUID(): el texto no puede ser nulo"
            );
        }

        if (uuid.length() != 16) {
            throw new IllegalArgumentException(
                    "stringToUUID(): el texto debe tener exactamente 16 caracteres"
            );
        }

        String masSignificativo =
                uuid.substring(0, 8);

        String menosSignificativo =
                uuid.substring(8, 16);

        long parteMasSignificativa =
                Utilidades.bytesToLong(
                        masSignificativo.getBytes(StandardCharsets.UTF_8)
                );

        long parteMenosSignificativa =
                Utilidades.bytesToLong(
                        menosSignificativo.getBytes(StandardCharsets.UTF_8)
                );

        return new UUID(
                parteMasSignificativa,
                parteMenosSignificativa
        );
    }


    /**
     * Convierte un UUID en el texto representado por sus 16 bytes.
     *
     * Diseño lógico:
     * uuid: UUID --> uuidToString() --> Text
     *
     * @param uuid UUID que se desea convertir.
     * @return Representación textual de sus bytes.
     */
    public static String uuidToString(UUID uuid) {

        if (uuid == null) {
            return "";
        }

        return bytesToString(
                dosLongToBytes(
                        uuid.getMostSignificantBits(),
                        uuid.getLeastSignificantBits()
                )
        );
    }


    /**
     * Convierte un UUID en una representación hexadecimal.
     *
     * Diseño lógico:
     * uuid: UUID --> uuidToHexString() --> Text
     *
     * @param uuid UUID que se desea convertir.
     * @return Bytes del UUID expresados en hexadecimal.
     */
    public static String uuidToHexString(UUID uuid) {

        if (uuid == null) {
            return "";
        }

        return bytesToHexString(
                dosLongToBytes(
                        uuid.getMostSignificantBits(),
                        uuid.getLeastSignificantBits()
                )
        );
    }


    /**
     * Convierte un array de bytes en texto utilizando UTF-8.
     *
     * Diseño lógico:
     * bytes: [Z] --> bytesToString() --> Text
     *
     * @param bytes Array de bytes.
     * @return Texto correspondiente.
     */
    public static String bytesToString(byte[] bytes) {

        if (bytes == null) {
            return "";
        }

        return new String(
                bytes,
                StandardCharsets.UTF_8
        );
    }


    /**
     * Convierte dos valores long en un array de 16 bytes.
     *
     * El primer long representa los 8 bytes más significativos
     * y el segundo los 8 bytes menos significativos.
     *
     * Diseño lógico:
     * mas_significativos: Z,
     * menos_significativos: Z
     *      --> dosLongToBytes() --> [Z]_16
     *
     * @param masSignificativos Parte más significativa.
     * @param menosSignificativos Parte menos significativa.
     * @return Array de 16 bytes.
     */
    public static byte[] dosLongToBytes(
            long masSignificativos,
            long menosSignificativos
    ) {

        ByteBuffer buffer =
                ByteBuffer.allocate(
                        2 * Long.BYTES
                );

        buffer.putLong(
                masSignificativos
        );

        buffer.putLong(
                menosSignificativos
        );

        return buffer.array();
    }


    /**
     * Convierte un array de bytes en un entero con signo.
     *
     * BigInteger interpreta los bytes utilizando complemento a dos.
     *
     * Diseño lógico:
     * bytes: [Z] --> bytesToInt() --> Z
     *
     * @param bytes Bytes que representan el entero.
     * @return Valor entero correspondiente.
     */
    public static int bytesToInt(byte[] bytes) {

        if (bytes == null || bytes.length == 0) {
            return 0;
        }

        return new BigInteger(
                bytes
        ).intValue();
    }


    /**
     * Convierte un array de bytes en un valor long con signo.
     *
     * Diseño lógico:
     * bytes: [Z] --> bytesToLong() --> Z
     *
     * @param bytes Bytes que representan el valor.
     * @return Valor long correspondiente.
     */
    public static long bytesToLong(byte[] bytes) {

        if (bytes == null || bytes.length == 0) {
            return 0L;
        }

        return new BigInteger(
                bytes
        ).longValue();
    }


    /**
     * Convierte manualmente hasta 4 bytes en un entero con signo.
     *
     * Los bytes se interpretan en orden big-endian:
     * primero el byte más significativo.
     *
     * Diseño lógico:
     * bytes: [Z] --> bytesToIntOK() --> Z
     *
     * @param bytes Array de hasta 4 bytes.
     * @return Valor entero correspondiente.
     *
     * @throws IllegalArgumentException Si se proporcionan
     * más de 4 bytes.
     */
    public static int bytesToIntOK(byte[] bytes) {

        if (bytes == null ||
                bytes.length == 0) {

            return 0;
        }

        if (bytes.length > 4) {
            throw new IllegalArgumentException(
                    "bytesToIntOK(): demasiados bytes para convertir a int"
            );
        }

        int res = 0;

        /*
         * Los bytes se procesan de izquierda a derecha.
         * & 0xFF evita la extensión de signo de cada byte
         * individual al convertirlo a int.
         */
        for (byte b : bytes) {

            res =
                    (res << 8)
                            | (b & 0xFF);
        }

        /*
         * Si el bit más significativo del primer byte es 1,
         * el número original es negativo.
         *
         * El código original utilizaba 0x08, que comprueba
         * el bit 3 en lugar del bit de signo (0x80).
         */
        if (
                bytes.length < Integer.BYTES
                        &&
                (bytes[0] & 0x80) != 0
        ) {

            res |=
                    (-1 << (bytes.length * 8));
        }

        return res;
    }


    /**
     * Convierte un array de bytes a representación hexadecimal.
     *
     * Cada byte se muestra utilizando dos dígitos hexadecimales
     * separados mediante ':'.
     *
     * Diseño lógico:
     * bytes: [Z] --> bytesToHexString() --> Text
     *
     * @param bytes Array que se desea representar.
     * @return Representación hexadecimal de los bytes.
     */
    public static String bytesToHexString(byte[] bytes) {

        if (bytes == null) {
            return "";
        }

        StringBuilder sb =
                new StringBuilder();

        for (byte b : bytes) {

            sb.append(
                    String.format(
                            "%02x",
                            b & 0xFF
                    )
            );

            sb.append(':');
        }

        return sb.toString();
    }

}

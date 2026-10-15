// =============================================================================
// repositorioMediciones.js
//
// Descripción: acceso a la base de datos SQLite de mediciones (componente database).
// Autor:       Júlia Beltrán Girbés
// Fecha:       02/10/2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/database_design.md. Solo guarda y recupera
//              datos: no valida reglas de negocio ni contiene HTTP o JSON.
// =============================================================================

const Database = require("better-sqlite3");

const CAMPOS_OBLIGATORIOS = ["sensorId", "tipo", "valor", "ubicacion", "fechaHora"];

/*
 * Punto = ( latitud: R, longitud: R )
 * Medicion = ( sensorId: Text, tipo: Text, valor: R, ubicacion: Punto,
 *              fechaHora: Text )
 */
const SELECT_MEDICIONES = `SELECT sensorId, tipo, valor, ubicacion, fechaHora
                           FROM MEDICIONES`;
class RepositorioMediciones {

    /*
     * --------------------------------------------------------------
     * Propósito: crea el repositorio y asegura que existe la tabla
     *            MEDICIONES.
     *
     * Diseño lógico:
     *     ruta: Text --> RepositorioMediciones() -->
     *
     * Parámetros:
     *     ruta: Text. Ruta del fichero de la BD, o ":memory:" para una
     *           BD en memoria (útil en tests).
     * Retorno: ninguno.
     * Errores: Error si no se puede abrir la base de datos.
     * --------------------------------------------------------------
     */
    constructor(ruta) {
        try {
            this.conexion = new Database(ruta);
            this.conexion.exec(`
                CREATE TABLE IF NOT EXISTS MEDICIONES (
                    sensorId  TEXT NOT NULL,
                    tipo      TEXT NOT NULL,
                    valor     REAL NOT NULL,
                    ubicacion POINT NOT NULL,
                    fechaHora TEXT NOT NULL,
                    PRIMARY KEY (sensorId, fechaHora)
                )
            `);
        } catch (error) {
            throw new Error("No se pudo abrir la base de datos de mediciones");
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: guarda una medición. La clave primaria es (sensorId,
     *            fechaHora): si ya hay una medición de ese sensor en ese
     *            instante, no se guarda.
     *
     * Diseño lógico:
     *     m: Medicion --> insertar() -->
     *       resultado: B <--
     *
     * Parámetros:
     *     m: Medicion. Medición a guardar.
     * Retorno: resultado: B. true si se ha guardado; false si ya existía
     *          una medición con la misma clave primaria.
     * Errores: Error si falta algún campo obligatorio o si falla la BD.
     * --------------------------------------------------------------
     */
    insertar(m) {
        this.#exigirCamposObligatorios(m);
        try {
            const resultado = this.conexion
                .prepare(
                    `INSERT OR IGNORE INTO MEDICIONES (sensorId, tipo, valor, ubicacion, fechaHora)
                     VALUES (?, ?, ?, ?, ?)`
                )
                .run(m.sensorId, m.tipo, m.valor, puntoATexto(m.ubicacion), m.fechaHora);
            return resultado.changes === 1;
        } catch (error) {
            throw new Error("Error de base de datos al insertar la medición");
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: recupera la última medición: la de mayor fechaHora y,
     *            en caso de empate, la de mayor sensorId.
     *
     * Diseño lógico:
     *     Medicion <-- recuperarUltima() <--
     *
     * Parámetros: ninguno.
     * Retorno: Medicion, o null si no hay ninguna medición.
     * Errores: Error si falla la BD.
     * --------------------------------------------------------------
     */
    recuperarUltima() {
        try {
            const fila = this.conexion
                .prepare(`${SELECT_MEDICIONES} ORDER BY fechaHora DESC, sensorId DESC LIMIT 1`)
                .get();
            return fila === undefined ? null : filaAMedicion(fila);
        } catch (error) {
            throw new Error("Error de base de datos al recuperar la última medición");
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: recupera todas las mediciones guardadas, ordenadas por fechaHora
     *            y, en caso de empate, por sensorId.
     *
     * Diseño lógico:
     *     [Medicion] <-- recuperarTodas() <--
     *
     * Parámetros: ninguno.
     * Retorno: [Medicion]. Lista vacía si no hay mediciones.
     * Errores: Error si falla la BD.
     * --------------------------------------------------------------
     */
    recuperarTodas() {
        try {
            return this.conexion
                .prepare(`${SELECT_MEDICIONES} ORDER BY fechaHora ASC, sensorId ASC`)
                .all()
                .map(filaAMedicion);
        } catch (error) {
            throw new Error("Error de base de datos al recuperar las mediciones");
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: comprueba que la medición trae los cinco campos
     *            obligatorios (restricción NOT NULL de la tabla) y que
     *            la ubicación trae latitud y longitud.
     *
     * Diseño lógico:
     *     m: Medicion --> exigirCamposObligatorios() -->
     *
     * Parámetros:
     *     m: Medicion. Medición a comprobar.
     * Retorno: ninguno.
     * Errores: Error si m no existe o falta algún campo.
     * --------------------------------------------------------------
     */
    #exigirCamposObligatorios(m) {
        if (m === null || m === undefined) {
            throw new Error("Medición inválida: no se ha recibido ninguna medición");
        }
        for (const campo of CAMPOS_OBLIGATORIOS) {
            if (m[campo] === null || m[campo] === undefined) {
                throw new Error(`Medición inválida: falta el campo ${campo}`);
            }
        }
        const { latitud, longitud } = m.ubicacion;
        if (latitud === null || latitud === undefined || longitud === null || longitud === undefined) {
            throw new Error("Medición inválida: falta la latitud o la longitud de ubicacion");
        }
    }
}

/*
 * --------------------------------------------------------------
 * Propósito: convierte un Punto al texto WKT que se guarda en la
 *            columna POINT: "POINT(longitud latitud)".
 *
 * Diseño lógico:
 *     p: Punto --> puntoATexto() --> texto: Text
 *
 * Parámetros:
 *     p: Punto. Ubicación a guardar.
 * Retorno: texto: Text. Por ejemplo "POINT(-0.1806 38.9675)".
 * Errores: ninguno.
 * --------------------------------------------------------------
 */
function puntoATexto(p) {
    return `POINT(${p.longitud} ${p.latitud})`;
}

/*
 * --------------------------------------------------------------
 * Propósito: convierte el texto WKT de la columna POINT en un Punto.
 *
 * Diseño lógico:
 *     texto: Text --> textoAPunto() --> p: Punto
 *
 * Parámetros:
 *     texto: Text. Por ejemplo "POINT(-0.1806 38.9675)".
 * Retorno: p: Punto.
 * Errores: Error si el texto no tiene formato POINT(longitud latitud).
 * --------------------------------------------------------------
 */
function textoAPunto(texto) {
    const partes = /^POINT\(\s*(\S+)\s+(\S+)\s*\)$/.exec(texto);
    if (partes === null) {
        throw new Error("Ubicación guardada con formato no válido");
    }
    return { latitud: Number(partes[2]), longitud: Number(partes[1]) };
}

/*
 * --------------------------------------------------------------
 * Propósito: convierte una fila de la tabla en una Medicion
 *            (la columna ubicacion pasa de texto WKT a Punto).
 *
 * Diseño lógico:
 *     fila: Fila --> filaAMedicion() --> m: Medicion
 *
 * Parámetros:
 *     fila: Fila. Objeto con una propiedad por columna de MEDICIONES.
 * Retorno: m: Medicion.
 * Errores: Error si la columna ubicacion no tiene formato válido.
 * --------------------------------------------------------------
 */
function filaAMedicion(fila) {
    return { ...fila, ubicacion: textoAPunto(fila.ubicacion) };
}

module.exports = RepositorioMediciones;

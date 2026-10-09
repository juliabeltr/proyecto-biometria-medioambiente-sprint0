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

const CAMPOS_OBLIGATORIOS = ["tipo", "valor", "latitud", "longitud", "fechaHora"];

/*
 * Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R,
 *              fechaHora: Text )
 */
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
                    id        INTEGER PRIMARY KEY AUTOINCREMENT,
                    tipo      TEXT NOT NULL,
                    valor     REAL NOT NULL,
                    latitud   REAL NOT NULL,
                    longitud  REAL NOT NULL,
                    fechaHora TEXT NOT NULL
                )
            `);
        } catch (error) {
            throw new Error("No se pudo abrir la base de datos de mediciones");
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: guarda una medición. El id lo asigna la BD, por lo que
     *            el id recibido se ignora.
     *
     * Diseño lógico:
     *     m: Medicion --> insertar() -->
     *       id: N     <--
     *
     * Parámetros:
     *     m: Medicion. Medición a guardar.
     * Retorno: id: N. Identificador asignado a la medición.
     * Errores: Error si falta algún campo obligatorio o si falla la BD.
     * --------------------------------------------------------------
     */
    insertar(m) {
        this.#exigirCamposObligatorios(m);
        try {
            const resultado = this.conexion
                .prepare(
                    `INSERT INTO MEDICIONES (tipo, valor, latitud, longitud, fechaHora)
                     VALUES (?, ?, ?, ?, ?)`
                )
                .run(m.tipo, m.valor, m.latitud, m.longitud, m.fechaHora);
            return Number(resultado.lastInsertRowid);
        } catch (error) {
            throw new Error("Error de base de datos al insertar la medición");
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: recupera la última medición: la de mayor fechaHora y,
     *            en caso de empate, la de mayor id.
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
                .prepare(
                    `SELECT id, tipo, valor, latitud, longitud, fechaHora
                     FROM MEDICIONES
                     ORDER BY fechaHora DESC, id DESC
                     LIMIT 1`
                )
                .get();
            return fila === undefined ? null : fila;
        } catch (error) {
            throw new Error("Error de base de datos al recuperar la última medición");
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: recupera todas las mediciones guardadas, ordenadas por id.
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
                .prepare(
                    `SELECT id, tipo, valor, latitud, longitud, fechaHora
                     FROM MEDICIONES
                     ORDER BY id ASC`
                )
                .all();
        } catch (error) {
            throw new Error("Error de base de datos al recuperar las mediciones");
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: comprueba que la medición trae los cinco campos
     *            obligatorios (restricción NOT NULL de la tabla).
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
    }
}

module.exports = RepositorioMediciones;

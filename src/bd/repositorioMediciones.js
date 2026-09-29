'use strict';

const Database = require('better-sqlite3');

const COLUMNAS = 'id, tipo, valor, latitud, longitud, fechaHora';

/**
 * Repositorio de mediciones de contaminación atmosférica sobre SQLite.
 *
 * Tipo de dato principal:
 *   Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text )
 *
 * Diseño lógico de la clase:
 * --------------------
 *  --------- RepositorioMediciones ---------------
 *  |
 *  |  conexion: conexión a la base de datos
 *  |
 *  |
 *  ruta: Text --> RepositorioMediciones() -->
 *  |
 *  |
 *  m: Medicion --> insertar() -->
 *    id: N     <--
 *  |
 *  |
 *  Medicion <-- recuperarUltima() <--
 *  |
 *  |
 *  [Medicion] <-- recuperarTodas() <--
 *  |
 *  -----------------------------------------------
 * --------------------
 *
 * Tabla gestionada: MEDICIONES (id, tipo, valor, latitud, longitud, fechaHora).
 * fechaHora se guarda como texto ISO 8601 (su formato lo valida la lógica de negocio).
 * Este componente no valida reglas de negocio ni contiene HTTP ni JSON.
 */
class RepositorioMediciones {
  /**
   * Propósito: abrir la base de datos SQLite y crear la tabla MEDICIONES
   * si todavía no existe.
   *
   * Diseño lógico:
   * --------------------
   * ruta: Text --> RepositorioMediciones() -->
   * --------------------
   *
   * @param {string} ruta Ruta del fichero de la BD; ":memory:" usa una BD en memoria.
   * @returns {RepositorioMediciones} Repositorio listo para usar.
   * @throws {Error} Si la base de datos no puede abrirse o inicializarse.
   */
  constructor(ruta) {
    try {
      this.conexion = new Database(ruta);
      this.conexion.exec(`
        CREATE TABLE IF NOT EXISTS MEDICIONES (
          id        INTEGER PRIMARY KEY AUTOINCREMENT,
          tipo      TEXT    NOT NULL,
          valor     REAL    NOT NULL,
          latitud   REAL    NOT NULL,
          longitud  REAL    NOT NULL,
          fechaHora TEXT    NOT NULL
        )
      `);
    } catch (error) {
      if (this.conexion && this.conexion.open) this.conexion.close();
      throw new Error('No se pudo abrir o inicializar la base de datos.');
    }
  }

  /**
   * Propósito: guardar una medición y devolver el identificador asignado.
   * El campo id de la medición recibida se ignora: lo genera la base de datos.
   *
   * Diseño lógico:
   * --------------------
   * m: Medicion --> insertar() --> id: N
   * --------------------
   *
   * @param {Medicion} medicion Medición a guardar (su id se ignora).
   * @returns {number} Identificador asignado a la medición insertada.
   * @throws {Error} Si falta algún campo obligatorio o es null ("Medición inválida").
   * @throws {Error} Si SQLite falla al insertar (por ejemplo, conexión cerrada).
   */
  insertar(medicion) {
    try {
      const { tipo, valor, latitud, longitud, fechaHora } = medicion;
      const resultado = this.conexion
        .prepare(
          `INSERT INTO MEDICIONES (tipo, valor, latitud, longitud, fechaHora)
           VALUES (?, ?, ?, ?, ?)`
        )
        .run(tipo, valor, latitud, longitud, fechaHora);
      return Number(resultado.lastInsertRowid);
    } catch (error) {
      const incumpleObligatoriedad = String(error.code).startsWith('SQLITE_CONSTRAINT');
      const medicionAusente = medicion === null || medicion === undefined;
      if (incumpleObligatoriedad || medicionAusente) {
        throw new Error('Medición inválida: todos los campos son obligatorios.');
      }
      throw new Error('No se pudo insertar la medición en la base de datos.');
    }
  }

  /**
   * Propósito: recuperar la medición más reciente. Se considera la de mayor
   * fechaHora y, si hay empate, la de mayor id.
   *
   * Diseño lógico:
   * --------------------
   * recuperarUltima() --> Medicion
   * --------------------
   * Si no hay mediciones almacenadas, el resultado es null.
   *
   * @returns {Medicion|null} Última medición, o null si no hay ninguna.
   * @throws {Error} Si SQLite falla al consultar (por ejemplo, conexión cerrada).
   */
  recuperarUltima() {
    try {
      const fila = this.conexion
        .prepare(
          `SELECT ${COLUMNAS} FROM MEDICIONES
           ORDER BY fechaHora DESC, id DESC
           LIMIT 1`
        )
        .get();
      return fila === undefined ? null : fila;
    } catch (error) {
      throw new Error('No se pudo recuperar la última medición de la base de datos.');
    }
  }

  /**
   * Propósito: recuperar todas las mediciones almacenadas, ordenadas por id
   * ascendente (orden de inserción).
   *
   * Diseño lógico:
   * --------------------
   * recuperarTodas() --> [Medicion]
   * --------------------
   *
   * @returns {Medicion[]} Lista de mediciones; vacía si no hay ninguna.
   * @throws {Error} Si SQLite falla al consultar (por ejemplo, conexión cerrada).
   */
  recuperarTodas() {
    try {
      return this.conexion
        .prepare(`SELECT ${COLUMNAS} FROM MEDICIONES ORDER BY id ASC`)
        .all();
    } catch (error) {
      throw new Error('No se pudieron recuperar las mediciones de la base de datos.');
    }
  }
}

module.exports = RepositorioMediciones;

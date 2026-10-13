// =============================================================================
// logicaMediciones.js
//
// Descripción: lógica de negocio de las mediciones (componente business_logic).
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/business_logic_design.md. Valida y gestiona
//              mediciones. No depende de ningún mecanismo de comunicación ni de interfaz de usuario.
//              El acceso a datos llega por el constructor.
// =============================================================================

/*
 * Punto = ( latitud: R, longitud: R )
 * Medicion = ( sensorId: Text, tipo: Text, valor: R, ubicacion: Punto,
 *              fechaHora: Text )
 * TipoMedicion = { CO2, TEMP, RUIDO }
 */
const TIPOS_MEDICION = Object.freeze(["CO2", "TEMP", "RUIDO"]);

// Fecha y hora ISO 8601 con zona horaria: 2026-10-02T10:00:00Z
const PATRON_FECHA_HORA_ISO =
    /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})(\.\d+)?(Z|[+-]\d{2}:\d{2})$/;

class LogicaMediciones {

    /*
     * --------------------------------------------------------------
     * Propósito: crea la lógica de mediciones sobre un repositorio.
     *
     * Diseño lógico:
     *     repositorio: RepositorioMediciones --> LogicaMediciones() -->
     *
     * Parámetros:
     *     repositorio: RepositorioMediciones. Capa de datos con
     *                  insertar(), recuperarUltima() y recuperarTodas().
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    constructor(repositorio) {
        this.repositorio = repositorio;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: valida una medición y, si es correcta, la guarda.
     *            No se guarda si ya existe una medición del mismo sensor
     *            en el mismo instante (clave primaria).
     *
     * Diseño lógico:
     *     m: Medicion --> guardarMedicion() -->
     *       resultado: B <--
     *
     * Parámetros:
     *     m: Medicion. Medición a guardar.
     * Retorno: resultado: B. true si se ha guardado; false si los datos
     *          no son válidos (en ese caso no se toca la capa de datos) o
     *          si la medición ya existía.
     * Errores: los de la capa de datos se propagan sin capturar.
     * --------------------------------------------------------------
     */
    guardarMedicion(m) {
        if (!this.#esMedicionValida(m)) {
            return false;
        }
        return this.repositorio.insertar({
            sensorId: m.sensorId,
            tipo: m.tipo,
            valor: m.valor,
            ubicacion: { latitud: m.ubicacion.latitud, longitud: m.ubicacion.longitud },
            fechaHora: m.fechaHora,
        });
    }

    /*
     * --------------------------------------------------------------
     * Propósito: obtiene la última medición guardada.
     *
     * Diseño lógico:
     *     Medicion <-- recuperarUltimaMedicion() <--
     *
     * Parámetros: ninguno.
     * Retorno: Medicion, o null si no hay ninguna medición.
     * Errores: los de la capa de datos se propagan sin capturar.
     * --------------------------------------------------------------
     */
    recuperarUltimaMedicion() {
        return this.repositorio.recuperarUltima();
    }

    /*
     * --------------------------------------------------------------
     * Propósito: obtiene todas las mediciones guardadas.
     *
     * Diseño lógico:
     *     [Medicion] <-- recuperarMediciones() <--
     *
     * Parámetros: ninguno.
     * Retorno: [Medicion]. Lista vacía si no hay mediciones.
     * Errores: los de la capa de datos se propagan sin capturar.
     * --------------------------------------------------------------
     */
    recuperarMediciones() {
        return this.repositorio.recuperarTodas();
    }

    /*
     * --------------------------------------------------------------
     * Propósito: comprueba las reglas de validación de una medición
     *            (id no existe: la clave es sensorId + fechaHora).
     *
     * Diseño lógico:
     *     m: Medicion --> esMedicionValida() --> B
     *
     * Parámetros:
     *     m: Medicion. Medición a comprobar.
     * Retorno: B. true si todos los campos son válidos.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    #esMedicionValida(m) {
        if (m === null || typeof m !== "object") {
            return false;
        }
        return (
            esTextoNoVacio(m.sensorId) &&
            TIPOS_MEDICION.includes(m.tipo) &&
            esNumeroFinito(m.valor) &&
            esPuntoValido(m.ubicacion) &&
            esFechaHoraIso8601(m.fechaHora)
        );
    }
}

/*
 * --------------------------------------------------------------
 * Propósito: indica si un dato es un texto con algún carácter que
 *            no sea espacio en blanco.
 *
 * Diseño lógico:
 *     x: Text --> esTextoNoVacio() -->
 *     resultado: B <--
 * --------------------------------------------------------------
 */
function esTextoNoVacio(x) {
    return typeof x === "string" && x.trim().length > 0;
}

/*
 * --------------------------------------------------------------
 * Propósito: indica si un dato es un Punto válido: latitud entre
 *            -90 y 90 y longitud entre -180 y 180, ambas incluidas.
 *
 * Diseño lógico:
 *     p: Punto --> esPuntoValido() -->
 *     resultado: B <--
 * --------------------------------------------------------------
 */
function esPuntoValido(p) {
    return (
        p !== null &&
        typeof p === "object" &&
        esNumeroEnRango(p.latitud, -90, 90) &&
        esNumeroEnRango(p.longitud, -180, 180)
    );
}

/*
 * --------------------------------------------------------------
 * Propósito: indica si un dato es un número real finito.
 *
 * Diseño lógico:
 *     x: R --> esNumeroFinito() -->
 *     resultado: B <--
 * --------------------------------------------------------------
 */
function esNumeroFinito(x) {
    return typeof x === "number" && Number.isFinite(x);
}

/*
 * --------------------------------------------------------------
 * Propósito: indica si un dato es un número finito dentro de un
 *            rango, con límites incluidos.
 *
 * Diseño lógico:
 *     x: R, minimo: R, maximo: R --> esNumeroEnRango() --> B
 * --------------------------------------------------------------
 */
function esNumeroEnRango(x, minimo, maximo) {
    return esNumeroFinito(x) && x >= minimo && x <= maximo;
}

/*
 * --------------------------------------------------------------
 * Propósito: indica si un texto es una fecha y hora ISO 8601 real
 *            (formato correcto y día existente en el calendario).
 *
 * Diseño lógico:
 *     texto: Text --> esFechaHoraIso8601() --> B
 *
 * Parámetros:
 *     texto: Text. Valor a comprobar, p. ej. "2026-10-02T10:00:00Z".
 * Retorno: B. true si es una fecha y hora válidas.
 * Errores: ninguno.
 * --------------------------------------------------------------
 */
function esFechaHoraIso8601(texto) {
    if (typeof texto !== "string") {
        return false;
    }
    const partes = PATRON_FECHA_HORA_ISO.exec(texto);
    if (partes === null) {
        return false;
    }
    const [anyo, mes, dia, hora, minuto, segundo] = partes.slice(1, 7).map(Number);
    const fecha = new Date(Date.UTC(anyo, mes - 1, dia));
    const diaExiste =
        fecha.getUTCFullYear() === anyo &&
        fecha.getUTCMonth() === mes - 1 &&
        fecha.getUTCDate() === dia;
    return diaExiste && hora <= 23 && minuto <= 59 && segundo <= 59;
}

module.exports = LogicaMediciones;

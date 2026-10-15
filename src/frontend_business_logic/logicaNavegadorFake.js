// =============================================================================
// logicaNavegadorFake.js
//
// Descripción: lógica fake del navegador (componente frontend_business_logic). Simula
//              a la lógica real para desarrollar la interfaz sin servidor.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/frontend_business_logic_design.md. No hace
//              peticiones, no accede al DOM y no contiene HTML. Sus
//              operaciones son asíncronas, como las de la lógica real.
// =============================================================================

/*
 * Punto = ( latitud: R, longitud: R )
 * Medicion = ( sensorId: Text, tipo: Text, valor: R, ubicacion: Punto,
 *              fechaHora: Text )
 */
const MEDICIONES_DE_EJEMPLO = Object.freeze([
    { sensorId: "sensor-demo-1", tipo: "TEMP", valor: 21.5, ubicacion: { latitud: 38.9675, longitud: -0.1806 }, fechaHora: "2026-10-02T09:00:00Z" },
    { sensorId: "sensor-demo-1", tipo: "RUIDO", valor: 58, ubicacion: { latitud: 38.9675, longitud: -0.1806 }, fechaHora: "2026-10-02T09:30:00Z" },
    { sensorId: "sensor-demo-1", tipo: "CO2", valor: 412, ubicacion: { latitud: 38.9675, longitud: -0.1806 }, fechaHora: "2026-10-02T10:00:00Z" },
]);

class LogicaNavegadorFake {

    /*
     * --------------------------------------------------------------
     * Propósito: crea la lógica fake con una lista de mediciones y un
     *            indicador para simular fallos.
     *
     * Diseño lógico:
     *     mediciones: [Medicion], falla: B --> LogicaNavegadorFake() -->
     *
     * Parámetros:
     *     mediciones: [Medicion]. Datos simulados. Si no se indica, se
     *                 usa un conjunto de ejemplo con una medición de CO2.
     *     falla: B. Si es true, las operaciones terminan con un error
     *            simulado. Por defecto, false.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    constructor(mediciones = MEDICIONES_DE_EJEMPLO, falla = false) {
        this.mediciones = mediciones.map((m) => ({ ...m }));
        this.falla = falla;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: devuelve la medición de mayor fechaHora; en caso de
     *            empate, la de mayor sensorId.
     *
     * Diseño lógico:
     *     Medicion <-- recuperarUltimaMedicion() <--
     *
     * Parámetros: ninguno.
     * Retorno: Promise de Medicion, o de null si no hay mediciones.
     * Errores: la Promise se rechaza con un Error si falla es true.
     * --------------------------------------------------------------
     */
    async recuperarUltimaMedicion() {
        this.#simularFallo();
        let ultima = null;
        for (const m of this.mediciones) {
            if (ultima === null || this.#esPosterior(m, ultima)) {
                ultima = m;
            }
        }
        return ultima === null ? null : { ...ultima };
    }

    /*
     * --------------------------------------------------------------
     * Propósito: devuelve todas las mediciones simuladas.
     *
     * Diseño lógico:
     *     [Medicion] <-- recuperarMediciones() <--
     *
     * Parámetros: ninguno.
     * Retorno: Promise de [Medicion]. Lista vacía si no hay mediciones.
     * Errores: la Promise se rechaza con un Error si falla es true.
     * --------------------------------------------------------------
     */
    async recuperarMediciones() {
        this.#simularFallo();
        return this.mediciones.map((m) => ({ ...m }));
    }

    /*
     * --------------------------------------------------------------
     * Propósito: lanza el error simulado cuando falla es true.
     *
     * Diseño lógico:
     *     simularFallo() -->
     *
     * Parámetros: ninguno.
     * Retorno: ninguno.
     * Errores: Error("error simulado") si falla es true.
     * --------------------------------------------------------------
     */
    #simularFallo() {
        if (this.falla) {
            throw new Error("error simulado");
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: indica si una medición es posterior a otra (mayor
     *            fechaHora, o mayor sensorId si coinciden).
     *
     * Diseño lógico:
     *     a: Medicion, b: Medicion --> esPosterior() --> B
     *
     * Parámetros:
     *     a: Medicion. Medición candidata.
     *     b: Medicion. Medición con la que se compara.
     * Retorno: B. true si a es posterior a b.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    #esPosterior(a, b) {
        const instanteA = Date.parse(a.fechaHora);
        const instanteB = Date.parse(b.fechaHora);
        return instanteA > instanteB || (instanteA === instanteB && a.sensorId > b.sensorId);
    }
}

// En Node.js (tests) se exporta; en el navegador la clase queda global.
if (typeof module !== "undefined" && module.exports) {
    module.exports = LogicaNavegadorFake;
}

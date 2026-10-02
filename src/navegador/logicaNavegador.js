// =============================================================================
// logicaNavegador.js
//
// Descripción: lógica real del navegador (componente navegador). Obtiene las
//              mediciones llamando al API REST.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/navegador_design.md. Tiene la misma
//              interfaz que LogicaNavegadorFake. No accede al DOM ni valida
//              reglas de negocio. Sus operaciones son asíncronas.
// =============================================================================

/*
 * Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R,
 *              fechaHora: Text )
 */
class LogicaNavegador {

    /*
     * --------------------------------------------------------------
     * Propósito: crea la lógica del navegador apuntando a un servidor.
     *
     * Diseño lógico:
     *     urlBase: Text --> LogicaNavegador() -->
     *
     * Parámetros:
     *     urlBase: Text. Dirección del servidor, p. ej.
     *              "http://localhost:8080". Si está vacía, se usa el mismo
     *              origen desde el que se cargó la página.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    constructor(urlBase = "") {
        this.urlBase = urlBase.replace(/\/+$/, "");
    }

    /*
     * --------------------------------------------------------------
     * Propósito: obtiene la última medición llamando a
     *            GET /mediciones/ultima.
     *
     * Diseño lógico:
     *     Medicion <-- recuperarUltimaMedicion() <--
     *
     * Parámetros: ninguno.
     * Retorno: Promise de Medicion (código 200), o de null si no hay
     *          mediciones (código 404).
     * Errores: la Promise se rechaza con un Error ante cualquier otro
     *          código, fallo de red o respuesta que no sea una Medicion
     *          en JSON.
     * --------------------------------------------------------------
     */
    async recuperarUltimaMedicion() {
        const respuesta = await this.#pedir("/mediciones/ultima");
        if (respuesta.status === 404) {
            return null;
        }
        this.#exigirCodigo200(respuesta);
        const medicion = await this.#leerJSON(respuesta);
        if (medicion === null || typeof medicion !== "object" || Array.isArray(medicion)) {
            throw new Error("la respuesta del servidor no es una medición");
        }
        return medicion;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: obtiene todas las mediciones llamando a
     *            GET /mediciones.
     *
     * Diseño lógico:
     *     [Medicion] <-- recuperarMediciones() <--
     *
     * Parámetros: ninguno.
     * Retorno: Promise de [Medicion]. Lista vacía si no hay mediciones.
     * Errores: la Promise se rechaza con un Error ante un código distinto
     *          de 200, fallo de red o respuesta que no sea una lista JSON.
     * --------------------------------------------------------------
     */
    async recuperarMediciones() {
        const respuesta = await this.#pedir("/mediciones");
        this.#exigirCodigo200(respuesta);
        const mediciones = await this.#leerJSON(respuesta);
        if (!Array.isArray(mediciones)) {
            throw new Error("la respuesta del servidor no es una lista de mediciones");
        }
        return mediciones;
    }

    /*
     * --------------------------------------------------------------
     * Propósito: hace una petición GET al servidor sin usar la caché
     *            del navegador, para ver siempre los datos más recientes.
     *
     * Diseño lógico:
     *     ruta: Text --> pedir() --> respuesta
     *
     * Parámetros:
     *     ruta: Text. Ruta de la API, p. ej. "/mediciones".
     * Retorno: respuesta HTTP.
     * Errores: Error si no se puede conectar con el servidor.
     * --------------------------------------------------------------
     */
    async #pedir(ruta) {
        try {
            return await fetch(this.urlBase + ruta, { cache: "no-store" });
        } catch (error) {
            throw new Error("no se pudo conectar con el servidor");
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: comprueba que la respuesta tiene código 200.
     *
     * Diseño lógico:
     *     respuesta --> exigirCodigo200() -->
     *
     * Parámetros:
     *     respuesta: respuesta HTTP.
     * Retorno: ninguno.
     * Errores: Error si el código no es 200.
     * --------------------------------------------------------------
     */
    #exigirCodigo200(respuesta) {
        if (respuesta.status !== 200) {
            throw new Error(`el servidor respondió con el código ${respuesta.status}`);
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: lee el cuerpo de la respuesta como JSON.
     *
     * Diseño lógico:
     *     respuesta --> leerJSON() --> datos
     *
     * Parámetros:
     *     respuesta: respuesta HTTP.
     * Retorno: datos JSON.
     * Errores: Error si el cuerpo no es JSON válido.
     * --------------------------------------------------------------
     */
    async #leerJSON(respuesta) {
        try {
            return await respuesta.json();
        } catch (error) {
            throw new Error("la respuesta del servidor no es JSON válido");
        }
    }
}

// En Node.js (tests) se exporta; en el navegador la clase queda global.
if (typeof module !== "undefined" && module.exports) {
    module.exports = LogicaNavegador;
}

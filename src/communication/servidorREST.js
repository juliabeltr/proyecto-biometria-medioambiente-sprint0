// =============================================================================
// servidorREST.js
//
// Descripción: servidor REST (HTTP + JSON) que conecta los clientes con la
//              lógica de negocio (componente rest).
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/rest_design.md. Solo recibe peticiones,
//              llama a la lógica y devuelve JSON. No contiene lógica de
//              negocio ni accede a la base de datos. Además sirve los
//              ficheros estáticos de la interfaz web (ux, navegador y
//              navegador_fake) para que página y API compartan origen.
// =============================================================================

const express = require("express");
const path = require("path");

const CARPETAS_WEB = ["ux", "navegador", "navegador_fake"];
const CAMPOS_MEDICION = ["tipo", "valor", "latitud", "longitud", "fechaHora"];

/*
 * Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R,
 *              fechaHora: Text )
 */
class ServidorREST {

    /*
     * --------------------------------------------------------------
     * Propósito: crea el servidor, registra las tres rutas del diseño y,
     *            si se indica rutaWeb, sirve la interfaz web. La aplicación
     *            Express queda en this.app.
     *
     * Diseño lógico:
     *     logica: LogicaMediciones, rutaWeb: Text --> ServidorREST() -->
     *
     * Parámetros:
     *     logica: LogicaMediciones. Lógica de negocio a la que se llama.
     *     rutaWeb: Text. Carpeta que contiene ux, navegador y
     *              navegador_fake. Si no se indica, no se sirve ninguna
     *              página (caso de los tests de la API).
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    constructor(logica, rutaWeb) {
        this.logica = logica;
        this.app = express();
        this.app.use(express.json());
        this.app.post("/mediciones", (req, res) => this.postMediciones(req, res));
        this.app.get("/mediciones", (req, res) => this.getMediciones(req, res));
        this.app.get("/mediciones/ultima", (req, res) => this.getUltimaMedicion(req, res));
        if (rutaWeb) {
            this.#servirInterfazWeb(rutaWeb);
        }
        this.app.use((error, req, res, next) => this.#responderError(error, res));
    }

    /*
     * --------------------------------------------------------------
     * Propósito: ruta POST /mediciones. Comprueba la forma de la
     *            petición y pide a la lógica que guarde la medición.
     *
     * Diseño lógico:
     *     m: Medicion --> postMediciones() -->
     *       codigo: N, cuerpo: Text <--
     *
     * Parámetros:
     *     req: petición HTTP con el JSON de la medición en el cuerpo.
     *     res: respuesta HTTP.
     * Retorno: 201 { "guardada": true } si se guarda; 400 { "error" } si
     *          el cuerpo no tiene los cinco campos o la lógica rechaza los
     *          datos; 500 { "error" } si la lógica o la capa de datos fallan.
     * Errores: ninguno hacia fuera: los captura y responde 500.
     * --------------------------------------------------------------
     */
    postMediciones(req, res) {
        try {
            const faltan = this.#camposAusentes(req.body);
            if (faltan.length > 0) {
                res.status(400).json({ error: `faltan campos: ${faltan.join(", ")}` });
                return;
            }
            if (!this.logica.guardarMedicion(req.body)) {
                res.status(400).json({ error: "datos de medición no válidos" });
                return;
            }
            res.status(201).json({ guardada: true });
        } catch (error) {
            this.#responderError(error, res);
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: ruta GET /mediciones. Devuelve todas las mediciones.
     *
     * Diseño lógico:
     *     codigo: N, cuerpo: Text <-- getMediciones() <--
     *
     * Parámetros:
     *     req: petición HTTP.
     *     res: respuesta HTTP.
     * Retorno: 200 con la lista JSON de Medicion (vacía si no hay);
     *          500 { "error" } si la lógica o la capa de datos fallan.
     * Errores: ninguno hacia fuera: los captura y responde 500.
     * --------------------------------------------------------------
     */
    getMediciones(req, res) {
        try {
            res.status(200).json(this.logica.recuperarMediciones());
        } catch (error) {
            this.#responderError(error, res);
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: ruta GET /mediciones/ultima. Devuelve la última
     *            medición guardada.
     *
     * Diseño lógico:
     *     codigo: N, cuerpo: Text <-- getUltimaMedicion() <--
     *
     * Parámetros:
     *     req: petición HTTP.
     *     res: respuesta HTTP.
     * Retorno: 200 con el objeto Medicion; 404 { "error" } si no hay
     *          mediciones; 500 { "error" } si la lógica o la capa de
     *          datos fallan.
     * Errores: ninguno hacia fuera: los captura y responde 500.
     * --------------------------------------------------------------
     */
    getUltimaMedicion(req, res) {
        try {
            const ultima = this.logica.recuperarUltimaMedicion();
            if (ultima === null || ultima === undefined) {
                res.status(404).json({ error: "no hay mediciones" });
                return;
            }
            res.status(200).json(ultima);
        } catch (error) {
            this.#responderError(error, res);
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: sirve como ficheros estáticos solo las tres carpetas
     *            de la interfaz web. El código del servidor, de la
     *            lógica y de la base de datos nunca se publica.
     *
     * Diseño lógico:
     *     rutaWeb: Text --> servirInterfazWeb() -->
     *
     * Parámetros:
     *     rutaWeb: Text. Carpeta que contiene ux, navegador y
     *              navegador_fake.
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    #servirInterfazWeb(rutaWeb) {
        for (const carpeta of CARPETAS_WEB) {
            this.app.use(`/${carpeta}`, express.static(path.join(rutaWeb, carpeta)));
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: lista los campos obligatorios que no trae el cuerpo.
     *
     * Diseño lógico:
     *     cuerpo: Medicion --> camposAusentes() --> [Text]
     *
     * Parámetros:
     *     cuerpo: cuerpo JSON recibido (puede no ser un objeto).
     * Retorno: [Text]. Nombres de los campos ausentes; vacía si están todos.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    #camposAusentes(cuerpo) {
        if (cuerpo === null || typeof cuerpo !== "object" || Array.isArray(cuerpo)) {
            return [...CAMPOS_MEDICION];
        }
        return CAMPOS_MEDICION.filter(
            (campo) => cuerpo[campo] === undefined || cuerpo[campo] === null
        );
    }

    /*
     * --------------------------------------------------------------
     * Propósito: responde a un error sin revelar detalles internos.
     *            JSON mal formado da 400; cualquier otro fallo, 500.
     *
     * Diseño lógico:
     *     error: Text --> responderError() --> codigo: N, cuerpo: Text
     *
     * Parámetros:
     *     error: error capturado.
     *     res: respuesta HTTP.
     * Retorno: 400 o 500 con { "error": "<motivo genérico>" }.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    #responderError(error, res) {
        if (error && error.type === "entity.parse.failed") {
            res.status(400).json({ error: "JSON mal formado" });
            return;
        }
        console.error("Error interno del servidor REST:", error);
        res.status(500).json({ error: "error interno del servidor" });
    }
}

module.exports = ServidorREST;

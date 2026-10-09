// =============================================================================
// servidorREST.test.js
//
// Descripción: tests automáticos del componente rest (ServidorREST).
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Casos pedidos en doc/rest_design.md, con una lógica simulada
//              (sin base de datos real) y Supertest. Incluye los ficheros
//              estáticos de la interfaz web.
// =============================================================================

const path = require("path");
const request = require("supertest");
const ServidorREST = require("./servidorREST");

/*
 * --------------------------------------------------------------
 * Propósito: crea una Medicion válida de prueba, sustituyendo los
 *            campos que se indiquen.
 *
 * Diseño lógico:
 *     cambios: Medicion --> medicionDePrueba() --> Medicion
 * --------------------------------------------------------------
 */
function medicionDePrueba(cambios = {}) {
    return {
        tipo: "CO2",
        valor: 412.5,
        latitud: 38.96,
        longitud: -0.18,
        fechaHora: "2026-10-02T10:00:00Z",
        ...cambios,
    };
}

/*
 * --------------------------------------------------------------
 * Propósito: crea una lógica simulada con las tres operaciones de
 *            LogicaMediciones.
 *
 * Diseño lógico:
 *     logicaSimulada() --> LogicaMediciones
 * --------------------------------------------------------------
 */
function logicaSimulada() {
    return {
        guardarMedicion: jest.fn().mockReturnValue(true),
        recuperarUltimaMedicion: jest.fn().mockReturnValue(null),
        recuperarMediciones: jest.fn().mockReturnValue([]),
    };
}

describe("ServidorREST", () => {
    let logica;
    let app;

    beforeEach(() => {
        logica = logicaSimulada();
        app = new ServidorREST(logica).app;
        // Los errores 500 se registran en consola: se silencian en los tests.
        jest.spyOn(console, "error").mockImplementation(() => {});
    });

    afterEach(() => {
        jest.restoreAllMocks();
    });

    describe("POST /mediciones", () => {
        test("guarda una medición correcta: 201 { guardada: true }", async () => {
            const respuesta = await request(app).post("/mediciones").send(medicionDePrueba());

            expect(respuesta.status).toBe(201);
            expect(respuesta.body).toEqual({ guardada: true });
            expect(logica.guardarMedicion).toHaveBeenCalledWith(medicionDePrueba());
        });

        test("pasa el id a la lógica sin usarlo (la lógica lo ignora)", async () => {
            const respuesta = await request(app)
                .post("/mediciones")
                .send(medicionDePrueba({ id: 99 }));

            expect(respuesta.status).toBe(201);
        });

        test("JSON mal formado: 400 y no llama a la lógica", async () => {
            const respuesta = await request(app)
                .post("/mediciones")
                .set("Content-Type", "application/json")
                .send('{"tipo": "CO2", ');

            expect(respuesta.status).toBe(400);
            expect(respuesta.body).toEqual({ error: "JSON mal formado" });
            expect(logica.guardarMedicion).not.toHaveBeenCalled();
        });

        test.each(["tipo", "valor", "latitud", "longitud", "fechaHora"])(
            "falta el campo %s: 400 y no llama a la lógica",
            async (campo) => {
                const medicion = medicionDePrueba();
                delete medicion[campo];

                const respuesta = await request(app).post("/mediciones").send(medicion);

                expect(respuesta.status).toBe(400);
                expect(respuesta.body.error).toContain(campo);
                expect(logica.guardarMedicion).not.toHaveBeenCalled();
            }
        );

        test("cuerpo vacío: 400", async () => {
            const respuesta = await request(app).post("/mediciones");

            expect(respuesta.status).toBe(400);
            expect(logica.guardarMedicion).not.toHaveBeenCalled();
        });

        test("cuerpo que es una lista: 400", async () => {
            const respuesta = await request(app).post("/mediciones").send([medicionDePrueba()]);

            expect(respuesta.status).toBe(400);
            expect(logica.guardarMedicion).not.toHaveBeenCalled();
        });

        test("datos rechazados por la lógica (false): 400", async () => {
            logica.guardarMedicion.mockReturnValue(false);

            const respuesta = await request(app).post("/mediciones").send(medicionDePrueba());

            expect(respuesta.status).toBe(400);
            expect(respuesta.body).toHaveProperty("error");
        });

        test("fallo interno de la lógica: 500 sin detalles internos", async () => {
            logica.guardarMedicion.mockImplementation(() => {
                throw new Error("SELECT * FROM MEDICIONES falló en /srv/bd.sqlite");
            });

            const respuesta = await request(app).post("/mediciones").send(medicionDePrueba());

            expect(respuesta.status).toBe(500);
            expect(respuesta.body).toEqual({ error: "error interno del servidor" });
            expect(JSON.stringify(respuesta.body)).not.toContain("SELECT");
            expect(JSON.stringify(respuesta.body)).not.toContain("bd.sqlite");
        });
    });

    describe("GET /mediciones", () => {
        test("devuelve la lista de mediciones: 200", async () => {
            const todas = [
                { id: 1, ...medicionDePrueba() },
                { id: 2, ...medicionDePrueba({ tipo: "TEMP", valor: 21 }) },
            ];
            logica.recuperarMediciones.mockReturnValue(todas);

            const respuesta = await request(app).get("/mediciones");

            expect(respuesta.status).toBe(200);
            expect(respuesta.body).toEqual(todas);
        });

        test("lista vacía: 200 []", async () => {
            const respuesta = await request(app).get("/mediciones");

            expect(respuesta.status).toBe(200);
            expect(respuesta.body).toEqual([]);
        });

        test("fallo interno: 500 sin trazas", async () => {
            logica.recuperarMediciones.mockImplementation(() => {
                throw new Error("fallo con traza interna");
            });

            const respuesta = await request(app).get("/mediciones");

            expect(respuesta.status).toBe(500);
            expect(respuesta.body).toEqual({ error: "error interno del servidor" });
        });
    });

    describe("GET /mediciones/ultima", () => {
        test("devuelve la última medición: 200", async () => {
            const ultima = { id: 7, ...medicionDePrueba() };
            logica.recuperarUltimaMedicion.mockReturnValue(ultima);

            const respuesta = await request(app).get("/mediciones/ultima");

            expect(respuesta.status).toBe(200);
            expect(respuesta.body).toEqual(ultima);
        });

        test("sin mediciones: 404 con error", async () => {
            const respuesta = await request(app).get("/mediciones/ultima");

            expect(respuesta.status).toBe(404);
            expect(respuesta.body).toHaveProperty("error");
        });

        test("fallo interno: 500 sin trazas", async () => {
            logica.recuperarUltimaMedicion.mockImplementation(() => {
                throw new Error("fallo con traza interna");
            });

            const respuesta = await request(app).get("/mediciones/ultima");

            expect(respuesta.status).toBe(500);
            expect(respuesta.body).toEqual({ error: "error interno del servidor" });
        });
    });

    describe("ficheros estáticos de la interfaz web", () => {
        const rutaWeb = path.join(__dirname, "..");
        let appConWeb;

        beforeEach(() => {
            appConWeb = new ServidorREST(logica, rutaWeb).app;
        });

        test("con rutaWeb, GET /ux/ devuelve la página index.html", async () => {
            const respuesta = await request(appConWeb).get("/ux/");

            expect(respuesta.status).toBe(200);
            expect(respuesta.headers["content-type"]).toContain("text/html");
            expect(respuesta.text).toContain("Calidad del aire");
        });

        test("con rutaWeb, se sirven los ficheros de la interfaz y de la lógica del navegador", async () => {
            for (const ruta of [
                "/ux/controladorUX.js",
                "/ux/estilos.css",
                "/navegador/logicaNavegador.js",
                "/navegador_fake/logicaNavegadorFake.js",
            ]) {
                const respuesta = await request(appConWeb).get(ruta);

                expect(respuesta.status).toBe(200);
            }
        });

        test.each([
            "/bd/repositorioMediciones.js",
            "/logica/logicaMediciones.js",
            "/rest/servidorREST.js",
            "/rest/index.js",
        ])("con rutaWeb, NO se sirve el código del servidor: %s", async (ruta) => {
            const respuesta = await request(appConWeb).get(ruta);

            expect(respuesta.status).toBe(404);
            expect(respuesta.text).not.toContain("require(");
        });

        test("con rutaWeb, no se puede salir de las carpetas servidas", async () => {
            const respuesta = await request(appConWeb).get("/ux/%2e%2e/bd/repositorioMediciones.js");

            expect(respuesta.status).not.toBe(200);
            expect(respuesta.text).not.toContain("better-sqlite3");
        });

        test("sin rutaWeb, GET /ux/ devuelve 404", async () => {
            const respuesta = await request(app).get("/ux/");

            expect(respuesta.status).toBe(404);
        });

        test("servir la web no altera las rutas de la API", async () => {
            logica.recuperarUltimaMedicion.mockReturnValue({ id: 1, ...medicionDePrueba() });

            const ultima = await request(appConWeb).get("/mediciones/ultima");
            const guardar = await request(appConWeb).post("/mediciones").send(medicionDePrueba());

            expect(ultima.status).toBe(200);
            expect(guardar.status).toBe(201);
        });
    });
});

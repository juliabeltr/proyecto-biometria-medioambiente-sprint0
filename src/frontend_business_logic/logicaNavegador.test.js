// =============================================================================
// logicaNavegador.test.js
//
// Descripción: tests automáticos del componente navegador (LogicaNavegador).
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Casos pedidos en doc/navegador_design.md, con fetch simulado
//              (sin servidor real).
// =============================================================================

const LogicaNavegador = require("./logicaNavegador");

/*
 * --------------------------------------------------------------
 * Propósito: crea una Medicion de prueba.
 *
 * Diseño lógico:
 *     medicionDePrueba() --> Medicion
 * --------------------------------------------------------------
 */
function medicionDePrueba() {
    return {
        id: 3,
        tipo: "CO2",
        valor: 235,
        latitud: 38.96,
        longitud: -0.18,
        fechaHora: "2026-10-02T10:00:00Z",
    };
}

/*
 * --------------------------------------------------------------
 * Propósito: crea una respuesta HTTP simulada con un código y un
 *            cuerpo JSON (o un cuerpo que falla al leerlo).
 *
 * Diseño lógico:
 *     codigo: N, cuerpo: Text --> respuestaSimulada() --> respuesta
 * --------------------------------------------------------------
 */
function respuestaSimulada(codigo, cuerpo, jsonInvalido = false) {
    return {
        status: codigo,
        json: jsonInvalido
            ? () => Promise.reject(new SyntaxError("Unexpected token"))
            : () => Promise.resolve(cuerpo),
    };
}

describe("LogicaNavegador", () => {
    beforeEach(() => {
        global.fetch = jest.fn();
    });

    afterEach(() => {
        delete global.fetch;
    });

    describe("recuperarUltimaMedicion()", () => {
        test("código 200: devuelve la medición recibida", async () => {
            fetch.mockResolvedValue(respuestaSimulada(200, medicionDePrueba()));

            expect(await new LogicaNavegador().recuperarUltimaMedicion()).toEqual(medicionDePrueba());
        });

        test("código 404: devuelve null (no hay mediciones)", async () => {
            fetch.mockResolvedValue(respuestaSimulada(404, { error: "no hay mediciones" }));

            expect(await new LogicaNavegador().recuperarUltimaMedicion()).toBeNull();
        });

        test("código 500: se rechaza con un error", async () => {
            fetch.mockResolvedValue(respuestaSimulada(500, { error: "error interno del servidor" }));

            await expect(new LogicaNavegador().recuperarUltimaMedicion()).rejects.toThrow("código 500");
        });

        test("fallo de red: se rechaza con un error", async () => {
            fetch.mockRejectedValue(new TypeError("Failed to fetch"));

            await expect(new LogicaNavegador().recuperarUltimaMedicion()).rejects.toThrow(
                "no se pudo conectar con el servidor"
            );
        });

        test("JSON no válido: se rechaza con un error", async () => {
            fetch.mockResolvedValue(respuestaSimulada(200, null, true));

            await expect(new LogicaNavegador().recuperarUltimaMedicion()).rejects.toThrow(
                "no es JSON válido"
            );
        });

        test.each([["una lista", []], ["null", null], ["un texto", "hola"]])(
            "código 200 con %s en vez de una medición: se rechaza",
            async (_nombre, cuerpo) => {
                fetch.mockResolvedValue(respuestaSimulada(200, cuerpo));

                await expect(new LogicaNavegador().recuperarUltimaMedicion()).rejects.toThrow(
                    "no es una medición"
                );
            }
        );
    });

    describe("recuperarMediciones()", () => {
        test("código 200: devuelve la lista recibida", async () => {
            const lista = [medicionDePrueba(), { ...medicionDePrueba(), id: 4 }];
            fetch.mockResolvedValue(respuestaSimulada(200, lista));

            expect(await new LogicaNavegador().recuperarMediciones()).toEqual(lista);
        });

        test("código 200 con lista vacía: devuelve []", async () => {
            fetch.mockResolvedValue(respuestaSimulada(200, []));

            expect(await new LogicaNavegador().recuperarMediciones()).toEqual([]);
        });

        test("código 500: se rechaza con un error", async () => {
            fetch.mockResolvedValue(respuestaSimulada(500, { error: "error interno del servidor" }));

            await expect(new LogicaNavegador().recuperarMediciones()).rejects.toThrow("código 500");
        });

        test("fallo de red: se rechaza con un error", async () => {
            fetch.mockRejectedValue(new TypeError("Failed to fetch"));

            await expect(new LogicaNavegador().recuperarMediciones()).rejects.toThrow(
                "no se pudo conectar con el servidor"
            );
        });

        test("JSON no válido: se rechaza con un error", async () => {
            fetch.mockResolvedValue(respuestaSimulada(200, null, true));

            await expect(new LogicaNavegador().recuperarMediciones()).rejects.toThrow("no es JSON válido");
        });

        test("código 200 que no es una lista: se rechaza", async () => {
            fetch.mockResolvedValue(respuestaSimulada(200, medicionDePrueba()));

            await expect(new LogicaNavegador().recuperarMediciones()).rejects.toThrow(
                "no es una lista de mediciones"
            );
        });
    });

    describe("URL llamada", () => {
        test("con urlBase vacío usa la ruta relativa al mismo origen", async () => {
            fetch.mockResolvedValue(respuestaSimulada(200, medicionDePrueba()));

            await new LogicaNavegador().recuperarUltimaMedicion();

            expect(fetch.mock.calls[0][0]).toBe("/mediciones/ultima");
        });

        test("con urlBase indicado lo antepone a la ruta", async () => {
            fetch.mockResolvedValue(respuestaSimulada(200, []));

            await new LogicaNavegador("http://localhost:8080").recuperarMediciones();

            expect(fetch.mock.calls[0][0]).toBe("http://localhost:8080/mediciones");
        });

        test("ignora la barra final del urlBase", async () => {
            fetch.mockResolvedValue(respuestaSimulada(200, []));

            await new LogicaNavegador("http://localhost:8080/").recuperarMediciones();

            expect(fetch.mock.calls[0][0]).toBe("http://localhost:8080/mediciones");
        });
    });
});

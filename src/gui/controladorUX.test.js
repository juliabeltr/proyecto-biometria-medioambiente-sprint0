/**
 * @jest-environment jsdom
 */

// =============================================================================
// controladorUX.test.js
//
// Descripción: tests automáticos del componente ux (ControladorUX).
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Casos pedidos en doc/ux_design.md, con la lógica fake y el
//              index.html real cargado en jsdom (zona horaria fijada en UTC
//              por jest.global-setup.js).
// =============================================================================

const fs = require("fs");
const path = require("path");
const ControladorUX = require("./controladorUX");
const LogicaNavegadorFake = require("../navegador_fake/logicaNavegadorFake");

/*
 * --------------------------------------------------------------
 * Propósito: carga el index.html real en el documento de jsdom.
 *
 * Diseño lógico:
 *     cargarPantalla() -->
 * --------------------------------------------------------------
 */
function cargarPantalla() {
    const html = fs.readFileSync(path.join(__dirname, "index.html"), "utf8");
    document.documentElement.innerHTML = html;
}

/*
 * --------------------------------------------------------------
 * Propósito: crea una Medicion de prueba, sustituyendo los campos
 *            que se indiquen.
 *
 * Diseño lógico:
 *     cambios: Medicion --> medicionDePrueba() --> Medicion
 * --------------------------------------------------------------
 */
function medicionDePrueba(cambios = {}) {
    return {
        id: 1,
        tipo: "CO2",
        valor: 412,
        latitud: 38.96,
        longitud: -0.18,
        fechaHora: "2026-10-02T10:05:00Z",
        ...cambios,
    };
}

/*
 * --------------------------------------------------------------
 * Propósito: devuelve el texto de un elemento de la pantalla.
 *
 * Diseño lógico:
 *     id: Text --> texto() --> Text
 * --------------------------------------------------------------
 */
function texto(id) {
    return document.getElementById(id).textContent;
}

describe("ControladorUX", () => {
    beforeEach(() => {
        cargarPantalla();
    });

    describe("formatearMedicion()", () => {
        test("separa tipo, valor, fecha y hora", () => {
            expect(ControladorUX.formatearMedicion(medicionDePrueba())).toEqual({
                tipo: "CO2",
                valor: "412",
                fecha: "02/10/2026",
                hora: "10:05",
            });
        });

        test("conserva los decimales del valor", () => {
            expect(ControladorUX.formatearMedicion(medicionDePrueba({ valor: 21.5 })).valor).toBe("21.5");
        });

        test("añade ceros a día, mes y hora de una cifra", () => {
            const resultado = ControladorUX.formatearMedicion(
                medicionDePrueba({ fechaHora: "2026-03-04T05:06:00Z" })
            );

            expect(resultado.fecha).toBe("04/03/2026");
            expect(resultado.hora).toBe("05:06");
        });

        test("convierte un desfase horario a la zona del navegador", () => {
            const resultado = ControladorUX.formatearMedicion(
                medicionDePrueba({ fechaHora: "2026-10-02T10:05:00+02:00" })
            );

            expect(resultado.hora).toBe("08:05");
        });

        test("lanza un error si fechaHora no es válida", () => {
            expect(() => ControladorUX.formatearMedicion(medicionDePrueba({ fechaHora: "ayer" }))).toThrow(
                "fechaHora no válida"
            );
        });
    });

    describe("iniciar() con datos", () => {
        test("muestra tipo, valor, fecha y hora de la última medición", async () => {
            const logica = new LogicaNavegadorFake([medicionDePrueba()]);

            await new ControladorUX(logica).iniciar();

            expect(texto("dato-tipo")).toBe("CO2");
            expect(texto("dato-valor")).toBe("412");
            expect(texto("dato-fecha")).toBe("02/10/2026");
            expect(texto("dato-hora")).toBe("10:05");
            expect(document.getElementById("tarjeta-medicion").hidden).toBe(false);
            expect(texto("mensaje-estado")).toBe("");
        });

        test("con los datos de ejemplo muestra la medición de CO2", async () => {
            await new ControladorUX(new LogicaNavegadorFake()).iniciar();

            expect(texto("dato-tipo")).toBe("CO2");
        });
    });

    describe("estado sin datos", () => {
        test("muestra el mensaje y oculta la tarjeta", async () => {
            await new ControladorUX(new LogicaNavegadorFake([])).iniciar();

            expect(texto("mensaje-estado")).toBe("Todavía no hay mediciones");
            expect(document.getElementById("tarjeta-medicion").hidden).toBe(true);
        });
    });

    describe("estado de error", () => {
        test("muestra el mensaje de error y oculta la tarjeta", async () => {
            await new ControladorUX(new LogicaNavegadorFake([medicionDePrueba()], true)).iniciar();

            expect(texto("mensaje-estado")).toBe("No se pudo obtener la medición");
            expect(document.getElementById("mensaje-estado").classList.contains("estado--error")).toBe(true);
            expect(document.getElementById("tarjeta-medicion").hidden).toBe(true);
        });

        test("un fechaHora inválido también se muestra como error", async () => {
            const logica = new LogicaNavegadorFake([medicionDePrueba({ fechaHora: "ayer" })]);

            await new ControladorUX(logica).iniciar();

            expect(texto("mensaje-estado")).toBe("No se pudo obtener la medición");
        });

        test("el botón vuelve a estar activo tras el error", async () => {
            await new ControladorUX(new LogicaNavegadorFake([medicionDePrueba()], true)).iniciar();

            expect(document.getElementById("boton-actualizar").disabled).toBe(false);
        });
    });

    describe("estado cargando", () => {
        test("muestra 'Cargando...' y desactiva el botón mientras espera", async () => {
            let resolver;
            const logica = {
                recuperarUltimaMedicion: () => new Promise((resolve) => (resolver = resolve)),
            };

            const carga = new ControladorUX(logica).actualizar();

            expect(texto("mensaje-estado")).toBe("Cargando...");
            expect(document.getElementById("boton-actualizar").disabled).toBe(true);
            expect(document.getElementById("tarjeta-medicion").hidden).toBe(true);

            resolver(medicionDePrueba());
            await carga;

            expect(document.getElementById("boton-actualizar").disabled).toBe(false);
            expect(document.getElementById("tarjeta-medicion").hidden).toBe(false);
        });
    });

    describe("botón Actualizar", () => {
        test("pide de nuevo la última medición y muestra la nueva", async () => {
            const logica = {
                recuperarUltimaMedicion: jest
                    .fn()
                    .mockResolvedValueOnce(medicionDePrueba({ valor: 100 }))
                    .mockResolvedValueOnce(medicionDePrueba({ valor: 250 })),
            };
            const controlador = new ControladorUX(logica);
            await controlador.iniciar();
            expect(texto("dato-valor")).toBe("100");

            document.getElementById("boton-actualizar").click();
            await Promise.resolve();
            await new Promise((resolve) => setTimeout(resolve, 0));

            expect(logica.recuperarUltimaMedicion).toHaveBeenCalledTimes(2);
            expect(texto("dato-valor")).toBe("250");
        });

        test("se recupera de un error al volver a actualizar", async () => {
            const logica = {
                recuperarUltimaMedicion: jest
                    .fn()
                    .mockRejectedValueOnce(new Error("fallo"))
                    .mockResolvedValueOnce(medicionDePrueba({ valor: 77 })),
            };
            const controlador = new ControladorUX(logica);
            await controlador.iniciar();
            expect(texto("mensaje-estado")).toBe("No se pudo obtener la medición");

            await controlador.actualizar();

            expect(texto("dato-valor")).toBe("77");
            expect(document.getElementById("mensaje-estado").classList.contains("estado--error")).toBe(false);
        });

        test("pasa de datos a sin datos si desaparecen las mediciones", async () => {
            const logica = {
                recuperarUltimaMedicion: jest
                    .fn()
                    .mockResolvedValueOnce(medicionDePrueba())
                    .mockResolvedValueOnce(null),
            };
            const controlador = new ControladorUX(logica);
            await controlador.iniciar();
            await controlador.actualizar();

            expect(texto("mensaje-estado")).toBe("Todavía no hay mediciones");
            expect(document.getElementById("tarjeta-medicion").hidden).toBe(true);
        });
    });
});

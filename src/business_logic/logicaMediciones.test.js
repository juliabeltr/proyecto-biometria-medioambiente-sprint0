// =============================================================================
// logicaMediciones.test.js
//
// Descripción: tests automáticos del componente logica (LogicaMediciones).
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Casos pedidos en doc/logica_design.md, con un repositorio
//              simulado (sin base de datos real).
// =============================================================================

const LogicaMediciones = require("./logicaMediciones");

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
 * Propósito: crea un repositorio simulado con las tres operaciones
 *            de RepositorioMediciones.
 *
 * Diseño lógico:
 *     repositorioSimulado() --> RepositorioMediciones
 * --------------------------------------------------------------
 */
function repositorioSimulado() {
    return {
        insertar: jest.fn().mockReturnValue(1),
        recuperarUltima: jest.fn().mockReturnValue(null),
        recuperarTodas: jest.fn().mockReturnValue([]),
    };
}

describe("LogicaMediciones", () => {
    let repositorio;
    let logica;

    beforeEach(() => {
        repositorio = repositorioSimulado();
        logica = new LogicaMediciones(repositorio);
    });

    describe("guardarMedicion() con datos válidos", () => {
        test("devuelve true y llama a insertar()", () => {
            expect(logica.guardarMedicion(medicionDePrueba())).toBe(true);
            expect(repositorio.insertar).toHaveBeenCalledTimes(1);
            expect(repositorio.insertar).toHaveBeenCalledWith(medicionDePrueba());
        });

        test.each(["CO2", "TEMP", "RUIDO"])("acepta el tipo %s", (tipo) => {
            expect(logica.guardarMedicion(medicionDePrueba({ tipo }))).toBe(true);
        });

        test("ignora el id recibido y no lo pasa a la capa de datos", () => {
            logica.guardarMedicion(medicionDePrueba({ id: 999 }));

            expect(repositorio.insertar.mock.calls[0][0]).not.toHaveProperty("id");
        });

        test.each([
            ["latitud -90", { latitud: -90 }],
            ["latitud 90", { latitud: 90 }],
            ["longitud -180", { longitud: -180 }],
            ["longitud 180", { longitud: 180 }],
        ])("acepta el límite exacto: %s", (_nombre, cambios) => {
            expect(logica.guardarMedicion(medicionDePrueba(cambios))).toBe(true);
        });

        test.each([
            "2026-10-02T10:00:00Z",
            "2026-10-02T10:00:00.123Z",
            "2026-10-02T10:00:00+02:00",
            "2028-02-29T00:00:00Z",
        ])("acepta la fecha ISO 8601 %s", (fechaHora) => {
            expect(logica.guardarMedicion(medicionDePrueba({ fechaHora }))).toBe(true);
        });
    });

    describe("guardarMedicion() con datos inválidos", () => {
        test.each([
            ["tipo desconocido", { tipo: "OZONO" }],
            ["tipo en minúsculas", { tipo: "co2" }],
            ["valor no numérico", { valor: "412" }],
            ["valor NaN", { valor: NaN }],
            ["valor infinito", { valor: Infinity }],
            ["latitud menor que -90", { latitud: -90.1 }],
            ["latitud mayor que 90", { latitud: 90.1 }],
            ["longitud menor que -180", { longitud: -180.1 }],
            ["longitud mayor que 180", { longitud: 180.1 }],
            ["latitud no numérica", { latitud: "38.9" }],
            ["fecha con texto cualquiera", { fechaHora: "ayer" }],
            ["fecha sin hora", { fechaHora: "2026-10-02" }],
            ["fecha sin formato ISO", { fechaHora: "2026-10-02 10:00:00" }],
            ["mes inexistente", { fechaHora: "2026-13-01T10:00:00Z" }],
            ["30 de febrero", { fechaHora: "2026-02-30T10:00:00Z" }],
            ["29 de febrero en año no bisiesto", { fechaHora: "2026-02-29T10:00:00Z" }],
            ["hora inexistente", { fechaHora: "2026-10-02T25:00:00Z" }],
            ["fecha no textual", { fechaHora: 1790000000 }],
        ])("devuelve false y no llama a insertar(): %s", (_nombre, cambios) => {
            expect(logica.guardarMedicion(medicionDePrueba(cambios))).toBe(false);
            expect(repositorio.insertar).not.toHaveBeenCalled();
        });

        test.each(["tipo", "valor", "latitud", "longitud", "fechaHora"])(
            "devuelve false si falta el campo %s",
            (campo) => {
                const medicion = medicionDePrueba();
                delete medicion[campo];

                expect(logica.guardarMedicion(medicion)).toBe(false);
                expect(repositorio.insertar).not.toHaveBeenCalled();
            }
        );

        test.each([null, undefined, "medición", 42])(
            "devuelve false si la medición es %p",
            (medicion) => {
                expect(logica.guardarMedicion(medicion)).toBe(false);
                expect(repositorio.insertar).not.toHaveBeenCalled();
            }
        );
    });

    describe("recuperarUltimaMedicion()", () => {
        test("devuelve la última medición", () => {
            const ultima = { id: 7, ...medicionDePrueba() };
            repositorio.recuperarUltima.mockReturnValue(ultima);

            expect(logica.recuperarUltimaMedicion()).toEqual(ultima);
        });

        test("devuelve null si no hay mediciones", () => {
            expect(logica.recuperarUltimaMedicion()).toBeNull();
        });
    });

    describe("recuperarMediciones()", () => {
        test("devuelve varias mediciones", () => {
            const todas = [
                { id: 1, ...medicionDePrueba() },
                { id: 2, ...medicionDePrueba({ tipo: "TEMP", valor: 21 }) },
            ];
            repositorio.recuperarTodas.mockReturnValue(todas);

            expect(logica.recuperarMediciones()).toEqual(todas);
        });

        test("devuelve lista vacía si no hay mediciones", () => {
            expect(logica.recuperarMediciones()).toEqual([]);
        });
    });

    describe("errores de la capa de datos", () => {
        const fallo = new Error("fallo de la capa de datos");

        test("guardarMedicion() propaga el error", () => {
            repositorio.insertar.mockImplementation(() => {
                throw fallo;
            });

            expect(() => logica.guardarMedicion(medicionDePrueba())).toThrow(fallo);
        });

        test("recuperarUltimaMedicion() propaga el error", () => {
            repositorio.recuperarUltima.mockImplementation(() => {
                throw fallo;
            });

            expect(() => logica.recuperarUltimaMedicion()).toThrow(fallo);
        });

        test("recuperarMediciones() propaga el error", () => {
            repositorio.recuperarTodas.mockImplementation(() => {
                throw fallo;
            });

            expect(() => logica.recuperarMediciones()).toThrow(fallo);
        });
    });
});

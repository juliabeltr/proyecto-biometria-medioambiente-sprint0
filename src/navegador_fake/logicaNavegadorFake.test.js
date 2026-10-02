// =============================================================================
// logicaNavegadorFake.test.js
//
// Descripción: tests automáticos del componente navegador_fake.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Casos pedidos en doc/navegador_fake_design.md.
// =============================================================================

const LogicaNavegadorFake = require("./logicaNavegadorFake");

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
        valor: 400,
        latitud: 38.96,
        longitud: -0.18,
        fechaHora: "2026-10-02T10:00:00Z",
        ...cambios,
    };
}

describe("LogicaNavegadorFake", () => {
    describe("recuperarUltimaMedicion()", () => {
        test("devuelve la medición de mayor fechaHora", async () => {
            const logica = new LogicaNavegadorFake([
                medicionDePrueba({ id: 1, valor: 1, fechaHora: "2026-10-02T09:00:00Z" }),
                medicionDePrueba({ id: 2, valor: 2, fechaHora: "2026-10-02T12:00:00Z" }),
                medicionDePrueba({ id: 3, valor: 3, fechaHora: "2026-10-02T10:00:00Z" }),
            ]);

            expect((await logica.recuperarUltimaMedicion()).valor).toBe(2);
        });

        test("desempata por mayor id si la fechaHora coincide", async () => {
            const logica = new LogicaNavegadorFake([
                medicionDePrueba({ id: 5, valor: 1 }),
                medicionDePrueba({ id: 9, valor: 2 }),
                medicionDePrueba({ id: 7, valor: 3 }),
            ]);

            expect((await logica.recuperarUltimaMedicion()).id).toBe(9);
        });

        test("compara instantes reales aunque las zonas horarias difieran", async () => {
            const logica = new LogicaNavegadorFake([
                medicionDePrueba({ id: 1, valor: 1, fechaHora: "2026-10-02T10:00:00Z" }),
                medicionDePrueba({ id: 2, valor: 2, fechaHora: "2026-10-02T11:30:00+02:00" }),
            ]);

            // 11:30+02:00 son las 09:30Z, anterior a las 10:00Z
            expect((await logica.recuperarUltimaMedicion()).valor).toBe(1);
        });

        test("devuelve null si no hay mediciones", async () => {
            const logica = new LogicaNavegadorFake([]);

            expect(await logica.recuperarUltimaMedicion()).toBeNull();
        });

        test("devuelve una copia: modificarla no altera los datos", async () => {
            const logica = new LogicaNavegadorFake([medicionDePrueba({ valor: 10 })]);

            const ultima = await logica.recuperarUltimaMedicion();
            ultima.valor = 999;

            expect((await logica.recuperarUltimaMedicion()).valor).toBe(10);
        });
    });

    describe("recuperarMediciones()", () => {
        test("devuelve varias mediciones", async () => {
            const datos = [medicionDePrueba({ id: 1 }), medicionDePrueba({ id: 2 })];
            const logica = new LogicaNavegadorFake(datos);

            expect(await logica.recuperarMediciones()).toEqual(datos);
        });

        test("devuelve lista vacía si no hay mediciones", async () => {
            const logica = new LogicaNavegadorFake([]);

            expect(await logica.recuperarMediciones()).toEqual([]);
        });
    });

    describe("datos de ejemplo", () => {
        test("sin lista, usa un conjunto de ejemplo con una medición de CO2", async () => {
            const logica = new LogicaNavegadorFake();

            const todas = await logica.recuperarMediciones();

            expect(todas.length).toBeGreaterThan(0);
            expect(todas.some((m) => m.tipo === "CO2")).toBe(true);
        });

        test("los datos de ejemplo tienen la forma de Medicion", async () => {
            const logica = new LogicaNavegadorFake();

            for (const m of await logica.recuperarMediciones()) {
                expect(Object.keys(m).sort()).toEqual(
                    ["fechaHora", "id", "latitud", "longitud", "tipo", "valor"]
                );
                expect(Number.isNaN(Date.parse(m.fechaHora))).toBe(false);
            }
        });

        test("la última medición de ejemplo es la de CO2", async () => {
            const logica = new LogicaNavegadorFake();

            expect((await logica.recuperarUltimaMedicion()).tipo).toBe("CO2");
        });
    });

    describe("error simulado (falla = true)", () => {
        test("recuperarUltimaMedicion() se rechaza", async () => {
            const logica = new LogicaNavegadorFake([medicionDePrueba()], true);

            await expect(logica.recuperarUltimaMedicion()).rejects.toThrow("error simulado");
        });

        test("recuperarMediciones() se rechaza", async () => {
            const logica = new LogicaNavegadorFake([medicionDePrueba()], true);

            await expect(logica.recuperarMediciones()).rejects.toThrow("error simulado");
        });
    });
});

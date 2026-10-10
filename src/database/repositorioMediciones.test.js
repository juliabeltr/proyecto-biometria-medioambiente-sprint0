// =============================================================================
// repositorioMediciones.test.js
//
// Descripción: tests automáticos del componente database (RepositorioMediciones).
// Autor:       Júlia Beltrán Girbés
// Fecha:       02/10/2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Casos pedidos en doc/database_design.md, con BD en memoria.
// =============================================================================

const RepositorioMediciones = require("./repositorioMediciones");

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
        tipo: "CO2",
        valor: 412.5,
        latitud: 38.96,
        longitud: -0.18,
        fechaHora: "2026-10-02T10:00:00Z",
        ...cambios,
    };
}

describe("RepositorioMediciones", () => {
    let repositorio;

    beforeEach(() => {
        repositorio = new RepositorioMediciones(":memory:");
    });

    afterEach(() => {
        if (repositorio.conexion.open) {
            repositorio.conexion.close();
        }
    });

    describe("insertar()", () => {
        test("guarda una medición y devuelve su id", () => {
            const id = repositorio.insertar(medicionDePrueba());

            expect(id).toBe(1);
            expect(repositorio.recuperarTodas()).toEqual([
                { id: 1, ...medicionDePrueba() },
            ]);
        });

        test("asigna ids autoincrementales y distintos", () => {
            const id1 = repositorio.insertar(medicionDePrueba());
            const id2 = repositorio.insertar(medicionDePrueba());

            expect(id2).toBeGreaterThan(id1);
        });

        test("ignora el id recibido: lo asigna la base de datos", () => {
            const id = repositorio.insertar(medicionDePrueba({ id: 999 }));

            expect(id).toBe(1);
        });
    });

    describe("recuperarUltima()", () => {
        test("devuelve la medición de mayor fechaHora", () => {
            repositorio.insertar(medicionDePrueba({ fechaHora: "2026-10-02T12:00:00Z", valor: 2 }));
            repositorio.insertar(medicionDePrueba({ fechaHora: "2026-10-02T09:00:00Z", valor: 1 }));

            expect(repositorio.recuperarUltima().valor).toBe(2);
        });

        test("desempata por mayor id si la fechaHora coincide", () => {
            repositorio.insertar(medicionDePrueba({ valor: 1 }));
            const idUltimo = repositorio.insertar(medicionDePrueba({ valor: 2 }));

            const ultima = repositorio.recuperarUltima();

            expect(ultima.id).toBe(idUltimo);
            expect(ultima.valor).toBe(2);
        });

        test("devuelve null si no hay mediciones", () => {
            expect(repositorio.recuperarUltima()).toBeNull();
        });
    });

    describe("recuperarTodas()", () => {
        test("devuelve varias mediciones", () => {
            repositorio.insertar(medicionDePrueba({ valor: 1 }));
            repositorio.insertar(medicionDePrueba({ valor: 2 }));
            repositorio.insertar(medicionDePrueba({ valor: 3 }));

            const todas = repositorio.recuperarTodas();

            expect(todas).toHaveLength(3);
            expect(todas.map((m) => m.valor)).toEqual([1, 2, 3]);
        });

        test("devuelve lista vacía si no hay mediciones", () => {
            expect(repositorio.recuperarTodas()).toEqual([]);
        });
    });

    describe("datos inválidos", () => {
        test.each(["tipo", "valor", "latitud", "longitud", "fechaHora"])(
            "rechaza una medición sin el campo %s",
            (campo) => {
                const medicion = medicionDePrueba();
                delete medicion[campo];

                expect(() => repositorio.insertar(medicion)).toThrow(
                    `falta el campo ${campo}`
                );
                expect(repositorio.recuperarTodas()).toEqual([]);
            }
        );

        test("rechaza un campo con valor null", () => {
            expect(() => repositorio.insertar(medicionDePrueba({ valor: null }))).toThrow(
                "falta el campo valor"
            );
        });

        test("rechaza una medición inexistente", () => {
            expect(() => repositorio.insertar(null)).toThrow("no se ha recibido");
            expect(() => repositorio.insertar(undefined)).toThrow("no se ha recibido");
        });
    });

    describe("errores de base de datos", () => {
        beforeEach(() => {
            repositorio.conexion.close();
        });

        test("insertar() lanza un error claro sin exponer SQL", () => {
            expect(() => repositorio.insertar(medicionDePrueba())).toThrow(
                "Error de base de datos al insertar la medición"
            );
        });

        test("recuperarUltima() lanza un error claro", () => {
            expect(() => repositorio.recuperarUltima()).toThrow(
                "Error de base de datos al recuperar la última medición"
            );
        });

        test("recuperarTodas() lanza un error claro", () => {
            expect(() => repositorio.recuperarTodas()).toThrow(
                "Error de base de datos al recuperar las mediciones"
            );
        });

        test("el constructor falla con una ruta inaccesible", () => {
            expect(() => new RepositorioMediciones("/ruta/que/no/existe/bd.sqlite")).toThrow(
                "No se pudo abrir la base de datos"
            );
        });
    });
});

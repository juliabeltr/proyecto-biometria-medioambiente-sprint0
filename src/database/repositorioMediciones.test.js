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
        sensorId: "AA:BB:CC:DD:EE:01",
        tipo: "CO2",
        valor: 412.5,
        ubicacion: { latitud: 38.96, longitud: -0.18 },
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
        test("guarda una medición y devuelve true", () => {
            const resultado = repositorio.insertar(medicionDePrueba());

            expect(resultado).toBe(true);
            expect(repositorio.recuperarTodas()).toEqual([medicionDePrueba()]);
        });

        test("la clave primaria es (sensorId, fechaHora)", () => {
            const claves = repositorio.conexion
                .prepare("SELECT name FROM pragma_table_info('MEDICIONES') WHERE pk > 0 ORDER BY pk")
                .all()
                .map((c) => c.name);

            expect(claves).toEqual(["sensorId", "fechaHora"]);
        });

        test("no guarda otra medición del mismo sensor en el mismo instante y devuelve false", () => {
            repositorio.insertar(medicionDePrueba({ valor: 1 }));

            expect(repositorio.insertar(medicionDePrueba({ valor: 2 }))).toBe(false);
            expect(repositorio.recuperarTodas().map((m) => m.valor)).toEqual([1]);
        });

        test("guarda mediciones del mismo instante si son de sensores distintos", () => {
            expect(repositorio.insertar(medicionDePrueba({ sensorId: "sensor-A" }))).toBe(true);
            expect(repositorio.insertar(medicionDePrueba({ sensorId: "sensor-B" }))).toBe(true);
            expect(repositorio.recuperarTodas()).toHaveLength(2);
        });

        test("guarda mediciones del mismo sensor en instantes distintos", () => {
            expect(repositorio.insertar(medicionDePrueba({ fechaHora: "2026-10-02T10:00:00Z" }))).toBe(true);
            expect(repositorio.insertar(medicionDePrueba({ fechaHora: "2026-10-02T10:00:01Z" }))).toBe(true);
            expect(repositorio.recuperarTodas()).toHaveLength(2);
        });

        test("guarda la ubicación en una sola columna POINT, con formato POINT(longitud latitud)", () => {
            repositorio.insertar(medicionDePrueba());

            const fila = repositorio.conexion.prepare("SELECT ubicacion FROM MEDICIONES").get();
            expect(fila.ubicacion).toBe("POINT(-0.18 38.96)");
        });

        test("recupera idénticos el sensorId y la ubicación, también con coordenadas negativas", () => {
            const ubicacion = { latitud: -33.8688, longitud: -151.2093 };
            repositorio.insertar(medicionDePrueba({ sensorId: "sensor-7", ubicacion }));

            const [medicion] = repositorio.recuperarTodas();
            expect(medicion.sensorId).toBe("sensor-7");
            expect(medicion.ubicacion).toEqual(ubicacion);
            expect(repositorio.recuperarUltima().ubicacion).toEqual(ubicacion);
        });

    });

    describe("recuperarUltima()", () => {
        test("devuelve la medición de mayor fechaHora", () => {
            repositorio.insertar(medicionDePrueba({ fechaHora: "2026-10-02T12:00:00Z", valor: 2 }));
            repositorio.insertar(medicionDePrueba({ fechaHora: "2026-10-02T09:00:00Z", valor: 1 }));

            expect(repositorio.recuperarUltima().valor).toBe(2);
        });

        test("desempata por mayor sensorId si la fechaHora coincide", () => {
            repositorio.insertar(medicionDePrueba({ sensorId: "sensor-A", valor: 1 }));
            repositorio.insertar(medicionDePrueba({ sensorId: "sensor-C", valor: 3 }));
            repositorio.insertar(medicionDePrueba({ sensorId: "sensor-B", valor: 2 }));

            const ultima = repositorio.recuperarUltima();

            expect(ultima.sensorId).toBe("sensor-C");
            expect(ultima.valor).toBe(3);
        });

        test("devuelve null si no hay mediciones", () => {
            expect(repositorio.recuperarUltima()).toBeNull();
        });
    });

    describe("recuperarTodas()", () => {
        test("devuelve varias mediciones", () => {
            repositorio.insertar(medicionDePrueba({ valor: 1, fechaHora: "2026-10-02T10:00:01Z" }));
            repositorio.insertar(medicionDePrueba({ valor: 2, fechaHora: "2026-10-02T10:00:02Z" }));
            repositorio.insertar(medicionDePrueba({ valor: 3, fechaHora: "2026-10-02T10:00:03Z" }));

            const todas = repositorio.recuperarTodas();

            expect(todas).toHaveLength(3);
            expect(todas.map((m) => m.valor)).toEqual([1, 2, 3]);
        });

        test("devuelve lista vacía si no hay mediciones", () => {
            expect(repositorio.recuperarTodas()).toEqual([]);
        });
    });

    describe("datos inválidos", () => {
        test.each(["sensorId", "tipo", "valor", "ubicacion", "fechaHora"])(
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

        test.each(["latitud", "longitud"])(
            "rechaza una ubicación sin %s",
            (campo) => {
                const ubicacion = { latitud: 38.96, longitud: -0.18 };
                delete ubicacion[campo];

                expect(() => repositorio.insertar(medicionDePrueba({ ubicacion }))).toThrow(
                    "falta la latitud o la longitud"
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

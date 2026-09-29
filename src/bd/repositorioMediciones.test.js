'use strict';

const RepositorioMediciones = require('./repositorioMediciones');

const CAMPOS_OBLIGATORIOS = ['tipo', 'valor', 'latitud', 'longitud', 'fechaHora'];

/**
 * Propósito: construir una medición válida para los tests, sobrescribiendo
 * los campos indicados.
 *
 * Diseño lógico:
 * --------------------
 * cambios: Medicion --> medicionValida() --> m: Medicion
 * --------------------
 *
 * @param {Object} [cambios] Campos a sobrescribir.
 * @returns {Medicion} Medición con todos los campos informados.
 * @throws {Error} No lanza errores.
 */
function medicionValida(cambios = {}) {
  return {
    id: 0,
    tipo: 'NO2',
    valor: 42.5,
    latitud: 39.4699,
    longitud: -0.3763,
    fechaHora: '2026-09-29T10:00:00Z',
    ...cambios,
  };
}

describe('RepositorioMediciones', () => {
  let repositorio;

  beforeEach(() => {
    repositorio = new RepositorioMediciones(':memory:');
  });

  describe('insertar', () => {
    test('guarda una medición y devuelve su id', () => {
      const id = repositorio.insertar(medicionValida());

      expect(id).toBe(1);
      expect(repositorio.recuperarTodas()).toEqual([medicionValida({ id })]);
    });

    test('ignora el id de la medición recibida', () => {
      const id = repositorio.insertar(medicionValida({ id: 999 }));

      expect(id).toBe(1);
    });

    test('asigna ids autoincrementales y distintos', () => {
      const id1 = repositorio.insertar(medicionValida());
      const id2 = repositorio.insertar(medicionValida());
      const id3 = repositorio.insertar(medicionValida());

      expect([id1, id2, id3]).toEqual([1, 2, 3]);
      expect(new Set([id1, id2, id3]).size).toBe(3);
    });
  });

  describe('recuperarUltima', () => {
    test('devuelve la medición de mayor fechaHora', () => {
      repositorio.insertar(medicionValida({ fechaHora: '2026-09-29T10:00:00Z' }));
      repositorio.insertar(medicionValida({ fechaHora: '2026-09-29T12:00:00Z', tipo: 'O3' }));
      repositorio.insertar(medicionValida({ fechaHora: '2026-09-29T11:00:00Z' }));

      const ultima = repositorio.recuperarUltima();

      expect(ultima.tipo).toBe('O3');
      expect(ultima.fechaHora).toBe('2026-09-29T12:00:00Z');
    });

    test('desempata por mayor id si la fechaHora coincide', () => {
      const fechaHora = '2026-09-29T10:00:00Z';
      repositorio.insertar(medicionValida({ fechaHora, valor: 1 }));
      const idUltimo = repositorio.insertar(medicionValida({ fechaHora, valor: 2 }));

      const ultima = repositorio.recuperarUltima();

      expect(ultima.id).toBe(idUltimo);
      expect(ultima.valor).toBe(2);
    });

    test('devuelve null si no hay mediciones', () => {
      expect(repositorio.recuperarUltima()).toBeNull();
    });
  });

  describe('recuperarTodas', () => {
    test('devuelve todas las mediciones guardadas', () => {
      const id1 = repositorio.insertar(medicionValida({ tipo: 'NO2' }));
      const id2 = repositorio.insertar(medicionValida({ tipo: 'O3', valor: 80 }));

      expect(repositorio.recuperarTodas()).toEqual([
        medicionValida({ id: id1, tipo: 'NO2' }),
        medicionValida({ id: id2, tipo: 'O3', valor: 80 }),
      ]);
    });

    test('devuelve una lista vacía si no hay mediciones', () => {
      expect(repositorio.recuperarTodas()).toEqual([]);
    });
  });

  describe('datos inválidos', () => {
    describe.each(CAMPOS_OBLIGATORIOS)('campo %s', (campo) => {
      test('se rechaza si es null', () => {
        expect(() => repositorio.insertar(medicionValida({ [campo]: null }))).toThrow(
          /Medición inválida/
        );
        expect(repositorio.recuperarTodas()).toEqual([]);
      });

      test('se rechaza si está ausente', () => {
        const medicion = medicionValida();
        delete medicion[campo];

        expect(() => repositorio.insertar(medicion)).toThrow(/Medición inválida/);
        expect(repositorio.recuperarTodas()).toEqual([]);
      });
    });

    test('se rechaza una medición null', () => {
      expect(() => repositorio.insertar(null)).toThrow(/Medición inválida/);
    });
  });

  describe('errores de base de datos', () => {
    const FRAGMENTO_SQL = /INSERT INTO|SELECT |VALUES|ORDER BY|LIMIT|\?/;

    beforeEach(() => {
      repositorio.conexion.close(); // simula una conexión cerrada
    });

    test.each([
      ['insertar', (r) => r.insertar(medicionValida()), 'No se pudo insertar la medición en la base de datos.'],
      ['recuperarUltima', (r) => r.recuperarUltima(), 'No se pudo recuperar la última medición de la base de datos.'],
      ['recuperarTodas', (r) => r.recuperarTodas(), 'No se pudieron recuperar las mediciones de la base de datos.'],
    ])('%s lanza un Error claro sin exponer el SQL', (_nombre, operacion, mensaje) => {
      let error;
      try {
        operacion(repositorio);
      } catch (e) {
        error = e;
      }

      expect(error).toBeInstanceOf(Error);
      expect(error.message).toBe(mensaje);
      expect(error.message).not.toMatch(FRAGMENTO_SQL);
    });
  });

  describe('constructor', () => {
    test('lanza un Error claro si la BD no puede abrirse', () => {
      expect(() => new RepositorioMediciones('/directorio/inexistente/mediciones.db')).toThrow(
        'No se pudo abrir o inicializar la base de datos.'
      );
    });
  });
});

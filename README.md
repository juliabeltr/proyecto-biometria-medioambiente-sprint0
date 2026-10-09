# proyecto-biometria-medioambiente-sprint0

Repositorio correspondiente al Sprint 0 del proyecto de Biometría y Medioambiente.

Autora: Júlia Beltrán Girbés

Prototipo de una aplicación de crowdsensing para la recogida y visualización de mediciones ambientales.

El objetivo de este Sprint 0 es implementar una primera versión funcional del sistema, conectando las distintas capas de la aplicación:

Sensor → App Móvil → API REST → Lógica de negocio → Base de datos → Interfaz web

## Funcionalidad principal

El sistema permite:

- recibir una medición ambiental;
- almacenar la medición en una base de datos;
- recuperar la última medición;
- recuperar las mediciones almacenadas;
- mostrar la última medición en una interfaz web.

Cada medición contiene:

- tipo de contaminante (`CO2`, `TEMP` o `RUIDO`);
- valor de la medición;
- latitud;
- longitud;
- fecha y hora (ISO 8601).

## Arquitectura

El proyecto sigue una arquitectura por capas:

- **Base de datos** (`src/bd`): almacenamiento persistente de las mediciones en SQLite.
- **Lógica de negocio** (`src/logica`): validación y gestión de las mediciones.
- **API REST** (`src/rest`): comunicación entre los clientes y la lógica de negocio, con HTTP y JSON.
- **Lógica fake en teléfono y navegador**: permite desarrollar las interfaces sin depender del backend real.
- **UX web**: muestra las mediciones al usuario.

Cada capa solo conoce a la siguiente: el REST no accede a la base de datos y la lógica no contiene HTTP ni JSON. La lógica recibe el repositorio por el constructor y el REST recibe la lógica por el constructor, por lo que se pueden sustituir por versiones simuladas en los tests.

## Requisitos

- [Node.js](https://nodejs.org) (versión LTS) y npm.

## Despliegue

```bash
git clone https://github.com/juliabeltr/proyecto-biometria-medioambiente-sprint0.git
cd proyecto-biometria-medioambiente-sprint0
git checkout develop
npm install
node src/rest/index.js
```

El servidor escucha en el puerto 8080 y crea el fichero de base de datos `mediciones.sqlite` en la carpeta desde la que se arranca. Se pueden cambiar con variables de entorno:

| Variable | Significado | Valor por defecto |
|---|---|---|
| `PORT` | Puerto del servidor | `8080` |
| `DB_PATH` | Ruta del fichero SQLite | `mediciones.sqlite` |

## API REST

| Ruta | Descripción | Respuestas |
|---|---|---|
| `POST /mediciones` | Guarda una medición | 201, 400, 500 |
| `GET /mediciones` | Devuelve todas las mediciones | 200, 500 |
| `GET /mediciones/ultima` | Devuelve la última medición | 200, 404, 500 |

Ejemplo:

```bash
curl -X POST http://localhost:8080/mediciones \
  -H "Content-Type: application/json" \
  -d '{"tipo":"CO2","valor":235,"latitud":38.96,"longitud":-0.18,"fechaHora":"2026-10-02T10:00:00Z"}'

curl http://localhost:8080/mediciones/ultima
```

El detalle de cada ruta está en `doc/rest_design.md`.

## Tests

```bash
npm test
```

Ejecuta los tests automáticos de los tres componentes del servidor con Jest:

- `src/bd`: base de datos en memoria.
- `src/logica`: repositorio simulado.
- `src/rest`: lógica simulada y Supertest.

## Estructura del repositorio

```text
author.md
README.md
package.json
codigo_original/        código proporcionado (Arduino, Android y web)
codigo_mejorado/        código original con mejoras
doc/
├── bd_design.md        diseño del componente bd
├── logica_design.md    diseño del componente logica
├── rest_design.md      diseño del componente rest
├── diseno_logico/      planos de ingeniería inversa
└── errores_y_mejoras/
prompts/                prompts usados con la IA para generar el código
src/
├── bd/
├── logica/
└── rest/
```

## Diseño y uso de IA

Los diseños de cada componente están en `doc/`, con la notación lógica de la asignatura. El código de `src/` se ha generado con IA a partir de esos diseños y de los prompts guardados en `prompts/`, y se ha revisado comprobando que cumple el diseño y que los tests pasan.

## Estado del Sprint 0

- [x] Base de datos, lógica de negocio y API REST, con tests
- [ ] Lógica fake del navegador y UX web
- [ ] Lógica fake del teléfono y conexión Android → REST
- [ ] Prueba completa Arduino → Android → REST → BD → web

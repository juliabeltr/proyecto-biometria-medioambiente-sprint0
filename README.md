# proyecto-biometria-medioambiente-sprint0

Repositorio correspondiente al Sprint 0 del proyecto de Biometría y Medioambiente.

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

- tipo de contaminante;
- valor de la medición;
- latitud;
- longitud;
- fecha y hora.

## Arquitectura

El proyecto sigue una arquitectura por capas:

- Base de datos: almacenamiento persistente de las mediciones.
- Lógica de negocio: gestión y validación de los datos.
- API REST: comunicación entre los clientes y la lógica de negocio.
- Lógica fake en teléfono y navegador: permite desarrollar las interfaces sin depender del backend real.
- UX web: muestra las mediciones al usuario.

La API REST utiliza HTTP y JSON para la comunicación entre los distintos componentes.

## Estructura del repositorio

```text
codigo_original/
├── arduino/
├── android/
└── web/

codigo_mejorado/
├── arduino/
├── android/
└── web/

doc/
├── diseno_logico/
└── errores_y_mejoras/

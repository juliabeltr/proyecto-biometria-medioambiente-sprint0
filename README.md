# proyecto-biometria-medioambiente-sprint0

Repositorio del Sprint 0 del proyecto de Biometría y Medioambiente.

Autora: Julia Beltrán Girbés (ver `author.md`).

Prototipo de una aplicación de crowdsensing para la recogida y visualización de mediciones ambientales. El objetivo del Sprint 0 es una primera versión funcional que conecte todas las capas:

Sensor (placa Arduino) → App móvil Android → API REST → Lógica de negocio → Base de datos → Interfaz web

## Funcionalidad

- Recibir una medición ambiental, validarla y guardarla en una base de datos.
- Recuperar la última medición y todas las mediciones almacenadas.
- Mostrar la última medición en una página web.

Cada medición contiene: tipo de contaminante (`CO2`, `TEMP` o `RUIDO`), valor, latitud, longitud y fecha y hora (ISO 8601, en UTC).

En el Sprint 0 el sensor es ficticio: la placa emite un valor fijo que se cambia en `src/arduino/Medidor.h`.

## Arquitectura

Arquitectura por capas. Cada componente tiene su diseño en `doc/` (`xxx_design.md`) y su código en `src/xxx/`:

| Componente | Diseño | Código | Qué hace |
|---|---|---|---|
| Base de datos | `doc/database_design.md` | `src/database/` | Tabla `MEDICIONES` en SQLite y acceso a ella |
| Lógica de negocio | `doc/business_logic_design.md` | `src/business_logic/` | Valida y gestiona las mediciones. No conoce la comunicación |
| Comunicación | `doc/communication_design.md` | `src/communication/` | API REST (HTTP y JSON) que invoca a la lógica de negocio y sirve la web |
| Lógica de negocio del cliente | `doc/frontend_business_logic_design.md` | `src/frontend_business_logic/` | Misma interfaz que la lógica de negocio, como proxy real del servidor o como fake |
| Interfaz web | `doc/gui_design.md` | `src/gui/` | Pantalla "Última medición" |
| App Android | `doc/android_design.md` | `src/android/MedicionesApp/` | Escucha los iBeacon de la placa y envía las mediciones al servidor |
| Placa Arduino | `doc/arduino_design.md` | `src/arduino/` | Emite la medida ficticia como iBeacon |

Dependencias: `communication` → `business_logic` → `database`. La interfaz web solo usa `frontend_business_logic` y nunca se comunica directamente con el servidor.

## Requisitos

- [Node.js](https://nodejs.org) (versión LTS) y npm, para el servidor y la web.
- Android Studio y un móvil Android con Bluetooth LE, para la app.
- Arduino IDE con el soporte de la placa nRF52840 (Adafruit nRF52), para la placa.

## Despliegue y ejecución

### Servidor y web

```bash
git clone https://github.com/juliabeltr/proyecto-biometria-medioambiente-sprint0.git
cd proyecto-biometria-medioambiente-sprint0
git checkout develop
npm install
npm run servidor
```

El servidor escucha en el puerto 8080 y crea la base de datos `mediciones.sqlite` en la carpeta desde la que se arranca. Se puede cambiar con variables de entorno:

| Variable | Significado | Valor por defecto |
|---|---|---|
| `PORT` | Puerto del servidor | `8080` |
| `DB_PATH` | Ruta del fichero SQLite | `mediciones.sqlite` |

La web se abre en **http://localhost:8080/gui/** y muestra la última medición; el botón "Actualizar" vuelve a consultarla.

### API REST

| Ruta | Descripción | Respuestas |
|---|---|---|
| `POST /mediciones` | Guarda una medición | 201, 400, 500 |
| `GET /mediciones` | Devuelve todas las mediciones | 200, 500 |
| `GET /mediciones/ultima` | Devuelve la última medición | 200, 404, 500 |

```bash
curl -X POST http://localhost:8080/mediciones -H "Content-Type: application/json" \
  -d '{"tipo":"CO2","valor":235,"latitud":38.96,"longitud":-0.18,"fechaHora":"2026-10-02T10:00:00Z"}'
curl http://localhost:8080/mediciones/ultima
```

El detalle está en `doc/communication_design.md`.

### App Android

1. Abre `src/android/MedicionesApp` en Android Studio y conecta el móvil con la depuración USB activada.
2. Ejecuta la app. Escribe en ella la dirección del servidor, con la IP del ordenador en la wifi (por ejemplo `http://192.168.1.35:8080`), y pulsa "Iniciar escucha".
3. El móvil y el ordenador deben estar en la misma red.

### Placa Arduino

1. Abre `src/arduino/arduino.ino` en el Arduino IDE (los ficheros `.h` aparecen como pestañas) y elige la placa y el puerto.
2. La medida ficticia se cambia en `src/arduino/Medidor.h` (`medirCO2()` y `medirTemperatura()`).
3. Sube el código y abre el Monitor Serie a 115200 baudios: la placa no empieza a emitir hasta que se abre.

## Tests

```bash
npm test
```

Ejecuta con Jest los tests automáticos de los cinco componentes del servidor y la web (`database`, `business_logic`, `communication`, `frontend_business_logic` y `gui`). Cada componente se prueba aislado, sustituyendo el siguiente por una versión simulada.

La app Android tiene tests JUnit en `src/android/MedicionesApp/app/src/test` (clic derecho sobre la carpeta `test` en Android Studio → Run Tests). El código de la placa no tiene tests automáticos: se comprueba compilando en el Arduino IDE y con la prueba completa.

### Criterio de aceptación del Sprint 0 (reproducible)

1. Arrancar el servidor y abrir la web: sin datos muestra "Todavía no hay mediciones".
2. Poner un valor ficticio en `Medidor.h` (por ejemplo 412), cargarlo en la placa y abrir la app con "Iniciar escucha".
3. En unos segundos la app muestra "CO2 = 412 enviada" y, al pulsar "Actualizar", la web muestra CO2 con valor 412.
4. La medición aparece como una fila nueva de la tabla `MEDICIONES` en `mediciones.sqlite`.

La hora de la web está en la zona horaria del navegador y la base de datos guarda UTC; por eso pueden diferir en horas.

## Estructura del repositorio

```text
author.md
README.md
package.json
doc/          diseños (xxx_design.md) en la notación de la asignatura
src/          código generado a partir de los diseños, un directorio por componente
prompts/      prompts usados con la IA para generar el código
```

Las ramas son `develop` (trabajo) y `master` (versiones estables).

## Uso de IA

El código de `src/` se ha generado con IA a partir de los diseños de `doc/`, con los prompts de `prompts/`. Después se ha revisado que cumple el diseño (métodos y nombres, cabeceras con el diseño lógico, separación de capas) y que los tests pasan.

## Estado del Sprint 0

- [x] Base de datos, lógica de negocio y API REST, con tests
- [x] Lógica de negocio del cliente (real y fake) y web, con tests
- [x] App Android: conversión de la trama iBeacon, envío al servidor y tests JUnit
- [x] Código Arduino adaptado para emitir la medida ficticia
- [ ] Prueba completa placa → móvil → servidor → web

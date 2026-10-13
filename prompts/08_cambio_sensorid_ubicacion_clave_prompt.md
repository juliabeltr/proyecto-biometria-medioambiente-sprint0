# Prompt 08: cambio del modelo de datos (sensorId, ubicacion y clave primaria)

**Diseños modificados:** `doc/database_design.md`, `doc/business_logic_design.md`, `doc/communication_design.md`, `doc/frontend_business_logic_design.md`, `doc/gui_design.md` y `doc/android_design.md`

Tras revisar el Sprint 0, el profesor pidió tres cambios en la base de datos: añadir un campo `sensorId`, juntar `latitud` y `longitud` en un único campo de un tipo específico para coordenadas, y usar `sensorId` y `fechaHora` como clave primaria.

```text
Cambia el modelo de datos del proyecto. Primero actualiza los diseños de doc/
(las tres secciones de cada uno) y después el código y los tests de src/ para
que coincidan con ellos.

Nuevo modelo:
Punto = (latitud: R, longitud: R)
Medicion = (sensorId: Text, tipo: Text, valor: R, ubicacion: Punto, fechaHora: Text)

Base de datos:
- Tabla MEDICIONES con las columnas sensorId, tipo, valor, ubicacion (tipo POINT)
  y fechaHora. Desaparece la columna id.
- Clave primaria compuesta: (sensorId, fechaHora).
- SQLite no tiene tipo POINT nativo: declara la columna como POINT y guarda el
  punto en formato WKT, "POINT(longitud latitud)". El repositorio convierte entre
  Punto y ese texto; el resto del sistema solo ve Punto.
- insertar() devuelve true si guarda y false si ya existe esa clave primaria.
- La última medición es la de mayor fechaHora; en empate, la de mayor sensorId.

Lógica de negocio: sensorId texto no vacío; ubicacion es un Punto con latitud
entre -90 y 90 y longitud entre -180 y 180. guardarMedicion() devuelve false si
los datos no son válidos o si la medición ya existía.

API REST: el JSON lleva sensorId, tipo, valor, ubicacion {latitud, longitud} y
fechaHora. Sin id.

Android: clase Punto; Medicion sin id y con sensorId y ubicacion. El sensorId es
la dirección Bluetooth del emisor, que Android da en cada resultado del escaneo.

Mantén las cabeceras con el diseño lógico y actualiza todos los tests. No añadas
nada que no esté en los diseños.
```

Revisión del resultado: se comprobó que los diseños y el código usan los mismos nombres y firmas, que los tests cubren los casos nuevos (clave repetida, ubicación incompleta, `sensorId` vacío) y que la cadena completa funciona con un servidor real y una base de datos nueva.

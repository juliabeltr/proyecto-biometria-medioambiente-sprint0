# Prompt 06: lógica real (proxy) del componente frontend_business_logic

**Diseño adjunto:** `doc/frontend_business_logic_design.md`

```text
Genera la lógica real del navegador del prototipo Sprint 0, que obtiene las
mediciones llamando al API REST.

Respeta exactamente el diseño adjunto (doc/frontend_business_logic_design.md): sus tres
secciones forman parte de este prompt.

Medicion = (id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text)

Tecnología: JavaScript para el navegador, sin librerías (fetch), cargable también
desde Node.js. Tests con Jest y fetch simulado.

Ficheros a generar, dentro de src/frontend_business_logic/: logicaNavegador.js y
logicaNavegador.test.js.

Directrices:
- Clase LogicaNavegador(urlBase) con solo recuperarUltimaMedicion() y
  recuperarMediciones(), asíncronas, con la misma interfaz que
  LogicaNavegadorFake.
- recuperarUltimaMedicion llama a GET /mediciones/ultima: 200 devuelve la
  medición; 404 devuelve null; otro código, fallo de red o JSON no válido es un
  error.
- recuperarMediciones llama a GET /mediciones: 200 devuelve la lista; cualquier
  otro caso es un error.
- urlBase vacío usa el mismo origen de la página.
- Sin DOM, HTML ni validación de negocio. Sin operaciones fuera del diseño.

Comentarios: cabecera con propósito, diseño lógico entre líneas de guiones,
parámetros, retorno y errores en cada función.

Tests: 200, 404, 500, fallo de red y JSON no válido; lista con datos y vacía; URL
llamada con urlBase vacío e indicado.
```

**Revisión del resultado:** tests en verde; misma interfaz que la fake; el
servidor real devuelve lo que la lógica espera (comprobado con prueba completa).

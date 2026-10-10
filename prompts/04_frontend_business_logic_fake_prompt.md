# Prompt 04: lógica fake del componente frontend_business_logic

**Diseño adjunto:** `doc/frontend_business_logic_design.md`

```text
Genera la lógica fake del navegador del prototipo Sprint 0, que simula a la
lógica real para desarrollar la interfaz web sin servidor.

Respeta exactamente el diseño adjunto (doc/frontend_business_logic_design.md): sus tres
secciones forman parte de este prompt.

Medicion = (id: N, tipo: Text, valor: R, latitud: R, longitud: R, fechaHora: Text)

Tecnología: JavaScript para el navegador, sin librerías externas, cargable
también desde Node.js (module.exports condicional). Tests con Jest.

Ficheros a generar, dentro de src/frontend_business_logic/: logicaNavegadorFake.js y
logicaNavegadorFake.test.js.

Directrices:
- Clase LogicaNavegadorFake(mediciones, falla) con solo recuperarUltimaMedicion()
  y recuperarMediciones(), ambas asíncronas (devuelven una Promise).
- Sin lista, usa un conjunto de ejemplo con al menos una medición de CO2.
- La última es la de mayor fechaHora (comparando instantes reales); en empate,
  la de mayor id; null si no hay mediciones.
- Si falla es true, ambas operaciones se rechazan con un error simulado.
- Devuelve copias de los datos. Sin peticiones, DOM ni HTML.
- Sin operaciones fuera del diseño.

Comentarios: cabecera con propósito, diseño lógico entre líneas de guiones,
parámetros, retorno y errores en cada función.

Tests: última con datos y desempate por id, zonas horarias distintas, sin
mediciones (null), copias, varias y lista vacía, datos de ejemplo, error simulado.
```

**Revisión del resultado:** tests en verde; misma interfaz que la lógica real;
sin acceso al DOM ni a la red.

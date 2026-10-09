// =============================================================================
// main.js
//
// Descripción: arranque de la interfaz. Elige la lógica y arranca el
//              controlador. Sin más lógica.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Usa la lógica real del navegador, que llama al servidor desde
//              el que se cargó la página (abrir http://localhost:8080/ux/).
//              Para desarrollar sin servidor basta cargar en index.html
//              ../navegador_fake/logicaNavegadorFake.js y usar aquí
//              new LogicaNavegadorFake(): tiene la misma interfaz.
// =============================================================================

const logica = new LogicaNavegador();

new ControladorUX(logica).iniciar();

// =============================================================================
// main.js
//
// Descripción: arranque de la interfaz. Elige la lógica y arranca el
//              controlador. Sin más lógica.
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Usa la lógica fake del navegador. Para conectar con el
//              servidor real basta sustituir esta línea por la lógica real,
//              que tiene la misma interfaz.
// =============================================================================

const logica = new LogicaNavegadorFake();

new ControladorUX(logica).iniciar();

// =============================================================================
// index.js
//
// Descripción: punto de arranque del servidor REST. Solo monta los
//              componentes bd, logica y rest y escucha peticiones.
// Autor:       Júlia Beltrán Girbés
// Fecha:       02/10/2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Arranque mínimo, sin lógica. Puerto: variable PORT o 8080.
//              Base de datos: variable DB_PATH o mediciones.sqlite.
// =============================================================================

const RepositorioMediciones = require("../bd/repositorioMediciones");
const LogicaMediciones = require("../logica/logicaMediciones");
const ServidorREST = require("./servidorREST");

const puerto = process.env.PORT || 8080;
const rutaBaseDeDatos = process.env.DB_PATH || "mediciones.sqlite";

const repositorio = new RepositorioMediciones(rutaBaseDeDatos);
const logica = new LogicaMediciones(repositorio);
const servidor = new ServidorREST(logica);

servidor.app.listen(puerto, () => {
    console.log(`Servidor REST escuchando en el puerto ${puerto}`);
});

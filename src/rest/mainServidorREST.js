// ------------------------------------------------------------
// mainServidorREST.js
//
// Arranca el servidor REST del Sprint 0.
// Conecta la lógica de negocio con las reglas REST y sirve
// también la interfaz web.
// ------------------------------------------------------------

const express = require("express");
const path = require("path");

const Logica = require("../logica/Logica.js");
const reglasREST = require("./ReglasREST.js");

// ------------------------------------------------------------
// crearServidor()
//
// Crea y configura el servidor Express.
// ------------------------------------------------------------
function crearServidor() {

    const app = express();

    // Permite recibir cuerpos JSON en las peticiones POST.
    app.use(express.json());

    // Ruta absoluta de la base de datos situada en la raíz.
    const rutaBD = path.join(
        __dirname,
        "../../mediciones.sqlite"
    );

    // Crear la lógica real.
    const logica = new Logica(rutaBD);

    // Cargar las rutas REST.
    reglasREST.cargar(app, logica);

    // Servir la interfaz web desde src/ux.
    const rutaUX = path.join(
        __dirname,
        "../ux"
    );

    app.use(express.static(rutaUX));

    // Al entrar en http://localhost:8080/
    // se devuelve la página principal.
    app.get("/", (req, res) => {
        res.sendFile(
            path.join(rutaUX, "index.html")
        );
    });

    return app;
}

// ------------------------------------------------------------
// main()
//
// Arranca el servidor HTTP en el puerto 8080.
// ------------------------------------------------------------
function main() {

    const app = crearServidor();

    const PUERTO = 8080;

    const servidor = app.listen(PUERTO, () => {
        console.log(
            `Servidor REST escuchando en http://localhost:${PUERTO}`
        );
    });

    // Cierre ordenado con Ctrl+C.
    process.on("SIGINT", () => {

        console.log("\nCerrando servidor...");

        servidor.close(() => {
            console.log("Servidor cerrado.");
            process.exit(0);
        });
    });
}

// Solo arrancar automáticamente cuando ejecutamos este archivo.
if (require.main === module) {
    main();
}

// Exportarlo permite utilizarlo desde los tests.
module.exports = {
    crearServidor
};

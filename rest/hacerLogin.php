<?php

require_once('../logica/hacerLogin.php');

// ----------------------------------------------------------------
//
// Endpoint REST para iniciar sesión.
//
// POST ../rest/hacerLogin.php
//
// Cuerpo JSON:
// {
//   "nombre": "...",
//   "password": "..."
// }
//
// Diseño lógico:
// nombre: Text, password: Text
//      --> hacerLoginREST()
//      --> resultado: B
//
// Si el login es correcto, el usuario queda almacenado
// en la sesión.
//
// ----------------------------------------------------------------

header('Content-Type: application/json; charset=utf-8');

session_start();

$objetoResultado = new stdClass();

try {

    /*
     * Solo se acepta POST porque la operación contiene
     * credenciales y modifica el estado de la sesión.
     */
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {

        http_response_code(405);

        $objetoResultado->resultado = false;
        $objetoResultado->error =
            "método HTTP no permitido";

        echo json_encode(
            $objetoResultado
        );

        exit;
    }

    /*
     * Se obtiene y decodifica el JSON recibido.
     */
    $contenido =
        file_get_contents("php://input");

    $datos =
        json_decode(
            $contenido,
            true
        );

    if (!is_array($datos)) {

        http_response_code(400);

        $objetoResultado->resultado = false;
        $objetoResultado->error =
            "JSON no válido";

        echo json_encode(
            $objetoResultado
        );

        exit;
    }

    $nombre =
        $datos["nombre"] ?? null;

    $password =
        $datos["password"] ?? null;

    if (
        !is_string($nombre)
        ||
        !is_string($password)
        ||
        trim($nombre) === ""
    ) {

        http_response_code(400);

        $objetoResultado->resultado = false;
        $objetoResultado->error =
            "faltan credenciales válidas";

        echo json_encode(
            $objetoResultado
        );

        exit;
    }

    /*
     * Llamada a la verdadera función de lógica.
     */
    if (
        hacerLogin(
            $nombre,
            $password
        )
    ) {

        /*
         * Regenera el identificador de sesión tras
         * autenticar correctamente al usuario.
         */
        session_regenerate_id(true);

        $_SESSION["usuario"] =
            $nombre;

        $objetoResultado->resultado =
            true;

        $objetoResultado->usuario =
            $nombre;

        $objetoResultado->error =
            0;

    } else {

        /*
         * Si el login falla se eliminan los datos
         * de autenticación de la sesión actual.
         */
        unset(
            $_SESSION["usuario"]
        );

        $objetoResultado->resultado =
            false;

        $objetoResultado->error =
            "credenciales incorrectas";

        /*
         * No distinguimos públicamente si ha fallado
         * el usuario o la contraseña.
         */
        http_response_code(401);
    }

} catch (Throwable $error) {

    http_response_code(500);

    $objetoResultado->resultado =
        false;

    $objetoResultado->error =
        "error interno del servidor";
}

echo json_encode(
    $objetoResultado
);

?>

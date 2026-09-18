<?php

require_once('../logica/diHola.php');

// -------------------------------------------------
//
// Endpoint REST para obtener un saludo.
//
// GET ../rest/diHola.php
//
// Diseño lógico:
// usuario: Text --> diHolaREST()
//      --> (nombre: Text, saludo: Text) | error: Text
//
// El usuario se obtiene implícitamente de la sesión.
//
// -------------------------------------------------

header('Content-Type: application/json; charset=utf-8');

session_start();

$objetoResultado = new stdClass();

try {

    /*
     * Este endpoint únicamente consulta información,
     * por lo que se acepta GET.
     */
    if ($_SERVER['REQUEST_METHOD'] !== 'GET') {

        http_response_code(405);

        $objetoResultado->error =
            "método HTTP no permitido";

        echo json_encode(
            $objetoResultado
        );

        exit;
    }

    /*
     * Se comprueba que exista un usuario autenticado
     * en la sesión.
     */
    if (
        !isset($_SESSION["usuario"])
        ||
        !is_string($_SESSION["usuario"])
        ||
        trim($_SESSION["usuario"]) === ""
    ) {

        http_response_code(401);

        $objetoResultado->error =
            "usuario no acreditado";

        echo json_encode(
            $objetoResultado
        );

        exit;
    }

    $usuario =
        $_SESSION["usuario"];

    /*
     * Llamada a la función de lógica.
     */
    $objetoResultado =
        diHola(
            $usuario
        );

    $objetoResultado->error =
        0;

} catch (Throwable $error) {

    http_response_code(500);

    $objetoResultado =
        new stdClass();

    $objetoResultado->error =
        "error interno del servidor";
}

echo json_encode(
    $objetoResultado
);

?>

<?php

// -------------------------------------------------
//
// Genera un saludo para un usuario.
//
// Diseño lógico:
// usuario: Text --> diHola()
//      --> (nombre: Text, saludo: Text)
//
// -------------------------------------------------

function diHola($usuario) {

    if (!is_string($usuario)) {
        throw new InvalidArgumentException(
            "diHola(): usuario no válido"
        );
    }

    $objetoResultado = new stdClass();

    $objetoResultado->nombre =
        $usuario;

    $objetoResultado->saludo =
        "That's all folks";

    return $objetoResultado;
}

?>

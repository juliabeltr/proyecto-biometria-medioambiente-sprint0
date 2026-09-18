<?php

// ---------------------------------------------------------------
//
// Lógica de demostración para comprobar credenciales.
//
// Diseño lógico:
// nombre: Text, password: Text --> hacerLogin() --> B
//
// IMPORTANTE:
// Esta función utiliza una contraseña fija únicamente
// como ejemplo académico. No debe utilizarse como
// sistema de autenticación real.
//
// ---------------------------------------------------------------

function hacerLogin($nombre, $password) {

    if (!is_string($nombre) || !is_string($password)) {
        return false;
    }

    if (trim($nombre) === "") {
        return false;
    }

    /*
     * Contraseña fija del ejemplo original.
     *
     * En una aplicación real deberían utilizarse
     * usuarios almacenados de forma segura y hashes
     * de contraseña.
     */
    if ($password === "1234") {
        return true;
    }

    return false;
}

?>

// ---------------------------------------------------
//
// Versión cliente de la función de lógica hacerLogin().
//
// Diseño lógico:
// nombre: Text, password: Text --> hacerLogin() --> B
//
// El resultado se devuelve mediante callback.
//
// ---------------------------------------------------

function hacerLogin(nombre, password, cb) {

    if (typeof cb !== "function") {
        throw new Error("hacerLogin(): el callback no es válido");
    }

    var xmlhttp = new XMLHttpRequest();

    xmlhttp.onreadystatechange = function() {

        if (this.readyState !== 4) {
            return;
        }

        if (this.status >= 200 && this.status < 300) {

            try {

                console.log("recibo: " + this.responseText);

                var resultado =
                    JSON.parse(this.responseText);

                cb(null, resultado);

            } catch (error) {

                console.error(
                    "hacerLogin(): respuesta JSON no válida",
                    error
                );

                cb(
                    "respuesta JSON no válida",
                    null
                );
            }

        } else {

            console.error(
                "hacerLogin(): error HTTP " + this.status
            );

            cb(
                "error HTTP " + this.status,
                null
            );
        }
    };

    xmlhttp.onerror = function() {

        console.error(
            "hacerLogin(): error de red"
        );

        cb(
            "error de red",
            null
        );
    };

    /*
     * Se utiliza POST en vez de GET para evitar
     * incluir la contraseña en la URL.
     */
    xmlhttp.open(
        "POST",
        "../rest/hacerLogin.php",
        true
    );

    xmlhttp.setRequestHeader(
        "Content-Type",
        "application/json; charset=UTF-8"
    );

    var datos = {
        nombre: nombre,
        password: password
    };

    xmlhttp.send(
        JSON.stringify(datos)
    );
}

// ---------------------------------------------------
//
// Versión cliente de la función de lógica diHola().
//
// Diseño lógico:
// usuario: Text --> diHola()
//      --> (nombre: Text, saludo: Text) | error: Text
//
// El usuario se obtiene implícitamente mediante la sesión.
// El resultado se devuelve mediante callback(error, resultado).
//
// ---------------------------------------------------

function diHola(cb) {

    if (typeof cb !== "function") {
        throw new Error("diHola(): el callback no es válido");
    }

    var xmlhttp = new XMLHttpRequest();

    xmlhttp.onreadystatechange = function() {

        if (this.readyState !== 4) {
            return;
        }

        if (this.status >= 200 && this.status < 300) {

            try {

                console.log(
                    "recibo: " + this.responseText
                );

                var resultado =
                    JSON.parse(this.responseText);

                if (
                    resultado.error !== undefined
                    &&
                    resultado.error !== 0
                ) {

                    cb(
                        resultado.error,
                        null
                    );

                    return;
                }

                cb(
                    null,
                    resultado
                );

            } catch (error) {

                console.error(
                    "diHola(): respuesta JSON no válida",
                    error
                );

                cb(
                    "respuesta JSON no válida",
                    null
                );
            }

        } else {

            console.error(
                "diHola(): error HTTP " + this.status
            );

            cb(
                "error HTTP " + this.status,
                null
            );
        }
    };

    xmlhttp.onerror = function() {

        console.error(
            "diHola(): error de red"
        );

        cb(
            "error de red",
            null
        );
    };

    xmlhttp.open(
        "GET",
        "../rest/diHola.php",
        true
    );

    xmlhttp.send();
}

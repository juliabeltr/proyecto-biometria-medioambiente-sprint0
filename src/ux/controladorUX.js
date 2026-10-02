// =============================================================================
// controladorUX.js
//
// Descripción: controlador de la pantalla "Última medición" (componente ux).
// Autor:       Júlia Beltrán Girbés
// Fecha:       2026
// Copyright:   Proyecto Biometría y Medioambiente, Sprint 0
// Aportación:  Implementación de doc/ux_design.md. Obtiene los datos solo a
//              través de LogicaNavegador (fake o real, misma interfaz). No
//              hace peticiones HTTP ni contiene lógica de negocio.
// =============================================================================

const TEXTO_CARGANDO = "Cargando...";
const TEXTO_SIN_DATOS = "Todavía no hay mediciones";
const TEXTO_ERROR = "No se pudo obtener la medición";

/*
 * Medicion = ( id: N, tipo: Text, valor: R, latitud: R, longitud: R,
 *              fechaHora: Text )
 */
class ControladorUX {

    /*
     * --------------------------------------------------------------
     * Propósito: crea el controlador y localiza los elementos de la
     *            pantalla.
     *
     * Diseño lógico:
     *     logica: LogicaNavegador --> ControladorUX() -->
     *
     * Parámetros:
     *     logica: LogicaNavegador. Lógica fake o real, con
     *             recuperarUltimaMedicion().
     * Retorno: ninguno.
     * Errores: ninguno.
     * --------------------------------------------------------------
     */
    constructor(logica) {
        this.logica = logica;
        this.mensajeEstado = document.getElementById("mensaje-estado");
        this.tarjeta = document.getElementById("tarjeta-medicion");
        this.datoTipo = document.getElementById("dato-tipo");
        this.datoValor = document.getElementById("dato-valor");
        this.datoFecha = document.getElementById("dato-fecha");
        this.datoHora = document.getElementById("dato-hora");
        this.botonActualizar = document.getElementById("boton-actualizar");
    }

    /*
     * --------------------------------------------------------------
     * Propósito: asocia el botón "Actualizar" a actualizar() y carga
     *            la última medición al abrir la página.
     *
     * Diseño lógico:
     *     iniciar() -->
     *
     * Parámetros: ninguno.
     * Retorno: Promise que se cumple al terminar la primera carga.
     * Errores: ninguno: los fallos se muestran en pantalla.
     * --------------------------------------------------------------
     */
    iniciar() {
        this.botonActualizar.addEventListener("click", () => this.actualizar());
        return this.actualizar();
    }

    /*
     * --------------------------------------------------------------
     * Propósito: pide la última medición y muestra el estado de la
     *            pantalla: cargando, con datos, sin datos o error.
     *
     * Diseño lógico:
     *     actualizar() -->
     *
     * Parámetros: ninguno.
     * Retorno: Promise que se cumple al terminar la actualización.
     * Errores: ninguno: los fallos se muestran en pantalla.
     * --------------------------------------------------------------
     */
    async actualizar() {
        this.#mostrarCargando();
        try {
            const medicion = await this.logica.recuperarUltimaMedicion();
            if (medicion === null || medicion === undefined) {
                this.#mostrarSinDatos();
            } else {
                this.#mostrarMedicion(ControladorUX.formatearMedicion(medicion));
            }
        } catch (error) {
            this.#mostrarError();
        } finally {
            this.botonActualizar.disabled = false;
        }
    }

    /*
     * --------------------------------------------------------------
     * Propósito: prepara una medición para mostrarla, separando
     *            fechaHora en fecha (dd/mm/aaaa) y hora (hh:mm) en la
     *            zona horaria del navegador. No accede al DOM.
     *
     * Diseño lógico:
     *     m: Medicion --> formatearMedicion() --x
     *       texto: (tipo: Text, valor: Text, fecha: Text, hora: Text) <--
     *
     * Parámetros:
     *     m: Medicion. Medición a formatear.
     * Retorno: texto: (tipo: Text, valor: Text, fecha: Text, hora: Text).
     * Errores: Error si fechaHora no es una fecha válida.
     * --------------------------------------------------------------
     */
    static formatearMedicion(m) {
        const instante = new Date(m.fechaHora);
        if (Number.isNaN(instante.getTime())) {
            throw new Error("fechaHora no válida");
        }
        const dos = (n) => String(n).padStart(2, "0");
        return {
            tipo: String(m.tipo),
            valor: String(m.valor),
            fecha: `${dos(instante.getDate())}/${dos(instante.getMonth() + 1)}/${instante.getFullYear()}`,
            hora: `${dos(instante.getHours())}:${dos(instante.getMinutes())}`,
        };
    }

    /*
     * --------------------------------------------------------------
     * Propósito: estado "cargando": mensaje, tarjeta oculta y botón
     *            desactivado.
     *
     * Diseño lógico:
     *     mostrarCargando() -->
     * --------------------------------------------------------------
     */
    #mostrarCargando() {
        this.botonActualizar.disabled = true;
        this.tarjeta.hidden = true;
        this.#escribirEstado(TEXTO_CARGANDO, false);
    }

    /*
     * --------------------------------------------------------------
     * Propósito: estado "con datos": muestra la tarjeta con la medición
     *            formateada y borra el mensaje.
     *
     * Diseño lógico:
     *     texto: (tipo: Text, valor: Text, fecha: Text, hora: Text)
     *         --> mostrarMedicion() -->
     * --------------------------------------------------------------
     */
    #mostrarMedicion(texto) {
        this.datoTipo.textContent = texto.tipo;
        this.datoValor.textContent = texto.valor;
        this.datoFecha.textContent = texto.fecha;
        this.datoHora.textContent = texto.hora;
        this.tarjeta.hidden = false;
        this.#escribirEstado("", false);
    }

    /*
     * --------------------------------------------------------------
     * Propósito: estado "sin datos": mensaje y tarjeta oculta.
     *
     * Diseño lógico:
     *     mostrarSinDatos() -->
     * --------------------------------------------------------------
     */
    #mostrarSinDatos() {
        this.tarjeta.hidden = true;
        this.#escribirEstado(TEXTO_SIN_DATOS, false);
    }

    /*
     * --------------------------------------------------------------
     * Propósito: estado "error": mensaje de error y tarjeta oculta.
     *
     * Diseño lógico:
     *     mostrarError() -->
     * --------------------------------------------------------------
     */
    #mostrarError() {
        this.tarjeta.hidden = true;
        this.#escribirEstado(TEXTO_ERROR, true);
    }

    /*
     * --------------------------------------------------------------
     * Propósito: escribe el mensaje de estado y marca si es un error.
     *
     * Diseño lógico:
     *     texto: Text, esError: B --> escribirEstado() -->
     * --------------------------------------------------------------
     */
    #escribirEstado(texto, esError) {
        this.mensajeEstado.textContent = texto;
        this.mensajeEstado.classList.toggle("estado--error", esError);
    }
}

// En Node.js (tests) se exporta; en el navegador la clase queda global.
if (typeof module !== "undefined" && module.exports) {
    module.exports = ControladorUX;
}

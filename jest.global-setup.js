// -----------------------------------------------------------------------------
// Fija la zona horaria de los tests para que formatearMedicion() dé el mismo
// resultado en cualquier ordenador.
// -----------------------------------------------------------------------------
module.exports = async () => {
    process.env.TZ = "UTC";
};

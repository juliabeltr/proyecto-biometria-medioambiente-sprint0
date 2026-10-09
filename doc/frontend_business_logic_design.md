# Diseño del componente: frontend_business_logic

## 1. Diseño del componente

Medicion = (
  id: N,
  tipo: Text,
  valor: R,
  latitud: R,
  longitud: R,
  fechaHora: Text
)

```text
 --------- FrontendBusinessLogic ----------------
 |
 |
 Medicion <-- recuperarUltimaMedicion() <--
 |
 |
 [Medicion] <-- recuperarMediciones() <--
 |
 ------------------------------------------------

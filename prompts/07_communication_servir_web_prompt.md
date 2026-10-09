# Prompt 07: ampliación de communication para servir la interfaz web

**Diseño adjunto:** `doc/communication_design.md` (versión con `rutaWeb`)

```text
Amplía el servidor REST ya generado (src/communication/) siguiendo la versión actualizada
del diseño adjunto (doc/communication_design.md).

Cambios:
- El constructor de ServidorREST recibe además rutaWeb: Text, la carpeta que
  contiene gui y frontend_business_logic. Si no se indica, no se sirve ninguna
  página.
- Con rutaWeb, el servidor sirve como ficheros estáticos solo esas dos carpetas
  (rutas /gui y /frontend_business_logic). El código de bd, logica y rest nunca
  se publica.
- index.js pasa rutaWeb al servidor y muestra la URL de la interfaz.
- Las rutas de la API no cambian.

Tests nuevos: con rutaWeb, GET /gui/ devuelve index.html; se sirven los ficheros
de gui y frontend_business_logic; no se sirven database, business_logic ni communication; no se puede salir
de las carpetas servidas; sin rutaWeb, GET /gui/ devuelve 404; servir la web no
altera la API.

No modifiques nada más ni añadas funcionalidades fuera del diseño.
```

**Revisión del resultado:** `curl localhost:8080/database/repositorioMediciones.js`
devuelve 404; los 137 tests pasan.

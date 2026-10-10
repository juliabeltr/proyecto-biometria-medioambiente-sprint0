# Prompts usados con la IA

Cada fichero es el prompt que se dio a la IA junto con el diseño indicado en `doc/`. El código resultante está en `src/` y se revisó comprobando que los tests pasan y que cumple el diseño (métodos y nombres, cabeceras con el diseño lógico, separación de capas).

| Prompt | Diseño adjunto | Código generado |
|---|---|---|
| `01_database_prompt.md` | `doc/database_design.md` | `src/database/` |
| `02_business_logic_prompt.md` | `doc/business_logic_design.md` | `src/business_logic/` |
| `03_communication_prompt.md` | `doc/communication_design.md` | `src/communication/` |
| `04_frontend_business_logic_fake_prompt.md` | `doc/frontend_business_logic_design.md` | `src/frontend_business_logic/` (lógica fake) |
| `05_gui_prompt.md` | `doc/gui_design.md` | `src/gui/` |
| `06_frontend_business_logic_prompt.md` | `doc/frontend_business_logic_design.md` | `src/frontend_business_logic/` (lógica real) |
| `07_communication_servir_web_prompt.md` | `doc/communication_design.md` | ampliación de `src/communication/` |

Nota: los prompts se escribieron con los nombres de componente anteriores (`bd`, `logica`, `rest`, `navegador`, `ux`); aquí aparecen ya con los nombres actuales.

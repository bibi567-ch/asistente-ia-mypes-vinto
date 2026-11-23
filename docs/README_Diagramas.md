# Diagramas

Fuentes editables para las vistas de contexto, contenedores, casos de uso y flujos. Se deben revisar contra el código de la rama que se presente y exportar con legibilidad suficiente para la defensa.

| Archivo | Qué muestra | Revisión necesaria |
|---|---|---|
| [C1_Contexto.mmd](../diagrams/C1_Contexto.mmd) | Comerciante, teléfono y API | Validar actores, límites y relación de sincronización |
| [C2_Contenedores.mmd](../diagrams/C2_Contenedores.mmd) | Teléfono, persistencia local, API y PostgreSQL | Actualizar para distinguir componentes implementados, previstos y pendientes |
| [Casos_Uso.puml](../diagrams/Casos_Uso.puml) | Casos de uso principales | Cotejar login PIN, onboarding, inventario, ventas y sincronización con la interfaz disponible |
| [Flujos_Usuario.md](../diagrams/Flujos_Usuario.md) | Recorridos previstos | Marcar qué pasos fueron demostrados y cuáles siguen siendo solo diseño |

## Consistencia tecnológica

La rama revisada implementa backend Node.js/TypeScript/Express con PostgreSQL; algunos documentos históricos conservan decisiones de una versión anterior con Spring Boot. Los diagramas no deben combinar ambas líneas sin explicar la migración y el estado de cada decisión.

La app móvil contiene Room, componentes de ventas y Outbox/WorkManager. Su presencia en código no prueba la conexión completa con la API. El cliente móvil usa 10.0.2.2, configurado para el emulador; el teléfono físico necesita validación aparte. El backend se documenta como servicio local y no como despliegue cloud.

## Criterio para presentar

Cada vista debe tener versión/fecha, leyenda de estado (actual, propuesta o pendiente) y enlace a requisitos/decisiones. Un diagrama describe diseño; no es evidencia de ejecución. Ver el [plan de pruebas](13_Plan_de_Pruebas.md) y la [matriz de evidencia](../defensa_tecnica/03_matriz_evidencia.md) para las brechas y evidencias por reunir.

# Reporte de Integración Continua (CI)

El workflow [Backend](../.github/workflows/backend.yml) ejecuta en GitHub Actions, para los eventos configurados, la instalación reproducible, migraciones PostgreSQL, build, chequeo de tipos, pruebas unitarias y pruebas de integración. Cada ejecución publica automáticamente su resultado y enlace en el resumen del run.

## Historial de ejecuciones

| Fecha UTC | Commit ID | Estado de Tests Unitarios | Estado de Tests de Integración (PostgreSQL) | Estado General | Ejecución |
|---|---|---|---|---|---|
| 2026-11-10 (local) | `c187bba` | ✅ Aprobado: 8/8 | ⚪ No verificado: autenticación local de PostgreSQL bloqueó la ejecución | ⚪ Parcial; no es CI | Sin enlace de GitHub Actions |
| Pendiente de CI | Pendiente | Pendiente | Pendiente | Pendiente | Agregar aquí el enlace al run real |

La fila local refleja únicamente `npm test` en el equipo de desarrollo; no debe interpretarse como evidencia de GitHub Actions. La segunda fila se completa cuando el workflow termine. No registrar ejemplos inventados ni checks verdes antes de observar el resultado.

## Evidencia visual de la última ejecución

[INSERTAR IMAGEN AQUÍ: Captura de pantalla de GitHub Actions mostrando el check verde y los 8 tests pasando]

También se puede enlazar directamente el resumen de Actions, que conserva commit, fecha, estados por paso y logs:

- **Enlace al último run:** pendiente de la primera ejecución del workflow después de este cambio.
- **Captura:** guardar en `evidencias/08_backend/` y sustituir este marcador por `![Ejecución backend en GitHub Actions](evidencias/08_backend/github-actions-backend.png)`.

## Observaciones

- El job usa PostgreSQL efímero y no depende de la base de desarrollo local.
- La ejecución local del 2026-11-10 completó build, chequeo de tipos y 8 pruebas unitarias; la base de integración local rechazó las credenciales de prueba proporcionadas y no permitió verificar integración.
- El estado de integración queda pendiente hasta que GitHub Actions complete esa etapa correctamente.
- Documentar aquí fallos reales, su diagnóstico, corrección y enlace al run posterior.

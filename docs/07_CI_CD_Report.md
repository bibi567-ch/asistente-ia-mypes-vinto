# Reporte de Integración Continua (CI)

El workflow [Backend](../.github/workflows/backend.yml) instala dependencias, aplica migraciones PostgreSQL y ejecuta build, chequeo de tipos, pruebas unitarias y de integración. Los resultados se registran en los enlaces oficiales de GitHub Actions; esta tabla no sustituye los logs del run.

## Ejecuciones verificadas

| Fecha UTC | Rama | Revisión | Resultado | Alcance | Ejecución |
|---|---|---|---|---|---|
| 2026-11-12 | `main` | `03afcbd58e5d91a7374b52768e50061aaeb9fab9` | Éxito: instalación, PostgreSQL, migraciones, build, typecheck, unit e integración | Workflow backend con PostgreSQL 16 temporal. No prueba Android ni despliegue. | [Run 43157514411](https://github.com/bibi567-ch/asistente-ia-mypes-vinto/actions/runs/43157514411) |
| 2026-11-12 | `dev` | `6ec2a28c3e4ece1651ef39e99c786a2fc715842e` | Éxito | Workflow backend; no equivale a validación funcional Android. | [Run 43150156269](https://github.com/bibi567-ch/asistente-ia-mypes-vinto/actions/runs/43150156269) |

El run `43157514411` corresponde a la revisión base de esta rama documental. La prueba local bloqueada del 10/11 se conserva como antecedente en [Resultados](05_Resultados_de_Pruebas.md); no se reemplaza por una afirmación de que esa ejecución local haya pasado.

## Alcance no cubierto por CI

- Interfaz y flujos completos Android en un teléfono.
- Reconocimiento acústico con Vosk, latencia o precisión.
- Migración de una base Room existente en dispositivo.
- Conexión física Android–API, red pública o despliegue de producción.
- Persistencia end-to-end de factura/QR y consumo real de eventos SSE.

Una captura aislada, una compilación Android o una ejecución de pruebas no debe presentarse como evidencia de los escenarios anteriores.

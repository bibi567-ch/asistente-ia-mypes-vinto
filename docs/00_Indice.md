# Índice canónico — Yapay

**Proyecto:** Asistente conversacional offline-first para MYPES de Vinto
**Actualizado:** 23/11/2026
**Entrada única:** [README.md](README.md)

Este índice usa únicamente documentos que existen en el repositorio. Los documentos históricos están en `docs/archive/` y no sustituyen la arquitectura vigente.

## Lectura recomendada

| Orden | Documento | Propósito |
|---:|---|---|
| 01 | [Proyecto y alcance](01_Proyecto_y_Alcance.md) | Problema, usuarios, alcance y exclusiones |
| 02 | [Requisitos y UX](02_Requisitos_y_UX.md) | Flujos, intenciones, privacidad y criterios de aceptación |
| 03 | [SAD de arquitectura](03_SAD_Arquitectura.md) | Arquitectura C4, Room v7, Outbox y decisiones |
| 04 | [Plan de pruebas](04_Plan_de_Pruebas.md) | Escenarios, criterios y estados |
| 05 | [Resultados de pruebas](05_Resultados_de_Pruebas.md) | Resultados realmente ejecutados y bloqueos |
| 06 | [Guía de despliegue](06_Guia_de_Despliegue.md) | Configuración local y ejecución |
| 07 | [Reporte CI/CD](07_CI_CD_Report.md) | Evidencia del pipeline y límites |
| 08 | [Informe segunda entrega](Informe_Segunda_Entrega_TI.md) | Contratos y alcance de Tecnologías en Internet |
| 09 | [Plan de pruebas operativo](13_Plan_de_Pruebas.md) | Pruebas de campo, WER, SUS y latencia |
| 10 | [Resultados operativos](14_Resultados_de_Pruebas.md) | Métricas pendientes y resultados de campo |
| 11 | [Guía de despliegue operativa](15_Guia_de_Despliegue.md) | Procedimiento reproducible de entrega |
| 12 | [Viabilidad técnica](16_Reporte_Viabilidad_Tecnica.md) | Dependencias, límites y viabilidad |

## Código y material de defensa

- [../backend/](../backend/): API, migraciones y pruebas.
- [../mobile/](../mobile/): aplicación Android y Gradle Wrapper.
- [../diagrams/](../diagrams/): fuentes de contexto, contenedores, casos de uso y flujos.
- [evidencias/](evidencias/): capturas y resultados anonimizados.
- [defensa_tecnica/](../defensa_tecnica/): guía personal con análisis de fallas, comandos, guion C4, Docker, trade-offs y preguntas.
- [../legacy/](../legacy/): prototipos históricos separados del sistema activo.
- [archive/](archive/): antecedentes no normativos.

## Regla de coherencia

Una compilación demuestra compilación, no funcionamiento en dispositivo. Una prueba unitaria no demuestra integración PostgreSQL. Toda afirmación de la defensa debe enlazar código, comando ejecutado y evidencia, o declararse pendiente.

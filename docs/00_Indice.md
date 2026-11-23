# Índice de documentación

## Documentación vigente del proyecto

1. [Proyecto y alcance](01_Proyecto_y_Alcance.md): propósito, límites y tecnologías.
2. [Requisitos y UX — resumen operativo](01_Requisitos_y_UX.md): requisitos relevantes observados y límites de verificación.
3. [Especificación formal de requisitos y UX](02_Requisitos_y_UX.md): línea base académica v1.1 más su nota de implementación.
4. [Arquitectura actual](02_Arquitectura_SAD.md): componentes y conexiones presentes en el código, con limitaciones explícitas.
5. [Manual de usuario](03_Manual_de_Usuario.md): pasos para los flujos implementados y fallos previsibles.
6. [Plan de pruebas](04_Plan_de_Pruebas.md): escenarios y estados pendientes/aprobados.
7. [Resultados de pruebas](05_Resultados_de_Pruebas.md): bitácora de comprobaciones con fecha, alcance y evidencia.
8. [Guía de ejecución local](06_Guia_de_Despliegue.md): preparación de Android, API y PostgreSQL.
9. [CI/CD](07_CI_CD_Report.md): enlace a ejecuciones reales de GitHub Actions.
10. [Informe de Segunda Entrega — Tecnologías en Internet](Informe_Segunda_Entrega_TI.md): trazabilidad, arquitectura, persistencia, API, instalación y límites.

## Fuentes y evidencias

- [Backend API](../backend/README.md) y [aplicación Android](../mobile/README.md).
- [Fuentes de diagramas](../diagrams/README.md).
- [Instrucciones para registrar evidencia](../evidence/README.md).
- [Captura disponible del login](evidencias/01_login/README.md).
- [Referencias históricas](archive/README.md). No usar como descripción de la implementación actual sin revisar su estado.

## Criterio de vigencia

El SAD [v1.1 entregado](03_SAD_Arquitectura.md) se conserva como artefacto académico histórico y contiene decisiones que ya no representan el backend implementado. Para describir el código actual, usar [Arquitectura actual](02_Arquitectura_SAD.md) y [Resultados de pruebas](05_Resultados_de_Pruebas.md). Ningún documento convierte por sí solo un requisito en evidencia de cumplimiento.

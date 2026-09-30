# Índice del proyecto — Asistente MYPES Vinto

**Equipo:** Eva Chino Quispe, Christian Gonzales, Marializ Mamani, Kevin Rocha y Luis Lazo  
**Versión del índice:** 1.0 · 30/09/2026  
**Repositorio:** [GitHub](https://github.com/bibi567-ch/asistente-ia-mypes-vinto)

Este índice sigue como referencia de organización el [repositorio SMIA del equipo](https://github.com/bibi567-ch/Sistema_Municipal_de_Informacion_Ambiental-smia-): documentos numerados y separados por propósito, con requisitos, historias/backlog, arquitectura, persistencia y cronograma. El sistema de MYPES tiene otro dominio y otra arquitectura; no se copian la solución ambiental ni las tecnologías Angular, Django o PostGIS.

## Lectura recomendada

| Orden | Documento | Uso |
|---:|---|---|
| 01 | [Proyecto y alcance](docs/01_Proyecto_y_Alcance.md) | Problema, usuarios, MVP y límites |
| 02 | [Requisitos y UX](docs/02_Requisitos_y_UX.md) | Requisitos, casos de uso y flujos UX |
| 03 | [SAD de arquitectura](docs/03_SAD_Arquitectura.md) | Versión entregada, vistas y adenda de propuesta |
| 04 | [Decisiones de stack](docs/04_Decisiones_Stack.md) | Node.js + PostgreSQL, trade-offs y consultas docentes |
| 05 | [Product Backlog y equipo](docs/05_Product_Backlog_y_Equipo.md) | Épicas, tareas, responsables y aceptación |
| 06 | [Matriz de trazabilidad](docs/06_Matriz_Trazabilidad.md) | Requisitos, arquitectura y evidencia |
| 07 | [Auditoría de rúbricas](docs/07_Auditoria_Rubricas.md) | Estado de requisitos y brechas de entrega |
| 08 | [Cronograma y riesgos](docs/08_Cronograma_y_Riesgos.md) | Hitos, coordinación y riesgos R3/R6 |
| 09 | [Guía de entrega del repositorio](docs/09_Guia_Entrega.md) | Revisión previa a mostrar avances |
| 10 | [Registro de evidencias](docs/10_Evidencias.md) | Estado de diagramas, Figma y mediciones |
| 11 | [Plan de implementación y API](docs/11_Plan_Implementacion_y_API.md) | Orden vertical de construcción y contrato inicial |
| 12 | [Matriz de rúbricas de tres materias](docs/12_Matriz_Rubricas_Tres_Materias.md) | Cobertura y brechas académicas conocidas |

## Código y modelos

- [`backend/`](backend/README.md): scaffold Node.js/TypeScript/Express con conexión PostgreSQL y health check.
- [`mobile/`](mobile/README.md): guía de preparación; la aplicación Android todavía no está implementada.
- [`diagrams/`](diagrams/README.md): fuentes C1/C2, casos de uso y flujos; revisar el render antes de presentar.
- [`evidence/`](evidence/README.md): evidencias fechadas y anonimizadas.
- [`docs/archive/`](docs/archive/README.md): propuestas antiguas y duplicadas, solo como antecedente.

## Línea base técnica propuesta

Android Kotlin/Compose y SQLite local para operar offline; Node.js/TypeScript/Express + PostgreSQL para la API y persistencia remota. La sustitución de Spring por Node requiere aprobación y actualización formal del SAD. El sílabo de Tecnologías en Internet incluye MongoDB/Mongoose y producto web full-stack: falta confirmar con el docente si se exige un módulo NoSQL separado y una interfaz web. PostgreSQL no se declara equivalente a MongoDB.

# Asistente conversacional offline para MYPES de Vinto

Proyecto integrador de Ingeniería en Sistemas — Universidad Adventista de Bolivia — Gestión 2026-2.

**Actualizado:** 23/11/2026 · **Documentación de entrada:** este es el único `README.md` del repositorio.

## Estado del repositorio

El proyecto está en fase de validación técnica. El backend pasó sus pruebas automatizadas y compilación TypeScript; el build Android, la integración de sesión y las pruebas en dispositivos siguen pendientes. No debe exponerse a Internet ni usarse con datos reales hasta completar controles y revisión de seguridad.

## Arquitectura

La definición vigente está en el [SAD](docs/03_SAD_Arquitectura.md), las decisiones en el [documento de stack](docs/04_Decisiones_Stack.md) y los diagramas fuente en `diagrams/`.

## Inicio rápido

Requisitos: Node.js 24.x, npm y Docker Desktop con Compose.

1. Copia `.env.example` a `.env` y genera valores aleatorios para `POSTGRES_PASSWORD` y `JWT_SECRET`.
2. Desde la raíz, ejecuta:

```powershell
docker compose up --build -d
docker compose ps
Invoke-RestMethod http://127.0.0.1:3000/api/v1/health
```

Compose expone la API solo en `127.0.0.1:3000`. Para los pasos completos, ejecución sin Docker, credenciales ASR y pruebas, consulta la [guía de despliegue](docs/15_Guia_de_Despliegue.md). El contrato y los comandos de desarrollo están en [API e integración](docs/11_Plan_Implementacion_y_API.md).

## Documentación

El [índice documental](00_Indice.md) es la única lista canónica de documentos activos (01–16). Las referencias históricas están en `docs/archive/` y no definen el alcance vigente. El backlog, las responsabilidades y los próximos incrementos se mantienen en [Product backlog](docs/05_Product_Backlog_y_Equipo.md); las fechas y riesgos, en [Cronograma](docs/08_Cronograma_y_Riesgos.md).

## Componentes del repositorio

- `mobile/`: aplicación Android; su arquitectura y preparación están documentadas en los documentos 03 y 15.
- `backend/`: API Node.js/TypeScript/Express; su contrato vigente está en el documento 11.
- `diagrams/`: fuentes C1/C2 y flujos. Mantenerlas alineadas con el SAD y revisar el render antes de presentar.
- `evidence/`: capturas y mediciones anonimizadas con fecha, versión, entorno y pasos reproducibles.
- `docs/archive/`: referencias históricas, no normativas y fuera del alcance vigente.

## Diseño

- [Prototipo navegable en Figma](https://snake-dijon-92194767.figma.site/)
- [Sistema de diseño](https://www.figma.com/make/jr73cU7am5OisMNhkgxkkr/Sistema-de-Dise%C3%B1o-MYPES-Vinto?p=f&fullscreen=1)

## Reglas para colaborar

- No subir `.env`, tokens, contraseñas, datos reales ni audio identificable.
- Crear ramas pequeñas y pedir revisión antes de integrar cambios a `main`.
- Registrar decisiones que afecten stack, datos, sincronización o alcance mediante ADR.
- Mantener README, código, SAD y diagramas coherentes con la versión realmente construida.
- Separar **propuesto**, **diseñado**, **implementado**, **probado** y **evaluado**.
- Mantener credenciales y datos identificables fuera del repositorio.

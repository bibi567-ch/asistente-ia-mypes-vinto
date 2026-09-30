# Matriz de trazabilidad

**Proyecto:** Asistente Conversacional Offline para la Gestión de MYPES de Vinto  
**Versión:** 1.2 — 30/09/2026
**Estado:** trazabilidad documental; no implica que las funciones estén implementadas.

## Propósito y lectura de estados

Esta matriz conecta requisitos priorizados con flujos, decisiones y evidencia localizable. Los estados se limitan a **documentado**, **propuesto**, **en desarrollo** y **verificado**. Solo se utilizará “verificado” cuando exista evidencia reproducible, fechada y revisable. Los diagramas representan una arquitectura objetivo; tampoco demuestran implementación.

## Trazabilidad funcional

| Requisito | Flujo / historia | Componente objetivo | Evidencia en el repositorio | Estado |
|---|---|---|---|---|
| RF-004 reconocimiento de voz sin red | US-01 captura de voz | Android + Vosk local | `docs/02_Requisitos_y_UX.md`; `docs/03_SAD_Arquitectura.md` | Propuesto |
| RF-005 clasificación de intención | US-01 | Clasificador conversacional local | `docs/02_Requisitos_y_UX.md`; `docs/12_Plan_Implementacion_y_API.md` | Propuesto |
| RF-006 extracción de entidades | US-01 | Parser/NLU local | `docs/02_Requisitos_y_UX.md` | Propuesto |
| RF-007 confirmación antes de venta | US-02 registrar venta | Aplicación Android | `docs/02_Requisitos_y_UX.md` | Documentado |
| RF-009 guardar venta | US-02 | SQLite local y PostgreSQL remoto tras sincronizar | `docs/02_Requisitos_y_UX.md`; `backend/db/migrations/001_initial.sql` | Diseño documentado; código inicial |
| RF-013 actualizar inventario | US-03 compra/venta | Servicio de dominio + persistencia | `docs/02_Requisitos_y_UX.md`; `docs/12_Plan_Implementacion_y_API.md` | Propuesto |
| RF-017 consulta de stock | US-04 consulta | Aplicación local | `docs/02_Requisitos_y_UX.md` | Propuesto |
| RF-018 guardar operaciones sin conexión | US-05 Outbox | SQLite/Room | `docs/02_Requisitos_y_UX.md`; `docs/03_SAD_Arquitectura.md` | Propuesto |
| RF-019 sincronizar al recuperar conexión | US-05 | Android + API Node.js | `docs/02_Requisitos_y_UX.md`; `docs/12_Plan_Implementacion_y_API.md` | Propuesto |
| RF-020 preservar ventas concurrentes | US-05 conciliación | Idempotencia API + conciliación de stock | `docs/02_Requisitos_y_UX.md`; ADR en `docs/04_Decisiones_Stack.md` | Decisión propuesta |
| RF-021 resumen diario | US-06 dashboard | Cliente local/API | `docs/02_Requisitos_y_UX.md` | Propuesto |

## Trazabilidad de calidad

| Requisito | Escenario / medición | Decisión relacionada | Evidencia esperada | Estado |
|---|---|---|---|---|
| RNF-001 uso de memoria | Medir RAM en Android de referencia; objetivo <500 MB sujeto a protocolo | Kotlin nativo y presupuesto de recursos | Registro de dispositivo, versión y medición | No verificado |
| RNF-002 latencia | Medir captura a respuesta local; objetivo ≤2 s sujeto a protocolo | Inferencia local y consultas locales | Dataset/prueba y percentiles | No verificado |
| RNF-003 operación offline | Ejecutar flujos del MVP sin red y verificar persistencia | SQLite local + Outbox | Video/logs de prueba sin datos reales | No verificado |
| RNF-004 seguridad | Revisar cifrado local, TLS, secretos y controles de API | Seguridad por capas | Configuración y reporte de revisión | No verificado |
| RNF-005 accesibilidad | Contraste, tamaño táctil, texto y navegación | Diseño UX accesible | Capturas y checklist | Por validar |
| RNF-006 compatibilidad | Ejecutar en los Android acordados | Cliente nativo | Matriz dispositivo/versión | No verificado |
| RNF-007 usabilidad | SUS con participantes y protocolo aprobado; objetivo ≥70 | Diseño centrado en comerciante | Instrumento y resultados anonimizados | No verificado |

## Evidencias de arquitectura e integración

| Elemento | Ubicación | Qué demuestra actualmente |
|---|---|---|
| C1 contexto | `diagrams/C1_Contexto.mmd` | Contexto propuesto; revisar/renderizar antes de presentar |
| C2 contenedores | `diagrams/C2_Contenedores.mmd` | Contenedores objetivo y estado no implementado indicado en etiquetas |
| API y plan de construcción | `docs/12_Plan_Implementacion_y_API.md` | Contrato/orden de trabajo propuesto |
| Esquema PostgreSQL inicial | `backend/db/migrations/001_initial.sql` | DDL inicial, aún requiere revisión y aplicación local |
| Health check | `backend/src/routes/health.routes.ts` | Endpoint de estado del servicio/BD en el scaffold |
| Matriz académica | `docs/13_Matriz_Rubricas_Tres_Materias.md` | Relación documental con las tres materias; confirmaciones docentes pendientes |

## Pendientes para completar la trazabilidad

1. Asignar cada RF a un issue de GitHub y una prueba de aceptación.
2. Asociar requisitos UX con frames concretos de Figma y registrar acceso verificado.
3. Añadir casos de uso UML versionados y comprobar consistencia de IDs.
4. Para cada prueba, registrar fecha, commit, versión, dispositivo/entorno y resultado.
5. Cambiar un estado a “verificado” solo cuando una tercera persona pueda repetir la comprobación.
6. Confirmar con el docente si TI exige MongoDB/Mongoose además de la línea base unificada Node.js/PostgreSQL y si requiere frontend web.

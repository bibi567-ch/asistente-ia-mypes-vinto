# Tareas por integrante — ARQ-643 y TEC-644

**Rama de trabajo:** `arq-tec` (no subir directo a `main`)  
**Perfil de investigación:** documento **fuera** del repositorio  
**Fecha de asignación:** noviembre 2026

---

## Roles del equipo (referencia)

| Integrante | Rol |
|---|---|
| **Eva Lizeth Chino Quispe** | Líder, requisitos, QA documental, consolidación de informes |
| **Christian Gonzales Mamani** | IA / pipeline conversacional (Vosk, límites reales) |
| **Marializ Mamani Quispe** | Backend, seguridad, persistencia, JWT |
| **Kevin Jhonathan Rocha Rojas** | UX/UI, prototipo, front (React si se exige) |
| **Luis Gerardo Lazo Bustamante** | Arquitectura C4, QA técnico, diagramas, coherencia código–SAD |

---

## Evaluación rápida (qué falta según rúbricas)

### TEC — rúbrica 100 pts (especificación 2ª entrega)

| Criterio | Peso | Estado actual | Prioridad |
|---|---:|---|---|
| Repo público + estructura + README | 15% | Estructura `backend/`+`mobile/`; verificar **público** | Alta |
| JWT + RBAC | 20% | JWT + scrypt OK; RBAC débil | Alta |
| MongoDB/Mongoose | 20% | **No hay** — justificar + práctica o acuerdo docente | Crítica |
| C4 + secuencia JWT | 15% | Borrador en informe TEC | Media |
| API + Swagger | 15% | API sí; **Swagger no** | Crítica |
| Informe 9 secciones | 15% | Borrador en `docs/entregas/TEC_Segunda_Entrega_Informe.md` | Alta |

### ARQ — rúbrica patrón (guía patrones)

| Criterio | Pond. | Estado | Prioridad |
|---|---:|---|---|
| Justificación + escenarios calidad | 25% | Outbox + AQ-OUT-01/02 redactados | Alta |
| C4 Nivel 2 | 25% | Mermaid en informe ARQ | Alta |
| Prototipo ejecutable del patrón | 35% | Batch API idempotente sí; e2e móvil pendiente | Crítica |
| Documento + defensa | 15% | Borrador 5 secciones listo | Alta |

---

## Tareas por persona

### Eva — Documentación, trazabilidad, control de calidad

| # | Tarea | Materia | Entregable | Hecho cuando |
|---|---|---|---|---|
| E1 | Mantener repo en rama `arq-tec`; PR a main solo cuando el equipo revise | Ambas | PR con checklist | PR abierto y revisado |
| E2 | Completar carátula, fechas, enlace GitHub **público** en informes | TEC | Secciones 1 y 8 del informe TEC | Enlace abre en incógnito |
| E3 | Matriz RF → código actualizada sin inventar React | TEC | Tabla en informe TEC | Cada RF con estado real |
| E4 | Archivar evidencias en `evidence/` (plantilla de la matriz) | Ambas | EV-API-01, EV-SYNC-01, EV-MOB-* | Pasos + commit + captura |
| E5 | Coordinar pregunta formal al docente TEC (Mongo + React) y guardar respuesta | TEC | Nota en `docs/` o acta | Respuesta escrita del docente |
| E6 | Consolidar SAD v2 **como archivo nuevo** (no borrar v1.1) con Luis | ARQ | `docs/03_SAD_Arquitectura_v2.md` | Portada v2 + ADRs Node |

### Marializ — Backend, JWT, seguridad, persistencia

| # | Tarea | Materia | Entregable | Hecho cuando |
|---|---|---|---|---|
| M1 | Implementar **Swagger OpenAPI 3** (`/api-docs`) sobre rutas existentes | TEC | Swagger UI usable en local | Docente puede probar sin Postman |
| M2 | Documentar y, si aplica, endurecer **RBAC** (claims de rol en JWT + middleware) | TEC | Middleware + nota en informe | Ruta admin vs propietario diferenciada o límite declarado |
| M3 | Script/colección de prueba: login, products, batch idempotente | TEC/ARQ | Postman o `evidence/` | EV-API-01 y EV-SYNC-01 |
| M4 | Redactar justificación PostgreSQL vs Mongo y armar **lab mínimo Mongoose** si el docente no acepta solo justificación | TEC | `labs/mongo-practica/` o párrafo aprobado | Criterio 20% no en cero |
| M5 | Revisar `.env.example`, `.gitignore`, que no haya secretos en Git | TEC | Checklist seguridad | `git status` limpio de `.env` |
| M6 | Apoyar demo del batch Outbox en defensa ARQ | ARQ | Comandos reproducibles | Demo < 3 minutos |

### Luis — C4, SAD, coherencia, QA técnico

| # | Tarea | Materia | Entregable | Hecho cuando |
|---|---|---|---|---|
| L1 | C4 Nivel 2 final (Mermaid/PlantUML) alineado a Node + Android + Outbox | ARQ | Diagrama en informe + `diagrams/` | Coincide con carpetas reales |
| L2 | Diagrama de secuencia JWT (también sirve a TEC) | Ambas | En ambos informes | Pasos = código `auth/` |
| L3 | Redactar **SAD v2** (stack Node, ADR cambio Spring→Node, escenarios AQ-OUT) | ARQ | Archivo nuevo v2 | No modifica texto histórico v1.1 |
| L4 | Verificar que ningún documento diga Spring como stack actual | ARQ | Lista de archivos revisados | Solo v1.1 histórico lo menciona |
| L5 | Checklist de defensa: qué se demostra vs qué es diseño | ARQ | 1 página | Equipo ensaya con la lista |

### Kevin — UX / front

| # | Tarea | Materia | Entregable | Hecho cuando |
|---|---|---|---|---|
| K1 | Si el docente **exige React**: SPA mínima Vite+Tailwind (login + listado productos contra la API) | TEC | carpeta `client/` o `frontend/` | Login + GET products funciona |
| K2 | Si el docente **acepta Android**: documento de 1 página “Compose vs React” + capturas UI reales | TEC | PDF/MD en `docs/entregas/` | No se vende Figma como app |
| K3 | Flujo UX del Outbox: badge “pendiente de sync”, estados vacío/error | ARQ/TEC | Mock o pantalla | Usuario entiende estado offline |
| K4 | Revisar contraste/accesibilidad básica en pantallas a mostrar | TEC | Notas WCAG | Capturas en evidence |

### Christian — PLN / límites del producto

| # | Tarea | Materia | Entregable | Hecho cuando |
|---|---|---|---|---|
| C1 | Lista de lo que Vosk/NLU **sí** y **no** hace en el código actual | ARQ/TEC | Párrafo para defensa | Nadie promete precisión no medida |
| C2 | Casos de prueba de comandos de voz **después** de que venta por pantalla funcione | Proyecto | Guion de demo | Voz no bloquea la defensa de Outbox/API |
| C3 | Apoyar escenarios de calidad si el estímulo es “comando de voz en ruido” | ARQ | Fila opcional QS | Solo si hay medición |

---

## Orden de ataque (2 semanas tipo)

### Semana 1
1. Eva: repo público + pregunta al docente TEC (Mongo/React).  
2. Marializ: Swagger + evidencias API + batch idempotente.  
3. Luis: C4 + secuencia + inicio SAD v2.  
4. Kevin: espera decisión docente; prepara estructura `client/` por si acaso.  
5. Christian: hoja de límites PLN.

### Semana 2
1. Según respuesta docente: lab Mongo y/o React mínimo.  
2. Demo ensayada Outbox (API obligatoria, móvil si alcanza).  
3. Eva cierra informes PDF/MD y evidence.  
4. PR desde `arq-tec` → `main` solo con lo revisado.

---

## Mensaje corto para el grupo (copiar)

> Trabajamos en **`arq-tec`**. Perfil **fuera** del repo.  
> **TEC:** Swagger y evidencia API ya; Mongo y React **solo** con acuerdo del docente o práctica mínima — no mentir en la defensa.  
> **ARQ:** patrón **Outbox**; Luis diagramas + SAD v2; Marializ demo batch; Eva evidencia e informes.  
> Nadie sube a `main` sin revisión.

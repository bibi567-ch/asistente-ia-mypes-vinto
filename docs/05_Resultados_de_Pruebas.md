# Bitácora de evidencias y resultados

Registro incremental. Cada entrada distingue comprobaciones técnicas, pruebas automatizadas y pruebas funcionales. No se infieren resultados de un commit ni se inventan evidencias.

## Historial previo: funcionalidades y validaciones técnicas

- **Fecha:** anterior al 2026-11-10; fecha exacta no conservada.
- **Trabajo implementado:** ventas locales con persistencia transaccional de venta, stock y Outbox; rechazo de stock negativo; autenticación API separada del PIN local; sincronización de catálogo paginada; voz que propone datos pero requiere confirmación; deduplicación de productos de plantilla; y pantalla de reportes diarios.
- **Validaciones técnicas reportadas en la sesión previa:** build backend, chequeo de tipos, 8 pruebas unitarias, 6 pruebas de integración PostgreSQL y compilación Android debug finalizaron correctamente.
- **Límite de esta constancia:** no se conservaron el enlace de CI, la fecha exacta ni los logs primarios. Es un antecedente de trabajo, no evidencia de GitHub Actions ni prueba funcional manual. Las pruebas funcionales de Android permanecen pendientes de capturas.

## Fase 1: Cambios Android recientes

- **Fecha:** 2026-11-10
- **Qué se hizo:** se integraron el reporte diario, navegación desde el asistente de voz al formulario con sugerencias y la deduplicación de productos de plantilla.
- **Comprobación técnica:** `:app:assembleDebug` finalizó correctamente durante la validación del cambio. Esto solo confirma compilación; no confirma el comportamiento en un dispositivo.
- **Resultado funcional:** PENDIENTE de prueba manual y captura/video.
- **Evidencia:** pendiente; guardar en `evidencias/03_dashboard/`, `evidencias/04_ventas/` o `evidencias/06_chat_ia/` según el caso.

## Fase 2: Argumentos tipados para venta sugerida por voz

- **Fecha:** 2026-11-10
- **Qué se cambió:** el destino de venta recibe `producto` codificado y `cantidad` como texto numérico; la cantidad se valida y convierte a `Double` al entrar porque Navigation 2.8.3 no expone `NavType.DoubleType`. El formulario solo selecciona coincidencias normalizadas exactas.
- **Comprobación técnica:** `:app:assembleDebug` finalizó correctamente; la compilación no valida el flujo en un dispositivo.
- **Resultado funcional:** PENDIENTE de probar el dictado, confirmar el producto/cantidad y guardar evidencia en `evidencias/06_chat_ia/` y `evidencias/04_ventas/`.

## Fase 3: Backend - build, tipos y pruebas unitarias locales

- **Fecha:** 2026-11-10
- **Qué se probó:** desde `backend/`, se ejecutaron `npm run build`, `npm run typecheck:test` y `npm test`.
- **Resultado obtenido:** build correcto, chequeo de tipos correcto y 8 pruebas unitarias aprobadas (8 pasaron, 0 fallaron).
- **Alcance:** ejecución local en el commit `c187bba`; no es una ejecución de GitHub Actions.
- **Evidencia:** salida de consola observada en esta sesión; enlazar el workflow cuando se ejecute en GitHub. No hay captura añadida aún.

## Fase 4: Backend - pruebas de integración PostgreSQL

- **Fecha:** 2026-11-10
- **Qué se probó:** intento de preparar/migrar la base de pruebas local para ejecutar `npm run test:integration`.
- **Resultado obtenido:** BLOQUEADO; PostgreSQL rechazó la autenticación para el usuario `mypes_test`. No se pudo confirmar el resultado de integración local.
- **Acción pendiente:** ejecutar el workflow, que configura un PostgreSQL temporal con credenciales de prueba aisladas, y registrar su resultado real.
- **Evidencia:** pendiente; enlazar el workflow o guardar una captura anonimizada en `evidencias/08_backend/`.

## Fase 5: Pruebas funcionales en dispositivo

- **Fecha:** pendiente
- **Qué se probó:** pendiente de confirmación por el equipo.
- **Resultado obtenido:** PENDIENTE. No hay capturas/videos confirmados en `evidencias/`.
- **Evidencia:** añadir una entrada por escenario solo después de ejecutar la prueba y guardar su captura/video.

## Plantilla para nuevas ejecuciones

### Fase [n]: [módulo y escenario]

- **Fecha:**
- **Commit / versión:**
- **Qué se probó:**
- **Entorno y datos de prueba:**
- **Resultado esperado:**
- **Resultado obtenido:**
- **Estado:** PENDIENTE / APROBADO / FALLIDO / BLOQUEADO
- **Evidencia:** `evidencias/[carpeta]/[archivo]` o enlace al workflow de GitHub Actions.

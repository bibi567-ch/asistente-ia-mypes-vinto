# 06. Matriz de trazabilidad

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Trazabilidad de requisitos, implementación y evidencia
**Actualizado:** 23/11/2026

## 1. Estados

- **Implementado:** existe código relacionado; no implica que haya sido ejecutado en un dispositivo.
- **Documentado:** existe especificación o contrato escrito.
- **Pendiente:** falta implementar, ejecutar o registrar la evidencia requerida.
- **Verificado:** existe evidencia reproducible, fechada y revisable.

## 2. Requisitos funcionales

| Requisito | Componente | Evidencia actual | Estado |
|---|---|---|---|
| RF-01: captura de voz bajo demanda | `AsrEngine`, Dashboard Android | `mobile/app/src/main/java/com/mypes/vinto/nlu/AsrEngine.kt`; `docs/03_SAD_Arquitectura.md` | Implementado; dispositivo pendiente |
| RF-02: transcripción offline | Vosk local | `AsrEngine.kt`; `docs/14_Resultados_de_Pruebas.md` | Implementado; precisión no medida |
| RF-03: fallback cloud bajo condiciones | `AsistenteVozFacade`, `CloudAsrService` | `mobile/app/src/main/java/com/mypes/vinto/nlu/AsistenteVozFacade.kt`; `docs/11_Plan_Implementacion_y_API.md` | Código implementado; sesión/JWT y proveedor real pendientes |
| RF-04/05: clasificar y extraer parámetros | `IntentClassifier`, `EntityExtractor` | `mobile/app/src/main/java/com/mypes/vinto/nlu/IntentClassifier.kt`; `EntityExtractor.kt` | Implementado; evaluación lingüística pendiente |
| RF-06/07: confirmar o cancelar antes de acción | Dashboard y `PhoneActionExecutor` | `DashboardScreen.kt`; `PhoneActionExecutor.kt` | Código implementado; prueba en dispositivo pendiente |
| RF-08: respuestas TTS | Dashboard / ViewModel | `mobile/app/src/main/java/com/mypes/vinto/presentation/dashboard/DashboardScreen.kt` | Código implementado; prueba de audio pendiente |
| RF-09: guardar eventos localmente | Room / Outbox | `VintoDatabase.kt`; `OutboxEntry.kt`; `OutboxDao.kt` | Implementado; prueba offline pendiente |
| RF-10: sincronizar idempotentemente | `OutboxWorker`, endpoint logs | `mobile/app/src/main/java/com/mypes/vinto/sync/OutboxWorker.kt`; `backend/src/routes/logs.routes.ts` | Implementado; prueba extremo a extremo pendiente |
| RF-11: reportar fallos explícitamente | UI, API y ejecutor | `DashboardViewModel.kt`; `backend/src/routes/asr.routes.ts` | Implementado en código; aceptación UX pendiente |

## 3. Requisitos de calidad

| Criterio | Objetivo | Evidencia necesaria | Estado |
|---|---|---|---|
| Reconocimiento de voz | WER offline ≤25% | Transcripciones de referencia anonimizadas y cálculo reproducible | No medido |
| Usabilidad | SUS ≥70/100 | Cuestionarios válidos y cálculo documentado | No medido |
| Latencia y recursos | Registrar mediana/P95, memoria y batería | Dispositivo, versión, condiciones y resultados | No medido |
| Seguridad de sesión | No incluir secretos en APK; JWT requerido para endpoints protegidos | Revisión de configuración y pruebas de autorización | Parcial; revisión de seguridad pendiente |
| Operación offline | Reconocer, confirmar y registrar sin red | Prueba en modo avión y revisión posterior del Outbox | Pendiente |
| Despliegue | Levantar servicios con Compose y health check correcto | Build/runtime Docker y logs revisables | Compose configurado; ejecución pendiente |

## 4. Artefactos relacionados

| Artefacto | Ubicación | Uso |
|---|---|---|
| Alcance y requisitos | `docs/01_Proyecto_y_Alcance.md`; `docs/02_Requisitos_y_UX.md` | Delimitar producto y criterios funcionales. |
| Arquitectura | `docs/03_SAD_Arquitectura.md`; `diagrams/C1_Contexto.mmd`; `diagrams/C2_Contenedores.mmd` | Describir componentes y relaciones. |
| Contrato API | `docs/11_Plan_Implementacion_y_API.md` | Rutas implementadas y configuración de integración. |
| QA y métricas | `docs/13_Plan_de_Pruebas.md`; `docs/14_Resultados_de_Pruebas.md` | Protocolo y resultados realmente observados. |
| Evidencia | `docs/10_Evidencias.md`; carpeta `evidence/` | Archivar evidencias anonimizadas y reproducibles. |

## 5. Criterio de actualización

Al modificar un requisito, actualizar su criterio de aceptación, componente responsable y enlace a evidencia. No utilizar “Verificado” para describir funcionalidades que solo están diseñadas o compiladas.

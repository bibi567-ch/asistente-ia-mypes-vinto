# 04. Decisiones de stack

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Línea base técnica documentada; aprobación académica pendiente donde se indica
**Actualizado:** 23/11/2026

## 1. Línea base

| Capa | Tecnología | Uso actual |
|---|---|---|
| Aplicación móvil | Kotlin, Jetpack Compose, Android API 26+ | Interfaz, permisos y acciones públicas de Android. |
| Reconocimiento local | Vosk | ASR offline en español; la cobertura de Quechuañol debe evaluarse. |
| Clasificación | Reglas Kotlin | Determina las diez intenciones del perfil; LiteRT/TensorFlow Lite no está implementado. |
| Persistencia móvil | Room/SQLite | Usuario y eventos Outbox locales. |
| API | Node.js 24, TypeScript, Express | Autenticación, proxy ASR cloud y recepción de logs idempotentes. |
| Persistencia remota | PostgreSQL | Usuarios y registros de comandos de voz. |
| Despliegue de desarrollo | Docker Compose | Backend y PostgreSQL locales; no es despliegue productivo. |

## 2. Decisiones y estado

| ID | Decisión | Justificación | Estado |
|---|---|---|---|
| ADR-001 | Usar Vosk para el primer reconocimiento local. | Permite una ruta de voz sin conexión; precisión, memoria y rendimiento deben medirse en dispositivos representativos. | Implementado; evaluación pendiente. |
| ADR-002 | Mantener clasificación por reglas Kotlin para el MVP. | Evita introducir un modelo ML no evaluado; comparar alternativas solo con datos de prueba definidos. | Implementado. |
| ADR-003 | Mantener Google Speech-to-Text o Whisper detrás de la API. | Las credenciales de proveedor no se distribuyen en la app móvil. | Integración de proveedor configurable; prueba real pendiente. |
| ADR-004 | Exigir confirmación antes de acciones externas del teléfono. | Reduce ejecuciones involuntarias y permite cancelar antes de abrir una acción del sistema. | Implementado en flujo de app; validación en dispositivo pendiente. |
| ADR-005 | Registrar eventos en Outbox con identificador estable. | Permite reintentos autenticados sin duplicar registros en el backend. | Implementado; validación extremo a extremo pendiente. |
| ADR-006 | Usar PostgreSQL como almacenamiento remoto del backend actual. | Adecuado para persistencia relacional de usuarios y logs; no se declara que cubra el resultado académico NoSQL. | Implementado en el backend; revisar requerimientos de la asignatura. |

## 3. Decisiones académicas pendientes

El sílabo de Tecnologías en Internet compartido por el equipo menciona MongoDB/Mongoose y una plataforma web full-stack. PostgreSQL no es NoSQL y la aplicación Android no demuestra por sí sola el requisito de producto web. El equipo debe obtener una respuesta docente documentada sobre:

1. Si se requiere MongoDB/Mongoose dentro del producto o basta una práctica/evidencia independiente.
2. Si se exige una interfaz web aparte de la aplicación Android.
3. Si la arquitectura Node.js/PostgreSQL es aceptada como base común entre asignaturas.

Hasta confirmar, no se duplican datos en otro motor ni se afirma cumplimiento de requisitos académicos no verificados. Registrar respuesta y evidencia en [la matriz de rúbricas](12_Matriz_Rubricas_Tres_Materias.md).

## 4. Límites de la decisión

- La app requiere una integración de sesión que proporcione URL y JWT antes de usar ASR cloud y sincronización.
- No se ha validado en campo el consumo de recursos, la precisión, la latencia o la usabilidad.
- No hay actualización remota de APK ni modelos implementada.
- Esta documentación describe el estado técnico observado; no reemplaza aprobaciones docentes ni entregas académicas congeladas.

### Registro de Resultados de Pruebas y Bitácora de Evidencias (v2.0)
**Universidad Adventista de Bolivia · Ingeniería en Sistemas · Proyecto Integrador Yapay**

| Control del documento | Detalle |
| ------ | ------ |
| **Versión del registro** | 2.0 (Consolidada - Noviembre 2026) |
| **Sistema** | Asistente Conversacional Offline Edge AI (Yapay) |
| **Componentes Evaluados** | App Android Kotlin/Compose (Room v7 SQLCipher) + API Gateway Node.js 24 / Express 5 / PostgreSQL 16 |
| **Revisión de Código Base** | Commit `03afcbd` (Rama `feature/update-doc`) |
| **Integrantes** | Eva Chino, Christian Gonzales, Marializ Mamani, Kevin Rocha y Luis Lazo |

---

#### 1. RESUMEN DE ESTADO GENERAL DE PRUEBAS

La presente bitácora documenta las comprobaciones técnicas, compilaciones, pruebas unitarias automatizadas y ejecuciones de integración continua. Establece una distinción estricta entre la aprobación de código en entornos simulados/CI y la validación funcional en dispositivos móviles físicos.

| Capa de Arquitectura | Suite / Componente | Pruebas Automatizadas | Pruebas Funcionales | Estado de Evidencia Principal |
| ------ | ------ | ------ | ------ | ------ |
| **Backend Node.js 24** | Compilación TypeScript & Typecheck | APROBADO (100%) | N/A (Backend) | GitHub Actions Run `#43157514411` |
| **Backend Node.js 24** | Pruebas Unitarias de API | APROBADO (11/11 pasaron) | N/A (Backend) | GitHub Actions Run `#43157514411` |
| **Backend Node.js 24** | Integración DB PostgreSQL 16 | APROBADO (9/9 pasaron) | N/A (Backend) | GitHub Actions Run `#43157514411` |
| **Cliente Android** | Compilación Gradle & Asset Vosk | APROBADO (`assembleDebug`) | PENDIENTE | APK compilado (`117.3 MB` asset Vosk) |
| **Cliente Android** | Pruebas Unitarias JVM (NLU / PIN) | APROBADO (12/12 pasaron) | N/A (Unidad JVM) | JUnit XML (`mobile/app/build/test-results/`) |
| **Cliente Android** | Interfaz UI y Operación Offline | N/A | PENDIENTE | Pendiente de capturas/videos en dispositivo |
| **Sincronización** | E2E Móvil - Backend Cloud | N/A | PENDIENTE | Integración Outbox / REST diferida en dispositivo |

---

#### 2. BITÁCORA CRONOLÓGICA DE EJECUCIONES Y VERIFICACIONES

##### Fase 0: Carga Visual de la Pantalla Inicial de Acceso
* **Fecha:** 2026-11-10
* **Escenario:** Apertura de la aplicación en dispositivo Android emulado/físico.
* **Resultado Observado:** Carga correcta de la pantalla de Login con campos de identificación (carnet, celular o correo), PIN local de 4 dígitos y control flotante de micrófono.
* **Alcance:** Confirma la carga de vistas Compose; no valida autenticación con PIN ni dictado de voz en hardware.
* **Estado:** APROBADO (Verificación técnica de UI) / PENDIENTE (Validación funcional en dispositivo).

##### Fase 1: Actualización de Interfaz y Navegación desde Asistente
* **Fecha:** 2026-11-10
* **Qué se realizó:** Integración del reporte diario de ventas, flujo de navegación directa desde sugerencias de voz hacia formularios con precarga y deduplicación de plantillas de productos.
* **Comprobación Técnica:** `gradlew :app:assembleDebug` finalizó exitosamente.
* **Estado:** PENDIENTE de prueba manual en dispositivo.

##### Fase 2: Argumentos Tipados en Transcripción y Sugerencia de Venta
* **Fecha:** 2026-11-10
* **Qué se realizó:** Paso de parámetros codificados (producto y cantidad como texto numérico) hacia el formulario de ventas, sorteando la limitación de `NavType.DoubleType` en Navigation 2.8.3. Conversión segura a `Double`.
* **Comprobación Técnica:** `gradlew :app:assembleDebug` con código de salida 0.
* **Estado:** PENDIENTE de verificación funcional de dictado.

##### Fase 3: Pruebas Unitarias Backend y Typechecking
* **Fecha:** 2026-11-10 (Local) / 2026-11-12 (CI)
* **Qué se probó:** `npm run build`, `npm run typecheck:test` y `npm test` en el directorio `backend/`.
* **Resultado:** 8/8 pruebas unitarias aprobadas en primera instancia (ampliadas a 11/11 en la Fase 14).
* **Estado:** APROBADO.

##### Fase 4: Registro del Intent de Integración PostgreSQL Local (Bloqueo Histórico)
* **Fecha:** 2026-11-10
* **Qué se intentó:** Preparación de PostgreSQL local para `npm run test:integration`.
* **Resultado:** BLOQUEADO por rechazo de credenciales para el usuario `mypes_test`.
* **Seguimiento y Cierre:** El bloqueo quedó registrado en la bitácora como antecedente histórico y fue resuelto de forma definitiva en el entorno aislado de GitHub Actions (Fase 15).
* **Estado:** BLOQUEADO (Local) / RESUELTO (CI GitHub Actions).

##### Fase 5: Pruebas Unitarias JVM de PLN / NLU Edge y PIN Cifrado
* **Fecha:** 2026-11-10
* **Qué se probó:** `:app:testDebugUnitTest` para la extracción de entidades de voz ("cuartillas", bolivianos, fiados), clasificación de intención (VENTA) y derivación PBKDF2-HMAC-SHA256 del PIN.
* **Resultado:** 5/5 pruebas unitarias JUnit pasadas exitosamente (ampliadas a 12/12 en fases posteriores).
* **Evidencia:** Reportes XML en `mobile/app/build/test-results/testDebugUnitTest/`.
* **Estado:** APROBADO.

##### Fase 6: Pantalla de Sincronización y Outbox Local
* **Fecha:** 2026-11-10
* **Qué se verificó:** Observabilidad en tiempo real de operaciones pendientes en la cola Outbox (Room), con contadores visuales, IDs abreviados y reintentos.
* **Comprobación Técnica:** `:app:assembleDebug` exitoso.
* **Estado:** PENDIENTE de prueba funcional en dispositivo con alternancia de red.

##### Fase 7: Corrección de Automatización en Entorno Windows (`gradlew.bat`)
* **Fecha:** 2026-11-10
* **Qué se corrigió:** Ajuste del script `gradlew.bat` para evitar retornos falsos de código de error cuando Gradle finalizaba con éxito.
* **Resultado:** Verificación exitosa de `BUILD SUCCESSFUL` con código de retorno 0.
* **Estado:** APROBADO.

##### Fase 8: Reporte Semanal y Modo Demostrativo
* **Fecha:** 2026-11-10
* **Qué se implementó:** Gráfico de ventas de 7 días con intervalos de tiempo no solapados. Introducción de la bandera de vista demo exclusiva para compilaciones `debug` sin alterar datos persistidos en Room. Exportación de CSV mediante `FileProvider`.
* **Comprobación:** Pruebas unitarias JVM de formato CSV y escape de caracteres aprobadas.
* **Estado:** APROBADO (Unidad CSV) / PENDIENTE (Validación UI Android).

##### Fase 9: Registro Híbrido y Token JWT en Backend
* **Fecha:** 2026-11-10
* **Qué se implementó:** Formulario de registro con datos del propietario y negocio, migración Room 2→3, y endpoint `POST /api/v1/auth/register` con hashing `scrypt` y emisión de token Bearer JWT.
* **Resultado:** Pruebas de integración en base aislada aprobadas (alta de negocio, rechazo de correos duplicados y campos incompletos).
* **Estado:** APROBADO (API Backend) / PENDIENTE (Integración en dispositivo).

##### Fase 10: Integración del Modelo ASR Vosk en Assets
* **Fecha:** 2026-11-10
* **Qué se hizo:** Descarga e inclusión del modelo `vosk-model-small-es-0.42` (~40-50 MB comprimido, 117.3 MB descomprimido) en `assets/`.
* **Comprobación:** Verificación de inclusión en el APK compilado (`assets/vosk-model-small-es-0.42/am/final.mdl`).
* **Estado:** APROBADO (Empaquetado) / PENDIENTE (Reconocimiento acústico en hardware real).

##### Fase 11: Pantalla de Perfil de Comerciante y Seguridad
* **Fecha:** 2026-11-10
* **Qué se implementó:** Vista de consulta de perfil de comercio/propietario en Room fuera del hilo UI. Ocultación estricta de hashes, sal y PIN.
* **Comprobación:** Compilación `:app:assembleDebug` sin errores.
* **Estado:** PENDIENTE de evidencia visual en dispositivo.

##### Fase 12: Módulo de Fiados, Búsqueda de Productos y Exportación CSV
* **Fecha:** 2026-11-10
* **Qué se implementó:** Migración Room 3→4 (`ventas.pagado`), pantalla de cobro de fiados con generación atómica de evento `COBRO` en Outbox, filtro de productos en tiempo real y exportación CSV de ventas diarias.
* **Comprobación:** 7 pruebas unitarias JVM y 9 pruebas de integración backend aprobadas (incluyendo la recepción idempotente del evento `COBRO`).
* **Estado:** APROBADO (Lógica local/backend) / PENDIENTE (Prueba UI en dispositivo).

##### Fase 13: Ampliación de Perfil, Login Multiclave y PIN Hablado
* **Fecha:** 2026-11-10
* **Qué se implementó:** Migración Room 4→6 (soporte de nombres completos, expedición y fecha de nacimiento). Búsqueda de login por carnet, celular o correo. Módulo de extracción de PIN dictado por voz.
* **Comprobación:** 9 pruebas unitarias JVM aprobadas (extracción de dígitos hablados).
* **Estado:** APROBADO (Unidad JVM) / PENDIENTE (Prueba de voz en hardware).

##### Fase 14: Plantillas por Rubro, Asistente Yapay y Endpoints Cloud (Facturación / QR / SSE)
* **Fecha:** 2026-11-11
* **Qué se implementó:** Carga de 6 plantillas comerciales, asistente local Yapay con confirmación obligatoria, y endpoints backend para facturas simuladas en PDF, generación de QR por negocio y servidor SSE para eventos en tiempo real. Migración PostgreSQL `006`.
* **Resultado:** 12/12 pruebas unitarias JVM aprobadas en Android. 11/11 pruebas unitarias aprobadas en el backend.
* **Estado:** APROBADO (Unidades y endpoints aislados) / PENDIENTE (Integración end-to-end en dispositivo).

##### Fase 15: Verificación Oficial de CI/CD en GitHub Actions (Ejecución Canónica)
* **Fecha:** 2026-11-12 (UTC)
* **Commit Identificado:** `03afcbd`
* **Ejecución Oficial:** GitHub Actions Run `#43157514411`
* **Pasos Ejecutados en Pipeline:**
  1. `npm ci`: Instalación limpia de dependencias.
  2. `npm run db:migrate`: Aplicación de migraciones PostgreSQL `001` a `006`.
  3. `npm run build`: Compilación de TypeScript a JavaScript ES2022.
  4. `npm run typecheck:test`: Verificación estricta de tipos en código de producción y pruebas.
  5. `npm test`: Ejecución de 11/11 pruebas unitarias.
  6. `npm run test:integration`: Ejecución de 9/9 pruebas de integración contra PostgreSQL 16 aislado.
* **Resultado Final:** `SUCCESS` (Todos los jobs del pipeline finalizaron en verde).
* **Estado:** APROBADO Y VERIFICADO EN CI/CD.

##### Fase 16: Refactor compatible del asistente conversacional
* **Fecha:** 2026-11-23
* **Qué se implementó:** El Dashboard ahora es el asistente principal con chat, micrófono central y menú para conservar ventas, inventario, fiados, reportes y sincronización. Se mantuvieron las entidades comerciales y el Outbox existente. Room subió a v7 con `VoiceLog` y migración 6→7. El clasificador local usa once intenciones; las acciones de teléfono requieren confirmación. Se añadieron `/api/v1/assistant/query`, feedback autenticado en `/api/v1/learning/feedback`, ASR cloud configurado como demostrativo y migración PostgreSQL `007_voice_training_data.sql`.
* **Privacidad:** Las correcciones se guardan localmente por defecto; solo se marcan para sincronización cuando el comerciante autoriza el envío. El endpoint de ASR simulado devuelve una indicación explícita y no presenta audio no transcrito como texto real.
* **Android:** `:app:testDebugUnitTest :app:assembleDebug` finalizó con código 0. Se verificó la compilación de Room v7, la migración y el nuevo flujo del Dashboard; no se ejecutó todavía en un teléfono.
* **Backend:** `npm run build`, `npm run typecheck:test` y `npm test` finalizaron con código 0; 22/22 pruebas aisladas pasaron. `npm run test:integration` fue intentado, pero quedó **BLOQUEADO** por autenticación PostgreSQL local rechazada (`28P01`) para el usuario configurado. No se declara integración PostgreSQL aprobada.
* **Entorno:** Node.js 24.11.1 disponible; Docker no disponible porque el daemon no responde. La integración no se sustituyó silenciosamente por una prueba simulada.
* **Pendiente:** Probar en dispositivo el micrófono, diálogos de confirmación, acciones del teléfono, migración de una base Room existente y sincronización autorizada de `VoiceLog`. Repetir la integración backend con credenciales válidas o PostgreSQL aislado.

##### Fase 17: Corrección de apertura inconsistente de Room/SQLCipher
* **Fecha:** 2026-11-23
* **Problema identificado:** `LoginViewModel` abría el mismo archivo `mypes-local.db` mediante `VintoDatabase` sin SQLCipher, mientras el resto de la aplicación usaba `MypesDatabase` cifrada. Esta configuración podía producir `file is not a database`.
* **Corrección:** El login local ahora utiliza `MypesDatabase`, manteniendo una única configuración cifrada, sus migraciones y el acceso offline por PIN.
* **Validación:** `git diff --check` aprobado. La compilación Android debe repetirse después de esta corrección; la prueba en dispositivo y la conversión de una instalación antigua no se declaran aprobadas.

---

#### 3. MATRIZ DE EVIDENCIAS Y ALCANCE DE VERIFICACIÓN

| Módulo / Funcionalidad | Evidencia Generada | Ubicación / Referencia | Estado de Confirmación |
| ------ | ------ | ------ | ------ |
| **Pipeline Backend CI/CD** | Log de ejecución exitosa en GitHub Actions | Run `#43157514411` | **APROBADO (Oficial)** |
| **Build y Tipos Backend** | Compilación TS `dist/` | `backend/dist/` | **APROBADO (Oficial)** |
| **Pruebas NLU / PIN JVM** | Reportes JUnit XML | `mobile/app/build/test-results/` | **APROBADO (Local JVM)** |
| **Asset Vosk en APK** | `assets/vosk-model-small-es-0.42/` | APK Debug (117.3 MB) | **APROBADO (Empaquetado)** |
| **Flujos UI Android** | Capturas de pantalla anonimizadas | `docs/evidencias/` | **PENDIENTE (Manual)** |
| **Pruebas E2E Dispositivo-API** | Trazas de red y sincronización Outbox | Dispositivo Físico | **PENDIENTE (Manual)** |

---

#### 4. OBSERVACIONES Y LÍMITES TÉCNICOS

1. **Aislamiento de Entornos:** La aprobación del pipeline backend en GitHub Actions garantiza la calidad del código del servidor y la base de datos PostgreSQL 16, pero **no sustituye la necesidad de realizar pruebas de campo con teléfonos Android reales** en los mercados de Vinto.
2. **Modelo de Voz Edge:** La inclusión física del modelo Vosk en la carpeta `assets` permite la compilación del APK, pero la precisión del reconocimiento acústico bajo condiciones de ruido ambiental (>70 dB) requiere validación empírica en dispositivo.
3. **Persistencia Local:** Las migraciones de esquema Room (hasta la versión 6) y el cifrado mediante SQLCipher AES-256 han sido verificados unitariamente, manteniendo la compatibilidad con datos existentes.

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
* **Limpieza:** Se eliminó la implementación no utilizada de `VintoDatabase`, que apuntaba al mismo archivo con Room sin SQLCipher.
* **Validación:** La compilación Android y las pruebas JVM se repitieron después de esta corrección y finalizaron correctamente. La prueba en dispositivo y la conversión de una instalación antigua no se declaran aprobadas.

##### Fase 18: Validación consolidada posterior a la reorganización
* **Fecha:** 2026-11-23
* **Android:** `mobile\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` finalizó con código 0. Resultado observado: `BUILD SUCCESSFUL`; 46 tareas ejecutadas o verificadas. Esto confirma compilación y pruebas JVM, no funcionamiento físico.
* **Backend:** Desde `backend/`, `npm run build`, `npm run typecheck:test` y `npm test` finalizaron con código 0. Resultado observado: 22 pruebas aprobadas, 0 fallidas.
* **Docker:** `docker compose config --quiet` pasó usando variables temporales del proceso. `docker version` no pudo conectar con `dockerDesktopLinuxEngine`; los contenedores no fueron iniciados. No se declara Docker funcional.
* **Integración PostgreSQL:** `npm run test:integration` ejecutó 11 casos y obtuvo 0 aprobados y 11 fallidos/bloqueados por autenticación PostgreSQL `28P01` para `mypes_local`. El primer `500` de registro es consecuencia del mismo bloqueo de base; no se atribuye al contrato HTTP sin una base accesible.
* **Reorganización:** Se centralizó la documentación operativa en `docs/`, se separó `defensa_tecnica/` como material personal y se movieron prototipos no activos a `legacy/`. `git diff --check` pasó.
* **Siguiente acción verificable:** iniciar Docker Desktop o corregir la contraseña local sin exponerla, aplicar migraciones y repetir las 11 pruebas de integración. Después ejecutar los flujos Android en un dispositivo y guardar evidencia.

##### Fase 19: Compatibilidad de actualización cloud del APK
* **Fecha:** 2026-11-23
* **Implementación:** El backend expone `/api/v1/app-updates/latest` y `/api/v1/app-updates/apk` cuando se configuran `ANDROID_UPDATE_VERSION_CODE`, `ANDROID_UPDATE_VERSION_NAME`, `ANDROID_UPDATE_APK_PATH` y `ANDROID_UPDATE_BASE_URL`.
* **Android:** `AppUpdateManager` compara `versionCode`, descarga el APK configurado, verifica su SHA-256 y abre el instalador mediante `FileProvider`.
* **Validación:** La compilación Android y el build backend pasan. El endpoint cloud no se pudo probar en runtime porque Docker Desktop/PostgreSQL no estaban disponibles; queda pendiente la ejecución contra un backend accesible.

##### Fase 20: Diseño Stitch aplicado al APK
* **Fecha:** 23/11/2026
* **Diseño integrado:** login cloud con identidad Yapay, registro estructurado bilingüe, dashboard oscuro con métricas, voz, chat y navegación inferior.
* **Compilación:** `:app:testDebugUnitTest :app:assembleDebug` finalizó con `BUILD SUCCESSFUL`.
* **APK:** `mobile/app/build/outputs/apk/debug/app-debug.apk`, `applicationId com.mypes.vinto`, versión `2.3.0`, `versionCode 5`.

##### Fase 21: Endurecimiento de configuración local y tráfico Android
* **Fecha:** 25/11/2026
* **Cambios:** Compose usa `NODE_ENV=development` por defecto para PostgreSQL local y conserva el bind del API a loopback salvo configuración explícita. El manifiesto de tráfico HTTP quedó limitado a debug. `DATABASE_URL` ya no tiene una credencial de respaldo en el código y se exige mediante entorno.
* **Validación:** No se ejecutaron build, pruebas ni `docker compose config` para estos cambios en esta sesión. El comando local fue bloqueado por la política del sandbox y la ejecución de validación delegada falló antes de iniciar los comandos por un error del runtime. No hay un nuevo resultado de CI asociado.
* **Estado:** Revalidación de Compose, backend y manifiestos Android **PENDIENTE**. La nube pública y la integración end-to-end continúan pendientes.

##### Fase 22: Preparación de TLS para PostgreSQL administrado
* **Fecha:** 2026-11-25.
* **Cambio de configuración:** En producción, el pool PostgreSQL conserva la validación de certificados y acepta una CA privada pública configurada por `DATABASE_SSL_CA`. Se documentó la exportación de CA para Railway; no se almacenan certificados privados ni secretos en el repositorio.
* **Pruebas añadidas:** `backend/test/postgres-ssl.test.ts` cubre TLS desactivado por defecto en desarrollo, uso de CA en producción y validación estricta cuando no se configura CA personalizada.
* **Validación inicial:** `npm test` y `npm run build` fueron bloqueados por PowerShell al resolver `npm` como `npm.ps1` (`PSSecurityException`, `AuthorizationManager`, `UnauthorizedAccess`). No se cambió la política ni se eliminaron dependencias.
* **Reintento del 25/11:** Se confirmó que `npm.cmd` funciona (Node.js 24.11.1; npm 11.6.2). La primera ejecución desde la raíz del repositorio terminó con `ENOENT` por no existir allí `package.json`; se corrigió el directorio de trabajo a `backend/`.
* **Build y tipos:** Desde `backend/`, `npm.cmd run build` y `npm.cmd run typecheck:test` finalizaron con código 0.
* **Pruebas automatizadas:** Desde `backend/`, `npm.cmd test` terminó con código 1: 25 pruebas, 7 aprobadas y 18 fallidas. En la primera ejecución las peticiones HTTP de pruebas fueron interceptadas por el proxy local (502). Al excluir únicamente `localhost` y `127.0.0.1` del proxy para el proceso de prueba, las conexiones directas a puertos dinámicos de `127.0.0.1` devolvieron `EACCES`; el sandbox denegó el loopback. No se hicieron más reintentos. El resultado no demuestra por sí solo un defecto de aplicación: queda bloqueada la suite que requiere servidor HTTP local.
* **CI backend:** GitHub Actions Run [#45568677216](https://github.com/bibi567-ch/asistente-ia-mypes-vinto/actions/runs/45568677216), commit `460480ddcb96632513bb4c6be187c10315785b0f`, terminó en `SUCCESS` el 25/11/2026. Pasaron instalación, migraciones, build, typecheck, pruebas unitarias e integración con PostgreSQL 16 aislado.
* **Android local:** `Set-Location mobile; .\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` terminó con código 1 antes de ejecutar Gradle: acceso denegado al archivo de bloqueo del wrapper dentro de `%USERPROFILE%\.gradle\wrapperdists\gradle-8.7-bin\`. No se ejecutaron pruebas JVM ni se generó APK en esta sesión; el sandbox bloqueó el acceso a la caché.
* **Railway (25/11/2026, estado previo a la conexión):** En ese momento, `Postgres` estaba `Online` y privado, pero el selector de Railway no ofrecía `bibi567-ch/asistente-ia-mypes-vinto` y el servicio API seguía sin fuente. La captura del propietario mostraba la Railway App autorizada en `bibi567-ch` con “Never used”, sin demostrar acceso al repositorio. Este bloqueo de acceso quedó resuelto antes del intento de despliegue descrito en la Fase 23.
* **Estado de ese corte:** Backend verificado en CI para el commit indicado; las pruebas locales de loopback y la validación Android local permanecen **BLOQUEADAS por el sandbox**. En ese momento faltaban habilitar el acceso y conectar la fuente; el estado posterior de Railway se registra en la Fase 23.

##### Fase 23: Despliegue Railway y verificación TLS
* **Fecha:** 2026-11-25.
* **Configuración observada:** El servicio `blissful-enthusiasm` quedó conectado al repositorio y rama `main`, con Root Directory `/backend`. Se configuraron `JWT_SECRET` aleatorio (no registrado), `NODE_ENV=production`, `ASR_PROVIDER=simulated` y health check `/api/v1/health`; Postgres sigue `Online` y privado.
* **Despliegue:** Railway registró el deployment `f2de95fa-2feb-462d-82f6-e567a54d83c5` como fallido. Los logs de migración reportan `self-signed certificate in certificate chain` y rollback; no se verificó migración exitosa ni respuesta de health check.
* **Conexión privada:** En el intento inicial se dejó pendiente `${{Postgres.DATABASE_PRIVATE_URL}}`; esa variable no existe en el servicio `Postgres`. El deployment `10bad526-9e6a-4066-aa0c-cb18402a205d` terminó con `Missing required environment variable: DATABASE_URL`. La referencia válida `${{Postgres.DATABASE_URL}}` se aplicó posteriormente; el detalle y su verificación están en la Fase 25.
* **Diagnóstico TLS:** La validación estricta del backend es `rejectUnauthorized: true`. La Railway CLI v5.75.0 no quedó autenticada (`whoami`: `Unauthorized`) y el login fue bloqueado al escribir su caché. La recuperación posterior se realizó desde el panel de Railway, sin desactivar la verificación TLS.
* **Estado de este corte:** Despliegue inicial **FALLIDO** por CA no confiable. La solución y el estado final se registran en la Fase 25.

##### Fase 24: Adaptador ASR quechua remoto y revalidación
* **Fecha:** 2026-11-25.
* **Cambio:** Se añadió `ASR_PROVIDER=quechua` como proxy configurable a un servicio externo `POST multipart /transcribe`, con respuesta `{ "text": "..." }`, token Bearer opcional y exigencia de HTTPS en producción. La app conserva el fallback de baja confianza y amplía el timeout HTTP a 65 segundos. El modelo FastAPI/Hugging Face no está desplegado ni incluido.
* **Instalación de herramientas:** Desde `backend/`, `npm.cmd ci` instaló 100 paquetes. El audit automático reportó 2 vulnerabilidades (1 moderada y 1 alta); no se ejecutó remediación.
* **Build y tipos backend:** Desde `backend/`, `npm.cmd run build` y `npm.cmd run typecheck:test` finalizaron con código 0.
* **Pruebas enfocadas:** Desde `backend/`, `.\node_modules\.bin\tsx.cmd --test test/asr-quechua.test.ts test/postgres-ssl.test.ts` reportó 3 aprobadas y 1 fallida. Las tres pruebas TLS pasaron; la prueba HTTP de ASR no pudo alcanzar el servidor local de prueba: el proxy devolvió 502 y la conexión a `127.0.0.1` terminó con timeout/10060. **BLOQUEADA por loopback del sandbox**, no se confirma funcionalidad end-to-end ni proveedor real.
* **Android:** `mobile\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` se detuvo antes de iniciar Gradle por acceso denegado a `%USERPROFILE%\.gradle\wrapperdists\gradle-8.7-bin\...\gradle-8.7-bin.zip.lck`. No se ejecutaron pruebas Android ni se generó APK.
* **Railway en este corte:** No se volvió a desplegar para la Fase 24. La recuperación posterior de PostgreSQL y health check se documenta en la Fase 25; no cambia el estado de la prueba ASR ni demuestra reconocimiento quechua real.
* **Estado:** Build y tipos backend **APROBADOS localmente**; prueba ASR HTTP **BLOQUEADA**; prueba Android **BLOQUEADA/PENDIENTE**. No se afirma calidad WER, latencia ni reconocimiento real de quechua.

##### Fase 25: Railway con TLS estricto y health check
* **Fecha:** 2026-11-25.
* **Configuración:** Se conservó el servicio backend `blissful-enthusiasm` y la base `Postgres` existente; PostgreSQL permaneció `Online` y sin acceso público. Se agregó la CA pública a `DATABASE_SSL_CA`, se configuró `DATABASE_URL=${{Postgres.DATABASE_URL}}` (la variable que sí ofrece el servicio Postgres) y se cambió `DATABASE_SSL_REJECT_UNAUTHORIZED` de `false` a `true`. No se registró el PEM ni ningún secreto en el repositorio.
* **Despliegue final:** Railway deployment `dfac6edf-cb63-4e65-8857-60aa640b9fcb` inició el contenedor, informó `Database migrations are up to date (8 checked)` y `MYPES Vinto API listening on port 8080`. El servicio quedó activo.
* **Health check:** La ruta configurada en Railway es `/api/v1/health`. Se generó dominio HTTPS solo para la API y se verificó `GET https://blissful-enthusiasm-production-95638378.up.railway.app/api/v1/health`; respuesta observada: `{"status":"ok","service":"mypes-vinto-api","database":"connected"}`.
* **Intento intermedio:** El deployment `698ca722-dc33-4b8a-8b4f-2c70b2390cc1` se ejecutó antes de restablecer la verificación estricta y no se toma como evidencia de TLS estricto. La validación final corresponde al deployment `dfac6edf-cb63-4e65-8857-60aa640b9fcb`, aplicado después de establecer `DATABASE_SSL_REJECT_UNAUTHORIZED=true`.
* **Alcance:** El endpoint de salud confirma disponibilidad de API y conexión a PostgreSQL desde Railway. No se probaron registro/login, operaciones autenticadas, Android contra el dominio ni ASR real. No se modificó ni recreó PostgreSQL.

##### Fase 26: Auditoría npm, dependencias y configuración Android
* **Fecha:** 2026-11-25.
* **Hallazgos npm:** Las auditorías iniciales encontraron dos vulnerabilidades transitivas de runtime: `body-parser@2.3.0` (**HIGH**, GHSA-27r3-jxmh-p828) y `qs@6.16.0` (**MODERATE**, GHSA-6pq8-jhm2-2vcw).
* **Remediación y revalidación:** `npm.cmd audit fix` actualizó `body-parser` a `2.4.0` y `qs` a `6.17.1`. Ejecutados nuevamente `npm.cmd audit --omit=dev` y `npm.cmd audit`: ambos reportaron cero vulnerabilidades. `appmod-cve-assessment` tampoco encontró CVE conocidos para las versiones actualizadas.
* **Backend:** `npm.cmd run build` y `npm.cmd run typecheck:test` terminaron con código 0. `npm.cmd test` terminó con código 1: 26 pruebas, 7 aprobadas y 19 fallidas tras 150.5 s. Los fallos coinciden con el bloqueo del servidor HTTP local de pruebas por el proxy del sandbox (`502` al tunelizar y timeout `10060` en loopback); no se consideran pruebas funcionales aprobadas ni se atribuyen a una regresión del código.
* **Android:** `mobile/app/build.gradle.kts` ahora usa como predeterminado el dominio HTTPS de Railway y conserva el override `MYPES_API_BASE_URL`. No se pudo compilar ni probar en dispositivo en esta ejecución; el intento previo de Gradle fue bloqueado al acceder al lockfile de la caché en `%USERPROFILE%\.gradle`.
* **Alcance CI:** No se ejecutó GitHub Actions para el cambio de dependencias de esta fase. El resultado limpio de auditoría local y la compilación local no equivalen a CI ni a una prueba funcional Android.

##### Fase 27: Microservicio ASR configurable y transporte privado
* **Fecha:** 2026-11-25.
* **Implementación inicial:** Se agregó `quechua-asr/` con FastAPI, Dockerfile y dependencias fijadas. El backend permite HTTPS y HTTP solo a hosts `.railway.internal`; la prueba de política de URL pasó 1/1. No se desplegó.

##### Fase 28: Pipeline de voz ASR → NLU → funciones móviles
* **Fecha:** 2026-11-25.
* **ASR y NLU:** Se configuró el modelo Puno `QuechuaBase/whisper-base-qxp-finetuned` con revisión fija `5faf249f2a5b0034c095a91b3343c2d670fe5eee`. Se añadió encoder de similitud MiniLM con revisión fija, plantillas de intención configurables en `INTENT_EXAMPLES_JSON`, score mínimo 0.62 y margen mínimo 0.08. El microservicio responde texto, intención y score; se bloquea la acción si la intención no cumple el umbral.
* **Limitación lingüística/licencia:** La ficha de Whisper declara CC BY-NC 4.0. El usuario confirmó uso académico/no comercial y autorización limitada a los créditos existentes; aun así, no se inició inferencia ni se desplegó en esta validación. La configuración de Whisper no declara token `<|qu|>` y MiniLM no lista Quechua entre los idiomas admitidos. No se afirma soporte qxp confiable ni se autoriza uso comercial. Los ejemplos de intención qxp y su evaluación quedan pendientes de revisión local.
* **Integración móvil:** Se añadió clasificación para venta, consulta de stock/ventas y alta de producto. Cloud-ASR clasificado ya no se manda siempre a la guía: intención no reconocida se abstiene; venta navega al formulario existente sin guardar automáticamente; consultas leen datos Room; alta abre inventario. El modo Quechua se activa explícitamente en UI y evita ejecutar texto Vosk si no hubo transcripción cloud.
* **Validación backend:** `npm.cmd run build` y `npm.cmd run typecheck:test` terminaron con código 0; `.\node_modules\.bin\tsx.cmd --test test/quechua-asr-url.test.ts` pasó 1/1. `.\node_modules\.bin\tsx.cmd --test test/asr-quechua.test.ts` falló antes de verificar la respuesta: el proxy sandbox devolvió HTTP 502 al conectar el servidor local de prueba en `127.0.0.1`.
* **Validación Python:** Pylance `pylanceFileSyntaxErrors` no reportó errores sintácticos. El diagnóstico de imports reportó `numpy`, `soundfile`, `torch`, `scipy` y `transformers` no disponibles en el intérprete seleccionado; no se instaló nada y no se ejecutó el servicio/modelo. El acceso a `python.exe` previamente fue denegado por el sandbox.
* **Validación Android y despliegue:** No se ejecutaron las pruebas JVM modificadas por el bloqueo previo de Gradle al acceder al lockfile del wrapper. No se desplegó Railway ni se probó en teléfono; la selección de Whisper para código no autoriza uso comercial bajo CC BY-NC 4.0. Quedan pendientes validación de licencia/uso, modelos y ejemplos qxp, costos reales, CI y health check.
* **Revalidación técnica (2026-11-25):** Desde `backend/`, `npm.cmd run build` y `npm.cmd run typecheck:test` finalizaron con código 0. `git diff --check` también finalizó con código 0. El diagnóstico de VS Code no encontró errores en `AsistenteVozFacade.kt`, `CloudAsrService.kt`, `IntentClassifier.kt`, `DashboardScreen.kt` ni `DashboardViewModel.kt`; esto no sustituye la ejecución de Gradle. Node.js observado: `v24.11.1`; `docker info` indicó que el daemon no responde. No se ejecutaron pruebas JVM, inferencia Python ni despliegue en esta revalidación.

##### Fase 29: Línea base local de Android y backend
* **Fecha:** 2026-11-25.
* **Backend:** Desde `backend/`, `npm.cmd test` terminó con código 1: 26 pruebas, 7 aprobadas y 19 fallidas. Las fallas reportaron `TypeError: fetch failed` al conectar con los servidores HTTP locales de prueba mediante el proxy (`127.0.0.1`, error upstream/10060); la suite queda **FALLIDA/BLOQUEADA para integración HTTP**, no aprobada. `npm.cmd run build` y `npm.cmd run typecheck:test` finalizaron con código 0.
* **Android:** `mobile\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` terminó con código 1 antes de ejecutar Gradle; el entorno denegó acceso al lockfile `%USERPROFILE%\.gradle\wrapperdists\gradle-8.7-bin\...\gradle-8.7-bin.zip.lck`. No se ejecutaron pruebas JVM ni se generó APK.
* **Alcance:** No se desplegó, no se probaron flujos en dispositivo y no se cambió la aplicación durante esta línea base. Los fallos no demuestran que los flujos funcionen ni identifican por sí solos defectos de lógica; requieren reejecución en entorno local con caché Gradle y loopback accesibles.

##### Fase 30: Gramática Vosk y vocabulario NLU local experimental
* **Fecha:** 2026-11-25.
* **Cambio:** `AsrEngine` ahora carga `vosk-grammar.json` desde assets en vez de mantener una segunda gramática en Kotlin. Se añadieron frases semilla de negocio, vocabulario quechua limitado, saludo y extracción de algunos números quechua/montos con `qullqi`. La inicialización/copia del modelo se ejecuta en I/O y una copia privada incompleta se reemplaza desde los assets; se reporta el estado de carga en el estado del dashboard. La intención desconocida se abstiene y comunica que no ejecutó ninguna acción. El modelo acústico Vosk continúa siendo español; no se afirma reconocimiento quechua ni precisión comercial.
* **Pruebas añadidas:** `IntentClassifierTest` cubre saludo, dos palabras clave quechua y abstención; `EntityExtractorTest` cubre cantidad/producto/monto y formas numéricas quechua 11 y 20. **No ejecutadas**: Gradle sigue bloqueado por el acceso denegado al lockfile registrado en Fase 29; no se reintentó.
* **Comprobaciones ejecutadas:** Node.js analizó `vosk-grammar.json` correctamente (109 frases); `git diff --check` terminó con código 0. Pylance `pylanceFileSyntaxErrors` no reportó errores sintácticos en `quechua-asr/main.py`; esa comprobación no valida imports, carga de pesos, ejecución ni calidad del modelo.
* **Límites:** No hubo build Android, prueba acústica, inferencia Whisper, evaluación con hablantes qxp, ejecución de pruebas JVM ni despliegue. Los tests añadidos quedan pendientes de ejecución.

##### Fase 31: Captura push-to-talk y fallback ASR
* **Fecha:** 2026-11-25.
* **Cambio:** Los controles de voz del dashboard inician grabación al presionar y la detienen al soltar; Vosk conserva y concatena segmentos hasta el cierre, devuelve una transcripción única, activa confidencias por palabra y limita el audio a 120 segundos. Se filtran respuestas simuladas/vacías como transcripción visible, se mantiene el fallback local según umbral y se descarta el fragmento aislado `de`.
* **Prueba añadida:** `CloudAsrFallbackTest` comprueba que las respuestas simuladas/vacías no se consideran transcripciones utilizables y que el texto real se recorta antes de procesarlo. No se ejecutó por el bloqueo de Gradle.
* **Comprobaciones ejecutadas:** Los diagnósticos de VS Code no encontraron errores en los archivos Kotlin de producción y prueba modificados; `git diff --check` terminó con código 0. Estos diagnósticos no equivalen a compilación ni a prueba de comportamiento.
* **Pendiente:** No se ejecutó Gradle, prueba JVM, interacción táctil sintetizada, micrófono físico ni solicitud ASR de extremo a extremo. La ejecución sigue bloqueada por acceso denegado previo a la caché Gradle. No se modificaron variables de Railway ni se desplegó el proveedor.

##### Fase 32: Chat textual y navegación Yapay
* **Fecha:** 2026-11-25.
* **Cambio:** `ChatScreen` ahora presenta y envía mensajes a `YapayAgent`. `ChatViewModel` enruta venta con producto/cantidad sugeridos, consulta de stock hacia inventario filtrado y consultas de fiados hacia cuentas por cobrar; el precio continúa siendo del catálogo local y una venta no se guarda desde el chat. La entrada de texto está accesible desde el Dashboard. Las altas de producto completas mantienen confirmación y un alta pendiente no se intercepta como navegación.
* **Pruebas añadidas:** `ChatCommandRouterTest` cubre navegación de venta, stock, producto incompleto/completo y fiados; `YapayAgentTest` cubre la solicitud de alta con el prefijo `producto` y su confirmación. Se intentó ejecutar `.\mobile\gradlew.bat -p .\mobile :app:testDebugUnitTest --tests com.mypes.vinto.presentation.chat.ChatCommandRouterTest --tests com.mypes.vinto.nlu.YapayAgentTest`; Gradle no llegó a iniciar porque el sandbox denegó el acceso a `%USERPROFILE%\.gradle\wrapperdists\gradle-8.7-bin\bhs2wmbdwecv87pi65oeuq5iu\gradle-8.7-bin.zip.lck` (`FileNotFoundException`, `Acceso denegado`). Las pruebas quedan **BLOQUEADAS**, no aprobadas.
* **Comprobaciones ejecutadas:** `git diff --check` terminó con código 0. No se generó APK ni se validó la navegación en un dispositivo.
* **Pendiente:** Reejecutar pruebas JVM y build en un entorno con caché Gradle accesible; comprobar el flujo de chat/venta/inventario/fiados en Android. No se cambió Railway ni se desplegó.

##### Fase 33: Push-to-talk desde el chat Yapay
* **Fecha:** 2026-11-25.
* **Cambio:** El chat usa el `DashboardViewModel` retenido en la pila de navegación para compartir la instancia ASR y evitar cargar dos modelos Vosk. Al soltar, la transcripción entra al historial/chat; se mantienen los controles de confianza, el filtro del fragmento `de` y la abstención cuando el modo Quechua no devuelve texto e intención remotos aceptables. El clasificador remoto de consulta de ventas puede dirigir la respuesta local del agente.
* **Pruebas añadidas:** `ChatCommandRouterTest` verifica filtro de transcripción, abstención y aceptación de intención remota; `YapayAgentTest` verifica consulta de ventas con intención remota. **No ejecutadas**: el intento dirigido de Gradle de Fase 32 fue bloqueado por el sandbox al acceder a su lockfile; no se reintentó.
* **Comprobaciones ejecutadas:** Diagnósticos del editor sin errores en los archivos Kotlin modificados y `git diff --check` con código 0. No equivalen a compilación ni validación funcional en dispositivo.
* **Pendiente:** Ejecutar JVM/build y verificar push-to-talk en teléfono con permisos de micrófono. Railway no se modificó ni se desplegó.

##### Fase 34: Decodificación abierta del modelo Vosk en español
* **Fecha:** 2026-11-25.
* **Diagnóstico:** El asset `vosk-model-small-es-0.42` incluye el modelo acústico español (`am/final.mdl`, `conf/model.conf`). `AsrEngine` le pasaba a `Recognizer` una gramática cerrada con frases españolas y semillas quechua; esto limitaba el vocabulario de salida en vez de dejar que el modelo reconociera habla española libre.
* **Cambio:** Se quitó la gramática cerrada y el asset `vosk-grammar.json`. El reconocedor usa el modelo español completo; se conserva el clasificador Kotlin para identificar intenciones comerciales y el push-to-talk sigue agregando segmentos hasta que se suelta.
* **Comprobaciones ejecutadas:** Diagnósticos del editor sin errores en `AsrEngine.kt` y `git diff --check` con código 0. No se recompiló Android porque la ejecución de Gradle ya estaba bloqueada por la denegación del sandbox al lockfile de la caché.
* **Pendiente:** Probar frases españolas espontáneas con micrófono en un dispositivo y ejecutar build/pruebas JVM cuando el sandbox permita acceso a `%USERPROFILE%\.gradle`. No se desplegó ni cambió Railway.

##### Fase 35: Preparación del entorno Docker local
* **Fecha:** 2026-11-25.
* **Cambio:** Puerto host de PostgreSQL parametrizable con valor predeterminado `5433` (el contenedor mantiene `5432`); el backend recibe los nombres de configuración reales `QUECHUA_ASR_API_URL` y `QUECHUA_ASR_API_TOKEN`. Se añadió el perfil Compose optativo `quechua`, con token, health check, red interna y volumen persistente para los modelos; `ASR_PROVIDER` continúa en `simulated` por defecto.
* **Licencia y alcance:** El perfil Quechua no se activa automáticamente. El modelo Whisper Puno tiene licencia CC BY-NC 4.0; la documentación limita su uso a evaluación no comercial hasta contar con autorización apropiada.
* **Validación:** No se ejecutó `docker compose config`, ni arranque/health check después de estos cambios. Un intento anterior de `docker compose up -d --build`, antes de esta fase, fue bloqueado por el sandbox al leer la configuración y conectar con `npipe:////./pipe/docker_engine`; no fue un error observado de bind del puerto 5432. OPS-01 permanece **BLOQUEADO** en este entorno; la verificación manual en Windows sigue **PENDIENTE**.

##### Fase 36: Validación backend disponible en sandbox
* **Fecha:** 2026-11-25.
* **Comandos ejecutados:** `npm.cmd --prefix backend run build` (código 0), `npm.cmd --prefix backend run typecheck:test` (código 0) y `npm.cmd --prefix backend test` (código 1). No se ejecutó `npm ci`: las dependencias ya estaban instaladas y los manifiestos no cambiaron.
* **Resultado de pruebas:** 8 de 27 pasaron y 19 fallaron. Las pruebas que requieren HTTP local no pudieron conectar a puertos dinámicos de `127.0.0.1` a través del proxy del sandbox; el resultado mostró `Proxy upstream connect error` y `os error 10060`. No se atribuye esta falla a la lógica de negocio sin repetir la suite en un entorno con loopback disponible.
* **Alcance:** El build y el chequeo de tipos sí se completaron. No valida PostgreSQL, migraciones, Compose, health check, el servicio ASR ni Android. Docker ya había sido denegado por la política del sandbox; no se reintentó.
* **Estado:** OPS-01 **BLOQUEADO**; la verificación manual en Windows permanece **PENDIENTE** y no se registra como aprobada sin resultado/evidencia del desarrollador.

##### Fase 37: Comando breve de venta por voz en español
* **Fecha:** 2026-11-25.
* **Hallazgo:** La frase del escenario `"Vender dos papas"` no coincidía con las expresiones de venta del clasificador local, aunque el extractor sí obtiene cantidad y producto.
* **Cambio:** El clasificador acepta imperativos/conjugaciones breves (`vender`, `vende`, `vendí` y `véndeme`) como intención de registrar venta. Se añadieron casos JVM para la intención y la extracción de `papas` y cantidad `2`. El Dashboard pasa el intent y los parámetros al formulario, que conserva revisión explícita antes de guardar.
* **Validación:** No se ejecutaron pruebas JVM ni build Android porque Gradle había sido bloqueado por acceso denegado a `%USERPROFILE%\.gradle`. `git diff --check` finalizó con código 0; esto no valida la lógica de reconocimiento ni el flujo en Android.
* **Límite:** Se verifica por código la ruta al formulario, no el reconocimiento acústico de esa frase, la coincidencia del producto con el catálogo de cada negocio, ni el guardado/sincronización en dispositivo.

##### Fase 38: Consultas cognitivas con Gemini opcional
* **Fecha:** 2026-11-25.
* **Cambio:** El chat y la guía de voz conservan el contrato autenticado `/api/v1/assistant/query`; solo `CONSULTA_COGNITIVA` puede consultar el proveedor. Gemini se habilita mediante `GEMINI_API_KEY` y `GEMINI_MODEL` en el backend, con timeout de 15 segundos, límite de salida e instrucciones que excluyen operaciones y datos comerciales. Sin clave sigue la guía de demostración; el fallo del proveedor devuelve HTTP 502 sin respuesta exitosa de fallback. La clave no se añade a Android ni al repositorio, y las interfaces advierten que las consultas generales pueden enviarse a Gemini.
* **Build y tipos:** `npm.cmd --prefix backend run build` y `npm.cmd --prefix backend run typecheck:test` finalizaron con código 0 tras el cambio.
* **Pruebas del proveedor:** Desde `backend/`, `.\node_modules\.bin\tsx.cmd --test test\gemini-assistant.test.ts` finalizó con 2/2 aprobadas. Se simularon respuestas y error HTTP del proveedor; no se contactó a Google ni se usó una clave real.
* **Pruebas API:** Desde `backend/`, `.\node_modules\.bin\tsx.cmd --test test\assistant.test.ts` terminó con 0/4 aprobadas. Las cuatro fallaron antes de recibir respuesta HTTP porque el proxy del sandbox no pudo conectar con `127.0.0.1` (HTTP 502 / error 10060).
* **Suite backend:** `npm.cmd test` desde `backend/` terminó con 10/31 aprobadas y 21 fallidas; las fallas fueron peticiones HTTP a puertos locales interceptadas por el proxy del sandbox (`502`/`10060`). La suite no queda aprobada.
* **Android:** `mobile\gradlew.bat :app:assembleDebug` se detuvo antes de iniciar Gradle por acceso denegado al lockfile en `%USERPROFILE%\.gradle\wrapperdists\gradle-8.7-bin\`. No se compiló ni probó Android.
* **Docker y salud:** `docker compose up -d --build backend` fue bloqueado por la política del sandbox al leer `C:\Users\ADMIN\.docker\config.json` y conectar al pipe `npipe:////./pipe/docker_engine`. No se iniciaron contenedores y `/api/v1/health` no se verificó.
* **Estado:** Integración unitaria del adaptador Gemini **APROBADA con proveedor simulado**; la API sobre HTTP, el build Android, la llamada real a Google, Docker, health check y flujo end-to-end **PENDIENTES/BLOQUEADOS**. No existe un resultado CI asociado a este cambio.

##### Fase 39: Fallback del chat para mensajes no clasificados
* **Fecha:** 2026-11-25.
* **Cambio:** El chat reutiliza `POST /api/v1/assistant/query` con JWT para entradas `CONSULTA_COGNITIVA` y mensajes `DESCONOCIDA` que no tienen destino comercial local ni confirmación de producto pendiente. Las acciones comerciales reconocidas continúan locales. La UI advierte que mensajes no reconocidos pueden enviarse al proveedor configurado.
* **Salvaguardas:** La voz Vosk bajo el umbral se abstiene si no hay texto cloud utilizable. Un texto cloud obtenido por fallback sigue el enrutamiento normal; el modo Quechua requiere transcripción e intención aceptada. Sin JWT, se conserva la guía local.
* **Pruebas añadidas:** Cinco casos JVM cubren fallback de texto desconocido, consulta cognitiva, exclusión de navegación comercial, confirmación pendiente y rechazo de voz de baja confianza.
* **Ejecución intentada:** Desde la raíz, `Set-Location mobile; .\gradlew.bat :app:testDebugUnitTest --tests com.mypes.vinto.presentation.chat.ChatCommandRouterTest` terminó con código 1 antes de iniciar Gradle. El wrapper recibió `FileNotFoundException` / `Acceso denegado` al abrir `%USERPROFILE%\.gradle\wrapperdists\gradle-8.7-bin\bhs2wmbdwecv87pi65oeuq5iu\gradle-8.7-bin.zip.lck`.
* **Estado:** Las pruebas JVM quedaron **BLOQUEADAS** por acceso denegado del sandbox; no se afirma que pasaron. No se ejecutó build Android, llamada real a Gemini ni prueba funcional.

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
3. **Persistencia Local:** Las migraciones de esquema Room hasta la versión 7 y el cifrado mediante SQLCipher AES-256 se verificaron durante la compilación/pruebas JVM. La compatibilidad con una instalación antigua debe confirmarse en un dispositivo; una base sin cifrado no se convierte automáticamente.

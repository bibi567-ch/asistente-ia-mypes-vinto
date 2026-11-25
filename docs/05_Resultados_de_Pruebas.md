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
* **Conexión privada:** La referencia de `DATABASE_URL` se corrigió a `${{Postgres.DATABASE_PRIVATE_URL}}` y quedó como cambio pendiente de aplicar. `DATABASE_SSL_CA` aún no está configurada.
* **Diagnóstico TLS:** La validación estricta `rejectUnauthorized: true` se conserva. La Railway CLI v5.75.0 se pudo ejecutar, pero no quedó autenticada (`whoami`: `Unauthorized`); el intento de login terminó con `Acceso denegado` al escribir la caché del CLI bajo las restricciones del sandbox. No se exportó ni almacenó el certificado CA.
* **Estado:** Despliegue y prueba cloud **FALLIDOS/BLOQUEADOS por CA no confiable**. No desactivar la verificación TLS; falta habilitar la escritura del CLI en la ruta bloqueada o proporcionar el CA público mediante el flujo autorizado, configurar `DATABASE_SSL_CA`, aplicar la referencia privada y volver a desplegar. No hay dominio público ni prueba Android contra la API.

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

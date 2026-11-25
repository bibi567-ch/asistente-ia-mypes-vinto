# Manual personal completo: cómo funciona y cómo ejecutarlo

Este documento explica el sistema completo para la defensa. Los comandos se ejecutan desde la raíz del repositorio salvo que se indique `cd backend` o `cd mobile`.

## 0. Registro histórico de comprobaciones del 23/11/2026

La tabla conserva un registro anterior; **no representa el estado de la última integración**. El estado más reciente disponible se resume a continuación y está detallado en la Fase 38 de [docs/05_Resultados_de_Pruebas.md](../docs/05_Resultados_de_Pruebas.md).

Estos son resultados ejecutados, no supuestos:

| Comprobación | Resultado observado |
|---|---|
| `mobile\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` | **PASS** — `BUILD SUCCESSFUL` |
| `backend`: `npm run build` | **PASS** |
| `backend`: `npm run typecheck:test` | **PASS** |
| `backend`: `npm test` | **PASS** — 22/22 |
| `docker compose config --quiet` | **PASS** con variables temporales |
| `docker version` | **BLOQUEADO** — daemon `dockerDesktopLinuxEngine` no disponible |
| `backend`: `npm run test:integration` | **BLOQUEADO** — PostgreSQL responde `28P01` para `mypes_local` |
| APK Android debug | **GENERADO** — `mobile\app\build\outputs\apk\debug\app-debug.apk`, `com.mypes.vinto`, versión `2.3.0` (`versionCode 5`) |

Una compilación no demuestra funcionamiento en dispositivo. Las pruebas de integración solo se pueden aprobar después de corregir el acceso a PostgreSQL o iniciar Docker Desktop y repetirlas.

### Estado posterior registrado el 25/11/2026

- Backend: `npm run build` y `npm run typecheck:test` pasaron; la suite `npm test` terminó **10/31**, bloqueada por loopback HTTP del sandbox.
- Proveedor Gemini con mocks: **2/2** pruebas enfocadas pasaron. No se llamó a Google ni se usó una clave real.
- Android: Gradle no llegó a iniciar porque el sandbox denegó el lockfile de la caché; no se generó APK en esa validación.
- Docker y `/api/v1/health`: no se pudieron verificar después de la integración Gemini.
- Railway: hubo un health check satisfactorio en un despliegue anterior; eso no demuestra el despliegue de la versión con Gemini.

Para localizar endpoints, modelos, rutas Retrofit y comandos por entorno, consultar el [mapa técnico](06_mapa_tecnico_apis_conexiones_ia.md) y el [contrato detallado](../.github/modernize/assessment/engines/facts/api-service-contracts.md).

La actualización cloud de Android ahora usa `GET /api/v1/app-updates/latest`. El backend publica únicamente el APK configurado mediante `ANDROID_UPDATE_APK_PATH`; Android compara `versionCode`, descarga el APK, verifica SHA-256 y abre el instalador del sistema. Esto no sustituye la firma del APK ni permite una actualización silenciosa.

## 1. Arquitectura completa

```text
Comerciante
   |
   v
Android Compose
   |-- Login local/PIN
   |-- Dashboard conversacional
   |-- Vosk + clasificador local
   |-- Room v7 + SQLCipher
   |-- Outbox + WorkManager
   |
   +---- cuando hay red ----> API Node.js/Express
                                |-- auth
                                |-- sync/transacciones
                                |-- assistant/query -> Gemini opcional
                                |-- asr/transcribe
                                |-- learning/feedback
                                v
                           PostgreSQL 16 privado/TLS
```

### Capas Android

1. **Presentación:** `MainActivity`, Compose, navegación y estados visibles.
2. **ViewModel:** coordina login, conversación, confirmación, TTS y sincronización.
3. **Dominio local:** `IntentClassifier`, `EntityExtractor`, `PhoneActionExecutor` y políticas de confirmación.
4. **Persistencia:** `MypesDatabase` Room v7 cifrada con SQLCipher; contiene Usuario, ventas, productos, Outbox y VoiceLog.
5. **Sincronización:** WorkManager lee pendientes, envía lotes y conserva reintentos.

### Capas backend

1. `src/server.ts` comprueba PostgreSQL antes de escuchar.
2. `src/app.ts` monta Express, JSON, rutas y errores HTTP.
3. `routes/` valida y atiende auth, sincronización, ASR, assistant, learning, productos, logs y funciones cloud.
4. `db/migrations/` crea tablas y restricciones.
5. `pg.Pool` mantiene la conexión a PostgreSQL.

### Correspondencia con el código adjunto

El código propuesto en la guía adjunta ya está integrado, pero no debe reemplazarse literalmente porque la implementación vigente agrega controles necesarios:

| Propuesta | Implementación vigente | Mejora conservada |
|---|---|---|
| `IntentClassifier.kt` | `mobile/app/src/main/java/com/mypes/vinto/nlu/IntentClassifier.kt` | Clasifica intenciones del teléfono y del negocio con reglas deterministas; usa `DESCONOCIDA` si no reconoce una expresión. |
| `PhoneActionExecutor.kt` | `mobile/app/src/main/java/com/mypes/vinto/nlu/PhoneActionExecutor.kt` | Extrae parámetros, solicita confirmación, comprueba permisos y usa marcador cuando no hay permiso de llamada. |
| `CloudAsrService.kt` | `mobile/app/src/main/java/com/mypes/vinto/nlu/CloudAsrService.kt` | Usa Retrofit, JWT, multipart, conectividad validada y marca explícitamente el modo ASR simulado. |
| `AsistenteVozFacade.kt` | `mobile/app/src/main/java/com/mypes/vinto/nlu/AsistenteVozFacade.kt` | Usa Vosk primero, deriva a cloud solo con baja confianza y sesión válida, y reporta errores en vez de ocultarlos. |
| `DashboardViewModel.kt` | `mobile/app/src/main/java/com/mypes/vinto/viewmodel/DashboardViewModel.kt` | Mantiene el estado de confirmación, TTS, correcciones autorizadas y consulta autenticada al backend. |
| `assistant.routes.ts` | `backend/src/routes/assistant.routes.ts` | Exige autenticación y valida pregunta, idioma y longitud antes de responder. |

Por esta razón, “funcional” significa compilado y probado automáticamente; las llamadas, alarmas, linterna, micrófono y sincronización todavía requieren una prueba en dispositivo y una PostgreSQL accesible.

### Correspondencia con el segundo texto adjunto

| Propuesta | Estado real |
|---|---|
| `DashboardScreen.kt` con chat, micrófono, TTS y confirmación | Ya existe en `mobile/app/src/main/java/com/mypes/vinto/presentation/dashboard/DashboardScreen.kt`. El TTS se coordina en el ViewModel para evitar dos motores hablando simultáneamente y la pantalla conserva menú, correcciones y confirmación. |
| `AsrEngine.kt` con Vosk | Ya existe en `mobile/app/src/main/java/com/mypes/vinto/nlu/AsrEngine.kt`. Captura PCM, calcula confianza desde el resultado Vosk, conserva audio para fallback y reporta errores de permisos/modelo. No se simula una confianza fija. |
| `OutboxWorker.kt` para aprendizaje | Ya existe en `mobile/app/src/main/java/com/mypes/vinto/sync/OutboxWorker.kt`. Conserva ventas y catálogo, y además envía únicamente `VoiceLog` autorizado; no inventa logs de prueba. |
| `ApiClient.kt` y DTOs | Ya existen APIs separadas para autenticación, sincronización, asistente y aprendizaje en `mobile/app/src/main/java/com/mypes/vinto/network/`. La URL se obtiene de `BuildConfig.API_BASE_URL`, no se fija en 4000, y el backend exige JWT. |
| `backend/src/app.ts` | Ya monta health, auth, ASR, assistant, learning, logs, catálogo y transacciones. No deben eliminarse las rutas comerciales que siguen usando Android. |

El texto adjunto sirve como explicación conceptual, no como parche para pegar. Reemplazarlo eliminaría ventas offline, catálogo, autenticación, validación de errores y sincronización idempotente.

Para mantener compatibilidad con ejemplos que esperan una API más simple, `DashboardViewModel` también expone `ChatMessage`, `historial`, `escuchando` y `onBotonMicrofonoPresionado()`. Esos nombres delegan al estado real y no crean un segundo motor de voz ni un segundo historial.

El manifiesto incluye `RECORD_AUDIO`, `INTERNET`, `ACCESS_NETWORK_STATE`, `CALL_PHONE`, `SET_ALARM` y `CAMERA`. El micrófono se solicita en tiempo de ejecución; las llamadas usan el marcador si el permiso telefónico no fue concedido, evitando un fallo silencioso.

## 2. Docker explicado

Docker no es la aplicación ni reemplaza el repositorio. Es el entorno reproducible para levantar PostgreSQL y el backend con versiones y variables controladas.

### Servicios del compose

- `postgres`: PostgreSQL 16 Alpine, base `mypes_vinto_dev`, usuario `mypes_local`, volumen persistente `postgres_data` y healthcheck.
- `backend`: construye `backend/Dockerfile`, escucha internamente en el puerto 3000 y se conecta a PostgreSQL usando el nombre de servicio `postgres`, no `localhost`.
- Los puertos se publican solo en `127.0.0.1`: el backend queda en `http://127.0.0.1:3000` y PostgreSQL en `127.0.0.1:5433` por defecto. PostgreSQL conserva el puerto interno `5432`; el backend se conecta a `postgres:5432` dentro de Compose.
- `ASR_PROVIDER` queda en `simulated` si no se configura un proveedor real.
- `quechua-asr` es opcional: solo se inicia con `--profile quechua`, usa el puerto interno 8000 y descarga los modelos a un volumen. No publica el servicio en el host.

### Preparar variables sin exponer secretos

Crear `.env` en la raíz a partir de `.env.example` y poner valores locales. Nunca guardar la contraseña real ni `JWT_SECRET` en Git.

```powershell
Copy-Item .env.example .env
# Editar .env de forma local; no copiar su contenido a la defensa ni a capturas
```

### Comandos Docker

```powershell
docker version
docker compose config
docker compose up -d --build
docker compose ps
docker compose logs --tail=100 postgres
docker compose logs --tail=100 backend
docker compose exec postgres pg_isready -U mypes_local -d mypes_vinto_dev
docker compose --profile quechua up -d --build
docker compose down
```

Antes de `docker compose up`, construir el APK y preparar las variables de actualización:

```powershell
Set-Location mobile
.\gradlew.bat :app:assembleDebug -PMYPES_API_BASE_URL=http://10.0.2.2:3000/
Set-Location ..
Copy-Item .env.example .env
# En .env usar una contraseña/JWT locales y confirmar:
# ANDROID_UPDATE_VERSION_CODE=5
# ANDROID_UPDATE_VERSION_NAME=2.3.0
# ANDROID_UPDATE_APK_PATH=/updates/app-debug.apk
# ANDROID_UPDATE_BASE_URL=http://10.0.2.2:3000
docker compose up -d --build
Invoke-WebRequest http://127.0.0.1:3000/api/v1/app-updates/latest
```

Para un teléfono físico, `ANDROID_UPDATE_BASE_URL` y `MYPES_API_BASE_URL` deben usar la IP LAN del equipo, no `10.0.2.2`. Compose publica el backend en `127.0.0.1` por defecto; para una prueba física local se requiere `API_BIND_ADDRESS=0.0.0.0` en el `.env`, acceso solo desde una LAN confiable y APK debug. No dejar el puerto expuesto ni usar HTTP en producción.

Interpretación:

- `docker version` confirma que el daemon responde; si falla, Docker Desktop no está listo.
- `docker compose config` valida YAML y variables, pero no inicia servicios.
- `up -d --build` crea/recrea imágenes y arranca ambos servicios.
- `ps` debe mostrar PostgreSQL saludable y backend iniciado.
- `logs` permite distinguir error de contraseña, migración, puerto o código.
- `down` detiene contenedores, pero conserva el volumen.
- Para borrar también los datos de PostgreSQL, usar `docker compose down -v` solo con datos de desarrollo y autorización explícita.

### Migraciones Docker

El contenedor del backend debe tener el proyecto compilado según su Dockerfile. Si se requiere ejecutar migraciones dentro del contenedor:

```powershell
docker compose exec backend npm run db:migrate
```

No ejecutar `down -v` como “solución” a un error de aplicación: elimina la base de demostración.

## 3. Backend sin Docker

Usar esto cuando PostgreSQL ya corre localmente:

```powershell
Set-Location backend
Copy-Item .env.example .env
npm ci
npm run db:migrate
npm run build
npm run typecheck:test
npm test
npm run test:integration
npm run dev
```

Comprobación de salud:

```powershell
Invoke-WebRequest http://127.0.0.1:3000/api/v1/health
```

Si aparece `28P01`, PostgreSQL rechazó usuario/contraseña. No es una aprobación parcial: corregir las variables o usar un PostgreSQL aislado y repetir migraciones e integración.

## 4. Android

```powershell
Set-Location mobile
.\download-vosk-model.ps1
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug -PMYPES_API_BASE_URL=http://10.0.2.2:3000/
```

Para instalar el APK con ADB:

```powershell
adb devices
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
```

Estos comandos mantienen la terminal dentro de `mobile/`; por eso el APK se instala desde `.\app\build\outputs\apk\debug\app-debug.apk`. No uses archivos de `intermediates/` ni el APK de otro proyecto. La aplicación actual se identifica como `com.mypes.vinto`, versión `2.3.0`. Si Android Studio muestra una versión anterior, abre la carpeta `mobile/`, selecciona la variante `app > debug`, ejecuta `Clean Project` y vuelve a instalar este archivo.

La primera instalación del actualizador cloud debe hacerse manualmente: una versión antigua que no contiene `AppUpdateManager` no puede actualizarse a sí misma. Después de instalar la versión `2.3.0` (`versionCode 5`) y configurar el backend, las siguientes versiones sí pueden descargarse desde la nube.

Para más detalle sobre Retrofit, autenticación JWT y URL por entorno, consultar la sección “Conexión Android ↔ Backend” del [mapa técnico](06_mapa_tecnico_apis_conexiones_ia.md).

`adb install` demuestra instalación, no una prueba funcional. Para probar de verdad:

1. Identificar dispositivo y versión Android.
2. Conceder micrófono solo cuando el sistema lo solicite.
3. Crear datos ficticios.
4. Probar login, onboarding, venta, fiado, reporte y sincronización.
5. Repetir con modo avión.
6. Guardar capturas/video en `docs/evidencias/`.

## 5. Flujo de datos de una venta offline

1. La UI recibe texto o voz.
2. Vosk produce una transcripción local.
3. `IntentClassifier` decide la intención.
4. `EntityExtractor` obtiene producto, cantidad, monto y cliente.
5. La UI solicita confirmación.
6. Room guarda la venta y genera un evento Outbox en la misma operación lógica.
7. WorkManager espera conectividad.
8. La API recibe el lote.
9. PostgreSQL aplica idempotencia usando el identificador de operación.
10. El cliente elimina o actualiza el pendiente solo después de una respuesta válida.

## 6. Flujo de aprendizaje autorizado

1. El usuario corrige una transcripción.
2. `VoiceLog` se guarda localmente con `consentToUpload = false`.
3. Solo una acción explícita cambia el consentimiento.
4. WorkManager selecciona únicamente logs autorizados.
5. `POST /api/v1/learning/feedback` recibe el lote.
6. PostgreSQL conserva los datos y deduplica por `eventId`.
7. El cliente elimina el log local solo tras confirmación completa.

## 7. Diagnóstico rápido

| Síntoma | Causa probable | Acción |
|---|---|---|
| `file is not a database` | Archivo antiguo sin SQLCipher o configuración Room inconsistente | No usar fallback destructivo; respaldar/reinstalar una instalación de desarrollo y usar solo MypesDatabase |
| Backend no inicia | PostgreSQL inaccesible o variables inválidas | Revisar `DATABASE_URL`, `docker compose ps` y logs |
| `28P01` | Contraseña PostgreSQL incorrecta | Corregir secreto local; no publicarlo |
| Android no llega a API | URL incorrecta para emulador/teléfono | Usar `10.0.2.2` en emulador o IP LAN en teléfono |
| ASR devuelve modo simulado | `ASR_PROVIDER=simulated` | Es comportamiento esperado; no afirmar transcripción cloud |
| Migración falla | Esquema/versión incompatible | Revisar Room schema y migración; no borrar datos automáticamente |

## 8. Qué debe decirse en la defensa

- “La arquitectura está implementada por capas y diseñada para operar offline-first”.
- “La validación más reciente registrada tiene build/typecheck backend aprobados y 2/2 pruebas mock del proveedor Gemini; la suite HTTP completa, Android y Docker quedaron bloqueados o pendientes según el resultado documentado”.
- “El health check HTTPS de Railway pasó en un despliegue anterior; no se volvió a validar después de la integración Gemini”.
- “La integración PostgreSQL se considera aprobada solo después de una ejecución real contra una base accesible”.
- “El ASR simulado demuestra el contrato, no la calidad de un proveedor cloud”.
- “Vosk es el reconocedor neuronal local; el clasificador Android de intenciones funciona con reglas. Gemini y el servicio Whisper/MiniLM de Quechua son modelos externos opcionales”.
- “Las métricas WER, SUS y latencia siguen pendientes hasta realizar la campaña”.

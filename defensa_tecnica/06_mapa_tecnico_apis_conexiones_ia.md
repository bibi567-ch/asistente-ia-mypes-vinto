# Mapa técnico: APIs, conexiones, IA y ejecución

Guía de referencia para ubicar cada componente del sistema, explicar cómo se conecta Android con el backend y distinguir modelos reales, reglas locales, simulaciones y servicios pendientes de validar. Los enlaces llevan al código fuente correspondiente.

## 1. Mapa general

```text
Comerciante
   |
   v
Android (Compose)
   |-- voz local -> Vosk -> clasificador por reglas -> formularios/acciones
   |-- datos offline -> Room + SQLCipher -> Outbox -> WorkManager
   |-- consultas generales -> HTTPS/HTTP -> API Express -> Gemini opcional
   |-- baja confianza/Quechua -> API ASR -> proveedor configurado
   |
   +-- sincronización con JWT -> API Express -> PostgreSQL privado
```

La app resuelve ventas y acciones comerciales en el dispositivo. El backend gestiona identidad cloud, sincronización y servicios opcionales. Gemini no ejecuta ventas ni consulta inventario.

## 2. Dónde está cada parte

| Componente | Archivo o carpeta | Qué hace |
|---|---|---|
| Entrada y prefijos API | [backend/src/app.ts](../backend/src/app.ts) | Monta los routers bajo `/api/v1` |
| Arranque del servidor | [backend/src/server.ts](../backend/src/server.ts) | Comprueba PostgreSQL y abre el puerto configurado |
| Configuración backend | [backend/src/config/env.ts](../backend/src/config/env.ts) | Lee variables de entorno; no contiene credenciales |
| Rutas HTTP | [backend/src/routes/](../backend/src/routes) | Health, auth, ASR, assistant, sincronización, productos y servicios cloud |
| Acceso PostgreSQL | [backend/src/db/pool.ts](../backend/src/db/pool.ts) | Pool `pg` y límites de conexión |
| Validación de TLS PostgreSQL | [backend/src/db/postgres-ssl.ts](../backend/src/db/postgres-ssl.ts) | En producción exige certificado válido; admite CA privada configurada |
| Cliente HTTP Android | [ApiClient.kt](../mobile/app/src/main/java/com/mypes/vinto/network/ApiClient.kt) | Centraliza Retrofit, URL, JSON y timeouts generales |
| URL Android | [mobile/app/build.gradle.kts](../mobile/app/build.gradle.kts) | Define `BuildConfig.API_BASE_URL`; admite `MYPES_API_BASE_URL` |
| Contratos Retrofit | [mobile/app/src/main/java/com/mypes/vinto/network/](../mobile/app/src/main/java/com/mypes/vinto/network) | Interfaces y DTOs de auth, registro, asistente, productos y transacciones |
| Sincronización local | [OutboxWorker.kt](../mobile/app/src/main/java/com/mypes/vinto/sync/OutboxWorker.kt) | Reintenta el envío de eventos pendientes con conectividad |
| Persistencia móvil | [mobile/app/src/main/java/com/mypes/vinto/data/](../mobile/app/src/main/java/com/mypes/vinto/data) | Room y repositorios locales |
| Modelo Vosk español | [mobile/app/src/main/assets/vosk-model-small-es-0.42/](../mobile/app/src/main/assets/vosk-model-small-es-0.42) | Pesos del reconocedor local empaquetados en Android |
| Captura y decodificación de voz | [AsrEngine.kt](../mobile/app/src/main/java/com/mypes/vinto/nlu/AsrEngine.kt) | Audio PCM 16 kHz y reconocimiento Vosk |
| Derivación local a nube | [AsistenteVozFacade.kt](../mobile/app/src/main/java/com/mypes/vinto/nlu/AsistenteVozFacade.kt) | Usa Vosk; deriva a ASR cloud según confianza/modo y sesión |
| Fallback ASR HTTP | [CloudAsrService.kt](../mobile/app/src/main/java/com/mypes/vinto/nlu/CloudAsrService.kt) | Sube audio multipart con JWT si la política permite fallback |
| Clasificación por reglas | [IntentClassifier.kt](../mobile/app/src/main/java/com/mypes/vinto/nlu/IntentClassifier.kt) | Detecta intenciones localmente mediante expresiones, no una red neuronal |
| Entidades y agente local | [EntityExtractor.kt](../mobile/app/src/main/java/com/mypes/vinto/nlu/EntityExtractor.kt), [YapayAgent.kt](../mobile/app/src/main/java/com/mypes/vinto/nlu/YapayAgent.kt) | Extrae datos y resuelve acciones comerciales en el dispositivo |
| Asistente Gemini | [backend/src/services/gemini-assistant.ts](../backend/src/services/gemini-assistant.ts) | Integra el SDK de Google desde el backend, con timeout y restricciones |
| ASR Quechua experimental | [quechua-asr/main.py](../quechua-asr/main.py) | FastAPI carga Whisper y MiniLM desde Hugging Face |
| Servicios locales | [docker-compose.yml](../docker-compose.yml) | PostgreSQL, backend y perfil opcional Quechua |
| API y contratos detallados | [api-service-contracts.md](../.github/modernize/assessment/engines/facts/api-service-contracts.md) | Catálogo de servicios, rutas, autenticación, DTOs y secuencia Mermaid |

## 3. Conexión Android ↔ Backend

### URL según entorno

| Entorno | Base URL | Observación |
|---|---|---|
| Railway | `https://blissful-enthusiasm-production-95638378.up.railway.app/` | Valor predeterminado documentado en el Gradle móvil |
| Emulador Android con backend local | `http://10.0.2.2:3000/` | `10.0.2.2` apunta al host de desarrollo desde el emulador |
| Teléfono físico en LAN | `http://IP_LAN_DEL_EQUIPO:3000/` | Solo en debug y tras publicar el puerto en una interfaz LAN de confianza; no usar `localhost` del teléfono |

Compose publica el backend en `127.0.0.1` por defecto, lo que limita el acceso al equipo anfitrión. Para probar con teléfono físico, configurar `API_BIND_ADDRESS=0.0.0.0` en el `.env` local y compilar el APK debug con la IP LAN del equipo. Esto expone el puerto a las interfaces de red: usar solo una LAN privada y firewall restrictivo; no guardar datos reales ni dejar esta configuración en un despliegue público. El manifiesto debug permite HTTP en claro; producción debe usar HTTPS.

Ejemplo para compilar hacia Compose local:

```powershell
Set-Location mobile
.\gradlew.bat :app:assembleDebug -PMYPES_API_BASE_URL=http://10.0.2.2:3000/
```

El parámetro solo cambia la URL que compila en el APK. Para instalarlo se usa `adb install` o Android Studio; instalar el APK no prueba el flujo funcional.

### Autenticación y datos

1. Android solicita login/registro a `/api/v1/auth/*`.
2. El backend devuelve un JWT Bearer; Android lo conserva en la sesión cloud.
3. Las solicitudes privadas agregan `Authorization: Bearer <JWT>`.
4. El backend verifica el token y limita las operaciones al `businessId` incluido en sus claims.
5. Las ventas se guardan primero en Room/Outbox y WorkManager manda `POST /api/v1/transacciones/batch` al recuperar conectividad.
6. El backend procesa el lote con idempotencia y PostgreSQL; Android confirma/retira pendientes con la respuesta.

El cliente central Retrofit está en [ApiClient.kt](../mobile/app/src/main/java/com/mypes/vinto/network/ApiClient.kt). Usa 15 s para conexión y 30 s para lectura/escritura. La subida de voz tiene su propio timeout de lectura/llamada de 125 s en [CloudAsrService.kt](../mobile/app/src/main/java/com/mypes/vinto/nlu/CloudAsrService.kt).

## 4. Inventario de las 20 rutas backend

Todas estas rutas se montan desde [backend/src/app.ts](../backend/src/app.ts). “JWT” indica autenticación Bearer obligatoria. Las rutas marcadas “No” son públicas.

| Método y ruta | Auth | Cliente Android encontrado |
|---|---|---|
| `GET /api/v1/health` | No | No; existe como endpoint de diagnóstico, sin verificación posterior a Gemini |
| `POST /api/v1/auth/login` | No | Sí, `AuthApi` |
| `POST /api/v1/auth/register` | No | Sí, `RegistroApi` |
| `PUT /api/v1/auth/business` | JWT | Sí, `RegistroApi` |
| `POST /api/v1/auth/change-password` | JWT | No se encontró llamada Retrofit |
| `POST /api/v1/asr/transcribe` | JWT | Sí, `CloudAsrApi` dentro de `CloudAsrService` |
| `GET /api/v1/app-updates/latest` | No | Sí, `AppUpdateApi` |
| `GET /api/v1/app-updates/apk` | No | URL de descarga entregada por el backend |
| `POST /api/v1/assistant/query` | JWT | Sí, `AssistantApi`; consultas cognitivas y mensajes no clasificados sin acción local |
| `POST /api/v1/learning/feedback` | JWT | Sí, `LearningApi` |
| `POST /api/v1/logs/comandos` | JWT | No se encontró llamada Retrofit |
| `POST /api/v1/facturas` | JWT | No se encontró llamada Retrofit |
| `POST /api/v1/qr` | JWT | No se encontró llamada Retrofit |
| `GET /api/v1/sync/updates` | JWT, SSE | No se encontró cliente SSE Retrofit |
| `GET /api/v1/products?limit&offset` | JWT | No se encontró cliente Retrofit para este listado |
| `POST /api/v1/products` | JWT | No se encontró cliente Retrofit |
| `PUT /api/v1/products/:id` | JWT | No se encontró cliente Retrofit |
| `DELETE /api/v1/products/:id` | JWT | No se encontró cliente Retrofit |
| `GET /api/v1/products/sync?desde&cursorActualizadoEn&cursorId` | JWT | Sí, `SyncApi` |
| `POST /api/v1/transacciones/batch` | JWT | Sí, `SyncApi` |

Una ruta existente en el servidor no implica que la app actual la invoque. En particular, facturas, QR y CRUD de productos son contratos backend, no flujos móviles demostrados. El contrato completo de requests/responses y estados HTTP está en [api-service-contracts.md](../.github/modernize/assessment/engines/facts/api-service-contracts.md).

## 5. IA, redes neuronales y modelos de voz

### En el teléfono (offline)

- **Vosk español:** modelo `vosk-model-small-es-0.42` en los assets Android; contiene el modelo acústico (`am/final.mdl`) y configuración. [AsrEngine.kt](../mobile/app/src/main/java/com/mypes/vinto/nlu/AsrEngine.kt) lo copia al almacenamiento privado de la app y procesa audio del micrófono a 16 kHz.
- **NLU local:** [IntentClassifier.kt](../mobile/app/src/main/java/com/mypes/vinto/nlu/IntentClassifier.kt) y [EntityExtractor.kt](../mobile/app/src/main/java/com/mypes/vinto/nlu/EntityExtractor.kt) usan reglas/patrones. No son redes neuronales entrenadas.
- **Acciones comerciales:** venta, stock y formularios son código local con confirmación; no son ejecutados por Gemini.
- No se encontró un modelo `.tflite` en el flujo móvil descrito.

### En el backend/nube

- **Gemini:** modelo configurable por `GEMINI_MODEL` (Compose propone `gemini-3.5-flash-lite`); el backend usa `GEMINI_API_KEY`. Si falta la clave, `/assistant/query` responde con guía demostrativa local. El proveedor real no se contacta en ese caso. La petición real tiene timeout de 15 s y el error del proveedor da HTTP 502.
- **Qué se envía:** pregunta general y código de idioma, no ventas/inventario. Android consulta el backend para `CONSULTA_COGNITIVA` y para mensajes `DESCONOCIDA` sin destino comercial local ni confirmación pendiente. Sin sesión conserva el comportamiento local.
- **ASR cloud:** en Compose `ASR_PROVIDER` queda en `simulated` por defecto. Ese modo devuelve explícitamente que no transcribió audio. Google Speech, OpenAI Whisper o Quechua requieren configuración y credenciales privadas.
- **Abstención de voz:** Vosk bajo el umbral se rechaza si no existe texto cloud utilizable. Un texto cloud obtenido por fallback sigue el enrutamiento del chat; el modo Quechua también requiere intención remota aceptada.
- **Quechua experimental:** [quechua-asr/main.py](../quechua-asr/main.py) obtiene desde Hugging Face el modelo Whisper `QuechuaBase/whisper-base-qxp-finetuned` (revisión fijada en el código) y `paraphrase-multilingual-MiniLM-L12-v2` para similitud de intención. Se descargan en el volumen `quechua_asr_models`; perfil Compose: `quechua`. No está habilitado por defecto ni hay resultado que pruebe el despliegue/calidad. Revisar la licencia del modelo antes de cualquier uso comercial.

## 6. PostgreSQL, Docker y nube

### Desarrollo local

- PostgreSQL escucha en 5432 dentro del contenedor; Compose lo publica como `127.0.0.1:5433` por defecto.
- El backend escucha en 3000 y se conecta a `postgres:5432` dentro de la red Compose. Android emulador llega al backend por `10.0.2.2:3000`.
- Compose publica el puerto del backend en `127.0.0.1` por defecto; un teléfono físico necesita `API_BIND_ADDRESS=0.0.0.0` en la red local y un APK debug configurado con la IP LAN.
- `quechua-asr` escucha en 8000 solo dentro de Compose; requiere activar el perfil opcional.
- TLS en backend producción verifica certificados (`rejectUnauthorized: true`). Si la CA del Postgres privado no es pública/confiable, configurar `DATABASE_SSL_CA`; no desactivar la verificación TLS.

Comandos básicos desde la raíz:

```powershell
# Preparar .env local desde la plantilla; editarlo localmente sin compartir secretos
Copy-Item .env.example .env

# Levantar PostgreSQL y backend (requiere Docker Desktop y .env válido)
docker compose up -d --build
docker compose ps
docker compose logs --tail=100 backend

# Verificar API y conexión a DB; solo registrar resultado después de ejecutarlo
Invoke-WebRequest http://127.0.0.1:3000/api/v1/health

# Levantar ASR Quechua experimental además del stack base
docker compose --profile quechua up -d --build

# Detener sin borrar datos
docker compose down
```

`docker compose down -v` elimina también el volumen de PostgreSQL: no usar si se necesitan conservar datos.

### Railway

- Railway hospeda el servicio API y PostgreSQL permanece como servicio privado.
- El contenedor API usa el puerto asignado por la plataforma; la app móvil llama al dominio HTTPS.
- La bitácora registra un health check HTTPS exitoso el 25/11/2026 en la Fase 25, con base conectada. También registra un intento intermedio con error de certificado. Ese resultado es histórico: **no valida el código Gemini posterior ni prueba login, ventas Android, ASR real o todas las rutas**.
- La configuración actual de producción debe mantener TLS estricto y referenciar el URL privado del servicio Postgres y su CA. No incluir PEM, URL con credenciales, tokens ni claves en esta carpeta.

## 7. Comandos de validación disponibles

Los siguientes son comandos para que el desarrollador ejecute en su máquina. No representan resultados ejecutados por esta guía.

```powershell
# Backend, desde backend/
npm ci
npm run build
npm run typecheck:test
npm test
npm run test:integration

# Android, desde mobile/
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug -PMYPES_API_BASE_URL=http://10.0.2.2:3000/
```

El build/typecheck no sustituye pruebas HTTP, integración PostgreSQL ni la prueba móvil. Consultar [docs/05_Resultados_de_Pruebas.md](../docs/05_Resultados_de_Pruebas.md) antes de presentar estados.

## 8. Estado de evidencia más reciente registrado

Según la Fase 38 de [docs/05_Resultados_de_Pruebas.md](../docs/05_Resultados_de_Pruebas.md):

- Backend: build y typecheck pasaron; la suite completa terminó **10/31**, bloqueada por conexiones de prueba a loopback interceptadas por el proxy del sandbox.
- Proveedor Gemini simulado: **2/2** pruebas enfocadas pasaron; no se llamó a Google ni se usó clave real.
- API HTTP, Android/Gradle, Docker y health check **no quedaron verificados después de esa integración**.
- El despliegue histórico de Railway y la salud observada el 25/11 son evidencia de ese despliegue, no de esta versión del código.

Para registrar pruebas nuevas, actualizar el plan/resultados conforme a [docs/COMO_DOCUMENTAR.md](../docs/COMO_DOCUMENTAR.md). Nunca convertir una comprobación bloqueada en aprobada ni confundir una compilación con una prueba funcional.

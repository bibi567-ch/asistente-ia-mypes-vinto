# 15. Guía de despliegue y ejecución

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Despliegue de prueba en Railway operativo; despliegue productivo no aprobado
**Actualizado:** 25/11/2026

## 1. Requisitos previos

- Docker Desktop con Compose.
- Node.js 24 y npm, si se ejecuta el backend fuera de Docker.
- Android Studio, Android SDK 35 y JDK 17.
- Emulador o dispositivo Android 8.0 (API 26) o posterior, con micrófono.
- Modelo Vosk español en `mobile/app/src/main/assets/vosk-model-small-es-0.42`. El script `mobile/download-vosk-model.ps1` descarga el modelo desde una distribución autorizada y verifica su integridad antes de incorporarlo al build. El modelo está excluido por `.gitignore`.

## 2. Backend con Docker Compose

Desde PowerShell, en la raíz del proyecto:

```powershell
Copy-Item .env.example .env
$env:POSTGRES_PASSWORD = (& node -e "process.stdout.write(require('node:crypto').randomBytes(24).toString('base64url'))")
$env:JWT_SECRET = (& node -e "process.stdout.write(require('node:crypto').randomBytes(32).toString('base64url'))")
$configuracion = Get-Content .env
$configuracion = $configuracion -replace '^POSTGRES_PASSWORD=.*$', "POSTGRES_PASSWORD=$env:POSTGRES_PASSWORD"
$configuracion = $configuracion -replace '^JWT_SECRET=.*$', "JWT_SECRET=$env:JWT_SECRET"
Set-Content -Encoding ASCII .env $configuracion
docker compose up -d --build
docker compose ps
Invoke-RestMethod http://127.0.0.1:3000/api/v1/health
```

La API escucha en el puerto `3000`, no `4000`, y Compose la enlaza solo a loopback. Las claves de ASR son opcionales:

- Google: `ASR_PROVIDER=google`, `GOOGLE_ASR_API_KEY` y opcionalmente `GOOGLE_ASR_LANGUAGE`.
- Whisper: `ASR_PROVIDER=whisper`, `OPENAI_ASR_API_KEY` y opcionalmente `OPENAI_ASR_BASE_URL`.
- Quechua: `ASR_PROVIDER=quechua`, `QUECHUA_ASR_API_URL` y `QUECHUA_ASR_API_TOKEN`. El endpoint acepta `POST multipart/form-data` con el campo `audio` y devuelve JSON `{ "text": "..." }`. En producción usa HTTPS, excepto HTTP en un hostname privado `.railway.internal`. El backend actúa como proxy y no contiene ni descarga el modelo.

Guarda esas variables solo en `.env`; nunca las insertes en el APK. La API ofrece registro en `POST /api/v1/auth/register`. Si necesitas crear un propietario de prueba desde el contenedor, ejecuta `docker compose exec backend node dist/scripts/create-owner.js "MYPE de prueba" "propietario@example.test"` y protege la contraseña temporal generada.

Compose usa `NODE_ENV=development` por defecto para la base PostgreSQL local. `API_BIND_ADDRESS=127.0.0.1` restringe el puerto al equipo local y permite el acceso desde el emulador Android mediante `10.0.2.2`. Para probar temporalmente con un teléfono en una red Wi-Fi confiable, cambia `API_BIND_ADDRESS` a `0.0.0.0`, limita el acceso con el firewall y usa la IP LAN del equipo; no expongas ese modo directamente a Internet. Para ejecutar Node directamente fuera de Docker, completa `DATABASE_URL` en el `backend/.env` local; el ejemplo deja ese valor vacío intencionalmente y el backend no inicia si falta.

Para detener los servicios sin borrar los datos, usa `docker compose down`. `docker compose down -v` elimina también el volumen local de PostgreSQL.

## 3. App Android

1. Abre `mobile/` en Android Studio.
2. Asegura que el modelo Vosk esté en la ruta indicada arriba.
3. Usa el Gradle Wrapper versionado incluido en `mobile/`.
4. Desde `mobile/`, configura la URL de API y ejecuta las pruebas/build:

   ```powershell
   .\gradlew.bat -PMYPES_API_BASE_URL=http://10.0.2.2:3000/ :app:testDebugUnitTest :app:assembleDebug
   ```

5. Instala el APK debug en un dispositivo/emulador y concede permiso de micrófono. Concede cámara cuando se solicite para la linterna.

El manifiesto principal no permite HTTP en builds release; el permiso de tráfico sin cifrar está limitado al manifiesto debug para pruebas locales. El valor predeterminado de `MYPES_API_BASE_URL` ahora es el dominio HTTPS de Railway. Para pruebas locales, sobrescríbelo con `-PMYPES_API_BASE_URL=http://10.0.2.2:3000/`; para otro despliegue, usa `-PMYPES_API_BASE_URL=https://<dominio-api>/`. Las funciones cloud requieren autenticación cloud y un JWT vigente; el PIN local no crea esa sesión.

## 4. Pruebas de funcionamiento

- Probar en modo offline cada intención y confirmar que Vosk funciona sin backend.
- Probar que las acciones muestran confirmación antes de ejecutarse y que cancelar no dispara la acción.
- Probar ASR cloud con una cuenta JWT y un proveedor configurado; el endpoint responde `{ "texto": "..." }`.
- Probar el Outbox con desconexión/reconexión. La sincronización requiere URL y JWT.
- En llamadas/mensajes por nombre, proporcionar un número explícito: aún no se consulta la agenda.

## 5. Actualización del APK y estado de la nube

El backend ya contiene `GET /api/v1/app-updates/latest` y `GET /api/v1/app-updates/apk`; Android consulta versión, descarga el APK configurado, verifica su SHA-256 y abre el instalador del sistema. La instalación requiere confirmación del usuario. Esto no es Google Play In-App Updates ni una actualización silenciosa.

En Compose, el APK se monta desde `mobile/app/build/outputs/apk/debug`. Para habilitar la descarga local, configura en el `.env` raíz `ANDROID_UPDATE_VERSION_CODE`, `ANDROID_UPDATE_VERSION_NAME`, `ANDROID_UPDATE_APK_PATH` y `ANDROID_UPDATE_BASE_URL` con valores coherentes con el APK que realmente existe.

El backend y la base de datos están desplegados en Railway para una prueba. La cuenta Railway disponible está en prueba; el crédito y la vigencia son limitados y no equivalen a hosting gratuito permanente. No ingreses datos reales de comerciantes ni habilites un plan pagado para una demostración.

### Despliegue de prueba en Railway

1. Crea un proyecto y agrega PostgreSQL 16. Mantén la base privada; no habilites acceso público.
2. Despliega este repositorio desde GitHub como servicio backend y configura **Root Directory** en `/backend`. Railway encontrará el `Dockerfile` en esa raíz; el archivo compila la API y ejecuta las migraciones antes de iniciar el servidor. No es necesario añadir otro Dockerfile ni cambiar la raíz si el servicio ya muestra `/backend`.
3. En las variables del backend, establece `DATABASE_URL` como referencia al valor que realmente expone el servicio Postgres (en este proyecto, `${{Postgres.DATABASE_URL}}`; no se encontró `DATABASE_PRIVATE_URL`) y configura `JWT_SECRET` con al menos 32 bytes aleatorios. Para la prueba inicial usa `ASR_PROVIDER=simulated`; no agregues claves de proveedores externos.
4. Railway PostgreSQL puede usar una CA privada. Obtén únicamente el certificado raíz público desde la instancia PostgreSQL; en esta instancia se tomó de `root.crt` y se verificó contra `server.crt`. El comando `export-ssl-ca` no está disponible en el contenedor. Pega el PEM público en `DATABASE_SSL_CA`, conservando `BEGIN CERTIFICATE` y `END CERTIFICATE`, y configura `DATABASE_SSL_REJECT_UNAUTHORIZED=true`. No copies `root.key` ni `server.key`, ni agregues `sslmode=require` a `DATABASE_URL`, ni desactives la verificación.
5. Configura el health check del servicio backend como `/api/v1/health`, genera un dominio HTTPS solo para la API y prueba esa URL antes de conectar Android. No publiques PostgreSQL.
6. Para una build debug que apunte al servicio, usa `-PMYPES_API_BASE_URL=https://<dominio-api>/`. Verifica primero registro, login y persistencia con datos ficticios. El build Android por sí solo no prueba la integración cloud.

Estado observado al 25/11/2026: el servicio `blissful-enthusiasm` usa `/backend`; PostgreSQL `Postgres` sigue privado y en línea. El deployment `dfac6edf-cb63-4e65-8857-60aa640b9fcb` terminó con 8 migraciones al día y la API escuchando en 8080. Con `DATABASE_SSL_CA` configurada y `DATABASE_SSL_REJECT_UNAUTHORIZED=true`, el health check `GET /api/v1/health` respondió `status=ok` y `database=connected` en `https://blissful-enthusiasm-production-95638378.up.railway.app`. El dominio expone solo la API; Android ya usa ese dominio como valor predeterminado, pero no se ha validado en un dispositivo físico ni se han probado flujos autenticados. No se desactivó la validación TLS y no se recreó ni publicó PostgreSQL.

### ASR quechua remoto

El proveedor `quechua` reenvía audio a `QUECHUA_ASR_API_URL`, envía `Authorization: Bearer ...` si se configura `QUECHUA_ASR_API_TOKEN` y espera `{ "text": "...", "intent": "...|null", "confidence": 0.0 }`. El código FastAPI de `quechua-asr/` usa por defecto `QuechuaBase/whisper-base-qxp-finetuned`, revisión `5faf249f2a5b0034c095a91b3343c2d670fe5eee`; también carga un encoder MiniLM fijo por revisión para proponer intención. Ambos pesos se descargan al iniciar. El endpoint de salud responde solo cuando los modelos están cargados.

**Licencia:** la ficha de Whisper Puno declara CC BY-NC 4.0, que limita uso comercial; no lo uses en una operación comercial sin permiso/licencia apropiada. La selección de este modelo por el usuario autoriza preparar el código, pero no cambia sus términos. No se desplegó inferencia. El encoder MiniLM está bajo Apache-2.0 y su lista publicada de lenguas no incluye quechua: la clasificación por similitud es experimental, debe evaluarse con hablantes qxp y no debe considerarse entendimiento confiable de quechua.

Para desplegar un servicio Railway separado (solo después de resolver el uso de licencia y confirmar que no excederá los créditos existentes), deja el Root Directory vacío y define `RAILWAY_DOCKERFILE_PATH=/quechua-asr/Dockerfile`. Configura `/health` como health check y `PORT=8000`. Configura `ASR_API_TOKEN` como variable privada; `ASR_MODEL_ID`, `ASR_MODEL_REVISION`, `INTENT_MODEL_ID` e `INTENT_MODEL_REVISION` ya tienen defaults fijados. `HF_HOME` apunta a `/data/huggingface`; sin un volumen montado allí, los pesos se descargarán de nuevo al reiniciar o desplegar. La ficha de Hugging Face reporta unos 581 MB usados solo por Whisper; la memoria y créditos requeridos por los dos modelos no se midieron. Si se aportan ejemplos de intención qxp revisados por hablantes, se pueden agregar en `INTENT_EXAMPLES_JSON` como JSON de `{ "registrar_venta": ["frase verificada", ...], ... }`; solo se admiten las etiquetas del catálogo. No hay ejemplos quechua validados en el repositorio hoy.

En el servicio backend define `ASR_PROVIDER=quechua`, `QUECHUA_ASR_API_URL=http://quechua-asr.railway.internal:8000/transcribe` y `QUECHUA_ASR_API_TOKEN` con el mismo valor de `ASR_API_TOKEN`. La API acepta HTTP solo para hostnames privados `.railway.internal`; destinos públicos requieren HTTPS. No expongas el microservicio públicamente sin TLS. No habilites un plan de pago.

El endpoint limita el audio a 5 MB y 120 segundos; decodifica WAV/FLAC/OGG, normaliza a mono 16 kHz y no conserva audio de forma persistente. No se fuerza `language="qu"`: la revisión configurada tiene `language="spanish"` y no incluye token `<|qu|>` en `lang_to_id`; se usa su generación predeterminada. El encoder MiniLM clasifica por similitud frente a plantillas y se abstiene bajo 0.62 o margen 0.08; score no equivale a probabilidad ni accuracy. El móvil usa el intent remoto solo si está permitido y encima del umbral. Venta abre el formulario y no guarda hasta acción explícita del usuario; consultas de stock/ventas leen Room local. La app envía audio cloud cuando hay conectividad y baja confianza Vosk; habilítalo solo con consentimiento y autorización de datos. WER, latencia, precisión de intención y calidad qxp siguen sin medir.

La actualización remota de modelos Vosk no está implementada.

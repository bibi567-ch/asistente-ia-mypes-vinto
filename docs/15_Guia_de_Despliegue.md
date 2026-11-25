# 15. Guía de despliegue y ejecución

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Instrucciones locales y preparación para despliegue de prueba; despliegue productivo no implementado
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

El manifiesto principal no permite HTTP en builds release; el permiso de tráfico sin cifrar está limitado al manifiesto debug para pruebas locales. Para una versión release, compila con una URL HTTPS, por ejemplo `-PMYPES_API_BASE_URL=https://tu-api.example/`. Las funciones cloud requieren autenticación cloud y un JWT vigente; el PIN local no crea esa sesión.

## 4. Pruebas de funcionamiento

- Probar en modo offline cada intención y confirmar que Vosk funciona sin backend.
- Probar que las acciones muestran confirmación antes de ejecutarse y que cancelar no dispara la acción.
- Probar ASR cloud con una cuenta JWT y un proveedor configurado; el endpoint responde `{ "texto": "..." }`.
- Probar el Outbox con desconexión/reconexión. La sincronización requiere URL y JWT.
- En llamadas/mensajes por nombre, proporcionar un número explícito: aún no se consulta la agenda.

## 5. Actualización del APK y estado de la nube

El backend ya contiene `GET /api/v1/app-updates/latest` y `GET /api/v1/app-updates/apk`; Android consulta versión, descarga el APK configurado, verifica su SHA-256 y abre el instalador del sistema. La instalación requiere confirmación del usuario. Esto no es Google Play In-App Updates ni una actualización silenciosa.

En Compose, el APK se monta desde `mobile/app/build/outputs/apk/debug`. Para habilitar la descarga local, configura en el `.env` raíz `ANDROID_UPDATE_VERSION_CODE`, `ANDROID_UPDATE_VERSION_NAME`, `ANDROID_UPDATE_APK_PATH` y `ANDROID_UPDATE_BASE_URL` con valores coherentes con el APK que realmente existe.

El backend y la base de datos aún no están desplegados en un proveedor público. La cuenta Railway disponible está en prueba; el crédito y la vigencia son limitados y no equivalen a hosting gratuito permanente. No ingreses datos reales de comerciantes ni habilites un plan pagado para una demostración.

### Despliegue de prueba en Railway

1. Crea un proyecto y agrega PostgreSQL 16. Mantén la base privada; no habilites acceso público.
2. Despliega este repositorio desde GitHub como servicio backend y configura **Root Directory** en `/backend`. El `Dockerfile` ejecuta las migraciones antes de iniciar la API.
3. En las variables del backend, referencia `DATABASE_URL` desde el servicio PostgreSQL y configura `JWT_SECRET` con al menos 32 bytes aleatorios. Para la prueba inicial usa `ASR_PROVIDER=simulated`; no agregues claves de proveedores externos.
4. Railway PostgreSQL puede usar una CA privada. En el equipo con Railway CLI autenticado, exporta únicamente el certificado raíz público:

   ```powershell
   railway ssh --project <proyecto> --service <servicio-postgres> --environment <entorno> -- export-ssl-ca
   ```

   Pega el PEM resultante en la variable `DATABASE_SSL_CA` del backend, conservando las líneas `BEGIN CERTIFICATE` y `END CERTIFICATE`. No copies `root.key` ni `server.key`. La API mantiene la validación del certificado; no agregues `sslmode=require` a `DATABASE_URL` ni desactives la verificación.
5. Configura el health check del servicio backend como `GET /api/v1/health`, genera un dominio HTTPS solo para la API y prueba esa URL antes de conectar Android. No publiques PostgreSQL.
6. Para una build debug que apunte al servicio, usa `-PMYPES_API_BASE_URL=https://<dominio-api>/`. Verifica primero registro, login y persistencia con datos ficticios. El build Android por sí solo no prueba la integración cloud.

La variable de URL de producción no debe quedar guardada como valor de release dentro del código: pásala al compilar. El despliegue, las migraciones, la persistencia PostgreSQL y la conexión desde Android siguen pendientes de prueba real. La actualización remota de modelos Vosk no está implementada.

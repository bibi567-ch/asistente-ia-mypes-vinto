# 15. Guía de despliegue y ejecución

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Instrucciones locales; despliegue productivo no implementado
**Actualizado:** 23/11/2026

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

Guarda esas variables solo en `.env`; nunca las insertes en el APK. El login no tiene registro público. Para crear un propietario desde el contenedor, ejecuta `docker compose exec backend node dist/scripts/create-owner.js "MYPE de prueba" "propietario@example.test"` y guarda la contraseña temporal impresa.

Para detener los servicios sin borrar los datos, usa `docker compose down`. `docker compose down -v` elimina también el volumen local de PostgreSQL.

## 3. App Android

1. Abre `mobile/` en Android Studio.
2. Asegura que el modelo Vosk esté en la ruta indicada arriba.
3. Restaura un Gradle Wrapper versionado antes de ejecutar CI o builds reproducibles; actualmente faltan `gradlew.bat` y `gradle/wrapper/`.
4. Ejecuta `testDebugUnitTest` y `assembleDebug` desde Android Studio o con el wrapper.
5. Instala el APK debug en un dispositivo/emulador y concede permiso de micrófono. Concede cámara cuando se solicite para la linterna.

El emulador Android puede alcanzar el host usando `10.0.2.2`; el puerto de API del Compose actual es `3000`. Android moderno bloquea HTTP sin cifrar por defecto: para pruebas online usa un endpoint HTTPS o una configuración cleartext limitada a debug. La app requiere que el inicio de sesión proporcione `api_base_url` y un JWT vigente en preferencias privadas `voice_cloud`; el login local por PIN aún no genera ese JWT.

## 4. Pruebas de funcionamiento

- Probar en modo offline cada intención y confirmar que Vosk funciona sin backend.
- Probar que las acciones muestran confirmación antes de ejecutarse y que cancelar no dispara la acción.
- Probar ASR cloud con una cuenta JWT y un proveedor configurado; el endpoint responde `{ "texto": "..." }`.
- Probar el Outbox con desconexión/reconexión. La sincronización requiere URL y JWT.
- En llamadas/mensajes por nombre, proporcionar un número explícito: aún no se consulta la agenda.

## 5. Estrategias futuras de actualización

Las siguientes estrategias son propuestas; no hay `UpdateManager`, endpoint de versiones, Play In-App Updates ni actualizador remoto de modelos implementados en este repositorio.

### Estrategia 1: Google Play In-App Updates

Si la app se publica en Google Play, Play puede distribuir actualizaciones y la API Play In-App Updates puede ofrecer flujos flexible o inmediato. Antes de implementarla:

1. Publicar la app mediante Play y configurar los tracks de prueba.
2. Agregar `com.google.android.play:app-update-ktx` en el módulo `app`.
3. Consultar `AppUpdateManager` al iniciar y manejar disponibilidad, descarga, reanudación, cancelación y errores.
4. Probar los flujos desde Play Internal Testing; no funcionan como actualización in-app para APK instalados manualmente.

No agregues la dependencia hasta decidir que Play será un canal de distribución.

### Estrategia 2: APK fuera de Google Play

Para pruebas de campo por APK, podría añadirse un endpoint autenticado o firmado `GET /api/v1/app/version` que publique versión, URL HTTPS, tamaño y SHA-256 del APK. Una futura implementación Android debe:

1. Comparar la versión disponible con `BuildConfig.VERSION_NAME`.
2. Mostrar un diálogo y permitir rechazar/posponer.
3. Descargar a almacenamiento privado, validar SHA-256 y certificado de firma del APK, tamaño y versión antes de abrir el instalador.
4. Usar `FileProvider` para compartir un URI `content://`; el usuario debe confirmar la instalación y habilitar la fuente correspondiente.
5. Solicitar `REQUEST_INSTALL_PACKAGES` solo si el canal de distribución lo exige y tras revisar las políticas aplicables.

No publiques una URL arbitraria ni instales un APK sin validar su firma. El endpoint, `UpdateManager`, permisos y proveedor de descargas aún deben diseñarse e implementarse.

### Estrategia 3: Actualización de modelos de IA

- **LiteRT/TensorFlow Lite:** no hay modelo TFLite/LiteRT en el clasificador actual. Si se incorpora uno, Firebase ML Model Management puede evaluarse para distribuirlo; requiere proyecto Firebase, configuración Android y políticas de descarga/rollback.
- **Vosk:** una futura distribución desde servidor debe publicar una versión firmada con tamaño y checksum. El cliente descarga por HTTPS, valida, extrae a un directorio temporal, comprueba los archivos y cambia el modelo de forma atómica conservando una copia de rollback. Vosk abre el modelo desde `filesDir`; no necesita `FileProvider`.

Antes de una actualización remota, medir el tamaño de descarga, almacenamiento temporal, compatibilidad del modelo y calidad de reconocimiento. El modelo actual está en assets y debe incluirse de forma autorizada en el paquete de distribución.

### Secuencia propuesta

| Componente | Estrategia | Estado |
|---|---|---|
| APK de producción | Google Play In-App Updates | Propuesta si se publica en Play |
| APK de pruebas de campo | Endpoint firmado + instalador del sistema | No implementado |
| Modelo Vosk | Descarga versionada, checksum y rollback | No implementado |
| Modelo LiteRT | Firebase ML solo si se adopta LiteRT | No implementado; clasificador actual usa reglas |

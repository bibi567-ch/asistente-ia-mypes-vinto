# Aplicación Android

Módulo Kotlin + Jetpack Compose bajo `app/`. Incluye persistencia local Room/SQLite, autenticación local por PIN, ventas offline, asistente de voz y sincronización diferida. La presencia de código no demuestra que todos los flujos se hayan probado en un dispositivo.

## Funciones

- Login local con PIN de cuatro dígitos, PBKDF2-HMAC-SHA256 y política de bloqueo temporal. El PIN no sustituye al login JWT del backend.
- Onboarding, dashboard e inventario Compose.
- Persistencia Room para productos, ventas, caja y operaciones pendientes (Outbox).
- Pantallas conectadas para registrar ventas, usar el asistente de voz y consultar el estado de sincronización.
- Login API con email y contraseña, independiente del PIN local.
- Procesamiento de voz local con Vosk. El asistente interpreta solicitudes; las ventas todavía requieren revisión y confirmación en el formulario manual.

La estudiante informó que instaló el APK debug en un Motorola G60 y guardó un producto de prueba. Es un resultado reportado, no una prueba formal; archivar pasos y evidencia anonimizada antes de presentarlo como tal.

## Acceso a la API y sincronización

- «Vincular cuenta del servidor» usa `POST /api/v1/auth/login`. La contraseña no se guarda; el token JWT sí se conserva para el worker. El token dura 15 minutos. Si la API devuelve `401`, se elimina el token y hace falta iniciar sesión otra vez; las ventas locales pendientes se conservan.
- `http://10.0.2.2:3000/` es la URL predeterminada para emuladores Android. En `mobile/gradle.properties`, configura `MYPES_API_BASE_URL=http://<IP-LAN-del-equipo>:3000/` para un teléfono conectado a una red local confiable. No expongas el backend de desarrollo a Internet.
- WorkManager envía las ventas pendientes a `POST /api/v1/transacciones/batch` y descarga productos modificados de `GET /api/v1/products/sync`, en páginas de hasta 500 elementos. Los cambios remotos de nombre y precio conservan el stock local.
- Los productos creados solo en el dispositivo aún no se envían al catálogo del backend. La sincronización del catálogo es de descarga.
- Docker Compose inicia el backend y PostgreSQL localmente, pero no configura acceso desde el teléfono ni crea cuentas API. Crea el propietario según [las instrucciones del backend](../backend/README.md); no uses credenciales fijas ni compartidas.

Probar por separado la API, el registro offline local y la sincronización. Usar datos ficticios y no incluir tokens en la evidencia.

## Modelo Vosk

El modelo no se versiona por su tamaño. El código espera los archivos locales en `app/src/main/assets/vosk-model-small-es-0.42/`. Sin instalar el modelo, no afirmar que el reconocimiento de voz está probado en el teléfono. Registrar idioma, modelo, dispositivo, entorno y resultado.

## Compilar y probar

1. Abrir `mobile/` en Android Studio y esperar la sincronización de Gradle.
2. Seleccionar un dispositivo o emulador y compilar la variante debug.
3. Probar con datos ficticios el PIN, onboarding, alta y persistencia de productos, modo avión y registro de ventas.
4. Probar voz y sincronización por separado; anotar cualquier comportamiento pendiente.
5. Guardar evidencia según `evidence/README.md` y relacionarla con la matriz de las tres materias.

No incluir APK, keystore, token, datos reales de comerciantes ni audios identificables en el repositorio.

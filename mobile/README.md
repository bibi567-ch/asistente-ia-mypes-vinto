# Aplicación Android

Módulo Kotlin + Jetpack Compose bajo `app/`. Incluye persistencia local Room/SQLite, autenticación local por PIN, ventas offline, asistente de voz y sincronización diferida. La presencia de código no demuestra que todos los flujos se hayan probado en un dispositivo.

## Funciones

- Login local con PIN de cuatro dígitos, PBKDF2-HMAC-SHA256 y política de bloqueo temporal. El PIN no sustituye al login JWT del backend.
- Onboarding, dashboard e inventario Compose.
- Persistencia Room para productos, ventas, caja y operaciones pendientes (Outbox).
- Pantallas conectadas para registrar ventas, usar el asistente de voz y consultar el estado de sincronización.
- Módulo de fiados para listar deudas pendientes; al confirmar un cobro completo, el estado pagado, el ingreso de caja y el evento `COBRO` del Outbox se guardan en una misma transacción Room.
- Inventario con búsqueda local por nombre y exportación del reporte diario de ventas a CSV compartido mediante `FileProvider`.
- La pantalla de sincronización observa el Outbox Room y muestra las operaciones pendientes, errores y el estado del trabajo de WorkManager.
- Reporte local de ventas del día, con recálculo al cambiar la fecha.
- Login API con email y contraseña, independiente del PIN local.
- Registro local/API: los datos personales se guardan primero en Room y, al registrar con conexión, también en PostgreSQL; el PIN se guarda como hash solo en Room y la contraseña de nube se almacena como hash scrypt en el servidor, sin persistirla en el dispositivo. El nombre/rubro del negocio se completa en el onboarding y se actualiza en la API cuando hay conexión. Si el alta se hizo offline, la pantalla de sincronización permite registrar posteriormente el perfil local en la nube volviendo a introducir el correo y la contraseña.
- Login local por carnet, celular o correo y PIN; el PIN también puede dictarse en español con el reconocimiento Vosk offline.
- Procesamiento de voz local con Vosk. El asistente interpreta solicitudes; las ventas todavía requieren revisión y confirmación en el formulario manual.

La estudiante informó que instaló el APK debug en un Motorola G60 y guardó un producto de prueba. Es un resultado reportado, no una prueba formal; archivar pasos y evidencia anonimizada antes de presentarlo como tal.

## Acceso a la API y sincronización

- «Vincular cuenta del servidor» usa `POST /api/v1/auth/login`. La contraseña no se guarda; el token JWT sí se conserva para el worker. El token dura 15 minutos. Si la API devuelve `401`, se elimina el token y hace falta iniciar sesión otra vez; las ventas locales pendientes se conservan.
- El formulario de registro usa `POST /api/v1/auth/register`. Si no hay conexión, permite continuar solo después de guardar el perfil/PIN localmente y lo indica explícitamente; la cuenta de nube debe vincularse después desde Sincronización.
- `http://10.0.2.2:3000/` es la URL predeterminada para emuladores Android. En `mobile/gradle.properties`, configura `MYPES_API_BASE_URL=http://<IP-LAN-del-equipo>:3000/` para un teléfono conectado a una red local confiable. No expongas el backend de desarrollo a Internet.
- WorkManager envía las ventas pendientes a `POST /api/v1/transacciones/batch` y descarga productos modificados de `GET /api/v1/products/sync`, en páginas de hasta 500 elementos. Los cambios remotos de nombre y precio conservan el stock local.
- Los cobros de fiados se ponen en el Outbox con tipo `COBRO` y se envían idempotentemente por el mismo endpoint. El reporte CSV incluye las ventas locales del día y se comparte solo después de una acción explícita del usuario.
- Los productos creados solo en el dispositivo aún no se envían al catálogo del backend. La sincronización del catálogo es de descarga.
- La voz puede sugerir producto y cantidad y abrir el formulario de venta; la persona debe revisar y confirmar. El PIN local no se utiliza para iniciar sesión automáticamente en la API ni hay credenciales de demostración integradas.
- Docker Compose inicia el backend y PostgreSQL localmente, pero no configura acceso desde el teléfono ni crea cuentas API. Crea el propietario según [las instrucciones del backend](../backend/README.md); no uses credenciales fijas ni compartidas.

Probar por separado la API, el registro offline local y la sincronización. Usar datos ficticios y no incluir tokens en la evidencia.

## Modelo Vosk

El modelo no se versiona por su tamaño y permanece excluido de Git. Desde `mobile/`, instalarlo para la compilación local con:

```powershell
.\download-vosk-model.ps1
```

El script descarga el modelo pequeño de español Vosk 0.42 desde alphacephei.com y verifica que el archivo `am/final.mdl` esté presente. El código espera los archivos en `app/src/main/assets/vosk-model-small-es-0.42/`. Cada máquina que compile la app debe instalarlo; sin el modelo, no afirmar que el reconocimiento de voz está probado en el teléfono. Registrar idioma, modelo, dispositivo, entorno y resultado.

## Compilar y probar

1. Abrir `mobile/` en Android Studio y esperar la sincronización de Gradle.
2. Seleccionar un dispositivo o emulador y compilar la variante debug.
3. Ejecutar las pruebas unitarias JVM desde `mobile/` con `.\gradlew.bat :app:testDebugUnitTest`; los resultados JUnit quedan en `app/build/test-results/testDebugUnitTest/`.
4. Probar con datos ficticios el PIN, onboarding, alta y persistencia de productos, modo avión y registro de ventas.
5. Probar voz y sincronización por separado; anotar cualquier comportamiento pendiente.
6. Registrar la evidencia según [`COMO_DOCUMENTAR.md`](../COMO_DOCUMENTAR.md) y guardarla bajo `docs/evidencias/`.

No incluir APK, keystore, token, datos reales de comerciantes ni audios identificables en el repositorio.

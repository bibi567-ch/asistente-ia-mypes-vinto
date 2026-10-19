# Aplicación Android

Módulo Kotlin + Jetpack Compose bajo app/. La rama main revisada incluye persistencia local Room/SQLite, autenticación local por PIN y componentes de PLN, ventas y sincronización diferida. La presencia de código no demuestra que todos los flujos de UI estén integrados o probados de extremo a extremo.

## Funciones y componentes en el código

- Login local con PIN de cuatro dígitos, derivación PBKDF2-HMAC-SHA256 y política de bloqueo temporal. El login local no es el login JWT del backend.
- Onboarding de plantilla, dashboard e inventario Compose.
- Entidad, DAO y base de datos Room persistente en mypes-local.db.
- El formulario de inventario permite crear productos. En la pantalla revisada no hay opción para eliminar un producto; el DAO tampoco define esa operación.
- Código para ventas, detalles de venta, caja, Outbox, Retrofit y un Worker de WorkManager para sincronización.
- Pipeline local de NLU/Vosk y solicitud de permiso de micrófono.

La estudiante informó que el APK debug se instaló en un Motorola G60 y que un producto de prueba se guardó. Esta es una demostración puntual reportada en conversación; archivar pasos y evidencia anonimizada antes de llamarla prueba formal.

## Sincronización: límites conocidos

- El backend requiere un token Bearer para acceder a rutas protegidas. El worker móvil lee un token de AuthSession; el login por PIN es local y no debe confundirse con la autenticación de la API.
- ApiClient usa http://10.0.2.2:3000/, dirección del host para emuladores Android. No es la dirección LAN del equipo para un teléfono físico.
- Docker Compose inicia la API y PostgreSQL localmente; no configura por sí solo el acceso desde el teléfono ni el token de la app.
- Probar por separado la API, el registro offline local y la sincronización. Registrar request/response con datos ficticios y sin copiar tokens a evidencia.

## Modelo Vosk

El modelo no se versiona por su tamaño. El código espera los archivos locales del modelo en:

mobile/app/src/main/assets/vosk-model-small-es-0.42/

Sin el modelo instalado, no afirmar que el reconocimiento de voz está probado en el teléfono. Registrar idioma, modelo, dispositivo, entorno y resultado.

## Compilar y probar

1. Abrir la carpeta mobile/ en Android Studio y esperar la sincronización de Gradle.
2. Seleccionar un dispositivo/emulador y compilar la variante debug.
3. Probar cada flujo con datos ficticios: PIN, configuración inicial, alta de producto, persistencia tras reabrir y modo avión.
4. Probar ventas, voz y sincronización por separado; anotar cualquier comportamiento pendiente.
5. Guardar evidencia según evidence/README.md y relacionarla con la matriz de tres materias.

No incluir APK, keystore, token, datos reales de comerciantes ni audios identificables en el repositorio.

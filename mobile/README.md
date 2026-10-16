# Aplicación Android

El módulo `app/` contiene el primer esqueleto compilable de Android: Kotlin + Jetpack Compose, Room/SQLite local y un pipeline NLU determinista preparado para Vosk.

## Línea base acordada

- Kotlin + Jetpack Compose.
- Room sobre SQLite para lectura y persistencia local.
- WorkManager + Outbox para sincronización diferida.
- Venta manual/local antes de voz y modelos de IA.

## Primer incremento implementado

- Proyecto Android Studio bajo `mobile/`, con configuración Gradle y SDK mínimo 26.
- Entidad `Producto`, DAO y base local Room persistente en `mypes-local.db`.
- Usuario local con PIN derivado mediante PBKDF2-HMAC-SHA256 y bloqueo temporal tras intentos fallidos.
- Plantillas iniciales de negocio y repositorio para cargarlas durante onboarding.
- Clasificador de intenciones y extractor de entidades para texto en español.
- Fachada y motor Vosk offline; el modelo se copia desde `app/src/main/assets/` al almacenamiento privado de la aplicación.
- Navegación Compose para login, onboarding de plantilla, dashboard e inventario.
- Solicitud de permiso de micrófono en tiempo de ejecución.

El modelo Vosk no se versiona por su tamaño. Descárgalo localmente en:

`mobile/app/src/main/assets/vosk-model-small-es-0.42/`

La aplicación sigue siendo un primer incremento: registro de ventas, outbox, WorkManager y sincronización con la API aún no están implementados. El botón de voz transcribe y clasifica texto, pero todavía no confirma ni persiste una venta.

## Siguientes incrementos

1. Crear proyecto Android Studio y documentar versiones/herramientas.
2. Implementar catálogo local y formulario de venta.
3. Probar persistencia tras cerrar la app y en modo avión.
4. Integrar API/sync cuando exista contrato y auth.
5. Añadir Vosk después de medir en el dispositivo disponible.

Asignar responsable, criterio de aceptación y evidencia a cada tarea en el tablero. No subir keystores, tokens, datos reales ni grabaciones identificables.

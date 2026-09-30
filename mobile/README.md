# Aplicación Android

No existe todavía un proyecto Android Studio en esta carpeta. El repositorio contiene documentación de la arquitectura móvil propuesta; no la presentes como una app compilable.

## Línea base acordada

- Kotlin + Jetpack Compose.
- Room sobre SQLite para lectura y persistencia local.
- WorkManager + Outbox para sincronización diferida.
- Venta manual/local antes de voz y modelos de IA.

## Primer incremento sugerido

1. Crear proyecto Android Studio y documentar versiones/herramientas.
2. Implementar catálogo local y formulario de venta.
3. Probar persistencia tras cerrar la app y en modo avión.
4. Integrar API/sync cuando exista contrato y auth.
5. Añadir Vosk después de medir en el dispositivo disponible.

Asignar responsable, criterio de aceptación y evidencia a cada tarea en el tablero. No subir keystores, tokens, datos reales ni grabaciones identificables.

# Preguntas probables y respuestas técnicas

## ¿Por qué Node.js y no Java 17?

El backend actual está implementado en Node.js 24 con TypeScript y Express. Node no elimina la necesidad de calidad: se valida con compilación estricta, pruebas automatizadas y contratos HTTP. Java 17 sería una alternativa válida, pero cambiar de runtime sin un requisito no aporta valor y ampliaría el riesgo.

## ¿Qué pasa si no hay internet?

El perfil local, la operación comercial y el Outbox pueden continuar. Las funciones que requieren JWT, PostgreSQL o un proveedor cloud quedan pendientes hasta recuperar conectividad. No se debe afirmar que toda función cloud opera offline.

## ¿Cómo evitan duplicar ventas?

Cada operación usa un identificador estable y el backend debe aplicar idempotencia. El reintento puede repetirse, pero la restricción y el procesamiento remoto deben producir una sola operación lógica. Esto requiere una prueba contra PostgreSQL, no solo una captura del Outbox.

## ¿Por qué no usan `fallbackToDestructiveMigration()`?

Porque convierte un problema de migración en pérdida de datos. En una aplicación de ventas es inaceptable borrar perfil, ventas o pendientes automáticamente. Se prefieren migraciones explícitas, respaldo y una ruta de recuperación controlada.

## ¿Por qué existen PIN y contraseña?

El PIN autentica el uso local del dispositivo sin depender de red. La contraseña autentica la cuenta cloud y permite obtener un JWT. Son amenazas, momentos y almacenes distintos; mezclarlos rompería el modo offline.

## ¿La IA entiende quechua y aimara?

La app tiene Vosk en español y clasificación local por reglas. Existe un servicio experimental Quechua con Whisper y MiniLM, pero no está habilitado por defecto ni se ha probado funcionalmente; Aimara no debe presentarse como una capacidad validada. La precisión por idioma requiere una campaña WER con corpus, participantes y evidencia.

## ¿El ASR cloud ya es real?

En Compose, el modo por defecto es `simulated`. Google Speech, Whisper o el servicio Quechua se pueden configurar, pero el resultado registrado no demuestra que un proveedor real haya transcrito audio. Para afirmar soporte cloud se necesitan proveedor configurado, credenciales fuera del repositorio y prueba reproducible.

## ¿Dónde están las APIs y cómo se conecta Android?

Las 20 rutas y su autenticación están enumeradas en el [contrato detallado](../.github/modernize/assessment/engines/facts/api-service-contracts.md). Android fija su base URL desde `BuildConfig.API_BASE_URL` en `ApiClient.kt`; el [mapa técnico](06_mapa_tecnico_apis_conexiones_ia.md) detalla Railway, emulador (`10.0.2.2`) y teléfono físico.

## ¿Dónde están las redes neuronales?

Vosk y su modelo acústico español están empaquetados en los assets Android. El clasificador de intenciones Android es código de reglas, no una red neuronal. Gemini es un LLM externo opcional; Whisper y MiniLM pertenecen al servicio experimental Quechua. No presentar el modelo remoto como desplegado o medido sin evidencia.

## ¿Qué patrón arquitectónico es el más importante?

El Outbox es el principal para el problema de conectividad: la operación se confirma localmente y la entrega remota se reintenta. MVVM/StateFlow organiza la UI, Room/Repository encapsula persistencia y la confirmación visual protege acciones sensibles.

## ¿Qué queda pendiente?

Cerrar la integración PostgreSQL, demostrar el flujo en un dispositivo real, medir WER/SUS/latencia/recursos y revisar que todos los diagramas distingan implementación, simulación y propuesta.

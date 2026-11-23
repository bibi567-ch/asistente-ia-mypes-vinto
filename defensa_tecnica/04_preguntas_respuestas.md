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

La arquitectura contempla variantes locales, pero la implementación actualmente demostrable usa Vosk local y clasificación por reglas con alcance limitado. La precisión por idioma requiere una campaña WER con corpus, participantes y evidencia. No debe defenderse como resultado medido si no existe esa campaña.

## ¿El ASR cloud ya es real?

El contrato está preparado y el modo por defecto es `simulated`. Las pruebas validan el contrato y el manejo de errores, no una transcripción real. Para afirmar soporte cloud se necesitan proveedor configurado, credenciales fuera del repositorio y prueba reproducible.

## ¿Qué patrón arquitectónico es el más importante?

El Outbox es el principal para el problema de conectividad: la operación se confirma localmente y la entrega remota se reintenta. MVVM/StateFlow organiza la UI, Room/Repository encapsula persistencia y la confirmación visual protege acciones sensibles.

## ¿Qué queda pendiente?

Cerrar la integración PostgreSQL, demostrar el flujo en un dispositivo real, medir WER/SUS/latencia/recursos y revisar que todos los diagramas distingan implementación, simulación y propuesta.

# 03. Documento de arquitectura de software (SAD)

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Arquitectura técnica actual y pendientes
**Actualizado:** 23/11/2026

## 1. Arquitectura tecnológica

El sistema se divide en una capa Android de interacción y una capa backend de servicios.

### A. Capa Edge (Android)

- **Reconocimiento offline:** Vosk con audio PCM a 16 kHz. El modelo español reside en assets y se extrae al almacenamiento privado de la app.
- **Clasificación de intenciones:** clasificador por reglas Kotlin y extractor de entidades. LiteRT/TensorFlow Lite es una alternativa futura; no está incorporado en el código actual.
- **Confirmación y ejecución:** el ViewModel expone `EsperandoConfirmacion`; la interfaz muestra un diálogo y solo después llama a `PhoneActionExecutor`.
- **Acciones del teléfono:** intents públicos de Android para llamadas/mensajes, alarma, apertura de aplicaciones y ajustes; `CameraManager` controla la linterna.
- **Respuestas:** TextToSpeech reproduce prompts, resultados y errores.
- **Persistencia offline:** Room mantiene usuario y `OutboxEntry`; WorkManager intenta sincronizar registros cuando hay conexión.

### B. Capa Cloud (Backend en Docker)

- **API REST:** Node.js 24, TypeScript y Express. Conserva autenticación JWT y expone health, ASR y recepción de logs de voz.
- **Cloud ASR:** el backend reenvía audio a Google Speech-to-Text o Whisper según configuración de entorno, y devuelve `{ "texto": "..." }`.
- **Persistencia:** PostgreSQL conserva los logs por negocio. La clave idempotente `(business_id, event_id)` evita duplicados en reintentos.
- **Ejecución local:** Docker Compose levanta PostgreSQL y el backend; el puerto API configurado es 3000 y se enlaza a loopback.

## 2. Flujo de operación híbrido

1. El usuario inicia una orden con el micrófono y la app captura audio.
2. Vosk reconoce localmente el segmento y calcula confianza a partir de sus resultados.
3. Con confianza Vosk igual o mayor a `0.70`, el texto pasa al clasificador local.
4. Con confianza menor a `0.70`, se verifica conectividad validada. Si están configurados URL y JWT, se envía WAV a `POST /api/v1/asr/transcribe`; si no, se conserva el texto local.
5. Si el proveedor cloud falla, la app informa el fallo y usa el texto reconocido por Vosk.
6. La UI presenta `EsperandoConfirmacion` para acciones; ayuda, guía y consulta de hora se responden sin modificar el teléfono.
7. El usuario confirma o cancela. La app actualiza el Outbox con el resultado.
8. WorkManager envía registros pendientes a `POST /api/v1/logs/comandos` al recuperar conectividad y contar con un JWT.

## 3. Decisiones arquitectónicas

- **ADR-001: Vosk en el dispositivo.** Permite reconocimiento sin conexión; el modelo español es una dependencia local que debe estar disponible en builds limpios.
- **ADR-002: Docker Compose.** Proporciona un entorno reproducible para backend y PostgreSQL. Las claves y contraseñas se proporcionan mediante `.env` local, no se incorporan a la imagen ni al repositorio.
- **ADR-003: ASR externo detrás del backend.** Las credenciales de Google/Whisper permanecen del lado servidor; el cliente solo usa el endpoint protegido.
- **ADR-004: confirmación previa.** Las acciones externas del dispositivo requieren confirmación explícita para reducir ejecuciones accidentales.

## 4. Riesgos y trabajo futuro

- Integrar el inicio de sesión móvil con el backend para emitir/proveer un JWT y configurar `api_base_url`/`access_token` en `voice_cloud`.
- Validar API 26+, memoria, latencia, precisión de voz y permisos de cámara en dispositivos físicos; 3 GB de RAM es solo una recomendación inicial, no un mínimo medido.
- Resolver contactos por nombre y ampliar el mapeo de paquetes instalados.
- Incorporar LiteRT solo si una evaluación demuestra que mejora la clasificación frente a las reglas actuales.
- Evaluar estrategias de distribución/actualización de APK y modelos, con verificación de firma, integridad y rollback.

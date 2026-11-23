# 13. Plan de pruebas

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Protocolo definido; ejecución pendiente
**Actualizado:** 23/11/2026

## 1. Pruebas de viabilidad técnica

Estas pruebas son planificadas; los umbrales son criterios de aceptación y no resultados ya medidos.

- **Prueba 1 — Offline:** desactivar Wi-Fi y datos móviles. Probar los diez comandos, comprobar el texto/intent reconocido y medir el tiempo hasta la respuesta o acción. Confirmar que no se requiere backend para Vosk.
- **Prueba 2 — Online:** habilitar conectividad, configurar `ASR_PROVIDER` y una sesión backend vigente. Probar una orden con confianza Vosk menor a 70% y verificar la petición multipart a `/api/v1/asr/transcribe` y la respuesta `{ "texto": "..." }`.
- **Prueba 3 — Confirmación:** comprobar que aparece un diálogo antes de llamadas, mensajes, alarmas, apertura de apps, linterna y ajustes de red. Cancelar y verificar que no se ejecuta la acción.
- **Prueba 4 — TTS:** verificar en castellano las confirmaciones, respuestas informativas, acciones y errores, incluyendo disponibilidad del motor de voz del dispositivo.
- **Prueba 5 — Outbox:** registrar comandos con y sin conectividad, confirmar/ejecutar o cancelar, y verificar que el Worker reintenta al recuperar red sin duplicar registros en PostgreSQL.
- **Prueba 6 — Permisos y compatibilidad:** probar micrófono, cámara/linterna y llamadas en Android API 26 y posterior, incluyendo permisos denegados y acciones sin app compatible.
- **Prueba 7 — Estrés:** repetir los diez comandos en sesiones consecutivas, observar cierres/ANR, errores de audio, crecimiento de memoria, batería y estabilidad del modelo. Registrar dispositivo, versión Android y duración; no definir el sistema como estable sin evidencia en hardware.

## 2. Pruebas de campo y métricas

- **Muestra objetivo:** 20 comerciantes de la zona de Vinto, sujeto a consentimiento y disponibilidad. Usar identificadores anónimos y no guardar nombres ni datos de contacto.
- **WER (Word Error Rate):** pedir a cada participante diez comandos representativos. Con consentimiento, registrar audio; crear una referencia humana y comparar transcripción Vosk y cloud por separado. Reportar WER global y por condición/idioma. Criterio propuesto: `WER ≤ 25%`; no se considera aprobado hasta completar la medición.
- **SUS (System Usability Scale):** aplicar el cuestionario estándar de diez ítems, escala 1–5, después de las tareas. Criterio propuesto: promedio `≥ 70/100`; calcular con la fórmula estándar y no confundirlo con el promedio simple de respuestas.
- **Latencia:** medir por separado Vosk, backend/proveedor cloud y ejecución de la acción; reportar mediana y percentil 95.
- **Recursos:** medir RAM pico, almacenamiento ocupado por el modelo y batería en teléfonos de prueba; confirmar o corregir la recomendación inicial de 3 GB de RAM.
- **Seguridad de acciones:** registrar casos de permiso denegado, intención incorrecta, cancelación y ausencia de número. Ninguna acción sensible debe ejecutarse sin aceptación visual.

## 3. Pruebas automatizadas del backend

Desde `backend/`:

```powershell
npm test
npm run build
npm run typecheck:test
npm run test:integration
```

`npm test` valida autenticación, contrato ASR, validación de logs y rutas retiradas. `npm run test:integration` requiere PostgreSQL, migraciones aplicadas y sus variables de entorno.

## 4. Registro de resultados

Registrar resultados, cifras, incidencias y enlaces a evidencia en [Resultados de pruebas](14_Resultados_de_Pruebas.md). Los umbrales de esta sección son metas, no resultados. Las pruebas Android (`testDebugUnitTest`, `assembleDebug`) requieren restaurar `mobile/gradlew.bat` y `mobile/gradle/wrapper/`; voz, WER, SUS, latencia, batería y permisos necesitan emulador/dispositivo físico.

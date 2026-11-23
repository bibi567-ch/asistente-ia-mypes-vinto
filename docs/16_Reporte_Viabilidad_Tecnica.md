# 16. Reporte de viabilidad técnica

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Fecha de revisión:** 23/11/2026
**Estado:** Evaluación preliminar; no constituye validación de campo ni aprobación de despliegue

## 1. Resumen ejecutivo

El repositorio contiene una base técnica para un asistente Android con reconocimiento local Vosk, clasificación por reglas, confirmación, acciones Android, TTS, Outbox y backend Node.js/TypeScript con PostgreSQL. La viabilidad es **condicional**: las pruebas automatizadas del backend y la compilación TypeScript pasaron en el entorno de desarrollo, pero no se verificaron el build Android, la ejecución real de Docker, la transcripción con proveedor cloud ni las métricas de campo.

La integración de sesión móvil sigue pendiente: el login local por PIN no aprovisiona el JWT requerido por los endpoints cloud. No se debe presentar la solución como lista para producción o como evaluada con comerciantes hasta completar las validaciones documentadas en los documentos 13 y 14.

## 2. Alcance funcional evaluado

| Comando ilustrativo | Intención | Acción o respuesta del sistema |
|---|---|---|
| «Llama al 5551234567» | `LLAMAR_CONTACTO` | Solicita confirmación; usa la acción de llamada disponible si los permisos lo permiten o abre el marcador. |
| «Envía un mensaje al 5551234567 diciendo llego tarde» | `ENVIAR_MENSAJE` | Solicita confirmación y abre el compositor SMS; el usuario revisa y envía el mensaje. |
| «Lee en voz alta buenos días» | `LEER_TEXTO` | Lee el texto reconocido mediante TextToSpeech. |
| «Pon alarma a las 5» | `CREAR_ALARMA` | Solicita confirmación y abre la interfaz de alarmas del sistema. |
| «Abre WhatsApp» | `ABRIR_APP` | Solicita confirmación e intenta abrir la aplicación instalada reconocida. |
| «Qué hora es» | `CONSULTAR_HORA` | Responde la hora local en pantalla y mediante TTS. |
| «Prende la linterna» | `LINTERNA` | Solicita confirmación y activa la linterna si el dispositivo lo permite. |
| «Abre ajustes de red» | `AJUSTES_RED` | Solicita confirmación y abre ajustes de Wi-Fi. |
| «Guíame con el teléfono» | `GUIA_TELEFONO` | Presenta y lee una guía breve. |
| «Ayuda» | `AYUDA` | Presenta y lee las intenciones disponibles. |

Las frases son ejemplos de prueba, no una afirmación de precisión. La agenda no se consulta para resolver nombres; llamadas y mensajes requieren un número. La aplicación de mensajería conserva el control del envío final.

## 3. Operación híbrida

1. La app captura audio y Vosk intenta transcribirlo localmente.
2. Con confianza Vosk igual o mayor a `0.70`, el texto continúa por el clasificador local.
3. Con confianza inferior a `0.70`, el cliente puede enviar audio al endpoint ASR solo si hay conectividad validada, URL base y JWT vigentes.
4. El backend usa el proveedor configurado (Google Speech-to-Text o Whisper) y devuelve `{ "texto": "..." }`.
5. Si no hay sesión/red/proveedor o la solicitud cloud falla, la app informa el problema y conserva la ruta local cuando es posible.
6. La app solicita confirmación antes de la acción correspondiente; luego comunica el resultado por TTS y registra el evento local.

Las claves del proveedor se configuran únicamente en el backend. El contrato de endpoints y las limitaciones de integración se detallan en [API e integración](11_Plan_Implementacion_y_API.md); la arquitectura completa está en [SAD](03_SAD_Arquitectura.md).

## 4. Requisitos mínimos y supuestos de hardware

| Requisito | Valor / estado |
|---|---|
| Sistema operativo | Android 8.0 (API 26) o posterior, conforme al `minSdk` del proyecto. |
| Herramientas de compilación | Android SDK 35 y JDK 17; build aún no verificado por falta de Gradle Wrapper en el checkout. |
| Modelo offline | Vosk español pequeño, ubicado en `mobile/app/src/main/assets/vosk-model-small-es-0.42` cuando se prepara un build autorizado. |
| Tamaño del modelo | Aproximadamente 57.5 MiB en el workspace observado; el APK y el uso de almacenamiento final deben medirse. |
| Memoria | 3 GB RAM es una recomendación inicial para seleccionar dispositivos de prueba, no un mínimo medido ni una garantía de ejecución. |
| Red | No es necesaria para el reconocimiento local; se requiere conectividad y sesión válida para ASR cloud y sincronización. |

El modelo Vosk está excluido por la regla `.gitignore`; un build limpio debe obtenerlo desde una distribución autorizada, verificar su integridad e incluirlo de forma reproducible.

## 5. Evidencia técnica disponible

| Área | Resultado registrado | Estado |
|---|---|---|
| Backend automatizado | 11 pruebas aprobadas; el proveedor ASR se simula en pruebas. | Verificado en pruebas automatizadas. |
| TypeScript | `npm run build` y `npm run typecheck:test` completados correctamente. | Verificado en entorno de desarrollo. |
| Docker Compose | La configuración se validó; no se construyó ni levantó el servicio porque el daemon no estaba activo. | Configuración validada; runtime pendiente. |
| Android | No se generó APK ni se ejecutaron pruebas Android; falta Gradle Wrapper y Gradle instalado. | Pendiente. |
| ASR cloud real | No se ejecutó con credenciales de proveedor desde un dispositivo. | Pendiente. |
| WER, SUS, latencia, memoria y batería | No existen mediciones de campo verificadas en este repositorio. | Pendiente. |

El detalle de resultados y su plantilla de registro están en [Resultados de pruebas](14_Resultados_de_Pruebas.md). No utilizar cifras ilustrativas como resultados.

## 6. Despliegue de laboratorio

Los pasos reproducibles para configurar secretos locales, levantar Docker Compose, comprobar el health check y preparar la app Android están centralizados en la [Guía de despliegue](15_Guia_de_Despliegue.md). El backend se enlaza a `127.0.0.1:3000` en el entorno local. El flujo requiere credenciales válidas para habilitar un proveedor ASR real y una cuenta/JWT para los endpoints protegidos.

No ejecutar `docker compose down -v` si se deben conservar los datos locales de PostgreSQL.

## 7. Riesgos y condiciones de cierre

1. Restaurar Gradle Wrapper y ejecutar unit tests/build Android.
2. Integrar el inicio de sesión móvil con el backend sin exponer tokens.
3. Probar confirmación, cancelación, permisos, TTS y las diez intenciones en dispositivos representativos.
4. Levantar Docker, validar migraciones y comprobar sincronización Outbox/idempotencia.
5. Probar un proveedor ASR real con credenciales protegidas.
6. Ejecutar el protocolo de campo de 20 comerciantes, calcular WER y SUS y registrar latencia/observaciones anonimizadas.
7. Resolver los requisitos académicos pendientes sobre MongoDB/Mongoose y cliente web.

**Conclusión:** existe una base implementada y verificable parcialmente, pero la viabilidad integral permanece pendiente de build Android, pruebas de integración y evaluación de campo. El criterio definitivo debe basarse en los resultados reproducibles, no en las capacidades descritas por diseño.

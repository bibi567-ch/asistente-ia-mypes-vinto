# 14. Resultados de pruebas

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Registro inicial; métricas de campo pendientes
**Actualizado:** 25/11/2026

> **Estado del documento:** registro inicial. Los objetivos no son resultados. Completar las métricas únicamente después de ejecutar las pruebas y conservar evidencia verificable; no reemplazar estados pendientes con ejemplos.

## 1. Pruebas técnicas (QA)

| Prueba | Resultado observado | Estado / evidencia |
|---|---|---|
| Compilación Android / APK debug | La ejecución del 23/11 pasó; el 25/11 `mobile\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` terminó con código 1 antes de iniciar Gradle por acceso denegado al caché del wrapper en `%USERPROFILE%\.gradle`. | **Build previo aprobado; revalidación bloqueada por el sandbox**. No se generó APK en esta ejecución. |
| Backend: pruebas automatizadas | En GitHub Actions Run [#45568677216](https://github.com/bibi567-ch/asistente-ia-mypes-vinto/actions/runs/45568677216), commit `460480ddcb96632513bb4c6be187c10315785b0f`, migraciones y pruebas unitarias e integración PostgreSQL terminaron correctamente. La ejecución local posterior de `npm.cmd test` terminó con código 1: 7/26 aprobadas y 19/26 fallidas porque el proxy del sandbox rechazó las conexiones HTTP a loopback (502/timeout 10060). | **CI aprobado para el commit indicado; ejecución local actual bloqueada por loopback del sandbox**. La ejecución CI no cubre el cambio posterior de dependencias. |
| Backend: compilación y tipos | Para el estado posterior a la actualización de dependencias, `npm.cmd run build` y `npm.cmd run typecheck:test` finalizaron con código 0. | **Aprobado localmente**; no se ejecutó CI para esta actualización. |
| Backend: auditoría de dependencias | Los escaneos iniciales reportaron `body-parser@2.3.0` HIGH (GHSA-27r3-jxmh-p828) y `qs@6.16.0` MODERATE (GHSA-6pq8-jhm2-2vcw). Tras `npm.cmd audit fix`, quedaron `body-parser@2.4.0` y `qs@6.17.1`. `npm.cmd audit --omit=dev` y `npm.cmd audit` reportaron cero vulnerabilidades; `appmod-cve-assessment` no encontró CVE conocidos en las versiones corregidas. | **Remediado y re-auditado localmente**. No implica certificación de CI ni ausencia de vulnerabilidades desconocidas. |
| Backend API en Railway | Tras el fallo inicial por CA no confiable, se configuró la CA pública en `DATABASE_SSL_CA`, `DATABASE_URL=${{Postgres.DATABASE_URL}}` (variable existente en el servicio) y `DATABASE_SSL_REJECT_UNAUTHORIZED=true`. El deployment `dfac6edf-cb63-4e65-8857-60aa640b9fcb` informó 8 migraciones al día y la API escuchando en el puerto 8080. `GET https://blissful-enthusiasm-production-95638378.up.railway.app/api/v1/health` devolvió `{"status":"ok","service":"mypes-vinto-api","database":"connected"}`. Postgres permanece online y privado. | **APROBADO para despliegue y health check**. No prueba flujos autenticados, Android ni ASR real. La prueba negativa con CA incorrecta sigue pendiente. |
| URL Android de Railway | `mobile/app/build.gradle.kts` establece el dominio HTTPS de Railway como valor predeterminado y mantiene el parámetro `MYPES_API_BASE_URL` para overrides. Gradle no se ejecutó porque el acceso al lockfile de la caché fue denegado por el sandbox; no existe evidencia de build o conexión desde dispositivo en esta ejecución. | **Configuración aplicada; compilación y prueba física pendientes**. |
| Proxy y política de URL ASR | Se agregó una prueba unitaria de la política: HTTPS se acepta, HTTP se acepta solo para `.railway.internal`, y HTTP público, hostname spoofeado y otros protocolos se rechazan. El caso enfocado pasó 1/1. | **Política aprobada en test local**; la llamada al servicio real sigue pendiente. |
| Microservicio ASR quechua | Se añadieron `quechua-asr/main.py`, `requirements.txt` y `Dockerfile`. Whisper Puno tiene ID y revisión predeterminados; el token sigue obligatorio. El código carga también un encoder MiniLM y devuelve intent/score junto al texto. Pylance informó sintaxis limpia, pero imports sin resolver (`numpy`, `soundfile`, `torch`, `scipy`, `transformers`) en el intérprete actual; no se instaló ni ejecutó el runtime. | **Código conectado; runtime, dependencias, descarga y despliegue pendientes**. |
| Flujo móvil de intenciones | Se conectó la etiqueta remota al clasificador Kotlin y a consultas de inventario/ventas o navegación a los formularios existentes. La intención remota se rechaza bajo score `0.62`; no se guarda una venta sin el flujo explícito del formulario. El modo Quechua fuerza cloud cuando hay conexión/token y no ejecuta acciones si no hubo transcripción. Gradle no se ejecutó por el bloqueo previo del wrapper. | **Pendiente de pruebas JVM, integración y dispositivo**. |
| Integración de respuesta ASR Node | `npm.cmd run build` y `npm.cmd run typecheck:test` pasaron; la política de URL pasó 1/1. La prueba `test/asr-quechua.test.ts` falló al conectar con loopback local: proxy sandbox 502; por ello no valida el nuevo contrato de intent en ejecución. | **Compilación aprobada; E2E API bloqueada por proxy**. |
| Dependencias Python ASR | El evaluador CVE disponible no acepta PyPI. El fallback LLM no identificó hallazgos verificables, pero tampoco certifica las versiones actuales; se requiere pip-audit/OSV antes de desplegar. | **Pendiente; no se afirma lista limpia**. |
| Docker Compose | La configuración anterior pasó `docker compose config --quiet` con variables temporales de prueba; la configuración actualizada del 25/11 no se pudo validar en esta sesión. | **Validación previa aprobada; cambio actual pendiente**; no implica que los servicios hayan iniciado. |
| Imagen/servicios Docker | No se construyeron ni iniciaron: Docker Desktop no tenía activo el daemon Linux. | **Pendiente / bloqueada**. |
| Modo offline en teléfono | No hay resultados de dispositivo adjuntos. | **Pendiente**: probar los diez comandos con Wi-Fi y datos desactivados. |
| Modo online con ASR real | No se probó proveedor cloud con credenciales reales ni desde el teléfono. | **Pendiente**: configurar proveedor, backend, JWT y conexión de la app. |
| Diálogo de confirmación y TTS | No se verificó en emulador ni dispositivo físico. | **Pendiente**. |

### Criterios para cerrar QA

- Guardar nombre/modelo del dispositivo de prueba, versión Android, versión APK, fecha y pasos reproducibles.
- No afirmar “APK exitosa”, “9/10 reconocidos” o “<2 segundos” hasta adjuntar una compilación y mediciones reales.
- Probar cancelación y permisos denegados, además del caso exitoso, antes de marcar confirmación como aprobada.

## 2. Pruebas de campo y métricas

**Muestra objetivo:** 20 comerciantes del centro de Vinto. A la fecha de este registro no se han aportado hojas de resultados; el número de participantes completados y los resultados siguen sin verificar.

| Métrica | Resultado real | Objetivo | Estado |
|---|---:|---:|---|
| Participantes con prueba completada | No registrado | 20 | Pendiente |
| WER de Vosk offline | No medido | ≤25% | Pendiente |
| WER de ASR cloud | No medido | Reportar como comparación; fijar criterio antes del estudio | Pendiente |
| SUS promedio | No medido | ≥70/100 | Pendiente |
| Latencia de Vosk | No medida | Registrar mediana y P95 | Pendiente |
| Latencia de ASR cloud | No medida | Registrar mediana y P95; no asumir <2 s | Pendiente |
| Comandos más usados | No medidos | Reportar conteos y porcentaje del total observado | Pendiente |

### Hoja de captura WER

Crear una fila por comando y participante, usando un identificador anónimo:

| ID anónimo | Comando objetivo | Idioma/variante | Referencia literal | Texto Vosk | Sustituciones (S) | Borrados (D) | Inserciones (I) | Palabras referencia (N) | WER |
|---|---|---|---|---|---:|---:|---:|---:|---:|
| P01 | CMD-01 | Pendiente | Pendiente | Pendiente | — | — | — | — | — |

Fórmula: `WER = (S + D + I) / N × 100`. Calcularla sobre transcripciones anonimizadas y reportar claramente cómo se agregan los comandos y participantes. Separar castellano y quechuañol cuando la muestra permita una comparación válida. Grabar audio solo con consentimiento informado; protegerlo y eliminarlo según el protocolo de investigación aprobado.

### Hoja de captura SUS

Aplicar los diez ítems estándar en escala 1–5 después de tareas definidas. Calcular cada cuestionario con la fórmula SUS estándar: para ítems impares, respuesta menos 1; para pares, 5 menos respuesta; sumar y multiplicar por 2.5. Reportar respuestas agregadas y número de cuestionarios válidos, sin identificar participantes.

| Cuestionarios válidos | Puntaje promedio SUS | Objetivo | Observaciones anonimizadas |
|---:|---:|---:|---|
| No registrado | No medido | ≥70/100 | Pendiente |

### Uso de comandos

| Intención | Conteo observado | Porcentaje |
|---|---:|---:|
| Linterna | No medido | No medido |
| Alarma | No medido | No medido |
| Llamar | No medido | No medido |
| Mensaje | No medido | No medido |
| Otras intenciones | No medido | No medido |

Actualizar este documento con resultados reales, fecha, tamaño de muestra, metodología, incidencias y ubicación de evidencia una vez concluida la campaña.

# 14. Resultados de pruebas

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Registro inicial; métricas de campo pendientes
**Actualizado:** 25/11/2026

> **Estado del documento:** registro inicial. Los objetivos no son resultados. Completar las métricas únicamente después de ejecutar las pruebas y conservar evidencia verificable; no reemplazar estados pendientes con ejemplos.

## 1. Pruebas técnicas (QA)

| Prueba | Resultado observado | Estado / evidencia |
|---|---|---|
| Compilación Android / APK debug | La ejecución del 23/11 pasó; el 25/11 `mobile\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` terminó con código 1 antes de iniciar Gradle por acceso denegado al caché del wrapper en `%USERPROFILE%\.gradle`. | **Build previo aprobado; revalidación bloqueada por el sandbox**. No se generó APK en esta ejecución. |
| Backend: pruebas automatizadas | En GitHub Actions Run [#45568677216](https://github.com/bibi567-ch/asistente-ia-mypes-vinto/actions/runs/45568677216), commit `460480ddcb96632513bb4c6be187c10315785b0f`, migraciones y pruebas unitarias e integración PostgreSQL terminaron correctamente. El intento local del 25/11 fue 7/25 aprobado y 18/25 bloqueado por `EACCES` al conectar con loopback. | **Aprobado en CI para ese commit**; ejecución local bloqueada por el sandbox. ASR de pruebas simulado, no API cloud real. |
| Backend: compilación y tipos | GitHub Actions Run [#45568677216](https://github.com/bibi567-ch/asistente-ia-mypes-vinto/actions/runs/45568677216) y los comandos locales `npm.cmd run build` / `npm.cmd run typecheck:test` finalizaron correctamente. | **Aprobado** en CI y localmente para el commit/estado documentado. |
| Backend API en Railway | Servicio `blissful-enthusiasm` conectado al repositorio en `main`, Root Directory `/backend`; `JWT_SECRET` aleatorio (no registrado), `NODE_ENV=production`, `ASR_PROVIDER=simulated` y health check `/api/v1/health`. Deployment `f2de95fa-2feb-462d-82f6-e567a54d83c5` falló en migraciones con `self-signed certificate in certificate chain` y rollback. `DATABASE_URL` quedó corregida a la referencia privada `${{Postgres.DATABASE_PRIVATE_URL}}` como cambio pendiente; `DATABASE_SSL_CA` no está configurada. La CLI requiere autorización y su login fue bloqueado por la política del sandbox al escribir en su caché. PostgreSQL sigue `Online` y privada; no se generó dominio ni se verificó el endpoint. | **FALLIDO / BLOQUEADO** por CA TLS no configurada y caché de Railway CLI denegada por sandbox. Mantener validación TLS estricta; pendiente habilitar la ruta bloqueada, configurar la CA pública, aplicar el cambio pendiente y volver a validar migraciones y `/api/v1/health`. |
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

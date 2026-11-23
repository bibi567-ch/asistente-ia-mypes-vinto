# 05. Product backlog y responsabilidades

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Backlog de consolidación y validación; refinar en GitHub Issues
**Actualizado:** 23/11/2026

## 1. Responsabilidades

| Integrante | Área de coordinación | Resultado verificable |
|---|---|---|
| Eva Lizeth Chino Quispe | Coordinación, requisitos y QA documental | Alcance, índice, revisiones y evidencias consistentes. |
| Christian Gonzales Mamani | IA y flujo conversacional | Intenciones, extracción de parámetros y conjunto de evaluación. |
| Marializ Mamani Quispe | Backend, seguridad y persistencia | API, autenticación, PostgreSQL, migraciones y sincronización. |
| Kevin Jhonathan Rocha Rojas | UX/UI y prototipado | Dashboard de voz, confirmación, estados y accesibilidad. |
| Luis Gerardo Lazo Bustamante | Arquitectura y QA técnico | SAD, diagramas, contratos, trazabilidad y validaciones reproducibles. |

La responsabilidad indica quién coordina la tarea; no impide revisión cruzada ni colaboración.

## 2. Backlog priorizado

| ID | Trabajo | Criterio de aceptación | Estado |
|---|---|---|---|
| BL-01 | Integrar sesión móvil con el backend | La app recibe URL base y JWT de forma segura, y los configura para ASR y sincronización. | Pendiente; el PIN local no provisiona JWT. |
| BL-02 | Restaurar build Android reproducible | Añadir Gradle Wrapper versionado y ejecutar `testDebugUnitTest` y `assembleDebug` en CI. | Bloqueado; no hay wrapper en el checkout. |
| BL-03 | Validar flujo de voz offline | Probar las diez intenciones, permisos y errores en Android API 26+ sin conexión. | Pendiente en dispositivo. |
| BL-04 | Validar confirmación y TTS | Demostrar confirmación, cancelación, resultado y error sin ejecutar acciones canceladas. | Pendiente en dispositivo. |
| BL-05 | Completar evaluación ASR cloud | Configurar proveedor real, JWT y backend; medir latencia y registrar fallback offline. | Pendiente; los tests usan proveedor simulado. |
| BL-06 | Validar sincronización Outbox | Crear eventos offline, recuperar conectividad, sincronizar con JWT y comprobar idempotencia. | Pendiente extremo a extremo. |
| BL-07 | Evaluar métricas de campo | Aplicar protocolo con 20 comerciantes, 10 comandos, WER y SUS; anonimizar registros. | Pendiente; ver [plan](13_Plan_de_Pruebas.md). |
| BL-08 | Validar Docker | Construir e iniciar servicios con Docker activo y comprobar health check, migraciones y persistencia. | Pendiente; daemon no disponible durante la última validación. |
| BL-09 | Cerrar decisiones académicas | Documentar respuesta sobre MongoDB/Mongoose, cliente web y stack compartido. | Pendiente de confirmación docente. |
| BL-10 | Revisar artefactos de arquitectura | Alinear C1/C2, SAD, contratos, índice y código con el alcance conversacional actual. | En curso. |
| BL-11 | Preparar evidencia reproducible | Registrar fecha, versión, entorno, pasos y resultado para cada criterio que se afirme verificado. | En curso. |

## 3. Secuencia recomendada

1. Resolver sesión móvil y Gradle Wrapper.
2. Ejecutar pruebas de unidad, build y backend con la configuración del proyecto.
3. Validar llamadas, mensajes, alarma, linterna y otros permisos en dispositivo.
4. Probar fallback cloud con credenciales exclusivamente del lado servidor.
5. Validar Outbox y reintentos idempotentes.
6. Ejecutar el protocolo de campo y actualizar [resultados](14_Resultados_de_Pruebas.md).
7. Revisar la entrega y los requisitos pendientes por asignatura.

## 4. Reglas de gestión

- Gestionar cada elemento como issue pequeño, con responsable, dependencias y aceptación comprobable.
- Usar estados coherentes: `Pendiente`, `En curso`, `Bloqueado` y `Verificado`.
- Cambiar a `Verificado` solo con evidencia reproducible; un documento de diseño no demuestra ejecución.
- Integrar cambios mediante PR y revisión cruzada.
- No guardar credenciales, información personal ni audio identificable en el repositorio.

# Matriz de evidencia para cerrar la defensa

| Área | Demostración mínima | Comando/escenario | Evidencia requerida | Estado actual |
|---|---|---|---|---|
| Arquitectura C4 | Contexto y contenedores coherentes con el código | Revisión de diagramas y rutas | PNG/SVG fechado + referencia a SAD | Parcial |
| Login local | PIN correcto, incorrecto y bloqueo | Prueba en dispositivo | Captura/video anonimizado + pasos | Pendiente físico |
| Registro | Perfil local y respuesta de API | `POST /api/v1/auth/register` | Respuesta sin secretos + registro DB | Parcial |
| Ventas offline | Venta sin red y Outbox pendiente | Modo avión + UI | Video/capturas + entrada Outbox | Pendiente físico |
| Sincronización | Reintento e idempotencia | `npm run test:integration` | Log backend + consulta con datos ficticios | Bloqueado por PostgreSQL |
| Asistente | Clasificación y confirmación | Tests JVM + flujo UI | Reporte JUnit + video | Automatizado aprobado; UI pendiente |
| Privacidad | Corrección sin consentimiento no se envía | Caso opt-in/opt-out | Estado local y request observado | Automatizado; físico pendiente |
| ASR | Diferenciar local de cloud simulado | `npm test` | Resultado del test y configuración | Aprobado para modo simulado |
| Calidad | Build, tipos y pruebas | Comandos backend/Android | Logs completos con fecha | Aprobado en local |
| Métricas | WER, SUS, latencia y recursos | Protocolo de `docs/13` | Hojas anonimizadas y cálculo | Pendiente |

## Plantilla de evidencia

Cada archivo bajo `docs/evidencias/` debe contener:

```text
ID:
Fecha:
Commit:
Entorno:
Precondiciones:
Pasos:
Resultado esperado:
Resultado observado:
Estado:
Limitaciones:
Archivos relacionados:
```

No incluir tokens, contraseñas, datos reales, audios identificables ni capturas con información personal.

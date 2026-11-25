# Matriz de evidencia para cerrar la defensa

| Área | Demostración mínima | Comando/escenario | Evidencia requerida | Estado actual |
|---|---|---|---|---|
| Arquitectura C4 | Contexto y contenedores coherentes con el código | Revisión de diagramas y rutas | PNG/SVG fechado + referencia a SAD | Parcial |
| Login local | PIN correcto, incorrecto y bloqueo | Prueba en dispositivo | Captura/video anonimizado + pasos | Pendiente físico |
| Registro | Perfil local y respuesta de API | `POST /api/v1/auth/register` | Respuesta sin secretos + registro DB | Parcial |
| Ventas offline | Venta sin red y Outbox pendiente | Modo avión + UI | Video/capturas + entrada Outbox | Pendiente físico |
| Sincronización | Reintento e idempotencia | `npm run test:integration` | Log backend + consulta con datos ficticios | Bloqueado por PostgreSQL |
| Asistente | Clasificación local y consulta cognitiva | Tests previos de reglas; Gemini mock 2/2; UI/API HTTP pendientes | Reporte de tests + video | Mock aprobado; Android y ruta HTTP pendientes |
| IA generativa | Consulta cognitiva autenticada con Gemini opcional | Tests del proveedor mock; sin credencial real | Resultado test + llamada real autorizada | Mock 2/2; API HTTP y proveedor real pendientes |
| Privacidad | Corrección sin consentimiento no se envía | Caso opt-in/opt-out | Estado local y request observado | Automatizado; físico pendiente |
| ASR | Diferenciar Vosk local de cloud simulado | Pruebas de contrato registradas; prueba en dispositivo/proveedor real | Resultado del test y configuración | Simulación identificada; transcripción real pendiente |
| Calidad | Build, tipos y pruebas | Comandos backend/Android | Logs completos con fecha | Backend build/typecheck aprobados; suite HTTP 10/31 bloqueada; Android pendiente |
| Health backend | Respuesta API y conexión PostgreSQL | `GET /api/v1/health` | Respuesta HTTP y deployment identificado | Aprobado históricamente en Railway 25/11; no revalidado tras Gemini |
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

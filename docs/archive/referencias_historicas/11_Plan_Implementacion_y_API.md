# 11. Contrato API e integración

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Contrato del backend de voz
**Actualizado:** 23/11/2026

## 1. Propósito y límites

El backend Node.js/TypeScript/Express ofrece autenticación, transcripción cloud y recepción de registros de comandos. PostgreSQL conserva usuarios y logs de voz. No hay rutas activas de productos, ventas, inventario ni transacciones comerciales; esos módulos están fuera del alcance vigente.

El contrato documenta el estado actual. La integración móvil de sesión aún debe proveer URL base y JWT de forma segura para activar ASR cloud y sincronización.

## 2. Endpoints

| Método | Ruta | Autenticación | Función | Estado |
|---|---|---|---|---|
| `GET` | `/api/v1/health` | No | Estado de la API y disponibilidad de la persistencia. | Implementado |
| `POST` | `/api/v1/auth/login` | No | Autentica un usuario creado administrativamente y devuelve JWT de vida corta. | Implementado |
| `POST` | `/api/v1/auth/change-password` | Bearer JWT | Verifica contraseña vigente y permite reemplazarla; la nueva clave requiere al menos 12 caracteres. | Implementado |
| `POST` | `/api/v1/asr/transcribe` | Bearer JWT | Recibe archivo multipart `audio`, llama al proveedor configurado y devuelve `{ "texto": "..." }`. Límite de archivo: 5 MB. | Implementado; proveedor real requiere credenciales |
| `POST` | `/api/v1/logs/comandos` | Bearer JWT | Persiste un evento de voz con control de duplicados por evento/negocio. | Implementado |

No existe registro público. El propietario inicial se crea mediante el comando administrativo `admin:create-owner`.

## 3. Contratos principales

### Inicio de sesión

`POST /api/v1/auth/login`

```json
{
  "email": "propietario@example.test",
  "password": "<contraseña>"
}
```

La respuesta exitosa contiene `accessToken`, `tokenType` y `expiresIn`. El JWT caduca a los 15 minutos según la configuración actual del servicio.

### Transcripción

`POST /api/v1/asr/transcribe` requiere `Authorization: Bearer <accessToken>` y `multipart/form-data` con el archivo en el campo `audio`.

- `ASR_PROVIDER=google` requiere `GOOGLE_ASR_API_KEY`; `GOOGLE_ASR_LANGUAGE` es opcional.
- `ASR_PROVIDER=whisper` requiere `OPENAI_ASR_API_KEY`; `OPENAI_ASR_BASE_URL` es opcional.
- Sin proveedor/credenciales configurados, el servicio responde `503`.
- Un fallo del proveedor responde `500` sin exponer claves ni detalles sensibles.
- El endpoint de prueba usa mock; no acredita acceso a un proveedor cloud.

### Registro de comandos

`POST /api/v1/logs/comandos` requiere JWT y recibe `eventId`, `texto`, `intencion`, `confirmado`, `ejecutado` y `timestamp`. El cliente conserva el mismo `eventId` al reintentar. PostgreSQL impone idempotencia por negocio y evento.

## 4. Configuración y ejecución

La ejecución local y la configuración de Docker Compose se mantienen en la [guía de despliegue](15_Guia_de_Despliegue.md). El archivo `.env.example` contiene nombres de variables sin secretos; los valores reales se generan localmente y no se versionan.

Desde `backend/`, los comandos de validación son:

```powershell
npm ci
npm run db:migrate
npm run build
npm run typecheck:test
npm test
npm run test:integration
```

`npm run test:integration` requiere PostgreSQL y migraciones aplicadas. No marcar el backend listo para producción por pasar pruebas unitarias: faltan validaciones de seguridad, despliegue, sesión móvil y proveedor ASR real.

## 5. Seguridad y pendientes

- Mantener las claves de Google/Whisper y secretos JWT únicamente en configuración protegida del servidor.
- No habilitar tráfico externo ni usar datos reales hasta revisar autorización, TLS, limitación de intentos, auditoría y gestión de sesiones.
- El cliente móvil lee URL y JWT de preferencias privadas `voice_cloud`; el inicio de sesión local por PIN aún no provisiona esos valores.
- Probar en Docker activo migraciones, health check, persistencia y reintentos idempotentes.
- Evaluar separadamente cualquier requisito académico de MongoDB/Mongoose o interfaz web; PostgreSQL no se presenta como NoSQL.

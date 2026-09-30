# Backend API

## Estado actual

API local con Node.js 24, TypeScript, Express y PostgreSQL. Incluye migraciones SQL versionadas, alta administrativa de propietarios, inicio de sesión con contraseña hasheada mediante scrypt, JWT de 15 minutos y consulta/creación de productos limitadas al negocio del token. Hay pruebas automatizadas de autenticación, tokens y rechazo de acceso anónimo al catálogo.

Ventas, sincronización, actualización/borrado de productos, recuperación de contraseña, limitación de intentos y despliegue todavía no están implementados. No exponer esta API a una red ni usarla con datos reales.

## Requisitos

- Node.js 24.x, conforme al rango de `package.json`.
- npm.
- Docker Desktop con soporte Compose, o una instancia local de PostgreSQL compatible.

## Inicio local

Desde la raíz del repositorio, iniciar PostgreSQL:

```powershell
docker compose up -d postgres
```

Espera hasta que `docker compose ps` muestre PostgreSQL como `healthy`.

En otra terminal:

```powershell
Copy-Item backend/.env.example backend/.env
$secret = node -e "process.stdout.write(require('node:crypto').randomBytes(32).toString('base64url'))"
Add-Content backend/.env "JWT_SECRET=$secret"
Set-Location backend
npm ci
npm run db:migrate
npm run admin:create-owner -- "MYPE de prueba" "propietario@example.test"
npm run dev
```

Guarda la contraseña temporal que el comando imprime una sola vez. Comprueba `http://127.0.0.1:3000/api/v1/health`; el estado esperado es `ok` cuando PostgreSQL responde. La configuración y el host se mantienen locales; no publiques `.env`.

## Estructura

```text
backend/
  db/migrations/       Esquema inicial PostgreSQL
  src/config/          Configuración validada
  src/db/              Pool PostgreSQL
  src/routes/          Rutas HTTP
  src/app.ts            Composición Express
  src/server.ts         Inicio local
```

## API inicial

### Iniciar sesión

`POST /api/v1/auth/login`

```json
{
  "email": "propietario@example.test",
  "password": "<contraseña temporal>"
}
```

La respuesta devuelve `accessToken`, `tokenType` y `expiresIn`. El token expira en 15 minutos. No hay endpoint de registro público: el propietario se crea mediante el comando administrativo.

`POST /api/v1/auth/change-password` requiere el token Bearer y acepta `currentPassword` y `newPassword` (mínimo 12 caracteres). Úsalo para reemplazar la contraseña generada durante el alta.

### Consultar catálogo

`GET /api/v1/products?limit=50&offset=0`

Requiere `Authorization: Bearer <accessToken>`. El negocio se obtiene de los claims firmados, nunca de un parámetro enviado por el cliente. Límite permitido: 1–100 productos; offset: 0–100000.

### Crear producto

`POST /api/v1/products`

Requiere el mismo encabezado de autorización y un cuerpo JSON:

```json
{
  "name": "Harina",
  "unit": "bolsa",
  "salePrice": "12.50"
}
```

`salePrice` es una cadena decimal con hasta dos decimales para no introducir redondeo binario. Un nombre duplicado en el mismo negocio devuelve `409`.

## Comandos

```powershell
npm run build
npm run typecheck:test
npm test
npm run test:integration
npm run db:migrate
```

`npm test` no requiere PostgreSQL. `npm run test:integration` requiere PostgreSQL, migraciones aplicadas y limpia sus dos negocios de prueba al finalizar. El workflow de GitHub ejecuta ambos tipos de pruebas contra un servicio PostgreSQL temporal.

## Próximos incrementos

1. Completar pruebas de integración PostgreSQL, incluyendo aislamiento entre dos negocios.
2. Añadir actualización/borrado seguro de productos y paginación documentada.
3. Añadir limitación de intentos y revocación de sesiones antes de permitir acceso desde una red.
4. Añadir ventas, líneas y movimientos en transacción.
5. Añadir recepción de Outbox con UUID idempotente y resultado por operación.
6. Actualizar ADR/C4 y ampliar pruebas de seguridad antes de cualquier despliegue.

La materia Tecnologías en Internet incluye MongoDB/Mongoose; la profesora debe confirmar si se exige módulo NoSQL dentro del producto o basta una evidencia didáctica acotada. PostgreSQL no equivale a MongoDB y este backend no declara cubrir ese punto.

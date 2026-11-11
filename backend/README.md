# Backend API

## Estado actual

API local con Node.js 24, TypeScript, Express y PostgreSQL. Incluye migraciones SQL versionadas, registro de propietarios, inicio de sesión con contraseña hasheada mediante scrypt, actualización protegida del perfil del negocio, JWT de 15 minutos, consulta/creación de productos limitadas al negocio del token y recepción idempotente de lotes de transacciones offline. Hay pruebas automatizadas de autenticación, tokens y rechazo de acceso anónimo al catálogo.

Actualización/borrado de productos, recuperación de contraseña, limitación de intentos y despliegue todavía no están implementados. No exponer esta API a una red ni usarla con datos reales.

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

La respuesta devuelve `accessToken`, `tokenType` y `expiresIn`. El token expira en 15 minutos.

### Registrar propietario y negocio

`POST /api/v1/auth/register` crea el negocio y su propietario dentro de una transacción y devuelve una sesión Bearer:

```json
{
  "nombre": "Nombre completo",
  "nombres": "Nombres",
  "apellidos": "Apellidos",
  "carnet": "1234567",
  "expedido": "La Paz (LP)",
  "fechaNacimiento": "10/02/1980",
  "lugarNacimiento": "La Paz",
  "celular": "70000000",
  "email": "propietario@example.test",
  "password": "<mínimo 12 caracteres>"
}
```

Los correos se normalizan a minúsculas y la contraseña se almacena como hash scrypt; el PIN local nunca se envía al backend. El registro crea un negocio provisional con nombre derivado del propietario, que se completa después con el onboarding. Los datos incompletos o inválidos reciben `400`; un correo ya registrado recibe `409`.

`PUT /api/v1/auth/business` requiere `Authorization: Bearer <accessToken>` y un cuerpo con `nombreNegocio` y `rubro`. Actualiza únicamente el negocio asociado al token. El endpoint de registro aún no tiene limitación de intentos; mantenerlo en un entorno local/de pruebas y no exponerlo a Internet.

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

### Sincronización offline

`POST /api/v1/transacciones/batch` requiere el token y recibe hasta 100 transacciones:

```json
{
  "transacciones": [
    {
      "operationId": "00000000-0000-4000-8000-000000000001",
      "tipo": "VENTA",
      "payload": {
        "ventaId": "00000000-0000-4000-8000-000000000002",
        "productoId": "00000000-0000-4000-8000-000000000003",
        "cantidad": 1,
        "precioUnitario": 12.5,
        "total": 12.5,
        "formaPago": "CONTADO",
        "creadoEn": 1760572800000
      },
      "schemaVersion": 1
    }
  ]
}
```

Cada `operationId` es idempotente dentro del negocio del token. La respuesta devuelve `ACEPTADA`, `DUPLICADA`, `CONFLICTO` o `RECHAZADA` por operación; el tipo `COBRO` registra de forma idempotente el evento de cobro enviado desde el móvil. El backend de este MVP no mantiene un saldo de deuda remoto. `GET /api/v1/products/sync?desde=<timestamp-ms>` devuelve hasta 500 productos modificados después del timestamp. Cuando la página contiene 500 elementos, solicita la siguiente página con `cursorActualizadoEn` y `cursorId` tomados del último producto; ambos parámetros deben ir juntos. Repite con el mismo `desde` y guarda el `timestamp` de la primera respuesta como cursor para la siguiente sincronización. El stock permanece en el móvil durante este MVP.

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
5. Actualizar ADR/C4 y ampliar pruebas de seguridad antes de cualquier despliegue.

La materia Tecnologías en Internet incluye MongoDB/Mongoose; la profesora debe confirmar si se exige módulo NoSQL dentro del producto o basta una evidencia didáctica acotada. PostgreSQL no equivale a MongoDB y este backend no declara cubrir ese punto.


## Justificación de Node.js y PostgreSQL para el proyecto

Node.js, TypeScript y Express son la implementación actual del backend y coinciden con la unidad de desarrollo backend del sílabo de Tecnologías en Internet. Express expone las rutas REST; TypeScript permite revisar tipos durante la compilación. La evidencia de esta parte debe incluir arranque local, contrato de endpoints y pruebas reproducibles.

PostgreSQL se eligió para el dominio central porque el backend relaciona negocios, propietarios, productos y transacciones. Las claves, restricciones y transacciones SQL ayudan a preservar integridad entre estas entidades y a procesar operaciones de venta de forma consistente. El equipo ya tiene migraciones SQL y API construidas sobre PostgreSQL; agregar una segunda base sin una necesidad del producto requeriría mantener otro esquema, duplicar/sincronizar datos y ampliar las pruebas.

El sílabo TEC-644 sí incluye MongoDB/Mongoose. PostgreSQL no lo sustituye ni acredita por equivalencia ese resultado. Si la asignatura lo exige, confirmar con el docente si se acepta una práctica MongoDB/Mongoose separada; si se requiere cambiar la base del producto, registrar antes una decisión arquitectónica y un plan de migración. No afirmar que el proyecto usa MongoDB.

La asignación de responsables, fechas y tareas de las tres materias se detalla en [el plan de segunda entrega](../docs/14_Segunda_Entrega_Tareas_por_Materia.md).

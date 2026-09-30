# Backend API

## Estado actual

API inicial con Node.js, TypeScript, Express y conexión de PostgreSQL mediante `pg`. Incluye `GET /api/v1/health` y un esquema SQL inicial para negocios, productos y registro idempotente de operaciones. **Todavía no incluye login, autorización por negocio, CRUD de productos, ventas, sincronización de operaciones, pruebas automatizadas ni despliegue.** No exponer esta API a una red ni usarla con datos reales hasta añadir autenticación y autorización.

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

La migración `backend/db/migrations/001_initial.sql` se ejecuta automáticamente **solo al inicializar por primera vez el volumen** de PostgreSQL. Para una base ya creada, aplicar nuevas migraciones con el procedimiento que el equipo acuerde; no eliminar el volumen con datos para volver a ejecutar la inicialización.

En otra terminal:

```powershell
Copy-Item backend/.env.example backend/.env
Set-Location backend
npm install
npm run dev
```

Comprobar `http://127.0.0.1:3000/api/v1/health`. El estado esperado es `ok` cuando PostgreSQL responde. Si la conexión falla, la API no inicia y el mensaje no imprime secretos.

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

## Próximos incrementos

1. Definir ADR sucesora que reemplaza Spring por Node y conserva PostgreSQL.
2. Acordar identidad/negocio y autorización antes de exponer rutas comerciales.
3. Implementar catálogo con validación y consultas parametrizadas.
4. Añadir ventas, líneas y movimientos en transacción.
5. Añadir recepción de Outbox con UUID idempotente y resultado por operación.
6. Implementar autenticación, scopes, documentación OpenAPI y pruebas.

La materia Tecnologías en Internet incluye MongoDB/Mongoose; la profesora debe confirmar si se exige módulo NoSQL dentro del producto o basta una evidencia didáctica acotada. PostgreSQL no equivale a MongoDB y este backend no declara cubrir ese punto.

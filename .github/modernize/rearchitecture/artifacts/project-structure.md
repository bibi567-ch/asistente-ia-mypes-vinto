# Project Structure

## Classification

Full-stack monorepo: an Android mobile client and a TypeScript/Express backend API. The repository also contains documentation, diagrams, and Docker Compose configuration; those are not runtime source units.

## Modules and layers

- `mobile/`: Android application.
  - `app/src/main/java/com/mypes/vinto/presentation/`: Jetpack Compose screens and theme.
  - `app/src/main/java/com/mypes/vinto/viewmodel/`: lifecycle-aware UI state and coroutine orchestration.
  - `app/src/main/java/com/mypes/vinto/data/`: Room entities, DAOs, database, hashing, and repositories.
  - `app/src/main/java/com/mypes/vinto/nlu/`: Vosk speech recognition and intent/entity processing.
  - `app/src/main/java/com/mypes/vinto/plantillas/`: business-template seed data.
  - `MainActivity.kt`: Android entry point and navigation graph.
- `backend/`: Node.js API.
  - `src/server.ts`: startup entry point and database readiness check.
  - `src/app.ts`: Express middleware, route mounting, 404, and error handling.
  - `src/routes/`: health, authentication, and products HTTP surfaces.
  - `src/auth/`: password hashing, JWT creation/verification, and bearer middleware.
  - `src/db/`: PostgreSQL pool and migrations.
  - `src/config/`: environment loading and validation.
  - `src/scripts/`: operational CLI scripts.
- `docs/`, `diagrams/`, `evidence/`: project documentation and design evidence.

## Functional areas

Authentication and local PIN access, onboarding with business templates, dashboard metrics and voice assistant, local inventory, remote API authentication, remote product catalog, and health/readiness.

## Runtime entry points

- Android `MainActivity.onCreate`: `mobile/app/src/main/java/com/mypes/vinto/MainActivity.kt:17`.
- Node `start`: `backend/src/server.ts:5`.
- Express route registration: `backend/src/app.ts:7`.
- Database migration CLI: `backend/src/db/migrate.ts:1`.
- Owner bootstrap CLI: `backend/src/scripts/create-owner.ts:1`.

## Configuration

- `mobile/app/build.gradle.kts`: Android SDK, Kotlin/JVM 17, Compose, Room/KSP, SQLCipher, Vosk, and coroutine dependencies.
- `backend/package.json`: Node engine, scripts, runtime dependencies, and TypeScript tooling.
- `backend/src/config/env.ts`: environment variables and defaults.
- `backend/db/migrations/*.sql`: PostgreSQL schema and authentication tables.


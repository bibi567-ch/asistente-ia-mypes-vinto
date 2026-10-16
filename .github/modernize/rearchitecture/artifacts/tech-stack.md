# Technology Stack

## Mobile

- Kotlin Android application using Jetpack Compose and Material 3.
- Android namespace/application id: `com.mypes.vinto`.
- `compileSdk`/`targetSdk`: 35; `minSdk`: 26.
- Java and Kotlin JVM target: 17.
- Navigation Compose `2.8.3`, lifecycle ViewModel Compose `2.8.6`.
- Room `2.6.1` with KSP; SQLCipher Android `4.5.6`; SQLite KTX `2.4.0`.
- Vosk Android `0.3.47`, JNA `5.13.0`, coroutines Android `1.8.1`.
- The Vosk model directory is intentionally absent from the current checkout; `AsrEngine` still expects the asset at runtime.

## Backend

- Node.js ESM service constrained by `>=24 <25`.
- TypeScript `^5.9.0`, compiled by `tsc -p tsconfig.json`.
- Express `^5.1.0`, PostgreSQL client `pg ^8.16.3`, JOSE `^6.1.0`, dotenv `^16.4.7`.
- Development/runtime tooling: `tsx ^4.20.0`, Node type definitions `^24.0.0`.
- PostgreSQL migrations are plain SQL and use UUID identifiers, numeric prices, and timestamps.

## Build/runtime constraints

- Android requires SDK 35 and JDK 17-compatible Gradle tooling.
- Backend requires PostgreSQL connectivity before listening; startup executes `SELECT 1`.
- Backend configuration is environment-driven through `DATABASE_URL`, JWT settings, and host/port values read by `src/config/env.ts`.
- No Gradle wrapper is present in the repository; Android Studio supplies the build environment.


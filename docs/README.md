# Yapay — Asistente para MYPES de Vinto

Yapay es un asistente conversacional **offline-first** para pequeños comercios. La aplicación Android permite trabajar localmente con perfil, ventas, inventario, fiados, reportes y una cola Outbox; el backend Node.js sincroniza los datos cuando existe conectividad.

> **Estado real:** el proyecto tiene código funcional y pruebas automatizadas, pero la validación en dispositivo, las métricas de voz y la integración PostgreSQL local deben demostrarse con evidencia separada. No se presentan como aprobadas solo porque exista código o compile.

## Alcance implementado

- **Android:** Kotlin, Jetpack Compose, Room v7, SQLCipher, Android Keystore, WorkManager y Vosk para reconocimiento local.
- **Asistente:** chat, clasificación local de intenciones, extracción de entidades, confirmación antes de acciones y TTS. La consulta cloud y el ASR simulado están diferenciados de una IA cloud real.
- **Gestión comercial:** ventas, inventario, fiados, cobros y reportes CSV.
- **Backend:** Node.js 24, TypeScript, Express 5 y PostgreSQL 16.
- **Sincronización:** Outbox idempotente para operaciones comerciales y feedback de voz autorizado explícitamente.
- **Seguridad:** PIN local con hash, contraseña cloud solo para registro/login, JWT y base local cifrada.

## Estructura

```text
backend/             API Express, migraciones y pruebas Node
mobile/              Proyecto Android; abrir esta carpeta en Android Studio
diagrams/            Fuentes C4, casos de uso y flujos
docs/                Documentación operativa y resultados verificables
docs/evidencias/     Evidencias anonimizadas con procedimiento reproducible
../defensa_tecnica/  Guía personal de defensa, fallas, trade-offs y preguntas
docs/00_Indice.md    Índice canónico de documentación y rutas
legacy/              Prototipos históricos no usados por el build principal
```

La carpeta `defensa_tecnica/` está fuera de `docs/` porque es una guía personal de exposición, no documentación operativa del sistema.

## Ejecución del backend

Requisitos: Node.js 24, npm y PostgreSQL local o Docker Desktop operativo.

```powershell
cd backend
npm ci
Copy-Item .env.example .env
npm run db:migrate
npm run build
npm test
npm run test:integration
npm run dev
```

`npm run test:integration` necesita una PostgreSQL accesible con credenciales correctas. Si no puede conectarse, el resultado debe quedar bloqueado, no convertido en aprobación.

## Ejecución Android

Requisitos: Android Studio, JDK 17 y un emulador o dispositivo Android API 26+.

```powershell
cd mobile
Set-Location ..\mobile
.\download-vosk-model.ps1
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
```

El modelo Vosk se instala localmente y no debe incluirse como secreto ni presentarse como prueba de reconocimiento en hardware. Para un teléfono, configura `MYPES_API_BASE_URL` con una IP LAN confiable; `10.0.2.2` es solo para emulador.

## Documentación y evidencia

Empieza por [00_Indice.md](00_Indice.md). La arquitectura vigente está en [03_SAD_Arquitectura.md](03_SAD_Arquitectura.md), el plan y resultados de pruebas en [04_Plan_de_Pruebas.md](04_Plan_de_Pruebas.md) y [05_Resultados_de_Pruebas.md](05_Resultados_de_Pruebas.md).

La evidencia debe ser anonimizada e incluir fecha, entorno, pasos, resultado observado y límites. Consulta [COMO_DOCUMENTAR.md](COMO_DOCUMENTAR.md). No guardar tokens, contraseñas, datos reales de comerciantes ni audio identificable.

## Equipo

Eva Lizeth Chino Quispe · Christian Gonzales Mamani · Marializ Mamani Quispe · Kevin Jhonathan Rocha Rojas · Luis Gerardo Lazo Bustamante

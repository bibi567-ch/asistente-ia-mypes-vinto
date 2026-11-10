# Guía de configuración y ejecución local

> Guía de desarrollo/pruebas locales. El repositorio no define un despliegue de producción aprobado; no expongas la API ni uses datos reales.

## Requisitos

- Node.js 24.x y npm para el backend.
- Docker Desktop con Docker Compose, o PostgreSQL compatible.
- Android Studio, Android SDK y JDK 17 para compilar la aplicación móvil.

## Iniciar backend local

Desde la raíz del repositorio:

```powershell
docker compose up -d postgres
docker compose ps
Copy-Item backend/.env.example backend/.env
```

Edita `backend/.env` con valores locales: usa un secreto JWT aleatorio de al menos 32 bytes y el usuario/base PostgreSQL de desarrollo definidos en `docker-compose.yml`. No copies secretos a Git ni a capturas.

```powershell
Set-Location backend
npm ci
npm run db:migrate
npm run admin:create-owner -- "MYPE de prueba" "propietario@example.test"
npm run dev
```

Confirma el estado local en `http://127.0.0.1:3000/api/v1/health`. La contraseña temporal generada durante el alta debe tratarse como secreto local y cambiarse según la guía del backend.

## Compilar la app Android

1. Abre la carpeta `mobile/` en Android Studio.
2. Configura Gradle para usar JDK 17 y verifica la instalación del Android SDK requerido.
3. Configura `MYPES_API_BASE_URL` según `mobile/README.md` si se probará una API local desde emulador/teléfono.
4. Ejecuta `:app:assembleDebug` o compila la variante debug desde Android Studio.
5. Para validar comportamiento, instala en emulador/dispositivo y registra las pruebas manuales y evidencias por separado.

## CI del backend

El workflow [Backend](../.github/workflows/backend.yml) se ejecuta en cada push y en pull requests hacia `main`. Crea PostgreSQL temporal, aplica migraciones y ejecuta build, typecheck y pruebas unitarias/de integración. Cada ejecución publica un resumen automático en GitHub Actions. Consulta [07_CI_CD_Report.md](07_CI_CD_Report.md) para registrar el enlace del run.

El reporte del workflow no equivale a una prueba funcional de Android ni a un despliegue productivo.

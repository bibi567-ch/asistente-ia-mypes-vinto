# Análisis de fallas y soluciones

## 1. Hallazgos críticos

| Hallazgo | Evidencia | Impacto | Solución aplicada o necesaria | Estado |
|---|---|---|---|---|
| Dos configuraciones Room apuntaban al mismo `mypes-local.db`; una usaba SQLCipher y otra no. | `MypesDatabase` y el antiguo `VintoDatabase`. | `file is not a database`, pérdida de acceso al perfil y riesgo de abrir una base con el motor incorrecto. | Unificar el login en `MypesDatabase`, eliminar la instancia duplicada y conservar migraciones explícitas. | Corregido; compilación y pruebas Android aprobadas. |
| Se propuso `fallbackToDestructiveMigration()`. | Instrucción de implementación recibida. | Puede borrar ventas, perfil, Outbox y evidencias locales. | No usarlo. Para una base antigua incompatible, hacer respaldo y migración controlada; si es una instalación de desarrollo sin datos, reinstalar conscientemente. | Decisión aplicada. |
| La navegación usa dos accesos distintos: PIN local y login cloud. | `MainActivity`, `LoginScreen` y `ApiLoginScreen`. | Cambiar todo a email/contraseña rompería el modo offline y confundiría autenticación local con JWT. | Mantener `login` para PIN local; reservar `api_login` para vinculación cloud. | Corregido/documentado. |
| El README raíz estaba incompleto y contenía bloques de ayuda de npm dentro de la guía. | `README.md` anterior. | Instalación confusa y afirmaciones desactualizadas sobre Room y capacidades cloud. | Consolidar el README documental en `docs/README.md`; la guía Android ya no está duplicada. | Corregido. |
| Existían dos índices con contenidos y rutas distintas. | `00_Indice.md` raíz y `docs/00_Indice.md`. | No había una fuente canónica; varias rutas apuntaban a documentos inexistentes. | Mantener `docs/00_Indice.md` como índice único y corregir sus rutas. | Corregido. |
| El proyecto contiene dos líneas documentales de pruebas. | `docs/04-07` y `docs/13-16`. | Se pueden presentar resultados históricos como si fueran del estado actual. | El índice distingue plan/resultados consolidados y plan/resultados operativos; la defensa exige fecha y comando. | Parcial: revisar antes de entregar. |
| La integración PostgreSQL local no está cerrada. | Ejecución previa con error `28P01`. | No se puede afirmar sincronización end-to-end aprobada. | Corregir credenciales fuera del repositorio o levantar PostgreSQL aislado; repetir migraciones e integración. | Bloqueado. |
| Las pruebas funcionales Android no sustituyen a una demostración física. | No existe evidencia física completa para todos los flujos. | No se puede afirmar WER, SUS, permisos, TTS o sincronización en teléfono. | Ejecutar protocolo en dispositivo y guardar evidencia anonimizada. | Pendiente. |
| ASR cloud está configurado como demostrativo por defecto. | `ASR_PROVIDER=simulated` y pruebas de ASR simulado. | No se debe afirmar soporte cloud real para quechua/aimara. | Presentar Vosk local como capacidad implementada y cloud como seam preparada/simulada. | Correcto, pendiente de proveedor real. |
| No se encontró `COMO_DOCUMENTAR.md`, aunque una guía lo enlazaba. | Búsqueda del repositorio. | Un colaborador puede no saber cómo registrar evidencia. | Crear `docs/COMO_DOCUMENTAR.md` y centralizar allí el procedimiento. | Corregido. |

## 2. Riesgos técnicos que deben explicarse

### Persistencia y migraciones

Room v7 usa migraciones explícitas, incluyendo 6→7 para `VoiceLog`. SQLCipher depende de una clave protegida por Android Keystore. Una base antigua sin cifrado no se convierte usando una opción genérica de Room: requiere exportación/transformación controlada o reinstalación de desarrollo. La solución robusta es preservar datos y fallar explícitamente, no borrar silenciosamente.

### Autenticación

El PIN local permite operar offline y se almacena como hash. El JWT del backend permite sincronización y consultas cloud. Son credenciales con propósitos distintos. La defensa debe mostrar esta separación como una decisión de seguridad, no como una duplicación accidental.

### Sincronización

El Outbox conserva operaciones cuando no hay red y usa identificadores de operación para idempotencia. La confirmación de una prueba debe incluir el servidor y la base PostgreSQL; una cola visible en la UI solo demuestra el lado cliente.

### IA y reconocimiento

Vosk local permite demostrar operación offline. El clasificador por reglas es interpretable y testeable, pero no equivale a un modelo multilingüe entrenado. El endpoint cloud simulado sirve para probar contratos y errores, no calidad de transcripción.

## 3. Estado de validación conocido

- Android: `:app:testDebugUnitTest :app:assembleDebug` pasó después de unificar Room.
- Backend: `npm run build`, `npm run typecheck:test` y `npm test` pasaron en la validación previa; la integración PostgreSQL quedó bloqueada por credenciales locales.
- `git diff --check`: pasó en los cambios recientes.
- No declarar aprobadas pruebas físicas Android, WER, SUS, latencia, batería ni integración cloud real sin evidencia.

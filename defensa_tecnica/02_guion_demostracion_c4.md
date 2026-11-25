# Guion de demostración y modelado C4

Para ubicar cada API, enlace, modelo y comando antes de la exposición, usar el [mapa técnico](06_mapa_tecnico_apis_conexiones_ia.md) y el [contrato detallado](../.github/modernize/assessment/engines/facts/api-service-contracts.md).

## 1. Orden recomendado de la demostración

### Paso A — Contexto C4

Mostrar [diagrams/C1_Contexto.mmd](../diagrams/C1_Contexto.mmd):

1. El comerciante es el actor.
2. El teléfono ejecuta la operación local y conserva datos cifrados.
3. El backend recibe sincronización y autentica acceso cloud.
4. La red no es requisito para registrar una operación local.

Frase de defensa: “La separación Edge-Cloud reduce la dependencia de conectividad, pero la consistencia remota solo se demuestra cuando el Outbox llega a PostgreSQL”.

### Paso B — Contenedores C4

Mostrar [diagrams/C2_Contenedores.mmd](../diagrams/C2_Contenedores.mmd) y contrastarlo con [03_SAD_Arquitectura.md](../docs/03_SAD_Arquitectura.md):

- UI Compose y ViewModels.
- Motor Vosk y clasificador local.
- Room v7 + SQLCipher.
- Outbox/WorkManager.
- API Express.
- PostgreSQL.

Distinguir con una leyenda: **implementado**, **simulado** y **pendiente de integración**. No presentar componentes descritos en documentos históricos como si fueran dependencias actuales.

### Paso C — Flujo offline

1. Iniciar sesión localmente con PIN.
2. Registrar una operación usando datos ficticios.
3. Desactivar la red.
4. Confirmar la operación en la UI.
5. Mostrar la entrada pendiente en Sincronización.
6. Volver a conectar.
7. Ejecutar el Worker y verificar el resultado del backend si PostgreSQL está disponible.

Si PostgreSQL no está disponible, detener la demostración en el punto 5 y decirlo explícitamente.

### Paso D — Seguridad y confirmación

Demostrar un comando que implique una acción sensible. Mostrar que aparece confirmación antes de ejecutar. Cancelar y comprobar que no cambia el estado. Repetir con permiso denegado si el dispositivo lo permite.

### Paso E — Backend

Ejecutar desde `backend/`:

```powershell
npm run build
npm run typecheck:test
npm test
npm run db:migrate
npm run test:integration
```

Registrar el resultado de cada comando. Si la integración falla por infraestructura, presentar el error y su bloqueo, no un resultado verde ficticio.

## 2. Modelo C4 que debe defenderse

### Nivel 1 — Contexto

**Comerciante → Yapay → PostgreSQL/API**, con el teléfono como frontera de operación offline y el backend como frontera de sincronización/servicios cloud.

### Nivel 2 — Contenedores

| Contenedor | Responsabilidad | Estado |
|---|---|---|
| Android UI | Interacción, chat, confirmaciones y navegación | Implementado; validación física pendiente |
| Motor local | Vosk, extracción y clasificación | Implementado con reglas; calidad lingüística pendiente |
| Room/SQLCipher | Perfil, ventas, inventario, fiados, Outbox y VoiceLog | Implementado con migración v7 |
| WorkManager/Outbox | Reintentos y sincronización diferida | Implementado; integración real pendiente |
| API Express | Auth, sync, ASR simulado, assistant y learning | Implementado; integración PostgreSQL pendiente |
| PostgreSQL | Persistencia central e idempotencia | Diseñado y migrado; ejecución local bloqueada |

## 3. Patrones y justificación

- **Offline-first:** el camino crítico no depende de la red.
- **Outbox:** desacopla escritura local de entrega remota y permite reintentos.
- **Idempotencia:** evita duplicados cuando se reintenta un lote.
- **Repository/DAO:** separa UI de persistencia.
- **MVVM/StateFlow:** hace observable el estado de login, chat y sincronización.
- **Confirmación explícita:** reduce el costo de una transcripción incorrecta.
- **Separation of concerns:** PIN local y JWT cloud no comparten propósito ni almacenamiento.

## 4. Trade-offs

| Decisión | Beneficio | Costo aceptado |
|---|---|---|
| Vosk local | Privacidad y operación sin red | APK grande y precisión pendiente de medir |
| Reglas locales | Explicabilidad y bajo consumo | Cobertura lingüística menor que un modelo entrenado |
| Room cifrado | Protección local | Migraciones y recuperación de clave más delicadas |
| Outbox | Resiliencia offline | La confirmación remota es eventual |
| ASR cloud simulado | Contrato testeable sin secretos/costos | No demuestra calidad cloud real |

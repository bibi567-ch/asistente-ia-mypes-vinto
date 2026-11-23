# Registro de pruebas y evidencias

Bitácora cronológica de las comprobaciones registradas para el proyecto. Cada entrada identifica su alcance y distingue compilaciones, pruebas automatizadas y validaciones funcionales. Un resultado solo aplica a la revisión y al entorno indicados.

## Historial previo: funcionalidades y validaciones técnicas

- **Fecha:** anterior al 2026-11-10; fecha exacta no conservada.
- **Trabajo implementado:** ventas locales con persistencia transaccional de venta, stock y Outbox; rechazo de stock negativo; autenticación API separada del PIN local; sincronización de catálogo paginada; voz que propone datos pero requiere confirmación; deduplicación de productos de plantilla; y pantalla de reportes diarios.
- **Validaciones técnicas reportadas en la sesión previa:** build backend, chequeo de tipos, 8 pruebas unitarias, 6 pruebas de integración PostgreSQL y compilación Android debug finalizaron correctamente.
- **Límite de esta constancia:** no se conservaron el enlace de CI, la fecha exacta ni los logs primarios. Es un antecedente de trabajo, no evidencia de GitHub Actions ni prueba funcional manual. Las pruebas funcionales de Android permanecen pendientes de capturas.

## Fase 0: Carga de la pantalla inicial de acceso

- **Fecha:** 2026-11-10
- **Escenario:** abrir la aplicación en un dispositivo Android conectado.
- **Resultado observado:** se mostró la pantalla de acceso con los campos de identificación y PIN, además del control de micrófono.
- **Evidencia:** ![Login local en dispositivo Android](evidencias/01_login/mypes_login.png)
- **Alcance:** confirma la carga visual inicial; no verifica el PIN, el dictado ni el acceso con credenciales. Esos escenarios permanecen pendientes.

## Estado de evidencia para acceso y registro

- Las capturas que demostrarían un acceso exitoso y un registro confirmado en PostgreSQL aún no están disponibles.
- La implementación y la compilación no sustituyen esa evidencia funcional. Los casos se mantienen pendientes en el [plan de pruebas](04_Plan_de_Pruebas.md).

## Fase 1: Cambios Android recientes

- **Fecha:** 2026-11-10
- **Qué se hizo:** se integraron el reporte diario, navegación desde el asistente de voz al formulario con sugerencias y la deduplicación de productos de plantilla.
- **Comprobación técnica:** `:app:assembleDebug` finalizó correctamente durante la validación del cambio. Esto solo confirma compilación; no confirma el comportamiento en un dispositivo.
- **Resultado funcional:** PENDIENTE de prueba manual y captura/video.
- **Evidencia:** pendiente; guardar en `evidencias/03_dashboard/`, `evidencias/04_ventas/` o `evidencias/06_chat_ia/` según el caso.

## Fase 2: Argumentos tipados para venta sugerida por voz

- **Fecha:** 2026-11-10
- **Qué se cambió:** el destino de venta recibe `producto` codificado y `cantidad` como texto numérico; la cantidad se valida y convierte a `Double` al entrar porque Navigation 2.8.3 no expone `NavType.DoubleType`. El formulario solo selecciona coincidencias normalizadas exactas.
- **Comprobación técnica:** `:app:assembleDebug` finalizó correctamente; la compilación no valida el flujo en un dispositivo.
- **Resultado funcional:** PENDIENTE de probar el dictado, confirmar el producto/cantidad y guardar evidencia en `evidencias/06_chat_ia/` y `evidencias/04_ventas/`.

## Fase 3: Backend - build, tipos y pruebas unitarias locales

- **Fecha:** 2026-11-10
- **Qué se probó:** desde `backend/`, se ejecutaron `npm run build`, `npm run typecheck:test` y `npm test`.
- **Resultado obtenido:** build correcto, chequeo de tipos correcto y 8 pruebas unitarias aprobadas (8 pasaron, 0 fallaron).
- **Alcance:** ejecución local en el commit `c187bba`; no es una ejecución de GitHub Actions.
- **Evidencia:** salida de consola observada en esta sesión; enlazar el workflow cuando se ejecute en GitHub. No hay captura añadida aún.

## Fase 4: Backend - pruebas de integración PostgreSQL

- **Fecha:** 2026-11-10
- **Qué se probó:** intento de preparar/migrar la base de pruebas local para ejecutar `npm run test:integration`.
- **Resultado obtenido:** BLOQUEADO; PostgreSQL rechazó la autenticación para el usuario `mypes_test`. No se pudo confirmar el resultado de integración local.
- **Estado al momento del intento:** pendiente; la instancia local no permitió confirmar las pruebas.
- **Seguimiento:** la suite de migraciones e integración sí pasó posteriormente en el [run de GitHub Actions 43157514411](https://github.com/bibi567-ch/asistente-ia-mypes-vinto/actions/runs/43157514411), con PostgreSQL 16 temporal. Ver Fase 16; ese resultado no convierte el intento local en exitoso.
- **Evidencia:** conservar el bloqueo local como antecedente y usar el enlace al run para el resultado de CI.

## Fase 5: Pruebas funcionales en dispositivo

- **Fecha:** pendiente
- **Escenario ejecutado:** no registrado en esta bitácora.
- **Resultado:** PENDIENTE. No se dispone de capturas o videos que acrediten los flujos funcionales completos.
- **Evidencia requerida:** registrar cada escenario después de ejecutarlo y guardar su captura/video anonimizado.

## Fase 6: Pruebas unitarias JVM de NLU y PIN

- **Fecha:** 2026-11-10
- **Qué se probó:** `:app:testDebugUnitTest` con extracción de entidades (incluye "cuartillas", montos en bolivianos y "al fiado"), clasificación de intención de venta y hashing PBKDF2 del PIN.
- **Resultado obtenido:** 5 pruebas reportadas por JUnit XML; 5 pasaron, 0 fallaron, 0 errores y 0 omitidas. Suites: 2 de extracción, 1 de clasificación y 2 de hash/PIN. Los resultados están en `mobile/app/build/test-results/testDebugUnitTest/`.
- **Alcance:** pruebas JVM locales; validan componentes aislados, no reconocimiento acústico en un dispositivo ni la UI de sincronización. `:app:assembleDebug` también terminó correctamente.
- **Evidencia:** reportes XML locales; las capturas indicadas por el equipo se agregarán tras obtenerlas.

## Fase 7: Pantalla de sincronización conectada al Outbox

- **Qué se verificó:** la pantalla observa las operaciones `PENDIENTE` y errores desde Room y presenta el conteo, los IDs abreviados y los intentos; el botón conserva la lógica existente de vinculación de cuenta y WorkManager.
- **Estado técnico:** `:app:assembleDebug` finalizó correctamente y generó el APK debug.
- **Resultado funcional:** PENDIENTE de verificar en el dispositivo y guardar `evidencias/07_sincronizacion/pantalla_sync_real.png`.
- **Límite:** no se creó una captura ni se declaró aprobada la prueba de UI.

## Fase 8: Corrección del wrapper de Gradle en Windows

- **Qué se corrigió:** `mobile/gradlew.bat` devolvía código 1 incluso si Gradle terminaba correctamente, lo que hacía que automatizaciones interpretaran falsamente como fallida una compilación exitosa.
- **Resultado obtenido:** se verificó `gradlew.bat --version` con código 0 y se ejecutaron las pruebas JVM y el APK con `BUILD SUCCESSFUL`, código 0.
- **Alcance:** el wrapper preserva éxito y fallo de Gradle para que CI local y scripts de Windows puedan interpretar el resultado.

## Fase 9: Gráfico semanal y vista de demostración del reporte

- **Fecha:** 2026-11-10
- **Qué se implementó:** el reporte consulta los totales de cada uno de los últimos siete días mediante intervalos locales `[inicio, fin)`. Se añadió una vista demostrativa solo para builds debug; sus cifras ficticias afectan únicamente al gráfico y no se persisten. El selector de compartir sigue usando el total real del día.
- **Comprobación técnica:** `:app:testDebugUnitTest :app:assembleDebug` finalizó con código 0 usando JBR 21 y el SDK Android instalado. JUnit reportó 5 pruebas, 0 fallos, 0 errores y 0 omitidas; el APK debug se compiló correctamente.
- **Resultado funcional:** PENDIENTE de validar el gráfico, el cambio de día, el selector de compartir y la separación entre datos demo y reales en un dispositivo.
- **Plan asociado:** `DASH-03`, `DASH-04` y `DASH-05` permanecen PENDIENTES hasta contar con esa validación.
- **Evidencia:** reportes JUnit locales en `mobile/app/build/test-results/testDebugUnitTest/`; captura funcional pendiente en `evidencias/03_dashboard/`.

## Fase 10: Registro local y alta de cuenta en backend

- **Fecha:** 2026-11-10
- **Qué se implementó:** formulario de registro Android con datos personales, negocio, PIN y contraseña de nube; migración Room 2→3 con columnas de perfil opcionales para conservar usuarios existentes; endpoint `POST /api/v1/auth/register` con validación, hash scrypt, transacción PostgreSQL para negocio/propietario y token Bearer. La contraseña de nube no se persiste en el dispositivo. Si la red falla, se puede continuar explícitamente con el perfil local y vincular la cuenta después.
- **Comprobaciones backend:** `npm run build`, `npm run typecheck:test` y `npm test` pasaron (8 pruebas unitarias). En una base PostgreSQL temporal y aislada se aplicaron las migraciones 001–004 y `npm run test:integration` pasó (8 pruebas, 0 fallos), incluyendo registro exitoso, acceso con el token, correo duplicado sin negocio huérfano y rechazo de datos incompletos.
- **Comprobaciones Android:** `:app:testDebugUnitTest :app:assembleDebug` pasó (5 pruebas, 0 fallos) con Java 21 y el SDK local.
- **Resultado funcional Android:** PENDIENTE de completar el registro en un dispositivo y probar conexión, modo offline y migración de una base existente.
- **Evidencia:** resultados automatizados locales; capturas pendientes en `evidencias/01_login/` o `evidencias/08_backend/`. No se usaron datos reales.

## Fase 11: Instalación local del modelo de voz Vosk

- **Fecha:** 2026-11-10
- **Qué se hizo:** se descargó el modelo pequeño de español Vosk 0.42 desde su origen oficial y se extrajo en `mobile/app/src/main/assets/vosk-model-small-es-0.42/`. Se añadió `mobile/download-vosk-model.ps1` para instalarlo en otras máquinas y se comprobó su comportamiento idempotente.
- **Comprobación técnica:** el APK debug se compiló con el modelo y contiene `assets/vosk-model-small-es-0.42/am/final.mdl`. Su tamaño resultó de 117,392,971 bytes; el modelo permanece excluido de Git, por lo que cada equipo debe ejecutar el script antes de compilar.
- **Resultado funcional:** PENDIENTE de inicializar Vosk y dictar una frase en un dispositivo; la presencia del modelo en el APK no prueba el reconocimiento acústico.
- **Evidencia:** compilación local; captura de reconocimiento pendiente en `evidencias/06_chat_ia/`.

## Fase 12: Pantalla de perfil del comerciante

- **Fecha:** 2026-11-10
- **Qué se implementó:** ruta `perfil` accesible desde el icono del encabezado del dashboard y pantalla de solo lectura que consulta el registro local Room fuera del hilo principal. Muestra los datos personales y del negocio, con estado de carga/error y un texto explícito para campos opcionales no registrados; no muestra PIN, hash ni sal.
- **Comprobación técnica:** `:app:testDebugUnitTest :app:assembleDebug` finalizó con código 0 tras incorporar la pantalla.
- **Resultado funcional:** PENDIENTE de registrar un perfil y verificar su visualización en dispositivo; no se declara APROBADO sin evidencia.
- **Evidencia:** pendiente en `evidencias/01_login/perfil_usuario.png`.
- **Prueba de backend relacionada:** `backend/test/integration/auth-registration.test.ts` ya valida el alta del negocio/propietario, lectura persistida, JWT utilizable, rechazo de campos incompletos y duplicados. No se creó el archivo de prueba duplicado propuesto.

## Fase 13: Fiados, búsqueda y exportación CSV

- **Fecha:** 2026-11-10
- **Qué se implementó:** Room v4 añade `ventas.pagado` con migración 3→4 y valor inicial falso para conservar las deudas antiguas. La pantalla Fiados lista ventas fiadas pendientes y pide confirmación de cobro completo. El estado pagado, el ingreso de caja y el evento `COBRO` del Outbox se guardan atómicamente; el mismo `operationId` se sincroniza idempotentemente. Inventario filtra por nombre en tiempo real. El reporte exporta las ventas de hoy a CSV, escapa campos y comparte el archivo desde un `FileProvider` restringido al subdirectorio de reportes de caché.
- **Comprobaciones Android:** `:app:testDebugUnitTest :app:assembleDebug` finalizó correctamente; 7 pruebas JVM, 0 fallos, incluidos casos de formato/escape CSV. Room generó el esquema versión 4.
- **Comprobaciones backend:** build y chequeo de tipos correctos; migraciones y `npm run test:integration` contra PostgreSQL temporal pasaron con 9 pruebas, 0 fallos, incluida la aceptación e idempotencia de `COBRO`.
- **Alcance pendiente:** no se ejecutó en dispositivo la migración de una base real versión 3, el cobro transaccional Room, búsqueda en pantalla ni el envío del URI CSV a otra app. Esas pruebas funcionales siguen PENDIENTES; no se declara probado el ciclo completo en interfaz.
- **Evidencia:** reportes automatizados locales; capturas pendientes en `evidencias/09_fiados/`, `evidencias/05_inventario/` y `evidencias/03_dashboard/`.

## Fase 14: Perfil personal ampliado, login por identificador y PIN por voz

- **Fecha:** 2026-11-10
- **Qué se implementó:** Room v6 conserva los campos anteriores y añade nombres, apellidos, expedición y datos de nacimiento; el login local busca por carnet, celular o correo y ya no crea una cuenta al introducir un PIN en una base vacía. Se añadió extracción unitaria de cuatro dígitos hablados. Registro envía los datos del perfil al backend; la contraseña se almacena como hash scrypt en PostgreSQL y no se conserva en Room. El onboarding guarda nombre/rubro localmente y los actualiza en el negocio asociado al token. Si el registro fue offline, Sincronización permite volver a ingresar correo/contraseña para crear la cuenta con el perfil local.
- **Android:** `:app:testDebugUnitTest :app:assembleDebug` finalizó correctamente con SDK explícito. JUnit reportó 9 pruebas, 0 fallos, 0 errores y 0 omitidas. La prueba incluye extracción de PIN hablado y casos ambiguos. La compilación no valida el flujo ni la migración Room en un dispositivo.
- **Backend en la revisión de esa fase:** `npm run build`, `npm run typecheck:test` y `npm test` finalizaron correctamente; 8 pruebas unitarias pasaron. `npm run test:integration` tuvo 10 fallos y 0 aprobadas porque PostgreSQL rechazó la autenticación para `mypes_local`; Docker Desktop tampoco estaba disponible para iniciar la base temporal. Ese intento local quedó bloqueado.
- **Seguimiento posterior:** el workflow de `main` ejecutó las migraciones y la suite de integración en PostgreSQL temporal con éxito (ver [Fase 16](#fase-16-ejecución-verificada-del-workflow-backend-en-main)). Esto valida CI para la revisión indicada allí, no la prueba local ni los flujos funcionales Android.
- **Estado funcional Android:** PENDIENTE de ejecutar login por identificador/voz, registro, onboarding y migración 4→6 en dispositivo. No se generó captura/video ni se marca ninguna prueba funcional como aprobada.
- **Evidencia:** reportes JUnit locales en `mobile/app/build/test-results/testDebugUnitTest/`; captura funcional pendiente en `evidencias/01_login/`, `evidencias/02_onboarding/` y `evidencias/06_chat_ia/`.

## Plantilla para nuevas ejecuciones

## Fase 15: Plantillas ampliadas, Yapay local y endpoints cloud

- **Fecha:** 2026-11-11
- **Qué se implementó:** seis plantillas locales con categorías, márgenes orientativos y al menos diez productos; Yapay consulta el total local del día y exige confirmación antes de insertar; la API añade facturas con PDF simulado, QR Base64 por negocio y SSE autenticado con eventos aislados por negocio. Se añadió migración PostgreSQL 006.
- **Android:** `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` finalizó correctamente; 12 pruebas JVM, 0 fallos, 0 errores y 0 omitidas. Incluye la cobertura nueva de las plantillas y confirmación de producto. Gradle emitió una advertencia de compatibilidad entre Android Gradle Plugin 8.5.2 y `compileSdk` 35; no impidió el build.
- **Backend:** desde `backend/`, `npm run build`, `npm run typecheck:test` y `npm test` finalizaron correctamente; 11 pruebas, 0 fallos. Incluye autenticación/rechazo de payload inválido para los endpoints nuevos y aislamiento unitario del bus de eventos.
- **Integridad de iteración:** durante el desarrollo, una compilación Kotlin inicial detectó un error de tipo y una ejecución intermedia detectó un fallo en el fixture de prueba; se corrigieron y la ejecución final descrita arriba pasó.
- **Comprobación de diff:** `git diff --check` finalizó sin errores.
- **Estado al momento de la fase:** la integración local y la migración 006 aún no se habían confirmado. Posteriormente el workflow de `main` aplicó las migraciones y ejecutó la suite de integración con éxito (ver Fase 16); ese run no debe tomarse como prueba específica de los flujos factura/QR si no están cubiertos por sus casos.
- **Pendiente:** no se validaron factura/QR persistidos desde cliente, transporte SSE real, interfaz en un dispositivo, ni un LLM o respuestas reales en quechua/aymara.
- **Datos de plantillas:** no se encontró el documento fuente citado para Farmacia Eliana/Pastelería Pampas; los precios y porcentajes cargados son ejemplos editables, no datos confirmados como exactos.
- **Estado funcional Android:** PENDIENTE de validar en dispositivo; no se declara aprobada ninguna prueba funcional.

## Fase 16: Ejecución verificada del workflow backend en `main`

- **Fecha:** 2026-11-12 (UTC).
- **Revisión:** `03afcbd58e5d91a7374b52768e50061aaeb9fab9`.
- **Ejecución:** [GitHub Actions run 43157514411](https://github.com/bibi567-ch/asistente-ia-mypes-vinto/actions/runs/43157514411).
- **Qué se ejecutó:** `npm ci`, `npm run db:migrate`, `npm run build`, `npm run typecheck:test`, `npm test` y `npm run test:integration` con PostgreSQL 16 temporal.
- **Resultado observado:** todos los pasos y el job `verify` finalizaron con `success`.
- **Alcance:** evidencia CI del backend para esa revisión. No valida UI Android, reconocimiento acústico, migración Room en el teléfono, conexión física móvil–API, HTTPS productivo, ni los endpoints de factura/QR mediante persistencia end-to-end.
- **Relación con registros anteriores:** la fase 14 documenta un bloqueo de integración local; este run posterior valida migraciones e integración en el entorno aislado de CI, sin borrar el registro histórico del intento local.

### Fase [n]: [módulo y escenario]

- **Fecha:**
- **Commit / versión:**
- **Qué se probó:**
- **Entorno y datos de prueba:**
- **Resultado esperado:**
- **Resultado obtenido:**
- **Estado:** PENDIENTE / APROBADO / FALLIDO / BLOQUEADO
- **Evidencia:** `evidencias/[carpeta]/[archivo]` o enlace al workflow de GitHub Actions.

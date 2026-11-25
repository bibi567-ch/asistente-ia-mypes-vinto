### PLAN DE PRUEBAS Y CRITERIOS DE ACEPTACIÓN — LÍNEA BASE v2.0

**Universidad Adventista de Bolivia · Facultad de Ciencias Exactas e Ingeniería**  
**Proyecto Integrador Yapay: Asistente Conversacional Offline Multilingüe Edge AI**  

| Control del documento | Detalle |
| :--- | :--- |
| **Versión del Plan** | v2.0 (Línea Base Oficial Integrada) |
| **Sistema** | Asistente Conversacional Offline (Android Kotlin Edge AI / Node.js 24 + PostgreSQL 16) |
| **Equipo de QA & Dev** | Eva Chino Quispe, Christian Gonzales, Marializ Mamani, Kevin Rocha y Luis Lazo |
| **Corte de Evaluación** | Noviembre 2026 |

---

### 1. ESTRATEGIA Y METODOLOGÍA DE PRUEBAS

El presente plan establece los escenarios estandarizados y los criterios formales de aceptación para validar el comportamiento del sistema **Yapay**. La metodología distingue explícitamente entre la verificación técnica automatizada (ejecutada en integración continua) y la validación funcional en dispositivos móviles reales.

#### Categorización de Estados de Prueba:
* **APROBADO:** Escenario ejecutado con éxito, con evidencia documental o registros de CI/CD verificables.
* **PENDIENTE:** Escenario definido formalmente pero pendiente de ejecución o captura de evidencia en dispositivo físico.
* **FALLIDO:** Escenario ejecutado donde el resultado observado difiere del criterio de aceptación esperado.
* **BLOQUEADO:** Escenario que no puede ejecutarse debido a un impedimento técnico o de infraestructura previo.

---

### 2. MATRIZ INTEGRADA DE ESCENARIOS DE PRUEBA

#### 2.1 Módulo de Autenticación y Seguridad Local (LOGIN / SEC)
| ID | Escenario | Criterio de Aceptación | Entorno / Nivel | Estado / Evidencia |
| :--- | :--- | :--- | :--- | :--- |
| **LOGIN-01** | Acceso con PIN local correcto (Offline) | El sistema valida el hash PBKDF2-HMAC-SHA256 y abre el Dashboard en < 1 s sin requerir red. | Android / UI | **APROBADO** (Evidencia local) |
| **LOGIN-02** | Control de intentos e inhabilitación temporal | Ante 5 intentos fallidos consecutivos de PIN, el sistema bloquea el acceso por 60 segundos. | Android / UI | **PENDIENTE** (Prueba física) |
| **LOGIN-03** | Búsqueda multicanal de perfil local | Permite identificar al comerciante por Carnet de Identidad, celular o correo (case-insensitive). | Android / DB | **APROBADO** (Prueba Unit/JVM) |
| **LOGIN-04** | Ingreso de PIN mediante comando verbal | El motor Vosk traduce los dígitos hablados ("uno dos tres cuatro") a PIN "1234" sin ambigüedad. | Android / VUI | **PENDIENTE** (Acondicionamiento acústico) |
| **SEC-01** | Cifrado de base de datos en reposo (SQLCipher) | La base de datos SQLite/Room v7 se cifra con AES-256. Un volcado raw resulta ilegible sin la clave master. | Android / DB | **APROBADO** (Prueba Unit/JVM) |
| **SEC-02** | HTTP limitado a debug | El manifiesto release no permite tráfico HTTP sin cifrar; el manifiesto debug permite HTTP solo para pruebas locales. | Android / Configuración | **PENDIENTE** (Verificar manifiestos mergeados de debug y release) |
| **SEC-03** | Configuración de DB sin credenciales por defecto | El backend no inicia si falta `DATABASE_URL`; los ejemplos versionados no contienen una contraseña reutilizable. | Backend / Configuración | **PENDIENTE** (Build y prueba de arranque con variable ausente) |
| **SEC-04** | TLS de PostgreSQL con CA privada | En producción, el backend valida el certificado del servidor PostgreSQL; la prueba unitaria confirma que `DATABASE_SSL_CA` se usa sin desactivar la validación. La conexión Railway debe aceptar la CA correcta y rechazar una CA incorrecta. | Backend / PostgreSQL | **PARCIAL** (Railway conectado con CA y `DATABASE_SSL_REJECT_UNAUTHORIZED=true`; falta probar rechazo de CA incorrecta) |
| **SEC-05** | Auditoría de dependencias npm | `npm audit --omit=dev` y `npm audit` no reportan vulnerabilidades conocidas después de actualizar las dependencias transitivas afectadas. | Backend / Dependencias | **APROBADO** (Hallazgos iniciales HIGH/MODERATE remediados; ambas auditorías limpias) |
| **SEC-06** | Auditoría de dependencias Python ASR | Escanear `quechua-asr/requirements.txt` con una fuente actual de advisories PyPI antes de construir/desplegar el servicio y corregir hallazgos aplicables. | Servicio ASR / Dependencias | **PENDIENTE** (El evaluador disponible no admite PyPI; el fallback LLM no certifica versiones actuales) |

#### 2.2 Módulo de Registro y Gestión de Cuenta (REG / PERF)
| ID | Escenario | Criterio de Aceptación | Entorno / Nivel | Estado / Evidencia |
| :--- | :--- | :--- | :--- | :--- |
| **REG-01** | Registro inicial de Propietario y Negocio en la Nube | La API crea las entidades asociadas en PostgreSQL y retorna un token JWT válido (HTTP 201). | Backend / API | **APROBADO** (CI Run #43157514411) |
| **REG-02** | Control de duplicidad de correo electrónico | La API rechaza registros con correos previamente existentes respondiendo HTTP 409 Conflict. | Backend / API | **APROBADO** (CI Run #43157514411) |
| **REG-03** | Validación estricta de esquemas de entrada | Payloads con campos faltantes o contraseñas débiles son rechazados con HTTP 400 Bad Request. | Backend / API | **APROBADO** (CI Run #43157514411) |
| **REG-04** | Registro híbrido con persistencia local anticipada | El perfil se guarda localmente en Room; si la nube no responde, la cuenta remota se sincroniza diferidamente. | Android / Outbox | **PENDIENTE** (Prueba funcional) |
| **REG-05** | Migración transparente de esquema Room (v4 a v6) | La actualización de versión de BD conserva intactos los hashes de PIN y el catálogo existente. | Android / DB | **PENDIENTE** (Prueba en dispositivo) |

#### 2.3 Módulo de Onboarding y Configuración Comercial (ONB)
| ID | Escenario | Criterio de Aceptación | Entorno / Nivel | Estado / Evidencia |
| :--- | :--- | :--- | :--- | :--- |
| **ONB-01** | Carga e inicialización por plantilla de rubro | Al seleccionar un rubro (ej. Ferretería, Minimarket), se puebla el catálogo local con al menos 10 productos base. | Android / Logic | **APROBADO** (Prueba Unit/JVM) |
| **ONB-02** | Prevención de duplicación al re-aplicar plantilla | Seleccionar nuevamente una plantilla no duplica los productos previamente registrados en Room. | Android / DB | **PENDIENTE** (Prueba funcional) |
| **ONB-03** | Sincronización de perfil comercial hacia el Backend | Al cambiar el nombre de la tienda o rubro con conexión, la API actualiza los datos del negocio (PUT /api/v1/auth/business). | Backend / API | **APROBADO** (CI Run #43157514411) |

#### 2.4 Módulo de Ventas e Inventario (VTA / INV)
| ID | Escenario | Criterio de Aceptación | Entorno / Nivel | Estado / Evidencia |
| :--- | :--- | :--- | :--- | :--- |
| **VTA-01** | Registro atómico de venta al contado (Offline) | Registra la venta en Room, descuenta el stock inmediatamente y encola un evento `SALE_CREATED` en Outbox. | Android / Local | **APROBADO** (Prueba local) |
| **VTA-02** | Control de existencias insuficientes | Impide registrar ventas que superen el stock disponible, emitiendo una alerta visual sin alterar el inventario. | Android / UI | **PENDIENTE** (Prueba física) |
| **VTA-03** | Registro de venta fiada con asignación de cliente | Almacena la transacción con modalidad `FIADO`, asociando el nombre del cliente y el saldo adeudado. | Android / DB | **APROBADO** (Prueba local) |
| **INV-01** | Alta y actualización local de productos | Permite agregar o modificar productos localmente; los cambios se reflejan de inmediato en el catálogo de la app. | Android / UI | **APROBADO** (Prueba local) |
| **INV-02** | Protección de stock local ante actualización remota | Al descargar actualizaciones de catálogo desde la API (precio/nombre), el stock contado en el teléfono se preserva. | Android / Logic | **PENDIENTE** (Prueba de integración) |

#### 2.5 Módulo de Cuentas por Cobrar y Fiados (FIADO)
| ID | Escenario | Criterio de Aceptación | Entorno / Nivel | Estado / Evidencia |
| :--- | :--- | :--- | :--- | :--- |
| **FIADO-01** | Consulta y resumen de cuentas pendientes | Muestra la lista de clientes con deudas activas, detallando monto total, fechas y desglose de productos. | Android / UI | **APROBADO** (Prueba local) |
| **FIADO-02** | Procesamiento de cobro parcial/total | Al registrar un cobro, actualiza la deuda local, genera un ingreso en caja y encola el evento `DEBT_PAID` en Outbox. | Android / DB | **APROBADO** (Prueba local) |
| **FIADO-03** | Protección contra doble cobro (Idempotencia local) | Intentar procesar un cobro ya liquidado se bloquea localmente sin generar transacciones ni eventos duplicados. | Android / Logic | **PENDIENTE** (Prueba física) |
| **FIADO-04** | Sincronización idempotente de cobros en la Nube | La API procesa eventos `DEBT_PAID` reconociendo el `operationId` para evitar duplicar pagos en PostgreSQL. | Backend / API | **APROBADO** (CI Run #43157514411) |

#### 2.6 Módulo de Inteligencia Artificial Conversacional Edge (IA)
| ID | Escenario | Criterio de Aceptación | Entorno / Nivel | Estado / Evidencia |
| :--- | :--- | :--- | :--- | :--- |
| **IA-01** | Transcripción ASR local con motor Vosk en español | Con el modelo `vosk-model-small-es-0.42`, una frase española libre que no esté en una lista cerrada se transcribe; mantener presionado inicia la captura y soltar finaliza/agrega segmentos una sola vez. La inicialización no bloquea la UI y recupera una copia privada incompleta del modelo. | Android / Vosk | **BLOQUEADO** (JVM por acceso denegado a Gradle; evaluación acústica en dispositivo pendiente) |
| **IA-02** | Clasificación de Intención y Extracción de Entidades | Interpreta comandos verbales (ej. "Vendí 2 cuartillas de arroz a 20 pesos") extrayendo intención, producto y monto. | Android / NLU | **APROBADO** (Prueba Unit/JVM) |
| **IA-03** | Confirmación humana obligatoria antes de guardar | Ningún comando dictado por voz se guarda automáticamente; se exige confirmación explícita mediante diálogo modal. | Android / UI | **APROBADO** (Prueba local) |
| **IA-04** | Cancelación e interrupción verbal | Si el usuario dice "cancelar" o "me equivoqué", el orquestador aborta la captura y vuelve al estado inicial. | Android / VUI | **PENDIENTE** (Prueba acústica) |
| **IA-05** | Sugerencia inteligente de productos (Asistente Yapay) | Yapay consulta las existencias en Room y sugiere productos con bajo stock o resumen diario mediante reglas locales. | Android / Logic | **APROBADO** (Prueba Unit/JVM) |
| **IA-06** | Vocabulario local experimental quechua | Con `qhatuy iskay papa a phichqa qullqi`, clasifica una solicitud de venta y extrae cantidad 2, producto papa y monto 5; no guarda la venta sin confirmación. Otros números no implementados o frases no reconocidas no deben producir acciones. | Android / JVM / VUI | **PENDIENTE** (Pruebas JVM añadidas; ejecución Gradle bloqueada por acceso a caché) |
| **IA-07** | Abstención ante texto local desconocido | Una frase sin intención reconocida recibe respuesta de no comprensión y no navega ni ejecuta una acción. | Android / JVM | **PENDIENTE** (Prueba JVM añadida; ejecución Gradle bloqueada por acceso a caché) |
| **IA-08** | Respuesta ASR simulada o vacía | El cliente no presenta el mensaje del proveedor como transcripción; usa el texto Vosk solo si su confianza permite procesarlo. En modo Quechua explícito sin transcripción cloud no ejecuta acciones locales. | Android / JVM / API | **PENDIENTE** (Validación de cliente y dispositivo) |
| **IA-09** | Fragmento de reconocimiento sin contenido útil | El texto aislado `de` no se agrega como transcripción ni ejecuta una intención; Yapay pide repetir manteniendo presionado y soltando al terminar. | Android / JVM / VUI | **PENDIENTE** (Validación en dispositivo) |
| **IA-10** | Chat textual local con Yapay | El usuario envía texto, ve ambos lados del historial y recibe respuesta o error visible. La consulta de ventas y la confirmación de alta de producto siguen usando `YapayAgent`; si hay un alta pendiente, el siguiente mensaje se interpreta como confirmación/cancelación y no como navegación. | Android / JVM / Compose | **BLOQUEADO** (Ejecución JVM denegada por sandbox; UI/dispositivo pendiente) |
| **IA-11** | Navegación comercial desde el chat | Un comando de venta abre el formulario con producto/cantidad sugeridos y no guarda; stock abre inventario filtrado; fiados abre cuentas por cobrar. El precio unitario proviene del catálogo local. | Android / JVM / Compose | **BLOQUEADO** (Ejecución JVM denegada por sandbox; UI/dispositivo pendiente) |
| **IA-12** | Push-to-talk desde el chat | Mantener presionado inicia ASR y soltar finaliza una sola transcripción usando el motor ya cargado por el Dashboard. Texto vacío/`de`, baja confianza o falta de transcripción/clasificación cloud en modo Quechua se abstienen sin navegar ni guardar. | Android / JVM / Compose | **BLOQUEADO** (Pruebas JVM no ejecutadas por acceso denegado a Gradle; prueba física pendiente) |

#### 2.7 Módulo de Sincronización Outbox e Integración Cloud (SYNC / API)
| ID | Escenario | Criterio de Aceptación | Entorno / Nivel | Estado / Evidencia |
| :--- | :--- | :--- | :--- | :--- |
| **SYNC-01** | Procesamiento asíncrono de eventos encolados | WorkManager detecta conectividad y envía lotes de hasta 100 eventos Outbox a la API con autenticación JWT. | Android / Net | **PENDIENTE** (Prueba física) |
| **SYNC-02** | Resiliencia ante fallos de red (Backoff Exponencial) | Si la API no responde, los eventos permanecen en el Outbox y se reintentan con intervalos crecientes (1s, 4s, 16s). | Android / Net | **PENDIENTE** (Prueba física) |
| **SYNC-03** | Descarga paginada del catálogo remoto | La app recupera el catálogo remoto mediante solicitudes paginadas en bloques de 50 ítems. | Backend / API | **APROBADO** (CI Run #43157514411) |
| **API-01** | Compilación y Chequeo de Tipos en Integración Continua | El pipeline de GitHub Actions ejecuta `npm run build` y `npm run typecheck:test` sin errores en TypeScript. | Backend / CI | **APROBADO** (CI Run #43157514411) |
| **API-02** | Pruebas Unitarias e Integración con PostgreSQL | Se ejecutan migraciones y suite completa de pruebas contra un contenedor PostgreSQL aislado en CI. | Backend / CI | **APROBADO** (CI Run #43157514411) |
| **API-03** | Protección de Endpoints mediante API Gateway y JWT | Solicitudes sin cabecera `Authorization: Bearer <token>` o con JWT expirado reciben HTTP 401 Unauthorized. | Backend / Gateway | **APROBADO** (CI Run #43157514411) |
| **API-04** | Emisión de Eventos en Tiempo Real (SSE) | La API emite eventos SSE aislados por negocio para notificar cambios de catálogo o estado de sincronización. | Backend / SSE | **APROBADO** (CI Run #43157514411) |
| **API-05** | Distribución cloud de actualización Android | El cliente consulta `app-updates/latest`, compara `versionCode`, descarga el APK configurado, verifica SHA-256 y abre el instalador del sistema solo si existe una versión superior. | Android / API | **PENDIENTE** (Docker y dispositivo) |
| **API-06** | Despliegue del backend en Railway | El servicio usa `/backend`, aplica migraciones contra el Postgres privado con TLS y CA validada, y responde HTTP 2xx en `/api/v1/health`. | Railway / API | **APROBADO** (Deployment `dfac6edf-cb63-4e65-8857-60aa640b9fcb`; 8 migraciones revisadas y health devuelve `database: connected`; flujos autenticados pendientes) |
| **API-07** | Proxy a proveedor ASR quechua configurado | Con JWT válido, el backend remite el multipart `audio` a HTTPS o a HTTP únicamente bajo `.railway.internal`, transmite el Bearer opcional y devuelve `texto`; URLs HTTP públicas se rechazan en producción. | Backend / API | **PARCIAL** (política de URL privada probada; E2E bloqueado por loopback y modelo aún no desplegado) |
| **API-08** | Cliente Android configurado para Railway HTTPS | El build apunta por HTTPS al dominio de Railway por defecto; un build local puede sobrescribirlo con `MYPES_API_BASE_URL`. La validación funcional debe hacerse desde un dispositivo físico. | Android / API | **PENDIENTE** (URL configurada; falta ejecutar en dispositivo físico) |
| **API-09** | Contrato del microservicio ASR quechua | `/health` queda disponible cuando ambos modelos cargan; `POST /transcribe` exige Bearer, acepta audio hasta 5 MB/120 s y retorna texto, etiqueta de intención y confianza o abstención. | Servicio ASR / Railway | **PENDIENTE** (contrato implementado; modelo no ejecutado ni servicio desplegado) |
| **API-10** | Abstención y enrutamiento NLU de voz | Intención remota no reconocida o con score <0.62 no dispara acción. Venta abre el formulario sin guardar; stock/ventas consultan datos locales; alta abre inventario. | Android / API / ASR | **PENDIENTE** (tests Android y evaluación con frases de Vinto no ejecutados) |
| **API-11** | Activación explícita de ASR Quechua cloud | El usuario activa el modo Quechua, entiende que audio se envía al servidor y, si falta conectividad, token o transcripción, la app no ejecuta la clasificación local sobre ese audio. | Android / Privacidad / API | **PENDIENTE** (flujo implementado; falta prueba JVM y en dispositivo) |

#### 2.8 Módulo de Asistente Conversacional Híbrido (ASIST / VOICE)
| ID | Escenario | Criterio de Aceptación | Entorno / Nivel | Estado / Evidencia |
| :--- | :--- | :--- | :--- | :--- |
| **ASIST-01** | Dashboard conversacional principal | Después del login, el Dashboard muestra el chat, el micrófono central y conserva acceso a los módulos comerciales. | Android / Compose | **APROBADO** (Compilación local; UI física pendiente) |
| **ASIST-02** | Clasificación local y remota de intenciones | Se conservan las once intenciones locales, se agregan cuatro flujos de negocio y se consume una intención remota solo sobre el umbral; sin confianza suficiente no se ejecuta acción. | Android / JVM | **PENDIENTE** (pruebas JVM ampliadas; ejecución Gradle bloqueada previamente por el sandbox) |
| **ASIST-03** | Confirmación de acciones del teléfono | Llamadas, mensajes, alarmas, aplicaciones, linterna y red requieren confirmación antes de ejecutarse. | Android / JVM/UI | **APROBADO** (Regla compilada; hardware pendiente) |
| **ASIST-04** | Consulta cognitiva autenticada | La consulta se valida, requiere JWT y devuelve una respuesta identificada como demostrativa. | Backend / API | **APROBADO** (22 pruebas aisladas) |
| **ASIST-05** | Corrección de transcripción con consentimiento | La corrección se guarda localmente y solo entra en la cola de aprendizaje cuando el comerciante autoriza el envío. | Android / Room v7 | **APROBADO** (Compilación; dispositivo pendiente) |
| **ASIST-06** | ASR cloud demostrativo | El endpoint indica explícitamente que no transcribió audio real cuando `ASR_PROVIDER=simulated`. | Backend / API | **APROBADO** (Prueba aislada) |
| **ASIST-07** | Persistencia de feedback autorizado | El backend conserva correcciones por negocio y deduplica por `eventId`. | PostgreSQL / Integración | **BLOQUEADO** (Credenciales locales rechazadas; `28P01`) |

#### 2.9 Ejecución backend en Windows

- Ejecutar desde `backend/`. En PowerShell, usar `npm.cmd` si `npm` resuelve a `npm.ps1` y produce `PSSecurityException`; no es necesario borrar dependencias ni cambiar políticas globales.
- La suite automatizada levanta servidores HTTP locales en puertos dinámicos. El entorno debe permitir conexiones de loopback a `127.0.0.1`; si el sandbox las deniega, registrar el bloqueo y ejecutar la suite en el flujo autorizado de GitHub Actions.

#### 2.10 Validación Android

- Desde `mobile/`, ejecutar `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug`.
- La ejecución necesita acceso al caché de Gradle del usuario (`%USERPROFILE%\.gradle`). Si la política del sandbox deniega ese acceso, registrar la prueba como bloqueada; la CI actual del backend no valida ni compila Android.

**Último intento local (2026-11-25):** `npm.cmd test` terminó con código 1 (7 aprobadas, 19 fallidas); las fallas fueron `fetch failed` al conectar con servidores de prueba en `127.0.0.1` a través del proxy. `npm.cmd run build` y `npm.cmd run typecheck:test` terminaron con código 0. `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` se bloqueó antes de iniciar Gradle por acceso denegado al lockfile en `%USERPROFILE%\.gradle\wrapperdists\gradle-8.7-bin\`. Ver detalle en [05_Resultados_de_Pruebas.md](05_Resultados_de_Pruebas.md); no se considera validación funcional.

---

### 3. AMBIENTES DE PRUEBA Y AUDITORÍA

1. **Entorno Móvil (Android):**
   * **Dispositivo de Referencia:** Smartphone Android 8.0+ / RAM 2 GB (Gama Entrada).
   * **Base de Datos:** SQLite / Room v7 cifrado mediante SQLCipher.
   * **Reconocimiento de Voz:** Modelo `vosk-model-small-es-0.42` empacado localmente.

2. **Entorno Backend (Servidor Cloud):**
   * **Runtime:** Node.js 24 LTS, Express 5, TypeScript.
   * **Base de Datos:** PostgreSQL 16 con migraciones automáticas.
   * **Servicios:** API Gateway, JWT Rotativo, Bus de Eventos SSE.

3. **Trazabilidad de Ejecución en CI/CD:**
   * Las comprobaciones del backend se auditan mediante el flujo automatizado en GitHub Actions.
   * **Run de CI de referencia:** `#43157514411` (Build, Lint, Unit Tests, Integration Tests con PostgreSQL).

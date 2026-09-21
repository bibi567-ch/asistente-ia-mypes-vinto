# Blueprint Arquitectónico: Asistente Conversacional Offline MYPES Vinto

**Ecosistema Desacoplado: App Móvil Offline-First (Kotlin) + Backend API REST (Spring Boot)**

## 1. Visión General de la Arquitectura

El sistema utilizará una arquitectura **Cliente-Servidor Desacoplada y Offline-First**.

A diferencia de una SPA web tradicional, aquí el **cliente es la fuente de verdad temporal**: el comerciante opera 100 % sobre el dispositivo Android sin depender de la red, y el backend actúa como consolidador diferido.

- **App Móvil (Cliente):** aplicación **nativa Android**, no híbrida ni PWA — la restricción de hardware (≤ 2 GB RAM) descarta motores basados en WebView. Ejecuta localmente el reconocimiento de voz, la clasificación de intención y toda la lógica de ventas/inventario.
- **Backend (Servidor API):** vive en la nube, recibe las transacciones encoladas por cada comerciante, aplica reglas de negocio de consolidación y resuelve conflictos entre dispositivos.
- **Base de Datos Maestra:** oculta y protegida, accesible únicamente por el backend; el dispositivo móvil mantiene su propia copia local cifrada.

## 2. CAPA MÓVIL (App Android — Cliente)

### Tecnologías a Utilizar

- **Lenguaje y Framework Core:** Kotlin nativo + Jetpack Compose (UI declarativa, sin XML).
- **Voz a Texto (ASR):** Vosk — modelo offline en español (~50 MB), corre 100 % on-device.
- **Clasificación de Intención y Entidades (NLU):** modelo TFLite propio, entrenado para el dominio comercial de Vinto (venta, compra, fiado, cobro, consulta, gasto).
- **Persistencia Local:** Room (capa ORM sobre SQLite) + **SQLCipher** para cifrado AES-256 en reposo.
- **Seguridad Local:** Android Keystore para la gestión de la clave maestra de cifrado; autenticación por PIN con hashing **Argon2** (con sal); Certificate Pinning sobre TLS 1.3 para las llamadas al backend.
- **Sincronización en segundo plano:** WorkManager, con reintentos automáticos ante fallos de red.
- **Cliente HTTP:** Retrofit + OkHttp (interceptor de autenticación JWT).
- **Inyección de dependencias:** Hilt (Dagger).
- **Texto a voz (respuesta hablada):** Android TextToSpeech API nativa.

### Patrones de Diseño (App Móvil)

- **MVVM (Model-View-ViewModel):** cada pantalla (Dashboard, Inventario, Fiados) tiene un ViewModel que expone estado observable a la UI de Compose.
- **Clean Architecture (3 capas):** Presentación (Compose + ViewModel) → Dominio (UseCases, reglas de negocio puras) → Datos (Repository + fuentes local/remota). Esto es lo que sostiene RA-05 (modificabilidad) en el SAD.
- **Repository Pattern:** `VentaRepository`, `InventarioRepository`, etc. ocultan si el dato viene de Room o de la API remota.
- **Observer (Kotlin Flow / StateFlow):** la UI reacciona automáticamente a cambios en la base local (p. ej. el stock se actualiza en pantalla apenas se confirma una venta).
- **Facade:** un `AsistenteVozFacade` oculta la complejidad de orquestar Vosk (ASR) → TFLite (NLU) → confirmación visual → persistencia, exponiendo una sola función a la capa de presentación.
- **Outbox Pattern:** toda transacción se escribe primero en una tabla `outbox` local; el `SyncManager` la envía y la marca como sincronizada solo tras respuesta `200 OK` del backend.
- **Singleton (vía Hilt):** `SessionManager`/`AuthManager` (token JWT), `DatabaseInstance` (Room), con una única instancia en toda la app.
- **Strategy (implícito en NLU):** el clasificador de intención selecciona en runtime el "manejador" correspondiente (`RegistrarVentaHandler`, `RegistrarFiadoHandler`, `ConsultarInventarioHandler`, etc.) según el intent detectado.

### Estructura de Carpetas (Clean Architecture + Feature-Driven)

```
vinto-app-android/
├── app/
│   ├── src/main/java/com/vinto/asistente/
│   │   ├── core/                      # Configuración transversal
│   │   │   ├── di/                    # Módulos Hilt (DatabaseModule, NetworkModule)
│   │   │   ├── security/              # SQLCipher key management, JWT interceptor
│   │   │   └── voice/                 # Wrapper de Vosk (ASR) + TextToSpeech
│   │   │
│   │   ├── data/                      # Capa de Datos
│   │   │   ├── local/
│   │   │   │   ├── dao/               # VentaDao, ProductoDao, OutboxDao (Room)
│   │   │   │   └── entities/          # Entidades @Entity de Room
│   │   │   ├── remote/
│   │   │   │   ├── api/               # VintoApiService (Retrofit)
│   │   │   │   └── dto/               # Modelos de request/response JSON
│   │   │   └── repository/            # Implementaciones de los Repository
│   │   │
│   │   ├── domain/                    # Capa de Dominio (lógica de negocio pura)
│   │   │   ├── model/                 # Venta, Producto, Fiado (modelos de dominio)
│   │   │   ├── repository/            # Interfaces de Repository (contratos)
│   │   │   └── usecase/               # RegistrarVentaUseCase, ConsultarStockUseCase...
│   │   │
│   │   ├── nlu/                       # Pipeline de PLN Edge
│   │   │   ├── AsrEngine.kt           # Integración Vosk
│   │   │   ├── IntentClassifier.kt    # Integración modelo TFLite
│   │   │   └── EntityExtractor.kt     # Extracción de producto/cantidad/monto
│   │   │
│   │   ├── sync/                      # Sincronización diferida
│   │   │   ├── OutboxWorker.kt        # WorkManager: detecta red y envía cola
│   │   │   └── ConflictResolver.kt    # Política LWW en el cliente (pre-envío)
│   │   │
│   │   └── presentation/              # MÓDULOS DE NEGOCIO (features)
│   │       ├── auth/                  # Login por PIN local
│   │       ├── dashboard/             # Resumen del día, botón de micrófono
│   │       ├── inventario/            # CRUD de productos y stock
│   │       ├── fiados/                # Registro y cobro de deudas
│   │       └── reportes/              # Productos más vendidos
│   │
│   └── res/                           # Íconos, colores, tipografía (Design Tokens)
└── build.gradle.kts
```

## 3. CAPA BACKEND (API REST de Consolidación)

### Tecnologías a Utilizar

- **Lenguaje y Framework:** Java 17 + Spring Boot 3.
- **API Framework:** Spring Web (REST) + Spring Data JPA.
- **Seguridad:** Spring Security + JWT (autenticación de cada comerciante/dispositivo).
- **Persistencia:** PostgreSQL como base de datos maestra.

### Patrones de Diseño (Backend)

- **MVC (Controller-Service-Repository):** separación estricta entre `Controller` (endpoints REST), `Service` (reglas de negocio y política LWW) y `Repository` (acceso a datos vía Spring Data JPA).
- **DTO (Data Transfer Object):** clases `*Dto` que filtran y formatean qué datos se exponen a la app móvil, desacoplando el modelo de persistencia del contrato de la API.
- **Repository Pattern:** las interfaces `JpaRepository` de Spring Data actúan como repositorio, aislando las consultas SQL de la lógica de negocio.
- **Strategy:** `ConflictResolutionStrategy` encapsula la política LWW y permite sustituirla en el futuro sin tocar el `Service`.
- **Idempotency / Outbox Consumer:** cada transacción recibida trae un `idempotency_key` generado en el cliente, para evitar duplicados si la app reintenta el envío.

### Estructura de Carpetas (Domain-Driven, estilo Spring Boot)

```
vinto-backend-springboot/
├── src/main/java/com/vinto/backend/
│   ├── config/                        # SecurityConfig, JwtConfig, CorsConfig
│   │
│   ├── auth/
│   │   ├── controller/                # AuthController (login, refresh token)
│   │   └── service/                   # AuthService
│   │
│   ├── comerciantes/                  # Gestión de cuentas de comerciantes
│   │   ├── model/                     # Entidad Comerciante
│   │   ├── repository/
│   │   └── service/
│   │
│   ├── transacciones/                 # Núcleo del dominio (ventas, compras, fiados)
│   │   ├── controller/                # TransaccionController (recibe lotes Outbox)
│   │   ├── model/                     # Venta, DetalleVenta, MovimientoCaja
│   │   ├── dto/                       # TransaccionRequestDto, TransaccionResponseDto
│   │   ├── repository/
│   │   └── service/
│   │       ├── TransaccionService.java
│   │       └── ConflictResolutionStrategy.java   # Política LWW
│   │
│   └── reportes/                      # Endpoints de agregación para dashboards
│       └── controller/
│
├── src/main/resources/
│   └── application.yml                # Configuración de BD, JWT, perfiles (dev/prod)
└── pom.xml
```

## 4. BASE DE DATOS Y DEVOPS

- **Base de datos local (dispositivo):** SQLite gestionada por Room, cifrada con **SQLCipher (AES-256)**.
- **Base de datos maestra (nube):** **PostgreSQL**.
- **DevOps (despliegue):** Docker + Docker Compose, con un `docker-compose.yml` que levanta 2 servicios:
  - Contenedor de **Spring Boot** (backend, expuesto vía Gunicorn-equivalente embebido de Spring/Tomcat).
  - Contenedor de **PostgreSQL**.
- **Ventaja:** garantiza que el backend funcione de forma idéntica en la máquina de cada desarrollador y en el entorno de despliegue final, y facilita levantar un entorno de pruebas para la defensa oral del proyecto.

## 5. Resumen del Stack Tecnológico

| Capa | Tecnología | Rol |
| :--- | :--- | :--- |
| UI Móvil | Kotlin + Jetpack Compose | Interfaz declarativa, offline-first |
| Voz → Texto | Vosk (on-device) | ASR sin conexión |
| Intención / Entidades | TFLite (clasificador propio) | NLU sin conexión |
| Persistencia local | Room + SQLCipher | Almacenamiento cifrado AES-256 |
| Sincronización | WorkManager + Outbox Pattern | Envío diferido y tolerante a fallos |
| Comunicación | Retrofit/OkHttp + JWT | Cliente HTTP autenticado |
| Backend | Java Spring Boot + Spring Security | API REST de consolidación |
| Persistencia remota | PostgreSQL | Base de datos maestra |
| DevOps | Docker + Docker Compose | Entorno reproducible |

---
*Este documento complementa el SAD v1.1 (secciones 9 y 10) y es la referencia técnica oficial para el desarrollo. Cualquier cambio de stack debe reflejarse simultáneamente aquí, en los ADRs del SAD y en los diagramas C2.*

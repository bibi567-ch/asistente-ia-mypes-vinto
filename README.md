# Documento Maestro del Proyecto Integrador

**Agente conversacional multilingüe (castellano, quechua, aimara y variantes mixtas) con procesamiento de lenguaje natural en el dispositivo (Edge AI) y arquitectura de microservicios resiliente (API Gateway) para la inclusión digital en las MYPES del centro de Vinto, 2026-2027**

Proyecto: Yapay · Materias integradas: Investigación I (Proyecto de Grado), Arquitectura de Software (ARQ-643) y Tecnologías en Internet (TEC-644)

## Resumen ejecutivo
Yapay es un asistente conversacional por voz que permite a comerciantes de Vinto registrar ventas, calcular el IVA y consultar su ganancia hablando en castellano, quechua, aimara o en sus variantes mixtas (quechuañol y aimarañol). El reconocimiento de voz y la interpretación de intenciones se ejecutan en el propio teléfono (Edge AI), por lo que la aplicación funciona sin internet. Cuando hay conexión, las operaciones se sincronizan con una nube compuesta por un API Gateway con Circuit Breaker y servicios REST sobre PostgreSQL, usando el patrón Outbox y operaciones idempotentes.

El documento se organiza en cuatro partes: la investigación (problema, objetivos, alcance, metodología), la arquitectura de software (decisiones, atributos de calidad, vistas C4), la implementación tecnológica (API REST, base de datos, seguridad, sincronización) y la planificación (cronograma, trade-offs, referencias).

---

# PARTE I: INVESTIGACIÓN (Perfil de Proyecto de Grado)
**Enfoque:** aporte científico, inclusión social, brecha lingüística y viabilidad de la IA Edge.

## 1. Introducción
El proyecto se sitúa en la intersección de la Ingeniería de Software, la Interacción Humano-Computadora (HCI) y la Lingüística Computacional. Su objeto de estudio es el proceso de interacción y registro de operaciones financieras en las Micro y Pequeñas Empresas (MYPES). El campo de acción es el diseño e implementación de un agente conversacional (Yapay) que opera de forma local (Edge Computing) y entiende comandos de voz en castellano, quechua, aimara y, fundamentalmente, sus variantes mixtas, que son el idioma real del comercio en Vinto.
El aporte científico radica en crear un puente cognitivo para comerciantes con baja alfabetización digital y hablantes de lenguas originarias. Se abandona el paradigma de la Interfaz Gráfica de Usuario (GUI) tradicional y se adopta una Interfaz de Usuario por Voz (VUI) adaptada al contexto real del mercado.

## 2. Antecedentes
### 2.1. Situación actual y procesos
En el centro de Vinto el comercio es predominantemente informal y manual: el cliente pide (mezclando idiomas), el comerciante revisa el stock visualmente, calcula con calculadora o mentalmente y anota en un cuaderno al final del día. Los datos no se analizan, lo que impide conocer márgenes de ganancia y tomar decisiones informadas.

### 2.2. Análisis de herramientas existentes
*   **Odoo POS:** Requiere servidor y conexión permanente; interfaz en castellano técnico; sin voz ni lenguas originarias. Descartado por costos y hardware.
*   **Loyverse POS:** Registra ventas offline, pero no muestra stock actualizado ni permite facturación sin red; interfaz puramente gráfica. Descartado: no resuelve la consulta de existencias sin red.
*   **Siigo / MultiCont:** Diseñados para escritorio con conectividad permanente; sin movilidad, voz ni quechua/aimara. Descartados.
Este análisis justifica el desarrollo de software a medida.

### 2.3. Revisión de literatura
*   **ASR en lenguas originarias:** Zevallos et al. (2022) demuestran que el Data Augmentation permite adaptar modelos ASR al quechua. Para el aimara y el quechuañol se emplearán gramáticas restringidas y modelos de lenguaje ligeros (TFLite).
*   **Edge AI:** la documentación de Android Developers (2024) respalda la ejecución offline de modelos como Vosk y TensorFlow Lite en dispositivos de gama media.
*   **Inclusión digital:** la CEPAL (2024) señala que la adaptación lingüística es clave para la adopción digital en economías informales.

### 2.4. Dependencias
Técnicas (modelos de código abierto), institucionales (acceso a comerciantes) y académicas (calendario 2026-2).

## 3. El problema
### 3.1. Situación problemática (enfoque causal)
*   **Causa:** Registro manual (cuaderno) y cálculo mental. **Efecto:** Lentitud en el cierre de caja y errores aritméticos que afectan la rentabilidad.
*   **Causa:** Rigidez idiomática de las herramientas (solo castellano, sin quechuañol ni aimarañol). **Efecto:** Exclusión digital de comerciantes bilingües.
*   **Causa:** Complejidad de las interfaces gráficas actuales. **Efecto:** Rechazo tecnológico y dependencia del papel.

### 3.2. Formulación del problema (Causa — Término de Relación — Efecto)
El empleo de registros manuales y el cálculo mental, sumados a la barrera idiomática y a la complejidad de los sistemas comerciales diseñados únicamente en castellano técnico (**Causa**), en el proceso de registro de ventas y cálculo de impuestos de las MYPES del centro de Vinto (**Término de Relación**), provoca errores aritméticos en el cierre de caja, pérdida de tiempo y exclusión digital de los comerciantes bilingües (**Efectos**), durante el periodo 2026-2027.

## 4. Objetivos
**Objetivo general:** desarrollar un agente conversacional multilingüe (castellano, quechua, aimara y variantes mixtas) con procesamiento de lenguaje natural en el dispositivo para el registro de ventas y cálculo de impuestos en las MYPES del centro de Vinto, 2026-2027.
**Objetivos específicos:**
*   **OE1:** diagnosticar las necesidades de interacción lingüística y generar un corpus de voz/texto en quechuañol y aimarañol mediante trabajo de campo.
*   **OE2:** adaptar un modelo ASR y NLU (Vosk + TFLite) mediante Transfer Learning para interpretar intenciones comerciales en variantes mixtas.
*   **OE3:** implementar la arquitectura del agente en una app Android offline con persistencia cifrada (Room/SQLCipher).
*   **OE4:** validar la precisión (WER) y la usabilidad (SUS) con comerciantes reales.

## 5. Alcance y límites
**Alcance.** La app Android recibe comandos de voz en castellano, quechua, aimara y variantes mixtas, interpreta la intención (registrar venta, calcular IVA, consultar ganancia) y ejecuta la operación en Room de forma 100% offline. Incluye confirmación visual y auditiva (TTS) y un módulo de asesoría contable básica (alertas de margen bajo y liquidez). La sincronización con la nube se apoya en los componentes descritos en las Partes II y III. Entregables: código fuente, modelos adaptados, corpus, informe SUS y documentación.
**Límites.**
*   No incluye facturación electrónica certificada por el SIN.
*   El reconocimiento de voz se limita a un vocabulario comercial acotado.
*   No se desarrolla para iOS.
*   El agente no reemplaza a un contador.
*   El núcleo de la investigación es la app y su IA local; el backend y el gateway son componentes de soporte para la sincronización, desarrollados en las materias del integrador.

## 6. Justificación
*   **Social (obligatoria):** el agente beneficia a comerciantes quechuahablantes, aimarahablantes y hablantes de variantes mixtas, permitiéndoles usar la tecnología con su propia voz. Promueve la inclusión social, reduce la brecha digital y mejora la calidad de vida del microempresario.
*   **Técnica:** herramientas de código abierto (Kotlin, Vosk, TFLite); existen corpus públicos para quechua y técnicas documentadas de Transfer Learning para quechua/aimara; el equipo cuenta con hardware de pruebas.
*   **Operativa:** la VUI reduce la carga cognitiva y las entrevistas preliminares mostraron alta receptividad.
*   **Modelo de negocio:** propuesta de valor offline multilingüe; segmento: MYPES de Vinto; canal: APK gratuita vía ACCV; costo operativo para el comerciante: 0 Bs (usa hardware existente).

## 7. Metodología
*   **Enfoque:** mixto (cualitativo para el corpus lingüístico; cuantitativo para pruebas SUS y WER).
*   **Trabajo de campo:** muestra de 20 comercios en Vinto, con grabación de comandos de voz en quechuañol y aimarañol.
*   **Desarrollo:** metodología ágil (Scrum).
*   **Validación:** pruebas SUS a 15 usuarios, medición de WER y juicio de expertos.

---

# PARTE II: ARQUITECTURA DE SOFTWARE (ARQ-643)
**Enfoque:** patrón API Gateway + Circuit Breaker, atributos de calidad y vistas C4.

## 1. Contexto y problema arquitectónico
Yapay se compone de una app Android (Edge) y varios microservicios en la nube (Auth, Sync, IA). El acoplamiento directo entre la app y cada servicio genera fragilidad: si el servicio de IA falla, la app queda esperando respuesta; además se expone la red interna y se consumen RAM y batería del móvil.

## 2. Decisiones arquitectónicas (ADR)
*   **ADR-001 (Edge AI vs Cloud AI):** Vosk y TFLite se ejecutan en el dispositivo por privacidad, latencia casi nula y operación offline.
*   **ADR-002 (API Gateway + Circuit Breaker):** el gateway es el punto único de entrada; centraliza la seguridad (JWT) y protege a la app de fallos en cascada. Si la IA cae, el comerciante sigue vendiendo.
*   **ADR-003 (PostgreSQL vs NoSQL):** se elige PostgreSQL por su integridad transaccional (ACID) al manejar dinero e impuestos.

## 3. Atributos de calidad y escenarios (ATAM)
*   **QS-01 Seguridad:** Acceso sin token. Bloqueo temprano (401) en < 50 ms.
*   **QS-02 Disponibilidad:** Servicio IA falla o timeout en hora pico. El Circuit Breaker abre el circuito y responde Fallback. 0% de bloqueo de la app; Fallback < 100 ms.
*   **QS-03 Rendimiento:** App móvil envía 100 ventas en Red 3G. Enruta en un solo salto. Latencia reducida 40%.
*   **QS-04 Modificabilidad:** Desarrollador agrega nuevo microservicio. Agrega ruta sin tocar la app. 0 cambios en la app.

## 4. Vistas arquitectónicas (especificación para diagramas)
### 4.1. C1 -- Contexto
Actor: comerciante de Vinto. Sistema: App Yapay. Sistemas externos: pasarela de pago QR y servicios del SIN. El comerciante interactúa por voz o texto; el sistema solicita la generación de QR y envía borradores fiscales.

### 4.2. C2 -- Contenedores
*   **Frontera Android (Edge):** App Yapay (Kotlin/Compose, Vosk, TFLite, Room/SQLCipher, Outbox).
*   **Frontera Cloud:** API Gateway (Node.js/Express/Opossum/jsonwebtoken) → AuthService, SyncService, IAService (Python/FastAPI) → PostgreSQL 16.
*   **Protocolos:** App → Gateway (HTTPS/JWT); Gateway → Servicios (HTTP interno); Servicios → DB (TCP/SQL).

### 4.3. C3 -- Componentes (Android)
Clean Architecture en cuatro capas: Presentación, Dominio, Datos e IA Local.

### 4.4. Secuencia: registro de venta y fallback
1.  **Camino feliz:** App → Gateway (valida JWT) → SyncService (guarda en PostgreSQL) → 200 OK.
2.  **Camino de falla:** App → Gateway → IAService (falla) → el Circuit Breaker abre → el Gateway responde 503 con Fallback → la app guarda la operación en el Outbox local.

### 4.5. Diagrama de estados (Outbox Android)
PENDIENTE → ENVIANDO → SINCRONIZADO, o ERROR (con reintento).

### 4.6. Diagrama de clases (Dominio)
Usuario, Negocio, Producto, Venta, DetalleVenta.

### 4.7. Entidad-Relación (PostgreSQL)
Tablas con PK, FK y restricciones UNIQUE para idempotencia (por ejemplo, business_id + operation_id). Ver el detalle en la Parte III.

### 4.8. Despliegue (Docker)
API Gateway (3000), AuthService (3001), SyncService (4000), IAService (5000), PostgreSQL (5432).

## 5. Prototipo funcional del API Gateway
*   **Stack:** Node.js + Express + Opossum + jsonwebtoken.
*   **Lógica clave:** un middleware centraliza la validación del JWT. El Circuit Breaker envuelve las llamadas a IA y Sync. Si se acumulan 5 fallos (timeout de 2 s), el circuito se abre durante 10 s y devuelve un 503 con mensaje de Fallback, de modo que el comerciante sigue operando.

## 6. Trade-offs
*   **Latencia:** cada salto de red adicional suma unos 15 ms.
*   **Punto único de fallo:** en producción requiere alta disponibilidad (clúster de gateways).
*   **Complejidad:** un contenedor más que desplegar y monitorear.

---

# PARTE III: TECNOLOGÍAS EN INTERNET (TEC-644)
**Título del componente:** Implementación de una API REST transaccional e idempotente para la consolidación de operaciones offline-first.

## 1. Stack tecnológico
*   **Backend:** Node.js 24, TypeScript, Express 5
*   **Base de datos:** PostgreSQL 16 (ACID, JSONB, NUMERIC)
*   **Autenticación:** JWT (jose, HMAC-SHA256) y hash de contraseñas con scrypt
*   **Pruebas:** Node Test Runner, integración con Docker

**¿Por qué PostgreSQL y no MongoDB?** El sistema maneja dinero e impuestos. PostgreSQL garantiza transacciones ACID, integridad referencial (claves foráneas), precisión decimal (NUMERIC) e idempotencia a nivel de base de datos mediante restricciones UNIQUE. MongoDB se descartó para el núcleo transaccional; para el payload de sincronización se usa JSONB dentro de PostgreSQL.

## 2. Especificación de la API REST
*   POST /api/v1/auth/login: Autentica y retorna un JWT de 15 minutos.
*   POST /api/v1/auth/change-password: Actualiza credenciales.
*   GET/POST/PUT/DELETE /api/v1/products: CRUD de catálogo, con alcance por business_id.
*   GET /api/v1/products/sync?desde=: Sincronización paginada del catálogo.
*   POST /api/v1/transacciones/batch: Recibe lotes del Outbox móvil (máx. 100) y valida idempotencia con operation_id.
*   POST /api/v1/cloud/facturas: Almacena borradores de factura.
*   POST /api/v1/cloud/qr: Almacena códigos QR.
*   GET /api/v1/cloud/sync/updates: Canal SSE para notificar cambios de catálogo en tiempo real.

## 3. Módulos del backend
AuthController, ProductController, SyncController y CloudController, detrás del API Gateway descrito en la Parte II.

## 4. Diseño de la base de datos (PostgreSQL)
*   **businesses:** id (PK, UUID), name, rubro
*   **users:** id (PK), business_id (FK), email (UNIQUE), password_hash, role
*   **products:** id (PK), business_id (FK), name (UNIQUE por negocio), sale_price (NUMERIC)
*   **transacciones:** id (PK), business_id (FK), operation_id (UNIQUE compuesto con business_id), payload_json (JSONB), estado
*   **conflictos:** id (PK), operation_id, tipo_conflicto, resuelto (Bool)

## 5. Seguridad
*   **Aislamiento multi-tenant:** el business_id se extrae del token JWT, nunca del cuerpo de la petición. Todas las consultas SQL filtran por ese ID, de modo que un comerciante no puede ver datos de otro.
*   **Autenticación:** JWT stateless de vida corta (15 min) y contraseñas con scrypt, resistente a ataques de fuerza bruta.
*   **Centralización:** el API Gateway valida el JWT antes de enrutar (escenario QS-01).

## 6. Sincronización y tiempo real
*   **Patrón Outbox:** en la app móvil, cuando el usuario recupera la conexión, el OutboxWorker envía un lote a /transacciones/batch.
*   **Respuesta por operación:** el backend devuelve un estado para cada operación: ACEPTADA, DUPLICADA o CONFLICTO.
*   **Idempotencia:** un operation_id ya procesado para el mismo negocio se responde como DUPLICADA, evitando ventas repetidas por reintentos de red.
*   **Conflictos:** si una venta offline choca con el stock en la nube, se registra en la tabla conflictos para revisión manual. El catálogo usa política Last-Write-Wins (LWW).
*   **Tiempo real:** Server-Sent Events (SSE) notifican al móvil los cambios del catálogo.

## 7. Manual de despliegue
1.  git clone [repo]
2.  docker-compose up -d postgres
3.  npm install y npm run db:migrate
4.  npm run dev (Gateway en el puerto 3000)
5.  Pruebas: npm test y npm run test:integration

---

# PARTE IV: PLANIFICACIÓN Y REFERENCIAS

## 1. Cronograma (12 meses)
*   **Meses 1-2:** Diagnóstico y recolección de corpus
*   **Meses 3-5:** Adaptación de modelos ASR/NLU
*   **Meses 6-8:** Desarrollo de la app Android (UI, Room, SQLCipher) y del backend/gateway
*   **Meses 9-10:** Pruebas SUS y medición de WER
*   **Meses 11-12:** Documentación y defensa

## 2. Referencias
[1] R. Zevallos et al., "Data Augmentation for Low-Resource Quechua ASR Improvement," Proc. Interspeech, 2022.
[2] Android Developers, "Build an offline-first app," 2024.
[3] CEPAL, "Inclusión financiera digital en América Latina," 2024.

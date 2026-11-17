# Proyecto y Alcance

## 1. Identificación y Ficha Técnica

* **Título Oficial del Proyecto:** Agente conversacional multilingüe (castellano, quechua, aimara y variantes mixtas locales) con procesamiento de lenguaje natural en el dispositivo para el registro de ventas y cálculo de impuestos en las MYPES del centro de Vinto, 2026-2027.
* **Nombre Comercial / Sigla:** Yapay ("Agente Conversacional Yapay").
* **Equipo de Desarrollo:**
  * **Eva Lizeth Chino Quispe** — Líder de Proyecto, Requisitos y QA Documental.
  * **Christian Gonzales Mamani** — Ingeniería de IA y Pipeline Conversacional.
  * **Marializ Mamani Quispe** — Backend, Seguridad y Persistencia de Datos.
  * **Kevin Jhonathan Rocha Rojas** — Diseño UX/UI y Prototipado.
  * **Luis Gerardo Lazo Bustamante** — Arquitectura C4 y QA Técnico.
* **Institución:** Universidad Adventista de Bolivia (UAB) — Carrera de Ingeniería en Sistemas.
* **Materias Integradas:** Investigación I (INV-645), Arquitectura de Software (ARQ-643) y Tecnologías en Internet (TEC-644).

---

## 2. Propósito y Descripción General

El proyecto **Yapay** es un asistente conversacional multilingüe de borde (Edge AI) diseñado para romper la brecha de inclusión digital y la barrera idiomática en las Micro y Pequeñas Empresas (MYPES) del centro de Vinto, Cochabamba. 

Permite a los comerciantes registrar ventas, calcular impuestos básicos (IVA) y consultar sus totales y ganancias diarias de manera intuitiva mediante comandos de voz en **castellano, quechua, aimara y sus variantes mixtas locales (quechuañol y aimarañol)**, así como mediante teclado. 

El sistema opera bajo el paradigma **Offline-First**: todo el procesamiento de voz, clasificación de intenciones y almacenamiento de transacciones se ejecuta 100% en el dispositivo móvil Android sin requerir conexión a internet. Cuando existe conectividad disponible, el sistema sincroniza las operaciones locales de forma diferida e idempotente hacia la nube mediante una arquitectura de microservicios protegida por un **API Gateway con Circuit Breaker**.

---

## 3. Enlaces Oficiales y Evidencias

* **Repositorio Oficial de Código (GitHub):** `bibi567-ch/asistente-ia-mypes-vinto`
* **Prototipo Interactivo y Sistema de Diseño (Figma):**
  * **Prototipo Navegable en Figma:** Prototipo interactivo registrado en la especificación y sistema de diseño UX/UI.
  * **Sitio Web del Prototipo:** Plataforma de prototipos navegables Figma (`snake-dijon`).
* **Directorio de Evidencias y Pruebas:**
  * **Bitácora de Pruebas y Evidencias Visuales:** Ubicada en `docs/evidencias/` y registrada cronológicamente en `05_Resultados_de_Pruebas.md`.
  * **Reporte de Integración Continua (CI/CD):** Ejecuciones automatizadas en GitHub Actions registradas en `07_CI_CD_Report.md`.

---

## 4. Alcance Funcional (Qué Incluye)

### A. Capa Móvil (Android - Edge AI)
1. **Autenticación y Perfil Local:**
   * Registro e inicio de sesión local mediante identificador (carnet, celular o correo) y PIN de 4 dígitos cifrado localmente (PBKDF2 / Argon2 con sal aleatoria) y bloqueo temporal tras 5 intentos fallidos.
   * Gestión de perfil del comerciante y datos de la MYPE.
2. **Onboarding por Plantillas de Negocio:**
   * Configuración inicial rápida adaptada a 6 rubros comerciales locales: *Farmacia, Pastelería, Ferretería, Licorería, Broastería y Minimarket*, cargando categorías, márgenes orientativos y catálogos predeterminados.
3. **Procesamiento de Lenguaje Natural en el Dispositivo (Edge AI):**
   * Reconocimiento Automático de Voz (ASR) con motor **Vosk** en español/variantes locales.
   * Clasificación de intenciones comercial (*Venta, Consulta, Cobro, Fiado*) y extracción de entidades (*producto, cantidad, precio, unidad, cliente*).
   * Soporte multilingüe para castellano, quechua, aimara y variantes mixtas (quechuañol y aimarañol).
4. **Principio de Confirmación Humana Obligatoria:**
   * Todo dictado de voz genera una tarjeta de confirmación visual y auditiva (TTS) antes de guardar cualquier transacción en la base de datos local.
5. **Gestión Comercial y Deudas (Fiados):**
   * Registro de ventas al contado y fiadas.
   * Módulo de Fiados para listar cuentas pendientes y registrar cobros totales o parciales transaccionalmente.
   * Cálculo automático de totales e impuestos (IVA) y descuento en tiempo real del saldo local de existencias.
6. **Reportes e Indicadores Financieros:**
   * Dashboard con métricas diarias, gráfico semanal interactivo y alertas de margen bajo o stock crítico.
   * Exportación de reportes de ventas diarias a formato CSV compartido.
7. **Sincronización Offline (Patrón Outbox):**
   * Encolamiento automático de todas las operaciones locales en la tabla Outbox cifrada con SQLCipher (AES-256).
   * Envío diferido en segundo plano mediante `WorkManager` cuando se detecta red Wi-Fi o datos móviles.

### B. Capa Nube / Backend (API REST & Microservicios)
1. **API Gateway con Resiliencia:**
   * Punto único de entrada en Node.js 24 + Express 5, con patrón **Circuit Breaker** (Opossum) para evitar fallos en cascada si un microservicio interno se cae.
2. **Autenticación y Seguridad Remota:**
   * Registro e inicio de sesión independiente con correo/contraseña (hash scrypt) y emisión de tokens **JWT** (vida de 15 min) con **Refresh Token rotativo** y período de gracia offline para no interrumpir la sincronización del Outbox.
3. **Recepción Idempotente de Lotes:**
   * Sincronización de transacciones por lotes mediante claves de idempotencia (`operation_id` UUID v4), garantizando que ninguna venta se duplique ni se pierda.
4. **Prototipos de Integración:**
   * Generación de borradores de factura con PDF simulado (no fiscal).
   * Almacenamiento y consulta de códigos QR de cobro por negocio.
   * Notificaciones en tiempo real vía Server-Sent Events (SSE) autenticados.

---

## 5. Exclusiones y Límites Explícitos (Qué NO Incluye)

1. **Facturación Electrónica Fiscal:** No incluye certificación ni emisión oficial de facturas ante el Servicio de Impuestos Nacionales (SIN). Las facturas emitidas son únicamente borradores/PDFs simulados para control interno.
2. **Módulo de Almacén / Compras a Proveedores:** No es un ERP ni un sistema de compras a proveedores, gestión de lotes, fechas de vencimiento de suministros ni contabilidad de costos avanzada.
3. **Desarrollo para otros Sistemas Operativos:** No se desarrolla aplicación nativa para iOS (iPhone) ni para Windows/macOS. El cliente móvil es 100% Android nativo.
4. **Autonomía Total de la IA o Reemplazo Contable:** El agente Yapay opera con reglas y modelos acotados; no utiliza un LLM generativo remoto para tomar decisiones comerciales autónomas ni reemplaza la asesoría de un contador profesional.
5. **Pasarelas de Pago e Integraciones Bancarias Reales:** No realiza cobros automáticos con tarjetas de crédito/débito ni procesamiento bancario en línea.
6. **Frontend Web para Comercio:** No incluye una plataforma e-commerce ni un portal web público de venta para clientes finales.

---

## 6. Arquitectura y Stack Tecnológico

| Capa / Componente | Tecnología Seleccionada | Detalle de Implementación |
| :--- | :--- | :--- |
| **Cliente Móvil** | Kotlin Nativo + Jetpack Compose | Android SDK 35 (minSdk 26), Clean Architecture + MVVM. |
| **Persistencia Local** | Room / SQLite + SQLCipher | Cifrado AES-256 en reposo para datos comerciales y cola Outbox. |
| **Edge AI (Voz & NLU)** | Vosk ASR + Reglas NLU Kotlin | Modelos cuantizados en el APK (`vosk-model-small-es-0.42`). |
| **Sincronización Móvil** | WorkManager + Retrofit / OkHttp | Envío de lotes JSON con autenticación Bearer JWT y reintentos exponencial backoff. |
| **API Gateway** | Node.js 24 + Express 5 + Opossum | Circuit Breaker, ruteo unificado `/api/v1/`, validación JWT centralizada. |
| **Backend / Servicios** | Node.js 24 + TypeScript + Express 5 | Servicios modularizados (AuthService, SyncService, IAService opcional). |
| **Persistencia Servidor** | PostgreSQL 16 | Transacciones ACID, tipos `NUMERIC` para precisión monetaria, payloads `JSONB` y restricciones `UNIQUE` para idempotencia multi-tenant. |
| **Orquestación y CI/CD** | Docker Compose & GitHub Actions | Contenedores Docker para dev/test y pipelines automatizados de migraciones y pruebas. |

---

## 7. Denominación Oficial y Criterio de Vigencia Documental

* **Denominación Oficial Vigente:** “Agente conversacional multilingüe (castellano, quechua, aimara y variantes mixtas) con procesamiento de lenguaje natural en el dispositivo para MYPES de Vinto” (denominado **Yapay**).
* **Criterio de Vigencia:** La arquitectura e implementación técnica descrita en este documento y en `02_Arquitectura_SAD.md` corresponde al stack **Node.js 24 / Express 5 / PostgreSQL 16**. El SAD v1.1 histórico (que mencionaba Spring Boot) se conserva únicamente como antecedente académico.

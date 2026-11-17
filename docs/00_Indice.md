# 00_Indice.md — Índice General de la Documentación del Sistema (Versión Actualizada)

> **Estado del documento:** Índice consolidado alineado con el **Documento Maestro del Proyecto Integrador** y la estructura unificada de las tres asignaturas del plan de estudios (Gestión 2-2026 / 2026-2027).  
> **Sistema:** Asistente Conversacional Multilingüe Offline-First **Yapay** para MYPES de Vinto.

---

## 1. Identificación y Coherencia General del Proyecto

* **Título Oficial del Proyecto:** “Agente conversacional multilingüe (castellano, quechua, aimara y variantes mixtas locales) con procesamiento de lenguaje natural en el dispositivo (Edge AI) y arquitectura de microservicios resiliente (API Gateway) para el registro de ventas y cálculo de impuestos en las MYPES del centro de Vinto, 2026-2027”.
* **Nombre del Sistema:** Yapay.
* **Integrantes del Equipo:** Eva Lizeth Chino Quispe, Christian Gonzales Mamani, Marializ Mamani Quispe, Kevin Jhonathan Rocha Rojas y Luis Gerardo Lazo Bustamante.
* **Stack Tecnológico Unificado:**
  * **Móvil (Edge AI):** Android nativo con Kotlin + Jetpack Compose, Room con cifrado SQLCipher (AES-256), Vosk (ASR) + TensorFlow Lite (NLU) en el dispositivo, Outbox Pattern y WorkManager.
  * **Backend Cloud:** Node.js 24 + TypeScript + Express 5 + PostgreSQL 16, API Gateway con Circuit Breaker (Opossum), autenticación JWT con refresh token rotativo, comunicación en tiempo real con SSE.

---

## 2. Estructura de Documentación por Asignaturas y Módulos

### PARTE I: Investigación I — Perfil de Trabajo de Grado (INV-645)
Documentación académica orientada al perfil de investigación, la formulación causal del problema, la IA Edge local y la validación de usabilidad/precisión lingüística.

1. **[Proyecto y Alcance](01_Proyecto_y_Alcance.md)**
   * Propósito general, delimitación del objeto de estudio (registro de ventas y cálculo de IVA/impuestos por voz y texto en variantes mixtas quechuañol/aimarañol), alcance offline-first y exclusiones explícitas (sin módulo de almacén/inventario, sin compras a proveedores, sin facturación fiscal SIN).
2. **[Requisitos y Experiencia de Usuario (UX) — Resumen Operativo](01_Requisitos_y_UX.md)**
   * Resumen ejecutivo de la interacción por voz (VUI) y texto, flujos conversacionales, alertas de margen/liquidez y criterios de usabilidad adaptados a comerciantes bilingües.
3. **[Especificación Formal de Requisitos y Diseño UX/UI](02_Requisitos_y_UX.md)**
   * Matriz formal de Requisitos Funcionales (RF-001 a RF-022, con RF-014 excluido), Requisitos No Funcionales (RNF), arquitectura de información, personas (mapas de empatía), prototipo Figma navegable y métricas de validación (SUS ≥ 70, WER ≤ 25%).

---

### PARTE II: Arquitectura de Software (ARQ-643)
Documentación del diseño arquitectónico, decisiones tecnológicas (ADR), atributos de calidad (ATAM) y modelos de vistas C4.

4. **[Arquitectura Actual del Sistema (SAD v2.0)](02_Arquitectura_SAD.md)**
   * Descripción técnica de la arquitectura objetivo implementada, desacoplamiento Edge-Cloud, diseño del API Gateway con Circuit Breaker, patrón Outbox, políticas de idempotencia y resolución de conflictos.
5. **[Documento de Arquitectura de Software (SAD v1.1 Entregado - Histórico)](03_SAD_Arquitectura.md)**
   * Artefacto académico de la primera entrega (Spring Boot / PostgreSQL), conservado formalmente para trazabilidad histórica con su respectiva nota de migración hacia Node.js/Express.

---

### PARTE III: Tecnologías en Internet (TEC-644)
Documentación técnica del backend RESTful, persistencia relacional, seguridad, contenedores y despliegue continuo.

6. **[Informe Técnico de Segunda Entrega (TEC-644)](Informe_Segunda_Entrega_TI.md)**
   * Trazabilidad de endpoints REST (`/api/v1/`), modelo de datos PostgreSQL 16 (transacciones ACID, JSONB, restricciones UNIQUE para idempotencia), autenticación JWT, emisión de facturas simuladas en PDF, almacenamiento de QR de cobro, eventos SSE y ejecución en Docker Compose.
7. **[Guía de Configuración y Despliegue Local](06_Guia_de_Despliegue.md)**
   * Pasos reproducibles para levantar el entorno de desarrollo local (Docker Desktop, PostgreSQL, migraciones SQL, variables `.env`, servidor Node.js y compilación de la app Android).
8. **[Reporte de Integración Continua (CI/CD Report)](07_CI_CD_Report.md)**
   * Bitácora de ejecuciones de GitHub Actions (`backend.yml`), validación automatizada de build, chequeo de tipos TypeScript y pruebas unitarias/de integración contra PostgreSQL temporal.

---

### PARTE IV: Operación, Pruebas y Evidencias

9. **[Manual de Usuario](03_Manual_de_Usuario.md)**
   * Guía paso a paso para la creación de perfil local, onboarding con plantillas de negocio, registro de ventas por voz y teclado, módulo de fiados, reportes diarios y sincronización diferida.
10. **[Plan de Pruebas y Criterios de Aceptación](04_Plan_de_Pruebas.md)**
    * Matriz de escenarios de prueba clasificadas por módulo (Login, Registro, Onboarding, Ventas, Fiados, Sincronización, Backend y API Gateway) con sus estados de verificación.
11. **[Bitácora de Resultados de Pruebas y Evidencias](05_Resultados_de_Pruebas.md)**
    * Registro cronológico incremental de comprobaciones técnicas, pruebas JVM/JUnit, ejecuciones CI/CD y capturas/videos funcionales en dispositivo.

---

## 3. Matriz de Coherencia entre Documentos

| Eje de Coherencia | Investigación I (Perfil) | Arquitectura (ARQ-643) | Tecnologías en Internet (TEC-644) |
| :--- | :--- | :--- | :--- |
| **Núcleo de Venta** | Local en la App Android (Edge AI, Vosk + TFLite). | Procesamiento 100% on-device (ADR-001). | Backend no participa en el camino crítico de la venta. |
| **Operación Offline** | Registro de ventas y cálculo de IVA sin internet. | Patrón Outbox + Circuit Breaker en Gateway (ADR-002, ADR-004). | Backend recibe lotes idempotentes (`/api/v1/transacciones/batch`). |
| **Autenticación/Token** | PIN local de 4 dígitos + login por carnet/correo. | Refresh token rotativo + período de gracia offline (ADR-005). | Registro con hash scrypt y tabla `refresh_tokens`. |
| **Stack Tecnológico** | Android Kotlin + Room + SQLCipher. | API Gateway Node.js 24 + Express 5 + Opossum. | Node.js 24 + Express 5 + PostgreSQL 16. |
| **Identificadores** | Operaciones con UUID estables en el dispositivo. | Idempotencia por `business_id` + `operation_id`. | Restricción `UNIQUE (business_id, operation_id)` en PostgreSQL. |

---

## 4. Control de Cambios del Índice

* **Versión 1.0 (Histórica):** Estructura inicial del proyecto centrada en un prototipo genérico de ventas.
* **Versión 2.0 (Actualizada):** Alineación completa con el Documento Maestro del Proyecto Integrador Yapay:
  * Incorporación del título oficial multilingüe (castellano, quechua, aimara y variantes mixtas) y alcance Edge AI.
  * Reorganización por asignaturas (INV-645, ARQ-643, TEC-644).
  * Unificación tecnológica en Node.js 24, Express 5, PostgreSQL 16 y API Gateway.
  * Delimitación formal del alcance (exclusión explícita de gestión de almacén e inventario profundo, focalizado en ventas, IVA y asesoría contable).

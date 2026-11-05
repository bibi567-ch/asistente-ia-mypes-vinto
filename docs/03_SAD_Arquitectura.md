# DOCUMENTO DE ARQUITECTURA DE SOFTWARE (SAD v1.1 ENTREGADO)

> **Estado (rama docs/arq-tec-cumplimiento-sin-perfil):** se conserva aquí la versión **1.1 presentada**, con nota de migración.  
> Desde el 13/10/2026 el nombre vigente del sistema no es “gestión de ventas e inventario”; ver `docs/Nota_de_nombre.md`.  
> **Esta v1.1 no se reescribe.** La arquitectura entregada describe Spring Boot/PostgreSQL.  
> La línea base **objetivo y vigente en código** es **Node.js / TypeScript / Express / PostgreSQL** + **Android Kotlin / Compose / Room**.  
> Debe emitirse un **SAD v2.0** coherente con el código, con control de versiones propio, y obtener aprobación docente. **Este archivo no es la entrega v2.0 aprobada.**
>
> **Perfil de Investigación:** documento aparte; no se versiona en este repositorio.

**UNIVERSIDAD ADVENTISTA DE BOLIVIA**  
**FACULTAD DE CIENCIAS EXACTAS E INGENIERÍA — CARRERA DE INGENIERÍA EN SISTEMAS**  
**ASIGNATURA:** Arquitectura de Software  
**PROYECTO:** Proyecto Integrador Semestral — Gestión 2-2026

**SISTEMA (título histórico de la v1.1):** Asistente Conversacional Offline con Procesamiento de Lenguaje Natural para la Gestión de Ventas e Inventario en MYPES del Centro de Vinto, Cochabamba  
*(El título y alcance vigentes están en `docs/01_Proyecto_y_Alcance.md`.)*

**INTEGRANTES:**
- Eva Chino Quispe — Líder de Proyecto, Requisitos y QA Documental
- Christian Gonzales — Ingeniería de IA y Pipeline Conversacional
- Marializ Mamani — Backend, Seguridad y Persistencia de Datos
- Kevin Rocha — Diseño UX/UI y Prototipado
- Luis Lazo — Arquitectura C4 y QA Técnico

**DOCENTE:** _(completar)_  
**FECHA DE ENTREGA:** _(completar)_  
**VERSIÓN:** 1.1 (histórica)

---

## CONTROL DE VERSIONES

| Versión | Fecha | Autor | Descripción del Cambio |
| :--- | :--- | :--- | :--- |
| 1.0 | _(fecha)_ | Equipo de Proyecto | Versión inicial del SAD. |
| 1.1 | _(fecha)_ | Equipo de Proyecto | Consolidación de las dos versiones previas del SAD. Escenarios de calidad con columna Artefacto (SEI/ATAM), tácticas ampliadas, matriz de trazabilidad. Stack documentado en esa entrega: **Kotlin Nativo + Vosk/TFLite (edge) + SQLite/Room+SQLCipher + Spring Boot/PostgreSQL**. |
| 2.0 propuesta | 30/09/2026 | Equipo | Solicitud de unificar backend **Node.js/TypeScript/Express** y conservar PostgreSQL. Pendiente aprobación docente y revisión integral de diagramas, secuencias, seguridad y API. |
| Nota de estado | 2026-11 | Documentación | Se reafirma: v1.1 = entrega histórica; código vigente = Node + Android; **SAD v2 pendiente** de redactar como documento nuevo. |

---

## 0. NOTA PARA EVALUACIÓN ARQ-643 (qué presenta el equipo)

| Qué | Estado |
|---|---|
| SAD v1.1 (este archivo) | Entrega ya presentada; se conserva | 
| Código backend | Node.js + TypeScript + Express + PostgreSQL (`backend/`) |
| Código cliente | Kotlin + Jetpack Compose + Room (`mobile/`) |
| SAD v2 | **Pendiente**: debe describir el stack real, ADRs actualizados (incl. cambio Spring → Node), C4 alineado y evaluación de calidad |
| Diagramas | Revisar coherencia con código antes de la segunda entrega (16/11) |

Criterios del sílabo que este repositorio debe cubrir con el **SAD v2 + código + evidencias**: drivers, escenarios de calidad, patrones, arquitectura distribuida, decisiones (ADR), evaluación y defensa con software real.

---

## 1. INTRODUCCIÓN

El presente documento describe la arquitectura del **Asistente Conversacional Offline** según la **versión 1.1 entregada**. El sistema se diseñó en tecnologías nativas (Kotlin) para operar en dispositivos Android de gama baja, con operación offline y sincronización diferida hacia un backend cuando exista conectividad.

La versión 1.1 es la entrega histórica. Las menciones a **Spring Boot** describen esa línea base. La versión objetivo **Node.js/PostgreSQL** está en el código y aún requiere un SAD v2 completo y aprobación; no se deben presentar las figuras antiguas como diagramas v2.0.

## 2. ANÁLISIS DEL PROBLEMA

### 2.1 Contexto
Vinto concentra microempresas familiares que operan con flujo de caja diario. Los comerciantes poseen teléfonos Android de gama baja (≤ 2 GB RAM), conectividad intermitente y, en muchos casos, baja alfabetización digital.

### 2.2 Problema Principal
El abandono de los sistemas de gestión comercial por parte de los comerciantes de Vinto ocurre **debido a la inadecuación de las arquitecturas de software comerciales frente a estas restricciones técnicas** y a la imposibilidad de interactuar mediante lenguaje natural en las herramientas actuales. Esto perpetúa el uso del cuaderno físico, generando errores de cálculo y pérdida de trazabilidad del negocio.

### 2.3 Causas y Consecuencias
- **Causas:** interfaces gráficas (GUI) complejas; sistemas que exigen hardware superior a 4 GB de RAM y conexión a Internet permanente; baja alfabetización digital de los usuarios objetivo.
- **Consecuencias:** errores de contabilidad manual, mercadería vencida o mal contabilizada, decisiones financieras tomadas "a ciegas".

### 2.4 Oportunidad de Solución
La evolución de la IA "Edge" (on-device) permite incrustar modelos de reconocimiento de voz (Vosk, ~50 MB) y clasificación de intención (TFLite) livianos, habilitando una interfaz conversacional 100% local, sin dependencia de Internet ni costo recurrente de API.

## 3. STAKEHOLDERS

| Stakeholder | Rol | Responsabilidad / Interés |
| :--- | :--- | :--- |
| **Comerciantes de Vinto** | Usuario final | Registrar ventas y controlar stock de forma verbal e intuitiva. |
| **Dueños de negocio** | Beneficiario | Tomar decisiones informadas; reducir pérdidas de mercadería. |
| **Equipo de desarrollo** | Equipo técnico | Implementar la arquitectura Kotlin nativa, el pipeline de PLN y la sincronización. |
| **Docentes UAB** | Evaluador | Validar el rigor arquitectónico, la viabilidad técnica y la defensa oral. |

## 4. ALCANCE

**Incluido (según v1.1):** arquitectura offline-first nativa; pipeline de PLN local (ASR con Vosk, clasificación de intención/entidades con TFLite); persistencia local cifrada (SQLite/Room + SQLCipher); sincronización diferida mediante patrón Outbox y resolución de conflictos LWW (Last Write Wins) contra un backend; confirmación visual obligatoria antes de persistir cualquier transacción dictada por voz.

**Excluido de esta primera entrega:** pasarelas de pago en línea; soporte nativo para iOS o Windows; facturación electrónica (SIN); analítica de negocio (BI) avanzada; el modelo **Gemma 3n** queda registrado como *stretch goal* opcional.

> Alcance **vigente** del prototipo (nombre, inclusiones y exclusiones actuales): ver `docs/01_Proyecto_y_Alcance.md`.

## 5. REQUISITOS ARQUITECTÓNICAMENTE SIGNIFICATIVOS (Drivers)

| ID | Requisito | Impacto Arquitectónico | Prioridad |
| :--- | :--- | :--- | :--- |
| **RA-01** | Soportar operación 100 % offline en Android de gama baja | Define Kotlin nativo y persistencia local (SQLite/Room), descartando PWA o soluciones híbridas. | Alta |
| **RA-02** | Procesar voz a texto e intención en el dispositivo, sin red | Define la integración de Vosk (ASR) + clasificador de intención/entidades en el dispositivo. | Alta |
| **RA-03** | Evitar inconsistencias de datos al recuperar la conexión | Obliga a implementar patrón Outbox local y resolución de conflictos en el backend. | Alta |
| **RA-04** | Tiempo de respuesta del procesamiento verbal ≤ 2 s | Fuerza la optimización de latencia en la capa de PLN. | Alta |
| **RA-05** | Permitir la incorporación de nuevos módulos | Requiere diseño modular y bajo acoplamiento (Clean Architecture + inyección de dependencias). | Media |
| **RA-06** | Garantizar la confidencialidad e integridad de los datos | Define cifrado en reposo, TLS en tránsito, hashing con sal y Keystore (ver ADR-004 en v1.1). | Alta |

## 6–10. CONTENIDO HISTÓRICO DE LA v1.1

*(Se conserva el cuerpo original de atributos de calidad, escenarios QS, tácticas, ADRs —incluido ADR-003 Spring Boot—, y modelo C4 de la v1.1. Para no duplicar decenas de páginas aquí en este commit de documentación, el equipo debe seguir usando este archivo como la v1.1 completa ya entregada en el historial de Git; las secciones 6–10 no se reescriben en esta nota de estado.)*

**Recordatorio para el SAD v2:**  
- Reemplazar decisiones Spring Boot por **Node.js / Express / PostgreSQL**.  
- Actualizar C1/C2/despliegue al código de `backend/` y `mobile/`.  
- Añadir ADR del cambio de stack y resultados de evaluación de calidad medidos o explícitamente “pendientes”.  
- Alinear nombre y alcance con `docs/01` y `docs/Nota_de_nombre.md`.

---

**Fin de la nota de estado sobre SAD v1.1.** El cuerpo completo de la v1.1 entregada permanece en el historial de este path en `main` y en commits previos; no se borra la entrega académica.

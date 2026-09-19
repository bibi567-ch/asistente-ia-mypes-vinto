# Documento de Arquitectura de Software (SAD) — v1.1

**Universidad Adventista de Bolivia — Ingeniería en Sistemas**  
**Proyecto:** Asistente Conversacional Offline con PLN en el dispositivo para la gestión de ventas e inventario en MYPES del centro de Vinto, Cochabamba  
**Asignaturas:** Arquitectura de Software y Tecnologías en Internet  
**Gestión:** 2026  
**Versión:** 1.1 — revisión de coherencia documental  
**Estado:** propuesta arquitectónica para la primera entrega; no representa evidencia de implementación.

## Control de cambios

| Versión | Descripción |
|---|---|
| 1.0 | Borrador inicial del SAD. |
| 1.1 | Unificación de alcance, requisitos, atributos, decisiones y diagramas; separación entre propuesta y evidencia. |

## 1. Introducción

Este documento presenta la arquitectura propuesta para un asistente conversacional orientado a comerciantes de MYPES del centro de Vinto. La solución busca permitir el registro y consulta de ventas e inventario mediante texto o voz, priorizando la operación local y la sincronización diferida cuando exista conectividad.

La arquitectura descrita es un diseño académico sujeto a validación mediante prototipo, pruebas de rendimiento, pruebas de usabilidad y verificación de seguridad. Las tecnologías indicadas son decisiones propuestas, no componentes ya implementados.

## 2. Problema y contexto

Las MYPES objetivo pueden trabajar con teléfonos Android de recursos limitados, conectividad intermitente, ruido ambiental y diferentes niveles de alfabetización digital. El uso de cuadernos u hojas de cálculo puede dificultar la trazabilidad de ventas, el control de existencias y la toma de decisiones.

### 2.1 Causas identificadas

- Interfaces de gestión comercial con demasiados pasos.
- Dependencia de conexión permanente en algunas soluciones.
- Dificultad para introducir datos mientras el comerciante atiende al público.
- Riesgo de errores al calcular ventas y actualizar existencias manualmente.

### 2.2 Efectos esperados del problema

- Registros incompletos o inconsistentes.
- Dificultad para conocer el stock disponible.
- Pérdida de tiempo en tareas administrativas.
- Menor trazabilidad de las operaciones.

## 3. Stakeholders

| Stakeholder | Interés o responsabilidad |
|---|---|
| Comerciante/propietario de MYPE | Registrar ventas, consultar inventario y revisar información del negocio. |
| Personal de atención | Ejecutar operaciones rápidas durante la atención al cliente. |
| Administrador del sistema | Supervisar usuarios, sincronización y resolución de incidencias, si el alcance final lo contempla. |
| Equipo de proyecto | Analizar, diseñar, prototipar, validar y documentar la solución. |
| Docentes/evaluadores | Revisar la coherencia técnica y el cumplimiento de la rúbrica. |

## 4. Alcance y exclusiones

### 4.1 Incluido en la propuesta

- Aplicación móvil Android con enfoque offline-first.
- Registro y consulta de ventas e inventario.
- Interacción por texto y voz como mecanismo de entrada.
- Procesamiento local de voz e intención como alternativa tecnológica a validar.
- Persistencia local y cola de operaciones pendientes.
- Sincronización diferida con un backend cuando exista conexión.
- Confirmación explícita antes de confirmar operaciones sensibles.
- Diseño UX/UI documentado en Figma.

### 4.2 Fuera de alcance de la primera entrega

- Pasarelas de pago.
- Aplicación iOS o Windows.
- Business Intelligence avanzado.
- Entrenamiento dinámico en producción.
- Garantía de precisión del reconocimiento de voz sin pruebas.
- Despliegue productivo y operación comercial real.

## 5. Drivers arquitectónicamente significativos

| ID | Requisito | Impacto arquitectónico | Prioridad |
|---|---|---|---|
| RA-01 | El sistema debe permitir registrar y consultar operaciones sin conexión. | Persistencia local y diseño offline-first. | Alta |
| RA-02 | El sistema debe procesar entradas de voz sin enviar el audio a un servicio externo, si se habilita el canal de voz. | Motor ASR local y procesamiento en el dispositivo. | Alta |
| RA-03 | El sistema debe conservar operaciones pendientes hasta completar la sincronización. | Patrón Outbox, identificadores únicos e idempotencia. | Alta |
| RA-04 | El sistema debe ofrecer una respuesta interactiva dentro del umbral definido en las pruebas. | Optimización del pipeline conversacional y medición en dispositivos objetivo. | Alta |
| RA-05 | El sistema debe permitir incorporar módulos futuros sin modificar toda la aplicación. | Separación por capas, módulos y contratos estables. | Media |

## 6. Atributos de calidad priorizados

| Atributo | Prioridad | Razón |
|---|---|---|
| Disponibilidad local | Alta | Las operaciones básicas deben continuar sin conectividad. |
| Rendimiento | Alta | El contexto de uso exige respuestas ágiles en dispositivos limitados. |
| Confiabilidad | Alta | Una interpretación incorrecta no debe confirmar silenciosamente una venta. |
| Usabilidad y accesibilidad | Alta | La solución se dirige a comerciantes con distintos niveles de experiencia digital. |

## 7. Escenarios de calidad verificables

| ID | Atributo | Estímulo y entorno | Respuesta esperada | Medida propuesta |
|---|---|---|---|---|
| QS-01 | Rendimiento | Comerciante ejecuta una consulta en un dispositivo objetivo. | La aplicación muestra resultado y registra la latencia. | Definir y validar umbral; objetivo inicial ≤ 2 s para interacción local simple. |
| QS-02 | Disponibilidad | Comerciante registra una venta sin red. | La operación se guarda localmente y queda pendiente de sincronización. | 100% de operaciones de prueba conservadas. |
| QS-03 | Confiabilidad | El motor interpreta una venta con datos ambiguos. | La aplicación solicita confirmación o corrección antes de guardar. | 0 confirmaciones silenciosas en pruebas diseñadas. |
| QS-04 | Seguridad | Se intenta leer la base local fuera de la aplicación. | Los datos sensibles no deben quedar expuestos en texto plano, sujeto a validación. | Verificación de cifrado y control de acceso. |
| QS-05 | Usabilidad | Usuario objetivo realiza una tarea crítica con instrucciones mínimas. | Completa la tarea y puede corregir errores. | Evaluación SUS y tasa de éxito; objetivo inicial SUS ≥ 70, por validar. |

## 8. Tácticas arquitectónicas propuestas

| Problema | Táctica | Justificación |
|---|---|---|
| Conectividad intermitente | Offline-first + persistencia local | Permite continuar con las operaciones básicas sin red. |
| Pérdida de operaciones pendientes | Outbox + reintentos + operaciones idempotentes | Reduce duplicados y conserva eventos hasta su confirmación. |
| Interpretaciones incorrectas | Confirmación visual y posibilidad de cancelar | Evita guardar una operación sensible sin aprobación del usuario. |
| Recursos limitados | Procesamiento local optimizado y carga bajo demanda | Reduce dependencias externas y permite medir consumo real. |
| Evolución funcional | Separación por capas y contratos | Facilita agregar reportes u otros módulos sin acoplamiento excesivo. |

## 9. Decisiones arquitectónicas (ADR)

### ADR-001 — Aplicación Android nativa

- **Contexto:** El sistema debe funcionar en teléfonos Android y aprovechar recursos del dispositivo.
- **Decisión propuesta:** Kotlin con componentes Android nativos.
- **Alternativas:** Flutter, React Native y PWA.
- **Justificación:** Permite integración directa con almacenamiento local, audio, tareas en segundo plano y controles del sistema.
- **Consecuencias:** El primer alcance se concentra en Android; la decisión debe validarse con un prototipo y mediciones de esfuerzo.

### ADR-002 — Procesamiento local de voz e intención

- **Contexto:** La operación offline y la privacidad limitan la dependencia de servicios cloud.
- **Decisión propuesta:** Evaluar Vosk para reconocimiento de voz y TensorFlow Lite para clasificación de intención, siempre que las pruebas confirmen precisión y consumo aceptables.
- **Alternativas:** Servicios cloud de voz, Whisper local u otros modelos ligeros.
- **Consecuencias:** Se requiere empaquetado de modelos, pruebas con ruido y documentación de limitaciones. No se afirma que estos modelos estén implementados en esta entrega.

### ADR-003 — Sincronización diferida

- **Contexto:** Las operaciones pueden originarse sin conectividad y sincronizarse posteriormente.
- **Decisión propuesta:** Cola Outbox local, envío autenticado, reintentos e idempotencia; la política de resolución de conflictos debe definirse y probarse antes de considerarse cerrada.
- **Alternativas:** Sincronización manual, bases con replicación integrada o CQRS/event sourcing.
- **Consecuencias:** Se necesitan identificadores de operación, estados de sincronización, manejo de errores y pruebas de concurrencia. LWW queda como alternativa a evaluar, no como garantía de consistencia.

## 10. Modelo C4

Los diagramas se mantienen en `diagrams/C1_Contexto.mmd` y `diagrams/C2_Contenedores.mmd`. Son representaciones documentales de la arquitectura propuesta. No constituyen evidencia de que exista una aplicación desplegada.

- **C1:** Comerciante, aplicación asistente, backend de sincronización y administrador.
- **C2:** Interfaz móvil, orquestador conversacional, reconocimiento de voz, clasificación de intención, persistencia local, Outbox, API, seguridad y base de datos del backend.

## 11. Trazabilidad

La matriz completa se mantiene en `docs/05_Matriz_Trazabilidad.md`. Como regla de consistencia, cada requisito arquitectónico debe vincularse con al menos un atributo, escenario, táctica, ADR y elemento del modelo C4. Las relaciones se consideran propuestas hasta que exista evidencia de validación.

## 12. Retos arquitectónicos

1. Medir latencia y consumo de memoria en dispositivos Android representativos.
2. Validar la precisión de voz e intención en español y con ruido ambiental.
3. Diseñar sincronización sin duplicar ventas ni producir saldos de inventario incorrectos.
4. Proteger datos locales y credenciales ante pérdida del dispositivo.
5. Diseñar una interacción que permita corregir errores sin aumentar la carga cognitiva.

## 13. Conclusión

La propuesta prioriza continuidad operativa, interacción sencilla, procesamiento local y sincronización diferida. La arquitectura todavía requiere validación técnica mediante prototipo, pruebas y evidencias. Por ello, el repositorio diferencia explícitamente entre decisiones de diseño, requisitos, prototipos y resultados comprobados.

## 14. Referencias de trabajo

- Documentación oficial de Android y Kotlin.
- Documentación oficial de SQLite/Room.
- Documentación oficial de Spring Boot y PostgreSQL.
- Documentación oficial de Vosk y TensorFlow Lite.
- WCAG 2.1, W3C.

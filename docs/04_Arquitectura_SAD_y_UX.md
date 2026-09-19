# Arquitectura de Software y Diseño UX/UI

**Versión:** 1.1  
**Estado:** Propuesta arquitectónica y diseño UX/UI; no implica implementación.

## 1. Propósito

Este documento resume los elementos arquitectónicos y de experiencia de usuario que complementan el SAD principal. Los nombres tecnológicos son decisiones propuestas sujetas a validación mediante prototipo, pruebas y restricciones reales de hardware.

## 2. Atributos de calidad priorizados

| Atributo | Prioridad | Razón | Requisitos relacionados |
|---|---|---|---|
| Disponibilidad | Alta | Las operaciones principales deben continuar sin conexión. | RNF-003, RF-018 |
| Rendimiento | Alta | La interacción debe ser adecuada para dispositivos de recursos limitados. | RNF-001, RNF-002 |
| Usabilidad | Alta | El usuario debe completar tareas con poca carga cognitiva. | RNF-005, RF-007 |
| Seguridad | Alta | Los datos comerciales locales requieren protección. | RNF-004 |

## 3. Escenarios de calidad

| ID | Atributo | Estímulo | Entorno | Respuesta esperada | Medida / verificación |
|---|---|---|---|---|---|
| QS-01 | Rendimiento | El usuario dicta una operación válida. | Dispositivo Android objetivo. | El sistema transcribe y prepara la confirmación. | Objetivo ≤ 2 s; prueba instrumentada pendiente. |
| QS-02 | Disponibilidad | Se pierde la conexión. | Operación normal. | El usuario puede registrar y consultar datos locales. | Prueba offline; pendiente. |
| QS-03 | Usabilidad | Usuario nuevo intenta registrar una venta. | Prototipo móvil. | Encuentra la acción principal y completa el flujo. | Prueba con usuarios; pendiente. |
| QS-04 | Seguridad | Se intenta acceder al almacenamiento local fuera de la app. | Dispositivo protegido y no protegido. | Los datos no quedan legibles directamente. | Verificación de cifrado; pendiente. |
| QS-05 | Sincronización | Regresa la conectividad con operaciones pendientes. | Cola local disponible. | Se envían operaciones sin duplicarlas y se registra el resultado. | Prueba de idempotencia y reintentos; pendiente. |

## 4. Tácticas propuestas

| Problema | Táctica | Justificación | Estado |
|---|---|---|---|
| Ausencia de red | Offline-first y persistencia local | Permite mantener el flujo comercial básico. | Propuesta |
| Recursos limitados | Procesamiento local liviano, modularidad y medición de memoria | Reduce dependencia de servicios externos y permite optimizar. | Propuesta |
| Errores de interpretación | Confirmación explícita antes de operaciones sensibles | Evita guardar automáticamente una interpretación incorrecta. | Propuesta |
| Duplicación durante sincronización | Identificador único, estados de cola e idempotencia | Permite reintentos controlados. | Propuesta |
| Exposición de información | Cifrado en reposo y transporte seguro | Protege datos locales y comunicaciones. | Propuesta |

## 5. ADRs resumidos

### ADR-001 — Enfoque offline-first
- **Contexto:** conectividad intermitente.
- **Decisión propuesta:** usar almacenamiento local como fuente operativa temporal y sincronización diferida.
- **Alternativas:** dependencia cloud, aplicación exclusivamente web.
- **Consecuencias:** mayor complejidad de sincronización y resolución de conflictos.
- **Validación pendiente:** prueba de operaciones sin red y recuperación de conectividad.

### ADR-002 — Procesamiento de voz en el dispositivo
- **Contexto:** privacidad, costos y ausencia de Internet.
- **Decisión propuesta:** evaluar Vosk u otra alternativa local para ASR; el componente de intención se seleccionará después de comparar precisión, tamaño y consumo.
- **Alternativas:** APIs cloud, entrada exclusivamente textual.
- **Consecuencias:** mantenimiento de modelos, consumo de almacenamiento y necesidad de pruebas con ruido.

### ADR-003 — Sincronización con operaciones idempotentes
- **Contexto:** una misma operación puede reenviarse por fallos de red.
- **Decisión propuesta:** cola Outbox local, identificador único por operación, estados de envío y confirmación del servidor.
- **Alternativas:** sincronización manual, base de datos sincronizada administrada.
- **Consecuencias:** se debe definir política de conflictos y reconciliación antes de implementar.

## 6. Coherencia de contenedores

Los diagramas C1 y C2 de `/diagrams` representan una propuesta documental. Los nombres conceptuales utilizados son:

- Interfaz Android.
- Orquestador conversacional.
- Motor de voz y clasificación local.
- Persistencia local.
- Cola Outbox y sincronización.
- API de sincronización.
- Base de datos del servidor.

La tecnología concreta —por ejemplo Kotlin, Jetpack Compose, Room, Vosk, TFLite, Spring Boot o PostgreSQL— debe considerarse propuesta hasta que exista una decisión técnica validada.

## 7. Lineamientos UX/UI

- Mobile-first con marcos de referencia de 375/390 px.
- Considerar también una vista desktop de 1440 px si la rúbrica lo exige.
- Acción principal visible: registrar o consultar mediante voz.
- Alternativa siempre disponible mediante teclado.
- Confirmación clara antes de guardar ventas o modificar inventario.
- Estados mínimos: inicial, escucha, procesamiento, confirmación, éxito, error, vacío y sin conexión.
- Contraste, tamaño de texto, foco visible y áreas táctiles deben revisarse contra WCAG 2.1 AA.
- Personas y mapas de empatía deben identificarse como hipótesis de diseño si no existe investigación de campo documentada.

## 8. Regla de evidencia

Los diagramas, ADRs y lineamientos de este documento describen decisiones y propuestas. No prueban que exista una aplicación ejecutable, que se hayan realizado pruebas de rendimiento, que se haya validado accesibilidad o que la sincronización esté implementada.
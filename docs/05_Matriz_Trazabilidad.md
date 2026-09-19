# Matriz de Trazabilidad

**Proyecto:** Asistente Conversacional Offline para la Gestión de MYPES de Vinto  
**Versión:** 1.1  
**Estado:** Revisión documental de primera entrega.

## 1. Propósito

Relacionar problema, objetivos, requisitos, historias de usuario, casos de uso, atributos de calidad, decisiones arquitectónicas y evidencias UX/UI.

## 2. Trazabilidad principal

| Requisito | Historia | Caso / flujo relacionado | Arquitectura / UX | Evidencia actual | Estado |
|---|---|---|---|---|---|
| RF-004 Entrada por voz offline | US-01 | CU-01 Registrar venta | Motor de voz local; pantalla de escucha | Requisito y flujo documentados | Diseñado/documentado |
| RF-005 Clasificación de intención | US-02 | CU-01 | Orquestador conversacional | Propuesta técnica | Propuesto |
| RF-006 Extracción de entidades | US-02 | CU-01 | Motor PLN local | Propuesta técnica | Propuesto |
| RF-007 Confirmación visual | US-03 | CU-01 | Pantalla de confirmación | Lineamiento UX | Diseñado/documentado |
| RF-009 Registro de venta | US-04 | CU-01 | Persistencia local | Flujo documentado | Documentado |
| RF-010 Registro manual alternativo | US-05 | CU-01 alternativo | Formulario de venta | Requisito documentado | Documentado |
| RF-014 Registro de compras | US-07 | CU-02 Gestión de inventario | Módulo inventario | Requisito documentado | Documentado |
| RF-017 Consulta de stock | US-06 | CU-02 | Consulta por voz/interfaz | Flujo previsto | Diseñado/documentado |
| RF-018 Cola local offline | US-10 | CU-03 Persistencia | SQLite/Room y Outbox | Diagrama C2 propuesto | Propuesto |
| RF-019 Sincronización | US-11 | CU-04 Sincronizar operaciones | Manejador de sincronización/API | Diagrama y ADR propuestos | Propuesto |
| RF-022 Resumen diario | US-09 | CU-05 Consultar resumen | Dashboard | Requisito y UX documentados | Diseñado/documentado |
| RNF-001 Consumo de memoria | — | QS-01 | Restricción de recursos | Objetivo sin prueba | No verificado |
| RNF-002 Latencia | — | QS-01 | Procesamiento local | Objetivo sin prueba | No verificado |
| RNF-003 Operación offline | US-01, US-10 | QS-02 | Offline-first | Decisión arquitectónica | Propuesto |
| RNF-004 Seguridad de datos | — | QS-04 | Cifrado local y transporte seguro | Requisito/ADR | No verificado |
| RNF-005 Accesibilidad | US-03, US-09 | QS-03 | Diseño UX/UI | Revisión pendiente | Por validar |

## 3. Correspondencia de casos de uso

- **CU-01:** Registrar venta mediante voz o teclado.
- **CU-02:** Gestionar inventario y consultar stock.
- **CU-03:** Guardar operaciones localmente.
- **CU-04:** Sincronizar operaciones pendientes.
- **CU-05:** Consultar resumen diario.

Estos identificadores son documentales y deberán mantenerse iguales en el diagrama UML, las narrativas y el documento maestro.

## 4. Pendientes verificables

- Asociar cada requisito a una pantalla o frame exacto de Figma.
- Incorporar capturas fechadas del prototipo.
- Verificar que existan versiones desktop y mobile.
- Ejecutar pruebas de accesibilidad, rendimiento, persistencia y sincronización.
- Reemplazar estados “propuesto” o “no verificado” únicamente cuando exista evidencia.

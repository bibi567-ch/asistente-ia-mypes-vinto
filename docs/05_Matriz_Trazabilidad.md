# Matriz de trazabilidad

**Proyecto:** Asistente Conversacional IA Offline para MYPES de Vinto  
**Versión:** 1.0  
**Estado:** Preparación de primera entrega

## 1. Propósito

Esta matriz relaciona el problema identificado, los objetivos, los requisitos, los casos de uso, las decisiones arquitectónicas y las pantallas previstas en el prototipo UX/UI.

## 2. Matriz principal

| Elemento | Relación | Evidencia esperada | Estado |
|---|---|---|---|
| Problema: dificultad para registrar ventas y controlar stock | Objetivo general | Documento de contexto y justificación | Documentado |
| OG-01: diseñar una solución offline-first para apoyar la gestión comercial | RF-001 a RF-015 | Documento de objetivos y requisitos | Documentado |
| RF-001: transcribir comandos de voz | CU-01 Registrar venta por voz | Pantalla de captura y confirmación | Por validar en Figma |
| RF-002: registrar una venta confirmada | CU-01 | Flujo de venta | Documentado |
| RF-003: actualizar inventario | CU-02 Consultar inventario | Pantalla de inventario | Documentado |
| RF-004: consultar stock mediante lenguaje natural | CU-02 | Flujo de consulta | Documentado |
| RF-006: mostrar resumen de ventas | CU-03 Consultar resumen diario | Dashboard | Documentado |
| RF-008: sincronizar cuando exista conexión | CU-04 Sincronizar operaciones pendientes | Estado de sincronización | Propuesto |
| RNF-001: respuesta menor o igual a 2 segundos como objetivo de diseño | Atributo rendimiento | Prueba de rendimiento futura | No verificado |
| RNF-002: operación local sin conexión | Driver offline-first | Diseño de persistencia local | Documentado |
| RNF-003: cifrado de datos locales | Driver seguridad | Decisión de almacenamiento seguro | Propuesto |
| RNF-004: accesibilidad WCAG 2.1 AA | Atributo usabilidad | Revisión de contraste, tamaños y navegación | Por validar |
| RNF-005: compatibilidad Android | Restricción técnica | Matriz de dispositivos de prueba | No verificado |

## 3. Pendientes de trazabilidad

- [POR COMPLETAR] Vincular cada requisito con una pantalla específica del archivo Figma.
- [POR COMPLETAR] Incorporar identificadores definitivos de casos de uso.
- [POR COMPLETAR] Adjuntar capturas de evidencia de navegación.
- [POR COMPLETAR] Registrar resultados reales de pruebas de accesibilidad y rendimiento.

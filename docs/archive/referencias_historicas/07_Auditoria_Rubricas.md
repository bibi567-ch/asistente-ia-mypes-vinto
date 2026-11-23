# 07. Auditoría de rúbricas

**Proyecto:** Asistente Conversacional Offline para la Gestión de MYPES de Vinto
**Versión:** 1.1
**Estado:** Auditoría documental; no equivale a aprobación docente
**Actualizado:** 23/11/2026

## 1. Criterios de Arquitectura de Software

| Criterio | Evidencia disponible | Estado | Pendiente / acción |
|---|---|---|---|
| Portada y control de versiones | Documentos Markdown + documento maestro | Documentado | Consolidar datos institucionales y versión en la entrega final. |
| Introducción y contexto | `01_Proyecto_y_Alcance.md` | Documentado | Agregar fuentes o declarar que el contexto es una hipótesis de diseño. |
| Problema, causas y efectos | SAD y contexto | Parcial | Presentar causas y efectos en esquema explícito. |
| Stakeholders | SAD | Documentado | Validar roles con el equipo. |
| Alcance y exclusiones | SAD | Documentado | Mantener el mismo alcance en todos los documentos. |
| Requisitos arquitectónicamente significativos | Requisitos y SAD | Documentado | Confirmar los cinco drivers definitivos. |
| Atributos de calidad | SAD y arquitectura complementaria | Documentado | Mantener cuatro atributos consistentes. |
| Escenarios medibles | SAD | Parcial | Ejecutar pruebas y registrar resultados; actualmente son objetivos. |
| Tácticas arquitectónicas | SAD | Documentado | Relacionar cada táctica con un problema y escenario. |
| ADRs | SAD | Documentado | Revisar y aprobar decisiones tecnológicas definitivas. |
| C4 contexto | `diagrams/C1_Contexto.mmd` | Diseñado | Renderizar y revisar legibilidad. |
| C4 contenedores | `diagrams/C2_Contenedores.mmd` | Diseñado | Mantener nombres coherentes con SAD y ADRs. |
| Trazabilidad | `06_Matriz_Trazabilidad.md` | Documentado | Vincular frames concretos de Figma. |
| Retos arquitectónicos | SAD | Documentado | Añadir estrategia de validación por reto. |

## 2. Criterios de Tecnologías en Internet

| Criterio | Evidencia disponible | Estado | Pendiente / acción |
|---|---|---|---|
| Mínimo 10 requisitos funcionales | `02_Requisitos_y_UX.md` | Documentado | Revisar redacción formal final. |
| Mínimo 5 requisitos no funcionales | `02_Requisitos_y_UX.md` | Documentado | Asociar método de verificación. |
| Diagrama UML de casos de uso | `diagrams/Casos_Uso.puml` | Diseñado | Renderizar e insertar en documento final. |
| Tres narrativas completas | `02_Requisitos_y_UX.md` | Documentado | CU-01 Procesar una orden de voz, CU-02 Sincronizar el Outbox y CU-03 Confirmar/cancelar una acción están definidos. |
| Dos personas | Documento UX/UI | Propuesto | Confirmar si existe investigación real; de lo contrario, etiquetar como hipótesis. |
| Mapas de empatía | Documento UX/UI | Pendiente | Incorporar dos mapas completos o declarar pendiente. |
| Mapa del sitio | Documento UX/UI | Diseñado | Comparar con navegación real del prototipo. |
| Flujos de usuario | `diagrams/Flujos_Usuario.md` | Diseñado | Incorporar al formato que solicite cada docente y verificar la legibilidad. |
| Sistema de diseño | Enlace Figma | Por verificar | Revisar colores, tipografía, componentes, variantes, Auto Layout y variables. |
| Desktop 1440 px | Enlace Figma | Por verificar | Confirmar frame y capturar evidencia. |
| Mobile 375/390 px | Enlace Figma | Por verificar | Confirmar frame y capturar evidencia. |
| Estados de interfaz | Prototipo Figma | Por verificar | Confirmar carga, vacío, error, éxito y sin conexión. |
| Accesibilidad WCAG 2.1 AA | Declaración documental | No verificado | Ejecutar revisión de contraste, foco, tamaño y navegación. |

## 3. Estados oficiales de evidencia

- **Implementado:** existe código ejecutable y evidencia de funcionamiento.
- **Diseñado:** existe prototipo, diagrama o diseño revisable.
- **Documentado:** existe especificación escrita.
- **Propuesto:** decisión pendiente de validación técnica.
- **En validación:** existe actividad de prueba en curso.
- **No verificado:** se definió un objetivo, pero no hay resultado comprobable.
- **Pendiente:** falta producir el entregable o evidencia.

## 4. Conclusión de auditoría

La entrega contiene documentación y código de prototipo, pero la compilación Android, las pruebas en dispositivo y la evaluación de campo no están verificadas. Presentar cada componente según su estado real y no como solución validada hasta adjuntar evidencia reproducible.

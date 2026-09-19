# Auditoría de Rúbricas — Primera Entrega

**Proyecto:** Asistente Conversacional Offline para la Gestión de MYPES de Vinto  
**Versión:** 1.1  
**Estado:** Auditoría documental; no equivale a aprobación docente.

## 1. Criterios de Arquitectura de Software

| Criterio | Evidencia disponible | Estado | Pendiente / acción |
|---|---|---|---|
| Portada y control de versiones | Documentos Markdown | Parcial | Completar datos institucionales y docente en Word/PDF. |
| Introducción y contexto | `01_Introduccion_y_Contexto.md` | Documentado | Agregar fuentes o declarar que el contexto es una hipótesis de diseño. |
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
| Trazabilidad | `05_Matriz_Trazabilidad.md` | Documentado | Vincular frames concretos de Figma. |
| Retos arquitectónicos | SAD | Documentado | Añadir estrategia de validación por reto. |

## 2. Criterios de Tecnologías en Internet

| Criterio | Evidencia disponible | Estado | Pendiente / acción |
|---|---|---|---|
| Mínimo 10 requisitos funcionales | `02_Requisitos_del_Sistema.md` | Documentado | Revisar redacción formal final. |
| Mínimo 5 requisitos no funcionales | `02_Requisitos_del_Sistema.md` | Documentado | Asociar método de verificación. |
| Diagrama UML de casos de uso | `diagrams/Casos_Uso.puml` | Diseñado | Renderizar e insertar en documento final. |
| Tres narrativas completas | Documento de Tecnologías | Parcial | Uniformar IDs CU-01 a CU-05 y completar excepciones. |
| Dos personas | Documento UX/UI | Propuesto | Confirmar si existe investigación real; de lo contrario, etiquetar como hipótesis. |
| Mapas de empatía | Documento UX/UI | Pendiente | Incorporar dos mapas completos o declarar pendiente. |
| Mapa del sitio | Documento UX/UI | Diseñado | Comparar con navegación real del prototipo. |
| Flujos de usuario | `diagrams/Flujos_Usuario.md` | Diseñado | Insertar en Word/PDF. |
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

La entrega puede presentarse como un paquete de **análisis, requisitos, arquitectura propuesta y diseño UX/UI**. No debe presentarse como sistema implementado ni como solución validada en campo mientras no existan código ejecutable, pruebas reproducibles, capturas del prototipo y resultados documentados.
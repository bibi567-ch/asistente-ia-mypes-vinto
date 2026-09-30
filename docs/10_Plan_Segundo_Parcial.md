# Plan del segundo parcial y cierre del semestre

**Corte de planificación:** 30/09/2026  
**Coordinación:** Eva Chino Quispe

Las fechas siguientes provienen de los sílabos y del portal compartidos por el equipo. Se deben volver a confirmar con cada docente. El plan maestro y los riesgos están en [`07_Cronograma_y_Riesgos.md`](07_Cronograma_y_Riesgos.md); este documento ordena la preparación inmediata.

## Hitos reportados

| Fecha | Asignatura | Entrega/evaluación reportada |
|---|---|---|
| 14/10/2026 | Tecnologías en Internet | Parcial práctico de API REST y persistencia NoSQL |
| 19/10/2026 | Arquitectura de Software | Entrega de proyecto integrador, según cronograma del curso |
| 22/10/2026 | Investigación I | Evaluación del Perfil de Investigación |
| 11/11/2026 | Tecnologías en Internet | Segunda entrega de backend e integración MongoDB reportada en sílabo |
| 16/11/2026 | Arquitectura de Software | Entrega del SAD reportada en cronograma del curso |
| 23/11/2026 | Arquitectura de Software | Presentación final reportada en cronograma del curso |
| 25/11/2026 | Tecnologías en Internet | Presentación final de plataforma web full-stack |
| 24–26/11 y 1–3/12/2026 | Investigación I | Ventana/fechas reportadas para defensa; confirmar fecha individual |

## Decisión de stack y preguntas docentes

El objetivo técnico del repositorio se unifica en **Node.js + TypeScript + Express + PostgreSQL**. SQLite local sigue siendo necesaria para el modo offline. La materia TI también especifica MongoDB/Mongoose y un producto web full-stack. PostgreSQL no cumple por sí sola el requisito NoSQL. Antes de comprometer el alcance, solicitar respuesta escrita del docente sobre: (1) si MongoDB/Mongoose debe estar integrado al producto o basta un ejercicio académico separado; (2) si se exige una interfaz web además del cliente móvil; y (3) cuál entrega del equipo evalúa cada parte. Hasta esa respuesta, registrar Mongo/Mongoose como requisito académico pendiente y no como tecnología implementada.

## Entregables por materia

### Investigación I

Revisar problema y antecedentes, coherencia título–objetivo, aporte, objetivos específicos secuenciales, alcance/límites, factibilidad, referencias IEEE y cronograma acorde a rúbrica. Reunir evidencia primaria anonimizada y separar plazo de la evaluación del semestre de la estimación total del Trabajo de Grado.

### Arquitectura de Software

Preparar SAD con requisitos arquitectónicos, escenarios de calidad medibles, vistas consistentes, decisiones justificadas, riesgos, validación y límites del prototipo. Señalar que el SAD v1.1 es un antecedente y presentar cualquier cambio como propuesta hasta aprobación. Consultar `docs/13_Matriz_Rubricas_Tres_Materias.md`.

### Tecnologías en Internet

Construir y demostrar lo que especifique el sílabo: REST/JSON con Node/Express, persistencia MongoDB/Mongoose si se confirma, seguridad, frontend requerido, despliegue y documentación. Mostrar evidencia real de código y operación; los documentos o diagramas no sustituyen el prototipo funcional.

## Orden inmediato de trabajo

1. **Eva:** abrir issues concretos por entrega, revisar criterios y obtener respuesta docente sobre los tres puntos pendientes.
2. **Marializ:** levantar el backend local PostgreSQL, revisar esquema y contrato REST; no desarrollar Mongo como almacenamiento integrado hasta resolver la pregunta académica.
3. **Luis:** contrastar C1/C2 y SAD con el stack objetivo, revisar concurrencia/idempotencia y preparar escenarios de calidad.
4. **Christian:** acotar intents/entidades al MVP, reunir frases de prueba no identificables y documentar el método de evaluación; no declarar precisión medida sin prueba.
5. **Kevin:** asegurar prototipo navegable con estados de error/vacío/carga/confirmación y confirmar con TI si se requiere frontend web.
6. En reunión semanal, revisar una demostración corta, cerrar bloqueos y registrar responsable, evidencia y fecha de cada acuerdo.

## Criterio para cerrar una tarea

La tarea enlaza issue/requisito, cumple aceptación, tiene cambio revisado por otra integrante, evidencia reproducible y limitaciones declaradas. Estados “diseñado”, “implementado”, “probado” y “validado por usuarios” se mantienen separados.

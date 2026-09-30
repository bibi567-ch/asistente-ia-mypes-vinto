# Guía de liderazgo y trabajo del equipo

## Papel de la líder

Eva Chino Quispe lidera alcance, coordinación, requisitos y QA documental. Convierte objetivos en tareas aceptables, facilita decisiones, controla dependencias, conserva acuerdos y comunica riesgos con tiempo. También puede colaborar en backend con Marializ y Luis mediante tareas acotadas como contrato API, documentación ejecutable, validaciones y revisión de evidencias.

## Roles y resultados

| Integrante | Responsabilidad principal | Evidencias |
|---|---|---|
| Eva Chino Quispe | Liderazgo, requisitos, QA documental y apoyo acotado backend | Backlog, matriz de rúbrica, actas, perfil coherente, revisión de PR |
| Christian Gonzales | IA y pipeline conversacional | Intenciones, ejemplos etiquetados, parser o evaluación de voz con protocolo |
| Marializ Mamani | Backend, seguridad y persistencia | Node/Express + PostgreSQL como línea base objetivo; API, esquema e idempotencia. MongoDB/Mongoose queda pendiente de confirmación académica. |
| Kevin Rocha | UX/UI y prototipado | Flujos, Figma, estados y evidencia de accesibilidad/responsividad |
| Luis Lazo | C4, arquitectura y QA técnico | SAD/ADR, diagramas, trazabilidad, escenarios y pruebas |

## Rituales ligeros

- Reunión semanal de 30–45 minutos: demo, bloqueos, decisión y compromisos.
- Actualizar tablero el mismo día; cada tarea tiene responsable y revisora.
- Limitar el trabajo simultáneo a una tarea principal por persona.
- Revisar criterios de aceptación antes de iniciar y evidencia antes de cerrar.
- Registrar decisiones en ADR/acta si afectan stack, alcance, datos o calendario.

## Flujo de GitHub

Crear ramas cortas `feat/...`, `fix/...` y `docs/...`; proteger `main`; abrir pull request con motivo, alcance, evidencia y requisito asociado. Al menos una compañera revisa antes de integrar. No subir `.env`, credenciales, grabaciones no autorizadas o datos personales. Usar issues para tareas y enlazarlas a commits/PR.

## Definición de terminado

La tarea cumple su criterio, otra persona la revisó, evidencia reproducible y documentación/trazabilidad están actualizadas, y las limitaciones se declaran. “Diseñado”, “implementado”, “probado” y “validado por usuario” son estados distintos.

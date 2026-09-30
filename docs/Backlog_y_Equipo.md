# Product backlog y responsabilidades del equipo

**Corte:** 30/09/2026  
**Prioridad:** primero un recorrido pequeño que funcione y pueda revisarse. Las prioridades son de coordinación; el equipo debe refinarlas en Issues/Projects de GitHub y confirmar fechas contra [`07_Cronograma_y_Riesgos.md`](07_Cronograma_y_Riesgos.md).

Este backlog toma la organización del repositorio SMIA como referencia: épicas, historias, criterios de aceptación y relación con requisitos. No copia sus historias, su dominio municipal ni sus estimaciones de puntos. No se asignan story points hasta que el equipo acuerde una escala y capacidad real.

## Roles de trabajo

| Integrante | Área principal | Responsabilidad verificable |
|---|---|---|
| Eva Chino Quispe | Liderazgo, requisitos y QA documental | Mantener prioridades, convocar revisión semanal, enlazar issues con criterios y rúbricas; apoyar a Marializ/Luis mediante contrato API, revisión de PR y evidencias |
| Christian Gonzales | IA y flujo conversacional | Delimitar intenciones/entidades del MVP y crear conjunto de frases sintéticas de prueba con método, sin atribuir precisión no medida |
| Marializ Mamani | Backend, seguridad y datos | Mantener API Node/Express y PostgreSQL; acordar esquema/migraciones; proponer autorización antes de exponer datos de negocio |
| Kevin Rocha | UX/UI y prototipo | Diseñar y revisar flujos de catálogo/venta/confirmación, errores, estados vacíos y funcionamiento sin conexión |
| Luis Lazo | Arquitectura C4 y QA técnico | Mantener C1/C2, decisiones, contratos y trazabilidad con el estado real del código |

Los responsables pueden colaborar; la asignación indica quién coordina y presenta el resultado, no una división rígida.

## Épicas y tareas iniciales

| ID | Épica / historia | Criterio de aceptación | Responsable | Dependencias | Estado |
|---|---|---|---|---|---|
| EP-01 / US-01 | Acordar alcance común del MVP | Lista priorizada: catálogo mínimo, venta manual, consulta local y sincronización posterior; exclusiones explícitas en `01_Proyecto_y_Alcance.md` | Eva + equipo | Ninguna | Documentado; revisar con docente |
| EP-01 / US-02 | Resolver observaciones docentes | Registrar respuestas sobre Node/PostgreSQL, Mongo/Mongoose y frontend web en ADR/acta con fecha | Eva | Consultar sílabos | Pendiente |
| EP-02 / US-03 | Preparar backend local | Otro miembro puede levantar Compose y obtener health check conectado a PostgreSQL con instrucciones del README | Marializ; Eva revisa | Docker, `.env.example` | Scaffold existente; falta verificación local |
| EP-02 / US-04 | Acordar modelo relacional v1 | Esquema revisado con negocio, producto, venta, líneas y movimientos; constraints/índices explicados; sin duplicar persistencia remota | Marializ + Luis | EP-01 | Parcial: esquema solo inicial |
| EP-03 / US-05 | Definir contrato de productos | Ejemplos JSON, validación, errores, paginación y requisito de autorización por negocio documentados antes de CRUD | Eva + Marializ | EP-04 | Propuesto |
| EP-03 / US-06 | Implementar CRUD mínimo protegido | Crear/listar/editar producto solo dentro del negocio autorizado; criterios reproducibles y ninguna ruta comercial anónima | Marializ | EP-04, US-05 | Pendiente |
| EP-04 / US-07 | Prototipar registrar venta manual | Figma permite completar venta, revisar detalle y confirmar; incluye error, vacío y precio/cantidad inválidos | Kevin | EP-01 | Por verificar |
| EP-04 / US-08 | Registrar venta local | Venta confirmada queda guardada localmente tras reiniciar; modo avión no pierde operación; cálculo de total demostrable | Marializ + Kevin; Eva coordina | US-04, US-07 | Pendiente; móvil aún no creado |
| EP-04 / US-09 | Sincronizar venta idempotentemente | Al repetir el mismo UUID, servidor no duplica la venta; preservar el evento y mostrar estado/conflicto de stock | Marializ + Luis | US-08, autenticación | Pendiente |
| EP-05 / US-10 | Preparar conversación de alcance acotado | Intents iniciales documentadas y fallback a formulario ante baja confianza; frases de prueba anonimizadas; no es modelo entrenado | Christian + Kevin | US-07 | Pendiente |
| EP-06 / US-11 | Construir evidencia de calidad | Registrar protocolo, versión y resultado real para offline, idempotencia, seguridad básica y latencia; no declarar objetivos como resultados | Luis + cada responsable | Implementaciones anteriores | Pendiente |
| EP-06 / US-12 | Preparar entrega integrada | README, demo y checklist describen lo que corre, lo que está simulado y los límites; revisión de PR por otra integrante | Eva + equipo | Todos | En preparación |

## Orden de trabajo recomendado

1. **Desbloquear decisiones:** docente aclara Mongo/Mongoose, frontend web y transición de backend. Eva registra respuesta; equipo conserva PostgreSQL como backend común solicitado.
2. **Probar el entorno actual:** Marializ reproduce PostgreSQL + health check; reporta cualquier diferencia y actualiza instrucciones.
3. **Congelar MVP y contrato:** Eva, Marializ y Luis acuerdan modelo/contrato; Kevin ajusta prototipo a dichos flujos.
4. **Implementar una función vertical:** catálogo autorizado → venta manual → almacenamiento local → Outbox → API → PostgreSQL.
5. **Reintentar sin duplicar:** mismo UUID, resultados por evento y conciliación explícita; no usar LWW para reemplazar una venta.
6. **Añadir voz y evaluación:** Christian y Kevin integran reconocimiento de intención solo después de que la venta manual sea confiable.
7. **Revisar evidencia:** cada responsable hace demo; otra integrante repite los pasos; Eva verifica rúbrica y fecha del cronograma.

## Reglas de gestión

- Cada historia pasa a GitHub Issue con alcance pequeño, responsable, prioridad, dependencia y aceptación comprobable.
- Columnas sugeridas: `Backlog`, `Ready`, `In progress`, `Review`, `Done`; una persona no cierra su propio trabajo sin revisión cruzada.
- Registrar decisiones de stack, alcance, datos y fechas; no cambiar código, README y diagramas en direcciones distintas.
- R3 (choque de evaluaciones) se gestiona en el cronograma: congelar opcionales antes de entregas y cortar alcance antes que ocultar retrasos.
- R6 (ausencia/rotación) se mitiga con instrucciones reproducibles, revisora suplente y conocimiento compartido; no transferir secretos en el repositorio.


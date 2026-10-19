# Segunda entrega y preparación de presentaciones — tres materias

**Fecha de preparación:** 19/10/2026  
**Proyecto:** Asistente conversacional offline para MYPES de Vinto  
**Integrantes:** Eva Lizeth Chino Quispe, Christian Gonzales Mamani, Marializ Mamani Quispe, Kevin Jhonathan Rocha Rojas y Luis Gerardo Lazo Bustamante.

> Este plan distribuye tareas usando los roles que ya constan en el SAD del equipo. Es una propuesta de coordinación, no una asignación individual impuesta por el sílabo. Cada integrante debe revisar su parte y el equipo debe confirmar que los roles siguen vigentes.

## 1. Cómo se está realizando el proyecto

### Tecnologías observadas en el repositorio

| Parte | Tecnología usada | Estado descrito con honestidad |
|---|---|---|
| Aplicación móvil | Kotlin, Jetpack Compose, Room/SQLite; componentes de login local por PIN, onboarding, dashboard e inventario | El usuario reportó instalación en un Motorola G60 y guardado local de un producto de prueba. La opción para eliminar productos no existe en la pantalla revisada. |
| PLN/voz en el dispositivo | Kotlin, clasificación por reglas/plantillas y fachada de Vosk | El código existe, pero el modelo Vosk no se versiona; no afirmar reconocimiento operativo sin instalarlo y probarlo en el dispositivo. |
| Backend/API | Node.js 24, TypeScript y Express | Implementa rutas de autenticación/catálogo y sincronización descritas en backend/README.md. |
| Base central del backend | PostgreSQL con migraciones SQL | Es la base efectivamente usada por el backend. El servicio se documenta como local; no como despliegue en nube. |
| Sincronización móvil | Retrofit, Outbox y WorkManager | Hay componentes en el código. No está verificada de extremo a extremo en el teléfono físico; la URL móvil 10.0.2.2 está configurada para emulador y la autenticación local por PIN no es el token JWT del backend. |
| Contenedores de desarrollo | Docker Compose para PostgreSQL y backend local | Sirve para desarrollo y demostración local. No constituye despliegue cloud, HTTPS/TLS o monitoreo de producción. |

La documentación y el código no tienen el mismo nivel de evidencia. La pantalla de inventario que se mostró prueba la operación puntual de guardar, según el reporte de la estudiante; para una revisión académica deben guardarse los pasos, fecha, commit y captura anonimizada en evidence/.

### Justificación breve de Node.js y PostgreSQL

**Node.js sí se está utilizando.** El backend del proyecto está escrito con Node.js, TypeScript y Express. Esto coincide con la unidad de backend del sílabo de Tecnologías en Internet, que solicita Node.js/Express, APIs REST, asincronía y manejo de peticiones. Express organiza las rutas HTTP del API y TypeScript ayuda a detectar errores de tipos durante el desarrollo. Por tanto, no se debe presentar Node.js como una tecnología omitida; la evidencia pendiente es mostrar sus endpoints, pruebas y documentación en la entrega.

**PostgreSQL fue una decisión del proyecto, no un sustituto idéntico de MongoDB.** El sistema relaciona propietarios/negocios, productos, ventas, detalles de venta y operaciones sincronizadas. Una base relacional permite declarar claves, relaciones, restricciones y transacciones SQL para conservar consistencia al registrar operaciones financieras y el stock. PostgreSQL también es la base ya definida en las migraciones del backend y en la arquitectura actual del equipo; mantener una sola base central evita introducir una segunda persistencia y dos modelos de datos que habría que sincronizar y probar.

El sílabo TEC-644 incluye MongoDB/Mongoose y la segunda entrega menciona integración MongoDB. Por eso esta elección **no demuestra por sí sola** ese criterio curricular: PostgreSQL no es MongoDB. Antes de la presentación, el equipo debe pedir al docente que confirme una de estas alternativas:

1. Aceptar PostgreSQL como decisión justificada del proyecto y demostrar MongoDB/Mongoose en una práctica separada.
2. Exigir un módulo didáctico MongoDB/Mongoose separado, sin duplicar datos reales del proyecto.
3. Exigir que el proyecto adopte MongoDB; en ese caso, registrar primero la decisión, los cambios de arquitectura y el plan de migración antes de cambiar el código.

El proyecto también usa Android nativo con Compose, no una interfaz web hecha con HTML5/CSS3/JavaScript. La práctica de diseño web del sílabo debe demostrarse aparte o según la alternativa que indique el docente. Un prototipo Figma y una app Compose no son evidencia de una página web responsiva.

### Cómo explicar la decisión en la exposición

> “El backend se implementó con Node.js, TypeScript y Express, de acuerdo con el componente de APIs del sílabo. Para la persistencia central elegimos PostgreSQL porque el dominio relaciona negocios, productos y ventas y necesita restricciones y transacciones consistentes. Esta es una decisión del proyecto, no una equivalencia con MongoDB. Para cumplir el contenido MongoDB/Mongoose estamos consultando si se presentará como laboratorio independiente o si el docente solicita un cambio de arquitectura.”

No afirmar que el docente aprobó la sustitución hasta contar con confirmación. Registrar su respuesta y el alcance acordado en el control de decisiones.

## 2. Roles existentes y responsabilidad en la preparación

Los roles que aparecen en el SAD v1.1 son:

| Integrante | Rol documentado | Responsabilidad transversal para las entregas |
|---|---|---|
| Eva Lizeth Chino Quispe | Líder del proyecto, requisitos y QA documental | Convocar revisión, mantener documentos/versiones, enlazar criterio–artefacto–evidencia y consolidar la presentación. |
| Christian Gonzales Mamani | Ingeniería de IA y pipeline conversacional | Explicar PLN/Vosk, casos de voz/texto y límites realmente demostrados. |
| Marializ Mamani Quispe | Backend, seguridad y persistencia de datos | Explicar API, Node/Express, PostgreSQL, autenticación y pruebas de backend. |
| Kevin Jhonathan Rocha Rojas | Diseño UX/UI y prototipado | Explicar personas/flujo, prototipo y diferencia entre diseño Figma, pantalla Android y web front-end. |
| Luis Gerardo Lazo Bustamante | Arquitectura C4 y QA técnico | Mantener C1/C2/despliegue, coherencia con código, decisiones y resultados de pruebas técnicas. |

Si el reparto cambió, actualicen este documento y el SAD antes de presentar; no atribuyan a una persona evidencia que no preparó o no revisó.

## 3. Investigación I — INV-645

### Qué se presenta y cuándo

El sílabo no etiqueta literalmente una actividad como “segunda presentación”. La próxima revisión indicada es **22/10/2026: evaluación del Perfil de Investigación (10%) y prácticas realizadas en clase (14%)**. La fase final del **26/11/2026** incluye perfil (10%), ensayo (5%) y defensa del perfil (20%), además de otras evidencias institucionales. Confirmar en clase el formato, tiempo y si habrá exposición oral el 22/10.

### Para el hito del 22/10, cada integrante debe dejar listo

| Integrante | Preparación concreta | Material que entrega a Eva para integrar |
|---|---|---|
| Eva | Revisar que título, problema, pregunta, objetivo general/específicos, justificación, alcance y límites sean coherentes en la versión 11; preparar un control de cambios y preguntas abiertas al tutor. | Lista de cotejo del perfil, versión y referencias verificadas; agenda breve de presentación si el docente la solicita. |
| Christian | Entregar antecedentes técnicos verificables sobre asistente local, PLN/Vosk y uso sin conexión; distinguir fuentes publicadas de ideas del equipo. | Fichas de 2–3 fuentes pertinentes con referencia completa, aporte y limitación; no prometer precisión no medida. |
| Marializ | Revisar factibilidad técnica/operativa del backend, Room/SQLite, PostgreSQL, autenticación, conectividad y resguardo de datos; registrar supuestos/costos que sí se conozcan. | Nota de factibilidad con fuentes, costos documentados o “por cotizar”, y riesgos; evitar inventar precios o disponibilidad. |
| Kevin | Revisar actores y recorrido actual del comerciante, prototipo y necesidades de interfaz; separar entrevistas reales, prototipo y app instalada. | Flujo UX con capturas rotuladas como prototipo o app, datos anonimizados y hallazgos con su muestra/limitación. |
| Luis | Revisar alternativas de solución, arquitectura de alto nivel y plan de validación ligado a los objetivos del perfil. | Diagrama legible y nota de viabilidad/medición; identificar qué es diseño y qué ya está implementado. |

**Todos:** leer la versión que se va a presentar, aportar correcciones antes de congelarla, poder explicar el problema y el aporte, y verificar citas/referencias. No generalizar una entrevista pequeña a todos los comerciantes de Vinto.

### Preparación para la fase final del 26/11

- **Eva:** consolidar versión final, control de cambios, guion de defensa y lista de preguntas/respuestas.
- **Christian:** apoyar el estado del arte y explicar con evidencia qué aporta el PLN local y qué queda como trabajo futuro.
- **Marializ:** sustentar factibilidad del producto, seguridad/privacidad y límites de integración.
- **Kevin:** preparar figuras del flujo actual y del prototipo con etiquetas claras y accesibles.
- **Luis:** revisar diagramas, consistencia técnica, plan de validación y riesgos.
- **Todos:** revisar el ensayo, el perfil final y la defensa; cada autor aprueba sus aportes y sus fuentes.

## 4. Arquitectura de Software — ARQ-643

### Segunda entrega y defensa

El sílabo fecha la **segunda entrega del Proyecto Integrador para el 16/11/2026 (10%)**. Pide una solución arquitectónica justificada, estilos/patrones, arquitectura distribuida, diagramas de componentes, contenedores, despliegue e interacción, mecanismos de integración y decisiones. El **23/11/2026** aparecen prácticas/casos (10%), artículo científico (10%) y defensa final con software y documentación (15%).

### Para la segunda entrega del 16/11, cada integrante debe dejar listo

| Integrante | Preparación concreta | Artefacto para el SAD y exposición |
|---|---|---|
| Eva | Consolidar requisitos/drivers, casos de uso y trazabilidad requisito → escenario de calidad → decisión → evidencia. Resolver contradicciones de nombre/alcance y registrar pendientes docentes. | Tabla de trazabilidad, alcance y cambios; control de versiones y resumen de brechas. |
| Christian | Documentar componentes de NLU/Vosk y el flujo de texto/voz hasta la confirmación; marcar pasos no implementados o sin medición. | Vista de componentes/secuencia y escenarios de latencia/memoria con resultados solo si se midieron. |
| Marializ | Explicar API Node/Express, PostgreSQL, autenticación, datos y lote idempotente; justificar por qué se eligió esta persistencia y registrar que no satisface MongoDB por equivalencia. | Vista de backend/datos, contrato REST, ADR de stack y resultados reproducibles de API. |
| Kevin | Relacionar pantallas y navegación con casos de uso; diferenciar Figma, Compose y funciones disponibles. | Flujos/UX y capturas fechadas; observaciones de accesibilidad/usabilidad si fueron probadas. |
| Luis | Coordinar actualización de C1, C2, componentes, despliegue e interacción; comparar patrones/alternativas, riesgos y atributos de calidad. | Diagramas legibles, decisiones y sección de evaluación (método, escenarios, resultados y límites). |

**Entregable común:** SAD nuevo con versión propia. Conservar el SAD v1.1 que se presentó; no sustituirlo silenciosamente. La versión actual debe describir Node.js/TypeScript/Express/PostgreSQL y el cliente Android/Room como están en el código, o marcar claramente lo que todavía es propuesta. Explicar la diferencia Spring Boot → Node.js como cambio de decisión, con consecuencias.

### Para el 23/11

- Completar el artículo científico con formato exigido por el docente, fuentes académicas comprobables, citas enlazadas y revisión de todos los autores.
- Preparar defensa con dos demostraciones separadas: flujo local que realmente funciona y API/sincronización que haya sido probada aparte.
- Presentar fallos y pendientes como resultados; no simular una integración ni afirmar despliegue cloud inexistente.

## 5. Tecnologías en Internet — TEC-644

### Segunda entrega y presentaciones asociadas

El sílabo fecha la **segunda entrega de Backend e Integración MongoDB para el 11/11/2026 (10%)**. El **18/11/2026** pide guías/prácticas continuas relacionadas con las últimas unidades (10%). La defensa de la plataforma full-stack desplegada y el código en repositorio aparece el **25/11/2026 (15%)**. Confirmar con el docente el criterio para MongoDB y front-end web porque el producto del equipo usa Android y PostgreSQL.

### Para la segunda entrega del 11/11, cada integrante debe dejar listo

| Integrante | Preparación concreta | Artefacto para entregar/presentar |
|---|---|---|
| Eva | Verificar requisitos, documentación de endpoints, consistencia de nombres, alcance, decisiones y matriz de evidencias; obtener respuesta docente sobre MongoDB y web front-end. | Requisitos priorizados, guía de API, trazabilidad y nota de acuerdos docentes con fecha. |
| Christian | Preparar ejemplos de comandos/casos que consumirá el asistente y el formato esperado por el API; separar flujo de PLN local de sincronización demostrada. | Datos ficticios y secuencia de integración; capturas/logs sin datos personales ni tokens. |
| Marializ | Preparar backend Node 24/TypeScript/Express, PostgreSQL, migraciones, login JWT, catálogo y endpoint de sincronización; ejecutar build/typecheck/pruebas indicadas por backend/README.md. | API arrancada localmente, colección Postman o comandos reproducibles, respuestas esperadas y explicación de la decisión PostgreSQL. Si se exige MongoDB, una práctica Mongo/Mongoose aislada y aprobada, no una afirmación de que Postgres es Mongo. |
| Kevin | Preparar prototipo y pantallas Android actuales; identificar qué requiere la unidad de HTML/CSS/JavaScript y proponer un front-end web pequeño separado si el docente lo exige. | Comparación Figma vs. Android ya implementado; flujo web de práctica o confirmación de alternativa aceptada. |
| Luis | Preparar diagrama de despliegue local con Docker Compose, puertos, dependencias y procedimiento reproducible; diferenciar Compose local de cloud. | Diagrama actualizado y evidencia de health check, servicios healthy y pruebas permitidas. No reportar HTTPS/cloud/monitoreo sin implementación. |

### Para el 18/11 y el cierre del 25/11

- **Eva:** cerrar la documentación de prácticas, criterios y datos anonimizados.
- **Christian:** apoyar una demo del flujo de usuario con resultados reales del pipeline; declarar si necesita modelo Vosk.
- **Marializ:** mostrar pruebas de backend y controles de autenticación/aislamiento; preparar hallazgos y limitaciones de seguridad.
- **Kevin:** validar navegación, legibilidad y pantallas; no presentar el prototipo Figma como sistema desplegado.
- **Luis:** coordinar la demo técnica, la evidencia Docker y la revisión de diagramas.
- **Todos:** explicar con precisión qué significa full-stack en esta versión. La app móvil y API local no constituyen por sí solas un front-end web responsivo ni un despliegue cloud; confirmar con el docente cómo cubrir la unidad.

## 6. Lista de control antes de cada exposición

### Cada integrante debe poder responder

- ¿Qué criterio del sílabo cubre mi parte?
- ¿Qué archivo, función o evidencia lo demuestra?
- ¿Qué probé personalmente y con qué pasos?
- ¿Qué está en código, qué solo está documentado y qué sigue pendiente?
- ¿Qué limitación o riesgo encontré?
- ¿Qué cambio entre versiones y por qué?

### Eva consolida antes de subir

- [ ] Versión/fecha/commit en la portada y control de cambios.
- [ ] Criterio del sílabo ligado a documento, código/evidencia y responsable.
- [ ] Nombres, alcance y tecnologías consistentes en README, SAD, diagramas y presentación.
- [ ] Capturas anonimizadas y datos ficticios; ningún secreto, token ni contraseña.
- [ ] Respuesta del docente documentada si modifica el alcance requerido (MongoDB, web front-end, cloud).
- [ ] Presentación ensayada por el equipo con una demo reproducible y un plan si la demo falla.

## Fuentes y límites

- Programas analíticos ARQ-643, TEC-644 e INV-645, Gestión 2026-2, proporcionados por la estudiante.
- Roles del equipo declarados en docs/03_SAD_Arquitectura.md.
- Estado técnico contrastado con README.md, backend/README.md, mobile/README.md y código de la rama main revisada.
- Los roles de exposición son una propuesta basada en los roles ya documentados; confirmar cambios con el equipo.
- Las fechas y porcentajes proceden del sílabo. No se proporcionaron rúbricas analíticas completas ni acuerdos del docente sobre sustituir MongoDB o demostrar el front-end web en una práctica separada.

# Matriz de cumplimiento y evidencias para las tres materias — Gestión 2026-2

**Corte de revisión:** 19/10/2026  
**Propósito:** ayudar a preparar revisiones y entregas desde el rol de documentación, control de versiones, trazabilidad y organización de evidencias.  
**Proyecto:** prototipo de asistente conversacional offline, configurable por rubro, para registrar ventas y consultar el saldo local de existencias en MYPES del centro de Vinto.

> Esta matriz traduce los resultados y la evaluación descritos en los tres programas analíticos compartidos. No es una rúbrica oficial, no predice notas ni reemplaza instrucciones del docente. Si existe una rúbrica adicional o una corrección docente, se debe incorporar antes de cerrar cada entrega.

## Base de la revisión

Se revisaron los sílabos de **Arquitectura de Software (ARQ-643)**, **Tecnologías en Internet (TEC-644)** e **Investigación I (INV-645)**, Gestión 2026-2. Sus códigos de verificación son 242ae6ba-a945-4192-94b0-50fe73348d18, c84d0319-4908-495f-90e3-525d7137c007 y 37e51b11-c5eb-4414-8bf3-cd1ce8c48c24, respectivamente.

También se contrastó la documentación y el código de la rama `main` del repositorio en el commit `ec9028b` (16/10/2026). El usuario reportó que instaló la app en un Motorola G60 y que un producto de prueba quedó guardado en Inventario; esa demostración aún no está archivada en `evidence/`.

### Cómo leer los estados

| Estado | Significado |
|---|---|
| **Documentado** | Hay un documento, requisito o diagrama en el repositorio. Aún puede requerir revisión o aprobación. |
| **En código** | El código correspondiente aparece en la rama revisada. Esto no demuestra que el flujo completo funcione. |
| **Demostrado por el equipo** | El equipo reportó o mostró una prueba puntual; falta archivar un registro repetible si no se indica lo contrario. |
| **Pendiente de evidencia** | El criterio requiere un resultado, prueba, entrega o defensa que no se encontró en el repositorio revisado. |
| **Brecha de alcance/tecnología** | La expectativa del sílabo y la solución actual no coinciden; se debe consultar al docente antes de prometer cobertura. |



La explicación del stack realmente usado (Node.js/Express, PostgreSQL, Kotlin/Compose y Room), la justificación técnica de PostgreSQL y la distribución de tareas para las próximas entregas están en el [Plan de segunda entrega por materia](14_Segunda_Entrega_Tareas_por_Materia.md).Una característica solo debe marcarse **verificada** en una entrega cuando tenga pasos repetibles, entorno, fecha, commit y resultado observable. Un diagrama, una lista de requisitos o una compilación no bastan por sí solos para demostrar el comportamiento.

## Resumen ejecutivo

| Materia | Evidencia principal disponible | Riesgo documental o técnico que debe atenderse |
|---|---|---|
| Investigación I | `docs/perfil/Perfil_final.md`, versión 11, con el problema, alcance y propuesta del perfil. | Preparar y revisar el ensayo, la versión final del perfil y su defensa; conservar trazabilidad de fuentes y evidencia. La existencia del perfil no demuestra que la evaluación haya sido aprobada. |
| Arquitectura de Software | `docs/03_SAD_Arquitectura.md`, diagramas C4 y casos de uso. | El SAD 1.1 conserva Spring Boot, mientras el backend de `main` usa Node.js/TypeScript/Express/PostgreSQL. Hace falta una versión actualizada claramente identificada, con decisiones justificadas, evaluación de calidad y diagramas sincronizados con el código. |
| Tecnologías en Internet | Requisitos/UX, API Node.js/Express/PostgreSQL y app Android Kotlin/Compose con Room. | El sílabo pide contenidos de web front-end y MongoDB/Mongoose; la implementación revisada es Android y PostgreSQL. Docker local no demuestra despliegue en nube, HTTPS/TLS, auditoría de seguridad ni auditoría de impacto. Consultar qué evidencia alternativa acepta el docente. |

## 1. Investigación I — INV-645

### Productos y evaluación que indica el sílabo

| Fecha del sílabo | Evidencia evaluada | Puntaje |
|---|---|---:|
| 03/09/2026 | Prueba sobre elaboración de un perfil orientado a resolver problemas | 8% |
| 03/09/2026 | Prácticas realizadas en clase | 14% |
| 22/10/2026 | Evaluación del Perfil de Investigación mediante rúbrica de trabajos escritos | 10% |
| 22/10/2026 | Prácticas realizadas en clase y participación | 14% |
| 26/11/2026 | Asistencia y ticket de salida | 10% |
| 26/11/2026 | Participación en la Jornada de Investigación | 4% |
| 26/11/2026 | Actividades cocurriculares/extracurriculares y hito intermedio indicado por el sílabo | 5% |
| 26/11/2026 | Ensayo | 5% |
| 26/11/2026 | Perfil de Trabajo de Grado | 10% |
| 26/11/2026 | Defensa oral del Perfil de Trabajo de Grado | 20% |
|  | **Total** | **100%** |

Las fechas anteriores son las del programa analítico. No se infiere de ellas que una actividad esté aprobada o que exista una calificación registrada.

### Lista de revisión del perfil y el ensayo

| Criterio para revisar | Evidencia que se debe localizar o completar | Estado documental |
|---|---|---|
| Problema tecnológico situado en un contexto real | Situación actual, objeto de estudio, población/contexto, antecedentes y alcance de la evidencia primaria | Documentado en el perfil v11; revisar fuentes y consistencia antes de entregar |
| Pregunta/problema y causas–efectos | Formulación clara, árbol o esquema causal enlazado con antecedentes y objetivos | Revisar en el perfil final y guardar la versión aceptada |
| Objetivo general y objetivos específicos | Verbos, secuencia lógica, producto/aporte y relación explícita con el problema | Pendiente de cotejo final con la consigna del docente |
| Justificación, beneficiarios e impacto | Razones técnicas, sociales y académicas, sin presentar beneficios hipotéticos como resultados medidos | Documentado parcialmente; sostener con fuentes y delimitaciones |
| Alcance, límites y factibilidad | Usuarios, rubros, funciones, entorno Android, recursos, costos y plazo | Alcance descrito; confirmar que no se mezclen entregas de materia con el Trabajo de Grado |
| Estado del arte y ensayo | Fuentes académicas pertinentes, síntesis crítica, brecha, citas y referencias verificables | Pendiente de adjuntar/revisar como producto separado |
| Ética e integridad | Consentimiento cuando corresponda, anonimización, citas, derechos de autor y prevención de plagio | Mantener evidencia anonimizada y revisar referencias una por una |
| Defensa | Presentación alineada con el perfil, respuesta a preguntas y registro de retroalimentación | Pendiente de preparar/archivar evidencia |

**Artefacto base:** [Perfil final](perfil/Perfil_final.md). La versión 11 se identifica como el perfil que se presenta; cualquier corrección posterior debe quedar como nueva versión y no sobrescribir silenciosamente la entregada.

## 2. Arquitectura de Software — ARQ-643

### Productos y evaluación que indica el sílabo

| Fecha del sílabo | Evidencia evaluada | Puntaje |
|---|---|---:|
| 31/08/2026 | Examen teórico-práctico integrador | 10% |
| 07/09/2026 | Resolución de casos, diagramas/modelos, exposición y participación | 10% |
| 21/09/2026 | Primera entrega del SAD: problema, drivers, atributos/escenarios de calidad, tácticas y vistas preliminares | 10% |
| 19/10/2026 | Examen aplicado de patrones y arquitectura distribuida | 10% |
| 16/11/2026 | Segunda entrega del proyecto y del SAD: estilos/patrones, distribución, diagramas, integración y decisiones | 10% |
| 23/11/2026 | Prácticas/casos y trabajo colaborativo | 10% |
| 23/11/2026 | Artículo científico relacionado con arquitectura o ingeniería de software | 10% |
| 23/11/2026 | Defensa final del proyecto, software y documentación | 15% |
| 30/11/2026 | Actividades cocurriculares/extracurriculares | 5% |
| 30/11/2026 | Asistencia y ticket de salida | 5% |
| 30/11/2026 | Hito intermedio del perfil de egreso | 5% |
|  | **Total** | **100%** |

### Lista de revisión del SAD y sus evidencias

| Criterio del sílabo | Evidencia que debe incluir el SAD/entrega | Situación observada en el repositorio |
|---|---|---|
| Problema, contexto y requisitos arquitectónicamente significativos | Drivers priorizados, restricciones y vínculo con requisitos funcionales/no funcionales | SAD v1.1 y requisitos existen; reconciliar nombres, alcance y stack |
| Atributos y escenarios de calidad | Estímulo, entorno, respuesta y medida verificable para disponibilidad offline, rendimiento, seguridad, usabilidad y modificabilidad | Hay material arquitectónico previo; registrar resultados medidos y separar objetivos de resultados |
| Alternativas y patrones arquitectónicos | Comparación, ventajas/desventajas, contexto, selección y justificación | Actualizar la decisión para la arquitectura del código actual |
| Arquitectura distribuida | Diagramas de componentes, contenedores, despliegue e interacción; protocolos y contratos | C1/C2 y casos de uso existen; verificar que cada elemento coincide con el código vigente |
| Calidad y evaluación | Métricas, checklist o método (por ejemplo ATAM/SAAM según la consigna), riesgos, sensibilidad, trade-offs y mejoras | Pendiente de incorporar evaluación fechada y resultados |
| Tendencias | Comparación de nube, IA y blockchain; adopción o descarte razonado según alcance/viabilidad | Pendiente de una sección breve, específica al proyecto, sin añadir tecnologías solo para marcar casillas |
| Decisiones de arquitectura | ADR con contexto, opciones, decisión, consecuencias y estado de aprobación | Revisar coherencia con Android/Room, backend Node/Express y PostgreSQL |
| Artículo y defensa | Artículo científico con fuentes académicas, estructura/formato que solicite el docente y evidencia de defensa | Pendiente de localizar como archivo de entrega |

**Artefactos base:** [SAD v1.1](03_SAD_Arquitectura.md), [diagramas](../diagrams/README.md) y decisiones históricas bajo `docs/archive/referencias_historicas/`.

El SAD 1.1 es una entrega histórica y no debe presentarse como arquitectura actual sin una nota de estado. La rama actual tiene backend Node.js/TypeScript/Express/PostgreSQL; el documento v1.1 aún describe Spring Boot en parte de sus decisiones. Preparar un SAD actualizado como versión nueva, conservar la versión entregada y solicitar revisión docente cuando corresponda.

## 3. Tecnologías en Internet — TEC-644

### Productos y evaluación que indica el sílabo

| Fecha del sílabo | Evidencia evaluada | Puntaje |
|---|---|---:|
| 26/08/2026 | Examen práctico integrador de desarrollo web | 10% |
| 09/09/2026 | Guías de laboratorio y prácticas continuas | 5% |
| 23/09/2026 | Primera entrega del proyecto: UX/UI y arquitectura de requisitos | 10% |
| 14/10/2026 | Examen práctico de APIs RESTful y persistencia NoSQL | 15% |
| 11/11/2026 | Segunda entrega: backend e integración MongoDB | 10% |
| 18/11/2026 | Guías/prácticas de seguridad y despliegue | 10% |
| 25/11/2026 | Jornada de Investigación Institucional | 5% |
| 25/11/2026 | Taller de interacción social, equipo/liderazgo/comunicación | 5% |
| 25/11/2026 | Defensa de plataforma full-stack desplegada y código en repositorio | 15% |
| 25/11/2026 | Actividades cocurriculares/extracurriculares | 5% |
| 25/11/2026 | Asistencia y tickets de salida | 5% |
| 25/11/2026 | Hito intermedio del perfil de egreso | 5% |
|  | **Total** | **100%** |

### Cotejo de tecnologías y productos

| Criterio del sílabo | Evidencia esperada | Situación observada / acción |
|---|---|---|
| Front-end web: HTML5, CSS3, JavaScript, responsividad, accesibilidad e interoperabilidad | Interfaz web interactiva, validada en dispositivos/navegadores, más decisiones de diseño | La app Android Compose no demuestra por sí sola un front-end web. Confirmar con el docente si Figma/documentación o una práctica web independiente cubre este criterio |
| Backend Node.js y Express | Rutas REST, validación, errores, documentación de endpoints y pruebas | Backend Node/TypeScript/Express está en el repositorio; adjuntar pruebas reproducibles y colección Postman si se solicita |
| Persistencia MongoDB/Mongoose | Colecciones, CRUD, validación e integración con Node | El backend descrito por el repositorio usa PostgreSQL. PostgreSQL no equivale a MongoDB; confirmar si se requiere una actividad didáctica separada |
| Seguridad y auditoría | Autenticación/autorización, protección de datos, revisión de OWASP Top 10, pruebas y registro de resultados | API documenta contraseña hasheada, JWT y pruebas iniciales; falta evidencia suficiente de auditoría OWASP, HTTPS/TLS y despliegue seguro |
| Despliegue, optimización y monitoreo | Entorno publicado o práctica cloud, Docker, indicadores de rendimiento, monitoreo y evaluación ética/socio-digital | Docker Compose local permite probar servicios en el equipo; no demuestra despliegue en nube, TLS, monitoreo o auditoría de impacto |
| Producto full-stack e integración | Flujo de usuario, cliente, API, base de datos, despliegue y demostración | La app Android guarda inventario local según demostración reportada. La integración móvil–API en un teléfono físico no está verificada; el cliente móvil configura la dirección de emulador `10.0.2.2` |
| Entregables continuos | Guías, procedimientos y evidencias de laboratorio | Mantener una evidencia separada por práctica y relacionarla con el criterio que demuestra |

**Artefactos base:** [Requisitos y UX/UI](02_Requisitos_y_UX.md), [README del backend](../backend/README.md), [README móvil](../mobile/README.md) y [evidencias](../evidence/README.md).

## 4. Evidencias que debe reunir la responsable de documentación

Guardar solamente datos de prueba y capturas anonimizadas. No incluir contraseñas, tokens, números de teléfono, nombres de comerciantes ni grabaciones identificables.

| ID sugerido | Evidencia | Criterio relacionado | Estado |
|---|---|---|---|
| EV-MOB-01 | Instalación y apertura del APK; dispositivo, Android, commit y pasos | Demostración del producto / desarrollo móvil | Reportado por la estudiante; archivar captura y procedimiento |
| EV-MOB-02 | Crear producto de prueba y comprobar que aparece tras cerrar/reabrir app y sin red | Persistencia y UX | Alta inicial reportada; persistencia en reapertura/modo avión pendiente |
| EV-MOB-03 | Registrar venta, confirmar/cancelar, recalcular stock y revisar historial local | Requisitos offline / casos de uso | Pendiente de demostrar |
| EV-AUTH-01 | Primer acceso con PIN y acceso posterior con el mismo PIN; no incluir el PIN real en captura | Autenticación local | Pendiente de archivar |
| EV-API-01 | Health check y peticiones autorizadas a login/catálogo/sync con datos ficticios | REST, backend y seguridad | Reproducir según README del backend |
| EV-SYNC-01 | Operación pendiente offline, reconexión, respuesta del API e idempotencia | Arquitectura distribuida y sincronización | Pendiente; verificar también token y dirección accesible desde el teléfono |
| EV-ARQ-01 | Diagramas C1/C2/deployment y tabla de decisiones comparados con el commit | SAD y arquitectura | Pendiente de revisión de consistencia |
| EV-QUAL-01 | Escenarios de calidad con método, carga/condición, resultado y evidencia | Calidad arquitectónica / rendimiento | Pendiente de medición |
| EV-SEC-01 | Procedimiento de pruebas de seguridad, hallazgos, severidad y corrección | Seguridad web | Pendiente de auditoría documentada |
| EV-INV-01 | Ensayo, perfil final, referencias verificadas y rúbrica/comentarios autorizados | Investigación I | Localizar y cotejar antes de cada entrega |
| EV-DEF-01 | Presentación, fecha, participantes, preguntas, retroalimentación y evidencia permitida | Defensas de las tres materias | Pendiente de archivar |

### Plantilla mínima para cada registro

- **ID y criterio:** código de esta matriz y fila del sílabo.
- **Fecha y responsable:** quién ejecuta y quién registra.
- **Versión:** hash corto del commit y versión de APK/API.
- **Entorno:** modelo de dispositivo o sistema, red/modo avión y versiones relevantes.
- **Pasos:** acciones numeradas que otra persona pueda repetir.
- **Esperado / observado:** separar lo que debía pasar de lo que realmente pasó.
- **Resultado:** aprobado, fallido o inconcluso, con explicación.
- **Adjuntos:** capturas anonimizadas, respuesta de API o reporte pertinente.
- **Límites:** qué no cubrió la prueba y qué falta repetir.

## 5. Prioridades para preparar las revisiones

1. **Corregir contradicciones entre documentos y código.** El README principal, el índice, el mapa, el README móvil y el de diagramas contenían afirmaciones antiguas sobre funciones Android no implementadas. Esta rama propone alinearlos con la revisión de `main`, distinguiendo código, prueba reportada y evidencia formal.
2. **Acordar brechas curriculares con los docentes.** Consultar si el front-end web y MongoDB/Mongoose deben desarrollarse dentro del proyecto o demostrarse en prácticas separadas. No presentar Compose como HTML/CSS/JavaScript ni PostgreSQL como MongoDB.
3. **Actualizar SAD y diagramas como nuevas versiones.** No sobrescribir el SAD 1.1 entregado; preparar una versión actual, enlazar cada decisión con código y registrar aprobación o estado pendiente.
4. **Capturar pruebas del móvil y backend.** Empezar con login local, guardado/reapertura, modo avión y flujo de venta. Después probar el API y, por separado, la sincronización desde el dispositivo físico.
5. **Completar los productos académicos faltantes.** Ensayo de Investigación I, artículo de Arquitectura de Software, resultados de evaluación de calidad/seguridad y evidencias de defensa.
6. **Mantener un control de cambios.** Cada corrección debe indicar versión, fecha, autor del cambio, motivo y artefactos afectados.

## Fuentes y límites de esta matriz

- Programas analíticos ARQ-643, TEC-644 e INV-645, Gestión 2026-2, proporcionados por la estudiante.
- Repositorio `bibi567-ch/asistente-ia-mypes-vinto`, rama `main`, commit `ec9028b`, consultado el 19/10/2026.
- Demostración de guardado de un producto en el Motorola G60 reportada por la estudiante en esta conversación.
- No se proporcionaron rúbricas analíticas completas por criterio, calificaciones, comentarios docentes ni comprobantes de asistencia. Esos resultados no se inventan y deben agregarse únicamente cuando el equipo disponga de ellos.

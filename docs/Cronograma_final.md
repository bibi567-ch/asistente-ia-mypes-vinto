# Cronograma final

**Trabajo:** prototipo de asistente conversacional offline, configurable por rubro, para el registro de ventas y la consulta del saldo local de existencias.  
**No se llama** “gestión de ventas e inventario”.  
**Equipo:** Eva Chino, Christian Gonzales, Marializ Mamani, Kevin Rocha, Luis Lazo.  
**Cierre del Trabajo de Grado:** 16 de abril de 2027 (26 semanas, dentro de las 6 a 12 meses del acta).  
**Cierre de este semestre:** defensa del perfil, no del producto terminado.

Hoy es 13 de octubre de 2026. Lo de abajo está ordenado para cumplir las materias sin prometer lo que el código no tiene.

---

## 1. Las dos fechas que no se mezclan

| Reloj | Desde | Hasta | Qué se defiende |
|---|---|---|---|
| Asignaturas de este semestre | julio 2026 | diciembre 2026 | El perfil, la arquitectura del núcleo y la API de consolidación |
| Trabajo de Grado | 20/10/2026 | 16/04/2027 | El prototipo completo, la voz medida y la prueba con comerciantes |

Noviembre no es la prueba SUS. Abril sí puede serlo.

Eva le pregunta al Lic. Alabe, antes del 22/10, si el acta acepta que la ejecución cruce a 2027-1. Si dice que todo muere el 4/12/2026, no se agregan funciones: se le pide dejar constancia de que el calendario de la materia son 18 semanas y el acta pide 6 meses.

---

## 2. Trabajo de Grado

| Fase | Fechas | Producto que se puede mostrar | Responsable de que exista |
|---|---|---|---|
| 1. Perfil y requerimientos | 20/10 – 02/11 | Este perfil y las entrevistas ordenadas | Eva |
| 2. Arquitectura | 03/11 – 23/11 | Diagramas iguales al código y contrato de la API | Luis |
| 3. Núcleo sin red | 03/11 – 14/12 | Venta guardada en el teléfono, saldo, reapertura, mismo identificador sin duplicar | Kevin y Marializ |
| 4. Texto y cuatro rubros | 15/12 – 25/01 | Corpus, quechua escrito, plantillas, resumen del día | Christian, Luis y Kevin |
| 5. Voz | 26/01 – 22/02 | Medición en el teléfono de 2 GB. Se integra solo si cabe | Christian |
| 6. Validación | 23/02 – 22/03 | Expertos y prueba de uso | Eva |
| 7. Documento final | 23/03 – 16/04 | Trabajo redactado y defensa | Eva, con secciones de cada uno |

No se empieza la voz mientras la venta por pantalla no sobreviva al modo avión.

---

## 3. Este semestre, semana por semana

| Fecha | Materia | Qué es | Qué se lleva de verdad |
|---|---|---|---|
| 14/10 | Tecnologías en Internet | Examen de API REST y NoSQL | Estudio. No se reescribe el backend |
| 19/10 | Arquitectura | Examen integrador | Escenario de calidad y decisión offline-first. No un módulo nuevo |
| 22/10 | Investigación I | Evaluación del perfil | El perfil final, impreso |
| 11/11 | Tecnologías en Internet | Backend e integración | API con registro de la venta, idempotente. Mongo solo si la docente lo pidió por escrito, y aparte |
| 16/11 | Arquitectura | Entrega del SAD | SAD v2 con el nombre correcto y el stack Node. La v1.1 de Spring no se borra: queda como antecedente |
| 23/11 | Arquitectura | Defensa | Teléfono en modo avión, o una demo honesta de hasta dónde llegó el núcleo |
| 25/11 | Tecnologías en Internet | Plataforma y jornada | Pantalla web mínima solo si la materia la exige. Si no, no se construye |
| 24/11–03/12 | Investigación I | Defensa del perfil. Confirmar el día | El mismo perfil. La demo, si entra, se presenta como avance de la fase 3 |

---

## 4. Quién hace qué ahora

**Hasta el 14/10, incluido el examen.** Nadie integra código nuevo a `main`. Se estudia REST: verbo, recurso, código de estado, JSON, qué es idempotente. Se estudia la diferencia entre PostgreSQL y MongoDB. El producto usa PostgreSQL. El examen puede preguntar Mongo igual.

**Del 15/10 al 22/10.**

| Persona | Entrega |
|---|---|
| Eva | Perfil final maquetado al formato UAB. Descripción del repositorio corregida. Pregunta al tutor sobre el plazo de 6 meses |
| Luis | Una página, no un SAD nuevo: el sistema no se llama “ventas e inventario”. Lista de figuras que hay que renombrar |
| Marializ | Demo local de salud de la API, login y alta de producto. Nada de compras a proveedores |
| Kevin | No más pantallas en Figma. Lista de los frames que ya existen para registrar la venta y para consultar el saldo |
| Christian | 30 frases reales de las entrevistas y 40 palabras comerciales en quechua. Sin entrenar un modelo esta semana |

**Del 23/10 al 11/11.** Este es el tramo que decide si noviembre se puede defender.

| Persona | Entrega |
|---|---|
| Kevin | Proyecto Android que compila. Catálogo y formulario de venta. Modo avión y volver a abrir la app |
| Marializ | Registrar la operación en una transacción, descontar la cantidad y rechazar el mismo identificador si ya llegó |
| Luis | ADR: Spring queda reemplazado por Node. Diagramas C1 y C2 con los nombres nuevos. Casos de uso: “Registrar una venta” y “Consultar el saldo” |
| Christian | Reglas para tres intenciones: anotar, consultar, corregir. Vosk no entra todavía |
| Eva | Guion de la demo de 4 minutos y checklist de lo que no se dice |

**Del 12/11 al 25/11.** Se congela el alcance el 20/11. Los tres días previos a cada defensa no se mergea funcionalidad.

| Persona | Entrega |
|---|---|
| Kevin | Si la docente de TI exige web: una página que inicia sesión y lista el registro del negocio. Si no lo exige: cero páginas nuevas |
| Marializ | Instrucciones para levantar la API y la prueba del identificador repetido |
| Luis | SAD v2 corto, alineado al código que sí corre |
| Christian | Una frase de ejemplo reconocida por reglas, en pantalla, no por un modelo que no se midió |
| Eva | Ensayo. Cada persona explica solo su parte |

---

## 5. Definición de “listo” para el 23 y el 25 de noviembre

Está listo si una persona del equipo puede hacer esto delante del docente, sin narrar un Figma:

1. Poner el teléfono en modo avión.
2. Elegir la plantilla de abarrotes.
3. Anotar una venta por pantalla.
4. Ver el total y el vuelto, y confirmar.
5. Consultar cuánto queda.
6. Cerrar la aplicación, abrirla y ver la misma operación.
7. Si hay tiempo y hay red: enviar la operación y mostrar que el segundo envío no la duplica.

Si el paso 7 no está, se dice “la cola local está diseñada y el envío todavía no”. Eso es mejor que un diagrama que miente.

La voz, el quechua hablado, las cuatro plantillas completas y el SUS no son la demo de noviembre.

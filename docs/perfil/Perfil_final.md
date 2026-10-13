UNIVERSIDAD ADVENTISTA DE BOLIVIA
FACULTAD DE INGENIERÍA
CARRERA DE INGENIERÍA EN SISTEMAS

Prototipo de asistente conversacional offline con procesamiento de lenguaje natural en el dispositivo, configurable por tipo de negocio, para el registro de ventas y la consulta del saldo local de existencias en las MYPES del centro de Vinto, Cochabamba

PERFIL DE TRABAJO DE GRADO

Presentado como requisito para la asignatura de Investigación I

Postulantes

Eva Lizeth Chino Quispe
Christian Gonzales Mamani
Marializ Mamani Quispe
Kevin Jhonathan Rocha Rojas
Luis Gerardo Lazo Bustamante

Docentes

Lic. Ivo Alberto Alabe Perales (Investigación I)
Ing. Víctor Hugo Sarzuri Flores (Arquitectura de Software)

Cochabamba, octubre de 2026

---

CONTROL DE VERSIONES

| Versión | Fecha | Autor | Qué cambió |
|---|---|---|---|
| 1.0 a 7.0 | 20/09/2026 – 29/09/2026 | Equipo | Borradores de causas, citas, objetivo general y factibilidad |
| 8.0 a 10.0 | 12/10/2026 – 13/10/2026 | Equipo | Se probó ampliar el alcance y comprimir el plazo. Esas versiones no se presentan |
| 11.0 | 13/10/2026 | Equipo | Perfil que se presenta. El nombre del trabajo queda en registro de ventas y consulta del saldo. El plazo del Trabajo de Grado pasa a 26 semanas |

---

1. INTRODUCCIÓN

En el centro de abastos de Vinto la venta se anota todavía en un cuaderno. El comerciante mira si le queda producto, saca la cuenta de memoria o con la calculadora, da el vuelto y, al final del día, compara la caja con lo escrito. Cuando el puesto está lleno, esa cuenta se hace mientras otra persona espera.

Los programas de gestión que revisamos están pensados para otro contexto: muchas pantallas, un servidor y conexión permanente. En un teléfono sencillo, y en un momento en el que puede no haber datos, ese diseño no ayuda a anotar la venta ni a saber cuánto queda.

Este perfil propone un prototipo de asistente conversacional, configurable según el tipo de negocio, para esas dos tareas y nada más amplio que eso. El teléfono guarda la operación y responde sin depender de internet en el momento de la atención.

El objeto de estudio es el proceso de registro de ventas y de consulta del saldo local de existencias. El campo de acción es una arquitectura que funciona primero sin conexión, con plantillas según el rubro y con interacción en lenguaje natural, en teléfonos Android de gama baja.

El aporte de este trabajo es juntar, en un solo prototipo y en un teléfono de referencia de 2 GB de RAM, cuatro cosas que las herramientas revisadas no reúnen: anotar la venta en el propio equipo, calcular el total y el vuelto, consultar el saldo aunque no haya señal, y hacerlo por voz en castellano o por texto en castellano y en un quechua comercial acotado. Las plantillas importan porque no se vende igual en una tienda de abarrotes, una pensión, una broastería y una ferretería. El aporte se va a medir en el teléfono y con una prueba de uso pequeña. No se va a presentar como un ahorro ya calculado.

2. ANTECEDENTES

2.1. El centro de abastos

La Asociación de Comerciantes del Centro de Vinto estima 320 comerciantes minoristas en su padrón interno de 2025 [1]. En los puestos se vende poco dinero por vez, casi siempre en efectivo, y la gente rota. Entre los rubros hay tiendas de abarrotes, pensiones, broasterías y ferreterías. Hay otros giros; este trabajo no los cubre.

La ATT publica mapas de cobertura móvil del país y del departamento [2]. No encontramos una medición hecha dentro del centro de abastos. Por eso la cobertura irregular se toma como referencia, no como un dato ya cerrado de Vinto. Si la señal falla, o si no hay saldo de datos, el registro no puede quedarse esperando la red. Esa es una decisión de diseño. La fase de requerimientos va a contrastarla en el lugar.

Sobre el idioma, el INE publicó, con el Censo de 2012, la lengua en la que la población de cada departamento aprendió a hablar [3]. Esos cuadros no describen, por sí solos, a los comerciantes de Vinto en 2026. El castellano y el quechua del centro se van a contrastar con las entrevistas. No citamos cifras del Censo 2024 mientras no tengamos el boletín municipal abierto delante.

El teléfono de referencia tiene 2 GB de RAM. El prototipo tiene que poder usarse en ese equipo. No significa “hace falta un teléfono de más de 2 GB”. El relevamiento de hardware va a confirmar o corregir este supuesto.

2.2. Cómo se vende hoy

La secuencia es corta. La persona pide un producto. El comerciante revisa el puesto o el cuaderno. Calcula el monto y el vuelto. Anota la venta. Al cerrar, compara el efectivo con el cuaderno.

En el tercer trimestre de 2026 hablamos con 15 comerciantes del centro. No fue una muestra al azar. De esas 15 personas, 13 usaban solo el cuaderno para anotar las ventas, 11 contaron al menos un error de cuenta por semana y 8, bilingües en quechua, dijeron que les cuesta usar programas que están solo en castellano. Esto describe a quienes conversaron con nosotros. No describe a los 320.

Preguntar “¿cuánto me queda?” también se resuelve mirando la mercadería o la hoja. El cuestionario y el consentimiento van en el Anexo B.

2.3. Qué hacen tres herramientas que ya existen

Revisamos tres herramientas. Miramos qué registran, dónde se instalan, cuánto cuestan y qué pasa cuando no hay internet. El resumen está en la Tabla 1.

| Herramienta | Qué hace y dónde vive | Costo | Sin conexión |
|---|---|---|---|
| Odoo / ERPNext | Ventas, existencias y contabilidad, normalmente en un servidor, con alguien que lo administre [4], [5] | Hay ediciones libres; lo comercial y el alojamiento se pagan | Odoo documenta un modo sin conexión, limitado [4] |
| Loyverse POS | Punto de venta en el teléfono | La base es gratuita; varias funciones se pagan | Deja anotar la venta sin red, pero la documentación dice que en ese modo no muestra cuánto queda [6] |
| Zoho Inventory | Existencias ligadas a la nube de Zoho | Planes de pago [7] | Pensado para usarse en línea; lo que hace sin red depende de la versión que leímos [7] |

Tabla 1. Tres herramientas revisadas

En esas versiones no vimos quechua, ni una conversación por voz o por texto para anotar la venta y preguntar el saldo sin red, ni una configuración inicial para el menú de una pensión, el cuarto de pollo o la cuartilla. Esto vale solo para lo que revisamos. No es una frase sobre todo el mercado.

Loyverse sirve para un punto concreto: se puede guardar la venta sin internet y, aun así, perder de vista el saldo [6]. No explica por sí solo la costumbre de Vinto. Allí el cuaderno sigue siendo el registro.

2.4. En qué nos apoyamos para construir

Todavía no medimos nosotros la voz ni la intención en el mercado. La viabilidad preliminar sale de piezas con licencia pública y documentación que sí podemos abrir. Vosk publica modelos pequeños de castellano para reconocimiento de voz sin conexión, con Apache License 2.0 [8]. TensorFlow Lite permite correr un clasificador liviano en el teléfono, también con Apache 2.0 [9]. En Android, lo habitual para guardar datos en el equipo es SQLite. La percepción de uso se va a mirar con la escala SUS [10]. Para hablar de calidad del producto vamos a usar tres características de ISO/IEC 25010: que haga lo que debe, que responda con un costo de recursos razonable y que se pueda usar [11].

No citamos informes que el equipo no haya leído. Si más adelante entra un artículo sobre quechua o sobre modelos en el teléfono, primero se abre el PDF y después se agrega a esta lista.

3. SITUACIÓN PROBLEMÁTICA

Los porcentajes son solo de las 15 entrevistas. La lectura de las herramientas va aparte, para no mezclar las fuentes.

Anotar en el cuaderno y calcular de memoria, o con la calculadora, deja la caja expuesta a errores. Once de las quince personas reportaron al menos uno por semana. Al cierre, el efectivo se concilia releyendo esas hojas.

El saldo se sabe mirando el producto o la libreta. Mientras se atiende, no hay una consulta guardada en el teléfono. Si se corta la señal, pasa lo mismo. Loyverse muestra un caso parecido en su propia documentación: la venta se guarda sin red y el saldo no se muestra [6].

Los programas que vimos están en castellano y no traen armada la forma de vender de cada rubro. Ocho de las quince personas bilingües dijeron que les cuesta operar sistemas solo en castellano. Eso es lo que dijeron ellas. Otra cosa, distinta, es que Odoo, Loyverse y Zoho no traen listo el menú de una pensión, el cuarto de pollo ni la cuartilla. Unas piden servidor, otra está pensada para la nube, y la que trabaja sin red no muestra el saldo.

| Causa | Qué proceso toca | Qué se observa | De dónde sale |
|---|---|---|---|
| Anotación manual y cálculo de memoria o con calculadora | Anotar la venta y cerrar la caja | 11 de 15 reportaron al menos un error de cuenta por semana | Entrevistas |
| El saldo está en el cuaderno, en la memoria o en la revisión del puesto. Una herramienta revisada no muestra el saldo sin red | Consultar y actualizar cuánto queda | No hay una consulta inmediata en el teléfono, ni cuando falta la red | Entrevistas y Loyverse [6] |
| Interfaces solo en castellano, y herramientas que no traen las unidades ni el cobro del rubro | Hablar con el programa al vender y al consultar | 8 de 15 bilingües en quechua reportaron dificultad. El desajuste de unidades sale de la Tabla 1, no de ese porcentaje | Entrevistas y Tabla 1 |

Tabla 2. Causas, procesos y efectos

4. FORMULACIÓN DEL PROBLEMA

El uso de procedimientos manuales para anotar las ventas y calcular de memoria, la verificación de existencias mirando el puesto o el cuaderno, el uso de interfaces comerciales disponibles solo en castellano y la rigidez de esas herramientas frente a las unidades y al cobro de cada rubro, en el proceso de venta y de consulta del saldo de las MYPES del centro de Vinto, Cochabamba, provocan errores de cuenta que se repiten en el cierre de caja, dependencia de la memoria o de la revisión física para saber cuánto queda, dificultad de uso en comerciantes bilingües en quechua y un desajuste entre la herramienta y la forma de vender del rubro, entre octubre de 2026 y abril de 2027.

La estructura queda así:

- Causa: anotación y cálculo manuales; existencias revisadas a ojo o en el cuaderno; interfaces solo en castellano; herramientas rígidas frente a las unidades y al cobro de cada rubro.
- Término de relación: el proceso de venta y de consulta del saldo.
- Efecto: errores de cuenta en el cierre; dependencia de la memoria o de mirar el puesto para saber cuánto queda; dificultad de uso en comerciantes bilingües en quechua; desajuste entre la herramienta y la forma de vender.
- Lugar y tiempo: MYPES del centro de Vinto, Cochabamba, de octubre de 2026 a abril de 2027.

No está formulado como pregunta, ni como “falta un sistema”.

5. OBJETIVO GENERAL

Desarrollar un prototipo de asistente conversacional offline con procesamiento de lenguaje natural en el dispositivo, configurable por tipo de negocio, para el registro de ventas y la consulta del saldo local de existencias en las MYPES del centro de Vinto, Cochabamba.

6. OBJETIVOS ESPECÍFICOS

OE1. Elicitar y documentar los requerimientos funcionales, técnicos y de interacción de las MYPES elegidas: cómo venden, cómo consultan el saldo, qué teléfono tienen, qué idioma necesitan y de qué rubro son.

OE2. Elaborar un corpus comercial local en castellano y en quechua escrito, con un vocabulario cerrado, las unidades que sí se usan y las frases propias de cada rubro.

OE3. Seleccionar y adaptar modelos abiertos para reconocer intenciones en castellano y en quechua escrito, y para reconocer voz en castellano, revisando la licencia y probando si la inferencia cabe en el teléfono de referencia.

OE4. Diseñar la arquitectura que guarda primero en el teléfono, el modelo SQLite y las plantillas de abarrotes, pensión, broastería y ferretería.

OE5. Implementar el prototipo móvil para registrar la venta por voz en castellano y por texto en castellano y quechua acotado, calcular el total y el vuelto, actualizar el saldo local, permitir también el registro por pantalla y mostrar un resumen de la jornada con los datos de ese teléfono.

OE6. Validar el prototipo con revisión de expertos y con una prueba de uso con participantes elegidos, y dejar escrito qué se pudo observar y qué no.

| OE | Qué se hace | Qué queda | Qué causa atiende |
|---|---|---|---|
| OE1 | Instrumento, entrevistas, teléfonos, señal y frases | Informe de requerimientos | Las tres, como base |
| OE2 | Frases, vocabulario quechua, etiquetas | Corpus etiquetado | El idioma y las unidades del rubro |
| OE3 | Comparar modelos, leer licencias, probar en el teléfono de 2 GB | Informe técnico y modelos elegidos | El idioma y el error al anotar de oído o por texto |
| OE4 | Tablas, flujo de la venta, descuento del saldo, plantillas | Arquitectura y plantillas | El saldo, el error de caja y el desajuste de rubro |
| OE5 | Construir por partes: guardar, calcular, confirmar, texto, voz, resumen | Aplicación instalable | El error de caja, el saldo y el idioma |
| OE6 | Expertos y prueba de uso | Informe de validación | Verificar las tres, sin generalizar |

Tabla 3. Objetivos, productos y causas

Ningún objetivo abre otro problema. No hay pronóstico de demanda, ni puntaje de clientes, ni cobro por QR.

7. ALCANCE

El trabajo diseña, construye y evalúa un prototipo móvil, configurable por rubro, para registrar ventas, consultar el saldo guardado en ese teléfono y ver un resumen de lo ya anotado ahí. Calcula el total y el vuelto. Antes de guardar, pide confirmación del producto, la cantidad y el monto. Guarda la operación en el equipo y descuenta lo vendido del saldo local.

Las plantillas iniciales son cuatro. Abarrotes: unidad, docena y cuartilla, al contado. Pensión: menú del día, con un precio fijo. Broastería: cuarto, medio y entero, más los acompañamientos. Ferretería: unidad, metro y kilo. Cada una trae categorías, productos de ejemplo, unidades y la forma de cobro habitual. La persona puede agregar, cambiar o quitar productos. El fiado, si el informe de requerimientos lo confirma, será solo una marca de “todavía no pagó” en el mismo teléfono. No es un módulo de crédito.

Hay dos maneras de anotar la misma venta. En la pantalla, se elige el producto, y se puede cargar foto, precio y categoría. En la conversación, se dicta o se escribe. La voz es solo en castellano. El texto acepta castellano y las palabras quechuas que estén en el corpus. Las dos maneras conviven. La voz y la intención se procesan en el teléfono, si la prueba en el equipo de 2 GB lo permite. Los datos locales van en SQLite.

El resumen de jornada muestra, con lo guardado en ese teléfono, qué se vendió más en el día, cuánto se vendió en el día y un aviso si el saldo baja de un número que el comerciante define. No predice la semana que viene.

Queda prevista una cola local de eventos, con un identificador único por operación, para consolidar después. En el proyecto que compartimos con Arquitectura de Software y Tecnologías en Internet se construye una API con Node.js, TypeScript, Express y PostgreSQL. Esa API no hace falta para anotar ni para consultar el saldo. La prueba de uso se hace sin conexión. No vamos a decir que dos teléfonos quedan con el mismo saldo.

Si Tecnologías en Internet pide una pantalla web, será una consulta del mismo negocio, aparte de la pregunta de investigación, sin analítica. Si pide MongoDB, será un ejercicio separado, no una segunda base del asistente.

Entregamos el informe de requerimientos, el corpus y el informe de los modelos, la arquitectura con las plantillas, la aplicación Android, el informe de validación y el documento final.

Las pruebas miran si el recorrido funciona, si las partes conversan entre sí, cuánto tarda y cuánta memoria usa en el teléfono de referencia, y si las personas logran usarlo. Como punto de partida, no como resultado ya obtenido, buscamos una respuesta del recorrido principal de hasta 3 segundos en ese teléfono, un F1 de intenciones de 0,85 en el vocabulario cerrado y un SUS de al menos 68 en la muestra de la prueba. Si la medición obliga a mover esos números, se documenta el cambio. No se reportan como logrados antes de medirlos. El grupo de la prueba es chico y no representa a todas las MYPES de Vinto.

8. LÍMITES

El prototipo no administra un almacén. No lleva lotes, costos, proveedores, compras, contabilidad ni facturación electrónica. No reconoce voz en quechua ni en aimara. El quechua entra solo por texto, y solo con las palabras definidas para el prototipo. No entran otras lenguas.

El saldo que evaluamos es el de un teléfono. La API no promete que varios puestos queden iguales. No hay pronóstico, ni puntaje de clientes, ni lector de códigos, ni impresora, ni pasarela de pago, ni cobro por QR.

Se prueba en el teléfono de referencia y en los equipos que tengamos. No prometemos el mismo resultado en otros modelos. Las entrevistas y la prueba de uso no son muestras al azar. Hay cuatro plantillas; otros rubros quedan para después. El cifrado de la base local se decide durante la construcción. En este perfil no está declarado como hecho.

9. JUSTIFICACIÓN

9.1. Social

El prototipo busca que el comerciante calcule y anote menos mientras atiende, y que pueda ver el saldo aunque no tenga datos. La voz en castellano y el texto quechua responden a lo que una parte de las personas entrevistadas contó. No damos por hecho que las dos lenguas queden cubiertas igual: la voz no va en quechua, y eso se dice desde ahora. Las plantillas evitan empezar de cero un catálogo que no se parece al puesto. Si la atención al cliente se vuelve más rápida, se verá en la prueba. No se afirma antes. El asistente apoya a quien vende. No lo reemplaza.

9.2. Técnica

El aporte técnico está en la introducción. No es otra aplicación de menús: la conversación, el quechua escrito acotado y la plantilla del rubro van juntos, con el límite real de memoria [8], [9]. Si el corpus es chico o el teléfono no aguanta el modelo, el resultado se informa así, sin estirarlo.

9.3. Económica

Se usan piezas libres o gratuitas, en teléfonos que la gente ya tiene, si el relevamiento confirma que los tienen. No pedimos que el puesto compre un equipo nuevo para participar en la prueba. Calcular el total y el vuelto baja la ocasión del error. No convertimos eso en bolivianos ahorrados. Sin conexión no hace falta un plan de datos. El costo y el beneficio con uso real quedan para después.

10. FACTIBILIDAD

10.1. Técnica

Hay computadoras de desarrollo y teléfonos Android 8 o superior, uno de ellos con 2 GB de RAM. El modelo definitivo se elige cuando se pruebe en ese teléfono, no en este papel.

| Pieza | Candidata | Licencia |
|---|---|---|
| Datos en el teléfono | SQLite, con Room | Dominio público / Apache 2.0 |
| Voz en castellano | Vosk, modelo compacto | Apache 2.0 [8] |
| Intención | Clasificador liviano con TensorFlow Lite. Si no cabe, reglas sobre el vocabulario cerrado | Apache 2.0 [9] |
| Aplicación | Kotlin y Jetpack Compose | Apache 2.0 |
| Plantillas | Archivos JSON | Formato abierto |
| Consolidación, solo con red | Node.js, TypeScript, Express y PostgreSQL | MIT / PostgreSQL |

Tabla 4. Tecnologías candidatas

Si el modelo no entra con holgura en 2 GB, se usa un clasificador por reglas. El objetivo no cambia y no aparece un servidor de lenguaje.

10.2. Económica

El equipo paga los costos de campo. Las licencias de la Tabla 4 no se cobran para este alcance. La API, en las pruebas, corre en una máquina local o en un nivel gratuito. No hace falta para usar el asistente sin red.

| Concepto | Bs |
|---|---|
| Transporte | 250 |
| Impresiones y material | 80 |
| Datos, solo para documentar y probar | 70 |
| Total | 400 |

Tabla 5. Costos que asume el equipo

10.3. Operativa

Quienes conversaron con nosotros ya sienten el registro actual como una carga. Eso no significa que vayan a adoptar el prototipo. La disposición se pregunta en la prueba. Hay contacto preliminar con comerciantes del centro y con la ACCV. En la elicitación y en la prueba de uso se va a medir cuánto tarda una persona en aprender el recorrido, si tiene Android, y si acepta la voz y el texto quechua.

Hay cinco efectos operativos que queremos observar, no declarar de antemano: cerrar la venta sin recalcular de memoria; no gastar en equipo nuevo; ver el saldo del teléfono; entenderse con frases del rubro; y dejar la jornada anotada sin montar un servidor en el puesto.

10.4. Ética

Antes de entrevistar, grabar o probar, pedimos consentimiento, escrito o verbal y registrado. El audio del corpus se anonimiza, se usa solo aquí y se borra si la persona lo pide. Al repositorio no entran datos de comerciantes, contraseñas ni voces identificables.

11. CRONOGRAMA

El Trabajo de Grado dura 26 semanas, del 20 de octubre de 2026 al 16 de abril de 2027. Entra en el rango de 6 a 12 meses. Investigación I termina antes: entre octubre y diciembre de 2026 se evalúa y se defiende este perfil. Esa defensa no es el informe final ni la prueba con comerciantes. En noviembre, Arquitectura de Software y Tecnologías en Internet ven el núcleo de la fase 3. No ven todavía la voz cerrada ni la validación de campo.

Se construye por partes. No se integra la voz hasta que la venta por pantalla se guarde y siga ahí después de cerrar la aplicación, sin red.

| Fase | Qué se hace | OE | Fechas |
|---|---|---|---|
| 1. Perfil y requerimientos | Cerrar este documento, ordenar las 15 entrevistas y completar teléfonos y frases que falten | OE1 | 20/10/2026 – 02/11/2026 |
| 2. Arquitectura | SQLite, plantillas, cola de eventos, diagramas iguales al código, contrato de la API | OE4 | 03/11/2026 – 23/11/2026 |
| 3. Núcleo | Catálogo, venta, total, vuelto, confirmación, saldo, reapertura sin red, envío sin duplicar | OE5, base | 03/11/2026 – 14/12/2026 |
| 4. Corpus, texto y rubros | Frases por rubro, quechua escrito, cuatro plantillas, resumen del día | OE2 y OE5 | 15/12/2026 – 25/01/2027 |
| 5. Voz | Elegir el modelo, medirlo en el teléfono de 2 GB e integrarlo solo si cabe | OE3 y OE5 | 26/01/2027 – 22/02/2027 |
| 6. Validación | Expertos y prueba de uso. Tiempos, errores y SUS | OE6 | 23/02/2027 – 22/03/2027 |
| 7. Documento final | Redacción, IEEE, observaciones y defensa del trabajo | Todos | 23/03/2027 – 16/04/2027 |

Tabla 6. Cronograma del Trabajo de Grado

Hitos que no se mueven de nombre, aunque una fecha de aula sí pueda moverse:

- 22/10/2026: se evalúa este perfil en Investigación I.
- 23/11/2026: hay arquitectura coherente con el código y un núcleo que se puede mostrar sin red. Es un avance, no el cierre.
- 14/12/2026: la venta queda guardada, el saldo cambia y repetir el mismo identificador no duplica la operación.
- 25/01/2027: están las cuatro plantillas y el texto en castellano y en quechua acotado.
- 22/02/2027: informe de la voz en el teléfono de referencia, integrada o reemplazada por reglas, con el motivo escrito.
- 22/03/2027: informe de validación.
- 16/04/2027: documento final.

Los riesgos que ya conocemos: el ruido del mercado, y por eso siempre hay texto; un corpus quechua corto, y por eso se achica el vocabulario antes que el castellano; un modelo que no quepa en 2 GB, y por eso existen las reglas; pocos participantes en la prueba, con un piso de 8 personas y el límite escrito en el informe; y los exámenes, así que tres días antes de cada defensa de materia no se integra nada nuevo.

12. REFERENCIAS

[1] Asociación de Comerciantes del Centro de Vinto (ACCV), “Registro de comerciantes minoristas,” documento interno, Vinto, Bolivia, 2025.

[2] Autoridad de Regulación y Fiscalización de Telecomunicaciones y Transportes (ATT), “Mapa de cobertura de redes móviles,” 2025. [En línea]. Disponible: https://www.att.gob.bo/telecomunicaciones/mapa-de-cobertura. Consultado: 6 oct. 2026.

[3] Instituto Nacional de Estadística (INE), “Idioma que aprendió a hablar en su niñez, según departamento. Censo de Población y Vivienda 2012,” La Paz, Bolivia. [En línea]. Disponible: https://www.ine.gob.bo. Consultado: 6 oct. 2026.

[4] Odoo S.A., “Offline mode,” Odoo Documentation, versión SaaS 19.2. [En línea]. Disponible: https://www.odoo.com/documentation/saas-19.2/es/applications/general/offline_mode.html. Consultado: 6 oct. 2026.

[5] ERPNext, “System Requirements,” documentación versión 14. [En línea]. Disponible: https://docs.erpnext.com/docs/v14/user/manual/en/introduction/system-requirements. Consultado: 6 oct. 2026.

[6] Loyverse, “Offline use of Loyverse POS,” Loyverse Help Center. [En línea]. Disponible: https://help.loyverse.com/help/offline-work-of-pos. Consultado: 6 oct. 2026.

[7] Zoho Corporation, “Zoho Inventory: Features and Pricing.” [En línea]. Disponible: https://www.zoho.com/inventory/features.html. Consultado: 6 oct. 2026.

[8] Alpha Cephei, “Vosk Speech Recognition Toolkit.” [En línea]. Disponible: https://alphacephei.com/vosk/. Consultado: 13 oct. 2026.

[9] Google, “TensorFlow Lite.” [En línea]. Disponible: https://www.tensorflow.org/lite. Consultado: 13 oct. 2026.

[10] J. Brooke, “SUS: A ‘quick and dirty’ usability scale,” in Usability Evaluation in Industry, P. W. Jordan, B. Thomas, B. A. Weerdmeester e I. L. McClelland, Eds. London, U.K.: Taylor & Francis, 1996, pp. 189–194.

[11] International Organization for Standardization, Systems and software engineering — Systems and software Quality Requirements and Evaluation (SQuaRE) — Product quality model, ISO/IEC 25010.

Antes de imprimir, alguien del equipo abre cada enlace y comprueba que la frase que vamos a decir en la defensa está en esa página. Si la página cambió, se actualiza la fecha de consulta o se quita la cita. No entra ninguna referencia que un asistente haya sugerido y nadie haya leído.

ANEXOS

Anexo A. Dibujo del proceso actual, con los cinco pasos del apartado 2.2.

Anexo B. Cuestionario usado con las 15 personas y constancia del consentimiento.

Anexo C. Nota de la ACCV, solo si ya existe. Si no existe, este anexo no se inventa.

Anexo D. Cómo conversa este cronograma con las entregas de noviembre de Arquitectura de Software y de Tecnologías en Internet. Esas entregas cubren las fases 2 y 3. No reemplazan la voz, la prueba de uso ni el documento final.

# 01 Proyecto y alcance

## Identificación

**Título de trabajo:** Asistente conversacional offline con procesamiento de lenguaje natural en el dispositivo para la gestión automatizada de ventas e inventario en MYPES del centro de Vinto, Cochabamba.

**Equipo:** Eva Lizeth Chino Quispe (liderazgo, requisitos y QA documental); Christian Gonzales Mamani (IA y pipeline conversacional); Marializ Mamani Quispe (backend, seguridad y persistencia); Kevin Jhonathan Rocha Rojas (UX/UI); Luis Gerardo Lazo Bustamante (arquitectura C4 y QA técnico).

## Producto y problema

El proyecto propone un prototipo que permita gestionar operaciones comerciales mediante una interfaz sencilla, conserve información localmente cuando no existe Internet y sincronice después con un servicio remoto. El problema y las afirmaciones sobre alfabetización digital, conectividad, recursos de los teléfonos, errores y pérdida de datos deben sustentarse con las entrevistas, observación y fuentes citadas en el Perfil. No se deben presentar las descripciones del perfil como mediciones primarias sin evidencia.

## MVP de desarrollo

1. Registrar y consultar productos localmente.
2. Crear una venta con productos conocidos y confirmación antes de guardar.
3. Mantener la venta y el movimiento local tras reiniciar la app sin red.
4. Sincronizar la operación a una API Node.js/Express y PostgreSQL al recuperar conexión.
5. Reintentar el mismo identificador sin producir ventas duplicadas.
6. Incorporar texto conversacional y voz offline después del recorrido básico.

Las compras, fiados, reportes, onboarding por voz, NLU entrenado y validación de usabilidad se implementan conforme al cronograma y capacidad; su prioridad y aceptación requieren congelar alcance con tutor. Facturación electrónica SIN, pagos, iOS, escritorio y voz continua están excluidos según el Perfil compartido.

## Decisión de stack

La línea base unificada solicitada es **Kotlin/Compose + Room/SQLite + Node.js/TypeScript/Express + PostgreSQL**. PostgreSQL es el almacén remoto autoritativo; SQLite resuelve persistencia local. El stack anterior del Perfil y SAD usa Spring Boot/PostgreSQL, por lo que la sustitución de Spring debe quedar documentada como ADR y propagarse con aprobación docente. El sílabo de Tecnologías en Internet incluye MongoDB/Mongoose; PostgreSQL no satisface por sí solo ese resultado de aprendizaje. Eva confirmará si MongoDB se requiere dentro del producto o puede demostrarse mediante un ejercicio acotado separado.

## Fuentes normativas del proyecto

El Perfil de Grado, el SAD, la entrega de Tecnologías en Internet, el cronograma de ejecución, el Gantt, los sílabos y las rúbricas son fuentes distintas. Si difieren, registrar la discrepancia y consultar al docente. El manual operativo está en la raíz del repositorio y no sustituye ninguno de los documentos oficiales.

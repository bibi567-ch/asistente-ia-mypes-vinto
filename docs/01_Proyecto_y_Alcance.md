# 01 Proyecto y alcance

## Identificación

**Título:** Prototipo de asistente conversacional offline con procesamiento de lenguaje natural en el dispositivo, configurable por tipo de negocio, para el registro de ventas y la consulta del saldo local de existencias en las MYPES del centro de Vinto, Cochabamba.

**Equipo:** Eva Lizeth Chino Quispe (liderazgo, requisitos y QA documental); Christian Gonzales Mamani (IA y pipeline conversacional); Marializ Mamani Quispe (backend, seguridad y persistencia); Kevin Jhonathan Rocha Rojas (UX/UI); Luis Gerardo Lazo Bustamante (arquitectura C4 y QA técnico).

El perfil completo está en [Perfil_final.md](perfil/Perfil_final.md). Si este resumen y el perfil se contradicen, manda el perfil.

## Qué se construye

Un prototipo para anotar una venta y consultar cuánto queda, primero en el teléfono. La consolidación en la API ocurre después, cuando hay red, y no hace falta para atender.

No es un sistema de almacén. No lleva compras, proveedores, lotes, costos ni facturación.

## Recorrido que tiene que funcionar antes que nada

1. Guardar y consultar productos en el teléfono.
2. Anotar una venta de productos ya cargados y confirmar antes de guardar.
3. Cerrar la aplicación, sin red, abrirla y encontrar la misma venta y el mismo saldo.
4. Enviar esa operación a la API Node.js/Express y a PostgreSQL cuando vuelva la red.
5. Reintentar con el mismo identificador y no duplicar la venta.
6. Recién después, texto y voz.

Quedan fuera: compras a proveedores, lotes, costos, facturación, voz en quechua, aimara, lector de códigos, impresora y cobro por QR.

## Decisión de stack

La línea base es **Kotlin/Compose + Room/SQLite + Node.js/TypeScript/Express + PostgreSQL**. PostgreSQL es el almacén remoto. SQLite resuelve la persistencia en el teléfono. El SAD v1.1 habla de Spring Boot; esa versión se conserva como antecedente. La sustitución por Node se documenta en un ADR y no se hace pasar por si el SAD v1.1 siempre hubiera sido Node. Tecnologías en Internet incluye MongoDB/Mongoose. PostgreSQL no cubre ese punto. Si la docente lo exige, se entrega un ejercicio aparte, no una segunda base del asistente.

## Fuentes

El perfil, el SAD, la entrega de Tecnologías en Internet y los sílabos son fuentes distintas. Si difieren, se anota la diferencia y se consulta al docente. Desde el 13/10/2026 el nombre vigente del sistema es el del perfil, no “gestión de ventas e inventario”. Ver [Nota_de_nombre.md](Nota_de_nombre.md).

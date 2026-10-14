# Proyecto y alcance

## Identificación

**Título:** Prototipo de asistente conversacional offline con procesamiento de lenguaje natural en el dispositivo, configurable por tipo de negocio, para el registro de ventas y la consulta del saldo local de existencias en las MYPES del centro de Vinto, Cochabamba.

**Equipo:** Eva Lizeth Chino Quispe, Christian Gonzales Mamani, Marializ Mamani Quispe, Kevin Jhonathan Rocha Rojas y Luis Gerardo Lazo Bustamante.

El texto completo del perfil está en [Perfil_final.md](perfil/Perfil_final.md). Si este resumen y el perfil no coinciden, manda el perfil.

## De qué se trata

El prototipo anota una venta y muestra cuánto queda de cada producto, en el teléfono y sin internet. El comerciante elige una plantilla según el rubro (abarrotes, pensión, broastería o ferretería) y puede ajustarla.

La copia remota, en la API, llega cuando hay conexión. Atender no depende de esa copia.

## Qué incluye

- Productos guardados en el teléfono.
- Registro de una venta de productos ya cargados, con total y cambio, y confirmación antes de guardar.
- Consulta del saldo local.
- El mismo dato sigue ahí si se cierra la aplicación sin red.
- Envío posterior a la API (Node.js, Express y PostgreSQL) sin duplicar la operación.
- Texto y voz en castellano, y texto con vocabulario acotado en quechua, cuando el registro en pantalla ya funciona.

## Qué no incluye

Compras a proveedores, lotes, costos, facturación, reconocimiento de voz en quechua, aimara, lector de códigos, impresora y cobro por QR.

Descontar lo vendido del saldo del teléfono forma parte de registrar la venta. No abre un módulo de almacén.

## Cómo está construido

La aplicación prevista es Kotlin y Jetpack Compose, con SQLite en el teléfono. La API de esta versión del repositorio es Node.js, TypeScript, Express y PostgreSQL.

El documento de arquitectura v1.1 describe Spring Boot. Esa entrega se conserva. El código actual no usa Spring.

## Nombre

El sistema no se llama “gestión de ventas e inventario”. El nombre vigente es el del título de esta página. El detalle está en [Nota_de_nombre.md](Nota_de_nombre.md).

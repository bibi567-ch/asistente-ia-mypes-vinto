# Proyecto y alcance

## Identificación

**Título:** Prototipo de asistente conversacional offline con procesamiento de lenguaje natural en el dispositivo, configurable por tipo de negocio, para el registro de ventas y la consulta del saldo local de existencias en las MYPES del centro de Vinto, Cochabamba.

**Equipo:** Eva Lizeth Chino Quispe, Christian Gonzales Mamani, Marializ Mamani Quispe, Kevin Jhonathan Rocha Rojas y Luis Gerardo Lazo Bustamante.

> El **Perfil de Investigación / Trabajo de Grado** es un documento académico **aparte**. No forma parte de este repositorio. Si el perfil y este resumen de alcance no coinciden, manda el perfil (fuera del repo) y actualiza este archivo para que el alcance técnico quede alineado.

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

## Cómo está construido (stack vigente)

| Capa | Tecnología | Notas |
|---|---|---|
| Cliente | Kotlin + Jetpack Compose | App Android nativa |
| Persistencia local | Room / SQLite | Offline-first |
| Backend | Node.js 24 + TypeScript + Express | API local de consolidación |
| Persistencia central | PostgreSQL | Migraciones en `backend/db/migrations` |
| Contenedores de desarrollo | Docker Compose | Solo entorno local |

El documento de arquitectura **SAD v1.1** describe Spring Boot en la entrega histórica. Esa entrega se conserva. El **código actual** no usa Spring; la arquitectura vigente es Node.js/Express/PostgreSQL + Android/Room. Debe emitirse un SAD v2 coherente con el código (ver `docs/03_SAD_Arquitectura.md`).

## Nombre

El sistema no se llama “gestión de ventas e inventario”. El nombre vigente es el del título de esta página. El detalle está en [Nota_de_nombre.md](Nota_de_nombre.md).

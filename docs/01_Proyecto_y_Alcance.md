# Proyecto y alcance

## Identificación

**Título:** Prototipo de asistente conversacional offline con procesamiento de lenguaje natural en el dispositivo, configurable por tipo de negocio, para el registro de ventas y la consulta del saldo local de existencias en las MYPES del centro de Vinto, Cochabamba.

**Equipo:** Eva Lizeth Chino Quispe, Christian Gonzales Mamani, Marializ Mamani Quispe, Kevin Jhonathan Rocha Rojas y Luis Gerardo Lazo Bustamante.

El propósito y los límites descritos en esta página constituyen el perfil vigente del proyecto.

## De qué se trata

El prototipo permite registrar ventas y consultar existencias en el teléfono, incluso sin internet. El comerciante puede partir de una de seis plantillas locales —farmacia, pastelería, ferretería, licorería, broastería o minimarket— y ajustar los ejemplos.

La copia remota, en la API, llega cuando hay conexión. Atender no depende de esa copia.

## Qué incluye

- Perfil local del comerciante y acceso mediante identificador y PIN.
- Catálogo local de productos, onboarding por plantillas, ventas manuales, fiados y consultas/reportes locales.
- Asistente de voz en español con Vosk cuando el modelo está disponible; una sugerencia no guarda una venta sin revisión de la persona.
- Operaciones locales y cola Outbox para sincronización diferida con una API Node.js/TypeScript/Express y PostgreSQL, cuando se configura y valida la conexión.
- Asistente Yapay basado en reglas locales para funciones demostrativas; no se conecta a un LLM.

La existencia de una función en el código no demuestra que se haya validado en un teléfono. Consulta el [estado de las pruebas](04_Plan_de_Pruebas.md) y su [bitácora](05_Resultados_de_Pruebas.md).

## Qué no incluye

Compras a proveedores, gestión de lotes/costos, facturación fiscal, reconocimiento de voz en quechua o aimara, lector de códigos, impresora, cobro QR y un frontend web integrado.

Descontar lo vendido del saldo del teléfono forma parte de registrar la venta. No abre un módulo de almacén.

## Cómo está construido

La aplicación Android usa Kotlin y Jetpack Compose; Room/SQLite con SQLCipher conserva datos locales. La API de esta versión usa Node.js 24, TypeScript, Express y PostgreSQL. La configuración, los contratos y los límites están en [Arquitectura actual](02_Arquitectura_SAD.md).

El SAD v1.1 presentado describe Spring Boot. Se conserva como antecedente y no describe el backend actual, que usa Node.js/Express. El código actual no usa Spring.

## Nombre

El nombre descriptivo vigente es “Prototipo de asistente conversacional offline para MYPES de Vinto”, como se indica en el título de esta página. No se presenta como un sistema integral de gestión de ventas e inventario.

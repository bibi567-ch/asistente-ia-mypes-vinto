# Arquitectura actual del sistema

> **Estado del documento:** resumen técnico de la implementación del repositorio; no es una aprobación académica ni reemplaza el SAD entregado.

## Documentos de referencia

El documento académico existente [03_SAD_Arquitectura.md](03_SAD_Arquitectura.md) conserva la versión SAD 1.1. Su descripción tecnológica contiene decisiones históricas de Spring Boot que no corresponden al backend Node.js actual. No presentar esa versión como una descripción fiel del código actual ni modificarla silenciosamente. La actualización integral del SAD y su aprobación académica son trabajo separado.

## Componentes implementados

| Componente | Tecnología | Responsabilidad |
|---|---|---|
| Aplicación móvil | Kotlin, Android, Jetpack Compose | Interfaz, acceso local, inventario, ventas, voz y sincronización; plantillas de negocio y Yapay local simulado |
| Persistencia móvil | Room/SQLite y SQLCipher | Datos locales y cola Outbox para operación offline |
| API | Node.js 24, TypeScript, Express | Registro/autenticación, actualización del negocio, catálogo, lotes offline, facturas simuladas, QR y eventos SSE |
| Persistencia del servidor | PostgreSQL | Usuarios, negocios, productos, transacciones, facturas y QR de pago |
| Automatización backend | GitHub Actions | Migraciones, compilación, chequeo de tipos y pruebas unitarias/de integración |

## Flujos y límites

1. El comerciante entra localmente con carnet, celular o correo y PIN; el PIN puede escribirse o dictarse con el modelo Vosk offline. La autenticación remota es independiente: usa email/contraseña y entrega un JWT. El servidor conserva solo el hash scrypt de la contraseña; Room conserva solo el hash del PIN.
2. El registro guarda primero el perfil personal en Room y, cuando hay conexión, también en PostgreSQL. El negocio remoto se crea provisionalmente y se actualiza con nombre/rubro durante el onboarding cuando existe conexión. Si el alta se hizo offline, Sincronización puede registrar el perfil local al volver a introducir el correo y la contraseña; la contraseña no se persiste en el dispositivo.
3. Las ventas se guardan localmente con sus cambios de stock y operación pendiente dentro de una transacción Room.
4. El worker de sincronización envía el Outbox al backend autenticado. La API procesa lotes con identificadores de operación idempotentes.
5. El catálogo del servidor se descarga de forma paginada; el stock local se conserva durante la sincronización.
6. El asistente de voz solo traslada sugerencias al formulario de venta. No persiste una venta sin confirmación.
7. Yapay agrega productos a Room solo después de confirmar; las consultas y consejos son reglas locales demostrativas, no inferencias de un LLM. El catálogo no conserva costos de compra ni categoría actualmente.
8. La API puede emitir eventos SSE a conexiones autenticadas del mismo negocio. La distribución es en memoria por instancia y no reemplaza el mecanismo paginado de sincronización del móvil.
9. Las facturas devuelven un PDF simulado no fiscal, y el QR se almacena como Base64 con límite de tamaño en PostgreSQL.

## Contratos de API

El backend actual documenta rutas, autenticación, paginación, sincronización y límites de facturación/QR/SSE en [README del backend](../backend/README.md). La configuración y límites del cliente móvil están en [README móvil](../mobile/README.md). Los precios y márgenes de las nuevas plantillas son orientativos: no se encontró el documento comercial fuente en el repositorio.

## Verificación

El workflow [backend.yml](../.github/workflows/backend.yml) publica un resumen verificable de cada ejecución. La compilación Android es una comprobación técnica y no sustituye pruebas funcionales documentadas en dispositivo.

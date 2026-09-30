# Plan de implementación y API

## Objetivo inmediato

Implementar un recorrido vertical: producto → venta manual → persistencia local → API Node.js/Express → PostgreSQL → reintento idempotente → estados de sincronización → parser de texto → voz offline experimental. No comenzar por Gemma, microservicios o una sincronización compleja antes de probar el flujo local. El backend existente es scaffold con health check y conexión a PostgreSQL; todavía no contiene CRUD comercial, autenticación ni sincronización.

## Stack

Node.js + TypeScript + Express para la API; PostgreSQL para datos remotos; Room/SQLite para operación Android local; WorkManager/Outbox para diferir envío. Elegir e instalar una biblioteca para PostgreSQL y migraciones mediante ADR breve. La instrucción de unificación conserva PostgreSQL del Perfil/SAD, sustituye Spring y requiere actualizar C4 y documentación. El módulo Mongo/Mongoose solo se añade si TI lo exige y se acota como evidencia curricular, sin convertirlo en segunda fuente del inventario.

## Primeras etapas

1. Reproducir scaffold y guardar estado actual.
2. Configurar variables de entorno y conexión PostgreSQL local.
3. Migración de negocio/producto y health check de la base.
4. CRUD de producto con validación, paginación y autorización.
5. Venta y líneas transaccionales, precios históricos y movimientos.
6. Autenticación, rol/negocio y pruebas de acceso cruzado.
7. Contrato de sync batch con UUID, idempotencia y resultado por evento.
8. Cliente local Room/SQLite y WorkManager; modo avión y reintentos.
9. Parser textual; Vosk después de probar dispositivo y casos.
10. Seguridad, despliegue, documentación y métricas reproducibles.

## Endpoints iniciales

| Método y ruta | Función | Reglas mínimas |
|---|---|---|
| GET /api/v1/health | Estado del proceso/DB | No exponer secretos; informar dependencia sin credenciales |
| GET /api/v1/products | Consultar catálogo autorizado | Negocio del token, paginación y filtros validados |
| POST /api/v1/products | Crear producto | Nombre, unidad, precio/stock válidos |
| POST /api/v1/sync/operations | Recibir lote Outbox | operationId UUID, versión y payload tipado |
| POST /api/v1/auth/login | Autenticar usuario | Contraseña hasheada, JWT con expiración |

La respuesta de sync informa resultado por operación: accepted, duplicate, rejected o conflict; no confiar en businessId enviado por cliente. Documentar ejemplos JSON, códigos HTTP, errores, autenticación y colección de pruebas.

## Modelo y consistencia

Entidades: Business, User, Product, Sale, SaleLine, InventoryMovement y SyncOperation. Índice único por businessId + operationId. Los precios históricos quedan en SaleLine. Las ventas son hechos inmutables: no resolver concurrencia con Last Write Wins ni descartar eventos; conservarlos y enviar discrepancias de stock a conciliación explícita. Cantidad, precio y dinero tienen representación uniforme; elegir unidad menor entera o DECIMAL y documentarlo. Validar transacción completa, reinicio, retries, errores parciales y token vencido.

## Comandos de desarrollo

Los comandos se escriben una vez seleccionados package manager y librería. El README raíz y backend/README deben reflejar comandos probados en Windows. No copiar secretos a .env.example. Antes de publicar: ejecutar instalación limpia, migraciones, lint/typecheck y pruebas disponibles; si alguno no existe, no declararlo aprobado.

# Plan de implementación y API

## Objetivo inmediato

Implementar un recorrido vertical: catálogo protegido → venta manual → persistencia local → API Node.js/Express → PostgreSQL → reintento idempotente → estados de sincronización → parser de texto → voz offline experimental. No comenzar por Gemma, microservicios o una sincronización compleja antes de probar el flujo local. El primer incremento incluye alta administrativa de propietarios, login y lectura/creación de productos; la integración con PostgreSQL y la prueba de aislamiento entre negocios se ejecutan en CI y deben verificarse localmente.

## Stack

Node.js + TypeScript + Express para la API; PostgreSQL para datos remotos; Room/SQLite para operación Android local; WorkManager/Outbox para diferir envío. PostgreSQL se accede mediante `pg`; las migraciones SQL numeradas se aplican transaccionalmente con `npm run db:migrate`. La instrucción de unificación conserva PostgreSQL del Perfil/SAD, sustituye Spring y requiere actualizar C4 y documentación. El módulo Mongo/Mongoose solo se añade si TI lo exige y se acota como evidencia curricular, sin convertirlo en segunda fuente del inventario.

## Primeras etapas

1. Levantar PostgreSQL local y aplicar migraciones versionadas.
2. Crear propietarios por comando administrativo; no habilitar registro público.
3. Autenticar con contraseña hasheada y token de vida corta.
4. Implementar catálogo de lectura/alta, validación, paginación y aislamiento por negocio.
5. Añadir pruebas de integración con dos negocios y completar edición/borrado.
6. Implementar venta y líneas transaccionales, precios históricos y movimientos.
7. Añadir recepción de Outbox con UUID idempotente y resultado por evento.
8. Crear cliente local Room/SQLite y WorkManager; verificar modo avión y reintentos.
9. Añadir parser textual; Vosk después de probar dispositivo y casos.
10. Revisar seguridad, despliegue, documentación y métricas reproducibles.

## Endpoints iniciales

| Método y ruta | Función | Reglas mínimas |
|---|---|---|
| GET /api/v1/health | Estado del proceso/DB | No exponer secretos; informar dependencia sin credenciales |
| POST /api/v1/auth/login | Autenticar propietario | Usuario creado administrativamente; contraseña hasheada; JWT expira en 15 minutos |
| POST /api/v1/auth/change-password | Cambiar clave del usuario autenticado | Verificar clave actual y exigir nueva clave de al menos 12 caracteres |
| GET /api/v1/products | Consultar catálogo autorizado | Negocio del token; paginación validada; no aceptar businessId del cliente |
| POST /api/v1/products | Crear producto | Nombre, unidad y precio decimal validados; unicidad por negocio |
| POST /api/v1/sync/operations | Recibir lote Outbox | operationId UUID, versión y payload tipado |

**Implementado como primer incremento:** autenticación local, comando de alta administrativa y lectura/creación de productos. La verificación con base de datos y el aislamiento cruzado siguen pendientes antes de considerarlo listo para despliegue.

La respuesta de sync informa resultado por operación: accepted, duplicate, rejected o conflict; no confiar en businessId enviado por cliente. Documentar ejemplos JSON, códigos HTTP, errores, autenticación y colección de pruebas.

## Modelo y consistencia

Entidades: Business, User, Product, Sale, SaleLine, InventoryMovement y SyncOperation. Índice único por businessId + operationId. Los precios históricos quedan en SaleLine. Las ventas son hechos inmutables: no resolver concurrencia con Last Write Wins ni descartar eventos; conservarlos y enviar discrepancias de stock a conciliación explícita. Cantidad, precio y dinero tienen representación uniforme; elegir unidad menor entera o DECIMAL y documentarlo. Validar transacción completa, reinicio, retries, errores parciales y token vencido.

## Comandos de desarrollo

Desde `backend/`: `npm ci`, `npm run db:migrate`, `npm run build`, `npm run typecheck:test`, `npm test` y `npm run test:integration`. La prueba de integración requiere PostgreSQL; el workflow usa una instancia efímera de PostgreSQL. No copiar secretos a `.env.example`. Antes de publicar, registrar qué validaciones se ejecutaron realmente y su resultado.

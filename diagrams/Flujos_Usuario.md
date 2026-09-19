# Flujos principales de usuario

## Flujo 1: Registrar una venta

1. El comerciante inicia sesión.
2. Accede a **Registrar venta**.
3. Ingresa productos, cantidades y tipo de pago.
4. El sistema valida la información.
5. Muestra un resumen para confirmación.
6. El comerciante confirma.
7. El sistema registra la venta y actualiza el inventario local.
8. La operación queda marcada como pendiente de sincronización si no existe conexión.

## Flujo 2: Consultar inventario

1. El comerciante abre **Inventario**.
2. Busca un producto por nombre o categoría.
3. El sistema muestra stock disponible y estado del producto.
4. Si no existen resultados, se presenta un estado vacío con orientación para realizar otra búsqueda.

## Flujo 3: Sincronizar operaciones

1. El sistema detecta conexión disponible.
2. Consulta la cola de operaciones pendientes.
3. Envía operaciones al backend usando identificadores idempotentes.
4. Procesa la respuesta.
5. Marca como sincronizadas las operaciones confirmadas.
6. Muestra errores recuperables y conserva las operaciones no sincronizadas.

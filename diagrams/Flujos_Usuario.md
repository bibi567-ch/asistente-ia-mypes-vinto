# Flujos principales de usuario

## CU-01: Registrar una venta

1. El comerciante inicia sesión o utiliza la sesión local válida.
2. Accede a **Registrar venta**.
3. Ingresa productos y cantidades mediante voz o teclado.
4. El sistema interpreta o valida la información.
5. Muestra un resumen editable para confirmación.
6. El comerciante confirma o cancela.
7. Si confirma, el sistema registra la venta y actualiza el inventario local.
8. Si no existe conexión, la operación queda pendiente de sincronización.

## CU-02: Consultar inventario

1. El comerciante abre **Inventario** o realiza una consulta compatible.
2. Busca un producto.
3. El sistema muestra la existencia disponible y el estado local de la información.
4. Si no se encuentra el producto, muestra un estado vacío con orientación para una nueva búsqueda.

## CU-03: Sincronizar operaciones pendientes

1. El sistema detecta conectividad.
2. Consulta la cola de operaciones pendientes.
3. Prepara cada operación con un identificador único.
4. Envía las operaciones al backend cuando se cumplen las condiciones de seguridad.
5. Marca como sincronizadas las operaciones confirmadas.
6. Mantiene pendientes las operaciones fallidas e informa el motivo cuando esté disponible.

> Estos flujos son documentales y no constituyen evidencia de implementación.

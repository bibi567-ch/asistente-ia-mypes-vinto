# Manual de usuario

> Guía de uso del código disponible. Los nombres visibles pueden variar entre versiones. Las capturas reales se agregan en `evidencias/` después de validar cada flujo.

## 1. Acceder a la aplicación

1. Abre la aplicación.
2. Introduce el PIN local configurado en el dispositivo.
3. El acceso mediante PIN funciona de forma local; no inicia sesión en el backend.

Para usar sincronización con el servidor, entra a la opción para vincular cuenta e inicia sesión con una cuenta API válida. No uses credenciales de demostración compartidas.

## 2. Completar el onboarding

Sigue la configuración inicial y, si eliges una plantilla de negocio, revisa el catálogo generado antes de usarlo. La aplicación evita volver a insertar productos de plantilla con nombres ya existentes.

## 3. Revisar panel e inventario

Desde el panel se puede abrir el inventario y consultar el reporte diario. El inventario se almacena localmente. El reporte muestra total y cantidad de ventas registradas desde el inicio del día local.

## 4. Registrar una venta

1. Abre **Registrar venta**.
2. Selecciona el producto, verifica la cantidad y el precio.
3. Elige contado o fiado y completa el cliente si corresponde.
4. Revisa el total y pulsa **Guardar venta**.

La venta se guarda localmente y queda pendiente de sincronización. No se debe registrar una venta de prueba con datos reales.

## 5. Usar el asistente de voz

1. Abre el chat y concede permiso de micrófono si Android lo solicita.
2. Habla con claridad y espera la transcripción.
3. Si se detecta una posible venta, revisa el producto y la cantidad en el formulario.
4. Corrige cualquier sugerencia antes de guardar. La voz por sí sola no registra la venta.

La disponibilidad del modelo local depende de que el modelo Vosk esperado esté instalado en el APK/desarrollo. Si no está disponible, utiliza el registro manual.

## 6. Sincronizar

1. Comprueba que la API PostgreSQL esté disponible y que una cuenta API válida esté vinculada.
2. Abre **Sincronización** o espera el trabajo programado.
3. Revisa el estado de operaciones pendientes y errores.

Si la conexión o autenticación falla, las ventas locales pendientes deben conservarse. La sincronización del catálogo descarga cambios del servidor; no sube al backend los productos creados solo en el dispositivo.

## 7. Reportar un problema

Anota el dispositivo/emulador, versión, pasos, resultado esperado y resultado observado. Añade capturas sin datos personales a `evidencias/` y registra el caso en [el plan de pruebas](04_Plan_de_Pruebas.md).

La existencia de esta guía no constituye evidencia de que los flujos hayan sido ejecutados.

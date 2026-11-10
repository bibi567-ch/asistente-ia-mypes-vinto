# Plan de pruebas

Los casos se mantienen pendientes hasta completar su criterio de evidencia. Para una prueba funcional Android, el estado solo cambia a **APROBADO** cuando el escenario se ejecutó y existe captura/video en la carpeta indicada. Para backend, se registra el resultado del workflow real de GitHub Actions; una ejecución local se etiqueta explícitamente como local.

Estados: `PENDIENTE`, `APROBADO`, `FALLIDO`, `BLOQUEADO`.

## Login

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| LOGIN-01 | Ingresar con PIN local válido | Se abre el panel sin requerir Internet | PENDIENTE |
| LOGIN-02 | Ingresar con PIN incorrecto | Se rechaza el acceso y se aplica la política local de intentos | PENDIENTE |

[ESPACIO PARA EVIDENCIA: Insertar aquí las capturas del módulo Login]

## Registro

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| REG-01 | Prueba de integración API: registrar propietario y negocio con correo único | Se guardan propietario y negocio, y la sesión Bearer permite usar una ruta protegida | APROBADO (integración local con PostgreSQL temporal) |
| REG-02 | Prueba de integración API: volver a registrar el mismo correo | Se responde `409` y no se crea un negocio huérfano | APROBADO (integración local con PostgreSQL temporal) |
| REG-03 | Prueba de integración API: enviar campos incompletos y contraseña corta | Se responde `400` antes de crear cuentas | APROBADO (integración local con PostgreSQL temporal) |
| REG-04 | Completar el formulario Android con conexión | El perfil se conserva en Room, el token se guarda y la app avanza al onboarding | PENDIENTE de prueba funcional |
| REG-05 | Registrar sin conexión y elegir continuar localmente | El PIN y perfil permanecen en Room; la contraseña de nube no se almacena y la UI informa que la cuenta no está vinculada | PENDIENTE de prueba funcional |
| REG-06 | Migrar una base Room versión 2 con usuario existente | La actualización a versión 3 conserva el PIN y permite abrir la base con los campos de perfil opcionales | PENDIENTE de prueba en dispositivo/base existente |
| PERF-01 | Abrir Perfil desde el dashboard después del registro | Room muestra los campos disponibles del comerciante y negocio; los opcionales ausentes aparecen como "No registrado" | PENDIENTE de prueba funcional |

[ESPACIO PARA EVIDENCIA: Insertar aquí las capturas del módulo Registro]

## Onboarding

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| ONB-01 | Aplicar una plantilla de negocio | Se agregan ejemplos y la pantalla muestra el resultado | PENDIENTE |
| ONB-02 | Aplicar de nuevo la misma plantilla | No se duplican productos existentes | PENDIENTE |

[ESPACIO PARA EVIDENCIA: Insertar aquí las capturas del módulo Onboarding]

## Dashboard y reportes

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| DASH-01 | Abrir el reporte diario sin ventas | Total y cantidad son cero | PENDIENTE |
| DASH-02 | Consultar el reporte después de una venta | Total y cantidad reflejan las ventas locales de hoy | PENDIENTE |
| DASH-03 | Consultar el gráfico de los últimos siete días | Cada barra suma las ventas del día local correspondiente, usando intervalos sin solapamiento | PENDIENTE |
| DASH-04 | Compartir el reporte con la vista demo activa | El texto compartido contiene únicamente los datos reales del día | PENDIENTE |
| DASH-05 | Activar la vista demo en una compilación debug | Los valores ficticios se limitan al gráfico y no se guardan en la base | PENDIENTE |
| DASH-06 | Exportar el reporte diario como archivo CSV | El archivo contiene solo las ventas del día, con campos escapados y se comparte mediante URI `FileProvider` de solo lectura | PENDIENTE |
| DASH-07 | Prueba JVM de cabecera CSV, formato de total y escape de comillas/separadores | El contenido CSV contiene la cabecera esperada y protege valores del cliente | APROBADO (unit test local; no valida el selector Android) |

[ESPACIO PARA EVIDENCIA: Insertar aquí las capturas del módulo Dashboard]

## Ventas

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| VTA-01 | Registrar venta válida de contado | Venta, stock y operación pendiente se guardan coherentemente | PENDIENTE |
| VTA-02 | Intentar vender más unidades que el stock disponible | Se rechaza la operación y no se genera stock negativo | PENDIENTE |
| VTA-03 | Registrar venta fiada con cliente | Se guarda la modalidad y el cliente | PENDIENTE |

[ESPACIO PARA EVIDENCIA: Insertar aquí las capturas del módulo Ventas]

## Inventario

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| INV-01 | Crear y consultar un producto local | El producto aparece en el inventario después de reabrir la pantalla | PENDIENTE |
| INV-02 | Recibir actualización remota de nombre/precio | Cambian los campos remotos y se conserva el stock local | PENDIENTE |
| INV-03 | Buscar un producto por nombre | La lista se filtra en tiempo real sin alterar los datos de Room y muestra estado vacío cuando no hay coincidencias | PENDIENTE |

[ESPACIO PARA EVIDENCIA: Insertar aquí las capturas del módulo Inventario]

## Fiados

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| FIADO-01 | Consultar las deudas pendientes | Se muestran las ventas FIADO no pagadas, el cliente, fecha y total pendiente | PENDIENTE |
| FIADO-02 | Confirmar el cobro completo de una deuda | En una transacción local se marca pagada, se registra ingreso de caja y se crea operación COBRO en Outbox | PENDIENTE |
| FIADO-03 | Intentar cobrar dos veces la misma deuda | La segunda operación no crea un ingreso ni un evento Outbox adicional | PENDIENTE |
| FIADO-04 | Cobrar sin conexión y sincronizar después | El cobro queda registrado localmente y la operación COBRO se conserva pendiente hasta reconectar | PENDIENTE |
| FIADO-05 | Prueba backend de sincronización de un cobro `COBRO` | PostgreSQL registra la operación y la repetición del mismo `operationId` se reconoce como duplicada | APROBADO (integración local con PostgreSQL temporal) |

[ESPACIO PARA EVIDENCIA: Insertar aquí las capturas del módulo Fiados]

## Chat IA

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| IA-01 | Iniciar reconocimiento de voz con modelo disponible | Se muestra el texto reconocido | PENDIENTE |
| IA-02 | Dictar una venta reconocible | Se abre el formulario con sugerencias, sin guardar automáticamente | PENDIENTE |
| IA-03 | Usar el chat sin modelo disponible o cancelar | Se informa el estado y no se registra una venta | PENDIENTE |
| IA-04 | Prueba JVM de extracción de cantidad, producto, monto, unidad y cliente | Los ejemplos "cuartilla" y "al fiado" producen las entidades esperadas | APROBADO (unit test local) |
| IA-05 | Prueba JVM de clasificación de "Vendí dos cuartillas de arroz" | Se clasifica como VENTA | APROBADO (unit test local) |
| IA-06 | Inicializar Vosk desde los assets y reconocer voz sin conexión | El modelo se extrae de assets, inicializa y reconoce una frase en español en dispositivo | PENDIENTE |

[ESPACIO PARA EVIDENCIA: Insertar aquí las capturas del módulo Chat IA]

## Sincronización

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| SYNC-01 | Registrar una venta offline y recuperar conexión | La operación pendiente se envía al autenticarse y se conserva ante error | PENDIENTE |
| SYNC-02 | Descargar catálogo paginado con más de 500 productos | Se recorren todas las páginas sin omitir productos | PENDIENTE |
| SYNC-03 | Abrir la pantalla con operaciones Outbox pendientes | El conteo y el listado reflejan Room y cambian al actualizarse el Outbox | PENDIENTE de prueba funcional |

[ESPACIO PARA EVIDENCIA: Insertar aquí las capturas del módulo Sincronización]

## Backend

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| API-01 | Ejecutar `npm run build` en CI | TypeScript compila sin errores | PENDIENTE DE CI |
| API-02 | Ejecutar `npm run typecheck:test` en CI | Código de pruebas pasa el chequeo de tipos | PENDIENTE DE CI |
| API-03 | Ejecutar `npm test` en CI | Todas las pruebas unitarias pasan | PENDIENTE DE CI |
| API-04 | Ejecutar migraciones y `npm run test:integration` contra PostgreSQL aislado | Migraciones y pruebas de integración pasan | PENDIENTE DE CI |

[ESPACIO PARA EVIDENCIA: Insertar aquí las capturas del módulo Backend]

El workflow genera un resumen de cada ejecución en GitHub Actions. Añade el enlace a la ejecución en [07_CI_CD_Report.md](07_CI_CD_Report.md); no marques los casos de CI como aprobados si el workflow falló o aún no se ejecutó.

## Seguridad local

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| SEC-01 | Pruebas JVM de `PinHasher`: verificar PIN correcto/incorrecto y sal aleatoria | Solo el PIN original verifica; no se almacena el PIN en claro y cada hash usa sal distinta | APROBADO (unit test local) |

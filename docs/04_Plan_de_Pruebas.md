# Plan de pruebas

Los casos se mantienen pendientes hasta completar su criterio de evidencia. Para una prueba funcional Android, el estado solo cambia a **APROBADO** cuando el escenario se ejecutó y existe captura/video en la carpeta indicada. Para backend, se registra el resultado del workflow real de GitHub Actions; una ejecución local se etiqueta explícitamente como local.

Estados: `PENDIENTE`, `APROBADO`, `FALLIDO`, `BLOQUEADO`.

## Login

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| LOGIN-01 | Ingresar con PIN local válido | Se abre el panel sin requerir Internet | PENDIENTE |
| LOGIN-02 | Ingresar con PIN incorrecto | Se rechaza el acceso y se aplica la política local de intentos | PENDIENTE |

[ESPACIO PARA EVIDENCIA: Insertar aquí las capturas del módulo Login]

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

[ESPACIO PARA EVIDENCIA: Insertar aquí las capturas del módulo Inventario]

## Chat IA

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| IA-01 | Iniciar reconocimiento de voz con modelo disponible | Se muestra el texto reconocido | PENDIENTE |
| IA-02 | Dictar una venta reconocible | Se abre el formulario con sugerencias, sin guardar automáticamente | PENDIENTE |
| IA-03 | Usar el chat sin modelo disponible o cancelar | Se informa el estado y no se registra una venta | PENDIENTE |

[ESPACIO PARA EVIDENCIA: Insertar aquí las capturas del módulo Chat IA]

## Sincronización

| ID de Prueba | Descripción | Resultado Esperado | Estado (Aprobado/Fallido) |
|---|---|---|---|
| SYNC-01 | Registrar una venta offline y recuperar conexión | La operación pendiente se envía al autenticarse y se conserva ante error | PENDIENTE |
| SYNC-02 | Descargar catálogo paginado con más de 500 productos | Se recorren todas las páginas sin omitir productos | PENDIENTE |

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

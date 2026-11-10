# Requisitos y experiencia de usuario

Este archivo sirve como índice operativo de requisitos y UX para el equipo. La especificación académica vigente se conserva en [02_Requisitos_y_UX.md](02_Requisitos_y_UX.md); se mantiene allí para no duplicar ni divergir la matriz formal de requisitos.

## Alcance funcional observado en el código

- Acceso local con PIN independiente de la cuenta del backend.
- Onboarding, panel principal e inventario local.
- Registro manual de ventas y persistencia local offline.
- Asistente de voz que puede sugerir producto y cantidad y abrir el formulario de venta. La venta requiere revisión y confirmación manual.
- Sincronización diferida de operaciones pendientes cuando se configura y autentica la API.
- Reporte local de total y cantidad de ventas del día.

La presencia de estas funciones en el código no demuestra que se hayan validado en un teléfono. Los estados de prueba y sus evidencias se registran en [04_Plan_de_Pruebas.md](04_Plan_de_Pruebas.md) y [05_Resultados_de_Pruebas.md](05_Resultados_de_Pruebas.md).

## Límites que deben mantenerse claros

- El PIN local no autentica en la API ni equivale a una contraseña.
- La voz no registra ventas automáticamente.
- Los resultados de la app requieren validación manual en dispositivo para considerarse pruebas funcionales.
- Los requisitos formales y exclusiones académicas están en la especificación enlazada arriba; este índice no los reemplaza.

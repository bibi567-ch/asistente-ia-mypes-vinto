# Requisitos y experiencia de usuario

Este archivo sirve como índice operativo de requisitos y UX para el equipo. La especificación académica vigente se conserva en [02_Requisitos_y_UX.md](02_Requisitos_y_UX.md); se mantiene allí para no duplicar ni divergir la matriz formal de requisitos.

## Alcance funcional observado en el código

- Registro local de perfil y PIN; inicio de sesión con carnet, celular o correo y PIN. La pantalla inicia en login y ofrece navegación a registro; no hay una pantalla de bienvenida independiente.
- Onboarding, seis plantillas locales, panel, productos, ventas manuales, fiados y reportes.
- Voz en español mediante Vosk cuando se instala el modelo local; el análisis de intención/entidades actual es basado en reglas, no TFLite.
- El asistente local Yapay puede consultar el total vendido del día y preparar un alta de producto sujeta a confirmación; no es un LLM ni guarda ventas por cuenta propia.
- Outbox y WorkManager para operaciones pendientes, y descarga paginada del catálogo cuando la API está disponible y se ha autenticado.

La presencia de funciones en el código no demuestra su validación en teléfono. Los estados de prueba y sus evidencias se registran en [04_Plan_de_Pruebas.md](04_Plan_de_Pruebas.md) y [05_Resultados_de_Pruebas.md](05_Resultados_de_Pruebas.md).

## Límites que deben mantenerse claros

- El PIN local no autentica en la API ni equivale a una contraseña.
- La contraseña de nube y el token JWT son independientes del PIN. La contraseña no se guarda en el teléfono.
- La voz no registra ventas automáticamente; la persona debe revisar la propuesta y guardar manualmente.
- Vosk requiere el modelo local. Su presencia en una carpeta o en un APK no prueba precisión, latencia ni funcionamiento sin conexión en un dispositivo.
- Los resultados de la app requieren validación manual en dispositivo para considerarse pruebas funcionales.
- Los requisitos formales y exclusiones académicas están en la [especificación](02_Requisitos_y_UX.md); este resumen no los reemplaza.

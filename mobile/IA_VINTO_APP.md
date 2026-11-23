# IA Vinto — App Android (Kotlin + Jetpack Compose)

Después del ingreso con PIN, la app junta **las dos partes del proyecto**:

- **El negocio** (la versión anterior, la que se defiende en Arquitectura y en Tecnologías en Internet), con las pantallas del primer prototipo Figma (Yapay) que faltaban.
- **El agente de voz IA Vinto** (alcance del perfil).

Barra inferior: **Inicio · Ventas · IA Vinto · Caja · Más**

| Pantalla | Qué hace | Estado |
|---|---|---|
| Inicio | Ventas de hoy, caja estimada, fiados pendientes, stock bajo y accesos rápidos | Nuevo (Dashboard del Figma) |
| Ventas | Historial de ventas con filtros (todas, hoy, al fiado) y ticket promedio | Nuevo |
| IA Vinto | Voz sin internet (Vosk): órdenes del teléfono **y** del negocio («vendí 3 panes», «¿cuánto vendí hoy?», «cuánto queda de arroz») | Nuevo |
| Caja | Ventas, contado, fiado, cobros, gastos y caja estimada del día; registrar gastos | Nuevo (Caja del Figma) |
| Análisis | Ventas de 7 días en barras, promedio diario y los 5 más vendidos | Nuevo (Análisis del Figma) |
| Pagos QR / Facturas | Pantallas del Figma con aviso honesto: proveedor bancario y SIAT no conectados | Nuevo (igual que el Figma) |
| Inventario, Fiados, Reporte del día, Venta manual, Sincronización, Perfil, Chat Yapay | Pantallas que ya existían | Sin cambios |
| Contactos, Guías, Historial de órdenes, Ajustes | Agente IA Vinto | Nuevo |

Órdenes del teléfono: llamar (con permiso, llama tras confirmar; sin permiso abre el marcador), mensaje SMS con el texto listo, alarma en el reloj, abrir WhatsApp, YouTube, cámara, mapas…, hora, linterna, ajustes de wifi, datos y Bluetooth, guías y ayuda. Todo lo que cambia algo pide confirmación. Las ventas por voz abren el formulario de venta lleno para que la persona confirme; nunca se guardan solas.

## Código nuevo

- `asistente/Comandos.kt`: clasificador (mismas reglas que el frontend web)
- `asistente/ExtractorComandos.kt`: número, contacto, mensaje, hora, aplicación y linterna
- `asistente/AgenteVinto.kt`: plan y ejecución con intents de Android
- `asistente/AlmacenVinto.kt`: contactos, historial y preferencias (solo en el teléfono)
- `asistente/Guias.kt` y `asistente/Locutor.kt` (respuestas habladas con TextToSpeech)
- `presentation/vinto/*`: pantallas con la paleta del Figma (`NegocioScreens.kt`: Inicio, Ventas, Caja, Análisis, Pagos QR, Facturas, Más)
- `viewmodel/NegocioViewModel.kt` y `data/RankingProducto.kt`; en `VentaDao.kt` se agregaron dos consultas (movimientos y más vendidos). **No se cambió el esquema de Room** (sigue en la versión 6).
- `test/.../asistente/ComandosTest.kt`: pruebas unitarias

Archivos modificados: `MainActivity.kt` (después del login va al inicio con la barra nueva; el registro sigue pasando por la plantilla del negocio), `VentaDao.kt`, `theme/Colors.kt` (paleta del Figma), `LoginScreen.kt` (marca IA Vinto), `AndroidManifest.xml` (nombre «IA Vinto», permisos `CALL_PHONE` y `SET_ALARM`, lista de apps que se pueden abrir) y `res/values/styles.xml` (tema oscuro). El panel anterior sigue disponible en la ruta `dashboard_anterior`. La copia original de los archivos modificados está en `mobile/_respaldo_antes_ia_vinto/`.

## Instalar en el celular

1. En el celular: Ajustes → Acerca del teléfono → toca 7 veces **Número de compilación**. Luego Ajustes → Opciones de desarrollador → activa **Depuración USB**.
2. Conecta el celular por USB a la computadora y acepta «Permitir depuración».
3. En Android Studio: **File → Open** → carpeta `mobile` → espera la sincronización de Gradle.
4. Elige tu celular arriba y presiona **Run ▶** (o `gradlew.bat installDebug` en la terminal, dentro de `mobile`).
5. Al abrir, permite el **micrófono** y las **llamadas**. La primera vez el modelo de voz tarda unos segundos en prepararse.

Pruebas: `gradlew.bat testDebugUnitTest`.

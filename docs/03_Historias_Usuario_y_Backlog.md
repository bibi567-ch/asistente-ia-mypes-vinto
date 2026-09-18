# 3. Product Backlog e Historias de Usuario

Esta sección estructura el trabajo en formato ágil para guiar el desarrollo durante el semestre.

## 3.1 Épicas del Proyecto
*   **Épica 1 (E1):** Implementación del Asistente Conversacional Offline.
*   **Épica 2 (E2):** Gestión del Inventario y Ventas en el Dispositivo.
*   **Épica 3 (E3):** Experiencia de Usuario (Dashboard y UX).
*   **Épica 4 (E4):** Sincronización y Respaldo en la Nube.

## 3.2 Product Backlog (Historias de Usuario)

| ID | Épica | Historia de Usuario | Criterios de Aceptación | Estimación (Puntos) |
| :--- | :--- | :--- | :--- | :--- |
| **US-01** | E1 | **Como** dueño de MYPE, **quiero** dictar las ventas por voz **para** no tener que escribir en una pantalla pequeña. | 1. El botón de micrófono es el elemento principal.<br>2. Transcribe comandos como "Vendí 2 cocacolas".<br>3. Funciona sin internet (Vosk). | 8 |
| **US-02** | E1 | **Como** dueño de MYPE, **quiero** que el sistema entienda qué producto vendí **para** descontarlo automáticamente. | 1. El modelo NLP extrae cantidad e ítem.<br>2. Confirma la acción por voz y texto.<br>3. Tiempo de respuesta < 2s. | 13 |
| **US-03** | E2 | **Como** dueño de MYPE, **quiero** consultar mi stock preguntando con mi voz **para** saber qué me falta comprar. | 1. Reconoce la intención de "consulta".<br>2. El sistema responde con audio (TTS) el stock actual. | 8 |
| **US-04** | E2 | **Como** dueño de MYPE, **quiero** que los datos de mis ventas se guarden en mi celular **para** no perderlos si se corta la luz o el internet. | 1. Base de datos SQLite configurada.<br>2. Datos cifrados (AES-256). | 5 |
| **US-05** | E3 | **Como** dueño de MYPE, **quiero** ver un resumen fácil de entender al abrir la app **para** saber cuánto gané en el día. | 1. Pantalla principal con ganancias del día.<br>2. Diseño en Figma aprobado y validado. | 5 |
| **US-06** | E4 | **Como** dueño de MYPE, **quiero** que mis datos se guarden en internet cuando tenga megas **para** tener un respaldo si pierdo el celular. | 1. Detecta conexión a Wi-Fi/Datos.<br>2. Sincronización silenciosa en background.<br>3. Sin conflictos de concurrencia. | 8 |

## 3.3 Casos de Uso Clave (Narrativa)

### Caso de Uso 1: Registrar Venta por Voz (US-01, US-02)
*   **Actores:** Comerciante (Usuario Final).
*   **Precondiciones:** La app está abierta. Hay productos registrados en la BD local.
*   **Flujo Principal:**
    1. El usuario presiona el botón central del micrófono.
    2. El usuario dice: "Registra la venta de 3 kilos de azúcar".
    3. Vosk transcribe el audio a texto offline.
    4. El motor NLP identifica la intención (Venta), la cantidad (3) y el producto (azúcar).
    5. El sistema busca "azúcar" en la base local, deduce 3 unidades.
    6. El sistema emite un sonido de éxito y actualiza el dashboard.
*   **Flujos Alternativos:**
    *   *4a.* El NLP no entiende el producto. El sistema pregunta: "¿Puedes repetir el producto?".
    *   *5a.* No hay stock suficiente. El sistema advierte: "Solo tienes 1 kilo de azúcar en stock".
*   **Postcondiciones:** El inventario se actualiza en la BD local y la transacción queda guardada con su timestamp.

# 02. Requisitos funcionales y diseño UX/UI

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Especificación vigente para el alcance de voz
**Actualizado:** 23/11/2026

## 1. Propósito y alcance

Este documento define los requisitos observables y los flujos principales del asistente de voz Android. El alcance se limita a diez intenciones, reconocimiento offline con alternativa cloud, confirmación de acciones, respuestas habladas y registro de eventos. No especifica ventas, inventario, fiados ni facturación.

Los requisitos describen el comportamiento esperado; su presencia en esta tabla no implica que la función esté implementada o validada. El estado técnico se registra en [la matriz de trazabilidad](06_Matriz_Trazabilidad.md) y [los resultados de pruebas](14_Resultados_de_Pruebas.md).

## 2. Requisitos funcionales

| Código | Requisito | Prioridad |
|---|---|---|
| RF-01 | El sistema debe capturar una orden hablada cuando el usuario active el control de micrófono y los permisos necesarios estén concedidos. | Alta |
| RF-02 | El sistema debe intentar transcribir la orden en el dispositivo mediante Vosk cuando no haya conexión a Internet. | Alta |
| RF-03 | El sistema debe enviar audio al servicio cloud únicamente si la confianza local es menor que 0,70, existe conectividad validada y la sesión/API están configuradas. | Alta |
| RF-04 | El sistema debe clasificar la orden en una de las diez intenciones definidas en la sección 3, o informar que no pudo identificarla. | Alta |
| RF-05 | El sistema debe extraer los parámetros necesarios para la acción reconocida y solicitar información faltante en vez de asumirla. | Alta |
| RF-06 | El sistema debe presentar una confirmación visual antes de ejecutar acciones externas del teléfono. | Alta |
| RF-07 | El sistema debe ejecutar una acción solo después de recibir confirmación; al cancelar, no debe ejecutarla. | Alta |
| RF-08 | El sistema debe comunicar por TTS la respuesta, el resultado o el error de la operación. | Alta |
| RF-09 | El sistema debe guardar localmente el texto, la intención y el resultado de la acción en el Outbox para su sincronización posterior. | Alta |
| RF-10 | El sistema debe sincronizar los eventos pendientes cuando haya conectividad y una sesión válida, evitando duplicados mediante un identificador idempotente. | Alta |
| RF-11 | El sistema debe informar de manera comprensible la falta de permisos, parámetros, conectividad o aplicaciones compatibles, sin indicar éxito cuando la acción no se completó. | Alta |

## 3. Intenciones incluidas

| Código | Intención | Ejemplo de entrada | Resultado previsto |
|---|---|---|---|
| INT-01 | `LLAMAR_CONTACTO` | «Llama al 5551234567» | Solicita confirmación y abre la acción de llamada disponible para el número indicado. |
| INT-02 | `ENVIAR_MENSAJE` | «Envía un mensaje al 5551234567 diciendo llego tarde» | Solicita confirmación y abre el compositor de SMS para que el usuario complete/envíe el mensaje. |
| INT-03 | `LEER_TEXTO` | «Lee en voz alta buenos días» | Lee el texto reconocido mediante TTS, previa confirmación conforme a la política de acciones. |
| INT-04 | `CREAR_ALARMA` | «Pon alarma a las 5» | Solicita confirmación y abre la interfaz de alarmas con la hora reconocida. |
| INT-05 | `ABRIR_APP` | «Abre WhatsApp» | Solicita confirmación e intenta abrir la aplicación instalada que coincida. |
| INT-06 | `CONSULTAR_HORA` | «Qué hora es» | Comunica la hora local mediante texto y TTS. |
| INT-07 | `LINTERNA` | «Prende la linterna» | Solicita confirmación y activa la linterna si el dispositivo y permisos lo permiten. |
| INT-08 | `AJUSTES_RED` | «Abre ajustes de red» | Solicita confirmación y abre los ajustes de Wi-Fi disponibles en Android. |
| INT-09 | `GUIA_TELEFONO` | «Guíame con el teléfono» | Presenta y lee instrucciones breves de uso. |
| INT-10 | `AYUDA` | «Ayuda» | Presenta y lee las intenciones disponibles. |

La resolución de contactos desde la agenda no forma parte de la implementación actual. Para llamadas o mensajes se requiere un número explícito; la aplicación de mensajería conserva el paso final de envío.

## 4. Requisitos no funcionales

| Código | Requisito / criterio de evaluación | Método |
|---|---|---|
| RNF-01 | La ruta local debe poder utilizarse sin conexión; el fallback cloud no debe ser requisito para reconocer y clasificar comandos. | Prueba en modo avión y revisión de registros. |
| RNF-02 | La confianza se interpretará con el umbral configurado de 0,70; se reportarán por separado las mediciones de Vosk y del proveedor cloud. | Prueba automatizada y evaluación de transcripciones. |
| RNF-03 | El umbral de WER objetivo para la evaluación offline es ≤25%; aún no se ha medido en campo. | Comparar transcripciones con referencias humanas y aplicar la fórmula WER. |
| RNF-04 | La puntuación SUS objetivo es ≥70/100; aún no se ha medido en campo. | Cuestionario estándar posterior a tareas y cálculo SUS. |
| RNF-05 | La latencia, memoria, batería y compatibilidad se deben medir en dispositivos identificados; no se declaran límites aprobados hasta ejecutar las pruebas. | Registrar mediana/P95, dispositivo, versión y condiciones. |
| RNF-06 | Credenciales de proveedores cloud y secretos de backend no deben incorporarse al APK, al código fuente ni a evidencia pública. | Inspección de configuración y repositorio. |
| RNF-07 | Los resultados deben ser accesibles y comprensibles; los controles críticos requieren etiquetas, estados y opción de cancelar. | Revisión UX y pruebas con participantes. |

## 5. Flujos de usuario

### CU-01. Procesar una orden de voz

1. El usuario activa el micrófono y concede permisos si Android los solicita.
2. La app captura audio y Vosk intenta reconocerlo localmente.
3. Si la confianza es inferior a 0,70, la app intenta el servicio cloud solo cuando se cumplen las condiciones de conexión y sesión.
4. El clasificador determina la intención y extrae los parámetros disponibles.
5. Si faltan datos o la intención no se reconoce, la app informa el problema y no ejecuta una acción.
6. La app presenta una confirmación para la acción correspondiente.
7. El usuario confirma o cancela; solo la confirmación permite continuar.
8. La app comunica el resultado o error con TTS y registra el evento localmente.

### CU-02. Sincronizar el Outbox

1. La app conserva los eventos en Room mientras no haya conexión o sesión válida.
2. WorkManager identifica eventos pendientes cuando se recupera la conectividad.
3. El cliente envía eventos autenticados con identificadores estables.
4. El backend reconoce reintentos idempotentes; la app marca sincronizado solo el evento aceptado.
5. Los errores permanecen visibles y reintentables; no se informa sincronización exitosa si el servidor no la confirmó.

### CU-03. Confirmar o cancelar una acción

1. El sistema identifica una acción y los parámetros que requiere.
2. La interfaz muestra el nombre de la acción y los datos reconocidos, sin completar valores por inferencia.
3. El usuario elige “Sí” o “Cancelar”.
4. Si confirma, el sistema ejecuta la acción disponible y comunica el resultado; si cancela, no inicia la acción y registra la cancelación.
5. Si faltan parámetros o permisos, el sistema informa el bloqueo y no comunica éxito.

## 6. Lineamientos UX

- Mantener la acción principal de voz visible en el Dashboard y mostrar estados de escucha, procesamiento, confirmación, éxito y error.
- La confirmación debe nombrar la acción y sus parámetros esenciales; “Cancelar” debe tener el mismo nivel de visibilidad que “Sí”.
- No mostrar nombre de contacto, número, hora o aplicación supuestos si no fueron reconocidos.
- Mantener texto legible, controles táctiles accesibles y mensajes utilizables con TTS.
- Registrar en [la matriz de trazabilidad](06_Matriz_Trazabilidad.md) las pantallas/prototipos concretos y las evidencias de accesibilidad una vez revisados.

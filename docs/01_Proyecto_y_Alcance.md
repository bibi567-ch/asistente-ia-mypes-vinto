# 01. Proyecto y alcance: Agente Conversacional IA Vinto

**Estado:** Alcance vigente del asistente de voz
**Actualizado:** 23/11/2026

## 1. Descripción del Proyecto

El proyecto consiste en desarrollar un agente conversacional para Android que permita interactuar con herramientas del teléfono mediante comandos de voz en castellano y expresiones de quechuañol. El diseño usa un flujo híbrido: Vosk reconoce voz localmente y, si la confianza es menor al 70%, hay conectividad y existe una sesión válida, el cliente puede enviar el audio a una API cloud para transcribirlo.

## 2. Alcance del Sistema

- **Interacción por voz:** diez intenciones: llamar, enviar mensaje, leer texto, crear alarma, abrir app, consultar hora, usar linterna, abrir ajustes de red, guía del teléfono y ayuda.
- **Modo híbrido:** reconocimiento local en el dispositivo y fallback ASR cloud cuando hay conectividad, confianza Vosk baja y configuración válida.
- **Confirmación humana:** la app presenta un diálogo antes de ejecutar acciones del teléfono; TextToSpeech comunica confirmaciones y resultados.
- **Log de operaciones (Outbox):** guarda localmente texto, intención y estado de confirmación/ejecución para sincronizarlo cuando haya red.
- **Backend:** API Node.js/Express con autenticación JWT, proxy ASR configurable y almacenamiento PostgreSQL de logs de voz.

## 3. Límites

- No incluye gestión de ventas, inventarios, fiados ni facturación.
- Aimara no forma parte de esta versión; se plantea como trabajo futuro.
- No se desarrolla para iOS.
- La app actual clasifica intenciones mediante reglas Kotlin; LiteRT/TensorFlow Lite no está integrado.
- El login local por PIN no emite por sí mismo el JWT del backend. La URL y un token válido deben ser proporcionados por una futura integración de sesión para activar ASR cloud y sincronización.
- La búsqueda de contactos por nombre aún no está integrada; para llamar o enviar un mensaje se necesita reconocer un número telefónico.

## 4. Criterios de viabilidad

La app fija Android API 26 como mínimo y utiliza el modelo Vosk español empaquetado como asset local. La prueba técnica debe medir uso de RAM, latencia, precisión en castellano/quechuañol y consumo de batería en dispositivos de campo antes de afirmar compatibilidad con una cantidad mínima de memoria.

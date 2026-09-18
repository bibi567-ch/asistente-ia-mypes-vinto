# 1. Introducción y Contexto

## 1.1 Introducción
El presente proyecto describe la arquitectura e interfaz de un Asistente Conversacional Offline con Procesamiento de Lenguaje Natural (PLN) para la gestión de ventas e inventario en las MYPES del centro de Vinto, Cochabamba. El sistema está diseñado en tecnologías nativas (Kotlin) para operar en dispositivos Android de gama baja, garantizando un consumo menor a 500 MB de RAM y operación 100% offline.

## 1.2 Análisis del Problema
**Contexto:** Vinto concentra microempresas familiares que operan con flujo de caja diario. Los comerciantes poseen teléfonos Android antiguos, conectividad intermitente y baja alfabetización digital.

**Problema Principal:** El abandono de los sistemas de gestión comercial tradicionales ocurre **debido a la inadecuación de las arquitecturas de software comerciales frente a estas restricciones técnicas y a la imposibilidad de interacción mediante lenguaje natural**. Esto perpetúa el uso de cuadernos, causando errores matemáticos y pérdida de mercadería.

**Oportunidad de Solución:** La IA "Edge" permite incrustar modelos de reconocimiento de voz (Vosk) que pesan menos de 50MB, habilitando una interfaz conversacional local.

## 1.3 Stakeholders
| Stakeholder | Rol | Interés Principal |
| :--- | :--- | :--- |
| **Comerciantes Vinto** | Usuario Final | Registrar ventas mediante la voz de forma intuitiva. |
| **Dueños de Negocios** | Beneficiario | Toma de decisiones, reducir pérdidas. |
| **Desarrolladores** | Equipo Técnico | Implementar arquitectura Kotlin nativa y Edge AI. |
| **Docentes UAB** | Evaluador | Validar el rigor arquitectónico. |

## 1.4 Alcance
* **Incluido:** Arquitectura offline-first nativa. Pipeline de PLN local (Vosk + TFLite). Sincronización Outbox Pattern y resolución LWW. Confirmación Visual (VUI+GUI).
* **Excluido:** Pasarelas de pago, versión web/iOS, modelo pesado Gemma 3n (pasa a ser stretch goal).

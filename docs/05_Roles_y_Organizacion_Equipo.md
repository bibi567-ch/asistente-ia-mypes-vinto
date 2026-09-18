# 5. Organización del Equipo y Roles (Equipo de 5 Integrantes)

Para garantizar el éxito del desarrollo del "Asistente Conversacional Offline para MYPES", el trabajo se ha dividido estratégicamente en 5 roles, permitiendo desarrollo en paralelo y especialización.

## 5.1 Asignación de Roles

### 1. Project Manager & QA Lead (Líder y Calidad)
* **Misión:** Gestionar la metodología ágil, mantener el Backlog, asegurar la trazabilidad arquitectónica (C1/C2) y realizar las pruebas de usabilidad (Métricas SUS).
* **Herramientas:** Trello, GitHub, Documentación SAD.

### 2. UX/UI Designer & Frontend (Especialista en Diseño)
* **Misión:** Diseñar la interfaz móvil orientada a VUI (Voice-User Interface). Aplicar el sistema de diseño (tokens, paleta primaria #2E7D32) centrado en usuarios de baja alfabetización digital (User Personas: María y Carlos).
* **Herramientas:** Figma.

### 3. AI & Edge Developer (Desarrollador de IA Móvil)
* **Misión:** Construir el pipeline de Procesamiento de Lenguaje Natural (PLN) directamente en el dispositivo (Edge Computing). Transcribir voz a texto y extraer intenciones (Venta, Compra, Consulta).
* **Herramientas:** Kotlin, Vosk, TensorFlow Lite / Gemma 3n.

### 4. Offline-First Engineer (Ingeniero de Datos Móvil)
* **Misión:** Implementar la persistencia local en el teléfono móvil. Garantizar que la app opere 100% offline y gestionar la cola de transacciones pendientes (Outbox pattern local) para sincronización.
* **Herramientas:** Kotlin, SQLite / Room, Android WorkManager.

### 5. Cloud & Backend Architect (Arquitecto de Servidores)
* **Misión:** Diseñar la API REST en la nube, gestionar la base de datos maestra y resolver conflictos de datos (Políticas LWW) cuando los dispositivos móviles envían sincronizaciones en diferido.
* **Herramientas:** Java Spring Boot, Axon Framework (CQRS/Event Sourcing), PostgreSQL, Railway.

---
*Este documento ha sido estructurado para la presentación de avance y distribución de carga técnica equitativa.*

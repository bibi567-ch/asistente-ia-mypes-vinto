# 5. Organización del Equipo y Roles

Para garantizar que el proyecto se entregue con éxito el **20 de Septiembre** y cumpla con las expectativas de ambas materias, es fundamental dividir el trabajo de manera estratégica.

A continuación, se propone una estructura de roles que puedes asignar a los miembros de tu equipo.

---

## 5.1 Roles y Responsabilidades

### 1. Scrum Master / Project Manager (Líder del Equipo)
*   **Responsabilidades:**
    *   Supervisar que todos cumplan con sus entregas antes del 20 de septiembre.
    *   Mantener actualizado el Product Backlog y organizar las reuniones diarias de estado (Daily Standups).
    *   Asegurar que la documentación en GitHub (los archivos `.md`) esté perfecta.
    *   Preparar la presentación principal (diapositivas) utilizando la información del archivo `01_Introduccion_y_Contexto.md`.

### 2. Analista / Arquitecto de Software
*   **Responsabilidades:**
    *   Dominar el documento `04_Arquitectura_SAD_y_UX.md`.
    *   Explicar al tribunal por qué se eligió el modelo C4 y cómo funciona la arquitectura Offline-First.
    *   Defender los Requisitos Funcionales, No Funcionales, y los ADRs (Decisiones Arquitectónicas).
    *   Asegurar la trazabilidad (Que lo que está en Figma y el código concuerde con los diagramas C1 y C2).

### 3. Diseñador UX/UI (Especialista en Figma)
*   **Responsabilidades:**
    *   Construir el prototipo interactivo en Figma basándose en el enfoque *Voice-User Interface (VUI)*.
    *   Crear el Sistema de Diseño: paletas de color de alto contraste, tipografías grandes y componentes básicos (Auto Layout).
    *   Armar el mapa de sitio y los "User Personas" (ej: Comerciante mayor, celular básico).
    *   Asegurar que el enlace al prototipo navegable sea público y funcione para la presentación.

### 4. Desarrollador Especialista en IA y Móvil (Tech Lead)
*   **Responsabilidades:**
    *   Investigar y preparar la viabilidad técnica de integrar Vosk (Speech-to-text) en Android.
    *   Entender y defender la elección de "Gemma 3n" para la extracción de intenciones de lenguaje natural de manera offline.
    *   Hablar sobre las restricciones de hardware (teléfonos de menos de 2GB de RAM) y cómo la aplicación las supera.

---

## 5.2 Plan de Acción para la Presentación (20 de Septiembre)

### Antes de la presentación:
1.  **Importar a GitHub:** Suban todos estos archivos Markdown (`.md`) al repositorio `bibi567-ch/asistente-ia-mypes-vinto`.
2.  **Validar Figma:** El responsable de UX debe tener el enlace listo, con el flujo principal simulando una "venta por voz" operando sin errores.
3.  **Ensayo:** Cada miembro debe repasar su parte basándose en los documentos generados. No intenten aprender todo, ¡confíen en la división de roles!

### Día de la Presentación:
*   **Minuto 1-2:** El *Líder* expone el problema en Vinto (La brecha digital y la falta de internet).
*   **Minuto 3-5:** El *Arquitecto* muestra el diagrama de Contenedores (C2) y explica que el sistema funciona 100% offline.
*   **Minuto 6-8:** El *Tech Lead* menciona la innovación de usar Inteligencia Artificial local (Vosk/Gemma).
*   **Minuto 9-12:** El *Diseñador UX* muestra la pantalla de Figma, demostrando que con "un solo botón" el comerciante puede hacer todo, justificando la inclusión digital.

---
💡 **Consejo:** Si tu equipo tiene menos de 4 personas, pueden fusionar roles. Por ejemplo, el Líder puede ser también el Arquitecto, y el Diseñador UX puede apoyar al Desarrollador.

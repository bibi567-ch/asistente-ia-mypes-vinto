# DOCUMENTACIÓN TÉCNICA - TECNOLOGÍAS DE INTERNET

**UNIVERSIDAD ADVENTISTA DE BOLIVIA**  
**INGENIERÍA EN SISTEMAS**  
**TECNOLOGÍAS DE INTERNET**  

**SISTEMA:** Asistente Conversacional Offline con Procesamiento de Lenguaje Natural para la Gestión de MYPES en Vinto  
**INTEGRANTES:** [Nombres]  
**DOCENTE:** [Nombre del Docente]  
**FECHA:** [Fecha]  

---

## 1. DATOS GENERALES Y ESTRUCTURA DE LA ENTREGA
*   **Asignatura:** Tecnologías en Internet
*   **Formato de Entrega:** Enlace público a Figma + Documentación Técnica estructurada en PDF/Word/Markdown.

## 2. REQUISITOS DEL SOFTWARE (ARQUITECTURA DE REQUISITOS)
Esta matriz expandida supera el mínimo exigido, blindando el alcance del proyecto.

### 2.1 Matriz de Requisitos Funcionales (RF)
| Código | Módulo | Descripción del Requisito (El sistema debe...) | Prioridad |
| :--- | :--- | :--- | :--- |
| **RF-001** | Autenticación | ...permitir el registro de comerciantes validando su número de teléfono. | Alta |
| **RF-002** | Autenticación | ...permitir el inicio de sesión offline mediante un PIN local de 4 dígitos. | Alta |
| **RF-003** | Perfil | ...permitir configurar el nombre de la tienda y el rubro comercial. | Media |
| **RF-004** | PLN Edge | ...transcribir el audio a texto localmente sin usar Internet (Vosk). | Alta |
| **RF-005** | PLN Edge | ...clasificar la intención del usuario (Venta, Compra, Consulta, Cancelar). | Alta |
| **RF-006** | PLN Edge | ...extraer entidades clave del texto (Producto, Cantidad, Precio). | Alta |
| **RF-007** | Mitigación | ...mostrar una **Confirmación Visual (VUI+GUI)** obligatoria antes de procesar una venta dictada. | Alta |
| **RF-008** | Navegación | ...cancelar cualquier flujo activo si el usuario dice "Cancelar" o "Me equivoqué". | Alta |
| **RF-009** | Ventas | ...registrar una venta asignando fecha, hora, productos y monto total. | Alta |
| **RF-010** | Ventas | ...permitir registrar una venta manualmente (teclado) si el entorno es muy ruidoso. | Alta |
| **RF-011** | Ventas | ...permitir aplicar descuentos verbales ("le cobré 5 bolivianos menos"). | Media |
| **RF-012** | Ventas | ...clasificar transacciones como "Al contado" o "Fiado". | Alta |
| **RF-013** | Inventario | ...actualizar (restar) automáticamente el stock local tras cada venta confirmada. | Alta |
| **RF-014** | Inventario | ...sumar automáticamente el stock al registrar una compra a proveedores. | Alta |
| **RF-015** | Inventario | ...generar una alerta visual en rojo si un producto llega a 0 unidades. | Media |
| **RF-016** | Inventario | ...permitir agregar un nuevo producto al catálogo mediante comandos de voz. | Alta |
| **RF-017** | Inventario | ...responder consultas verbales de stock (ej. "¿Cuántas sodas me quedan?"). | Alta |
| **RF-018** | Outbox | ...guardar todas las transacciones en una cola local (SQLite) si está offline. | Alta |
| **RF-019** | Backend | ...detectar red y enviar transacciones encoladas automáticamente en formato JSON. | Alta |
| **RF-020** | Backend | ...resolver conflictos de stock en la nube mediante política LWW (Last Write Wins). | Alta |
| **RF-021** | Backend | ...notificar al usuario si un conflicto es irresoluble y requiere reconciliación manual. | Media |
| **RF-022** | Reportes | ...mostrar un Dashboard principal con el resumen de ingresos en tiempo real (del día). | Alta |
| **RF-023** | Reportes | ...generar un reporte visual de los 3 productos más vendidos en el mes. | Baja |
| **RF-024** | Configuración| ...permitir el entrenamiento dinámico del motor de voz con palabras locales ("yapa"). | Media |

### 2.2 Requisitos No Funcionales (RNF)
| Código | Categoría | Descripción (El sistema debe...) | Métrica |
| :--- | :--- | :--- | :--- |
| **RNF-001** | Rendimiento | ...estar desarrollado en Kotlin Nativo para asegurar consumo estricto de memoria. | **RAM < 500 MB** |
| **RNF-002** | Rendimiento | ...responder a interacciones de voz y renderizar la interfaz rápidamente. | **Latencia ≤ 2 s** |
| **RNF-003** | Disponibilidad| ...operar la lógica de negocio y ventas al 100% sin conexión a la red. | **Offline-first** |
| **RNF-004** | Seguridad | ...cifrar la base local (SQLCipher) y las comunicaciones (TLS 1.3) con JWT. | **AES-256 / JWT** |
| **RNF-005** | Accesibilidad | ...cumplir directrices WCAG 2.1 AA (contrastes altos, botones XL) para adultos mayores. | **WCAG 2.1 AA** |
| **RNF-006** | Compatibilidad| ...desplegar correctamente en teléfonos antiguos de pantallas pequeñas. | **Android 8+** |
| **RNF-007** | Calidad/UX | ...lograr aceptación real comprobada mediante evaluación estandarizada de usabilidad. | **SUS ≥ 70 pts** |

## 3. MODELADO DE CASOS DE USO
*(Nota: El diagrama C4 de Nivel 1 Contexto se encuentra en el Documento SAD).*

### Especificación Narrativa de los 3 Flujos Críticos
**Flujo 1: Registro de Venta mediante Voz (Con mitigación visual)**
*   **Actor:** Comerciante.
*   **Precondición:** Autenticado y Dashboard activo.
*   **Flujo Principal:** 
    1. Toca el botón flotante del micrófono y dicta: *"Vendí 3 Gaseosas"*. 
    2. El modelo PLN transcribe el audio offline y extrae la intención (Venta) y la cantidad (3). 
    3. El sistema muestra un Modal Gigante: *"¿Guardar venta de 3 Gaseosas?"* (Botones Verde SÍ / Rojo NO).
    4. El actor presiona "SÍ".
    5. El sistema persiste en SQLite y resta stock.
*   **Flujo Alternativo:** Si dice "Me equivoqué" o presiona "NO", el sistema anula la captura y vuelve a escuchar.

**Flujo 2: Sincronización Automática Outbox**
*   **Actor:** Sistema Android / Sistema Backend.
*   **Precondición:** El usuario registró ventas offline y existe cola pendiente.
*   **Flujo Principal:** 
    1. El OS detecta que el usuario activó sus Datos Móviles. 
    2. La App envía un POST REST con el JSON de operaciones pendientes. 
    3. El Backend Spring Boot recibe y aplica la política *Last Write Wins (LWW)* para resolver fechas. 
    4. El Backend retorna HTTP 200 OK. 
    5. La App limpia su cola de sincronización.

**Flujo 3: Creación de Producto Rápido**
*   **Actor:** Comerciante.
*   **Flujo Principal:** 
    1. El comerciante dicta: *"Nuevo producto: Galletas Mabel, cuesta 2 bolivianos"*. 
    2. La IA clasifica intención (Crear). 
    3. Pide confirmación visual. 
    4. El catálogo SQLite se actualiza y el producto ya puede ser "vendido" por voz.

## 4. DISEÑO UX/UI EN FIGMA
### 4.1 Investigación e Información
*   **Usuarios y Personas:**
    *   *María (52, Tienda):* Poca visión, detesta los menús. Requiere tipografía gigante (H1) y botones que no exijan precisión fina.
    *   *Carlos (38, Ferretería):* Mucho ruido ambiental (martillos, sierras). Requiere alto contraste de color para confirmar de reojo las ventas mientras atiende.
*   **Mapa de Sitio (Plano):**
    *   Dashboard (Voz) -> Inventario / Reportes (Máximo 2 niveles de profundidad).

### 4.2 Sistema de Diseño (Design System Figma)
*   **Paleta de Color:** Orientada a la neuro-asociación de dinero y alerta.
    *   *Primario:* Verde #2E7D32 (Éxito, ingreso monetario).
    *   *Peligro/Acción Negativa:* Rojo #F44336 (Cancelar, falta de stock).
*   **Tipografía y Componentes:** Material Design 3. Auto Layout para *Cards* flexibles. Pantalla base a 375px (Mobile-first).

### 4.3 Validación y Métricas del Prototipo
Al construir el prototipo en Figma, el éxito de la interfaz no se medirá solo visualmente, sino con 3 métricas de negocio para la MYPE:
1.  **Eficiencia:** (Tiempo de registro por voz en el celular) VS (Tiempo de escritura en el cuaderno).
2.  **Eficacia:** Reducción de la tasa de errores matemáticos en caja a fin de mes.
3.  **SUS (System Usability Scale):** Score de usabilidad validado empíricamente.

---
*(Fin del documento)*

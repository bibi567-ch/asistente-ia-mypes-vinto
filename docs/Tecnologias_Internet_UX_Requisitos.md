# Tecnologías en Internet — Requisitos, Casos de Uso y UX/UI

**Proyecto:** Asistente Conversacional Offline con PLN en el dispositivo para la gestión de ventas e inventario en MYPES del centro de Vinto, Cochabamba  
**Asignaturas:** Tecnologías en Internet y Arquitectura de Software  
**Gestión:** 2026  
**Estado:** documentación de requisitos y diseño; no evidencia implementación.

## 1. Propósito y alcance de la entrega

Este documento consolida los requisitos funcionales y no funcionales, los casos de uso críticos y los lineamientos UX/UI del proyecto. Los verbos “debe” expresan requisitos propuestos; las tecnologías mencionadas son alternativas de diseño sujetas a validación.

## 2. Requisitos funcionales

Todos los requisitos siguen la forma “El sistema debe + acción + entidad/objeto + condición o contexto”.

| ID | Módulo | Requisito | Prioridad |
|---|---|---|---|
| RF-001 | Acceso | El sistema debe permitir registrar una cuenta de comerciante mediante datos de identificación válidos. | Alta |
| RF-002 | Acceso | El sistema debe permitir iniciar sesión localmente cuando el dispositivo ya tenga una sesión autorizada y no exista conexión. | Alta |
| RF-003 | Perfil | El sistema debe permitir configurar el nombre y el rubro de la tienda desde el perfil del comerciante. | Media |
| RF-004 | Entrada | El sistema debe permitir introducir comandos mediante teclado en cualquier flujo compatible. | Alta |
| RF-005 | Voz | El sistema debe convertir voz a texto localmente cuando el canal de voz esté habilitado y exista un modelo disponible. | Alta |
| RF-006 | PLN | El sistema debe identificar la intención de una solicitud dentro del conjunto de operaciones soportadas. | Alta |
| RF-007 | PLN | El sistema debe extraer producto, cantidad, precio y modalidad de pago cuando esos datos estén presentes en la solicitud. | Alta |
| RF-008 | Confirmación | El sistema debe mostrar un resumen editable antes de confirmar una operación que modifique datos. | Alta |
| RF-009 | Ventas | El sistema debe registrar una venta confirmada con fecha, productos, cantidades y monto calculado. | Alta |
| RF-010 | Ventas | El sistema debe permitir cancelar o corregir una operación antes de su confirmación definitiva. | Alta |
| RF-011 | Ventas | El sistema debe permitir registrar una venta mediante formulario cuando la entrada por voz no sea adecuada. | Alta |
| RF-012 | Inventario | El sistema debe actualizar las existencias locales después de confirmar una venta válida. | Alta |
| RF-013 | Inventario | El sistema debe permitir registrar entradas de productos al inventario mediante un flujo explícito. | Alta |
| RF-014 | Inventario | El sistema debe permitir consultar las existencias de un producto desde el panel o mediante una solicitud compatible. | Alta |
| RF-015 | Sincronización | El sistema debe conservar localmente las operaciones pendientes cuando no exista conexión. | Alta |
| RF-016 | Sincronización | El sistema debe intentar sincronizar las operaciones pendientes cuando se detecte conectividad y se cumplan las condiciones de seguridad. | Alta |
| RF-017 | Sincronización | El sistema debe informar el estado de cada operación pendiente, sincronizada o fallida. | Media |
| RF-018 | Alertas | El sistema debe mostrar una alerta cuando la existencia de un producto alcance el umbral configurado. | Media |
| RF-019 | Reportes | El sistema debe mostrar un resumen de ventas del periodo seleccionado usando los datos disponibles localmente. | Media |
| RF-020 | Administración | El sistema debe permitir al rol autorizado revisar incidencias de sincronización, si el módulo administrativo forma parte del alcance final. | Baja |

## 3. Requisitos no funcionales

| ID | Categoría | Requisito | Criterio de verificación propuesto |
|---|---|---|---|
| RNF-001 | Disponibilidad | El sistema debe permitir las operaciones básicas de ventas e inventario sin conexión. | Prueba offline con operaciones exitosas. |
| RNF-002 | Rendimiento | El sistema debe responder a una consulta local simple dentro del umbral definido para el dispositivo objetivo. | Medición de latencia; objetivo inicial ≤ 2 s, por validar. |
| RNF-003 | Recursos | El sistema debe funcionar en dispositivos Android de recursos limitados sin cierres inesperados durante la prueba definida. | Prueba en dispositivo representativo y registro de consumo. |
| RNF-004 | Seguridad | El sistema debe proteger las credenciales, los datos locales y las comunicaciones mediante mecanismos adecuados al diseño aprobado. | Revisión de configuración y pruebas de acceso. |
| RNF-005 | Integridad | El sistema debe evitar duplicar operaciones al reintentar una sincronización. | Prueba de reintentos con identificadores idempotentes. |
| RNF-006 | Usabilidad | El sistema debe permitir completar las tareas críticas con instrucciones mínimas y mensajes comprensibles. | Prueba con usuarios y tasa de éxito. |
| RNF-007 | Accesibilidad | El sistema debe aplicar contraste, tamaño de controles, jerarquía visual y navegación compatibles con WCAG 2.1 AA en la medida aplicable al producto móvil. | Lista de verificación y revisión del prototipo. |
| RNF-008 | Compatibilidad | El sistema debe documentar la versión mínima de Android y validar el comportamiento en los dispositivos objetivo. | Matriz de compatibilidad. |
| RNF-009 | Mantenibilidad | El sistema debe separar presentación, dominio, persistencia e integración mediante responsabilidades claramente delimitadas. | Revisión arquitectónica del código cuando exista. |
| RNF-010 | Trazabilidad | El sistema debe registrar el estado de las operaciones para facilitar diagnóstico y auditoría. | Inspección de registros y pruebas de error. |

## 4. Casos de uso críticos

### CU-01 — Registrar venta

- **Actor principal:** Comerciante.
- **Precondiciones:** El comerciante tiene acceso a la aplicación y existe un catálogo disponible.
- **Disparador:** El comerciante desea registrar una venta.
- **Flujo principal:**
  1. El comerciante selecciona voz o teclado.
  2. Introduce producto, cantidad y datos disponibles.
  3. El sistema interpreta o valida los datos.
  4. El sistema muestra un resumen para revisión.
  5. El comerciante confirma.
  6. El sistema guarda la operación localmente y actualiza las existencias.
  7. Si no existe conexión, la operación queda pendiente de sincronización.
- **Alternativas:** Datos incompletos; producto inexistente; cancelación; error de persistencia.
- **Postcondición:** La venta queda registrada o se informa claramente por qué no fue confirmada.

### CU-02 — Consultar inventario

- **Actor principal:** Comerciante.
- **Precondiciones:** Existe catálogo local.
- **Flujo principal:**
  1. El comerciante abre Inventario o realiza una consulta compatible.
  2. El sistema busca el producto.
  3. El sistema muestra nombre, existencia y fecha de actualización disponible.
- **Alternativas:** Producto no encontrado; datos pendientes de sincronización.
- **Postcondición:** El comerciante obtiene información identificada como local o sincronizada.

### CU-03 — Sincronizar operaciones pendientes

- **Actor principal:** Sistema.
- **Precondiciones:** Existen operaciones pendientes y conectividad disponible.
- **Flujo principal:**
  1. El sistema detecta conectividad.
  2. Valida autenticación y prepara operaciones con identificadores únicos.
  3. Envía el lote al backend.
  4. El backend responde por operación o lote.
  5. La aplicación marca como sincronizadas las operaciones confirmadas.
  6. Las fallidas permanecen pendientes con información del error.
- **Alternativas:** Sin conexión, token inválido, conflicto, respuesta parcial o timeout.
- **Postcondición:** Cada operación conserva un estado verificable.

## 5. Personas y necesidades UX

### Persona 1 — Comerciante de tienda de barrio

- Necesita registrar ventas rápidamente mientras atiende clientes.
- Puede tener poca experiencia con sistemas administrativos.
- Valora botones grandes, lenguaje simple, confirmación visible y funcionamiento sin Internet.

### Persona 2 — Comerciante de rubro con ruido ambiental

- Trabaja en un entorno con conversaciones y maquinaria.
- Necesita alternar entre voz y teclado.
- Requiere mensajes claros cuando el sistema no comprende una instrucción.

Estas personas son perfiles de diseño propuestos; deben validarse con usuarios reales antes de generalizar sus características.

## 6. Arquitectura de información y flujos

Mapa de sitio propuesto:

- Inicio/Dashboard
  - Registrar venta
  - Consultar inventario
  - Resumen de ventas
  - Sincronización
  - Configuración

Flujos detallados adicionales: `diagrams/Flujos_Usuario.md`.

## 7. Sistema de diseño y prototipo

El prototipo de referencia se encuentra en Figma:  
https://snake-dijon-92194767.figma.site/

El sistema de diseño debe documentar:

- Colores y tokens.
- Tipografía y jerarquías.
- Componentes reutilizables y variantes.
- Auto Layout, propiedades y variables.
- Estados de carga, vacío, error, confirmación y sincronización.
- Vistas desktop y móvil según la rúbrica.
- Contraste, tamaño de controles y navegación accesible.

La existencia de un enlace de prototipo no demuestra por sí sola que todos los criterios de accesibilidad o responsive hayan sido verificados; estos deben comprobarse mediante una lista de revisión y capturas.

## 8. Trazabilidad y estado de validación

Los requisitos se relacionan con la arquitectura en `docs/05_Matriz_Trazabilidad.md`. Los criterios de la rúbrica y las evidencias pendientes se controlan en `docs/06_Auditoria_Rubricas.md` y `docs/08_Evidencias.md`.

**Regla de consistencia:** ningún requisito, tecnología, métrica o funcionalidad debe presentarse como implementado, probado o validado si no existe evidencia correspondiente.

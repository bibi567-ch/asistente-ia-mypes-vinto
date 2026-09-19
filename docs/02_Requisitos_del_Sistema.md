# 2. Requisitos del Sistema

**Estado documental:** requisitos propuestos para validación académica. No representan funcionalidades implementadas hasta contar con evidencia de desarrollo y pruebas.

## 2.1 Convención de redacción

Se utiliza la estructura formal:

> **El sistema debe + acción + entidad/objeto + condición o contexto.**

Las prioridades se interpretan así:

- **Alta:** necesaria para el MVP o para demostrar el flujo principal.
- **Media:** importante, pero puede implementarse después del flujo principal.
- **Baja:** complementaria y susceptible de posponerse.

## 2.2 Requisitos Funcionales

| Código | Módulo | Requisito | Prioridad |
|---|---|---|---|
| RF-001 | Autenticación | El sistema debe autenticar al comerciante mediante un mecanismo local de acceso antes de mostrar información privada. | Alta |
| RF-002 | Productos | El sistema debe registrar productos con nombre, precio, stock y stock mínimo en el catálogo local. | Alta |
| RF-003 | Productos | El sistema debe permitir actualizar los datos de un producto registrado desde la interfaz de gestión. | Alta |
| RF-004 | Ventas | El sistema debe registrar una venta manual con uno o varios productos y sus cantidades. | Alta |
| RF-005 | Ventas | El sistema debe calcular el total de una venta considerando los precios y cantidades confirmados por el usuario. | Alta |
| RF-006 | Inventario | El sistema debe descontar del inventario las cantidades correspondientes a una venta confirmada. | Alta |
| RF-007 | Inventario | El sistema debe permitir consultar el stock disponible de un producto mediante búsqueda textual. | Alta |
| RF-008 | Alertas | El sistema debe mostrar una alerta cuando el stock de un producto sea igual o inferior al stock mínimo configurado. | Media |
| RF-009 | Voz | El sistema debe convertir un comando de voz compatible en texto utilizando procesamiento local cuando el modelo esté disponible en el dispositivo. | Alta |
| RF-010 | Voz | El sistema debe identificar la intención y las entidades principales de un comando compatible antes de proponer una operación. | Alta |
| RF-011 | Confirmación | El sistema debe mostrar un resumen de la operación interpretada y solicitar confirmación antes de guardar cambios críticos. | Alta |
| RF-012 | Sincronización | El sistema debe enviar operaciones locales pendientes al servidor cuando exista conectividad y se cumplan las condiciones de sincronización. | Media |
| RF-013 | Pendientes | El sistema debe mostrar las operaciones pendientes de sincronización y su estado actual. | Media |
| RF-014 | Reportes | El sistema debe mostrar un resumen de ventas del día a partir de los registros disponibles localmente. | Media |
| RF-015 | Historial | El sistema debe permitir consultar el historial de ventas registradas por el comerciante. | Media |

## 2.3 Requisitos No Funcionales

| Código | Categoría | Requisito verificable | Prioridad |
|---|---|---|---|
| RNF-001 | Rendimiento | El sistema debe mostrar la pantalla principal en un tiempo objetivo menor o igual a 2 segundos en un dispositivo Android de referencia definido por el equipo. | Alta |
| RNF-002 | Rendimiento | El sistema debe procesar un comando de voz compatible dentro de un objetivo inicial de 2 segundos, sujeto a medición con un conjunto de pruebas definido. | Alta |
| RNF-003 | Disponibilidad | El sistema debe permitir registrar ventas manuales sin conexión a Internet durante el funcionamiento normal de la aplicación. | Alta |
| RNF-004 | Seguridad | El sistema debe proteger los datos locales sensibles mediante cifrado en reposo y controlar el acceso a la información del comerciante. | Alta |
| RNF-005 | Seguridad | El sistema debe transmitir información al backend mediante HTTPS cuando exista sincronización remota. | Alta |
| RNF-006 | Compatibilidad | El sistema debe funcionar en la versión mínima de Android definida en la matriz de compatibilidad del proyecto. | Alta |
| RNF-007 | Usabilidad | El sistema debe presentar mensajes de confirmación, error y estado de sincronización comprensibles para usuarios con experiencia tecnológica limitada. | Alta |
| RNF-008 | Accesibilidad | El sistema debe mantener contraste, tamaños táctiles, jerarquía visual y navegación compatibles con los criterios aplicables de WCAG 2.1 AA. | Alta |
| RNF-009 | Integridad | El sistema debe evitar el descuento duplicado de inventario cuando una operación se reintente durante la sincronización. | Alta |
| RNF-010 | Mantenibilidad | El sistema debe separar presentación, dominio, persistencia y comunicación remota mediante responsabilidades claramente delimitadas. | Media |

> **Nota:** los valores de rendimiento son objetivos de diseño hasta que se ejecuten pruebas instrumentadas. No deben presentarse como resultados comprobados en esta primera entrega.

## 2.4 Requisitos Arquitectónicamente Significativos

| ID | Requisito relacionado | Impacto arquitectónico | Prioridad |
|---|---|---|---|
| RA-01 | RNF-003 / RF-004 | Requiere persistencia local y un flujo offline-first para operaciones esenciales. | Alta |
| RA-02 | RF-009 / RF-010 | Requiere una canalización conversacional local con reconocimiento, clasificación y extracción de entidades. | Alta |
| RA-03 | RF-011 | Requiere una etapa explícita de confirmación antes de ejecutar operaciones críticas. | Alta |
| RA-04 | RF-012 / RF-013 | Requiere cola de operaciones pendientes, reintentos, estados e idempotencia. | Media |
| RA-05 | RNF-004 | Requiere una estrategia de protección de datos locales y control de acceso. | Alta |
| RA-06 | RNF-009 | Requiere identificadores únicos de operación y reglas para evitar duplicaciones. | Alta |
| RA-07 | RNF-010 | Requiere separación de responsabilidades y dependencias controladas entre capas. | Media |

## 2.5 Criterios de validación pendientes

Antes de considerar estos requisitos como aprobados, el equipo debe validar:

- [ ] La versión mínima real de Android.
- [ ] El dispositivo de referencia para pruebas.
- [ ] El conjunto de comandos de voz admitidos por el MVP.
- [ ] Las entidades mínimas reconocidas: producto, cantidad, precio, cliente y modalidad de pago.
- [ ] La estrategia definitiva de autenticación local.
- [ ] El mecanismo de resolución de conflictos de inventario.
- [ ] Los umbrales de rendimiento mediante pruebas reproducibles.

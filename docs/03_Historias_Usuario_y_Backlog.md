# Historias de Usuario y Product Backlog

**Proyecto:** Asistente Conversacional Offline para la Gestión de MYPES de Vinto  
**Versión:** 1.1  
**Estado:** Documentación de primera entrega; no implica implementación.

## 1. Épicas

- **E1 — Interacción conversacional:** entrada por voz, interpretación y confirmación.
- **E2 — Ventas e inventario:** operaciones comerciales locales.
- **E3 — Experiencia de usuario:** dashboard, accesibilidad y navegación.
- **E4 — Persistencia y sincronización:** almacenamiento local y respaldo diferido.
- **E5 — Seguridad y configuración:** protección de datos y perfil de negocio.

## 2. Backlog trazable

| ID | Épica | Historia de usuario | Requisitos relacionados | Prioridad | Puntos |
|---|---|---|---|---|---:|
| US-01 | E1 | Como comerciante, quiero ingresar comandos por voz sin conexión para registrar operaciones sin depender de Internet. | RF-005, RNF-001 | Alta | 8 |
| US-02 | E1 | Como comerciante, quiero que el sistema identifique intención, producto y cantidad para reducir el registro manual. | RF-006, RF-007 | Alta | 13 |
| US-03 | E1 | Como comerciante, quiero confirmar visualmente una operación interpretada antes de guardarla para evitar errores. | RF-008 | Alta | 5 |
| US-04 | E2 | Como comerciante, quiero registrar ventas confirmadas para mantener actualizado mi control diario. | RF-009, RF-012 | Alta | 8 |
| US-05 | E2 | Como comerciante, quiero registrar ventas mediante teclado cuando exista ruido o dificultad de reconocimiento. | RF-011 | Alta | 5 |
| US-06 | E2 | Como comerciante, quiero consultar el stock mediante voz o interfaz para conocer qué productos necesito reponer. | RF-013 | Alta | 5 |
| US-07 | E2 | Como comerciante, quiero registrar entradas de mercadería para actualizar existencias. | RF-013 | Alta | 8 |
| US-08 | E2 | Como comerciante, quiero recibir alertas cuando un producto llegue a cero para identificar faltantes. | RF-018 | Media | 3 |
| US-09 | E3 | Como comerciante, quiero visualizar un resumen diario de ventas para comprender el movimiento de mi negocio. | RF-016 | Alta | 5 |
| US-10 | E4 | Como comerciante, quiero que las operaciones se guarden localmente cuando no haya Internet para no perder información. | RF-015, RNF-001 | Alta | 8 |
| US-11 | E4 | Como comerciante, quiero que las operaciones pendientes se sincronicen cuando exista conexión para disponer de respaldo. | RF-016, RF-017 | Alta | 13 |
| US-12 | E5 | Como propietario, quiero configurar el nombre y rubro de mi tienda para personalizar el sistema. | RF-003 | Media | 3 |

## 3. Criterios de aceptación representativos

### US-01 — Entrada por voz offline
- El sistema permite iniciar la captura desde la pantalla principal.
- La captura no requiere conexión a Internet.
- Si no se obtiene una transcripción confiable, se informa al usuario y se ofrece repetir o usar teclado.

### US-03 — Confirmación de operación
- La intención, producto, cantidad y monto identificado se muestran antes de guardar.
- El usuario puede confirmar o cancelar.
- Una operación cancelada no modifica ventas ni inventario.

### US-04 — Registro de venta
- Solo una operación confirmada se registra.
- Se almacenan fecha, hora, productos, cantidades, total y modalidad de pago.
- El inventario local se actualiza de manera consistente con la venta.

### US-10 — Persistencia local
- Una operación creada sin conexión queda en estado pendiente de sincronización.
- La operación permanece disponible después de cerrar y abrir la aplicación.
- El sistema informa si el almacenamiento local no está disponible.

### US-11 — Sincronización
- Las operaciones pendientes se envían únicamente cuando existe conectividad.
- Cada operación posee un identificador único para evitar duplicados.
- Los errores de sincronización quedan registrados y pueden reintentarse.

## 4. Caso de uso principal: Registrar venta

**Actor principal:** Comerciante.  
**Precondiciones:** catálogo disponible y sesión local válida.  
**Flujo principal:** iniciar captura → ingresar comando → interpretar → mostrar resumen → confirmar → guardar localmente → actualizar inventario → informar resultado.  
**Alternativas:** audio no comprendido, producto inexistente, stock insuficiente, cancelación del usuario o almacenamiento no disponible.  
**Postcondición:** venta confirmada almacenada localmente o flujo cancelado sin cambios.

## 5. Regla de gestión

Las historias describen comportamiento esperado. No constituyen evidencia de implementación ni de validación. Los puntos son estimaciones preliminares del equipo y deberán revisarse durante la planificación.

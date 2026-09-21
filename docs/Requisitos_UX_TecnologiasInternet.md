# ESPECIFICACIÓN DE REQUISITOS Y DISEÑO UX/UI (v1.1)

**UNIVERSIDAD ADVENTISTA DE BOLIVIA — INGENIERÍA EN SISTEMAS**
**ASIGNATURA:** Tecnologías en Internet
**SISTEMA:** Asistente Conversacional Offline para la Gestión de MYPES en Vinto, Cochabamba
**INTEGRANTES:** Eva Chino Quispe, Christian Gonzales, Marializ Mamani, Kevin Rocha, Luis Lazo
**DOCENTE:** _(completar)_ — **FECHA:** _(completar)_ — **VERSIÓN:** 1.1

> Este documento reemplaza y consolida `02_Requisitos_del_Sistema.md` y la primera versión de `Tecnologias_Internet_UX_Requisitos.md`, eliminando la duplicidad de matrices de requisitos (10 vs. 24 RF) y de personas UX (María/Carlos vs. Don Juan). Es el documento **único y oficial** para esta asignatura.

---

## 1. DATOS GENERALES DE LA ENTREGA
- **Modalidad:** Equipo de 5 integrantes.
- **Formato de entrega:** enlace público a Figma + esta documentación técnica (Markdown/PDF).
- **Enlaces Figma:**
  - Prototipo navegable: https://snake-dijon-92194767.figma.site/
  - Sistema de diseño: https://www.figma.com/make/jr73cU7am5OisMNhkgxkkr/Sistema-de-Diseño-MYPES-Vinto

## 2. MATRIZ DE REQUISITOS FUNCIONALES (RF)

Sintaxis formal obligatoria: *«El sistema debe [acción] + [entidad/objeto] + [condición/contexto]»*.

| Código | Módulo | Descripción del Requisito | Prioridad |
| :--- | :--- | :--- | :--- |
| RF-001 | Autenticación | El sistema debe permitir el registro del comerciante validando su número de teléfono. | Alta |
| RF-002 | Autenticación | El sistema debe permitir el inicio de sesión offline mediante un PIN local de 4 dígitos. | Alta |
| RF-003 | Perfil | El sistema debe permitir configurar el nombre de la tienda y el rubro comercial. | Media |
| RF-004 | PLN Edge | El sistema debe transcribir el audio a texto localmente sin usar Internet (motor Vosk). | Alta |
| RF-005 | PLN Edge | El sistema debe clasificar la intención del comando de voz (Venta, Compra, Fiado, Cobro, Consulta, Gasto). | Alta |
| RF-006 | PLN Edge | El sistema debe extraer las entidades clave del comando (producto, cantidad, unidad, monto, cliente). | Alta |
| RF-007 | Mitigación de errores | El sistema debe mostrar una confirmación visual obligatoria antes de persistir cualquier venta dictada por voz. | Alta |
| RF-008 | Navegación | El sistema debe cancelar cualquier flujo activo si el usuario dice "cancelar" o "me equivoqué". | Alta |
| RF-009 | Ventas | El sistema debe registrar una venta asignando fecha, hora, producto(s) y monto total. | Alta |
| RF-010 | Ventas | El sistema debe permitir registrar una venta manualmente (teclado) cuando el entorno sea muy ruidoso. | Media |
| RF-011 | Ventas | El sistema debe permitir aplicar descuentos verbales sobre el monto dictado. | Media |
| RF-012 | Ventas | El sistema debe clasificar cada transacción como "al contado" o "fiado". | Alta |
| RF-013 | Inventario | El sistema debe descontar automáticamente el stock local tras cada venta confirmada. | Alta |
| RF-014 | Inventario | El sistema debe incrementar automáticamente el stock al registrar una compra a proveedores. | Alta |
| RF-015 | Inventario | El sistema debe generar una alerta visual cuando un producto llegue a 0 unidades. | Media |
| RF-016 | Inventario | El sistema debe permitir agregar un nuevo producto al catálogo mediante comando de voz o formulario. | Alta |
| RF-017 | Inventario | El sistema debe responder consultas verbales de stock disponible por producto. | Alta |
| RF-018 | Sincronización | El sistema debe encolar (Outbox) todas las transacciones locales cuando no haya conexión a Internet. | Alta |
| RF-019 | Sincronización | El sistema debe detectar la red disponible y enviar las transacciones encoladas automáticamente en formato JSON. | Alta |
| RF-020 | Sincronización | El sistema debe resolver los conflictos de stock en la nube mediante la política LWW (Last Write Wins). | Media |
| RF-021 | Panel principal | El sistema debe desplegar un dashboard con el resumen de ventas del día al iniciar sesión. | Alta |
| RF-022 | Reportes | El sistema debe generar un reporte visual de los productos más vendidos del mes. | Baja |

## 3. REQUISITOS NO FUNCIONALES (RNF)

| Código | Categoría | Descripción del Requisito | Métrica |
| :--- | :--- | :--- | :--- |
| RNF-001 | Rendimiento | El sistema debe estar desarrollado en Kotlin nativo para asegurar un consumo de memoria estricto. | RAM < 500 MB |
| RNF-002 | Rendimiento | El sistema debe procesar el comando de voz y emitir una respuesta en un tiempo acotado. | Latencia ≤ 2 s |
| RNF-003 | Disponibilidad | El sistema debe operar la lógica de negocio y el registro de ventas al 100 % sin conexión a Internet. | Offline-first |
| RNF-004 | Seguridad | El sistema debe cifrar la base de datos local y las comunicaciones con el backend. | AES-256 (SQLCipher) / TLS 1.3 + JWT |
| RNF-005 | Usabilidad / Accesibilidad | El sistema debe cumplir las pautas WCAG 2.1 nivel AA (contraste alto, botones grandes, tipografía legible). | WCAG 2.1 AA |
| RNF-006 | Compatibilidad | El sistema debe funcionar de forma fluida en dispositivos Android 8.0 o superior. | Android 8+ |
| RNF-007 | Calidad / UX | El sistema debe lograr una aceptación de usabilidad comprobada empíricamente. | SUS ≥ 70 pts |

## 4. MODELADO DE CASOS DE USO

*(El diagrama C4 Nivel 1 — Contexto — se encuentra en el Documento SAD, sección 10.1, y es el mismo diagrama utilizado en ambas asignaturas para garantizar consistencia).*

### Caso de Uso 1 — Registrar Venta por Voz
- **Actor:** Comerciante.
- **Precondición:** Usuario autenticado, dashboard activo, catálogo con productos.
- **Flujo principal:**
  1. El comerciante presiona el botón flotante del micrófono y dicta: *"Vendí 3 gaseosas"*.
  2. El motor Vosk transcribe el audio offline; el clasificador TFLite extrae intención (Venta) y entidades (cantidad=3, producto="gaseosa").
  3. El sistema muestra un modal grande de confirmación: *"¿Guardar venta de 3 gaseosas?"* (botón verde SÍ / botón rojo NO).
  4. El usuario confirma con "SÍ".
  5. El sistema persiste la venta en SQLite y descuenta el stock.
- **Flujos alternativos:**
  - 2a. El motor no reconoce el producto → el sistema pregunta: "¿Puedes repetir el producto?".
  - 4a. El usuario dice "me equivoqué" o presiona "NO" → se anula la captura y el sistema vuelve a escuchar.
- **Postcondición:** inventario actualizado localmente; transacción con timestamp guardada en la cola Outbox.

### Caso de Uso 2 — Sincronizar Transacciones Offline con el Servidor Cloud
- **Actores:** Sistema Android / Backend Spring Boot.
- **Precondición:** existen transacciones pendientes en la cola Outbox local.
- **Flujo principal:**
  1. El sistema operativo detecta la activación de datos móviles o Wi-Fi.
  2. La app envía un `POST` REST con el JSON de las operaciones pendientes, autenticado vía JWT.
  3. El backend Spring Boot recibe las transacciones y aplica la política LWW para resolver posibles conflictos de stock.
  4. El backend responde `HTTP 200 OK` con el resultado de la consolidación.
  5. La app limpia su cola Outbox local de los ítems confirmados.
- **Flujo alternativo:** si el backend detecta un conflicto irresoluble automáticamente (RF-021), marca la transacción para reconciliación manual y notifica al usuario en el dashboard.
- **Postcondición:** los datos del comerciante quedan respaldados en PostgreSQL.

### Caso de Uso 3 — Consultar Inventario y Ventas del Día
*Narrativa elaborada por Luis Lazo (Arquitectura C4 y QA Técnico).*
- **Actor principal:** Comerciante de MYPE. **Actores secundarios:** Motor ASR Vosk, Clasificador TFLite, Persistencia Local (Room).
- **Precondiciones:** sesión iniciada (PIN/biometría); catálogo de productos cargado; al menos una venta del día (para resumen) o catálogo poblado (para stock).
- **Disparador:** el comerciante necesita conocer stock de un producto y/o el total vendido en el día.
- **Flujo principal:**
  1. El comerciante abre "Consultas" o dicta un comando (ej. "¿Cuánto vendí hoy?" o "Stock de aceite").
  2. El Orquestador Conversacional envía la entrada al motor ASR (voz) o la procesa directo (teclado).
  3. El Clasificador TFLite identifica la intención: `CONSULTAR_STOCK` (extrae "producto") o `CONSULTAR_RESUMEN_DIARIO`.
  4. El Orquestador consulta Room: stock → existencia actual; resumen → suma de ventas confirmadas del día en BS.
  5. La interfaz despliega el resultado (stock: nombre/existencia/estado; resumen: total/transacciones/última actualización).
  6. Se muestra el origen de los datos ("Datos locales · Última sincronización: hace 12 min").
- **Flujos alternativos:**
  - 3a. Producto no encontrado → ofrece "Buscar con otro nombre" o "Registrar nuevo producto".
  - 3b. Comando no comprendido (confianza < 0.75) → fallback a teclado, sin perder contexto.
  - 4a. Sin ventas del día → estado vacío con sugerencia "Registrar primera venta".
  - 4b. Operaciones pendientes de sincronizar → badge visible ("⚠ 3 pendientes"), resumen distingue sincronizadas de pendientes.
  - 5a. Error de lectura local → mensaje de error, log local, reintento o cierre seguro de sesión.
- **Postcondición:** el comerciante visualiza el estado de las existencias y/o el resumen de ventas del día, con indicación del origen y estado de sincronización. Ninguna escritura en base de datos.
- **Escenarios de calidad que valida:** QS-01 (Rendimiento ≤ 2 s), QS-02 (Disponibilidad offline), QS-10 (Sincronización/Usabilidad).

## 5. DISEÑO UX/UI EN FIGMA

### 5.1 Usuarios y Personas — Mapas de Empatía
*Elaborados por Kevin Rocha (UX/UI) y Marializ Mamani.*

**Persona 1: María Elena Quispe Flores — Comerciante de Tienda de Barrio**
Edad: 52 años · Ubicación: Av. 6 de Agosto, Vinto · Ocupación: dueña de tienda de abarrotes "El Hogar" · Nivel educativo: primaria completa · Dispositivo: Samsung Galaxy J2 Prime (Android 8.1, 1.5 GB RAM) · Alfabetización digital: baja (solo WhatsApp para llamadas y notas de voz).

| Dimensión | Contenido |
| :--- | :--- |
| Piensa y siente | "Necesito controlar mi mercadería, pero no tengo tiempo para aprender sistemas complicados. Temo perder plata por no saber cuánto vendí en el día". |
| Ve | Vecinos usando cuadernos, sus hijos usando el celular con facilidad, la competencia creciendo. |
| Oye | "Los sistemas son caros y difíciles", "Internet falla todo el tiempo en Vinto", "Si le hablas al celular, es más fácil". |
| Dice y hace | "Yo no sé de computadoras, prefiero mi cuaderno"; anota todo a mano y calcula mentalmente. |
| Esfuerzos (pains) | Pierde tiempo contando mercadería, errores al calcular ganancias, mercadería que se vence sin darse cuenta. |
| Resultados (gains) | Quiere saber cuánto ganó al día sin complicaciones; necesita alertas de stock; desea un sistema que "le hable" en vez de obligarla a leer menús. |

*Cita clave: "Si el celular me entendiera cuando le hablo como a una persona, sí lo usaría. Pero si tengo que aprender botones y menús, mejor sigo con mi cuaderno".*

**Persona 2: Carlos Mamani Condori — Comerciante con Ruido Ambiental**
Edad: 38 años · Ubicación: Mercado Central, Vinto · Ocupación: dueño de ferretería "El Constructor" · Nivel educativo: secundaria + curso técnico · Dispositivo: Xiaomi Redmi 9A (Android 10, 2 GB RAM) · Alfabetización digital: media-baja (WhatsApp, Facebook Marketplace, YouTube).

| Dimensión | Contenido |
| :--- | :--- |
| Piensa y siente | "Necesito modernizarme para competir. Tengo miedo de que el sistema borre mis datos si se va el internet". |
| Ve | Ferreterías grandes usando computadoras, clientes pidiendo factura rápido, competidores con mejores precios. |
| Oye | "Debes tener un sistema para no perder ventas", "El internet en Vinto es malo", "Los sistemas necesitan internet sí o sí". |
| Dice y hace | "Mi celular es viejo pero funciona"; usa WhatsApp para pedidos y anota las fiadas en un cuaderno aparte. |
| Esfuerzos (pains) | Pierde ventas por no encontrar rápido el precio, clientes se van por la demora, no sabe su margen de ganancia real. |
| Resultados (gains) | Quiere registrar ventas rápido con la voz; necesita saber su stock sin contar manualmente; desea algo que funcione 100% sin internet. |

*Cita clave: "Si el sistema me entendiera por voz y funcionara sin internet, lo usaría. Pero si depende de internet y se pone lento, mejor sigo como estoy".*

### 5.2 Mapa de Sitio
Estructura plana, máximo 2 niveles de profundidad:
`Login/PIN → Dashboard (Voz) → {Inventario, Reportes, Fiados}`

### 5.3 Sistema de Diseño (Figma)
- **Paleta de color:** primario Verde `#2E7D32` (éxito / ingreso monetario); negativo Rojo `#F44336` (cancelar / falta de stock); neutros en escala de grises con contraste WCAG AA verificado.
- **Tipografía:** escala modular basada en Material Design 3; jerarquía H1/H2/H3/Body/Small configurada como Text Styles en Figma.
- **Componentes:** botón principal de micrófono con retroalimentación visual (ondas de audio), tarjetas de confirmación, inputs, modales, barra de navegación inferior — todos construidos con Auto Layout y Component Properties (variantes).
- **Estados obligatorios por componente clave:** Vacío, Carga, Error/Alerta, Éxito — deben estar diseñados explícitamente para el flujo de Registrar Venta y el flujo de Sincronización.
- **Tokens/Variables:** variables de Figma para espaciado y, si aplica, modo claro/oscuro.

### 5.4 Prototipado
- Pantallas responsive: Desktop 1440 px (panel de reportes del dueño) y Mobile 375/390 px (uso diario del comerciante, prioritario).
- Flujo navegable completo y sin enlaces rotos para los 3 casos de uso de la sección 4.
- Enlace de Figma con permisos de lectura pública, verificado en modo incógnito.

### 5.5 Métricas de Validación del Prototipo
1. **Eficiencia:** tiempo de registro por voz vs. tiempo de escritura en cuaderno físico.
2. **Eficacia:** reducción de la tasa de errores matemáticos en el cierre de caja.
3. **SUS (System Usability Scale):** puntaje de usabilidad ≥ 70 (RNF-007).

## 6. CHECKLIST DE ENTREGA

- [ ] Enlace público a Figma con permisos de lectura activos, verificado en incógnito.
- [ ] Documento escrito con portada, índice y control de versiones.
- [ ] Matriz de requisitos completa (22 RF, 7 RNF — supera el mínimo de 10 RF y 5 RNF exigido).
- [ ] Diagrama de Casos de Uso (UML) + Diagrama C4 Nivel 1 incluidos como imágenes/vectores.
- [ ] Sistema de diseño en Figma con componentes reutilizables, variantes y los 4 estados (Vacío, Carga, Error, Éxito).
- [ ] Prototipo navegable comprobado sin enlaces rotos en el flujo principal.

---
*Tecnologías en Internet — Proyecto Integrador — Documento consolidado v1.1*

# Especificación de requisitos y diseño UX/UI

**Universidad Adventista de Bolivia · Ingeniería en Sistemas · Tecnologías en Internet**

| Control del documento | Detalle |
|---|---|
| Versión de la línea base académica | 1.1 |
| Sistema | Asistente conversacional offline para MYPES de Vinto, Cochabamba |
| Integrantes | Eva Chino Quispe, Christian Gonzales, Marializ Mamani, Kevin Rocha y Luis Lazo |
| Docente | Completar con el dato oficial de la asignatura |
| Fecha de la línea base | Confirmar con la portada original antes de entregar |
| Actualización técnica | 13 de noviembre de 2026 |

> **Cómo leer este documento.** La especificación v1.1 conserva los requisitos académicos de referencia. La sección final contrasta esa línea base con el código revisado; un requisito escrito o implementado no se considera probado automáticamente. Este documento no sustituye la rúbrica oficial ni una aprobación docente.

> Nombre operativo: asistente conversacional offline para MYPES de Vinto. RF-014 queda excluido: no se registran compras a proveedores.


---

## 1. DATOS GENERALES DE LA ENTREGA
- **Modalidad:** Equipo de 5 integrantes.
- **Formato de entrega:** enlace público a Figma + esta documentación técnica (Markdown/PDF).
- **Figma vigente:** [Mockup Improvement Request — Community](https://www.figma.com/make/detrwbrxCanqoPjHb9TjwX/Mockup-Improvement-Request--Community---copia---copia-?p=f&t=qANDWN72XqQ9Pt4y-0). Las URLs anteriores no son la referencia visual vigente.

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
| RF-013 | Saldo local | El sistema debe descontar automáticamente el stock local tras cada venta confirmada. | Alta |
| RF-014 | Excluido | EXCLUIDO el 13/10/2026. No se registran compras a proveedores. Queda fuera del perfil y no se implementa. | — |
| RF-015 | Saldo local | El sistema debe generar una alerta visual cuando un producto llegue a 0 unidades. | Media |
| RF-016 | Saldo local | El sistema debe permitir agregar un nuevo producto al catálogo mediante comando de voz o formulario. | Alta |
| RF-017 | Saldo local | El sistema debe responder consultas verbales de stock disponible por producto. | Alta |
| RF-018 | Sincronización | El sistema debe encolar (Outbox) todas las transacciones locales cuando no haya conexión a Internet. | Alta |
| RF-019 | Sincronización | El sistema debe detectar la red disponible y enviar las transacciones encoladas automáticamente en formato JSON. | Alta |
| RF-020 | Sincronización | El sistema debe identificar operaciones concurrentes sobre existencias y conservar el historial de ventas, evitando descartar transacciones mediante sobrescritura automática. | Alta |
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
- **Actores:** Sistema Android / API Node.js + TypeScript + Express (línea base objetivo, pendiente de actualizar en SAD v2.0).
- **Precondición:** existen transacciones pendientes en la cola Outbox local.
- **Flujo principal:**
  1. El sistema operativo detecta la activación de datos móviles o Wi-Fi.
  2. La app envía un `POST` REST con el JSON de las operaciones pendientes, autenticado vía JWT.
  3. La API Node.js recibe eventos idempotentes y preserva las ventas; los conflictos de inventario se marcan para reconciliación según la política que defina el SAD v2.0.
  4. El backend responde `HTTP 200 OK` con el resultado de la consolidación.
  5. La app limpia su cola Outbox local de los ítems confirmados.
- **Flujo alternativo:** si el backend detecta un conflicto irresoluble automáticamente (RF-020), marca la operación de inventario para reconciliación manual y notifica al usuario en el dashboard; conserva intactos los eventos de venta.
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

### 5.1 Arquetipos de usuario y mapas de empatía

Los siguientes arquetipos se utilizan para orientar decisiones de diseño. Son escenarios ilustrativos del equipo, no entrevistas, participantes reales ni resultados de investigación de campo. Las frases en primera persona son necesidades hipotéticas y no citas textuales de comerciantes.

**Arquetipo 1 — Comerciante de tienda de barrio**

Perfil ilustrativo: persona adulta que administra un pequeño comercio, usa el teléfono principalmente para llamadas y mensajería y prefiere instrucciones directas. No se asignan identidad, dirección ni dispositivo reales.

| Dimensión | Contenido |
| :--- | :--- |
| Piensa y siente | Necesita controlar sus productos sin dedicar demasiado tiempo a aprender una herramienta compleja. |
| Ve | Vecinos usando cuadernos, sus hijos usando el celular con facilidad, la competencia creciendo. |
| Oye | Comentarios sobre el costo y la dificultad de algunos sistemas, y sobre la conveniencia de herramientas que funcionen con conectividad limitada. |
| Dice y hace | Puede preferir registrar las ventas en papel y hacer cálculos manuales. |
| Esfuerzos (pains) | Pierde tiempo contando mercadería, errores al calcular ganancias, mercadería que se vence sin darse cuenta. |
| Resultados (gains) | Quiere saber cuánto ganó al día sin complicaciones; necesita alertas de stock; desea un sistema que "le hable" en vez de obligarla a leer menús. |

| Necesidad de diseño | Reducir pasos, usar etiquetas comprensibles y ofrecer revisión antes de guardar una operación. |

**Arquetipo 2 — Comerciante en un entorno con ruido**

Perfil ilustrativo: persona que atiende clientes mientras realiza otras tareas, utiliza aplicaciones móviles comunes y puede trabajar en un ambiente donde el reconocimiento de voz no siempre es fiable. No representa a una persona entrevistada.

| Dimensión | Contenido |
| :--- | :--- |
| Piensa y siente | Quiere agilizar la atención y teme perder información cuando se interrumpe la conexión. |
| Ve | Ferreterías grandes usando computadoras, clientes pidiendo factura rápido, competidores con mejores precios. |
| Oye | Recomendaciones de digitalizar el negocio y dudas sobre la dependencia de Internet. |
| Dice y hace | Puede alternar entre el teléfono y un registro manual para pedidos o ventas fiadas. |
| Esfuerzos (pains) | Pierde ventas por no encontrar rápido el precio, clientes se van por la demora, no sabe su margen de ganancia real. |
| Resultados (gains) | Quiere registrar ventas con rapidez, consultar existencias y disponer de una alternativa manual si falla la voz o la conexión. |

| Necesidad de diseño | Hacer visible el estado de conexión, permitir corregir la transcripción y mantener disponible el ingreso manual. |

### 5.2 Mapa de sitio

El diseño Figma es la referencia visual. En el código Android revisado, la ruta inicial es Login; el formulario permite ir a Registro, y el alta continúa a Onboarding y luego al Dashboard. Desde la app se navega a productos, ventas, fiados, reportes, perfil, chat y sincronización. No existe una pantalla de bienvenida independiente. La presencia de rutas no demuestra que todos los recorridos estén validados de extremo a extremo.

### 5.3 Sistema de Diseño (Figma)
- **Paleta de referencia vigente:** fondo navy `#071725`, superficies oscuras `#111C2B` y `#17263A`, acento menta `#27D9A0`, azul `#2584E8` y texto claro. La paleta actualmente declarada en Android (`FondoOscuro #101418`, `SuperficieOscura #1B232C`, `VerdeNeon #8BE28B`) todavía difiere de esos valores; no afirmar paridad visual exacta.
- **Tipografía:** escala modular basada en Material Design 3; jerarquía H1/H2/H3/Body/Small configurada como Text Styles en Figma.
- **Componentes:** botón principal de micrófono con retroalimentación visual (ondas de audio), tarjetas de confirmación, inputs, modales, barra de navegación inferior — todos construidos con Auto Layout y Component Properties (variantes).
- **Estados obligatorios por componente clave:** Vacío, Carga, Error/Alerta, Éxito — deben estar diseñados explícitamente para el flujo de Registrar Venta y el flujo de Sincronización.
- **Tokens/Variables:** variables de Figma para espaciado y, si aplica, modo claro/oscuro.

La descripción anterior establece objetivos del diseño, no certifica que estén presentes en la app. El contraste WCAG AA, la cobertura de estados y la equivalencia pantalla por pantalla siguen pendientes de medición/documentación.

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


## Antecedente de cambio de stack — 30/09/2026

Esta nota conserva el contexto de la actualización del 30/09/2026: se pasó de la referencia inicial a Spring Boot a la línea Node.js/TypeScript/Express y PostgreSQL que aparece en el repositorio actual. La descripción vigente está en [Arquitectura actual](02_Arquitectura_SAD.md); las menciones anteriores a Spring se interpretan como antecedentes, no como componentes actuales. El requisito curricular de MongoDB/Mongoose se mantiene separado y pendiente de confirmación; PostgreSQL no lo sustituye.

## Estado de implementación observado — 13/11/2026

La tabla de requisitos anterior expresa la línea base académica. Esta tabla resume el estado del código de la revisión `03afcbd`; **implementado en código** no significa **validado funcionalmente**.

| Área | Estado observado en el repositorio | Límite / validación pendiente |
|---|---|---|
| Registro y acceso local | Formulario de perfil, PIN local y acceso por carnet, celular o correo; PIN derivado con PBKDF2-HMAC-SHA256. | No hay verificación SMS/OTP. Probar registro, login y política de bloqueo en dispositivo. |
| Cuenta remota | API de registro/login con contraseña scrypt y JWT, separada del PIN local; el registro puede continuar con perfil local si la nube falla. | Probar en dispositivo y comprobar la sincronización con el backend configurado. |
| Persistencia móvil | Room/SQLite con SQLCipher; esquema Room versión 6 y migraciones definidas hasta esa versión. | La migración de bases existentes requiere una prueba con datos de prueba y evidencia; no se afirma como validada en todos los dispositivos. |
| Voz / PLN | Vosk puede transcribir localmente si se dispone del modelo; extracción de entidades y clasificación están implementadas con reglas Kotlin. | No se encontró un clasificador TFLite activo. Falta medir latencia, precisión y reconocimiento acústico en dispositivo. |
| Ventas / fiados | Flujo manual de ventas, persistencia local, Outbox y consulta/cobro local de fiados presentes en el código. | Pruebas UI y de operación offline en dispositivo pendientes según el plan. |
| Asistente Yapay | Consulta el total vendido del día y propone productos con confirmación; es lógica local basada en reglas. | No es un LLM, no ofrece respuestas reales en quechua/aimara y no reemplaza el registro manual de ventas. |
| Backend | Node.js/TypeScript/Express con PostgreSQL; registro, catálogo protegido, recepción de lotes, factura simulada, QR y SSE. | Las pruebas de CI cubren backend; no acreditan integración móvil end-to-end ni despliegue productivo. |
| Interfaz / Figma | Figma vigente enlazado arriba; la app revisada inicia en login y usa colores propios declarados en Android. | La bienvenida independiente y la paridad visual completa no están demostradas. |

Consultar [04_Plan_de_Pruebas.md](04_Plan_de_Pruebas.md) y [05_Resultados_de_Pruebas.md](05_Resultados_de_Pruebas.md) para estados y evidencias. La decisión PostgreSQL frente al requisito académico MongoDB/Mongoose se justifica en [Informe_Segunda_Entrega_TI.md](Informe_Segunda_Entrega_TI.md); PostgreSQL no se presenta como equivalente a MongoDB.

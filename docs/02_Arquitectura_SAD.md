# Arquitectura actual del sistema

> **Corte de revisión:** 13/11/2026, código de `feature/update-doc` en la revisión `03afcbd`. Este resumen describe el repositorio local; no es una aprobación académica ni reemplaza el SAD entregado.

## Documentos de referencia

El documento académico existente [03_SAD_Arquitectura.md](03_SAD_Arquitectura.md) conserva la versión SAD 1.1. Su descripción tecnológica contiene decisiones históricas de Spring Boot que no corresponden al backend Node.js actual. No presentar esa versión como una descripción fiel del código actual ni modificarla silenciosamente. La actualización integral del SAD y su aprobación académica son trabajo separado.

## Componentes implementados

| Componente | Tecnología | Responsabilidad |
|---|---|---|
| Aplicación móvil | Kotlin, Android, Jetpack Compose | Login/registro, onboarding, perfil, inventario, ventas, fiados, reportes y chat. La ruta inicial actual es login; no hay una pantalla separada de bienvenida. |
| Voz y reglas locales | Vosk + Kotlin | Reconocimiento de voz sujeto al modelo disponible; extracción/clasificación con lógica basada en reglas. Yapay es demostrativo y no conecta con un LLM. |
| Persistencia móvil | Room/SQLite, versión 6, y SQLCipher | Perfil, productos, ventas, detalles, movimientos de caja y Outbox para operación local. Migraciones definidas hasta la versión 6. |
| API | Node.js 24, TypeScript, Express | Registro/login, perfil de negocio, CRUD remoto de productos, sincronización de lotes, facturas de prueba, QR y SSE. |
| Persistencia del servidor | PostgreSQL | Propietarios, negocios, productos, eventos/transacciones recibidas, facturas y QR. No equivale a un libro remoto completo de ventas/deudas. |
| Automatización backend | GitHub Actions | Migraciones, compilación TypeScript, chequeo de tipos y pruebas unitarias/de integración contra PostgreSQL temporal. |

## Flujos y límites

1. El login local busca un perfil por carnet, celular o correo y valida el PIN. El PIN se deriva con PBKDF2-HMAC-SHA256; el bloqueo local se aplica tras cinco intentos fallidos durante 60 segundos. El dictado depende del modelo Vosk. La autenticación remota es independiente: usa email/contraseña y entrega un JWT.
2. El registro guarda el perfil y el hash del PIN en Room antes de intentar crear la cuenta remota. La contraseña de nube se envía al API y no se conserva en Room; el backend la almacena como hash scrypt. Si la persona continúa sin nube, debe vincular la cuenta después desde Sincronización. El onboarding guarda nombre/rubro localmente e intenta actualizar el negocio remoto cuando hay token y conexión.
3. Las ventas actualizan la persistencia local y el stock; el evento Outbox permite reintentar el envío después. El alta de venta, stock y Outbox debe tratarse como una operación local; las pruebas funcionales en dispositivo siguen pendientes.
4. WorkManager envía el Outbox al backend con JWT. El API limita los lotes a 100 operaciones y usa `operationId` para reconocer reintentos; la política de conflictos no debe describirse como una sobrescritura LWW de ventas.
5. El cliente descarga productos de forma paginada; en este MVP no sube al catálogo los productos creados solo localmente y conserva el stock local durante la actualización de nombre/precio.
6. La voz puede sugerir producto/cantidad y abrir el formulario manual. La persona revisa y guarda la venta; el reconocimiento en un dispositivo depende del modelo y requiere prueba independiente.
7. Yapay consulta el total local del día y propone productos con confirmación. Sus reglas, consultas y consejos no son inferencias de un LLM; el modelo local no guarda costos de compra.
8. La API emite eventos SSE autenticados por negocio, pero su bus está en memoria por proceso; no garantiza replay ni coordinación entre réplicas.
9. Facturación y QR son endpoints backend: el PDF es simulado/no fiscal y no existe una interfaz Android end-to-end verificada para esos endpoints.

## Contratos de API

El backend actual documenta rutas, autenticación, paginación, sincronización y límites de facturación/QR/SSE en [README del backend](../backend/README.md). La configuración y límites del cliente móvil están en [README móvil](../mobile/README.md). Los precios y márgenes de las nuevas plantillas son orientativos: no se encontró el documento comercial fuente en el repositorio.

## Verificación

El workflow [backend.yml](../.github/workflows/backend.yml) publica un resumen verificable de cada ejecución. La ejecución de `main` sobre la revisión `03afcbd` completó los pasos del backend; el enlace y su alcance están en [CI/CD](07_CI_CD_Report.md). La compilación Android es una comprobación técnica y no sustituye pruebas funcionales, migraciones de una base real ni pruebas teléfono–API documentadas en dispositivo.

## Límites de la línea base

- La API y Docker Compose son para desarrollo local; la base se publica solo en loopback. La conexión desde un teléfono físico no queda habilitada por defecto.
- El cliente móvil usa `10.0.2.2` por defecto, válido para emulador Android y no para un teléfono físico.
- No se declara un frontend React/web, una base MongoDB/Mongoose, un despliegue productivo, TLS verificado ni una integración fiscal.
- El Figma vigente es una referencia de diseño; no todas sus pantallas o interacciones se han validado como equivalentes en la app Android.
- Para contrastar requisitos, casos y evidencias, consultar la [especificación](02_Requisitos_y_UX.md), el [plan](04_Plan_de_Pruebas.md) y la [bitácora](05_Resultados_de_Pruebas.md).

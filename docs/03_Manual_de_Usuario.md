# Manual de usuario

> Instrucciones para la versión fuente revisada en la rama `feature/update-doc`. Los textos y las rutas pueden variar si se instala otro APK. La aplicación comienza en Login; la pantalla de bienvenida independiente del Figma no forma parte del flujo Android revisado.

## 1. Crear un perfil

1. Abre la aplicación y pulsa **Crear cuenta nueva** desde Login.
2. Completa los datos personales solicitados, el correo, celular, contraseña de nube y PIN local de cuatro dígitos.
3. Pulsa **Crear cuenta y continuar**. Si hay conexión, la API registra la cuenta remota; el perfil y el hash del PIN se guardan localmente.
4. Si el servidor no está disponible, revisa el mensaje. Puedes elegir **Continuar solo con perfil local** si la app lo habilita. La contraseña de nube no se guarda para reintentar después: tendrás que volver a introducirla al vincular la cuenta.
5. Si el correo ya está registrado, no vuelvas a crear una cuenta. Usa la opción de vincular cuenta desde Sincronización.

Usa únicamente datos ficticios durante las pruebas. No captures ni compartas PIN, contraseñas, números reales o información de comerciantes.

## 2. Iniciar sesión

1. Introduce el carnet, celular o correo del perfil local.
2. Escribe el PIN de cuatro dígitos con el teclado. Si Vosk está disponible, puedes usar el control de voz y decir los cuatro números claramente.
3. Si el acceso falla, verifica el identificador y el PIN. El bloqueo local se activa tras cinco intentos fallidos por 60 segundos.

El PIN sirve para el acceso local; no inicia sesión en el backend. La app tiene un acceso remoto separado para usar una cuenta API.

## 3. Configurar el negocio

1. Después del registro, completa el onboarding.
2. Escribe el nombre del negocio, elige el rubro y revisa la plantilla sugerida antes de aplicarla.
3. Los productos de plantilla se guardan localmente. Son ejemplos editables, no una lista de precios confirmada.
4. Con una sesión API válida, el nombre y el rubro también se envían al negocio asociado al token. Sin conexión, el perfil local sigue siendo la referencia en el dispositivo.

## 4. Consultar productos, ventas y reportes

- Usa Inventario para revisar el catálogo local y las existencias. Los productos creados solo en el teléfono no se cargan automáticamente al catálogo del servidor.
- Usa Ventas para registrar una operación manual. Confirma los productos, las cantidades, los importes y si la venta es al contado o fiada antes de guardar.
- Usa Fiados para consultar las deudas locales. Revisa el cliente y el total antes de confirmar un cobro.
- Usa Reportes para revisar los totales locales disponibles. Los valores de demostración, si se habilitan en una compilación debug, no son ventas reales.

Las ventas y sus cambios locales quedan en el dispositivo; los eventos pendientes se envían cuando se cumplen las condiciones de sincronización. No registres datos reales para probar.

## 5. Usar voz y Yapay

1. Concede el permiso de micrófono si Android lo solicita.
2. En el chat, espera a que se indique que el modelo de voz está disponible y habla en español.
3. Revisa la transcripción y la sugerencia. Para una venta, el formulario manual es quien permite revisar y guardar.
4. Yapay puede consultar el total vendido localmente durante el día y preparar un producto nuevo. Confirma explícitamente el alta o cancélala.

Vosk requiere el modelo local `vosk-model-small-es-0.42`. Si no está disponible o el dictado falla, utiliza el teclado. Yapay es una demostración basada en reglas locales, no un modelo generativo, asesor profesional ni sistema conectado a quechua/aimara.

## 6. Vincular y sincronizar con el servidor

1. Asegúrate de que la API y PostgreSQL estén iniciados.
2. Desde Sincronización, inicia sesión con correo y contraseña de nube. Este paso es independiente del PIN local.
3. Revisa la cantidad y el estado de operaciones pendientes.
4. WorkManager intenta enviar el Outbox con el JWT y descargar el catálogo cuando hay conectividad.

El emulador Android puede usar la URL predeterminada `http://10.0.2.2:3000/`. Esa dirección no funciona desde un teléfono físico. Docker Compose y la API de desarrollo están enlazados a loopback por defecto; no expongas servicios a una red pública. Para una prueba con teléfono físico hace falta configurar una red local confiable, el host/firewall y la URL del cliente, y documentar esa prueba por separado.

La sincronización actual descarga el catálogo remoto; no sube productos creados únicamente en el móvil. Una respuesta 401 requiere volver a vincular la cuenta; no confundas ese estado con el PIN.

## 7. Si algo no funciona

| Síntoma | Qué revisar |
|---|---|
| No acepta el PIN | Revisa que el identificador corresponda al perfil local y espera a que termine el bloqueo temporal si hubo intentos fallidos. |
| No aparece el control de voz o no reconoce | Revisa el permiso de micrófono y la instalación del modelo Vosk; usa entrada manual mientras tanto. |
| El backend no responde | Comprueba PostgreSQL, API, `MYPES_API_BASE_URL` y, en teléfono físico, la configuración de red. |
| Hay operaciones pendientes | Mantén los datos locales; vuelve a autenticar la cuenta API y repite la prueba con datos ficticios. |

Para reportar un problema, anota fecha, commit/versión, dispositivo, entorno, pasos, resultado esperado y observado. Añade evidencia anonimizada y actualiza el [plan de pruebas](04_Plan_de_Pruebas.md) y la [bitácora](05_Resultados_de_Pruebas.md). Esta guía no demuestra por sí misma que los flujos se hayan ejecutado.

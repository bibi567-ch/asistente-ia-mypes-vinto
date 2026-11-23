# Evidencias

Guardar aquí registros revisables y anonimizados de pruebas. Relacionar cada evidencia con un caso del [plan de pruebas](../docs/04_Plan_de_Pruebas.md) y registrar su resultado en la [bitácora](../docs/05_Resultados_de_Pruebas.md). La cobertura académica debe contrastarse además con las rúbricas originales; este repositorio no mantiene una matriz vigente de cumplimiento de las tres materias.

## Qué registrar

Cada evidencia debe indicar:

- ID/criterio del sílabo (ARQ-643, TEC-644 o INV-645) y requisito relacionado.
- Fecha, responsable, hash corto del commit y versión de APK/API.
- Equipo, sistema operativo, red y configuración necesarios para repetir la prueba.
- Pasos numerados, resultado esperado y resultado observado.
- Estado: aprobado, fallido o inconcluso; límites conocidos y siguiente acción.
- Capturas anonimizadas, respuesta de API o reporte pertinente.

## Nombre sugerido

Usar una ruta descriptiva, por ejemplo:

`docs/evidencias/08_backend/EV-API-01-healthcheck.md`

Para capturas, conservar el mismo ID, por ejemplo `docs/evidencias/05_inventario/EV-MOB-02-inventario.png`. Registrar el procedimiento, el resultado y el alcance en un archivo Markdown asociado.

## Privacidad y verificación

No guardar secretos, tokens, contraseñas, números de teléfono, datos reales de comerciantes ni grabaciones identificables. Usar datos ficticios.

Una compilación, una captura aislada, un diagrama o la presencia de código no bastan para declarar una función verificada. Las métricas de rendimiento, reconocimiento de voz, usabilidad, seguridad y sincronización requieren procedimiento repetible, entorno y resultado. No afirmar despliegue HTTPS/TLS, auditoría o integración física hasta adjuntar evidencia directa.

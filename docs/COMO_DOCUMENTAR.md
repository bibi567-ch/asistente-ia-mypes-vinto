# Cómo documentar cambios y pruebas

Este procedimiento es obligatorio para mantener la documentación alineada con el código.

## Para un cambio de código

1. Identificar el componente afectado y el requisito relacionado.
2. Actualizar la documentación directamente relacionada:
   - Arquitectura y contratos: [03_SAD_Arquitectura.md](03_SAD_Arquitectura.md).
   - Plan de pruebas: [04_Plan_de_Pruebas.md](04_Plan_de_Pruebas.md).
   - Resultado observado: [05_Resultados_de_Pruebas.md](05_Resultados_de_Pruebas.md).
3. Registrar solo comandos realmente ejecutados.
4. Escribir fecha, entorno, alcance y resultado exacto.
5. Si algo falla o no se pudo ejecutar, marcarlo como `BLOQUEADO`, `FALLIDO` o `PENDIENTE`.

## Para una evidencia

Guardar evidencias anonimizadas en `docs/evidencias/`. Cada evidencia debe indicar:

- ID del escenario o requisito.
- Fecha y commit.
- Dispositivo, sistema operativo, versión de APK/API y red.
- Precondiciones y pasos reproducibles.
- Resultado esperado y observado.
- Estado y limitaciones.
- Enlaces a código, logs o reporte.

Una compilación no demuestra funcionamiento en dispositivo. Una prueba unitaria no demuestra integración PostgreSQL. No marcar pruebas funcionales Android como aprobadas sin captura o video real.

## Privacidad

No guardar tokens, contraseñas, claves, números de teléfono, datos reales de comerciantes ni audio identificable. Usar cuentas y datos ficticios.

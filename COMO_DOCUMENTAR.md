# Guía de documentación y evidencias

## Regla de integridad

No se declara aprobada una prueba funcional de Android sin una captura o video verificable guardado en `docs/evidencias/`. No se inventan ejecuciones, fechas, resultados, dispositivos ni evidencias.

Las pruebas automatizadas del backend son distintas: se consideran verificadas para una ejecución concreta cuando el workflow de GitHub Actions termina correctamente. El workflow publica un resumen con el commit, los pasos ejecutados y el enlace a esa ejecución. Una ejecución local no se presenta como una ejecución de GitHub Actions.

## Flujo al terminar una tarea

1. Ejecutar las comprobaciones pertinentes y conservar sus resultados exactos.
2. Para flujos de Android, probar en un dispositivo o emulador y tomar una captura anonimizada; no incluir credenciales, tokens ni datos reales.
3. Guardar la evidencia en la carpeta del módulo dentro de `docs/evidencias/`, usando un nombre descriptivo, por ejemplo `04_ventas/venta_confirmada.png`.
4. Actualizar `docs/04_Plan_de_Pruebas.md` y añadir una entrada fechada a `docs/05_Resultados_de_Pruebas.md`.
5. Incluir en la entrada el comando o escenario, resultado, alcance y enlace relativo a la evidencia o ejecución de CI. Si falta evidencia, dejar el estado pendiente.
6. Revisar los cambios y crear un commit cuyo mensaje indique el trabajo y las pruebas realmente realizadas, por ejemplo: `test: verificar sincronizacion offline`.

Copilot debe actualizar la documentación junto con los cambios de código. Las instrucciones compartidas están en `.github/copilot-instructions.md`.

## Evidencia por tipo de prueba

- **Backend:** `.github/workflows/backend.yml` ejecuta migraciones, build, chequeo de tipos, pruebas unitarias y de integración con un PostgreSQL efímero. El resumen de cada ejecución aparece en GitHub Actions. Enlazar la ejecución real en `docs/07_CI_CD_Report.md`; no usar filas ficticias ni marcar una ejecución pendiente como aprobada.
- **Android:** la compilación demuestra que el APK se construye, no que un flujo funcione en el teléfono. Una prueba funcional aprobada requiere el escenario realizado y su captura/video en `docs/evidencias/`.
- **Código y documentación:** el diff y los comandos reproducibles pueden registrarse como comprobaciones técnicas, separados de las pruebas funcionales con usuario.

## Carpetas de evidencias

| Carpeta | Módulo |
|---|---|
| `docs/evidencias/01_login/` | Inicio de sesión local |
| `docs/evidencias/02_onboarding/` | Configuración inicial |
| `docs/evidencias/03_dashboard/` | Panel principal y reportes |
| `docs/evidencias/04_ventas/` | Registro y confirmación de ventas |
| `docs/evidencias/05_inventario/` | Catálogo y existencias |
| `docs/evidencias/06_chat_ia/` | Reconocimiento y asistencia por voz |
| `docs/evidencias/07_sincronizacion/` | Outbox y sincronización |
| `docs/evidencias/08_backend/` | API y ejecuciones automatizadas |

## Prompt para documentar una tarea

> Actualiza la documentación del módulo que acabo de cambiar. Cambios realizados: [descripción]. Pruebas ejecutadas: [comandos/escenarios y resultados exactos]. Evidencia: [ruta relativa, enlace al workflow o “ninguna”]. Actualiza `docs/04_Plan_de_Pruebas.md` y agrega una entrada a `docs/05_Resultados_de_Pruebas.md`. No marques una prueba funcional como aprobada sin captura/video en `docs/evidencias/`. Para backend, vincula el workflow real de GitHub Actions y diferencia los resultados locales de CI. No inventes resultados ni fechas.

## Convención de imágenes

Usar nombres breves, descriptivos y sin información personal, por ejemplo:

```markdown
![Venta confirmada con datos de prueba](evidencias/04_ventas/venta_confirmada.png)
```

Los enlaces en documentos ubicados en `docs/` son relativos a esa carpeta.

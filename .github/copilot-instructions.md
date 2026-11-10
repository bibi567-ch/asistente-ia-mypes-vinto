# Instrucciones compartidas del proyecto

- Mantén la documentación operativa alineada con cada cambio de código. Consulta `COMO_DOCUMENTAR.md` y los documentos pertinentes de `docs/`.
- Actualiza `docs/02_Arquitectura_SAD.md` cuando cambien componentes, contratos o decisiones arquitectónicas; actualiza `docs/04_Plan_de_Pruebas.md` y `docs/05_Resultados_de_Pruebas.md` cuando cambien o se ejecuten pruebas.
- Registra solo comandos y escenarios que realmente se ejecutaron, con su resultado, fecha disponible y alcance. No presentes una compilación como prueba funcional.
- No marques pruebas funcionales de Android como aprobadas sin confirmación del usuario y una captura/video existente en `docs/evidencias/`.
- Para pruebas automatizadas del backend, registra el resultado observado y enlaza la ejecución real de GitHub Actions cuando exista. No afirmes que CI pasó basándote únicamente en una ejecución local.
- Si una comprobación no se pudo ejecutar o falló, documenta el bloqueo y deja su estado pendiente/fallido; no lo ocultes ni lo conviertas en éxito.
- No registres credenciales, tokens, datos reales de comerciantes ni información sensible en documentos o capturas.

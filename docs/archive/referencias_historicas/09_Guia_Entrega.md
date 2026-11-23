# 09. Guía de revisión y entrega

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Lista de preparación; validar contra las consignas oficiales
**Actualizado:** 23/11/2026

## 1. Orden de consulta

1. Abrir el único README del repositorio, [`README.md`](../README.md), como punto de entrada.
2. Consultar el [índice documental](../00_Indice.md) para localizar la fuente vigente de cada tema.
3. Revisar requisitos y alcance antes de modificar comportamiento: documentos 01 y 02.
4. Verificar arquitectura, stack y contrato API: documentos 03, 04 y 11.
5. Revisar backlog, trazabilidad y rúbricas: documentos 05, 06, 07 y 12.
6. Ejecutar el protocolo y registrar evidencia: documentos 10, 13 y 14.
7. Seguir las instrucciones de configuración local en el documento 15.

## 2. Criterios de presentación

- Distinguir claramente lo **propuesto**, **implementado**, **probado** y **validado en campo**.
- No afirmar compilación Android, ejecución Docker, proveedor cloud real o métricas de campo sin evidencia verificable.
- Presentar la documentación vigente; el contenido de `docs/archive/` es antecedente y no fuente de requisitos actuales.
- Alinear demo, diagramas, requisitos y código con el alcance conversacional: ventas, inventario y fiados están excluidos.
- Confirmar con cada docente los requisitos de materia pendientes, en especial MongoDB/Mongoose y producto web.
- Adjuntar versión, entorno, pasos y resultado para cada prueba mostrada.

## 3. Lista antes de compartir

- [ ] Revisar enlaces del índice y confirmar que solo existe un archivo `README.md`.
- [ ] Ejecutar las pruebas aplicables y registrar resultados con el protocolo establecido.
- [ ] Revisar diagramas y comprobar que coincidan con el backend de voz y la app actual.
- [ ] Verificar que `.env`, claves, tokens, contraseñas, datos personales y audio identificable no estén en el cambio.
- [ ] Solicitar revisión cruzada del PR y resolver comentarios antes de integrar.
- [ ] Identificar explícitamente los bloqueos técnicos y las confirmaciones docentes pendientes.

## 4. Evidencia académica

Las consignas oficiales y formatos solicitados por los docentes prevalecen sobre esta guía. La documentación del repositorio organiza la evidencia técnica, pero no garantiza por sí sola el cumplimiento de rúbricas ni sustituye entregas PDF, exposiciones o artefactos requeridos por cada asignatura.

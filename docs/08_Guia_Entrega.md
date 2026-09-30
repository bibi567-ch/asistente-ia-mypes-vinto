# Guía de entrega del repositorio y del prototipo

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto  
**Corte:** 30/09/2026

Esta guía prepara el repositorio para que el equipo trabaje y presente evidencia técnica ordenada. Los formatos finales que cada docente solicite (por ejemplo PDF o exposición) se entregan desde sus herramientas académicas; este repositorio mantiene las fuentes, el código, diagramas y evidencia reproducible.

## 1. Qué debe encontrar el equipo

- `README.md`: resumen del proyecto, estado real, stack objetivo, inicio local y enlaces al material canónico.
- `00_Indice.md` y `docs/01`–`docs/11`: alcance, requisitos, SAD, stack, backlog, trazabilidad, evaluación, calendario, evidencias y plan de implementación.
- `backend/`: scaffold inicial de API Node.js/TypeScript y esquema PostgreSQL.
- `mobile/`: instrucciones iniciales; aún no contiene una aplicación Android implementada.
- `diagrams/`: fuentes Mermaid/PlantUML; los diagramas deben renderizarse para la presentación.
- `evidence/`: resultados y capturas anonimizadas, con fecha y procedimiento.

## 2. Revisión antes de compartir

- [ ] Leer primero `README.md` y `docs/07_Cronograma_y_Riesgos.md`.
- [ ] Confirmar con el docente las observaciones de `docs/11_Matriz_Rubricas_Tres_Materias.md`, en especial MongoDB/Mongoose y frontend web para TI.
- [ ] No afirmar que el SAD v1.1 histórico ya fue sustituido/aprobado; revisar el addendum de arquitectura.
- [ ] Distinguir componentes propuestos, scaffolds, implementaciones y pruebas verificadas.
- [ ] Confirmar que el diagrama C1/C2 corresponde a Node.js + PostgreSQL y mostrar límites actuales.
- [ ] Ejecutar el backend y revisar su conexión a PostgreSQL antes de usarlo en demo.
- [ ] Nunca publicar credenciales, `.env`, datos reales de comerciantes o grabaciones identificables.
- [ ] Revisar enlaces Markdown y que la documentación apunte a nombres actuales.

## 3. Evidencia técnica que se debe añadir

Para cada resultado, guardar un registro en `evidence/` con ID, fecha, commit, entorno, pasos y conclusión. Ejemplos: respuesta del health check y consulta de base local; captura de una pantalla Figma; revisión del diagrama; prueba de API; matriz de dispositivo; resultados SUS anonimizados. No guardar solo una captura sin explicar qué acredita.

## 4. Presentación por materia

Preparar la evidencia académica que pida cada sílabo: perfil y defensa para Investigación I; SAD, decisiones y atributos de calidad para Arquitectura; y el producto web, backend, NoSQL, seguridad y despliegue que Tecnologías en Internet solicite. La arquitectura compartida ayuda a reutilizar trabajo, pero no garantiza que un entregable de una materia satisfaga automáticamente otra. Registrar aclaraciones y cambios en `docs/11_Matriz_Rubricas_Tres_Materias.md` y `docs/04_Decisiones_Stack.md`.

## 5. Publicación en GitHub

Trabajar en una rama corta y abrir pull request. El PR debe explicar el cambio, requisitos y rúbricas relacionados, pasos de verificación, capturas/enlaces de evidencia y pendientes conocidos. Solicitar revisión a otra integrante; integrar a `main` solo después de resolver comentarios. La rama de trabajo actual no equivale a aprobación docente ni a despliegue productivo.

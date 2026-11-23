# 10. Registro de evidencias

**Proyecto:** Asistente Conversacional Offline para MYPES de Vinto
**Estado:** Registro preparado; las evidencias se agregan al ejecutar cada validación
**Actualizado:** 23/11/2026

## 1. Inventario

| Código | Evidencia | Ubicación prevista | Estado |
|---|---|---|---|
| EV-01 | Repositorio y documentación | GitHub, `README.md`, `00_Indice.md` | Disponible |
| EV-02 | Prototipo y sistema de diseño | Enlaces Figma en `README.md` | Pendiente de revisión |
| EV-03 | Requisitos y experiencia de usuario | `docs/02_Requisitos_y_UX.md` | Documentado |
| EV-04 | Arquitectura y diagramas C1/C2 | `docs/03_SAD_Arquitectura.md`, `diagrams/` | Fuentes disponibles; render/revisión pendiente |
| EV-05 | Contrato y pruebas de backend | `docs/11_Plan_Implementacion_y_API.md`, backend tests | Pruebas automatizadas disponibles |
| EV-06 | Build y pruebas Android | `mobile/`, evidencia de CI/dispositivo | Pendiente: Gradle Wrapper y build reproducible |
| EV-07 | Protocolo de QA | `docs/13_Plan_de_Pruebas.md` | Documentado |
| EV-08 | Resultados técnicos y de campo | `docs/14_Resultados_de_Pruebas.md` | Registro inicial; campo pendiente |
| EV-09 | Capturas, logs y mediciones | `evidence/` | Carpeta preparada; agregar por prueba |

## 2. Formato de cada evidencia

Incluir como mínimo:

- identificador de evidencia y fecha;
- commit o versión del artefacto;
- entorno, dispositivo y versión de sistema cuando corresponda;
- pasos de reproducción y resultado observado;
- requisito que acredita y responsable de la prueba;
- límites, errores o elementos aún no verificados.

## 3. Protección de datos

Guardar solo capturas, logs y mediciones necesarios, anonimizados. No incluir claves, tokens, contraseñas, números telefónicos reales, nombres de comerciantes ni grabaciones identificables. Obtener consentimiento para cualquier audio de evaluación y aplicar el protocolo aprobado para resguardo y eliminación.

Una captura aislada no demuestra una prueba reproducible. Las métricas se consideran verificadas solo si se documentan el método, la muestra, los datos anonimizados y el cálculo.

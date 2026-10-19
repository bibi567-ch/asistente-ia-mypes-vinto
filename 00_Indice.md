# Índice del proyecto

El proyecto es un prototipo de asistente conversacional offline para registrar ventas y consultar el saldo local de existencias en MYPES del centro de Vinto. El alcance vigente y las exclusiones están en [Proyecto y alcance](docs/01_Proyecto_y_Alcance.md).

**Equipo:** Eva Chino Quispe, Christian Gonzales Mamani, Marializ Mamani Quispe, Kevin Rocha y Luis Lazo.

## Estado documental

La revisión de la rama main del 16/10/2026 encontró código fuente Android, backend y documentación con distintos niveles de avance. La app instaló y guardó un producto de prueba según la demostración reportada por la estudiante, pero falta archivar esa prueba. La sincronización de la app con la API en un teléfono físico y la eliminación de productos no están verificadas o implementadas en la interfaz revisada.

| Documento | Contenido |
|---|---|
| [Matriz de cumplimiento 2026-2](docs/13_Matriz_Cumplimiento_Tres_Materias_2026-2.md) | Cotejo de los sílabos ARQ-643, TEC-644 e INV-645, brechas y evidencia requerida |
| [Mapa de presentación](docs/MAPA_PRESENTACION.md) | Relación del trabajo con Investigación I, Arquitectura y Tecnologías en Internet |
| [Perfil](docs/perfil/Perfil_final.md) | Perfil de Trabajo de Grado, versión 11 |
| [Cronograma](docs/Cronograma_final.md) | Separación entre las entregas del semestre y el Trabajo de Grado |
| [Nota de nombre](docs/Nota_de_nombre.md) | Nombre y alcance vigentes |
| [01 Alcance](docs/01_Proyecto_y_Alcance.md) | Qué se construye y qué queda fuera |
| [02 Requisitos y UX](docs/02_Requisitos_y_UX.md) | Requisitos y experiencia de uso |
| [03 Arquitectura](docs/03_SAD_Arquitectura.md) | SAD v1.1 entregado; no es una descripción aprobada de toda la arquitectura actual |
| [Backend](backend/README.md) | API Node.js, TypeScript, Express y PostgreSQL |
| [Android](mobile/README.md) | App Kotlin/Compose, persistencia local y estado de sincronización |
| [Diagramas](diagrams/README.md) | Fuentes y estado de revisión de los diagramas |
| [Evidencias](evidence/README.md) | Formato para pruebas y resultados verificables |

## Código

- backend/: API local con Node.js, TypeScript, Express y PostgreSQL. Ver backend/README.md para funciones disponibles y límites.
- mobile/: aplicación Android Kotlin/Compose, Room y componentes de dominio/sincronización. La presencia de clases no demuestra que todos los recorridos estén conectados.
- diagrams/: fuentes editables de arquitectura y flujos.

El código fuente no equivale a un APK publicado ni a una integración móvil–API validada. No se debe afirmar que existe una función si no hay código y evidencia correspondiente.

## Versiones históricas

docs/archive/ conserva borradores y artefactos previos. Consultarlos como antecedentes, no como estado actual, salvo que una entrega solicite expresamente esa versión.

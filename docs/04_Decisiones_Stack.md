# Decisión de stack del proyecto unificado

**Estado:** Node.js y PostgreSQL confirmados como obligatorios por el docente; falta archivar la referencia de asignatura/docente y actualizar/aprobar la documentación académica.
**Fecha:** 30/09/2026.

## Decisión

El backend común del proyecto se implementa con **Node.js + TypeScript + Express y PostgreSQL**. La base PostgreSQL es remota y autoritativa para usuarios, catálogo, ventas, detalles, movimientos e idempotencia. Android usa Room/SQLite local para el funcionamiento offline. Node/Express y PostgreSQL son componentes complementarios: framework de servidor y motor de persistencia relacional.

Esta selección responde a la instrucción de unificar el proyecto entre Investigación I, Arquitectura y Tecnologías en Internet, al tiempo que mantiene PostgreSQL del Perfil/SAD para transacciones relacionales. Aunque Node.js y PostgreSQL fueron confirmados como obligatorios, falta documentar qué docente/asignatura lo confirmó y obtener la aprobación específica de Arquitectura para reflejar la transición en la documentación evaluada. El SAD v1.1 y el Perfil entregado dicen Spring Boot/PostgreSQL, así que se crea una ADR sucesora que sustituye Spring, conserva PostgreSQL y registra trade-offs. No editar la entrega evaluada como si hubiese nacido con Node.

## Comparación

| Criterio | Node/Express + PostgreSQL | Spring Boot + PostgreSQL | Node/Express + MongoDB |
|---|---|---|---|
| Alineación con TI | Node/Express directo; PostgreSQL no prueba NoSQL | No demuestra Node ni Mongo/Mongoose | Node/Express y Mongo/Mongoose directos |
| Coherencia del dominio | Relaciones y transacciones para ventas/movimientos | Adecuada | Documentos posibles, relaciones y consistencia requieren diseño explícito |
| Compatibilidad técnica | Node se conecta a PostgreSQL mediante cliente/driver; patrón común | Integración Java/JPA consolidada | Integración Node/Mongoose |
| Alineación con entregas existentes | Conserva PostgreSQL; sustituye backend Spring | Igual al SAD actual | Cambia tanto backend como DB |
| Complejidad del cambio | Media: migrar framework y API, conservar modelo DB | Menor respecto al SAD entregado | Mayor: framework y persistencia |
| Resolución recomendada | Base de trabajo unificada solicitada | Alternativa si Arquitectura exige mantener ADR | Evidencia académica de NoSQL solo si TI la requiere |

## MongoDB en Tecnologías en Internet

El sílabo proporcionado incluye MongoDB y Mongoose explícitamente. PostgreSQL no se presenta como sustituto NoSQL. Eva consultará al docente si el resultado de aprendizaje exige que la base del producto final sea MongoDB, si se admite una demostración académica delimitada separada o si el proyecto puede mantener PostgreSQL con justificación arquitectónica. Hasta recibir respuesta, MongoDB no se añade a la fuente autoritativa ni se duplica catálogo/ventas en dos motores. Si se autoriza módulo separado, definir alcance, colecciones, prueba y criterio exacto que demuestra.

## Consecuencias y trabajo de migración

1. Crear ADR sucesora: contexto, decisión, alternativas, aprobación, consecuencias y fecha.
2. Cambiar implementación server de Spring/Java a Node/TypeScript/Express.
3. Conservar PostgreSQL, modelo relacional, integridad transaccional e idempotencia tras revisión.
4. Actualizar C2, despliegue, secuencias, API, seguridad, estructura, cronograma, README e instrucciones.
5. Revisar dependencias: sustituir JPA/Hibernate/Spring Security/Flyway solo por bibliotecas que realmente se adopten.
6. Validar si TI requiere una interfaz web aparte del Android nativo.
7. No afirmar implementación, cifrado, despliegue ni cumplimiento Mongo sin evidencia.

## Reglas del dominio y persistencia

SQLite guarda primero operaciones locales; una transacción añade la venta y el evento Outbox. Cada evento usa UUID estable. PostgreSQL impone unicidad por negocio e identificador de operación. Ventas son hechos inmutables y no se resuelven descartando una de dos mediante Last Write Wins. Los conflictos de catálogo pueden tener otra política; los de stock quedan visibles y documentados.

## Aprobación

| Consulta | Estado | Responsable | Resultado que se archiva |
|---|---|---|---|
| Arquitectura aprueba Node/PostgreSQL | Pendiente | Eva | Mensaje/acta del docente |
| TI exige MongoDB dentro del producto | Pendiente | Eva | Mensaje/acta y criterio de rúbrica |
| TI requiere cliente web | Pendiente | Eva | Consigna oficial/respuesta |

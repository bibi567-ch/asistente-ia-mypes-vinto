# Nota de nombre y de alcance

Fecha: 13/10/2026.

El SAD v1.1 y varios requisitos usaron el nombre “gestión de ventas e inventario”. Ese nombre queda retirado de los documentos vigentes.

El sistema se llama como el perfil de Investigación I: prototipo de asistente conversacional offline para el registro de ventas y la consulta del saldo local de existencias, configurable por tipo de negocio.

Consecuencias para Arquitectura de Software y para Tecnologías en Internet:

- El caso de uso “Consultar inventario” pasa a llamarse “Consultar el saldo”. El renombre de los diagramas entra con el SAD v2, después del examen del 19/10. La v1.1 no se reescribe.
- El requisito de compras a proveedores (RF-014) queda fuera. No se diseña y no se codifica.
- Descontar la cantidad vendida es parte de registrar la venta. No abre un módulo de almacén.
- El backend vigente es Node.js, TypeScript, Express y PostgreSQL. Spring Boot pertenece solo al SAD v1.1.
- PostgreSQL no es una base NoSQL. Si la materia pide MongoDB, el ejercicio se entrega aparte y no guarda las ventas del prototipo.

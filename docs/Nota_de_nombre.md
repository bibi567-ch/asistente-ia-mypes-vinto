# Nota de nombre

El documento de arquitectura v1.1 usó el nombre “gestión de ventas e inventario”. Ese nombre no describe esta versión.

El nombre vigente es el del perfil: prototipo de asistente conversacional offline para el registro de ventas y la consulta del saldo local de existencias, configurable por tipo de negocio.

- Consultar el saldo no es un módulo de almacén.
- Las compras a proveedores quedan fuera del alcance.
- Descontar lo vendido forma parte de registrar la venta.
- El backend de esta versión es Node.js, TypeScript, Express y PostgreSQL. Spring Boot queda en el SAD v1.1.
- PostgreSQL no es una base NoSQL.

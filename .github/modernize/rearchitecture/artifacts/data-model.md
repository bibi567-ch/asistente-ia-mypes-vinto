# Data Model

## Mobile Room model

### `Producto`

Source: `mobile/app/src/main/java/com/mypes/vinto/data/Producto.kt:5`.

- Table: `productos`.
- `id: String` primary key.
- `nombre: String`.
- `precio: Double`.
- `stock: Double`.
- `actualizadoEn: Long`, defaulting to current epoch milliseconds.

### `Usuario`

Source: `mobile/app/src/main/java/com/mypes/vinto/data/Usuario.kt:5`.

- Table: `usuarios`.
- `id: Long` primary key, default `1` (single local account).
- `nombre: String`.
- `pinHash: String`.
- `salt: String`.
- `iteraciones: Int`.
- `intentosFallidos: Int`, default `0`.
- `bloqueadoHasta: Long`, default `0`.

`MypesDatabase` registers both entities at version 1 and exposes `ProductoDao` and `UsuarioDao` (`mobile/app/src/main/java/com/mypes/vinto/data/MypesDatabase.kt:6`).

## Backend PostgreSQL model

- `businesses`: business/tenant root created by authentication migration.
- `users`: user identity, `business_id`, normalized email, and password hash; scoped to a business.
- `products`: product catalog with `business_id`, name, unit, sale price, and created/updated timestamps.
- Unique product identity is `(business_id, name)`; product queries are tenant-scoped.
- Authentication queries use `users.id` and `users.business_id`; product queries use `products.business_id`.

## Relationships

- One business has many users.
- One business has many products.
- A user belongs to one business.
- A product belongs to one business.
- Mobile Room entities are local and are not statically connected to the backend PostgreSQL entities.

## Transaction boundaries

The analyzed source does not declare an explicit multi-statement transaction. Mobile template application inserts products sequentially through `PlantillaRepository`; backend login, password change, and product writes each use individual PostgreSQL statements.


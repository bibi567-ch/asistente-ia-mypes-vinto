# Architecture Index

This index is not the full contract. Do not implement from this file alone; follow the artifact paths below.

## Implementation Guide

### Global artifacts

- `unit_graph.yaml`: all units, source anchors, dependencies, dynamic entrypoints, and shared-reference whitelists. Filter by the assigned `name`.
- `wire_contracts.yaml`: outward HTTP contracts. Read rows whose `unit` matches the assigned unit.
- `shared_modules.yaml`: shared code and field/method truth. Read rows whose `used_by_units` includes the assigned unit.
- `cross_unit_state.yaml`: implicit token/state flows. Read rows whose writer or reader is the assigned unit.
- `project-structure.md`, `tech-stack.md`, `data-model.md`: global factual context; do not treat them as redesign proposals.
- No seams artifact is emitted: the analysis found no declared or inferred partial-migration cut requiring a bridge contract.

### Unit: health-api

- External trigger: `GET /api/v1/health`.
- Read `units/health-api/behavior.yaml` for response and error behavior.
- Read `units/health-api/bindings.yaml` for Express route mounting.
- Read `units/health-api/unit_decomposition.yaml` only for advisory split candidates.
- Completion evidence: report artifacts read, preserved status response, and HTTP verification.

### Unit: auth-login-api

- External trigger: `POST /api/v1/auth/login`.
- Read `units/auth-login-api/behavior.yaml`, `bindings.yaml`, and `unit_decomposition.yaml`.
- Filter `wire_contracts.yaml` to `unit: auth-login-api`; filter shared modules to `used_by_units` containing this unit.
- Completion evidence: report validation branches, generic credential errors, token contract, and authentication test evidence.

### Unit: auth-change-password-api

- External trigger: `POST /api/v1/auth/change-password`.
- Read the three files under `units/auth-change-password-api/`.
- Filter wire contracts, shared auth middleware, and token state flows for this unit.
- Completion evidence: report authorization, validation, unchanged-password, update, and 204/400/401 evidence.

### Unit: products-list-api

- External trigger: `GET /api/v1/products`.
- Read the three files under `units/products-list-api/`.
- Filter the product-list wire contract, auth middleware, pool, and token state rows.
- Completion evidence: report pagination limits, tenant filter, response mapping, and 200/400/401 evidence.

### Unit: products-create-api

- External trigger: `POST /api/v1/products`.
- Read the three files under `units/products-create-api/`.
- Filter the product-create wire contract, auth middleware, pool, and token state rows.
- Completion evidence: report validation, decimal-string preservation, conflict behavior, and 201/400/401/409 evidence.

### Unit: mobile-login-page

- External trigger: Compose route `login`.
- Read the three files under `units/mobile-login-page/`.
- Filter shared Room database and theme rows.
- Completion evidence: report PIN entry, first-user creation, lockout/error states, and navigation evidence.

### Unit: mobile-onboarding-page

- External trigger: Compose route `onboarding_plantilla`.
- Read the three files under `units/mobile-onboarding-page/`.
- Filter shared database, product model, and theme rows.
- Completion evidence: report template list, sequential seed insertion, progress state, and dashboard navigation.

### Unit: mobile-dashboard-page

- External trigger: Compose route `dashboard`.
- Read the three files under `units/mobile-dashboard-page/`.
- Filter shared database, product model, theme, and voice-related source anchors.
- Completion evidence: report metrics, microphone start/stop, partial/error states, and inventory navigation.

### Unit: mobile-inventory-page

- External trigger: Compose route `inventario`.
- Read the three files under `units/mobile-inventory-page/`.
- Filter shared database, product model, and theme rows.
- Completion evidence: report live product collection, low-stock coloring, validation, insertion, and cancel behavior.


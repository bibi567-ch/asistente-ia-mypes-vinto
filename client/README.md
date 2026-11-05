# Cliente web (React + Vite + Tailwind)

Panel **mínimo** para la rúbrica de Tecnologías en Internet (front web).

- Colores del sistema de diseño Figma: verde `#2E7D32`, rojo `#F44336`.
- Login JWT contra la API Node (`POST /api/v1/auth/login`).
- Listado y alta de productos.

**No reemplaza** la app Android offline del comerciante.

## Cómo correr

1. Backend arriba (`docker compose up -d postgres` + `npm run dev` en `backend/`).
2. En esta carpeta:

```bash
npm install
npm run dev
```

Abre http://127.0.0.1:5173  
El proxy de Vite reenvía `/api` a `http://127.0.0.1:3000`.

Usa el propietario creado con `npm run admin:create-owner` en el backend.

## Responsable sugerido

Kevin (UI) + Marializ (contrato API). Ampliar pantallas según Figma cuando el equipo priorice.

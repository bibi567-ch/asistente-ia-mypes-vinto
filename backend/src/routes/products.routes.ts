import { Router } from 'express';
import { requireAuth } from '../auth/require-auth.js';
import { pool } from '../db/pool.js';
import { obtenerProductosParaSync } from '../sincronizacion/service/sincronizacion.service.js';

export const productsRouter = Router();
productsRouter.use(requireAuth);

productsRouter.get('/', async (request, response) => {
  const limit = parseInteger(request.query.limit, 50);
  const offset = parseInteger(request.query.offset, 0);
  const businessId = request.auth?.businessId;
  if (!businessId) {
    response.status(401).json({ error: { code: 'unauthorized', message: 'Authentication is required.' } });
    return;
  }
  if (limit === null || limit < 1 || limit > 100 || offset === null || offset < 0 || offset > 100_000) {
    response.status(400).json({ error: { code: 'invalid_pagination', message: 'Use limit 1-100 and offset 0-100000.' } });
    return;
  }

  const result = await pool.query<{
    id: string;
    name: string;
    unit: string;
    sale_price: string;
    created_at: Date;
    updated_at: Date;
  }>(
    `SELECT id, name, unit, sale_price, created_at, updated_at
     FROM products
     WHERE business_id = $1
     ORDER BY created_at DESC, id
     LIMIT $2 OFFSET $3`,
    [businessId, limit, offset]
  );
  response.status(200).json({
    items: result.rows.map((product) => ({
      id: product.id,
      name: product.name,
      unit: product.unit,
      salePrice: product.sale_price,
      createdAt: product.created_at,
      updatedAt: product.updated_at
    })),
    pagination: { limit, offset }
  });
});

productsRouter.post('/', async (request, response) => {
  const businessId = request.auth?.businessId;
  if (!businessId) {
    response.status(401).json({ error: { code: 'unauthorized', message: 'Authentication is required.' } });
    return;
  }

  const { name, unit, salePrice } = request.body ?? {};
  if (
    typeof name !== 'string'
    || name.trim().length < 1
    || name.trim().length > 160
    || typeof unit !== 'string'
    || unit.trim().length < 1
    || unit.trim().length > 40
    || typeof salePrice !== 'string'
    || !/^(0|[1-9]\d{0,9})(\.\d{1,2})?$/.test(salePrice)
  ) {
    response.status(400).json({
      error: {
        code: 'invalid_product',
        message: 'Provide a name (1-160 characters), unit (1-40 characters), and non-negative salePrice with up to two decimals as a string.'
      }
    });

    productsRouter.get('/sync', async (request, response) => {
      const businessId = request.auth?.businessId;
      if (!businessId) {
        response.status(401).json({ error: { code: 'unauthorized', message: 'Authentication required.' } });
        return;
      }

      const desdeParam = request.query.desde;
      const desde = typeof desdeParam === 'string' ? Number(desdeParam) : 0;
      if (!Number.isFinite(desde) || desde < 0) {
        response.status(400).json({
          error: { code: 'invalid_since', message: 'El parámetro "desde" debe ser un timestamp en milisegundos.' }
        });
        return;
      }

      try {
        const productos = await obtenerProductosParaSync(businessId, desde);
        response.status(200).json({ items: productos, total: productos.length, timestamp: Date.now() });
      } catch {
        console.error('Error al obtener productos para sync.');
        response.status(500).json({
          error: { code: 'sync_failed', message: 'No se pudo obtener el catálogo.' }
        });
      }
    });
    return;
  }

  const result = await pool.query<{
    id: string;
    name: string;
    unit: string;
    sale_price: string;
    created_at: Date;
    updated_at: Date;
  }>(
    `INSERT INTO products (business_id, name, unit, sale_price)
     VALUES ($1, $2, $3, $4)
     ON CONFLICT (business_id, name) DO NOTHING
     RETURNING id, name, unit, sale_price, created_at, updated_at`,
    [businessId, name.trim(), unit.trim(), salePrice]
  );

  if (!result.rows[0]) {
    response.status(409).json({ error: { code: 'product_already_exists', message: 'A product with this name already exists in this business.' } });
    return;
  }

  const product = result.rows[0];
  response.status(201).json({
    id: product.id,
    name: product.name,
    unit: product.unit,
    salePrice: product.sale_price,
    createdAt: product.created_at,
    updatedAt: product.updated_at
  });
});

function parseInteger(value: unknown, fallback: number): number | null {
  if (value === undefined) return fallback;
  if (typeof value !== 'string' || !/^\d+$/.test(value)) return null;
  const parsed = Number(value);
  return Number.isSafeInteger(parsed) ? parsed : null;
}

import type { PoolClient } from 'pg';
import { pool } from '../../db/pool.js';
import type {
  ResultadoPorOperacion,
  SyncBatchResponse,
  TransaccionEntrante,
  TipoTransaccion
} from '../dto/transaccion.dto.js';

const conflictTypes = ['STOCK_NEGATIVO', 'VERSION_DESACTUALIZADA', 'DUPLICADO'] as const;
type ConflictType = (typeof conflictTypes)[number];

export async function procesarLote(
  businessId: string,
  transacciones: TransaccionEntrante[]
): Promise<SyncBatchResponse> {
  const resultados: ResultadoPorOperacion[] = [];
  const client = await pool.connect();

  try {
    await client.query('BEGIN');
    for (const transaccion of transacciones) {
      resultados.push(await procesarUna(client, businessId, transaccion));
    }
    await client.query('COMMIT');
  } catch (error) {
    await client.query('ROLLBACK');
    throw error;
  } finally {
    client.release();
  }

  return { procesadas: resultados.length, resultados };
}

async function procesarUna(
  client: PoolClient,
  businessId: string,
  transaccion: TransaccionEntrante
): Promise<ResultadoPorOperacion> {
  const payload = transaccion.payload;
  if (
    !payload
    || !Number.isFinite(payload.cantidad)
    || payload.cantidad <= 0
    || !Number.isFinite(payload.total)
    || payload.total < 0
  ) {
    return {
      operationId: transaccion.operationId,
      estado: 'RECHAZADA',
      mensaje: 'Datos de la transacción inválidos'
    };
  }

  const stockConflict = transaccion.tipo === 'VENTA' && payload.cantidad > 100;
  const estado = stockConflict ? 'CONFLICTO' : 'ACEPTADA';
  const inserted = await client.query(
    `INSERT INTO transacciones
      (business_id, operation_id, tipo, payload_json, total, forma_pago, cliente_nombre, estado)
     VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
     ON CONFLICT (business_id, operation_id) DO NOTHING
     RETURNING id`,
    [
      businessId,
      transaccion.operationId,
      transaccion.tipo,
      JSON.stringify(payload),
      payload.total,
      payload.formaPago,
      payload.clienteFiado ?? null,
      estado
    ]
  );

  if (inserted.rowCount === 0) {
    return {
      operationId: transaccion.operationId,
      estado: 'DUPLICADA',
      mensaje: 'La operación ya fue procesada anteriormente'
    };
  }

  if (stockConflict) {
    await registrarConflicto(client, businessId, transaccion, 'STOCK_NEGATIVO', {
      mensaje: 'Cantidad inusualmente alta, revisar',
      payload
    });
    return {
      operationId: transaccion.operationId,
      estado: 'CONFLICTO',
      mensaje: 'Marcado para reconciliación manual'
    };
  }

  return { operationId: transaccion.operationId, estado: 'ACEPTADA' };
}

async function registrarConflicto(
  client: PoolClient,
  businessId: string,
  transaccion: TransaccionEntrante,
  tipo: ConflictType,
  detalle: Record<string, unknown>
): Promise<void> {
  await client.query(
    `INSERT INTO conflictos (business_id, operation_id, tipo_conflicto, detalle)
     VALUES ($1, $2, $3, $4)`,
    [businessId, transaccion.operationId, tipo, JSON.stringify(detalle)]
  );
}

export interface ProductoSync {
  id: string;
  nombre: string;
  precio: number;
  stock: number;
  actualizadoEn: number;
}

export async function obtenerProductosParaSync(
  businessId: string,
  desde: number
): Promise<ProductoSync[]> {
  const result = await pool.query<{
    id: string;
    name: string;
    sale_price: string;
    updated_at: Date;
  }>(
    `SELECT id, name, sale_price, updated_at
     FROM products
     WHERE business_id = $1 AND updated_at > to_timestamp($2 / 1000.0)
     ORDER BY updated_at DESC
     LIMIT 500`,
    [businessId, desde]
  );

  return result.rows.map((row) => ({
    id: row.id,
    nombre: row.name,
    precio: Number(row.sale_price),
    stock: 0,
    actualizadoEn: row.updated_at.getTime()
  }));
}

export function esTipoTransaccion(value: unknown): value is TipoTransaccion {
  return value === 'VENTA' || value === 'COMPRA' || value === 'FIADO' || value === 'COBRO';
}

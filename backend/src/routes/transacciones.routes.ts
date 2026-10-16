import { Router } from 'express';
import { requireAuth } from '../auth/require-auth.js';
import { esTipoTransaccion, procesarLote } from '../sincronizacion/service/sincronizacion.service.js';
import { esFormaPago } from '../sincronizacion/dto/transaccion.dto.js';
import type { SyncBatchRequest, TransaccionEntrante } from '../sincronizacion/dto/transaccion.dto.js';

export const transaccionesRouter = Router();
transaccionesRouter.use(requireAuth);

transaccionesRouter.post('/batch', async (request, response) => {
  const businessId = request.auth?.businessId;
  if (!businessId) {
    response.status(401).json({ error: { code: 'unauthorized', message: 'Authentication required.' } });
    return;
  }

  const body = request.body as Partial<SyncBatchRequest> | null;
  if (!body || !Array.isArray(body.transacciones)) {
    response.status(400).json({
      error: { code: 'invalid_batch', message: 'El cuerpo debe contener un arreglo "transacciones".' }
    });
    return;
  }
  if (body.transacciones.length === 0) {
    response.status(200).json({ procesadas: 0, resultados: [] });
    return;
  }
  if (body.transacciones.length > 100) {
    response.status(400).json({
      error: { code: 'batch_too_large', message: 'Máximo 100 transacciones por lote.' }
    });
    return;
  }
  if (!body.transacciones.every(esTransaccionValida)) {
    response.status(400).json({
      error: { code: 'invalid_transaction', message: 'Una o más transacciones están mal formadas.' }
    });
    return;
  }

  try {
    response.status(200).json(await procesarLote(businessId, body.transacciones));
  } catch {
    console.error('Error al procesar lote de transacciones.');
    response.status(500).json({
      error: { code: 'sync_failed', message: 'No se pudo procesar el lote.' }
    });
  }
});

function esTransaccionValida(value: unknown): value is TransaccionEntrante {
  if (!value || typeof value !== 'object') return false;
  const transaction = value as Partial<TransaccionEntrante>;
  const payload = transaction.payload;
  return (
    typeof transaction.operationId === 'string'
    && /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(transaction.operationId)
    && esTipoTransaccion(transaction.tipo)
    && typeof transaction.schemaVersion === 'number'
    && Number.isInteger(transaction.schemaVersion)
    && transaction.schemaVersion > 0
    && !!payload
    && typeof payload === 'object'
    && typeof payload.ventaId === 'string'
    && /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(payload.ventaId)
    && typeof payload.productoId === 'string'
    && /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(payload.productoId)
    && typeof payload.cantidad === 'number'
    && Number.isFinite(payload.cantidad)
    && payload.cantidad > 0
    && typeof payload.precioUnitario === 'number'
    && Number.isFinite(payload.precioUnitario)
    && payload.precioUnitario >= 0
    && typeof payload.total === 'number'
    && Number.isFinite(payload.total)
    && payload.total >= 0
    && esFormaPago(payload.formaPago)
    && typeof payload.creadoEn === 'number'
    && Number.isFinite(payload.creadoEn)
  );
}

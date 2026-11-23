/**
 * Servicio de sincronización simulado: recibe lotes de la cola Outbox del teléfono
 * y los procesa de forma idempotente usando operationId (UUID v4).
 */
import express from 'express';
import { config } from './config';

export function createSyncService() {
  const app = express();
  app.use(express.json({ limit: '200kb' }));
  const procesadas = new Set<string>();
  let modo: 'normal' | 'caido' = (process.env.SYNC_MODO as 'normal' | 'caido') ?? 'normal';

  app.get('/health', (_req, res) => res.json({ servicio: 'sync', estado: modo }));
  app.post('/admin/modo', (req, res) => { modo = req.body?.modo === 'caido' ? 'caido' : 'normal'; res.json({ modo }); });

  app.post('/sync/batch', (req, res) => {
    if (modo === 'caido') return res.status(500).json({ error: 'sync_no_disponible' });
    const ops: Array<{ operationId?: string }> = Array.isArray(req.body?.operaciones) ? req.body.operaciones : [];
    if (ops.length === 0 || ops.length > 100) return res.status(400).json({ error: 'lote_invalido', detalle: 'entre 1 y 100 operaciones' });
    let aceptadas = 0, duplicadas = 0;
    for (const op of ops) {
      if (!op.operationId) continue;
      if (procesadas.has(op.operationId)) { duplicadas++; continue; }
      procesadas.add(op.operationId); aceptadas++;
    }
    return res.json({ aceptadas, duplicadas, usuario: req.header('x-usuario') ?? null });
  });
  return app;
}

if (require.main === module) {
  createSyncService().listen(config.syncPort, () => console.log(`[sync] escuchando en :${config.syncPort}`));
}

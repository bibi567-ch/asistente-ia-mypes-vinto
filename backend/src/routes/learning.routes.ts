import { Router } from 'express';
import { requireAuth } from '../auth/require-auth.js';
import { pool } from '../db/pool.js';

const INTENCIONES = new Set([
  'LLAMAR_CONTACTO',
  'ENVIAR_MENSAJE',
  'LEER_TEXTO',
  'CREAR_ALARMA',
  'ABRIR_APP',
  'CONSULTAR_HORA',
  'LINTERNA',
  'AJUSTES_RED',
  'GUIA_TELEFONO',
  'CONSULTA_COGNITIVA',
  'AYUDA'
]);
const UUID_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-8][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

interface Correction {
  eventId: string;
  textoOriginal: string;
  textoCorregido: string;
  intencion: string;
  fecha: number;
}

export const learningRouter = Router();
learningRouter.use(requireAuth);

learningRouter.post('/feedback', async (request, response) => {
  const businessId = request.auth?.businessId;
  const body: unknown = request.body;
  if (!businessId) {
    response.status(401).json({
      error: { code: 'unauthorized', message: 'Authentication is required.' }
    });
    return;
  }
  if (
    !body
    || typeof body !== 'object'
    || Array.isArray(body)
    || !Array.isArray((body as { logs?: unknown }).logs)
  ) {
    response.status(400).json({
      error: { code: 'invalid_feedback', message: 'Provide a logs array.' }
    });
    return;
  }

  const logs = (body as { logs: unknown[] }).logs;
  if (logs.length < 1 || logs.length > 100 || !logs.every(isCorrection)) {
    response.status(400).json({
      error: { code: 'invalid_feedback', message: 'Provide 1-100 valid, consented corrections.' }
    });
    return;
  }

  try {
    await pool.query(
      `INSERT INTO voice_training_data
         (business_id, event_id, original_text, corrected_text, intent, occurred_at)
       SELECT $1, item.event_id, item.original_text, item.corrected_text, item.intent,
              to_timestamp(item.occurred_at_ms / 1000.0)
       FROM jsonb_to_recordset($2::jsonb) AS item(
         event_id UUID,
         original_text TEXT,
         corrected_text TEXT,
         intent TEXT,
         occurred_at_ms BIGINT
       )
       ON CONFLICT (business_id, event_id) DO NOTHING`,
      [
        businessId,
        JSON.stringify(logs.map((log) => ({
          event_id: log.eventId,
          original_text: log.textoOriginal.trim(),
          corrected_text: log.textoCorregido.trim(),
          intent: log.intencion,
          occurred_at_ms: log.fecha
        })))
      ]
    );
    response.status(200).json({ accepted: logs.length });
  } catch {
    console.error('Error al almacenar correcciones de voz.');
    response.status(500).json({
      error: { code: 'feedback_storage_failed', message: 'No se pudieron almacenar las correcciones.' }
    });
  }
});

function isCorrection(value: unknown): value is Correction {
  if (!value || typeof value !== 'object' || Array.isArray(value)) return false;
  const log = value as Partial<Correction>;
  return (
    typeof log.eventId === 'string'
    && UUID_PATTERN.test(log.eventId)
    && typeof log.textoOriginal === 'string'
    && log.textoOriginal.trim().length > 0
    && log.textoOriginal.length <= 500
    && typeof log.textoCorregido === 'string'
    && log.textoCorregido.trim().length > 0
    && log.textoCorregido.length <= 500
    && typeof log.intencion === 'string'
    && INTENCIONES.has(log.intencion)
    && typeof log.fecha === 'number'
    && Number.isSafeInteger(log.fecha)
    && log.fecha > 0
  );
}

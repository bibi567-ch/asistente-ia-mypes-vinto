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
  'AYUDA',
  'DESCONOCIDO'
]);

const UUID_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-8][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

export const logsRouter = Router();
logsRouter.use(requireAuth);

logsRouter.post('/comandos', async (request, response) => {
  const body: unknown = request.body;
  if (!esRegistro(body)) {
    response.status(400).json({
      error: { code: 'invalid_voice_log', message: 'A valid voice command log is required.' }
    });
    return;
  }

  const { eventId, texto, intencion, confirmado, ejecutado, timestamp } = body;
  if (
    typeof eventId !== 'string' || !UUID_PATTERN.test(eventId) ||
    typeof texto !== 'string' || texto.trim().length < 1 || texto.length > 500 ||
    typeof intencion !== 'string' || !INTENCIONES.has(intencion) ||
    typeof confirmado !== 'boolean' || typeof ejecutado !== 'boolean' ||
    typeof timestamp !== 'number' || !Number.isSafeInteger(timestamp) || timestamp <= 0 ||
    !request.auth
  ) {
    response.status(400).json({
      error: { code: 'invalid_voice_log', message: 'The voice command log contains invalid fields.' }
    });
    return;
  }

  try {
    const result = await pool.query(
      `INSERT INTO voice_logs
         (business_id, event_id, command_text, intent, confirmed, executed, occurred_at)
       VALUES ($1, $2, $3, $4, $5, $6, to_timestamp($7 / 1000.0))
       ON CONFLICT (business_id, event_id) DO NOTHING`,
      [
        request.auth.businessId,
        eventId,
        texto.trim(),
        intencion,
        confirmado,
        ejecutado,
        timestamp
      ]
    );
    response.status(result.rowCount === 1 ? 201 : 200).json({ accepted: true });
  } catch {
    response.status(500).json({
      error: { code: 'voice_log_failed', message: 'The voice command log could not be stored.' }
    });
  }
});

function esRegistro(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}

import { Router } from 'express';
import { requireAuth } from '../auth/require-auth.js';

export const assistantRouter = Router();
assistantRouter.use(requireAuth);

assistantRouter.post('/query', (request, response) => {
  const body: unknown = request.body;
  if (!body || typeof body !== 'object' || Array.isArray(body)) {
    response.status(400).json({
      error: { code: 'invalid_question', message: 'Provide a question and language.' }
    });
    return;
  }

  const { pregunta, idioma } = body as { pregunta?: unknown; idioma?: unknown };
  if (
    typeof pregunta !== 'string'
    || pregunta.trim().length === 0
    || pregunta.trim().length > 1000
    || typeof idioma !== 'string'
    || !['es', 'qu', 'ay'].includes(idioma)
  ) {
    response.status(400).json({
      error: { code: 'invalid_question', message: 'Question must be 1-1000 characters and language must be es, qu, or ay.' }
    });
    return;
  }

  response.status(200).json({ respuesta: responder(pregunta, idioma) });
});

function responder(pregunta: string, idioma: string): string {
  if (idioma !== 'es') {
    return 'Esta guía de demostración responde en castellano por ahora. No traduzcas sus respuestas como asesoría oficial.';
  }
  const normalized = pregunta.normalize('NFD')
    .replace(/\p{Mn}+/gu, '')
    .toLowerCase();

  if (normalized.includes('iva')) {
    return 'Demostración: en Bolivia, la alícuota general del IVA es del 13%. Verifica la normativa vigente o consulta a un profesional para tu caso específico.';
  }
  if (normalized.includes('ganancia') || normalized.includes('utilidad')) {
    return 'Demostración: para estimar la ganancia, resta los costos y gastos del negocio a los ingresos del mismo período. Registra ambos con sus comprobantes y revisa el resultado con un contador.';
  }
  if (normalized.includes('inventario') || normalized.includes('stock')) {
    return 'Demostración: lleva un registro actualizado de entradas y salidas, y compara periódicamente las existencias registradas con el conteo físico.';
  }
  return 'Esta respuesta usa una guía de demostración, no un sistema RAG ni asesoría profesional. Consulta una fuente oficial para confirmar información contable o tributaria.';
}

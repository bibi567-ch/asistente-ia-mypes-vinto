/**
 * Servicio de reconocimiento de voz en línea simulado (representa el servidor con el modelo MMS).
 * IA_MODO: normal | lento | caido | aleatorio. Se puede cambiar en vivo con POST /admin/modo.
 * GET /admin/stats devuelve cuántas llamadas recibió: sirve para demostrar que, con el
 * circuito abierto, el gateway deja de enviarle tráfico.
 */
import express from 'express';
import { config } from './config';

export type ModoIA = 'normal' | 'lento' | 'caido' | 'aleatorio';
const esperar = (ms: number) => new Promise((r) => setTimeout(r, ms));

export function createIaService(modoInicial: ModoIA = (process.env.IA_MODO as ModoIA) ?? 'normal', latenciaLentaMs = 3000) {
  const app = express();
  app.use(express.json({ limit: '6mb' }));
  let modo: ModoIA = modoInicial;
  let llamadas = 0;

  app.get('/health', (_req, res) => res.json({ servicio: 'ia', modo }));
  app.post('/admin/modo', (req, res) => {
    const m = req.body?.modo as ModoIA;
    if (['normal', 'lento', 'caido', 'aleatorio'].includes(m)) modo = m;
    res.json({ modo });
  });
  app.get('/admin/stats', (_req, res) => res.json({ llamadas, modo }));
  app.post('/admin/reset', (_req, res) => { llamadas = 0; res.json({ llamadas }); });

  app.post('/asr/transcribe', async (req, res) => {
    llamadas++;
    const falla = modo === 'caido' || (modo === 'aleatorio' && Math.random() < 0.5);
    if (modo === 'lento') await esperar(latenciaLentaMs);
    else await esperar(120 + Math.floor(Math.random() * 80));
    if (falla) return res.status(500).json({ error: 'modelo_no_disponible' });
    return res.json({
      texto: req.body?.textoSimulado ?? 'llama a mi hermana',
      idioma: req.body?.idioma ?? 'quh',
      confianza: 0.91,
      motor: 'mms-simulado',
    });
  });
  return app;
}

if (require.main === module) {
  createIaService().listen(config.iaPort, () => console.log(`[ia] escuchando en :${config.iaPort} (modo ${process.env.IA_MODO ?? 'normal'})`));
}

/**
 * Panel de arquitectura en vivo (http://localhost:8090).
 * Es un CLIENTE más del sistema, como la app Android: no forma parte del gateway.
 * Ejecuta los escenarios de calidad ID-01 a ID-06 contra el gateway real y muestra,
 * en el diagrama de contenedores, qué componente arquitectónico actuó en cada petición.
 */
import express from 'express';
import { join } from 'node:path';
import { randomUUID } from 'node:crypto';
import { config } from './config';

const GW = process.env.PANEL_GATEWAY_URL ?? `http://localhost:${config.gatewayPort}`;
const IA = process.env.PANEL_IA_URL ?? `http://localhost:${config.iaPort}`;
const SYNC = process.env.PANEL_SYNC_URL ?? `http://localhost:${config.syncPort}`;
const PUERTO = Number(process.env.PANEL_PORT ?? 8090);

type Resultado = { n: number; http: number; circuito: string | null; ms: number; detalle: string; actuo: string };

async function llamar(metodo: string, url: string, cuerpo?: unknown, token?: string) {
  const t0 = performance.now();
  try {
    const r = await fetch(url, {
      method: metodo,
      headers: { 'content-type': 'application/json', ...(token ? { authorization: `Bearer ${token}` } : {}) },
      body: cuerpo === undefined ? undefined : JSON.stringify(cuerpo),
    });
    const ms = Math.round(performance.now() - t0);
    let data: any = null;
    try { data = await r.json(); } catch { /* sin cuerpo JSON */ }
    return { http: r.status, circuito: r.headers.get('x-circuit-state'), ms, data };
  } catch (e) {
    return { http: -1, circuito: null, ms: Math.round(performance.now() - t0), data: { error: String(e) } };
  }
}

const tokens: Record<string, string | undefined> = {};
async function token(usuario: 'comerciante' | 'admin', renovar = false) {
  if (tokens[usuario] && !renovar) return tokens[usuario]!;
  const pass = usuario === 'admin' ? 'admin2026' : 'vinto2026';
  const r = await llamar('POST', `${GW}/api/v1/auth/login`, { usuario, password: pass });
  tokens[usuario] = r.data?.token;
  return tokens[usuario] ?? '';
}

/** Qué pieza de la arquitectura respondió, según el código HTTP y el motivo del gateway. */
function quienActuo(r: Awaited<ReturnType<typeof llamar>>): string {
  if (r.http === -1) return 'Sin conexión con el gateway';
  if (r.http === 401) return 'requiereJwt (gateway.ts:125): rechazó en el borde';
  if (r.http === 403) return "soloRol('admin') (gateway.ts:137): control por rol";
  if (r.http === 429) return 'Límite de tasa (gateway.ts:68)';
  const m = r.data?.motivo;
  if (m === 'circuito_abierto') return 'Circuit Breaker ABIERTO: respondió sin llamar al servicio (gateway.ts:102)';
  if (m === 'tiempo_agotado') return 'Circuit Breaker: cortó por timeout de 2 s (config.ts)';
  if (m === 'fallo_del_servicio') return 'Circuit Breaker: contó un fallo y respondió el fallback (gateway.ts:94)';
  if (r.http === 200 && r.data?.duplicadas !== undefined) return 'Sync: idempotencia por operationId (syncMock.ts:24)';
  if (r.http === 200) return 'Gateway → servicio: respuesta normal';
  return `HTTP ${r.http}`;
}

function detalle(r: Awaited<ReturnType<typeof llamar>>): string {
  const d = r.data ?? {};
  if (d.texto) return `Nube: «${d.texto}» (${d.motor ?? 'ia'})`;
  if (d.fallback) return `Fallback: ${d.fallback} → ${d.accion ?? ''}`;
  if (d.accion) return `Fallback: ${d.accion}`;
  if (d.aceptadas !== undefined) return `aceptadas ${d.aceptadas} · duplicadas ${d.duplicadas}`;
  if (d.error) return d.error;
  return JSON.stringify(d).slice(0, 120);
}

async function transcribir(n: number, conToken = true): Promise<Resultado[]> {
  const out: Resultado[] = [];
  for (let i = 1; i <= n; i++) {
    let t = conToken ? await token('comerciante') : undefined;
    let r = await llamar('POST', `${GW}/api/v1/asr/transcribe`, { textoSimulado: 'vendí dos panes', idioma: 'quh' }, t);
    if (r.http === 401 && conToken) { t = await token('comerciante', true); r = await llamar('POST', `${GW}/api/v1/asr/transcribe`, { textoSimulado: 'vendí dos panes', idioma: 'quh' }, t); }
    out.push({ n: i, http: r.http, circuito: r.circuito, ms: r.ms, detalle: detalle(r), actuo: quienActuo(r) });
  }
  return out;
}

async function enviarLote(lote: unknown, n: number): Promise<Resultado[]> {
  const out: Resultado[] = [];
  for (let i = 1; i <= n; i++) {
    const r = await llamar('POST', `${GW}/api/v1/sync/batch`, lote, await token('comerciante'));
    out.push({ n: i, http: r.http, circuito: r.circuito, ms: r.ms, detalle: detalle(r), actuo: quienActuo(r) });
  }
  return out;
}

const esperar = (ms: number) => new Promise((r) => setTimeout(r, ms));
const modoIa = (modo: string) => llamar('POST', `${IA}/admin/modo`, { modo });
const modoSync = (modo: string) => llamar('POST', `${SYNC}/admin/modo`, { modo });

/** Cada escenario: qué se demuestra, qué pieza de la arquitectura lo resuelve y dónde está en el código. */
const ESCENARIOS: Record<string, { titulo: string; atributo: string; explicacion: string; correr: () => Promise<Resultado[]> }> = {
  normal: {
    titulo: 'Funcionamiento normal', atributo: 'Línea base',
    explicacion: 'El teléfono pide una segunda transcripción. El gateway valida el JWT y reenvía con breaker.fire() al servicio de IA. Circuito CERRADO, ~150 ms.',
    correr: async () => { await modoIa('normal'); return transcribir(3); },
  },
  'sin-token': {
    titulo: 'ID-05 · Petición sin token', atributo: 'Seguridad',
    explicacion: 'requiereJwt rechaza en el borde con 401. El servicio de IA nunca recibe la petición: la seguridad está centralizada en el gateway.',
    correr: () => transcribir(1, false),
  },
  rol: {
    titulo: 'ID-05 · Comerciante pide el estado de los circuitos', atributo: 'Seguridad',
    explicacion: "La ruta /gateway/estado exige rol admin (soloRol). Con el token del comerciante el gateway responde 403.",
    correr: async () => {
      const r = await llamar('GET', `${GW}/gateway/estado`, undefined, await token('comerciante'));
      return [{ n: 1, http: r.http, circuito: null, ms: r.ms, detalle: detalle(r), actuo: quienActuo(r) }];
    },
  },
  caida: {
    titulo: 'ID-01 e ID-02 · Se cae la IA', atributo: 'Disponibilidad y rendimiento',
    explicacion: 'Las primeras peticiones fallan (~150 ms) y el breaker las cuenta. Al superar el 50 % de fallos con al menos 4 llamadas, el circuito se ABRE: el gateway responde 503 con fallback vosk_local en ~1 ms y deja de llamar a la IA.',
    correr: async () => { await llamar('POST', `${IA}/admin/reset`, {}); await modoIa('caido'); return transcribir(10); },
  },
  lenta: {
    titulo: 'ID-03 · La IA está lenta (3 s)', atributo: 'Rendimiento',
    explicacion: 'El breaker corta cada llamada a los 2 s (timeout) y responde el fallback: el teléfono nunca espera los 3 s del servicio.',
    correr: async () => { await modoIa('lento'); return transcribir(2); },
  },
  recuperar: {
    titulo: 'ID-04 · La IA vuelve', atributo: 'Disponibilidad',
    explicacion: 'Pasados 10 s (resetTimeout) el circuito queda SEMIABIERTO y deja pasar una llamada de prueba. Como funciona, se CIERRA solo, sin reiniciar nada.',
    correr: async () => { await modoIa('normal'); await esperar(11_000); return transcribir(2); },
  },
  sync: {
    titulo: 'ID-06 · El Outbox reenvía el mismo lote', atributo: 'Confiabilidad',
    explicacion: 'El mismo lote (mismos operationId) llega dos veces tras una reconexión. El servicio de sincronización lo procesa una sola vez: la segunda vez son duplicadas, no ventas nuevas.',
    correr: async () => {
      await modoSync('normal');
      const lote = { operaciones: [1, 2, 3].map((i) => ({ operationId: randomUUID(), tipo: 'VENTA', total: i * 5 })) };
      return enviarLote(lote, 2);
    },
  },
  'sync-caido': {
    titulo: 'Breaker por servicio · Se cae la sincronización', atributo: 'Disponibilidad',
    explicacion: 'Se abre el circuito de sync, no el de la IA: cada servicio tiene su propio breaker. El fallback le dice al teléfono que conserve las ventas en el Outbox y reintente (retry-after).',
    correr: async () => {
      await modoSync('caido');
      const lote = { operaciones: [{ operationId: randomUUID(), tipo: 'VENTA', total: 10 }] };
      const r = await enviarLote(lote, 6);
      await modoSync('normal');
      return r;
    },
  },
};

const app = express();
app.use(express.json());
app.use(express.static(join(__dirname, '..', 'public')));

app.get('/api/escenarios', (_req, res) =>
  res.json(Object.entries(ESCENARIOS).map(([id, e]) => ({ id, titulo: e.titulo, atributo: e.atributo, explicacion: e.explicacion }))));

app.post('/api/escenario/:id', async (req, res) => {
  const e = ESCENARIOS[req.params.id];
  if (!e) return res.status(404).json({ error: 'escenario_desconocido' });
  const resultados = await e.correr();
  res.json({ id: req.params.id, titulo: e.titulo, explicacion: e.explicacion, resultados });
});

app.get('/api/estado', async (_req, res) => {
  const [estado, ia, iaSalud, sync] = await Promise.all([
    token('admin').then((t) => llamar('GET', `${GW}/gateway/estado`, undefined, t)),
    llamar('GET', `${IA}/admin/stats`),
    llamar('GET', `${IA}/health`),
    llamar('GET', `${SYNC}/health`),
  ]);
  if (estado.http === 401) await token('admin', true);
  res.json({
    gateway: estado.http === 200 ? estado.data : null,
    ia: { llamadas: ia.data?.llamadas ?? null, modo: iaSalud.data?.modo ?? null },
    sync: { modo: sync.data?.estado ?? null },
  });
});

export function iniciarPanel() {
  return app.listen(PUERTO, () => console.log(`[panel] arquitectura en vivo: http://localhost:${PUERTO}`));
}

if (require.main === module) iniciarPanel();

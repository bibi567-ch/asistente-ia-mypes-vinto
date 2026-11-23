/**
 * API Gateway con Circuit Breaker — prototipo del TP de Arquitectura de Software (ARQ-643).
 *
 * Responsabilidades del gateway (punto único de entrada para el cliente Android Kotlin):
 *  1. Enrutar /api/v1/auth, /api/v1/sync y /api/v1/asr hacia sus servicios.
 *  2. Validar el JWT una sola vez en el borde (los servicios internos reciben X-Usuario y X-Rol).
 *  3. Limitar la tasa de peticiones por IP.
 *  4. Proteger cada servicio con un Circuit Breaker (biblioteca opossum):
 *     - CERRADO: las llamadas pasan normalmente.
 *     - ABIERTO: tras superar el umbral de fallos, responde de inmediato con un fallback
 *       sin llamar al servicio caído (evita esperas y cascadas de errores).
 *     - SEMIABIERTO: pasado resetTimeout deja pasar una llamada de prueba; si funciona, se cierra.
 *
 * Fallback del reconocimiento en línea: el gateway indica al teléfono que use la transcripción
 * local de Vosk (lo mismo que ya hace CloudAsrFallback en el cliente Kotlin).
 */
import express, { type NextFunction, type Request, type Response } from 'express';
import jwt from 'jsonwebtoken';
import CircuitBreaker from 'opossum';
import { randomUUID } from 'node:crypto';
import { config, type BreakerConfig } from './config';

type Upstream = { status: number; body: unknown };
type Servicio = 'auth' | 'sync' | 'ia';

class UpstreamError extends Error {
  constructor(public status: number, public body: unknown) { super(`upstream ${status}`); }
}

export interface GatewayOptions {
  authUrl: string; syncUrl: string; iaUrl: string;
  jwtSecret: string; breaker: BreakerConfig; rateLimitPorMinuto: number;
}

/** Llama a un servicio interno. Los 5xx cuentan como fallo del breaker; los 4xx no (son errores del cliente). */
async function llamar(url: string, body: unknown, headers: Record<string, string>, timeoutMs: number): Promise<Upstream> {
  const r = await fetch(url, {
    method: 'POST',
    headers: { 'content-type': 'application/json', ...headers },
    body: JSON.stringify(body ?? {}),
    signal: AbortSignal.timeout(timeoutMs + 100),
  });
  const data = await r.json().catch(() => ({}));
  if (r.status >= 500) throw new UpstreamError(r.status, data);
  return { status: r.status, body: data };
}

export function createGateway(opts: GatewayOptions) {
  const app = express();
  app.disable('x-powered-by');
  app.use(express.json({ limit: '6mb' }));

  // ---- Correlación y registro de cada petición ----
  app.use((req, res, next) => {
    const id = req.header('x-request-id') ?? randomUUID();
    res.setHeader('x-request-id', id);
    (req as Request & { requestId: string }).requestId = id;
    const t0 = performance.now();
    res.on('finish', () => {
      if (process.env.NODE_ENV !== 'test') {
        console.log(`[gateway] ${req.method} ${req.path} -> ${res.statusCode} (${(performance.now() - t0).toFixed(0)} ms) id=${id}`);
      }
    });
    next();
  });

  // ---- Limitador de tasa (ventana fija por IP) ----
  const ventana = new Map<string, { inicio: number; n: number }>();
  app.use('/api', (req, res, next) => {
    const ip = req.ip ?? 'desconocida';
    const ahora = Date.now();
    const v = ventana.get(ip);
    if (!v || ahora - v.inicio > 60_000) { ventana.set(ip, { inicio: ahora, n: 1 }); return next(); }
    if (++v.n > opts.rateLimitPorMinuto) return res.status(429).json({ error: 'demasiadas_peticiones' });
    next();
  });

  // ---- Un Circuit Breaker por servicio ----
  const crearBreaker = (nombre: Servicio) => {
    const b = new CircuitBreaker(
      (url: string, body: unknown, headers: Record<string, string>) => llamar(url, body, headers, opts.breaker.timeout),
      { ...opts.breaker, name: nombre },
    );
    b.on('open', () => console.warn(`[breaker:${nombre}] ABIERTO`));
    b.on('halfOpen', () => console.warn(`[breaker:${nombre}] SEMIABIERTO (llamada de prueba)`));
    b.on('close', () => console.warn(`[breaker:${nombre}] CERRADO`));
    return b;
  };
  const breakers: Record<Servicio, CircuitBreaker<[string, unknown, Record<string, string>], Upstream>> = {
    auth: crearBreaker('auth'), sync: crearBreaker('sync'), ia: crearBreaker('ia'),
  };

  // Respuestas de degradación controlada cuando el circuito está abierto o la llamada falla.
  const FALLBACK: Record<Servicio, { status: number; body: Record<string, unknown> }> = {
    auth: { status: 503, body: { error: 'auth_no_disponible', accion: 'reintentar_mas_tarde' } },
    sync: { status: 503, body: { error: 'sync_no_disponible', accion: 'conservar_en_outbox' } },
    ia: { status: 503, body: { error: 'asr_en_linea_no_disponible', fallback: 'vosk_local', accion: 'usar_transcripcion_local' } },
  };

  const estadoDe = (b: CircuitBreaker) => (b.opened ? 'ABIERTO' : b.halfOpen ? 'SEMIABIERTO' : 'CERRADO');

  function reenviar(servicio: Servicio, url: string) {
    return async (req: Request, res: Response) => {
      const b = breakers[servicio];
      const u = (req as Request & { usuario?: { sub: string; rol: string } }).usuario;
      const headers: Record<string, string> = { 'x-request-id': res.getHeader('x-request-id') as string };
      if (u) { headers['x-usuario'] = u.sub; headers['x-rol'] = u.rol; }
      try {
        const r = await b.fire(url, req.body, headers);
        res.setHeader('x-circuit-state', estadoDe(b));
        return res.status(r.status).json(r.body);
      } catch (e) {
        const f = FALLBACK[servicio];
        const code = (e as { code?: string }).code;
        const motivo = code === 'EOPENBREAKER' ? 'circuito_abierto' : code === 'ETIMEDOUT' ? 'tiempo_agotado' : 'fallo_del_servicio';
        res.setHeader('x-circuit-state', estadoDe(b));
        res.setHeader('x-fallback', servicio === 'ia' ? 'vosk-local' : 'true');
        if (servicio === 'sync') res.setHeader('retry-after', String(Math.ceil(opts.breaker.resetTimeout / 1000)));
        return res.status(f.status).json({ ...f.body, motivo });
      }
    };
  }

  // ---- Validación del JWT en el borde ----
  function requiereJwt(req: Request, res: Response, next: NextFunction) {
    const h = req.header('authorization') ?? '';
    const token = h.startsWith('Bearer ') ? h.slice(7) : '';
    if (!token) return res.status(401).json({ error: 'token_requerido' });
    try {
      const p = jwt.verify(token, opts.jwtSecret, { algorithms: ['HS256'] }) as { sub: string; rol: string };
      (req as Request & { usuario?: { sub: string; rol: string } }).usuario = { sub: p.sub, rol: p.rol };
      next();
    } catch {
      return res.status(401).json({ error: 'token_invalido_o_expirado' });
    }
  }
  const soloRol = (rol: string) => (req: Request, res: Response, next: NextFunction) =>
    (req as Request & { usuario?: { rol: string } }).usuario?.rol === rol ? next() : res.status(403).json({ error: 'rol_insuficiente' });

  // ---- Rutas públicas del gateway ----
  app.get('/health', (_req, res) => res.json({ servicio: 'gateway', estado: 'ok' }));
  app.post('/api/v1/auth/login', reenviar('auth', `${opts.authUrl}/login`));
  app.post('/api/v1/sync/batch', requiereJwt, reenviar('sync', `${opts.syncUrl}/sync/batch`));
  app.post('/api/v1/asr/transcribe', requiereJwt, reenviar('ia', `${opts.iaUrl}/asr/transcribe`));

  // Estado de los circuitos (observabilidad para la demostración). Solo rol admin.
  const estado = (_req: Request, res: Response) => {
    const out: Record<string, unknown> = {};
    for (const [n, b] of Object.entries(breakers)) {
      const s = b.stats;
      out[n] = { estado: estadoDe(b), exitos: s.successes, fallos: s.failures, timeouts: s.timeouts,
        rechazadasPorCircuito: s.rejects, fallbacks: s.fallbacks, latenciaMediaMs: Math.round(s.latencyMean) };
    }
    res.json(out);
  };
  app.get('/gateway/estado', requiereJwt, soloRol('admin'), estado);

  app.use((_req, res) => res.status(404).json({ error: 'ruta_no_encontrada' }));
  return { app, breakers };
}

if (require.main === module) {
  const { app } = createGateway(config);
  app.listen(config.gatewayPort, () => console.log(`[gateway] escuchando en :${config.gatewayPort}`));
}

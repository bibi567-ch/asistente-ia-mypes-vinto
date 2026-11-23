/**
 * Demostración en vivo del patrón (un solo comando: npm run demo).
 * Levanta auth, sync, ia y gateway en sus puertos y ejecuta cuatro fases:
 *   A) IA normal  B) IA caída (el circuito se abre)  C) recuperación  D) sincronización idempotente.
 * Al final imprime las métricas medidas y las guarda en demo-resultados.json.
 */
import { writeFileSync } from 'node:fs';
import { config } from './config';
import { createAuthService } from './authMock';
import { createSyncService } from './syncMock';
import { createIaService } from './iaMock';
import { createGateway } from './gateway';

const GW = `http://localhost:${config.gatewayPort}`;
const IA = `http://localhost:${config.iaPort}`;
const esperar = (ms: number) => new Promise((r) => setTimeout(r, ms));
const post = async (url: string, body: unknown, token?: string) => {
  const t0 = performance.now();
  const r = await fetch(url, { method: 'POST', headers: { 'content-type': 'application/json', ...(token ? { authorization: `Bearer ${token}` } : {}) }, body: JSON.stringify(body) });
  const data = await r.json() as any;
  return { status: r.status, ms: performance.now() - t0, estado: r.headers.get('x-circuit-state'), data };
};
const stats = (xs: number[]) => {
  const s = [...xs].sort((a, b) => a - b);
  const p = (q: number) => s[Math.min(s.length - 1, Math.floor(q * s.length))];
  return { n: s.length, mediaMs: +(s.reduce((a, b) => a + b, 0) / s.length).toFixed(1), p50Ms: +p(0.5).toFixed(1), p95Ms: +p(0.95).toFixed(1), maxMs: +s[s.length - 1].toFixed(1) };
};

async function main() {
  const servers = [
    createAuthService().listen(config.authPort),
    createSyncService().listen(config.syncPort),
    createIaService('normal').listen(config.iaPort),
    createGateway(config).app.listen(config.gatewayPort),
  ];
  await esperar(300);
  const token = (await post(`${GW}/api/v1/auth/login`, { usuario: 'comerciante', password: 'vinto2026' })).data.token;
  const admin = (await post(`${GW}/api/v1/auth/login`, { usuario: 'admin', password: 'admin2026' })).data.token;
  const llamadasIA = async () => ((await (await fetch(`${IA}/admin/stats`)).json()) as any).llamadas as number;
  const resultados: Record<string, unknown> = { config: config.breaker };

  console.log('\n=== Fase A: IA normal (10 peticiones) ===');
  const a = [];
  for (let i = 0; i < 10; i++) a.push(await post(`${GW}/api/v1/asr/transcribe`, { textoSimulado: 'llama a mi hermana', idioma: 'quh' }, token));
  resultados.faseA = { exitos: a.filter((r) => r.status === 200).length, latencia: stats(a.map((r) => r.ms)) };
  console.log(resultados.faseA);

  console.log('\n=== Fase B: IA caída (20 peticiones) ===');
  await post(`${IA}/admin/modo`, { modo: 'caido' });
  const antesB = await llamadasIA();
  const b = [];
  for (let i = 0; i < 20; i++) {
    const r = await post(`${GW}/api/v1/asr/transcribe`, {}, token);
    b.push(r);
    console.log(`  #${String(i + 1).padStart(2)} -> ${r.status} ${r.data.motivo ?? ''} [${r.estado}] ${r.ms.toFixed(1)} ms`);
  }
  const abiertas = b.filter((r) => r.data.motivo === 'circuito_abierto');
  resultados.faseB = {
    peticiones: b.length,
    llegaronALaIA: (await llamadasIA()) - antesB,
    respondidasConCircuitoAbierto: abiertas.length,
    todasConFallbackVoskLocal: b.every((r) => r.data.fallback === 'vosk_local'),
    latenciaConCircuitoAbierto: stats(abiertas.map((r) => r.ms)),
    latenciaAntesDeAbrir: stats(b.filter((r) => r.data.motivo !== 'circuito_abierto').map((r) => r.ms)),
  };
  console.log(resultados.faseB);
  resultados.estadoTrasFaseB = ((await (await fetch(`${GW}/gateway/estado`, { headers: { authorization: `Bearer ${admin}` } })).json()) as any).ia;
  console.log('Estado del circuito ia:', resultados.estadoTrasFaseB);

  console.log(`\n=== Fase C: la IA se recupera; esperando resetTimeout (${config.breaker.resetTimeout} ms) ===`);
  await post(`${IA}/admin/modo`, { modo: 'normal' });
  const tRec = performance.now();
  await esperar(config.breaker.resetTimeout + 200);
  const c = [];
  for (let i = 0; i < 5; i++) c.push(await post(`${GW}/api/v1/asr/transcribe`, { textoSimulado: 'pon una alarma a las seis' }, token));
  resultados.faseC = { exitos: c.filter((r) => r.status === 200).length, estadoFinal: c[c.length - 1].estado, segundosHastaRecuperar: +((performance.now() - tRec) / 1000).toFixed(1) };
  console.log(resultados.faseC);

  console.log('\n=== Fase D: sincronización idempotente ===');
  const lote = { operaciones: [{ operationId: '7b1e-01' }, { operationId: '7b1e-02' }, { operationId: '7b1e-03' }] };
  const d1 = await post(`${GW}/api/v1/sync/batch`, lote, token);
  const d2 = await post(`${GW}/api/v1/sync/batch`, lote, token);
  resultados.faseD = { primerEnvio: d1.data, reenvioDelMismoLote: d2.data };
  console.log(resultados.faseD);

  writeFileSync('demo-resultados.json', JSON.stringify(resultados, null, 2));
  console.log('\nResultados guardados en demo-resultados.json');
  servers.forEach((s) => s.close());
}

main().catch((e) => { console.error(e); process.exit(1); });

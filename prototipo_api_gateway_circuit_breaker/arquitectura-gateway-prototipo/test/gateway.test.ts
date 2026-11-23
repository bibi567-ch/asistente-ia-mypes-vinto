/**
 * Pruebas automáticas del patrón (node:test). Levantan los 4 servicios en puertos libres.
 * Ejecutar: npm test
 */
import { test, before, after } from 'node:test';
import assert from 'node:assert/strict';
import type { Server } from 'node:http';
import type { AddressInfo } from 'node:net';
import { createGateway } from '../src/gateway';
import { createAuthService } from '../src/authMock';
import { createSyncService } from '../src/syncMock';
import { createIaService } from '../src/iaMock';

process.env.NODE_ENV = 'test';
const SECRET = 'secreto-de-pruebas-de-al-menos-32-bytes';
const servers: Server[] = [];
let GW = '', IA = '';
const listen = (app: import('express').Express) => new Promise<string>((r) => {
  const s = app.listen(0, () => r(`http://127.0.0.1:${(s.address() as AddressInfo).port}`)); servers.push(s);
});
const post = async (url: string, body: unknown, token?: string) => {
  const r = await fetch(url, { method: 'POST', headers: { 'content-type': 'application/json', ...(token ? { authorization: `Bearer ${token}` } : {}) }, body: JSON.stringify(body) });
  return { status: r.status, headers: r.headers, body: await r.json() as any };
};
let token = '', tokenAdmin = '';

before(async () => {
  const auth = await listen(createAuthService(SECRET));
  const sync = await listen(createSyncService());
  IA = await listen(createIaService('normal', 600));
  const { app } = createGateway({
    authUrl: auth, syncUrl: sync, iaUrl: IA, jwtSecret: SECRET, rateLimitPorMinuto: 1000,
    breaker: { timeout: 300, errorThresholdPercentage: 50, resetTimeout: 800, volumeThreshold: 4, rollingCountTimeout: 10000 },
  });
  GW = await listen(app);
  token = (await post(`${GW}/api/v1/auth/login`, { usuario: 'comerciante', password: 'vinto2026' })).body.token;
  tokenAdmin = (await post(`${GW}/api/v1/auth/login`, { usuario: 'admin', password: 'admin2026' })).body.token;
});
after(() => servers.forEach((s) => s.close()));

const modoIA = (modo: string) => post(`${IA}/admin/modo`, { modo });
const llamadasIA = async () => (await (await fetch(`${IA}/admin/stats`)).json() as any).llamadas as number;

test('rechaza peticiones sin JWT (401) y con credenciales inválidas', async () => {
  assert.equal((await post(`${GW}/api/v1/asr/transcribe`, {})).status, 401);
  assert.equal((await post(`${GW}/api/v1/auth/login`, { usuario: 'x', password: 'y' })).status, 401);
});

test('con circuito cerrado, la transcripción en línea responde 200', async () => {
  const r = await post(`${GW}/api/v1/asr/transcribe`, { textoSimulado: 'kunan llama a mi mama', idioma: 'quh' }, token);
  assert.equal(r.status, 200);
  assert.equal(r.body.texto, 'kunan llama a mi mama');
  assert.equal(r.headers.get('x-circuit-state'), 'CERRADO');
});

test('el endpoint de estado exige rol admin (RBAC)', async () => {
  assert.equal((await fetch(`${GW}/gateway/estado`, { headers: { authorization: `Bearer ${token}` } })).status, 403);
  assert.equal((await fetch(`${GW}/gateway/estado`, { headers: { authorization: `Bearer ${tokenAdmin}` } })).status, 200);
});

test('si la IA cae, el circuito se abre y responde el fallback sin llamar al servicio', async () => {
  await modoIA('caido');
  for (let i = 0; i < 4; i++) await post(`${GW}/api/v1/asr/transcribe`, {}, token);
  const antes = await llamadasIA();
  const t0 = performance.now();
  const r = await post(`${GW}/api/v1/asr/transcribe`, {}, token);
  const ms = performance.now() - t0;
  assert.equal(r.status, 503);
  assert.equal(r.body.fallback, 'vosk_local');
  assert.equal(r.body.motivo, 'circuito_abierto');
  assert.equal(r.headers.get('x-circuit-state'), 'ABIERTO');
  assert.equal(await llamadasIA(), antes, 'con el circuito abierto no debe llegar tráfico a la IA');
  assert.ok(ms < 100, `el fallback debe ser inmediato (fue ${ms.toFixed(1)} ms)`);
});

test('tras resetTimeout pasa a semiabierto y se cierra cuando la IA se recupera', async () => {
  await modoIA('normal');
  await new Promise((r) => setTimeout(r, 900));
  const r = await post(`${GW}/api/v1/asr/transcribe`, { textoSimulado: 'pon una alarma' }, token);
  assert.equal(r.status, 200);
  assert.equal(r.headers.get('x-circuit-state'), 'CERRADO');
});

test('una IA lenta se corta por timeout y devuelve fallback', async () => {
  await modoIA('lento');
  const t0 = performance.now();
  const r = await post(`${GW}/api/v1/asr/transcribe`, {}, token);
  assert.equal(r.status, 503);
  assert.equal(r.body.motivo, 'tiempo_agotado');
  assert.ok(performance.now() - t0 < 500);
  await modoIA('normal');
});

test('la sincronización es idempotente por operationId', async () => {
  const lote = { operaciones: [{ operationId: 'a1' }, { operationId: 'a2' }] };
  assert.deepEqual((await post(`${GW}/api/v1/sync/batch`, lote, token)).body.aceptadas, 2);
  const r = await post(`${GW}/api/v1/sync/batch`, lote, token);
  assert.equal(r.body.aceptadas, 0);
  assert.equal(r.body.duplicadas, 2);
  assert.equal(r.body.usuario, 'comerciante', 'el gateway propaga el usuario del JWT');
});

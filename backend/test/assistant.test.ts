import assert from 'node:assert/strict';
import { once } from 'node:events';
import { createServer } from 'node:http';
import { test } from 'node:test';

process.env.JWT_SECRET ??= 'test-secret-that-is-at-least-32-bytes-long';
process.env.GEMINI_API_KEY = '';
const { app } = await import('../src/app.js');
const { createAccessToken } = await import('../src/auth/tokens.js');
const { env } = await import('../src/config/env.js');

async function startTestServer(): Promise<{ baseUrl: string; close: () => Promise<void> }> {
  const server = createServer(app);
  server.listen(0, '127.0.0.1');
  await once(server, 'listening');
  const address = server.address();
  assert.ok(address && typeof address !== 'string');
  return {
    baseUrl: `http://127.0.0.1:${address.port}`,
    close: () => new Promise<void>((resolve, reject) => {
      server.close((error) => error ? reject(error) : resolve());
    })
  };
}

test('assistant query requires authentication', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const response = await fetch(`${server.baseUrl}/api/v1/assistant/query`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ pregunta: '¿Qué es el IVA?', idioma: 'es' })
  });
  assert.equal(response.status, 401);
});

test('assistant query validates input and returns a labelled demo answer', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const token = await createAccessToken({
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  });
  const invalid = await fetch(`${server.baseUrl}/api/v1/assistant/query`, {
    method: 'POST',
    headers: { authorization: `Bearer ${token}`, 'content-type': 'application/json' },
    body: JSON.stringify({ pregunta: '', idioma: 'es' })
  });
  assert.equal(invalid.status, 400);

  const response = await fetch(`${server.baseUrl}/api/v1/assistant/query`, {
    method: 'POST',
    headers: { authorization: `Bearer ${token}`, 'content-type': 'application/json' },
    body: JSON.stringify({ pregunta: '¿Qué es el IVA?', idioma: 'es' })
  });
  const body = await response.json() as { respuesta?: string };
  assert.equal(response.status, 200);
  assert.match(body.respuesta ?? '', /13%/);
});

test('assistant query uses configured Gemini without exposing the key to mobile', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const token = await createAccessToken({
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  });
  const originalKey = env.geminiApiKey;
  const originalModel = env.geminiModel;
  const originalFetch = globalThis.fetch;
  context.after(() => {
    env.geminiApiKey = originalKey;
    env.geminiModel = originalModel;
    globalThis.fetch = originalFetch;
  });
  env.geminiApiKey = 'test-gemini-key';
  env.geminiModel = 'test-model';
  globalThis.fetch = async (input, init) => {
    if (String(input).startsWith('http://127.0.0.1:')) {
      return originalFetch(input, init);
    }

    assert.match(String(input), /generativelanguage\.googleapis\.com/);
    assert.equal(new Headers(init?.headers).get('x-goog-api-key'), 'test-gemini-key');
    assert.match(String(init?.body), /¿Qué es el IVA\?/);
    return new Response(JSON.stringify({
      candidates: [{ content: { parts: [{ text: 'Respuesta simulada del proveedor.' }] } }]
    }), { status: 200, headers: { 'content-type': 'application/json' } });
  };

  const response = await fetch(`${server.baseUrl}/api/v1/assistant/query`, {
    method: 'POST',
    headers: { authorization: `Bearer ${token}`, 'content-type': 'application/json' },
    body: JSON.stringify({ pregunta: '¿Qué es el IVA?', idioma: 'es' })
  });
  const body = await response.json() as { respuesta?: string; error?: { code?: string } };
  assert.equal(response.status, 200);
  assert.equal(body.respuesta, 'Respuesta simulada del proveedor.');
  assert.equal(body.error, undefined);
});

test('assistant query reports a provider failure without returning a successful demo answer', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const token = await createAccessToken({
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  });
  const originalKey = env.geminiApiKey;
  const originalFetch = globalThis.fetch;
  context.after(() => {
    env.geminiApiKey = originalKey;
    globalThis.fetch = originalFetch;
  });
  env.geminiApiKey = 'test-gemini-key';
  globalThis.fetch = async (input, init) => {
    if (String(input).startsWith('http://127.0.0.1:')) {
      return originalFetch(input, init);
    }
    return new Response('provider unavailable', { status: 503 });
  };

  const response = await fetch(`${server.baseUrl}/api/v1/assistant/query`, {
    method: 'POST',
    headers: { authorization: `Bearer ${token}`, 'content-type': 'application/json' },
    body: JSON.stringify({ pregunta: '¿Qué es el IVA?', idioma: 'es' })
  });
  const body = await response.json() as { respuesta?: string; error?: { code?: string } };
  assert.equal(response.status, 502);
  assert.equal(body.respuesta, undefined);
  assert.equal(body.error?.code, 'assistant_provider_failed');
});

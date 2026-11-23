import assert from 'node:assert/strict';
import { once } from 'node:events';
import { createServer } from 'node:http';
import { test } from 'node:test';

process.env.JWT_SECRET ??= 'test-secret-that-is-at-least-32-bytes-long';
const { app } = await import('../src/app.js');
const { createAccessToken } = await import('../src/auth/tokens.js');

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

test('learning feedback requires authentication', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const response = await fetch(`${server.baseUrl}/api/v1/learning/feedback`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ logs: [] })
  });
  assert.equal(response.status, 401);
});

test('learning feedback rejects invalid corrections before database access', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const token = await createAccessToken({
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  });
  const response = await fetch(`${server.baseUrl}/api/v1/learning/feedback`, {
    method: 'POST',
    headers: {
      authorization: `Bearer ${token}`,
      'content-type': 'application/json'
    },
    body: JSON.stringify({
      logs: [{
        eventId: 'not-a-uuid',
        textoOriginal: 'hola',
        textoCorregido: 'ola',
        intencion: 'AYUDA',
        fecha: Date.now()
      }]
    })
  });
  const body = await response.json() as { error?: { code?: string } };
  assert.equal(response.status, 400);
  assert.equal(body.error?.code, 'invalid_feedback');
});

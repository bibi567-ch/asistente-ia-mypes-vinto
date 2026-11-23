import assert from 'node:assert/strict';
import { once } from 'node:events';
import { test } from 'node:test';
import { createServer } from 'node:http';

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

test('voice command logs require authentication', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const response = await fetch(`${server.baseUrl}/api/v1/logs/comandos`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: '{}'
  });
  assert.equal(response.status, 401);
});

test('voice command logs reject invalid fields before database access', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const token = await createAccessToken({
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  });
  const response = await fetch(`${server.baseUrl}/api/v1/logs/comandos`, {
    method: 'POST',
    headers: {
      authorization: `Bearer ${token}`,
      'content-type': 'application/json'
    },
    body: JSON.stringify({ eventId: 'not-a-uuid' })
  });
  const body = await response.json() as { error?: { code?: string } };
  assert.equal(response.status, 400);
  assert.equal(body.error?.code, 'invalid_voice_log');
});

import assert from 'node:assert/strict';
import { once } from 'node:events';
import { test } from 'node:test';
import { createServer } from 'node:http';

process.env.JWT_SECRET ??= 'test-secret-that-is-at-least-32-bytes-long';
process.env.ASR_PROVIDER = '';
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

test('ASR transcription requires authentication', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const response = await fetch(`${server.baseUrl}/api/v1/asr/transcribe`, {
    method: 'POST',
    body: 'not-multipart'
  });
  assert.equal(response.status, 401);
});

test('ASR transcription reports missing provider configuration safely', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const token = await createAccessToken({
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  });
  const response = await fetch(`${server.baseUrl}/api/v1/asr/transcribe`, {
    method: 'POST',
    headers: { authorization: `Bearer ${token}`, 'content-type': 'application/json' },
    body: '{}'
  });
  const body = await response.json() as { error?: { code?: string } };
  assert.equal(response.status, 503);
  assert.equal(body.error?.code, 'asr_not_configured');
});

test('sales routes require authentication', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  for (const path of ['/api/v1/products', '/api/v1/transacciones/batch']) {
    const response = await fetch(`${server.baseUrl}${path}`);
    assert.equal(response.status, 401);
  }
});

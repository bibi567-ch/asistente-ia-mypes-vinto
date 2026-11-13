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

test('product catalog rejects requests without an access token', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const response = await fetch(`${server.baseUrl}/api/v1/products`);
  const body = await response.json() as { error?: { code?: string } };

  assert.equal(response.status, 401);
  assert.equal(body.error?.code, 'unauthorized');
});

test('cloud invoice, QR, and SSE endpoints reject requests without an access token', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const requests: Array<Promise<Response>> = [
    fetch(`${server.baseUrl}/api/v1/facturas`, {
      method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({ items: [{ name: 'Pan', quantity: 1, unitPrice: 1 }] })
    }),
    fetch(`${server.baseUrl}/api/v1/qr`, {
      method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({ qrBase64: 'aGVsbG8=' })
    }),
    fetch(`${server.baseUrl}/api/v1/sync/updates`)
  ];

  for (const response of await Promise.all(requests)) {
    const body = await response.json() as { error?: { code?: string } };
    assert.equal(response.status, 401);
    assert.equal(body.error?.code, 'unauthorized');
  }
});

test('cloud routes validate invoice and QR inputs before accessing PostgreSQL', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const token = await createAccessToken({
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  });
  const headers = { authorization: `Bearer ${token}`, 'content-type': 'application/json' };
  const invalidInvoice = await fetch(`${server.baseUrl}/api/v1/facturas`, {
    method: 'POST',
    headers,
    body: JSON.stringify({ items: [{ name: '', quantity: 0, unitPrice: -1 }] })
  });
  const invalidQr = await fetch(`${server.baseUrl}/api/v1/qr`, {
    method: 'POST',
    headers,
    body: JSON.stringify({ qrBase64: 'not base64!' })
  });

  assert.equal(invalidInvoice.status, 400);
  assert.equal((await invalidInvoice.json() as { error?: { code?: string } }).error?.code, 'invalid_invoice');
  assert.equal(invalidQr.status, 400);
  assert.equal((await invalidQr.json() as { error?: { code?: string } }).error?.code, 'invalid_qr');
});

test('product catalog validates prices before accessing PostgreSQL', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const token = await createAccessToken({
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  });
  const response = await fetch(`${server.baseUrl}/api/v1/products`, {
    method: 'POST',
    headers: { authorization: `Bearer ${token}`, 'content-type': 'application/json' },
    body: JSON.stringify({ name: 'Harina', unit: 'bolsa', salePrice: 12.5 })
  });
  const body = await response.json() as { error?: { code?: string } };

  assert.equal(response.status, 400);
  assert.equal(body.error?.code, 'invalid_product');
});

test('product sync validates cursor parameters before accessing PostgreSQL', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const token = await createAccessToken({
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  });
  const urls = [
    '/api/v1/products/sync?desde=0&cursorActualizadoEn=10',
    '/api/v1/products/sync?desde=0&cursorActualizadoEn=10&cursorId=not-a-uuid',
    '/api/v1/products/sync?desde=1.5'
  ];

  for (const path of urls) {
    const response = await fetch(`${server.baseUrl}${path}`, {
      headers: { authorization: `Bearer ${token}` }
    });
    const body = await response.json() as { error?: { code?: string } };
    assert.equal(response.status, 400);
    assert.equal(body.error?.code, 'invalid_since');
  }
});

test('password changes require a new password of at least 12 characters', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const token = await createAccessToken({
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  });
  const response = await fetch(`${server.baseUrl}/api/v1/auth/change-password`, {
    method: 'POST',
    headers: { authorization: `Bearer ${token}`, 'content-type': 'application/json' },
    body: JSON.stringify({ currentPassword: 'old-password', newPassword: 'short' })
  });
  const body = await response.json() as { error?: { code?: string } };

  assert.equal(response.status, 400);
  assert.equal(body.error?.code, 'invalid_password_change');
});

test('malformed JSON receives a client error response', async (context) => {
  const server = await startTestServer();
  context.after(server.close);
  const response = await fetch(`${server.baseUrl}/api/v1/auth/login`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: '{invalid json'
  });
  const body = await response.json() as { error?: { code?: string } };

  assert.equal(response.status, 400);
  assert.equal(body.error?.code, 'invalid_json');
});

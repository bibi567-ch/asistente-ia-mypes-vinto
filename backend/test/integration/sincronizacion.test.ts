import assert from 'node:assert/strict';
import { randomUUID } from 'node:crypto';
import { once } from 'node:events';
import { createServer } from 'node:http';
import { after, before, test } from 'node:test';
import { hashPassword } from '../../src/auth/password.js';

process.env.JWT_SECRET ??= 'integration-test-secret-that-is-at-least-32-bytes';
const { app } = await import('../../src/app.js');
const { pool } = await import('../../src/db/pool.js');

const email = `${randomUUID()}.sync@example.test`;
let businessId: string;
let server: ReturnType<typeof createServer>;
let baseUrl: string;
let token: string;

before(async () => {
  const business = await pool.query<{ id: string }>(
    'INSERT INTO businesses (name) VALUES ($1) RETURNING id',
    [`Sync Test ${email}`]
  );
  businessId = business.rows[0]!.id;
  await pool.query(
    'INSERT INTO users (business_id, email, password_hash, role) VALUES ($1, $2, $3, $4)',
    [businessId, email, await hashPassword('test-password-long-enough'), 'owner']
  );

  server = createServer(app);
  server.listen(0, '127.0.0.1');
  await once(server, 'listening');
  const address = server.address();
  assert.ok(address && typeof address !== 'string');
  baseUrl = `http://127.0.0.1:${address.port}`;

  const loginResponse = await fetch(`${baseUrl}/api/v1/auth/login`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ email, password: 'test-password-long-enough' })
  });
  assert.equal(loginResponse.status, 200);
  const loginBody = await loginResponse.json() as { accessToken: string };
  token = loginBody.accessToken;
});

after(async () => {
  if (server?.listening) {
    await new Promise<void>((resolve, reject) => {
      server.close((error) => error ? reject(error) : resolve());
    });
  }
  if (businessId) {
    await pool.query('DELETE FROM conflictos WHERE business_id = $1', [businessId]);
    await pool.query('DELETE FROM transacciones WHERE business_id = $1', [businessId]);
    await pool.query('DELETE FROM users WHERE business_id = $1', [businessId]);
    await pool.query('DELETE FROM businesses WHERE id = $1', [businessId]);
  }
  await pool.end();
});

test('POST /transacciones/batch acepta un lote válido y es idempotente', async () => {
  const operationId = randomUUID();
  const payload = {
    transacciones: [{
      operationId,
      tipo: 'VENTA',
      payload: {
        ventaId: randomUUID(),
        productoId: randomUUID(),
        cantidad: 3,
        precioUnitario: 5,
        total: 15,
        formaPago: 'CONTADO',
        creadoEn: Date.now()
      },
      schemaVersion: 1
    }]
  };

  const first = await postBatch(payload);
  assert.equal(first.response.status, 200);
  assert.equal(first.body.resultados[0]?.estado, 'ACEPTADA');

  const second = await postBatch(payload);
  assert.equal(second.response.status, 200);
  assert.equal(second.body.resultados[0]?.estado, 'DUPLICADA');
});

test('POST /transacciones/batch marca cantidades altas como conflicto', async () => {
  const response = await postBatch({
    transacciones: [{
      operationId: randomUUID(),
      tipo: 'VENTA',
      payload: {
        ventaId: randomUUID(),
        productoId: randomUUID(),
        cantidad: 101,
        precioUnitario: 1,
        total: 101,
        formaPago: 'CONTADO',
        creadoEn: Date.now()
      },
      schemaVersion: 1
    }]
  });

  assert.equal(response.response.status, 200);
  assert.equal(response.body.resultados[0]?.estado, 'CONFLICTO');
});

test('GET /productos/sync rechaza timestamps inválidos', async () => {
  const response = await fetch(`${baseUrl}/api/v1/products/sync?desde=-1`, {
    headers: { authorization: `Bearer ${token}` }
  });
  const body = await response.json() as { error: { code: string } };
  assert.equal(response.status, 400);
  assert.equal(body.error.code, 'invalid_since');
});

async function postBatch(payload: object): Promise<{
  response: Response;
  body: { resultados: Array<{ estado: string }> };
}> {
  const response = await fetch(`${baseUrl}/api/v1/transacciones/batch`, {
    method: 'POST',
    headers: {
      authorization: `Bearer ${token}`,
      'content-type': 'application/json'
    },
    body: JSON.stringify(payload)
  });
  return {
    response,
    body: await response.json() as { resultados: Array<{ estado: string }> }
  };
}

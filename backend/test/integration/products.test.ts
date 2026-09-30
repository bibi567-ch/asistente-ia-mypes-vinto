import assert from 'node:assert/strict';
import { randomUUID } from 'node:crypto';
import { once } from 'node:events';
import { createServer } from 'node:http';
import { after, before, test } from 'node:test';
import { hashPassword } from '../../src/auth/password.js';

process.env.JWT_SECRET ??= 'integration-test-secret-that-is-at-least-32-bytes';
const { app } = await import('../../src/app.js');
const { pool } = await import('../../src/db/pool.js');

const emailPrefix = randomUUID();
const emailA = `${emailPrefix}.a@example.test`;
const emailB = `${emailPrefix}.b@example.test`;
const businessIds: string[] = [];
let server: ReturnType<typeof createServer>;
let baseUrl: string;
let tokenA: string;
let tokenB: string;

before(async () => {
  for (const [name, email] of [['Business A', emailA], ['Business B', emailB]]) {
    const business = await pool.query<{ id: string }>(
      'INSERT INTO businesses (name) VALUES ($1) RETURNING id',
      [`${name} ${emailPrefix}`]
    );
    const businessId = business.rows[0]?.id;
    assert.ok(businessId);
    businessIds.push(businessId);
    await pool.query(
      'INSERT INTO users (business_id, email, password_hash, role) VALUES ($1, $2, $3, $4)',
      [businessId, email, await hashPassword('test-password-long-enough'), 'owner']
    );
  }

  server = createServer(app);
  server.listen(0, '127.0.0.1');
  await once(server, 'listening');
  const address = server.address();
  assert.ok(address && typeof address !== 'string');
  baseUrl = `http://127.0.0.1:${address.port}`;
  tokenA = await login(emailA);
  tokenB = await login(emailB);
});

after(async () => {
  if (server?.listening) {
    await new Promise<void>((resolve, reject) => {
      server.close((error) => error ? reject(error) : resolve());
    });
  }
  if (businessIds.length > 0) {
    await pool.query('DELETE FROM products WHERE business_id = ANY($1::uuid[])', [businessIds]);
    await pool.query('DELETE FROM users WHERE business_id = ANY($1::uuid[])', [businessIds]);
    await pool.query('DELETE FROM businesses WHERE id = ANY($1::uuid[])', [businessIds]);
  }
  await pool.end();
});

test('product operations stay within the business in the signed token', async () => {
  const createResponse = await fetch(`${baseUrl}/api/v1/products`, {
    method: 'POST',
    headers: { authorization: `Bearer ${tokenA}`, 'content-type': 'application/json' },
    body: JSON.stringify({
      name: 'Harina',
      unit: 'bolsa',
      salePrice: '12.50',
      businessId: businessIds[1]
    })
  });
  assert.equal(createResponse.status, 201);

  const listResponse = await fetch(`${baseUrl}/api/v1/products?businessId=${businessIds[1]}`, {
    headers: { authorization: `Bearer ${tokenA}` }
  });
  const listA = await listResponse.json() as { items: Array<{ name: string; salePrice: string }> };
  assert.equal(listResponse.status, 200);
  assert.deepEqual(listA.items.map(({ name }) => name), ['Harina']);
  assert.equal(listA.items[0]?.salePrice, '12.50');

  const otherBusinessResponse = await fetch(`${baseUrl}/api/v1/products`, {
    headers: { authorization: `Bearer ${tokenB}` }
  });
  const listB = await otherBusinessResponse.json() as { items: Array<{ name: string }> };
  assert.deepEqual(listB.items, []);
});

test('login issues a valid access token for each account', async () => {
  assert.ok(tokenA);
  assert.ok(tokenB);
  const response = await fetch(`${baseUrl}/api/v1/products`, {
    headers: { authorization: `Bearer ${tokenA}` }
  });
  assert.equal(response.status, 200);
});

async function login(email: string): Promise<string> {
  const response = await fetch(`${baseUrl}/api/v1/auth/login`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ email, password: 'test-password-long-enough' })
  });
  assert.equal(response.status, 200);
  const body = await response.json() as { accessToken?: string; tokenType?: string; expiresIn?: number };
  assert.ok(body.accessToken);
  assert.equal(body.tokenType, 'Bearer');
  assert.equal(body.expiresIn, 900);
  return body.accessToken;
}

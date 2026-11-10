import assert from 'node:assert/strict';
import { randomUUID } from 'node:crypto';
import { once } from 'node:events';
import { createServer } from 'node:http';
import { after, before, test } from 'node:test';

process.env.JWT_SECRET ??= 'integration-test-secret-that-is-at-least-32-bytes';
const { app } = await import('../../src/app.js');
const { pool } = await import('../../src/db/pool.js');

const emailPrefix = randomUUID();
const email = `owner.${emailPrefix}@example.test`;
const rejectedEmail = `invalid.${emailPrefix}@example.test`;
let server: ReturnType<typeof createServer>;
let baseUrl: string;

before(async () => {
  server = createServer(app);
  server.listen(0, '127.0.0.1');
  await once(server, 'listening');
  const address = server.address();
  assert.ok(address && typeof address !== 'string');
  baseUrl = `http://127.0.0.1:${address.port}`;
});

after(async () => {
  if (server?.listening) {
    await new Promise<void>((resolve, reject) => {
      server.close((error) => error ? reject(error) : resolve());
    });
  }

  const businesses = await pool.query<{ business_id: string }>(
    'SELECT business_id FROM users WHERE email = ANY($1::text[])',
    [[email, rejectedEmail]]
  );
  await pool.query('DELETE FROM users WHERE email = ANY($1::text[])', [[email, rejectedEmail]]);
  if (businesses.rows.length > 0) {
    await pool.query(
      'DELETE FROM businesses WHERE id = ANY($1::uuid[])',
      [businesses.rows.map(({ business_id }) => business_id)]
    );
  }
  await pool.end();
});

test('registration creates a usable owner account and rejects duplicate email', async () => {
  const payload = {
    nombre: 'Comerciante de prueba',
    carnet: '1234567',
    celular: '70000000',
    direccion: 'Zona de prueba',
    email: email.toUpperCase(),
    password: 'a-long-test-password',
    nombreNegocio: `Tienda ${emailPrefix}`,
    rubro: 'Abarrotes'
  };
  const response = await fetch(`${baseUrl}/api/v1/auth/register`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify(payload)
  });
  assert.equal(response.status, 201);

  const result = await response.json() as {
    accessToken?: string;
    tokenType?: string;
    expiresIn?: number;
  };
  assert.ok(result.accessToken);
  assert.equal(result.tokenType, 'Bearer');
  assert.equal(result.expiresIn, 900);

  const savedProfile = await pool.query<{
    email: string;
    nombre: string;
    carnet: string;
    celular: string;
    direccion: string;
    business_name: string;
    rubro: string;
  }>(
    `SELECT users.email, users.nombre, users.carnet, users.celular, users.direccion,
            businesses.name AS business_name, businesses.rubro
     FROM users JOIN businesses ON businesses.id = users.business_id
     WHERE users.email = $1`,
    [email]
  );
  assert.deepEqual(savedProfile.rows[0], {
    email,
    nombre: payload.nombre,
    carnet: payload.carnet,
    celular: payload.celular,
    direccion: payload.direccion,
    business_name: payload.nombreNegocio,
    rubro: payload.rubro
  });

  const productsResponse = await fetch(`${baseUrl}/api/v1/products`, {
    headers: { authorization: `Bearer ${result.accessToken}` }
  });
  assert.equal(productsResponse.status, 200);
  const products = await productsResponse.json() as { items: unknown[] };
  assert.deepEqual(products.items, []);

  const loginResponse = await fetch(`${baseUrl}/api/v1/auth/login`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ email, password: payload.password })
  });
  assert.equal(loginResponse.status, 200);

  const duplicateResponse = await fetch(`${baseUrl}/api/v1/auth/register`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify(payload)
  });
  assert.equal(duplicateResponse.status, 409);
  const duplicateBody = await duplicateResponse.json() as { error?: { code?: string } };
  assert.equal(duplicateBody.error?.code, 'email_already_registered');
  const businesses = await pool.query<{ count: number }>(
    'SELECT count(*)::integer AS count FROM businesses WHERE name = $1',
    [payload.nombreNegocio]
  );
  assert.equal(businesses.rows[0]?.count, 1);
});

test('registration validates input before creating account records', async () => {
  const response = await fetch(`${baseUrl}/api/v1/auth/register`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({
      email: rejectedEmail,
      password: 'short'
    })
  });
  assert.equal(response.status, 400);

  const user = await pool.query(
    'SELECT 1 FROM users WHERE email = $1',
    [rejectedEmail]
  );
  assert.equal(user.rowCount, 0);
});

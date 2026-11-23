import assert from 'node:assert/strict';
import { randomUUID } from 'node:crypto';
import { once } from 'node:events';
import { createServer } from 'node:http';
import { after, before, test } from 'node:test';
import { hashPassword } from '../../src/auth/password.js';

process.env.JWT_SECRET ??= 'integration-test-secret-that-is-at-least-32-bytes';
const { app } = await import('../../src/app.js');
const { pool } = await import('../../src/db/pool.js');

const email = `${randomUUID()}@learning.example.test`;
let businessId: string;
let server: ReturnType<typeof createServer>;
let baseUrl: string;
let token: string;

before(async () => {
  const business = await pool.query<{ id: string }>(
    'INSERT INTO businesses (name) VALUES ($1) RETURNING id',
    [`Learning Test ${email}`]
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

  const login = await fetch(`${baseUrl}/api/v1/auth/login`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ email, password: 'test-password-long-enough' })
  });
  assert.equal(login.status, 200);
  token = (await login.json() as { accessToken: string }).accessToken;
});

after(async () => {
  if (server?.listening) {
    await new Promise<void>((resolve, reject) => {
      server.close((error) => error ? reject(error) : resolve());
    });
  }
  if (businessId) {
    await pool.query('DELETE FROM voice_training_data WHERE business_id = $1', [businessId]);
    await pool.query('DELETE FROM users WHERE business_id = $1', [businessId]);
    await pool.query('DELETE FROM businesses WHERE id = $1', [businessId]);
  }
  await pool.end();
});

test('POST /learning/feedback persists opted-in corrections idempotently', async () => {
  const log = {
    eventId: randomUUID(),
    textoOriginal: 'cuatro arrobas de azucar',
    textoCorregido: 'cuatro arrobas de azúcar',
    intencion: 'CONSULTA_COGNITIVA',
    fecha: Date.now()
  };
  const first = await postFeedback(log);
  assert.equal(first.status, 200);

  const duplicate = await postFeedback(log);
  assert.equal(duplicate.status, 200);

  const stored = await pool.query<{ count: string }>(
    `SELECT count(*)::text AS count
     FROM voice_training_data
     WHERE business_id = $1 AND event_id = $2`,
    [businessId, log.eventId]
  );
  assert.equal(stored.rows[0]?.count, '1');
});

async function postFeedback(log: object): Promise<Response> {
  return fetch(`${baseUrl}/api/v1/learning/feedback`, {
    method: 'POST',
    headers: {
      authorization: `Bearer ${token}`,
      'content-type': 'application/json'
    },
    body: JSON.stringify({ logs: [log] })
  });
}

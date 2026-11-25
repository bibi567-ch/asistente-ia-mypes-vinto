import assert from 'node:assert/strict';
import { test } from 'node:test';
import { postgresSslConfig } from '../src/db/postgres-ssl.js';

test('production PostgreSQL TLS passes the CA without disabling validation', () => {
  const ca = '-----BEGIN CERTIFICATE-----\npublic-test-ca\n-----END CERTIFICATE-----';
  assert.deepEqual(postgresSslConfig('production', ca), {
    rejectUnauthorized: true,
    ca
  });
});

test('production PostgreSQL TLS remains strict without a custom CA', () => {
  assert.deepEqual(postgresSslConfig('production', ''), {
    rejectUnauthorized: true
  });
});

test('development does not enable PostgreSQL TLS by default', () => {
  assert.equal(postgresSslConfig('development', ''), undefined);
});

test('DATABASE_SSL_REJECT_UNAUTHORIZED=false disables validation explicitly', () => {
  assert.deepEqual(postgresSslConfig('production', '', 'false'), {
    rejectUnauthorized: false
  });
});

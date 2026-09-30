import assert from 'node:assert/strict';
import { test } from 'node:test';
import { hashPassword, verifyPassword } from '../src/auth/password.js';

process.env.JWT_SECRET ??= 'test-secret-that-is-at-least-32-bytes-long';
const { createAccessToken, verifyAccessToken } = await import('../src/auth/tokens.js');

test('password hashes verify only the original password', async () => {
  const hash = await hashPassword('a-long-test-password');

  assert.notEqual(hash, 'a-long-test-password');
  assert.equal(await verifyPassword('a-long-test-password', hash), true);
  assert.equal(await verifyPassword('a-different-password', hash), false);
});

test('invalid password hashes are rejected', async () => {
  assert.equal(await verifyPassword('password', 'not-a-password-hash'), false);
});

test('access tokens preserve their user and business claims', async () => {
  const claims = {
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  };
  const token = await createAccessToken(claims);

  assert.deepEqual(await verifyAccessToken(token), claims);
  assert.equal(await verifyAccessToken(`${token}x`), null);
});

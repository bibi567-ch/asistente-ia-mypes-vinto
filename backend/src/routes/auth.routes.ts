import { randomBytes } from 'node:crypto';
import { Router } from 'express';
import { hashPassword, verifyPassword } from '../auth/password.js';
import { requireAuth } from '../auth/require-auth.js';
import { accessTokenLifetimeSeconds, createAccessToken } from '../auth/tokens.js';
import { pool } from '../db/pool.js';

export const authRouter = Router();
const dummyPasswordHash = hashPassword(randomBytes(32).toString('base64url'));

authRouter.post('/login', async (request, response) => {
  const { email, password } = request.body ?? {};
  if (
    typeof email !== 'string'
    || email.trim().length < 3
    || typeof password !== 'string'
    || email.length > 254
    || Buffer.byteLength(password, 'utf8') > 1024
  ) {
    response.status(400).json({ error: { code: 'invalid_credentials_format', message: 'Email and password are required.' } });
    return;
  }

  const normalizedEmail = email.trim().toLowerCase();
  const result = await pool.query<{
    id: string;
    business_id: string;
    password_hash: string;
  }>(
    'SELECT id, business_id, password_hash FROM users WHERE email = $1',
    [normalizedEmail]
  );
  const user = result.rows[0];
  const storedHash = user?.password_hash ?? await dummyPasswordHash;
  const passwordIsValid = await verifyPassword(password, storedHash);
  if (!user || !passwordIsValid) {
    response.status(401).json({ error: { code: 'invalid_credentials', message: 'Email or password is incorrect.' } });
    return;
  }

  const accessToken = await createAccessToken({ userId: user.id, businessId: user.business_id });
  response.status(200).json({
    accessToken,
    tokenType: 'Bearer',
    expiresIn: accessTokenLifetimeSeconds
  });
});

authRouter.post('/change-password', requireAuth, async (request, response) => {
  const { currentPassword, newPassword } = request.body ?? {};
  const userId = request.auth?.userId;
  const businessId = request.auth?.businessId;
  if (
    !userId
    || !businessId
    || typeof currentPassword !== 'string'
    || typeof newPassword !== 'string'
    || currentPassword.length > 1024
    || Buffer.byteLength(newPassword, 'utf8') > 1024
    || newPassword.length < 12
    || newPassword.length > 256
  ) {
    response.status(400).json({
      error: { code: 'invalid_password_change', message: 'Provide the current password and a new password of at least 12 characters.' }
    });
    return;
  }

  const user = await pool.query<{ password_hash: string }>(
    'SELECT password_hash FROM users WHERE id = $1 AND business_id = $2',
    [userId, businessId]
  );
  const passwordHash = user.rows[0]?.password_hash;
  if (!passwordHash || !await verifyPassword(currentPassword, passwordHash)) {
    response.status(401).json({ error: { code: 'invalid_current_password', message: 'The current password is incorrect.' } });
    return;
  }
  if (await verifyPassword(newPassword, passwordHash)) {
    response.status(400).json({ error: { code: 'password_unchanged', message: 'Choose a password different from the current password.' } });
    return;
  }

  const updated = await pool.query(
    'UPDATE users SET password_hash = $1 WHERE id = $2 AND business_id = $3',
    [await hashPassword(newPassword), userId, businessId]
  );
  if (updated.rowCount !== 1) {
    response.status(401).json({ error: { code: 'unauthorized', message: 'The authenticated account no longer exists.' } });
    return;
  }
  response.status(204).end();
});

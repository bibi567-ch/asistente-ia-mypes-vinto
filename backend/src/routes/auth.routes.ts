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

authRouter.post('/register', async (request, response) => {
  const body: unknown = request.body;
  if (typeof body !== 'object' || body === null || Array.isArray(body)) {
    response.status(400).json({ error: { code: 'invalid_registration', message: 'Provide all required registration fields.' } });
    return;
  }

  const fields = body as Record<string, unknown>;
  const {
    nombre,
    nombres,
    apellidos,
    carnet,
    expedido,
    fechaNacimiento,
    lugarNacimiento,
    celular,
    direccion,
    email,
    password,
    nombreNegocio,
    rubro
  } = fields;
  if (
    typeof nombre !== 'string'
    || nombre.trim().length < 1
    || nombre.trim().length > 160
    || (nombres !== undefined && (
      typeof nombres !== 'string'
      || nombres.trim().length < 1
      || nombres.trim().length > 80
    ))
    || (apellidos !== undefined && (
      typeof apellidos !== 'string'
      || apellidos.trim().length < 1
      || apellidos.trim().length > 100
    ))
    || typeof carnet !== 'string'
    || carnet.trim().length < 1
    || carnet.trim().length > 40
    || (expedido !== undefined && (
      typeof expedido !== 'string'
      || expedido.trim().length < 1
      || expedido.trim().length > 40
    ))
    || (fechaNacimiento !== undefined && (
      typeof fechaNacimiento !== 'string'
      || fechaNacimiento.trim().length < 1
      || fechaNacimiento.trim().length > 32
    ))
    || (lugarNacimiento !== undefined && (
      typeof lugarNacimiento !== 'string'
      || lugarNacimiento.trim().length < 1
      || lugarNacimiento.trim().length > 120
    ))
    || typeof celular !== 'string'
    || celular.trim().length < 1
    || celular.trim().length > 40
    || (direccion !== undefined && (
      typeof direccion !== 'string'
      || direccion.trim().length > 240
    ))
    || typeof email !== 'string'
    || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())
    || email.trim().length > 254
    || typeof password !== 'string'
    || password.length < 12
    || password.length > 256
    || Buffer.byteLength(password, 'utf8') > 1024
    || (nombreNegocio !== undefined && nombreNegocio !== null && (
      typeof nombreNegocio !== 'string'
      || nombreNegocio.trim().length > 160
    ))
    || (rubro !== undefined && rubro !== null && (
      typeof rubro !== 'string'
      || rubro.trim().length > 80
    ))
  ) {
    response.status(400).json({ error: { code: 'invalid_registration', message: 'Check the required fields and password length (12 to 256 characters).' } });
    return;
  }

  const client = await pool.connect();
  let transactionOpen = false;
  try {
    await client.query('BEGIN');
    transactionOpen = true;
    const businessResult = await client.query<{ id: string }>(
      'INSERT INTO businesses (name, rubro) VALUES ($1, $2) RETURNING id',
      [
        typeof nombreNegocio === 'string' && nombreNegocio.trim()
          ? nombreNegocio.trim()
          : `Negocio de ${nombre.trim()}`.slice(0, 160),
        typeof rubro === 'string' && rubro.trim() ? rubro.trim() : null
      ]
    );
    const businessId = businessResult.rows[0]?.id;
    if (!businessId) throw new Error('Business insert did not return an identifier.');

    const normalizedEmail = email.trim().toLowerCase();
    const userResult = await client.query<{ id: string }>(
      `INSERT INTO users (
         business_id, email, password_hash, role, nombre, nombres, apellidos, carnet,
         expedido, fecha_nacimiento, lugar_nacimiento, celular, direccion
       ) VALUES ($1, $2, $3, 'owner', $4, $5, $6, $7, $8, $9, $10, $11, $12) RETURNING id`,
      [
        businessId,
        normalizedEmail,
        await hashPassword(password),
        nombre.trim(),
        typeof nombres === 'string' ? nombres.trim() : nombre.trim(),
        typeof apellidos === 'string' ? apellidos.trim() : null,
        carnet.trim(),
        typeof expedido === 'string' ? expedido.trim() : null,
        typeof fechaNacimiento === 'string' ? fechaNacimiento.trim() : null,
        typeof lugarNacimiento === 'string' ? lugarNacimiento.trim() : null,
        celular.trim(),
        typeof direccion === 'string' && direccion.trim() ? direccion.trim() : null
      ]
    );
    const userId = userResult.rows[0]?.id;
    if (!userId) throw new Error('User insert did not return an identifier.');

    const accessToken = await createAccessToken({ userId, businessId });
    await client.query('COMMIT');
    transactionOpen = false;
    response.status(201).json({
      accessToken,
      tokenType: 'Bearer',
      expiresIn: accessTokenLifetimeSeconds
    });
  } catch (error) {
    if (transactionOpen) {
      try {
        await client.query('ROLLBACK');
      } catch {
        console.error('Registration transaction rollback failed.');
      }
    }

    if (isUniqueViolation(error)) {
      response.status(409).json({ error: { code: 'email_already_registered', message: 'An account with that email already exists.' } });
      return;
    }

    console.error('Account registration failed.');
    response.status(500).json({ error: { code: 'register_failed', message: 'The account could not be registered.' } });
  } finally {
    client.release();
  }
});

authRouter.put('/business', requireAuth, async (request, response) => {
  const { nombreNegocio, rubro } = request.body ?? {};
  const businessId = request.auth?.businessId;
  if (
    !businessId
    || typeof nombreNegocio !== 'string'
    || nombreNegocio.trim().length < 1
    || nombreNegocio.trim().length > 160
    || typeof rubro !== 'string'
    || rubro.trim().length < 1
    || rubro.trim().length > 80
  ) {
    response.status(400).json({
      error: { code: 'invalid_business_profile', message: 'Provide a business name and category.' }
    });
    return;
  }

  const result = await pool.query(
    'UPDATE businesses SET name = $1, rubro = $2 WHERE id = $3',
    [nombreNegocio.trim(), rubro.trim(), businessId]
  );
  if (result.rowCount !== 1) {
    response.status(404).json({ error: { code: 'business_not_found', message: 'The business no longer exists.' } });
    return;
  }
  response.status(204).end();
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

function isUniqueViolation(error: unknown): boolean {
  return typeof error === 'object'
    && error !== null
    && 'code' in error
    && error.code === '23505';
}

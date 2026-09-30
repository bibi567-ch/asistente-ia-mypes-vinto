import { randomBytes } from 'node:crypto';
import { hashPassword } from '../auth/password.js';
import { pool } from '../db/pool.js';

async function createOwner(): Promise<void> {
  const [businessNameArgument, emailArgument, ...extraArguments] = process.argv.slice(2);
  const businessName = businessNameArgument?.trim();
  const email = emailArgument?.trim().toLowerCase();

  if (
    extraArguments.length > 0
    || !businessName
    || businessName.length > 160
    || !email
    || email.length > 254
    || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
  ) {
    throw new Error('Usage: npm run admin:create-owner -- "<business name>" "<owner email>"');
  }

  const temporaryPassword = randomBytes(32).toString('base64url');
  const passwordHash = await hashPassword(temporaryPassword);
  const client = await pool.connect();
  let transactionOpen = false;

  try {
    await client.query('BEGIN');
    transactionOpen = true;
    const business = await client.query<{ id: string }>(
      'INSERT INTO businesses (name) VALUES ($1) RETURNING id',
      [businessName]
    );
    const businessId = business.rows[0]?.id;
    if (!businessId) throw new Error('Business creation did not return an identifier.');

    await client.query(
      'INSERT INTO users (business_id, email, password_hash, role) VALUES ($1, $2, $3, $4)',
      [businessId, email, passwordHash, 'owner']
    );
    await client.query('COMMIT');
    transactionOpen = false;
    console.info(`Owner created for business ${businessId} (${email}).`);
    console.info(`Temporary password (shown once): ${temporaryPassword}`);
    console.info('Sign in and store this credential securely; no copy is retained by the command.');
  } catch (error) {
    if (transactionOpen) await client.query('ROLLBACK');
    if (isUniqueViolation(error)) throw new Error('An account with that email already exists.');
    throw error;
  } finally {
    client.release();
    await pool.end();
  }
}

function isUniqueViolation(error: unknown): boolean {
  return typeof error === 'object'
    && error !== null
    && 'code' in error
    && error.code === '23505';
}

createOwner().catch((error: unknown) => {
  console.error(error instanceof Error ? error.message : 'Owner creation failed.');
  process.exitCode = 1;
});

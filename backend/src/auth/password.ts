import { randomBytes, scrypt, timingSafeEqual } from 'node:crypto';

const SCRYPT_COST = 32_768;
const SCRYPT_BLOCK_SIZE = 8;
const SCRYPT_PARALLELIZATION = 1;
const KEY_LENGTH = 64;
const MAX_MEMORY = 64 * 1024 * 1024;
const MAX_PASSWORD_BYTES = 1024;

function deriveKey(password: string, salt: Buffer): Promise<Buffer> {
  return new Promise((resolve, reject) => {
    scrypt(
      password,
      salt,
      KEY_LENGTH,
      {
        N: SCRYPT_COST,
        r: SCRYPT_BLOCK_SIZE,
        p: SCRYPT_PARALLELIZATION,
        maxmem: MAX_MEMORY
      },
      (error, derivedKey) => error ? reject(error) : resolve(derivedKey)
    );
  });
}

export async function hashPassword(password: string): Promise<string> {
  if (Buffer.byteLength(password, 'utf8') > MAX_PASSWORD_BYTES) {
    throw new Error('Password exceeds the maximum supported length.');
  }
  const salt = randomBytes(16);
  const derivedKey = await deriveKey(password, salt);
  return [
    'scrypt',
    SCRYPT_COST,
    SCRYPT_BLOCK_SIZE,
    SCRYPT_PARALLELIZATION,
    salt.toString('base64url'),
    derivedKey.toString('base64url')
  ].join('$');
}

export async function verifyPassword(password: string, storedHash: string): Promise<boolean> {
  if (Buffer.byteLength(password, 'utf8') > MAX_PASSWORD_BYTES) return false;
  const [algorithm, cost, blockSize, parallelization, saltText, keyText, ...extra] = storedHash.split('$');

  if (
    algorithm !== 'scrypt'
    || Number(cost) !== SCRYPT_COST
    || Number(blockSize) !== SCRYPT_BLOCK_SIZE
    || Number(parallelization) !== SCRYPT_PARALLELIZATION
    || !saltText
    || !keyText
    || extra.length > 0
  ) {
    return false;
  }

  const salt = Buffer.from(saltText, 'base64url');
  const expectedKey = Buffer.from(keyText, 'base64url');
  if (salt.length !== 16 || expectedKey.length !== KEY_LENGTH) return false;

  const actualKey = await deriveKey(password, salt);
  return timingSafeEqual(expectedKey, actualKey);
}

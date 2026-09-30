import pg from 'pg';
import { env } from '../config/env.js';

const { Pool } = pg;

export const pool = new Pool({
  connectionString: env.databaseUrl,
  max: 5,
  idleTimeoutMillis: 30_000,
  connectionTimeoutMillis: 5_000,
  ...(env.nodeEnv === 'production' ? { ssl: { rejectUnauthorized: true } } : {})
});

pool.on('error', () => {
  // Do not print connection details or secrets to logs.
  console.error('Unexpected idle PostgreSQL client error.');
});

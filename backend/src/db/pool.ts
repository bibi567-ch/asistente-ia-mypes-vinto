import pg from 'pg';
import { env } from '../config/env.js';
import { postgresSslConfig } from './postgres-ssl.js';

const { Pool } = pg;
const ssl = postgresSslConfig(env.nodeEnv, env.databaseSslCa, process.env.DATABASE_SSL_REJECT_UNAUTHORIZED);

export const pool = new Pool({
  connectionString: env.databaseUrl,
  max: 5,
  idleTimeoutMillis: 30_000,
  connectionTimeoutMillis: 5_000,
  ...(ssl ? { ssl } : {})
});

pool.on('error', () => {
  // Do not print connection details or secrets to logs.
  console.error('Unexpected idle PostgreSQL client error.');
});

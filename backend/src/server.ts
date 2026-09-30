import { app } from './app.js';
import { env } from './config/env.js';
import { pool } from './db/pool.js';

async function start(): Promise<void> {
  await pool.query('SELECT 1');
  app.listen(env.port, env.host, () => {
    console.log(`MYPES Vinto API listening on port ${env.port}`);
  });
}

start().catch((error: unknown) => {
  console.error('API startup failed. Check DATABASE_URL and PostgreSQL availability.');
  process.exitCode = 1;
});

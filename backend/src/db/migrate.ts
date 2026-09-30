import { readdir, readFile } from 'node:fs/promises';
import { join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { pool } from './pool.js';

const migrationsDirectory = fileURLToPath(new URL('../../db/migrations/', import.meta.url));

async function migrate(): Promise<void> {
  const files = (await readdir(migrationsDirectory))
    .filter((file) => /^\d{3}_[a-z0-9_]+\.sql$/.test(file))
    .sort();
  const client = await pool.connect();

  try {
    for (const file of files) {
      await client.query('BEGIN');
      try {
        await client.query('SELECT pg_advisory_xact_lock($1::bigint)', ['71042026']);
        await client.query(`
          CREATE TABLE IF NOT EXISTS schema_migrations (
            migration_name TEXT PRIMARY KEY,
            applied_at TIMESTAMPTZ NOT NULL DEFAULT now()
          )
        `);
        const existing = await client.query(
          'SELECT 1 FROM schema_migrations WHERE migration_name = $1',
          [file]
        );

        if (existing.rows.length === 0) {
          const sql = await readFile(join(migrationsDirectory, file), 'utf8');
          await client.query(sql);
          await client.query('INSERT INTO schema_migrations (migration_name) VALUES ($1)', [file]);
        }

        await client.query('COMMIT');
        if (existing.rows.length === 0) console.info(`Applied migration ${file}`);
      } catch (error) {
        await client.query('ROLLBACK');
        throw error;
      }
    }
    console.info(`Database migrations are up to date (${files.length} checked).`);
  } finally {
    client.release();
    await pool.end();
  }
}

migrate().catch((error: unknown) => {
  console.error('Database migration failed; the current migration was rolled back.');
  if (error instanceof Error) console.error(error.message);
  process.exitCode = 1;
});

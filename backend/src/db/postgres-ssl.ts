import type { PoolConfig } from 'pg';

export function postgresSslConfig(
  nodeEnv: string,
  databaseSslCa: string
): PoolConfig['ssl'] {
  if (nodeEnv !== 'production') return undefined;

  return {
    rejectUnauthorized: true,
    ...(databaseSslCa ? { ca: databaseSslCa } : {})
  };
}

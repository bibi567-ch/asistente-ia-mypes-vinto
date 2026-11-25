import type { PoolConfig } from 'pg';

export function postgresSslConfig(
  nodeEnv: string,
  databaseSslCa: string,
  rejectUnauthorized = true
): PoolConfig['ssl'] {
  if (nodeEnv !== 'production') return undefined;

  return {
    rejectUnauthorized,
    ...(databaseSslCa ? { ca: databaseSslCa } : {})
  };
}

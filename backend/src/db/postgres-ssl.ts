import type { PoolConfig } from 'pg';

export function postgresSslConfig(
  nodeEnv: string,
  databaseSslCa: string,
  rejectUnauthorizedEnv = ''
): PoolConfig['ssl'] {
  if (nodeEnv !== 'production') return undefined;

  return {
    rejectUnauthorized: rejectUnauthorizedEnv.trim().toLowerCase() !== 'false',
    ...(databaseSslCa ? { ca: databaseSslCa } : {})
  };
}

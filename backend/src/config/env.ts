import 'dotenv/config';

function required(name: string, fallback?: string): string {
  const value = process.env[name] ?? fallback;
  if (!value) throw new Error(`Missing required environment variable: ${name}`);
  return value;
}

export const env = {
  nodeEnv: process.env.NODE_ENV ?? 'development',
  port: Number(process.env.PORT ?? 3000),
  host: process.env.HOST ?? '127.0.0.1',
  jwtSecret: required('JWT_SECRET'),
  databaseUrl: required('DATABASE_URL'),
  asrProvider: process.env.ASR_PROVIDER?.trim().toLowerCase() || '',
  googleAsrApiKey: process.env.GOOGLE_ASR_API_KEY?.trim() || '',
  googleAsrLanguage: process.env.GOOGLE_ASR_LANGUAGE?.trim() || 'es-ES',
  openaiAsrApiKey: process.env.OPENAI_ASR_API_KEY?.trim() || '',
  openaiAsrBaseUrl: process.env.OPENAI_ASR_BASE_URL?.trim() || 'https://api.openai.com/v1',
  databaseSslCa: process.env.DATABASE_SSL_CA?.replace(/\\n/g, '\n').trim() || '',
  databaseSslRejectUnauthorized: process.env.DATABASE_SSL_REJECT_UNAUTHORIZED?.trim().toLowerCase() !== 'false',
  androidUpdateVersionCode: Number(process.env.ANDROID_UPDATE_VERSION_CODE ?? 0),
  androidUpdateVersionName: process.env.ANDROID_UPDATE_VERSION_NAME?.trim() || '',
  androidUpdateApkPath: process.env.ANDROID_UPDATE_APK_PATH?.trim() || '',
  androidUpdateBaseUrl: process.env.ANDROID_UPDATE_BASE_URL?.trim() || ''
};

if (!Number.isInteger(env.port) || env.port < 1 || env.port > 65535) {
  throw new Error('PORT must be an integer between 1 and 65535');
}

if (Buffer.byteLength(env.jwtSecret, 'utf8') < 32) {
  throw new Error('JWT_SECRET must contain at least 32 bytes');
}

if (!Number.isInteger(env.androidUpdateVersionCode) || env.androidUpdateVersionCode < 0) {
  throw new Error('ANDROID_UPDATE_VERSION_CODE must be a non-negative integer');
}

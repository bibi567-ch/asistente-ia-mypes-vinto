/**
 * Configuración central del prototipo (variables de entorno con valores de desarrollo).
 * En producción JWT_SECRET debe venir de un gestor de secretos, nunca del código.
 */
export const config = {
  gatewayPort: Number(process.env.GATEWAY_PORT ?? 8080),
  authPort: Number(process.env.AUTH_PORT ?? 3001),
  syncPort: Number(process.env.SYNC_PORT ?? 4000),
  iaPort: Number(process.env.IA_PORT ?? 5000),
  authUrl: process.env.AUTH_URL ?? 'http://localhost:3001',
  syncUrl: process.env.SYNC_URL ?? 'http://localhost:4000',
  iaUrl: process.env.IA_URL ?? 'http://localhost:5000',
  jwtSecret: process.env.JWT_SECRET ?? 'solo-desarrollo-cambiar-en-produccion-32b',
  breaker: {
    timeout: Number(process.env.CB_TIMEOUT_MS ?? 2000),               // corta llamadas lentas
    errorThresholdPercentage: Number(process.env.CB_ERROR_PCT ?? 50), // % de fallos para abrir
    resetTimeout: Number(process.env.CB_RESET_MS ?? 10000),           // tiempo abierto antes de probar
    volumeThreshold: Number(process.env.CB_VOLUME ?? 4),              // mínimo de llamadas para evaluar
    rollingCountTimeout: Number(process.env.CB_WINDOW_MS ?? 10000),   // ventana estadística
  },
  rateLimitPorMinuto: Number(process.env.RATE_LIMIT ?? 120),
};
export type BreakerConfig = typeof config.breaker;

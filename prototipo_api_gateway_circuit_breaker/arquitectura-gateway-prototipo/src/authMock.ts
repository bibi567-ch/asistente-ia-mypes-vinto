/**
 * Servicio de autenticación simulado (mock). Emite JWT HS256 de 15 minutos.
 * Los usuarios son de demostración; el backend real usa scrypt y PostgreSQL.
 */
import express from 'express';
import jwt from 'jsonwebtoken';
import { config } from './config';

const USUARIOS: Record<string, { password: string; rol: 'comerciante' | 'admin' }> = {
  comerciante: { password: 'vinto2026', rol: 'comerciante' },
  admin: { password: 'admin2026', rol: 'admin' },
};

export function createAuthService(jwtSecret = config.jwtSecret) {
  const app = express();
  app.use(express.json());
  app.get('/health', (_req, res) => res.json({ servicio: 'auth', estado: 'ok' }));
  app.post('/login', (req, res) => {
    const { usuario, password } = req.body ?? {};
    const u = USUARIOS[usuario];
    if (!u || u.password !== password) {
      return res.status(401).json({ error: 'credenciales_invalidas' });
    }
    const token = jwt.sign({ sub: usuario, rol: u.rol }, jwtSecret, { algorithm: 'HS256', expiresIn: '15m' });
    return res.json({ token, expiraEn: 900, rol: u.rol });
  });
  return app;
}

if (require.main === module) {
  createAuthService().listen(config.authPort, () => console.log(`[auth] escuchando en :${config.authPort}`));
}

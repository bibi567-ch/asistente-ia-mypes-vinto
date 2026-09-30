import type { RequestHandler } from 'express';
import { verifyAccessToken } from './tokens.js';

declare global {
  namespace Express {
    interface Request {
      auth?: {
        userId: string;
        businessId: string;
      };
    }
  }
}

export const requireAuth: RequestHandler = async (request, response, next) => {
  const authorization = request.header('authorization');
  const match = authorization?.match(/^Bearer ([^\s]+)$/);
  if (!match) {
    response.status(401).json({ error: { code: 'unauthorized', message: 'Authentication is required.' } });
    return;
  }

  const claims = await verifyAccessToken(match[1]);
  if (!claims) {
    response.status(401).json({ error: { code: 'unauthorized', message: 'The access token is invalid or expired.' } });
    return;
  }

  request.auth = claims;
  next();
};

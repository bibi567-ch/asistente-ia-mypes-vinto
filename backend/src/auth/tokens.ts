import { errors, SignJWT, jwtVerify } from 'jose';
import { env } from '../config/env.js';

const ISSUER = 'mypes-vinto-api';
const AUDIENCE = 'mypes-vinto-client';
const TOKEN_LIFETIME_SECONDS = 900;
const signingKey = new TextEncoder().encode(env.jwtSecret);

export interface AuthClaims {
  userId: string;
  businessId: string;
}

export async function createAccessToken(claims: AuthClaims): Promise<string> {
  return new SignJWT({ businessId: claims.businessId })
    .setProtectedHeader({ alg: 'HS256', typ: 'JWT' })
    .setSubject(claims.userId)
    .setIssuer(ISSUER)
    .setAudience(AUDIENCE)
    .setIssuedAt()
    .setExpirationTime(`${TOKEN_LIFETIME_SECONDS}s`)
    .sign(signingKey);
}

export async function verifyAccessToken(token: string): Promise<AuthClaims | null> {
  try {
    const { payload } = await jwtVerify(token, signingKey, {
      algorithms: ['HS256'],
      issuer: ISSUER,
      audience: AUDIENCE
    });
    if (!payload.sub || typeof payload.businessId !== 'string') return null;
    return { userId: payload.sub, businessId: payload.businessId };
  } catch (error) {
    if (error instanceof errors.JOSEError) return null;
    throw error;
  }
}

export const accessTokenLifetimeSeconds = TOKEN_LIFETIME_SECONDS;

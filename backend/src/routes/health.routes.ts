import { Router } from 'express';
import { pool } from '../db/pool.js';

export const healthRouter = Router();

healthRouter.get('/', async (_request, response) => {
  try {
    await pool.query('SELECT 1');
    response.status(200).json({ status: 'ok', service: 'mypes-vinto-api', database: 'connected' });
  } catch {
    console.warn('Health check failed: PostgreSQL is unavailable.');
    response.status(503).json({ status: 'degraded', service: 'mypes-vinto-api', database: 'unavailable' });
  }
});

import express from 'express';
import { authRouter } from './routes/auth.routes.js';
import { healthRouter } from './routes/health.routes.js';
import { productsRouter } from './routes/products.routes.js';

export const app = express();
app.disable('x-powered-by');
app.use(express.json({ limit: '100kb' }));
app.use('/api/v1/health', healthRouter);
app.use('/api/v1/auth', authRouter);
app.use('/api/v1/products', productsRouter);

app.use((_request, response) => {
  response.status(404).json({ error: { code: 'not_found', message: 'The requested endpoint does not exist.' } });
});

app.use((error: unknown, _request: express.Request, response: express.Response, _next: express.NextFunction) => {
  if (
    typeof error === 'object'
    && error !== null
    && 'type' in error
    && error.type === 'entity.parse.failed'
  ) {
    response.status(400).json({ error: { code: 'invalid_json', message: 'The request body must be valid JSON.' } });
    return;
  }

  if (
    typeof error === 'object'
    && error !== null
    && 'type' in error
    && error.type === 'entity.too.large'
  ) {
    response.status(413).json({ error: { code: 'payload_too_large', message: 'The request body is too large.' } });
    return;
  }

  console.error('Unhandled API error.');
  response.status(500).json({ error: { code: 'internal_error', message: 'An unexpected error occurred.' } });
});

import assert from 'node:assert/strict';
import { once } from 'node:events';
import { createServer } from 'node:http';
import { test } from 'node:test';

process.env.JWT_SECRET ??= 'test-secret-that-is-at-least-32-bytes-long';
process.env.ASR_PROVIDER = 'simulated';
const { app } = await import('../src/app.js');
const { createAccessToken } = await import('../src/auth/tokens.js');

test('simulated ASR reports that audio was not actually transcribed', async (context) => {
  const server = createServer(app);
  server.listen(0, '127.0.0.1');
  await once(server, 'listening');
  context.after(() => new Promise<void>((resolve, reject) => {
    server.close((error) => error ? reject(error) : resolve());
  }));
  const address = server.address();
  assert.ok(address && typeof address !== 'string');
  const token = await createAccessToken({
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  });
  const boundary = 'vinto-test-boundary';
  const body = [
    `--${boundary}`,
    'Content-Disposition: form-data; name="audio"; filename="voice.wav"',
    'Content-Type: audio/wav',
    '',
    'sample-audio',
    `--${boundary}--`,
    ''
  ].join('\r\n');
  const response = await fetch(`http://127.0.0.1:${address.port}/api/v1/asr/transcribe`, {
    method: 'POST',
    headers: {
      authorization: `Bearer ${token}`,
      'content-type': `multipart/form-data; boundary=${boundary}`
    },
    body
  });
  const result = await response.json() as { texto?: string; simulada?: boolean; mensaje?: string };
  assert.equal(response.status, 200);
  assert.equal(result.texto, '');
  assert.equal(result.simulada, true);
  assert.match(result.mensaje ?? '', /no se transcribió/);
});

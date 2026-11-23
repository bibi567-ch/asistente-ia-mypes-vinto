import assert from 'node:assert/strict';
import { once } from 'node:events';
import { test } from 'node:test';
import { createServer } from 'node:http';

process.env.JWT_SECRET ??= 'test-secret-that-is-at-least-32-bytes-long';
process.env.ASR_PROVIDER = 'google';
process.env.GOOGLE_ASR_API_KEY = 'test-key';

const realFetch = globalThis.fetch;
let calledGoogle = false;
globalThis.fetch = async (input, init) => {
  if (typeof input === 'string' && input.startsWith('https://speech.googleapis.com/')) {
    calledGoogle = true;
    assert.equal(init?.method, 'POST');
    return Response.json({
      results: [{ alternatives: [{ transcript: 'pon una alarma' }] }]
    });
  }
  return realFetch(input, init);
};

const { app } = await import('../src/app.js');
const { createAccessToken } = await import('../src/auth/tokens.js');

async function startTestServer(): Promise<{ baseUrl: string; close: () => Promise<void> }> {
  const server = createServer(app);
  server.listen(0, '127.0.0.1');
  await once(server, 'listening');
  const address = server.address();
  assert.ok(address && typeof address !== 'string');
  return {
    baseUrl: `http://127.0.0.1:${address.port}`,
    close: () => new Promise<void>((resolve, reject) => {
      server.close((error) => error ? reject(error) : resolve());
    })
  };
}

test('ASR endpoint forwards audio and returns the provider transcription', async (context) => {
  const server = await startTestServer();
  context.after(() => {
    globalThis.fetch = realFetch;
    return server.close();
  });
  const token = await createAccessToken({
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  });
  const audio = new FormData();
  audio.append('audio', new Blob([new Uint8Array([0, 0, 0, 0])], { type: 'audio/wav' }), 'voice.wav');
  const response = await realFetch(`${server.baseUrl}/api/v1/asr/transcribe`, {
    method: 'POST',
    headers: { authorization: `Bearer ${token}` },
    body: audio
  });
  const body = await response.json() as { texto?: string };
  assert.equal(response.status, 200);
  assert.equal(body.texto, 'pon una alarma');
  assert.equal(calledGoogle, true);
});

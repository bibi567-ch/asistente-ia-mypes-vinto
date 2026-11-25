import assert from 'node:assert/strict';
import { once } from 'node:events';
import { test } from 'node:test';
import { createServer } from 'node:http';

process.env.JWT_SECRET ??= 'test-secret-that-is-at-least-32-bytes-long';
process.env.ASR_PROVIDER = 'quechua';
process.env.QUECHUA_ASR_API_URL = 'https://quechua-asr.example.test/transcribe';
process.env.QUECHUA_ASR_API_TOKEN = 'test-asr-token';

const realFetch = globalThis.fetch;
let calledQuechuaAsr = false;
globalThis.fetch = async (input, init) => {
  if (String(input) === process.env.QUECHUA_ASR_API_URL) {
    calledQuechuaAsr = true;
    assert.equal(init?.method, 'POST');
    assert.deepEqual(init?.headers, { authorization: 'Bearer test-asr-token' });
    assert.ok(init?.body instanceof FormData);
    const audio = init.body.get('audio');
    assert.ok(audio instanceof File);
    assert.equal(audio.name, 'voice.wav');
    assert.deepEqual(
      Buffer.from(await audio.arrayBuffer()),
      Buffer.from([1, 2, 3, 4])
    );
    return Response.json({
      text: 'Allin p’unchay',
      intent: 'registrar_venta',
      confidence: 0.81
    });
  }
  return realFetch(input, init);
};

const { app } = await import('../src/app.js');
const { createAccessToken } = await import('../src/auth/tokens.js');

test('Quechua ASR forwards the authenticated audio to the configured model service', async (context) => {
  const server = createServer(app);
  server.listen(0, '127.0.0.1');
  await once(server, 'listening');
  context.after(() => {
    globalThis.fetch = realFetch;
    return new Promise<void>((resolve, reject) => {
      server.close((error) => error ? reject(error) : resolve());
    });
  });

  const address = server.address();
  assert.ok(address && typeof address !== 'string');
  const token = await createAccessToken({
    userId: '30a86a83-8809-4df4-80e4-2d0fcba0647e',
    businessId: '228eae95-7c43-48ea-90bb-4e1fb9a0374c'
  });
  const audio = new FormData();
  audio.append('audio', new Blob([new Uint8Array([1, 2, 3, 4])], { type: 'audio/wav' }), 'voice.wav');

  const response = await realFetch(`http://127.0.0.1:${address.port}/api/v1/asr/transcribe`, {
    method: 'POST',
    headers: { authorization: `Bearer ${token}` },
    body: audio
  });
  const body = await response.json() as {
    texto?: string;
    simulada?: boolean;
    clasificada?: boolean;
    intencion?: string;
    confianzaIntencion?: number;
  };

  assert.equal(response.status, 200);
  assert.equal(body.texto, 'Allin p’unchay');
  assert.equal(body.simulada, false);
  assert.equal(body.clasificada, true);
  assert.equal(body.intencion, 'registrar_venta');
  assert.equal(body.confianzaIntencion, 0.81);
  assert.equal(calledQuechuaAsr, true);
});

import assert from 'node:assert/strict';
import { test } from 'node:test';

process.env.GEMINI_API_KEY = '';
const { env } = await import('../src/config/env.js');
const { generarRespuestaGemini } = await import('../src/services/gemini-assistant.js');

test('Gemini assistant sends the query with the server-side key and returns generated text', async (context) => {
  const originalKey = env.geminiApiKey;
  const originalModel = env.geminiModel;
  const originalFetch = globalThis.fetch;
  context.after(() => {
    env.geminiApiKey = originalKey;
    env.geminiModel = originalModel;
    globalThis.fetch = originalFetch;
  });
  env.geminiApiKey = 'test-gemini-key';
  env.geminiModel = 'test-model';
  globalThis.fetch = async (input, init) => {
    assert.match(String(input), /generativelanguage\.googleapis\.com/);
    assert.equal(new Headers(init?.headers).get('x-goog-api-key'), 'test-gemini-key');
    const body = JSON.parse(String(init?.body)) as {
      contents?: Array<{ parts?: Array<{ text?: string }> }>;
      systemInstruction?: { parts?: Array<{ text?: string }> };
    };
    assert.match(body.contents?.[0]?.parts?.[0]?.text ?? '', /¿Qué es el IVA\?/);
    assert.match(body.systemInstruction?.parts?.[0]?.text ?? '', /No ejecutes ni confirmes operaciones comerciales/);
    return new Response(JSON.stringify({
      candidates: [{ content: { parts: [{ text: 'Respuesta simulada del proveedor.' }] } }]
    }), { status: 200, headers: { 'content-type': 'application/json' } });
  };

  const respuesta = await generarRespuestaGemini('¿Qué es el IVA?', 'es');
  assert.equal(respuesta, 'Respuesta simulada del proveedor.');
});

test('Gemini assistant rejects provider errors instead of returning a successful answer', async (context) => {
  const originalKey = env.geminiApiKey;
  const originalFetch = globalThis.fetch;
  context.after(() => {
    env.geminiApiKey = originalKey;
    globalThis.fetch = originalFetch;
  });
  env.geminiApiKey = 'test-gemini-key';
  globalThis.fetch = async () => new Response('provider unavailable', { status: 503 });

  await assert.rejects(
    () => generarRespuestaGemini('¿Qué es el IVA?', 'es'),
    /503/
  );
});

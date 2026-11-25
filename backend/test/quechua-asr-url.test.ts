import assert from 'node:assert/strict';
import { test } from 'node:test';
import { isAllowedQuechuaAsrUrl } from '../src/config/quechua-asr-url.js';

test('Quechua ASR allows HTTPS endpoints and Railway private HTTP only', () => {
  assert.equal(isAllowedQuechuaAsrUrl(new URL('https://asr.example.test/transcribe')), true);
  assert.equal(isAllowedQuechuaAsrUrl(new URL('http://quechua-asr.railway.internal:8000/transcribe')), true);
  assert.equal(isAllowedQuechuaAsrUrl(new URL('http://asr.example.test/transcribe')), false);
  assert.equal(isAllowedQuechuaAsrUrl(new URL('http://railway.internal.attacker.test/transcribe')), false);
  assert.equal(isAllowedQuechuaAsrUrl(new URL('ftp://quechua-asr.railway.internal/transcribe')), false);
});

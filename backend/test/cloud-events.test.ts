import assert from 'node:assert/strict';
import { test } from 'node:test';
import { publishBusinessUpdate, subscribeToBusinessUpdates } from '../src/sincronizacion/cloud-events.js';

test('cloud updates are delivered only to subscribers of the affected business', () => {
  const updatesA: string[] = [];
  const updatesB: string[] = [];
  const unsubscribe = subscribeToBusinessUpdates('business-a', (update) => updatesA.push(update.type));
  subscribeToBusinessUpdates('business-b', (update) => updatesB.push(update.type));

  publishBusinessUpdate('business-a', 'product.created', { id: 'product-1' });
  unsubscribe();
  publishBusinessUpdate('business-a', 'product.deleted', { id: 'product-1' });

  assert.deepEqual(updatesA, ['product.created']);
  assert.deepEqual(updatesB, []);
});

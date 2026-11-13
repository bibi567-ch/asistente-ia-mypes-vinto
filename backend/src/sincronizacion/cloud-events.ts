export type CloudUpdateType = 'product.created' | 'product.updated' | 'product.deleted' | 'invoice.created' | 'payment-qr.updated';

export interface CloudUpdate {
  type: CloudUpdateType;
  data: Record<string, unknown>;
  occurredAt: string;
}

type Listener = (update: CloudUpdate) => void;

const listenersByBusiness = new Map<string, Set<Listener>>();

export function subscribeToBusinessUpdates(businessId: string, listener: Listener): () => void {
  const listeners = listenersByBusiness.get(businessId) ?? new Set<Listener>();
  listeners.add(listener);
  listenersByBusiness.set(businessId, listeners);

  return () => {
    listeners.delete(listener);
    if (listeners.size === 0) listenersByBusiness.delete(businessId);
  };
}

export function publishBusinessUpdate(
  businessId: string,
  type: CloudUpdateType,
  data: Record<string, unknown>
): void {
  const update: CloudUpdate = { type, data, occurredAt: new Date().toISOString() };
  for (const listener of listenersByBusiness.get(businessId) ?? []) {
    try {
      listener(update);
    } catch {
      console.error(`Failed to publish ${type} update to a connected client.`);
    }
  }
}

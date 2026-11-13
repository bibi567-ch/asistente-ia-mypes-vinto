import { Router } from 'express';
import { requireAuth } from '../auth/require-auth.js';
import { pool } from '../db/pool.js';
import { publishBusinessUpdate, subscribeToBusinessUpdates } from '../sincronizacion/cloud-events.js';

interface InvoiceItem {
  name: string;
  quantity: number;
  unitPrice: number;
}

export const cloudRouter = Router();

cloudRouter.post('/facturas', requireAuth, async (request, response) => {
  const businessId = request.auth?.businessId;
  if (!businessId) {
    response.status(401).json({ error: { code: 'unauthorized', message: 'Authentication is required.' } });
    return;
  }

  const body = request.body as { customerName?: unknown; items?: unknown } | null;
  if (
    !body
    || (body.customerName !== undefined && (
      typeof body.customerName !== 'string'
      || body.customerName.trim().length > 160
    ))
    || !Array.isArray(body.items)
    || body.items.length < 1
    || body.items.length > 100
    || !body.items.every(isInvoiceItem)
  ) {
    response.status(400).json({
      error: { code: 'invalid_invoice', message: 'Provide an optional customerName (up to 160 characters) and 1-100 valid items.' }
    });
    return;
  }

  const items = body.items as InvoiceItem[];
  const total = items.reduce((sum, item) => sum + Math.round(item.quantity * item.unitPrice * 100), 0) / 100;
  if (!Number.isSafeInteger(Math.round(total * 100)) || total > 9_999_999_999.99) {
    response.status(400).json({
      error: { code: 'invoice_total_out_of_range', message: 'The invoice total exceeds the supported range.' }
    });
    return;
  }
  const customerName = typeof body.customerName === 'string' ? body.customerName.trim() : null;

  try {
    const result = await pool.query<{ id: string; created_at: Date }>(
      `INSERT INTO invoices (business_id, customer_name, items_json, total)
       VALUES ($1, $2, $3::jsonb, $4)
       RETURNING id, created_at`,
      [businessId, customerName, JSON.stringify(items), total.toFixed(2)]
    );
    const invoice = result.rows[0];
    if (!invoice) throw new Error('Invoice insert returned no row.');

    const invoiceNumber = `FAC-${invoice.id.slice(0, 8).toUpperCase()}`;
    publishBusinessUpdate(businessId, 'invoice.created', { id: invoice.id, invoiceNumber });
    response.status(201).json({
      id: invoice.id,
      invoiceNumber,
      total: total.toFixed(2),
      createdAt: invoice.created_at,
      pdfBase64: Buffer.from(buildSimulatedPdf(invoiceNumber, customerName, items, total), 'ascii').toString('base64'),
      pdfSimulated: true
    });
  } catch {
    console.error('Error al crear factura.');
    response.status(500).json({ error: { code: 'invoice_creation_failed', message: 'No se pudo crear la factura.' } });
  }
});

cloudRouter.post('/qr', requireAuth, async (request, response) => {
  const businessId = request.auth?.businessId;
  if (!businessId) {
    response.status(401).json({ error: { code: 'unauthorized', message: 'Authentication is required.' } });
    return;
  }

  const { qrBase64 } = request.body ?? {};
  if (typeof qrBase64 !== 'string' || !isBoundedBase64(qrBase64)) {
    response.status(400).json({
      error: { code: 'invalid_qr', message: 'qrBase64 must be valid Base64 encoding up to 48 KiB.' }
    });
    return;
  }

  try {
    await pool.query(
      `INSERT INTO business_payment_qr (business_id, qr_base64, updated_at)
       VALUES ($1, $2, now())
       ON CONFLICT (business_id)
       DO UPDATE SET qr_base64 = EXCLUDED.qr_base64, updated_at = now()`,
      [businessId, qrBase64]
    );
    publishBusinessUpdate(businessId, 'payment-qr.updated', { updated: true });
    response.status(200).json({ message: 'QR guardado.' });
  } catch {
    console.error('Error al guardar el QR de pago.');
    response.status(500).json({ error: { code: 'qr_save_failed', message: 'No se pudo guardar el QR.' } });
  }
});

cloudRouter.get('/sync/updates', requireAuth, (request, response) => {
  const businessId = request.auth?.businessId;
  if (!businessId) {
    response.status(401).json({ error: { code: 'unauthorized', message: 'Authentication is required.' } });
    return;
  }

  response.status(200);
  response.setHeader('Content-Type', 'text/event-stream; charset=utf-8');
  response.setHeader('Cache-Control', 'no-cache, no-transform');
  response.setHeader('Connection', 'keep-alive');
  response.setHeader('X-Accel-Buffering', 'no');
  response.flushHeaders();
  response.write('event: connected\ndata: {}\n\n');

  const unsubscribe = subscribeToBusinessUpdates(businessId, (update) => {
    response.write(`event: ${update.type}\ndata: ${JSON.stringify(update)}\n\n`);
  });
  const heartbeat = setInterval(() => response.write(': keep-alive\n\n'), 25_000);
  response.on('close', () => {
    clearInterval(heartbeat);
    unsubscribe();
  });
});

function isInvoiceItem(value: unknown): value is InvoiceItem {
  if (!value || typeof value !== 'object') return false;
  const item = value as Partial<InvoiceItem>;
  return (
    typeof item.name === 'string'
    && item.name.trim().length > 0
    && item.name.trim().length <= 160
    && typeof item.quantity === 'number'
    && Number.isFinite(item.quantity)
    && item.quantity > 0
    && item.quantity <= 100_000
    && Number.isInteger(item.quantity * 1000)
    && typeof item.unitPrice === 'number'
    && Number.isFinite(item.unitPrice)
    && item.unitPrice >= 0
    && item.unitPrice <= 1_000_000_000
    && Number.isInteger(item.unitPrice * 100)
  );
}

function isBoundedBase64(value: string): boolean {
  if (value.length === 0 || value.length > 65_536) return false;
  if (!/^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$/.test(value)) return false;
  const decoded = Buffer.from(value, 'base64');
  return decoded.length > 0 && decoded.length <= 48 * 1024 && decoded.toString('base64') === value;
}

function buildSimulatedPdf(
  invoiceNumber: string,
  customerName: string | null,
  items: InvoiceItem[],
  total: number
): string {
  const rows = [
    `Factura simulada: ${invoiceNumber}`,
    `Cliente: ${customerName || 'Consumidor final'}`,
    ...items.slice(0, 20).map((item) =>
      `${item.name} | ${item.quantity} x ${item.unitPrice.toFixed(2)} Bs`
    ),
    `Total: ${total.toFixed(2)} Bs`
  ].map(toPdfAscii);
  const stream = [
    'BT',
    '/F1 11 Tf',
    '50 760 Td',
    ...rows.flatMap((row, index) => [
      ...(index === 0 ? [] : ['0 -18 Td']),
      `(${escapePdfText(row)}) Tj`
    ]),
    'ET'
  ].join('\n');
  const objects = [
    '<< /Type /Catalog /Pages 2 0 R >>',
    '<< /Type /Pages /Kids [3 0 R] /Count 1 >>',
    '<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>',
    '<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>',
    `<< /Length ${Buffer.byteLength(stream, 'ascii')} >>\nstream\n${stream}\nendstream`
  ];

  let pdf = '%PDF-1.4\n';
  const offsets = [0];
  for (const [index, object] of objects.entries()) {
    offsets.push(Buffer.byteLength(pdf, 'ascii'));
    pdf += `${index + 1} 0 obj\n${object}\nendobj\n`;
  }
  const xrefOffset = Buffer.byteLength(pdf, 'ascii');
  pdf += `xref\n0 ${objects.length + 1}\n0000000000 65535 f \n`;
  for (const offset of offsets.slice(1)) pdf += `${String(offset).padStart(10, '0')} 00000 n \n`;
  return `${pdf}trailer\n<< /Size ${objects.length + 1} /Root 1 0 R >>\nstartxref\n${xrefOffset}\n%%EOF`;
}

function toPdfAscii(value: string): string {
  return value.normalize('NFD')
    .replace(/\p{Mn}+/gu, '')
    .replace(/[^\x20-\x7e]/g, '?');
}

function escapePdfText(value: string): string {
  return value.replace(/[\\()]/g, '\\$&');
}

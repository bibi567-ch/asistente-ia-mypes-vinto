CREATE TABLE IF NOT EXISTS invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE RESTRICT,
    customer_name TEXT,
    items_json JSONB NOT NULL CHECK (jsonb_typeof(items_json) = 'array'),
    total NUMERIC(12, 2) NOT NULL CHECK (total >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS invoices_business_created_idx
    ON invoices (business_id, created_at DESC);

CREATE TABLE IF NOT EXISTS business_payment_qr (
    business_id UUID PRIMARY KEY REFERENCES businesses(id) ON DELETE RESTRICT,
    qr_base64 TEXT NOT NULL CHECK (length(qr_base64) BETWEEN 1 AND 65536),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

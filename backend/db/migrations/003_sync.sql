-- Offline synchronization inbox and manual conflict records.
CREATE TABLE IF NOT EXISTS transacciones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE RESTRICT,
    operation_id UUID NOT NULL,
    tipo TEXT NOT NULL CHECK (tipo IN ('VENTA', 'COMPRA', 'FIADO', 'COBRO')),
    payload_json JSONB NOT NULL,
    total NUMERIC(12, 2) NOT NULL CHECK (total >= 0),
    forma_pago TEXT NOT NULL CHECK (forma_pago IN ('CONTADO', 'FIADO')),
    cliente_nombre TEXT,
    estado TEXT NOT NULL DEFAULT 'ACEPTADA'
        CHECK (estado IN ('ACEPTADA', 'DUPLICADA', 'CONFLICTO', 'RECHAZADA')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (business_id, operation_id)
);

CREATE INDEX IF NOT EXISTS transacciones_business_created_idx
    ON transacciones (business_id, created_at DESC);

CREATE INDEX IF NOT EXISTS transacciones_estado_idx
    ON transacciones (estado);

CREATE TABLE IF NOT EXISTS conflictos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE RESTRICT,
    operation_id UUID NOT NULL,
    tipo_conflicto TEXT NOT NULL
        CHECK (tipo_conflicto IN ('STOCK_NEGATIVO', 'VERSION_DESACTUALIZADA', 'DUPLICADO')),
    detalle JSONB NOT NULL,
    resuelto BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS conflictos_business_idx
    ON conflictos (business_id, resuelto);

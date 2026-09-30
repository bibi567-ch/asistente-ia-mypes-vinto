-- Initial relational model for local development.
-- This migration creates schema only; no real user or merchant data is seeded.
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS businesses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL CHECK (length(trim(name)) BETWEEN 1 AND 160),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE RESTRICT,
    name TEXT NOT NULL CHECK (length(trim(name)) BETWEEN 1 AND 160),
    unit TEXT NOT NULL CHECK (length(trim(unit)) BETWEEN 1 AND 40),
    sale_price NUMERIC(12, 2) NOT NULL CHECK (sale_price >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (business_id, name)
);

CREATE INDEX IF NOT EXISTS products_business_created_idx
    ON products (business_id, created_at DESC);

CREATE TABLE IF NOT EXISTS sync_operations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE RESTRICT,
    operation_id UUID NOT NULL,
    schema_version INTEGER NOT NULL CHECK (schema_version > 0),
    operation_type TEXT NOT NULL CHECK (length(trim(operation_type)) BETWEEN 1 AND 80),
    status TEXT NOT NULL CHECK (status IN ('accepted', 'rejected', 'conflict')),
    received_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (business_id, operation_id)
);

CREATE TABLE IF NOT EXISTS voice_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE RESTRICT,
    event_id UUID NOT NULL,
    command_text TEXT NOT NULL CHECK (length(command_text) BETWEEN 1 AND 500),
    intent TEXT NOT NULL CHECK (intent IN (
        'LLAMAR_CONTACTO',
        'ENVIAR_MENSAJE',
        'LEER_TEXTO',
        'CREAR_ALARMA',
        'ABRIR_APP',
        'CONSULTAR_HORA',
        'LINTERNA',
        'AJUSTES_RED',
        'GUIA_TELEFONO',
        'AYUDA',
        'DESCONOCIDO'
    )),
    confirmed BOOLEAN NOT NULL,
    executed BOOLEAN NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    received_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (business_id, event_id)
);

CREATE INDEX IF NOT EXISTS voice_logs_business_occurred_idx
    ON voice_logs (business_id, occurred_at DESC);

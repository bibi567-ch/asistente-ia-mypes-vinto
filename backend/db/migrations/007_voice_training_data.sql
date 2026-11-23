CREATE TABLE IF NOT EXISTS voice_training_data (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE RESTRICT,
    event_id UUID NOT NULL,
    original_text TEXT NOT NULL CHECK (length(original_text) BETWEEN 1 AND 500),
    corrected_text TEXT NOT NULL CHECK (length(corrected_text) BETWEEN 1 AND 500),
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
        'CONSULTA_COGNITIVA',
        'AYUDA'
    )),
    occurred_at TIMESTAMPTZ NOT NULL,
    received_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (business_id, event_id)
);

CREATE INDEX IF NOT EXISTS voice_training_data_business_received_idx
    ON voice_training_data (business_id, received_at DESC);

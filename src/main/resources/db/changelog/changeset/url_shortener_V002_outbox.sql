CREATE TABLE outbox
(
    id           BIGSERIAL PRIMARY KEY,
    entity_id    VARCHAR(256) NOT NULL,
    event_type   SMALLINT     NOT NULL,
    payload      TEXT,
    status       SMALLINT     NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMPTZ,
    UNIQUE (entity_id, event_type)
);

CREATE INDEX ON outbox (event_type);
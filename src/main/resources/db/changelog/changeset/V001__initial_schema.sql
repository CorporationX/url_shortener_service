-- Таблица для хранения свободных и используемых хэшей
CREATE TABLE hash (
    id BIGSERIAL PRIMARY KEY,
    hash_value VARCHAR(10) NOT NULL UNIQUE
);
-- Таблица для хранения связей между коротким и оригинальным URL
CREATE TABLE url (
    id BIGSERIAL PRIMARY KEY,
    hash VARCHAR(10) NOT NULL UNIQUE,
    original_url TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_accessed_at TIMESTAMP,
    access_count BIGINT NOT NULL DEFAULT 0
);

-- Индексы для оптимизации поиска
CREATE INDEX idx_url_hash ON url(hash);

CREATE INDEX idx_hashes_hash_value ON hash(hash_value);
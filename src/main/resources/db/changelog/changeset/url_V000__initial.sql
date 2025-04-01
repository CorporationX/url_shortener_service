CREATE TABLE IF NOT EXISTS url (
    id BIGSERIAL PRIMARY KEY,
    hash VARCHAR(7) UNIQUE NOT NULL,
    original_url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    expires_at TIMESTAMP
);

create table if not exists hash(
    id   BIGSERIAL PRIMARY KEY,
    hash varchar(7) UNIQUE NOT NULL
);

CREATE SEQUENCE if not exists unique_number_seq START WITH 100 INCREMENT BY 1;

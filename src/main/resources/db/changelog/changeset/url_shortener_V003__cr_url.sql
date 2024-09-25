CREATE TABLE url (
    id BIGSERIAL PRIMARY KEY,
    hash VARCHAR(7) NOT NULL UNIQUE,
    url VARCHAR NOT NULL UNIQUE,
    created_at TIMESTAMP
)
CREATE TABLE url_hash (
    id UUID PRIMARY KEY,
    url VARCHAR NOT NULL,
    hash VARCHAR(6) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp NOT NULL
);

CREATE index hash_idx on url_hash (hash);
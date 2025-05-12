CREATE TABLE url (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    url varchar(512) UNIQUE NOT NULL,
    hash varchar(8) UNIQUE NOT NULL,
    last_get_at timestamptz NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE INDEX idx_hash ON url(hash);
CREATE INDEX idx_url ON url(url);


CREATE TABLE free_hash (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hash varchar(8) UNIQUE NOT NULL
);

CREATE SEQUENCE unique_hash_number_seq
    START WITH 250000
    INCREMENT BY 31;
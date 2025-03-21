CREATE TABLE IF NOT EXISTS hash
(
    id   bigint PRIMARY KEY DEFAULT nextval('unique_hash_number_sequence'),
    hash varchar(6) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS url
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    url        VARCHAR    NOT NULL UNIQUE,
    hash       VARCHAR(6) NOT NULL UNIQUE,
    created_at timestamptz DEFAULT current_timestamp
);
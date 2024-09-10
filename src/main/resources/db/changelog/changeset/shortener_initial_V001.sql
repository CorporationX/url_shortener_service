CREATE TABLE url (
    hash      VARCHAR(6) PRIMARY KEY,
    url       VARCHAR(2048),
    created_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT unique_url UNIQUE (url)
);

CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;
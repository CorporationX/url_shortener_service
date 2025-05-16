CREATE TABLE url (
    hash varchar(8) PRIMARY KEY UNIQUE NOT NULL,
    url varchar(512) NOT NULL,
    last_get_at timestamptz NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE INDEX idx_hash ON url(hash);
CREATE INDEX idx_url ON url(url);


CREATE TABLE free_hash (
    hash varchar(8)  PRIMARY KEY UNIQUE NOT NULL
);

CREATE SEQUENCE unique_hash_number_seq
    START WITH 250000
    INCREMENT BY 7;
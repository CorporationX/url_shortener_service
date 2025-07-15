CREATE table urls (
    hash varchar(6) PRIMARY KEY,
    url varchar(2048) NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP
);

CREATE table hashes (
    hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;
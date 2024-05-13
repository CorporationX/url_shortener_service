CREATE TABLE url(
    hash varchar(6) PRIMARY KEY,
    url varchar NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash(
    hash varchar(2048) PRIMARY KEY
);

CREATE SEQUENCE table_name_id_seq START 1 INCREMENT BY 1;
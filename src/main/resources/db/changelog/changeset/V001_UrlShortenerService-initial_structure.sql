CREATE SEQUENCE number_id_seq START 1;
CREATE TABLE free_account_number (
    id BIGINT PRIMARY KEY DEFAULT nextval('number_id_seq'),
    sequence BIGINT NOT NULL
);

CREATE TABLE hash (
    hash VARCHAR(8) PRIMARY KEY NOT NULL
);

CREATE TABLE url (
    hash NUMERIC(8) PRIMARY KEY,
    url VARCHAR(1024) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
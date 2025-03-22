CREATE TABLE hash
(
    hash varchar(6) PRIMARY KEY
);

CREATE TABLE url
(
    hash varchar(6) PRIMARY KEY,
    url text NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (hash) REFERENCES hash(hash)
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
START WITH 1
INCREMENT BY 1;
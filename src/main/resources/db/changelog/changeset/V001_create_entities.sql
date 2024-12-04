CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hash (
                      hash VARCHAR(6) PRIMARY KEY
);

CREATE TABLE url (
                     hash VARCHAR(6) PRIMARY KEY REFERENCES hash(hash),
                     url TEXT NOT NULL,
                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
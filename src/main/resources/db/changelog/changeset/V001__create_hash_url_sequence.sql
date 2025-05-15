CREATE TABLE IF NOT EXISTS hash (
                      hash VARCHAR(7) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS url (
                     hash VARCHAR(7) PRIMARY KEY,
                     url TEXT NOT NULL,
                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS unique_seq_number
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

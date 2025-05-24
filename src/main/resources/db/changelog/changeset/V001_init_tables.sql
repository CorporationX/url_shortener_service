CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 20;

CREATE TABLE hash (
    hash VARCHAR(10) PRIMARY KEY
);

CREATE TABLE url (
    hash VARCHAR(10) PRIMARY KEY,
    url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_url UNIQUE (url)
);

CREATE INDEX idx_url_created_at ON url (created_at);
CREATE INDEX idx_url_url ON url (url);
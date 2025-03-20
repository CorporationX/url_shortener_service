CREATE TABLE urls
(
    hash varchar(6) PRIMARY KEY,
    url  varchar(2048) NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hashes
(
    hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    INCREMENT BY 1
    START WITH 916132832;
CREATE TABLE url
(
    hash       varchar(6) PRIMARY KEY,
    url        varchar(1000) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash
(
    hash       varchar(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq START WITH 916132832 INCREMENT BY 1 MAXVALUE 56800235583;

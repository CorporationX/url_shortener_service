CREATE TABLE URL
(
    hash       varchar(6) NOT NULL PRIMARY KEY,
    url        varchar    NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hash
(
    hash varchar(6) NOT NULL PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq INCREMENT BY 1 START 1;

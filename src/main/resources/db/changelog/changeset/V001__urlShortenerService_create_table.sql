CREATE TABLE url
(
    hash VARCHAR(6) NOT NULL PRIMARY KEY UNIQUE ,
    url VARCHAR(512) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash
(
    hash VARCHAR(6) NOT NULL PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
  START WITH 1 INCREMENT BY 1;

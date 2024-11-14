CREATE TABLE IF NOT EXISTS url(
    hash char(6) PRIMARY KEY,
    url varchar(2048) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash(
    hash char(6) PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS num_sequence START WITH 1 INCREMENT BY 1;
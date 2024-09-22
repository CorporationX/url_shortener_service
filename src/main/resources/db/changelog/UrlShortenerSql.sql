CREATE TABLE IF NOT EXISTS url (
    hash varchar(6) primary key,
    url varchar(600) not null,
    timestamp timestamp default current_timestamp
);

CREATE INDEX IF NOT EXISTS idx_created_at ON url (created_at);

CREATE TABLE IF NOT EXISTS hash (
    hash varchar(6) primary key
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    START WITH 1
    INCREMENT BY 1;
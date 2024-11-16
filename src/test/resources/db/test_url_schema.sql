CREATE TABLE IF NOT EXISTS url (
    hash       varchar(6) PRIMARY KEY,
    url        varchar(2048) not null,
    created_at timestamp  DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash (
    hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq START WITH 1 INCREMENT BY 1;

INSERT INTO url(hash, url) VALUES ('100000', 'https://faang-school.com/courses') ON CONFLICT DO NOTHING;
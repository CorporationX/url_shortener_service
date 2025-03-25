CREATE SEQUENCE IF NOT EXISTS unique_hash_number_seq
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;

-- SELECT nextval('unique_hash_number_seq') AS generated_value
-- FROM generate_series(1, 10);

create table if not exists url (
    hash       VARCHAR(6) PRIMARY KEY,
    url        VARCHAR(1024) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table if not exists hash (
    hash varchar(6) primary key
);
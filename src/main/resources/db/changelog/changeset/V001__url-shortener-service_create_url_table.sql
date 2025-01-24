CREATE TABLE url (
    id bigint PRIMARY KEY,
    hash VARCHAR(7) NOT NULL,
    url VARCHAR(2000) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
    );

CREATE TABLE hash(
    id bigint PRIMARY KEY,
    hash VARCHAR(7) NOT NULL
    );

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE sequence_id_auto_gen
    START WITH 1
    INCREMENT BY 100
    NO MINVALUE
    NO MAXVALUE
    CACHE 100;

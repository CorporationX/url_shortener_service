CREATE TABLE IF NOT EXISTS hash (
                      id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                      hash varchar(6) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS url (
                     id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                     hash varchar(6) UNIQUE NOT NULL,
                     url varchar(2048) NOT NULL,
                     created_at timestamptz NOT NULL default current_timestamp,

                     CONSTRAINT fk_hash FOREIGN KEY (hash) REFERENCES hash (hash)
);

CREATE TABLE IF NOT EXISTS free_hash (
                           id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                           hash varchar(6) UNIQUE NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    START WITH 1
    INCREMENT BY 1;

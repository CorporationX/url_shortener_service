CREATE SEQUENCE hash_id_seq
AS BIGINT
INCREMENT BY 1
START WITH 1;

CREATE TABLE hash (
    id BIGINT PRIMARY KEY,
    hash VARCHAR(6) NOT NULL
);

CREATE TABLE url (
    id uuid PRIMARY KEY,
    url VARCHAR NOT NULL,
    hash_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp NOT NULL,

    CONSTRAINT fk_hash_id FOREIGN KEY (hash_id) REFERENCES hash (id)
);

create index hash_idx on url (hash_id);

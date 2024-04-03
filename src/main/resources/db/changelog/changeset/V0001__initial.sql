CREATE TABLE hash
(
    hash VARCHAR(6) NOT NULL
)

CREATE TABLE url
(
    hash       VARCHAR(6) NOT NULL,
    url        VARCHAR    NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hash
        FOREIGN KEY (hash)
            REFERENCES hash (hash)
);

CREATE SEQUENCE unique_number_sequence
START WITH 1
INCREMENT BY 1;

CREATE sequence unique_number_sequence --TODO: пока не совсем понятно, зачем он нужен
    start 1
    increment 1;

CREATE TABLE url(
    hash       VARCHAR(6)  PRIMARY KEY,
    url        VARCHAR     NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);
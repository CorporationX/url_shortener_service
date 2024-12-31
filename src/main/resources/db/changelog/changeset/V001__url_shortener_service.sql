CREATE TABLE urls (
    hash_value  VARCHAR(6)  PRIMARY KEY,
    url_value   TEXT        NOT NULL,
    validated_at  TIMESTAMP   NOT NULL
);

CREATE TABLE free_hash_set (
    hash_value  VARCHAR(6)  PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    START WITH 916132832
    INCREMENT BY 1;

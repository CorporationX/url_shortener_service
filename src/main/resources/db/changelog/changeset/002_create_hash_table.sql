CREATE TABLE hash (
    hash varchar(6) PRIMARY KEY,
    unique_number_seq BIGINT NOT NULL DEFAULT NEXTVAL('unique_number_seq')
);
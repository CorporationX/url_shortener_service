CREATE TABLE url (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hash varchar(128) NOT NULL,
    long_url varchar(128) NOT NULL
);
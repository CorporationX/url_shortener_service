CREATE TABLE url
(
    hash       varchar(6)   NOT NULL PRIMARY KEY,
    url        varchar(2048) NOT NULL UNIQUE,
    created_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
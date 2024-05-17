CREATE TABLE url
(
    hash       varchar(6) PRIMARY KEY,
    url        varchar(512) NOT NULL,
    created_at timestamp    NOT NULL DEFAULT current_timestamp
)

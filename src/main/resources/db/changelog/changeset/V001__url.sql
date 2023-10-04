CREATE TABLE url (
    hash varchar(6) NOT NULL,
    text varchar(256),
    created_at timestamptz
);

CREATE TABLE hash (
    hash varchar(256) PRIMARY KEY
);
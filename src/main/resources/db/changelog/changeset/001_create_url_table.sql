CREATE TABLE url (
    hash varchar(6) PRIMARY KEY,
    url text NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (hash) REFERENCES hash(hash)
);
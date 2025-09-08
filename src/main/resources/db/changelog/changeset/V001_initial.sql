CREATE TABLE url (
    hash VARCHAR(6) PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT current_timestamp
);

CREATE SEQUENCE unique_hash_number_seq
  START WITH 1
  INCREMENT BY 1;

  CREATE TABLE hash (
      hash VARCHAR(6) PRIMARY KEY
  );
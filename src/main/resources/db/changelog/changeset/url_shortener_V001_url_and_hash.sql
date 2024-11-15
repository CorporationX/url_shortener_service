CREATE TABLE IF NOT EXISTS url (
      hash varchar(6) PRIMARY KEY,
      url varchar(128) NOT NULL,
      created_at timestamptz
);

CREATE TABLE IF NOT EXISTS hash (
      hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq START WITH 1 INCREMENT BY 1;

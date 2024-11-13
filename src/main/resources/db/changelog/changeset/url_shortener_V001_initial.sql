CREATE TABLE IF NOT EXISTS url(
  hash varchar(6) PRIMARY KEY CHECK (length(hash) = 6),
  url varchar(2048) not null,
  created_at timestamp not null DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash(
  hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq START WITH 1 INCREMENT BY 1;
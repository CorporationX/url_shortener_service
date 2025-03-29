CREATE TABLE url
(
   hash         VARCHAR(6) NOT NULL,
   url          VARCHAR(255) NOT NULL,
   created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
   CONSTRAINT pk_url PRIMARY KEY (hash)
);

CREATE TABLE hash
(
   hash         VARCHAR(6) NOT NULL,
   CONSTRAINT pk_hash PRIMARY KEY (hash)
);

CREATE SEQUENCE unique_number_seq
   START WITH 1
   INCREMENT BY 1;
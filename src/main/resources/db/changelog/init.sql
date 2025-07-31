CREATE SEQUENCE unique_hash_number_sequence
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE hash (
                      id BIGSERIAL PRIMARY KEY,
                      actual_hash VARCHAR(7) NOT NULL UNIQUE
);
CREATE TABLE urls (
                      id BIGSERIAL PRIMARY KEY,
                      long_url TEXT NOT NULL UNIQUE,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      hash VARCHAR(7) NOT NULL,
                      CONSTRAINT fk_hash FOREIGN KEY (hash) REFERENCES hash(actual_hash)
);

CREATE UNIQUE INDEX idx_urls_hash ON urls (hash);

CREATE TABLE shedlock (
                          name VARCHAR(64),
                          lock_until TIMESTAMP(3) NULL,
                          locked_at TIMESTAMP(3) NULL,
                          locked_by VARCHAR(255),
                          PRIMARY KEY (name)
)
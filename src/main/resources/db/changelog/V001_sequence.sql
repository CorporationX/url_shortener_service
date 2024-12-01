CREATE SEQUENCE url_sequence
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

CREATE TABLE urls (
                      id BIGINT NOT NULL DEFAULT nextval('url_sequence'),
                      hash VARCHAR(6) NOT NULL,
                      url VARCHAR(2048) NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      PRIMARY KEY (id)
);

CREATE TABLE hashes (
                     hash VARCHAR(6) NOT NULL,
                     PRIMARY KEY (id)
);
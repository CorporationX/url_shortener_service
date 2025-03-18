CREATE TABLE url (
    hash VARCHAR(6) PRIMARY KEY,
    url VARCHAR(2000) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT current_timestamp
);

CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY
);
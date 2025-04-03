CREATE TABLE cache_lock
(
    id           INT PRIMARY KEY,
    locked       BOOLEAN NOT NULL,
    last_updated TIMESTAMP
);

INSERT INTO cache_lock (id, locked, last_updated)
VALUES (1, false, NOW());

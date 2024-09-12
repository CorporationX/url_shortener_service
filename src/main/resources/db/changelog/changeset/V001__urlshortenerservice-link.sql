CREATE TABLE urls (
    id BIGSERIAL PRIMARY KEY,
    short_url TEXT NOT NULL,
    long_url TEXT NOT NULL
);
CREATE TABLE url (
    hash CHAR(6) PRIMARY KEY,
    url VARCHAR,
    created_at timestamptz DEFAULT current_timestamp
);

create index url_index on url(url);
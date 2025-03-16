create table url (
    hash varchar(6) PRIMARY KEY,
    url varchar(256) unique not null,
    created_at timestamptz DEFAULT current_timestamp
);

create table hash (
    hash varchar(6) PRIMARY KEY
);
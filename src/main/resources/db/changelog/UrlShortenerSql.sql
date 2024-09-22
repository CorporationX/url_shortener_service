create table url (
    hash varchar(6) primary key,
    url varchar(600) not null,
    timestamp timestamp default current_timestamp
);

CREATE INDEX idx_created_at ON url (created_at);

create table hash (
    hash varchar(6) primary key
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;
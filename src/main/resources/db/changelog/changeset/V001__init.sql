create table url (
    hash varchar(6) primary key,
    url varchar(2048) not null,
    created_at timestamptz default CURRENT_TIMESTAMP
);

create table hash (
    hash varchar(6) primary key
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;
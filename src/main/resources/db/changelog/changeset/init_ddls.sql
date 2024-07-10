create table url (
    hash varchar(6) primary key,
    url varchar(2048) not null unique,
    created_at timestamp default current_timestamp not null
);

create sequence unique_number_seq
    start 1
    increment 1;

create table hashes (
    hash varchar(6) primary key
)
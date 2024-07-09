create table url (
    varchar(6) hash primary key,
    varchar(2048) url not null, unique,
    created_at timestamp default current_timestamp not null
)

create sequence unique_number_seq (
    start 1
    increment 1
)

create table hashes (
    varchar(6) hash primary key,
)
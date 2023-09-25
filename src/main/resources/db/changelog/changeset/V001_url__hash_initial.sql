create table url
(
    hash       varchar(6)   not null primary key,
    url        varchar(100) not null,
    created_at timestamptz default current_timestamp
);

create table hash
(
    hash varchar(6) not null primary key
);

create sequence unique_number_sequence
    start 1
    increment 1;
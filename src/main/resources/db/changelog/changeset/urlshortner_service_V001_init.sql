create table url
(
    id         bigint primary key generated always as identity,
    hash       varchar(6) unique not null,
    url        text      not null,
    created_at timestamp default current_timestamp
);

create table hash
(
    hash varchar(6) unique primary key
);

create sequence unique_number_seq
    start with 1
    increment by 1;

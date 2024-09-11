create table if not exists url
(
    hash       varchar(6) primary key,
    url        varchar(255),
    created_at timestamp not null default current_timestamp
);

create table if not exists hash
(
    hash varchar(6) primary key
);

create sequence if not exists unique_number_seq
    as bigint
    increment by 1
    cache 1;
create table if not exists url
(
    hash       varchar(6) primary key,
    url        varchar(2048) not null,
    created_at timestamp     not null default current_timestamp
);

create table if not exists hash
(
    hash varchar(6) primary key
);

create sequence unique_number_seq start 1;
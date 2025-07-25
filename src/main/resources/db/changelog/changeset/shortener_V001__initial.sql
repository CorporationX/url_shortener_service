create sequence if not exists unique_number_seq as bigint increment by 1 start with 1;

create table if not exists url (
    hash varchar(6) primary key,
    url varchar(512) not null,
    created_at timestamptz DEFAULT current_timestamp
);

create table if not exists hash (
    hash varchar(6) primary key
);
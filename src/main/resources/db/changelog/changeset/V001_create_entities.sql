create table if not exists public.url (
    hash varchar(6) primary key,
    url varchar not null,
    created_at timestamp not null default current_timestamp
);

create table if not exists public.hash (
    hash varchar(6) primary key
);

create sequence if not exists public.unique_number_seq start with 0 minvalue 0 maxvalue 9223372036854775807;

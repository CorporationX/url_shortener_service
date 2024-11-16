drop table if exists public.url;
create table public.url (
    hash varchar(6) primary key,
    url varchar not null,
    created_at timestamp not null default current_timestamp
);

drop table if exists public.hash;
create table public.hash (
    hash varchar(6) primary key
);

drop sequence if exists public.unique_number_seq;
create sequence public.unique_number_seq start with 0 minvalue 0 maxvalue 9223372036854775807;

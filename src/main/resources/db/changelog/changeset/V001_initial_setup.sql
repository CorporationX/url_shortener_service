CREATE TABLE IF NOT EXISTS public.url (
    hash varchar(8) not null primary key,
    url varchar(1024) not null,
    created_at timestamp default current_timestamp not null
);

CREATE TABLE if not exists public.hash (
    hash varchar(8) not null primary key
);

CREATE SEQUENCE public.unique_number_seq
    START WITH 1000,
    INCREMENT BY 1;

create index idx_url on public.url(url);
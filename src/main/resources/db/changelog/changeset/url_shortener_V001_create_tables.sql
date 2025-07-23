create table if not exists public.urls (
    hash       varchar(7) primary key,
    url        varchar(2048) not null,
    created_at timestamp default current_timestamp
);

create table if not exists public.hashes (
    hash varchar(7) primary key
);

create sequence if not exists public.unique_number_seq increment 1 start 1;

create index unq_urls_url on public.urls using btree (url)
    with (deduplicate_items=true);
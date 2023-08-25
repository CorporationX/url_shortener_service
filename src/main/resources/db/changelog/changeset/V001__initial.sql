create table urls
(
    id         serial primary key,
    long_url   varchar     not null,
    short_url  varchar     not null,
    created_by bigint      not null,
    created_at timestamptz not null
);

create sequence hash_sequence
    start with 1
    increment by 1
    no minvalue
    no maxvalue
    cache 1;
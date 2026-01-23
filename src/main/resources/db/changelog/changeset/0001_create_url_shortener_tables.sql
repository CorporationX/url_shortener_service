create sequence if not exists unique_number_sequence
    start with 1
    increment by 1;

create table if not exists hash (
    hash varchar(6) not null,
    constraint pk_hash primary key (hash)
);

create table if not exists url (
    hash varchar(6) not null,
    url text not null,
    created_at timestamp not null default current_timestamp,
    constraint pk_url primary key (hash)
);

create index if not exists idx_url_created_at on url (created_at);
create index if not exists idx_url_url on url (url);
create index if not exists idx_url_url_created_at_desc on url (url, created_at desc);
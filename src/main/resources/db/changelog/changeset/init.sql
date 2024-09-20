CREATE SEQUENCE unique_hash_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE hash_id_seq
    START WITH 1
    INCREMENT BY 1;

create table if not exists url
(
    id           bigint primary key generated always as IDENTITY UNIQUE,
    short_url    varchar(8),
    url          text,
    created_at   timestamp default CURRENT_TIMESTAMP,
    updated_at   timestamp default CURRENT_TIMESTAMP,
    requested_at timestamp
);

create table if not exists hash
(
    id   bigint primary key default nextval('hash_id_seq'),
    hash varchar(8)
);

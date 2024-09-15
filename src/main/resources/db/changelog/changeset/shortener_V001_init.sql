create table IF NOT EXISTS hash
(
    id bigint primary key not null GENERATED ALWAYS AS IDENTITY UNIQUE,
    hash varchar(6) not null unique
);

CREATE TABLE IF NOT EXISTS url
(
    hash       varchar(6) primary key not null,
    url        varchar(2048)           not null,
    created_at timestamptz DEFAULT current_timestamp
);

create sequence if not exists unique_number_seq start with 1 increment by 1;
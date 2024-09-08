create table IF NOT EXISTS hash
(
    hash varchar(6) primary key not null
);

CREATE TABLE IF NOT EXISTS url
(
    hash       varchar(6) primary key not null,
    url        varchar(128)           not null,
    created_at timestamptz DEFAULT current_timestamp,
    constraint url_hash_fk foreign key (hash) references hash (hash) ON DELETE CASCADE
);

create sequence unique_number_seq start with 1 increment by 1;
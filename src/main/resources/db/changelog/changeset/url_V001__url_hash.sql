create table url
(
    hash       varchar(6) NOT NULL primary key,
    url        character  not null,
    created_at timestamptz DEFAULT current_timestamp
);

create table hash
(
    hash varchar(6) NOT NULL primary key
);

create sequence unique_number_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE;
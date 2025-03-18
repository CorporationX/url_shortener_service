drop index if exists url_created_idx;
drop table if exists url;
drop table if exists hash;
drop sequence hash_id_seq;

DELETE FROM databasechangelog;
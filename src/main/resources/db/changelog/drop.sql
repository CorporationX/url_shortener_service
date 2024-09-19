DROP SEQUENCE unique_number_seq;
DROP TABLE url;
DROP TABLE hash;

DELETE FROM databasechanhelog WHERE filename =
'db/changelog/changeset/url_schortener_V001_setUp.sql';
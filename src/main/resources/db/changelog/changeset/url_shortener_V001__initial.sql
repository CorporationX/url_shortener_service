CREATE TABLE hash (
                      id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                      hash varchar(6) UNIQUE NOT NULL
);

CREATE TABLE url (
                     id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                     hash varchar(6) UNIQUE NOT NULL,
                     url varchar(2048) NOT NULL,
                     created_at timestamp NOT NULL,

                     CONSTRAINT fk_hash FOREIGN KEY (hash) REFERENCES hash (hash)
);

CREATE TABLE free_hash (
                           id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                           hash varchar(6) UNIQUE NOT NULL
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE OR REPLACE FUNCTION insert_into_free_hash()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO free_hash (hash)
VALUES (NEW.hash);
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_insert_hash
    AFTER INSERT ON hash
    FOR EACH ROW
    EXECUTE FUNCTION insert_into_free_hash();

CREATE OR REPLACE FUNCTION check_hash_in_free_hash()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM free_hash WHERE hash = NEW.hash) THEN
        RAISE EXCEPTION 'Hash value % already exists in free_hash', NEW.hash;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER before_insert_url
    BEFORE INSERT ON url
    FOR EACH ROW
    EXECUTE FUNCTION check_hash_in_free_hash();
INSERT INTO hash (hash) VALUES 
    ('123456'),
    ('123457'),
    ('123458'),
    ('123459'),
    ('123460'),
    ('123461'),
    ('123462'),
    ('123463'),
    ('123464'),
    ('123465');

SELECT COUNT(hash) FROM hash LIMIT 100;

CREATE OR REPLACE FUNCTION generate_base62(length integer) 
RETURNS text AS $$
DECLARE
    chars text := '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
    result text := '';
    i integer;
    pos integer;
BEGIN
    FOR i IN 1..length LOOP
        pos := 1 + floor(random() * 62)::integer;
        result := result || substr(chars, pos, 1);
    END LOOP;
    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- Then insert 20 values
INSERT INTO hash (hash) VALUES
    (generate_base62(6)),
    (generate_base62(6)),
    (generate_base62(5)),
    (generate_base62(6)),
    (generate_base62(4)),
    (generate_base62(6)),
    (generate_base62(3)),
    (generate_base62(6)),
    (generate_base62(6)),
    (generate_base62(5)),
    (generate_base62(6)),
    (generate_base62(4)),
    (generate_base62(6)),
    (generate_base62(3)),
    (generate_base62(6)),
    (generate_base62(6)),
    (generate_base62(5)),
    (generate_base62(6)),
    (generate_base62(4)),
    (generate_base62(6));

TRUNCATE TABLE hash;

SELECT *, 
       (SELECT COUNT(*) FROM hash) AS total_count
            FROM hash
                LIMIT 100;

ALTER SEQUENCE table_name_column_name_seq RESTART WITH 1;
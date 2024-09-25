 INSERT INTO hash (id, hash)
 VALUES (1, '1234567'),
        (2, '8910111');
 SELECT SETVAL('hash_id_seq', (SELECT MAX(id) FROM hash));




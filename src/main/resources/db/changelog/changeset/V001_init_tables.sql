CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 20;

CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY
);

CREATE TABLE url (
    hash VARCHAR(6) PRIMARY KEY,
    url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_url_created_at ON url (created_at);

-- Insert initial hashes
INSERT INTO hash (hash)
SELECT encode_number AS hash
FROM (
    SELECT num,
           CASE
               WHEN num = 0 THEN 'aaaaaa'
               ELSE (
                   WITH RECURSIVE base62 AS (
                       SELECT num - 1 AS n, '' AS hash
                       UNION ALL
                       SELECT n / 62,
                              substring('abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789' FROM (n % 62 + 1)::integer FOR 1) || hash
                       FROM base62
                       WHERE n > 0
                   )
                   SELECT lpad(hash, 6, 'a')
                   FROM base62
                   WHERE n = 0
                   LIMIT 1
               )
           END AS encode_number
    FROM (
        SELECT nextval('unique_number_seq') AS num
        FROM generate_series(1, 1000)
    ) t
) enc
ON CONFLICT (hash) DO NOTHING;
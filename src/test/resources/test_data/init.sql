CREATE TABLE IF NOT EXISTS hash
(
    hash VARCHAR(6) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS url
(
    hash VARCHAR(6) PRIMARY KEY,
    url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

INSERT INTO hash (hash)
VALUES
    ('abc123'),
    ('def456'),
    ('ghi789'),
    ('jkl012'),
    ('mno345'),
    ('pqr678'),
    ('stu901'),
    ('vwx234'),
    ('yz5678'),
    ('abc890');

INSERT INTO url (hash, url, created_at) VALUES
('abc123', 'http://example.com/1', NOW() - INTERVAL '1 HOUR'),
('def456', 'http://example.com/2', NOW() - INTERVAL '2 HOURS'),
('ghi789', 'http://example.com/3', NOW() - INTERVAL '3 HOURS'),
('jkl012', 'http://example.com/4', NOW() - INTERVAL '25 HOURS'),
('mno345', 'http://example.com/5', NOW() - INTERVAL '26 HOURS'),
('pqr678', 'http://example.com/6', NOW() - INTERVAL '27 HOURS'),
('stu901', 'http://example.com/7', NOW() - INTERVAL '28 HOURS'),
('vwx234', 'http://example.com/8', NOW() - INTERVAL '29 HOURS'),
('yz5678', 'http://example.com/9', NOW() - INTERVAL '30 HOURS'),
('abc890', 'http://example.com/10', NOW() - INTERVAL '31 HOURS');
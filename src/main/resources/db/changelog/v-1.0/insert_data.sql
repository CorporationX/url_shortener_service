INSERT INTO url (hash, url, created_at)
VALUES ('abc123', 'https://example.com', CURRENT_TIMESTAMP - INTERVAL '6 months'),
       ('xyz789', 'https://another-example.com', CURRENT_TIMESTAMP - INTERVAL '2 years'),
       ('def456', 'https://third-example.com', CURRENT_TIMESTAMP - INTERVAL '1 year 1 month');

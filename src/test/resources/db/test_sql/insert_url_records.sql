INSERT INTO url (hash, url, created_at)
VALUES
    ('hash1', 'youtube.com', CURRENT_TIMESTAMP - INTERVAL '3 days'),
    ('hash2', 'https://google.com', CURRENT_TIMESTAMP - INTERVAL '1 month'),
    ('hash3', 'http://dns-shop.ru', CURRENT_TIMESTAMP - INTERVAL '2 years'),
    ('hash4', 'http://mvideo.ru', CURRENT_TIMESTAMP - INTERVAL '3 years'),
    ('hash5', 'steamcommunity.com', CURRENT_TIMESTAMP - INTERVAL '1 month');

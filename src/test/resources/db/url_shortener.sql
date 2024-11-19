CREATE TABLE hashes
(
    hash VARCHAR(6) PRIMARY KEY
);

CREATE TABLE url
(
    hash       VARCHAR(6) PRIMARY KEY,
    url        VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_DATE
);

CREATE SEQUENCE unique_number_seq
    START WITH 2000000000
    INCREMENT BY 1
    NO MAXVALUE;

INSERT INTO public.hashes (hash) VALUES('XZHO');
INSERT INTO public.hashes (hash) VALUES('XZHP');
INSERT INTO public.hashes (hash) VALUES('XZHQ');
INSERT INTO public.hashes (hash) VALUES('XZHR');
INSERT INTO public.hashes (hash) VALUES('XZHS');
INSERT INTO public.hashes (hash) VALUES('XZHT');
INSERT INTO public.hashes (hash) VALUES('XZHU');
INSERT INTO public.hashes (hash) VALUES('XZHV');

INSERT INTO public.url (hash, url, created_at) VALUES('XZHO', 'https://vkontakte.ru', '2000-01-01');
INSERT INTO public.url (hash, url, created_at) VALUES('XZHP', 'https://ok.ru', '2000-01-01');
INSERT INTO public.url (hash, url, created_at) VALUES('XZHQ', 'https://mail.ru', '2000-01-01');
INSERT INTO public.url (hash, url, created_at) VALUES('XZHR', 'https://drom.ru', '2000-01-01');
INSERT INTO public.url (hash, url, created_at) VALUES('XZHS', 'https://sportbox.ru', '2000-01-01');
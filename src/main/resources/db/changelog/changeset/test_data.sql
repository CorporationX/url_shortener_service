--TRUNCATE TABLE balance, savings_account RESTART IDENTITY;

--
INSERT INTO url (url, hash) VALUES
  ('https://google.com', 'd4q0'),
  ('https://amazon.com', 'aaa');
----
--INSERT INTO "owner" ("type",owner_id,created_at,updated_at) VALUES
--	 ('USER',2,'2024-12-21 15:55:36.852197+03','2024-12-21 15:55:36.852463+03'),
--	 ('USER',1,'2024-12-21 15:56:49.972093+03','2024-12-21 15:56:49.972131+03'),
--	 ('USER',5,'2024-12-21 15:58:57.894621+03','2024-12-21 15:58:57.894676+03'),
--	 ('USER',6,'2024-12-21 16:19:47.179467+03','2024-12-21 16:19:47.179725+03'),
--	 ('USER',3,'2025-01-09 00:41:35.530674+03','2025-01-09 00:41:35.530711+03');
--
--INSERT INTO account (account_number,owner_id,"type",currency,status,created_at,updated_at,closed_at,"version",is_verified,notes) VALUES
--	 ('4555117129248353903',2,'SAVINGS','USD','PENDING','2025-01-09 00:41:05.59323+03','2025-01-09 00:41:05.593296+03',NULL,0,false,'account ownerId =1 type=user !!!!'),
--	 ('82624469383156851',1,'SAVINGS','USD','PENDING','2025-01-09 00:41:23.920605+03','2025-01-09 00:41:23.920633+03',NULL,0,false,'account ownerId =2 type=user !!!!'),
--	 ('837503082776',3,'SAVINGS','USD','PENDING','2025-01-09 00:41:35.537259+03','2025-01-09 00:41:35.537293+03',NULL,0,false,'account ownerId =3 type=user !!!!');
--
--INSERT INTO balance (account_id,authorized_value,actual_value) VALUES
--	 (1,100000,100000),
--	 (2,118283,118283),
--	 (3,115282,115282);
--
--INSERT INTO savings_account (account_id,tariff_history,last_income_at) VALUES
--	 (1,'[2]','2025-01-10'),
--	 (2,'[1, 3]','2025-01-10'),
--	 (3,'[1]','2025-01-10');


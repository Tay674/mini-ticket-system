-- Wir fügen 5 Standard-Boards hinzu. 
-- Als Besitzer nehmen wir den ersten User, der die Rolle 'ADMIN' hat.
INSERT INTO projects (id, name, description, owner_id, created_at)
SELECT gen_random_uuid(), 'Anwendungsfragen', 'Fragen zu Software, Lizenzen und Bedienung', id, NOW() FROM users WHERE role = 'ADMIN' LIMIT 1;

INSERT INTO projects (id, name, description, owner_id, created_at)
SELECT gen_random_uuid(), 'Hardware & Ausstattung', 'Neue Laptops, Monitore, Tastaturen und Reparaturen', id, NOW() FROM users WHERE role = 'ADMIN' LIMIT 1;

INSERT INTO projects (id, name, description, owner_id, created_at)
SELECT gen_random_uuid(), 'Zugriffsrechte & Accounts', 'Passwort-Resets, VPN-Zugang, neue User', id, NOW() FROM users WHERE role = 'ADMIN' LIMIT 1;

INSERT INTO projects (id, name, description, owner_id, created_at)
SELECT gen_random_uuid(), 'Büro & Facility', 'Defekte Möbel, Heizung, Schlüssel, Umzüge', id, NOW() FROM users WHERE role = 'ADMIN' LIMIT 1;

INSERT INTO projects (id, name, description, owner_id, created_at)
SELECT gen_random_uuid(), 'Allgemeine Anfragen', 'Sonstige Anfragen, die nirgendwo anders reinpassen', id, NOW() FROM users WHERE role = 'ADMIN' LIMIT 1;
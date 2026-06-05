INSERT INTO partners (id, name, credit_limit)
VALUES (1, 'Distribuidora Alpha', 50000.00)
    ON CONFLICT (id) DO NOTHING;

INSERT INTO partners (id, name, credit_limit)
VALUES (2, 'Logística Beta', 1500.00)
    ON CONFLICT (id) DO NOTHING;

INSERT INTO partners (id, name, credit_limit)
VALUES (3, 'Comércio MM', 3000.00)
    ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('partners', 'id'), COALESCE(MAX(id), 1)) FROM partners;

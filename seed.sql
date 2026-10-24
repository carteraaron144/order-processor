-- Sample inventory from README.md.
-- Run after schema.sql with: psql -d order_processor -f seed.sql
-- Orders are not seeded: postNewOrder() should insert them, so intake validation runs.

INSERT INTO items (sku, item_name, price, quantity_in_stock) VALUES
    ('A100', 'Keyboard',  49.99, 10),
    ('B200', 'Mouse',     19.99, 25),
    ('C300', 'Monitor',  189.00,  4),
    ('D400', 'USB Cable',  5.49, 50);

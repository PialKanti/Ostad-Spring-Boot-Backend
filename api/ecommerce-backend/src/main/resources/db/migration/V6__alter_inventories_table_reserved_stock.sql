ALTER TABLE inventories
    RENAME COLUMN quantity to total_quantity;

ALTER TABLE inventories
    ADD COLUMN reserved_quantity INTEGER DEFAULT 0 CHECK (reserved_quantity >= 0);
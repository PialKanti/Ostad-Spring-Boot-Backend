-- Stock table: Primary stock tracking
CREATE TABLE stock
(
    id                 BIGSERIAL PRIMARY KEY,
    product_id         BIGINT    NOT NULL UNIQUE,
    quantity_available INT       NOT NULL DEFAULT 0,
    quantity_reserved  INT       NOT NULL DEFAULT 0,
    reorder_level      INT       NOT NULL DEFAULT 0,
    updated_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Stock reservation table: Temporary reservations for orders
CREATE TABLE stock_reservation
(
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT      NOT NULL,
    quantity    INT         NOT NULL,
    status      VARCHAR(20) NOT NULL,
    order_id    BIGINT      NULL,
    expiry_time TIMESTAMP   NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version     BIGINT      NOT NULL DEFAULT 0
);

-- Indexes for stock_reservation
CREATE INDEX idx_stock_reservation_status ON stock_reservation (status);
CREATE INDEX idx_stock_reservation_expiry_time ON stock_reservation (expiry_time);

-- Inventory transaction table: Audit log of all stock changes
CREATE TABLE inventory_transaction
(
    id               BIGSERIAL PRIMARY KEY,
    product_id       BIGINT      NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    quantity_changed INT         NOT NULL,
    previous_qty     INT         NOT NULL,
    new_qty          INT         NOT NULL,
    reference_id     BIGINT      NULL,
    created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

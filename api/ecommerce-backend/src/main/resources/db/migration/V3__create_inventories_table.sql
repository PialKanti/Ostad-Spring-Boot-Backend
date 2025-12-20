CREATE TABLE inventories
(
    id         BIGSERIAL PRIMARY KEY,

    product_id BIGINT  NOT NULL,

    quantity   INTEGER NOT NULL CHECK (quantity >= 0),

    version    BIGINT  NOT NULL DEFAULT 0,

    CONSTRAINT uk_inventories_product UNIQUE (product_id),

    CONSTRAINT fk_inventories_product
        FOREIGN KEY (product_id)
            REFERENCES products (id)
            ON DELETE RESTRICT
);

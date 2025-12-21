-- Create carts table
CREATE TABLE carts
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    created_at  TIMESTAMP,
    modified_at TIMESTAMP
);

-- Create cart_items table
CREATE TABLE cart_items
(
    id         BIGSERIAL PRIMARY KEY,
    cart_id    BIGINT  NOT NULL,
    product_id BIGINT  NOT NULL,
    quantity   INTEGER NOT NULL,
    unit_price NUMERIC(15, 2) NOT NULL,

    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id)
        REFERENCES carts (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id)
        REFERENCES products (id),

    CONSTRAINT uk_cart_product UNIQUE (cart_id, product_id)
);

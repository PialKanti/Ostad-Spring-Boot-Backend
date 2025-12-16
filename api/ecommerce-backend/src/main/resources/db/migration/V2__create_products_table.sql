CREATE TABLE products
(
    id          BIGSERIAL PRIMARY KEY,

    sku         VARCHAR(100)   NOT NULL UNIQUE,
    name        VARCHAR(200)   NOT NULL,
    description VARCHAR(1000),
    price       NUMERIC(15, 2) NOT NULL,
    is_active   BOOLEAN        NOT NULL DEFAULT TRUE,

    category_id BIGINT         NOT NULL REFERENCES categories (id) ON DELETE RESTRICT,

    image_url   VARCHAR(250),

    created_at  TIMESTAMP,
    modified_at TIMESTAMP,
    created_by  BIGINT,
    modified_by BIGINT
);
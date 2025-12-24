CREATE TABLE payment_history
(
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT        NOT NULL,
    session_id   VARCHAR(255)  NOT NULL UNIQUE,
    payment_link VARCHAR(1024) NOT NULL,
    status       VARCHAR(50)   NOT NULL,
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at  TIMESTAMP     NULL,
    CONSTRAINT fk_payment_history_order FOREIGN KEY (order_id) REFERENCES orders (id)
);
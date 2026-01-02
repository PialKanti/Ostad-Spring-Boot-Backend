CREATE TABLE refresh_tokens
(
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(255) NOT NULL UNIQUE,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    expiry_date TIMESTAMP    NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    revoked_at  TIMESTAMP    NULL,
    user_id     BIGINT       NOT NULL,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
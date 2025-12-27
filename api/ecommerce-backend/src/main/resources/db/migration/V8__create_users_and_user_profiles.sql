CREATE TABLE users
(
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(50),
    email       VARCHAR(100) NOT NULL UNIQUE,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP,
    modified_at TIMESTAMP,
    created_by  BIGINT,
    modified_by BIGINT
);

CREATE TABLE user_profiles
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT UNIQUE,
    phone_number  VARCHAR(20),
    date_of_birth DATE,
    gender        VARCHAR(10),
    created_at    TIMESTAMP,
    modified_at   TIMESTAMP,
    CONSTRAINT fk_user_profile_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
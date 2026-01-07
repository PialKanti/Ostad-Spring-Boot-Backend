-- Seed users with BCrypt hashed password for 'password123'
-- Hash: $2a$10$DVaZleF3yUCi90ZV7FkVpOgYuq/ba8gawtr3lq8yFqUbKvhCYfhsW

INSERT INTO users (first_name, last_name, email, username, password, created_at)
VALUES ('Super', 'Admin', 'superadmin@example.com', 'superadmin',
        '$2a$10$DVaZleF3yUCi90ZV7FkVpOgYuq/ba8gawtr3lq8yFqUbKvhCYfhsW', CURRENT_TIMESTAMP),
       ('Admin', 'User', 'admin@example.com', 'admin', '$2a$10$DVaZleF3yUCi90ZV7FkVpOgYuq/ba8gawtr3lq8yFqUbKvhCYfhsW',
        CURRENT_TIMESTAMP),
       ('Seller', 'User', 'seller@example.com', 'seller',
        '$2a$10$DVaZleF3yUCi90ZV7FkVpOgYuq/ba8gawtr3lq8yFqUbKvhCYfhsW', CURRENT_TIMESTAMP),
       ('Customer', 'User', 'customer@example.com', 'customer',
        '$2a$10$DVaZleF3yUCi90ZV7FkVpOgYuq/ba8gawtr3lq8yFqUbKvhCYfhsW', CURRENT_TIMESTAMP);

-- Map users to roles
INSERT INTO user_roles (user_id, role_id)
VALUES ((SELECT id FROM users WHERE username = 'superadmin'), (SELECT id FROM roles WHERE code = 'SUPER_ADMIN')),
       ((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE code = 'ADMIN')),
       ((SELECT id FROM users WHERE username = 'seller'), (SELECT id FROM roles WHERE code = 'SELLER')),
       ((SELECT id FROM users WHERE username = 'customer'), (SELECT id FROM roles WHERE code = 'CUSTOMER'));

-- Create profile for customer role user only
INSERT INTO user_profiles (user_id, phone_number, date_of_birth, gender, created_at)
VALUES ((SELECT id FROM users WHERE username = 'customer'), '1234567890', '1990-01-01', 'MALE', CURRENT_TIMESTAMP);

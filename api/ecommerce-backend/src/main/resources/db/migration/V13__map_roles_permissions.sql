-- SUPER_ADMIN gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE r.code = 'SUPER_ADMIN';

-- ADMIN gets Product + Category Admin + Payment monitoring
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p ON p.code IN (
                                          'CREATE_PRODUCT', 'UPDATE_PRODUCT', 'DELETE_PRODUCT', 'UPDATE_INVENTORY',
                                          'CREATE_CATEGORY', 'UPDATE_CATEGORY', 'DELETE_CATEGORY',
                                          'TOGGLE_CATEGORY_STATUS', 'VIEW_CATEGORY',
                                          'PAYMENT_SUCCESS', 'PAYMENT_CANCEL'
    )
WHERE r.code = 'ADMIN';

-- SELLER gets limited product + category view + payment monitoring
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p ON p.code IN (
                                          'CREATE_PRODUCT', 'UPDATE_PRODUCT', 'UPDATE_INVENTORY', 'VIEW_CATEGORY',
                                          'PAYMENT_SUCCESS', 'PAYMENT_CANCEL'
    )
WHERE r.code = 'SELLER';

-- CUSTOMER gets shopping + payment permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p ON p.code IN (
                                          'VIEW_PRODUCTS', 'VIEW_CATEGORY', 'VIEW_CART', 'ADD_TO_CART',
                                          'CHECKOUT', 'PAYMENT_SUCCESS', 'PAYMENT_CANCEL'
    )
WHERE r.code = 'CUSTOMER';

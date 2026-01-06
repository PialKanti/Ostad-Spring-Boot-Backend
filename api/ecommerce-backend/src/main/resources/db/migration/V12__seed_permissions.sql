-- Seed permissions
INSERT INTO permissions (name, code)
VALUES
    -- Product Admin
    ('Create Product', 'CREATE_PRODUCT'),
    ('Update Product', 'UPDATE_PRODUCT'),
    ('Delete Product', 'DELETE_PRODUCT'),
    ('Update Inventory', 'UPDATE_INVENTORY'),

    -- Category Admin
    ('Create Category', 'CREATE_CATEGORY'),
    ('Update Category', 'UPDATE_CATEGORY'),
    ('Delete Category', 'DELETE_CATEGORY'),
    ('Toggle Category Status', 'TOGGLE_CATEGORY_STATUS'),
    ('View Category', 'VIEW_CATEGORY'),

    -- Customer / General
    ('View Products', 'VIEW_PRODUCTS'),
    ('View Cart', 'VIEW_CART'),
    ('Add To Cart', 'ADD_TO_CART'),

    -- Payments
    ('Checkout', 'CHECKOUT'),
    ('Payment Success', 'PAYMENT_SUCCESS'),
    ('Payment Cancel', 'PAYMENT_CANCEL');

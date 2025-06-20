-- Add audit and additional features
-- Version: 2.0
-- Description: Add audit trails, user roles, and product reviews

-- Create user_roles table
CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    granted_by BIGINT,
    is_active BOOLEAN DEFAULT true,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (granted_by) REFERENCES users(id),
    UNIQUE(user_id, role_name)
);

-- Create product_reviews table
CREATE TABLE product_reviews (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(200),
    comment TEXT,
    is_verified_purchase BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE(product_id, user_id)
);

-- Create audit_log table
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    record_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL, -- INSERT, UPDATE, DELETE
    old_values JSONB,
    new_values JSONB,
    changed_by BIGINT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (changed_by) REFERENCES users(id)
);

-- Add discount and promotion support
ALTER TABLE products ADD COLUMN discount_percentage DECIMAL(5,2) DEFAULT 0 CHECK (discount_percentage >= 0 AND discount_percentage <= 100);
ALTER TABLE products ADD COLUMN promotion_start_date TIMESTAMP;
ALTER TABLE products ADD COLUMN promotion_end_date TIMESTAMP;

-- Add customer addresses
CREATE TABLE user_addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_type VARCHAR(20) NOT NULL, -- BILLING, SHIPPING
    street_address VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state_province VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100) NOT NULL,
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create payment_methods table for saved payment methods
CREATE TABLE payment_methods (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    method_type VARCHAR(20) NOT NULL, -- CREDIT_CARD, PAYPAL, STRIPE
    method_name VARCHAR(100) NOT NULL,
    is_default BOOLEAN DEFAULT false,
    encrypted_data TEXT, -- Encrypted payment details
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Add indexes for new tables
CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_name);
CREATE INDEX idx_product_reviews_product ON product_reviews(product_id);
CREATE INDEX idx_product_reviews_user ON product_reviews(user_id);
CREATE INDEX idx_audit_log_table ON audit_log(table_name);
CREATE INDEX idx_audit_log_record ON audit_log(record_id);
CREATE INDEX idx_user_addresses_user ON user_addresses(user_id);
CREATE INDEX idx_payment_methods_user ON payment_methods(user_id);

-- Insert default roles
INSERT INTO user_roles (user_id, role_name, granted_by) VALUES
(3, 'ADMIN', 3),
(1, 'CUSTOMER', 3),
(2, 'CUSTOMER', 3);

-- Insert sample addresses
INSERT INTO user_addresses (user_id, address_type, street_address, city, state_province, postal_code, country, is_default) VALUES
(1, 'BILLING', '123 Main St', 'New York', 'NY', '10001', 'USA', true),
(1, 'SHIPPING', '123 Main St', 'New York', 'NY', '10001', 'USA', true),
(2, 'BILLING', '456 Oak Ave', 'Los Angeles', 'CA', '90210', 'USA', true),
(2, 'SHIPPING', '456 Oak Ave', 'Los Angeles', 'CA', '90210', 'USA', true);

-- Insert sample reviews
INSERT INTO product_reviews (product_id, user_id, rating, title, comment, is_verified_purchase) VALUES
(1, 1, 5, 'Excellent laptop!', 'Great performance and build quality. Highly recommended!', true),
(1, 2, 4, 'Good value', 'Works well for my needs, though a bit heavy.', true),
(2, 1, 5, 'Amazing sound quality', 'Best headphones I have ever owned!', true),
(3, 2, 4, 'Comfortable fit', 'Nice fabric and good fit. Will buy more colors.', true); 
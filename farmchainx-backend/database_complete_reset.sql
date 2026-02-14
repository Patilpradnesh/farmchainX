-- FARMCHAINX DATABASE COMPLETE RESET SCRIPT
-- IMPORTANT: This script is destructive. Back up your data before running.
-- Usage (MySQL client):
--   mysql -u root -p < database_complete_reset.sql
-- Or paste into MySQL Workbench and execute.

SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS farmchainx_db;
CREATE DATABASE farmchainx_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE farmchainx_db;

-- ===== TABLE: users =====
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN','FARMER','DISTRIBUTOR','RETAILER','CONSUMER') NOT NULL,
    status ENUM('PENDING','APPROVED','REJECTED','SUSPENDED') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===== PROFILE TABLES (one-to-one with users.id) =====
CREATE TABLE farmer_profiles (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    verification_status ENUM('PENDING','APPROVED','REJECTED','SUSPENDED') NOT NULL DEFAULT 'PENDING',
    farm_name VARCHAR(255),
    location VARCHAR(255),
    land_area DOUBLE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE distributor_profiles (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    verification_status ENUM('PENDING','APPROVED','REJECTED','SUSPENDED') NOT NULL DEFAULT 'PENDING',
    company_name VARCHAR(255),
    license_number VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE retailer_profiles (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    verification_status ENUM('PENDING','APPROVED','REJECTED','SUSPENDED') NOT NULL DEFAULT 'PENDING',
    store_name VARCHAR(255),
    store_location VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE consumer_profiles (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    verification_status ENUM('PENDING','APPROVED','REJECTED','SUSPENDED') NOT NULL DEFAULT 'PENDING',
    full_name VARCHAR(255),
    address TEXT,
    phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===== TABLE: crops =====
CREATE TABLE crops (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    crop_name VARCHAR(255) NOT NULL,
    quantity DOUBLE NOT NULL,
    harvest_date DATETIME NOT NULL,
    location VARCHAR(255) NOT NULL,
    certificate_ref VARCHAR(255),
    blockchain_hash VARCHAR(255) NOT NULL UNIQUE,
    current_owner_id BIGINT NOT NULL,
    current_owner_role ENUM('ADMIN','FARMER','DISTRIBUTOR','RETAILER','CONSUMER') NOT NULL,
    crop_state ENUM('CREATED','LISTED','ORDERED','SHIPPED','DELIVERED','CLOSED') NOT NULL DEFAULT 'CREATED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (current_owner_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===== TABLE: orders =====
CREATE TABLE orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    crop_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    order_state ENUM('PLACED','ACCEPTED','SHIPPED','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PLACED',
    requested_quantity DOUBLE,
    offered_price DOUBLE,
    delivery_address TEXT,
    notes TEXT,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (crop_id) REFERENCES crops(id) ON DELETE CASCADE,
    FOREIGN KEY (buyer_id) REFERENCES users(id),
    FOREIGN KEY (seller_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===== TABLE: disputes =====
CREATE TABLE disputes (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    crop_id BIGINT NOT NULL,
    order_id BIGINT,
    raised_by_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    status ENUM('OPEN','ESCALATED','RESOLVED','CLOSED','REJECTED') NOT NULL DEFAULT 'OPEN',
    resolution TEXT,
    admin_notes TEXT,
    evidence TEXT,
    escalation_reason TEXT,
    closure_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    escalated_at TIMESTAMP NULL,
    closed_at TIMESTAMP NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (crop_id) REFERENCES crops(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (raised_by_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===== TABLE: crop_history =====
CREATE TABLE crop_history (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    crop_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    from_state VARCHAR(50),
    to_state VARCHAR(50),
    performed_by_id BIGINT NOT NULL,
    performed_by_role ENUM('ADMIN','FARMER','DISTRIBUTOR','RETAILER','CONSUMER') NOT NULL,
    details TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (crop_id) REFERENCES crops(id) ON DELETE CASCADE,
    FOREIGN KEY (performed_by_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===== TABLE: notifications =====
CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===== TABLE: blockchain_records =====
CREATE TABLE blockchain_records (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    transaction_hash VARCHAR(255) NOT NULL UNIQUE,
    crop_id BIGINT,
    block_number BIGINT,
    transaction_type VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===== TABLE: audit_log =====
CREATE TABLE audit_log (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_name VARCHAR(255),
    entity_id BIGINT,
    action VARCHAR(100),
    performed_by BIGINT,
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (performed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===== INDEXES =====
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);

CREATE INDEX idx_crops_state ON crops(crop_state);
CREATE INDEX idx_crops_owner ON crops(current_owner_id);
CREATE INDEX idx_crops_hash ON crops(blockchain_hash);

CREATE INDEX idx_orders_state ON orders(order_state);
CREATE INDEX idx_orders_buyer ON orders(buyer_id);
CREATE INDEX idx_orders_seller ON orders(seller_id);

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(is_read);

-- ===== SAMPLE / DEV SEEDS (optional) =====
-- NOTE: For security, prefer creating users via the /api/auth/register endpoint, which encodes the password properly.
-- If you must insert an admin via SQL, follow these steps locally:
-- 1) Generate a BCrypt hash on your machine (node example):
--    node -e "const bcrypt=require('bcrypt'); bcrypt.hash('admin@123',10).then(h=>console.log(h))"
-- 2) Replace <BCRYPT_HASH> below with the printed hash and run the INSERT.
-- INSERT INTO users (email,password,role,status,created_at) VALUES
-- ('admin@example.com', '<BCRYPT_HASH>', 'ADMIN', 'APPROVED', NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- Done. Database farmchainx_db created and initialized.
SELECT '✅ Database farmchainx_db created successfully!' AS status;
SELECT '✅ All tables created with proper foreign key constraints' AS schema_status;
SELECT '✅ Ready for Spring Boot application startup' AS ready_status;


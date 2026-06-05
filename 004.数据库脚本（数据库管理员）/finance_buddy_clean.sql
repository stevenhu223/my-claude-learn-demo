-- Finance Buddy Database Schema
-- MySQL 9.7.0
-- ============================================

CREATE DATABASE IF NOT EXISTS finance_buddy
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE finance_buddy;

-- ============================================
-- Table 1: users
-- ============================================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    username        VARCHAR(50) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    nickname        VARCHAR(100),
    avatar_url      VARCHAR(500),
    monthly_budget  DECIMAL(12,2) DEFAULT 0.00,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      DATETIME NULL,
    INDEX idx_username (username),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table 2: categories
-- ============================================
DROP TABLE IF EXISTS categories;
CREATE TABLE categories (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    type            TINYINT NOT NULL COMMENT '1=expense, 2=income',
    name            VARCHAR(50) NOT NULL,
    icon            VARCHAR(100),
    sort_order      INT DEFAULT 0,
    is_system       TINYINT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      DATETIME NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_user_type (user_id, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table 3: payment_methods
-- ============================================
DROP TABLE IF EXISTS payment_methods;
CREATE TABLE payment_methods (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    name            VARCHAR(50) NOT NULL,
    icon            VARCHAR(100),
    sort_order      INT DEFAULT 0,
    is_system       TINYINT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      DATETIME NULL,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table 4: accounts
-- ============================================
DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    name            VARCHAR(100) NOT NULL,
    icon            VARCHAR(100),
    balance         DECIMAL(12,2) DEFAULT 0.00,
    note            VARCHAR(500),
    sort_order      INT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      DATETIME NULL,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table 5: records
-- ============================================
DROP TABLE IF EXISTS records;
CREATE TABLE records (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    type            TINYINT NOT NULL COMMENT '1=expense, 2=income',
    amount          DECIMAL(12,2) NOT NULL,
    category_id     BIGINT NOT NULL,
    account_id      BIGINT,
    payment_method_id BIGINT,
    record_date     DATE NOT NULL,
    remark          VARCHAR(500),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      DATETIME NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_record_date (record_date),
    INDEX idx_type (type),
    INDEX idx_category_id (category_id),
    INDEX idx_user_date (user_id, record_date),
    INDEX idx_user_type_date (user_id, type, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table 6: budgets
-- ============================================
DROP TABLE IF EXISTS budgets;
CREATE TABLE budgets (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    ym              VARCHAR(7) NOT NULL,
    budget_amount   DECIMAL(12,2) NOT NULL,
    spent_amount    DECIMAL(12,2) DEFAULT 0.00,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_month (user_id, ym),
    INDEX idx_user_id (user_id),
    INDEX idx_ym (ym)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table 7: fixed_expenses
-- ============================================
DROP TABLE IF EXISTS fixed_expenses;
CREATE TABLE fixed_expenses (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    name            VARCHAR(100) NOT NULL,
    amount          DECIMAL(12,2) NOT NULL,
    category_id     BIGINT NOT NULL,
    payment_method_id BIGINT,
    cycle_type      TINYINT NOT NULL COMMENT '1=monthly, 2=quarterly, 3=yearly',
    cycle_day       INT NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE NULL,
    is_active       TINYINT DEFAULT 1,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      DATETIME NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Table 8: sessions
-- ============================================
DROP TABLE IF EXISTS sessions;
CREATE TABLE sessions (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    session_token   VARCHAR(255) NOT NULL UNIQUE,
    expires_at      DATETIME NOT NULL,
    device_info     VARCHAR(500),
    ip_address      VARCHAR(50),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_session_token (session_token),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- System default data (user_id = 0)
-- ============================================

INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES
(0, 1, '餐饮', 'restaurant', 1, 1),
(0, 1, '交通', 'directions_car', 2, 1),
(0, 1, '购物', 'shopping_bag', 3, 1),
(0, 1, '居住', 'home', 4, 1),
(0, 1, '服饰', 'checkroom', 5, 1),
(0, 1, '娱乐', 'styler', 6, 1),
(0, 1, '医疗', 'medical_services', 7, 1),
(0, 1, '教育', 'school', 8, 1),
(0, 1, '通讯', 'smartphone', 9, 1),
(0, 1, '人情', 'favorite', 10, 1),
(0, 1, '其他', 'more_horiz', 11, 1);

INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES
(0, 2, '工资', 'payments', 1, 1),
(0, 2, '奖金', 'card_giftcard', 2, 1),
(0, 2, '投资收益', 'trending_up', 3, 1),
(0, 2, '兼职收入', 'work', 4, 1),
(0, 2, '产品销售', 'storefront', 5, 1),
(0, 2, '服务收入', 'handyman', 6, 1),
(0, 2, '其他收入', 'more_horiz', 7, 1);

INSERT INTO payment_methods (user_id, name, icon, sort_order, is_system) VALUES
(0, '现金', 'paid', 1, 1),
(0, '支付宝', 'account_balance_wallet', 2, 1),
(0, '微信支付', 'qr_code', 3, 1),
(0, '银行卡', 'credit_card', 4, 1),
(0, '对公账户', 'account_balance', 5, 1);
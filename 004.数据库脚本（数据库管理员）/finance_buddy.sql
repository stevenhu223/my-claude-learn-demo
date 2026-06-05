-- ============================================
-- 财务管家 V1.0 数据库设计文档
-- 版本: 1.0.0
-- 创建日期: 2026-06-05
-- 数据库类型: MySQL 9.7.0
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS finance_buddy
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE finance_buddy;

-- ============================================
-- 表1: 用户表 (users)
-- 功能: 存储用户基本信息
-- ============================================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username        VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password_hash   VARCHAR(255) NOT NULL COMMENT '密码哈希',
    nickname        VARCHAR(100) COMMENT '用户昵称',
    avatar_url      VARCHAR(500) COMMENT '头像URL',
    monthly_budget  DECIMAL(12,2) DEFAULT 0.00 COMMENT '月度预算',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at      DATETIME NULL COMMENT '删除时间(软删除)',
    INDEX idx_username (username),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 表2: 分类表 (categories)
-- 功能: 存储收支分类信息
-- ============================================
DROP TABLE IF EXISTS categories;
CREATE TABLE categories (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID(0为系统预设)',
    type            TINYINT NOT NULL COMMENT '类型: 1=支出, 2=收入',
    name            VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon            VARCHAR(100) COMMENT '图标标识',
    sort_order      INT DEFAULT 0 COMMENT '排序顺序',
    is_system       TINYINT DEFAULT 0 COMMENT '是否系统预设: 0=自定义, 1=系统预设',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at      DATETIME NULL COMMENT '删除时间(软删除)',
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_user_type (user_id, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- ============================================
-- 表3: 支付方式表 (payment_methods)
-- 功能: 存储支付方式信息
-- ============================================
DROP TABLE IF EXISTS payment_methods;
CREATE TABLE payment_methods (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付方式ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID(0为系统预设)',
    name            VARCHAR(50) NOT NULL COMMENT '支付方式名称',
    icon            VARCHAR(100) COMMENT '图标标识',
    sort_order      INT DEFAULT 0 COMMENT '排序顺序',
    is_system       TINYINT DEFAULT 0 COMMENT '是否系统预设: 0=自定义, 1=系统预设',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at      DATETIME NULL COMMENT '删除时间(软删除)',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付方式表';

-- ============================================
-- 表4: 账户表 (accounts)
-- 功能: 存储用户账户信息
-- ============================================
DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账户ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    name            VARCHAR(100) NOT NULL COMMENT '账户名称',
    icon            VARCHAR(100) COMMENT '图标标识',
    balance         DECIMAL(12,2) DEFAULT 0.00 COMMENT '账户余额',
    note            VARCHAR(500) COMMENT '备注',
    sort_order      INT DEFAULT 0 COMMENT '排序顺序',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at      DATETIME NULL COMMENT '删除时间(软删除)',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户表';

-- ============================================
-- 表5: 记账记录表 (records)
-- 功能: 存储所有收支记录
-- ============================================
DROP TABLE IF EXISTS records;
CREATE TABLE records (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    type            TINYINT NOT NULL COMMENT '类型: 1=支出, 2=收入',
    amount          DECIMAL(12,2) NOT NULL COMMENT '金额',
    category_id     BIGINT NOT NULL COMMENT '分类ID',
    account_id      BIGINT COMMENT '账户ID',
    payment_method_id BIGINT COMMENT '支付方式ID',
    record_date     DATE NOT NULL COMMENT '记录日期',
    remark          VARCHAR(500) COMMENT '备注说明',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at      DATETIME NULL COMMENT '删除时间(软删除)',
    INDEX idx_user_id (user_id),
    INDEX idx_record_date (record_date),
    INDEX idx_type (type),
    INDEX idx_category_id (category_id),
    INDEX idx_user_date (user_id, record_date),
    INDEX idx_user_type_date (user_id, type, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记账记录表';

-- ============================================
-- 表6: 预算表 (budgets)
-- 功能: 存储用户预算设置
-- ============================================
DROP TABLE IF EXISTS budgets;
CREATE TABLE budgets (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预算ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    year_month      VARCHAR(7) NOT NULL COMMENT '预算月份(格式: YYYY-MM)',
    budget_amount   DECIMAL(12,2) NOT NULL COMMENT '预算金额',
    spent_amount    DECIMAL(12,2) DEFAULT 0.00 COMMENT '已花费金额',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_month (user_id, year_month),
    INDEX idx_user_id (user_id),
    INDEX idx_year_month (year_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预算表';

-- ============================================
-- 表7: 固定支出表 (fixed_expenses)
-- 功能: 存储周期固定支出(如房租等)
-- ============================================
DROP TABLE IF EXISTS fixed_expenses;
CREATE TABLE fixed_expenses (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '固定支出ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    name            VARCHAR(100) NOT NULL COMMENT '支出名称',
    amount          DECIMAL(12,2) NOT NULL COMMENT '支出金额',
    category_id     BIGINT NOT NULL COMMENT '分类ID',
    payment_method_id BIGINT COMMENT '支付方式ID',
    cycle_type      TINYINT NOT NULL COMMENT '周期类型: 1=每月, 2=每季度, 3=每年',
    cycle_day       INT NOT NULL COMMENT '周期日期(每月几号/每季度几号/每年几号)',
    start_date      DATE NOT NULL COMMENT '开始日期',
    end_date        DATE NULL COMMENT '结束日期(NULL表示永久)',
    is_active       TINYINT DEFAULT 1 COMMENT '是否激活: 0=停用, 1=激活',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at      DATETIME NULL COMMENT '删除时间(软删除)',
    INDEX idx_user_id (user_id),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='固定支出表';

-- ============================================
-- 表8: 登录会话表 (sessions)
-- 功能: 存储用户登录会话信息
-- ============================================
DROP TABLE IF EXISTS sessions;
CREATE TABLE sessions (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    session_token   VARCHAR(255) NOT NULL UNIQUE COMMENT '会话Token',
    expires_at      DATETIME NOT NULL COMMENT '过期时间',
    device_info     VARCHAR(500) COMMENT '设备信息',
    ip_address      VARCHAR(50) COMMENT 'IP地址',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_session_token (session_token),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录会话表';

-- ============================================
-- 系统预设数据初始化
-- ============================================

-- 插入支出分类预设数据 (user_id = 0 表示系统预设)
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

-- 插入收入分类预设数据
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES
(0, 2, '工资', 'payments', 1, 1),
(0, 2, '奖金', 'card_giftcard', 2, 1),
(0, 2, '投资收益', 'trending_up', 3, 1),
(0, 2, '兼职收入', 'work', 4, 1),
(0, 2, '产品销售', 'storefront', 5, 1),
(0, 2, '服务收入', 'handyman', 6, 1),
(0, 2, '其他收入', 'more_horiz', 7, 1);

-- 插入支付方式预设数据
INSERT INTO payment_methods (user_id, name, icon, sort_order, is_system) VALUES
(0, '现金', 'paid', 1, 1),
(0, '支付宝', 'account_balance_wallet', 2, 1),
(0, '微信支付', 'qr_code', 3, 1),
(0, '银行卡', 'credit_card', 4, 1),
(0, '对公账户', 'account_balance', 5, 1);

-- ============================================
-- 说明文档
-- ============================================
-- 1. 所有表均使用 InnoDB 引擎，支持事务
-- 2. 字符集使用 utf8mb4，支持 emoji 和特殊字符
-- 3. 所有表都有 deleted_at 字段，支持软删除
-- 4. user_id = 0 表示系统预设数据
-- 5. 金额字段统一使用 DECIMAL(12,2)，支持最大999999999.99
-- 6. 索引设计考虑常用查询:
--    - 按用户ID查询
--    - 按日期范围查询
--    - 按类型和日期组合查询
-- ============================================
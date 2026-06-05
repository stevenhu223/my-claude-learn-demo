# 财务管家 - 数据库设计文档

## 1. 概述

本文档描述财务管家 V1.0 的数据库设计方案，基于产品PRD和前端项目分析得出。

## 2. 数据库信息

- **数据库名称**: finance_buddy
- **数据库类型**: MySQL 9.7.0
- **字符集**: utf8mb4 (支持 emoji)
- **排序规则**: utf8mb4_unicode_ci

## 3. 表结构设计

### 3.1 用户表 (users)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 用户ID，主键自增 |
| username | VARCHAR(50) | 用户名，唯一 |
| password_hash | VARCHAR(255) | 密码哈希 |
| nickname | VARCHAR(100) | 用户昵称 |
| avatar_url | VARCHAR(500) | 头像URL |
| monthly_budget | DECIMAL(12,2) | 月度预算 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted_at | DATETIME | 删除时间(软删除) |

### 3.2 分类表 (categories)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 分类ID，主键自增 |
| user_id | BIGINT | 用户ID(0=系统预设) |
| type | TINYINT | 类型: 1=支出, 2=收入 |
| name | VARCHAR(50) | 分类名称 |
| icon | VARCHAR(100) | 图标标识 |
| sort_order | INT | 排序顺序 |
| is_system | TINYINT | 是否系统预设 |

### 3.3 支付方式表 (payment_methods)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 支付方式ID，主键自增 |
| user_id | BIGINT | 用户ID(0=系统预设) |
| name | VARCHAR(50) | 支付方式名称 |
| icon | VARCHAR(100) | 图标标识 |

### 3.4 账户表 (accounts)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 账户ID，主键自增 |
| user_id | BIGINT | 用户ID |
| name | VARCHAR(100) | 账户名称 |
| icon | VARCHAR(100) | 图标标识 |
| balance | DECIMAL(12,2) | 账户余额 |

### 3.5 记账记录表 (records)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 记录ID，主键自增 |
| user_id | BIGINT | 用户ID |
| type | TINYINT | 类型: 1=支出, 2=收入 |
| amount | DECIMAL(12,2) | 金额 |
| category_id | BIGINT | 分类ID |
| account_id | BIGINT | 账户ID |
| payment_method_id | BIGINT | 支付方式ID |
| record_date | DATE | 记录日期 |
| remark | VARCHAR(500) | 备注说明 |

### 3.6 预算表 (budgets)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 预算ID，主键自增 |
| user_id | BIGINT | 用户ID |
| year_month | VARCHAR(7) | 预算月份(YYYY-MM) |
| budget_amount | DECIMAL(12,2) | 预算金额 |
| spent_amount | DECIMAL(12,2) | 已花费金额 |

### 3.7 固定支出表 (fixed_expenses)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 固定支出ID，主键自增 |
| user_id | BIGINT | 用户ID |
| name | VARCHAR(100) | 支出名称 |
| amount | DECIMAL(12,2) | 支出金额 |
| category_id | BIGINT | 分类ID |
| cycle_type | TINYINT | 周期类型: 1=每月, 2=每季度, 3=每年 |
| cycle_day | INT | 周期日期 |

### 3.8 登录会话表 (sessions)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 会话ID，主键自增 |
| user_id | BIGINT | 用户ID |
| session_token | VARCHAR(255) | 会话Token |
| expires_at | DATETIME | 过期时间 |

## 4. ER 关系图

```
users (用户)
    │
    ├──< categories (分类)
    ├──< payment_methods (支付方式)
    ├──< accounts (账户)
    ├──< records (记账记录)
    ├──< budgets (预算)
    └──< fixed_expenses (固定支出)
            │
            └──> sessions (登录会话)
```

## 5. 预设数据

系统预置了以下数据（user_id = 0）:

### 支出分类
- 餐饮、交通、购物、居住、服饰、娱乐、医疗、教育、通讯、人情、其他

### 收入分类
- 工资、奖金、投资收益、兼职收入、产品销售、服务收入、其他收入

### 支付方式
- 现金、支付宝、微信支付、银行卡、对公账户

## 6. 使用说明

执行以下命令创建数据库和表:

```bash
docker exec -i mysql mysql -uroot -p19970714 < finance_buddy.sql
```

或登录后执行:

```sql
SOURCE finance_buddy.sql;
```

## 7. 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 2026-06-05 | 初始版本 |
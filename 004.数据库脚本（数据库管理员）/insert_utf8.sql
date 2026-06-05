SET NAMES utf8mb4;
USE finance_buddy;

DELETE FROM categories WHERE user_id = 0;
DELETE FROM payment_methods WHERE user_id = 0;

INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 1, '餐饮', 'restaurant', 1, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 1, '交通', 'directions_car', 2, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 1, '购物', 'shopping_bag', 3, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 1, '居住', 'home', 4, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 1, '服饰', 'checkroom', 5, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 1, '娱乐', 'styler', 6, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 1, '医疗', 'medical_services', 7, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 1, '教育', 'school', 8, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 1, '通讯', 'smartphone', 9, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 1, '人情', 'favorite', 10, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 1, '其他', 'more_horiz', 11, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 2, '工资', 'payments', 1, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 2, '奖金', 'card_giftcard', 2, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 2, '投资收益', 'trending_up', 3, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 2, '兼职收入', 'work', 4, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 2, '产品销售', 'storefront', 5, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 2, '服务收入', 'handyman', 6, 1);
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES (0, 2, '其他收入', 'more_horiz', 7, 1);

INSERT INTO payment_methods (user_id, name, icon, sort_order, is_system) VALUES (0, '现金', 'paid', 1, 1);
INSERT INTO payment_methods (user_id, name, icon, sort_order, is_system) VALUES (0, '支付宝', 'account_balance_wallet', 2, 1);
INSERT INTO payment_methods (user_id, name, icon, sort_order, is_system) VALUES (0, '微信支付', 'qr_code', 3, 1);
INSERT INTO payment_methods (user_id, name, icon, sort_order, is_system) VALUES (0, '银行卡', 'credit_card', 4, 1);
INSERT INTO payment_methods (user_id, name, icon, sort_order, is_system) VALUES (0, '对公账户', 'account_balance', 5, 1);
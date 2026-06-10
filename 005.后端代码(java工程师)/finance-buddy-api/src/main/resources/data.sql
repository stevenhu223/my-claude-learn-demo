-- 插入默认支出分类
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES
(0, 1, '餐饮', 'restaurant', 1, TRUE),
(0, 1, '交通', 'directions_car', 2, TRUE),
(0, 1, '购物', 'shopping_bag', 3, TRUE),
(0, 1, '居住', 'home', 4, TRUE),
(0, 1, '服饰', 'checkroom', 5, TRUE),
(0, 1, '医疗', 'medical_services', 6, TRUE),
(0, 1, '娱乐', 'sports_esports', 7, TRUE),
(0, 1, '教育', 'school', 8, TRUE),
(0, 1, '人情', 'favorite', 9, TRUE),
(0, 1, '其他', 'more_horiz', 10, TRUE);

-- 插入默认收入分类
INSERT INTO categories (user_id, type, name, icon, sort_order, is_system) VALUES
(0, 2, '工资', 'payments', 1, TRUE),
(0, 2, '奖金', 'card_giftcard', 2, TRUE),
(0, 2, '投资收益', 'trending_up', 3, TRUE),
(0, 2, '兼职', 'work', 4, TRUE),
(0, 2, '其他', 'more_horiz', 5, TRUE);

-- 插入默认支付方式
INSERT INTO payment_methods (user_id, name, icon) VALUES
(0, '现金', 'wallet'),
(0, '支付宝', 'account_balance_wallet'),
(0, '微信', 'chat'),
(0, '银行卡', 'credit_card'),
(0, '信用卡', 'credit_score');
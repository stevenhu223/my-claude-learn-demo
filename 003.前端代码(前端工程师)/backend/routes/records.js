const express = require('express');
const router = express.Router();
const pool = require('../db/database');
const { verifyToken } = require('../middleware/auth');

// 认证中间件
function authMiddleware(req, res, next) {
  const authHeader = req.headers.authorization;

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({ code: 401, message: '未登录，请先登录' });
  }

  const token = authHeader.split(' ')[1];
  const decoded = verifyToken(token);

  if (!decoded) {
    return res.status(401).json({ code: 401, message: 'Token无效或已过期' });
  }

  req.user = decoded;
  next();
}

// 创建记账记录
router.post('/', authMiddleware, async (req, res) => {
  try {
    const { type, amount, category_id, account_id, payment_method_id, record_date, remark } = req.body;
    const user_id = req.user.id;

    // 参数验证
    if (!type || !amount || !category_id || !record_date) {
      return res.status(400).json({ code: 400, message: '缺少必要参数' });
    }

    if (![1, 2].includes(parseInt(type))) {
      return res.status(400).json({ code: 400, message: '类型必须是1(支出)或2(收入)' });
    }

    // 插入记录
    const [result] = await pool.execute(
      `INSERT INTO records (user_id, type, amount, category_id, account_id, payment_method_id, record_date, remark)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
      [user_id, type, amount, category_id, account_id || null, payment_method_id || null, record_date, remark || '']
    );

    res.status(201).json({
      code: 201,
      message: '创建成功',
      data: {
        id: result.insertId,
        user_id,
        type,
        amount,
        category_id,
        record_date,
        remark
      }
    });
  } catch (error) {
    console.error('Create record error:', error);
    res.status(500).json({ code: 500, message: '服务器内部错误' });
  }
});

// 获取记账记录列表
router.get('/', authMiddleware, async (req, res) => {
  try {
    const user_id = req.user.id;
    const { type, start_date, end_date, limit = 50, offset = 0 } = req.query;

    let whereClause = 'WHERE r.user_id = ? AND r.deleted_at IS NULL';
    let params = [user_id];

    if (type) {
      whereClause += ' AND r.type = ?';
      params.push(type);
    }

    if (start_date) {
      whereClause += ' AND r.record_date >= ?';
      params.push(start_date);
    }

    if (end_date) {
      whereClause += ' AND r.record_date <= ?';
      params.push(end_date);
    }

    // 获取记录列表
    const [rows] = await pool.query(
      `SELECT r.*, c.name as category_name, c.icon as category_icon,
              pm.name as payment_method_name
       FROM records r
       LEFT JOIN categories c ON r.category_id = c.id
       LEFT JOIN payment_methods pm ON r.payment_method_id = pm.id
       ${whereClause}
       ORDER BY r.record_date DESC, r.created_at DESC
       LIMIT ? OFFSET ?`,
      [...params, parseInt(limit), parseInt(offset)]
    );

    // 获取总数
    const [countResult] = await pool.query(
      `SELECT COUNT(*) as total FROM records r ${whereClause}`,
      params
    );

    res.json({
      code: 200,
      message: '获取成功',
      data: {
        records: rows,
        total: countResult[0].total,
        limit: parseInt(limit),
        offset: parseInt(offset)
      }
    });
  } catch (error) {
    console.error('Get records error:', error);
    res.status(500).json({ code: 500, message: '服务器内部错误' });
  }
});

// 删除记账记录
router.delete('/:id', authMiddleware, async (req, res) => {
  try {
    const user_id = req.user.id;
    const record_id = req.params.id;

    // 软删除
    const [result] = await pool.execute(
      'UPDATE records SET deleted_at = NOW() WHERE id = ? AND user_id = ? AND deleted_at IS NULL',
      [record_id, user_id]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ code: 404, message: '记录不存在或已删除' });
    }

    res.json({ code: 200, message: '删除成功' });
  } catch (error) {
    console.error('Delete record error:', error);
    res.status(500).json({ code: 500, message: '服务器内部错误' });
  }
});

module.exports = router;
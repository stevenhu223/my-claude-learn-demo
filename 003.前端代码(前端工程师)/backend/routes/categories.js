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

// 获取分类列表（区分支出和收入）
router.get('/', authMiddleware, async (req, res) => {
  try {
    const user_id = req.user.id;
    const { type } = req.query; // 1=支出, 2=收入

    let whereClause = 'WHERE deleted_at IS NULL AND (user_id = 0 OR user_id = ?)';
    let params = [user_id];

    if (type) {
      whereClause += ' AND type = ?';
      params.push(type);
    }

    const [rows] = await pool.query(
      `SELECT id, type, name, icon, sort_order, is_system
       FROM categories
       ${whereClause}
       ORDER BY is_system DESC, sort_order ASC, id ASC`,
      params
    );

    res.json({
      code: 200,
      message: '获取成功',
      data: rows
    });
  } catch (error) {
    console.error('Get categories error:', error);
    res.status(500).json({ code: 500, message: '服务器内部错误' });
  }
});

module.exports = router;
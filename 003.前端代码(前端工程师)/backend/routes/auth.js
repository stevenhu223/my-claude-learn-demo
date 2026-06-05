const express = require('express');
const bcrypt = require('bcryptjs');
const pool = require('../db/database');
const { generateToken, authMiddleware } = require('../middleware/auth');

const router = express.Router();

// =====================
// 用户注册
// =====================
router.post('/register', async (req, res) => {
  try {
    const { username, password, nickname } = req.body;

    // 参数验证
    if (!username || !password) {
      return res.status(400).json({
        code: 400,
        message: '用户名和密码不能为空'
      });
    }

    if (username.length < 3 || username.length > 20) {
      return res.status(400).json({
        code: 400,
        message: '用户名长度应为3-20个字符'
      });
    }

    if (password.length < 6) {
      return res.status(400).json({
        code: 400,
        message: '密码长度不能少于6位'
      });
    }

    // 检查用户名是否已存在
    const [existingUsers] = await pool.execute(
      'SELECT id FROM users WHERE username = ? AND deleted_at IS NULL',
      [username]
    );

    if (existingUsers.length > 0) {
      return res.status(409).json({
        code: 409,
        message: '用户名已存在'
      });
    }

    // 密码加密
    const passwordHash = await bcrypt.hash(password, 10);

    // 插入新用户
    const [result] = await pool.execute(
      'INSERT INTO users (username, password_hash, nickname) VALUES (?, ?, ?)',
      [username, passwordHash, nickname || username]
    );

    // 生成Token
    const user = { id: result.insertId, username };
    const token = generateToken(user);

    res.status(201).json({
      code: 201,
      message: '注册成功',
      data: {
        user: {
          id: result.insertId,
          username,
          nickname: nickname || username
        },
        token
      }
    });

  } catch (err) {
    console.error('Register error:', err);
    res.status(500).json({
      code: 500,
      message: '服务器内部错误'
    });
  }
});

// =====================
// 用户登录
// =====================
router.post('/login', async (req, res) => {
  try {
    const { username, password } = req.body;

    // 参数验证
    if (!username || !password) {
      return res.status(400).json({
        code: 400,
        message: '用户名和密码不能为空'
      });
    }

    // 查询用户
    const [users] = await pool.execute(
      'SELECT * FROM users WHERE username = ? AND deleted_at IS NULL',
      [username]
    );

    if (users.length === 0) {
      return res.status(401).json({
        code: 401,
        message: '用户名或密码错误'
      });
    }

    const user = users[0];

    // 验证密码
    const isPasswordValid = await bcrypt.compare(password, user.password_hash);

    if (!isPasswordValid) {
      return res.status(401).json({
        code: 401,
        message: '用户名或密码错误'
      });
    }

    // 生成Token
    const token = generateToken(user);

    res.json({
      code: 200,
      message: '登录成功',
      data: {
        user: {
          id: user.id,
          username: user.username,
          nickname: user.nickname,
          avatar_url: user.avatar_url,
          monthly_budget: user.monthly_budget
        },
        token
      }
    });

  } catch (err) {
    console.error('Login error:', err);
    res.status(500).json({
      code: 500,
      message: '服务器内部错误'
    });
  }
});

// =====================
// 获取当前用户信息
// =====================
router.get('/me', authMiddleware, async (req, res) => {
  try {
    const [users] = await pool.execute(
      'SELECT id, username, nickname, avatar_url, monthly_budget, created_at FROM users WHERE id = ? AND deleted_at IS NULL',
      [req.user.id]
    );

    if (users.length === 0) {
      return res.status(404).json({
        code: 404,
        message: '用户不存在'
      });
    }

    res.json({
      code: 200,
      data: users[0]
    });

  } catch (err) {
    console.error('Get user info error:', err);
    res.status(500).json({
      code: 500,
      message: '服务器内部错误'
    });
  }
});

// =====================
// 退出登录
// =====================
router.post('/logout', authMiddleware, (req, res) => {
  // 由于使用JWT无状态token，前端直接删除token即可
  // 这里可以做token黑名单等后续扩展
  res.json({
    code: 200,
    message: '退出成功'
  });
});

module.exports = router;
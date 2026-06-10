const express = require('express');
const cors = require('cors');
const authRoutes = require('./routes/auth');
const recordsRoutes = require('./routes/records');
const categoriesRoutes = require('./routes/categories');
const statisticsRoutes = require('./routes/statistics');

const app = express();
const PORT = 3000;

// Middleware
app.use(cors({
  origin: '*', // 生产环境应设置为具体域名
  credentials: true
}));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Request logging
app.use((req, res, next) => {
  console.log(`[${new Date().toISOString()}] ${req.method} ${req.path}`);
  next();
});

// Routes
app.use('/api/auth', authRoutes);
app.use('/api/records', recordsRoutes);
app.use('/api/categories', categoriesRoutes);
app.use('/api/statistics', statisticsRoutes);

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({ code: 404, message: '接口不存在' });
});

// Error handler
app.use((err, req, res, next) => {
  console.error('Server error:', err);
  res.status(500).json({ code: 500, message: '服务器内部错误' });
});

// Start server
app.listen(PORT, () => {
  console.log(`
╔══════════════════════════════════════════════════╗
║         财务管家后端服务启动成功                  ║
╠══════════════════════════════════════════════════╣
║  Local:  http://localhost:${PORT}                 ║
║  Health: http://localhost:${PORT}/health           ║
╠══════════════════════════════════════════════════╣
║  API Endpoints:                                 ║
║  POST /api/auth/register - 用户注册              ║
║  POST /api/auth/login    - 用户登录             ║
║  GET  /api/auth/me       - 获取用户信息          ║
║  POST /api/auth/logout   - 退出登录              ║
╚══════════════════════════════════════════════════╝
  `);
});

module.exports = app;
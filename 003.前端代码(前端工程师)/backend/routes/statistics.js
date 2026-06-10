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

// 获取月度汇总统计
router.get('/summary', authMiddleware, async (req, res) => {
  try {
    const user_id = req.user.id;
    const { year, month } = req.query;

    if (!year || !month) {
      return res.status(400).json({ code: 400, message: '缺少year或month参数' });
    }

    // 计算当月起始和结束日期
    const startDate = `${year}-${String(month).padStart(2, '0')}-01`;
    const lastDay = new Date(parseInt(year), parseInt(month), 0).getDate();
    const endDate = `${year}-${String(month).padStart(2, '0')}-${String(lastDay).padStart(2, '0')}`;

    // 上月数据用于对比
    const lastMonth = parseInt(month) - 1;
    const lastYear = lastMonth === 0 ? parseInt(year) - 1 : parseInt(year);
    const lastMonthFormatted = lastMonth === 0 ? 12 : lastMonth;
    const lastStartDate = `${lastYear}-${String(lastMonthFormatted).padStart(2, '0')}-01`;
    const lastEndDay = new Date(lastYear, lastMonthFormatted, 0).getDate();
    const lastEndDate = `${lastYear}-${String(lastMonthFormatted).padStart(2, '0')}-${String(lastEndDay).padStart(2, '0')}`;

    // 获取当月收支汇总
    const [currentStats] = await pool.query(
      `SELECT type, SUM(amount) as total, COUNT(*) as count
       FROM records
       WHERE user_id = ? AND record_date >= ? AND record_date <= ? AND deleted_at IS NULL
       GROUP BY type`,
      [user_id, startDate, endDate]
    );

    // 获取上月收支汇总
    const [lastStats] = await pool.query(
      `SELECT type, SUM(amount) as total, COUNT(*) as count
       FROM records
       WHERE user_id = ? AND record_date >= ? AND record_date <= ? AND deleted_at IS NULL
       GROUP BY type`,
      [user_id, lastStartDate, lastEndDate]
    );

    // 计算当月数据
    let totalIncome = 0, totalExpense = 0, expenseCount = 0, incomeCount = 0;
    let lastIncome = 0, lastExpense = 0;

    currentStats.forEach(row => {
      if (row.type === 1) {
        totalExpense = parseFloat(row.total) || 0;
        expenseCount = parseInt(row.count) || 0;
      } else if (row.type === 2) {
        totalIncome = parseFloat(row.total) || 0;
        incomeCount = parseInt(row.count) || 0;
      }
    });

    lastStats.forEach(row => {
      if (row.type === 1) lastExpense = parseFloat(row.total) || 0;
      if (row.type === 2) lastIncome = parseFloat(row.total) || 0;
    });

    const balance = totalIncome - totalExpense;
    const daysInMonth = new Date(parseInt(year), parseInt(month), 0).getDate();
    const dailyAvgIncome = totalIncome / daysInMonth;
    const dailyAvgExpense = totalExpense / daysInMonth;

    // 计算环比变化百分比
    const incomeChange = lastIncome > 0 ? ((totalIncome - lastIncome) / lastIncome * 100) : 0;
    const expenseChange = lastExpense > 0 ? ((totalExpense - lastExpense) / lastExpense * 100) : 0;

    res.json({
      code: 200,
      message: '获取成功',
      data: {
        total_income: totalIncome,
        total_expense: totalExpense,
        balance: balance,
        expense_count: expenseCount,
        income_count: incomeCount,
        daily_avg_income: dailyAvgIncome,
        daily_avg_expense: dailyAvgExpense,
        income_change_pct: incomeChange.toFixed(1),
        expense_change_pct: expenseChange.toFixed(1)
      }
    });
  } catch (error) {
    console.error('Get summary error:', error);
    res.status(500).json({ code: 500, message: '服务器内部错误' });
  }
});

// 获取年度趋势数据
router.get('/trend', authMiddleware, async (req, res) => {
  try {
    const user_id = req.user.id;
    const { year } = req.query;

    if (!year) {
      return res.status(400).json({ code: 400, message: '缺少year参数' });
    }

    const startDate = `${year}-01-01`;
    const endDate = `${year}-12-31`;

    const [rows] = await pool.query(
      `SELECT DATE_FORMAT(record_date, '%m') as month, type, SUM(amount) as total
       FROM records
       WHERE user_id = ? AND record_date >= ? AND record_date <= ? AND deleted_at IS NULL
       GROUP BY DATE_FORMAT(record_date, '%m'), type
       ORDER BY month ASC`,
      [user_id, startDate, endDate]
    );

    // 初始化12个月数据
    const monthlyData = [];
    for (let i = 1; i <= 12; i++) {
      monthlyData.push({
        month: String(i).padStart(2, '0'),
        income: 0,
        expense: 0
      });
    }

    // 填充数据
    rows.forEach(row => {
      const monthIndex = parseInt(row.month) - 1;
      if (row.type === 1) {
        monthlyData[monthIndex].expense = parseFloat(row.total) || 0;
      } else if (row.type === 2) {
        monthlyData[monthIndex].income = parseFloat(row.total) || 0;
      }
    });

    res.json({
      code: 200,
      message: '获取成功',
      data: {
        yearly: year,
        monthly_data: monthlyData
      }
    });
  } catch (error) {
    console.error('Get trend error:', error);
    res.status(500).json({ code: 500, message: '服务器内部错误' });
  }
});

// 获取分类收支分布
router.get('/category-breakdown', authMiddleware, async (req, res) => {
  try {
    const user_id = req.user.id;
    const { year, month, type } = req.query;

    if (!year || !month) {
      return res.status(400).json({ code: 400, message: '缺少year或month参数' });
    }

    const startDate = `${year}-${String(month).padStart(2, '0')}-01`;
    const lastDay = new Date(parseInt(year), parseInt(month), 0).getDate();
    const endDate = `${year}-${String(month).padStart(2, '0')}-${String(lastDay).padStart(2, '0')}`;

    let whereClause = 'WHERE r.user_id = ? AND r.record_date >= ? AND r.record_date <= ? AND r.deleted_at IS NULL';
    let params = [user_id, startDate, endDate];

    if (type) {
      whereClause += ' AND r.type = ?';
      params.push(type);
    }

    const [rows] = await pool.query(
      `SELECT c.id, c.name, c.icon, c.type,
              SUM(r.amount) as total,
              COUNT(*) as count
       FROM records r
       JOIN categories c ON r.category_id = c.id
       ${whereClause}
       GROUP BY c.id, c.name, c.icon, c.type
       ORDER BY total DESC`,
      params
    );

    // 计算总金额和百分比
    let grandTotal = rows.reduce((sum, row) => sum + parseFloat(row.total), 0);

    const categories = rows.map(row => ({
      id: row.id,
      name: row.name,
      icon: row.icon,
      type: row.type,
      total: parseFloat(row.total) || 0,
      percentage: grandTotal > 0 ? (parseFloat(row.total) / grandTotal * 100).toFixed(1) : 0,
      count: parseInt(row.count)
    }));

    res.json({
      code: 200,
      message: '获取成功',
      data: {
        categories: categories,
        total: grandTotal
      }
    });
  } catch (error) {
    console.error('Get category breakdown error:', error);
    res.status(500).json({ code: 500, message: '服务器内部错误' });
  }
});

// 获取今日统计数据
router.get('/today', authMiddleware, async (req, res) => {
  try {
    const user_id = req.user.id;
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth() + 1;
    const day = String(today.getDate()).padStart(2, '0');
    const todayStr = `${year}-${String(month).padStart(2, '0')}-${day}`;

    // 获取今日收支（使用 DATE() 比较避免时区问题）
    const [todayStats] = await pool.query(
      `SELECT type, SUM(amount) as total
       FROM records
       WHERE user_id = ? AND DATE(record_date) = ? AND deleted_at IS NULL
       GROUP BY type`,
      [user_id, todayStr]
    );

    let todayExpense = 0, todayIncome = 0;
    todayStats.forEach(row => {
      if (row.type === 1) todayExpense = parseFloat(row.total) || 0;
      if (row.type === 2) todayIncome = parseFloat(row.total) || 0;
    });

    const todayBalance = todayIncome - todayExpense;

    // 获取当月预算信息
    const yearMonth = `${year}-${String(month).padStart(2, '0')}`;
    const [budgetRows] = await pool.query(
      `SELECT budget_amount FROM budgets WHERE user_id = ? AND ym = ?`,
      [user_id, yearMonth]
    );
    const monthlyBudget = budgetRows.length > 0 ? parseFloat(budgetRows[0].budget_amount) : 0;

    // 计算日均预算
    const daysInMonth = new Date(year, month, 0).getDate();
    const dailyBudget = monthlyBudget / daysInMonth;

    res.json({
      code: 200,
      message: '获取成功',
      data: {
        date: todayStr,
        expense: todayExpense,
        income: todayIncome,
        balance: todayBalance,
        daily_budget: dailyBudget,
        monthly_budget: monthlyBudget
      }
    });
  } catch (error) {
    console.error('Get today stats error:', error);
    res.status(500).json({ code: 500, message: '服务器内部错误' });
  }
});

// 获取预算概览
router.get('/budget-overview', authMiddleware, async (req, res) => {
  try {
    const user_id = req.user.id;
    const { year, month } = req.query;

    if (!year || !month) {
      return res.status(400).json({ code: 400, message: '缺少year或month参数' });
    }

    const yearMonth = `${year}-${String(month).padStart(2, '0')}`;
    const startDate = `${year}-${String(month).padStart(2, '0')}-01`;
    const lastDay = new Date(parseInt(year), parseInt(month), 0).getDate();
    const endDate = `${year}-${String(month).padStart(2, '0')}-${String(lastDay).padStart(2, '0')}`;

    // 获取月度预算
    const [budgetRows] = await pool.query(
      `SELECT budget_amount FROM budgets WHERE user_id = ? AND ym = ?`,
      [user_id, yearMonth]
    );

    const totalBudget = budgetRows.length > 0 ? parseFloat(budgetRows[0].budget_amount) : 0;

    // 获取当月支出
    const [expenseRows] = await pool.query(
      `SELECT SUM(amount) as total FROM records
       WHERE user_id = ? AND type = 1 AND record_date >= ? AND record_date <= ? AND deleted_at IS NULL`,
      [user_id, startDate, endDate]
    );

    const totalSpent = parseFloat(expenseRows[0]?.total) || 0;
    const remaining = totalBudget - totalSpent;
    const percentage = totalBudget > 0 ? (totalSpent / totalBudget * 100).toFixed(1) : 0;

    res.json({
      code: 200,
      message: '获取成功',
      data: {
        total_budget: totalBudget,
        total_spent: totalSpent,
        remaining: remaining,
        percentage: percentage
      }
    });
  } catch (error) {
    console.error('Get budget overview error:', error);
    res.status(500).json({ code: 500, message: '服务器内部错误' });
  }
});

module.exports = router;
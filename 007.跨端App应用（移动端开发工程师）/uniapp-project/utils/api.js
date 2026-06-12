// API 配置
const API_BASE = 'http://localhost:8080/api';

// 存储 Token 的 Key
const TOKEN_KEY = 'finance_buddy_token';
const USER_KEY = 'finance_buddy_user';

// =====================
// API 基础请求函数
// =====================
async function request(endpoint, options = {}) {
  const url = API_BASE + endpoint;
  const token = uni.getStorageSync(TOKEN_KEY);

  const defaultHeaders = {
    'Content-Type': 'application/json'
  };

  if (token) {
    defaultHeaders['Authorization'] = 'Bearer ' + token;
  }

  try {
    const response = await uni.request({
      url: url,
      method: options.method || 'GET',
      header: defaultHeaders,
      data: options.data
    });

    if (response.statusCode && response.statusCode >= 200 && response.statusCode < 300) {
      return response.data;
    }

    if (response.statusCode === 401) {
      uni.removeStorageSync(TOKEN_KEY);
      uni.removeStorageSync(USER_KEY);
      uni.reLaunch({ url: '/pages/auth/login/index' });
      throw new Error('登录已过期，请重新登录');
    }

    throw new Error(response.data && response.data.message || '请求失败');
  } catch (error) {
    console.error('API request failed:', error);
    throw error;
  }
}

// =====================
// 认证相关 API
// =====================
export const AuthAPI = {
  async register(username, password, nickname) {
    const data = await request('/auth/register', {
      method: 'POST',
      data: { username: username, password: password, nickname: nickname }
    });
    if (data.data && data.data.token) {
      uni.setStorageSync(TOKEN_KEY, data.data.token);
      uni.setStorageSync(USER_KEY, JSON.stringify(data.data.user));
    }
    return data;
  },

  async login(username, password) {
    const data = await request('/auth/login', {
      method: 'POST',
      data: { username: username, password: password }
    });
    if (data.data && data.data.token) {
      uni.setStorageSync(TOKEN_KEY, data.data.token);
      uni.setStorageSync(USER_KEY, JSON.stringify(data.data.user));
    }
    return data;
  },

  async getCurrentUser() {
    return request('/auth/me');
  },

  async logout() {
    try {
      await request('/auth/logout', { method: 'POST' });
    } catch (e) {}
    uni.removeStorageSync(TOKEN_KEY);
    uni.removeStorageSync(USER_KEY);
  },

  isLoggedIn() {
    return !!uni.getStorageSync(TOKEN_KEY);
  },

  getUser() {
    const userStr = uni.getStorageSync(USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  },

  getToken() {
    return uni.getStorageSync(TOKEN_KEY);
  }
};

// =====================
// 记账记录相关 API
// =====================
export const RecordsAPI = {
  async createRecord(recordData) {
    const { type, amount, categoryId, accountId, paymentMethodId, recordDate, remark } = recordData;
    return request('/records', {
      method: 'POST',
      data: { type: type, amount: amount, categoryId: categoryId, accountId: accountId, paymentMethodId: paymentMethodId, recordDate: recordDate, remark: remark }
    });
  },

  async getRecords(params = {}) {
    return request('/records', { data: params });
  },

  async deleteRecord(id) {
    return request('/records/' + id, { method: 'DELETE' });
  }
};

// =====================
// 分类相关 API
// =====================
export const CategoriesAPI = {
  async getCategories(type) {
    const queryString = type ? '?type=' + type : '';
    return request('/categories' + queryString);
  }
};

// =====================
// 统计相关 API
// =====================
export const StatisticsAPI = {
  async getSummary(year, month) {
    return request('/statistics/summary?year=' + year + '&month=' + month);
  },

  async getTrend(year) {
    return request('/statistics/trend?year=' + year);
  },

  async getCategoryBreakdown(year, month, type) {
    return request('/statistics/category-breakdown?year=' + year + '&month=' + month + '&type=' + type);
  },

  async getBudgetOverview(year, month) {
    return request('/statistics/budget-overview?year=' + year + '&month=' + month);
  },

  async getToday() {
    return request('/statistics/today');
  }
};

// =====================
// 预算相关 API
// =====================
export const BudgetAPI = {
  async getBudget(ym) {
    return request('/budget?ym=' + ym);
  },

  async saveBudget(ym, budgetAmount) {
    return request('/budget', {
      method: 'POST',
      data: { ym: ym, budgetAmount: budgetAmount }
    });
  }
};

// =====================
// 工具函数
// =====================
export const Utils = {
  validateUsername(username) {
    if (!username || username.length < 3 || username.length > 20) {
      return '用户名长度应为3-20个字符';
    }
    if (!/^[a-zA-Z0-9_]+$/.test(username)) {
      return '用户名只能包含字母数字和下划线';
    }
    return null;
  },

  validatePassword(password) {
    if (!password || password.length < 6) {
      return '密码长度不能少于6位';
    }
    return null;
  },

  formatMoney(amount) {
    const val = parseFloat(amount);
    if (isNaN(val) || !isFinite(val)) return '¥ 0.00';
    return '¥ ' + val.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  },

  formatMoneyShort(amount) {
    const val = parseFloat(amount);
    if (isNaN(val) || !isFinite(val)) return '¥--';
    if (Math.abs(val) >= 10000) {
      return '¥' + (val / 10000).toFixed(1) + '万';
    }
    return '¥' + val.toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  },

  getCategoryEmoji(icon) {
    const iconMap = {
      'restaurant': '🍜',
      'directions_car': '🚌',
      'shopping_bag': '🛒',
      'home': '🏠',
      'checkroom': '👔',
      'medical_services': '💊',
      'sports_esports': '🎮',
      'school': '📚',
      'favorite': '❤️',
      'more_horiz': '📌',
      'payments': '💰',
      'card_giftcard': '🎁',
      'trending_up': '📈',
      'work': '💼',
      'storefront': '🏪',
      'handyman': '🔧'
    };
    return iconMap[icon] || '📌';
  },

  formatRecordDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    return month + '-' + day + ' ' + hours + ':' + minutes;
  },

  getTodayDateStr() {
    const today = new Date();
    return today.toISOString().split('T')[0];
  }
};

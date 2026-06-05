// API 配置
const API_BASE = 'http://localhost:3000/api';

// 存储 Token 的 Key
const TOKEN_KEY = 'finance_buddy_token';
const USER_KEY = 'finance_buddy_user';

// =====================
// API 基础请求函数
// =====================
async function request(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint}`;
  const token = localStorage.getItem(TOKEN_KEY);

  const defaultHeaders = {
    'Content-Type': 'application/json'
  };

  if (token) {
    defaultHeaders['Authorization'] = `Bearer ${token}`;
  }

  try {
    const response = await fetch(url, {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers
      }
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.message || '请求失败');
    }

    return data;
  } catch (error) {
    console.error('API request failed:', error);
    throw error;
  }
}

// =====================
// 认证相关 API
// =====================
const AuthAPI = {
  // 用户注册
  async register(username, password, nickname) {
    const data = await request('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, password, nickname })
    });
    if (data.token) {
      localStorage.setItem(TOKEN_KEY, data.token);
      localStorage.setItem(USER_KEY, JSON.stringify(data.data.user));
    }
    return data;
  },

  // 用户登录
  async login(username, password) {
    const data = await request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    });
    if (data.token) {
      localStorage.setItem(TOKEN_KEY, data.token);
      localStorage.setItem(USER_KEY, JSON.stringify(data.data.user));
    }
    return data;
  },

  // 获取当前用户信息
  async getCurrentUser() {
    return request('/auth/me');
  },

  // 退出登录
  async logout() {
    try {
      await request('/auth/logout', { method: 'POST' });
    } catch (e) {
      // 忽略退出错误
    }
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },

  // 检查是否已登录
  isLoggedIn() {
    return !!localStorage.getItem(TOKEN_KEY);
  },

  // 获取当前用户
  getUser() {
    const userStr = localStorage.getItem(USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  },

  // 获取 Token
  getToken() {
    return localStorage.getItem(TOKEN_KEY);
  }
};

// =====================
// 工具函数
// =====================
const Utils = {
  // 验证用户名格式
  validateUsername(username) {
    if (!username || username.length < 3 || username.length > 20) {
      return '用户名长度应为3-20个字符';
    }
    if (!/^[a-zA-Z0-9_]+$/.test(username)) {
      return '用户名只能包含字母、数字和下划线';
    }
    return null;
  },

  // 验证密码格式
  validatePassword(password) {
    if (!password || password.length < 6) {
      return '密码长度不能少于6位';
    }
    return null;
  }
};
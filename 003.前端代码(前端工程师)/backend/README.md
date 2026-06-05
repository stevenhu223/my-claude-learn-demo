# 财务管家后端 API

## 技术栈
- Node.js + Express
- MySQL 9.7.0
- JWT (JSON Web Token) 认证
- bcryptjs 密码加密

## 快速开始

### 1. 安装依赖
```bash
cd 003.前端代码(前端工程师)/backend
npm install
```

### 2. 确保 MySQL 运行中
```bash
docker ps  # 确认 mysql 容器运行中
```

### 3. 启动服务
```bash
# 开发模式（自动重启）
npm run dev

# 或生产模式
npm start
```

### 4. 测试 API

**注册用户：**
```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456","nickname":"测试用户"}'
```

**登录：**
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

**获取用户信息：**
```bash
curl http://localhost:3000/api/auth/me \
  -H "Authorization: Bearer <your_token>"
```

## API 文档

### 认证接口

| 方法 | 路径 | 描述 | 需要认证 |
|------|------|------|----------|
| POST | /api/auth/register | 用户注册 | 否 |
| POST | /api/auth/login | 用户登录 | 否 |
| GET | /api/auth/me | 获取当前用户信息 | 是 |
| POST | /api/auth/logout | 退出登录 | 是 |

### 请求/响应格式

**注册请求：**
```json
{
  "username": "用户名",
  "password": "密码",
  "nickname": "昵称（可选）"
}
```

**登录请求：**
```json
{
  "username": "用户名",
  "password": "密码"
}
```

**登录成功响应：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "user": {
      "id": 1,
      "username": "testuser",
      "nickname": "测试用户"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 201 | 创建成功（如注册） |
| 400 | 参数错误 |
| 401 | 未授权/登录失败 |
| 404 | 资源不存在 |
| 409 | 资源冲突（如用户名已存在） |
| 500 | 服务器内部错误 |

## 前端集成

前端可以通过以下方式调用 API：

```javascript
const API_BASE = 'http://localhost:3000/api';

// 注册
const res = await fetch(`${API_BASE}/auth/register`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username, password, nickname })
});

// 登录
const res = await fetch(`${API_BASE}/auth/login`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username, password })
});

// 获取用户信息（需要Token）
const res = await fetch(`${API_BASE}/auth/me`, {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| PORT | 3000 | 服务端口 |
| JWT_SECRET | 内置值 | JWT密钥，生产环境应修改 |

## 项目结构

```
backend/
├── db/
│   └── database.js      # 数据库连接
├── middleware/
│   └── auth.js          # JWT认证中间件
├── routes/
│   └── auth.js          # 认证路由
├── server.js            # 主入口
├── package.json         # 依赖配置
└── README.md            # 说明文档
```
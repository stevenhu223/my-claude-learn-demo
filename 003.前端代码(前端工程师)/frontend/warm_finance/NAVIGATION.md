# 财务管家 - 页面导航配置

## 路由映射

| 文件夹 | 页面 | 路由路径 |
|--------|------|----------|
| pages/auth/login.html | 登录 | `/login` |
| pages/auth/register.html | 注册 | `/register` |
| pages/auth/reset-password.html | 重置密码 | `/reset-password` |
| pages/auth/verify.html | 验证码校验 | `/verify` |
| pages/auth/password-success.html | 密码修改成功 | `/password-success` |
| pages/home/index.html | 首页 | `/` |
| pages/home/add-record.html | 记账页 | `/add` |
| pages/home/records.html | 流水页 | `/records` |
| pages/home/statistics.html | 统计页 | `/statistics` |
| pages/home/mine.html | 我的页 | `/mine` |

## 页面跳转关系

### 认证流程
```
login.html → register.html (立即注册)
login.html → reset-password.html (忘记密码)
reset-password.html → verify.html (发送验证码)
verify.html → password-success.html (验证成功)
password-success.html → login.html (立即进入系统)
```

### 主流程
```
index.html (首页)
    ├── add-record.html (底部导航/首页快速入口)
    ├── records.html (底部导航)
    ├── statistics.html (底部导航)
    └── mine.html (底部导航)

add-record.html → index.html (保存后返回)
records.html → index.html (底部导航)
statistics.html → index.html (底部导航)
mine.html → index.html (底部导航)
```

## 导航组件

### 桌面端顶部导航
```html
<nav class="flex items-center gap-8">
  <a href="index.html">首页</a>
  <a href="records.html">流水</a>
  <a href="statistics.html">统计</a>
  <a href="mine.html">我的</a>
</nav>
```

### 移动端底部导航
```html
<nav class="fixed bottom-0 md:hidden">
  <a href="index.html">首页</a>
  <a href="records.html">流水</a>
  <a href="statistics.html">统计</a>
  <a href="mine.html">我的</a>
</nav>
```

### 悬浮记账按钮
```html
<button class="fixed right-12 bottom-12">
  <a href="add-record.html">记一笔</a>
</button>
```
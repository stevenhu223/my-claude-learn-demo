# 财务管家 - 项目结构

```
003.前端代码(前端工程师)/frontend/warm_finance/
├── pages/
│   ├── auth/                         # 认证相关页面
│   │   ├── login.html                # 登录页
│   │   ├── register.html              # 注册页
│   │   ├── reset-password.html        # 重置密码
│   │   ├── verify.html               # 验证码校验
│   │   └── password-success.html     # 密码修改成功
│   │
│   └── home/                         # 主要功能页面
│       ├── index.html                # 首页（概览+快速记账）
│       ├── add-record.html           # 记账页（新增记录）
│       ├── records.html              # 流水页（消费记录列表）
│       ├── statistics.html           # 统计页（报表和分析）
│       └── mine.html                 # 我的页（设置、预算、数据导出）
│
├── shared/                            # 共享资源（预留）
│
├── DESIGN.md                          # 设计规范文档
├── NAVIGATION.md                      # 页面导航配置
└── README.md                          # 本文件
```

## 页面导航关系

### 认证流程 (Auth Flow)
```
login.html ──────────────→ register.html
     │
     └──→ reset-password.html ──→ verify.html ──→ password-success.html
                                    (发送验证码)    (验证成功)
                                                           │
                              ←──────────────────────────────┘
                              (返回登录页)
```

### 主要功能流程 (Main Flow)
```
index.html (首页)
    │
    ├───────────────────────────────底部导航──────────────────────────────┐
    │                                                                    │
    ▼                                                                    ▼
add-record.html ←──── 记一笔(FAB)                                 records.html (流水)
                                                                │
                                                                ├──── 侧边栏筛选
                                                                │
                                                            statistics.html (统计)
                                                                │
                                                                ├──── 支出排行榜
                                                                └──── 支出分布图

                                                                mine.html (我的)
                                                                │
                                                                ├──── 预算管理
                                                                ├──── 数据管理
                                                                └──── 系统设置
```

## 技术栈
- **CSS框架**: Tailwind CSS (via CDN)
- **字体**: Google Fonts (Be Vietnam Pro, JetBrains Mono)
- **图标**: Material Symbols Icons
- **纯前端**: 无需构建，直接用浏览器打开

## 设计规范
参见 [DESIGN.md](./DESIGN.md)

## 运行方式
直接在浏览器中打开 `pages/home/index.html` 即可预览完整应用。
所有页面通过导航链接互相跳转。

## 页面清单

| 页面 | 文件 | 功能描述 |
|------|------|----------|
| 登录 | [auth/login.html](pages/auth/login.html) | 用户名密码登录 |
| 注册 | [auth/register.html](pages/auth/register.html) | 新用户注册 |
| 重置密码 | [auth/reset-password.html](pages/auth/reset-password.html) | 找回密码入口 |
| 验证码 | [auth/verify.html](pages/auth/verify.html) | 验证码校验 |
| 密码修改成功 | [auth/password-success.html](pages/auth/password-success.html) | 密码重置成功提示 |
| 首页 | [home/index.html](pages/home/index.html) | 本月概览+快速分类入口+最近记录 |
| 记账 | [home/add-record.html](pages/home/add-record.html) | 新增收支记录 |
| 流水 | [home/records.html](pages/home/records.html) | 消费记录列表（按日分组） |
| 统计 | [home/statistics.html](pages/home/statistics.html) | 收支分析报表 |
| 我的 | [home/mine.html](pages/home/mine.html) | 个人设置、预算管理、数据导出 |
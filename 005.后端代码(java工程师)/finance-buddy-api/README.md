# 财务管家 API

基于 Spring Boot 3 + MyBatis-Plus + JWT + MySQL/H2 的财务管家后端 RESTful API 服务。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | LTS 版本 |
| Spring Boot | 3.3.0 | 核心框架 |
| MyBatis-Plus | 3.5.6 | ORM 框架 |
| MySQL | 8.0 | 生产环境数据库 |
| H2 | - | 开发/测试环境内存数据库 |
| JWT | 0.12.5 | 身份认证 |

## 项目结构

```
finance-buddy-api/
├── src/main/java/com/financebuddy/
│   ├── FinanceBuddyApplication.java   # 启动类
│   ├── config/                        # 配置类
│   │   ├── JwtConfig.java              # JWT 配置
│   │   ├── JwtAuthInterceptor.java     # JWT 拦截器
│   │   ├── MybatisPlusConfig.java      # MyBatis-Plus 配置
│   │   └── WebMvcConfig.java          # Web MVC 配置
│   ├── controller/                    # 控制器
│   │   ├── AuthController.java        # 认证接口
│   │   ├── RecordController.java     # 记账记录接口
│   │   ├── CategoryController.java    # 分类接口
│   │   ├── StatisticsController.java  # 统计接口
│   │   └── UserController.java       # 用户接口
│   ├── service/                      # 服务层
│   │   ├── AuthService.java
│   │   ├── UserService.java
│   │   ├── RecordService.java
│   │   ├── CategoryService.java
│   │   └── StatisticsService.java
│   ├── repository/                   # 数据访问层
│   │   ├── UserRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── RecordRepository.java
│   │   ├── BudgetRepository.java
│   │   └── PaymentMethodRepository.java
│   ├── entity/                      # 实体类
│   │   ├── User.java
│   │   ├── Category.java
│   │   ├── Record.java
│   │   ├── Budget.java
│   │   ├── Account.java
│   │   └── PaymentMethod.java
│   ├── dto/                         # 数据传输对象
│   │   ├── request/
│   │   └── response/
│   ├── common/                      # 公共组件
│   │   ├── exception/
│   │   └── result/
│   └── utils/                       # 工具类
└── src/main/resources/
    ├── application.yml                # 配置文件
    ├── schema.sql                    # 数据库表结构
    └── data.sql                      # 初始化数据
```

## 快速开始

### 1. 环境要求
- JDK 21+
- Maven 3.9+

### 2. 启动服务

```bash
cd finance-buddy-api
mvn package -DskipTests
java -jar target/finance-buddy-api-1.0.0.jar
```

服务地址：http://localhost:8080

### 3. API 测试

#### 注册
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456","nickname":"测试用户"}'
```

#### 登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'
```

## API 列表

### 认证接口 `/api/auth`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/register | 用户注册 |
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/logout | 退出登录 |
| GET | /api/auth/me | 获取当前用户信息 |

### 用户接口 `/api/users`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/users/me | 获取当前用户信息 |
| PUT | /api/users/me | 更新用户信息 |

### 记账接口 `/api/records`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/records | 创建记录 |
| GET | /api/records | 获取记录列表（支持 type/startDate/endDate 筛选） |
| DELETE | /api/records/{id} | 删除记录 |

### 分类接口 `/api/categories`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/categories | 获取分类列表（支持 type 筛选） |

### 统计接口 `/api/statistics`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/statistics/summary | 月度汇总统计 |
| GET | /api/statistics/trend | 年度趋势数据 |
| GET | /api/statistics/category-breakdown | 分类收支分布 |
| GET | /api/statistics/today | 今日统计数据 |
| GET | /api/statistics/budget-overview | 预算概览 |

## 数据库表

| 表名 | 说明 |
|------|------|
| users | 用户表 |
| categories | 分类表（系统+自定义） |
| records | 记账记录表 |
| budgets | 预算表 |
| accounts | 账户表 |
| payment_methods | 支付方式表 |
| fixed_expenses | 固定支出表 |

## 响应格式

```json
// 成功
{"code":200,"message":"success","data":{...}}

// 错误
{"code":400,"message":"参数错误","data":null}
```

## 状态码

| 码值 | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 409 | 资源冲突 |
| 500 | 服务器内部错误 |

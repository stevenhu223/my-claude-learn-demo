# Java 开发规范

> 本规范适用于基于 Spring Boot 的 Java 后端项目，旨在统一代码风格、提升可维护性、保障系统稳定性。

---

## 一、项目结构规范

### 1.1 标准分层架构

```
com.company.project/
├── controller/          # 控制层 - 接收请求、参数校验、返回响应
├── service/             # 服务接口层 - 定义业务能力抽象
│   └── impl/            # 服务实现层 - 具体业务逻辑
├── repository/           # 数据访问层 - 数据操作（对应 MyBatis Mapper 或 JPA Repository）
├── entity/               # 实体层 - 数据库表映射对象
├── dto/                  # 数据传输对象
│   ├── request/         # 请求 DTO（入参）
│   └── response/         # 响应 DTO（出参）
├── common/               # 公共组件
│   ├── exception/        # 自定义异常
│   └── result/           # 统一响应封装
├── config/               # 配置类 - 框架配置、系统配置
└── util/                 # 工具类
```

### 1.2 命名规范

| 层级 | 命名规则 | 示例 |
|------|---------|------|
| 项目名 | `xxx-api` / `xxx-service` | `finance-buddy-api` |
| 包名 | `com.company.project.module` | `com.financebuddy.record` |
| 类名 | UpperCamelCase | `UserService`, `RecordController` |
| 方法名 | lowerCamelCase | `getUserById`, `createRecord` |
| 变量名 | lowerCamelCase | `userName`, `pageSize` |
| 常量名 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE` |
| 数据库表名 | snake_case（单数） | `sys_user`, `fin_record` |
| 数据库列名 | snake_case | `user_name`, `created_at` |

### 1.3 RESTful API 命名规范

```
GET    /api/users           # 获取用户列表
GET    /api/users/{id}     # 获取单个用户
POST   /api/users          # 创建用户
PUT    /api/users/{id}     # 更新用户
DELETE /api/users/{id}     # 删除用户
```

---

## 二、代码规范

### 2.1 类编写规范

```java
// ✅ 正确示例
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public Result<UserResponse> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }
}
```

```java
// ❌ 错误示例
@Controller  // 不用 @Controller，用 @RestController
@RequestMapping("api/users")  // 路径缺少 /
public class UserController {
    @Autowired
    UserService service;  // 不用字段 injection，用构造器 injection
}
```

### 2.2 方法编写规范

```java
// ✅ 正确：方法参数不超过 3 个，超出用 DTO
public Result<Page<UserResponse>> list(UserQueryDTO query) {
    // query 包含分页、筛选等所有参数
}

// ❌ 错误：参数列表过长
public Result<Page<UserResponse>> list(
    Integer page, Integer size, String name,
    String email, Integer status, Long deptId, ...) {
}
```

```java
// ✅ 正确：业务逻辑放在 Service 层，Controller 仅做调度
@PostMapping
public Result<Long> create(@Valid @RequestBody UserCreateRequest request) {
    Long id = userService.create(request);
    return Result.success(id);
}

// ❌ 错误：在 Controller 做业务逻辑
@PostMapping
public Result<Long> create(@Valid @RequestBody UserCreateRequest request) {
    if (request.getName() == null) {
        throw new RuntimeException("名称不能为空");  // 业务校验不应在 Controller
    }
    // 直接操作数据库...
}
```

### 2.3 DTO 规范

```java
// ✅ 请求 DTO 必须加 @Valid 注解进行参数校验
@Data
public class UserCreateRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度3-20位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码至少6位")
    private String password;
}

// ✅ 响应 DTO 使用 @Builder 避免暴露内部结构
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String nickname;
}
```

---

## 三、异常处理规范

### 3.1 自定义业务异常

```java
// ✅ 统一业务异常类
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
```

### 3.2 全局异常处理器

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError()
                .getDefaultMessage();
        return Result.error(400, message);
    }

    // 业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    // 未授权异常
    @ExceptionHandler(UnauthorizedException.class)
    public Result<?> handleUnauthorizedException(UnauthorizedException e) {
        return Result.error(401, "未授权，请重新登录");
    }

    // 兜底异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "服务器内部错误");
    }
}
```

### 3.3 异常使用原则

```java
// ✅ 正确：抛出明确业务异常
if (user == null) {
    throw new BusinessException("用户不存在");
}

// ❌ 错误：使用 RuntimeException 或无意义的异常
if (user == null) {
    throw new RuntimeException("error");
}
```

---

## 四、统一响应规范

### 4.1 响应结构

```java
@Data
public class Result<T> {
    private Integer code;    // 业务状态码
    private String message;    // 信息
    private T data;            // 数据

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }
}
```

### 4.2 响应格式示例

```json
// 成功响应
{
  "code": 200,
  "message": "success",
  "data": { ... }
}

// 错误响应
{
  "code": 400,
  "message": "用户名不能为空",
  "data": null
}
```

---

## 五、数据库规范

### 5.1 SQL 编写规范

```xml
<!-- ✅ MyBatis XML：字段映射清晰，SQL 规范缩进 -->
<select id="selectById" resultMap="BaseResultMap">
    SELECT id, username, email, created_at
    FROM sys_user
    WHERE id = #{id}
      AND deleted = 0
</select>

<!-- ❌ 避免 SELECT *，明确列出需要的字段 -->
<select id="selectById" resultMap="BaseResultMap">
    SELECT * FROM sys_user WHERE id = #{id}
</select>
```

### 5.2 索引规范

- 主键自动建索引
- 常用查询字段建立索引：`INDEX idx_user_status (user_id, status)`
- 避免冗余索引
- 避免在索引列上做函数操作

### 5.3 日期字段规范

```sql
-- ✅ 时间戳用 BIGINT 或 DATETIME
create_time BIGINT comment '创建时间戳（毫秒）',
-- 或
create_time DATETIME comment '创建时间',

-- ✅ 软删除标识用 TINYINT
deleted TINYINT DEFAULT 0 comment '0-未删除 1-已删除',
```

---

## 六、日志规范

### 6.1 日志级别使用

| 级别 | 使用场景 |
|------|---------|
| ERROR | 程序异常、需要立即处理的问题 |
| WARN | 潜在问题、不建议但可用的操作 |
| INFO | 重要业务流程节点（如用户登录、订单创建） |
| DEBUG | 开发调试信息，生产环境不开启 |

```java
// ✅ 正确使用日志
log.info("用户登录成功: username={}", username);
log.warn("密码错误次数过多: userId={}, count={}", userId, count);
log.error("支付回调处理异常", e);  // e 不能省略

// ❌ 错误
log.info("开始处理");
log.error(e.getMessage());  // 信息不完整
```

### 6.2 敏感信息禁止打印

```java
// ❌ 禁止日志打印密码、Token、身份证等敏感信息
log.info("用户登录: password={}", password);
log.info("Token: {}", token);

// ✅ 脱敏打印
log.info("用户登录: username={}, phone={}",
    username, phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
```

---

## 七、安全规范

### 7.1 认证与授权

```java
// ✅ 所有敏感接口必须登录校验
@PostMapping("/api/orders")
public Result<OrderResponse> create(@RequestBody ...) {
    Long userId = getCurrentUserId();  // 从 Token 解析
    // ...
}

// ✅ 参数必须校验，防止注入
@PostMapping("/api/users")
public Result<Void> create(@Valid @RequestBody UserCreateRequest request) {
    // 使用 @Valid 注解自动校验
}
```

### 7.2 SQL 注入防护

```java
// ✅ 使用参数绑定，禁止字符串拼接 SQL
@Select("SELECT * FROM users WHERE name = #{name}")
User findByName(@Param("name") String name);

// ❌ 禁止字符串拼接
@Select("SELECT * FROM users WHERE name = '" + name + "'")
```

### 7.3 接口限流

```java
// ✅ 重要接口添加限流注解
@RateLimiter(value = 100, timeout = 1)
@PostMapping("/api/orders")
public Result<OrderResponse> create(...) { }
```

---

## 八、Git 提交规范

### 8.1 Commit Message 格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type 类型：**

| Type | 说明 |
|------|------|
| feat | 新功能 |
| fix | Bug 修复 |
| docs | 文档变更 |
| style | 代码格式（不影响功能） |
| refactor | 重构（非功能变更） |
| test | 测试相关 |
| chore | 构建/工具变更 |

**示例：**

```
feat(auth): 添加用户注册接口

- 支持手机号+验证码注册
- 添加密码强度校验

Closes #123
```

### 8.2 分支命名

```
feature/user-login       # 功能分支
bugfix/order-refund     # Bug 修复分支
hotfix/payment-crash    # 紧急修复分支
release/1.0.0           # 发布分支
```

---

## 九、配置规范

### 9.1 多环境配置

```
src/main/resources/
├── application.yml          # 公共配置
├── application-dev.yml      # 开发环境
├── application-test.yml     # 测试环境
└── application-prod.yml    # 生产环境
```

### 9.2 敏感配置

```yaml
# ❌ 禁止在代码仓库明文存储
password: "123456"
secret-key: "my-secret-key"

# ✅ 使用环境变量或配置中心
password: ${DB_PASSWORD}
secret-key: ${JWT_SECRET}
```

---

## 十、注释规范

### 10.1 类注释

```java
/**
 * 用户服务层
 * 负责用户注册、登录、信息管理等核心业务逻辑
 *
 * @author zhangsan
 * @since 2026-01-01
 */
@Service
public class UserService { }
```

### 10.2 方法注释

```java
/**
 * 根据ID获取用户信息
 *
 * @param id 用户ID
 * @return 用户信息，不存在返回null
 * @throws BusinessException 用户ID无效
 */
public User getById(Long id) { }
```

### 10.3 注释原则

- 代码即注释：优先让代码自解释，减少无效注释
- 关键逻辑必须注释说明**为什么**而非**是什么**
- 删除所有被注释掉的废弃代码（用 Git 管理历史）

---

## 十一、测试规范

### 11.1 单元测试原则

```java
// ✅ 正确：测试独立、可重复、有断言
@SpringBootTest
class UserServiceTest {
    @Test
    void shouldReturnUser_whenUserExists() {
        User user = userService.getById(1L);
        assertNotNull(user);
        assertEquals("zhangsan", user.getUsername());
    }
}

// ❌ 错误：无断言、依赖外部状态
@Test
void testGetUser() {
    User user = userService.getById(1L);
    System.out.println(user);  // 只打印不断言
}
```

### 11.2 测试覆盖率要求

- 核心业务逻辑覆盖率 > 80%
- Controller 层 API 测试覆盖所有接口
- 边界条件、异常流程必须测试

---

## 十二、代码审查清单

提交代码前自查：

- [ ] 代码格式符合项目规范
- [ ] 无硬编码值（配置化）
- [ ] 接口有参数校验
- [ ] 异常被正确捕获处理
- [ ] 日志级别使用正确
- [ ] 无敏感信息泄露
- [ ] SQL 性能达标（有索引）
- [ ] 单元测试通过
- [ ] Commit Message 规范

---

## 附：常用配置参考

### 常用注解

| 注解 | 用途 |
|------|------|
| `@RestController` | RESTful 控制器 |
| `@Service` | 业务逻辑组件 |
| `@Repository` | 数据访问组件 |
| `@Component` | 通用组件 |
| `@Configuration` | 配置类 |
| `@Valid` / `@Validated` | 参数校验 |
| `@Transactional` | 事务控制 |
| `@Async` | 异步执行 |
| `@Cacheable` | 缓存 |

### 常用工具类

| 工具 | 用途 |
|------|------|
| Hutool | 简化 Java 工具库 |
| Lombok | 简化 POJO 代码 |
| Jackson / Fastjson | JSON 处理 |
| Apache Commons | 通用工具 |

---

> **版本**：v1.0.0  
> **维护者**：后端架构组  
> **最后更新**：2026-06-10

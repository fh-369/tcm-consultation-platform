# 任务一：项目初始化与基础框架

> **目标**：搭建 Spring Boot 项目基础结构，完成依赖配置、数据库初始化、实体映射和全局配置。

---

## 环境要求

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 17 | 必须 JDK 17，不支持 JDK 11 或 21 |
| Maven | 3.8+ | 用于依赖管理和构建 |
| MySQL | 8.0 | 本项目使用 MySQL 8.0 |
| IDE | IntelliJ IDEA | 推荐开启 Lombok 插件 |

---

## 任务清单

### 任务 1.1：填写 `pom.xml` 依赖坐标

打开 `pom.xml`，找到所有 `<!-- TODO:` 标记，根据提示搜索并填写正确的 Maven 依赖坐标。

**需要填写的依赖（共 10 项）：**

| 序号 | 依赖名称 | 提示 |
|------|---------|------|
| 1 | Spring Boot 父依赖 | groupId: org.springframework.boot |
| 2 | spring-boot-starter-web | Spring Boot Web 框架核心 |
| 3 | spring-boot-starter-security | Spring Security 安全框架 |
| 4 | spring-boot-starter-validation | 参数校验支持 |
| 5 | mybatis-plus-spring-boot3-starter | 注意使用 spring-boot3 版本 |
| 6 | mysql-connector-j | scope 设为 runtime |
| 7-9 | jjwt 三件套 | api + impl + jackson，impl 和 jackson 的 scope 为 runtime |
| 10 | poi-ooxml | Apache POI Excel 支持 |
| 11 | opencsv | CSV 导出支持 |
| 12 | lombok | optional 设为 true |
| 13 | spring-boot-starter-test | scope 设为 test |

**提示**：访问 https://mvnrepository.com 搜索依赖坐标。

**验证**：执行 `mvn dependency:tree`，确认依赖树正确加载。

---

### 任务 1.2：填写 `application.yml` 配置

打开 `src/main/resources/application.yml`，找到所有 `TODO` 标记，根据注释填写配置值。

**关键配置项：**

| 配置路径 | 说明 | 参考值 |
|---------|------|--------|
| server.port | HTTP 端口 | 8080 |
| spring.datasource.url | 数据库连接 URL | jdbc:mysql://localhost:3306/tcm_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true |
| spring.datasource.driver-class-name | MySQL 驱动类 | com.mysql.cj.jdbc.Driver |
| mybatis-plus.configuration.map-underscore-to-camel-case | 下划线转驼峰 | true |
| jwt.expiration | JWT 过期时间(毫秒) | 86400000 (24小时) |

**注意**：
- `jwt.secret` 必须足够长（至少 32 字符），否则 HS256 算法会报错
- `ai.dashscope.api-key` 可使用环境变量占位符 `${DASHSCOPE_API_KEY:}`，留空即可
- `tcm.upload.path` 默认 `./uploads`

---

### 任务 1.3：初始化数据库

执行 `src/main/resources/schema.sql` 脚本，创建数据库和 6 张表。

```bash
mysql -u root -p < src/main/resources/schema.sql
```

**验证**：登录 MySQL，确认以下表已创建：

```sql
USE tcm_platform;
SHOW TABLES;
-- 应显示：users, patient_accounts, consultations, knowledge_articles, recipes, uploads
```

---

### 任务 1.4：完善启动类 `TcmPlatformApplication.java`

**需要完成：**
1. 添加 `@MapperScan` 注解，扫描 `com.tcm.platform.mapper` 包
2. 在 `main` 方法中调用 `SpringApplication.run()`

**提示**：`@MapperScan` 来自 `org.mybatis.spring.annotation.MapperScan`。

---

### 任务 1.5：实现 `Result.java` 统一响应类

**需要完成：**
1. 定义 4 个字段：`code(int)`、`message(String)`、`data(T)`、`timestamp(LocalDateTime)`
2. 实现 4 个静态工厂方法：
   - `success(T data)` — 成功响应，code=200
   - `success(String message, T data)` — 成功响应，自定义消息
   - `error(int code, String message)` — 错误响应
   - `error(String message)` — 错误响应，默认 code=500

**注意**：`data` 字段使用泛型 `T`，`timestamp` 使用 `LocalDateTime.now()`。

---

### 任务 1.6：实现 `GlobalExceptionHandler.java` 全局异常处理

**需要完成：**
1. `handleValidationException` — 从异常中提取字段校验错误消息，返回 `Result.error(400, message)`
2. `handleIllegalArgument` — 返回 `Result.error(400, ex.getMessage())`
3. `handleRuntimeException` — 打印日志后返回 `Result.error(500, "系统内部错误")`

**提示**：
- 校验错误消息提取：`ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "))`
- 日志输出可使用 `System.err.println()`（后续会引入 SLF4J）

---

### 任务 1.7：实现 `AppConfig.java` 通用 Bean

**需要完成：**
1. `passwordEncoder()` — 返回 `new BCryptPasswordEncoder()`
2. `restTemplate()` — 返回 `new RestTemplate()`

---

### 任务 1.8：实现 `MyBatisPlusConfig.java`

**需要完成：**
1. `mybatisPlusInterceptor()` — 创建 `MybatisPlusInterceptor`，添加 `PaginationInnerInterceptor(DbType.MYSQL)`
2. `metaObjectHandler()` — 创建匿名 `MetaObjectHandler` 实现：
   - `insertFill`: 填充 `createdAt` 和 `updatedAt` 为 `LocalDateTime.now()`
   - `updateFill`: 填充 `updatedAt` 为 `LocalDateTime.now()`

**提示**：使用 `this.strictInsertFill(metaObject, "fieldName", LocalDateTime.class, value)`。

---

### 任务 1.9：实现 `WebConfig.java` CORS 跨域配置

**需要完成：**
- 在 `addCorsMappings` 方法中配置 `/api/**` 的跨域规则

**提示**：
```java
registry.addMapping("/api/**")
    .allowedOriginPatterns("*")
    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    .allowedHeaders("*")
    .exposedHeaders("Authorization")
    .allowCredentials(true)
    .maxAge(3600);
```

---

### 任务 1.10：实现 `SecurityConfig.java` 安全配置

**这是本任务中最具挑战的部分**，需要配置 Spring Security 的 `SecurityFilterChain`。

**需要完成：**
1. 禁用 CSRF（无状态 API 不需要）
2. 禁用 frameOptions
3. 配置无状态会话管理 (`STATELESS`)
4. 设置 URL 权限规则
5. 添加 JWT 过滤器

**URL 权限规则：**

| URL 模式 | 权限 |
|---------|------|
| `/api/auth/**` | permitAll（允许所有访问） |
| `/api/patient/**` | hasAnyRole("PATIENT", "DOCTOR", "ADMIN") |
| `/api/admin/**` | hasAnyRole("DOCTOR", "ADMIN") |
| `/uploads/**` | permitAll |
| `/swagger-ui/**`, `/v3/api-docs/**` | permitAll |
| 其他请求 | authenticated（需要认证） |

**提示**：代码结构已用注释给出，取消注释并理解每行含义。

---

### 任务 1.11：完善 6 个实体类

打开 `entity/` 包下的 6 个实体类，根据 `schema.sql` 中的表结构定义字段。

**字段命名规则：**
- 数据库字段使用下划线命名（如 `password_hash`）
- Java 字段使用驼峰命名（如 `passwordHash`）
- MyBatis-Plus 的 `map-underscore-to-camel-case=true` 会自动映射

**需要添加的注解：**
- `@TableField(fill = FieldFill.INSERT)` — 插入时自动填充
- `@TableField(fill = FieldFill.INSERT_UPDATE)` — 插入和更新时都填充

| 实体类 | 对应表 | 字段数 | 特殊字段 |
|-------|--------|--------|---------|
| User | users | 7 | role 为 String 类型 |
| PatientAccount | patient_accounts | 7 | - |
| Consultation | consultations | 17 | 核心业务表，字段最多 |
| KnowledgeArticle | knowledge_articles | 9 | published 为 Boolean |
| Recipe | recipes | 9 | ingredients 和 steps 为 String（JSON 存储） |
| Upload | uploads | 7 | - |

---

### 任务 1.12：完善 DTO 校验注解

打开 `dto/` 包下的类，为必填字段添加 `@NotBlank` 或 `@Size` 校验注解。

| DTO 类 | 需要校验的字段 | 校验规则 |
|-------|-------------|---------|
| LoginRequest | username, password | `@NotBlank` |
| RegisterRequest | username | `@NotBlank` + `@Size(min=3)` |
| RegisterRequest | password | `@NotBlank` + `@Size(min=6)` |
| ConsultationRequest | patientName | `@NotBlank(message="患者姓名不能为空")` |
| ConsultationRequest | symptoms | `@NotBlank(message="症状描述不能为空")` |
| AIQuestionRequest | question | `@NotBlank(message="问题不能为空")` |

---

## 验收标准

### 自动验证

```bash
# 1. 编译通过
mvn compile

# 2. 应用能正常启动（无报错）
mvn spring-boot:run

# 3. 启动后访问 http://localhost:8080 应返回 404（正常，因为没有配置首页）
```

### 手动检查清单

- [ ] `pom.xml` 中 10 项依赖坐标全部填写正确
- [ ] `application.yml` 中所有 TODO 配置项已填写
- [ ] 数据库 6 张表已创建，初始数据已插入
- [ ] `TcmPlatformApplication` 能正常启动，日志中无异常
- [ ] `Result.success()` 和 `Result.error()` 能正确返回响应对象
- [ ] 全局异常处理器能捕获 `IllegalArgumentException`
- [ ] 6 个实体类字段与数据库表结构一致
- [ ] DTO 校验注解已添加

---

## 常见问题

### Q1: 启动时报 `ClassNotFoundException: com.mysql.cj.jdbc.Driver`
**A**: 检查 `pom.xml` 中 MySQL 驱动依赖的 scope 是否为 `runtime`。

### Q2: 启动时报 `No qualifying bean of type 'PasswordEncoder'`
**A**: 检查 `AppConfig.java` 中 `passwordEncoder()` 方法是否正确返回 `BCryptPasswordEncoder`。

### Q3: JWT 报 `IllegalArgumentException: secret key byte array is too short`
**A**: `jwt.secret` 至少需要 32 个字符。使用足够长的随机字符串。

### Q4: 实体类字段无法自动填充 `createdAt`/`updatedAt`
**A**: 确保：
1. 实体字段添加了 `@TableField(fill = FieldFill.INSERT)` 注解
2. `MyBatisPlusConfig` 中 `metaObjectHandler()` Bean 已正确配置
3. 字段名拼写正确：`createdAt` 和 `updatedAt`

### Q5: `mvn compile` 报 Lombok 相关错误
**A**: 确保 IDE 已安装 Lombok 插件，且 `pom.xml` 中 Lombok 依赖已正确配置。

---

## 参考资源

- Spring Boot 官方文档: https://docs.spring.io/spring-boot/docs/3.2.0/reference/html/
- MyBatis-Plus 官方文档: https://baomidou.com/pages/quick-start/
- Spring Security 官方文档: https://docs.spring.io/spring-security/reference/index.html
- Maven 仓库搜索: https://mvnrepository.com
- BCrypt 密码加密: https://github.com/josephwitt/BCrypt

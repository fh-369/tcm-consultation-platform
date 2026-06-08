# 中医问诊与养生平台实战引导

这份文档是给你边学边做用的，不是“把项目一次性写完”的答案包。

## 仓库结构与路径规则

当前项目采用前后端一体仓库结构：

```text
tcm-consultation-platform/
├─ backend/   Spring Boot 后端项目
├─ frontend/  前端项目预留目录
└─ docs/      项目文档
```

本指南原本以 Spring Boot 项目根目录为视角编写，因此文中出现的 `pom.xml`、`src/main/...`、`src/test/...` 和 `README.md`，均表示 `backend/` 下的对应路径。

例如：

```text
pom.xml                                  -> backend/pom.xml
src/main/resources/application.yml       -> backend/src/main/resources/application.yml
src/test/java/...                        -> backend/src/test/java/...
```

运行 Maven 命令前，推荐先进入后端目录：

```powershell
cd backend
mvn compile
```

你也可以留在仓库根目录并使用：

```powershell
mvn -f backend/pom.xml compile
```

`backend/src/test` 是 Maven 标准测试目录，后续 Day 17 会使用，不要删除。

我们的工作方式是：

1. 你亲手创建文件、复制代码、运行命令、观察报错。
2. 我给你每一步的目标、解释、完整可复制代码和验证方式。
3. 如果报错，你把报错贴给我，我带你定位原因。
4. 每完成一个小阶段，都要做一次验收，不靠感觉宣布完成。

项目原始任务书把开发周期拆成 21 天。我们按这个节奏推进，但不死板。如果某一天内容太多，可以拆成两次完成。

## 你应该怎么使用这份文档

每次开始时，你可以直接对我说：

```text
开始 Day 1
```

或者：

```text
我做到 Day 3 的 JwtUtils 了，请带我继续
```

我会按以下格式带你做：

```text
1. 先解释这一步要解决什么问题
2. 告诉你要打开/创建哪个文件
3. 给你完整代码，让你复制粘贴
4. 告诉你运行什么命令验证
5. 如果失败，带你看报错
```

## 重要约定

### 代码提供方式

虽然这不是一次性交付项目，但我会在每个步骤给你完整代码。

例如某一步需要写 `JwtUtils.java`，我会给你整个文件内容，而不是只给几行片段。你要做的是：

1. 自己创建对应文件。
2. 把完整代码粘贴进去。
3. 运行验证命令。
4. 把结果告诉我。

这样你不会卡在语法细节上，同时还能知道每个文件为什么存在。

### 不要跳过验证

每一天至少要做一次验证。常用验证方式包括：

```bash
mvn compile
mvn test
mvn spring-boot:run
```

接口类任务还要用 Postman 或 Apifox 测试。

### 出错时怎么问我

不要只说“报错了”。请尽量贴：

```text
我正在做 Day X 的任务 Y。
我运行了：xxx
报错内容是：
...
```

这样我能更快判断是依赖、配置、代码、数据库还是环境问题。

## 项目总目标

最终你要完成一个基于 Spring Boot 的中医问诊与养生平台后端，核心能力包括：

- 患者注册、登录、提交问诊单
- 医生/管理员登录、查看和处理问诊单
- 中医知识文章管理
- 药膳推荐管理
- Dashboard 数据统计
- 问诊单 CSV 导出
- AI 问答服务
- JWT 登录认证和角色权限控制
- MySQL 数据持久化
- Docker 部署
- 基础测试和项目交付

## 技术栈

| 技术 | 版本 | 用途 |
| --- | --- | --- |
| Java | 17 | 后端开发语言 |
| Spring Boot | 3.2.0 | 后端应用框架 |
| Spring Security | Boot 管理版本 | 登录认证、权限控制 |
| MyBatis-Plus | 3.5.5 | 数据库 ORM 和 CRUD |
| MySQL | 8.0 | 数据库 |
| JWT / jjwt | 0.12.3 | 无状态登录令牌 |
| Lombok | Boot 管理版本 | 简化 getter/setter |
| OpenCSV | 5.7.1 | CSV 导出 |
| Apache POI | 5.2.5 | Excel 相关能力预留 |
| Docker | 稳定版本 | 容器化部署 |

## 项目分层

项目采用典型后端分层结构：

```text
controller  -> 接收 HTTP 请求
service     -> 处理业务逻辑
mapper      -> 访问数据库
entity      -> 数据库表对应的 Java 对象
dto         -> 接口请求/响应对象
config      -> 框架配置
common      -> 通用响应、异常处理
security    -> JWT 和登录认证
ai          -> AI 服务集成
```

你可以这样理解：

```text
前端/Postman
  -> Controller
  -> Service
  -> Mapper
  -> MySQL
```

Controller 不直接写复杂业务逻辑，Service 不直接拼 HTTP 响应，Mapper 不处理业务判断。这样项目才不乱。

## 数据库核心表

| 表名 | 含义 |
| --- | --- |
| `users` | 医生和管理员账号 |
| `patient_accounts` | 患者账号 |
| `consultations` | 问诊单，核心业务表 |
| `knowledge_articles` | 中医知识文章 |
| `recipes` | 药膳推荐 |
| `uploads` | 文件上传记录 |

## 21 天路线图

| 天数 | 主题 | 你会完成什么 |
| --- | --- | --- |
| Day 1 | 项目初始化 | 补全 `pom.xml`、`application.yml`、启动类 |
| Day 2 | 实体和数据库 | 补全 6 个 Entity，执行建表脚本，验证 Mapper |
| Day 3 | JWT 工具类 | 创建 `JwtUtils`，实现 token 生成和解析 |
| Day 4 | 认证过滤器和安全配置 | 创建 JWT Filter，补全 SecurityConfig、CORS |
| Day 5 | 登录注册 | 实现 AuthService 和 AuthController |
| Day 6 | 统一响应和异常处理 | 完成 Result 和 GlobalExceptionHandler |
| Day 7 | 第一周回归 | Docker Compose 初稿，全链路认证测试 |
| Day 8 | 问诊业务 | 实现 ConsultationService |
| Day 9 | 提醒规则 | 实现 ReminderService |
| Day 10 | 患者端接口 | 实现 PatientController |
| Day 11 | 知识文章 | 实现 KnowledgeArticleService 和管理接口 |
| Day 12 | 药膳管理 | 实现 RecipeService 和管理接口 |
| Day 13 | Dashboard 和导出 | 统计接口、CSV 导出 |
| Day 14 | 第二周回归 | 完整业务流程测试 |
| Day 15 | AI 问答 | DashScopeService 和 AIController |
| Day 16 | 接口系统测试 | Postman/Apifox 全接口测试 |
| Day 17 | 单元测试 | Auth、Consultation、Reminder 测试 |
| Day 18 | Docker 部署 | Dockerfile、docker-compose |
| Day 19 | 安全加固 | SQL、安全、日志、异常优化 |
| Day 20 | 集成测试 | 端到端测试和测试报告 |
| Day 21 | 项目交付 | 整理代码、文档、演示材料 |

## Day 1：项目初始化

目标：让项目从“骨架”变成一个 Maven 能识别、Spring Boot 能启动的项目。

你会修改 3 个文件：

```text
pom.xml
src/main/resources/application.yml
src/main/java/com/tcm/platform/TcmPlatformApplication.java
```

### Day 1.1 补全 pom.xml

为什么要做：

`pom.xml` 决定这个项目能使用哪些框架。现在项目里有 Spring Boot、MyBatis-Plus、JWT、MySQL、Lombok 等代码提示，但依赖还没真正加入，所以会编译失败。

你要做：

打开 `pom.xml`，把里面的 TODO 按我给你的完整代码替换。

我在正式带你做 Day 1 时，会给你完整的 `pom.xml`。你复制进去之后，运行：

```bash
mvn dependency:tree
```

验收标准：

- Maven 能下载依赖。
- 输出里能看到 `spring-boot-starter-web`。
- 输出里能看到 `mybatis-plus-spring-boot3-starter`。
- 输出里能看到 `jjwt-api`、`jjwt-impl`、`jjwt-jackson`。

常见问题：

- 如果 Maven 下载很慢，可能是网络或镜像源问题。
- 如果提示 Java 版本不对，要检查 JDK 是否是 17。

### Day 1.2 补全 application.yml

为什么要做：

`application.yml` 是 Spring Boot 的配置中心。端口、数据库、JWT 密钥、上传路径、AI 配置都在这里。

你要配置的关键值：

```yaml
server:
  port: 8080

spring:
  application:
    name: tcm-platform
  datasource:
    url: jdbc:mysql://localhost:3306/tcm_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: ${DB_PASSWORD:root123}
    driver-class-name: com.mysql.cj.jdbc.Driver
```

注意：

`jwt.secret` 至少要 32 个字符，否则 JWT 签名会报错。

正式做 Day 1 时，我会给你完整的 `application.yml`。

### Day 1.3 完善启动类

为什么要做：

Spring Boot 项目必须有入口类。MyBatis-Plus 还需要知道 Mapper 接口在哪里，所以要加 `@MapperScan`。

你要补的关键 API：

```java
@MapperScan("com.tcm.platform.mapper")
SpringApplication.run(TcmPlatformApplication.class, args);
```

验收命令：

```bash
mvn compile
```

此时如果还没创建数据库，启动应用可能会因为数据库连接失败而报错，这是正常的。Day 1 的重点是编译通过。

## Day 2：实体类和数据库初始化

目标：让 Java 实体和 MySQL 表结构对应起来。

你会处理：

```text
src/main/java/com/tcm/platform/entity/User.java
src/main/java/com/tcm/platform/entity/PatientAccount.java
src/main/java/com/tcm/platform/entity/Consultation.java
src/main/java/com/tcm/platform/entity/KnowledgeArticle.java
src/main/java/com/tcm/platform/entity/Recipe.java
src/main/java/com/tcm/platform/entity/Upload.java
src/main/resources/schema.sql
```

### Day 2.1 补全 6 个实体类

为什么要做：

数据库里有 `users`、`consultations` 等表，Java 代码需要用实体类表示这些表中的一行数据。

你会学到：

- `@TableName`：指定数据库表名
- `@TableId`：指定主键
- `@TableField`：指定字段映射和自动填充
- `FieldFill.INSERT`：插入时自动填充
- `FieldFill.INSERT_UPDATE`：插入和更新时自动填充

我会逐个文件给你完整代码。你每粘贴完一个实体类，就可以对照 `schema.sql` 看字段是否一致。

### Day 2.2 初始化数据库

你要做：

1. 启动 MySQL 8.0。
2. 执行 `schema.sql`。
3. 确认 6 张表创建成功。

命令参考：

```bash
mysql -u root -p < src/main/resources/schema.sql
```

进入 MySQL 后验证：

```sql
USE tcm_platform;
SHOW TABLES;
```

应该看到：

```text
users
patient_accounts
consultations
knowledge_articles
recipes
uploads
```

### Day 2.3 验证 Mapper

目标：

确认 Java 项目能通过 MyBatis-Plus 连接数据库并查询数据。

我会带你写一个临时测试或简单接口来调用：

```java
userMapper.selectList(null)
```

如果能查到默认管理员和医生账号，说明数据库和 Mapper 连通了。

## Day 3：JWT 工具类

目标：创建 `JwtUtils`，让系统能生成和解析登录令牌。

你会创建：

```text
src/main/java/com/tcm/platform/security/JwtUtils.java
```

它负责：

- 从配置读取 `jwt.secret`
- 生成 token
- 从 token 解析用户 ID
- 从 token 解析角色
- 判断 token 是否有效
- 判断 token 是否过期

关键 API：

```java
Keys.hmacShaKeyFor(...)
Jwts.builder()
Jwts.parser().verifyWith(key).build()
```

注意：

jjwt `0.12.x` 的 API 和旧版本不一样，不能照抄很多旧教程里的写法。

## Day 4：JWT 过滤器和安全配置

目标：让请求进入 Controller 之前，Spring Security 能识别 token 和角色。

你会创建：

```text
src/main/java/com/tcm/platform/security/JwtAuthenticationFilter.java
```

你会补全：

```text
src/main/java/com/tcm/platform/config/SecurityConfig.java
src/main/java/com/tcm/platform/config/AppConfig.java
src/main/java/com/tcm/platform/config/WebConfig.java
```

权限规则：

| 路径 | 权限 |
| --- | --- |
| `/api/auth/**` | 所有人可访问 |
| `/api/patient/**` | PATIENT、DOCTOR、ADMIN |
| `/api/admin/**` | DOCTOR、ADMIN |
| `/uploads/**` | 所有人可访问 |
| `/swagger-ui/**`、`/v3/api-docs/**` | 所有人可访问 |
| 其他路径 | 必须登录 |

验收重点：

- 无 token 访问后台接口应被拒绝。
- 正确 token 可以访问对应角色接口。
- 角色不够时返回 403。

## Day 5：登录注册

目标：实现患者注册、患者登录、医生/管理员登录。

你会创建：

```text
src/main/java/com/tcm/platform/service/AuthService.java
src/main/java/com/tcm/platform/controller/AuthController.java
```

你会使用：

- `UserMapper`
- `PatientAccountMapper`
- `PasswordEncoder`
- `JwtUtils`
- `LoginRequest`
- `RegisterRequest`
- `LoginResponse`
- `Result`

接口：

| 方法 | 路径 | 含义 |
| --- | --- | --- |
| POST | `/api/auth/login/patient` | 患者登录 |
| POST | `/api/auth/login/admin` | 医生/管理员登录 |
| POST | `/api/auth/register` | 患者注册 |

验收：

用 Postman 或 Apifox 测试注册、登录，拿到 JWT token。

## Day 6：统一响应和异常处理

目标：让所有接口返回格式统一，让常见错误有清晰响应。

你会补全：

```text
src/main/java/com/tcm/platform/common/Result.java
src/main/java/com/tcm/platform/common/GlobalExceptionHandler.java
```

统一响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": "2026-06-04 15:00:00"
}
```

异常处理目标：

| 异常 | 返回 |
| --- | --- |
| 参数校验失败 | 400 |
| `IllegalArgumentException` | 400 |
| 资源不存在 | 404 |
| 权限不足 | 403 |
| 其他异常 | 500 |

## Day 7：第一周回归

目标：把认证授权链路跑通。

你要验证：

- 患者注册
- 患者登录
- 医生/管理员登录
- 无 token 访问受保护接口
- 错误 token
- 角色权限不足
- 参数校验失败

可选任务：

开始写 `docker-compose.yml`，先只启动 MySQL。

## Day 8：问诊业务

目标：实现问诊单的创建、查询、更新和统计。

你会创建：

```text
src/main/java/com/tcm/platform/service/ConsultationService.java
```

核心方法：

```text
createConsultation
listConsultations
getConsultationById
updateConsultation
getStatusDistribution
getUrgencyDistribution
getTrendLast6Months
```

你会用到：

- `LambdaQueryWrapper`
- `Page`
- `ConsultationMapper`
- `ConsultationRequest`
- `ConsultationUpdateRequest`

## Day 9：提醒规则服务

目标：根据症状、紧急度、持续时间生成提醒等级和提醒文本。

你会创建：

```text
src/main/java/com/tcm/platform/service/ReminderService.java
```

规则示例：

- 非常紧急：`urgent`
- 症状包含发热、胸痛、呼吸困难：提醒及时就医
- 症状包含失眠：给睡眠建议
- 症状包含胃痛、脾胃：给饮食建议
- 持续时间包含“周”或“月”：提醒重点关注

## Day 10：患者端接口

目标：让患者可以提交问诊、查看自己的问诊、浏览知识和药膳。

你会创建：

```text
src/main/java/com/tcm/platform/controller/PatientController.java
```

接口：

| 方法 | 路径 |
| --- | --- |
| POST | `/api/patient/consultation` |
| GET | `/api/patient/consultation/my` |
| GET | `/api/patient/knowledge` |
| GET | `/api/patient/recipe` |

## Day 11：知识文章管理

目标：实现中医知识文章的查询和后台 CRUD。

你会创建：

```text
src/main/java/com/tcm/platform/service/KnowledgeArticleService.java
```

你会扩展：

```text
src/main/java/com/tcm/platform/controller/AdminController.java
```

如果 `AdminController.java` 还不存在，我会带你创建。

## Day 12：药膳管理

目标：实现药膳推荐的查询和后台 CRUD。

你会创建：

```text
src/main/java/com/tcm/platform/service/RecipeService.java
```

支持筛选：

- 季节 `season`
- 体质 `constitution`

## Day 13：Dashboard 和 CSV 导出

目标：后台能看到统计数据，并导出问诊单。

你会创建：

```text
src/main/java/com/tcm/platform/service/ExportService.java
```

接口：

| 方法 | 路径 |
| --- | --- |
| GET | `/api/admin/dashboard` |
| GET | `/api/admin/export/consultation` |

Dashboard 返回内容：

- 问诊总数
- 待处理数量
- 文章数量
- 药膳数量
- 状态分布
- 紧急度分布
- 近 6 个月趋势

## Day 14：第二周回归

目标：把核心业务链路跑通。

完整流程：

```text
患者注册
患者登录
提交问诊单
医生登录
查看问诊单
更新问诊状态
查看 Dashboard
导出 CSV
管理知识文章
管理药膳
```

## Day 15：AI 问答

目标：接入阿里云 DashScope，同时支持无 API Key 时的 fallback。

你会创建：

```text
src/main/java/com/tcm/platform/ai/DashScopeService.java
src/main/java/com/tcm/platform/controller/AIController.java
```

接口：

| 方法 | 路径 |
| --- | --- |
| POST | `/api/ai/ask` |

返回示例：

```json
{
  "answer": "建议保持规律作息...",
  "mode": "fallback"
}
```

## Day 16：接口系统测试

目标：用 Postman 或 Apifox 把所有接口测一遍。

测试类型：

- 正常流程
- 参数为空
- token 为空
- token 错误
- 角色权限不足
- 分页参数边界
- 并发提交简单测试

建议你建立一个接口测试集合，后面答辩或验收会很有用。

## Day 17：单元测试

目标：至少覆盖核心 Service。

建议测试：

- `AuthService`
- `ConsultationService`
- `ReminderService`

常用注解：

```java
@SpringBootTest
@MockBean
@Autowired
```

## Day 18：Docker 部署

目标：让项目可以用 Docker 启动。

你会创建：

```text
Dockerfile
docker-compose.yml
```

Dockerfile 思路：

```text
第一阶段：用 Maven 镜像打包 JAR
第二阶段：用 JRE 镜像运行 JAR
```

docker-compose 包含：

- MySQL 8.0
- 后端应用
- 数据卷
- 初始化 SQL

## Day 19：安全加固和代码优化

目标：让项目更像一个可靠的后端项目。

检查项：

- 是否避免手写拼接 SQL
- 是否使用参数校验
- 是否有统一异常处理
- 是否有清晰日志
- 是否避免泄露密码和 token
- 是否把配置放到环境变量

## Day 20：集成测试

目标：完整模拟用户使用系统。

建议写测试报告，包含：

```text
测试场景
请求方式
请求路径
输入数据
期望结果
实际结果
是否通过
备注
```

## Day 21：项目交付

目标：整理最终提交材料。

交付清单：

- 源代码
- 数据库脚本
- 项目说明文档
- 接口测试截图或集合
- 测试报告
- Docker 部署说明
- 演示流程

演示建议顺序：

```text
1. 展示项目结构
2. 展示数据库表
3. 启动项目
4. 患者注册登录
5. 提交问诊单
6. 医生登录处理问诊
7. 展示 Dashboard
8. 展示知识文章/药膳管理
9. 展示 AI 问答
10. 展示 Docker 或测试结果
```

## 每天完成后的记录模板

你可以每天把下面这段复制到文档末尾，记录自己的进度。

```markdown
### Day X 完成记录

- 日期：
- 今天完成：
- 修改/创建的文件：
- 运行过的命令：
- 遇到的问题：
- 如何解决：
- 当前是否通过验收：
- 下一步：
```

## 当前建议的下一步

现在最适合开始的是：

```text
开始 Day 1
```

我会先带你补全 `pom.xml`。你复制粘贴后运行 Maven 验证。如果依赖下载或编译报错，我们就从报错开始学，这比直接给你一个完成品更有价值。

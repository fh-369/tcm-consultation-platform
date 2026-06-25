# 知身问养后端服务

本目录是知身问养平台的 Spring Boot 后端服务，负责账号认证、问诊业务、医生工作台、后台管理、内容管理、数据导出和 AI 问答等核心能力。

## 技术栈

- Java 17
- Spring Boot 3.2
- Spring Security
- JWT
- MyBatis-Plus
- MySQL
- OpenCSV
- DashScope OpenAI 兼容接口
- JUnit 5 / Spring Boot Test

## 核心模块

| 模块 | 说明 |
| --- | --- |
| 认证与账号 | 普通用户、医生、管理员登录注册，JWT 签发与校验 |
| 科室与医生准入 | 科室数据、医生入驻申请、管理员审核 |
| 问诊业务 | 用户提交问诊、科室分诊、自动分配、医生认领与处理 |
| 医患沟通 | 医生回复、患者补充回复、处理进度记录 |
| 内容管理 | 养生文章、药膳推荐的新增、编辑、发布、浏览量 |
| 数据统计 | 后台数据概览、问诊趋势、状态和紧急程度统计 |
| 数据导出 | 按条件导出问诊 CSV |
| AI 问答 | DashScope 模型调用、流式回答、对话持久化、站内推荐 |

## 环境要求

- JDK 17
- Maven 3.8+
- MySQL 8.0+ 或兼容版本

## 数据库初始化

进入后端目录后执行：

```powershell
mysql -u root -p < src/main/resources/schema.sql
```

如果 `src/main/resources/migration/` 中存在后续迁移 SQL，应按功能阶段依次执行。

当前主要表包括：

- `accounts`
- `users`
- `patient_accounts`
- `departments`
- `consultations`
- `consultation_messages`
- `consultation_progress_records`
- `knowledge_articles`
- `recipes`
- `uploads`
- `ai_conversations`
- `ai_messages`
- `ai_conversation_recommendations`

## 环境变量

后端配置位于 `src/main/resources/application.yml`。敏感信息通过环境变量传入，不应写入源码。

| 变量 | 说明 | 示例 |
| --- | --- | --- |
| `DB_USERNAME` | MySQL 用户名 | `root` |
| `DB_PASSWORD` | MySQL 密码 | `your_mysql_password` |
| `JWT_SECRET` | JWT 签名密钥 | `please_change_to_a_long_random_secret` |
| `DASHSCOPE_API_KEY` | DashScope API Key | `sk-xxxx` |
| `DASHSCOPE_MODEL` | DashScope 模型名称 | `qwen-plus` |

PowerShell 示例：

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="please_change_to_a_long_random_secret"
$env:DASHSCOPE_API_KEY="your_dashscope_api_key"
$env:DASHSCOPE_MODEL="qwen-plus"
```

CMD 示例：

```cmd
set DB_USERNAME=root
set DB_PASSWORD=your_mysql_password
set JWT_SECRET=please_change_to_a_long_random_secret
set DASHSCOPE_API_KEY=your_dashscope_api_key
set DASHSCOPE_MODEL=qwen-plus
```

`DASHSCOPE_API_KEY` 留空时，AI 问答不能真实调用模型，但系统仍可启动。

## 本地启动

```powershell
mvn spring-boot:run
```

如果 Maven 没有加入系统 `PATH`，可以使用完整路径：

```powershell
& "D:\IntelliJ IDEA 2025.3.3\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
```

后端默认端口：

```text
8080
```

如果启动失败并提示端口被占用，需要停止旧的后端进程，或修改 `server.port`。

## 测试

运行后端全量测试：

```powershell
mvn test
```

指定单个测试类：

```powershell
mvn -Dtest=ConsultationServiceTest test
```

## 打包

```powershell
mvn clean package
```

打包后可在 `target/` 中找到 jar 文件。部署时通常只需要 jar、数据库脚本、迁移脚本、上传目录和环境变量配置。

## 文件上传

上传目录由以下配置控制：

```yaml
tcm:
  upload:
    path: ./uploads
```

`uploads/` 是运行期文件目录，不应提交到 Git。

## 本地演示医生账号

当前本地演示数据创建了 5 个医生账号：

| 用户名 | 科室 |
| --- | --- |
| `doctor_general` | 综合咨询 |
| `doctor_internal` | 中医内科 |
| `doctor_gynecology` | 中医妇科 |
| `doctor_pediatrics` | 中医儿科 |
| `doctor_tuina` | 针灸推拿科 |

本地演示初始密码：

```text
doctor123456
```

该密码仅用于本地演示。真实部署时应提供修改密码或管理员重置密码机制，并立即更换默认密码。

## 常见问题

### `mvn` 不是内部或外部命令

说明 Maven 没有加入系统 `PATH`。可以使用 IntelliJ IDEA 自带 Maven 的完整路径运行，或配置系统环境变量。

### CMD 中 `$env:DB_PASSWORD` 报错

`$env:` 是 PowerShell 语法。CMD 应使用：

```cmd
set DB_PASSWORD=your_mysql_password
```

PowerShell 才使用：

```powershell
$env:DB_PASSWORD="your_mysql_password"
```

### 端口 8080 被占用

通常是旧后端进程仍在运行。停止旧进程后再启动，或临时修改 `server.port`。

### AI 模型调用超时

可能原因包括模型首字延迟较长、账号额度或权限限制、网络波动、模型不适合当前接口。当前项目默认推荐 `qwen-plus`。

### 不要提交敏感信息

不要把数据库密码、JWT Secret、DashScope API Key 写入源码、提交记录、截图或日志。

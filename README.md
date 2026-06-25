# 知身问养

知身问养是一个面向中医问诊与日常养护场景的全栈 Web 项目。平台支持普通用户提交在线问诊，医生按科室接诊与回复，管理员进行人员、问诊、内容和数据管理，同时提供养生知识、药膳推荐和 AI 养护问答能力。

本项目采用前后端分离结构，适合作为 Java 后端、Vue 前端、权限系统、业务流程建模和 Git/GitHub 协作实践项目。

## 核心功能

- 用户端
  - 用户注册与登录
  - 在线创建问诊单
  - 查看我的问诊、处理进度和医生回复
  - 浏览养生知识与药膳推荐
  - 使用 AI 养护问答，并可选择问诊单作为上下文

- 医生端
  - 医生入驻申请
  - 科室问诊池认领问诊
  - 我的问诊处理、回复和状态更新
  - 按科室、状态、紧急程度筛选问诊

- 管理员端
  - 平台数据概览
  - 用户管理与医生管理
  - 医生入驻审核
  - 问诊调度、科室调整和医生分配
  - 养生文章与药膳内容管理
  - 问诊数据筛选与 CSV 导出

- AI 问答
  - DashScope OpenAI 兼容接口接入
  - 流式回答体验
  - 对话与消息数据库持久化
  - 站内延伸阅读智能推荐快照

## 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Java 17, Spring Boot 3.2, Spring Security, MyBatis-Plus |
| 数据库 | MySQL |
| 认证 | JWT, BCrypt |
| 前端 | Vue 3, Vite, Pinia, Vue Router, Element Plus, Axios |
| AI | 阿里云 DashScope OpenAI 兼容接口 |
| 测试 | JUnit 5, Spring Boot Test, Vitest |
| 构建 | Maven, npm |

## 项目结构

```text
tcm-consultation-platform/
├─ backend/   Spring Boot 后端服务
├─ frontend/  Vue 3 前端应用
├─ docs/      项目报告、开发记录和内容路线
└─ README.md  GitHub 项目入口说明
```

## 环境要求

- JDK 17
- Maven 3.8+
- Node.js 18+
- npm
- MySQL 8.0+ 或兼容版本

## 快速启动

### 1. 初始化数据库

先创建并初始化数据库：

```powershell
cd backend
mysql -u root -p < src/main/resources/schema.sql
```

如果项目后续新增了 `backend/src/main/resources/migration/` 下的迁移 SQL，请按文件创建时间或功能阶段依次执行。

### 2. 配置后端环境变量

不要把真实密码或 API Key 写入源码。建议在本机终端设置环境变量。

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

`DASHSCOPE_API_KEY` 可以留空。留空时 AI 问答会走降级逻辑或返回不可用提示。

### 3. 启动后端

```powershell
cd backend
mvn spring-boot:run
```

如果本机没有把 Maven 加入 `PATH`，可以使用 IDE 自带 Maven 的完整路径，例如：

```powershell
& "D:\IntelliJ IDEA 2025.3.3\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run
```

后端默认地址：

```text
http://localhost:8080
```

### 4. 启动前端

```powershell
cd frontend
npm install
npm run dev
```

前端默认地址：

```text
http://localhost:5173
```

## 本地演示账号

本地演示数据中可以使用以下医生账号。初始密码仅用于本地演示，不要用于生产环境。

```text
doctor_general
doctor_internal
doctor_gynecology
doctor_pediatrics
doctor_tuina
```

本地演示医生初始密码：

```text
doctor123456
```

管理员账号由数据库初始化数据提供。真实部署时应立即修改默认账号密码，并重新设置 `JWT_SECRET`。

## 测试与构建

后端测试：

```powershell
cd backend
mvn test
```

前端测试：

```powershell
cd frontend
npm test
```

前端生产构建：

```powershell
cd frontend
npm run build
```

后端打包：

```powershell
cd backend
mvn clean package
```

## 部署轻量化建议

如果只需要部署运行，不需要携带完整开发历史和测试依赖，可以单独准备轻量部署目录，通常只保留：

```text
backend/target/*.jar
frontend/dist/
backend/src/main/resources/schema.sql
backend/src/main/resources/migration/
启动脚本和环境变量示例
```

不要把以下内容放入部署包：

```text
.git/
node_modules/
frontend/.vite/
backend/target/中除 jar 外的临时构建文件
.env
真实上传文件
数据库备份
本地 IDE 配置
```

## 项目文档

- [项目完整说明](docs/PROJECT_DESCRIPTION_REPORT.md)
- [阶段开发报告](docs/PROJECT_DEVELOPMENT_REPORT.md)
- [内容来源体系与产品路线](docs/CONTENT_SOURCE_AND_PRODUCT_ROADMAP.md)
- [前端计划与设计记录](docs/FRONTEND_PLAN.md)
- [Git 与 GitHub 实战指南](docs/中医问诊与养生平台_Git与GitHub双线实战指南.md)

## 安全说明

- 不要提交 `.env`、数据库密码、JWT 密钥和 API Key。
- 生产环境必须修改默认账号密码。
- AI 回答只作为一般养护参考，不能替代医生诊断和治疗。
- 导出数据可能包含个人信息，真实部署时应增加审计和权限控制。

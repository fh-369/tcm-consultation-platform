# 中医问诊与养生平台项目说明报告

## 0. 当前仓库结构

项目目前采用前后端一体仓库结构：

```text
tcm-consultation-platform/
├─ backend/   当前 Spring Boot 后端 Starter
├─ frontend/  前端项目预留目录
├─ docs/      项目说明与实战指南
└─ README.md  仓库总说明
```

当前实际业务代码位于 `backend/src/main`，测试代码将位于 `backend/src/test`。`src/test` 是 Maven 标准结构的一部分，不是冗余目录。

前端尚未初始化，因为当前任务书主要定义了后端 API。后续确定前端技术栈和页面范围后，再在 `frontend` 中创建项目。

## 1. 项目概述

中医问诊与养生平台是一个基于 Spring Boot 的后端 Web 应用实践项目。它面向中医问诊、健康养生内容管理和后台运营管理场景，目标是让患者能够在线提交问诊信息，让医生或管理员能够处理问诊单、维护中医知识文章和药膳推荐，并通过 Dashboard 查看业务统计数据。

从课程实践角度看，这个项目不是一个简单的 CRUD 练习，而是一个综合型 Java 企业应用训练项目。它覆盖后端开发中常见的能力：项目初始化、数据库建模、用户认证、权限控制、业务分层、数据查询、接口设计、异常处理、AI 服务集成、测试和 Docker 部署。

## 2. 项目建设目标

项目最终希望实现以下目标：

1. 搭建一个结构清晰的 Spring Boot 后端项目。
2. 使用 MyBatis-Plus 完成 MySQL 数据库持久化。
3. 使用 JWT 和 Spring Security 实现登录认证与角色权限控制。
4. 实现患者端、医生端和管理员端的核心业务接口。
5. 提供中医知识文章、药膳推荐、问诊单管理等业务能力。
6. 接入 AI 问答能力，提供基础中医养生问答服务。
7. 提供 Dashboard 统计和问诊单 CSV 导出能力。
8. 完成基础测试、Docker 部署和最终交付材料。

## 3. 用户角色

系统计划支持 3 类角色。

| 角色 | 说明 | 主要权限 |
| --- | --- | --- |
| 患者 Patient | 使用患者端功能的普通用户 | 注册、登录、提交问诊单、查看自己的问诊记录、浏览知识文章、浏览药膳推荐、使用 AI 问答 |
| 医生 Doctor | 后台处理问诊业务的用户 | 登录后台、查看问诊单、更新问诊状态、管理知识文章、管理药膳、查看 Dashboard、导出数据 |
| 管理员 Admin | 系统最高权限用户 | 拥有医生权限，并可作为系统管理角色使用 |

当前设计中，医生和管理员存放在 `users` 表中，患者存放在 `patient_accounts` 表中。

## 4. 核心业务范围

### 4.1 认证与账号业务

认证模块负责处理系统登录、注册和 token 生成。

计划实现的能力：

- 患者注册
- 患者登录
- 医生/管理员登录
- BCrypt 密码加密
- JWT token 生成
- JWT token 校验
- 根据角色控制接口访问权限

对应接口规划：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/register` | 患者注册 |
| POST | `/api/auth/login/patient` | 患者登录 |
| POST | `/api/auth/login/admin` | 医生/管理员登录 |

业务意义：

这是系统所有后续业务的入口。没有认证模块，系统无法区分患者、医生和管理员，也无法控制不同接口的访问权限。

### 4.2 患者问诊业务

问诊业务是本项目的核心业务。

患者可以提交问诊单，填写姓名、年龄、性别、手机号、症状描述、持续时间、过敏史、紧急程度和备注等信息。系统保存问诊单后，医生或管理员可以在后台查看、筛选和处理这些问诊单。

计划实现的能力：

- 患者提交问诊单
- 患者查看自己的问诊记录
- 后台分页查询问诊单
- 按状态筛选问诊单
- 按紧急程度筛选问诊单
- 按姓名、症状、手机号进行关键字搜索
- 医生更新问诊状态
- 医生填写处理备注
- 记录跟进时间

对应接口规划：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/patient/consultation` | 患者提交问诊单 |
| GET | `/api/patient/consultation/my` | 患者查看自己的问诊记录 |
| GET | `/api/admin/consultation` | 后台查询问诊单 |
| PUT | `/api/admin/consultation/{id}` | 后台更新问诊单 |

业务意义：

问诊单连接患者端和医生端，是整个平台的数据中心。后续的提醒规则、Dashboard 统计、CSV 导出都围绕问诊单展开。

### 4.3 问诊提醒业务

提醒业务根据问诊单内容生成辅助提醒。

计划实现的规则包括：

- 如果紧急程度是“非常紧急”，提醒医生优先查看。
- 如果症状包含“发热”“胸痛”“呼吸困难”等关键词，提示及时就医。
- 如果症状包含“失眠”，给出睡眠相关建议。
- 如果症状包含“胃痛”“脾胃”，给出饮食调理提示。
- 如果持续时间包含“周”“月”，提示病程较长，需要重点关注。
- 根据规则生成 `reminderLevel`，例如 `normal`、`attention`、`urgent`。
- 根据等级生成下一次跟进时间。

业务意义：

该模块让问诊单不只是被动存储数据，而是能够根据症状信息生成简单的业务判断，为医生处理问诊提供辅助。

### 4.4 中医知识文章业务

知识文章模块用于维护和展示中医健康知识。

计划实现的能力：

- 患者端浏览已发布文章
- 后台查看所有文章
- 后台新增文章
- 后台编辑文章
- 后台删除文章
- 按分类筛选
- 区分草稿和已发布状态
- 记录浏览量

对应接口规划：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/patient/knowledge` | 患者端查看已发布文章 |
| GET | `/api/admin/knowledge` | 后台查看文章列表 |
| POST | `/api/admin/knowledge` | 后台新增文章 |
| PUT | `/api/admin/knowledge/{id}` | 后台更新文章 |
| DELETE | `/api/admin/knowledge/{id}` | 后台删除文章 |

业务意义：

知识文章模块让平台不仅能处理问诊，还能提供健康科普内容，增强平台的养生服务属性。

### 4.5 药膳推荐业务

药膳模块用于维护和展示养生食疗推荐内容。

计划实现的能力：

- 患者端浏览已发布药膳
- 按季节筛选药膳
- 按体质筛选药膳
- 后台新增药膳
- 后台编辑药膳
- 后台删除药膳
- 区分草稿和已发布状态
- 记录浏览量

对应接口规划：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/patient/recipe` | 患者端查看药膳推荐 |
| GET | `/api/admin/recipe` | 后台查看药膳列表 |
| POST | `/api/admin/recipe` | 后台新增药膳 |
| PUT | `/api/admin/recipe/{id}` | 后台更新药膳 |
| DELETE | `/api/admin/recipe/{id}` | 后台删除药膳 |

业务意义：

药膳推荐是中医养生平台的重要业务内容，和知识文章一起构成平台的健康内容服务。

### 4.6 Dashboard 统计业务

Dashboard 模块用于后台查看系统运营数据。

计划统计内容：

- 问诊单总数
- 待处理问诊单数量
- 知识文章数量
- 药膳数量
- 问诊状态分布
- 紧急程度分布
- 最近 6 个月问诊趋势

对应接口规划：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/dashboard` | 后台 Dashboard 统计 |

业务意义：

Dashboard 能帮助医生或管理员快速了解平台当前运行情况，例如问诊压力、紧急问诊数量、内容建设情况等。

### 4.7 数据导出业务

数据导出模块用于将问诊单导出为 CSV 文件。

计划实现的能力：

- 导出问诊单列表
- 设置正确的响应头
- 使用 OpenCSV 生成 CSV 内容
- 支持后台用户下载数据

对应接口规划：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/export/consultation` | 导出问诊单 CSV |

业务意义：

导出能力适合课程验收、后台统计和离线分析场景。它也体现了后端处理文件响应的能力。

### 4.8 AI 问答业务

AI 模块计划接入阿里云 DashScope 的 Qwen 模型，为用户提供中医养生相关问答。

计划实现的能力：

- 接收用户问题
- 调用 DashScope API
- 返回 AI 回答
- 当 API Key 未配置或调用失败时，返回 fallback 预设回答
- 标识回答模式，例如 `ai` 或 `fallback`

对应接口规划：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/ai/ask` | AI 问答 |

业务意义：

AI 问答是项目的高级功能。它让平台从传统 CRUD 系统扩展到智能问答场景，同时训练外部 API 调用、配置管理和降级处理能力。

## 5. 系统模块划分

项目计划按照以下包结构组织代码：

```text
com.tcm.platform
├─ ai          AI 服务集成
├─ common      通用响应和异常处理
├─ config      Spring、Security、MyBatis、Web 配置
├─ controller  HTTP 接口控制器
├─ dto         请求和响应数据对象
├─ entity      数据库实体类
├─ mapper      MyBatis-Plus 数据访问接口
├─ security    JWT、认证过滤器、安全上下文
└─ service     业务服务
```

### 5.1 Controller 层

Controller 层负责接收 HTTP 请求、读取参数、触发参数校验，并调用 Service 层处理业务。

计划中的 Controller：

| Controller | 说明 |
| --- | --- |
| `AuthController` | 登录、注册接口 |
| `PatientController` | 患者端接口 |
| `AdminController` | 后台管理接口 |
| `AIController` | AI 问答接口 |

### 5.2 Service 层

Service 层负责业务逻辑，是系统最核心的代码层。

计划中的 Service：

| Service | 说明 |
| --- | --- |
| `AuthService` | 登录、注册、密码校验、token 生成 |
| `ConsultationService` | 问诊单创建、查询、更新、统计 |
| `ReminderService` | 问诊提醒规则 |
| `KnowledgeArticleService` | 中医知识文章业务 |
| `RecipeService` | 药膳推荐业务 |
| `ExportService` | CSV 导出 |
| `DashScopeService` | AI 模型调用和 fallback |

### 5.3 Mapper 层

Mapper 层负责数据库访问。项目使用 MyBatis-Plus，基础 CRUD 由 `BaseMapper` 提供。

已有 Mapper：

| Mapper | 对应实体 | 对应表 |
| --- | --- | --- |
| `UserMapper` | `User` | `users` |
| `PatientAccountMapper` | `PatientAccount` | `patient_accounts` |
| `ConsultationMapper` | `Consultation` | `consultations` |
| `KnowledgeArticleMapper` | `KnowledgeArticle` | `knowledge_articles` |
| `RecipeMapper` | `Recipe` | `recipes` |
| `UploadMapper` | `Upload` | `uploads` |

## 6. 数据模型说明

系统当前设计了 6 张核心表。

| 表名 | 业务含义 | 重要字段 |
| --- | --- | --- |
| `users` | 医生和管理员账号 | 用户名、密码哈希、角色、显示名称、科室 |
| `patient_accounts` | 患者账号 | 用户名、密码哈希、昵称、手机号、头像 |
| `consultations` | 问诊单 | 患者、姓名、年龄、性别、症状、紧急度、状态、医生备注、跟进时间 |
| `knowledge_articles` | 中医知识文章 | 标题、分类、摘要、正文、小贴士、封面、发布状态、浏览量 |
| `recipes` | 药膳推荐 | 名称、季节、体质、适宜人群、简介、食材、步骤、发布状态 |
| `uploads` | 上传记录 | 原始文件名、存储文件名、文件类型、大小、访问地址、上传者 |

其中 `consultations` 是核心业务表。患者端提交的数据、医生端处理的数据、Dashboard 统计和导出功能都依赖这张表。

## 7. 权限设计

系统使用 JWT 进行无状态认证，使用 Spring Security 控制接口访问权限。

计划权限规则：

| URL | 访问规则 |
| --- | --- |
| `/api/auth/**` | 不需要登录 |
| `/api/patient/**` | 患者、医生、管理员可访问 |
| `/api/admin/**` | 医生、管理员可访问 |
| `/uploads/**` | 不需要登录 |
| `/swagger-ui/**`、`/v3/api-docs/**` | 不需要登录 |
| 其他接口 | 需要登录 |

注意：

Spring Security 中角色通常会自动带有 `ROLE_` 前缀。因此代码中配置 `hasAnyRole("PATIENT", "DOCTOR", "ADMIN")` 时，实际权限会对应 `ROLE_PATIENT`、`ROLE_DOCTOR`、`ROLE_ADMIN`。

## 8. 技术架构

项目使用典型的后端分层架构：

```text
客户端 / Postman / 前端页面
        |
        v
Controller 层
        |
        v
Service 层
        |
        v
Mapper 层
        |
        v
MySQL 数据库
```

核心技术职责：

| 技术 | 职责 |
| --- | --- |
| Spring Boot | 启动应用、自动配置、Web 服务 |
| Spring MVC | 提供 RESTful API |
| Spring Security | 登录认证、接口权限控制 |
| JWT | 保存登录状态，避免服务端 Session |
| MyBatis-Plus | 简化数据库 CRUD |
| MySQL | 保存业务数据 |
| Lombok | 简化实体和 DTO 代码 |
| OpenCSV | 生成 CSV 导出文件 |
| RestTemplate | 调用外部 AI API |
| Docker | 容器化运行 MySQL 和后端应用 |

## 9. 当前项目实现状态

根据当前代码目录，项目目前仍处于“任务一：项目初始化与基础框架”的初始状态。

### 9.1 已经具备的内容

当前已经有：

- 前后端一体仓库目录结构
- `backend/README.md` 后端任务说明
- `docs/PROJECT_GUIDE.md` 实战引导文档
- `backend/pom.xml` 骨架
- `backend/src/main/resources/application.yml` 骨架
- `backend/src/main/resources/schema.sql` 数据库脚本
- 启动类骨架
- `common`、`config`、`dto`、`entity`、`mapper` 等基础包
- 6 个 Entity 类文件骨架
- 6 个 Mapper 接口
- 6 个 DTO 类文件骨架

### 9.2 尚未完成的内容

当前还没有真正完成：

- `backend/pom.xml` 依赖配置
- `backend/src/main/resources/application.yml` 配置值
- Spring Boot 启动类逻辑
- `Result<T>` 统一响应实现
- 全局异常处理实现
- Spring Security 配置实现
- JWT 工具类和过滤器
- 6 个实体类字段
- DTO 参数校验注解
- Controller 层业务接口
- Service 层业务逻辑
- AI 服务集成
- Docker 部署文件
- 单元测试和接口测试

因此，当前项目还不是可运行的完整系统，而是一个教学用的后端项目骨架。

## 10. 项目最终业务流程

项目完成后，理想业务流程如下：

```text
患者注册账号
  -> 患者登录获得 JWT
  -> 患者提交问诊单
  -> 系统根据症状生成提醒
  -> 医生或管理员登录后台
  -> 后台查看问诊单列表
  -> 医生筛选紧急问诊
  -> 医生更新问诊状态和备注
  -> 后台查看 Dashboard 统计
  -> 后台导出问诊数据
  -> 患者浏览知识文章和药膳推荐
  -> 用户使用 AI 问答获取养生建议
```

## 11. 项目学习价值

这个项目适合用来训练以下能力：

- Java 后端项目结构理解
- Maven 依赖管理
- Spring Boot 配置和启动
- RESTful API 设计
- DTO、Entity、Mapper 的分工
- MyBatis-Plus 基础使用
- MySQL 表结构和实体映射
- JWT 登录认证
- Spring Security 权限控制
- 参数校验和统一异常处理
- 分页查询和动态筛选
- CSV 文件导出
- 外部 AI API 调用
- Docker 部署
- 接口测试和项目交付

## 12. 后续新增功能规划建议

在设计新增功能之前，建议先把新增功能分成 3 类：

| 类型 | 说明 | 示例 |
| --- | --- | --- |
| 核心扩展 | 会影响数据库、权限或主流程，需要提前设计 | 预约挂号、医生排班、病历档案、处方管理 |
| 中途扩展 | 依赖某个模块完成后可以加入 | 文件上传、文章收藏、药膳点赞、问诊搜索增强 |
| 后置扩展 | 主项目完成后再做更稳 | 在线聊天、消息通知、数据可视化大屏、多端前端页面 |

建议下一步先不要直接改 `PROJECT_GUIDE.md`，而是先生成一份 `FEATURE_BACKLOG.md`，列出想加入的新增业务，再判断哪些需要提前进入主路线，哪些可以作为 Day 22 之后的扩展任务。

## 13. 总结

中医问诊与养生平台当前定位是一个“中医问诊 + 健康内容 + 后台管理 + AI 问答”的综合后端项目。

它的主线业务是患者提交问诊单，医生或管理员处理问诊单；辅助业务是知识文章、药膳推荐、Dashboard 统计、CSV 导出和 AI 问答。

当前代码还是基础骨架，适合从 Day 1 开始一步一步实现。等你理解清楚原始业务后，再规划新增功能，会比一开始盲目扩展更稳。

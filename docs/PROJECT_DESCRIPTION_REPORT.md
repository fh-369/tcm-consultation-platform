# 知身问养项目完整说明报告

> 报告日期：2026 年 6 月 24 日

## 1. 项目概述

知身问养是一个面向中医问诊与日常养护场景的全栈 Web 平台。项目围绕“用户提交问诊、医生接诊处理、管理员运营调度、内容科普辅助、AI 养护问答”构建完整业务闭环。

平台不是单纯的 CRUD 示例，而是一个包含账号认证、角色权限、科室分诊、多医生工作台、内容管理、数据导出、AI 对话持久化和前端体验设计的综合实践项目。

## 2. 当前项目状态

截至 2026 年 6 月 24 日，项目已经完成：

- Spring Boot 后端基础与核心业务接口
- Vue 3 前端用户端、医生端、管理员端页面
- MySQL 数据建模与业务持久化
- JWT 登录认证与角色权限
- 多医生与科室分诊体系
- 问诊处理流程与医患沟通
- 养生文章与药膳推荐内容管理
- Dashboard 统计与 CSV 导出
- DashScope AI 问答与数据库持久化
- 自动化测试与生产构建验收

当前项目已经从早期教学骨架升级为一个可本地运行、可演示完整业务流程的全栈项目。

## 3. 技术架构

```text
浏览器 / 前端 Vue 应用
        |
        v
Axios / 路由守卫 / Pinia 状态
        |
        v
Spring Boot REST API
        |
        v
Service 业务层
        |
        v
MyBatis-Plus Mapper
        |
        v
MySQL 数据库
```

| 层 | 技术 |
| --- | --- |
| 前端 | Vue 3, Vite, Vue Router, Pinia, Element Plus, Axios |
| 后端 | Java 17, Spring Boot 3.2, Spring Security, MyBatis-Plus |
| 数据库 | MySQL |
| 认证 | JWT, BCrypt |
| AI | DashScope OpenAI 兼容接口 |
| 测试 | JUnit 5, Spring Boot Test, Vitest |
| 构建 | Maven, npm |

## 4. 仓库结构

```text
tcm-consultation-platform/
├─ backend/
│  ├─ pom.xml
│  ├─ README.md
│  └─ src/
│     ├─ main/
│     └─ test/
├─ frontend/
│  ├─ package.json
│  ├─ vite.config.js
│  ├─ README.md
│  └─ src/
├─ docs/
│  ├─ PROJECT_DESCRIPTION_REPORT.md
│  ├─ PROJECT_DEVELOPMENT_REPORT.md
│  └─ CONTENT_SOURCE_AND_PRODUCT_ROADMAP.md
├─ AGENTS.md
└─ README.md
```

说明：

- `backend/src/main` 是后端正式代码。
- `backend/src/test` 是后端自动化测试。
- `frontend/src` 是前端正式代码。
- `frontend/dist` 是构建产物，不提交到 Git。
- `backend/target` 是 Maven 构建产物，不提交到 Git。
- `uploads` 是运行期上传文件目录，不提交到 Git。

## 5. 用户角色

| 角色 | 说明 | 核心能力 |
| --- | --- | --- |
| 普通用户 | 面向大众使用者 | 注册登录、创建问诊、查看医生回复、浏览知识和药膳、使用 AI 问答 |
| 医生 | 平台审核通过的问诊处理者 | 查看科室问诊池、认领问诊、处理我的问诊、填写回复和更新状态 |
| 管理员 | 平台运营与调度角色 | 管理用户和医生、审核医生申请、调度问诊、维护内容、查看统计、导出数据 |

当前系统采用全局账号表 `accounts` 统一用户名唯一性，再通过 `users` 和 `patient_accounts` 承载医生/管理员与普通用户资料。

## 6. 核心业务模块

### 6.1 认证与账号

功能：

- 普通用户注册和登录
- 医生入驻申请
- 管理员和医生后台登录
- BCrypt 密码加密
- JWT 生成与校验
- 登录过期后自动清理前端状态
- 全局用户名唯一

关键接口：

- `POST /api/auth/register`
- `POST /api/auth/login/patient`
- `POST /api/auth/login/admin`
- `POST /api/auth/doctor/apply`
- `GET /api/auth/departments`

### 6.2 科室与医生准入

功能：

- 建立科室主数据
- 医生注册时选择负责科室
- 管理员审核医生入驻申请
- 医生审核通过后才能进入工作台
- 管理员查看和管理医生账号状态

当前科室：

- 综合咨询
- 中医内科
- 中医妇科
- 中医儿科
- 针灸推拿科

### 6.3 用户问诊

功能：

- 用户创建问诊单
- 选择问诊科室
- 填写姓名、年龄、性别、手机号、症状、持续时间、过敏史、备注和紧急程度
- 前后端校验姓名、手机号、性别和必填项
- 系统生成提醒信息
- 用户查看自己的问诊列表、处理进度和医生回复
- 医生未完成问诊前，用户可以补充回复

### 6.4 医生工作台

功能：

- 科室问诊池：展示本科室和综合咨询中可认领的问诊
- 我的问诊：展示已分配或已认领给当前医生的问诊
- 医生认领问诊
- 医生填写回复
- 医生更新问诊状态
- 医生查看患者补充信息

设计边界：

- 科室问诊池只用于筛选和认领。
- 问诊回复只在“我的问诊”中进行。
- 当前版本不实现复杂排班和排班日历。

### 6.5 管理员调度

功能：

- 查看全平台问诊
- 按科室、医生、状态、紧急度筛选
- 修改问诊科室
- 分配或重新分配医生
- 查看问诊处理状态
- 统一调度待接诊、接诊中和已完成问诊

### 6.6 养生知识

功能：

- 用户浏览已发布文章
- 按分类筛选
- 关键词搜索
- 文章详情 Markdown 渲染
- 浏览量统计
- 管理员新增、编辑、发布、下架文章
- 管理员维护文章封面和摘要

### 6.7 药膳推荐

功能：

- 用户浏览已发布药膳
- 按季节筛选
- 按体质筛选
- 关键词搜索
- 药膳详情展示食材、步骤、适宜人群和说明
- 浏览量统计
- 管理员新增、编辑、发布、下架药膳

### 6.8 数据统计与导出

功能：

- 平台数据概览
- 问诊趋势统计
- 状态与紧急程度统计
- 内容数量统计
- 按条件查询可导出问诊数量
- 导出 CSV

导出数据可能包含个人信息，真实部署时应增加操作审计和更严格的权限控制。

### 6.9 AI 养护问答

功能：

- DashScope OpenAI 兼容接口接入
- 模型名称通过环境变量配置
- 支持流式回答体验
- 支持多轮对话
- 支持选择问诊单作为上下文
- 对话和消息保存到数据库
- 删除对话时同步删除数据库记录
- 每个对话首次提问时生成站内延伸阅读推荐快照
- 延伸阅读默认折叠，用户需要时展开

当前 AI 回答定位为一般养护参考，不能替代医生诊断和治疗。

## 7. 数据模型

当前核心表：

| 表名 | 说明 |
| --- | --- |
| `accounts` | 全局登录账号，保证用户名唯一 |
| `users` | 管理员和医生资料 |
| `patient_accounts` | 普通用户资料 |
| `departments` | 科室主数据 |
| `consultations` | 问诊单 |
| `consultation_messages` | 医患沟通消息 |
| `consultation_progress_records` | 问诊处理记录 |
| `knowledge_articles` | 养生知识文章 |
| `recipes` | 药膳推荐 |
| `uploads` | 上传文件记录 |
| `ai_conversations` | AI 对话 |
| `ai_messages` | AI 对话消息 |
| `ai_conversation_recommendations` | AI 对话站内推荐快照 |

核心关系：

- `accounts` 与 `users` / `patient_accounts` 一对一。
- `consultations` 关联普通用户、科室和医生。
- `consultation_messages` 和 `consultation_progress_records` 关联问诊单。
- `ai_conversations` 关联普通用户，可选关联问诊单。
- `ai_messages` 和 `ai_conversation_recommendations` 关联 AI 对话。

## 8. 权限设计

系统使用 JWT 无状态认证。

主要访问规则：

| 范围 | 权限 |
| --- | --- |
| 认证接口 | 未登录可访问 |
| 普通用户端接口 | 普通用户登录后访问 |
| 医生工作台接口 | 医生或管理员访问，按业务进一步限制 |
| 管理员接口 | 管理员访问 |
| 内容浏览接口 | 普通用户、医生、管理员可访问 |
| 上传文件访问 | 按静态资源方式访问 |

前端通过路由守卫控制页面访问，后端通过 Spring Security 和业务层校验控制接口访问。

## 9. 本地演示数据

当前本地演示数据已清理测试用户和历史问诊，保留管理员、科室、内容数据，并创建 5 个医生账号。

| 用户名 | 医生姓名 | 科室 |
| --- | --- | --- |
| `doctor_general` | 林安和 | 综合咨询 |
| `doctor_internal` | 周明远 | 中医内科 |
| `doctor_gynecology` | 许清岚 | 中医妇科 |
| `doctor_pediatrics` | 陈知夏 | 中医儿科 |
| `doctor_tuina` | 何砚秋 | 针灸推拿科 |

本地演示初始密码：

```text
doctor123456
```

该密码仅用于本地演示，不应作为生产密码。

## 10. 运行与配置

### 10.1 后端环境变量

| 变量 | 说明 |
| --- | --- |
| `DB_USERNAME` | MySQL 用户名 |
| `DB_PASSWORD` | MySQL 密码 |
| `JWT_SECRET` | JWT 签名密钥 |
| `DASHSCOPE_API_KEY` | DashScope API Key |
| `DASHSCOPE_MODEL` | DashScope 模型名称 |

### 10.2 后端启动

```powershell
cd backend
mvn spring-boot:run
```

### 10.3 前端启动

```powershell
cd frontend
npm install
npm run dev
```

## 11. 测试与验收

当前第 8 阶段验收结果：

| 验收项 | 命令 | 结果 |
| --- | --- | --- |
| 后端全量测试 | `mvn test` | 129 个测试通过，0 失败 |
| 前端单元测试 | `npm test -- --run` | 21 个测试文件、81 个测试通过 |
| 前端生产构建 | `npm run build` | 构建成功 |
| Git 空白检查 | `git diff --check` | 通过 |

## 12. 轻量化部署建议

如果后续要创建轻量部署文件夹，只建议放运行所需内容：

```text
tcm-platform-release/
├─ backend/
│  ├─ tcm-platform-1.0.0.jar
│  └─ uploads/
├─ frontend/
│  └─ dist/
├─ database/
│  ├─ schema.sql
│  └─ migration/
└─ README_DEPLOY.md
```

部署包不应包含：

- `.git`
- `node_modules`
- `frontend/.vite`
- `backend/target` 中除 jar 之外的构建缓存
- 本地 `.env`
- 数据库备份
- 本地 IDE 配置
- 开发过程临时文件

## 13. 当前限制与后续方向

当前限制：

- Docker 和生产部署尚未正式实施。
- AI 联网搜索尚未实现。
- AI 推荐仍以关键词和站内内容匹配为主，后续可升级为全文检索或向量检索。
- 生产构建主 chunk 偏大，后续可做代码拆分。
- 内容来源体系仍需进一步规范。
- 当前演示医生使用统一临时密码，正式环境应增加密码修改或重置机制。

后续建议：

- 完成 PR 合并并同步本地 `main`。
- 重新注册普通用户，验证完整业务闭环。
- 建立内容来源字段和书目骨架。
- 设计生产部署方案。
- 增加数据备份、操作审计和安全配置。

## 14. 相关文档

- `docs/PROJECT_DEVELOPMENT_REPORT.md`：项目阶段开发报告
- `docs/CONTENT_SOURCE_AND_PRODUCT_ROADMAP.md`：内容来源体系与产品路线
- `docs/FRONTEND_PLAN.md`：前端规划记录
- `docs/中医问诊与养生平台_Git与GitHub双线实战指南.md`：Git 与 GitHub 实战指南

# 中医问诊与养生平台：Git 与 GitHub 双线实战指南

## 1. 这份指南的目标

这份指南将两条学习路线结合在一起：

1. **项目开发线**：一步一步完成中医问诊与养生平台。
2. **Git/GitHub 学习线**：在真实开发过程中学习版本管理、分支、工作树、远程同步和 Pull Request。

它不是一份要求你独立写完全部代码的考试说明，也不是脱离项目的 Git 命令手册。

实际学习方式是：

```text
读取当前项目步骤
-> 检查 Git 状态
-> 规划分支和提交
-> 你亲手创建文件、粘贴代码并运行验证
-> 查看修改差异
-> 选择使用 Codex 或 VS Code 完成 Git 操作
-> 根据阶段决定是否推送、创建 PR 或合并
-> 记录项目与 Git 学习进度
```

每次只推进一个小步骤。项目功能没有通过验证前，不进入提交和合并阶段。

---

## 2. 文件之间的职责

项目目录中的主要说明文件分别承担不同职责：

| 文件 | 用途 |
| --- | --- |
| `README.md` | 整个前后端仓库的入口说明 |
| `backend/README.md` | 查看当前后端基础框架任务和验收要求 |
| `docs/PROJECT_DESCRIPTION_REPORT.md` | 理解项目是什么、服务哪些用户、计划实现哪些业务 |
| `docs/FRONTEND_PLAN.md` | 查看前端技术栈、页面范围、路由、布局和前端介入时机 |
| `docs/PROJECT_GUIDE.md` | 查看原始 21 天项目开发路线 |
| `docs/结合项目实战引导的Git与GitHub伴学指南.md` | 查看完整 Git/GitHub 教学规则和安全边界 |
| `docs/中医问诊与养生平台_Git与GitHub双线实战指南.md` | 实际开发时使用的项目与 Git 双线主指南 |

开始实战后，优先使用本文件作为主路线。遇到需要了解业务背景时，阅读项目说明报告；需要核对原始任务时，阅读 `PROJECT_GUIDE.md` 和 `README.md`。

### 2.1 当前仓库结构

```text
tcm-consultation-platform/
├─ backend/
│  ├─ pom.xml
│  └─ src/
│     ├─ main/
│     └─ test/
├─ frontend/  Vue 前端项目预留目录，后端 Day 10 后正式初始化
├─ docs/
└─ README.md
```

Git 应在 `tcm-consultation-platform` 根目录初始化，从而统一管理后端、未来的前端和项目文档。

Maven 命令应在 `backend` 目录运行：

```powershell
cd backend
mvn compile
```

或者在仓库根目录指定 `pom.xml`：

```powershell
mvn -f backend/pom.xml compile
```

本指南后续出现的 `pom.xml` 和 `src/...` 默认指 `backend/pom.xml` 和 `backend/src/...`。`backend/src/test` 是后续测试代码目录，不应删除。

---

## 3. 我们的协作方式

### 3.1 代码开发方式

每个具体开发步骤中，AI 应：

1. 解释当前代码要解决的问题。
2. 告诉你要打开或创建哪个文件。
3. 给出完整、可复制的文件代码。
4. 解释关键类、注解和方法的职责。
5. 让你亲手创建文件或粘贴代码。
6. 给出验证命令或接口测试方法。
7. 等你反馈结果后再继续。

### 3.2 Git 操作方式

每当需要进行 Git 操作时，你需要选择一种方式：

```text
1. Codex：AI 先解释操作，再替你执行并展示结果
2. VS Code：AI 给出准确的界面操作路径，你亲手完成
```

同一个 Git 操作只使用一种方式，不重复执行。

### 3.3 每次只推进一个小步骤

即使某一天包含多个任务，也应拆开完成。例如 Day 1 应拆成：

```text
Day 1.0：检查环境和项目状态
Day 1.1：补全 pom.xml
Day 1.2：补全 application.yml
Day 1.3：完善启动类
Day 1.4：编译验证
Day 1.5：审查差异并创建第一次提交
Day 1.6：连接 GitHub 远程仓库
```

---

## 4. Git 安全规则

整个实战过程必须遵守以下规则：

1. 不批量删除文件或目录。
2. 删除文件时，只处理一个明确路径，并先确认。
3. 不使用 `del /s`、`rd /s`、`rmdir /s`、`Remove-Item -Recurse` 或 `rm -rf`。
4. 不默认使用 `git add .`，优先明确暂存具体文件。
5. 不自动执行 `git commit`、`git push`、`git merge`、分支删除或工作树清理。
6. 不在未解释风险时执行 `git reset --hard`、rebase、强制推送或历史重写。
7. 不覆盖或撤销与当前任务无关的修改。
8. 不直接删除工作树目录，应使用正确的 Git 工作树流程，并单独确认。
9. 重要 Git 操作前后都检查状态。
10. 出现冲突时，先解释冲突双方内容，再由你决定最终业务结果。

---

## 5. 每个步骤的标准执行循环

### 5.1 定位项目步骤

开始前，AI 应明确：

```text
当前阶段：
当前 Day：
当前小步骤：
本步功能目标：
本步需要修改的文件：
本步验收标准：
```

### 5.2 检查 Git 状态

如果项目已经初始化为 Git 仓库，开始修改前检查：

```powershell
git status
git branch --show-current
git log --oneline --graph --all --decorate -n 15
git worktree list
git remote -v
```

需要解释：

- 当前在哪个分支和工作树。
- 是否存在未提交修改。
- 是否适合继续使用当前分支。
- 是否需要创建新分支或工作树。
- 是否需要先同步远程变化。

### 5.3 制定本步计划

开始写代码前，AI 应说明：

```text
本步代码任务：
本步使用的分支：
预计产生的 commit：
是否需要 push：
是否需要 Pull Request：
完成后如何验收：
```

### 5.4 完成功能和验证

你完成文件创建或代码粘贴后，需要运行相应验证：

```powershell
mvn compile
mvn test
mvn spring-boot:run
```

接口任务还需要使用 Postman 或 Apifox。

### 5.5 提交前审查

功能验证通过后，再检查：

```powershell
git status
git diff
git diff --staged
```

需要确认：

- 哪些文件发生了变化。
- 哪些变化属于当前任务。
- 是否混入无关修改。
- 应该暂存哪些明确文件。

### 5.6 提交后检查

提交后检查：

```powershell
git status
git log --oneline --graph --all --decorate -n 15
```

然后决定：

- 继续在当前分支完成下一小步。
- 推送当前分支作为远程备份。
- 创建 Pull Request。
- 合并到 `main`。
- 等当前阶段全部完成后再合并。

---

## 6. 推荐分支策略

项目使用较简单的功能分支模式：

```text
main
├─ feature/auth-foundation
├─ feature/consultation
├─ feature/knowledge-articles
├─ feature/recipes
├─ feature/dashboard-export
├─ feature/ai-assistant
├─ test/core-services
├─ chore/docker-deployment
└─ fix/security-hardening
```

### 6.1 `main` 分支

`main` 保存已经通过阶段验收的稳定代码。

适合直接在 `main` 完成的内容：

- Git 仓库初始化。
- 第一次基础框架提交。
- 极小且明确的文档修正。

进入功能开发后，尽量使用独立分支。

### 6.2 功能分支

独立业务功能使用 `feature/` 前缀：

```text
feature/auth-foundation
feature/consultation
feature/knowledge-articles
feature/recipes
feature/dashboard-export
feature/ai-assistant
```

### 6.3 测试、维护和修复分支

```text
test/core-services
chore/docker-deployment
fix/security-hardening
fix/<具体错误名称>
```

### 6.4 不需要为每个小任务创建分支

例如 Day 3 到 Day 6 都属于认证基础，可以在同一个 `feature/auth-foundation` 分支中分阶段提交。

---

## 7. Git Worktree 使用策略

工作树让不同分支同时拥有独立目录。它不是分支替代品，也不是每个功能都必须使用的工具。

### 7.1 适合使用工作树的情况

- 当前功能做到一半，需要同时修复 `main` 上的紧急问题。
- 需要同时运行或比较两个分支。
- 两项相互独立的功能需要并行开发。
- 需要保留当前开发现场，同时切换到另一个任务。

### 7.2 本项目推荐的首次 Worktree 实战

Day 11 的知识文章管理和 Day 12 的药膳管理相对独立，适合作为工作树练习：

```text
主项目目录                 -> feature/knowledge-articles
另一个工作树目录           -> feature/recipes
```

使用前必须先确认：

- 两个功能是否真的需要并行。
- 当前修改是否已安全提交。
- 工作树目录应该放在哪里。
- 不同工作树是否会竞争同一个服务端口或数据库数据。

如果你选择按顺序完成 Day 11 和 Day 12，也可以完全不使用工作树。

---

## 8. GitHub 学习路线

GitHub 操作按项目进度逐步引入：

1. Day 1：创建远程仓库，推送第一次稳定提交。
2. Day 3-6：首次发布功能分支并创建 Pull Request。
3. Day 7：合并认证基础 PR，创建第一个版本标签。
4. Day 8-14：练习多个功能分支、远程同步和 PR 审查。
5. Day 15-19：练习独立功能 PR、修复分支和安全撤销。
6. Day 20-21：进行发布前检查，创建 `v1.0.0` 标签和 GitHub Release。

需要逐步理解：

| 概念 | 含义 |
| --- | --- |
| commit | 在本地保存一份代码快照 |
| push | 把本地提交发送到 GitHub |
| Pull Request | 请求审查并合并一个分支 |
| merge | 合并两条开发历史 |
| fetch | 获取远程信息，不直接修改当前文件 |
| pull | 获取远程信息并整合到当前分支 |
| tag | 为某个重要提交添加版本标记 |
| release | 在 GitHub 上发布一个可说明、可下载的版本 |

---

## 9. 项目步骤与 Git 学习点映射表

| Day | 项目功能目标 | 推荐分支 | 主要 Git 学习点 | Worktree | GitHub 节点 |
| --- | --- | --- | --- | --- | --- |
| Day 1 | 项目初始化 | `main` | 初始化仓库、状态、暂存区、第一次提交、`.gitignore` | 否 | 创建远程仓库并首次推送 |
| Day 2 | 实体类和数据库 | `main` 或 `feature/data-model` | 查看差异、明确文件暂存、合理拆分提交 | 否 | 推送稳定进度 |
| Day 3 | JWT 工具类 | `feature/auth-foundation` | 创建功能分支、切换分支 | 否 | 发布远程分支 |
| Day 4 | JWT Filter 和安全配置 | `feature/auth-foundation` | 在同一功能分支连续提交 | 否 | 更新远程分支 |
| Day 5 | 登录和注册 | `feature/auth-foundation` | 阅读提交历史、检查分支差异 | 否 | 准备首次 PR |
| Day 6 | 统一响应和异常处理 | `feature/auth-foundation` | 提交审查、分支验收 | 否 | 创建首次 PR |
| Day 7 | 第一周回归 | `main` | 合并、同步、阶段标签 | 否 | 合并 PR，创建 `v0.1.0` |
| Day 8 | 问诊业务 | `feature/consultation` | 新功能分支、提交规划 | 否 | 发布分支 |
| Day 9 | 提醒规则 | `feature/consultation` | 在同一业务分支分阶段提交 | 否 | 更新远程分支 |
| Day 10 | 患者端接口 | `feature/consultation` | 分支验收、PR 描述 | 否 | 创建问诊业务 PR |
| Frontend 1 | 前端初始化与双布局 | `feature/frontend-foundation` | 前端工程分支、Node 依赖与前后端仓库管理 | 否 | 发布前端基础分支 |
| Frontend 2 | 登录注册与权限路由 | `feature/frontend-auth` | 前端功能提交、token 变更审查 | 否 | 创建前端认证 PR |
| Frontend 3 | 患者端问诊流程 | `feature/frontend-patient` | 前后端联调提交、浏览器验证记录 | 否 | 更新患者端 PR |
| Frontend 4 | 后台问诊工作台 | `feature/frontend-admin-consultation` | 前端表格与接口联调、PR 验证说明 | 可选 | 创建后台问诊 PR |
| Day 11 | 知识文章 | `feature/knowledge-articles` | 独立功能分支 | 可选 | 发布分支 |
| Day 12 | 药膳管理 | `feature/recipes` | 工作树、并行分支 | 推荐练习 | 发布分支 |
| Day 13 | Dashboard 和导出 | `feature/dashboard-export` | 多分支状态、远程同步 | 可选 | 创建 PR |
| Day 14 | 第二周回归 | `main` | 合并多个 PR、同步本地、阶段标签 | 否 | 创建 `v0.2.0` |
| Day 15 | AI 问答 | `feature/ai-assistant` | 独立功能 PR | 否 | 创建 AI PR |
| Frontend 5 | 内容、药膳与 AI 页面 | `feature/frontend-content-ai` | 前端跟随后端接口增量开发 | 可选 | 创建内容与 AI PR |
| Day 16 | 接口系统测试 | 当前待验收分支或 `test/api` | 测试记录提交、发现错误后创建修复分支 | 可选 | 更新 PR 验证说明 |
| Day 17 | 单元测试 | `test/core-services` | 测试分支、修复提交、了解 `revert` | 否 | 创建测试 PR |
| Day 18 | Docker 部署 | `chore/docker-deployment` | 维护类分支、配置文件提交 | 否 | 创建部署 PR |
| Day 19 | 安全加固 | `fix/security-hardening` | 修复分支、安全撤销 | 可选 | 创建安全修复 PR |
| Day 20 | 集成测试 | `main` 或发布准备分支 | 发布前历史检查、远程同步 | 否 | 检查所有 PR 和 Actions |
| Day 21 | 项目交付 | `main` | 最终合并、tag、版本历史 | 否 | 创建 `v1.0.0` 和 Release |

---

## 10. Day 1：项目初始化与第一次 Git 提交

### 项目目标

- 补全 `pom.xml`
- 补全 `application.yml`
- 完善启动类
- 让 Maven 能识别并编译项目

### Git 学习目标

- 理解工作目录、暂存区和 commit
- 检查项目是否已经是 Git 仓库
- 创建 `.gitignore`
- 初始化 Git 仓库
- 查看修改并进行第一次明确提交
- 创建 GitHub 远程仓库并首次推送

### 推荐分支

```text
main
```

### 推荐小步骤

#### Day 1.0 检查环境

项目检查：

```powershell
java -version
mvn -version
```

Git 检查：

```powershell
git --version
git status
```

如果项目不是 Git 仓库，`git status` 会明确提示。不要在未检查目录位置时直接初始化。

#### Day 1.1 补全 `pom.xml`

验证：

```powershell
mvn dependency:tree
```

暂时不急着提交，先继续完成 Day 1 的基础配置。

#### Day 1.2 补全 `application.yml`

注意不要把真实密码、API Key 或其他秘密提交到 GitHub。配置应优先使用环境变量占位符。

#### Day 1.3 完善启动类

验证：

```powershell
mvn compile
```

#### Day 1.4 创建 `.gitignore`

应至少忽略：

```gitignore
/.superpowers/
target/
.idea/
*.iml
.vscode/
uploads/
.env
frontend/node_modules/
frontend/dist/
```

是否忽略 `.vscode/` 可根据你是否希望共享 VS Code 配置决定。

#### Day 1.5 第一次提交

提交前查看：

```powershell
git status
git diff
```

建议提交目的：

```text
Initialize Spring Boot project foundation
```

执行 Git 操作前，由你选择 `Codex` 或 `VS Code`。

#### Day 1.6 连接 GitHub

完成第一次稳定提交后，再创建 GitHub 空仓库并连接远程。

需要理解：

```text
本地仓库和 GitHub 远程仓库不是同一个东西。
commit 只保存在本地。
push 才会把提交发送到 GitHub。
```

### Day 1 验收

- `mvn compile` 通过，或能明确说明当前剩余编译阻塞。
- Git 工作目录状态清楚。
- 第一次 commit 内容明确。
- GitHub 远程地址配置正确。
- 能解释 commit 和 push 的区别。

---

## 11. Day 2：数据模型与明确文件暂存

### 项目目标

- 补全 6 个实体类
- 执行 `schema.sql`
- 验证 Mapper 与数据库连接

### Git 学习目标

- 使用 `git diff` 阅读代码变化
- 按明确文件暂存，而不是默认 `git add .`
- 根据真实开发进度拆分合理 commit

### 推荐分支

基础学习阶段可以继续使用：

```text
main
```

如果希望从此阶段开始严格练习分支，可以使用：

```text
feature/data-model
```

### 推荐提交拆分

```text
Map database tables to entity classes
Configure MyBatis Plus database support
Document database initialization verification
```

实际是否拆成多个提交，要根据当天真实修改内容决定。

### Day 2 验收

- 6 个实体类与数据库表字段一致。
- 数据库中存在 6 张表。
- Mapper 查询验证通过。
- 能解释为什么某些文件被放进同一个 commit。
- 能使用 `git diff --staged` 查看即将提交的内容。

---

## 12. Day 3-Day 7：认证基础功能分支

### 项目范围

- Day 3：JWT 工具类
- Day 4：JWT Filter、SecurityConfig、AppConfig、WebConfig
- Day 5：登录注册
- Day 6：统一响应与异常处理
- Day 7：认证全链路回归

### Git 学习目标

- 创建并使用第一个长期功能分支
- 在同一功能分支中创建多个有意义的 commit
- 推送远程功能分支
- 创建首次 Pull Request
- 合并 PR 后同步本地 `main`
- 创建第一个阶段标签

### 推荐分支

```text
feature/auth-foundation
```

### 推荐提交节点

```text
Add JWT token utility
Configure JWT authentication and endpoint security
Implement patient and admin authentication
Add unified API responses and exception handling
```

### Pull Request 验收内容

首次 PR 应写明：

```text
完成的项目步骤：
主要变化：
验证方式：
已知问题：
后续任务：
```

### Day 7 阶段节点

认证链路通过后：

1. 审查 PR。
2. 合并到 `main`。
3. 同步本地 `main`。
4. 检查工作目录是否干净。
5. 经你确认后创建：

```text
v0.1.0
```

---

## 13. Day 8-Day 10：问诊核心业务分支

### 项目范围

- Day 8：问诊业务 Service
- Day 9：提醒规则 Service
- Day 10：患者端接口

### Git 学习目标

- 为新核心业务创建功能分支
- 阅读分支图
- 理解功能分支与 `main` 的差异
- 为完整业务流程准备 PR

### 推荐分支

```text
feature/consultation
```

### 推荐提交节点

```text
Implement consultation lifecycle service
Add consultation reminder rules
Expose patient consultation endpoints
```

### 验收后思考

- 为什么这三个 Day 适合放在同一分支？
- 哪些提交可以独立解释和回退？
- 如果提醒规则出错，应该修复现有分支还是另建修复分支？

---

## 14. Frontend Phase 1-4：前端基础与问诊主流程

前端正式介入时间：

```text
后端 Day 10 完成后
```

此时后端应已经具备：

- 登录认证
- JWT 权限控制
- 统一响应格式
- 患者提交问诊
- 患者查看我的问诊
- 后台问诊查询与更新的基础能力

前端技术方案以 `docs/FRONTEND_PLAN.md` 为准。

### Frontend Phase 1：前端初始化与双布局

推荐分支：

```text
feature/frontend-foundation
```

项目目标：

- 初始化 Vue 3 + Vite 项目
- 使用 JavaScript
- 安装 Vue Router、Pinia、Axios、Element Plus
- 建立患者端顶部导航布局
- 建立后台端侧边栏布局
- 建立基础目录结构
- 配置 Axios 基础地址

Git 学习目标：

- 在同一仓库中管理前端和后端
- 理解 `frontend/node_modules` 和 `frontend/dist` 为什么不能提交
- 学习前端依赖文件的提交边界，例如 `package.json` 和 lock 文件

### Frontend Phase 2：登录注册与权限路由

推荐分支：

```text
feature/frontend-auth
```

项目目标：

- `/login/patient`
- `/login/admin`
- `/register`
- Pinia 保存 token、角色和用户信息
- Axios 请求自动携带 Authorization
- 路由守卫控制患者和后台页面访问

Git 学习目标：

- 审查 token 相关代码，避免把真实 token 或本地配置提交
- 学习前端功能分支如何关联后端接口状态

### Frontend Phase 3：患者端问诊流程

推荐分支：

```text
feature/frontend-patient
```

项目目标：

- 患者首页，问诊入口优先
- 提交问诊表单
- 我的问诊记录
- 知识文章和药膳入口占位或初版列表

Git 学习目标：

- 将一次前后端联调拆成清晰提交
- 在 PR 中记录验证过的后端接口

### Frontend Phase 4：后台问诊工作台

推荐分支：

```text
feature/frontend-admin-consultation
```

项目目标：

- 后台首页以待办问诊优先
- 问诊管理表格
- 状态、紧急度、关键字筛选
- 查看问诊详情
- 更新问诊状态和医生备注

Git 学习目标：

- 学习表格类页面如何审查差异
- 在 PR 描述中写清楚接口、筛选条件和验证数据

### 前端阶段结束条件

完成 Frontend Phase 1-4 后，应能演示：

```text
患者注册
-> 患者登录
-> 提交问诊
-> 查看我的问诊
-> 后台登录
-> 查看待办问诊
-> 更新问诊状态
```

该流程通过后，可以回到后端 Day 11-Day 15，继续完善知识文章、药膳、Dashboard、导出和 AI。前端 Phase 5 会跟随这些后端功能补页面。

---

## 15. Day 11-Day 12：独立功能与 Worktree

### 项目范围

- Day 11：知识文章管理
- Day 12：药膳管理

### 推荐分支

```text
feature/knowledge-articles
feature/recipes
```

### Git 学习目标

- 理解两个独立功能为什么适合使用不同分支
- 理解工作树与分支的区别
- 使用 `git worktree list` 查看多个开发现场
- 理解不同工作树不会自动共享未提交修改

### 是否使用 Worktree

开始 Day 11 前，由你选择：

```text
A. 顺序开发：完成知识文章并合并后，再开发药膳
B. Worktree 实战：分别在两个目录中保留两个功能分支
```

如果选择 B，创建工作树、选择目录、切换分支、运行服务和最终清理都必须逐步确认。

不得为了练习而强行并行。如果你此时对分支仍不熟悉，选择顺序开发更合适。

---

## 16. Day 13-Day 14：多分支整合与第二阶段标签

### 项目范围

- Dashboard 统计
- CSV 导出
- 第二周业务回归

### 推荐分支

```text
feature/dashboard-export
```

### Git 学习目标

- 检查多个功能分支和 PR 状态
- 理解 fetch 与 pull
- 合并多个已经通过验收的功能
- 检查是否存在冲突
- 创建第二阶段标签

### Day 14 阶段节点

第二周完整业务流程通过后，经确认创建：

```text
v0.2.0
```

标签前必须检查：

```powershell
git status
git log --oneline --graph --all --decorate -n 30
git remote -v
```

---

## 17. Day 15：AI 问答独立功能

### 项目目标

- 实现 DashScope API 调用
- 实现 fallback 回答
- 实现 AIController

### 推荐分支

```text
feature/ai-assistant
```

### Git 学习目标

- 使用独立功能分支隔离外部 API 集成
- 确保 API Key 不进入提交历史
- 编写包含风险和 fallback 说明的 PR

### 重点检查

提交前确认：

- 没有真实 API Key。
- 没有 `.env` 或本地秘密配置被暂存。
- fallback 在没有 API Key 时能够工作。

---

## 18. Frontend Phase 5：内容、药膳、Dashboard 与 AI 页面

当前端已完成登录和问诊主流程，后端继续完成 Day 11-Day 15 后，再进入 Frontend Phase 5。

推荐分支：

```text
feature/frontend-content-ai
```

项目目标：

- 知识文章列表
- 知识文章详情
- 后台知识文章管理
- 药膳推荐列表
- 药膳推荐详情
- 后台药膳管理
- 后台 Dashboard 展示
- AI 问答页面
- CSV 导出入口

Git 学习目标：

- 学习前端跟随后端接口增量开发
- 对内容管理、统计展示和 AI 页面分别形成清晰提交
- 在 PR 中记录哪些接口已联调，哪些仍依赖后端修复

阶段验收：

```text
游客可浏览知识文章和药膳详情
患者登录后可使用 AI 问答
后台可管理文章和药膳
后台可查看 Dashboard
后台可触发 CSV 导出
```

---

## 19. Day 16-Day 17：测试、修复和安全撤销

### 项目范围

- 接口系统测试
- 核心 Service 单元测试
- 修复测试中发现的问题

### 推荐分支

```text
test/core-services
fix/<具体错误名称>
```

### Git 学习目标

- 测试代码也需要明确提交
- 错误修复使用清晰的修复提交
- 理解 `git revert` 的用途
- 区分“撤销一个提交”和“删除未提交修改”

### 安全撤销原则

出现错误时先说明：

1. 错误发生在未提交修改、已提交内容，还是已经推送的内容。
2. 哪些代码仍然安全存在。
3. 可以使用哪些恢复方案。
4. 每种方案会改变什么。

不默认执行危险撤销命令。

---

## 20. Day 18-Day 19：部署与安全修复分支

### Day 18 推荐分支

```text
chore/docker-deployment
```

用于：

- `Dockerfile`
- `docker-compose.yml`
- 部署文档

### Day 19 推荐分支

```text
fix/security-hardening
```

用于：

- SQL 和查询安全检查
- 日志改进
- 异常处理修复
- 敏感信息检查

### Git 学习目标

- 理解 `feature`、`fix` 和 `chore` 分支表达的意图
- 为修复提交写清楚问题和结果
- 在合并前查看安全相关差异

---

## 21. Day 20-Day 21：发布准备与最终交付

### 项目目标

- 完成端到端集成测试
- 整理代码和文档
- 准备演示
- 完成最终交付

### Git/GitHub 学习目标

- 检查所有分支和 PR
- 确认 `main` 是稳定版本
- 查看最终提交历史
- 创建最终版本标签
- 创建 GitHub Release

### 发布前检查

```powershell
git status
git branch --all
git log --oneline --graph --all --decorate -n 50
git remote -v
git worktree list
```

检查：

- 是否还有未合并的功能分支。
- 是否还有未提交修改。
- 是否还有未处理的工作树。
- GitHub 是否与本地同步。
- 项目是否通过最终测试。
- 文档是否说明运行方式。

最终经确认创建：

```text
v1.0.0
```

GitHub Release 应包含：

- 项目功能摘要
- 运行环境
- 启动方式
- 主要接口或演示流程
- 已知限制

---

## 22. 提交消息规范

提交消息要说明真实完成的工作。

推荐使用英文动词开头：

```text
Initialize Spring Boot project foundation
Map database tables to entity classes
Add JWT token utility
Configure endpoint authorization rules
Implement patient registration and login
Add consultation reminder rules
Fix expired token validation
Document Docker deployment steps
```

避免：

```text
update
change
fix
完成
修改代码
```

一个好的提交应该能够回答：

```text
这个提交为什么存在？
它完成或修复了什么？
如果撤销它，会失去什么？
```

---

## 23. Pull Request 模板

每次创建 PR 时，可以使用：

```markdown
## 对应项目步骤

- Day：
- 功能：

## 主要变化

- 

## 验证方式

- [ ] `mvn compile`
- [ ] `mvn test`
- [ ] 接口手动验证

## 已知问题

- 

## 后续任务

- 
```

PR 是审查和讨论入口，不等同于 push，也不等同于 merge。

---

## 24. Codex 与 VS Code 操作选择

### 选择 Codex

可以这样说：

```text
请检查当前 Git 状态，并建议本步应该继续当前分支还是创建新分支。先不要执行。
```

```text
这次 Git 操作选择 Codex。请列出准备暂存的文件和建议提交消息，等我确认后再提交。
```

```text
请判断当前分支是否适合创建 Pull Request，并解释原因。先不要推送。
```

### 选择 VS Code

可以这样说：

```text
这次 Git 操作选择 VS Code。请告诉我如何查看并暂存当前步骤的文件。
```

AI 应给出点击路径、预期界面状态，并等待你完成。

常见入口：

| 操作 | VS Code 入口 |
| --- | --- |
| 查看修改 | 左侧“源代码管理” |
| 暂存文件 | 文件旁边的 `+` |
| 取消暂存 | “暂存的更改”中文件旁边的 `-` |
| 创建提交 | 输入提交消息并点击“提交” |
| 创建或切换分支 | 点击左下角分支名称 |
| 合并分支 | `Ctrl+Shift+P` -> `Git: Merge Branch...` |
| 发布分支 | 点击“发布分支” |
| 创建 PR | GitHub Pull Requests 扩展或 GitHub 网页 |

---

## 25. 冲突和错误处理

发生冲突、命令失败或 detached HEAD 时，不要急着执行更多命令。

先确认：

```text
发生了什么？
当前在哪个分支？
哪些修改已经提交？
哪些修改还没有提交？
冲突双方分别想保留什么业务行为？
```

合并冲突必须由你决定最终业务内容。AI 可以解释和推荐，但不能在未确认时替你选择一方。

---

## 26. 每步结束时的验收问题

每完成一个小步骤，需要能够回答：

1. 本步项目功能完成了什么？
2. 修改了哪些文件？
3. 使用什么方式验证了功能？
4. 当前在哪个分支和工作树？
5. 本步产生了哪些 commit？为什么这样拆分？
6. 当前分支与 `main` 有什么区别？
7. 修改是否已经 push？
8. 是否已经创建或合并 PR？
9. 如果现在切换分支或工作树，哪些文件会变化？
10. 下一步项目任务和 Git 学习点是什么？

---

## 27. 项目与 Git 学习记录模板

```markdown
## 当前项目进度

- 当前日期：
- 当前阶段：
- 当前 Day：
- 当前小步骤：
- 本步功能状态：未开始 / 进行中 / 已完成
- 本步验收结果：

## Git 状态

- 当前工作树：
- 当前分支：
- 最近提交：
- 未提交修改：
- 已暂存修改：
- 远程同步状态：
- 当前 Pull Request：

## 学习进度

- 本步掌握的项目知识：
- 本步掌握的 Git/GitHub 概念：
- 仍需复习：
- 遇到的问题：
- 解决方式：

## 下一步

- 项目任务：
- 建议 Git 操作：
- Git 操作方式：Codex / VS Code / 尚未选择
```

可以经你同意后，将进度记录写入单独的 `LEARNING_PROGRESS.md`。不应在未确认时自动创建提交或更新进度文件。

---

## 28. 在新对话中开始或继续实战

你可以随时在这个项目目录下新开一个 Codex 对话。通常不需要复制旧对话的全部内容，因为主要约定已经写入项目文件。

新对话应直接打开仓库根目录：

```text
D:\Desktop\tcm-consultation-platform
```

不要只打开 `backend`，否则新对话不容易同时读取 `docs`，也无法在未来统一管理前端。

### 26.1 第一次开始实战时

在新对话中发送：

```text
请完整阅读以下文件：

1. docs/中医问诊与养生平台_Git与GitHub双线实战指南.md
2. docs/PROJECT_DESCRIPTION_REPORT.md
3. docs/FRONTEND_PLAN.md
4. README.md
5. backend/README.md

然后检查当前项目代码和 Git 状态。
不要立即修改代码，也不要执行提交、合并或推送。

请告诉我：
1. 当前项目进行到哪个步骤；
2. 当前 Git 仓库、分支、工作树和远程状态；
3. 建议从哪个小步骤开始；
4. 本步项目任务和 Git 学习目标。

等我确认后，再一次只带我完成一个小步骤。
每次需要 Git 操作时，让我选择 Codex 或 VS Code。
```

如果希望 AI 同时核对原始路线和完整 Git 教学规则，可以再要求阅读：

```text
docs/PROJECT_GUIDE.md
docs/结合项目实战引导的Git与GitHub伴学指南.md
```

### 26.2 继续已有进度时

如果已经开始开发，建议新对话同时阅读：

```text
docs/中医问诊与养生平台_Git与GitHub双线实战指南.md
LEARNING_PROGRESS.md（如果已经创建）
```

然后检查代码和 Git 状态。Git 提交历史、当前分支、未提交修改和远程状态会提供大部分实际进度信息。

### 26.3 什么旧对话信息需要手动带过去

通常不需要复制整个旧对话。

只有以下内容尚未写入文件或 Git 历史时，才需要手动说明：

- 你刚刚决定但尚未记录的需求。
- 正在讨论、尚未执行的方案。
- 未解决的报错和已经尝试过的解决方法。
- 你希望使用 Codex 还是 VS Code 完成下一次 Git 操作。
- 当前未提交修改的意图。

可以使用简短交接格式：

```text
当前做到：
刚完成：
尚未完成：
当前问题：
已经尝试：
下一步希望：
Git 操作偏好：
```

### 26.4 为什么仍然需要检查项目和 Git 状态

Markdown 指南说明“应该怎么做”，但实际代码和 Git 状态说明“已经做到了哪里”。

新对话不能只读指南后猜测进度，必须检查：

- 当前文件内容
- `git status`
- 当前分支
- 提交历史
- 工作树
- 远程仓库

---

## 29. 最终验收标准

完成整个项目后，你应同时具备项目开发和版本管理能力。

### 项目能力

- 能解释项目分层和核心业务流程。
- 能运行、测试和演示系统。
- 能理解认证、问诊、文章、药膳、统计、AI 和部署模块。
- 能根据报错定位依赖、配置、数据库或代码问题。

### Git/GitHub 能力

- 能判断新任务是否需要功能分支。
- 能判断什么时候真正需要工作树。
- 能查看并解释工作目录、暂存区和提交历史。
- 能将一个功能拆成合理 commit。
- 能区分 commit、push、PR、merge、fetch 和 pull。
- 能创建、审查和合并 Pull Request。
- 能处理或协助处理合并冲突。
- 能识别危险撤销操作并选择安全方案。
- 能使用 tag 和 GitHub Release 发布项目版本。

最终工作流程应接近：

```text
读取下一项目步骤
-> 检查项目和 Git 状态
-> 决定分支与工作树策略
-> 选择 Codex 或 VS Code
-> 完成一个小型代码任务
-> 验证功能
-> 查看差异
-> 明确暂存并提交
-> 根据阶段决定 push、PR 或 merge
-> 更新项目和学习进度
-> 继续下一小步骤
```

---

## 30. 当前建议的第一步

新开一个实战对话，使用第 26.1 节的启动提示词。

让 AI 先完整阅读指南、检查项目代码和 Git 状态，并给出 Day 1.0 的执行计划。确认计划后，再开始第一个实际操作。

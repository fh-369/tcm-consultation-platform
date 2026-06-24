# 科室与医生准入基础 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 建立科室主数据、医生公开申请、管理员审核和医生资料编辑能力，同时保持普通注册与现有医生账号兼容。

**Architecture:** 使用 `departments` 表作为科室唯一来源，在现有 `accounts + users` 模型上增加医生审核字段。医生申请预创建禁用账号，管理员审核事务同时更新医生审核状态与账号启用状态；前端使用独立申请页和增强后的医生管理页。

**Tech Stack:** Java 17、Spring Boot 3.2、Spring Security、MyBatis-Plus、MySQL、Vue 3、Element Plus、Vitest、JUnit 5、Mockito。

---

### Task 1: 数据模型与迁移

**Files:**
- Create: `backend/src/main/resources/migration/department_doctor_admission.sql`
- Modify: `backend/src/main/resources/schema.sql`
- Create: `backend/src/main/java/com/tcm/platform/entity/Department.java`
- Create: `backend/src/main/java/com/tcm/platform/mapper/DepartmentMapper.java`
- Modify: `backend/src/main/java/com/tcm/platform/entity/User.java`

- [ ] 写入可重复执行的数据库迁移，创建科室表、扩展医生审核字段、插入首批科室，并把现有医生迁移为已审核。
- [ ] 同步更新完整初始化脚本 `schema.sql`。
- [ ] 增加 `Department` 实体和 Mapper。
- [ ] 扩展 `User` 实体字段。
- [ ] 使用 MySQL 实际执行迁移并检查表结构和现有医生状态。

### Task 2: 科室查询与医生申请后端

**Files:**
- Create: `backend/src/main/java/com/tcm/platform/dto/DepartmentResponse.java`
- Create: `backend/src/main/java/com/tcm/platform/dto/DoctorApplicationRequest.java`
- Create: `backend/src/main/java/com/tcm/platform/dto/DoctorApplicationResponse.java`
- Modify: `backend/src/main/java/com/tcm/platform/controller/AuthController.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/AuthService.java`
- Test: `backend/src/test/java/com/tcm/platform/service/AuthServiceTest.java`

- [ ] 先添加科室有效性、医生申请成功、用户名重复和无效科室的失败测试。
- [ ] 运行 `AuthServiceTest`，确认新增测试因缺少实现而失败。
- [ ] 实现启用科室查询。
- [ ] 实现医生申请事务：创建禁用账号和 `PENDING` 医生资料，不签发 JWT。
- [ ] 实现待审核、驳回、停用三类医生登录提示。
- [ ] 运行 `AuthServiceTest`，确认全部通过。

### Task 3: 管理员审核与资料编辑后端

**Files:**
- Create: `backend/src/main/java/com/tcm/platform/dto/DoctorReviewRequest.java`
- Create: `backend/src/main/java/com/tcm/platform/dto/DoctorProfileUpdateRequest.java`
- Modify: `backend/src/main/java/com/tcm/platform/dto/PersonnelRecord.java`
- Modify: `backend/src/main/java/com/tcm/platform/mapper/AccountMapper.java`
- Modify: `backend/src/main/java/com/tcm/platform/controller/PersonnelController.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/PersonnelService.java`
- Test: `backend/src/test/java/com/tcm/platform/service/PersonnelServiceTest.java`

- [ ] 先添加审核通过、审核驳回、禁止绕过审核启用账号、资料编辑和状态筛选测试。
- [ ] 运行 `PersonnelServiceTest`，确认新增测试失败。
- [ ] 扩展医生列表查询与审核状态筛选。
- [ ] 实现审核事务，记录审核管理员、备注和时间。
- [ ] 实现医生资料编辑并校验科室有效。
- [ ] 限制未审核医生不能通过普通状态接口启用。
- [ ] 运行 `PersonnelServiceTest`，确认全部通过。

### Task 4: 前端 API 与纯逻辑测试

**Files:**
- Modify: `frontend/src/api/auth.js`
- Modify: `frontend/src/api/personnel.js`
- Create: `frontend/src/api/auth.test.js`
- Modify: `frontend/src/api/personnel.test.js`
- Create: `frontend/src/features/admin/doctorAdmission.js`
- Create: `frontend/src/features/admin/doctorAdmission.test.js`

- [ ] 先添加公开科室、医生申请、审核、资料编辑 API 测试。
- [ ] 添加医生审核状态文案和可执行操作的纯函数测试。
- [ ] 运行对应 Vitest，确认新增测试失败。
- [ ] 实现 API 方法与审核状态展示逻辑。
- [ ] 再次运行对应 Vitest，确认全部通过。

### Task 5: 医生申请页面

**Files:**
- Create: `frontend/src/views/auth/DoctorApplicationView.vue`
- Modify: `frontend/src/views/auth/AuthView.vue`
- Modify: `frontend/src/router/index.js`

- [ ] 新增公开路由 `/doctor/apply`。
- [ ] 在注册区域增加“申请成为医生”入口。
- [ ] 实现分区表单、科室加载、字段校验和提交成功状态。
- [ ] 确保普通用户注册流程和现有滑动登录注册交互不变。
- [ ] 运行前端测试和构建。

### Task 6: 管理员医生审核页面

**Files:**
- Modify: `frontend/src/views/admin/PersonnelManagementView.vue`

- [ ] 在医生模式增加审核状态筛选。
- [ ] 展示科室、联系方式、审核状态与账号状态。
- [ ] 实现审核弹窗和通过、驳回操作。
- [ ] 实现医生资料编辑弹窗。
- [ ] 保持用户管理页面原有布局和行为不变。
- [ ] 运行前端测试和构建。

### Task 7: 系统级验证

**Files:**
- Verify only.

- [ ] 执行后端完整测试：`mvn test`。
- [ ] 执行后端打包：`mvn package -DskipTests`。
- [ ] 执行前端完整测试：`npm test`。
- [ ] 执行前端构建：`npm run build`。
- [ ] 启动后端并验证普通注册、医生申请、待审核登录、管理员审核、医生登录和资料编辑。
- [ ] 由用户在页面完成视觉与业务验收。
- [ ] 验收通过后再提供 VS Code 暂存范围和推荐提交信息，不自动执行 Git 写操作。

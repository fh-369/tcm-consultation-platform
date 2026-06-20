# 问诊科室分诊 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 让每张问诊单关联一个有效科室，并支持患者选择、各端展示和管理员调整科室。

**Architecture:** 在 `consultations` 中保存 `department_id` 外键，通过工作区服务批量关联科室名称。创建问诊由后端校验启用科室；管理员使用独立接口修改未完成问诊的科室，且不改变医生分配和问诊状态。

**Tech Stack:** Java 17、Spring Boot 3.2、Spring Security、MyBatis-Plus、MySQL、Vue 3、Element Plus、JUnit 5、Mockito、Vitest。

---

### Task 1: 数据模型与迁移

**Files:**
- Create: `backend/src/main/resources/migration/consultation_department_triage.sql`
- Modify: `backend/src/main/resources/schema.sql`
- Modify: `backend/src/main/java/com/tcm/platform/entity/Consultation.java`
- Modify: `backend/src/main/java/com/tcm/platform/dto/ConsultationRequest.java`
- Create: `backend/src/main/java/com/tcm/platform/dto/ConsultationDepartmentUpdateRequest.java`
- Modify: `backend/src/main/java/com/tcm/platform/dto/ConsultationWorkspaceRecord.java`

- [ ] 创建迁移脚本，给问诊表增加科室外键。
- [ ] 将历史问诊迁移到 `general` 科室，再设置字段非空。
- [ ] 同步更新完整初始化脚本。
- [ ] 扩展实体与请求、响应 DTO。

### Task 2: 患者创建和查询后端

**Files:**
- Modify: `backend/src/main/java/com/tcm/platform/service/ConsultationService.java`
- Modify: `backend/src/main/java/com/tcm/platform/controller/PatientController.java`
- Test: `backend/src/test/java/com/tcm/platform/service/ConsultationServiceTest.java`

- [ ] 先添加有效科室创建成功、缺少科室和停用科室失败测试。
- [ ] 运行 `ConsultationServiceTest`，确认新增测试因缺少实现而失败。
- [ ] 注入 `DepartmentMapper` 并校验科室。
- [ ] 创建问诊时保存 `departmentId`。
- [ ] 患者问诊列表返回科室名称。
- [ ] 重跑测试并确认通过。

### Task 3: 后台筛选和修改科室

**Files:**
- Modify: `backend/src/main/java/com/tcm/platform/service/ConsultationWorkspaceService.java`
- Modify: `backend/src/main/java/com/tcm/platform/controller/AdminController.java`
- Test: `backend/src/test/java/com/tcm/platform/service/ConsultationWorkspaceServiceTest.java`
- Test: `backend/src/test/java/com/tcm/platform/controller/ApiSystemTest.java`

- [ ] 先添加管理员按科室筛选测试。
- [ ] 添加管理员修改未完成问诊科室测试。
- [ ] 添加已完成问诊不能修改和修改后分配状态不变测试。
- [ ] 运行目标测试并确认失败。
- [ ] 实现科室关联名称、管理员筛选和修改接口。
- [ ] 限制修改接口仅管理员访问。
- [ ] 重跑目标测试并确认通过。

### Task 4: 前端 API 与展示逻辑

**Files:**
- Modify: `frontend/src/api/adminConsultation.js`
- Modify: `frontend/src/api/adminConsultation.test.js`
- Modify: `frontend/src/features/consultation/display.js`
- Modify: `frontend/src/features/consultation/display.test.js`

- [ ] 先添加管理员修改科室 API 测试。
- [ ] 添加科室展示与跨科室判断测试。
- [ ] 运行目标 Vitest 并确认失败。
- [ ] 实现 API 与纯展示逻辑。
- [ ] 重跑目标测试并确认通过。

### Task 5: 患者问诊页面

**Files:**
- Modify: `frontend/src/views/patient/ConsultationFormView.vue`
- Modify: `frontend/src/views/patient/MyConsultationsView.vue`

- [ ] 在线问诊加载启用科室。
- [ ] 增加独立“问诊方向”卡片和必填校验。
- [ ] 提交请求增加 `departmentId`。
- [ ] 我的问诊卡片增加科室名称。
- [ ] 保持现有问诊表单卡片设计和提交交互。

### Task 6: 后台问诊页面

**Files:**
- Modify: `frontend/src/views/admin/ConsultationManagementView.vue`

- [ ] 管理员和医生列表增加问诊科室列。
- [ ] 管理员增加科室筛选。
- [ ] 详情展示问诊科室。
- [ ] 管理员可调整未完成问诊科室。
- [ ] 跨科室处理时显示提示，不阻止现有分配。
- [ ] 医生端只读展示，不提供修改。

### Task 7: 数据库与系统验证

**Files:**
- Verify only.

- [ ] 执行迁移并确认全部历史问诊属于“综合咨询”。
- [ ] 执行后端完整测试。
- [ ] 执行后端生产打包。
- [ ] 执行前端完整测试。
- [ ] 执行前端生产构建。
- [ ] 通过真实接口验证创建、查询、筛选、修改和禁止修改已完成问诊。
- [ ] 用户完成页面视觉与业务验收后再进入 Git 操作。

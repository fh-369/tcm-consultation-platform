# Admin Scheduling Upgrade Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Enforce department-safe consultation assignment and reset assignment when an administrator transfers a consultation.

**Architecture:** Keep the existing administration controller and scheduling page. Put authoritative validation in `ConsultationWorkspaceService`; the Vue page only narrows choices and explains destructive effects before calling the existing APIs.

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, JUnit 5, Vue 3, Element Plus, Vitest.

---

### Task 1: Backend assignment rules

**Files:**
- Modify: `backend/src/test/java/com/tcm/platform/service/ConsultationWorkspaceServiceTest.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/ConsultationWorkspaceService.java`

- [ ] Add failing tests for same-department assignment, general consultation assignment, rejected or disabled doctors, and transfer reset.
- [ ] Run `mvn -Dtest=ConsultationWorkspaceServiceTest test` and confirm the new assertions fail.
- [ ] Validate doctor approval, account status, and department compatibility in `assign`.
- [ ] Clear `doctorId` and restore `待接诊` when `updateDepartment` changes the department.
- [ ] Re-run the focused service test.

### Task 2: Scheduling page interaction

**Files:**
- Modify: `frontend/src/views/admin/ConsultationManagementView.vue`
- Create: `frontend/src/features/admin/scheduling.js`
- Create: `frontend/src/features/admin/scheduling.test.js`

- [ ] Add failing tests for eligible doctor filtering.
- [ ] Implement a pure helper that returns approved and enabled doctors from the selected department, or all eligible doctors for 综合咨询.
- [ ] Use the helper in the assignment selector.
- [ ] Add a transfer confirmation explaining that assignment will be cleared.
- [ ] Clear the selected doctor and refresh record state after transfer.

### Task 3: Verification

**Files:**
- Test: all backend and frontend tests.

- [ ] Run the complete backend test suite.
- [ ] Run the complete frontend test suite.
- [ ] Run the frontend production build.
- [ ] Restart the backend and verify same-department assignment, cross-department rejection, and transfer reset with local data.
- [ ] Present VS Code commit guidance after user page acceptance.


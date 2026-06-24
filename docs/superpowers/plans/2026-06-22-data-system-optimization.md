# Data and System Optimization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver role-scoped dashboards, filterable consultation exports, hardened permissions, and consistent administrator feedback.

**Architecture:** Dashboard endpoints infer the current role and resolve a doctor identity before querying scoped mapper methods. Export uses a validated filter object shared by count and CSV queries, while the frontend keeps filtering and download state in the export page.

**Tech Stack:** Spring Boot 3.2, Spring Security, MyBatis-Plus, OpenCSV, Vue 3, Element Plus, Vitest.

---

### Task 1: Role-scoped dashboard contracts

**Files:**
- Modify: `backend/src/main/java/com/tcm/platform/dto/DashboardSummary.java`
- Modify: `backend/src/main/java/com/tcm/platform/mapper/ConsultationMapper.java`
- Modify: `backend/src/main/java/com/tcm/platform/mapper/AccountMapper.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/DashboardService.java`
- Modify: `backend/src/main/java/com/tcm/platform/controller/DashboardController.java`
- Test: `backend/src/test/java/com/tcm/platform/service/DashboardServiceTest.java`
- Test: `backend/src/test/java/com/tcm/platform/controller/ApiSystemTest.java`

- [ ] Add failing tests proving administrators receive platform metrics and doctors receive only their own consultation data.
- [ ] Add scoped status, urgency, trend, department distribution, and doctor workload queries.
- [ ] Add administrator content and personnel metrics.
- [ ] Resolve the authenticated doctor before calling doctor-scoped service methods.
- [ ] Run focused dashboard and controller tests.

### Task 2: Filtered consultation export

**Files:**
- Create: `backend/src/main/java/com/tcm/platform/dto/ConsultationExportFilter.java`
- Create: `backend/src/main/java/com/tcm/platform/dto/ConsultationExportRecord.java`
- Modify: `backend/src/main/java/com/tcm/platform/mapper/ConsultationMapper.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/ConsultationExportService.java`
- Modify: `backend/src/main/java/com/tcm/platform/controller/DashboardController.java`
- Test: `backend/src/test/java/com/tcm/platform/service/ConsultationExportServiceTest.java`
- Test: `backend/src/test/java/com/tcm/platform/controller/ApiSystemTest.java`

- [ ] Add failing tests for combined filters, extended CSV columns, date validation, and zero records.
- [ ] Implement shared count and export queries using the validated filter.
- [ ] Add `/api/admin/export/consultations/count`.
- [ ] Return a descriptive dated filename for CSV downloads.
- [ ] Run focused export and controller tests.

### Task 3: Frontend dashboard and export experience

**Files:**
- Modify: `frontend/src/api/content.js`
- Modify: `frontend/src/api/content.test.js`
- Create: `frontend/src/features/feedback.js`
- Create: `frontend/src/features/feedback.test.js`
- Modify: `frontend/src/views/admin/DashboardView.vue`
- Modify: `frontend/src/views/admin/ExportView.vue`

- [ ] Add failing API and feedback helper tests.
- [ ] Normalize role-scoped dashboard fields and add filtered export API calls.
- [ ] Present administrator operation metrics, department distribution, and doctor workloads.
- [ ] Present doctor-only personal metrics and trends.
- [ ] Add export filters, match count, loading, retry, empty, and download states.
- [ ] Use consistent business error extraction.

### Task 4: Permission and regression verification

**Files:**
- Modify: `backend/src/test/java/com/tcm/platform/controller/ApiSystemTest.java`
- Modify only implementation files required by verified failures.

- [ ] Verify anonymous, patient, doctor, and administrator access to dashboard and export endpoints.
- [ ] Run the full backend test suite.
- [ ] Run the full frontend test suite.
- [ ] Run the frontend production build.
- [ ] Run `git diff --check` and inspect the final worktree.

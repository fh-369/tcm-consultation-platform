# Consultation Communication Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add compact patient consultation summaries and secure, expandable doctor-patient communication before a consultation is completed.

**Architecture:** Store conversation messages in a dedicated table while preserving the existing progress timeline for workflow state changes. Consultation list queries return only message summaries; full message history is loaded through role-specific endpoints after ownership checks.

**Tech Stack:** Spring Boot 3.2, Spring Security, MyBatis-Plus, MySQL, Vue 3, Element Plus, Vitest, JUnit 5, Mockito.

---

### Task 1: Message Domain and Migration

**Files:**
- Create: `backend/src/main/java/com/tcm/platform/entity/ConsultationMessage.java`
- Create: `backend/src/main/java/com/tcm/platform/dto/ConsultationMessageRequest.java`
- Create: `backend/src/main/java/com/tcm/platform/mapper/ConsultationMessageMapper.java`
- Modify: `backend/src/main/resources/schema.sql`
- Create: `backend/src/main/resources/migration/consultation_messages.sql`

- [ ] Write mapper/service-facing tests that require chronological message reads and validated inserts.
- [ ] Run the focused backend tests and confirm they fail because the message classes do not exist.
- [ ] Add the entity, validated request DTO, mapper SQL, schema table, and migration backfill.
- [ ] Re-run the focused tests.

### Task 2: Secure Communication Service and APIs

**Files:**
- Create: `backend/src/main/java/com/tcm/platform/service/ConsultationMessageService.java`
- Modify: `backend/src/main/java/com/tcm/platform/controller/PatientController.java`
- Modify: `backend/src/main/java/com/tcm/platform/controller/DoctorConsultationController.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/ConsultationWorkspaceService.java`
- Test: `backend/src/test/java/com/tcm/platform/service/ConsultationMessageServiceTest.java`
- Test: `backend/src/test/java/com/tcm/platform/service/ConsultationWorkspaceServiceTest.java`
- Test: `backend/src/test/java/com/tcm/platform/controller/ApiSystemTest.java`

- [ ] Add failing tests for patient ownership, doctor assignment, active-status sending, completed-status rejection, blank content, and cross-role endpoint access.
- [ ] Run focused tests and confirm expected failures.
- [ ] Implement role-specific reads and writes.
- [ ] Make final doctor replies create both a progress record and a conversation message in one transaction.
- [ ] Run focused tests until green.

### Task 3: Lightweight Consultation Summaries

**Files:**
- Modify: `backend/src/main/java/com/tcm/platform/entity/Consultation.java`
- Modify: `backend/src/main/java/com/tcm/platform/dto/ConsultationWorkspaceRecord.java`
- Modify: `backend/src/main/java/com/tcm/platform/mapper/ConsultationMessageMapper.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/ConsultationService.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/ConsultationWorkspaceService.java`
- Test: `backend/src/test/java/com/tcm/platform/service/ConsultationServiceTest.java`
- Test: `backend/src/test/java/com/tcm/platform/service/ConsultationWorkspaceServiceTest.java`

- [ ] Add failing tests for message count and latest-message summary attachment.
- [ ] Run focused tests and verify the missing summary behavior.
- [ ] Implement one batched summary query for each consultation page.
- [ ] Re-run focused tests.

### Task 4: Patient Expandable Communication

**Files:**
- Modify: `frontend/src/api/consultation.js`
- Modify: `frontend/src/views/patient/MyConsultationsView.vue`
- Create: `frontend/src/features/consultation/communication.js`
- Create: `frontend/src/features/consultation/communication.test.js`

- [ ] Add failing tests for reply eligibility and latest-message summary formatting.
- [ ] Run the focused Vitest file and confirm failures.
- [ ] Add message API calls and communication helpers.
- [ ] Replace the always-visible full timeline with a compact summary and one-card-at-a-time expansion.
- [ ] Add loading, retry, empty, sending, active and completed states.
- [ ] Run focused tests and the frontend build.

### Task 5: Doctor Communication Panel

**Files:**
- Modify: `frontend/src/api/doctorConsultation.js`
- Modify: `frontend/src/views/admin/DoctorMyConsultationsView.vue`
- Modify: `frontend/src/features/consultation/communication.js`
- Test: `frontend/src/features/consultation/communication.test.js`

- [ ] Add failing tests for doctor send eligibility and message display metadata.
- [ ] Run the focused test and confirm failure.
- [ ] Load messages when the doctor opens the drawer.
- [ ] Separate progress timeline from doctor-patient messages.
- [ ] Send ordinary replies through the message endpoint and retain final reply behavior on completion.
- [ ] Run focused tests and the frontend build.

### Task 6: Full Verification

**Files:**
- Verify all changed backend, frontend, migration, and documentation files.

- [ ] Run `mvn test` from `backend`.
- [ ] Run `npm test` from `frontend`.
- [ ] Run `npm run build` from `frontend`.
- [ ] Run `git diff --check`.
- [ ] Inspect the final worktree and provide manual page acceptance steps.
- [ ] After user page verification, provide the VS Code staging scope and recommended English commit message; do not commit or push automatically.


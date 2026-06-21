# Consultation Progress History Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Preserve every doctor consultation update and expose the resulting timeline to doctors and patients.

**Architecture:** Add an append-only progress record table and service mapper. Keep the existing consultation columns as the latest snapshot, and update the snapshot plus history record in one transaction.

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, MySQL 9.6, Vue 3, Element Plus, JUnit 5, Vitest.

---

### Task 1: Progress record persistence

**Files:**
- Create: `backend/src/main/java/com/tcm/platform/entity/ConsultationProgressRecord.java`
- Modify: `backend/src/main/java/com/tcm/platform/mapper/ConsultationMapper.java`
- Modify: `backend/src/main/resources/schema.sql`
- Create: `backend/src/main/resources/migration/consultation_progress_history.sql`

- [x] Add the append-only table, entity and consultation mapper methods.
- [x] Add indexes for consultation and creation time.

### Task 2: Transactional history creation

**Files:**
- Modify: `backend/src/test/java/com/tcm/platform/service/ConsultationWorkspaceServiceTest.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/ConsultationWorkspaceService.java`

- [x] Write a failing test that expects one history insert per effective doctor update.
- [x] Write a failing test that rejects an update with no effective change.
- [x] Update the latest consultation snapshot and insert the progress record in one transaction.
- [x] Run `ConsultationWorkspaceServiceTest`.

### Task 3: Timeline read model

**Files:**
- Modify: `backend/src/main/java/com/tcm/platform/entity/Consultation.java`
- Modify: `backend/src/main/java/com/tcm/platform/dto/ConsultationWorkspaceRecord.java`
- Modify: `backend/src/test/java/com/tcm/platform/service/ConsultationServiceTest.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/ConsultationService.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/ConsultationWorkspaceService.java`

- [x] Write failing tests for doctor and patient timeline loading.
- [x] Batch-load records for returned consultation IDs.
- [x] Attach records in chronological order.

### Task 4: Doctor and patient timeline UI

**Files:**
- Modify: `frontend/src/views/admin/DoctorMyConsultationsView.vue`
- Modify: `frontend/src/views/patient/MyConsultationsView.vue`

- [x] Render the doctor processing timeline in the details drawer.
- [x] Render the patient-visible timeline in each consultation card.
- [x] Preserve useful empty states and latest-reply fallback.

### Task 5: Verification

- [x] Run focused backend tests.
- [x] Run the complete backend suite.
- [x] Run frontend tests and production build.
- [x] Execute the single local migration and restart the backend.
- [ ] Verify one real doctor update is visible to both roles.

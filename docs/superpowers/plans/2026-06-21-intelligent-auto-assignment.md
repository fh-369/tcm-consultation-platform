# Intelligent Auto Assignment Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Automatically assign newly created department consultations to the least-loaded eligible doctor without blocking consultation submission.

**Architecture:** Add a focused `AutoAssignmentService` backed by one deterministic candidate query. `ConsultationService` creates the record first and treats automatic assignment as best effort; existing manual assignment paths maintain the same `assigned_at` field.

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, MySQL 8, JUnit 5.

---

### Task 1: Assignment timestamp schema

**Files:**
- Modify: `backend/src/main/resources/schema.sql`
- Create: `backend/src/main/resources/migration/consultation_auto_assignment.sql`
- Modify: `backend/src/main/java/com/tcm/platform/entity/Consultation.java`
- Modify: `backend/src/main/java/com/tcm/platform/dto/ConsultationWorkspaceRecord.java`

- [x] Add nullable `assigned_at DATETIME` and an index for assignment ordering.
- [x] Map `assignedAt` in entity and workspace response.

### Task 2: Candidate selection and automatic assignment

**Files:**
- Create: `backend/src/main/java/com/tcm/platform/service/AutoAssignmentService.java`
- Create: `backend/src/test/java/com/tcm/platform/service/AutoAssignmentServiceTest.java`
- Modify: `backend/src/main/java/com/tcm/platform/mapper/UserMapper.java`
- Modify: `backend/src/main/java/com/tcm/platform/mapper/ConsultationMapper.java`

- [x] Write failing tests for general consultation skip, candidate assignment, and no-candidate behavior.
- [x] Add deterministic candidate SQL ordered by active load, missing history, oldest assignment, and doctor ID.
- [x] Update the consultation with the selected doctor and assignment timestamp.
- [x] Run the focused tests.

### Task 3: Consultation creation integration

**Files:**
- Modify: `backend/src/test/java/com/tcm/platform/service/ConsultationServiceTest.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/ConsultationService.java`

- [x] Write failing tests that verify assignment is attempted after insert and exceptions do not fail creation.
- [x] Inject `AutoAssignmentService` and call it after successful insertion.
- [x] Log assignment failures without rethrowing.
- [x] Run the focused tests.

### Task 4: Manual assignment lifecycle

**Files:**
- Modify: `backend/src/main/java/com/tcm/platform/mapper/ConsultationMapper.java`
- Modify: `backend/src/test/java/com/tcm/platform/service/ConsultationWorkspaceServiceTest.java`

- [x] Assert manual assignment and claim set `assigned_at`.
- [x] Assert cancellation and department transfer clear `assigned_at`.
- [x] Update mapper SQL and in-memory response values.

### Task 5: Verification

- [x] Run the complete backend suite.
- [x] Execute the migration against the local MySQL database.
- [x] Restart the backend.
- [x] Create a test consultation and verify assignment or safe unassigned fallback.
- [x] Run frontend tests and production build to guard shared contracts.

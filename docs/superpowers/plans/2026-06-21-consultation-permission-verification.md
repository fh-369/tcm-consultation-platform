# Consultation Permission Verification Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Verify and harden consultation permissions and return actionable authentication, authorization and business errors.

**Architecture:** Keep URL role rules in Spring Security, add explicit JSON handlers for unauthenticated and forbidden requests, and verify service-level ownership checks independently.

**Tech Stack:** Java 17, Spring Security 6, Spring MockMvc, Vue 3, Axios, Vitest.

---

### Task 1: Security response semantics

- [x] Add failing tests for anonymous `401` and role-denied `403` JSON responses.
- [x] Configure authentication and access-denied handlers.
- [x] Verify patient, doctor and administrator endpoint boundaries.

### Task 2: Business ownership and account state

- [x] Verify patients cannot read another patient's consultation.
- [x] Verify doctors cannot process another doctor's consultation.
- [x] Verify disabled and unapproved doctors cannot list or update consultations.
- [x] Verify business errors retain their explicit messages.

### Task 3: Frontend handling

- [x] Add failing tests distinguishing `401` and `403`.
- [x] Clear and redirect only for `401`.
- [x] Preserve the session and show a permission message for `403`.

### Task 4: Verification

- [x] Run focused backend and frontend tests.
- [x] Run complete backend and frontend tests.
- [x] Run the frontend production build.
- [ ] Restart the backend for page verification.

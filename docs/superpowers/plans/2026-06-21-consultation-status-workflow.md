# Consultation Status Workflow Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Enforce a predictable doctor consultation workflow and remove the follow-up-time business feature.

**Architecture:** Keep the existing doctor update endpoint, but centralize transition validation in `ConsultationWorkspaceService`. Replace the generic status selector with explicit actions derived from the current status.

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, Vue 3, Element Plus, JUnit 5, Vitest.

---

### Task 1: Backend transition rules

- [x] Add failing tests for allowed and forbidden transitions.
- [x] Require a non-empty reply when completing a consultation.
- [x] Reject every update to a completed consultation.
- [x] Preserve append-only progress records.

### Task 2: Remove follow-up business fields

- [x] Remove follow-up fields from DTOs and response entities.
- [x] Remove follow-up columns from fresh-install schema definitions.
- [x] Stop selecting, writing and rendering follow-up data.
- [x] Keep existing deployed database columns untouched.

### Task 3: Explicit doctor actions

- [x] Add tested frontend helpers for available actions.
- [x] Replace the status selector with start, reply and complete actions.
- [x] Make completed consultations read-only.
- [x] Clear the reply editor after a successful write.

### Task 4: Verification

- [x] Run focused backend and frontend tests.
- [x] Run complete backend and frontend tests.
- [x] Run the frontend production build.
- [ ] Restart the backend for page verification.

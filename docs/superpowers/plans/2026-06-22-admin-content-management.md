# Admin Content Management Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a complete administrator workflow for managing wellness articles, recipes, publication state, and cover images.

**Architecture:** Keep the existing content entities and public read APIs, but introduce validated administrator request DTOs and explicit publication endpoints. Split the frontend into a shared management page, a focused editor drawer, a cover uploader, and resource configuration so article and recipe behavior remain understandable independently.

**Tech Stack:** Spring Boot 3.2, Jakarta Validation, MyBatis-Plus, Vue 3, Element Plus, markdown-it, Vitest.

---

### Task 1: Backend request contracts and publication workflow

**Files:**
- Create: `backend/src/main/java/com/tcm/platform/dto/KnowledgeArticleAdminRequest.java`
- Create: `backend/src/main/java/com/tcm/platform/dto/RecipeAdminRequest.java`
- Create: `backend/src/main/java/com/tcm/platform/dto/PublicationRequest.java`
- Modify: `backend/src/main/java/com/tcm/platform/controller/AdminController.java`
- Modify: `backend/src/main/java/com/tcm/platform/controller/RecipeAdminController.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/KnowledgeArticleService.java`
- Modify: `backend/src/main/java/com/tcm/platform/service/RecipeService.java`
- Test: `backend/src/test/java/com/tcm/platform/service/KnowledgeArticleServiceTest.java`
- Test: `backend/src/test/java/com/tcm/platform/service/RecipeServiceTest.java`

- [ ] Add failing tests for normalized input, invalid season/constitution, and independent publication updates.
- [ ] Run the focused service tests and confirm the new cases fail.
- [ ] Add validated DTOs and map them to the existing entities.
- [ ] Add service methods that update only the `published` field.
- [ ] Add `PUT /api/admin/knowledge/{id}/publication` and `PUT /api/admin/recipe/{id}/publication`.
- [ ] Run the focused service tests and confirm they pass.

### Task 2: Local cover upload

**Files:**
- Create: `backend/src/main/java/com/tcm/platform/service/ContentImageStorageService.java`
- Create: `backend/src/main/java/com/tcm/platform/controller/ContentImageController.java`
- Create: `backend/src/main/java/com/tcm/platform/dto/ContentImageResponse.java`
- Modify: `backend/src/main/java/com/tcm/platform/config/WebConfig.java`
- Modify: `backend/src/main/resources/application.yml`
- Test: `backend/src/test/java/com/tcm/platform/service/ContentImageStorageServiceTest.java`

- [ ] Add failing tests for valid JPEG/PNG/WebP uploads, empty files, unsupported types, and files above 5 MB.
- [ ] Implement generated safe filenames and configurable `uploads/content` storage.
- [ ] Expose stored files through `/uploads/content/**`.
- [ ] Add the administrator multipart upload endpoint.
- [ ] Run the focused storage tests.

### Task 3: Frontend API and resource rules

**Files:**
- Modify: `frontend/src/api/content.js`
- Modify: `frontend/src/api/content.test.js`
- Create: `frontend/src/features/admin/contentManagement.js`
- Create: `frontend/src/features/admin/contentManagement.test.js`

- [ ] Add failing tests for publication and multipart upload requests.
- [ ] Add API functions for publication changes and cover upload.
- [ ] Add resource field definitions, season ordering, constitution options, payload normalization, and validation helpers.
- [ ] Run the frontend unit tests.

### Task 4: Content editor components

**Files:**
- Create: `frontend/src/components/admin/ContentCoverUploader.vue`
- Create: `frontend/src/components/admin/ContentEditorDrawer.vue`
- Modify: `frontend/src/views/admin/ContentManagementView.vue`

- [ ] Replace the compact dialog with a wide editor drawer.
- [ ] Add cover selection, validation, preview, replacement, and reference removal.
- [ ] Add article Markdown editing and sanitized preview.
- [ ] Add structured recipe inputs with normalized select options.
- [ ] Add distinct “保存草稿” and “保存并发布” actions.
- [ ] Add explicit publish/unpublish controls in the list.
- [ ] Improve filters, empty state, loading state, pagination, and desktop layout while preserving the established admin design language.

### Task 5: Verification

**Files:**
- Modify only files required by failures discovered during verification.

- [ ] Run backend focused tests.
- [ ] Run the full backend test suite.
- [ ] Run `npm test`.
- [ ] Run `npm run build`.
- [ ] Run `git diff --check` and inspect the final worktree scope.
- [ ] Hand the administrator and patient-side verification checklist to the user before any Git operation.

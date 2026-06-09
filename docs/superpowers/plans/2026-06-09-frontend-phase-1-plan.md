# Frontend Phase 1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Initialize the Vue frontend and deliver the patient top-navigation and admin sidebar layout foundations required by Frontend Phase 1.

**Architecture:** One Vue 3 + Vite JavaScript application uses nested Vue Router routes to switch between patient and admin layouts. Shared CSS tokens provide the Clear Herbal visual system, while placeholder views verify navigation without implementing later-phase authentication or business functionality.

**Tech Stack:** Vue 3, Vite, JavaScript, Vue Router, Pinia, Axios, Element Plus, native CSS

---

### Task 1: Initialize The Vue Project

**Files:**
- Replace: `frontend/README.md`
- Create: `frontend/package.json`
- Create: `frontend/package-lock.json`
- Create: `frontend/index.html`
- Create: `frontend/vite.config.js`
- Create: `frontend/.env.example`
- Create: `frontend/src/App.vue`
- Create: `frontend/src/main.js`

- [ ] **Step 1: Scaffold Vue 3 + Vite in the existing frontend directory**

Run:

```powershell
npm create vite@latest frontend -- --template vue
```

Expected: Vite creates a Vue JavaScript project without TypeScript.

- [ ] **Step 2: Install Phase 1 dependencies**

Run:

```powershell
npm --prefix frontend install
npm --prefix frontend install vue-router pinia axios element-plus
```

Expected: `frontend/package.json` and `frontend/package-lock.json` include all four required dependencies.

- [ ] **Step 3: Configure Vite and environment example**

Set `vite.config.js` to use the Vue plugin and create `.env.example`:

```text
VITE_API_BASE_URL=http://localhost:8080/api
```

- [ ] **Step 4: Verify the initial project**

Run:

```powershell
npm --prefix frontend run build
```

Expected: Vite production build succeeds.

### Task 2: Establish The Clear Herbal Design Foundation

**Files:**
- Create: `frontend/src/assets/styles/tokens.css`
- Create: `frontend/src/assets/styles/global.css`
- Modify: `frontend/src/main.js`
- Modify: `frontend/src/App.vue`

- [ ] **Step 1: Define shared CSS tokens**

Define semantic colors, spacing, radius, typography, elevation, focus, and transition tokens. Use ink jade `#173C2D`, mist green `#F3F8F5`, cinnabar `#C9513D`, and white surfaces.

- [ ] **Step 2: Add global resets and accessibility states**

Implement readable base typography, visible focus states, route-link active states, responsive containers, and reduced-motion handling.

- [ ] **Step 3: Register global styles and Element Plus**

Import Element Plus CSS, tokens, and global styles from `src/main.js`.

- [ ] **Step 4: Verify the design foundation**

Run:

```powershell
npm --prefix frontend run build
```

Expected: Build succeeds with no missing CSS imports.

### Task 3: Build The Router And Patient Layout

**Files:**
- Create: `frontend/src/router/index.js`
- Create: `frontend/src/layouts/PatientLayout.vue`
- Create: `frontend/src/views/patient/HomeView.vue`
- Create: `frontend/src/views/patient/PatientPlaceholderView.vue`

- [ ] **Step 1: Define nested patient routes**

Create routes for:

```text
/
/consultation/new
/knowledge
/recipes
/profile
```

Use `PatientLayout` as the parent and lazy-load each route view.

- [ ] **Step 2: Build the patient top-navigation layout**

Add the brand, ordinary navigation links including `在线问诊`, router outlet, and footer. Do not place a second primary consultation button in the top navigation.

- [ ] **Step 3: Build the patient home and placeholder views**

The home view uses the selected Clear Herbal hero with a botanical illustration treatment and the only primary consultation action labeled `开始在线问诊`. The placeholder view clearly identifies future pages without implementing their functions.

- [ ] **Step 4: Verify patient navigation**

Run:

```powershell
npm --prefix frontend run dev
```

Expected: Patient routes render inside the patient layout and navigation changes pages without a full reload.

### Task 4: Build The Admin Layout

**Files:**
- Create: `frontend/src/layouts/AdminLayout.vue`
- Create: `frontend/src/views/admin/DashboardView.vue`
- Create: `frontend/src/views/admin/AdminPlaceholderView.vue`
- Modify: `frontend/src/router/index.js`

- [ ] **Step 1: Define nested admin routes**

Create routes for:

```text
/admin
/admin/consultations
/admin/knowledge
/admin/recipes
/admin/export
```

Use `AdminLayout` as the parent and lazy-load each route view.

- [ ] **Step 2: Build the admin sidebar layout**

Add sidebar navigation, a top account placeholder, router outlet, and a narrow-screen sidebar fallback.

- [ ] **Step 3: Build the admin dashboard and placeholder views**

Use static metrics and a static priority list only to demonstrate layout density. Do not call backend APIs.

- [ ] **Step 4: Verify admin navigation**

Expected: All admin routes render inside the admin layout and active navigation remains clear.

### Task 5: Add Axios Foundation And Complete Validation

**Files:**
- Create: `frontend/src/api/request.js`
- Create: `frontend/src/views/NotFoundView.vue`
- Modify: `frontend/src/router/index.js`
- Modify: `frontend/README.md`

- [ ] **Step 1: Create the Axios instance**

Configure:

```js
import axios from 'axios'

export default axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
})
```

Do not add token interceptors in Phase 1.

- [ ] **Step 2: Add a not-found route**

Create a useful not-found page with a route back to the patient home.

- [ ] **Step 3: Update frontend documentation**

Document the selected stack, local commands, Phase 1 route list, environment variable, and the boundary between Phase 1 and later phases.

- [ ] **Step 4: Run final production build**

Run:

```powershell
npm --prefix frontend run build
```

Expected: Build succeeds.

- [ ] **Step 5: Run browser validation**

Run:

```powershell
npm --prefix frontend run dev
```

Verify patient routes, admin routes, narrow viewport behavior, keyboard focus states, and browser console errors.

- [ ] **Step 6: Review Git boundaries**

Run:

```powershell
git status --short
git diff --check
```

Expected: `frontend/node_modules/` and `frontend/dist/` do not appear in Git status.

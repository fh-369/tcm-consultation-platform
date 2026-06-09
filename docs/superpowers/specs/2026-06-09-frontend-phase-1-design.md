# Frontend Phase 1 Design

## Scope

Frontend Phase 1 strictly follows `docs/中医问诊与养生平台_Git与GitHub双线实战指南.md`:

- Initialize a Vue 3 + Vite project using JavaScript.
- Install Vue Router, Pinia, Axios, and Element Plus.
- Establish the frontend directory structure.
- Create a patient-facing top-navigation layout.
- Create an admin sidebar layout.
- Configure the Axios base URL.

The following belong to later frontend phases and are intentionally excluded:

- Login and registration pages
- Token storage and automatic Authorization headers
- Permission-based route guards
- Real consultation, knowledge, recipe, or dashboard pages
- Real backend API integration

## Visual Direction

The selected direction is **Clear Herbal System**.

- Patient-facing pages use a calm, spacious layout with a modern herbal illustration.
- Admin pages use a denser layout optimized for scanning and repeated work.
- Both layouts share the same design tokens:
  - Ink jade: `#173C2D`
  - Mist green: `#F3F8F5`
  - Cinnabar accent: `#C9513D`
  - Content surface: `#FFFFFF`
- Cinnabar is reserved for the primary action and urgent status.
- The interface uses one consistent soft-corner system.
- The patient home hero has one primary action only: `开始在线问诊`.
- The top navigation includes `在线问诊` as a normal navigation link, not a second primary button.

## Architecture

### Application Entry

`src/main.js` creates the Vue application and registers:

- Pinia
- Vue Router
- Element Plus
- Global styles

### Routing

Vue Router provides Phase 1 navigation and layout switching.

Routes use a layout component as the parent:

- `PatientLayout` contains the public patient-facing placeholders.
- `AdminLayout` contains the admin placeholders.
- `NotFoundView` handles unmatched paths.

Phase 1 routes do not contain authentication or permission guards.

### Layouts

`PatientLayout` provides:

- Brand area
- Top navigation
- Main content outlet
- Simple footer

`AdminLayout` provides:

- Persistent sidebar navigation
- Top account placeholder
- Main content outlet

### Placeholder Views

Phase 1 includes only enough views to verify layouts and navigation:

- Patient home
- Patient placeholder page
- Admin dashboard placeholder
- Admin placeholder page
- Not found page

These views use static demonstration content and do not call backend APIs.

### API Foundation

`src/api/request.js` exports one Axios instance.

- The base URL comes from `VITE_API_BASE_URL`.
- The fallback base URL is `http://localhost:8080/api`.
- No token interceptor or response business handling is added in Phase 1.

## Directory Structure

```text
frontend/
├─ index.html
├─ package.json
├─ package-lock.json
├─ vite.config.js
├─ .env.example
└─ src/
   ├─ api/
   │  └─ request.js
   ├─ assets/
   │  └─ styles/
   │     ├─ tokens.css
   │     └─ global.css
   ├─ layouts/
   │  ├─ PatientLayout.vue
   │  └─ AdminLayout.vue
   ├─ router/
   │  └─ index.js
   ├─ views/
   │  ├─ patient/
   │  │  ├─ HomeView.vue
   │  │  └─ PatientPlaceholderView.vue
   │  ├─ admin/
   │  │  ├─ DashboardView.vue
   │  │  └─ AdminPlaceholderView.vue
   │  └─ NotFoundView.vue
   ├─ App.vue
   └─ main.js
```

## Interaction And Accessibility

- Interactive targets are at least 44 pixels high where practical.
- Links and buttons have visible hover and keyboard focus states.
- Navigation does not rely on color alone to indicate the active route.
- The layout must not cause horizontal scrolling on narrow screens.
- Motion is limited to short state transitions and respects `prefers-reduced-motion`.

## Validation

Frontend Phase 1 is complete when:

1. `npm install` succeeds.
2. `npm run build` succeeds.
3. Patient and admin layout routes render correctly.
4. Navigation switches between placeholder routes.
5. The browser console has no runtime errors.
6. Axios uses the configured base URL.
7. `frontend/node_modules/` and `frontend/dist/` remain ignored by Git.


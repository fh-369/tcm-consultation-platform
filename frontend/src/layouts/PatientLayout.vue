<script setup>
import { useRouter } from 'vue-router'

import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()

const navigation = [
  { label: '首页', to: '/' },
  { label: '在线问诊', to: '/consultation/new' },
  { label: '我的问诊', to: '/consultation/my' },
  { label: '养生知识', to: '/knowledge' },
  { label: '药膳推荐', to: '/recipes' },
]

async function logout() {
  auth.logout()
  await router.push('/')
}
</script>

<template>
  <div class="patient-shell">
    <header class="patient-header">
      <div class="page-container header-inner">
        <RouterLink class="brand" to="/" aria-label="返回岐黄问诊首页">
          <span class="brand-mark" aria-hidden="true">岐</span>
          <span>
            <strong>岐黄问诊</strong>
            <small>中医问诊与养生平台</small>
          </span>
        </RouterLink>

        <nav class="patient-nav" aria-label="患者端主导航">
          <RouterLink v-for="item in navigation" :key="item.to" :to="item.to">
            {{ item.label }}
          </RouterLink>
          <div v-if="auth.isPatient" class="patient-account">
            <span>{{ auth.displayName || '患者用户' }}</span>
            <button type="button" @click="logout">退出</button>
          </div>
          <RouterLink v-else class="login-link" to="/login/patient">登录</RouterLink>
        </nav>
      </div>
    </header>

    <main>
      <RouterView />
    </main>

    <footer class="patient-footer">
      <div class="page-container footer-inner">
        <strong>岐黄问诊</strong>
        <span>清楚记录身体变化，安心了解问诊进度。</span>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.patient-shell {
  min-height: 100vh;
  background:
    radial-gradient(circle at 85% 8%, rgb(126 183 153 / 20%), transparent 26%),
    var(--color-mist);
}

.patient-header {
  position: sticky;
  z-index: 20;
  top: 0;
  border-bottom: 1px solid rgb(217 230 223 / 86%);
  background: rgb(255 255 255 / 90%);
  backdrop-filter: blur(16px);
}

.header-inner {
  display: flex;
  min-height: 76px;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.brand {
  display: inline-flex;
  min-height: 48px;
  align-items: center;
  gap: 11px;
}

.brand-mark {
  display: grid;
  width: 38px;
  height: 38px;
  place-items: center;
  border-radius: 50%;
  background: var(--color-ink);
  color: white;
  font-size: 18px;
  font-weight: 800;
}

.brand strong,
.brand small {
  display: block;
}

.brand strong {
  letter-spacing: 0.08em;
}

.brand small {
  margin-top: 3px;
  color: var(--color-text-muted);
  font-size: 10px;
}

.patient-nav {
  display: flex;
  align-items: center;
  gap: 4px;
}

.patient-account {
  display: inline-flex;
  min-height: 38px;
  align-items: center;
  gap: 7px;
  margin-left: 5px;
  padding-left: 11px;
  border-left: 1px solid var(--color-border);
  color: var(--color-ink);
  font-size: 12px;
  font-weight: 700;
}

.patient-account button {
  min-height: 34px;
  border: 0;
  border-radius: var(--radius-sm);
  background: transparent;
  color: var(--color-cinnabar);
  cursor: pointer;
  font-size: 11px;
  font-weight: 800;
}

.patient-account button:hover {
  background: var(--color-cinnabar-soft);
}

.patient-nav .login-link {
  margin-left: 5px;
  background: var(--color-ink);
  color: white;
}

.patient-nav .login-link:hover {
  background: var(--color-ink-soft);
  color: white;
}

.patient-nav a {
  display: inline-flex;
  min-height: 44px;
  align-items: center;
  padding: 0 13px;
  border-radius: var(--radius-sm);
  color: var(--color-text-muted);
  font-size: 14px;
  font-weight: 600;
}

.patient-nav a:hover,
.patient-nav a.router-link-exact-active {
  background: var(--color-jade-light);
  color: var(--color-ink);
}

.patient-nav a.router-link-exact-active::after {
  width: 5px;
  height: 5px;
  margin-left: 7px;
  border-radius: 50%;
  background: var(--color-cinnabar);
  content: "";
}

.patient-footer {
  margin-top: 48px;
  background: var(--color-ink);
  color: rgb(255 255 255 / 72%);
}

.footer-inner {
  display: flex;
  min-height: 94px;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.footer-inner strong {
  color: white;
  letter-spacing: 0.08em;
}

@media (max-width: 850px) {
  .header-inner {
    display: block;
    padding-block: 12px;
  }

  .patient-nav {
    margin-top: 8px;
    overflow-x: auto;
  }

  .patient-nav a {
    flex: 0 0 auto;
  }

  .patient-account {
    flex: 0 0 auto;
  }
}

@media (max-width: 620px) {
  .brand small {
    display: none;
  }

  .footer-inner {
    display: grid;
    justify-content: start;
    padding-block: 22px;
  }
}
</style>

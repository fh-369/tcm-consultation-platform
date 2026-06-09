<script setup>
import { useRouter } from 'vue-router'

import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()

const navigation = [
  { label: '工作台', to: '/admin' },
  { label: '问诊管理', to: '/admin/consultations' },
  { label: '知识文章', to: '/admin/knowledge' },
  { label: '药膳管理', to: '/admin/recipes' },
  { label: '数据导出', to: '/admin/export' },
]

async function logout() {
  auth.logout()
  await router.replace('/login/admin')
}
</script>

<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <RouterLink class="admin-brand" to="/admin">
        <span class="admin-brand-mark">岐</span>
        <span>
          <strong>岐黄后台</strong>
          <small>诊疗工作空间</small>
        </span>
      </RouterLink>

      <nav class="admin-nav" aria-label="后台主导航">
        <RouterLink v-for="item in navigation" :key="item.to" :to="item.to">
          {{ item.label }}
        </RouterLink>
      </nav>

      <RouterLink class="back-home" to="/">返回患者端首页</RouterLink>
    </aside>

    <div class="admin-content">
      <header class="admin-topbar">
        <div>
          <strong>问诊工作台</strong>
          <small>静态布局预览</small>
        </div>
        <div class="account-placeholder">
          <span aria-hidden="true">医</span>
          <div>
            <strong>{{ auth.displayName || '后台用户' }}</strong>
            <small>{{ auth.role === 'admin' ? '管理员' : '医生' }}</small>
          </div>
          <button type="button" @click="logout">退出</button>
        </div>
      </header>

      <main class="admin-main">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-shell {
  display: grid;
  min-height: 100vh;
  grid-template-columns: 240px minmax(0, 1fr);
  background: #edf3ef;
}

.admin-sidebar {
  display: flex;
  flex-direction: column;
  padding: 24px 16px;
  background: var(--color-ink);
  color: white;
}

.admin-brand {
  display: flex;
  min-height: 52px;
  align-items: center;
  gap: 10px;
  padding: 0 8px;
}

.admin-brand-mark {
  display: grid;
  width: 36px;
  height: 36px;
  place-items: center;
  border: 1px solid rgb(255 255 255 / 24%);
  border-radius: 50%;
  background: var(--color-ink-soft);
  font-weight: 800;
}

.admin-brand strong,
.admin-brand small,
.account-placeholder strong,
.account-placeholder small {
  display: block;
}

.admin-brand small {
  margin-top: 3px;
  color: rgb(255 255 255 / 55%);
  font-size: 10px;
}

.admin-nav {
  display: grid;
  gap: 5px;
  margin-top: 42px;
}

.admin-nav a,
.back-home {
  display: flex;
  min-height: 46px;
  align-items: center;
  padding: 0 13px;
  border-radius: var(--radius-sm);
  color: rgb(255 255 255 / 65%);
  font-size: 14px;
}

.admin-nav a:hover,
.admin-nav a.router-link-exact-active {
  background: var(--color-ink-soft);
  color: white;
}

.admin-nav a.router-link-exact-active {
  box-shadow: inset 3px 0 var(--color-cinnabar);
}

.back-home {
  margin-top: auto;
  border: 1px solid rgb(255 255 255 / 14%);
  justify-content: center;
  font-size: 12px;
}

.admin-content {
  min-width: 0;
}

.admin-topbar {
  display: flex;
  min-height: 76px;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 12px 28px;
  border-bottom: 1px solid var(--color-border);
  background: rgb(255 255 255 / 88%);
}

.admin-topbar strong,
.admin-topbar small {
  display: block;
}

.admin-topbar small {
  margin-top: 3px;
  color: var(--color-text-muted);
  font-size: 10px;
}

.account-placeholder {
  display: flex;
  align-items: center;
  gap: 10px;
}

.account-placeholder > span {
  display: grid;
  width: 38px;
  height: 38px;
  place-items: center;
  border-radius: 50%;
  background: var(--color-jade-light);
  color: var(--color-ink);
  font-weight: 800;
}

.account-placeholder button {
  min-height: 34px;
  padding: 0 10px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: white;
  color: var(--color-cinnabar);
  cursor: pointer;
  font-size: 11px;
  font-weight: 800;
}

.account-placeholder button:hover {
  border-color: var(--color-cinnabar);
  background: var(--color-cinnabar-soft);
}

.admin-main {
  padding: 28px;
}

@media (max-width: 880px) {
  .admin-shell {
    grid-template-columns: 1fr;
  }

  .admin-sidebar {
    display: block;
    padding: 12px;
  }

  .admin-brand small,
  .back-home {
    display: none;
  }

  .admin-nav {
    display: flex;
    margin-top: 12px;
    overflow-x: auto;
  }

  .admin-nav a {
    flex: 0 0 auto;
  }
}

@media (max-width: 620px) {
  .admin-topbar {
    padding: 12px;
  }

  .account-placeholder div {
    display: none;
  }

  .admin-main {
    padding: 16px 12px;
  }
}
</style>

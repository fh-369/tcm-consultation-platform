<script setup>
import {
  ChatDotRound,
  Clock,
  DocumentAdd,
  Food,
  House,
  Reading,
  SwitchButton,
  User,
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()

const navigation = [
  { label: '首页', to: '/', icon: House },
  { label: '在线问诊', to: '/consultation/new', icon: DocumentAdd },
  { label: '我的问诊', to: '/consultation/my', icon: Clock },
  { label: '养生知识', to: '/knowledge', icon: Reading },
  { label: '药膳推荐', to: '/recipes', icon: Food },
  { label: 'AI 问答', to: '/ai/ask', icon: ChatDotRound },
]

async function logout() {
  auth.logout()
  await router.replace('/login')
}
</script>

<template>
  <div class="patient-shell">
    <header class="patient-header">
      <div class="page-container header-inner">
        <RouterLink class="brand" to="/" aria-label="返回知身问养首页">
          <img class="brand-mark" src="../assets/brand/logo-mark.png" alt="" />
          <span>
            <strong>知身问养</strong>
            <small>中医问诊与日常养护平台</small>
          </span>
        </RouterLink>

        <nav class="patient-nav" aria-label="患者端主导航">
          <RouterLink v-for="item in navigation" :key="item.to" :to="item.to">
            <el-icon><component :is="item.icon" /></el-icon>
            {{ item.label }}
          </RouterLink>
          <div v-if="auth.isPatient" class="patient-account">
            <span><el-icon><User /></el-icon>{{ auth.displayName || '用户' }}</span>
            <button type="button" aria-label="退出登录" @click="logout">
              <el-icon><SwitchButton /></el-icon>
              退出
            </button>
          </div>
          <RouterLink v-else class="login-link" to="/login">登录</RouterLink>
        </nav>
      </div>
    </header>

    <main>
      <RouterView />
    </main>
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
  border-bottom: 1px solid rgb(23 60 45 / 8%);
  background: rgb(250 252 250 / 88%);
  box-shadow: 0 10px 34px rgb(23 60 45 / 5%);
  backdrop-filter: blur(20px) saturate(145%);
}

.header-inner {
  display: flex;
  min-height: 82px;
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
  width: 42px;
  height: 42px;
  object-fit: contain;
}

.brand strong,
.brand small {
  display: block;
}

.brand strong {
  font-family: "Noto Serif SC", serif;
  font-size: 18px;
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
  gap: 7px;
}

.patient-account {
  display: inline-flex;
  min-height: 44px;
  align-items: center;
  gap: 4px;
  margin-left: 7px;
  padding-left: 14px;
  border-left: 1px solid var(--color-border);
  color: var(--color-ink);
  font-size: 12px;
  font-weight: 700;
}

.patient-account > span,
.patient-account button {
  display: inline-flex;
  min-height: 36px;
  align-items: center;
  gap: 5px;
  padding: 0 9px;
}

.patient-account > span {
  border-radius: 999px;
  background: var(--color-jade-light);
}

.patient-account button {
  border: 0;
  border-radius: 999px;
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
  margin-left: 7px;
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
  gap: 7px;
  padding: 0 14px;
  border: 1px solid transparent;
  border-radius: 999px;
  color: var(--color-text-muted);
  font-size: 13px;
  font-weight: 600;
}

.patient-nav a:hover {
  background: var(--color-jade-light);
  color: var(--color-ink);
}

.patient-nav a.router-link-exact-active {
  border-color: rgb(23 60 45 / 42%);
  background: white;
  color: var(--color-ink);
  box-shadow: 0 5px 15px rgb(23 60 45 / 8%);
}

.patient-nav .el-icon,
.patient-account .el-icon {
  font-size: 16px;
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

}
</style>

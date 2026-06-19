<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { getDashboardSummary, getDashboardTrend } from '../../api/content'
import { getWorkspaceIdentity } from '../../features/admin/workspace'
import { useAuthStore } from '../../stores/auth'

const auth = useAuthStore()
const loading = ref(false)
const trendLoading = ref(false)
const summary = ref({ statusDistribution: [], urgencyDistribution: [], trendLast6Months: [] })
const trend = ref([])
const trendPeriod = ref('month')
const total = computed(() => summary.value.statusDistribution.reduce((sum, item) => sum + Number(item.value || 0), 0))
const maxTrend = computed(() => Math.max(...trend.value.map((item) => Number(item.value || 0)), 1))
const identity = computed(() => getWorkspaceIdentity(auth.role))
const isDoctor = computed(() => auth.role === 'doctor')
const trendPeriods = [
  { label: '近 7 天', value: 'day' },
  { label: '近 4 周', value: 'week' },
  { label: '近 6 个月', value: 'month' },
]

function distributionValue(items, label) {
  return Number(items.find((item) => item.label === label)?.value || 0)
}

const pending = computed(() => distributionValue(summary.value.statusDistribution, '待接诊'))
const completed = computed(() => distributionValue(summary.value.statusDistribution, '已完成'))
const urgent = computed(() => (
  distributionValue(summary.value.urgencyDistribution, '紧急')
  + distributionValue(summary.value.urgencyDistribution, '非常紧急')
))

async function loadDashboard() {
  loading.value = true
  try {
    summary.value = await getDashboardSummary()
    trend.value = summary.value.trendLast6Months
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '工作台加载失败')
  } finally {
    loading.value = false
  }
}

async function changeTrendPeriod(period) {
  if (period === trendPeriod.value || trendLoading.value) return

  trendLoading.value = true
  try {
    trend.value = await getDashboardTrend(period)
    trendPeriod.value = period
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '趋势数据加载失败')
  } finally {
    trendLoading.value = false
  }
}

function formatTrendLabel(label) {
  if (trendPeriod.value === 'month') return label
  const [, month, day] = String(label).split('-')
  return month && day ? `${month}-${day}` : label
}

onMounted(loadDashboard)
</script>

<template>
  <section v-loading="loading" class="dashboard">
    <section class="welcome-panel">
      <div>
        <h1>{{ auth.displayName || identity.roleLabel }}，欢迎回来</h1>
        <span>
          {{ isDoctor
            ? '先关注紧急问诊与待处理记录。个人分配和认领数据将在下一阶段接入。'
            : '集中查看平台运行状态、问诊风险与内容管理入口。' }}
        </span>
      </div>
      <div class="welcome-actions">
        <RouterLink to="/admin/consultations">
          {{ isDoctor ? '进入问诊工作区' : '查看问诊调度' }}
        </RouterLink>
        <RouterLink v-if="!isDoctor" class="secondary" to="/admin/export">导出数据</RouterLink>
      </div>
    </section>

    <section class="action-strip">
      <div>
        <h2>
          {{ isDoctor
            ? `目前有 ${pending} 张待接诊、${urgent} 张紧急问诊需要关注`
            : `已有 ${completed} 张问诊完成处理，仍有 ${pending} 张等待接诊` }}
        </h2>
      </div>
    </section>

    <div class="dashboard-grid">
      <section v-loading="trendLoading" class="data-card trend-card">
        <header>
          <h2>问诊趋势</h2>
          <div class="trend-periods" aria-label="问诊趋势周期">
            <button
              v-for="period in trendPeriods"
              :key="period.value"
              type="button"
              :class="{ active: trendPeriod === period.value }"
              @click="changeTrendPeriod(period.value)"
            >
              {{ period.label }}
            </button>
          </div>
        </header>
        <div v-if="trend.length" class="trend-chart">
          <div v-for="item in trend" :key="item.label">
            <strong>{{ item.value }}</strong>
            <i :style="{ height: `${Math.max(Number(item.value) / maxTrend * 150, 4)}px` }"></i>
            <span>{{ formatTrendLabel(item.label) }}</span>
          </div>
        </div>
        <div v-else class="trend-empty">当前周期暂无问诊记录</div>
      </section>

      <section class="data-card distribution-card">
        <header>
          <h2>{{ isDoctor ? '问诊优先级' : '状态与风险' }}</h2>
        </header>

        <div class="distribution">
          <div v-for="item in summary.statusDistribution" :key="item.label">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
            <i :style="{ width: `${total ? Number(item.value) / total * 100 : 0}%` }"></i>
          </div>
        </div>

        <div class="urgency-list">
          <div v-for="item in summary.urgencyDistribution" :key="item.label">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </section>
    </div>
  </section>
</template>

<style scoped>
.dashboard {
  max-width: 1480px;
  margin: 0 auto;
}

.welcome-panel {
  display: flex;
  min-height: 176px;
  align-items: center;
  justify-content: space-between;
  gap: 30px;
  padding: 28px 32px;
  border-radius: 28px;
  background:
    radial-gradient(circle at 78% 0%, rgb(148 199 172 / 36%), transparent 32%),
    linear-gradient(135deg, #0e4937, #17614a);
  color: white;
  box-shadow: 0 18px 44px rgb(14 73 55 / 18%);
}

.welcome-panel h1 {
  margin: 0;
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: clamp(28px, 3vw, 42px);
  letter-spacing: -.04em;
}

.welcome-panel span {
  display: block;
  max-width: 660px;
  margin-top: 12px;
  color: rgb(255 255 255 / 70%);
  font-size: 13px;
  line-height: 1.8;
}

.welcome-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
}

.welcome-actions a {
  display: inline-flex;
  min-height: 44px;
  align-items: center;
  padding: 0 18px;
  border: 1px solid white;
  border-radius: 999px;
  background: white;
  color: var(--color-ink);
  font-size: 12px;
  font-weight: 900;
  transition: .18s ease;
}

.welcome-actions a.secondary {
  border-color: rgb(255 255 255 / 28%);
  background: transparent;
  color: white;
}

.welcome-actions a:hover {
  transform: translateY(-1px);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.55fr) minmax(320px, .75fr);
  gap: 14px;
  margin-top: 18px;
}

.data-card,
.action-strip {
  padding: 22px;
  border: 1px solid rgb(47 95 72 / 11%);
  border-radius: 22px;
  background: rgb(255 255 255 / 88%);
  box-shadow: 0 12px 30px rgb(21 56 42 / 5%);
}

.data-card header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.data-card h2,
.action-strip h2 {
  margin: 0;
  color: var(--color-ink);
  font-size: 18px;
}

.trend-periods {
  display: inline-flex;
  gap: 4px;
  padding: 4px;
  border: 1px solid rgb(47 95 72 / 10%);
  border-radius: 999px;
  background: #f2f7f3;
}

.trend-periods button {
  min-height: 30px;
  padding: 0 12px;
  border: 0;
  border-radius: 999px;
  background: transparent;
  color: var(--color-text-muted);
  cursor: pointer;
  font: inherit;
  font-size: 10px;
  font-weight: 800;
  transition: .18s ease;
}

.trend-periods button:hover {
  color: var(--color-ink);
}

.trend-periods button.active {
  background: var(--color-ink);
  color: white;
  box-shadow: 0 5px 12px rgb(23 77 55 / 15%);
}

.trend-chart {
  display: flex;
  min-height: 228px;
  align-items: end;
  gap: 20px;
  margin-top: 20px;
  padding: 18px 16px 0;
  border-radius: 18px;
  background: linear-gradient(rgb(37 83 61 / 5%) 1px, transparent 1px);
  background-size: 100% 38px;
}

.trend-chart div {
  display: grid;
  flex: 1;
  justify-items: center;
  gap: 7px;
  color: var(--color-text-muted);
  font-size: 9px;
}

.trend-chart i {
  width: min(48px, 72%);
  border-radius: 8px 8px 2px 2px;
  background: linear-gradient(180deg, #77ae91, var(--color-ink));
  box-shadow: 0 8px 16px rgb(23 77 55 / 14%);
}

.trend-chart strong {
  color: var(--color-ink);
  font-size: 11px;
}

.trend-empty {
  display: grid;
  min-height: 228px;
  margin-top: 20px;
  place-items: center;
  border-radius: 18px;
  background: #f5f8f5;
  color: var(--color-text-muted);
  font-size: 12px;
}

.distribution {
  display: grid;
  gap: 15px;
  margin-top: 22px;
}

.distribution div {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
  color: var(--color-text-muted);
  font-size: 11px;
}

.distribution strong {
  color: var(--color-ink);
}

.distribution i {
  display: block;
  height: 7px;
  grid-column: 1 / -1;
  border-radius: 99px;
  background: linear-gradient(90deg, #83b79a, var(--color-ink));
}

.urgency-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 7px;
  margin-top: 24px;
}

.urgency-list div {
  padding: 10px;
  border-radius: 14px;
  background: #f2f7f3;
  text-align: center;
}

.urgency-list span,
.urgency-list strong {
  display: block;
}

.urgency-list span {
  color: var(--color-text-muted);
  font-size: 9px;
}

.urgency-list strong {
  margin-top: 5px;
  color: var(--color-ink);
  font-size: 17px;
}

.action-strip {
  display: flex;
  align-items: center;
  min-height: 92px;
  margin-top: 18px;
  padding-inline: 26px;
  background:
    radial-gradient(circle at 92% 10%, rgb(147 190 166 / 20%), transparent 30%),
    rgb(255 255 255 / 88%);
}

@media (max-width: 900px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }

  .welcome-panel,
  .action-strip {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>

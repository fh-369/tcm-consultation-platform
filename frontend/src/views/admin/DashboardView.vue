<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { getAdminConsultations } from '../../api/adminConsultation'
import {
  formatConsultationTime,
  reminderDisplay,
  urgencyDisplay,
} from '../../features/consultation/display'

const loading = ref(false)
const consultations = ref([])

const today = new Date().toISOString().slice(0, 10)
const metrics = computed(() => [
  { label: '待接诊', value: consultations.value.filter((item) => item.status === '待接诊').length },
  {
    label: '紧急提醒',
    value: consultations.value.filter((item) => item.reminderLevel === 'urgent').length,
    urgent: true,
  },
  { label: '接诊中', value: consultations.value.filter((item) => item.status === '接诊中').length },
  {
    label: '今日新增',
    value: consultations.value.filter((item) => String(item.createdAt).startsWith(today)).length,
  },
])

const priorityItems = computed(() =>
  [...consultations.value]
    .filter((item) => item.status !== '已完成')
    .sort((left, right) => priority(right) - priority(left))
    .slice(0, 6),
)

function priority(item) {
  const reminderWeight = { urgent: 30, attention: 20, normal: 10 }
  const urgencyWeight = { 非常紧急: 3, 紧急: 2, 普通: 1 }
  return (reminderWeight[item.reminderLevel] || 0) + (urgencyWeight[item.urgency] || 0)
}

async function loadDashboard() {
  loading.value = true
  try {
    const page = await getAdminConsultations({ current: 1, size: 100 })
    consultations.value = page.records || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '工作台加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadDashboard)
</script>

<template>
  <section v-loading="loading" class="dashboard">
    <div class="dashboard-heading">
      <div>
        <p>按提醒等级和紧急程度排序</p>
        <h1>今日问诊</h1>
      </div>
      <span>当前工作台基于实时问诊数据汇总</span>
    </div>

    <div class="metrics">
      <article v-for="metric in metrics" :key="metric.label" :class="{ urgent: metric.urgent }">
        <span>{{ metric.label }}</span>
        <strong>{{ metric.value }}</strong>
      </article>
    </div>

    <section class="priority-list">
      <header>
        <div>
          <p>优先展示尚未完成的问诊</p>
          <h2>优先处理</h2>
        </div>
        <RouterLink to="/admin/consultations">进入问诊管理</RouterLink>
      </header>

      <div class="table" role="table" aria-label="优先问诊列表">
        <div class="table-row table-head" role="row">
          <span>主要症状</span>
          <span>患者</span>
          <span>提交时间</span>
          <span>提醒</span>
        </div>
        <div v-for="item in priorityItems" :key="item.id" class="table-row" role="row">
          <strong>{{ item.symptoms }}</strong>
          <span>{{ item.patientName }}</span>
          <span>{{ formatConsultationTime(item.createdAt) }}</span>
          <span :class="['level', `level-${urgencyDisplay(item.urgency).tone}`]">
            {{ reminderDisplay(item.reminderLevel).label }}
          </span>
        </div>
        <el-empty v-if="!loading && priorityItems.length === 0" description="暂无待处理问诊" />
      </div>
    </section>
  </section>
</template>

<style scoped>
.dashboard-heading,
.priority-list header {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 20px;
}

.dashboard-heading p,
.priority-list p {
  margin: 0 0 5px;
  color: var(--color-text-muted);
  font-size: 11px;
}

.dashboard-heading h1,
.priority-list h2 {
  margin: 0;
  color: var(--color-ink);
}

.dashboard-heading h1 {
  font-size: 30px;
}

.dashboard-heading > span {
  color: var(--color-text-muted);
  font-size: 11px;
}

.metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 24px;
}

.metrics article {
  padding: 20px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: white;
  box-shadow: var(--shadow-card);
}

.metrics article.urgent {
  border-color: rgb(201 81 61 / 35%);
  background: #fff8f6;
}

.metrics span,
.metrics strong {
  display: block;
}

.metrics span {
  color: var(--color-text-muted);
  font-size: 12px;
}

.metrics strong {
  margin-top: 12px;
  font-size: 32px;
}

.metrics article.urgent strong {
  color: var(--color-cinnabar);
}

.priority-list {
  margin-top: 18px;
  padding: 22px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: white;
  box-shadow: var(--shadow-card);
}

.priority-list header a {
  min-height: 44px;
  padding: 12px 8px;
  color: var(--color-cinnabar);
  font-size: 12px;
  font-weight: 800;
}

.table {
  margin-top: 18px;
}

.table-row {
  display: grid;
  grid-template-columns: 1.5fr 0.7fr 1fr 0.6fr;
  gap: 12px;
  align-items: center;
  min-height: 54px;
  border-top: 1px solid #edf2ef;
  font-size: 12px;
}

.table-head {
  min-height: 38px;
  border-top: 0;
  color: var(--color-text-muted);
  font-size: 10px;
  font-weight: 800;
}

.level {
  width: fit-content;
  padding: 5px 8px;
  border-radius: 5px;
  background: var(--color-jade-light);
  color: var(--color-ink);
  font-weight: 800;
}

.level-attention,
.level-urgent {
  background: var(--color-cinnabar-soft);
  color: #9f3f2e;
}

@media (max-width: 900px) {
  .metrics {
    grid-template-columns: repeat(2, 1fr);
  }

  .table {
    overflow-x: auto;
  }

  .table-row {
    min-width: 700px;
  }
}
</style>

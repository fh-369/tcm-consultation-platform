<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { getDashboardSummary } from '../../api/content'

const loading = ref(false)
const summary = ref({ statusDistribution: [], urgencyDistribution: [], trendLast6Months: [] })
const total = computed(() => summary.value.statusDistribution.reduce((sum, item) => sum + Number(item.value || 0), 0))
const maxTrend = computed(() => Math.max(...summary.value.trendLast6Months.map((item) => Number(item.value || 0)), 1))

async function loadDashboard() {
  loading.value = true
  try {
    summary.value = await getDashboardSummary()
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
        <p>问诊数据实时汇总</p>
        <h1>运营概览</h1>
      </div>
      <RouterLink to="/admin/export">导出问诊数据</RouterLink>
    </div>

    <div class="metrics">
      <article>
        <span>问诊总量</span>
        <strong>{{ total }}</strong>
      </article>
      <article v-for="item in summary.statusDistribution" :key="item.label">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </article>
    </div>

    <div class="dashboard-grid">
    <section class="priority-list">
      <header>
        <div><p>状态构成</p><h2>问诊状态分布</h2></div>
      </header>
      <div class="distribution">
        <div v-for="item in summary.statusDistribution" :key="item.label">
          <span>{{ item.label }}</span><strong>{{ item.value }}</strong>
          <i :style="{ width: `${total ? Number(item.value) / total * 100 : 0}%` }"></i>
        </div>
      </div>
    </section>
    <section class="priority-list">
      <header><div><p>风险观察</p><h2>紧急程度分布</h2></div></header>
      <div class="distribution urgency"><div v-for="item in summary.urgencyDistribution" :key="item.label"><span>{{ item.label }}</span><strong>{{ item.value }}</strong><i :style="{ width: `${total ? Number(item.value) / total * 100 : 0}%` }"></i></div></div>
    </section>
    </div>
    <section class="priority-list trend">
      <header><div><p>最近六个月</p><h2>问诊趋势</h2></div></header>
      <div class="trend-chart"><div v-for="item in summary.trendLast6Months" :key="item.label"><strong>{{ item.value }}</strong><i :style="{ height: `${Math.max(Number(item.value) / maxTrend * 150, 4)}px` }"></i><span>{{ item.label }}</span></div></div>
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

.dashboard-heading > a {
  color: var(--color-cinnabar);
  font-size: 12px;
  font-weight: 800;
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

.dashboard-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
.distribution { display: grid; gap: 15px; margin-top: 24px; }
.distribution div { display: grid; grid-template-columns: 1fr auto; gap: 7px; color: var(--color-text-muted); font-size: 12px; }
.distribution strong { color: var(--color-ink); }
.distribution i { display: block; height: 7px; grid-column: 1 / -1; border-radius: 99px; background: var(--color-jade); }
.distribution.urgency i { background: var(--color-cinnabar); }
.trend-chart { display: flex; min-height: 210px; align-items: end; gap: 18px; margin-top: 18px; }
.trend-chart div { display: grid; flex: 1; justify-items: center; gap: 7px; color: var(--color-text-muted); font-size: 10px; }
.trend-chart i { width: min(44px, 80%); border-radius: 5px 5px 0 0; background: linear-gradient(var(--color-jade), var(--color-ink)); }
.trend-chart strong { font-size: 11px; }

@media (max-width: 900px) {
  .metrics {
    grid-template-columns: repeat(2, 1fr);
  }
  .dashboard-grid { grid-template-columns: 1fr; }
}
</style>

<script setup>
const metrics = [
  { label: '待接诊', value: 18 },
  { label: '紧急提醒', value: 3, urgent: true },
  { label: '接诊中', value: 7 },
  { label: '今日新增', value: 12 },
]

const priorityItems = [
  { symptom: '胸痛伴呼吸困难', patient: '王女士', time: '5 分钟前', level: '紧急' },
  { symptom: '持续失眠三周', patient: '李先生', time: '21 分钟前', level: '关注' },
  { symptom: '脾胃不适伴胃痛', patient: '陈先生', time: '36 分钟前', level: '关注' },
]
</script>

<template>
  <section class="dashboard">
    <div class="dashboard-heading">
      <div>
        <p>静态布局预览</p>
        <h1>今日问诊</h1>
      </div>
      <span>真实数据将在后续后台问诊阶段接入</span>
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
          <p>按提醒等级与提交时间排序</p>
          <h2>优先处理</h2>
        </div>
        <RouterLink to="/admin/consultations">查看问诊管理</RouterLink>
      </header>

      <div class="table" role="table" aria-label="静态优先问诊列表">
        <div class="table-row table-head" role="row">
          <span>主要症状</span>
          <span>患者</span>
          <span>提交时间</span>
          <span>提醒</span>
        </div>
        <div v-for="item in priorityItems" :key="item.symptom" class="table-row" role="row">
          <strong>{{ item.symptom }}</strong>
          <span>{{ item.patient }}</span>
          <span>{{ item.time }}</span>
          <span :class="['level', { urgent: item.level === '紧急' }]">{{ item.level }}</span>
        </div>
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
  grid-template-columns: 1.5fr 0.7fr 0.8fr 0.5fr;
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

.level.urgent {
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
    min-width: 620px;
  }
}

@media (max-width: 560px) {
  .dashboard-heading,
  .priority-list header {
    display: block;
  }

  .dashboard-heading > span {
    display: block;
    margin-top: 8px;
  }
}
</style>

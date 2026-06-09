<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { getMyConsultations } from '../../api/consultation'
import {
  formatConsultationTime,
  reminderDisplay,
  statusDisplay,
  urgencyDisplay,
} from '../../features/consultation/display'

const loading = ref(false)
const consultations = ref([])
const total = ref(0)
const filters = reactive({
  current: 1,
  size: 6,
  status: '',
  urgency: '',
})

function errorMessage(error) {
  return error.response?.data?.message || error.message || '问诊记录加载失败，请稍后重试'
}

async function loadConsultations() {
  loading.value = true
  try {
    const page = await getMyConsultations(filters)
    consultations.value = page.records || []
    total.value = page.total || 0
  } catch (error) {
    ElMessage.error(errorMessage(error))
  } finally {
    loading.value = false
  }
}

function applyFilters() {
  filters.current = 1
  loadConsultations()
}

function changePage(page) {
  filters.current = page
  loadConsultations()
}

onMounted(loadConsultations)
</script>

<template>
  <section class="records-page page-container">
    <header class="records-heading">
      <div>
        <p>持续跟进身体变化</p>
        <h1>我的问诊</h1>
        <span>查看问诊状态、提醒信息与医生备注。</span>
      </div>
      <RouterLink class="primary-link" to="/consultation/new">建立新问诊单</RouterLink>
    </header>

    <section class="filters" aria-label="问诊记录筛选">
      <el-select v-model="filters.status" clearable placeholder="全部状态" @change="applyFilters">
        <el-option label="待接诊" value="待接诊" />
        <el-option label="接诊中" value="接诊中" />
        <el-option label="已完成" value="已完成" />
      </el-select>
      <el-select v-model="filters.urgency" clearable placeholder="全部紧急程度" @change="applyFilters">
        <el-option label="普通" value="普通" />
        <el-option label="紧急" value="紧急" />
        <el-option label="非常紧急" value="非常紧急" />
      </el-select>
      <span>共 {{ total }} 条问诊记录</span>
    </section>

    <div v-loading="loading" class="records">
      <article v-for="item in consultations" :key="item.id" class="record-card">
        <header>
          <div>
            <span :class="['tag', `tag-${statusDisplay(item.status).tone}`]">
              {{ statusDisplay(item.status).label }}
            </span>
            <span :class="['tag', `tag-${urgencyDisplay(item.urgency).tone}`]">
              {{ urgencyDisplay(item.urgency).label }}
            </span>
          </div>
          <time>{{ formatConsultationTime(item.createdAt) }}</time>
        </header>

        <h2>{{ item.symptoms }}</h2>
        <dl>
          <div>
            <dt>患者</dt>
            <dd>{{ item.patientName }}</dd>
          </div>
          <div>
            <dt>持续时间</dt>
            <dd>{{ item.duration || '未填写' }}</dd>
          </div>
          <div>
            <dt>提醒</dt>
            <dd :class="`text-${reminderDisplay(item.reminderLevel).tone}`">
              {{ reminderDisplay(item.reminderLevel).label }}
            </dd>
          </div>
        </dl>

        <section v-if="item.reminderText" class="note reminder-note">
          <strong>系统提醒</strong>
          <p>{{ item.reminderText }}</p>
        </section>
        <section v-if="item.doctorNote" class="note doctor-note">
          <strong>医生备注</strong>
          <p>{{ item.doctorNote }}</p>
        </section>
      </article>

      <el-empty
        v-if="!loading && consultations.length === 0"
        description="暂时没有符合条件的问诊记录"
      >
        <RouterLink class="primary-link" to="/consultation/new">建立第一张问诊单</RouterLink>
      </el-empty>
    </div>

    <el-pagination
      v-if="total > filters.size"
      background
      layout="prev, pager, next"
      :current-page="filters.current"
      :page-size="filters.size"
      :total="total"
      @current-change="changePage"
    />
  </section>
</template>

<style scoped>
.records-page {
  padding-top: 42px;
}

.records-heading,
.filters,
.record-card header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
}

.records-heading p {
  margin: 0;
  color: var(--color-cinnabar);
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.16em;
}

.records-heading h1 {
  margin: 10px 0;
  color: var(--color-ink);
  font-size: clamp(2.2rem, 5vw, 3.8rem);
  letter-spacing: -0.06em;
}

.records-heading span,
.filters > span,
.record-card time {
  color: var(--color-text-muted);
  font-size: 12px;
}

.filters {
  justify-content: start;
  margin-top: 30px;
  padding: 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: rgb(255 255 255 / 75%);
}

.filters .el-select {
  width: 180px;
}

.filters > span {
  margin-left: auto;
}

.records {
  display: grid;
  min-height: 260px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.record-card {
  padding: 24px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: white;
  box-shadow: var(--shadow-card);
}

.record-card header > div {
  display: flex;
  gap: 6px;
}

.tag {
  display: inline-flex;
  padding: 5px 8px;
  border-radius: 5px;
  background: var(--color-jade-light);
  color: var(--color-ink);
  font-size: 10px;
  font-weight: 800;
}

.tag-active,
.tag-attention {
  background: #fff0d6;
  color: #8a5c0f;
}

.tag-urgent {
  background: var(--color-cinnabar-soft);
  color: #9f3f2e;
}

.tag-complete {
  background: #e5eee9;
  color: var(--color-text-muted);
}

.record-card h2 {
  margin: 18px 0;
  color: var(--color-ink);
  font-size: 18px;
  line-height: 1.6;
}

dl {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin: 0;
}

dt {
  color: var(--color-text-muted);
  font-size: 10px;
}

dd {
  margin: 5px 0 0;
  font-size: 12px;
  font-weight: 700;
}

.text-attention,
.text-urgent {
  color: var(--color-cinnabar);
}

.note {
  margin-top: 18px;
  padding: 12px 14px;
  border-left: 3px solid var(--color-jade);
  background: var(--color-mist);
}

.doctor-note {
  border-color: var(--color-cinnabar);
  background: #fff8f6;
}

.note strong {
  font-size: 11px;
}

.note p {
  margin: 6px 0 0;
  color: var(--color-text-muted);
  font-size: 12px;
  line-height: 1.7;
}

.el-pagination {
  justify-content: center;
  margin-top: 26px;
}

@media (max-width: 850px) {
  .records {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 620px) {
  .records-heading,
  .filters {
    display: grid;
    justify-content: stretch;
  }

  .filters .el-select {
    width: 100%;
  }

  .filters > span {
    margin-left: 0;
  }

  dl {
    grid-template-columns: 1fr;
  }
}
</style>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

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
    <section class="filters" aria-label="问诊记录筛选">
      <h1>我的问诊</h1>
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
      <RouterLink class="new-consultation-link" to="/consultation/new">
        <el-icon><Plus /></el-icon>
        新建问诊单
      </RouterLink>
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
  padding-top: 24px;
  padding-bottom: 40px;
}

.filters,
.record-card header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
}

.filters h1 {
  flex: 0 0 auto;
  margin: 0 12px 0 0;
  color: var(--color-ink);
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 24px;
  letter-spacing: 0.035em;
}

.filters > span,
.record-card time {
  color: var(--color-text-muted);
  font-size: 12px;
}

.filters {
  justify-content: start;
  min-height: 72px;
  padding: 12px 14px 12px 20px;
  border: 1px solid var(--color-border);
  border-radius: 20px;
  background:
    radial-gradient(circle at 100% 0%, rgb(255 255 255 / 88%), transparent 32%),
    rgb(247 251 248 / 82%);
  box-shadow:
    inset 0 1px 0 rgb(255 255 255 / 86%),
    0 12px 32px rgb(23 60 45 / 6%);
  backdrop-filter: blur(16px);
}

.filters .el-select {
  width: 170px;
}

.filters :deep(.el-select__wrapper) {
  min-height: 42px;
  border: 1px solid rgb(67 126 97 / 13%);
  border-radius: 12px;
  background: rgb(255 255 255 / 72%);
  box-shadow: inset 0 1px 2px rgb(30 80 57 / 3%);
}

.filters > span {
  margin-left: auto;
  white-space: nowrap;
  font-size: 13px;
}

.new-consultation-link {
  display: inline-flex;
  min-height: 44px;
  flex: 0 0 auto;
  align-items: center;
  gap: 7px;
  padding: 0 17px;
  border: 1px solid rgb(255 255 255 / 40%);
  border-radius: 999px;
  background: var(--color-ink);
  box-shadow: 0 10px 24px rgb(17 66 47 / 18%);
  color: white;
  font-size: 13px;
  font-weight: 800;
}

.new-consultation-link:hover {
  background: #236e50;
  box-shadow: 0 13px 28px rgb(17 66 47 / 23%);
  transform: translateY(-1px);
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
  .filters {
    display: grid;
    justify-content: stretch;
  }

  .filters h1 {
    margin-right: 0;
  }

  .filters .el-select {
    width: 100%;
  }

  .filters > span {
    margin-left: 0;
  }

  .new-consultation-link {
    justify-content: center;
  }

  dl {
    grid-template-columns: 1fr;
  }
}
</style>

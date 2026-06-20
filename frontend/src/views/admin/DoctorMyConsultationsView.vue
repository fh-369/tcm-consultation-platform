<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import {
  getMyDoctorConsultations,
  updateDoctorConsultation,
} from '../../api/doctorConsultation'
import {
  formatConsultationTime,
  reminderDisplay,
  statusDisplay,
  urgencyDisplay,
} from '../../features/consultation/display'

const loading = ref(false)
const saving = ref(false)
const drawerVisible = ref(false)
const consultations = ref([])
const selected = ref(null)
const total = ref(0)
const filters = reactive({
  current: 1,
  size: 10,
  status: '',
  urgency: '',
  keyword: '',
})
const updateForm = reactive({
  status: '',
  doctorNote: '',
})

function errorMessage(error, fallback) {
  return error.response?.data?.message || error.message || fallback
}

async function loadConsultations() {
  loading.value = true
  try {
    const page = await getMyDoctorConsultations({
      current: filters.current,
      size: filters.size,
      status: filters.status || undefined,
      urgency: filters.urgency || undefined,
      keyword: filters.keyword || undefined,
    })
    consultations.value = page.records || []
    total.value = page.total || 0
  } catch (error) {
    ElMessage.error(errorMessage(error, '我的问诊加载失败'))
  } finally {
    loading.value = false
  }
}

function search() {
  filters.current = 1
  loadConsultations()
}

function resetFilters() {
  Object.assign(filters, {
    current: 1,
    size: 10,
    status: '',
    urgency: '',
    keyword: '',
  })
  loadConsultations()
}

function openDetails(item) {
  selected.value = item
  updateForm.status = item.status
  updateForm.doctorNote = item.doctorNote || ''
  drawerVisible.value = true
}

async function saveUpdate() {
  saving.value = true
  try {
    const updated = await updateDoctorConsultation(selected.value.id, updateForm)
    selected.value = { ...selected.value, ...updated }
    ElMessage.success('问诊处理结果已保存')
    await loadConsultations()
  } catch (error) {
    ElMessage.error(errorMessage(error, '问诊更新失败'))
  } finally {
    saving.value = false
  }
}

onMounted(loadConsultations)
</script>

<template>
  <section class="doctor-page">
    <header class="page-heading">
      <div>
        <h1>我的问诊</h1>
        <p>集中处理已经认领或由管理员分配给你的问诊，不混入其他医生的记录。</p>
      </div>
      <div class="record-count">
        <strong>{{ total }}</strong>
        <small>张负责问诊</small>
      </div>
    </header>

    <section class="filters">
      <el-input
        v-model="filters.keyword"
        clearable
        placeholder="搜索患者姓名或主要症状"
        @keyup.enter="search"
      />
      <el-select v-model="filters.status" clearable placeholder="全部状态">
        <el-option label="待接诊" value="待接诊" />
        <el-option label="接诊中" value="接诊中" />
        <el-option label="已完成" value="已完成" />
      </el-select>
      <el-select v-model="filters.urgency" clearable placeholder="全部紧急程度">
        <el-option label="普通" value="普通" />
        <el-option label="紧急" value="紧急" />
        <el-option label="非常紧急" value="非常紧急" />
      </el-select>
      <el-button type="primary" @click="search">筛选</el-button>
      <el-button @click="resetFilters">重置</el-button>
    </section>

    <section class="table-card">
      <el-table v-loading="loading" :data="consultations" stripe>
        <el-table-column label="患者" min-width="110" prop="patientName" />
        <el-table-column label="主要症状" min-width="260" show-overflow-tooltip prop="symptoms" />
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <span :class="['status-tag', `status-${statusDisplay(row.status).tone}`]">
              {{ statusDisplay(row.status).label }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="紧急程度" min-width="110">
          <template #default="{ row }">
            <span :class="['status-tag', `status-${urgencyDisplay(row.urgency).tone}`]">
              {{ urgencyDisplay(row.urgency).label }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="问诊科室" min-width="130">
          <template #default="{ row }">
            <span class="department-chip">{{ row.departmentName || '综合咨询' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="提醒" min-width="110">
          <template #default="{ row }">{{ reminderDisplay(row.reminderLevel).label }}</template>
        </el-table-column>
        <el-table-column label="更新时间" min-width="170">
          <template #default="{ row }">
            {{ formatConsultationTime(row.updatedAt || row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="105">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetails(row)">查看处理</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="当前筛选范围内没有负责的问诊" />
        </template>
      </el-table>

      <el-pagination
        v-if="total > filters.size"
        background
        layout="prev, pager, next"
        :current-page="filters.current"
        :page-size="filters.size"
        :total="total"
        @current-change="(page) => { filters.current = page; loadConsultations() }"
      />
    </section>

    <el-drawer v-model="drawerVisible" size="min(580px, 94vw)" title="问诊处理详情">
      <div v-if="selected" class="details">
        <div class="detail-summary">
          <span :class="['status-tag', `status-${urgencyDisplay(selected.urgency).tone}`]">
            {{ urgencyDisplay(selected.urgency).label }}
          </span>
          <time>{{ formatConsultationTime(selected.createdAt) }}</time>
          <h2>{{ selected.symptoms }}</h2>
          <p>{{ selected.reminderText || '暂无系统提醒' }}</p>
        </div>

        <dl>
          <div><dt>患者姓名</dt><dd>{{ selected.patientName }}</dd></div>
          <div><dt>年龄 / 性别</dt><dd>{{ selected.age || '未填' }} / {{ selected.gender || '未填' }}</dd></div>
          <div><dt>问诊科室</dt><dd>{{ selected.departmentName || '综合咨询' }}</dd></div>
          <div><dt>持续时间</dt><dd>{{ selected.duration || '未填' }}</dd></div>
          <div><dt>过敏史</dt><dd>{{ selected.allergyHistory || '未填' }}</dd></div>
          <div><dt>患者备注</dt><dd>{{ selected.patientNote || '未填' }}</dd></div>
        </dl>

        <el-form label-position="top">
          <el-form-item label="处理状态">
            <el-select v-model="updateForm.status">
              <el-option label="待接诊" value="待接诊" />
              <el-option label="接诊中" value="接诊中" />
              <el-option label="已完成" value="已完成" />
            </el-select>
          </el-form-item>
          <el-form-item label="医生回复">
            <el-input
              v-model="updateForm.doctorNote"
              :rows="5"
              maxlength="2000"
              show-word-limit
              placeholder="填写患者可以看到的处理说明"
              type="textarea"
            />
          </el-form-item>
          <el-button type="primary" :loading="saving" @click="saveUpdate">保存处理结果</el-button>
        </el-form>
      </div>
    </el-drawer>
  </section>
</template>

<style scoped>
.doctor-page {
  display: grid;
  gap: 16px;
}

.page-heading {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 24px;
  padding: 26px 28px;
  border: 1px solid rgb(47 95 72 / 12%);
  border-radius: 26px;
  background:
    radial-gradient(circle at 90% 10%, rgb(221 237 227 / 92%), transparent 34%),
    white;
  box-shadow: 0 16px 38px rgb(21 56 42 / 7%);
}

.page-heading h1 {
  margin: 0 0 6px;
  color: var(--color-ink);
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 30px;
}

.page-heading p {
  margin: 0;
  color: var(--color-text-muted);
  font-size: 12px;
}

.record-count {
  display: grid;
  min-width: 116px;
  padding: 16px 20px;
  border-radius: 20px;
  background: var(--color-ink);
  color: white;
  text-align: right;
}

.record-count strong {
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 32px;
}

.record-count small {
  color: rgb(255 255 255 / 68%);
  font-size: 10px;
}

.filters {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px;
  border: 1px solid var(--color-border);
  border-radius: 18px;
  background: rgb(255 255 255 / 88%);
  box-shadow: 0 10px 28px rgb(21 56 42 / 5%);
}

.filters .el-input {
  width: 280px;
}

.filters .el-select {
  width: 160px;
}

.table-card {
  padding: 14px;
  border: 1px solid var(--color-border);
  border-radius: 20px;
  background: white;
  box-shadow: var(--shadow-card);
}

.status-tag,
.department-chip {
  display: inline-flex;
  padding: 6px 9px;
  border-radius: 999px;
  background: #e7f2eb;
  color: var(--color-ink);
  font-size: 10px;
  font-weight: 800;
}

.status-active,
.status-attention {
  background: #fff0d6;
  color: #8a5c0f;
}

.status-urgent {
  background: var(--color-cinnabar-soft);
  color: #9f3f2e;
}

.status-complete {
  background: #e5eee9;
  color: var(--color-text-muted);
}

.el-pagination {
  justify-content: center;
  margin-top: 18px;
}

.detail-summary {
  padding: 20px;
  border-radius: var(--radius-sm);
  background: var(--color-mist);
}

.detail-summary time {
  float: right;
  color: var(--color-text-muted);
  font-size: 11px;
}

.detail-summary h2 {
  margin: 18px 0 8px;
  color: var(--color-ink);
  font-size: 20px;
  line-height: 1.6;
}

.detail-summary p {
  margin: 0;
  color: var(--color-text-muted);
  font-size: 12px;
  line-height: 1.7;
}

dl {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin: 20px 0;
}

dl div {
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
}

dt {
  color: var(--color-text-muted);
  font-size: 10px;
}

dd {
  margin: 6px 0 0;
  font-size: 12px;
  line-height: 1.6;
}

@media (max-width: 900px) {
  .filters {
    flex-wrap: wrap;
  }
}
</style>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import {
  getAdminConsultations,
  updateAdminConsultation,
} from '../../api/adminConsultation'
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
    const page = await getAdminConsultations(filters)
    consultations.value = page.records || []
    total.value = page.total || 0
  } catch (error) {
    ElMessage.error(errorMessage(error, '问诊列表加载失败'))
  } finally {
    loading.value = false
  }
}

function search() {
  filters.current = 1
  loadConsultations()
}

function resetFilters() {
  Object.assign(filters, { current: 1, size: 10, status: '', urgency: '', keyword: '' })
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
    const updated = await updateAdminConsultation(selected.value.id, updateForm)
    selected.value = updated
    ElMessage.success('问诊状态和医生备注已更新')
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
  <section class="management-page">
    <header class="management-heading">
      <div>
        <p>状态、紧急程度与关键字筛选</p>
        <h1>问诊管理</h1>
      </div>
      <span>共 {{ total }} 张问诊单</span>
    </header>

    <section class="filters">
      <el-input
        v-model="filters.keyword"
        clearable
        placeholder="搜索患者姓名或症状"
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
        <el-table-column label="主要症状" min-width="240" show-overflow-tooltip prop="symptoms" />
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
        <el-table-column label="提醒" min-width="110">
          <template #default="{ row }">{{ reminderDisplay(row.reminderLevel).label }}</template>
        </el-table-column>
        <el-table-column label="提交时间" min-width="170">
          <template #default="{ row }">{{ formatConsultationTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="90">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetails(row)">查看处理</el-button>
          </template>
        </el-table-column>
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

    <el-drawer v-model="drawerVisible" size="min(560px, 94vw)" title="问诊详情">
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
          <div><dt>手机号</dt><dd>{{ selected.phone || '未填' }}</dd></div>
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
          <el-form-item label="医生备注">
            <el-input
              v-model="updateForm.doctorNote"
              :rows="5"
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
.management-heading,
.filters {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.management-heading p {
  margin: 0 0 5px;
  color: var(--color-cinnabar);
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.12em;
}

.management-heading h1 {
  margin: 0;
  color: var(--color-ink);
  font-size: 30px;
}

.management-heading > span {
  color: var(--color-text-muted);
  font-size: 12px;
}

.filters {
  justify-content: start;
  margin-top: 22px;
  padding: 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: white;
}

.filters .el-input {
  width: 260px;
}

.filters .el-select {
  width: 160px;
}

.table-card {
  margin-top: 14px;
  padding: 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: white;
  box-shadow: var(--shadow-card);
}

.status-tag {
  display: inline-flex;
  padding: 5px 8px;
  border-radius: 5px;
  background: var(--color-jade-light);
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

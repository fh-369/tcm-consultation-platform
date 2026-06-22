<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import {
  claimDoctorConsultation,
  getDepartmentPool,
} from '../../api/doctorConsultation'
import {
  formatConsultationTime,
  urgencyDisplay,
} from '../../features/consultation/display'
import { getApiErrorMessage as errorMessage } from '../../features/feedback'

const loading = ref(false)
const claimingId = ref(null)
const consultations = ref([])
const total = ref(0)
const filters = reactive({
  current: 1,
  size: 10,
  scope: 'all',
  urgency: '',
  keyword: '',
})

async function loadConsultations() {
  loading.value = true
  try {
    const page = await getDepartmentPool({
      current: filters.current,
      size: filters.size,
      scope: filters.scope,
      urgency: filters.urgency || undefined,
      keyword: filters.keyword || undefined,
    })
    consultations.value = page.records || []
    total.value = page.total || 0
  } catch (error) {
    ElMessage.error(errorMessage(error, '科室问诊池加载失败'))
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
    scope: 'all',
    urgency: '',
    keyword: '',
  })
  loadConsultations()
}

async function claim(item) {
  claimingId.value = item.id
  try {
    await claimDoctorConsultation(item.id)
    ElMessage.success('认领成功')
    await loadConsultations()
  } catch (error) {
    ElMessage.error(errorMessage(error, '问诊认领失败'))
  } finally {
    claimingId.value = null
  }
}

onMounted(loadConsultations)
</script>

<template>
  <section class="doctor-page">
    <header class="page-heading">
      <div>
        <h1>科室问诊池</h1>
        <p>查看本科室及综合咨询中的待接诊记录，认领后统一进入“我的问诊”处理。</p>
      </div>
      <div class="pool-count">
        <strong>{{ total }}</strong>
        <small>张等待认领</small>
      </div>
    </header>

    <section class="filters">
      <el-segmented
        v-model="filters.scope"
        :options="[
          { label: '全部可认领', value: 'all' },
          { label: '本科室', value: 'department' },
          { label: '综合咨询', value: 'general' },
        ]"
        @change="search"
      />
      <el-input
        v-model="filters.keyword"
        clearable
        placeholder="搜索患者姓名或主要症状"
        @keyup.enter="search"
      />
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
        <el-table-column label="主要症状" min-width="280" show-overflow-tooltip prop="symptoms" />
        <el-table-column label="问诊科室" min-width="130">
          <template #default="{ row }">
            <span class="department-chip">{{ row.departmentName || '综合咨询' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="紧急程度" min-width="110">
          <template #default="{ row }">
            <span :class="['status-tag', `status-${urgencyDisplay(row.urgency).tone}`]">
              {{ urgencyDisplay(row.urgency).label }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="持续时间" min-width="110">
          <template #default="{ row }">{{ row.duration || '未填写' }}</template>
        </el-table-column>
        <el-table-column label="提交时间" min-width="170">
          <template #default="{ row }">{{ formatConsultationTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="116">
          <template #default="{ row }">
            <el-button
              link
              type="success"
              :loading="claimingId === row.id"
              @click="claim(row)"
            >
              认领问诊
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="当前筛选范围内没有待认领问诊" />
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
  border-radius: 26px;
  background:
    radial-gradient(circle at 85% 15%, rgb(152 202 174 / 26%), transparent 30%),
    linear-gradient(135deg, #0f4c36, #1d684b);
  color: white;
  box-shadow: 0 18px 42px rgb(18 65 47 / 14%);
}

.page-heading h1 {
  margin: 0 0 6px;
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 30px;
}

.page-heading p {
  margin: 0;
  color: rgb(255 255 255 / 72%);
  font-size: 12px;
}

.pool-count {
  display: grid;
  min-width: 116px;
  padding: 16px 20px;
  border: 1px solid rgb(255 255 255 / 18%);
  border-radius: 20px;
  background: rgb(255 255 255 / 10%);
  text-align: right;
  backdrop-filter: blur(12px);
}

.pool-count strong {
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 32px;
}

.pool-count small {
  color: rgb(255 255 255 / 66%);
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
  width: 250px;
  margin-left: auto;
}

.filters .el-select {
  width: 150px;
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
  font-size: 10px;
  font-weight: 800;
}

.status-tag,
.department-chip {
  background: #e7f2eb;
  color: var(--color-ink);
}

.status-attention {
  background: #fff0d6;
  color: #8a5c0f;
}

.status-urgent {
  background: var(--color-cinnabar-soft);
  color: #9f3f2e;
}

.el-pagination {
  justify-content: center;
  margin-top: 18px;
}

@media (max-width: 1050px) {
  .filters {
    flex-wrap: wrap;
  }

  .filters .el-input {
    margin-left: 0;
  }
}
</style>

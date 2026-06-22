<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import {
  countExportConsultations,
  exportConsultations,
} from '../../api/content'
import { getDepartments } from '../../api/auth'
import { getPersonnel } from '../../api/personnel'
import { getApiErrorMessage } from '../../features/feedback'

const counting = ref(false)
const downloading = ref(false)
const optionsLoading = ref(false)
const matchedCount = ref(null)
const countError = ref('')
const dateRange = ref([])
const departments = ref([])
const doctors = ref([])
const filters = reactive({
  status: '',
  urgency: '',
  departmentId: '',
  doctorId: '',
})

function buildParams() {
  const [dateFrom, dateTo] = dateRange.value || []
  return Object.fromEntries(
    Object.entries({
      dateFrom,
      dateTo,
      ...filters,
    }).filter(([, value]) => value !== '' && value != null),
  )
}

async function loadOptions() {
  optionsLoading.value = true
  try {
    const [departmentRecords, doctorPage] = await Promise.all([
      getDepartments(),
      getPersonnel('doctors', {
        current: 1,
        size: 100,
        approvalStatus: 'APPROVED',
      }),
    ])
    departments.value = departmentRecords || []
    doctors.value = (doctorPage.records || []).filter((doctor) => doctor.enabled)
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error, '筛选选项加载失败'))
  } finally {
    optionsLoading.value = false
  }
}

async function refreshCount() {
  counting.value = true
  countError.value = ''
  try {
    matchedCount.value = Number(await countExportConsultations(buildParams()))
  } catch (error) {
    matchedCount.value = null
    countError.value = getApiErrorMessage(error, '导出数量查询失败')
    ElMessage.error(countError.value)
  } finally {
    counting.value = false
  }
}

async function download() {
  if (!matchedCount.value) {
    ElMessage.warning('当前筛选条件下没有可导出的问诊记录')
    return
  }

  downloading.value = true
  try {
    const params = buildParams()
    const blob = await exportConsultations(params)
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `consultations-${params.dateFrom || 'all'}-to-${params.dateTo || 'all'}.csv`
    link.click()
    URL.revokeObjectURL(url)
    ElMessage.success(`已导出 ${matchedCount.value} 条问诊记录`)
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error, '导出失败'))
  } finally {
    downloading.value = false
  }
}

function reset() {
  dateRange.value = []
  Object.assign(filters, {
    status: '',
    urgency: '',
    departmentId: '',
    doctorId: '',
  })
  refreshCount()
}

onMounted(async () => {
  await loadOptions()
  await refreshCount()
})
</script>

<template>
  <section class="export-page">
    <header class="export-heading">
      <div>
        <h1>问诊数据导出</h1>
        <p>按业务条件筛选并导出 UTF-8 CSV，文件可直接使用 Excel 打开。</p>
      </div>
      <div class="format-badge">
        <strong>CSV</strong>
        <span>UTF-8</span>
      </div>
    </header>

    <div class="export-layout">
      <section v-loading="optionsLoading" class="filter-card">
        <header>
          <span>01</span>
          <div>
            <h2>选择数据范围</h2>
            <p>日期范围包含结束日期当天，筛选条件可以组合使用。</p>
          </div>
        </header>

        <el-form label-position="top">
          <el-form-item label="问诊创建日期">
            <el-date-picker
              v-model="dateRange"
              end-placeholder="结束日期"
              range-separator="至"
              start-placeholder="开始日期"
              type="daterange"
              unlink-panels
              value-format="YYYY-MM-DD"
            />
          </el-form-item>

          <div class="field-grid">
            <el-form-item label="问诊状态">
              <el-select v-model="filters.status" clearable placeholder="全部状态">
                <el-option label="待接诊" value="待接诊" />
                <el-option label="接诊中" value="接诊中" />
                <el-option label="已完成" value="已完成" />
              </el-select>
            </el-form-item>
            <el-form-item label="紧急程度">
              <el-select v-model="filters.urgency" clearable placeholder="全部紧急程度">
                <el-option label="普通" value="普通" />
                <el-option label="紧急" value="紧急" />
                <el-option label="非常紧急" value="非常紧急" />
              </el-select>
            </el-form-item>
            <el-form-item label="问诊科室">
              <el-select v-model="filters.departmentId" clearable filterable placeholder="全部科室">
                <el-option
                  v-for="department in departments"
                  :key="department.id"
                  :label="department.name"
                  :value="department.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="接诊医生">
              <el-select v-model="filters.doctorId" clearable filterable placeholder="全部医生">
                <el-option
                  v-for="doctor in doctors"
                  :key="doctor.userId"
                  :label="`${doctor.displayName || doctor.username} · ${doctor.department || '未设置科室'}`"
                  :value="doctor.userId"
                />
              </el-select>
            </el-form-item>
          </div>
        </el-form>

        <div class="filter-actions">
          <el-button @click="reset">重置条件</el-button>
          <el-button type="primary" :loading="counting" @click="refreshCount">
            查询匹配数量
          </el-button>
        </div>
      </section>

      <aside class="export-summary">
        <div class="summary-orbit" aria-hidden="true"></div>
        <span>当前筛选结果</span>
        <strong v-if="matchedCount !== null">{{ matchedCount }}</strong>
        <strong v-else>--</strong>
        <p>{{ countError || '条问诊记录可以导出' }}</p>

        <ul>
          <li>包含患者信息、症状和处理状态</li>
          <li>包含问诊科室、接诊医生与分配时间</li>
          <li>仅系统管理员可以执行数据导出</li>
        </ul>

        <el-button
          class="download-button"
          type="primary"
          :disabled="matchedCount === null || matchedCount === 0"
          :loading="downloading"
          @click="download"
        >
          {{ matchedCount ? `导出 ${matchedCount} 条记录` : '暂无可导出记录' }}
        </el-button>
      </aside>
    </div>
  </section>
</template>

<style scoped>
.export-page {
  max-width: 1320px;
  margin: 0 auto;
}

.export-heading {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 24px;
}

.export-heading h1 {
  margin: 0;
  color: var(--color-ink);
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 32px;
  letter-spacing: -.04em;
}

.export-heading p {
  margin: 8px 0 0;
  color: var(--color-text-muted);
  font-size: 12px;
}

.format-badge {
  display: flex;
  align-items: baseline;
  gap: 8px;
  padding: 11px 16px;
  border: 1px solid rgb(47 95 72 / 12%);
  border-radius: 16px;
  background: rgb(255 255 255 / 80%);
}

.format-badge strong {
  color: var(--color-ink);
  font-size: 18px;
}

.format-badge span {
  color: var(--color-text-muted);
  font-size: 9px;
}

.export-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.45fr) minmax(300px, .65fr);
  gap: 16px;
  margin-top: 22px;
}

.filter-card,
.export-summary {
  border: 1px solid rgb(47 95 72 / 11%);
  border-radius: 24px;
  background: rgb(255 255 255 / 90%);
  box-shadow: 0 16px 36px rgb(21 56 42 / 6%);
}

.filter-card {
  padding: 24px;
}

.filter-card > header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.filter-card > header > span {
  display: grid;
  width: 38px;
  height: 38px;
  border-radius: 12px;
  background: #e7f1eb;
  color: var(--color-ink);
  font-size: 10px;
  font-weight: 900;
  place-items: center;
}

.filter-card h2 {
  margin: 0;
  color: var(--color-ink);
  font-size: 18px;
}

.filter-card header p {
  margin: 5px 0 0;
  color: var(--color-text-muted);
  font-size: 10px;
}

.field-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 14px;
}

:deep(.el-date-editor),
:deep(.el-select) {
  width: 100%;
}

:deep(.el-form-item__label) {
  color: #486256;
  font-size: 11px;
  font-weight: 800;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 8px;
  border-top: 1px solid rgb(47 95 72 / 9%);
}

.export-summary {
  position: relative;
  min-height: 500px;
  padding: 34px 30px;
  overflow: hidden;
  background:
    radial-gradient(circle at 82% 8%, rgb(134 188 159 / 28%), transparent 28%),
    linear-gradient(155deg, #0f4938, #183f31);
  color: white;
}

.summary-orbit {
  position: absolute;
  top: -70px;
  right: -70px;
  width: 210px;
  height: 210px;
  border: 1px solid rgb(255 255 255 / 12%);
  border-radius: 50%;
  box-shadow:
    0 0 0 30px rgb(255 255 255 / 3%),
    0 0 0 65px rgb(255 255 255 / 2%);
}

.export-summary > span {
  color: #f1b4a8;
  font-size: 10px;
  font-weight: 900;
  letter-spacing: .12em;
}

.export-summary > strong {
  display: block;
  margin-top: 26px;
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 76px;
  line-height: 1;
}

.export-summary > p {
  margin: 10px 0 0;
  color: rgb(255 255 255 / 68%);
  font-size: 12px;
}

.export-summary ul {
  display: grid;
  gap: 13px;
  margin: 48px 0 30px;
  padding: 0;
  color: rgb(255 255 255 / 72%);
  font-size: 11px;
  line-height: 1.7;
  list-style: none;
}

.export-summary li {
  position: relative;
  padding-left: 18px;
}

.export-summary li::before {
  position: absolute;
  top: .65em;
  left: 0;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #82b99a;
  content: "";
}

.download-button {
  width: 100%;
  min-height: 46px;
}

@media (max-width: 900px) {
  .export-layout {
    grid-template-columns: 1fr;
  }

  .export-summary {
    min-height: auto;
  }
}
</style>

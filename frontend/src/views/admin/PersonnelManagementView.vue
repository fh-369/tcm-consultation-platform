<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import { getPersonnel, updateAccountEnabled } from '../../api/personnel'

const props = defineProps({
  resource: {
    type: String,
    required: true,
    validator: (value) => ['users', 'doctors'].includes(value),
  },
})

const loading = ref(false)
const updatingId = ref(null)
const records = ref([])
const total = ref(0)
const filters = reactive({
  current: 1,
  size: 10,
  keyword: '',
})

const isDoctors = computed(() => props.resource === 'doctors')
const pageCopy = computed(() => (
  isDoctors.value
    ? {
        title: '医生管理',
        description: '查看医生账号、所属科室与当前使用状态。',
        search: '搜索用户名、医生姓名或科室',
        empty: '暂无医生账号',
      }
    : {
        title: '用户管理',
        description: '查看平台注册用户、联系方式与账号使用状态。',
        search: '搜索用户名、昵称或手机号',
        empty: '暂无注册用户',
      }
))

function errorMessage(error, fallback) {
  return error.response?.data?.message || error.message || fallback
}

function formatTime(value) {
  if (!value) return '暂无'
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value))
}

async function loadPersonnel() {
  loading.value = true
  try {
    const page = await getPersonnel(props.resource, filters)
    records.value = page.records || []
    total.value = Number(page.total || 0)
  } catch (error) {
    ElMessage.error(errorMessage(error, '人员列表加载失败'))
  } finally {
    loading.value = false
  }
}

function search() {
  filters.current = 1
  loadPersonnel()
}

function resetFilters() {
  filters.current = 1
  filters.keyword = ''
  loadPersonnel()
}

async function changeStatus(row, enabled) {
  const action = enabled ? '启用' : '停用'
  try {
    await ElMessageBox.confirm(
      `${action}账号“${row.displayName || row.username}”后，${enabled ? '该账号可以正常登录平台。' : '该账号现有登录状态也将失效。'}`,
      `${action}账号`,
      {
        confirmButtonText: `确认${action}`,
        cancelButtonText: '取消',
        type: enabled ? 'success' : 'warning',
      },
    )
  } catch {
    return
  }

  updatingId.value = row.id
  try {
    await updateAccountEnabled(row.id, enabled)
    row.enabled = enabled
    ElMessage.success(`账号已${action}`)
  } catch (error) {
    ElMessage.error(errorMessage(error, `${action}账号失败`))
  } finally {
    updatingId.value = null
  }
}

watch(
  () => props.resource,
  () => {
    Object.assign(filters, { current: 1, size: 10, keyword: '' })
    loadPersonnel()
  },
)

onMounted(loadPersonnel)
</script>

<template>
  <section class="personnel-page">
    <header class="personnel-heading">
      <div>
        <h1>{{ pageCopy.title }}</h1>
        <p>{{ pageCopy.description }}</p>
      </div>
      <div class="total-badge">
        <strong>{{ total }}</strong>
        <span>{{ isDoctors ? '名医生' : '位用户' }}</span>
      </div>
    </header>

    <section class="personnel-toolbar">
      <el-input
        v-model="filters.keyword"
        clearable
        :placeholder="pageCopy.search"
        @clear="search"
        @keyup.enter="search"
      />
      <el-button type="primary" @click="search">搜索</el-button>
      <el-button @click="resetFilters">重置</el-button>
    </section>

    <section class="personnel-table-card">
      <el-table v-loading="loading" :data="records" row-key="id">
        <el-table-column label="账号" min-width="190">
          <template #default="{ row }">
            <div class="identity-cell">
              <span>{{ (row.displayName || row.username || '?').slice(0, 1) }}</span>
              <div>
                <strong>{{ row.displayName || row.username }}</strong>
                <small>@{{ row.username }}</small>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column v-if="isDoctors" label="科室" min-width="150">
          <template #default="{ row }">{{ row.department || '暂未设置' }}</template>
        </el-table-column>
        <el-table-column v-else label="手机号" min-width="150">
          <template #default="{ row }">{{ row.phone || '暂未填写' }}</template>
        </el-table-column>

        <el-table-column label="注册时间" min-width="180">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>

        <el-table-column label="账号状态" min-width="130">
          <template #default="{ row }">
            <span :class="['account-status', { disabled: !row.enabled }]">
              <i></i>
              {{ row.enabled ? '正常使用' : '已停用' }}
            </span>
          </template>
        </el-table-column>

        <el-table-column align="right" label="操作" width="130">
          <template #default="{ row }">
            <el-button
              link
              :loading="updatingId === row.id"
              :type="row.enabled ? 'danger' : 'success'"
              @click="changeStatus(row, !row.enabled)"
            >
              {{ row.enabled ? '停用账号' : '恢复账号' }}
            </el-button>
          </template>
        </el-table-column>

        <template #empty>
          <div class="empty-state">
            <strong>{{ pageCopy.empty }}</strong>
            <span>可以尝试调整搜索关键词后重新查询。</span>
          </div>
        </template>
      </el-table>

      <el-pagination
        v-if="total > filters.size"
        background
        layout="prev, pager, next"
        :current-page="filters.current"
        :page-size="filters.size"
        :total="total"
        @current-change="(page) => { filters.current = page; loadPersonnel() }"
      />
    </section>
  </section>
</template>

<style scoped>
.personnel-page {
  max-width: 1480px;
  margin: 0 auto;
}

.personnel-heading {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 24px;
}

.personnel-heading h1 {
  margin: 0;
  color: var(--color-ink);
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 32px;
  letter-spacing: -.04em;
}

.personnel-heading p {
  margin: 8px 0 0;
  color: var(--color-text-muted);
  font-size: 12px;
}

.total-badge {
  display: flex;
  min-width: 112px;
  align-items: baseline;
  justify-content: center;
  gap: 6px;
  padding: 12px 18px;
  border: 1px solid rgb(47 95 72 / 12%);
  border-radius: 18px;
  background: rgb(255 255 255 / 82%);
}

.total-badge strong {
  color: var(--color-ink);
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 24px;
}

.total-badge span {
  color: var(--color-text-muted);
  font-size: 10px;
}

.personnel-toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 22px;
  padding: 14px;
  border: 1px solid rgb(47 95 72 / 10%);
  border-radius: 18px;
  background: rgb(255 255 255 / 82%);
  box-shadow: 0 10px 28px rgb(21 56 42 / 4%);
}

.personnel-toolbar .el-input {
  width: min(420px, 100%);
}

.personnel-table-card {
  margin-top: 14px;
  padding: 10px 14px 16px;
  border: 1px solid rgb(47 95 72 / 10%);
  border-radius: 22px;
  background: rgb(255 255 255 / 90%);
  box-shadow: 0 14px 34px rgb(21 56 42 / 5%);
}

.identity-cell {
  display: flex;
  align-items: center;
  gap: 11px;
}

.identity-cell > span {
  display: grid;
  width: 38px;
  height: 38px;
  flex: 0 0 auto;
  border-radius: 12px;
  background: #e5f0e9;
  color: var(--color-ink);
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 15px;
  font-weight: 900;
  place-items: center;
}

.identity-cell strong,
.identity-cell small {
  display: block;
}

.identity-cell strong {
  color: var(--color-ink);
  font-size: 13px;
}

.identity-cell small {
  margin-top: 4px;
  color: var(--color-text-muted);
  font-size: 10px;
}

.account-status {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 6px 10px;
  border-radius: 999px;
  background: #e7f2eb;
  color: #256343;
  font-size: 10px;
  font-weight: 800;
}

.account-status i {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #3b8a60;
}

.account-status.disabled {
  background: #f4e8e5;
  color: #9b4738;
}

.account-status.disabled i {
  background: var(--color-cinnabar);
}

.empty-state {
  display: grid;
  min-height: 180px;
  place-content: center;
  text-align: center;
}

.empty-state strong {
  color: var(--color-ink);
  font-size: 16px;
}

.empty-state span {
  margin-top: 8px;
  color: var(--color-text-muted);
  font-size: 11px;
}

.el-pagination {
  justify-content: center;
  margin-top: 18px;
}

@media (max-width: 760px) {
  .personnel-heading,
  .personnel-toolbar {
    align-items: stretch;
    flex-direction: column;
  }

  .total-badge {
    align-self: flex-start;
  }
}
</style>

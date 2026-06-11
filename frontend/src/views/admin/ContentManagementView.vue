<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import { createAdminContent, deleteAdminContent, getAdminContent, updateAdminContent } from '../../api/content'

const props = defineProps({ resource: { type: String, required: true } })
const configs = {
  knowledge: {
    title: '知识文章', singular: '文章', nameKey: 'title', filterKey: 'category', filterLabel: '分类',
    fields: [
      ['title', '标题'], ['category', '分类'], ['summary', '摘要'], ['content', '正文', 'textarea'],
      ['tips', '日常提示', 'textarea'], ['coverImageUrl', '封面图片地址'],
    ],
  },
  recipe: {
    title: '药膳管理', singular: '药膳', nameKey: 'name', filterKey: 'season', filterLabel: '季节',
    fields: [
      ['name', '名称'], ['season', '季节'], ['constitution', '体质'], ['suitableFor', '适合人群'],
      ['summary', '摘要'], ['ingredients', '食材', 'textarea'], ['steps', '步骤', 'textarea'], ['imageUrl', '图片地址'],
    ],
  },
}
const config = computed(() => configs[props.resource])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const records = ref([])
const total = ref(0)
const editingId = ref(null)
const filters = reactive({ current: 1, size: 10, keyword: '', published: '' })
const form = reactive({})

function resetForm() {
  Object.keys(form).forEach((key) => delete form[key])
  config.value.fields.forEach(([key]) => { form[key] = '' })
  form.published = false
  editingId.value = null
}
function openCreate() { resetForm(); dialogVisible.value = true }
function openEdit(row) { resetForm(); editingId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
async function load() {
  loading.value = true
  try {
    const page = await getAdminContent(props.resource, filters)
    records.value = page.records || []; total.value = page.total || 0
  } catch (error) { ElMessage.error(error.message || `${config.value.title}加载失败`) }
  finally { loading.value = false }
}
async function save() {
  if (!form[config.value.nameKey]) return ElMessage.warning(`请填写${config.value.fields[0][1]}`)
  saving.value = true
  try {
    if (editingId.value) await updateAdminContent(props.resource, editingId.value, form)
    else await createAdminContent(props.resource, form)
    ElMessage.success(`${config.value.singular}已保存`); dialogVisible.value = false; await load()
  } catch (error) { ElMessage.error(error.message || '保存失败') }
  finally { saving.value = false }
}
async function remove(row) {
  try {
    await ElMessageBox.confirm(`确定删除“${row[config.value.nameKey]}”吗？`, `删除${config.value.singular}`, { type: 'warning' })
    await deleteAdminContent(props.resource, row.id); ElMessage.success('已删除'); await load()
  } catch (error) { if (error !== 'cancel' && error !== 'close') ElMessage.error(error.message || '删除失败') }
}
watch(() => props.resource, () => { Object.assign(filters, { current: 1, keyword: '', published: '' }); load() })
onMounted(load)
</script>

<template>
  <section>
    <header class="heading"><div><p>创建、编辑、发布与删除</p><h1>{{ config.title }}</h1></div><el-button type="primary" @click="openCreate">新建{{ config.singular }}</el-button></header>
    <section class="filters">
      <el-input v-model="filters.keyword" clearable placeholder="搜索标题或名称" @keyup.enter="load" />
      <el-select v-model="filters.published" clearable placeholder="全部发布状态"><el-option label="已发布" :value="true" /><el-option label="未发布" :value="false" /></el-select>
      <el-button @click="load">筛选</el-button>
    </section>
    <section class="table-card">
      <el-table v-loading="loading" :data="records" stripe>
        <el-table-column :label="config.fields[0][1]" min-width="220" :prop="config.nameKey" />
        <el-table-column :label="config.filterLabel" min-width="110" :prop="config.filterKey" />
        <el-table-column label="发布状态" width="110"><template #default="{ row }"><el-tag :type="row.published ? 'success' : 'info'">{{ row.published ? '已发布' : '未发布' }}</el-tag></template></el-table-column>
        <el-table-column label="浏览量" width="90" prop="viewCount" />
        <el-table-column fixed="right" label="操作" width="145"><template #default="{ row }"><el-button link type="primary" @click="openEdit(row)">编辑</el-button><el-button link type="danger" @click="remove(row)">删除</el-button></template></el-table-column>
      </el-table>
      <el-pagination v-if="total > filters.size" background layout="prev, pager, next" :current-page="filters.current" :page-size="filters.size" :total="total" @current-change="(page) => { filters.current = page; load() }" />
    </section>
    <el-dialog v-model="dialogVisible" :title="`${editingId ? '编辑' : '新建'}${config.singular}`" width="min(720px, 94vw)">
      <el-form label-position="top">
        <el-form-item v-for="[key, label, type] in config.fields" :key="key" :label="label"><el-input v-model="form[key]" :rows="type === 'textarea' ? 4 : undefined" :type="type || 'text'" /></el-form-item>
        <el-form-item label="发布状态"><el-switch v-model="form.published" active-text="已发布" inactive-text="未发布" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存</el-button></template>
    </el-dialog>
  </section>
</template>

<style scoped>
.heading, .filters { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.heading p { margin: 0 0 5px; color: var(--color-cinnabar); font-size: 11px; font-weight: 800; letter-spacing: .12em; }
.heading h1 { margin: 0; font-size: 30px; }
.filters { justify-content: start; margin-top: 22px; padding: 16px; border: 1px solid var(--color-border); border-radius: var(--radius-sm); background: white; }
.filters .el-input { width: 260px; } .filters .el-select { width: 170px; }
.table-card { margin-top: 14px; padding: 14px; border: 1px solid var(--color-border); border-radius: var(--radius-sm); background: white; box-shadow: var(--shadow-card); }
.el-pagination { justify-content: center; margin-top: 18px; }
@media (max-width: 700px) { .filters { flex-wrap: wrap; } }
</style>

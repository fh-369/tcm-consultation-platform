<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

import { exportConsultations } from '../../api/content'

const downloading = ref(false)
async function download() {
  downloading.value = true
  try {
    const blob = await exportConsultations()
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url; link.download = `consultations-${new Date().toISOString().slice(0, 10)}.csv`; link.click()
    URL.revokeObjectURL(url); ElMessage.success('问诊 CSV 已下载')
  } catch (error) { ElMessage.error(error.message || '导出失败') }
  finally { downloading.value = false }
}
</script>

<template>
  <section class="export-page">
    <div><p>数据留存与分析</p><h1>问诊数据导出</h1><span>导出当前系统中的问诊记录，文件格式为 CSV，可使用 Excel 打开。</span></div>
    <article><span>CSV</span><h2>全部问诊记录</h2><p>包含问诊状态、紧急程度、患者信息、症状、备注与创建时间。</p><el-button type="primary" :loading="downloading" @click="download">下载 consultations.csv</el-button></article>
  </section>
</template>

<style scoped>
.export-page { display: grid; grid-template-columns: .8fr 1.2fr; gap: 18px; }
.export-page > div, article { min-height: 330px; padding: 36px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: white; box-shadow: var(--shadow-card); }
.export-page > div { background: var(--color-ink); color: white; }
.export-page p, .export-page span { color: var(--color-text-muted); line-height: 1.8; }
.export-page > div p, .export-page > div span { color: rgb(255 255 255 / 62%); }
h1 { margin: 22px 0; font-size: 40px; } article span { color: var(--color-cinnabar); font-size: 11px; font-weight: 800; } article h2 { margin-top: 45px; }
@media (max-width: 800px) { .export-page { grid-template-columns: 1fr; } }
</style>

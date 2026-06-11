<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

import { getPublishedKnowledgeDetail } from '../../api/content'

const route = useRoute()
const loading = ref(false)
const article = ref(null)

onMounted(async () => {
  loading.value = true
  try {
    article.value = await getPublishedKnowledgeDetail(route.params.id)
  } catch (error) {
    ElMessage.error(error.message || '文章加载失败')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <article v-loading="loading" class="detail-page page-container">
    <RouterLink to="/knowledge">← 返回养生知识</RouterLink>
    <div v-if="article" class="detail-card">
      <header>
        <span>{{ article.category || '养生常识' }}</span>
        <h1>{{ article.title }}</h1>
        <p>{{ article.summary }}</p>
      </header>
      <section class="article-body">{{ article.content }}</section>
      <aside v-if="article.tips"><strong>日常提示</strong><p>{{ article.tips }}</p></aside>
    </div>
  </article>
</template>

<style scoped>
.detail-page { min-height: 60vh; padding-block: 42px; }
.detail-page > a { color: var(--color-cinnabar); font-size: 12px; font-weight: 800; }
.detail-card { max-width: 850px; margin: 22px auto 0; padding: clamp(28px, 6vw, 70px); border: 1px solid var(--color-border); border-radius: var(--radius-lg); background: white; box-shadow: var(--shadow-soft); }
header span { color: var(--color-cinnabar); font-size: 11px; font-weight: 800; letter-spacing: .14em; }
h1 { margin: 18px 0; font-size: clamp(2rem, 5vw, 3.7rem); letter-spacing: -.05em; line-height: 1.2; }
header p { color: var(--color-text-muted); line-height: 1.9; }
.article-body { margin-top: 42px; white-space: pre-wrap; font-size: 15px; line-height: 2.05; }
aside { margin-top: 38px; padding: 20px; border-left: 3px solid var(--color-cinnabar); background: var(--color-mist); }
aside p { margin: 8px 0 0; color: var(--color-text-muted); line-height: 1.8; }
</style>

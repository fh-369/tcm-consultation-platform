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
      <header
        class="detail-hero"
        :style="{ backgroundImage: `linear-gradient(0deg, rgba(15, 48, 36, .88), rgba(15, 48, 36, .18)), url('${article.coverImageUrl || '/knowledge/knowledge-hero.png'}')` }"
      >
        <div>
          <span>{{ article.category || '养生常识' }}</span>
          <h1>{{ article.title }}</h1>
          <p>{{ article.summary }}</p>
          <small>{{ article.viewCount || 0 }} 次浏览</small>
        </div>
      </header>
      <div class="reading-sheet">
        <section class="article-body">{{ article.content }}</section>
        <aside v-if="article.tips"><strong>日常提示</strong><p>{{ article.tips }}</p></aside>
      </div>
    </div>
  </article>
</template>

<style scoped>
.detail-page { min-height: 60vh; padding-block: 42px; }
.detail-page > a { color: var(--color-cinnabar); font-size: 12px; font-weight: 800; }
.detail-card { max-width: 980px; margin: 22px auto 0; overflow: hidden; border: 1px solid var(--color-border); border-radius: 26px; background: white; box-shadow: var(--shadow-soft); }
.detail-hero { min-height: 430px; display: flex; align-items: end; padding: clamp(32px, 6vw, 68px); background-position: center; background-size: cover; color: white; }
.detail-hero div { max-width: 720px; }
header span { display: inline-flex; padding: 6px 10px; border-radius: 999px; background: rgb(255 255 255 / 16%); font-size: 11px; font-weight: 800; letter-spacing: .14em; backdrop-filter: blur(8px); }
h1 { margin: 20px 0 14px; font-family: "Noto Serif SC", "STSong", serif; font-size: clamp(2.3rem, 5vw, 4.5rem); letter-spacing: -.05em; line-height: 1.2; }
header p { max-width: 650px; margin: 0; font-size: 15px; line-height: 1.9; opacity: .88; }
header small { display: block; margin-top: 18px; font-size: 11px; opacity: .75; }
.reading-sheet { padding: clamp(32px, 7vw, 76px); }
.article-body { white-space: pre-wrap; color: #29483b; font-size: 16px; line-height: 2.15; }
aside { margin-top: 38px; padding: 22px; border-left: 3px solid var(--color-cinnabar); background: var(--color-mist); }
aside p { margin: 8px 0 0; color: var(--color-text-muted); line-height: 1.8; }
@media (max-width: 620px) { .detail-hero { min-height: 340px; } }
</style>

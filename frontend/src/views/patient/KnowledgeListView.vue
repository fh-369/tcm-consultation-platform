<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { getPublishedKnowledge } from '../../api/content'

const loading = ref(false)
const articles = ref([])
const category = ref('')

const categories = computed(() => [...new Set(articles.value.map((item) => item.category).filter(Boolean))])
const visibleArticles = computed(() =>
  category.value ? articles.value.filter((item) => item.category === category.value) : articles.value,
)

async function loadArticles() {
  loading.value = true
  try {
    articles.value = await getPublishedKnowledge()
  } catch (error) {
    ElMessage.error(error.message || '养生知识加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadArticles)
</script>

<template>
  <section class="content-page page-container">
    <header class="content-hero">
      <div><p>顺时养生 · 日常可行</p><h1>养生知识</h1></div>
      <span>浏览后台已发布的中医养生文章，内容仅供健康科普参考。</span>
    </header>

    <div class="category-filter">
      <button :class="{ active: !category }" type="button" @click="category = ''">全部</button>
      <button
        v-for="item in categories"
        :key="item"
        :class="{ active: category === item }"
        type="button"
        @click="category = item"
      >{{ item }}</button>
    </div>

    <div v-loading="loading" class="article-grid">
      <RouterLink v-for="article in visibleArticles" :key="article.id" :to="`/knowledge/${article.id}`" class="article-card">
        <span>{{ article.category || '养生常识' }}</span>
        <h2>{{ article.title }}</h2>
        <p>{{ article.summary || '点击阅读全文，了解日常养生建议。' }}</p>
        <small>阅读文章 · {{ article.viewCount || 0 }} 次浏览</small>
      </RouterLink>
      <el-empty v-if="!loading && visibleArticles.length === 0" description="暂无已发布文章" />
    </div>
  </section>
</template>

<style scoped>
.content-page { padding-block: 48px 20px; }
.content-hero { display: flex; align-items: end; justify-content: space-between; gap: 24px; padding: 34px; border-radius: var(--radius-lg); background: linear-gradient(135deg, #dcece4, #fff); }
.content-hero p { margin: 0 0 8px; color: var(--color-cinnabar); font-size: 11px; font-weight: 800; letter-spacing: .15em; }
.content-hero h1 { margin: 0; font-size: clamp(2.4rem, 5vw, 4.4rem); letter-spacing: -.06em; }
.content-hero span { max-width: 390px; color: var(--color-text-muted); font-size: 13px; line-height: 1.8; }
.category-filter { display: flex; gap: 8px; margin: 24px 0 16px; overflow-x: auto; }
.category-filter button { flex: 0 0 auto; padding: 8px 14px; border: 1px solid var(--color-border); border-radius: 99px; background: white; color: var(--color-text-muted); cursor: pointer; }
.category-filter button.active { border-color: var(--color-ink); background: var(--color-ink); color: white; }
.article-grid { display: grid; min-height: 200px; grid-template-columns: repeat(3, 1fr); gap: 14px; }
.article-card { min-height: 230px; padding: 26px; border: 1px solid var(--color-border); border-radius: var(--radius-md); background: white; box-shadow: var(--shadow-card); }
.article-card:hover { border-color: var(--color-jade); transform: translateY(-3px); }
.article-card span, .article-card small { color: var(--color-cinnabar); font-size: 10px; font-weight: 800; }
.article-card h2 { margin: 24px 0 10px; font-size: 20px; line-height: 1.5; }
.article-card p { color: var(--color-text-muted); font-size: 13px; line-height: 1.8; }
.article-card small { display: block; margin-top: 22px; color: var(--color-jade); }
@media (max-width: 850px) { .article-grid { grid-template-columns: 1fr; } .content-hero { display: block; } .content-hero span { display: block; margin-top: 18px; } }
</style>
